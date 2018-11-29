package com.rba18.model;

public class WizardDueDate {
    private int mDatabaseID;
    private String mLabel;

    public WizardDueDate(int databaseID, String label){
        mDatabaseID = databaseID;
        mLabel = label;
    }

    public int getDatabaseID() {
        return mDatabaseID;
    }

    public void setDatabaseID(int databaseID) {
        mDatabaseID = databaseID;
    }

    public String getLabel() {
        return mLabel;
    }

    public void setLabel(String label) {
        mLabel = label;
    }
}
