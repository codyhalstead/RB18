package com.rentbud.wizards;

import android.support.v4.app.Fragment;

import com.example.android.wizardpager.wizard.model.ModelCallbacks;
import com.example.android.wizardpager.wizard.model.Page;
import com.example.android.wizardpager.wizard.model.ReviewItem;
import com.rentbud.activities.NewApartmentWizard;
import com.rentbud.fragments.ApartmentWizardPage2Fragment;
import com.rentbud.model.Apartment;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;

public class ApartmentWizardPage2 extends Page {
    public static final String APARTMENT_DESCRIPTION_DATA_KEY = "apartment_description";
    public static final String APARTMENT_NOTES_DATA_KEY = "apartment_notes";
    public static final String APARTMENT_PREFERRED_RENT_COST_FORMATTED_STRING_DATA_KEY = "apartment_preferred_rent_cost_formatted";
    public static final String APARTMENT_PREFERRED_RENT_COST_DATA_KEY = "apartment_preferred_rent_cost";

    public ApartmentWizardPage2(ModelCallbacks callbacks, String title) {
        super(callbacks, title);
        Apartment apartment = NewApartmentWizard.apartmentToEdit;
        // Log.d(TAG, "TenantWizardPage1: " + tenant.getFirstName());
        if (apartment != null) {
            mData.putString(APARTMENT_DESCRIPTION_DATA_KEY, apartment.getDescription());
            mData.putString(APARTMENT_NOTES_DATA_KEY, apartment.getNotes());
            //TODO preferred rent
            // BigDecimal amountBD = apartment.getPreferredRentCost;
            //String formatted = NumberFormat.getCurrencyInstance().format(amountBD);
            //mData.putString(APARTMENT_PREFERRED_RENT_COST_FORMATTED_STRING_DATA_KEY, apartment.getCity());
            //mData.putString(APARTMENT_PREFERRED_RENT_COST_DATA_KEY, apartment.getState());
            this.notifyDataChanged();
        }
    }

    @Override
    public Fragment createFragment() {
        return ApartmentWizardPage2Fragment.create(getKey());
    }

    @Override
    public void getReviewItems(ArrayList<ReviewItem> dest) {
        dest.add(new ReviewItem("Preferred Rent Cost", mData.getString(APARTMENT_PREFERRED_RENT_COST_FORMATTED_STRING_DATA_KEY), getKey(), -1));
        dest.add(new ReviewItem("Description", mData.getString(APARTMENT_DESCRIPTION_DATA_KEY), getKey(), -1));
        dest.add(new ReviewItem("Notes", mData.getString(APARTMENT_NOTES_DATA_KEY), getKey(), -1));
    }

    @Override
    public boolean isCompleted() {
        return true;
    }
}
