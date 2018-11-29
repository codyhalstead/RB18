package com.rba18.wizards;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;

import com.example.android.wizardpager.wizard.model.ModelCallbacks;
import com.example.android.wizardpager.wizard.model.Page;
import com.example.android.wizardpager.wizard.model.ReviewItem;
import com.rba18.R;
import com.rba18.fragments.ExpenseWizardPage1Fragment;

import java.util.ArrayList;

public class ExpenseWizardPage1 extends Page {
    public static final String EXPENSE_DATE_STRING_DATA_KEY = "expense_date";
    public static final String EXPENSE_DATE_STRING_FORMATTED_DATA_KEY = "expense_date_formatted";
    public static final String EXPENSE_AMOUNT_FORMATTED_STRING_DATA_KEY = "expense_amount_formatted";
    public static final String EXPENSE_AMOUNT_STRING_DATA_KEY = "expense_amount";
    public static final String EXPENSE_TYPE_ID_DATA_KEY = "expense_type_id";
    public static final String EXPENSE_TYPE_DATA_KEY = "expense_type";
    public static final String WAS_PRELOADED = "expense_page_1_was_preloaded";
    private Context mContext;

    public ExpenseWizardPage1(ModelCallbacks callbacks, String title, Context context) {
        super(callbacks, title);
        mContext = context;
        mData.putBoolean(WAS_PRELOADED, false);
    }

    @Override
    public Fragment createFragment() {
        return ExpenseWizardPage1Fragment.create(getKey());
    }

    @Override
    public void getReviewItems(ArrayList<ReviewItem> dest) {
        dest.add(new ReviewItem(mContext.getResources().getString(R.string.date), mData.getString(EXPENSE_DATE_STRING_FORMATTED_DATA_KEY), getKey(), -1));
        dest.add(new ReviewItem(mContext.getResources().getString(R.string.amount), mData.getString(EXPENSE_AMOUNT_FORMATTED_STRING_DATA_KEY), getKey(), -1));
        dest.add(new ReviewItem(mContext.getResources().getString(R.string.type), mData.getString(EXPENSE_TYPE_DATA_KEY), getKey(), -1));
    }

    @Override
    public boolean isCompleted() {
        return (!TextUtils.isEmpty(mData.getString(EXPENSE_DATE_STRING_DATA_KEY)) && !TextUtils.isEmpty(mData.getString(EXPENSE_TYPE_DATA_KEY))
                && !TextUtils.isEmpty(mData.getString(EXPENSE_AMOUNT_STRING_DATA_KEY)));
    }

    @Override
    public Bundle getData() {
        return super.getData();
    }
}