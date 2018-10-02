package com.rba18.wizards;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;

import com.example.android.wizardpager.wizard.model.ModelCallbacks;
import com.example.android.wizardpager.wizard.model.Page;
import com.example.android.wizardpager.wizard.model.ReviewItem;
import com.rba18.R;
import com.rba18.activities.NewApartmentWizard;
import com.rba18.activities.NewLeaseWizard;
import com.rba18.fragments.LeaseWizardPage1Fragment;
import com.rba18.helpers.MainArrayDataMethods;
import com.rba18.model.Apartment;
import com.rba18.model.Lease;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class LeaseWizardPage1 extends Page{
    public static final String LEASE_START_DATE_STRING_DATA_KEY = "lease_start_date_string";
    public static final String LEASE_END_DATE_STRING_DATA_KEY = "lease_end_date_string";
    public static final String LEASE_START_DATE_STRING_FORMATTED_DATA_KEY = "lease_start_date_formatted_string";
    public static final String LEASE_END_DATE_STRING_FORMATTED_DATA_KEY = "lease_end_date_formatted_string";
    public static final String LEASE_APARTMENT_STRING_DATA_KEY = "lease_apartment_string";

    public static final String LEASE_APARTMENT_DATA_KEY = "lease_apartment";
    public static final String LEASE_ARE_DATES_ACCEPTABLE = "lease_are_dates_acceptable";
    public static final String WAS_PRELOADED = "lease_page_1_was_preloaded";
    private Context context;

    public LeaseWizardPage1(ModelCallbacks callbacks, String title, Context context) {
        super(callbacks, title);
        this.context = context;
        mData.putBoolean(WAS_PRELOADED, false);
    }

    @Override
    public Fragment createFragment() {
        return LeaseWizardPage1Fragment.create(getKey());
    }

    @Override
    public void getReviewItems(ArrayList<ReviewItem> dest) {
        dest.add(new ReviewItem(context.getResources().getString(R.string.start_date), mData.getString(LEASE_START_DATE_STRING_FORMATTED_DATA_KEY), getKey(), -1));
        dest.add(new ReviewItem(context.getResources().getString(R.string.end_date), mData.getString(LEASE_END_DATE_STRING_FORMATTED_DATA_KEY), getKey(), -1));
        dest.add(new ReviewItem(context.getResources().getString(R.string.apartment), mData.getString(LEASE_APARTMENT_STRING_DATA_KEY), getKey(), -1));
    }

    @Override
    public boolean isCompleted() {
        return (!TextUtils.isEmpty(mData.getString(LEASE_START_DATE_STRING_DATA_KEY)) && !TextUtils.isEmpty(mData.getString(LEASE_END_DATE_STRING_DATA_KEY))
        && !TextUtils.isEmpty(mData.getString(LEASE_APARTMENT_STRING_DATA_KEY)) && mData.getBoolean(LEASE_ARE_DATES_ACCEPTABLE));
    }

    @Override
    public Bundle getData() {
        return super.getData();
    }
}
