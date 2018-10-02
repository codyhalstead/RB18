package com.RB18.wizards;

import android.content.Context;
import android.support.v4.app.Fragment;

import com.example.android.wizardpager.wizard.model.ModelCallbacks;
import com.example.android.wizardpager.wizard.model.Page;
import com.example.android.wizardpager.wizard.model.ReviewItem;
import com.example.cody.rentbud.R;
import com.RB18.fragments.TenantWizardPage3Fragment;

import java.util.ArrayList;

public class TenantWizardPage3 extends Page {
    public static final String TENANT_NOTES_DATA_KEY = "tenant_notes";
    public static final String WAS_PRELOADED = "tenant_page_3_was_preloaded";
    Context context;

    public TenantWizardPage3(ModelCallbacks callbacks, String title, Context context) {
        super(callbacks, title);
        this.context = context;
        mData.putBoolean(WAS_PRELOADED, false);
        //Tenant tenant = NewTenantWizard.tenantToEdit;
        // Log.d(TAG, "TenantWizardPage1: " + tenant.getFirstName());
        //if(tenant != null){
        //    mData.putString(TENANT_NOTES_DATA_KEY, tenant.getNotes());
        //    this.notifyDataChanged();
        //}
    }


    @Override
    public Fragment createFragment() {
        return TenantWizardPage3Fragment.create(getKey());
    }

    @Override
    public void getReviewItems(ArrayList<ReviewItem> dest) {
        dest.add(new ReviewItem(context.getResources().getString(R.string.notes), mData.getString(TENANT_NOTES_DATA_KEY), getKey(), -1));
    }

    @Override
    public boolean isCompleted() {
        return true;
    }
}
