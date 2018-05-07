package com.rentbud.model;

import android.support.v4.app.Fragment;
import android.text.TextUtils;

import com.example.android.wizardpager.wizard.model.ModelCallbacks;
import com.example.android.wizardpager.wizard.model.Page;
import com.example.android.wizardpager.wizard.model.ReviewItem;
import com.rentbud.fragments.LeaseWizardPage1Fragment;
import com.rentbud.fragments.LeaseWizardProratedRentPageFragment;

import java.util.ArrayList;

public class LeaseWizardProratedRentPage extends Page {
    public static final String LEASE_PRORATED_FIRST_PAYMENT_FORMATTED_STRING_DATA_KEY = "lease_prorated_first_formatted_string";
    public static final String LEASE_PRORATED_LAST_PAYMENT_FORMATTED_STRING_DATA_KEY = "lease_prorated_last_formatted_string";

    public static final String LEASE_PRORATED_FIRST_PAYMENT_STRING_DATA_KEY = "lease_prorated_first_string";
    public static final String LEASE_PRORATED_LAST_PAYMENT_STRING_DATA_KEY = "lease_prorated_last_string";


    public LeaseWizardProratedRentPage(ModelCallbacks callbacks, String title) {
        super(callbacks, title);
    }

    @Override
    public Fragment createFragment() {
        return LeaseWizardProratedRentPageFragment.create(getKey());
    }

    @Override
    public void getReviewItems(ArrayList<ReviewItem> dest) {
        dest.add(new ReviewItem("Prorated First Payment", mData.getString(LEASE_PRORATED_FIRST_PAYMENT_FORMATTED_STRING_DATA_KEY), getKey(), -1));
        dest.add(new ReviewItem("Prorated Last Payment", mData.getString(LEASE_PRORATED_LAST_PAYMENT_FORMATTED_STRING_DATA_KEY), getKey(), -1));
    }

    @Override
    public boolean isCompleted() {
        return true;
    }
}
