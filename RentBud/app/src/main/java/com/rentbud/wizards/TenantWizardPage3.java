package com.rentbud.wizards;

import android.support.v4.app.Fragment;

import com.example.android.wizardpager.wizard.model.ModelCallbacks;
import com.example.android.wizardpager.wizard.model.Page;
import com.example.android.wizardpager.wizard.model.ReviewItem;
import com.rentbud.activities.NewTenantWizard;
import com.rentbud.fragments.TenantWizardPage3Fragment;
import com.rentbud.model.Tenant;

import java.util.ArrayList;

public class TenantWizardPage3 extends Page {
    public static final String TENANT_NOTES_DATA_KEY = "tenant_notes";

    public TenantWizardPage3(ModelCallbacks callbacks, String title) {
        super(callbacks, title);
        Tenant tenant = NewTenantWizard.tenantToEdit;
        // Log.d(TAG, "TenantWizardPage1: " + tenant.getFirstName());
        if(tenant != null){
            mData.putString(TENANT_NOTES_DATA_KEY, tenant.getNotes());
            this.notifyDataChanged();
        }
    }


    @Override
    public Fragment createFragment() {
        return TenantWizardPage3Fragment.create(getKey());
    }

    @Override
    public void getReviewItems(ArrayList<ReviewItem> dest) {
        dest.add(new ReviewItem("Notes", mData.getString(TENANT_NOTES_DATA_KEY), getKey(), -1));
    }

    @Override
    public boolean isCompleted() {
        return true;
    }
}
