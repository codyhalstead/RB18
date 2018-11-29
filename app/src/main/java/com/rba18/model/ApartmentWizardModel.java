package com.rba18.model;

import android.content.Context;
import android.os.Bundle;

import com.example.android.wizardpager.wizard.model.AbstractWizardModel;
import com.example.android.wizardpager.wizard.model.Page;
import com.example.android.wizardpager.wizard.model.PageList;
import com.rba18.wizards.ApartmentWizardPage1;
import com.rba18.wizards.ApartmentWizardPage2;
import com.rba18.wizards.ApartmentWizardPage3;

import java.util.List;

public class ApartmentWizardModel extends AbstractWizardModel {
    public ApartmentWizardModel(Context context) {
        super(context);
    }

    @Override
    protected PageList onNewRootPageList() {
        return new PageList(
                new ApartmentWizardPage1(this, "Page1", super.mContext).setRequired(true),
                new ApartmentWizardPage2(this, "Page2", super.mContext).setRequired(false),
                new ApartmentWizardPage3(this, "Page3", super.mContext).setRequired(false)
        );
    }

    public void preloadData(Bundle bundle){
        List<Page> pages = getCurrentPageSequence();
        for(int i = 0; i < pages.size(); i++){
            pages.get(i).resetData(bundle);
        }
    }
}
