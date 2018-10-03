package com.rba18.wizards;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.text.TextUtils;

import com.example.android.wizardpager.wizard.model.ModelCallbacks;
import com.example.android.wizardpager.wizard.model.Page;
import com.example.android.wizardpager.wizard.model.ReviewItem;
import com.rba18.R;
import com.rba18.fragments.IncomeWizardPage1Fragment;

import java.util.ArrayList;

public class IncomeWizardPage1 extends Page {
    public static final String INCOME_DATE_STRING_DATA_KEY = "income_date";
    public static final String INCOME_DATE_STRING_FORMATTED_DATA_KEY = "income_date_formatted";
    public static final String INCOME_AMOUNT_FORMATTED_STRING_DATA_KEY = "income_amount_formatted";
    public static final String INCOME_AMOUNT_STRING_DATA_KEY = "income_amount";
    public static final String INCOME_TYPE_ID_DATA_KEY = "income_type_id";
    public static final String INCOME_TYPE_DATA_KEY = "income_type";
    public static final String WAS_PRELOADED = "income_page_1_was_preloaded";
    private Context context;

    public IncomeWizardPage1(ModelCallbacks callbacks, String title, Context context) {
        super(callbacks, title);
        this.context = context;
        mData.putBoolean(WAS_PRELOADED, false);
    }

    @Override
    public Fragment createFragment() {
        return IncomeWizardPage1Fragment.create(getKey());
    }

    @Override
    public void getReviewItems(ArrayList<ReviewItem> dest) {
        dest.add(new ReviewItem(context.getResources().getString(R.string.date), mData.getString(INCOME_DATE_STRING_FORMATTED_DATA_KEY), getKey(), -1));
        dest.add(new ReviewItem(context.getResources().getString(R.string.amount), mData.getString(INCOME_AMOUNT_FORMATTED_STRING_DATA_KEY), getKey(), -1));
        dest.add(new ReviewItem(context.getResources().getString(R.string.type), mData.getString(INCOME_TYPE_DATA_KEY), getKey(), -1));
    }

    @Override
    public boolean isCompleted() {
        return (!TextUtils.isEmpty(mData.getString(INCOME_DATE_STRING_DATA_KEY)) && !TextUtils.isEmpty(mData.getString(INCOME_TYPE_DATA_KEY))
                && !TextUtils.isEmpty(mData.getString(INCOME_AMOUNT_STRING_DATA_KEY)));
    }
}