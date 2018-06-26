package com.rentbud.model;

import android.content.Context;

import com.example.android.wizardpager.wizard.model.AbstractWizardModel;
import com.example.android.wizardpager.wizard.model.PageList;
import com.rentbud.wizards.ExpenseWizardPage1;
import com.rentbud.wizards.ExpenseWizardPage2;
import com.rentbud.wizards.ExpenseWizardPage3;

public class ExpenseWizardModel extends AbstractWizardModel {
    public ExpenseWizardModel(Context context) {
        super(context);
    }

    @Override
    protected PageList onNewRootPageList() {
        return new PageList(

                new ExpenseWizardPage1(this, "Page1").setRequired(true),
                new ExpenseWizardPage2(this, "Page2").setRequired(true),
                new ExpenseWizardPage3(this, "Page3").setRequired(false)
        );
    }
}