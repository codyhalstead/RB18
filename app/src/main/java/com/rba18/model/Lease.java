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
    private int id;
    private int primaryTenantID;
    private ArrayList<Integer> secondaryTenantIDs;
    private int apartmentID;
    private Date leaseStart;
    private Date leaseEnd;
    private int paymentDayID;
    private BigDecimal monthlyRentCost;
    private BigDecimal deposit;
    private int paymentFrequencyID;
    //private String paymentFrequency;
    private String notes;

    public Lease(int id, int primaryTenantID, ArrayList<Integer> secondaryTenantIDs, int apartmentID, Date leaseStart, Date leaseEnd,
                 int paymentDayID, BigDecimal monthlyRentCost, BigDecimal deposit, int paymentFrequencyID, String notes) {
        this.id = id;
        this.primaryTenantID = primaryTenantID;
        this.secondaryTenantIDs = secondaryTenantIDs;
        this.apartmentID = apartmentID;
        this.leaseStart = leaseStart;
        this.leaseEnd = leaseEnd;
        this.paymentDayID = paymentDayID;
        this.monthlyRentCost = monthlyRentCost;
        this.deposit = deposit;
        this.paymentFrequencyID = paymentFrequencyID;
        //this.paymentFrequency = paymentFrequency;
        this.notes = notes;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.id);
        parcel.writeInt(this.primaryTenantID);
        if (this.secondaryTenantIDs != null) {
            parcel.writeInt(this.secondaryTenantIDs.size());
            parcel.writeList(this.secondaryTenantIDs);
        } else {
            parcel.writeInt(-1);
        }
        parcel.writeInt(this.apartmentID);
        if(this.leaseStart != null){
            parcel.writeInt(1);
            parcel.writeLong(this.leaseStart.getTime());
        }else{
            parcel.writeInt(0);
        }
        if(this.leaseEnd != null){
            parcel.writeInt(1);
            parcel.writeLong(this.leaseEnd.getTime());
        }else{
            parcel.writeInt(0);
        }
        parcel.writeInt(this.paymentDayID);
        String amountString = this.monthlyRentCost.toPlainString();
        parcel.writeString(amountString);
        amountString = this.deposit.toPlainString();
        parcel.writeString(amountString);
        parcel.writeInt(paymentFrequencyID);
       // parcel.writeString(paymentFrequency);
        parcel.writeString(this.notes);
    }

    public Lease(Parcel parcel) {
        this.id = parcel.readInt();
        this.primaryTenantID = parcel.readInt();
        int size = parcel.readInt();
        if(size > -1){
            this.secondaryTenantIDs = parcel.readArrayList(null);
        } else {
            this.secondaryTenantIDs = new ArrayList<>();
        }
        this.apartmentID = parcel.readInt();
        int leaseStartDateIsNotNull = parcel.readInt();
        if(leaseStartDateIsNotNull == 1) {
            this.leaseStart = new Date(parcel.readLong());
        } else {
            this.leaseStart = null;
        }
        int leaseEndDateIsNotNull = parcel.readInt();
        if(leaseEndDateIsNotNull == 1) {
            this.leaseEnd = new Date(parcel.readLong());
        } else {
            this.leaseEnd = null;
        }
        this.paymentDayID = parcel.readInt();
        String amountString = parcel.readString();
        this.monthlyRentCost = new BigDecimal(amountString);
        amountString = parcel.readString();
        this.deposit = new BigDecimal(amountString);
        this.paymentFrequencyID = parcel.readInt();
        //this.paymentFrequency = parcel.readString();
        this.notes = parcel.readString();
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
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPrimaryTenantID() {
        return primaryTenantID;
    }

    public void setPrimaryTenantID(int primaryTenantID) {
        this.primaryTenantID = primaryTenantID;
    }

    public ArrayList<Integer> getSecondaryTenantIDs() {
        return secondaryTenantIDs;
    }

    public void setSecondaryTenantIDs(ArrayList<Integer> secondaryTenantIDs) {
        this.secondaryTenantIDs = secondaryTenantIDs;
    }

    public int getApartmentID() {
        return apartmentID;
    }

    public void setApartmentID(int apartmentID) {
        this.apartmentID = apartmentID;
    }

    public Date getLeaseStart() {
        return leaseStart;
    }

    public void setLeaseStart(Date leaseStart) {
        this.leaseStart = leaseStart;
    }

    public Date getLeaseEnd() {
        return leaseEnd;
    }

    public void setLeaseEnd(Date leaseEnd) {
        this.leaseEnd = leaseEnd;
    }

    public int getPaymentDayID() {
        return paymentDayID;
    }

    public void setPaymentDayID(int paymentDay) {
        this.paymentDayID = paymentDay;
    }

    public BigDecimal getMonthlyRentCost() {
        return monthlyRentCost;
    }

    public void setMonthlyRentCost(BigDecimal monthlyRentCost) {
        this.monthlyRentCost = monthlyRentCost;
    }

    public BigDecimal getDeposit() {
        return deposit;
    }

    public void setDeposit(BigDecimal deposit) {
        this.deposit = deposit;
    }

    //public BigDecimal getDepositWithheld() {

    public int getPaymentFrequencyID() {
        return paymentFrequencyID;
    }

    public void setPaymentFreuencyID(int paymentFreuencyID) {
        this.paymentFrequencyID = paymentFreuencyID;
    }

    //public String getPaymentFrequency() {
   //     return paymentFrequency;
   // }

   // public void setPaymentFrequency(String paymentFrequency) {
    //    this.paymentFrequency = paymentFrequency;
    //}
    //    return depositWithheld;
    //}

    //public void setDepositWithheld(BigDecimal depositWithheld) {
    //    this.depositWithheld = depositWithheld;
    //}

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getStartAndEndDatesString(int dateFormatCode) {
        StringBuilder sae = new StringBuilder(DateAndCurrencyDisplayer.getDateToDisplay(dateFormatCode, leaseStart));
        sae.append(" - ");
        sae.append(DateAndCurrencyDisplayer.getDateToDisplay(dateFormatCode, leaseEnd));
        return sae.toString();
    }
}
