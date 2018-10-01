package com.rentbud.activities;

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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.cody.rentbud.BuildConfig;
import com.example.cody.rentbud.R;
import com.rentbud.fragments.ApartmentViewFrag1;
import com.rentbud.fragments.ApartmentViewFrag3;
import com.rentbud.fragments.ApartmentViewFrag2;
import com.rentbud.helpers.ApartmentTenantViewModel;
import com.rentbud.helpers.AppFileManagementHelper;
import com.rentbud.helpers.CustomDatePickerDialogLauncher;
import com.rentbud.helpers.DateAndCurrencyDisplayer;
import com.rentbud.helpers.MainArrayDataMethods;
import com.rentbud.model.Apartment;
import com.rentbud.model.Lease;
import com.rentbud.model.Tenant;
import com.rentbud.sqlite.DatabaseHandler;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.support.constraint.Constraints.TAG;

public class ApartmentViewActivity extends BaseActivity implements View.OnClickListener,
        ApartmentViewFrag3.OnLeaseDataChangedListener,
        ApartmentViewFrag2.OnMoneyDataChangedListener,
        ApartmentViewFrag1.OnPicDataChangedListener {
    Apartment apartment;
    MainArrayDataMethods dataMethods;
    DatabaseHandler databaseHandler;
    ViewPager.OnPageChangeListener mPageChangeListener;
    ViewPager viewPager;
    ApartmentViewActivity.ViewPagerAdapter adapter;
    LinearLayout dateSelectorLL;
    Date filterDateStart, filterDateEnd;
    Tenant primaryTenant;
    ArrayList<Tenant> secondaryTenants;
    Lease currentLease;
    private Boolean wasLeaseEdited, wasIncomeEdited, wasExpenseEdited, wasApartmentEdited;
    private CustomDatePickerDialogLauncher datePickerDialogLauncher;
    Button dateRangeStartBtn, dateRangeEndBtn;
    private ApartmentViewFrag1 frag1;
    private ApartmentViewFrag2 frag2;
    private ApartmentViewFrag3 frag3;
    private ApartmentTenantViewModel viewModel;
    private String cameraImageFilePath;
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupUserAppTheme(MainActivity.curThemeChoice);
        setContentView(R.layout.activity_lease_view_actual);

        dateSelectorLL = findViewById(R.id.moneyDateSelecterLL);
        dateSelectorLL.setVisibility(View.GONE);
        this.dateRangeStartBtn = findViewById(R.id.moneyListDateRangeStartBtn);
        this.dateRangeStartBtn.setOnClickListener(this);
        this.dateRangeEndBtn = findViewById(R.id.moneyListDateRangeEndBtn);
        this.dateRangeEndBtn.setOnClickListener(this);

        viewPager = findViewById(R.id.pager);
        adapter = new ApartmentViewActivity.ViewPagerAdapter(getSupportFragmentManager());
        // Add Fragments to adapter one by one
        this.databaseHandler = new DatabaseHandler(this);
        this.dataMethods = new MainArrayDataMethods();
        Bundle bundle = getIntent().getExtras();
        int apartmentID = bundle.getInt("apartmentID");
        this.apartment = databaseHandler.getApartmentByID(apartmentID, MainActivity.user);
        bundle.putParcelable("apartment", apartment);
        viewModel = ViewModelProviders.of(this).get(ApartmentTenantViewModel.class);
        viewModel.init();
        viewModel.setApartment(apartment);
        currentLease = dataMethods.getCachedActiveLeaseByApartmentID(apartment.getId());
        secondaryTenants = new ArrayList<>();
        int dateFormatCode = preferences.getInt("dateFormat", DateAndCurrencyDisplayer.DATE_MMDDYYYY);
        if (currentLease != null) {
            primaryTenant = databaseHandler.getTenantByID(currentLease.getPrimaryTenantID(), MainActivity.user);
            ArrayList<Integer> secondaryTenantIDs = currentLease.getSecondaryTenantIDs();
            for (int i = 0; i < secondaryTenantIDs.size(); i++) {
                Tenant secondaryTenant = databaseHandler.getTenantByID(secondaryTenantIDs.get(i), MainActivity.user);
                secondaryTenants.add(secondaryTenant);
            }
        }
        viewModel.setLease(currentLease);
        viewModel.setPrimaryTenant(primaryTenant);
        viewModel.setSecondaryTenants(secondaryTenants);
        if (savedInstanceState != null) {
            if (savedInstanceState.getString("filterDateStart") != null) {
                DateFormat formatFrom = new SimpleDateFormat("yyyy/MM/dd", Locale.US);
                try {
                    Date startDate = formatFrom.parse(savedInstanceState.getString("filterDateStart"));
                    this.filterDateStart = startDate;
                    this.dateRangeStartBtn.setText(DateAndCurrencyDisplayer.getDateToDisplay(dateFormatCode, filterDateStart));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            if (savedInstanceState.getString("filterDateEnd") != null) {
                DateFormat formatFrom = new SimpleDateFormat("yyyy/MM/dd", Locale.US);
                try {
                    Date endDate = formatFrom.parse(savedInstanceState.getString("filterDateEnd"));
                    this.filterDateEnd = endDate;
                    this.dateRangeEndBtn.setText(DateAndCurrencyDisplayer.getDateToDisplay(dateFormatCode, filterDateEnd));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            wasLeaseEdited = savedInstanceState.getBoolean("was_lease_edited");
            wasIncomeEdited = savedInstanceState.getBoolean("was_income_edited");
            wasExpenseEdited = savedInstanceState.getBoolean("was_expense_edited");
            wasApartmentEdited = savedInstanceState.getBoolean("was_apartment_edited");
            cameraImageFilePath = savedInstanceState.getString("camera_image_file_path");
        } else {
            Date endDate = Calendar.getInstance().getTime();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(endDate);
            calendar.add(Calendar.YEAR, -1);
            Date startDate = calendar.getTime();
            this.filterDateEnd = endDate;
            this.filterDateStart = startDate;
            wasLeaseEdited = false;
            wasIncomeEdited = false;
            wasExpenseEdited = false;
            wasApartmentEdited = false;
            dateRangeStartBtn.setText(DateAndCurrencyDisplayer.getDateToDisplay(dateFormatCode, filterDateStart));
            dateRangeEndBtn.setText(DateAndCurrencyDisplayer.getDateToDisplay(dateFormatCode, filterDateEnd));
            cameraImageFilePath = "";
        }
        //ApartmentViewFrag3 ap2 = (ApartmentViewFrag3) frag2;
        //ap2.updateDates();
        //((ApartmentViewFrag3) frag2).updateDates();
        // adapter.addFragment(new FragmentThree(), "FRAG3");
        viewPager.setAdapter(adapter);
        viewModel.setMoneyArray(databaseHandler.getIncomeAndExpensesByApartmentIDWithinDates(MainActivity.user, apartment.getId(), filterDateStart, filterDateEnd));
        viewModel.setLeaseArray(databaseHandler.getUsersLeasesForApartment(MainActivity.user, apartment.getId()));

        //this.currentFilteredExpenses = db.getUsersExpensesWithinDates(MainActivity.user, startDate, endDate );

        mPageChangeListener = new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrollStateChanged(int arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onPageSelected(int pos) {
                if (pos == 0 || pos == 2) {
                    dateSelectorLL.setVisibility(View.GONE);
                } else {
                    dateSelectorLL.setVisibility(View.VISIBLE);
                }
            }

        };
        viewPager.addOnPageChangeListener(mPageChangeListener);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        setupBasicToolbar();
        datePickerDialogLauncher = new CustomDatePickerDialogLauncher(filterDateStart, filterDateEnd, true, this);
        datePickerDialogLauncher.setDateSelectedListener(new CustomDatePickerDialogLauncher.DateSelectedListener() {
            @Override
            public void onStartDateSelected(Date startDate, Date endDate) {
                filterDateStart = startDate;
                filterDateEnd = endDate;
                viewModel.setMoneyArray(databaseHandler.getIncomeAndExpensesByApartmentIDWithinDates(MainActivity.user, apartment.getId(), filterDateStart, filterDateEnd));
                int dateFormatCode = preferences.getInt("dateFormat", DateAndCurrencyDisplayer.DATE_MMDDYYYY);
                dateRangeEndBtn.setText(DateAndCurrencyDisplayer.getDateToDisplay(dateFormatCode, filterDateEnd));
                dateRangeStartBtn.setText(DateAndCurrencyDisplayer.getDateToDisplay(dateFormatCode, filterDateStart));
                updateFragmentDates();
            }

            @Override
            public void onEndDateSelected(Date startDate, Date endDate) {
                filterDateStart = startDate;
                filterDateEnd = endDate;
                viewModel.setMoneyArray(databaseHandler.getIncomeAndExpensesByApartmentIDWithinDates(MainActivity.user, apartment.getId(), filterDateStart, filterDateEnd));
                int dateFormatCode = preferences.getInt("dateFormat", DateAndCurrencyDisplayer.DATE_MMDDYYYY);
                dateRangeEndBtn.setText(DateAndCurrencyDisplayer.getDateToDisplay(dateFormatCode, filterDateEnd));
                dateRangeStartBtn.setText(DateAndCurrencyDisplayer.getDateToDisplay(dateFormatCode, filterDateStart));
                updateFragmentDates();
            }

            @Override
            public void onDateSelected(Date date) {

            }
        });
        this.setTitle(R.string.apartment_view);
        if (wasLeaseEdited || wasIncomeEdited || wasExpenseEdited || wasApartmentEdited) {
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
        if (wasLeaseEdited || wasIncomeEdited || wasExpenseEdited || wasApartmentEdited) {
            setResultToEdited();
        } else {
            setResult(RESULT_OK);
        }
    }

    private void setResultToEdited() {
        Intent intent = new Intent();
        intent.putExtra("was_lease_edited", wasLeaseEdited);
        intent.putExtra("was_income_edited", wasIncomeEdited);
        intent.putExtra("was_expense_edited", wasExpenseEdited);
        intent.putExtra("was_apartment_edited", wasApartmentEdited);
        setResult(MainActivity.RESULT_DATA_WAS_MODIFIED, intent);
    }

    @Override
    //Handle option menu actions
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.editApartment:
                Intent intent = new Intent(this, NewApartmentWizard.class);
                intent.putExtra("apartmentToEdit", apartment);
                wasApartmentEdited = true;
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
                if (adapter.getCount() > 9) {
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
                        int requestCode = 0;
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
                        int requestCode = 0;
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
        dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: " + requestCode);
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
                cameraImageFilePath = photoFile.getAbsolutePath();
                if (frag1 != null) {
                    frag1.updateCameraUri(cameraImageFilePath);
                }
                Uri photoUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".helpers.fileprovider", photoFile);
                pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                this.startActivityForResult(pictureIntent, MainActivity.REQUEST_CAMERA_FOR_MAIN_PIC);
            } else {
                Toast.makeText(this, R.string.permission_picture_denied, Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == MainActivity.REQUEST_CAMERA_FOR_OTHER_PICS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File photoFile = AppFileManagementHelper.createImageFileFromCamera();
                cameraImageFilePath = photoFile.getAbsolutePath();
                if (frag1 != null) {
                    frag1.updateCameraUri(cameraImageFilePath);
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
                if (apartment.getMainPic() != null) {
                    if (!apartment.getMainPic().equals("")) {
                        new File(apartment.getMainPic()).delete();
                    }
                }
                if (!apartment.getOtherPics().isEmpty()) {
                    for (int z = 0; z < apartment.getOtherPics().size(); z++) {
                        new File(apartment.getOtherPics().get(z)).delete();
                    }
                }
                databaseHandler.setApartmentInactive(apartment);
                wasApartmentEdited = true;
                setResultToEdited();
                ApartmentViewActivity.this.finish();

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
        //Uses apartment form to edit data
        if (requestCode == MainActivity.REQUEST_NEW_APARTMENT_FORM) {
            //If successful(not cancelled, passed validation)
            if (resultCode == RESULT_OK) {
                //Re-query cached apartment array to update cache and refresh current textViews to display new data. Re-query to sort list

                //int apartmentID = data.getIntExtra("editedApartmentID", 0);
                this.apartment = databaseHandler.getApartmentByID(apartment.getId(), MainActivity.user);
                viewModel.setApartment(apartment);
                //fillTextViews();
                //ApartmentListFragment.apartmentListAdapterNeedsRefreshed = true;
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
                if (this.apartment.getMainPic() != null) {
                    oldPic = this.apartment.getMainPic();
                }
                File copiedFile = AppFileManagementHelper.copyPictureFileToApp(filePath, oldPic);
                if (copiedFile != null) {
                    apartment.setMainPic(copiedFile.getAbsolutePath());
                    databaseHandler.changeApartmentMainPic(this.apartment);
                    viewModel.setApartment(apartment);
                } else {
                    Toast.makeText(ApartmentViewActivity.this, R.string.failed_to_save_image, Toast.LENGTH_LONG).show();
                }
                if (frag1 != null) {
                    if(frag1.isAdded()) {
                        frag1.updateMainPicIV(apartment.getMainPic());
                    }
                }
                wasApartmentEdited = true;
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
                // apartment.setMainPic(copiedFile.getAbsolutePath());
                //}
                if (copiedFile != null) {
                    this.apartment.addOtherPic(copiedFile.getAbsolutePath());
                    viewModel.setApartment(apartment);
                    databaseHandler.addApartmentOtherPic(apartment, copiedFile.getAbsolutePath(), MainActivity.user);
                } else {
                    Toast.makeText(ApartmentViewActivity.this, R.string.failed_to_save_image, Toast.LENGTH_LONG).show();
                }
                if (frag1 != null) {
                    frag1.hideOtherPicsRecyclerViewIfEmpty();
                    frag1.refreshPictureAdapter();
                }
                adapter.notifyDataSetChanged();
                wasApartmentEdited = true;
                setResultToEdited();
            }
        } else if (requestCode == MainActivity.REQUEST_CAMERA_FOR_MAIN_PIC) {
            if (resultCode == RESULT_OK) {
                if (this.apartment.getMainPic() != null) {
                    String oldPicPath = this.apartment.getMainPic();
                    File oldPic = new File(oldPicPath);
                    if (oldPic.exists()) {
                        oldPic.delete();
                    }
                }
                apartment.setMainPic(cameraImageFilePath);
                databaseHandler.changeApartmentMainPic(apartment);
                wasApartmentEdited = true;
                setResultToEdited();
                adapter.notifyDataSetChanged();
            }

        } else if (requestCode == MainActivity.REQUEST_CAMERA_FOR_OTHER_PICS) {
            if (resultCode == RESULT_OK) {
                if (this.apartment.getMainPic() != null) {
                    String oldPicPath = this.apartment.getMainPic();
                    File oldPic = new File(oldPicPath);
                    if (oldPic.exists()) {
                        oldPic.delete();
                    }
                }
                this.apartment.addOtherPic(cameraImageFilePath);
                databaseHandler.addApartmentOtherPic(apartment, cameraImageFilePath, MainActivity.user);
                //if (frag1 != null) {
                //    frag1.hideOtherPicsRecyclerViewIfEmpty();
                //    frag1.refreshPictureAdapter();
                //}
                adapter.notifyDataSetChanged();
                wasApartmentEdited = true;
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
        if (filterDateStart != null) {
            outState.putString("filterDateStart", formatter.format(filterDateStart));
        }
        if (filterDateEnd != null) {
            outState.putString("filterDateEnd", formatter.format(filterDateEnd));
        }
        outState.putBoolean("was_lease_edited", wasLeaseEdited);
        outState.putBoolean("was_income_edited", wasIncomeEdited);
        outState.putBoolean("was_expense_edited", wasExpenseEdited);
        outState.putBoolean("was_apartment_edited", wasApartmentEdited);
        outState.putString("camera_image_file_path", cameraImageFilePath);
        //getSupportFragmentManager().putFragment(outState, "frag1",  (adapter.getItem(0)));
        //getSupportFragmentManager().putFragment(outState, "frag2",  (adapter.getItem(1)));
        //getSupportFragmentManager().putFragment(outState, "frag3",  (adapter.getItem(2)));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.moneyListDateRangeStartBtn:
                datePickerDialogLauncher.launchStartDatePickerDialog();
                break;

            case R.id.moneyListDateRangeEndBtn:
                datePickerDialogLauncher.launchEndDatePickerDialog();
                break;

            default:
                break;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        datePickerDialogLauncher.dismissDatePickerDialog();
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    private void updateFragmentDates() {
        if (frag2 != null) {
            frag2.updateData();
        }
        if (frag3 != null) {
            frag3.updateData();
        }
    }

    @Override
    public void onLeaseDataChanged() {
        this.apartment = databaseHandler.getApartmentByID(apartment.getId(), MainActivity.user);
        viewModel.setApartment(apartment);
        //viewModel.setLease(null); //TODO update lease
        viewModel.setLeaseArray(databaseHandler.getUsersLeasesForApartment(MainActivity.user, apartment.getId()));
        viewModel.setMoneyArray(databaseHandler.getIncomeAndExpensesByApartmentIDWithinDates(MainActivity.user, apartment.getId(), filterDateStart, filterDateEnd));
        frag2.updateData();
        frag3.updateData();
        wasLeaseEdited = true;
        setResultToEdited();
    }

    @Override
    public void onLeasePaymentsChanged() {
        wasExpenseEdited = true;
        wasIncomeEdited = true;
        setResultToEdited();
    }

    @Override
    public void onMoneyDataChanged() {
        viewModel.setMoneyArray(databaseHandler.getIncomeAndExpensesByApartmentIDWithinDates(MainActivity.user, apartment.getId(), filterDateStart, filterDateEnd));
        frag2.updateData();
    }

    @Override
    public void onIncomeDataChanged() {
        wasIncomeEdited = true;
        setResultToEdited();
    }

    @Override
    public void onExpenseDataChanged() {
        wasExpenseEdited = true;
        setResultToEdited();
    }

    @Override
    public void onPicDataChanged() {
        wasApartmentEdited = true;
        setResultToEdited();
    }

    public void showEditNotesDialog() {
        final EditText editText = new EditText(ApartmentViewActivity.this);
        int maxLength = 500;
        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
        editText.setSingleLine(false);
        editText.setImeOptions(EditorInfo.IME_FLAG_NO_ENTER_ACTION);
        editText.setText(apartment.getNotes());
        editText.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        editText.setSelection(editText.getText().length());
        //editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);

        // create the AlertDialog as final
        dialog = new AlertDialog.Builder(ApartmentViewActivity.this)
                //.setMessage(R.string.comfirm_pass_to_delete_account_message)
                .setTitle(R.string.edit_notes)
                .setView(editText)

                // Set the action buttons
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String input = editText.getText().toString();
                        apartment.setNotes(input);
                        databaseHandler.editApartment(apartment, MainActivity.user.getId());
                        wasApartmentEdited = true;
                        setResultToEdited();
                        viewModel.setApartment(apartment);
                        if (frag1 != null) {
                            frag1.updateApartmentData(apartment);
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

        dialog.show();
    }

    // Adapter for the viewpager using FragmentPagerAdapter
    class ViewPagerAdapter extends FragmentStatePagerAdapter {

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            Bundle bundle = new Bundle();
            bundle.putParcelable("apartment", apartment);
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
                    frag1 = (ApartmentViewFrag1) createdFragment;
                    break;
                case 1:
                    frag2 = (ApartmentViewFrag2) createdFragment;
                    break;
                case 2:
                    frag3 = (ApartmentViewFrag3) createdFragment;
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


