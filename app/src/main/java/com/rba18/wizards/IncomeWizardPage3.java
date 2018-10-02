package com.rba18.wizards;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.example.android.wizardpager.wizard.model.ModelCallbacks;
import com.example.android.wizardpager.wizard.model.Page;
import com.example.android.wizardpager.wizard.model.ReviewItem;
import com.rba18.R;
import com.rba18.activities.NewExpenseWizard;
import com.rba18.activities.NewIncomeWizard;
import com.rba18.fragments.ExpenseWizardPage2Fragment;
import com.rba18.fragments.IncomeWizardPage3Fragment;
import com.rba18.helpers.MainArrayDataMethods;
import com.rba18.model.Apartment;
import com.rba18.model.ExpenseLogEntry;
import com.rba18.model.Lease;
import com.rba18.model.PaymentLogEntry;
import com.rba18.model.Tenant;

import java.util.ArrayList;

import static android.support.constraint.Constraints.TAG;

public class IncomeWizardPage3 extends Page {
    public static final String INCOME_RELATED_APT_DATA_KEY = "income_related_apt";
    public static final String INCOME_RELATED_APT_TEXT_DATA_KEY = "income_related_apt_text";
    public static final String INCOME_RELATED_APT_ID_DATA_KEY = "income_related_apt_id";

    public static final String INCOME_RELATED_TENANT_DATA_KEY = "income_related_tenant";
    public static final String INCOME_RELATED_TENANT_TEXT_DATA_KEY = "income_related_tenant_text";
    public static final String INCOME_RELATED_TENANT_ID_DATA_KEY = "income_related_tenant_id";

    public static final String INCOME_RELATED_LEASE_DATA_KEY = "income_related_lease";
    public static final String INCOME_RELATED_LEASE_TEXT_DATA_KEY = "income_related_lease_text";
    public static final String INCOME_RELATED_LEASE_ID_DATA_KEY = "income_related_lease_id";
    public static final String WAS_PRELOADED = "income_page_3_was_preloaded";
    private Context context;

    public IncomeWizardPage3(ModelCallbacks callbacks, String title, Context context) {
        super(callbacks, title);
        this.context = context;
        mData.putBoolean(WAS_PRELOADED, false);
        //PaymentLogEntry paymentLogEntry = NewIncomeWizard.incomeToEdit;

        //if (paymentLogEntry != null) {
        //    MainArrayDataMethods dataMethods = new MainArrayDataMethods();
            //TODO add empty value names
            //String apartmentString = "";
            //String tenantString = "";
            //String leaseString = "";
        //    if (paymentLogEntry.getApartmentID() != 0) {
                //Apartment apartment = dataMethods.getCachedApartmentByApartmentID(paymentLogEntry.getApartmentID());
                //mData.putParcelable(INCOME_RELATED_APT_DATA_KEY, apartment);
                //if (apartment != null) {
                //    apartmentString = apartment.getStreet1();
                //    if (apartment.getStreet2() != null) {
                //        apartmentString += " ";
                //        apartmentString += apartment.getStreet2();
                //    }
        //        mData.putInt(INCOME_RELATED_APT_ID_DATA_KEY, paymentLogEntry.getApartmentID());
        //    }
            //mData.putString(INCOME_RELATED_APT_TEXT_DATA_KEY, apartmentString);
        //    if (paymentLogEntry.getTenantID() != 0) {
                //Tenant tenant = dataMethods.getCachedTenantByTenantID(paymentLogEntry.getTenantID());
                //mData.putParcelable(INCOME_RELATED_TENANT_DATA_KEY, tenant);
                //if (tenant != null) {
                //    tenantString = tenant.getFirstName();
                //    tenantString += " ";
                //    tenantString += tenant.getLastName();
                //}
        //        mData.putInt(INCOME_RELATED_TENANT_ID_DATA_KEY, paymentLogEntry.getTenantID());
        //    }
            //mData.putString(INCOME_RELATED_TENANT_TEXT_DATA_KEY, tenantString);
        //    if (paymentLogEntry.getLeaseID() != 0) {
                //Lease lease = null;
                //mData.putParcelable(INCOME_RELATED_LEASE_DATA_KEY, lease);
                //if (lease != null) {
                //    leaseString = "";
                //}
        //        mData.putInt(INCOME_RELATED_LEASE_ID_DATA_KEY, paymentLogEntry.getLeaseID());

        //    }
            //mData.putString(INCOME_RELATED_LEASE_TEXT_DATA_KEY, leaseString);
        //    this.notifyDataChanged();
        //}
    }

    @Override
    public Fragment createFragment() {
        return IncomeWizardPage3Fragment.create(getKey());
    }

    @Override
    public void getReviewItems(ArrayList<ReviewItem> dest) {
        dest.add(new ReviewItem(context.getResources().getString(R.string.related_apt), mData.getString(INCOME_RELATED_APT_TEXT_DATA_KEY), getKey(), -1));
        dest.add(new ReviewItem(context.getResources().getString(R.string.related_tenant), mData.getString(INCOME_RELATED_TENANT_TEXT_DATA_KEY), getKey(), -1));
        dest.add(new ReviewItem(context.getResources().getString(R.string.related_lease), mData.getString(INCOME_RELATED_LEASE_TEXT_DATA_KEY), getKey(), -1));
    }

    @Override
    public boolean isCompleted() {
        return true;
    }
}