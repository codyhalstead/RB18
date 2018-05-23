package com.rentbud.model;

import android.content.Context;

import com.example.android.wizardpager.wizard.model.AbstractWizardModel;
import com.example.android.wizardpager.wizard.model.PageList;
import com.rentbud.wizards.TenantWizardPage1;
import com.rentbud.wizards.TenantWizardPage2;
import com.rentbud.wizards.TenantWizardPage3;

public class TenantWizardModel extends AbstractWizardModel {
    public TenantWizardModel(Context context) {
        super(context);
    }

    @Override
    protected PageList onNewRootPageList() {
        return new PageList(

                new TenantWizardPage1(this, "Page1").setRequired(true),
                new TenantWizardPage2(this, "Page2").setRequired(false),
                new TenantWizardPage3(this, "Page3").setRequired(false)

                );
    }
}

