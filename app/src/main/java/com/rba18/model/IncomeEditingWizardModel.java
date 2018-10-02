package com.rba18.model;

import android.content.Context;
import android.os.Bundle;

import com.example.android.wizardpager.wizard.model.AbstractWizardModel;
import com.example.android.wizardpager.wizard.model.Page;
import com.example.android.wizardpager.wizard.model.PageList;
import com.rba18.wizards.IncomeWizardPage1;
import com.rba18.wizards.IncomeWizardPage2;
import com.rba18.wizards.IncomeWizardPage3;

import java.util.List;

public class IncomeEditingWizardModel extends AbstractWizardModel{
    public IncomeEditingWizardModel(Context context) {
        super(context);
    }

    @Override
    protected PageList onNewRootPageList() {
        return new PageList(

                new IncomeWizardPage1(this, "Page1", super.mContext).setRequired(true),
                new IncomeWizardPage2(this, "Page2", true, super.mContext).setRequired(true),
                new IncomeWizardPage3(this, "Page3", super.mContext).setRequired(false)
        );
    }

    public void preloadData(Bundle bundle){
        List<Page> pages = getCurrentPageSequence();
        for(int i = 0; i < pages.size(); i++){
            pages.get(i).resetData(bundle);
        }
    }
}
