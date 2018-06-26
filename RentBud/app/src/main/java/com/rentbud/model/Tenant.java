package com.rentbud.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by Cody on 1/27/2018.
 */

public class Tenant implements Parcelable{
    private int id;
    private String firstName;
    private String lastName;
    private String phone;
    private String email;
    private String emergencyFirstName;
    private String emergencyLastName;
    private String emergencyPhone;
    private boolean hasLease;
    private String notes;
    private boolean isActive;


    public Tenant(int id, String firstName, String lastName, String phone, String email, String emergencyFirstName,
                  String emergencyLastName, String emergencyPhone, boolean hasLease, String notes, boolean isActive) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.email = email;
        this.emergencyFirstName = emergencyFirstName;
        this.emergencyLastName = emergencyLastName;
        this.emergencyPhone = emergencyPhone;
        this.hasLease = hasLease;
        this.notes = notes;
        this.isActive = isActive;
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
        parcel.writeString(this.email);
        parcel.writeString(this.emergencyFirstName);
        parcel.writeString(this.emergencyLastName);
        parcel.writeString(this.emergencyPhone);
        parcel.writeByte((byte) (hasLease? 1 : 0));
        parcel.writeString(this.notes);
        parcel.writeByte((byte) (isActive? 1 : 0));
    }

    private Tenant(Parcel in) {
        this.id = in.readInt();
        this.firstName = in.readString();
        this.lastName = in.readString();
        this.phone = in.readString();
        this.email= in.readString();
        this.emergencyFirstName = in.readString();
        this.emergencyLastName = in.readString();
        this.emergencyPhone = in.readString();
        this.hasLease = in.readByte() != 0;
        this.notes = in.readString();
        this.isActive = in.readByte() != 0;
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

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getTenantEmail() {
        return email;
    }

    public void setTenantEmail(String email) {
        this.email = email;
    }

    public String getEmergencyFirstName() {
        return emergencyFirstName;
    }

    public void setEmergencyFirstName(String emergencyFirstName) {
        this.emergencyFirstName = emergencyFirstName;
    }

    public String getEmergencyLastName() {
        return emergencyLastName;
    }

    public void setEmergencyLastName(String emergencyLastName) {
        this.emergencyLastName = emergencyLastName;
    }

    public String getEmergencyPhone() {
        return emergencyPhone;
    }

    public void setEmergencyPhone(String emergencyPhone) {
        this.emergencyPhone = emergencyPhone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getHasLease() {
        return hasLease;
    }

    public void setHasLease(Boolean hasLease) {
        this.hasLease = hasLease;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
