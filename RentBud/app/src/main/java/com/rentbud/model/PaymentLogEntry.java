package com.rentbud.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Cody on 1/27/2018.
 */

public class PaymentLogEntry implements Parcelable{
    private int id;
    private String paymentDate;
    private int typeID;
    private int tenantID;
    private int amount;

    public PaymentLogEntry(int id, String paymentDate, int typeID, int tenantID, int amount){
        this.id = id;
        this.paymentDate = paymentDate;
        this.typeID = typeID;
        this.tenantID = tenantID;
        this.amount = amount;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.id);
        parcel.writeString(this.paymentDate);
        parcel.writeInt(this.typeID);
        parcel.writeInt(this.tenantID);
        parcel.writeInt(this.amount);
    }

    protected PaymentLogEntry(Parcel in) {
        this.id = in.readInt();
        this.paymentDate = in.readString();
        this.typeID = in.readInt();
        this.tenantID = in.readInt();
        this.amount = in.readInt();
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

    public String getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(String paymentDate) {
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

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
