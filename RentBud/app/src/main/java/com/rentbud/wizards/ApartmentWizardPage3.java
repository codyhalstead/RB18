package com.rentbud.wizards;

import android.support.v4.app.Fragment;
import android.util.Log;

import com.example.android.wizardpager.wizard.model.ModelCallbacks;
import com.example.android.wizardpager.wizard.model.Page;
import com.example.android.wizardpager.wizard.model.ReviewItem;
import com.rentbud.activities.NewApartmentWizard;
import com.rentbud.fragments.ApartmentWizardPage3Fragment;
import com.rentbud.model.Apartment;

import java.util.ArrayList;

import static android.support.constraint.Constraints.TAG;

public class ApartmentWizardPage3 extends Page {
    public static final String APARTMENT_MAIN_PIC_DATA_KEY = "apartment_main_pic";
    public static final String APARTMENT_OTHER_PICS_DATA_KEY = "apartment_other_pics";
    public static final String APARTMENT_WAS_MAIN_PIC_ADDED_DATA_KEY = "apartment_was_main_pic_added";
    public static final String APARTMENT_AMOUNT_OF_OTHER_PICS_DATA_KEY = "apartment_amount_of_other_pics";

    public ApartmentWizardPage3(ModelCallbacks callbacks, String title) {
        super(callbacks, title);
        Apartment apartment = NewApartmentWizard.apartmentToEdit;
        // Log.d(TAG, "TenantWizardPage1: " + tenant.getFirstName());
        if(apartment != null){
            if(apartment.getMainPic() != null) {
                Log.d(TAG, "ApartmentWizardPage3: " + apartment.getMainPic());
                if(!apartment.getMainPic().equals("")){
                    mData.putString(APARTMENT_MAIN_PIC_DATA_KEY, apartment.getMainPic());
                    mData.putString(APARTMENT_WAS_MAIN_PIC_ADDED_DATA_KEY, "Yes");
                } else {
                    mData.putString(APARTMENT_WAS_MAIN_PIC_ADDED_DATA_KEY, "No");
                }
                mData.putString(APARTMENT_WAS_MAIN_PIC_ADDED_DATA_KEY, "No");
            } else {
                mData.putString(APARTMENT_WAS_MAIN_PIC_ADDED_DATA_KEY, "No");
            }
            if(apartment.getOtherPics() != null){
                mData.putStringArrayList(APARTMENT_OTHER_PICS_DATA_KEY, apartment.getOtherPics());
                mData.putInt(APARTMENT_AMOUNT_OF_OTHER_PICS_DATA_KEY, apartment.getOtherPics().size());
            }
            this.notifyDataChanged();
        }
    }

    @Override
    public Fragment createFragment() {
        return ApartmentWizardPage3Fragment.create(getKey());
    }

    @Override
    public void getReviewItems(ArrayList<ReviewItem> dest) {
        dest.add(new ReviewItem("Main Picture", mData.getString(APARTMENT_WAS_MAIN_PIC_ADDED_DATA_KEY), getKey(), -1));
        dest.add(new ReviewItem("Number Of Side Pictures", mData.getInt(APARTMENT_AMOUNT_OF_OTHER_PICS_DATA_KEY) + "", getKey(), -1));
    }

    @Override
    public boolean isCompleted() {
        return true;
    }
}
