package com.rba18.wizards;

import android.content.Context;
import android.support.v4.app.Fragment;

import com.example.android.wizardpager.wizard.model.ModelCallbacks;
import com.example.android.wizardpager.wizard.model.Page;
import com.example.android.wizardpager.wizard.model.ReviewItem;
import com.rba18.R;
import com.rba18.fragments.ApartmentWizardPage3Fragment;

import java.util.ArrayList;

import static android.support.constraint.Constraints.TAG;

public class ApartmentWizardPage3 extends Page {
    public static final String APARTMENT_MAIN_PIC_DATA_KEY = "apartment_main_pic";
    public static final String APARTMENT_OTHER_PICS_DATA_KEY = "apartment_other_pics";
    public static final String APARTMENT_WAS_MAIN_PIC_ADDED_DATA_KEY = "apartment_was_main_pic_added";
    public static final String APARTMENT_AMOUNT_OF_OTHER_PICS_DATA_KEY = "apartment_amount_of_other_pics";
    public static final String WAS_PRELOADED = "apartmant_page_3_was_preloaded";
    private Context context;

    public ApartmentWizardPage3(ModelCallbacks callbacks, String title, Context context) {
        super(callbacks, title);
        this.context = context;
        mData.putBoolean(WAS_PRELOADED, false);
    }

    @Override
    public Fragment createFragment() {
        return ApartmentWizardPage3Fragment.create(getKey());
    }

    @Override
    public void getReviewItems(ArrayList<ReviewItem> dest) {
        dest.add(new ReviewItem(context.getResources().getString(R.string.main_pic), mData.getString(APARTMENT_WAS_MAIN_PIC_ADDED_DATA_KEY), getKey(), -1));
        dest.add(new ReviewItem(context.getResources().getString(R.string.number_of_side_pics), mData.getInt(APARTMENT_AMOUNT_OF_OTHER_PICS_DATA_KEY) + "", getKey(), -1));
    }

    @Override
    public boolean isCompleted() {
        return true;
    }
}
