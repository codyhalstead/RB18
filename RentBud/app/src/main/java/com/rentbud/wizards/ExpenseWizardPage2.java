package com.rentbud.wizards;

import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;

import com.example.android.wizardpager.wizard.model.ModelCallbacks;
import com.example.android.wizardpager.wizard.model.Page;
import com.example.android.wizardpager.wizard.model.ReviewItem;
import com.rentbud.activities.NewExpenseWizard;
import com.rentbud.fragments.ExpenseWizardPage2Fragment;
import com.rentbud.model.ExpenseLogEntry;

import java.util.ArrayList;

import static android.support.constraint.Constraints.TAG;

public class ExpenseWizardPage2  extends Page {
    public static final String EXPENSE_DESCRIPTION_DATA_KEY = "expense_description";
    public static final String EXPENSE_RECEIPT_PIC_DATA_KEY = "expense_receipt_pic";
    public static final String EXPENSE_WAS_RECEIPT_PIC_ADDED_DATA_KEY = "expense_was_receipt_pic_added";
    public static final String WAS_PRELOADED = "expense_page_2_was_preloaded";

    public ExpenseWizardPage2(ModelCallbacks callbacks, String title) {
        super(callbacks, title);
        mData.putBoolean(WAS_PRELOADED, false);
        //ExpenseLogEntry expenseLogEntry = NewExpenseWizard.expenseToEdit;
        //if(expenseLogEntry != null){
        //    mData.putString(EXPENSE_DESCRIPTION_DATA_KEY, expenseLogEntry.getDescription());
        //    if(expenseLogEntry.getReceiptPic() != null) {
        //        if(!expenseLogEntry.getReceiptPic().equals("")){
        //            mData.putString(EXPENSE_RECEIPT_PIC_DATA_KEY, expenseLogEntry.getReceiptPic());
        //            mData.putString(EXPENSE_WAS_RECEIPT_PIC_ADDED_DATA_KEY, "Yes");
        //        } else {
        //            mData.putString(EXPENSE_WAS_RECEIPT_PIC_ADDED_DATA_KEY, "No");
        //        }
        //        mData.putString(EXPENSE_WAS_RECEIPT_PIC_ADDED_DATA_KEY, "No");
        //    } else {
        //        mData.putString(EXPENSE_WAS_RECEIPT_PIC_ADDED_DATA_KEY, "No");
        //    }
        //    this.notifyDataChanged();
        //}
    }

    @Override
    public Fragment createFragment() {
        return ExpenseWizardPage2Fragment.create(getKey());
    }

    @Override
    public void getReviewItems(ArrayList<ReviewItem> dest) {
        dest.add(new ReviewItem("Description", mData.getString(EXPENSE_DESCRIPTION_DATA_KEY), getKey(), -1));
        dest.add(new ReviewItem("Receipt Picture", mData.getString(EXPENSE_WAS_RECEIPT_PIC_ADDED_DATA_KEY), getKey(), -1));
    }

    @Override
    public boolean isCompleted() {
        return (!TextUtils.isEmpty(mData.getString(EXPENSE_DESCRIPTION_DATA_KEY)));
        //return true;
    }
}