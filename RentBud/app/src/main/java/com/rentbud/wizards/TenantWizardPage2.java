package com.rentbud.wizards;

import android.support.v4.app.Fragment;

import com.example.android.wizardpager.wizard.model.ModelCallbacks;
import com.example.android.wizardpager.wizard.model.Page;
import com.example.android.wizardpager.wizard.model.ReviewItem;
import com.rentbud.activities.NewTenantWizard;
import com.rentbud.fragments.TenantWizardPage2Fragment;
import com.rentbud.model.Tenant;

import java.util.ArrayList;

public class TenantWizardPage2 extends Page {
    public static final String TENANT_EMERGENCY_FIRST_NAME_DATA_KEY = "tenant_emergency_first_name";
    public static final String TENANT_EMERGENCY_LAST_NAME_DATA_KEY = "tenant_emergency_last_name";
    public static final String TENANT_EMERGENCY_PHONE_DATA_KEY = "tenant_emergency_phone";
    public static final String WAS_PRELOADED = "tenant_page_2_was_preloaded";

    public TenantWizardPage2(ModelCallbacks callbacks, String title) {
        super(callbacks, title);
        mData.putBoolean(WAS_PRELOADED, false);
        //Tenant tenant = NewTenantWizard.tenantToEdit;
        // Log.d(TAG, "TenantWizardPage1: " + tenant.getFirstName());
        //if(tenant != null){
        //    mData.putString(TENANT_EMERGENCY_FIRST_NAME_DATA_KEY, tenant.getEmergencyFirstName());
        //    mData.putString(TENANT_EMERGENCY_LAST_NAME_DATA_KEY, tenant.getEmergencyLastName());
        //    mData.putString(TENANT_EMERGENCY_PHONE_DATA_KEY, tenant.getEmergencyPhone());
        //    this.notifyDataChanged();
        //}
    }

    @Override
    public Fragment createFragment() {
        return TenantWizardPage2Fragment.create(getKey());
    }

    @Override
    public void getReviewItems(ArrayList<ReviewItem> dest) {
        dest.add(new ReviewItem("Emergency Contact First Name", mData.getString(TENANT_EMERGENCY_FIRST_NAME_DATA_KEY), getKey(), -1));
        dest.add(new ReviewItem("Emergency Contact Last Name", mData.getString(TENANT_EMERGENCY_LAST_NAME_DATA_KEY), getKey(), -1));
        dest.add(new ReviewItem("Emergency Contact Phone", mData.getString(TENANT_EMERGENCY_PHONE_DATA_KEY), getKey(), -1));
    }

    @Override
    public boolean isCompleted() {
        return true;
    }
}
