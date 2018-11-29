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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.rba18.BuildConfig;
import com.rba18.R;
import com.rba18.helpers.AppFileManagementHelper;
import com.rba18.helpers.DateAndCurrencyDisplayer;
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
    private ExpenseLogEntry mExpense;
    private TextView mDateTV, mAmountTV, mTypeTV, mDescriptionTV, mRelatedLeaseTV, mRelatedTenantTV, mRelatedApartmentAddressTV,
    mStatusTV;
    private ImageView mReceiptPicIV;
    private DatabaseHandler mDatabaseHandler;
    private String mReceiptPic;
    private Boolean mWasEdited;
    private String mCameraImageFilePath = "";
    private AlertDialog mDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupUserAppTheme(MainActivity.sCurThemeChoice);
        setContentView(R.layout.activity_expense_view);
        mDatabaseHandler = new DatabaseHandler(this);
        //if recreated
        if (savedInstanceState != null) {
            if (savedInstanceState.getParcelable("mExpense") != null) {
                mExpense = savedInstanceState.getParcelable("mExpense");
                mReceiptPic = mExpense.getReceiptPic();
            }
            mWasEdited = savedInstanceState.getBoolean("was_edited");
            mCameraImageFilePath = savedInstanceState.getString("camera_image_file_path");
        } else {
            //If new
            Bundle bundle = getIntent().getExtras();
            mWasEdited = false;
            //Get apartment item
            int expenseID = bundle.getInt("expenseID");
            mExpense = mDatabaseHandler.getExpenseLogEntryByID(expenseID, MainActivity.sUser);
            if (mExpense.getReceiptPic() != null) {
                mReceiptPic = mExpense.getReceiptPic();
            }
        }
        mDateTV = findViewById(R.id.expenseViewDateTV);
        mAmountTV = findViewById(R.id.expenseViewAmountTV);
        mTypeTV = findViewById(R.id.expenseViewTypeTV);
        mDescriptionTV = findViewById(R.id.expenseViewDescriptionTV);
        mReceiptPicIV = findViewById(R.id.expenseViewReceiptPicIV);
        mRelatedLeaseTV = findViewById(R.id.expenseViewRelatedLeaseTV);
        mRelatedTenantTV = findViewById(R.id.expenseViewRelatedTenantTV);
        mRelatedApartmentAddressTV = findViewById(R.id.expenseViewRelatedApartmentAddressTV);
        mStatusTV = findViewById(R.id.expenseViewStatusTV);
        LinearLayout adViewLL = findViewById(R.id.adViewLL);
        if (BuildConfig.FLAVOR.equals("free")) {
            //TODO enable for release
            //AdView adView = findViewById(R.id.adView);
            //AdRequest adRequest = new AdRequest.Builder().build();
            //adView.loadAd(adRequest);
        } else {
            adViewLL.setVisibility(View.GONE);
        }
        fillTextViews();
        setupBasicToolbar();
        addToolbarBackButton();
        setTitle(R.string.expense_view);
        if (mWasEdited) {
            setResult(MainActivity.RESULT_DATA_WAS_MODIFIED);
        } else {
            setResult(RESULT_OK);
        }
        mReceiptPicIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mReceiptPic != null) {
                    if (new File(mReceiptPic).exists()) {
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                            Intent intent = new Intent();
                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            intent.setAction(Intent.ACTION_VIEW);
                            intent.setDataAndType(Uri.fromFile(new File(mReceiptPic)), "image/*");
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent();
                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            intent.setAction(Intent.ACTION_VIEW);
                            Uri photoUri = FileProvider.getUriForFile(ExpenseViewActivity.this, BuildConfig.APPLICATION_ID + ".helpers.fileprovider", new File(mReceiptPic));
                            intent.setData(photoUri);
                            startActivity(intent);
                        }
                    } else {
                        Toast.makeText(ExpenseViewActivity.this, R.string.could_not_find_file, Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
        mReceiptPicIV.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (mReceiptPic != null) {
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
        mDateTV.setText(DateAndCurrencyDisplayer.getDateToDisplay(dateFormatCode, mExpense.getDate()));
        mAmountTV.setText(DateAndCurrencyDisplayer.getCurrencyToDisplay(moneyFormatCode, mExpense.getAmount()));
        mTypeTV.setText(mExpense.getTypeLabel());
        if(mExpense.getIsCompleted()){
            mStatusTV.setText(R.string.paid);
        } else {
            mStatusTV.setText(R.string.not_paid);
        }
        mDescriptionTV.setText(mExpense.getDescription());
        if (mReceiptPic != null) {
            Glide.with(this).load(mReceiptPic).placeholder(R.drawable.no_picture)
                    .override(200, 200).centerCrop().into(mReceiptPicIV);
        } else {
            Glide.with(this).load(R.drawable.no_picture).override(200, 200).centerCrop().into(mReceiptPicIV);
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
                intent.putExtra("expenseToEdit", mExpense);
                mWasEdited = true;
                setResult(MainActivity.RESULT_DATA_WAS_MODIFIED);
                startActivityForResult(intent, MainActivity.REQUEST_NEW_EXPENSE_FORM);
                return true;

            case R.id.deleteExpense:
                showDeleteConfirmationAlertDialog();
                return true;

            case R.id.editReceiptPic:
                launchCameraOrGalleryDialog();

            case R.id.changeStatus:
                if(mExpense.getIsCompleted()){
                    mExpense.setIsCompleted(false);
                } else {
                    mExpense.setIsCompleted(true);
                }
                mDatabaseHandler.editExpenseLogEntry(mExpense);
                fillTextViews();
                mWasEdited = true;
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
        mDialog = builder.create();
        mDialog.show();
    }

    @Override
    public void onPause() {
        super.onPause();
        if(mDialog != null){
            mDialog.dismiss();
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
                mDatabaseHandler.removeExpenseLogReceiptPic(mExpense);
                Glide.with(ExpenseViewActivity.this).load(R.drawable.no_picture).override(200, 200).centerCrop().into(mReceiptPicIV);
                new File(mReceiptPic).delete();
                mReceiptPic = null;
                mExpense.setReceiptPic(null);
            } else {
                Toast.makeText(this, R.string.permission_picture_denied, Toast.LENGTH_SHORT).show();
            }
            return;
        } else if (requestCode == MainActivity.REQUEST_CAMERA_FOR_MAIN_PIC) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File photoFile = AppFileManagementHelper.createImageFileFromCamera();
                mCameraImageFilePath = photoFile.getAbsolutePath();
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
                if (mExpense.getReceiptPic() != null) {
                    if (!mExpense.getReceiptPic().equals("")) {
                        new File(mExpense.getReceiptPic()).delete();
                    }
                }
                mDatabaseHandler.setExpenseInactive(mExpense);
                setResult(MainActivity.RESULT_DATA_WAS_MODIFIED);
                ExpenseViewActivity.this.finish();
            }
        });
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        // create and show the alert mDialog
        mDialog = builder.create();
        mDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MainActivity.REQUEST_NEW_EXPENSE_FORM) {
            //If successful(not cancelled, passed validation)
            if (resultCode == RESULT_OK) {
                int expenseID = data.getIntExtra("editedExpenseID", 0);
                mExpense = mDatabaseHandler.getExpenseLogEntryByID(expenseID, MainActivity.sUser);
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
                if (mExpense.getReceiptPic() != null) {
                    oldPic = mExpense.getReceiptPic();
                }
                File copiedFile = AppFileManagementHelper.copyPictureFileToApp(filePath, oldPic);
                if (copiedFile != null) {
                    mExpense.setReceiptPic(copiedFile.getAbsolutePath());
                    mDatabaseHandler.changeExpenseLogReceiptPic(mExpense);
                    updateMainPicIV(mExpense.getReceiptPic());
                } else {
                    Toast.makeText(ExpenseViewActivity.this, R.string.failed_to_save_image, Toast.LENGTH_LONG).show();
                }
            }
        } else if (requestCode == MainActivity.REQUEST_CAMERA_FOR_MAIN_PIC) {
            if (resultCode == RESULT_OK) {
                if (mExpense.getReceiptPic() != null) {
                    String oldPicPath = mExpense.getReceiptPic();
                    File oldPic = new File(oldPicPath);
                    if (oldPic.exists()) {
                        oldPic.delete();
                    }
                }
                mExpense.setReceiptPic(mCameraImageFilePath);
                mDatabaseHandler.changeExpenseLogReceiptPic(mExpense);
                updateMainPicIV(mExpense.getReceiptPic());
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //Save the fragment's instance
        super.onSaveInstanceState(outState);
        outState.putParcelable("mExpense", mExpense);
        outState.putBoolean("was_edited", mWasEdited);
        outState.putString("camera_image_file_path", mCameraImageFilePath);
    }

    private void setUpRelatedInfoSection() {
        int dateFormatCode = preferences.getInt("dateFormat", DateAndCurrencyDisplayer.DATE_MMDDYYYY);
        Apartment relatedApartment = mDatabaseHandler.getApartmentByID(mExpense.getApartmentID(), MainActivity.sUser);
        Tenant relatedTenant = mDatabaseHandler.getTenantByID(mExpense.getTenantID(), MainActivity.sUser);
        Lease relatedLease = mDatabaseHandler.getLeaseByID(MainActivity.sUser, mExpense.getLeaseID());
        if (relatedApartment != null) {
            mRelatedApartmentAddressTV.setText(relatedApartment.getFullAddressString());
        } else {
            mRelatedApartmentAddressTV.setText(R.string.na);
        }
        if (relatedTenant != null) {
            mRelatedTenantTV.setText(relatedTenant.getFirstAndLastNameString());
        } else {
            mRelatedTenantTV.setText(R.string.na);
        }
        if (relatedLease != null) {
            mRelatedLeaseTV.setText(relatedLease.getStartAndEndDatesString(dateFormatCode));
        } else {
            mRelatedLeaseTV.setText(R.string.na);
        }
    }

    public void updateMainPicIV(String picFileName) {
        Glide.with(this).load(picFileName).placeholder(R.drawable.no_picture)
                .override(200, 200).centerCrop().into(mReceiptPicIV);
        mReceiptPic = picFileName;

    }
}
