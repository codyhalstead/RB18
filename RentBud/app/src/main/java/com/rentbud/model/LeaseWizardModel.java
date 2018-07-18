package com.rentbud.model;

import android.content.Context;
import android.os.Bundle;

import com.example.android.wizardpager.wizard.model.AbstractWizardModel;
import com.example.android.wizardpager.wizard.model.Page;
import com.example.android.wizardpager.wizard.model.PageList;
import com.rentbud.activities.NewLeaseWizard;
import com.rentbud.wizards.LeaseWizardPage1;
import com.rentbud.wizards.LeaseWizardPage2;
import com.rentbud.wizards.LeaseWizardPage3;
import com.rentbud.wizards.LeaseWizardPage4;
import com.rentbud.wizards.LeaseWizardProratedRentPage;

import java.util.List;

public class LeaseWizardModel extends AbstractWizardModel {
    public LeaseWizardModel(Context context) {
        super(context);
    }

    @Override
    protected PageList onNewRootPageList() {
        return new PageList(

                new LeaseWizardPage1(this, "Page1").setRequired(true),

                new LeaseWizardPage2(this, "Page2", false).setRequired(true),

                new LeaseWizardPage4(this, "Page4").setRequired(false),

                new LeaseWizardPage3(this, "Page3")
                        .addBranch("Yes", new LeaseWizardProratedRentPage(this, "ProratedRentPage").setRequired(true)).setRequired(true)
        );
    }

    public void preloadData(Bundle bundle) {
        List<Page> pages = getCurrentPageSequence();
        for (int i = 0; i < pages.size(); i++) {
            pages.get(i).resetData(bundle);
        }
    }
}
