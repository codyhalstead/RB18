package com.rba18.activities;

import android.Manifest;
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
    private ImageButton mColorBtn;
    private LinearLayout mChangeThemeLL, mRemoveIncomeTypeLL, mRemoveExpenseTypeLL, mBackupDataLL, mImportDataLL, mChangeCurrencyLL, mChangeDateTypeLL, mRemoveUserLL,
            mChangeUserPasswordLL, mChangeUserEmailLL;
    private TextView mImportDataTV, mBackupDataTV;
    private ColorChooserDialog mDialog;
    private AlertDialog mAlertDialog;
    private DatabaseHandler mDBHandler;
    private boolean mWasDataEdited;
    private UserInputValidation mValidation;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Preferences must be initialized before setContentView because it is used in determining activities theme
        //Will be different from static MainActivity.currentThemeChoice when sUser selects themes within this activity
        //preferences = PreferenceManager.getDefaultSharedPreferences(this);
        mDBHandler = new DatabaseHandler(this);
        mValidation = new UserInputValidation(this);
        int theme = preferences.getInt(MainActivity.sUser.getEmail(), 0);
        setupUserAppTheme(theme);
        setContentView(R.layout.activity_settings);
        if (savedInstanceState != null) {
            mWasDataEdited = savedInstanceState.getBoolean("was_edited");
        } else {
            mWasDataEdited = false;
        }
        initializeVariables();
        setupBasicToolbar();
        addToolbarBackButton();
        setTitle(R.string.settings);
        //Color theme selection button to current theme choice
        Colorize(mColorBtn);
        if (mWasDataEdited) {
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

    //Shows theme chooser mDialog
    public void showColorPopup(View v) {
        //Create the mDialog
        mDialog = new ColorChooserDialog(SettingsActivity.this);
        mDialog.setColorListener(new ColorChooserDialog.ColorListener() {
            @Override
            public void OnColorClick(View v, int color) {
                //On selection, change current theme choice saved in shared preferences
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt(MainActivity.sUser.getEmail(), color);
                editor.commit();
                //Re-create activity with new theme
                Intent intent = new Intent(SettingsActivity.this, SettingsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
        mDialog.show();
    }

    private void initializeVariables() {
        mColorBtn = findViewById(R.id.button_color);
        mChangeThemeLL = findViewById(R.id.changeThemeLL);
        mChangeThemeLL.setOnClickListener(this);
        mRemoveIncomeTypeLL = findViewById(R.id.removeIncomeTypeLL);
        mRemoveIncomeTypeLL.setOnClickListener(this);
        mRemoveExpenseTypeLL = findViewById(R.id.removeExpenseTypeLL);
        mRemoveExpenseTypeLL.setOnClickListener(this);
        mBackupDataLL = findViewById(R.id.backupDataLL);
        mBackupDataLL.setOnClickListener(this);
        mBackupDataTV = findViewById(R.id.backupDataTV);
        mImportDataLL = findViewById(R.id.importDataLL);
        mImportDataLL.setOnClickListener(this);
        mImportDataTV = findViewById(R.id.importDataTV);
        mChangeCurrencyLL = findViewById(R.id.changeCurrencyLL);
        mChangeCurrencyLL.setOnClickListener(this);
        mChangeDateTypeLL = findViewById(R.id.changeDateTypeLL);
        mChangeDateTypeLL.setOnClickListener(this);
        mRemoveUserLL = findViewById(R.id.removeUserLL);
        mChangeUserEmailLL = findViewById(R.id.changeEmailLL);
        mChangeUserPasswordLL = findViewById(R.id.changePasswordLL);
        if(!MainActivity.sUser.IsGoogleAccount()) {
            mChangeUserEmailLL.setOnClickListener(this);
            mChangeUserPasswordLL.setOnClickListener(this);
            mRemoveUserLL.setOnClickListener(this);
        } else {
            mChangeUserEmailLL.setVisibility(View.GONE);
            mChangeUserPasswordLL.setVisibility(View.GONE);
            mRemoveUserLL.setVisibility(View.GONE);
        }
        if (BuildConfig.FLAVOR.equals("free")) {
            mImportDataTV.setTextColor(getResources().getColor(R.color.caldroid_lighter_gray));
            mBackupDataTV.setText(R.string.create_transfer_file);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mDialog != null) {
            mDialog.dismiss();
        }
        if (mAlertDialog != null) {
            mAlertDialog.dismiss();
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
                    mDBHandler.setTypeInactive(typeTotal);
                    MainActivity.sExpenseTypeLabels = mDBHandler.getExpenseTypeLabelsTreeMap();
                } else if (type == 2) {
                    mDBHandler.setTypeInactive(typeTotal);
                    MainActivity.sIncomeTypeLabels = mDBHandler.getExpenseTypeLabelsTreeMap();
                }
            }
        });
        // create and show the alert mDialog
        mAlertDialog = builder.create();
        mAlertDialog.show();
    }

    public void showAddRemoveIncomeTypeAlertDialog() {
        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.add_or_remove_income_type);
        builder.setNegativeButton(R.string.remove, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ArrayList<TypeTotal> incomeTypes = mDBHandler.getIncomeTypeLabelsForRemoval();
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
                mAlertDialog = new AlertDialog.Builder(SettingsActivity.this)
                        .setTitle(R.string.create_new_type)
                        .setView(editText)

                        // Set the action buttons
                        .setPositiveButton(R.string.create, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                mDBHandler.addNewIncomeType(editText.getText().toString());
                                MainActivity.sIncomeTypeLabels = mDBHandler.getIncomeTypeLabelsTreeMap();
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
                // this part will make the soft keyboard automatically visible
                editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (hasFocus) {
                            mAlertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                        }
                    }
                });

                mAlertDialog.show();
            }

        }).setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        // create and show the alert mDialog
        mAlertDialog = builder.create();
        mAlertDialog.show();
    }

    public void showAddRemoveExpenseTypeAlertDialog() {
        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.add_or_remove_expense_type);
        builder.setNegativeButton(R.string.remove, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ArrayList<TypeTotal> expenseTypes = mDBHandler.getExpenseTypeLabelsForRemoval();
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
                mAlertDialog = new AlertDialog.Builder(SettingsActivity.this)
                        .setTitle(R.string.create_new_type)
                        .setView(editText)

                        // Set the action buttons
                        .setPositiveButton(R.string.create, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                mDBHandler.addNewExpenseType(editText.getText().toString());
                                MainActivity.sExpenseTypeLabels = mDBHandler.getExpenseTypeLabelsTreeMap();
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
                // this part will make the soft keyboard automatically visible
                editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (hasFocus) {
                            mAlertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                        }
                    }
                });

                mAlertDialog.show();
            }

        }).setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        // create and show the alert mDialog
        mAlertDialog = builder.create();
        mAlertDialog.show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.button_color:
                showColorPopup(view);
                break;

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
                mAlertDialog = new AlertDialog.Builder(this)
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
                                // sUser clicked OK, so save the mSelectedItems results somewhere
                                // or return them to the component that opened the mDialog
                                int selectedPosition = (mAlertDialog).getListView().getCheckedItemPosition();
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
                                mWasDataEdited = true;
                                setResult(MainActivity.RESULT_DATA_WAS_MODIFIED, null);
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                // removes the mDialog from the screen
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
                mAlertDialog = new AlertDialog.Builder(this)

                        // specify the list array, the items to be selected by default (null for none),
                        // and the listener through which to receive call backs when items are selected
                        // again, R.array.choices were set in the resources res/values/strings.xml
                        .setSingleChoiceItems(R.array.date_choices_array, defaultDateChoice, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                            }

                        })

                        // Set the action buttons
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                // sUser clicked OK, so save the mSelectedItems results somewhere
                                // or return them to the component that opened the mDialog
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
                                mWasDataEdited = true;
                                setResult(MainActivity.RESULT_DATA_WAS_MODIFIED, null);
                            }
                        })

                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                // removes the mDialog from the screen
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
            if (BuildConfig.FLAVOR.equals("free")) {
                builder.setMessage(R.string.send_backup_to_email_question_free);
            } else {
                builder.setMessage(R.string.send_backup_to_email_question);
            }
            builder.setPositiveButton(R.string.email, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    File file;
                    file = AppFileManagementHelper.copyDBToExternal(SettingsActivity.this);
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
            // create and show the alert mDialog
            mAlertDialog = builder.create();
            mAlertDialog.show();
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
        }
    }

    public void backup(View view) {
        if (MainActivity.hasPermissions(this, android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.use_downloads_or_rentbud_folder);
            builder.setPositiveButton(R.string.rentbud_folder, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    File f = new File(Environment.getExternalStorageDirectory(), "RentalBud");
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
            // create and show the alert mDialog
            mAlertDialog = builder.create();
            mAlertDialog.show();
        } else {
            ActivityCompat.requestPermissions(
                    SettingsActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MainActivity.REQUEST_FILE_PERMISSION
            );
        }
    }

    public void displayFiles(final File downloads) {
        File[] fileList = downloads.listFiles();
        ArrayList<String> theNamesOfFiles = new ArrayList<>();
        for (int i = 0; i < fileList.length; i++) {
            if (fileList[i].getPath().endsWith(".db")) {
                if(fileList[i].getPath().contains(SettingsActivity.this.getResources().getString(R.string.backup_file_name))) {
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
                        mDBHandler.importBackupDB(backup);
                        endActivityAndRequestMainToLogUserOut();
                    }
                }
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("was_edited", mWasDataEdited);
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
        mAlertDialog = new AlertDialog.Builder(SettingsActivity.this)
                .setMessage(R.string.confirm_pass_to_delete_account_message)
                .setTitle(R.string.confirm_pass_to_delete_account_title)
                .setView(editText)

                // Set the action buttons
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String input = AppFileManagementHelper.SHA512Hash(editText.getText().toString());
                        if (mDBHandler.checkUser(MainActivity.sUser.getEmail(), input)) {
                            mDBHandler.setUserInactive(MainActivity.sUser);
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
        // this part will make the soft keyboard automatically visible
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mAlertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            }
        });

        mAlertDialog.show();
    }

    public void showConfirmPasswordForEmailChangeDialog() {
        final EditText editText = new EditText(SettingsActivity.this);
        int maxLength = 20;
        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
        editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);

        mAlertDialog = new AlertDialog.Builder(SettingsActivity.this)
                .setTitle(R.string.confirm_pass_to_change_account_info)
                .setView(editText)

                // Set the action buttons
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String input = AppFileManagementHelper.SHA512Hash(editText.getText().toString());
                        if (mDBHandler.checkUser(MainActivity.sUser.getEmail(), input)) {
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
        // this part will make the soft keyboard automatically visible
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mAlertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            }
        });

        mAlertDialog.show();
    }

    public void showChangeEmailDialog() {
        final EditText editText = new EditText(SettingsActivity.this);
        int maxLength = 50;
        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
        editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        mAlertDialog = new AlertDialog.Builder(SettingsActivity.this)
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
        // this part will make the soft keyboard automatically visible
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mAlertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            }
        });

        mAlertDialog.show();
        (mAlertDialog).getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mValidation.isInputEditTextEmail(editText, getString(R.string.enter_valid_email))) {
                    String input = editText.getText().toString();
                    MainActivity.sUser.setEmail(input);
                    mDBHandler.updateUser(MainActivity.sUser);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("last_user_email", MainActivity.sUser.getEmail());
                    editor.commit();
                    Toast.makeText(SettingsActivity.this, R.string.email_changed, Toast.LENGTH_SHORT).show();
                    mAlertDialog.dismiss();
                }
            }
        });
    }

    public void showConfirmPasswordForPassChangeDialog() {
        final EditText editText = new EditText(SettingsActivity.this);
        int maxLength = 20;
        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
        editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        mAlertDialog = new AlertDialog.Builder(SettingsActivity.this)
                .setTitle(R.string.type_old_pass)
                .setView(editText)

                // Set the action buttons
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String input = AppFileManagementHelper.SHA512Hash(editText.getText().toString());
                        if (mDBHandler.checkUser(MainActivity.sUser.getEmail(), input)) {
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
        // this part will make the soft keyboard automatically visible
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mAlertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            }
        });

        mAlertDialog.show();
    }

    public void showChangePassDialog() {
        final EditText editText = new EditText(SettingsActivity.this);
        int maxLength = 20;
        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
        editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);

        mAlertDialog = new AlertDialog.Builder(SettingsActivity.this)
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
        // this part will make the soft keyboard automatically visible
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener()

        {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mAlertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            }
        });

        mAlertDialog.show();
        (mAlertDialog).getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mValidation.isInputEditTextPassword(editText, getString(R.string.password_requirements))) {
                    String input = AppFileManagementHelper.SHA512Hash(editText.getText().toString());
                    MainActivity.sUser.setPassword(input);
                    mDBHandler.updateUser(MainActivity.sUser);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("last_user_password", MainActivity.sUser.getPassword());
                    editor.commit();
                    Toast.makeText(SettingsActivity.this, R.string.password_changed, Toast.LENGTH_SHORT).show();
                    mAlertDialog.dismiss();
                }
            }
        });

    }
}
