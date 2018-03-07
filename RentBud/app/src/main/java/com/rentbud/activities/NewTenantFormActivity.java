package com.rentbud.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.cody.rentbud.R;
import com.rentbud.helpers.UserInputValidation;
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
    Tenant tenantToEdit;
    boolean isEdit;
    UserInputValidation validation;
    private boolean isFormatting;
    private boolean deletingHyphen;
    private int hyphenStart;
    private boolean deletingBackward;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupUserAppTheme(MainActivity.curThemeChoice);
        setContentView(R.layout.activity_new_tenant_form);
        setupBasicToolbar();
        initializeVariables();
        setOnClickListeners();
        loadTenantDataIfEditing();
        setPhoneNumberEditTextHelper();
    }

    private void initializeVariables() {
        this.databaseHandler = new DatabaseHandler(this);
        this.validation = new UserInputValidation(this);
        this.firstNameET = findViewById(R.id.tenantFormFirstNameET);
        this.lastNameET = findViewById(R.id.tenantFormLastNameET);
        this.phoneET = findViewById(R.id.tenantFormPhoneET);
        this.notesET = findViewById(R.id.tenantFormNotesET);
        this.saveBtn = findViewById(R.id.tenantFormSaveBtn);
        this.cancelBtn = findViewById(R.id.tenantFormCancelBtn);
        this.isEdit = false;
    }

    private void setOnClickListeners() {
        //Sets onClickListener to saveBtn
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validate()) {
                    return;
                }
                //Get users input data
                String firstName = firstNameET.getText().toString().trim();
                String lastName = lastNameET.getText().toString().trim();
                String phone = phoneET.getText().toString().trim();
                String notes = notesET.getText().toString().trim();
                String leaseStart = " ";
                String leaseEnd = " ";
                String paymentDate = " ";
                int apartmentID = 0;
                Boolean isPrimary = false;
                if(tenantToEdit != null){
                    leaseStart = tenantToEdit.getLeaseStart();
                    leaseEnd = tenantToEdit.getLeaseEnd();
                    paymentDate = tenantToEdit.getPaymentDay();
                    isPrimary = tenantToEdit.getIsPrimary();
                    apartmentID = tenantToEdit.getApartmentID();
                }
                //Create new Tenant object with input data and add it to the database
                Tenant tenant = new Tenant(-1, firstName, lastName, phone, apartmentID, isPrimary, paymentDate, notes, leaseStart, leaseEnd);
                //Set result success, close this activity
                if (!isEdit) {
                    databaseHandler.addNewTenant(tenant, MainActivity.user.getId());
                    setResult(RESULT_OK);
                    finish();
                } else {
                    tenant.setId(tenantToEdit.getId());
                    databaseHandler.editTenant(tenant);
                    Intent data = new Intent();
                    data.putExtra("newTenantInfo", tenant);
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

    private void loadTenantDataIfEditing() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            this.isEdit = true;
            tenantToEdit = extras.getParcelable("tenantToEdit");
            firstNameET.setText(tenantToEdit.getFirstName());
            lastNameET.setText(tenantToEdit.getLastName());
            phoneET.setText(tenantToEdit.getPhone());
            notesET.setText(tenantToEdit.getNotes());
        }
    }

    //Method used to validate all of this activities edit text entries
    public boolean validate() {
        boolean valid = true;
        if (!validation.isInputEditTextFilled(this.firstNameET, getString(R.string.field_required))) {
            valid = false;
        }
        if (!validation.isInputEditTextFilled(this.lastNameET, getString(R.string.field_required))) {
            valid = false;
        }
        if (!validation.isInputEditText10DigitPhoneOrEmpty(this.phoneET, getString(R.string.please_use_10_digit_phone))) {
            valid = false;
        }
        return valid;
    }

    //Automatically enters hyphens for the user
    private void setPhoneNumberEditTextHelper(){
        this.phoneET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (isFormatting)
                    return;
                // Make sure user is deleting one char, without a selection
                final int selStart = Selection.getSelectionStart(charSequence);
                final int selEnd = Selection.getSelectionEnd(charSequence);
                if (charSequence.length() > 1 // Can delete another character
                        && i1 == 1 // Deleting only one character
                        && i2 == 0 // Deleting
                        && charSequence.charAt(i) == '-' // a hyphen
                        && selStart == selEnd) { // no selection
                    deletingHyphen = true;
                    hyphenStart = i;
                    // Check if the user is deleting forward or backward
                    if (selStart == i + 1) {
                        deletingBackward = true;
                    } else {
                        deletingBackward = false;
                    }
                } else {
                    deletingHyphen = false;
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable text) {
                if (isFormatting)
                    return;
                isFormatting = true;
                // If deleting hyphen, also delete character before or after it
                if (deletingHyphen && hyphenStart > 0) {
                    if (deletingBackward) {
                        if (hyphenStart - 1 < text.length()) {
                            text.delete(hyphenStart - 1, hyphenStart);
                        }
                    } else if (hyphenStart < text.length()) {
                        text.delete(hyphenStart, hyphenStart + 1);
                    }
                }
                if (text.length() == 3 || text.length() == 7) {
                    text.append('-');
                }
                isFormatting = false;
            }
        });
    }
}
