package com.rba18.activities;

import android.Manifest;
//import android.app.AlertDialog;
import android.support.v7.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.text.InputFilter;
import android.text.InputType;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rba18.BuildConfig;
import com.rba18.R;
import com.rba18.helpers.AppFileManagementHelper;
import com.rba18.helpers.ColorChooserDialog;
import com.rba18.helpers.DateAndCurrencyDisplayer;
import com.rba18.helpers.FileChooserDialog;
import com.rba18.helpers.CustomFileProvider;
import com.rba18.helpers.TypeChooserDialog;
import com.rba18.helpers.UserInputValidation;
import com.rba18.model.TypeTotal;
import com.rba18.sqlite.DatabaseHandler;

import java.io.File;
import java.util.ArrayList;


/**
 * Created by Cody on 1/18/2018.
 */

public class SettingsActivity extends BaseActivity implements View.OnClickListener {
    ImageButton colorBtn;
    LinearLayout changeThemeLL, removeIncomeTypeLL, removeExpenseTypeLL, backupDataLL, importDataLL, changeCurrencyLL, changeDateTypeLL, removeUserLL,
            changeUserPasswordLL, changeUserEmailLL;
    TextView importDataTV, backupDataTV;
    SharedPreferences preferences;
    private ColorChooserDialog dialog;
    private AlertDialog alertDialog;
    //private NewItemCreatorDialog newItemCreatorDialog;
    private DatabaseHandler dbHandler;
    private boolean wasDataEdited;
    UserInputValidation validation;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Preferences must be initialized before setContentView because it is used in determining activities theme
        //Will be different from static MainActivity5.currentThemeChoice when user selects themes within this activity
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        dbHandler = new DatabaseHandler(this);
        this.validation = new UserInputValidation(this);
        int theme = preferences.getInt(MainActivity.user.getEmail(), 0);
        setupUserAppTheme(theme);
        setContentView(R.layout.activity_settings);
        if (savedInstanceState != null) {
            wasDataEdited = savedInstanceState.getBoolean("was_edited");
        } else {
            wasDataEdited = false;
        }
        initializeVariables();
        setupBasicToolbar();
        this.setTitle(R.string.settings);
        //Color theme selection button to current theme choice
        Colorize(colorBtn);
        if (wasDataEdited) {
            setResult(MainActivity.RESULT_DATA_WAS_MODIFIED);
        }
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
        colorBtn.setBackground(d);
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
        backupDataTV = findViewById(R.id.backupDataTV);
        importDataLL = findViewById(R.id.importDataLL);
        importDataLL.setOnClickListener(this);
        importDataTV = findViewById(R.id.importDataTV);
        changeCurrencyLL = findViewById(R.id.changeCurrencyLL);
        changeCurrencyLL.setOnClickListener(this);
        changeDateTypeLL = findViewById(R.id.changeDateTypeLL);
        changeDateTypeLL.setOnClickListener(this);
        removeUserLL = findViewById(R.id.removeUserLL);
        removeUserLL.setOnClickListener(this);
        changeUserEmailLL = findViewById(R.id.changeEmailLL);
        changeUserEmailLL.setOnClickListener(this);
        changeUserPasswordLL = findViewById(R.id.changePasswordLL);
        changeUserPasswordLL.setOnClickListener(this);
        if (BuildConfig.FLAVOR.equals("free")) {
            importDataTV.setTextColor(getResources().getColor(R.color.caldroid_lighter_gray));
            backupDataTV.setText(R.string.backup_data);
        }
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
                final EditText editText = new EditText(SettingsActivity.this);
                int maxLength = 25;
                editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
                editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);

                // create the AlertDialog as final
                alertDialog = new AlertDialog.Builder(SettingsActivity.this)
                        //.setMessage("You are ready to type")
                        .setTitle(R.string.create_new_type)
                        .setView(editText)

                        // Set the action buttons
                        .setPositiveButton(R.string.create, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                dbHandler.addNewIncomeType(editText.getText().toString());
                                MainActivity.incomeTypeLabels = dbHandler.getIncomeTypeLabelsTreeMap();
                            }
                        })

                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                // removes the AlertDialog in the screen
                            }
                        })
                        .create();

                // set the focus change listener of the EditText
                // this part will make the soft keyboard automaticall visible
                editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (hasFocus) {
                            alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                        }
                    }
                });

                alertDialog.show();
            }

        }).setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

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
                final EditText editText = new EditText(SettingsActivity.this);
                int maxLength = 25;
                editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
                editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);

                // create the AlertDialog as final
                alertDialog = new AlertDialog.Builder(SettingsActivity.this)
                        //.setMessage("You are ready to type")
                        .setTitle(R.string.create_new_type)
                        .setView(editText)

                        // Set the action buttons
                        .setPositiveButton(R.string.create, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                dbHandler.addNewExpenseType(editText.getText().toString());
                                MainActivity.expenseTypeLabels = dbHandler.getExpenseTypeLabelsTreeMap();
                            }
                        })

                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                // removes the AlertDialog in the screen
                            }
                        })
                        .create();

                // set the focus change listener of the EditText
                // this part will make the soft keyboard automaticall visible
                editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (hasFocus) {
                            alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                        }
                    }
                });

                alertDialog.show();
            }

        }).setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

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
                if (BuildConfig.FLAVOR.equals("free")) {
                    Toast.makeText(this, R.string.import_not_available, Toast.LENGTH_LONG).show();
                } else {
                    backup(view);
                }

            case R.id.removeUserLL:
                showConfirmPasswordForAccountDeletionDialog();
                break;

            case R.id.changeEmailLL:
                showConfirmPasswordForEmailChangeDialog();
                break;

            case R.id.changePasswordLL:
                showConfirmPasswordForPassChangeDialog();
                break;

            case R.id.changeCurrencyLL:
                int oldSelection = preferences.getInt("currency", DateAndCurrencyDisplayer.CURRENCY_US);
                int defaultChoice = 0;
                if (oldSelection == DateAndCurrencyDisplayer.CURRENCY_UK) {
                    defaultChoice = 1;
                } else if (oldSelection == DateAndCurrencyDisplayer.CURRENCY_JAPAN) {
                    defaultChoice = 2;
                } else if (oldSelection == DateAndCurrencyDisplayer.CURRENCY_KOREA) {
                    defaultChoice = 3;
                } else if (oldSelection == DateAndCurrencyDisplayer.CURRENCY_GERMANY) {
                    defaultChoice = 4;
                }
                alertDialog = new AlertDialog.Builder(this)
                        // specify the list array, the items to be selected by default (null for none),
                        // and the listener through which to receive call backs when items are selected
                        // again, R.array.choices were set in the resources res/values/strings.xml
                        .setSingleChoiceItems(R.array.currency_choices_array, defaultChoice, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                //showToast("Some actions maybe? Selected index: " + arg1);
                            }
                        })
                        // Set the action buttons
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                // user clicked OK, so save the mSelectedItems results somewhere
                                // or return them to the component that opened the dialog
                                int selectedPosition = ((AlertDialog) alertDialog).getListView().getCheckedItemPosition();
                                //String choice = getResources().getStringArray(R.array.currency_choices_array)[selectedPosition];
                                //Toast.makeText(SettingsActivity.this, choice, Toast.LENGTH_LONG).show();
                                //Log.d(TAG, "onClick: " + selectedPosition);
                                //showToast("selectedPosition: " + selectedPosition);
                                SharedPreferences.Editor editor = preferences.edit();
                                if (selectedPosition == 0) {
                                    editor.putInt("currency", DateAndCurrencyDisplayer.CURRENCY_US);
                                } else if (selectedPosition == 1) {
                                    editor.putInt("currency", DateAndCurrencyDisplayer.CURRENCY_UK);
                                } else if (selectedPosition == 2) {
                                    editor.putInt("currency", DateAndCurrencyDisplayer.CURRENCY_JAPAN);
                                } else if (selectedPosition == 3) {
                                    editor.putInt("currency", DateAndCurrencyDisplayer.CURRENCY_KOREA);
                                } else if (selectedPosition == 4) {
                                    editor.putInt("currency", DateAndCurrencyDisplayer.CURRENCY_GERMANY);
                                }
                                editor.commit();
                                wasDataEdited = true;
                                setResult(MainActivity.RESULT_DATA_WAS_MODIFIED, null);
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                // removes the dialog from the screen
                            }
                        })
                        .show();

                break;

            case R.id.changeDateTypeLL:
                int oldDateSelection = preferences.getInt("dateFormat", DateAndCurrencyDisplayer.DATE_MMDDYYYY);
                int defaultDateChoice = 0;
                if (oldDateSelection == DateAndCurrencyDisplayer.DATE_DDMMYYYY) {
                    defaultDateChoice = 1;
                } else if (oldDateSelection == DateAndCurrencyDisplayer.DATE_YYYYMMDD) {
                    defaultDateChoice = 2;
                } else if (oldDateSelection == DateAndCurrencyDisplayer.DATE_YYYYDDMM) {
                    defaultDateChoice = 3;
                }
                alertDialog = new AlertDialog.Builder(this)

                        // Set the dialog title
                        // specify the list array, the items to be selected by default (null for none),
                        // and the listener through which to receive call backs when items are selected
                        // again, R.array.choices were set in the resources res/values/strings.xml
                        .setSingleChoiceItems(R.array.date_choices_array, defaultDateChoice, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                //showToast("Some actions maybe? Selected index: " + arg1);
                            }

                        })

                        // Set the action buttons
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                // user clicked OK, so save the mSelectedItems results somewhere
                                // or return them to the component that opened the dialog

                                int selectedPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                                //showToast("selectedPosition: " + selectedPosition);
                                SharedPreferences.Editor editor = preferences.edit();
                                if (selectedPosition == 0) {
                                    editor.putInt("dateFormat", DateAndCurrencyDisplayer.DATE_MMDDYYYY);
                                } else if (selectedPosition == 1) {
                                    editor.putInt("dateFormat", DateAndCurrencyDisplayer.DATE_DDMMYYYY);
                                } else if (selectedPosition == 2) {
                                    editor.putInt("dateFormat", DateAndCurrencyDisplayer.DATE_YYYYMMDD);
                                } else if (selectedPosition == 3) {
                                    editor.putInt("dateFormat", DateAndCurrencyDisplayer.DATE_YYYYDDMM);
                                }
                                editor.commit();
                                wasDataEdited = true;
                                setResult(MainActivity.RESULT_DATA_WAS_MODIFIED, null);
                            }
                        })

                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                // removes the dialog from the screen

                            }
                        })

                        .show();

                break;

            default:
                break;
        }
    }

    public void sendEmail(View view) {
        if (MainActivity.hasPermissions(this, android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.send_backip_to_email_question);
            builder.setPositiveButton(R.string.email, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    File file = null;
                    file = AppFileManagementHelper.copyDBToExternal(SettingsActivity.this);
                    //lastEmailedFilePath = file.getAbsolutePath();
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.putExtra(Intent.EXTRA_EMAIL, new String[]{""});
                    intent.putExtra(Intent.EXTRA_SUBJECT, "Backup");
                    intent.putExtra(Intent.EXTRA_TEXT, "");
                    intent.setType("application/octet-stream");
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    Uri uri = CustomFileProvider.getUriForFile(SettingsActivity.this, BuildConfig.APPLICATION_ID + ".helpers.fileprovider", file);
                    intent.putExtra(Intent.EXTRA_STREAM, uri);//Uri.parse(file.toURI().toString()));
                    startActivityForResult(Intent.createChooser(intent, SettingsActivity.this.getResources().getString(R.string.send_email)), MainActivity.REQUEST_EMAIL);
                }
            });
            builder.setNegativeButton(R.string.folder, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    File file = AppFileManagementHelper.copyDBToExternal(SettingsActivity.this);
                    if (file.exists()) {
                        Toast.makeText(SettingsActivity.this, "Success", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(SettingsActivity.this, "Failed", Toast.LENGTH_LONG).show();
                    }
                }
            });
            builder.setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            // create and show the alert dialog
            alertDialog = builder.create();
            alertDialog.show();
        } else {
            ActivityCompat.requestPermissions(
                    SettingsActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MainActivity.REQUEST_FILE_PERMISSION
            );
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MainActivity.REQUEST_FILE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {
                Toast.makeText(this, R.string.permission_file_access_denied, Toast.LENGTH_SHORT).show();
            }
            return;
        }
    }

    public void backup(View view) {
        if (MainActivity.hasPermissions(this, android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
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
        } else {
            ActivityCompat.requestPermissions(
                    SettingsActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MainActivity.REQUEST_FILE_PERMISSION
            );
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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("was_edited", wasDataEdited);
    }

    private void endActivityAndRequestMainToLogUserOut() {
        Intent intent = new Intent();
        intent.putExtra("need_to_log_out", true);

        setResult(MainActivity.RESULT_DATA_WAS_MODIFIED, intent);
        finish();
    }

    public void showConfirmPasswordForAccountDeletionDialog() {
        final EditText editText = new EditText(SettingsActivity.this);
        int maxLength = 20;
        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
        editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);

        // create the AlertDialog as final
        alertDialog = new AlertDialog.Builder(SettingsActivity.this)
                .setMessage(R.string.comfirm_pass_to_delete_account_message)
                .setTitle(R.string.comfirm_pass_to_delete_account_title)
                .setView(editText)

                // Set the action buttons
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String input = AppFileManagementHelper.SHA512Hash(editText.getText().toString());
                        if (dbHandler.checkUser(MainActivity.user.getEmail(), input)) {
                            dbHandler.setUserInactive(MainActivity.user);
                            endActivityAndRequestMainToLogUserOut();
                            Toast.makeText(SettingsActivity.this, R.string.account_removed, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(SettingsActivity.this, R.string.incorrect_password, Toast.LENGTH_SHORT).show();
                        }
                    }
                })

                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // removes the AlertDialog in the screen
                    }
                })
                .create();

        // set the focus change listener of the EditText
        // this part will make the soft keyboard automaticall visible
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            }
        });

        alertDialog.show();
    }

    public void showConfirmPasswordForEmailChangeDialog() {
        final EditText editText = new EditText(SettingsActivity.this);
        int maxLength = 20;
        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
        editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);

        // create the AlertDialog as final
        alertDialog = new AlertDialog.Builder(SettingsActivity.this)
                .setTitle(R.string.comfirm_pass_to_change_account_info)
                .setView(editText)

                // Set the action buttons
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String input = AppFileManagementHelper.SHA512Hash(editText.getText().toString());
                        if (dbHandler.checkUser(MainActivity.user.getEmail(), input)) {
                            showChangeEmailDialog();
                        } else {
                            Toast.makeText(SettingsActivity.this, R.string.incorrect_password, Toast.LENGTH_SHORT).show();
                        }
                    }
                })

                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // removes the AlertDialog in the screen
                    }
                })
                .create();

        // set the focus change listener of the EditText
        // this part will make the soft keyboard automaticall visible
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            }
        });

        alertDialog.show();
    }

    public void showChangeEmailDialog() {
        final EditText editText = new EditText(SettingsActivity.this);
        int maxLength = 50;
        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
        editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);

        // create the AlertDialog as final
        alertDialog = new AlertDialog.Builder(SettingsActivity.this)
                .setTitle(R.string.new_account_email)
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
                        // removes the AlertDialog in the screen
                    }
                })
                .create();

        // set the focus change listener of the EditText
        // this part will make the soft keyboard automaticall visible
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            }
        });

        alertDialog.show();
        ((AlertDialog) alertDialog).getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validation.isInputEditTextEmail(editText, getString(R.string.enter_valid_email))) {
                    String input = editText.getText().toString();
                    MainActivity.user.setEmail(input);
                    dbHandler.updateUser(MainActivity.user);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("last_user_email", MainActivity.user.getEmail());
                    editor.commit();
                    Toast.makeText(SettingsActivity.this, R.string.email_changed, Toast.LENGTH_SHORT).show();
                    alertDialog.dismiss();
                }
            }
        });
    }

    public void showConfirmPasswordForPassChangeDialog() {
        final EditText editText = new EditText(SettingsActivity.this);
        int maxLength = 20;
        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
        editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);

        // create the AlertDialog as final
        alertDialog = new AlertDialog.Builder(SettingsActivity.this)
                .setTitle(R.string.type_old_pass)
                .setView(editText)

                // Set the action buttons
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String input = AppFileManagementHelper.SHA512Hash(editText.getText().toString());
                        if (dbHandler.checkUser(MainActivity.user.getEmail(), input)) {
                            showChangePassDialog();
                        } else {
                            Toast.makeText(SettingsActivity.this, R.string.incorrect_password, Toast.LENGTH_SHORT).show();
                        }
                    }
                })

                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // removes the AlertDialog in the screen
                    }
                })
                .create();

        // set the focus change listener of the EditText
        // this part will make the soft keyboard automaticall visible
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            }
        });

        alertDialog.show();
    }

    public void showChangePassDialog() {
        final EditText editText = new EditText(SettingsActivity.this);
        int maxLength = 20;
        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
        editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);

        // create the AlertDialog as final
        alertDialog = new AlertDialog.Builder(SettingsActivity.this)
                .setTitle(R.string.new_account_password)
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
                        // removes the AlertDialog in the screen
                    }
                })
                .create();

        // set the focus change listener of the EditText
        // this part will make the soft keyboard automaticall visible
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener()

        {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            }
        });

        alertDialog.show();
        ((AlertDialog) alertDialog).getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validation.isInputEditTextPassword(editText, getString(R.string.password_requirements))) {
                    String input = AppFileManagementHelper.SHA512Hash(editText.getText().toString());
                    MainActivity.user.setPassword(input);
                    dbHandler.updateUser(MainActivity.user);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("last_user_password", MainActivity.user.getPassword());
                    editor.commit();
                    Toast.makeText(SettingsActivity.this, R.string.password_changed, Toast.LENGTH_SHORT).show();
                    alertDialog.dismiss();
                }
            }
        });

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
