package com.rentbud.model;

import android.content.Context;

import com.example.android.wizardpager.wizard.model.AbstractWizardModel;
import com.example.android.wizardpager.wizard.model.PageList;
import com.rentbud.wizards.IncomeWizardPage1;
import com.rentbud.wizards.IncomeWizardPage2;
import com.rentbud.wizards.IncomeWizardPage3;

public class IncomeWizardModel extends AbstractWizardModel {
    public IncomeWizardModel(Context context) {
        super(context);
    }

    @Override
    protected PageList onNewRootPageList() {
        return new PageList(

                new IncomeWizardPage1(this, "Page1").setRequired(true),
                new IncomeWizardPage2(this, "Page2").setRequired(true),
                new IncomeWizardPage3(this, "Page3").setRequired(false)
        );
    }
}