package com.RB18.model;

public class WizardDueDate {
    private int databaseID;
    private String label;

    public WizardDueDate(int databaseID, String label){
        this.databaseID = databaseID;
        this.label = label;
    }

    public int getDatabaseID() {
        return databaseID;
    }

    public void setDatabaseID(int databaseID) {
        this.databaseID = databaseID;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
