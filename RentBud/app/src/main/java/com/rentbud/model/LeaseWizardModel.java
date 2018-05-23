package com.rentbud.model;

import android.content.Context;

import com.example.android.wizardpager.wizard.model.AbstractWizardModel;
import com.example.android.wizardpager.wizard.model.PageList;
import com.rentbud.wizards.LeaseWizardPage1;
import com.rentbud.wizards.LeaseWizardPage2;
import com.rentbud.wizards.LeaseWizardPage3;
import com.rentbud.wizards.LeaseWizardProratedRentPage;

public class LeaseWizardModel extends AbstractWizardModel {
    public LeaseWizardModel(Context context) {
        super(context);
    }

    @Override
    protected PageList onNewRootPageList() {
        return new PageList(

                new LeaseWizardPage1(this, "Page1").setRequired(true),

                new LeaseWizardPage2(this, "Page2").setRequired(true),

                new LeaseWizardPage3(this, "Page3")
                        .addBranch("Yes", new LeaseWizardProratedRentPage(this, "ProratedRentPage").setRequired(true))
        );
    }
}
