package com.rba18.wizards;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.text.TextUtils;

import com.example.android.wizardpager.wizard.model.ModelCallbacks;
import com.example.android.wizardpager.wizard.model.Page;
import com.example.android.wizardpager.wizard.model.ReviewItem;
import com.rba18.R;
import com.rba18.fragments.TenantWizardPage1Fragment;

import java.util.ArrayList;

public class TenantWizardPage1 extends Page {
    public static final String TENANT_FIRST_NAME_DATA_KEY = "tenant_first_name";
    public static final String TENANT_LAST_NAME_DATA_KEY = "tenant_last_name";
    public static final String TENANT_PHONE_DATA_KEY = "tenant_phone";
    public static final String TENANT_EMAIL_DATA_KEY = "tenant_email";
    public static final String WAS_PRELOADED = "tenant_page_1_was_preloaded";
    private Context mContext;

    public TenantWizardPage1(ModelCallbacks callbacks, String title, Context context) {
        super(callbacks, title);
        mContext = context;
        mData.putBoolean(WAS_PRELOADED, false);
    }

    @Override
    public Fragment createFragment() {
        return TenantWizardPage1Fragment.create(getKey());
    }

    @Override
    public void getReviewItems(ArrayList<ReviewItem> dest) {
        dest.add(new ReviewItem(mContext.getResources().getString(R.string.first_name), mData.getString(TENANT_FIRST_NAME_DATA_KEY), getKey(), -1));
        dest.add(new ReviewItem(mContext.getResources().getString(R.string.last_name), mData.getString(TENANT_LAST_NAME_DATA_KEY), getKey(), -1));
        dest.add(new ReviewItem(mContext.getResources().getString(R.string.phone), mData.getString(TENANT_PHONE_DATA_KEY), getKey(), -1));
        dest.add(new ReviewItem(mContext.getResources().getString(R.string.email_review), mData.getString(TENANT_EMAIL_DATA_KEY), getKey(), -1));
    }

    @Override
    public boolean isCompleted() {
        return (!TextUtils.isEmpty(mData.getString(TENANT_FIRST_NAME_DATA_KEY)) && !TextUtils.isEmpty(mData.getString(TENANT_LAST_NAME_DATA_KEY)));
    }
}