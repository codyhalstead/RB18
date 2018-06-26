package com.rentbud.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by Cody on 1/27/2018.
 */

public class PaymentLogEntry extends MoneyLogEntry{
    private int typeID;
    private String typeLabel;
    private int tenantID;
    private int leaseID;
    private int apartmentID;
    private String receiptPic;

    public PaymentLogEntry(int id, Date paymentDate, int typeID, String typeLabel, int tenantID, int leaseID, int apartmentID, BigDecimal amount, String description, String receiptPic){
        super(id, paymentDate, amount, description);
        this.typeID = typeID;
        this.typeLabel = typeLabel;
        this.tenantID = tenantID;
        this.leaseID = leaseID;
        this.apartmentID = apartmentID;
        this.receiptPic = receiptPic;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        parcel.writeInt(this.typeID);
        parcel.writeString(this.typeLabel);
        parcel.writeInt(this.tenantID);
        parcel.writeInt(this.leaseID);
        parcel.writeInt(this.apartmentID);
        if (this.receiptPic != null) {
            parcel.writeInt(1);
            parcel.writeString(this.receiptPic);
        } else {
            parcel.writeInt(-1);
        }
    }

    protected PaymentLogEntry(Parcel in) {
        super(in);
        this.typeID = in.readInt();
        this.typeLabel = in.readString();
        this.tenantID = in.readInt();
        this.leaseID = in.readInt();
        this.apartmentID = in.readInt();
        int size = in.readInt();
        if(size > -1){
            //this.mainPic = new byte[size];
            //in.readByteArray(this.mainPic);
            this.receiptPic = in.readString();
        } else {
            this.receiptPic = null;
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

    public String getReceiptPic() {
        return receiptPic;
    }

    public void setReceiptPic(String receiptPic) {
        this.receiptPic = receiptPic;
    }

    public int getApartmentID() {
        return apartmentID;
    }

    public void setApartmentID(int apartmentID) {
        this.apartmentID = apartmentID;
    }
}
