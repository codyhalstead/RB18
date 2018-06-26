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
import com.rentbud.fragments.ApartmentListFragment;
import com.rentbud.helpers.MainArrayDataMethods;
import com.rentbud.helpers.UserInputValidation;
import com.rentbud.model.Apartment;
import com.rentbud.model.Tenant;
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
    ArrayAdapter<String> adapter;
    Boolean isEdit;
    Apartment apartmentToEdit;
    UserInputValidation validation;
    String mainPic;
    ArrayList<String> otherPics;
    MainArrayDataMethods dataMethods;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupUserAppTheme(MainActivity.curThemeChoice);
        setContentView(R.layout.activity_new_apartment_form);
        setupBasicToolbar();
        initializeVariables();
        populateStateSpinner();
        setOnClickListeners();
        loadApartmentDataIfEditing();
    }

    private void populateStateSpinner() {
        //Create state array from MainActivity5.stateMap
        List<String> spinnerArray = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : MainActivity.stateMap.entrySet()) {
            spinnerArray.add(entry.getKey());
        }
        //Create ArrayAdapter with state array
        adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, spinnerArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Set ArrayAdapter to stateSpinner
        this.stateSpinner.setAdapter(adapter);
    }

    private void initializeVariables() {
        this.databaseHandler = new DatabaseHandler(this);
        this.validation = new UserInputValidation(this);
        this.address1ET = findViewById(R.id.apartmentFormAddress1ET);
        this.address2ET = findViewById(R.id.apartmentFormAddress2ET);
        this.cityET = findViewById(R.id.apartmentFormCityET);
        this.zipET = findViewById(R.id.apartmentFormZIPET);
        this.descriptionET = findViewById(R.id.apartmentFormDescriptionET);
        this.notesET = findViewById(R.id.apartmentFormNotesET);
        this.stateSpinner = findViewById(R.id.apartmentStateSpinner);
        this.saveBtn = findViewById(R.id.apartmentFormSaveBtn);
        this.cancelBtn = findViewById(R.id.apartmentFormCancelBtn);
        this.isEdit = false;
        this.dataMethods = new MainArrayDataMethods();
    }

    private void setOnClickListeners() {
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validate()) {
                    return;
                }
                //Get users input data
                String address1 = address1ET.getText().toString().trim();
                String address2 = address2ET.getText().toString().trim();
                String city = cityET.getText().toString().trim();
                String zip = zipET.getText().toString().trim();
                String description = descriptionET.getText().toString().trim();
                String notes = notesET.getText().toString().trim();
                String state = stateSpinner.getSelectedItem().toString();
                int stateID = MainActivity.stateMap.get(state);
                String newMainPic = null;
                if(mainPic != null) {
                    newMainPic = mainPic;
                }
                ArrayList<String> newOtherPics = new ArrayList<>();
                if(otherPics != null){
                    newOtherPics = otherPics;
                }
                //Create new Apartment object with input data and add it to the database
                Apartment apartment = new Apartment(-1, address1, address2, city, stateID, state, zip, description, false, notes, newMainPic, newOtherPics, true);
                if(!isEdit){
                    databaseHandler.addNewApartment(apartment, MainActivity.user.getId());
                    //Set result success, close this activity
                    MainActivity.apartmentList = databaseHandler.getUsersApartmentsIncludingInactive(MainActivity.user);
                    setResult(RESULT_OK);
                    finish();
                }else{
                   // apartment.setId(apartmentToEdit.getId());
                    Apartment originalApartment = dataMethods.getCachedApartmentByApartmentID(apartmentToEdit.getId());
                    originalApartment.setStreet1(address1);
                    originalApartment.setStreet2(address2);
                    originalApartment.setCity(city);
                    originalApartment.setStateID(stateID);
                    originalApartment.setState(state);
                    originalApartment.setZip(zip);
                    originalApartment.setDescription(description);
                    originalApartment.setNotes(notes);
                    databaseHandler.editApartment(originalApartment, MainActivity.user.getId());
                    dataMethods.sortMainApartmentArray();
                    Intent data = new Intent();
                    data.putExtra("editedApartmentID", originalApartment.getId());
                    setResult(RESULT_OK, data);
                    finish();
                }
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

    private void loadApartmentDataIfEditing() {
        Bundle extras = getIntent().getExtras();
        if(extras != null){
            this.isEdit = true;
            apartmentToEdit = extras.getParcelable("apartmentToEdit");
            address1ET.setText(apartmentToEdit.getStreet1());
            address2ET.setText(apartmentToEdit.getStreet2());
            cityET.setText(apartmentToEdit.getCity());
            zipET.setText(apartmentToEdit.getZip());
            descriptionET.setText(apartmentToEdit.getDescription());
            notesET.setText(apartmentToEdit.getNotes());
            String state = apartmentToEdit.getState();
            if (state != null) {
                int spinnerPosition = adapter.getPosition(state);
                stateSpinner.setSelection(spinnerPosition);
            }
            this.mainPic = apartmentToEdit.getMainPic();
            this.otherPics = apartmentToEdit.getOtherPics();
        }
    }

    //Method used to validate all of this activities edit text entries
    public boolean validate() {
        boolean valid = true;
        if (!validation.isInputEditTextFilled(this.address1ET, getString(R.string.field_required))) {
            valid = false;
        }
        if (!validation.isInputEditTextFilled(this.cityET, getString(R.string.field_required))) {
            valid = false;
        }
        if (!validation.isInputEditTextFilled(this.zipET, getString(R.string.field_required))) {
            valid = false;
        }
        return valid;
    }
}
