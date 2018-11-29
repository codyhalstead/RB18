package com.rba18.helpers;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.rba18.model.Apartment;
import com.rba18.model.ExpenseLogEntry;
import com.rba18.model.Lease;
import com.rba18.model.PaymentLogEntry;
import com.rba18.model.Tenant;

import java.util.ArrayList;
import java.util.Date;

public class MainViewModel extends ViewModel {
    private MutableLiveData<ArrayList<Tenant>> mCachedTenants;
    private MutableLiveData<ArrayList<Apartment>> mCachedApartments;
    private MutableLiveData<ArrayList<Lease>> mCachedLeases;
    private MutableLiveData<ArrayList<PaymentLogEntry>> mCachedIncome;
    private MutableLiveData<ArrayList<ExpenseLogEntry>> mCachedExpenses;
    private MutableLiveData<Date> mStartDateRange;
    private MutableLiveData<Date> mEndDateRange;
    private MutableLiveData<Integer> mHomeTabSelection;
    private MutableLiveData<Date> mHomeTabYearSelected;

    public void init() {
        mCachedTenants = new MutableLiveData<>();
        mCachedApartments = new MutableLiveData<>();
        mCachedLeases = new MutableLiveData<>();
        mCachedIncome = new MutableLiveData<>();
        mCachedExpenses = new MutableLiveData<>();
        mStartDateRange = new MutableLiveData<>();
        mEndDateRange = new MutableLiveData<>();
        mHomeTabSelection = new MutableLiveData<>();
        mHomeTabSelection.setValue(0);
        mHomeTabYearSelected = new MutableLiveData<>();
    }

    public LiveData<ArrayList<Tenant>> getCachedTenants() {
        return mCachedTenants;
    }

    public void setCachedTenants(ArrayList<Tenant> cachedTenants) {
        mCachedTenants.setValue(cachedTenants);
    }

    public LiveData<ArrayList<Apartment>> getCachedApartments() {
        return mCachedApartments;
    }

    public void setCachedApartments(ArrayList<Apartment> cachedApartments) {
        mCachedApartments.setValue(cachedApartments);
    }

    public LiveData<ArrayList<Lease>> getCachedLeases() {
        return mCachedLeases;
    }

    public void setCachedLeases(ArrayList<Lease> cachedLeases) {
        mCachedLeases.setValue(cachedLeases);
    }

    public LiveData<ArrayList<PaymentLogEntry>> getCachedIncome() {
        return mCachedIncome;
    }

    public void setCachedIncome(ArrayList<PaymentLogEntry> cachedIncome) {
        mCachedIncome.setValue(cachedIncome);
    }

    public LiveData<ArrayList<ExpenseLogEntry>> getCachedExpenses() {
        return mCachedExpenses;
    }

    public void setCachedExpenses(ArrayList<ExpenseLogEntry> cachedExpenses) {
        mCachedExpenses.setValue(cachedExpenses);
    }

    public MutableLiveData<Date> getStartDateRangeDate() {
        return mStartDateRange;
    }

    public void setStartDateRange(Date date) {
        mStartDateRange.setValue(date);
    }

    public MutableLiveData<Date> getEndDateRangeDate() {
        return mEndDateRange;
    }

    public void setEndDateRange(Date date) {
        mEndDateRange.setValue(date);
    }

    public int getHomeTabSelection() {
        if (mHomeTabSelection != null) {
            return mHomeTabSelection.getValue();
        } else {
            return 0;
        }
    }

    public void setHomeTabSelection(int homeTabSelection) {
        mHomeTabSelection.setValue(homeTabSelection);
    }

    public Date getHomeTabYearSelected() {
        return mHomeTabYearSelected.getValue();
    }

    public void setHomeTabYearSelected(Date homeTabYearSelected) {
        mHomeTabYearSelected.setValue(homeTabYearSelected);
    }
}