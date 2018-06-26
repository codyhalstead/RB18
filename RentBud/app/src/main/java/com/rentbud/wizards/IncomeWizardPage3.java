package com.rentbud.wizards;

import android.support.v4.app.Fragment;

import com.example.android.wizardpager.wizard.model.ModelCallbacks;
import com.example.android.wizardpager.wizard.model.Page;
import com.example.android.wizardpager.wizard.model.ReviewItem;
import com.rentbud.activities.NewExpenseWizard;
import com.rentbud.activities.NewIncomeWizard;
import com.rentbud.fragments.ExpenseWizardPage2Fragment;
import com.rentbud.fragments.IncomeWizardPage3Fragment;
import com.rentbud.helpers.MainArrayDataMethods;
import com.rentbud.model.Apartment;
import com.rentbud.model.ExpenseLogEntry;
import com.rentbud.model.Lease;
import com.rentbud.model.PaymentLogEntry;
import com.rentbud.model.Tenant;

import java.util.ArrayList;

public class IncomeWizardPage3 extends Page {
    public static final String INCOME_RELATED_APT_DATA_KEY = "income_related_apt";
    public static final String INCOME_RELATED_APT_TEXT_DATA_KEY = "income_related_apt_text";

    public static final String INCOME_RELATED_TENANT_DATA_KEY = "income_related_tenant";
    public static final String INCOME_RELATED_TENANT_TEXT_DATA_KEY = "income_related_tenant_text";

    public static final String INCOME_RELATED_LEASE_DATA_KEY = "income_related_lease";
    public static final String INCOME_RELATED_LEASE_TEXT_DATA_KEY = "income_related_lease_text";

    public IncomeWizardPage3(ModelCallbacks callbacks, String title) {
        super(callbacks, title);
        PaymentLogEntry paymentLogEntry = NewIncomeWizard.incomeToEdit;
        if(paymentLogEntry != null){
            MainArrayDataMethods dataMethods = new MainArrayDataMethods();
            //TODO add empty value names
            String apartmentString = "";
            String tenantString = "";
            String leaseString = "";
            if(paymentLogEntry.getApartmentID() != 0) {
                Apartment apartment = dataMethods.getCachedApartmentByApartmentID(paymentLogEntry.getApartmentID());
                mData.putParcelable(INCOME_RELATED_APT_DATA_KEY, apartment);
                if (apartment != null) {
                    apartmentString = apartment.getStreet1();
                    if (apartment.getStreet2() != null) {
                        apartmentString += " ";
                        apartmentString += apartment.getStreet2();
                    }
                }
            }
            mData.putString(INCOME_RELATED_APT_TEXT_DATA_KEY, apartmentString);
            if(paymentLogEntry.getTenantID() != 0) {
                Tenant tenant = dataMethods.getCachedTenantByTenantID(paymentLogEntry.getTenantID());
                mData.putParcelable(INCOME_RELATED_TENANT_DATA_KEY, tenant);
                if (tenant != null) {
                    tenantString = tenant.getFirstName();
                    tenantString += " ";
                    tenantString += tenant.getLastName();
                }
            }
            mData.putString(INCOME_RELATED_TENANT_TEXT_DATA_KEY, tenantString);
            if(paymentLogEntry.getLeaseID() != 0) {
                Lease lease = null;
                mData.putParcelable(INCOME_RELATED_LEASE_DATA_KEY, lease);
                if(lease != null){
                    leaseString = "";
                }
                //TODO get lease text and lease object
            }
            mData.putString(INCOME_RELATED_LEASE_TEXT_DATA_KEY, leaseString);
            this.notifyDataChanged();
        }
    }

    @Override
    public Fragment createFragment() {
        return IncomeWizardPage3Fragment.create(getKey());
    }

    @Override
    public void getReviewItems(ArrayList<ReviewItem> dest) {
        dest.add(new ReviewItem("Related Apartment", mData.getString(INCOME_RELATED_APT_TEXT_DATA_KEY), getKey(), -1));
        dest.add(new ReviewItem("Related Tenant", mData.getString(INCOME_RELATED_TENANT_TEXT_DATA_KEY), getKey(), -1));
        dest.add(new ReviewItem("Related Lease", mData.getString(INCOME_RELATED_LEASE_TEXT_DATA_KEY), getKey(), -1));
    }

    @Override
    public boolean isCompleted() {
        return true;
    }
}