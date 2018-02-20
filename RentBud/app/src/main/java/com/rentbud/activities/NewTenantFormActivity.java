package com.rentbud.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.cody.rentbud.R;
import com.rentbud.model.Apartment;
import com.rentbud.model.Tenant;
import com.rentbud.model.User;
import com.rentbud.sqlite.DatabaseHandler;

import java.util.ArrayList;

/**
 * Created by Cody on 2/9/2018.
 */

public class NewTenantFormActivity extends BaseActivity {
    EditText firstNameET, lastNameET, phoneET, notesET;
    Button saveBtn, cancelBtn;
    DatabaseHandler databaseHandler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupUserAppTheme(MainActivity.curThemeChoice);
        setContentView(R.layout.activity_new_tenant_form);
        setupBasicToolbar();
        initializeVariables();
        setOnClickListeners();
    }

    private void initializeVariables() {
        this.databaseHandler = new DatabaseHandler(this);
        this.firstNameET = findViewById(R.id.tenantFormFirstNameET);
        this.lastNameET = findViewById(R.id.tenantFormLastNameET);
        this.phoneET = findViewById(R.id.tenantFormPhoneET);
        this.notesET = findViewById(R.id.tenantFormNotesET);
        this.saveBtn = findViewById(R.id.tenantFormSaveBtn);
        this.cancelBtn = findViewById(R.id.tenantFormCancelBtn);
    }

    private void setOnClickListeners() {
        //Sets onClickListener to saveBtn
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Get users input data
                String firstName = firstNameET.getText().toString();
                String lastName = lastNameET.getText().toString();
                String phone = phoneET.getText().toString();
                String notes = notesET.getText().toString();
                //TODO will need to update once tenant type is reworked
                //Create new Tenant object with input data and add it to the database
                Tenant tenant = new Tenant(-1, firstName, lastName, phone, 1, " ", notes, " ", " ");
                databaseHandler.addNewTenant(tenant, MainActivity.user.getId());
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
