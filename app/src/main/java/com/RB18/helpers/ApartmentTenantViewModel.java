package com.RB18.helpers;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.RB18.model.Apartment;
import com.RB18.model.Lease;
import com.RB18.model.MoneyLogEntry;
import com.RB18.model.Tenant;

import java.util.ArrayList;
import java.util.Date;

public class ApartmentTenantViewModel extends ViewModel {
    private MutableLiveData<Apartment> apartment;
    private MutableLiveData<Lease> lease;
    private MutableLiveData<Tenant> primaryTenant;
    private MutableLiveData<ArrayList<Tenant>> secondaryTenants;
    private MutableLiveData<ArrayList<MoneyLogEntry>> moneyArray;
    private MutableLiveData<ArrayList<Lease>> leaseArray;
    private MutableLiveData<Tenant> viewedTenant;
    private MutableLiveData<Date> date;

    public void init(){
        apartment = new MutableLiveData<>();
        lease = new MutableLiveData<>();
        primaryTenant = new MutableLiveData<>();
        secondaryTenants = new MutableLiveData<>();
        moneyArray = new MutableLiveData<>();
        leaseArray = new MutableLiveData<>();
        viewedTenant = new MutableLiveData<>();
        date = new MutableLiveData<>();
    }

    public LiveData<Apartment> getApartment() {
        return apartment;
    }

    public void setApartment(Apartment apartment){
        this.apartment.setValue(apartment);
    }

    public LiveData<Lease> getLease(){
        return lease;
    }

    public void setLease(Lease lease){
        this.lease.setValue(lease);
    }

    public MutableLiveData<Tenant> getPrimaryTenant() {
        return primaryTenant;
    }

    public void setPrimaryTenant(Tenant primaryTenant) {
        this.primaryTenant.setValue(primaryTenant);
    }

    public MutableLiveData<ArrayList<Tenant>> getSecondaryTenants() {
        return secondaryTenants;
    }

    public void setSecondaryTenants(ArrayList<Tenant> secondaryTenants) {
        this.secondaryTenants.setValue(secondaryTenants);
    }

    public MutableLiveData<ArrayList<MoneyLogEntry>> getMoneyArray() {
        return moneyArray;
    }

    public void setMoneyArray(ArrayList<MoneyLogEntry> moneyArray) {
        this.moneyArray.setValue(moneyArray);
    }

    public MutableLiveData<ArrayList<Lease>> getLeaseArray() {
        return leaseArray;
    }

    public void setLeaseArray(ArrayList<Lease> leaseArray) {
        this.leaseArray.setValue(leaseArray);
    }

    public MutableLiveData<Tenant> getViewedTenant() {
        return viewedTenant;
    }

    public void setViewedTenant(Tenant viewedTenant) {
        this.viewedTenant.setValue(viewedTenant);
    }

    public MutableLiveData<Date> getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date.setValue(date);
    }
}
