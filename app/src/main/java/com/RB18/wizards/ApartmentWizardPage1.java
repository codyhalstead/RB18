package com.RB18.wizards;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.text.TextUtils;

import com.example.android.wizardpager.wizard.model.ModelCallbacks;
import com.example.android.wizardpager.wizard.model.Page;
import com.example.android.wizardpager.wizard.model.ReviewItem;
import com.example.cody.rentbud.R;
import com.RB18.fragments.ApartmentWizardPage1Fragment;

import java.util.ArrayList;

public class ApartmentWizardPage1 extends Page {
    public static final String APARTMENT_ADDRESS_1_DATA_KEY = "apartment_address_1";
    public static final String APARTMENT_ADDRESS_2_DATA_KEY = "apartment_address_2";
    public static final String APARTMENT_CITY_DATA_KEY = "apartment_city";
    public static final String APARTMENT_STATE_DATA_KEY = "apartment_state";
    //public static final String APARTMENT_STATE_ID_DATA_KEY = "apartment_state_id";
    public static final String APARTMENT_ZIP_DATA_KEY = "apartment_zip";
    public static final String WAS_PRELOADED = "apartmant_page_1_was_preloaded";
    private Context context;

    public ApartmentWizardPage1(ModelCallbacks callbacks, String title, Context context) {
        super(callbacks, title);
        this.context = context;
        mData.putBoolean(WAS_PRELOADED, false);
    }

    @Override
    public Fragment createFragment() {
        return ApartmentWizardPage1Fragment.create(getKey());
    }

    @Override
    public void getReviewItems(ArrayList<ReviewItem> dest) {
        dest.add(new ReviewItem(context.getResources().getString(R.string.address_line_1), mData.getString(APARTMENT_ADDRESS_1_DATA_KEY), getKey(), -1));
        dest.add(new ReviewItem(context.getResources().getString(R.string.address_line_2), mData.getString(APARTMENT_ADDRESS_2_DATA_KEY), getKey(), -1));
        dest.add(new ReviewItem(context.getResources().getString(R.string.city), mData.getString(APARTMENT_CITY_DATA_KEY), getKey(), -1));
        dest.add(new ReviewItem(context.getResources().getString(R.string.state), mData.getString(APARTMENT_STATE_DATA_KEY), getKey(), -1));
        dest.add(new ReviewItem(context.getResources().getString(R.string.zip), mData.getString(APARTMENT_ZIP_DATA_KEY), getKey(), -1));
    }

    @Override
    public boolean isCompleted() {
        return (!TextUtils.isEmpty(mData.getString(APARTMENT_ADDRESS_1_DATA_KEY)) && !TextUtils.isEmpty(mData.getString(APARTMENT_CITY_DATA_KEY))
                && !TextUtils.isEmpty(mData.getString(APARTMENT_STATE_DATA_KEY)) && !TextUtils.isEmpty(mData.getString(APARTMENT_ZIP_DATA_KEY)));
        //return true;
    }
}