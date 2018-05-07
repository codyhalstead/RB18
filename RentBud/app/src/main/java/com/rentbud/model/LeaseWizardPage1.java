package com.rentbud.model;

import android.support.v4.app.Fragment;
import android.text.TextUtils;

import com.example.android.wizardpager.wizard.model.ModelCallbacks;
import com.example.android.wizardpager.wizard.model.Page;
import com.example.android.wizardpager.wizard.model.ReviewItem;
import com.rentbud.fragments.LeaseWizardPage1Fragment;

import java.util.ArrayList;

public class LeaseWizardPage1 extends Page{
    public static final String LEASE_START_DATE_STRING_DATA_KEY = "lease_start_date_string";
    public static final String LEASE_END_DATE_STRING_DATA_KEY = "lease_end_date_string";
    public static final String LEASE_APARTMENT_STRING_DATA_KEY = "lease_apartment_string";

    //public static final String LEASE_START_DATE_DATA_KEY = "lease_start_date";
    //public static final String LEASE_END_DATE_DATA_KEY = "lease_end_date";
    public static final String LEASE_APARTMENT_DATA_KEY = "lease_apartment";
    public static final String LEASE_ARE_DATES_ACCEPTABLE = "lease_are_dates_acceptable";


    public LeaseWizardPage1(ModelCallbacks callbacks, String title) {
        super(callbacks, title);
    }

    @Override
    public Fragment createFragment() {
        return LeaseWizardPage1Fragment.create(getKey());
    }

    @Override
    public void getReviewItems(ArrayList<ReviewItem> dest) {
        dest.add(new ReviewItem("Start Date", mData.getString(LEASE_START_DATE_STRING_DATA_KEY), getKey(), -1));
        dest.add(new ReviewItem("End Date", mData.getString(LEASE_END_DATE_STRING_DATA_KEY), getKey(), -1));
        dest.add(new ReviewItem("Apartment", mData.getString(LEASE_APARTMENT_STRING_DATA_KEY), getKey(), -1));
    }

    @Override
    public boolean isCompleted() {
        return (!TextUtils.isEmpty(mData.getString(LEASE_START_DATE_STRING_DATA_KEY)) && !TextUtils.isEmpty(mData.getString(LEASE_END_DATE_STRING_DATA_KEY))
        && !TextUtils.isEmpty(mData.getString(LEASE_APARTMENT_STRING_DATA_KEY)) && mData.getBoolean(LEASE_ARE_DATES_ACCEPTABLE));
    }
}
