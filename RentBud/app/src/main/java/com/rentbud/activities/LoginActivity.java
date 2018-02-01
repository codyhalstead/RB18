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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        this.emailText = (TextInputEditText) findViewById(R.id.input_email);
        this.passwordText = (TextInputEditText) findViewById(R.id.input_password);
        this.loginButton = (Button) findViewById(R.id.btn_login);
        this.signupLink = (TextView) findViewById(R.id.link_signup);
        this.validation = new UserInputValidation(this);
        this.user = new User();
        this.databaseHandler = new DatabaseHandler(this);

        loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });

        signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
            }
        });
    }

    public void login() {
        if (!validate()) {
            onLoginFailed();
            return;
        }

        loginButton.setEnabled(false);

       // final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
        //progressDialog.setIndeterminate(true);
        //progressDialog.setMessage("Authenticating...");
        //progressDialog.show();

        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();

        if (databaseHandler.checkUser(email, password)) {
            this.user = new User(databaseHandler.getUserName(email), email, password);
            onLoginSuccess();
        } else {
            onLoginFailed();
        }


        // TODO: Implement your own authentication logic here.

        //new android.os.Handler().postDelayed(
        //        new Runnable() {
        // public void run() {
        //               // On complete call either onLoginSuccess or onLoginFailed
        //onLoginSuccess();
        //onLoginFailed();
        //              progressDialog.dismiss();
        //          }
        //     }, 3000);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {
                this.user = (User) data.getExtras().get("newUserInfo");
                passwordText.setText(user.getPassword());
                emailText.setText(user.getEmail());
                Toast.makeText(this, getString(R.string.account_creation_success), Toast.LENGTH_LONG).show();
                //Intent intent = new Intent(this, EmailVerificationActivity.class);
                //intent.putExtra("userInfo", this.user);
                //startActivityForResult(intent, RESULT_FIRST_USER);
                //onLoginSuccess();
            }
        }
        if (requestCode == RESULT_FIRST_USER) {

        }
    }

    @Override
    public void onBackPressed() {
        // disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess() {
        loginButton.setEnabled(true);
        Intent data = new Intent();
        data.putExtra("newUserInfo", this.user);
        setResult(RESULT_OK, data);
        finish();
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), getString(R.string.login_failed), Toast.LENGTH_LONG).show();
        passwordText.setText(null);
        loginButton.setEnabled(true);
    }

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
}
