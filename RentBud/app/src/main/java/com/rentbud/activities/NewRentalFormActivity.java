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

public class NewRentalFormActivity extends BaseActivity {
SharedPreferences preferences;
EditText address1ET, address2ET, cityET, zipET, descriptionET, notesET;
Spinner stateSpinner;
Button saveBtn, cancelBtn;
DatabaseHandler databaseHandler;
User user;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String email = preferences.getString("last_user_email", "");
        int theme = preferences.getInt(email, 0);
        setupUserAppTheme(theme);
        setContentView(R.layout.activity_new_apartment_form);
        setupBasicToolbar();
        Bundle bundle = getIntent().getExtras();
        this.user = (User)bundle.get("user");
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
        populateStateSpinner();
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String address1 = address1ET.getText().toString();
                String address2 = address2ET.getText().toString();
                String city = cityET.getText().toString();
                String zip = zipET.getText().toString();
                String description = descriptionET.getText().toString();
                String notes = notesET.getText().toString();
                String state = stateSpinner.getSelectedItem().toString();
                //TODO will need to update once apartment type is reworked
                Apartment apartment = new Apartment(-1 ,address1, address2, city, 1, "IA", zip, notes, "", new ArrayList<String>());
                databaseHandler.addNewApartment(apartment, user.getId());
              //  Intent data = new Intent();
              // data.putExtra("newApartment", apartment);
              //  setResult(RESULT_OK, data);
                setResult(RESULT_OK);
                finish();
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent data = new Intent();
                setResult(RESULT_CANCELED, data);
                finish();
            }
        });
    }

    public void populateStateSpinner(){
        List<String> spinnerArray =  new ArrayList<String>();
        for (Map.Entry<String, Integer> entry : MainActivity.stateMap.entrySet()){
            spinnerArray.add(entry.getKey());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, spinnerArray);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.stateSpinner.setAdapter(adapter);
    }
}
