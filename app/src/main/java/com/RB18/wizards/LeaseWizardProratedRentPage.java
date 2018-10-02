package com.RB18.wizards;

import android.content.Context;
import android.support.v4.app.Fragment;

import com.example.android.wizardpager.wizard.model.ModelCallbacks;
import com.example.android.wizardpager.wizard.model.Page;
import com.example.android.wizardpager.wizard.model.ReviewItem;
import com.example.cody.rentbud.R;
import com.RB18.fragments.LeaseWizardProratedRentPageFragment;

import java.util.ArrayList;

public class LeaseWizardProratedRentPage extends Page {
    public static final String LEASE_PRORATED_FIRST_PAYMENT_FORMATTED_STRING_DATA_KEY = "lease_prorated_first_formatted_string";
    public static final String LEASE_PRORATED_LAST_PAYMENT_FORMATTED_STRING_DATA_KEY = "lease_prorated_last_formatted_string";

    public static final String LEASE_PRORATED_FIRST_SHOW_IN_REVIEW_DATA_KEY = "lease_prorated_first_show_in_review";
    public static final String LEASE_PRORATED_LAST_SHOW_IN_REVIEW_DATA_KEY = "lease_prorated_last_show_in_review";

    public static final String LEASE_PRORATED_FIRST_PAYMENT_STRING_DATA_KEY = "lease_prorated_first_string";
    public static final String LEASE_PRORATED_LAST_PAYMENT_STRING_DATA_KEY = "lease_prorated_last_string";
    public static final String LEASE_PRORATED_FIRST_PAYMENT_WAS_MODIFIED_DATA_KEY = "lease_prorated_first_was_modified";
    public static final String LEASE_PRORATED_LAST_PAYMENT_WAS_MODIFIED_DATA_KEY = "lease_prorated_last_was_modified";
    private Context context;

    //Will not be created if editing, do not have to prepare for that
    public LeaseWizardProratedRentPage(ModelCallbacks callbacks, String title, Context context) {
        super(callbacks, title);
        this.context = context;
        //Lease lease = NewLeaseWizard.leaseToEdit;
        //if(lease != null){
            //BigDecimal proratedFirstBD = ;
            //String formattedProratedFirst = NumberFormat.getCurrencyInstance().format(proratedFirstBD);
            //BigDecimal proratedLastBD = ;
            //String formattedProratedLast = NumberFormat.getCurrencyInstance().format(proratedLastBD);
            //mData.putString(LEASE_PRORATED_FIRST_PAYMENT_FORMATTED_STRING_DATA_KEY, primaryTenantString);
            //mData.putString(LEASE_PRORATED_LAST_PAYMENT_FORMATTED_STRING_DATA_KEY, secondaryTenantsString);
            //mData.putString(LEASE_PRORATED_FIRST_PAYMENT_STRING_DATA_KEY, formattedDeposit);
            //mData.putString(LEASE_PRORATED_LAST_PAYMENT_STRING_DATA_KEY, formattedDepositWithheld);
        //    mData.putBoolean(LEASE_PRORATED_FIRST_PAYMENT_WAS_MODIFIED_DATA_KEY, true);
        //    mData.putBoolean(LEASE_PRORATED_LAST_PAYMENT_WAS_MODIFIED_DATA_KEY, true);
        //    this.notifyDataChanged();
        //}
    }

    @Override
    public Fragment createFragment() {
        return LeaseWizardProratedRentPageFragment.create(getKey());
    }

    @Override
    public void getReviewItems(ArrayList<ReviewItem> dest) {
        if(mData.getBoolean(LEASE_PRORATED_FIRST_SHOW_IN_REVIEW_DATA_KEY)) {
            dest.add(new ReviewItem(context.getResources().getString(R.string.prorated_first_payment), mData.getString(LEASE_PRORATED_FIRST_PAYMENT_FORMATTED_STRING_DATA_KEY), getKey(), -1));
        }
        if(mData.getBoolean(LEASE_PRORATED_LAST_SHOW_IN_REVIEW_DATA_KEY)) {
            dest.add(new ReviewItem(context.getResources().getString(R.string.prorated_last_payment), mData.getString(LEASE_PRORATED_LAST_PAYMENT_FORMATTED_STRING_DATA_KEY), getKey(), -1));
        }
    }

    @Override
    public boolean isCompleted() {
        return true;
    }
}
