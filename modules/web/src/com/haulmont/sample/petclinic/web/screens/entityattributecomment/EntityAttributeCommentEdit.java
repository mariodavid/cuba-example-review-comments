package com.haulmont.sample.petclinic.web.screens.entityattributecomment;

import com.haulmont.cuba.gui.screen.*;
import com.haulmont.sample.petclinic.entity.EntityAttributeComment;

@UiController("petclinic_EntityAttributeComment.edit")
@UiDescriptor("entity-attribute-comment-edit.xml")
@EditedEntityContainer("entityAttributeCommentDc")
@LoadDataBeforeShow
public class EntityAttributeCommentEdit extends StandardEditor<EntityAttributeComment> {
}