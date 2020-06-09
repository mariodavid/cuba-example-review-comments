package com.haulmont.sample.petclinic.web;

import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.TabSheet;
import com.haulmont.cuba.gui.components.ValidationErrors;
import com.haulmont.cuba.gui.screen.FrameOwner;
import com.haulmont.cuba.gui.screen.ScreenValidation;
import com.haulmont.cuba.web.gui.components.WebTabWindow;

public class ExtScreenValidation extends ScreenValidation {

    @Override
    public void showValidationErrors(FrameOwner origin, ValidationErrors errors) {
        super.showValidationErrors(origin, errors);
        errors.getAll().forEach(e -> setErrorIcon(e.component));
    }



    private void setErrorIcon(Component component) {
        if (component == null) {
            return;
        }
        if ((component instanceof WebTabWindow) || (component instanceof TabSheet.Tab)) {
            ((Component.HasIcon) component).setIcon("font-icon:EXCLAMATION_CIRCLE");
        } else {
            setErrorIcon(component.getParent());
        }
    }
}
