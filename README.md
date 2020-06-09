# CUBA Petclinic Review Comments

<p align="center">
  <img src="https://github.com/cuba-platform/cuba-petclinic/blob/master/img/petclinic_logo_with_slogan.svg"/>
</p>

This examples shows how to create review comments for fields on an entity. 
The example contains the following business case:

> Nurses have to enter a visit documentation after a Pet visit is finished. When they enter the information
this documentation has to be reviewed by a Veterinarian. The Vet can take a look at the documentation
and add comments to the fields that were entered by the Nurse.

> If there is anything to change, the Vet adds a Comment to a Field. Then the nurse can look at the Documentation once
again and look at what needs to be changes. The comments are provided via the tooltip on the Field. Additionally
the fields are marked with a corresponding (i) icon.


### Screenshots

![Overview](/img/overview.gif)

#### Nurse: Enter Documentation 
![nurse-enter-documentation](/img/screenshots/nurse-enter-documentation.png)

#### Vet: Comments Management
![nurse-enter-documentation](/img/screenshots/vet-comments-management.png)

#### Vet: Create Comment
![nurse-enter-documentation](/img/screenshots/create-comment.png)

#### Nurse: Comments Management
![show-comment](/img/screenshots/show-comment.png)

### How does it work?

The example is built on top ot the following two add-ons:

* [entity-soft-reference](https://github.com/mariodavid/cuba-example-using-entity-soft-reference/)
* [metadata-extensions](https://github.com/mariodavid/cuba-component-metadata-extensions/)

to store the comments in a generic way.

#### Storing comments

There is a dedicated entity: [EntityAttributeComment]() which contains the comment information with a link to the VisitDocumentation instance.

```java
@Table(name = "PETCLINIC_ENTITY_ATTRIBUTE_COMMENT")
@Entity(name = "petclinic_EntityAttributeComment")
public class EntityAttributeComment extends EntityAttributeAwareStandardEntity {
    private static final long serialVersionUID = -308675045818098974L;

    @Convert(converter = EntitySoftReferenceConverter.class)
    @MetaProperty(datatype = "EntitySoftReference")
    @Column(name = "REFERS_TO")
    protected com.haulmont.cuba.core.entity.Entity refersTo;

    @Lob
    @Column(name = "COMMENT_")
    protected String comment;


    public com.haulmont.cuba.core.entity.Entity getRefersTo() {
        return refersTo;
    }

    public void setRefersTo(com.haulmont.cuba.core.entity.Entity refersTo) {
        this.refersTo = refersTo;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

}
```


#### Comment Indicators in the UI

It is mainly handled in the `VisitDocumentationEdit` controller class. When the screen is opened, all fields
are traversed in order to find out which fields to mark with a comment:

```java
public class VisitDocumentationEdit extends StandardEditor<VisitDocumentation> {
    
    // ...

    @Subscribe
    protected void onBeforeShow(BeforeShowEvent event) {
        loadComments();

        if (!security.isEntityOpPermitted(EntityAttributeComment.class, EntityOp.UPDATE)) {
            contentTabSheet.removeTab("commentsManagementTab");
        }
    }


    private void loadComments() {
        Collection<EntityAttributeComment> entityAttributeComments =
                softReferenceService.loadEntitiesForSoftReference(EntityAttributeComment.class, getEditedEntity(), "refersTo");
        commentsDc.setItems(entityAttributeComments);
        contentTabSheet.getTabs()
                .forEach(tab -> tab.setIcon(null));
        annotateFormsWithComments();
    }

    private void annotateFormsWithComments() {
        ComponentsHelper.traverseComponents(contentForm, this::markComponentAsCommentedIfRequired);
        ComponentsHelper.traverseComponents(feedbackForm, this::markComponentAsCommentedIfRequired);
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

    private void attachCommentToField(Field componentField, EntityAttributeComment entityAttributeComment) {
        componentField.setDescription(entityAttributeComment.getComment());
        componentField.setIconFromSet(CubaIcon.INFO_CIRCLE);
    }

    private MetaProperty metaProperty(Field field) {
        ContainerValueSource valueSource = (ContainerValueSource) field.getValueSource();
        return valueSource.getMetaPropertyPath().getMetaProperty();
    }
}
```

Additionally there is a tab for comments management. This Tab is only available for the Role `Veterinarian`.
