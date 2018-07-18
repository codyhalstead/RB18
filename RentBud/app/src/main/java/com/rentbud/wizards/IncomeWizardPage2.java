package com.rentbud.wizards;

import android.support.v4.app.Fragment;
import android.text.TextUtils;

import com.example.android.wizardpager.wizard.model.ModelCallbacks;
import com.example.android.wizardpager.wizard.model.Page;
import com.example.android.wizardpager.wizard.model.ReviewItem;
import com.rentbud.activities.NewIncomeWizard;
import com.rentbud.fragments.IncomeWizardPage2Fragment;
import com.rentbud.model.PaymentLogEntry;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class IncomeWizardPage2 extends Page {
    public static final String INCOME_DESCRIPTION_DATA_KEY = "income_description";
    public static final String INCOME_RECEIPT_PIC_DATA_KEY = "income_receipt_pic";
    public static final String INCOME_WAS_RECEIPT_PIC_ADDED_DATA_KEY = "income_was_receipt_pic_added";
    public static final String WAS_PRELOADED = "income_page_2_was_preloaded";

    public IncomeWizardPage2(ModelCallbacks callbacks, String title) {
        super(callbacks, title);
        mData.putBoolean(WAS_PRELOADED, false);
        //PaymentLogEntry paymentLogEntry = NewIncomeWizard.incomeToEdit;
        //if(paymentLogEntry != null){
        //    mData.putString(INCOME_DESCRIPTION_DATA_KEY, paymentLogEntry.getDescription());
        //    if(paymentLogEntry.getReceiptPic() != null) {
        //        if(!paymentLogEntry.getReceiptPic().equals("")){
        //            mData.putString(INCOME_RECEIPT_PIC_DATA_KEY, paymentLogEntry.getReceiptPic());
        //            mData.putString(INCOME_WAS_RECEIPT_PIC_ADDED_DATA_KEY, "Yes");
        //        } else {
        //            mData.putString(INCOME_WAS_RECEIPT_PIC_ADDED_DATA_KEY, "No");
        //        }
        //        mData.putString(INCOME_WAS_RECEIPT_PIC_ADDED_DATA_KEY, "No");
        //    } else {
        //        mData.putString(INCOME_WAS_RECEIPT_PIC_ADDED_DATA_KEY, "No");
        //}
        //    this.notifyDataChanged();
        //}
    }

    @Override
    public Fragment createFragment() {
        return IncomeWizardPage2Fragment.create(getKey());
    }

    @Override
    public void getReviewItems(ArrayList<ReviewItem> dest) {
        dest.add(new ReviewItem("Description", mData.getString(INCOME_DESCRIPTION_DATA_KEY), getKey(), -1));
        dest.add(new ReviewItem("Receipt Picture", mData.getString(INCOME_WAS_RECEIPT_PIC_ADDED_DATA_KEY), getKey(), -1));
    }

    @Override
    public boolean isCompleted() {
        return (!TextUtils.isEmpty(mData.getString(INCOME_DESCRIPTION_DATA_KEY)));
        //return true;
    }
}
