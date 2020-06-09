package com.haulmont.sample.petclinic.service;

import com.haulmont.chile.core.model.MetaProperty;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.sample.petclinic.entity.EntityAttributeComment;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Service(EntityAttributeCommentService.NAME)
public class EntityAttributeCommentServiceBean implements EntityAttributeCommentService {

    @Inject
    protected DataManager dataManager;


    @Override
    public void addAttributeComment(Entity entity, MetaProperty attribute, String comment) {
        EntityAttributeComment entityAttributeComment = dataManager.create(EntityAttributeComment.class);

        entityAttributeComment.setEntity(entity.getMetaClass());
        entityAttributeComment.setEntityAttribute(attribute);

        entityAttributeComment.setRefersTo(entity);
        entityAttributeComment.setComment(comment);

        dataManager.commit(entityAttributeComment);
    }
}