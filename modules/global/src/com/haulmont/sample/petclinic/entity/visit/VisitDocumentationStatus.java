package com.haulmont.sample.petclinic.entity.visit;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;


public enum VisitDocumentationStatus implements EnumClass<String> {

    IN_REVIEW("IN_REVIEW"),
    APPROVED("APPROVED");

    private String id;

    VisitDocumentationStatus(String value) {
        this.id = value;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static VisitDocumentationStatus fromId(String id) {
        for (VisitDocumentationStatus at : VisitDocumentationStatus.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}