package com.rba18.helpers;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.rba18.model.Apartment;
import com.rba18.model.Lease;
import com.rba18.model.MoneyLogEntry;
import com.rba18.model.Tenant;

import java.util.ArrayList;
import java.util.Date;

public class ApartmentTenantViewModel extends ViewModel {
    private MutableLiveData<Apartment> mApartment;
    private MutableLiveData<Lease> mLease;
    private MutableLiveData<Tenant> mPrimaryTenant;
    private MutableLiveData<ArrayList<Tenant>> mSecondaryTenants;
    private MutableLiveData<ArrayList<MoneyLogEntry>> mMoneyArray;
    private MutableLiveData<ArrayList<Lease>> mLeaseArray;
    private MutableLiveData<Tenant> mViewedTenant;
    private MutableLiveData<Date> mDate;

    public void init(){
        mApartment = new MutableLiveData<>();
        mLease = new MutableLiveData<>();
        mPrimaryTenant = new MutableLiveData<>();
        mSecondaryTenants = new MutableLiveData<>();
        mMoneyArray = new MutableLiveData<>();
        mLeaseArray = new MutableLiveData<>();
        mViewedTenant = new MutableLiveData<>();
        mDate = new MutableLiveData<>();
    }

    public LiveData<Apartment> getApartment() {
        return mApartment;
    }

    public void setApartment(Apartment apartment){
        mApartment.setValue(apartment);
    }

    public LiveData<Lease> getLease(){
        return mLease;
    }

    public void setLease(Lease lease){
        mLease.setValue(lease);
    }

    public MutableLiveData<Tenant> getPrimaryTenant() {
        return mPrimaryTenant;
    }

    public void setPrimaryTenant(Tenant primaryTenant) {
        mPrimaryTenant.setValue(primaryTenant);
    }

    public MutableLiveData<ArrayList<Tenant>> getSecondaryTenants() {
        return mSecondaryTenants;
    }

    public void setSecondaryTenants(ArrayList<Tenant> secondaryTenants) {
        mSecondaryTenants.setValue(secondaryTenants);
    }

    public MutableLiveData<ArrayList<MoneyLogEntry>> getMoneyArray() {
        return mMoneyArray;
    }

    public void setMoneyArray(ArrayList<MoneyLogEntry> moneyArray) {
        mMoneyArray.setValue(moneyArray);
    }

    public MutableLiveData<ArrayList<Lease>> getLeaseArray() {
        return mLeaseArray;
    }

    public void setLeaseArray(ArrayList<Lease> leaseArray) {
        mLeaseArray.setValue(leaseArray);
    }

    public MutableLiveData<Tenant> getViewedTenant() {
        return mViewedTenant;
    }

    public void setViewedTenant(Tenant viewedTenant) {
        mViewedTenant.setValue(viewedTenant);
    }

    public MutableLiveData<Date> getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate.setValue(date);
    }
}
