package com.haulmont.sample.petclinic.web.screens.visit.visitdocumentation;

import com.haulmont.cuba.gui.screen.*;
import com.haulmont.sample.petclinic.entity.visit.VisitDocumentation;

@UiController("petclinic_VisitDocumentation.browse")
@UiDescriptor("visit-documentation-browse.xml")
@LookupComponent("visitDocumentationsTable")
@LoadDataBeforeShow
public class VisitDocumentationBrowse extends StandardLookup<VisitDocumentation> {
}