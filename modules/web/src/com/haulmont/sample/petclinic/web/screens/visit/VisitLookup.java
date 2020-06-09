package com.haulmont.sample.petclinic.web.screens.visit;

import com.haulmont.cuba.gui.screen.*;
import com.haulmont.sample.petclinic.entity.visit.Visit;

@UiController("petclinic_Visit.lookup")
@UiDescriptor("visit-lookup.xml")
@LookupComponent("visitsTable")
@LoadDataBeforeShow
public class VisitLookup extends StandardLookup<Visit> {
}