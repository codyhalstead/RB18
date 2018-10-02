package com.RB18.model;

import android.content.Context;
import android.os.Bundle;

import com.example.android.wizardpager.wizard.model.AbstractWizardModel;
import com.example.android.wizardpager.wizard.model.Page;
import com.example.android.wizardpager.wizard.model.PageList;
import com.RB18.wizards.LeaseWizardPage1;
import com.RB18.wizards.LeaseWizardPage2;
import com.RB18.wizards.LeaseWizardPage3;
import com.RB18.wizards.LeaseWizardPage4;
import com.RB18.wizards.LeaseWizardProratedRentPage;

import java.util.List;

public class LeaseWizardModel extends AbstractWizardModel {
    public LeaseWizardModel(Context context) {
        super(context);
    }

    @Override
    protected PageList onNewRootPageList() {
        return new PageList(

                new LeaseWizardPage1(this, "Page1", super.mContext).setRequired(true),

                new LeaseWizardPage2(this, "Page2", false, super.mContext).setRequired(true),

                new LeaseWizardPage4(this, "Page4", super.mContext).setRequired(false),

                new LeaseWizardPage3(this, "Page3", false, super.mContext)
                        .addBranch("Yes", new LeaseWizardProratedRentPage(this, "ProratedRentPage", super.mContext).setRequired(true)).setRequired(true)
        );
    }

    public void preloadData(Bundle bundle) {
        List<Page> pages = getCurrentPageSequence();
        for (int i = 0; i < pages.size(); i++) {
            pages.get(i).resetData(bundle);
        }
    }
}
