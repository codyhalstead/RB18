package com.rentbud.wizards;

import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Pair;

import com.example.android.wizardpager.wizard.model.ModelCallbacks;
import com.example.android.wizardpager.wizard.model.Page;
import com.example.android.wizardpager.wizard.model.PageList;
import com.example.android.wizardpager.wizard.model.ReviewItem;
import com.rentbud.activities.NewLeaseWizard;
import com.rentbud.fragments.LeaseWizardPage3Fragment;
import com.rentbud.helpers.MainArrayDataMethods;
import com.rentbud.model.Lease;
import com.rentbud.model.Tenant;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class LeaseWizardPage3 extends Page {
    public static final String LEASE_RENT_COST_FORMATTED_STRING_DATA_KEY = "lease_rent_cost_formatted";
    public static final String LEASE_PAYMENT_FREQUENCY_STRING_DATA_KEY = "lease_payment_frequency_string";
    public static final String LEASE_DUE_DATE_STRING_DATA_KEY = "lease_due_date_string";

    public static final String LEASE_RENT_COST_DATA_KEY = "lease_rent_cost";
    public static final String LEASE_PAYMENT_FREQUENCY_DATA_KEY = "lease_payment_frequency";
    public static final String LEASE_DUE_DATE_DATA_KEY = "lease_due_date";
    public static final String LEASE_PAYMENT_DATES_ARRAY_DATA_KEY = "lease_payment_dates_array";

    public static final String LEASE_NEED_BRANCH = "lease_need_branch";


    public LeaseWizardPage3(ModelCallbacks callbacks, String title) {
        super(callbacks, title);
        Lease lease = NewLeaseWizard.leaseToEdit;
        if(lease != null){
            BigDecimal rentCost = lease.getMonthlyRentCost();
            String formattedRentCost = NumberFormat.getCurrencyInstance().format(rentCost);
            mData.putString(LEASE_RENT_COST_FORMATTED_STRING_DATA_KEY, formattedRentCost);
           // mData.putString(LEASE_PAYMENT_FREQUENCY_STRING_DATA_KEY, );
            //mData.putString(LEASE_DUE_DATE_STRING_DATA_KEY, );
            mData.putString(LEASE_RENT_COST_DATA_KEY, rentCost.toPlainString());
            //mData.putInt(LEASE_PAYMENT_FREQUENCY_DATA_KEY,); //Todo
            mData.putInt(LEASE_DUE_DATE_DATA_KEY, lease.getPaymentDay());
            //mData.putStringArrayList(LEASE_PAYMENT_DATES_ARRAY_DATA_KEY, );
            //mData.putBoolean(LEASE_NEED_BRANCH, );
            this.notifyDataChanged();
        }
    }
    protected ArrayList<String> mChoices = new ArrayList<String>();
    private List<Branch> mBranches = new ArrayList<>();

    @Override
    public Fragment createFragment() {
        return LeaseWizardPage3Fragment.create(getKey());
    }

    @Override
    public void getReviewItems(ArrayList<ReviewItem> dest) {
        dest.add(new ReviewItem("Rent Cost", mData.getString(LEASE_RENT_COST_FORMATTED_STRING_DATA_KEY), getKey(), -1));
        dest.add(new ReviewItem("Payment Frequency", mData.getString(LEASE_PAYMENT_FREQUENCY_STRING_DATA_KEY), getKey(), -1));
        dest.add(new ReviewItem("Due Date", mData.getString(LEASE_DUE_DATE_STRING_DATA_KEY), getKey(), -1));
    }

    @Override
    public boolean isCompleted() {
        return (!TextUtils.isEmpty(mData.getString(LEASE_RENT_COST_DATA_KEY)) && !TextUtils.isEmpty(mData.getString(LEASE_NEED_BRANCH)));
    }

    public LeaseWizardPage3 addBranch(String choice, Page... childPages) {
        PageList childPageList = new PageList(childPages);
        for (Page page : childPageList) {
            page.setParentKey(choice);
        }
        mBranches.add(new Branch(choice, childPageList));
        return this;
    }

    public LeaseWizardPage3 addBranch(String choice) {
        mBranches.add(new Branch(choice, new PageList()));
        return this;
    }


    @Override
    public void flattenCurrentPageSequence(ArrayList<Page> destination) {
        super.flattenCurrentPageSequence(destination);
        for (Branch branch : mBranches) {
            if (branch.choice.equals(mData.getString(LEASE_NEED_BRANCH))) {
                branch.childPageList.flattenCurrentPageSequence(destination);
                break;
            }
        }
    }

    @Override
    public Page findByKey(String key) {
        if (getKey().equals(key)) {
            return this;
        }

        for (Branch branch : mBranches) {
            Page found = branch.childPageList.findByKey(key);
            if (found != null) {
                return found;
            }
        }

        return null;
    }

    public LeaseWizardPage3 setValue(String value) {
        mData.putString(SIMPLE_DATA_KEY, value);
        return this;
    }

    private static class Branch {
        public String choice;
        public PageList childPageList;

        private Branch(String choice, PageList childPageList) {
            this.choice = choice;
            this.childPageList = childPageList;
        }
    }

    @Override
    public void notifyDataChanged() {
        mCallbacks.onPageTreeChanged();
        super.notifyDataChanged();
    }

}