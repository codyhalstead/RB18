package com.rentbud.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by Cody on 1/27/2018.
 */

public class EventLogEntry implements Parcelable {
    private int id;
    private Date eventTime;
    private int typeID;
    private String description;
    private int apartmentID;
    private int tenantID;

    public EventLogEntry(int id, Date eventTime, int typeID, String description, int apartmentID, int tenantID) {
        this.id = id;
        this.eventTime = eventTime;
        this.typeID = typeID;
        this.description = description;
        this.apartmentID = apartmentID;
        this.tenantID = tenantID;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.id);
        if(this.eventTime != null){
            parcel.writeInt(1);
            parcel.writeLong(this.eventTime.getTime());
        }else{
            parcel.writeInt(0);
        }
        parcel.writeInt(this.typeID);
        parcel.writeString(this.description);
        parcel.writeInt(this.apartmentID);
        parcel.writeInt(this.tenantID);
    }

    protected EventLogEntry(Parcel in) {
        this.id = in.readInt();
        int eventDateIsNotNull = in.readInt();
        if(eventDateIsNotNull == 1) {
            this.eventTime = new Date(in.readLong());
        }
        this.description = in.readString();
        this.apartmentID = in.readInt();
        this.tenantID = in.readInt();
    }

    public static final Creator<EventLogEntry> CREATOR = new Creator<EventLogEntry>() {
        @Override
        public EventLogEntry createFromParcel(Parcel in) {
            return new EventLogEntry(in);
        }

        @Override
        public EventLogEntry[] newArray(int size) {
            return new EventLogEntry[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getEventTime() {
        return eventTime;
    }

    public void setEventTime(Date eventTime) {
        this.eventTime = eventTime;
    }

    public int getTypeID() {
        return typeID;
    }

    public void setTypeID(int typeID) {
        this.typeID = typeID;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getApartmentID() {
        return apartmentID;
    }

    public void setApartmentID(int apartmentID) {
        this.apartmentID = apartmentID;
    }

    public int getTenantID() {
        return tenantID;
    }

    public void setTenantID(int tenantID) {
        this.tenantID = tenantID;
    }
}