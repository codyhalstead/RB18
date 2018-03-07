package com.rentbud.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Cody on 1/27/2018.
 */

public class Tenant implements Parcelable{
    private int id;
    private String firstName;
    private String lastName;
    private String phone;
    private int apartmentID;
    private Boolean isPrimary;
    private String paymentDay;
    private String notes;
    private String leaseStart;
    private String leaseEnd;


    public Tenant(int id, String firstName, String lastName, String phone, int apartmentID, Boolean isPrimary, String paymentDay,
                  String notes, String leaseStart, String leaseEnd) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.apartmentID = apartmentID;
        this.isPrimary = isPrimary;
        this.paymentDay = paymentDay;
        this.notes = notes;
        this.leaseStart = leaseStart;
        this.leaseEnd = leaseEnd;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.id);
        parcel.writeString(this.firstName);
        parcel.writeString(this.lastName);
        parcel.writeString(this.phone);
        parcel.writeInt(this.apartmentID);
        parcel.writeByte((byte) (isPrimary ? 1 : 0));
        parcel.writeString(this.paymentDay);
        parcel.writeString(this.notes);
        parcel.writeString(this.leaseStart);
        parcel.writeString(this.leaseEnd);
    }

    private Tenant(Parcel in) {
        this.id = in.readInt();
        this.firstName = in.readString();
        this.lastName = in.readString();
        this.phone = in.readString();
        this.apartmentID = in.readInt();
        this.isPrimary = in.readByte() != 0;
        this.paymentDay = in.readString();
        this.notes = in.readString();
        this.leaseStart = in.readString();
        this.leaseEnd = in.readString();
    }


    public static final Parcelable.Creator<Tenant> CREATOR
            = new Parcelable.Creator<Tenant>() {


        @Override
        public Tenant createFromParcel(Parcel source) {
            return new Tenant(source);

        }

        @Override
        public Tenant[] newArray(int size) {
            return new Tenant[size];
        }
    };

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getApartmentID() {
        return apartmentID;
    }

    public void setApartmentID(int apartmentID) {
        this.apartmentID = apartmentID;
    }

    public String getPaymentDay() {
        return paymentDay;
    }

    public void setPaymentDay(String paymentDay) {
        this.paymentDay = paymentDay;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getLeaseStart() {
        return leaseStart;
    }

    public void setLeaseStart(String leaseStart) {
        this.leaseStart = leaseStart;
    }

    public String getLeaseEnd() {
        return leaseEnd;
    }

    public void setLeaseEnd(String leaseEnd) {
        this.leaseEnd = leaseEnd;
    }

    public Boolean getIsPrimary() {
        return isPrimary;
    }

    public void setIsPrimary(Boolean primary) {
        isPrimary = primary;
    }
}
