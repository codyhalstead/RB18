package com.rentbud.wizards;

import android.support.v4.app.Fragment;
import android.text.TextUtils;

import com.example.android.wizardpager.wizard.model.ModelCallbacks;
import com.example.android.wizardpager.wizard.model.Page;
import com.example.android.wizardpager.wizard.model.ReviewItem;
import com.rentbud.activities.NewExpenseWizard;
import com.rentbud.fragments.ExpenseWizardPage1Fragment;
import com.rentbud.model.ExpenseLogEntry;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class ExpenseWizardPage1 extends Page {
    public static final String EXPENSE_DATE_STRING_DATA_KEY = "expense_date";
    public static final String EXPENSE_AMOUNT_FORMATTED_STRING_DATA_KEY = "expense_amount_formatted";
    public static final String EXPENSE_AMOUNT_STRING_DATA_KEY = "expense_amount";
    public static final String EXPENSE_TYPE_ID_DATA_KEY = "expense_type_id";
    public static final String EXPENSE_TYPE_DATA_KEY = "expense_type";

    public ExpenseWizardPage1(ModelCallbacks callbacks, String title) {
        super(callbacks, title);
        ExpenseLogEntry expenseLogEntry = NewExpenseWizard.expenseToEdit;
        if(expenseLogEntry != null){
            SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
            String dateString = formatter.format(expenseLogEntry.getExpenseDate());
            mData.putString(EXPENSE_DATE_STRING_DATA_KEY, dateString);
            BigDecimal amountBD = expenseLogEntry.getAmount();
            String formatted = NumberFormat.getCurrencyInstance().format(amountBD);
            mData.putString(EXPENSE_AMOUNT_FORMATTED_STRING_DATA_KEY, formatted);
            mData.putString(EXPENSE_AMOUNT_STRING_DATA_KEY, amountBD.toPlainString());
            mData.putInt(EXPENSE_TYPE_ID_DATA_KEY, expenseLogEntry.getTypeID());
            mData.putString(EXPENSE_TYPE_DATA_KEY, expenseLogEntry.getTypeLabel());
            this.notifyDataChanged();
        }
    }

    @Override
    public Fragment createFragment() {
        return ExpenseWizardPage1Fragment.create(getKey());
    }

    @Override
    public void getReviewItems(ArrayList<ReviewItem> dest) {
        dest.add(new ReviewItem("Date", mData.getString(EXPENSE_DATE_STRING_DATA_KEY), getKey(), -1));
        dest.add(new ReviewItem("Amount", mData.getString(EXPENSE_AMOUNT_FORMATTED_STRING_DATA_KEY), getKey(), -1));
        dest.add(new ReviewItem("Type", mData.getString(EXPENSE_TYPE_DATA_KEY), getKey(), -1));
    }

    @Override
    public boolean isCompleted() {
        return (!TextUtils.isEmpty(mData.getString(EXPENSE_DATE_STRING_DATA_KEY)) && !TextUtils.isEmpty(mData.getString(EXPENSE_TYPE_DATA_KEY))
                && !TextUtils.isEmpty(mData.getString(EXPENSE_AMOUNT_STRING_DATA_KEY)));
        //return true;
    }
}