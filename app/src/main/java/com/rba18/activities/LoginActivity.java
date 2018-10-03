package com.rba18.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.rba18.R;
import com.rba18.helpers.AppFileManagementHelper;
import com.rba18.helpers.FileChooserDialog;
import com.rba18.helpers.GMailSender;
import com.rba18.helpers.RandomNumberGenerator;
import com.rba18.helpers.UserInputValidation;
import com.rba18.model.User;
import com.rba18.sqlite.DatabaseHandler;

import java.io.File;
import java.util.ArrayList;

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
    TextView signupLink, forgotPassTV, backupRestoreLink;
    UserInputValidation validation;
    User user;
    DatabaseHandler databaseHandler;
    RandomNumberGenerator randomNumberGenerator;
    private AlertDialog alertDialog;

    //onCreate with app default theme
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initializeVariables();
        setUpOnClickListeners();
    }

    static {
        System.loadLibrary("native-lib");
    }

    private native String getEmailString();

    public String loadPass() {
        return getEmailString();
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
        String password = AppFileManagementHelper.SHA512Hash(passwordText.getText().toString());
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
                String password = data.getExtras().getString("password");
                passwordText.setText(password);
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

    private void initializeVariables() {
        this.emailText = findViewById(R.id.input_email);
        this.passwordText = findViewById(R.id.input_password);
        this.loginButton = findViewById(R.id.btn_login);
        this.signupLink =  findViewById(R.id.link_signup);
        this.forgotPassTV = findViewById(R.id.link_forgot_password);
        this.backupRestoreLink = findViewById(R.id.backup_link_login);
        this.validation = new UserInputValidation(this);
        this.databaseHandler = new DatabaseHandler(this);
        randomNumberGenerator = new RandomNumberGenerator();
        if (!databaseHandler.isUserTableEmpty()) {
            backupRestoreLink.setVisibility(View.GONE);
        }
    }

    private void setUpOnClickListeners() {
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
        forgotPassTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(LoginActivity.this, new String[]{Manifest.permission.INTERNET}, MainActivity.REQUEST_PHONE_CALL_PERMISSION);
                } else {
                    if (isInternetAvailable()) {
                        showForgotPasswordGetEmailDialog();
                    } else {
                        Toast.makeText(LoginActivity.this, R.string.no_internet_connection, Toast.LENGTH_LONG).show();
                    }
                }

            }
        });
        backupRestoreLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MainActivity.hasPermissions(LoginActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
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
                            LoginActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            MainActivity.REQUEST_FILE_PERMISSION
                    );
                }
            }
        });
    }

    private void showForgotPasswordGetEmailDialog() {
        final EditText editText = new EditText(LoginActivity.this);
        int maxLength = 25;
        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
        editText.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        alertDialog = new AlertDialog.Builder(LoginActivity.this)
                .setTitle(R.string.enter_account_email)
                .setView(editText)

                // Set the action buttons
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                    }
                })

                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                    }
                })
                .create();

        // set the focus change listener of the EditText
        // this part will make the soft keyboard automatically visible
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            }
        });

        alertDialog.show();
        (alertDialog).getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validation.isInputEditTextPassword(editText, getString(R.string.password_requirements))) {
                if (databaseHandler.checkUser(editText.getText().toString().trim())) {
                    alertDialog.dismiss();
                    showPasswordResetConfirmationDialog(editText.getText().toString().trim());
                } else {
                    editText.setText("");
                    Toast.makeText(LoginActivity.this, R.string.no_account_for_email, Toast.LENGTH_LONG).show();
                }
                }
            }
        });
    }

    private void showPasswordResetConfirmationDialog(final String email) {
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setMessage(R.string.password_recovery_confirmation);
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                recoverPasswordThroughEmail(email);
            }
        });

        // add the buttons
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        // create and show the alert dialog
        alertDialog = builder.create();
        alertDialog.show();
    }

    private void recoverPasswordThroughEmail(final String email) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (isInternetAvailable()) {
                    try {
                        String newPass = randomNumberGenerator.gererateVerificationNumber(6);
                        String subject = LoginActivity.this.getResources().getString(R.string.recovery_email_subject);
                        StringBuilder body = new StringBuilder(LoginActivity.this.getResources().getString(R.string.recovery_email_body));
                        body.append("\n");
                        body.append(newPass);
                        body.append("\n");
                        body.append(LoginActivity.this.getResources().getString(R.string.recovery_email_body2));
                        GMailSender sender = new GMailSender(LoginActivity.this.getResources().getString(R.string.pass_rec_email_addr), loadPass());
                        sender.sendMail(subject,
                                body.toString(),
                                LoginActivity.this.getResources().getString(R.string.pass_rec_email_addr),
                                email);
                        changeAccountPassword(email, newPass);
                    } catch (Exception e) {
                        Toast.makeText(LoginActivity.this, R.string.recovery_error, Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, R.string.no_internet_connection, Toast.LENGTH_LONG).show();
                }
            }
        }).start();
    }

    private void changeAccountPassword(String email, String newPassword) {
        int userID = databaseHandler.getUserID(email);
        String name = databaseHandler.getUserName(email);
        String password = AppFileManagementHelper.SHA512Hash(newPassword);
        User user = new User(userID, name, email, password);
        databaseHandler.updateUser(user);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (alertDialog != null) {
            alertDialog.dismiss();
        }
    }

    public boolean isInternetAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void displayFiles(final File downloads) {
        File[] fileList = downloads.listFiles();
        ArrayList<String> theNamesOfFiles = new ArrayList<>();
        for (int i = 0; i < fileList.length; i++) {
            if (fileList[i].getPath().endsWith(".db")) {
                if(fileList[i].getPath().contains(LoginActivity.this.getResources().getString(R.string.backup_file_name))) {
                    theNamesOfFiles.add(fileList[i].getName());
                }
            }
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
                        LoginActivity.this.finish();
                    }
                }
            }
        });
    }

    //public void autoLog(View view) {
    //    databaseHandler.addUser("c", "c@c.c", "ccccc");
    //    this.user = databaseHandler.getUser("c@c.c", "ccccc");
    //    onLoginSuccess();
    //}
}
