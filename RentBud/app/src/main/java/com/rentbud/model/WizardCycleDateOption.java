package com.rentbud.model;

import org.joda.time.LocalDate;

public class WizardCycleDateOption {
    private int id;
    private LocalDate date;

    public WizardCycleDateOption(int id, LocalDate date){
        this.id = id;
        this.date = date;
    }

    public int getID() {
        return id;
    }

    public void setID(int databaseID) {
        this.id = databaseID;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}

