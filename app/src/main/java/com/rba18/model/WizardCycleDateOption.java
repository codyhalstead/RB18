package com.rba18.model;

import org.joda.time.LocalDate;

public class WizardCycleDateOption {
    private int mID;
    private LocalDate mDate;

    public WizardCycleDateOption(int id, LocalDate date){
        mID = id;
        mDate = date;
    }

    public int getID() {
        return mID;
    }

    public void setID(int databaseID) {
        mID = databaseID;
    }

    public LocalDate getDate() {
        return mDate;
    }

    public void setDate(LocalDate date) {
        mDate = date;
    }
}

