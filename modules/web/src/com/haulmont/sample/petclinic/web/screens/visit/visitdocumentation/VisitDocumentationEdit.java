package com.haulmont.sample.petclinic.web.screens.visit.visitdocumentation;

import com.haulmont.chile.core.model.MetaProperty;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.MetadataTools;
import com.haulmont.cuba.core.global.Security;
import com.haulmont.cuba.gui.ComponentsHelper;
import com.haulmont.cuba.gui.Dialogs;
import com.haulmont.cuba.gui.Notifications;
import com.haulmont.cuba.gui.UiComponents;
import com.haulmont.cuba.gui.app.core.inputdialog.InputDialog;
import com.haulmont.cuba.gui.app.core.inputdialog.InputParameter;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.data.value.ContainerValueSource;
import com.haulmont.cuba.gui.icons.CubaIcon;
import com.haulmont.cuba.gui.model.CollectionContainer;
import com.haulmont.cuba.gui.screen.*;
import com.haulmont.cuba.security.entity.EntityOp;
import com.haulmont.sample.petclinic.entity.EntityAttributeComment;
import com.haulmont.sample.petclinic.entity.visit.VisitDocumentation;
import com.haulmont.sample.petclinic.entity.visit.VisitDocumentationStatus;
import com.haulmont.sample.petclinic.service.EntityAttributeCommentService;
import de.diedavids.cuba.entitysoftreference.SoftReferenceService;
import de.diedavids.cuba.metadataextensions.dataprovider.EntityDataProvider;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;
import java.util.stream.Collectors;

@UiController("petclinic_VisitDocumentation.edit")
@UiDescriptor("visit-documentation-edit.xml")
@EditedEntityContainer("visitDocumentationDc")
@LoadDataBeforeShow
public class VisitDocumentationEdit extends StandardEditor<VisitDocumentation> {
    @Inject
    protected TextArea<String> diagnoseField;
    @Inject
    protected SoftReferenceService softReferenceService;
    @Inject
    protected EntityAttributeCommentService entityAttributeCommentService;
    @Inject
    protected Form contentForm;
    @Inject
    protected Form feedbackForm;
    @Inject
    protected Dialogs dialogs;
    @Inject
    protected UiComponents uiComponents;
    @Inject
    protected EntityDataProvider entityDataProvider;
    @Inject
    protected MessageBundle messageBundle;
    @Inject
    protected CollectionContainer<EntityAttributeComment> commentsDc;
    @Inject
    protected Table<EntityAttributeComment> commentsTable;
    @Inject
    protected DataManager dataManager;
    @Inject
    protected Notifications notifications;

    @Inject
    protected Security security;

    @Inject
    protected TabSheet contentTabSheet;

    @Named("contentTabSheet.commentsManagementTab")
    protected VBoxLayout commentsManagementTab;
    @Inject
    protected MetadataTools metadataTools;

    @Subscribe
    protected void onBeforeShow(BeforeShowEvent event) {
        loadComments();

        if (!security.isEntityOpPermitted(EntityAttributeComment.class, EntityOp.UPDATE)) {
            contentTabSheet.removeTab("commentsManagementTab");
        }
    }

    private void annotateFormsWithComments() {
        ComponentsHelper.traverseComponents(contentForm, this::markComponentAsCommentedIfRequired);
        ComponentsHelper.traverseComponents(feedbackForm, this::markComponentAsCommentedIfRequired);
    }

    private MetaProperty metaProperty(Field field) {
        ContainerValueSource valueSource = (ContainerValueSource) field.getValueSource();
        return valueSource.getMetaPropertyPath().getMetaProperty();
    }

    private void attachCommentToField(Field componentField, EntityAttributeComment entityAttributeComment) {
        componentField.setDescription(entityAttributeComment.getComment());
        componentField.setIconFromSet(CubaIcon.INFO_CIRCLE);
    }


    @Subscribe("commentsTable.createAttributeComment")
    protected void onCreateAttributeComment(Action.ActionPerformedEvent event) {

        dialogs.createInputDialog(this)
                .withParameters(
                        InputParameter.parameter("attribute")
                                .withField(this::createAttributeField),
                        InputParameter.parameter("comment")
                                .withField(this::createCommentField)
                )
                .withCaption(messageBundle.getMessage("createAttributeComment"))
                .withCloseListener(this::afterCreateAttributeComment)
                .show();
    }


    private void loadComments() {
        Collection<EntityAttributeComment> entityAttributeComments =
                softReferenceService.loadEntitiesForSoftReference(EntityAttributeComment.class, getEditedEntity(), "refersTo");
        commentsDc.setItems(entityAttributeComments);
        contentTabSheet.getTabs()
                .forEach(tab -> tab.setIcon(null));
        annotateFormsWithComments();
    }

    @Install(to = "commentsTable.edit", subject = "afterCommitHandler")
    protected void commentsTableEditAfterCommitHandler(EntityAttributeComment entityAttributeComment) {
        loadComments();
        tray(messageBundle.getMessage("commentUpdated"));
    }


    private Field createCommentField() {
        TextArea field = uiComponents.create(TextArea.class);
        field.setCaption(messageBundle.getMessage("comment"));
        field.setRequired(true);
        field.setWidthFull();
        field.setRows(5);
        return field;
    }

    private Field createAttributeField() {
        LookupField field = uiComponents.create(LookupField.class);
        field.setOptionsMap(
                findAttributesForComments()
        );
        field.setCaption(messageBundle.getMessage("attribute"));
        field.setRequired(true);
        field.setWidthFull();
        return field;
    }

    private Map<String, MetaProperty> findAttributesForComments() {

        return entityDataProvider
                .entityAttributesLookupFieldOptions(getEditedEntity().getMetaClass()).entrySet()
                .stream()
                .filter(attribute -> isBusinessAttribute(attribute.getValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    boolean isBusinessAttribute(MetaProperty metaProperty) {
        return !metadataTools.isSystem(metaProperty)
                && !metadataTools.isSystemLevel(metaProperty);
    }


    private void afterCreateAttributeComment(InputDialog.InputDialogCloseEvent inputDialogCloseEvent) {
        if (inputDialogCloseEvent.getCloseAction().equals(InputDialog.INPUT_DIALOG_OK_ACTION)) {
            MetaProperty attribute = (MetaProperty) inputDialogCloseEvent.getValues().get("attribute");
            String comment = (String) inputDialogCloseEvent.getValues().get("comment");
            entityAttributeCommentService.addAttributeComment(getEditedEntity(), attribute, comment);

            loadComments();
            tray(messageBundle.getMessage("commentCreated"));

        }
    }

    @Subscribe("commentsTable.resolveComment")
    protected void onCommentsTableResolveComment(Action.ActionPerformedEvent event) {

        Set<EntityAttributeComment> comments = commentsTable
                .getSelected();

        resolveComments(comments);
    }

    private void resolveComments(Collection<EntityAttributeComment> comments) {
        comments
                .forEach(comment -> dataManager.remove(comment));

        loadComments();
        tray(messageBundle.getMessage("commentsResolved"));
    }

    private void tray(String message) {
        notifications.create(Notifications.NotificationType.TRAY)
                .withCaption(message)
                .show();
    }

    private void markComponentAsCommentedIfRequired(Component component) {
        if (component instanceof Field) {
            Field field = (Field) component;
            MetaProperty metaProperty = metaProperty(field);

            Optional<EntityAttributeComment> possibleComment = commentsDc.getItems()
                    .stream()
                    .filter(entityAttributeComment -> entityAttributeComment.getEntityAttribute().equals(metaProperty))
                    .findAny();

            possibleComment.ifPresent(entityAttributeComment -> {
                        attachCommentToField(field, entityAttributeComment);
                        if (component.getParent().getParent().getParent() instanceof TabSheet) {
                            TabSheet.Tab tab = contentTabSheet.getTab(component.getParent().getParent().getId());
                            tab.setIconFromSet(CubaIcon.INFO_CIRCLE);
                        }
                    }
            );


        }
    }

    @Subscribe("approve")
    protected void onApprove(Action.ActionPerformedEvent event) {
        if (commentsDc.getItems().size() > 0) {
            tray(messageBundle.getMessage("commentsHaveToBeResolvedBefore"));
        }
        else {
            getEditedEntity().setStatus(VisitDocumentationStatus.APPROVED);
            closeWithCommit();
        }
    }
}