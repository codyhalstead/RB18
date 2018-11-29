package com.rba18.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.text.InputFilter;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.rba18.BuildConfig;
import com.rba18.R;
import com.rba18.fragments.ApartmentViewFrag1;
import com.rba18.fragments.ApartmentViewFrag3;
import com.rba18.fragments.ApartmentViewFrag2;
import com.rba18.helpers.ApartmentTenantViewModel;
import com.rba18.helpers.AppFileManagementHelper;
import com.rba18.helpers.CustomDatePickerDialogLauncher;
import com.rba18.helpers.DateAndCurrencyDisplayer;
import com.rba18.helpers.MainArrayDataMethods;
import com.rba18.model.Apartment;
import com.rba18.model.Lease;
import com.rba18.model.Tenant;
import com.rba18.sqlite.DatabaseHandler;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ApartmentViewActivity extends BaseActivity implements View.OnClickListener,
        ApartmentViewFrag3.OnLeaseDataChangedListener,
        ApartmentViewFrag2.OnMoneyDataChangedListener,
        ApartmentViewFrag1.OnPicDataChangedListener {
    private Apartment mApartment;
    private DatabaseHandler mDatabaseHandler;
    private ApartmentViewActivity.ViewPagerAdapter mAdapter;
    private LinearLayout mDateSelectorLL;
    private Date mFilterDateStart, mFilterDateEnd;
    private Tenant mPrimaryTenant;
    private Boolean mWasLeaseEdited, mWasIncomeEdited, mWasExpenseEdited, mWasApartmentEdited;
    private CustomDatePickerDialogLauncher mDatePickerDialogLauncher;
    private Button mDateRangeStartBtn, mDateRangeEndBtn;
    private ApartmentViewFrag1 mFrag1;
    private ApartmentViewFrag2 mFrag2;
    private ApartmentViewFrag3 mFrag3;
    private ApartmentTenantViewModel mViewModel;
    private String mCameraImageFilePath;
    private AlertDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupUserAppTheme(MainActivity.sCurThemeChoice);
        setContentView(R.layout.activity_lease_view_actual);

        mDateSelectorLL = findViewById(R.id.moneyDateSelecterLL);
        mDateSelectorLL.setVisibility(View.GONE);
        mDateRangeStartBtn = findViewById(R.id.moneyListDateRangeStartBtn);
        mDateRangeStartBtn.setOnClickListener(this);
        mDateRangeEndBtn = findViewById(R.id.moneyListDateRangeEndBtn);
        mDateRangeEndBtn.setOnClickListener(this);

        ViewPager viewPager = findViewById(R.id.pager);
        mAdapter = new ApartmentViewActivity.ViewPagerAdapter(getSupportFragmentManager());
        // Add Fragments to mAdapter one by one
        mDatabaseHandler = new DatabaseHandler(this);
        MainArrayDataMethods dataMethods = new MainArrayDataMethods();
        Bundle bundle = getIntent().getExtras();
        int apartmentID = bundle.getInt("apartmentID");
        mApartment = mDatabaseHandler.getApartmentByID(apartmentID, MainActivity.sUser);
        bundle.putParcelable("mApartment", mApartment);
        mViewModel = ViewModelProviders.of(this).get(ApartmentTenantViewModel.class);
        mViewModel.init();
        mViewModel.setApartment(mApartment);
        Lease currentLease = dataMethods.getCachedActiveLeaseByApartmentID(mApartment.getId());
        ArrayList<Tenant> secondaryTenants = new ArrayList<>();
        int dateFormatCode = preferences.getInt("dateFormat", DateAndCurrencyDisplayer.DATE_MMDDYYYY);
        if (currentLease != null) {
            mPrimaryTenant = mDatabaseHandler.getTenantByID(currentLease.getPrimaryTenantID(), MainActivity.sUser);
            ArrayList<Integer> secondaryTenantIDs = currentLease.getSecondaryTenantIDs();
            for (int i = 0; i < secondaryTenantIDs.size(); i++) {
                Tenant secondaryTenant = mDatabaseHandler.getTenantByID(secondaryTenantIDs.get(i), MainActivity.sUser);
                secondaryTenants.add(secondaryTenant);
            }
        }
        mViewModel.setLease(currentLease);
        mViewModel.setPrimaryTenant(mPrimaryTenant);
        mViewModel.setSecondaryTenants(secondaryTenants);
        if (savedInstanceState != null) {
            if (savedInstanceState.getString("mFilterDateStart") != null) {
                DateFormat formatFrom = new SimpleDateFormat("yyyy/MM/dd", Locale.US);
                try {
                    mFilterDateStart = formatFrom.parse(savedInstanceState.getString("mFilterDateStart"));
                    mDateRangeStartBtn.setText(DateAndCurrencyDisplayer.getDateToDisplay(dateFormatCode, mFilterDateStart));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            if (savedInstanceState.getString("mFilterDateEnd") != null) {
                DateFormat formatFrom = new SimpleDateFormat("yyyy/MM/dd", Locale.US);
                try {
                    mFilterDateEnd = formatFrom.parse(savedInstanceState.getString("mFilterDateEnd"));
                    mDateRangeEndBtn.setText(DateAndCurrencyDisplayer.getDateToDisplay(dateFormatCode, mFilterDateEnd));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            mWasLeaseEdited = savedInstanceState.getBoolean("was_lease_edited");
            mWasIncomeEdited = savedInstanceState.getBoolean("was_income_edited");
            mWasExpenseEdited = savedInstanceState.getBoolean("was_expense_edited");
            mWasApartmentEdited = savedInstanceState.getBoolean("was_apartment_edited");
            mCameraImageFilePath = savedInstanceState.getString("camera_image_file_path");
        } else {
            Calendar calendar = Calendar.getInstance();
            mFilterDateEnd = calendar.getTime();
            calendar.add(Calendar.YEAR, -1);
            mFilterDateStart = calendar.getTime();
            mWasLeaseEdited = false;
            mWasIncomeEdited = false;
            mWasExpenseEdited = false;
            mWasApartmentEdited = false;
            mDateRangeStartBtn.setText(DateAndCurrencyDisplayer.getDateToDisplay(dateFormatCode, mFilterDateStart));
            mDateRangeEndBtn.setText(DateAndCurrencyDisplayer.getDateToDisplay(dateFormatCode, mFilterDateEnd));
            mCameraImageFilePath = "";
        }
        viewPager.setAdapter(mAdapter);
        mViewModel.setMoneyArray(mDatabaseHandler.getIncomeAndExpensesByApartmentIDWithinDates(MainActivity.sUser, mApartment.getId(), mFilterDateStart, mFilterDateEnd));
        mViewModel.setLeaseArray(mDatabaseHandler.getUsersLeasesForApartment(MainActivity.sUser, mApartment.getId()));
        ViewPager.OnPageChangeListener mPageChangeListener = new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrollStateChanged(int arg0) {

            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageSelected(int pos) {
                if (pos == 0 || pos == 2) {
                    mDateSelectorLL.setVisibility(View.GONE);
                } else {
                    mDateSelectorLL.setVisibility(View.VISIBLE);
                }
            }

        };
        viewPager.addOnPageChangeListener(mPageChangeListener);
        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        setupBasicToolbar();
        addToolbarBackButton();
        mDatePickerDialogLauncher = new CustomDatePickerDialogLauncher(mFilterDateStart, mFilterDateEnd, true, this);
        mDatePickerDialogLauncher.setDateSelectedListener(new CustomDatePickerDialogLauncher.DateSelectedListener() {
            @Override
            public void onStartDateSelected(Date startDate, Date endDate) {
                mFilterDateStart = startDate;
                mFilterDateEnd = endDate;
                mViewModel.setMoneyArray(mDatabaseHandler.getIncomeAndExpensesByApartmentIDWithinDates(MainActivity.sUser, mApartment.getId(), mFilterDateStart, mFilterDateEnd));
                int dateFormatCode = preferences.getInt("dateFormat", DateAndCurrencyDisplayer.DATE_MMDDYYYY);
                mDateRangeEndBtn.setText(DateAndCurrencyDisplayer.getDateToDisplay(dateFormatCode, mFilterDateEnd));
                mDateRangeStartBtn.setText(DateAndCurrencyDisplayer.getDateToDisplay(dateFormatCode, mFilterDateStart));
                updateFragmentDates();
            }

            @Override
            public void onEndDateSelected(Date startDate, Date endDate) {
                mFilterDateStart = startDate;
                mFilterDateEnd = endDate;
                mViewModel.setMoneyArray(mDatabaseHandler.getIncomeAndExpensesByApartmentIDWithinDates(MainActivity.sUser, mApartment.getId(), mFilterDateStart, mFilterDateEnd));
                int dateFormatCode = preferences.getInt("dateFormat", DateAndCurrencyDisplayer.DATE_MMDDYYYY);
                mDateRangeEndBtn.setText(DateAndCurrencyDisplayer.getDateToDisplay(dateFormatCode, mFilterDateEnd));
                mDateRangeStartBtn.setText(DateAndCurrencyDisplayer.getDateToDisplay(dateFormatCode, mFilterDateStart));
                updateFragmentDates();
            }

            @Override
            public void onDateSelected(Date date) {

            }
        });
        setTitle(R.string.apartment_view);
        if (mWasLeaseEdited || mWasIncomeEdited || mWasExpenseEdited || mWasApartmentEdited) {
            setResultToEdited();
        } else {
            setResult(RESULT_OK);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Get options menu
        getMenuInflater().inflate(R.menu.apartment_view_edit_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mWasLeaseEdited || mWasIncomeEdited || mWasExpenseEdited || mWasApartmentEdited) {
            setResultToEdited();
        } else {
            setResult(RESULT_OK);
        }
    }

    private void setResultToEdited() {
        Intent intent = new Intent();
        intent.putExtra("was_lease_edited", mWasLeaseEdited);
        intent.putExtra("was_income_edited", mWasIncomeEdited);
        intent.putExtra("was_expense_edited", mWasExpenseEdited);
        intent.putExtra("was_apartment_edited", mWasApartmentEdited);
        setResult(MainActivity.RESULT_DATA_WAS_MODIFIED, intent);
    }

    @Override
    //Handle option menu actions
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.editApartment:
                Intent intent = new Intent(this, NewApartmentWizard.class);
                intent.putExtra("apartmentToEdit", mApartment);
                mWasApartmentEdited = true;
                setResultToEdited();
                startActivityForResult(intent, MainActivity.REQUEST_NEW_APARTMENT_FORM);
                return true;

            case R.id.editNotes:
                showEditNotesDialog();
                return true;

            case R.id.editMainPic:
                launchCameraOrGalleryDialog(true);
                return true;

            case R.id.editOtherPics:
                if (mAdapter.getCount() > 9) {
                    Toast.makeText(this, R.string.pic_limit, Toast.LENGTH_LONG).show();
                } else {
                    launchCameraOrGalleryDialog(false);
                }
                return true;

            case R.id.deleteApartment:
                showDeleteConfirmationAlertDialog();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void launchCameraOrGalleryDialog(final boolean isMain) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.camera_or_gallery);
        builder.setPositiveButton(R.string.gallery,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        int requestCode;
                        if (isMain) {
                            requestCode = MainActivity.REQUEST_GALLERY_FOR_MAIN_PIC;
                        } else {
                            requestCode = MainActivity.REQUEST_GALLERY_FOR_OTHER_PICS;
                        }
                        ActivityCompat.requestPermissions(
                                ApartmentViewActivity.this,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                requestCode
                        );
                    }
                });

        builder.setNegativeButton(R.string.camera,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        int requestCode;
                        if (isMain) {
                            requestCode = MainActivity.REQUEST_CAMERA_FOR_MAIN_PIC;
                        } else {
                            requestCode = MainActivity.REQUEST_CAMERA_FOR_OTHER_PICS;
                        }
                        ActivityCompat.requestPermissions(
                                ApartmentViewActivity.this,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                                requestCode
                        );
                    }
                });
        mDialog = builder.create();
        mDialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == MainActivity.REQUEST_GALLERY_FOR_MAIN_PIC) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, MainActivity.REQUEST_GALLERY_FOR_MAIN_PIC);
            } else {
                Toast.makeText(this, R.string.permission_picture_denied, Toast.LENGTH_SHORT).show();
            }
            return;
        } else if (requestCode == MainActivity.REQUEST_GALLERY_FOR_OTHER_PICS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, MainActivity.REQUEST_GALLERY_FOR_OTHER_PICS);
            } else {
                Toast.makeText(this, R.string.permission_picture_denied, Toast.LENGTH_SHORT).show();
            }
            return;
        } else if (requestCode == MainActivity.REQUEST_CAMERA_FOR_MAIN_PIC) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File photoFile = AppFileManagementHelper.createImageFileFromCamera();
                mCameraImageFilePath = photoFile.getAbsolutePath();
                if (mFrag1 != null) {
                    mFrag1.updateCameraUri(mCameraImageFilePath);
                }
                Uri photoUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".helpers.fileprovider", photoFile);
                pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(pictureIntent, MainActivity.REQUEST_CAMERA_FOR_MAIN_PIC);
            } else {
                Toast.makeText(this, R.string.permission_picture_denied, Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == MainActivity.REQUEST_CAMERA_FOR_OTHER_PICS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File photoFile = AppFileManagementHelper.createImageFileFromCamera();
                mCameraImageFilePath = photoFile.getAbsolutePath();
                if (mFrag1 != null) {
                    mFrag1.updateCameraUri(mCameraImageFilePath);
                }
                Uri photoUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".helpers.fileprovider", photoFile);
                pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(pictureIntent, MainActivity.REQUEST_CAMERA_FOR_OTHER_PICS);
            } else {
                Toast.makeText(this, R.string.permission_picture_denied, Toast.LENGTH_SHORT).show();
            }
        } else {
            List<Fragment> fragments = getSupportFragmentManager().getFragments();
            if (fragments != null) {
                for (Fragment fragment : fragments) {
                    if (fragment != null) {
                        fragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
                    }
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void showDeleteConfirmationAlertDialog() {
        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //builder.setTitle("AlertDialog");
        builder.setMessage(R.string.apartment_deletion_confirmation);

        // add the buttons
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (mApartment.getMainPic() != null) {
                    if (!mApartment.getMainPic().equals("")) {
                        new File(mApartment.getMainPic()).delete();
                    }
                }
                if (!mApartment.getOtherPics().isEmpty()) {
                    for (int z = 0; z < mApartment.getOtherPics().size(); z++) {
                        new File(mApartment.getOtherPics().get(z)).delete();
                    }
                }
                mDatabaseHandler.setApartmentInactive(mApartment);
                mWasApartmentEdited = true;
                setResultToEdited();
                ApartmentViewActivity.this.finish();

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
        //Uses mApartment form to edit data
        if (requestCode == MainActivity.REQUEST_NEW_APARTMENT_FORM) {
            //If successful(not cancelled, passed validation)
            if (resultCode == RESULT_OK) {
                //Re-query cached mApartment array to update cache and refresh current textViews to display new data. Re-query to sort list
                mApartment = mDatabaseHandler.getApartmentByID(mApartment.getId(), MainActivity.sUser);
                mViewModel.setApartment(mApartment);
                List<Fragment> fragments = getSupportFragmentManager().getFragments();
                if (fragments != null) {
                    for (Fragment fragment : fragments) {
                        if (fragment != null) {
                            fragment.onActivityResult(requestCode, resultCode, data);
                        }
                    }
                }
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
                if (mApartment.getMainPic() != null) {
                    oldPic = mApartment.getMainPic();
                }
                File copiedFile = AppFileManagementHelper.copyPictureFileToApp(filePath, oldPic);
                if (copiedFile != null) {
                    mApartment.setMainPic(copiedFile.getAbsolutePath());
                    mDatabaseHandler.changeApartmentMainPic(mApartment);
                    mViewModel.setApartment(mApartment);
                } else {
                    Toast.makeText(ApartmentViewActivity.this, R.string.failed_to_save_image, Toast.LENGTH_LONG).show();
                }
                if (mFrag1 != null) {
                    if (mFrag1.isAdded()) {
                        mFrag1.updateMainPicIV(mApartment.getMainPic());
                    }
                }
                mWasApartmentEdited = true;
                setResultToEdited();
            }
        } else if (requestCode == MainActivity.REQUEST_GALLERY_FOR_OTHER_PICS) {
            if (resultCode == RESULT_OK && data != null) {
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String filePath = cursor.getString(columnIndex);
                cursor.close();
                File copiedFile = AppFileManagementHelper.copyPictureFileToApp(filePath, null);
                //if (copiedFile != null) {
                // mApartment.setMainPic(copiedFile.getAbsolutePath());
                //}
                if (copiedFile != null) {
                    mApartment.addOtherPic(copiedFile.getAbsolutePath());
                    mViewModel.setApartment(mApartment);
                    mDatabaseHandler.addApartmentOtherPic(mApartment, copiedFile.getAbsolutePath(), MainActivity.sUser);
                } else {
                    Toast.makeText(ApartmentViewActivity.this, R.string.failed_to_save_image, Toast.LENGTH_LONG).show();
                }
                if (mFrag1 != null) {
                    mFrag1.hideOtherPicsRecyclerViewIfEmpty();
                    mFrag1.refreshPictureAdapter();
                }
                mAdapter.notifyDataSetChanged();
                mWasApartmentEdited = true;
                setResultToEdited();
            }
        } else if (requestCode == MainActivity.REQUEST_CAMERA_FOR_MAIN_PIC) {
            if (resultCode == RESULT_OK) {
                if (mApartment.getMainPic() != null) {
                    String oldPicPath = mApartment.getMainPic();
                    File oldPic = new File(oldPicPath);
                    if (oldPic.exists()) {
                        oldPic.delete();
                    }
                }
                mApartment.setMainPic(mCameraImageFilePath);
                mDatabaseHandler.changeApartmentMainPic(mApartment);
                mWasApartmentEdited = true;
                setResultToEdited();
                mAdapter.notifyDataSetChanged();
            }

        } else if (requestCode == MainActivity.REQUEST_CAMERA_FOR_OTHER_PICS) {
            if (resultCode == RESULT_OK) {
                if (mApartment.getMainPic() != null) {
                    String oldPicPath = mApartment.getMainPic();
                    File oldPic = new File(oldPicPath);
                    if (oldPic.exists()) {
                        oldPic.delete();
                    }
                }
                mApartment.addOtherPic(mCameraImageFilePath);
                mDatabaseHandler.addApartmentOtherPic(mApartment, mCameraImageFilePath, MainActivity.sUser);
                mAdapter.notifyDataSetChanged();
                mWasApartmentEdited = true;
                setResultToEdited();
            }

        }

        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        if (fragments != null) {
            for (Fragment fragment : fragments) {
                if (fragment != null) {
                    fragment.onActivityResult(requestCode, resultCode, data);
                }
            }
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd", Locale.US);
        if (mFilterDateStart != null) {
            outState.putString("mFilterDateStart", formatter.format(mFilterDateStart));
        }
        if (mFilterDateEnd != null) {
            outState.putString("mFilterDateEnd", formatter.format(mFilterDateEnd));
        }
        outState.putBoolean("was_lease_edited", mWasLeaseEdited);
        outState.putBoolean("was_income_edited", mWasIncomeEdited);
        outState.putBoolean("was_expense_edited", mWasExpenseEdited);
        outState.putBoolean("was_apartment_edited", mWasApartmentEdited);
        outState.putString("camera_image_file_path", mCameraImageFilePath);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.moneyListDateRangeStartBtn:
                mDatePickerDialogLauncher.launchStartDatePickerDialog();
                break;

            case R.id.moneyListDateRangeEndBtn:
                mDatePickerDialogLauncher.launchEndDatePickerDialog();
                break;

            default:
                break;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mDatePickerDialogLauncher.dismissDatePickerDialog();
        if (mDialog != null) {
            mDialog.dismiss();
        }
    }

    private void updateFragmentDates() {
        if (mFrag2 != null) {
            mFrag2.updateData();
        }
        if (mFrag3 != null) {
            mFrag3.updateData();
        }
    }

    @Override
    public void onLeaseDataChanged() {
        mApartment = mDatabaseHandler.getApartmentByID(mApartment.getId(), MainActivity.sUser);
        mViewModel.setApartment(mApartment);
        mViewModel.setLeaseArray(mDatabaseHandler.getUsersLeasesForApartment(MainActivity.sUser, mApartment.getId()));
        mViewModel.setMoneyArray(mDatabaseHandler.getIncomeAndExpensesByApartmentIDWithinDates(MainActivity.sUser, mApartment.getId(), mFilterDateStart, mFilterDateEnd));
        mFrag2.updateData();
        mFrag3.updateData();
        mWasLeaseEdited = true;
        setResultToEdited();
    }

    @Override
    public void onLeasePaymentsChanged() {
        mWasExpenseEdited = true;
        mWasIncomeEdited = true;
        setResultToEdited();
    }

    @Override
    public void onMoneyDataChanged() {
        mViewModel.setMoneyArray(mDatabaseHandler.getIncomeAndExpensesByApartmentIDWithinDates(MainActivity.sUser, mApartment.getId(), mFilterDateStart, mFilterDateEnd));
        mFrag2.updateData();
    }

    @Override
    public void onIncomeDataChanged() {
        mWasIncomeEdited = true;
        setResultToEdited();
    }

    @Override
    public void onExpenseDataChanged() {
        mWasExpenseEdited = true;
        setResultToEdited();
    }

    @Override
    public void onPicDataChanged() {
        mWasApartmentEdited = true;
        setResultToEdited();
    }

    public void showEditNotesDialog() {
        final EditText editText = new EditText(ApartmentViewActivity.this);
        int maxLength = 500;
        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
        editText.setSingleLine(false);
        editText.setImeOptions(EditorInfo.IME_FLAG_NO_ENTER_ACTION);
        editText.setText(mApartment.getNotes());
        editText.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        editText.setSelection(editText.getText().length());

        // create the AlertDialog as final
        mDialog = new AlertDialog.Builder(ApartmentViewActivity.this)
                .setTitle(R.string.edit_notes)
                .setView(editText)

                // Set the action buttons
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String input = editText.getText().toString();
                        mApartment.setNotes(input);
                        mDatabaseHandler.editApartment(mApartment, MainActivity.sUser.getId());
                        mWasApartmentEdited = true;
                        setResultToEdited();
                        mViewModel.setApartment(mApartment);
                        if (mFrag1 != null) {
                            mFrag1.updateApartmentData(mApartment);
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

        mDialog.show();
    }

    // Adapter for the viewpager using FragmentPagerAdapter
    class ViewPagerAdapter extends FragmentStatePagerAdapter {

        private ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            Bundle bundle = new Bundle();
            bundle.putParcelable("mApartment", mApartment);
            switch (position) {
                case 0:
                    ApartmentViewFrag1 frg1 = new ApartmentViewFrag1();
                    frg1.setArguments(bundle);
                    return frg1;
                case 1:
                    ApartmentViewFrag2 frg2 = new ApartmentViewFrag2();
                    frg2.setArguments(bundle);
                    return frg2;
                case 2:
                    ApartmentViewFrag3 frg3 = new ApartmentViewFrag3();
                    frg3.setArguments(bundle);
                    return frg3;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment createdFragment = (Fragment) super.instantiateItem(container, position);
            // save the appropriate reference depending on position
            switch (position) {
                case 0:
                    mFrag1 = (ApartmentViewFrag1) createdFragment;
                    break;
                case 1:
                    mFrag2 = (ApartmentViewFrag2) createdFragment;
                    break;
                case 2:
                    mFrag3 = (ApartmentViewFrag3) createdFragment;
                    break;
            }
            return createdFragment;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getResources().getString(R.string.info_tab_title);
                case 1:
                    return getResources().getString(R.string.payments_tab_title);
                case 2:
                    return getResources().getString(R.string.lease_history_tab_title);
            }
            return "";
        }
    }
}


