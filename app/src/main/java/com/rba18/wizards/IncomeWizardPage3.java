package com.rba18.wizards;

import android.content.Context;
import android.support.v4.app.Fragment;

import com.example.android.wizardpager.wizard.model.ModelCallbacks;
import com.example.android.wizardpager.wizard.model.Page;
import com.example.android.wizardpager.wizard.model.ReviewItem;
import com.rba18.R;
import com.rba18.fragments.IncomeWizardPage3Fragment;

import java.util.ArrayList;

public class IncomeWizardPage3 extends Page {
    public static final String INCOME_RELATED_APT_DATA_KEY = "income_related_apt";
    public static final String INCOME_RELATED_APT_TEXT_DATA_KEY = "income_related_apt_text";
    public static final String INCOME_RELATED_APT_ID_DATA_KEY = "income_related_apt_id";

    public static final String INCOME_RELATED_TENANT_DATA_KEY = "income_related_tenant";
    public static final String INCOME_RELATED_TENANT_TEXT_DATA_KEY = "income_related_tenant_text";
    public static final String INCOME_RELATED_TENANT_ID_DATA_KEY = "income_related_tenant_id";

    public static final String INCOME_RELATED_LEASE_DATA_KEY = "income_related_lease";
    public static final String INCOME_RELATED_LEASE_TEXT_DATA_KEY = "income_related_lease_text";
    public static final String INCOME_RELATED_LEASE_ID_DATA_KEY = "income_related_lease_id";
    public static final String WAS_PRELOADED = "income_page_3_was_preloaded";
    private Context mContext;

    public IncomeWizardPage3(ModelCallbacks callbacks, String title, Context context) {
        super(callbacks, title);
        mContext = context;
        mData.putBoolean(WAS_PRELOADED, false);
    }

    @Override
    public Fragment createFragment() {
        return IncomeWizardPage3Fragment.create(getKey());
    }

    @Override
    public void getReviewItems(ArrayList<ReviewItem> dest) {
        dest.add(new ReviewItem(mContext.getResources().getString(R.string.related_apt), mData.getString(INCOME_RELATED_APT_TEXT_DATA_KEY), getKey(), -1));
        dest.add(new ReviewItem(mContext.getResources().getString(R.string.related_tenant), mData.getString(INCOME_RELATED_TENANT_TEXT_DATA_KEY), getKey(), -1));
        dest.add(new ReviewItem(mContext.getResources().getString(R.string.related_lease), mData.getString(INCOME_RELATED_LEASE_TEXT_DATA_KEY), getKey(), -1));
    }

    @Override
    public boolean isCompleted() {
        return true;
    }
}