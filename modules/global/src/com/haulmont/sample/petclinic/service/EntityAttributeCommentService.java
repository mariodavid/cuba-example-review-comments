package com.haulmont.sample.petclinic.service;

import com.haulmont.chile.core.model.MetaProperty;
import com.haulmont.cuba.core.entity.Entity;

public interface EntityAttributeCommentService {
    String NAME = "petclinic_EntityAttributeCommentService";

    void addAttributeComment(Entity entity, MetaProperty attribute, String comment);
}