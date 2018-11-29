package com.rba18.wizards;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.text.TextUtils;

import com.example.android.wizardpager.wizard.model.ModelCallbacks;
import com.example.android.wizardpager.wizard.model.Page;
import com.example.android.wizardpager.wizard.model.PageList;
import com.example.android.wizardpager.wizard.model.ReviewItem;
import com.rba18.R;
import com.rba18.fragments.LeaseWizardPage3Fragment;

import java.util.ArrayList;
import java.util.List;

public class LeaseWizardPage3 extends Page {
    public static final String LEASE_RENT_COST_FORMATTED_STRING_DATA_KEY = "lease_rent_cost_formatted";
    public static final String LEASE_PAYMENT_FREQUENCY_STRING_DATA_KEY = "lease_payment_frequency_string";
    public static final String LEASE_DUE_DATE_STRING_DATA_KEY = "lease_due_date_string";

    public static final String LEASE_RENT_COST_DATA_KEY = "lease_rent_cost";
    public static final String LEASE_PAYMENT_FREQUENCY_DATA_KEY = "lease_payment_frequency";
    public static final String LEASE_PAYMENT_FREQUENCY_ID_DATA_KEY = "lease_payment_frequency_id";

    public static final String LEASE_PAYMENT_CYCLE_DATA_KEY = "lease_cycle";
    public static final String LEASE_PAYMENT_CYCLE_FREQUENCY_ID_DATA_KEY = "lease_cycle_id";

    public static final String LEASE_DUE_DATE_ID_DATA_KEY = "lease_due_date_id";

    public static final String WAS_PRELOADED = "lease_page_3_was_preloaded";

    public static final String LEASE_PAYMENT_DATES_ARRAY_DATA_KEY = "lease_payment_dates_array";

    public static final String LEASE_IS_FIRST_PRORATED_REQUIRED_DATA_KEY = "lease_is_first_prorated_required";
    public static final String LEASE_IS_LAST_PRORATED_REQUIRED_DATA_KEY = "lease_is_last_prorated_required";

    public static final String LEASE_ARE_PAYMENTS_WEEKLY_DATA_KEY = "lease_are_payments_weekly";

    public static final String LEASE_NEED_BRANCH = "lease_need_branch";
    private Context mContext;
    private boolean mIsEdit;
    private List<Branch> mBranches = new ArrayList<>();

    public LeaseWizardPage3(ModelCallbacks callbacks, String title, boolean isEdit, Context context) {
        super(callbacks, title);
        mContext = context;
        mIsEdit = isEdit;
        mData.putBoolean(WAS_PRELOADED, false);
    }

    @Override
    public Fragment createFragment() {
        return LeaseWizardPage3Fragment.create(getKey());
    }

    @Override
    public void getReviewItems(ArrayList<ReviewItem> dest) {
        dest.add(new ReviewItem(mContext.getResources().getString(R.string.rent_cost), mData.getString(LEASE_RENT_COST_FORMATTED_STRING_DATA_KEY), getKey(), -1));
        dest.add(new ReviewItem(mContext.getResources().getString(R.string.payment_frequency), mData.getString(LEASE_PAYMENT_FREQUENCY_STRING_DATA_KEY), getKey(), -1));
        dest.add(new ReviewItem(mContext.getResources().getString(R.string.due_date), mData.getString(LEASE_DUE_DATE_STRING_DATA_KEY), getKey(), -1));
        if(!mIsEdit) {
            dest.add(new ReviewItem(mContext.getResources().getString(R.string.start_of_cycle), mData.getString(LEASE_PAYMENT_CYCLE_DATA_KEY), getKey(), -1));
        }
    }

    @Override
    public boolean isCompleted() {
        if(mIsEdit){
            return (!TextUtils.isEmpty(mData.getString(LEASE_RENT_COST_DATA_KEY)) && !TextUtils.isEmpty(mData.getString(LEASE_NEED_BRANCH)));
        } else {
            return (!TextUtils.isEmpty(mData.getString(LEASE_RENT_COST_DATA_KEY)) && !TextUtils.isEmpty(mData.getString(LEASE_NEED_BRANCH)) );//&& !TextUtils.isEmpty(mData.getString(LEASE_PAYMENT_CYCLE_DATA_KEY)));
        }
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