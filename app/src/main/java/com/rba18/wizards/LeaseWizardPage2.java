package com.rba18.wizards;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.text.TextUtils;

import com.example.android.wizardpager.wizard.model.ModelCallbacks;
import com.example.android.wizardpager.wizard.model.Page;
import com.example.android.wizardpager.wizard.model.ReviewItem;
import com.rba18.R;
import com.rba18.fragments.LeaseWizardPage2Fragment;

import java.util.ArrayList;

public class LeaseWizardPage2 extends Page {
    public static final String LEASE_PRIMARY_TENANT_STRING_DATA_KEY = "lease_primary_tenant_string";
    public static final String LEASE_SECONDARY_TENANTS_STRING_DATA_KEY = "lease_secondary_tenants_string";
    public static final String LEASE_DEPOSIT_FORMATTED_STRING_DATA_KEY = "lease_deposit_string";

    public static final String LEASE_PRIMARY_TENANT_DATA_KEY = "lease_primary_tenant";
    public static final String LEASE_SECONDARY_TENANTS_DATA_KEY = "lease_secondary_tenants";
    public static final String LEASE_DEPOSIT_STRING_DATA_KEY = "lease_deposit";
    public static final String WAS_PRELOADED = "lease_page_2_was_preloaded";

    private boolean isEdit;
    private Context context;

    public LeaseWizardPage2(ModelCallbacks callbacks, String title, boolean isEdit, Context context) {
        super(callbacks, title);
        this.isEdit = isEdit;
        this.context = context;
        mData.putBoolean(WAS_PRELOADED, false);
    }

    @Override
    public Fragment createFragment() {
        return LeaseWizardPage2Fragment.create(getKey());
    }

    @Override
    public void getReviewItems(ArrayList<ReviewItem> dest) {
        dest.add(new ReviewItem(context.getResources().getString(R.string.primary_tenant), mData.getString(LEASE_PRIMARY_TENANT_STRING_DATA_KEY), getKey(), -1));
        dest.add(new ReviewItem(context.getResources().getString(R.string.other_tenants), mData.getString(LEASE_SECONDARY_TENANTS_STRING_DATA_KEY), getKey(), -1));
        if(!isEdit) {
            dest.add(new ReviewItem(context.getResources().getString(R.string.deposit), mData.getString(LEASE_DEPOSIT_FORMATTED_STRING_DATA_KEY), getKey(), -1));
        }
    }

    @Override
    public boolean isCompleted() {
        return !TextUtils.isEmpty(mData.getString(LEASE_PRIMARY_TENANT_STRING_DATA_KEY));
    }
}

