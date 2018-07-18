package com.rentbud.wizards;

import android.support.v4.app.Fragment;
import android.text.TextUtils;

import com.example.android.wizardpager.wizard.model.ModelCallbacks;
import com.example.android.wizardpager.wizard.model.Page;
import com.example.android.wizardpager.wizard.model.ReviewItem;
import com.rentbud.activities.NewApartmentWizard;
import com.rentbud.fragments.ApartmentWizardPage1Fragment;
import com.rentbud.model.Apartment;

import java.util.ArrayList;

public class ApartmentWizardPage1 extends Page {
    public static final String APARTMENT_ADDRESS_1_DATA_KEY = "apartment_address_1";
    public static final String APARTMENT_ADDRESS_2_DATA_KEY = "apartment_address_2";
    public static final String APARTMENT_CITY_DATA_KEY = "apartment_city";
    public static final String APARTMENT_STATE_DATA_KEY = "apartment_state";
    public static final String APARTMENT_STATE_ID_DATA_KEY = "apartment_state_id";
    public static final String APARTMENT_ZIP_DATA_KEY = "apartment_zip";
    public static final String WAS_PRELOADED = "apartmant_page_1_was_preloaded";

    public ApartmentWizardPage1(ModelCallbacks callbacks, String title) {
        super(callbacks, title);
        mData.putBoolean(WAS_PRELOADED, false);
    }

    @Override
    public Fragment createFragment() {
        return ApartmentWizardPage1Fragment.create(getKey());
    }

    @Override
    public void getReviewItems(ArrayList<ReviewItem> dest) {
        dest.add(new ReviewItem("Address Line 1", mData.getString(APARTMENT_ADDRESS_1_DATA_KEY), getKey(), -1));
        dest.add(new ReviewItem("Address Line 2", mData.getString(APARTMENT_ADDRESS_2_DATA_KEY), getKey(), -1));
        dest.add(new ReviewItem("City", mData.getString(APARTMENT_CITY_DATA_KEY), getKey(), -1));
        dest.add(new ReviewItem("State", mData.getString(APARTMENT_STATE_DATA_KEY), getKey(), -1));
        dest.add(new ReviewItem("ZIP", mData.getString(APARTMENT_ZIP_DATA_KEY), getKey(), -1));
    }

    @Override
    public boolean isCompleted() {
        return (!TextUtils.isEmpty(mData.getString(APARTMENT_ADDRESS_1_DATA_KEY)) && !TextUtils.isEmpty(mData.getString(APARTMENT_CITY_DATA_KEY))
                && !TextUtils.isEmpty(mData.getString(APARTMENT_STATE_DATA_KEY)) && !TextUtils.isEmpty(mData.getString(APARTMENT_ZIP_DATA_KEY)));
        //return true;
    }
}