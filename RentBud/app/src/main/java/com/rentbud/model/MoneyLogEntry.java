package com.rentbud.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.math.BigDecimal;
import java.util.Date;

public class MoneyLogEntry implements Parcelable {
    private int id;
    private Date date;
    private BigDecimal amount;
    private String description;

    public MoneyLogEntry(int id, Date date, BigDecimal amount, String description) {
        this.id = id;
        this.date = date;
        this.amount = amount;
        this.description = description;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        if(this.date != null){
            parcel.writeInt(1);
            parcel.writeLong(this.date.getTime());
        }else{
            parcel.writeInt(0);
        }
        String amountString = amount.toPlainString();
        parcel.writeString(amountString);
        parcel.writeString(this.description);
    }

    protected MoneyLogEntry(Parcel in) {
        this.id = in.readInt();
        int dateIsNotNull = in.readInt();
        if(dateIsNotNull == 1) {
            this.date = new Date(in.readLong());
        }
        String amountString = in.readString();
        this.amount = new BigDecimal(amountString);
        this.description = in.readString();
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
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
