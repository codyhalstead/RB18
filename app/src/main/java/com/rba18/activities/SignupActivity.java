package com.rba18.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.rba18.R;
import com.rba18.helpers.AppFileManagementHelper;
import com.rba18.helpers.UserInputValidation;
import com.rba18.model.User;
import com.rba18.sqlite.DatabaseHandler;

/**
 * Created by Cody on 12/8/2017.
 */

public class SignUpActivity extends AppCompatActivity {
    private TextInputEditText mNameText, mEmailText, mPasswordText, mConfirmPasswordText;
    private Button mSignUpButton;
    private TextView mLoginLink;
    private UserInputValidation mValidation;
    private DatabaseHandler mDatabaseHandler;
    private String mName, mEmail, mPassword;
    private boolean mSuccessfulAccountCreation;
    //private AlertDialog alertDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        initializeVariables();
        setOnClickListeners();
    }

    public void signUp() {
        //If mValidation fails
        if (!validate()) {
            onSignUpFailed();
            return;
        }
        mSignUpButton.setEnabled(false);
        //Launch creating progressDialog
        final ProgressDialog progressDialog = new ProgressDialog(SignUpActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getResources().getString(R.string.creating_account));
        progressDialog.show();
        //Get input data
        mName = mNameText.getText().toString().trim();
        mEmail = mEmailText.getText().toString().trim();
        mPassword = AppFileManagementHelper.SHA512Hash(mPasswordText.getText().toString().trim());
        //If sUser is not already in the database (based on Email), sign-up success
        if (!mDatabaseHandler.checkUser(mEmail)) {
            mDatabaseHandler.addUser(mName, mEmail, mPassword, false);
            onSignUpSuccess();
        } else {
            //If they do already exist, set error in Email editText
            mEmailText.setError(getString(R.string.email_used));
            onSignUpFailed();
        }
        if (mSuccessfulAccountCreation) {
            emptyInputEditText();
            finish();
        }
        progressDialog.dismiss();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //if (alertDialog != null) {
        //    alertDialog.dismiss();
        //}
    }

    public void onSignUpSuccess() {
        mSignUpButton.setEnabled(true);
        //Get full sUser info from newly created sUser, pass it back to LoginActivity, and set result to success
        User user = mDatabaseHandler.getUser(mEmail, mPassword);
        Intent data = new Intent();
        data.putExtra("newUserInfo", user);
        data.putExtra("mPassword", mPasswordText.getText().toString().trim());
        setResult(RESULT_OK, data);
        mSuccessfulAccountCreation = true;
    }

    public void onSignUpFailed() {
        //Show creation failed toast and enable button to re-try
        Toast.makeText(this, getString(R.string.account_creation_failed), Toast.LENGTH_LONG).show();
        mSignUpButton.setEnabled(true);
    }

    public boolean validate() {
        //Validates all input from text boxes
        boolean valid = true;
        //Is mName ET not empty and fit mName requirements
        if (mValidation.isInputEditTextFilled(mNameText, getString(R.string.name_empty))) {
            if (!mValidation.isInputEditTextName(mNameText, getString(R.string.name_empty))) {
                valid = false;
            }
        } else {
            valid = false;
        }
        //Is mEmail ET not empty and fit mEmail requirements
        if (mValidation.isInputEditTextFilled(mEmailText, getString(R.string.email_empty))) {
            if (!mValidation.isInputEditTextEmail(mEmailText, getString(R.string.enter_valid_email))) {
                valid = false;
            }
        } else {
            valid = false;
        }
        //Is mPassword ET not empty and fit mPassword requirements
        if (mValidation.isInputEditTextFilled(mPasswordText, getString(R.string.password_empty))) {
            if (!mValidation.isInputEditTextPassword(mPasswordText, getString(R.string.password_requirements))) {
                valid = false;
            }
        } else {
            valid = false;
        }
        //Is confirmPassword ET not empty and fit confirmPassword requirements
        if (mValidation.isInputEditTextFilled(mConfirmPasswordText, getString(R.string.password_confirmation_empty))) {
            if (!mValidation.isInputEditTextMatches(mPasswordText, mConfirmPasswordText, getString(R.string.password_doesnt_match))) {
                valid = false;
            }
        } else {
            valid = false;
        }
        return valid;
    }

    private void emptyInputEditText() {
        //Clear all edit text inputs
        mNameText.setText(null);
        mEmailText.setText(null);
        mPasswordText.setText(null);
        mConfirmPasswordText.setText(null);
    }

    @Override
    //On back button press, just finish this Activity returning to LoginActivity
    public void onBackPressed() {
        finish();
    }

    private void initializeVariables() {
        mNameText = findViewById(R.id.input_name);
        mEmailText = findViewById(R.id.input_email);
        mPasswordText = findViewById(R.id.input_password);
        mConfirmPasswordText = findViewById(R.id.input_confirm_password);
        mSignUpButton = findViewById(R.id.btn_signup);
        mLoginLink = findViewById(R.id.link_login);
        mValidation = new UserInputValidation(this);
        mDatabaseHandler = new DatabaseHandler(this);
        mSuccessfulAccountCreation = false;
        mName = "";
        mEmail = "";
        mPassword = "";

    }

    private void setOnClickListeners() {
        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Attempt to create account
                signUp();
            }
        });
        mLoginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                finish();
            }
        });
    }
}
