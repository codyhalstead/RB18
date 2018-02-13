package com.rentbud.activities;

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

import com.example.cody.rentbud.R;
import com.rentbud.helpers.RandomNumberGenerator;
import com.rentbud.helpers.UserInputValidation;
import com.rentbud.model.User;
import com.rentbud.sqlite.DatabaseHandler;

/**
 * Created by Cody on 12/8/2017.
 */

public class SignupActivity extends AppCompatActivity {
    private static final String TAG = "SignupActivity";
    TextInputEditText nameText;
    TextInputEditText emailText;
    TextInputEditText passwordText;
    TextInputEditText confirmPasswordText;
    Button signupButton;
    TextView loginLink;
    UserInputValidation validation;
    DatabaseHandler databaseHandler;
    private String name;
    private String email;
    private String password;
    private boolean successfulAccountCreation;
    RandomNumberGenerator rng;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        this.nameText = (TextInputEditText) findViewById(R.id.input_name);
        this.emailText = (TextInputEditText) findViewById(R.id.input_email);
        this.passwordText = (TextInputEditText) findViewById(R.id.input_password);
        this.confirmPasswordText = (TextInputEditText) findViewById(R.id.input_confirm_password);
        this.signupButton = (Button) findViewById(R.id.btn_signup);
        this.loginLink = (TextView) findViewById(R.id.link_login);
        this.validation = new UserInputValidation(this);
        this.databaseHandler = new DatabaseHandler(this);
        this.successfulAccountCreation = false;
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });
        rng = new RandomNumberGenerator();
        name = "";
        email = "";
        password = "";
        loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                finish();
            }
        });
    }

    public void signup() {
        if (!validate()) {
            onSignupFailed();
            return;
        }

        signupButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(SignupActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        this.name = nameText.getText().toString().trim();
        this.email = emailText.getText().toString().trim();
        this.password = passwordText.getText().toString().trim();

        if(!databaseHandler.checkUser(email)){
            databaseHandler.addUser(name, email, password);
            onSignupSuccess();
        }else{
            emailText.setError(getString(R.string.email_used));
            onSignupFailed();
        }

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onSignupSuccess or onSignupFailed
                        // depending on success
                        //onSignupSuccess();
                        //onSignupFailed();
                        if(successfulAccountCreation){
                            emptyInputEditText();
                            finish();
                        }
                        progressDialog.dismiss();
                    }
                }, 3000);
    }


    public void onSignupSuccess() {
        signupButton.setEnabled(true);
        User user = databaseHandler.getUser(email, password);
        Intent data = new Intent();
        data.putExtra("newUserInfo", user);
        setResult(RESULT_OK, data);
        successfulAccountCreation = true;
    }

    public void onSignupFailed() {
        Toast.makeText(this, getString(R.string.account_creation_failed), Toast.LENGTH_LONG).show();

        signupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        if (validation.isInputEditTextFilled(this.nameText, getString(R.string.name_empty))) {
            if (!validation.isInputEditTextName(this.nameText, getString(R.string.name_empty))) {
                valid = false;
            }
        } else {
            valid = false;
        }

        if (validation.isInputEditTextFilled(this.emailText, getString(R.string.email_empty))) {
            if (!validation.isInputEditTextEmail(this.emailText, getString(R.string.enter_valid_email))) {
                valid = false;
            }
        } else {
            valid = false;
        }

        if (validation.isInputEditTextFilled(this.passwordText, getString(R.string.password_empty))) {
            if (!validation.isInputEditTextPassword(this.passwordText, getString(R.string.password_requirements))) {
                valid = false;
            }
        } else {
            valid = false;
        }


        if (validation.isInputEditTextFilled(this.confirmPasswordText, getString(R.string.password_confirmation_empty))) {
            if (!validation.isInputEditTextMatches(this.passwordText, this.confirmPasswordText, getString(R.string.password_doesnt_match))) {
                valid = false;
            }
        } else {
            valid = false;
        }


        return valid;
    }

    private void emptyInputEditText() {
        nameText.setText(null);
        emailText.setText(null);
        passwordText.setText(null);
        confirmPasswordText.setText(null);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
