package com.rba18.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.PopupMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.rba18.BuildConfig;
import com.rba18.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.rba18.activities.BaseActivity;
import com.rba18.helpers.AppFileManagementHelper;
import com.rba18.helpers.DateAndCurrencyDisplayer;
import com.rba18.helpers.MainArrayDataMethods;
import com.rba18.model.Apartment;
import com.rba18.model.ExpenseLogEntry;
import com.rba18.model.Lease;
import com.rba18.model.Tenant;
import com.rba18.sqlite.DatabaseHandler;

import java.io.File;

/**
 * Created by Cody on 3/28/2018.
 */

public class ExpenseViewActivity extends BaseActivity {
    ExpenseLogEntry expense;
    TextView dateTV, amountTV, typeTV, descriptionTV, relatedLeaseTV, relatedTenantTV, relatedApartmentAddressTV,
    statusTV;
    ImageView receiptPicIV;
    DatabaseHandler databaseHandler;
    String receiptPic;
    MainArrayDataMethods dataMethods;
    Boolean wasEdited;
    private String cameraImageFilePath = "";
    private AlertDialog dialog;
    AdView adView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupUserAppTheme(MainActivity.curThemeChoice);
        setContentView(R.layout.activity_expense_view);
        this.databaseHandler = new DatabaseHandler(this);
        dataMethods = new MainArrayDataMethods();
        //if recreated
        if (savedInstanceState != null) {
            if (savedInstanceState.getParcelable("expense") != null) {
                this.expense = savedInstanceState.getParcelable("expense");
                this.receiptPic = expense.getReceiptPic();
            }
            wasEdited = savedInstanceState.getBoolean("was_edited");
            cameraImageFilePath = savedInstanceState.getString("camera_image_file_path");
        } else {
            //If new
            Bundle bundle = getIntent().getExtras();
            wasEdited = false;
            //Get apartment item
            int expenseID = bundle.getInt("expenseID");
            this.expense = databaseHandler.getExpenseLogEntryByID(expenseID, MainActivity.user);
            if (expense.getReceiptPic() != null) {
                this.receiptPic = expense.getReceiptPic();
            }
        }
        this.dateTV = findViewById(R.id.expenseViewDateTV);
        this.amountTV = findViewById(R.id.expenseViewAmountTV);
        this.typeTV = findViewById(R.id.expenseViewTypeTV);
        this.descriptionTV = findViewById(R.id.expenseViewDescriptionTV);
        this.receiptPicIV = findViewById(R.id.expenseViewReceiptPicIV);
        this.relatedLeaseTV = findViewById(R.id.expenseViewRelatedLeaseTV);
        this.relatedTenantTV = findViewById(R.id.expenseViewRelatedTenantTV);
        this.relatedApartmentAddressTV = findViewById(R.id.expenseViewRelatedApartmentAddressTV);
        this.statusTV = findViewById(R.id.expenseViewStatusTV);
        if (BuildConfig.FLAVOR.equals("free")) {
            adView = findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
            adView.loadAd(adRequest);
        }
        //this.relatedApartmentStreet2TV = findViewById(R.id.expenseViewRelatedApartmentStreet2TV);
        fillTextViews();
        setupBasicToolbar();
        this.setTitle(R.string.expense_view);
        if (wasEdited) {
            setResult(MainActivity.RESULT_DATA_WAS_MODIFIED);
        } else {
            setResult(RESULT_OK);
        }
        receiptPicIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (receiptPic != null) {
                    if (new File(receiptPic).exists()) {
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                            Intent intent = new Intent();
                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            intent.setAction(Intent.ACTION_VIEW);
                            intent.setDataAndType(Uri.fromFile(new File(receiptPic)), "image/*");
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent();
                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            intent.setAction(Intent.ACTION_VIEW);
                            Uri photoUri = FileProvider.getUriForFile(ExpenseViewActivity.this, BuildConfig.APPLICATION_ID + ".helpers.fileprovider", new File(receiptPic));
                            intent.setData(photoUri);
                            startActivity(intent);
                        }
                    } else {
                        Toast.makeText(ExpenseViewActivity.this, R.string.could_not_find_file, Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
        receiptPicIV.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (receiptPic != null) {
                    PopupMenu popup = new PopupMenu(ExpenseViewActivity.this, view);
                    MenuInflater inflater = popup.getMenuInflater();
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {

                                case R.id.removePic:
                                    ActivityCompat.requestPermissions(
                                            ExpenseViewActivity.this,
                                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                            MainActivity.REQUEST_IMAGE_DELETE_PERMISSION
                                    );
                                    return true;

                                case R.id.changePic:
                                    launchCameraOrGalleryDialog();
                                    return true;

                                default:
                                    return false;
                            }
                        }
                    });
                    inflater.inflate(R.menu.picture_long_click_menu, popup.getMenu());
                    popup.show();
                } else {
                    PopupMenu popup = new PopupMenu(ExpenseViewActivity.this, view);
                    MenuInflater inflater = popup.getMenuInflater();
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {

                                case R.id.changePic:
                                    launchCameraOrGalleryDialog();
                                    return true;

                                default:
                                    return false;
                            }
                        }
                    });
                    inflater.inflate(R.menu.picture_long_click_no_pic_menu, popup.getMenu());
                    popup.show();
                }
                return true;
            }
        });
    }

    private void fillTextViews() {
        int dateFormatCode = preferences.getInt("dateFormat", DateAndCurrencyDisplayer.DATE_MMDDYYYY);
        int moneyFormatCode = preferences.getInt("currency", DateAndCurrencyDisplayer.CURRENCY_US);
        dateTV.setText(DateAndCurrencyDisplayer.getDateToDisplay(dateFormatCode, expense.getDate()));
        amountTV.setText(DateAndCurrencyDisplayer.getCurrencyToDisplay(moneyFormatCode, expense.getAmount()));
        typeTV.setText(expense.getTypeLabel());
        if(expense.getIsCompleted()){
            statusTV.setText(R.string.paid);
        } else {
            statusTV.setText(R.string.not_paid);
        }
        descriptionTV.setText(expense.getDescription());
        if (receiptPic != null) {
            Glide.with(this).load(receiptPic).placeholder(R.drawable.no_picture)
                    .override(200, 200).centerCrop().into(receiptPicIV);
        } else {
            Glide.with(this).load(R.drawable.no_picture).override(200, 200).centerCrop().into(receiptPicIV);
        }
        setUpRelatedInfoSection();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Get options menu
        getMenuInflater().inflate(R.menu.expense_view_edit_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    //Handle option menu actions
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.editExpense:
                Intent intent = new Intent(this, NewExpenseWizard.class);
                intent.putExtra("expenseToEdit", expense);
                wasEdited = true;
                setResult(MainActivity.RESULT_DATA_WAS_MODIFIED);
                startActivityForResult(intent, MainActivity.REQUEST_NEW_EXPENSE_FORM);
                return true;

            case R.id.deleteExpense:
                showDeleteConfirmationAlertDialog();
                return true;

            case R.id.editReceiptPic:
                launchCameraOrGalleryDialog();

            case R.id.changeStatus:
                if(expense.getIsCompleted()){
                    expense.setIsCompleted(false);
                } else {
                    expense.setIsCompleted(true);
                }
                databaseHandler.editExpenseLogEntry(expense);
                fillTextViews();
                wasEdited = true;
                setResult(MainActivity.RESULT_DATA_WAS_MODIFIED);

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void launchCameraOrGalleryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.camera_or_gallery);
        builder.setPositiveButton(R.string.gallery,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        ActivityCompat.requestPermissions(
                                ExpenseViewActivity.this,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                MainActivity.REQUEST_GALLERY_FOR_MAIN_PIC
                        );
                    }
                });

        builder.setNegativeButton(R.string.camera,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        ActivityCompat.requestPermissions(
                                ExpenseViewActivity.this,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                                MainActivity.REQUEST_CAMERA_FOR_MAIN_PIC
                        );
                    }
                });
        dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onPause() {
        super.onPause();
        if(dialog != null){
            dialog.dismiss();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MainActivity.REQUEST_GALLERY_FOR_MAIN_PIC) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, MainActivity.REQUEST_GALLERY_FOR_MAIN_PIC);
            } else {
                Toast.makeText(this, R.string.permission_picture_denied, Toast.LENGTH_SHORT).show();
            }
            return;
        } else if (requestCode == MainActivity.REQUEST_IMAGE_DELETE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                databaseHandler.removeExpenseLogReceiptPic(expense);
                Glide.with(ExpenseViewActivity.this).load(R.drawable.no_picture).override(200, 200).centerCrop().into(receiptPicIV);
                new File(receiptPic).delete();
                receiptPic = null;
                expense.setReceiptPic(null);
            } else {
                Toast.makeText(this, R.string.permission_picture_denied, Toast.LENGTH_SHORT).show();
            }
            return;
        } else if (requestCode == MainActivity.REQUEST_CAMERA_FOR_MAIN_PIC) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) { ;
                Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File photoFile = AppFileManagementHelper.createImageFileFromCamera();
                cameraImageFilePath = photoFile.getAbsolutePath();
                Uri photoUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".helpers.fileprovider", photoFile);
                pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(pictureIntent, MainActivity.REQUEST_CAMERA_FOR_MAIN_PIC);
            } else {
                Toast.makeText(this, R.string.permission_picture_denied, Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void showDeleteConfirmationAlertDialog() {
        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //builder.setTitle("AlertDialog");
        builder.setMessage(R.string.expense_deletion_confirmation);

        // add the buttons
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (expense.getReceiptPic() != null) {
                    if (!expense.getReceiptPic().equals("")) {
                        new File(expense.getReceiptPic()).delete();
                    }
                }
                databaseHandler.setExpenseInactive(expense);
                setResult(MainActivity.RESULT_DATA_WAS_MODIFIED);
                ExpenseViewActivity.this.finish();
            }
        });
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        // create and show the alert dialog
        dialog = builder.create();
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MainActivity.REQUEST_NEW_EXPENSE_FORM) {
            //If successful(not cancelled, passed validation)
            if (resultCode == RESULT_OK) {
                int expenseID = data.getIntExtra("editedExpenseID", 0);
                this.expense = databaseHandler.getExpenseLogEntryByID(expenseID, MainActivity.user);
                fillTextViews();
            }
        } else if (requestCode == MainActivity.REQUEST_GALLERY_FOR_MAIN_PIC) {
            if (resultCode == RESULT_OK && data != null) {
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String filePath = cursor.getString(columnIndex);
                cursor.close();
                String oldPic = null;
                if (this.expense.getReceiptPic() != null) {
                    oldPic = this.expense.getReceiptPic();
                }
                File copiedFile = AppFileManagementHelper.copyPictureFileToApp(filePath, oldPic);
                if (copiedFile != null) {
                    expense.setReceiptPic(copiedFile.getAbsolutePath());
                    databaseHandler.changeExpenseLogReceiptPic(this.expense);
                    updateMainPicIV(expense.getReceiptPic());
                } else {
                    Toast.makeText(ExpenseViewActivity.this, R.string.failed_to_save_image, Toast.LENGTH_LONG).show();
                }
            }
        } else if (requestCode == MainActivity.REQUEST_CAMERA_FOR_MAIN_PIC) {
            if (resultCode == RESULT_OK) {
                if (expense.getReceiptPic() != null) {
                    String oldPicPath = expense.getReceiptPic();
                    File oldPic = new File(oldPicPath);
                    if (oldPic.exists()) {
                        oldPic.delete();
                    }
                }
                expense.setReceiptPic(cameraImageFilePath);
                databaseHandler.changeExpenseLogReceiptPic(expense);
                updateMainPicIV(expense.getReceiptPic());
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //Save the fragment's instance
        super.onSaveInstanceState(outState);
        outState.putParcelable("expense", expense);
        outState.putBoolean("was_edited", wasEdited);
        outState.putString("camera_image_file_path", cameraImageFilePath);
    }

    private void setUpRelatedInfoSection() {
        int dateFormatCode = preferences.getInt("dateFormat", DateAndCurrencyDisplayer.DATE_MMDDYYYY);
        Apartment relatedApartment = databaseHandler.getApartmentByID(expense.getApartmentID(), MainActivity.user);
        Tenant relatedTenant = databaseHandler.getTenantByID(expense.getTenantID(), MainActivity.user);
        Lease relatedLease = databaseHandler.getLeaseByID(MainActivity.user, expense.getLeaseID());
        if (relatedApartment != null) {
            relatedApartmentAddressTV.setText(relatedApartment.getFullAddressString());
        } else {
            relatedApartmentAddressTV.setText(R.string.na);
        }
        if (relatedTenant != null) {
            relatedTenantTV.setText(relatedTenant.getFirstAndLastNameString());
        } else {
            relatedTenantTV.setText(R.string.na);
        }
        if (relatedLease != null) {
            relatedLeaseTV.setText(relatedLease.getStartAndEndDatesString(dateFormatCode));
        } else {
            relatedLeaseTV.setText(R.string.na);
        }
    }

    public void updateMainPicIV(String picFileName) {
        Glide.with(this).load(picFileName).placeholder(R.drawable.no_picture)
                .override(200, 200).centerCrop().into(receiptPicIV);
        this.receiptPic = picFileName;

    }
}
