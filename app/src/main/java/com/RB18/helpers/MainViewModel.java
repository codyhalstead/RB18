package com.RB18.helpers;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.RB18.model.Apartment;
import com.RB18.model.ExpenseLogEntry;
import com.RB18.model.Lease;
import com.RB18.model.PaymentLogEntry;
import com.RB18.model.Tenant;

import java.util.ArrayList;
import java.util.Date;

public class MainViewModel extends ViewModel {
    private MutableLiveData<ArrayList<Tenant>> cachedTenants;
    private MutableLiveData<ArrayList<Apartment>> cachedApartments;
    private MutableLiveData<ArrayList<Lease>> cachedLeases;
    private MutableLiveData<ArrayList<PaymentLogEntry>> cachedIncome;
    private MutableLiveData<ArrayList<ExpenseLogEntry>> cachedExpenses;
    private MutableLiveData<Date> startDateRange;
    private MutableLiveData<Date> endDateRange;
    private MutableLiveData<Integer> homeTabSelection;
    private MutableLiveData<Date> homeTabYearSelected;

    public void init() {
        cachedTenants = new MutableLiveData<>();
        cachedApartments = new MutableLiveData<>();
        cachedLeases = new MutableLiveData<>();
        cachedIncome = new MutableLiveData<>();
        cachedExpenses = new MutableLiveData<>();
        startDateRange = new MutableLiveData<>();
        endDateRange = new MutableLiveData<>();
        homeTabSelection = new MutableLiveData<>();
        homeTabSelection.setValue(0);
        homeTabYearSelected = new MutableLiveData<>();
    }

    public LiveData<ArrayList<Tenant>> getCachedTenants() {
        return cachedTenants;
    }

    public void setCachedTenants(ArrayList<Tenant> cachedTenants) {
        this.cachedTenants.setValue(cachedTenants);
    }

    public LiveData<ArrayList<Apartment>> getCachedApartments() {
        return cachedApartments;
    }

    public void setCachedApartments(ArrayList<Apartment> cachedApartments) {
        this.cachedApartments.setValue(cachedApartments);
    }

    public LiveData<ArrayList<Lease>> getCachedLeases() {
        return cachedLeases;
    }

    public void setCachedLeases(ArrayList<Lease> cachedLeases) {
        this.cachedLeases.setValue(cachedLeases);
    }

    public LiveData<ArrayList<PaymentLogEntry>> getCachedIncome() {
        return cachedIncome;
    }

    public void setCachedIncome(ArrayList<PaymentLogEntry> cachedIncome) {
        this.cachedIncome.setValue(cachedIncome);
    }

    public LiveData<ArrayList<ExpenseLogEntry>> getCachedExpenses() {
        return cachedExpenses;
    }

    public void setCachedExpenses(ArrayList<ExpenseLogEntry> cachedExpenses) {
        this.cachedExpenses.setValue(cachedExpenses);
    }

    public MutableLiveData<Date> getStartDateRangeDate() {
        return startDateRange;
    }

    public void setStartDateRange(Date date) {
        this.startDateRange.setValue(date);
    }

    public MutableLiveData<Date> getEndDateRangeDate() {
        return endDateRange;
    }

    public void setEndDateRange(Date date) {
        this.endDateRange.setValue(date);
    }

    public int getHomeTabSelection() {
        if (homeTabSelection != null) {
            return homeTabSelection.getValue();
        } else {
            return 0;
        }
    }

    public void setHomeTabSelection(int homeTabSelection) {
        this.homeTabSelection.setValue(homeTabSelection);
    }

    public Date getHomeTabYearSelected() {
        return homeTabYearSelected.getValue();
    }

    public void setHomeTabYearSelected(Date homeTabYearSelected) {
        this.homeTabYearSelected.setValue(homeTabYearSelected);
    }
}