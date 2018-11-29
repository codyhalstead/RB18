package com.rba18.model;

import android.os.Parcel;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by Cody on 1/27/2018.
 */

public class ExpenseLogEntry extends MoneyLogEntry {
    private int mApartmentID;
    private int mLeaseID;
    private int mTenantID;
    private int mTypeID;
    private String mTypeLabel;
    private String mReceiptPic;

    public ExpenseLogEntry(int id, Date expenseDate, BigDecimal amount, int apartmentID, int leaseID, int tenantID, String description, int typeID, String typeLabel,
            String receiptPic, boolean isPaid) {
        super(id, expenseDate, amount, description, isPaid);
        mApartmentID = apartmentID;
        mLeaseID = leaseID;
        mTenantID = tenantID;
        mTypeID = typeID;
        mTypeLabel = typeLabel;
        mReceiptPic = receiptPic;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        parcel.writeInt(mApartmentID);
        parcel.writeInt(mLeaseID);
        parcel.writeInt(mTenantID);
        parcel.writeInt(mTypeID);
        parcel.writeString(mTypeLabel);
        if (mReceiptPic != null) {
            parcel.writeInt(1);
            parcel.writeString(mReceiptPic);
        } else {
            parcel.writeInt(-1);
        }
    }

    protected ExpenseLogEntry(Parcel in) {
        super(in);
        mApartmentID = in.readInt();
        mLeaseID = in.readInt();
        mTenantID = in.readInt();
        mTypeID = in.readInt();
        mTypeLabel = in.readString();
        int size = in.readInt();
        if(size > -1){
            mReceiptPic = in.readString();
        } else {
            mReceiptPic = null;
        }
    }

    public static final Creator<ExpenseLogEntry> CREATOR = new Creator<ExpenseLogEntry>() {
        @Override
        public ExpenseLogEntry createFromParcel(Parcel in) {
            return new ExpenseLogEntry(in);
        }

        @Override
        public ExpenseLogEntry[] newArray(int size) {
            return new ExpenseLogEntry[size];
        }
    };

    public int getApartmentID() {
        return mApartmentID;
    }

    public void setApartmentID(int apartmentID) {
        mApartmentID = apartmentID;
    }

    public int getTypeID() {
        return mTypeID;
    }

    public void setTypeID(int typeID) {
        mTypeID = typeID;
    }

    public String getReceiptPic() {
        return mReceiptPic;
    }

    public void setReceiptPic(String receiptPic) {
        mReceiptPic = receiptPic;
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

    public int getTenantID() {
        return mTenantID;
    }

    public void setTenantID(int tenantID) {
        mTenantID = tenantID;
    }
}
