package com.haulmont.sample.petclinic.entity;

import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.cuba.core.entity.StandardEntity;
import de.diedavids.cuba.entitysoftreference.EntitySoftReferenceConverter;
import de.diedavids.cuba.metadataextensions.entity.EntityAttributeAwareStandardEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

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