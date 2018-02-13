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

public class NewRenterFormActivity extends BaseActivity {
    SharedPreferences preferences;
    EditText firstNameET, lastNameET, phoneET, notesET;
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
        setContentView(R.layout.activity_new_tenant_form);
        setupBasicToolbar();
        Bundle bundle = getIntent().getExtras();
        this.user = (User)bundle.get("user");
        this.databaseHandler = new DatabaseHandler(this);
        this.firstNameET = findViewById(R.id.tenantFormFirstNameET);
        this.lastNameET = findViewById(R.id.tenantFormLastNameET);
        this.phoneET = findViewById(R.id.tenantFormPhoneET);
        this.notesET = findViewById(R.id.tenantFormNotesET);
        this.saveBtn = findViewById(R.id.tenantFormSaveBtn);
        this.cancelBtn = findViewById(R.id.tenantFormCancelBtn);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String firstName = firstNameET.getText().toString();
                String lastName = lastNameET.getText().toString();
                String phone = phoneET.getText().toString();
                String notes = notesET.getText().toString();
                //TODO will need to update once tenant type is reworked
                Tenant tenant = new Tenant(-1, firstName, lastName, phone, 1, " ", notes, " ", " ");
                databaseHandler.addNewTenant(tenant, user.getId());
                //  Intent data = new Intent();
                // data.putExtra("newTenant", tenant);
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
}
