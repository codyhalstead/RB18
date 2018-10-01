package com.rentbud.activities;

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
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.cody.rentbud.BuildConfig;
import com.example.cody.rentbud.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.rentbud.helpers.AppFileManagementHelper;
import com.rentbud.helpers.DateAndCurrencyDisplayer;
import com.rentbud.helpers.MainArrayDataMethods;
import com.rentbud.model.Apartment;
import com.rentbud.model.Lease;
import com.rentbud.model.PaymentLogEntry;
import com.rentbud.model.Tenant;
import com.rentbud.sqlite.DatabaseHandler;

import java.io.File;

/**
 * Created by Cody on 3/28/2018.
 */

public class IncomeViewActivity extends BaseActivity {
    PaymentLogEntry income;
    TextView dateTV, amountTV, typeTV, descriptionTV, relatedLeaseTV, relatedTenantTV, relatedApartmentAddressTV,
    statusTV;
    DatabaseHandler databaseHandler;
    MainArrayDataMethods dataMethods;
    ImageView receiptPicIV;
    String receiptPic;
    Boolean wasEdited;
    private String cameraImageFilePath;
    private AlertDialog dialog;
    AdView adView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupUserAppTheme(MainActivity.curThemeChoice);
        setContentView(R.layout.activity_income_view);
        this.databaseHandler = new DatabaseHandler(this);
        dataMethods = new MainArrayDataMethods();
        //if recreated
        if (savedInstanceState != null) {
            if (savedInstanceState.getParcelable("income") != null) {
                this.income = savedInstanceState.getParcelable("income");
                this.receiptPic = income.getReceiptPic();
            }
            wasEdited = savedInstanceState.getBoolean("was_edited");
            cameraImageFilePath = savedInstanceState.getString("camera_image_file_path");
        } else {
            //If new
            Bundle bundle = getIntent().getExtras();
            wasEdited = false;
            //Get apartment item
            int incomeID = bundle.getInt("incomeID");
            this.income = databaseHandler.getPaymentLogEntryByID(incomeID, MainActivity.user);
            if (income.getReceiptPic() != null) {
                this.receiptPic = income.getReceiptPic();
            }
        }
        this.dateTV = findViewById(R.id.incomeViewDateTV);
        this.amountTV = findViewById(R.id.incomeViewAmountTV);
        this.typeTV = findViewById(R.id.incomeViewTypeTV);
        this.descriptionTV = findViewById(R.id.incomeViewDescriptionTV);
        this.receiptPicIV = findViewById(R.id.incomeViewReceiptPicIV);
        this.relatedLeaseTV = findViewById(R.id.incomeViewRelatedLeaseTV);
        this.relatedTenantTV = findViewById(R.id.incomeViewRelatedTenantTV);
        this.relatedApartmentAddressTV = findViewById(R.id.incomeViewRelatedApartmentAddressTV);
        this.statusTV = findViewById(R.id.incomeViewStatusTV);
        if (BuildConfig.FLAVOR.equals("free")) {
            adView = findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
            adView.loadAd(adRequest);
        }
        fillTextViews();
        setupBasicToolbar();
        this.setTitle(R.string.income_view);
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
                            Uri photoUri = FileProvider.getUriForFile(IncomeViewActivity.this, BuildConfig.APPLICATION_ID + ".helpers.fileprovider", new File(receiptPic));
                            intent.setData(photoUri);
                            startActivity(intent);
                        }
                    } else {
                        Toast.makeText(IncomeViewActivity.this, R.string.could_not_find_file, Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
        receiptPicIV.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (receiptPic != null) {
                    PopupMenu popup = new PopupMenu(IncomeViewActivity.this, view);
                    MenuInflater inflater = popup.getMenuInflater();
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {

                                case R.id.removePic:
                                    ActivityCompat.requestPermissions(
                                            IncomeViewActivity.this,
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
                    PopupMenu popup = new PopupMenu(IncomeViewActivity.this, view);
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
        dateTV.setText(DateAndCurrencyDisplayer.getDateToDisplay(dateFormatCode, income.getDate()));
        amountTV.setText(DateAndCurrencyDisplayer.getCurrencyToDisplay(moneyFormatCode, income.getAmount()));

        typeTV.setText(income.getTypeLabel());
        if(income.getIsCompleted()){
            statusTV.setText(R.string.received);
        } else {
            statusTV.setText(R.string.not_received);
        }
        descriptionTV.setText(income.getDescription());

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
        getMenuInflater().inflate(R.menu.income_view_edit_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    //Handle option menu actions
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.editIncome:
                Intent intent = new Intent(this, NewIncomeWizard.class);
                intent.putExtra("incomeToEdit", income);
                wasEdited = true;
                setResult(MainActivity.RESULT_DATA_WAS_MODIFIED);
                startActivityForResult(intent, MainActivity.REQUEST_NEW_INCOME_FORM);
                return true;

            case R.id.deleteIncome:
                showDeleteConfirmationAlertDialog();
                return true;

            case R.id.editReceiptPic:
                launchCameraOrGalleryDialog();

            case R.id.changeStatus:
                if(income.getIsCompleted()){
                    income.setIsCompleted(false);
                } else {
                    income.setIsCompleted(true);
                }
                databaseHandler.editPaymentLogEntry(income);
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
                                IncomeViewActivity.this,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                MainActivity.REQUEST_GALLERY_FOR_MAIN_PIC
                        );
                    }
                });

        builder.setNegativeButton(R.string.camera,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        ActivityCompat.requestPermissions(
                                IncomeViewActivity.this,
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
                databaseHandler.removePaymentLogReceiptPic(income);
                Glide.with(IncomeViewActivity.this).load(R.drawable.no_picture).override(200, 200).centerCrop().into(receiptPicIV);
                new File(receiptPic).delete();
                receiptPic = null;
                income.setReceiptPic(null);
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
        builder.setMessage(R.string.income_deletion_confirmation);

        // add the buttons
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (income.getReceiptPic() != null) {
                    if (!income.getReceiptPic().equals("")) {
                        new File(income.getReceiptPic()).delete();
                    }
                }
                databaseHandler.setPaymentLogEntryInactive(income);
                //IncomeListFragment.incomeListAdapterNeedsRefreshed = true;
                setResult(MainActivity.RESULT_DATA_WAS_MODIFIED);
                IncomeViewActivity.this.finish();
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
        if (requestCode == MainActivity.REQUEST_NEW_INCOME_FORM) {
            //If successful(not cancelled, passed validation)
            if (resultCode == RESULT_OK) {
                //Re-query cached apartment array to update cache and refresh current fragment to display new data
                int incomeID = data.getIntExtra("editedIncomeID", 0);
                this.income = databaseHandler.getPaymentLogEntryByID(incomeID, MainActivity.user);
                fillTextViews();
                //IncomeListFragment.incomeListAdapterNeedsRefreshed = true;
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
                if (this.income.getReceiptPic() != null) {
                    oldPic = this.income.getReceiptPic();
                }
                File copiedFile = AppFileManagementHelper.copyPictureFileToApp(filePath, oldPic);
                if (copiedFile != null) {
                    income.setReceiptPic(copiedFile.getAbsolutePath());
                    databaseHandler.changePaymentLogReceiptPic(this.income);
                    updateMainPicIV(income.getReceiptPic());
                } else {
                    Toast.makeText(IncomeViewActivity.this, R.string.failed_to_save_image, Toast.LENGTH_LONG).show();
                }
            }
        } else if (requestCode == MainActivity.REQUEST_CAMERA_FOR_MAIN_PIC) {
            if (resultCode == RESULT_OK) {
                if (income.getReceiptPic() != null) {
                    String oldPicPath = income.getReceiptPic();
                    File oldPic = new File(oldPicPath);
                    if (oldPic.exists()) {
                        oldPic.delete();
                    }
                }
                income.setReceiptPic(cameraImageFilePath);
                databaseHandler.changePaymentLogReceiptPic(income);
                updateMainPicIV(income.getReceiptPic());
            }
        }
    }

    private void setUpRelatedInfoSection() {
        int dateFormatCode = preferences.getInt("dateFormat", DateAndCurrencyDisplayer.DATE_MMDDYYYY);
        Apartment relatedApartment = databaseHandler.getApartmentByID(income.getApartmentID(), MainActivity.user);
        Tenant relatedTenant = databaseHandler.getTenantByID(income.getTenantID(), MainActivity.user);
        Lease relatedLease = databaseHandler.getLeaseByID(MainActivity.user, income.getLeaseID());
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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //Save the fragment's instance
        super.onSaveInstanceState(outState);
        outState.putParcelable("income", income);
        outState.putBoolean("was_edited", wasEdited);
        outState.putString("camera_image_file_path", cameraImageFilePath);
    }

    public void updateMainPicIV(String picFileName) {
        Glide.with(this).load(picFileName).placeholder(R.drawable.no_picture)
                .override(200, 200).centerCrop().into(receiptPicIV);
        this.receiptPic = picFileName;
    }
}
