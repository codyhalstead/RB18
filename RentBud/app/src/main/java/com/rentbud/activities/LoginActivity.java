package com.rentbud.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cody.rentbud.R;
import com.rentbud.helpers.UserInputValidation;
import com.rentbud.model.User;
import com.rentbud.sqlite.DatabaseHandler;

/**
 * Created by Cody on 12/8/2017.
 */

//Activity used for log-in
public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;
    TextInputEditText emailText;
    TextInputEditText passwordText;
    Button loginButton;
    TextView signupLink;
    UserInputValidation validation;
    User user;
    DatabaseHandler databaseHandler;

    //onCreate with app default theme
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initializeVariables();
        setUpOnClickListeners();
    }

    //Log in method
    public void login() {
        //Check text input validation first
        if (!validate()) {
            onLoginFailed();
            return;
        }
        loginButton.setEnabled(false);
        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();
        //Check if user exists within database
        if (databaseHandler.checkUser(email, password)) {
            //Get full user info if exists
            this.user = databaseHandler.getUser(email, password);
            onLoginSuccess();
        } else {
            onLoginFailed();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Result from sign up activity
        if (requestCode == REQUEST_SIGNUP) {
            //If sign up successful, fill log in text boxes with new user data for easy log in
            if (resultCode == RESULT_OK) {
                this.user = (User) data.getExtras().get("newUserInfo");
                passwordText.setText(user.getPassword());
                emailText.setText(user.getEmail());
                //Creation success toast
                Toast.makeText(this, getString(R.string.account_creation_success), Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        //Disable going back to the MainActivity
        moveTaskToBack(true);
    }

    //End sign up activity on log in success, pass user info to MainActivity
    public void onLoginSuccess() {
        loginButton.setEnabled(true);
        Intent data = new Intent();
        data.putExtra("newUserInfo", this.user);
        setResult(RESULT_OK, data);
        finish();
    }

    //Show toast and clear edit text boxes on log in failed (User does not exist)
    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), getString(R.string.login_failed), Toast.LENGTH_LONG).show();
        passwordText.setText(null);
        loginButton.setEnabled(true);
    }

    //Method used to validate all of this activities edit text entries
    public boolean validate() {
        boolean valid = true;
        if (!validation.isInputEditTextEmail(this.emailText, getString(R.string.enter_valid_email))) {
            valid = false;
        }
        if (!validation.isInputEditTextPassword(this.passwordText, getString(R.string.password_requirements))) {
            valid = false;
        }
        return valid;
    }

    private void initializeVariables(){
        this.emailText = (TextInputEditText) findViewById(R.id.input_email);
        this.passwordText = (TextInputEditText) findViewById(R.id.input_password);
        this.loginButton = (Button) findViewById(R.id.btn_login);
        this.signupLink = (TextView) findViewById(R.id.link_signup);
        this.validation = new UserInputValidation(this);
        this.databaseHandler = new DatabaseHandler(this);
    }

    private void setUpOnClickListeners(){
        //Log in button and listener
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
        //Sign up link and listener
        signupLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
            }
        });
    }

    public void autoLog(View view) {
        databaseHandler.addUser("c", "c@c.c", "ccccc");
        this.user = databaseHandler.getUser("c@c.c", "ccccc");
        onLoginSuccess();
    }
}
