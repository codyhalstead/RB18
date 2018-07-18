package com.rentbud.wizards;

import android.support.v4.app.Fragment;
import android.text.TextUtils;

import com.example.android.wizardpager.wizard.model.ModelCallbacks;
import com.example.android.wizardpager.wizard.model.Page;
import com.example.android.wizardpager.wizard.model.ReviewItem;
import com.rentbud.activities.NewIncomeWizard;
import com.rentbud.fragments.IncomeWizardPage1Fragment;
import com.rentbud.model.PaymentLogEntry;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class IncomeWizardPage1 extends Page {
    public static final String INCOME_DATE_STRING_DATA_KEY = "income_date";
    public static final String INCOME_AMOUNT_FORMATTED_STRING_DATA_KEY = "income_amount_formatted";
    public static final String INCOME_AMOUNT_STRING_DATA_KEY = "income_amount";
    public static final String INCOME_TYPE_ID_DATA_KEY = "income_type_id";
    public static final String INCOME_TYPE_DATA_KEY = "income_type";
    public static final String WAS_PRELOADED = "income_page_1_was_preloaded";

    public IncomeWizardPage1(ModelCallbacks callbacks, String title) {
        super(callbacks, title);
        mData.putBoolean(WAS_PRELOADED, false);
        //PaymentLogEntry paymentLogEntry = NewIncomeWizard.incomeToEdit;
        //if(paymentLogEntry != null){
        //    SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
        //    String dateString = formatter.format(paymentLogEntry.getDate());
        //    mData.putString(INCOME_DATE_STRING_DATA_KEY, dateString);
        //    BigDecimal amountBD = paymentLogEntry.getAmount();
        //    String formatted = NumberFormat.getCurrencyInstance().format(amountBD);
        //    mData.putString(INCOME_AMOUNT_FORMATTED_STRING_DATA_KEY, formatted);
        //    mData.putString(INCOME_AMOUNT_STRING_DATA_KEY, amountBD.toPlainString());
        //    mData.putInt(INCOME_TYPE_ID_DATA_KEY, paymentLogEntry.getTypeID());
        //    mData.putString(INCOME_TYPE_DATA_KEY, paymentLogEntry.getTypeLabel());
        //    this.notifyDataChanged();
        //}
    }

    @Override
    public Fragment createFragment() {
        return IncomeWizardPage1Fragment.create(getKey());
    }

    @Override
    public void getReviewItems(ArrayList<ReviewItem> dest) {
        dest.add(new ReviewItem("Date", mData.getString(INCOME_DATE_STRING_DATA_KEY), getKey(), -1));
        dest.add(new ReviewItem("Amount", mData.getString(INCOME_AMOUNT_FORMATTED_STRING_DATA_KEY), getKey(), -1));
        dest.add(new ReviewItem("Type", mData.getString(INCOME_TYPE_DATA_KEY), getKey(), -1));
    }

    @Override
    public boolean isCompleted() {
        return (!TextUtils.isEmpty(mData.getString(INCOME_DATE_STRING_DATA_KEY)) && !TextUtils.isEmpty(mData.getString(INCOME_TYPE_DATA_KEY))
                && !TextUtils.isEmpty(mData.getString(INCOME_AMOUNT_STRING_DATA_KEY)));
        //return true;
    }
}