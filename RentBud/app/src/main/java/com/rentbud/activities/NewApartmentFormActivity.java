package com.rentbud.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.cody.rentbud.R;
import com.rentbud.model.Apartment;
import com.rentbud.model.User;
import com.rentbud.sqlite.DatabaseHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Cody on 2/8/2018.
 */

public class NewApartmentFormActivity extends BaseActivity {
EditText address1ET, address2ET, cityET, zipET, descriptionET, notesET;
Spinner stateSpinner;
Button saveBtn, cancelBtn;
DatabaseHandler databaseHandler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupUserAppTheme(MainActivity.curThemeChoice);
        setContentView(R.layout.activity_new_apartment_form);
        setupBasicToolbar();
        initializeVariables();
        populateStateSpinner();
        setOnClickListeners();
    }

    private void populateStateSpinner(){
        //Create state array from MainActivity.stateMap
        List<String> spinnerArray =  new ArrayList<>();
        for (Map.Entry<String, Integer> entry : MainActivity.stateMap.entrySet()){
            spinnerArray.add(entry.getKey());
        }
        //Create ArrayAdapter with state array
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, spinnerArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Set ArrayAdapter to stateSpinner
        this.stateSpinner.setAdapter(adapter);
    }

    private void initializeVariables(){
        this.databaseHandler = new DatabaseHandler(this);
        this.address1ET = findViewById(R.id.apartmentFormAddress1ET);
        this.address2ET = findViewById(R.id.apartmentFormAddress2ET);
        this.cityET = findViewById(R.id.apartmentFormCityET);
        this.zipET = findViewById(R.id.apartmentFormZIPET);
        this.descriptionET = findViewById(R.id.apartmentFormDescriptionET);
        this.notesET = findViewById(R.id.apartmentFormNotesET);
        this.stateSpinner = findViewById(R.id.apartmentStateSpinner);
        this.saveBtn = findViewById(R.id.apartmentFormSaveBtn);
        this.cancelBtn = findViewById(R.id.apartmentFormCancelBtn);
    }

    private void setOnClickListeners(){
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Get users input data
                String address1 = address1ET.getText().toString();
                String address2 = address2ET.getText().toString();
                String city = cityET.getText().toString();
                String zip = zipET.getText().toString();
                String description = descriptionET.getText().toString();
                String notes = notesET.getText().toString();
                String state = stateSpinner.getSelectedItem().toString();
                //TODO will need to update once apartment type is reworked
                //Create new Apartment object with input data and add it to the database
                Apartment apartment = new Apartment(-1 ,address1, address2, city, 1, "IA", zip, notes, "", new ArrayList<String>());
                databaseHandler.addNewApartment(apartment, MainActivity.user.getId());
                //Set result success, close this activity
                setResult(RESULT_OK);
                finish();
            }
        });
        //Sets onClickListener to cancelBtn
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Set result to cancelled and close this activity
                Intent data = new Intent();
                setResult(RESULT_CANCELED, data);
                finish();
            }
        });
    }
}
