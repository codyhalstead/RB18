package com.rba18.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by Cody on 1/27/2018.
 */

public class ExpenseLogEntry extends MoneyLogEntry {
    private int apartmentID;
    private int leaseID;
    private int tenantID;
    private int typeID;
    private String typeLabel;
    private String receiptPic;

    public ExpenseLogEntry(int id, Date expenseDate, BigDecimal amount, int apartmentID, int leaseID, int tenantID, String description, int typeID, String typeLabel,
            String receiptPic, boolean isPaid) {
        super(id, expenseDate, amount, description, isPaid);
        this.apartmentID = apartmentID;
        this.leaseID = leaseID;
        this.tenantID = tenantID;
        this.typeID = typeID;
        this.typeLabel = typeLabel;
        this.receiptPic = receiptPic;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        parcel.writeInt(this.apartmentID);
        parcel.writeInt(this.leaseID);
        parcel.writeInt(this.tenantID);
        parcel.writeInt(this.typeID);
        parcel.writeString(this.typeLabel);
        if (this.receiptPic != null) {
            parcel.writeInt(1);
            parcel.writeString(this.receiptPic);
        } else {
            parcel.writeInt(-1);
        }
    }

    protected ExpenseLogEntry(Parcel in) {
        super(in);
        this.apartmentID = in.readInt();
        this.leaseID = in.readInt();
        this.tenantID = in.readInt();
        this.typeID = in.readInt();
        this.typeLabel = in.readString();
        int size = in.readInt();
        if(size > -1){
            //this.mainPic = new byte[size];
            //in.readByteArray(this.mainPic);
            this.receiptPic = in.readString();
        } else {
            this.receiptPic = null;
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
        return apartmentID;
    }

    public void setApartmentID(int apartmentID) {
        this.apartmentID = apartmentID;
    }

    public int getTypeID() {
        return typeID;
    }

    public void setTypeID(int typeID) {
        this.typeID = typeID;
    }

    public String getReceiptPic() {
        return receiptPic;
    }

    public void setReceiptPic(String receiptPic) {
        this.receiptPic = receiptPic;
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

    public int getTenantID() {
        return tenantID;
    }

    public void setTenantID(int tenantID) {
        this.tenantID = tenantID;
    }
}
