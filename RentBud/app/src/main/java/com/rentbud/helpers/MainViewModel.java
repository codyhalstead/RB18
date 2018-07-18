package com.rentbud.helpers;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.rentbud.model.Apartment;
import com.rentbud.model.ExpenseLogEntry;
import com.rentbud.model.Lease;
import com.rentbud.model.MoneyLogEntry;
import com.rentbud.model.PaymentLogEntry;
import com.rentbud.model.Tenant;

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

    public void init(){
        cachedTenants = new MutableLiveData<>();
        cachedApartments = new MutableLiveData<>();
        cachedLeases = new MutableLiveData<>();
        cachedIncome = new MutableLiveData<>();
        cachedExpenses = new MutableLiveData<>();
        startDateRange = new MutableLiveData<>();
        endDateRange = new MutableLiveData<>();
    }

    public LiveData<ArrayList<Tenant>> getCachedTenants() {
        return cachedTenants;
    }

    public void setCachedTenants(ArrayList<Tenant> cachedTenants){
        this.cachedTenants.setValue(cachedTenants);
    }

    public LiveData<ArrayList<Apartment>> getCachedApartments() {
        return cachedApartments;
    }

    public void setCachedApartments(ArrayList<Apartment> cachedApartments){
        this.cachedApartments.setValue(cachedApartments);
    }

    public LiveData<ArrayList<Lease>> getCachedLeases() {
        return cachedLeases;
    }

    public void setCachedLeases(ArrayList<Lease> cachedLeases){
        this.cachedLeases.setValue(cachedLeases);
    }

    public LiveData<ArrayList<PaymentLogEntry>> getCachedIncome() {
        return cachedIncome;
    }

    public void setCachedIncome(ArrayList<PaymentLogEntry> cachedIncome){
        this.cachedIncome.setValue(cachedIncome);
    }

    public LiveData<ArrayList<ExpenseLogEntry>> getCachedExpenses() {
        return cachedExpenses;
    }

    public void setCachedExpenses(ArrayList<ExpenseLogEntry> cachedExpenses){
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
}