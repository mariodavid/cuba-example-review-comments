package com.haulmont.sample.petclinic.entity.visit;

import com.haulmont.cuba.core.entity.StandardEntity;
import org.hibernate.validator.constraints.Length;

import javax.annotation.PostConstruct;
import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Table(name = "PETCLINIC_VISIT_DOCUMENTATION")
@Entity(name = "petclinic_VisitDocumentation")
public class VisitDocumentation extends StandardEntity {

    private static final long serialVersionUID = -5144068302882545367L;

    @NotNull
    @Column(name = "STATUS", nullable = false)
    protected String status;

    @Lob
    @Column(name = "INTERNAL_COMMENT")
    private String internalComment;

    @Lob
    @Column(name = "PET_FEEDBACK")
    private String petFeedback;

    @Lob
    @Column(name = "OWNER_FEEDBACK")
    private String ownerFeedback;

    @Column(name = "SUMMARY", length = 4000)
    private String summary;


    @Length(min = 50, max = 4000)
    @Lob
    @Column(name = "DIAGNOSE")
    private String diagnose;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "VISIT_ID")
    private Visit visit;

    @Max(5)
    @Min(0)
    @Column(name = "RATING")
    private Integer rating;

    public VisitDocumentationStatus getStatus() {
        return status == null ? null : VisitDocumentationStatus.fromId(status);
    }

    public void setStatus(VisitDocumentationStatus status) {
        this.status = status == null ? null : status.getId();
    }

    public Visit getVisit() {
        return visit;
    }

    public void setVisit(Visit visit) {
        this.visit = visit;
    }

    public String getDiagnose() {
        return diagnose;
    }

    public void setDiagnose(String diagnose) {
        this.diagnose = diagnose;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getOwnerFeedback() {
        return ownerFeedback;
    }

    public void setOwnerFeedback(String ownerFeedback) {
        this.ownerFeedback = ownerFeedback;
    }

    public String getPetFeedback() {
        return petFeedback;
    }

    public void setPetFeedback(String petFeedback) {
        this.petFeedback = petFeedback;
    }

    public String getInternalComment() {
        return internalComment;
    }

    public void setInternalComment(String internalComment) {
        this.internalComment = internalComment;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    @PostConstruct
    public void postConstruct() {
        if (getStatus() == null) {
            setStatus(VisitDocumentationStatus.IN_REVIEW);
        }
    }
}