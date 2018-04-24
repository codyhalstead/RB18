package com.rentbud.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by Cody on 1/27/2018.
 */

public class PaymentLogEntry implements Parcelable{
    private int id;
    private Date paymentDate;
    private int typeID;
    private String typeLabel;
    private int tenantID;
    private int leaseID;
    private BigDecimal amount;
    private String description;

    public PaymentLogEntry(int id, Date paymentDate, int typeID, String typeLabel, int tenantID, int leaseID, BigDecimal amount, String description){
        this.id = id;
        this.paymentDate = paymentDate;
        this.typeID = typeID;
        this.typeLabel = typeLabel;
        this.tenantID = tenantID;
        this.leaseID = leaseID;
        this.amount = amount;
        this.description = description;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.id);
        if(this.paymentDate != null){
            parcel.writeInt(1);
            parcel.writeLong(this.paymentDate.getTime());
        }else{
            parcel.writeInt(0);
        }
        parcel.writeInt(this.typeID);
        parcel.writeString(this.typeLabel);
        parcel.writeInt(this.tenantID);
        parcel.writeInt(this.leaseID);
        String amountString = amount.toPlainString();
        parcel.writeString(amountString);
        parcel.writeString(this.description);
    }

    protected PaymentLogEntry(Parcel in) {
        this.id = in.readInt();
        int paymentDayIsNotNull = in.readInt();
        if(paymentDayIsNotNull == 1) {
            this.paymentDate = new Date(in.readLong());
        }
        this.typeID = in.readInt();
        this.typeLabel = in.readString();
        this.tenantID = in.readInt();
        this.leaseID = in.readInt();
        String amountString = in.readString();
        this.amount = new BigDecimal(amountString);
        this.description = in.readString();
    }

    public static final Creator<PaymentLogEntry> CREATOR = new Creator<PaymentLogEntry>() {
        @Override
        public PaymentLogEntry createFromParcel(Parcel in) {
            return new PaymentLogEntry(in);
        }

        @Override
        public PaymentLogEntry[] newArray(int size) {
            return new PaymentLogEntry[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(Date paymentDate) {
        this.paymentDate = paymentDate;
    }

    public int getTypeID() {
        return typeID;
    }

    public void setTypeID(int typeID) {
        this.typeID = typeID;
    }

    public int getTenantID() {
        return tenantID;
    }

    public void setTenantID(int tenantID) {
        this.tenantID = tenantID;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTypeLabel() {
        return typeLabel;
    }

    public void setTypeLabel(String typeLabel) {
        this.typeLabel = typeLabel;
    }

    public int getLeaseID() {
        return leaseID;
    }

    public void setLeaseID(int leaseID) {
        this.leaseID = leaseID;
    }
}
