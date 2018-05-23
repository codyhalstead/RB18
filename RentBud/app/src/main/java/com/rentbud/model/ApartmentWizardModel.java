package com.rentbud.model;

import android.content.Context;

import com.example.android.wizardpager.wizard.model.AbstractWizardModel;
import com.example.android.wizardpager.wizard.model.PageList;
import com.rentbud.wizards.ApartmentWizardPage1;
import com.rentbud.wizards.ApartmentWizardPage2;
import com.rentbud.wizards.ApartmentWizardPage3;

public class ApartmentWizardModel extends AbstractWizardModel {
    public ApartmentWizardModel(Context context) {
        super(context);
    }

    @Override
    protected PageList onNewRootPageList() {
        return new PageList(

                new ApartmentWizardPage1(this, "Page1").setRequired(true),
                new ApartmentWizardPage2(this, "Page2").setRequired(false),
                new ApartmentWizardPage3(this, "Page3").setRequired(false)
        );
    }
}
