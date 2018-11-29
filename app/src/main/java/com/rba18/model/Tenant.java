package com.rba18.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Cody on 1/27/2018.
 */

public class Tenant implements Parcelable {
    private int mID;
    private String mFirstName;
    private String mLastName;
    private String mPhone;
    private String mEmail;
    private String mEmergencyFirstName;
    private String mEmergencyLastName;
    private String mEmergencyPhone;
    private boolean mHasLease;
    private String mNotes;
    private boolean mIsActive;

    public Tenant(int id, String firstName, String lastName, String phone, String email, String emergencyFirstName,
                  String emergencyLastName, String emergencyPhone, boolean hasLease, String notes, boolean isActive) {
        mID = id;
        mFirstName = firstName;
        mLastName = lastName;
        mPhone = phone;
        mEmail = email;
        mEmergencyFirstName = emergencyFirstName;
        mEmergencyLastName = emergencyLastName;
        mEmergencyPhone = emergencyPhone;
        mHasLease = hasLease;
        mNotes = notes;
        mIsActive = isActive;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(mID);
        parcel.writeString(mFirstName);
        parcel.writeString(mLastName);
        parcel.writeString(mPhone);
        parcel.writeString(mEmail);
        parcel.writeString(mEmergencyFirstName);
        parcel.writeString(mEmergencyLastName);
        parcel.writeString(mEmergencyPhone);
        parcel.writeByte((byte) (mHasLease ? 1 : 0));
        parcel.writeString(mNotes);
        parcel.writeByte((byte) (mIsActive ? 1 : 0));
    }

    private Tenant(Parcel in) {
        mID = in.readInt();
        mFirstName = in.readString();
        mLastName = in.readString();
        mPhone = in.readString();
        mEmail = in.readString();
        mEmergencyFirstName = in.readString();
        mEmergencyLastName = in.readString();
        mEmergencyPhone = in.readString();
        mHasLease = in.readByte() != 0;
        mNotes = in.readString();
        mIsActive = in.readByte() != 0;
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
        return mFirstName;
    }

    public void setFirstName(String firstName) {
        mFirstName = firstName;
    }

    public String getLastName() {
        return mLastName;
    }

    public void setLastName(String lastName) {
        mLastName = lastName;
    }

    public String getPhone() {
        if(mPhone != null){
            return mPhone;
        }
        return "";
    }

    public void setPhone(String phone) {
        mPhone = phone;
    }

    public int getId() {
        return mID;
    }

    public void setId(int id) {
        mID = id;
    }

    public String getNotes() {
        return mNotes;
    }

    public void setNotes(String notes) {
        mNotes = notes;
    }

    public String getTenantEmail() {
        if(mEmail != null){
            return mEmail;
        }
        return "";
    }

    public void setTenantEmail(String email) {
        mEmail = email;
    }

    public String getEmergencyFirstName() {
        return mEmergencyFirstName;
    }

    public void setEmergencyFirstName(String emergencyFirstName) {
        mEmergencyFirstName = emergencyFirstName;
    }

    public String getEmergencyLastName() {
        return mEmergencyLastName;
    }

    public void setEmergencyLastName(String emergencyLastName) {
        mEmergencyLastName = emergencyLastName;
    }

    public String getEmergencyPhone() {
        if(mEmergencyPhone != null){
            return mEmergencyPhone;
        }
        return "";
    }

    public void setEmergencyPhone(String emergencyPhone) {
        mEmergencyPhone = emergencyPhone;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        mEmail = email;
    }

    public Boolean getHasLease() {
        return mHasLease;
    }

    public void setHasLease(Boolean hasLease) {
        mHasLease = hasLease;
    }

    public boolean isActive() {
        return mIsActive;
    }

    public void setActive(boolean active) {
        mIsActive = active;
    }

    public String getFirstAndLastNameString() {
        StringBuilder name = new StringBuilder("");
        if(mFirstName != null) {
            name.append(mFirstName);
            name.append(" ");
        }
        if(mLastName != null) {
            name.append(mLastName);
        }
        return name.toString();
    }

    public String getEmergencyFirstAndLastNameString() {
        StringBuilder name = new StringBuilder(" ");
        if(mEmergencyFirstName != null) {
            name.append(mEmergencyFirstName);
            name.append(" ");
        }
        if(mEmergencyLastName != null) {
            name.append(mEmergencyLastName);
        }
        return name.toString();
    }
}
