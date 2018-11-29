package com.rba18.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.rba18.helpers.DateAndCurrencyDisplayer;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Cody on 4/11/2018.
 */

public class Lease implements Parcelable {
    private int mID;
    private int mPrimaryTenantID;
    private ArrayList<Integer> mSecondaryTenantIDs;
    private int mApartmentID;
    private Date mLeaseStart;
    private Date mLeaseEnd;
    private int mPaymentDayID;
    private BigDecimal mMonthlyRentCost;
    private BigDecimal mDeposit;
    private int mPaymentFrequencyID;
    private String mNotes;

    public Lease(int id, int primaryTenantID, ArrayList<Integer> secondaryTenantIDs, int apartmentID, Date leaseStart, Date leaseEnd,
                 int paymentDayID, BigDecimal monthlyRentCost, BigDecimal deposit, int paymentFrequencyID, String notes) {
        mID = id;
        mPrimaryTenantID = primaryTenantID;
        mSecondaryTenantIDs = secondaryTenantIDs;
        mApartmentID = apartmentID;
        mLeaseStart = leaseStart;
        mLeaseEnd = leaseEnd;
        mPaymentDayID = paymentDayID;
        mMonthlyRentCost = monthlyRentCost;
        mDeposit = deposit;
        mPaymentFrequencyID = paymentFrequencyID;
        mNotes = notes;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(mID);
        parcel.writeInt(mPrimaryTenantID);
        if (mSecondaryTenantIDs != null) {
            parcel.writeInt(mSecondaryTenantIDs.size());
            parcel.writeList(mSecondaryTenantIDs);
        } else {
            parcel.writeInt(-1);
        }
        parcel.writeInt(mApartmentID);
        if(mLeaseStart != null){
            parcel.writeInt(1);
            parcel.writeLong(mLeaseStart.getTime());
        }else{
            parcel.writeInt(0);
        }
        if(mLeaseEnd != null){
            parcel.writeInt(1);
            parcel.writeLong(mLeaseEnd.getTime());
        }else{
            parcel.writeInt(0);
        }
        parcel.writeInt(mPaymentDayID);
        String amountString = mMonthlyRentCost.toPlainString();
        parcel.writeString(amountString);
        amountString = mDeposit.toPlainString();
        parcel.writeString(amountString);
        parcel.writeInt(mPaymentFrequencyID);
        parcel.writeString(mNotes);
    }

    public Lease(Parcel parcel) {
        mID = parcel.readInt();
        mPrimaryTenantID = parcel.readInt();
        int size = parcel.readInt();
        if(size > -1){
            mSecondaryTenantIDs = parcel.readArrayList(null);
        } else {
            mSecondaryTenantIDs = new ArrayList<>();
        }
        mApartmentID = parcel.readInt();
        int leaseStartDateIsNotNull = parcel.readInt();
        if(leaseStartDateIsNotNull == 1) {
            mLeaseStart = new Date(parcel.readLong());
        } else {
            mLeaseStart = null;
        }
        int leaseEndDateIsNotNull = parcel.readInt();
        if(leaseEndDateIsNotNull == 1) {
            mLeaseEnd = new Date(parcel.readLong());
        } else {
            mLeaseEnd = null;
        }
        mPaymentDayID = parcel.readInt();
        String amountString = parcel.readString();
        mMonthlyRentCost = new BigDecimal(amountString);
        amountString = parcel.readString();
        mDeposit = new BigDecimal(amountString);
        mPaymentFrequencyID = parcel.readInt();
        mNotes = parcel.readString();
    }

    public static final Parcelable.Creator<Lease> CREATOR
            = new Parcelable.Creator<Lease>() {


        @Override
        public Lease createFromParcel(Parcel source) {
            return new Lease(source);

        }

        @Override
        public Lease[] newArray(int size) {
            return new Lease[size];
        }
    };

    public int getId() {
        return mID;
    }

    public void setId(int id) {
        mID = id;
    }

    public int getPrimaryTenantID() {
        return mPrimaryTenantID;
    }

    public void setPrimaryTenantID(int primaryTenantID) {
        mPrimaryTenantID = primaryTenantID;
    }

    public ArrayList<Integer> getSecondaryTenantIDs() {
        return mSecondaryTenantIDs;
    }

    public void setSecondaryTenantIDs(ArrayList<Integer> secondaryTenantIDs) {
        mSecondaryTenantIDs = secondaryTenantIDs;
    }

    public int getApartmentID() {
        return mApartmentID;
    }

    public void setApartmentID(int apartmentID) {
        mApartmentID = apartmentID;
    }

    public Date getLeaseStart() {
        return mLeaseStart;
    }

    public void setLeaseStart(Date leaseStart) {
        mLeaseStart = leaseStart;
    }

    public Date getLeaseEnd() {
        return mLeaseEnd;
    }

    public void setLeaseEnd(Date leaseEnd) {
        mLeaseEnd = leaseEnd;
    }

    public int getPaymentDayID() {
        return mPaymentDayID;
    }

    public void setPaymentDayID(int paymentDay) {
        mPaymentDayID = paymentDay;
    }

    public BigDecimal getMonthlyRentCost() {
        return mMonthlyRentCost;
    }

    public void setMonthlyRentCost(BigDecimal monthlyRentCost) {
        mMonthlyRentCost = monthlyRentCost;
    }

    public BigDecimal getDeposit() {
        return mDeposit;
    }

    public void setDeposit(BigDecimal deposit) {
        mDeposit = deposit;
    }

    public int getPaymentFrequencyID() {
        return mPaymentFrequencyID;
    }

    public void setPaymentFreuencyID(int paymentFreuencyID) {
        mPaymentFrequencyID = paymentFreuencyID;
    }

    public String getNotes() {
        return mNotes;
    }

    public void setNotes(String notes) {
        mNotes = notes;
    }

    public String getStartAndEndDatesString(int dateFormatCode) {
        StringBuilder sae = new StringBuilder(DateAndCurrencyDisplayer.getDateToDisplay(dateFormatCode, mLeaseStart));
        sae.append(" - ");
        sae.append(DateAndCurrencyDisplayer.getDateToDisplay(dateFormatCode, mLeaseEnd));
        return sae.toString();
    }
}
