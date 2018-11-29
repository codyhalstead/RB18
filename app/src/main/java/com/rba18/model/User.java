package com.rba18.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Cody on 12/12/2017.
 */

public class User implements Parcelable {
    private int mID;
    private String mName;
    private String mEmail;
    private String mPassword;
    private Boolean mIsGoogleAccount;

    //Partial sUser object creation allowed, but should be completed when available
    public User(String name, String email, String password, boolean isGoogleAccount) {
        mName = name;
        mEmail = email;
        mPassword = password;
        mIsGoogleAccount = isGoogleAccount;
    }

    public User(int id, String name, String email, String password, boolean isGoogleAccount) {
        mID = id;
        mName = name;
        mEmail = email;
        mPassword = password;
        mIsGoogleAccount = isGoogleAccount;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mID);
        dest.writeString(mName);
        dest.writeString(mEmail);
        dest.writeString(mPassword);
        dest.writeByte((byte) (mIsGoogleAccount ? 1 : 0));
    }

    private User(Parcel in) {
        mID = in.readInt();
        mName = in.readString();
        mEmail = in.readString();
        mPassword = in.readString();
        mIsGoogleAccount = in.readByte() != 0;
    }

    public static final Parcelable.Creator<User> CREATOR
            = new Parcelable.Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);

        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public int getId() {
        return mID;
    }

    public void setId(int id) {
        mID = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        mEmail = email;
    }

    public String getPassword() {
        return mPassword;
    }

    public void setPassword(String password) {
        mPassword = password;
    }

    public Boolean IsGoogleAccount() {
        return mIsGoogleAccount;
    }

    public void setIsGoogleAccount(Boolean googleAccount) {
        mIsGoogleAccount = googleAccount;
    }
}
