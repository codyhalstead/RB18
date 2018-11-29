package com.rba18.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.math.BigDecimal;
import java.util.Date;

public class MoneyLogEntry implements Parcelable {
    private int mID;
    private Date mDate;
    private BigDecimal mAmount;
    private String mDescription;
    private Boolean mCompleted;

    public MoneyLogEntry(int id, Date date, BigDecimal amount, String description, boolean completed) {
        mID = id;
        mDate = date;
        mAmount = amount;
        mDescription = description;
        mCompleted = completed;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(mID);
        if(mDate != null){
            parcel.writeInt(1);
            parcel.writeLong(mDate.getTime());
        }else{
            parcel.writeInt(0);
        }
        String amountString = mAmount.toPlainString();
        parcel.writeString(amountString);
        parcel.writeString(mDescription);
        parcel.writeByte((byte) (mCompleted ? 1 : 0));
    }

    protected MoneyLogEntry(Parcel in) {
        mID = in.readInt();
        int dateIsNotNull = in.readInt();
        if(dateIsNotNull == 1) {
            mDate = new Date(in.readLong());
        }
        String amountString = in.readString();
        mAmount = new BigDecimal(amountString);
        mDescription = in.readString();
        mCompleted = in.readByte() != 0;
    }

    public static final Creator<MoneyLogEntry> CREATOR = new Creator<MoneyLogEntry>() {
        @Override
        public MoneyLogEntry createFromParcel(Parcel in) {
            return new MoneyLogEntry(in);
        }

        @Override
        public MoneyLogEntry[] newArray(int size) {
            return new MoneyLogEntry[size];
        }
    };

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public BigDecimal getAmount() {
        return mAmount;
    }

    public void setAmount(BigDecimal amount) {
        mAmount = amount;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public int getId() {
        return mID;
    }

    public void setId(int id) {
        mID = id;
    }

    public Boolean getIsCompleted() {
        return mCompleted;
    }

    public void setIsCompleted(Boolean completed) {
        mCompleted = completed;
    }
}
