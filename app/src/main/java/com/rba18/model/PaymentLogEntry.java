package com.rba18.model;

import android.os.Parcel;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by Cody on 1/27/2018.
 */

public class PaymentLogEntry extends MoneyLogEntry{
    private int mTypeID;
    private String mTypeLabel;
    private int mTenantID;
    private int mLeaseID;
    private int mApartmentID;
    private String mReceiptPic;

    public PaymentLogEntry(int id, Date paymentDate, int typeID, String typeLabel, int tenantID, int leaseID, int apartmentID, BigDecimal amount, String description, String receiptPic, boolean isReceived){
        super(id, paymentDate, amount, description, isReceived);
        mTypeID = typeID;
        mTypeLabel = typeLabel;
        mTenantID = tenantID;
        mLeaseID = leaseID;
        mApartmentID = apartmentID;
        mReceiptPic = receiptPic;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        parcel.writeInt(mTypeID);
        parcel.writeString(mTypeLabel);
        parcel.writeInt(mTenantID);
        parcel.writeInt(mLeaseID);
        parcel.writeInt(mApartmentID);
        if (mReceiptPic != null) {
            parcel.writeInt(1);
            parcel.writeString(mReceiptPic);
        } else {
            parcel.writeInt(-1);
        }
    }

    protected PaymentLogEntry(Parcel in) {
        super(in);
        mTypeID = in.readInt();
        mTypeLabel = in.readString();
        mTenantID = in.readInt();
        mLeaseID = in.readInt();
        mApartmentID = in.readInt();
        int size = in.readInt();
        if(size > -1){
            mReceiptPic = in.readString();
        } else {
            mReceiptPic = null;
        }
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

    public int getTypeID() {
        return mTypeID;
    }

    public void setTypeID(int typeID) {
        mTypeID = typeID;
    }

    public int getTenantID() {
        return mTenantID;
    }

    public void setTenantID(int tenantID) {
        mTenantID = tenantID;
    }

    public String getTypeLabel() {
        return mTypeLabel;
    }

    public void setTypeLabel(String typeLabel) {
        mTypeLabel = typeLabel;
    }

    public int getLeaseID() {
        return mLeaseID;
    }

    public void setLeaseID(int leaseID) {
        mLeaseID = leaseID;
    }

    public String getReceiptPic() {
        return mReceiptPic;
    }

    public void setReceiptPic(String receiptPic) {
        mReceiptPic = receiptPic;
    }

    public int getApartmentID() {
        return mApartmentID;
    }

    public void setApartmentID(int apartmentID) {
        mApartmentID = apartmentID;
    }
}
