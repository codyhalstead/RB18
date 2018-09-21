package com.rentbud.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cody.rentbud.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.rentbud.helpers.AppFileManagementHelper;
import com.rentbud.helpers.FileChooserDialog;
import com.rentbud.helpers.RandomNumberGenerator;
import com.rentbud.helpers.UserInputValidation;
import com.rentbud.model.User;
import com.rentbud.sqlite.DatabaseHandler;

import java.io.File;
import java.util.ArrayList;

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
    TextView loginLink, backupRestoreLink;
    UserInputValidation validation;
    DatabaseHandler databaseHandler;
    private String name;
    private String email;
    private String password;
    private boolean successfulAccountCreation;
    private AlertDialog alertDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        initializeVariables();
        setOnClickListeners();
    }

    public void signup() {
        //If validation fails
        if (!validate()) {
            onSignupFailed();
            return;
        }
        signupButton.setEnabled(false);
        //Launch creating progressDialog
        final ProgressDialog progressDialog = new ProgressDialog(SignupActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getResources().getString(R.string.creating_account));
        progressDialog.show();
        //Get input data
        this.name = nameText.getText().toString().trim();
        this.email = emailText.getText().toString().trim();
        this.password = AppFileManagementHelper.SHA512Hash(passwordText.getText().toString().trim());
        //If user is not already in the database (based on Email), sign-up success
        if (!databaseHandler.checkUser(email)) {
            databaseHandler.addUser(name, email, password);
            onSignupSuccess();
        } else {
            //If they do already exist, set error in Email editText
            emailText.setError(getString(R.string.email_used));
            onSignupFailed();
        }
        //If creation successful, in 3 seconds empty text boxes empty and this Activity finishes
        // new android.os.Handler().postDelayed(
        //         new Runnable() {
        //             public void run() {
        if (successfulAccountCreation) {
            emptyInputEditText();
            finish();
        }
        progressDialog.dismiss();
        //              }
        //         }, 3000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (alertDialog != null) {
            alertDialog.dismiss();
        }
    }

    public void onSignupSuccess() {
        signupButton.setEnabled(true);
        //Get full user info from newly created user, pass it back to LoginActivity, and set result to success
        User user = databaseHandler.getUser(email, password);
        Intent data = new Intent();
        data.putExtra("newUserInfo", user);
        data.putExtra("password", passwordText.getText().toString().trim());
        setResult(RESULT_OK, data);
        successfulAccountCreation = true;
    }

    public void onSignupFailed() {
        //Show creation failed toast and enable button to re-try
        Toast.makeText(this, getString(R.string.account_creation_failed), Toast.LENGTH_LONG).show();
        signupButton.setEnabled(true);
    }

    public boolean validate() {
        //Validates all input from text boxes
        boolean valid = true;
        //Is name ET not empty and fit name requirements
        if (validation.isInputEditTextFilled(this.nameText, getString(R.string.name_empty))) {
            if (!validation.isInputEditTextName(this.nameText, getString(R.string.name_empty))) {
                valid = false;
            }
        } else {
            valid = false;
        }
        //Is email ET not empty and fit email requirements
        if (validation.isInputEditTextFilled(this.emailText, getString(R.string.email_empty))) {
            if (!validation.isInputEditTextEmail(this.emailText, getString(R.string.enter_valid_email))) {
                valid = false;
            }
        } else {
            valid = false;
        }
        //Is password ET not empty and fit password requirements
        if (validation.isInputEditTextFilled(this.passwordText, getString(R.string.password_empty))) {
            if (!validation.isInputEditTextPassword(this.passwordText, getString(R.string.password_requirements))) {
                valid = false;
            }
        } else {
            valid = false;
        }
        //Is confirmPassword ET not empty and fit confirmPassword requirements
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
        //Clear all edit text inputs
        nameText.setText(null);
        emailText.setText(null);
        passwordText.setText(null);
        confirmPasswordText.setText(null);
    }

    @Override
    //On back button press, just finish this Activity returning to LoginActivity
    public void onBackPressed() {
        finish();
    }

    private void initializeVariables() {
        this.nameText = findViewById(R.id.input_name);
        this.emailText = findViewById(R.id.input_email);
        this.passwordText = findViewById(R.id.input_password);
        this.confirmPasswordText = findViewById(R.id.input_confirm_password);
        this.signupButton = findViewById(R.id.btn_signup);
        this.loginLink = findViewById(R.id.link_login);
        this.backupRestoreLink = findViewById(R.id.backup_link_signup);
        this.validation = new UserInputValidation(this);
        this.databaseHandler = new DatabaseHandler(this);
        this.successfulAccountCreation = false;
        this.name = "";
        this.email = "";
        this.password = "";
        if (!databaseHandler.isUserTableEmpty()) {
            backupRestoreLink.setVisibility(View.GONE);
        }
    }

    private void setOnClickListeners() {
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Attempt to create account
                signup();
            }
        });
        loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                finish();
            }
        });
        backupRestoreLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //public void backup(View view) {
                if (MainActivity.hasPermissions(SignupActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(SignupActivity.this);
                    builder.setMessage(R.string.use_downloads_or_rentbud_folder);
                    builder.setPositiveButton(R.string.rentbud_folder, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            File f = new File(Environment.getExternalStorageDirectory(), "Rentbud");
                            if (!f.exists()) {
                                f.mkdirs();
                            }
                            File downloads = new File(f.getAbsolutePath() + "/", "Backups");
                            if (!downloads.exists()) {
                                downloads.mkdirs();
                            }
                            displayFiles(downloads);
                        }
                    });
                    builder.setNegativeButton(R.string.downloads, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            File downloads = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath());
                            displayFiles(downloads);
                        }
                    });
                    // create and show the alert dialog
                    alertDialog = builder.create();
                    alertDialog.show();
                } else {
                    ActivityCompat.requestPermissions(
                            SignupActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            MainActivity.REQUEST_FILE_PERMISSION
                    );
                }
            }
        });

        //   }
        // });
    }

    public void displayFiles(final File downloads) {
        File[] filelist = downloads.listFiles();
        ArrayList<String> theNamesOfFiles = new ArrayList<>();
        for (int i = 0; i < filelist.length; i++) {
            if (filelist[i].getPath().endsWith(".db")) {
                theNamesOfFiles.add(filelist[i].getName());
            }
            //Log.d("TAG", "backup: " + filelist[i]);
            //Toast.makeText(this, i, Toast.LENGTH_LONG).show();
        }
        final FileChooserDialog typeChooserDialog2 = new FileChooserDialog(this, theNamesOfFiles);
        typeChooserDialog2.show();
        typeChooserDialog2.setDialogResult(new FileChooserDialog.OnTypeChooserDialogResult() {
            @Override
            public void finish(String fileName) {
                if (fileName != null) {
                    File backup = new File(downloads.getAbsolutePath() + "/" + fileName);
                    if (backup.exists()) {
                        databaseHandler.importBackupDB(backup);
                        SignupActivity.this.finish();
                    }
                }
            }
        });
    }
}
