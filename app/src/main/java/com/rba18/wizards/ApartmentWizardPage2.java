package com.rba18.wizards;

import android.content.Context;
import android.support.v4.app.Fragment;

import com.example.android.wizardpager.wizard.model.ModelCallbacks;
import com.example.android.wizardpager.wizard.model.Page;
import com.example.android.wizardpager.wizard.model.ReviewItem;
import com.rba18.R;
import com.rba18.activities.NewApartmentWizard;
import com.rba18.fragments.ApartmentWizardPage2Fragment;
import com.rba18.model.Apartment;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;

public class ApartmentWizardPage2 extends Page {
    public static final String APARTMENT_DESCRIPTION_DATA_KEY = "apartment_description";
    public static final String APARTMENT_NOTES_DATA_KEY = "apartment_notes";
    //public static final String APARTMENT_PREFERRED_RENT_COST_FORMATTED_STRING_DATA_KEY = "apartment_preferred_rent_cost_formatted";
    //public static final String APARTMENT_PREFERRED_RENT_COST_DATA_KEY = "apartment_preferred_rent_cost";
    public static final String WAS_PRELOADED = "apartmant_page_2_was_preloaded";
    private Context context;

    public ApartmentWizardPage2(ModelCallbacks callbacks, String title, Context context) {
        super(callbacks, title);
        this.context = context;
        mData.putBoolean(WAS_PRELOADED, false);
    }

    @Override
    public Fragment createFragment() {
        return ApartmentWizardPage2Fragment.create(getKey());
    }

    @Override
    public void getReviewItems(ArrayList<ReviewItem> dest) {
        //dest.add(new ReviewItem("Preferred Rent Cost", mData.getString(APARTMENT_PREFERRED_RENT_COST_FORMATTED_STRING_DATA_KEY), getKey(), -1));
        dest.add(new ReviewItem(context.getResources().getString(R.string.description), mData.getString(APARTMENT_DESCRIPTION_DATA_KEY), getKey(), -1));
        dest.add(new ReviewItem(context.getResources().getString(R.string.notes), mData.getString(APARTMENT_NOTES_DATA_KEY), getKey(), -1));
    }

    @Override
    public boolean isCompleted() {
        return true;
    }
}
