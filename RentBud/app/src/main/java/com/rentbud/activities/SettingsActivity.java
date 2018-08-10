package com.rentbud.activities;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.cody.rentbud.BuildConfig;
import com.example.cody.rentbud.R;
import com.rentbud.helpers.ColorChooserDialog;
import com.rentbud.helpers.FileChooserDialog;
import com.rentbud.helpers.GenericFileProvider;
import com.rentbud.helpers.NewItemCreatorDialog;
import com.rentbud.helpers.TypeChooserDialog;
import com.rentbud.model.TypeTotal;
import com.rentbud.sqlite.DatabaseHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.content.ContentValues.TAG;


/**
 * Created by Cody on 1/18/2018.
 */

public class SettingsActivity extends BaseActivity implements View.OnClickListener {
    ImageButton colorBtn;
    LinearLayout changeThemeLL, removeIncomeTypeLL, removeExpenseTypeLL, backupDataLL, importDataLL;
    SharedPreferences preferences;
    private ColorChooserDialog dialog;
    private AlertDialog alertDialog;
    private NewItemCreatorDialog newItemCreatorDialog;
    private DatabaseHandler dbHandler;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Preferences must be initialized before setContentView because it is used in determining activities theme
        //Will be different from static MainActivity5.currentThemeChoice when user selects themes within this activity
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        dbHandler = new DatabaseHandler(this);
        int theme = preferences.getInt(MainActivity.user.getEmail(), 0);
        setupUserAppTheme(theme);
        setContentView(R.layout.activity_settings);
        initializeVariables();
        setupBasicToolbar();
        //Color theme selection button to current theme choice
        Colorize(colorBtn);
    }

    private void Colorize(ImageView colorBtn) {
        //Get current theme colors
        int[] colors = new int[2];
        colors[0] = fetchBackgroundColor();
        colors[1] = fetchPrimaryColor();
        //Sets color button colors to match current theme
        GradientDrawable d = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, colors);
        d.setGradientType(GradientDrawable.SWEEP_GRADIENT);
        d.setGradientCenter(-1, 0.3f);
        d.setBounds(58, 58, 58, 58);
        d.setStroke(2, Color.BLACK);
        if (Build.VERSION.SDK_INT > 15) {
            colorBtn.setBackground(d);
        } else {
            colorBtn.setBackgroundDrawable(d);
        }
    }

    //Shows theme chooser dialog
    public void showColorPopup(View v) {
        //Create the dialog
        dialog = new ColorChooserDialog(SettingsActivity.this);
        dialog.setColorListener(new ColorChooserDialog.ColorListener() {
            @Override
            public void OnColorClick(View v, int color) {
                //On selection, change current theme choice saved in shared preferences
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt(MainActivity.user.getEmail(), color);
                editor.commit();
                //Re-create activity with new theme
                Intent intent = new Intent(SettingsActivity.this, SettingsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
        dialog.show();
    }

    private void initializeVariables() {
        colorBtn = findViewById(R.id.button_color);
        changeThemeLL = findViewById(R.id.changeThemeLL);
        changeThemeLL.setOnClickListener(this);
        removeIncomeTypeLL = findViewById(R.id.removeIncomeTypeLL);
        removeIncomeTypeLL.setOnClickListener(this);
        removeExpenseTypeLL = findViewById(R.id.removeExpenseTypeLL);
        removeExpenseTypeLL.setOnClickListener(this);
        backupDataLL = findViewById(R.id.backupDataLL);
        backupDataLL.setOnClickListener(this);
        importDataLL = findViewById(R.id.importDataLL);
        importDataLL.setOnClickListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (dialog != null) {
            dialog.dismiss();
        }
        if (alertDialog != null) {
            alertDialog.dismiss();
        }
        if (newItemCreatorDialog != null) {
            newItemCreatorDialog.dismiss();
        }
    }

    public void showRemoveTypeConfirmationAlertDialog(final TypeTotal typeTotal, final int type) {
        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        StringBuilder stringBuilder = new StringBuilder(getResources().getText(R.string.remove));
        stringBuilder.append(" ");
        stringBuilder.append(typeTotal.getTypeLabel());
        stringBuilder.append(" ");
        stringBuilder.append(getResources().getText(R.string.type_question));
        builder.setMessage(stringBuilder.toString());
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        // add the buttons
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (type == 1) {
                    dbHandler.setTypeInactive(typeTotal);
                    MainActivity.expenseTypeLabels = dbHandler.getExpenseTypeLabelsTreeMap();
                } else if (type == 2) {
                    dbHandler.setTypeInactive(typeTotal);
                    MainActivity.incomeTypeLabels = dbHandler.getExpenseTypeLabelsTreeMap();
                }
            }
        });
        // create and show the alert dialog
        alertDialog = builder.create();
        alertDialog.show();
    }

    public void showAddRemoveIncomeTypeAlertDialog() {
        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.add_or_remove_income_type);
        builder.setNegativeButton(R.string.remove, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ArrayList<TypeTotal> incomeTypes = dbHandler.getIncomeTypeLabelsForRemoval();
                TypeChooserDialog typeChooserDialog2 = new TypeChooserDialog(SettingsActivity.this, incomeTypes);
                typeChooserDialog2.show();
                typeChooserDialog2.setDialogResult(new TypeChooserDialog.OnTypeChooserDialogResult() {
                    @Override
                    public void finish(TypeTotal selectedType) {
                        if (selectedType != null) {
                            showRemoveTypeConfirmationAlertDialog(selectedType, 2);
                        }
                    }
                });
            }
        });
        builder.setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                newItemCreatorDialog = new NewItemCreatorDialog(SettingsActivity.this);
                newItemCreatorDialog.show();
                newItemCreatorDialog.setDialogResult(new NewItemCreatorDialog.NewItemDialogResult() {
                    @Override
                    public void finish(String string) {
                        dbHandler.addNewIncomeType(string);
                        MainActivity.incomeTypeLabels = dbHandler.getExpenseTypeLabelsTreeMap();
                    }
                });
            }

        });
        // create and show the alert dialog
        alertDialog = builder.create();
        alertDialog.show();
    }

    public void showAddRemoveExpenseTypeAlertDialog() {
        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.add_or_remove_expense_type);
        builder.setNegativeButton(R.string.remove, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ArrayList<TypeTotal> expenseTypes = dbHandler.getExpenseTypeLabelsForRemoval();
                TypeChooserDialog typeChooserDialog2 = new TypeChooserDialog(SettingsActivity.this, expenseTypes);
                typeChooserDialog2.show();
                typeChooserDialog2.setDialogResult(new TypeChooserDialog.OnTypeChooserDialogResult() {
                    @Override
                    public void finish(TypeTotal selectedType) {
                        if (selectedType != null) {
                            showRemoveTypeConfirmationAlertDialog(selectedType, 1);
                        }
                    }
                });
            }
        });
        // add the buttons
        builder.setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                newItemCreatorDialog = new NewItemCreatorDialog(SettingsActivity.this);
                newItemCreatorDialog.show();
                newItemCreatorDialog.setDialogResult(new NewItemCreatorDialog.NewItemDialogResult() {
                    @Override
                    public void finish(String string) {
                        dbHandler.addNewExpenseType(string);
                        MainActivity.expenseTypeLabels = dbHandler.getExpenseTypeLabelsTreeMap();
                    }
                });
            }
        });
        // create and show the alert dialog
        alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.changeThemeLL:
                showColorPopup(view);
                break;

            case R.id.removeIncomeTypeLL:
                showAddRemoveIncomeTypeAlertDialog();
                break;

            case R.id.removeExpenseTypeLL:
                showAddRemoveExpenseTypeAlertDialog();
                break;

            case R.id.backupDataLL:
                sendEmail(view);
                break;

            case R.id.importDataLL:
                backup(view);
                break;

            default:
                break;
        }
    }

    public void sendEmail(View view) {
        if (!checkPermission()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.send_backip_to_email_question);
            builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    File file = null;
                    file = copyFileToExternal();
                    //lastEmailedFilePath = file.getAbsolutePath();
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.putExtra(Intent.EXTRA_EMAIL, new String[]{""});
                    intent.putExtra(Intent.EXTRA_SUBJECT, "Backup");
                    intent.putExtra(Intent.EXTRA_TEXT, "");
                    intent.setType("application/octet-stream");
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    Uri uri = GenericFileProvider.getUriForFile(SettingsActivity.this, BuildConfig.APPLICATION_ID + ".helpers.fileprovider", file);
                    intent.putExtra(Intent.EXTRA_STREAM, uri);//Uri.parse(file.toURI().toString()));
                    startActivityForResult(Intent.createChooser(intent, SettingsActivity.this.getResources().getString(R.string.send_email)), MainActivity.REQUEST_EMAIL);
                }
            });
            builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    File file = copyFileToExternal();
                    if (file.exists()) {
                        Toast.makeText(SettingsActivity.this, "Success", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(SettingsActivity.this, "Failed", Toast.LENGTH_LONG).show();
                    }
                }
            });
            // create and show the alert dialog
            alertDialog = builder.create();
            alertDialog.show();

        } else {
            if (checkPermission()) {
                requestPermissionAndContinue();
            }
        }
    }

    private File copyFileToExternal() {
        try {
            File sd = Environment.getExternalStorageDirectory();
            //File data = Environment.getDataDirectory();

            if (sd.canWrite()) {
                File f = new File(Environment.getExternalStorageDirectory(), "Rentbud");
                if (!f.exists()) {
                    f.mkdirs();
                }
                File backups = new File(f.getAbsolutePath() + "/", "Backups");
                if (!backups.exists()) {
                    backups.mkdirs();
                }
                Date today = Calendar.getInstance().getTime();
                StringBuilder stringBuilder = new StringBuilder("RentbudBackup_");
                SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy-hh:mmaa", Locale.US);
                stringBuilder.append(formatter.format(today));
                stringBuilder.append(".db");
                String currentDBPath = this.getDatabasePath(DatabaseHandler.DB_FILE_NAME).getAbsolutePath();
                String backupDBPath = stringBuilder.toString();
                File currentDB = new File(currentDBPath);
                File backupDB = new File(backups, backupDBPath);
                if (currentDB.exists()) {
                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                }
                return backupDB;
            }
        } catch (Exception e) {

        }
        return null;
    }

    private static final int PERMISSION_REQUEST_CODE = 200;

    private boolean checkPermission() {
        return ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissionAndContinue() {
        if (ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, WRITE_EXTERNAL_STORAGE)
                    && ActivityCompat.shouldShowRequestPermissionRationale(this, READ_EXTERNAL_STORAGE)) {
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
                alertBuilder.setCancelable(true);
                alertBuilder.setTitle("Hmmm");
                alertBuilder.setMessage("Uhhh");
                alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(SettingsActivity.this, new String[]{WRITE_EXTERNAL_STORAGE
                                , READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
                    }
                });
                AlertDialog alert = alertBuilder.create();
                alert.show();
                Log.e("", "permission denied, show dialog");
            } else {
                ActivityCompat.requestPermissions(SettingsActivity.this, new String[]{WRITE_EXTERNAL_STORAGE,
                        READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
            }
        } else {
            openActivity();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (permissions.length > 0 && grantResults.length > 0) {
                boolean flag = true;
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        flag = false;
                    }
                }
                if (flag) {
                    openActivity();
                } else {
                    finish();
                }
            } else {
                finish();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void openActivity() {
        //add your further process after giving permission or to download images from remote server.
    }

    public void backup(View view) {
        if (hasPermissions(this, android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
        }
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
                        dbHandler.importBackupDB(backup);
                        endActivityAndRequestMainToLogUserOut();
                    }
                }
            }
        });
    }

    private void endActivityAndRequestMainToLogUserOut(){
        Intent intent = new Intent();
        intent.putExtra("need_to_log_out", true);

        setResult(MainActivity.RESULT_DATA_WAS_MODIFIED, intent);
        finish();
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    //private class MyCopyTask extends AsyncTask<Uri, Integer, File> {
    //    ProgressDialog progressDialog;

    //    @Override
    //    protected File doInBackground(Uri... params) {
    //        File file = copyFileToExternal();
    //        try {
    //            Thread.sleep(5000);
    //        } catch (InterruptedException e) {
    //            e.printStackTrace();
    //        }
    //        return file;
    //    }

    //    @Override
    //    protected void onPostExecute(File result) {
    //        if (result.exists()) {
    //            //sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(result)));

//                Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_LONG).show();
//
    ///          } else {
    //           Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
    //     }
//
    //          // Hide ProgressDialog here
    //        //sendEmail();
    //  }
//
    //      @Override
    //    protected void onPreExecute() {
    //      // Show ProgressDialog here
//
    //          progressDialog = new ProgressDialog(SettingsActivity.this);
    //        progressDialog.setIndeterminate(true);
    //      progressDialog.setMessage("Creating Backup...");
    //    progressDialog.show();
//        }//

    //      @Override
    //    protected void onProgressUpdate(Integer... values) {
    //      super.onProgressUpdate(values);
    //}

    //}

}
