package com.rba18.wizards;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.text.TextUtils;

import com.example.android.wizardpager.wizard.model.ModelCallbacks;
import com.example.android.wizardpager.wizard.model.Page;
import com.example.android.wizardpager.wizard.model.ReviewItem;
import com.rba18.R;
import com.rba18.fragments.ExpenseWizardPage2Fragment;

import java.util.ArrayList;

public class ExpenseWizardPage2  extends Page {
    public static final String EXPENSE_DESCRIPTION_DATA_KEY = "expense_description";
    public static final String EXPENSE_RECEIPT_PIC_DATA_KEY = "expense_receipt_pic";
    public static final String EXPENSE_WAS_RECEIPT_PIC_ADDED_DATA_KEY = "expense_was_receipt_pic_added";
    public static final String WAS_PRELOADED = "expense_page_2_was_preloaded";
    private boolean isEdit;
    private Context context;

    public ExpenseWizardPage2(ModelCallbacks callbacks, String title, boolean isEdit, Context context) {
        super(callbacks, title);
        this.isEdit = isEdit;
        this.context = context;
        mData.putBoolean(WAS_PRELOADED, false);
    }

    @Override
    public Fragment createFragment() {
        return ExpenseWizardPage2Fragment.create(getKey());
    }

    @Override
    public void getReviewItems(ArrayList<ReviewItem> dest) {
        dest.add(new ReviewItem(context.getResources().getString(R.string.description), mData.getString(EXPENSE_DESCRIPTION_DATA_KEY), getKey(), -1));
        if(!isEdit) {
            dest.add(new ReviewItem(context.getResources().getString(R.string.receipt_pic), mData.getString(EXPENSE_WAS_RECEIPT_PIC_ADDED_DATA_KEY), getKey(), -1));
        }
    }

    @Override
    public boolean isCompleted() {
        return (!TextUtils.isEmpty(mData.getString(EXPENSE_DESCRIPTION_DATA_KEY)));
    }
}