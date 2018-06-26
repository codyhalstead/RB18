package com.rentbud.wizards;

import android.support.v4.app.Fragment;
import android.text.TextUtils;

import com.example.android.wizardpager.wizard.model.ModelCallbacks;
import com.example.android.wizardpager.wizard.model.Page;
import com.example.android.wizardpager.wizard.model.ReviewItem;
import com.rentbud.activities.NewExpenseWizard;
import com.rentbud.fragments.ExpenseWizardPage2Fragment;
import com.rentbud.fragments.ExpenseWizardPage3Fragment;
import com.rentbud.helpers.MainArrayDataMethods;
import com.rentbud.model.Apartment;
import com.rentbud.model.ExpenseLogEntry;

import java.util.ArrayList;

public class ExpenseWizardPage3 extends Page {
    public static final String EXPENSE_RELATED_APT_DATA_KEY = "expense_related_apt";
    public static final String EXPENSE_RELATED_APT_TEXT_DATA_KEY = "expense_related_apt_text";

    public ExpenseWizardPage3(ModelCallbacks callbacks, String title) {
        super(callbacks, title);
        ExpenseLogEntry expenseLogEntry = NewExpenseWizard.expenseToEdit;
        if(expenseLogEntry != null){

            MainArrayDataMethods dataMethods = new MainArrayDataMethods();
            //TODO add empty value name
            String apartmentString = "";
            if(expenseLogEntry.getApartmentID() != 0) {
                Apartment apartment = dataMethods.getCachedApartmentByApartmentID(expenseLogEntry.getApartmentID());
                mData.putParcelable(EXPENSE_RELATED_APT_DATA_KEY, apartment);
                if (apartment != null) {
                    apartmentString = apartment.getStreet1();
                    if (apartment.getStreet2() != null) {
                        apartmentString += " ";
                        apartmentString += apartment.getStreet2();
                    }
                }
            }
            mData.putString(EXPENSE_RELATED_APT_TEXT_DATA_KEY, apartmentString);
            this.notifyDataChanged();
        }
    }

    @Override
    public Fragment createFragment() {
        return ExpenseWizardPage3Fragment.create(getKey());
    }

    @Override
    public void getReviewItems(ArrayList<ReviewItem> dest) {
        dest.add(new ReviewItem("Related Apartment", mData.getString(EXPENSE_RELATED_APT_TEXT_DATA_KEY), getKey(), -1));
    }

    @Override
    public boolean isCompleted() {
        return true;
    }
}