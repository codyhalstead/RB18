package com.rentbud.model;

import android.content.Context;
import android.os.Bundle;

import com.example.android.wizardpager.wizard.model.AbstractWizardModel;
import com.example.android.wizardpager.wizard.model.Page;
import com.example.android.wizardpager.wizard.model.PageList;
import com.rentbud.wizards.ExpenseWizardPage1;
import com.rentbud.wizards.ExpenseWizardPage2;
import com.rentbud.wizards.ExpenseWizardPage3;

import java.util.ArrayList;
import java.util.List;

public class ExpenseWizardModel extends AbstractWizardModel {
    public ExpenseWizardModel(Context context) {
        super(context);
    }

    @Override
    protected PageList onNewRootPageList() {
        return new PageList(

                new ExpenseWizardPage1(this, "Page1", super.mContext).setRequired(true),
                new ExpenseWizardPage2(this, "Page2", super.mContext).setRequired(true),
                new ExpenseWizardPage3(this, "Page3", super.mContext).setRequired(false)
        );
    }

    public void preloadData(Bundle bundle){
        List<Page> pages = getCurrentPageSequence();
        for(int i = 0; i < pages.size(); i++){
            pages.get(i).resetData(bundle);
        }
    }
}