package com.RB18.wizards;

import android.content.Context;
import android.support.v4.app.Fragment;

import com.example.android.wizardpager.wizard.model.ModelCallbacks;
import com.example.android.wizardpager.wizard.model.Page;
import com.example.android.wizardpager.wizard.model.ReviewItem;
import com.example.cody.rentbud.R;
import com.RB18.fragments.LeaseWizardPage4Fragment;

import java.util.ArrayList;

public class LeaseWizardPage4 extends Page {
    public static final String LEASE_NOTES_DATA_KEY = "lease_notes";
    public static final String WAS_PRELOADED = "lease_page_4_was_preloaded";
    private Context context;

    public LeaseWizardPage4(ModelCallbacks callbacks, String title, Context context) {
        super(callbacks, title);
        this.context = context;
        mData.putBoolean(WAS_PRELOADED, false);
    }

    @Override
    public Fragment createFragment() {
        return LeaseWizardPage4Fragment.create(getKey());
    }

    @Override
    public void getReviewItems(ArrayList<ReviewItem> dest) {
        dest.add(new ReviewItem(context.getResources().getString(R.string.notes), mData.getString(LEASE_NOTES_DATA_KEY), getKey(), -1));
    }

    @Override
    public boolean isCompleted() {
        return true;
    }
}