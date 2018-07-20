package com.rentbud.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.cody.rentbud.R;
import com.rentbud.fragments.ApartmentListFragment;
import com.rentbud.fragments.LeaseListFragment;
import com.rentbud.fragments.TenantListFragment;
import com.rentbud.helpers.MainArrayDataMethods;
import com.rentbud.helpers.TenantOrApartmentChooserDialog;
import com.rentbud.helpers.UserInputValidation;
import com.rentbud.model.Apartment;
import com.rentbud.model.Lease;
import com.rentbud.model.Tenant;
import com.rentbud.sqlite.DatabaseHandler;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Cody on 2/27/2018.
 */

public class NewLeaseFormActivity extends BaseActivity implements View.OnClickListener {
    TextView changeLeaseStartTV, changeLeaseEndTV, primaryTenantTV, secondaryTenantsTV, changeApartmentTV;
    EditText depositET, rentCostET, depositWithheldET;
    LinearLayout depositWithheldLL;
    Button addSecondaryTenantBtn, removeSecondaryTenantBtn, saveBtn, cancelBtn;
    Spinner rentDueDateSpinner;
    private DatePickerDialog.OnDateSetListener dateSetLeaseStartListener, dateSetLeaseEndListener;
    DatabaseHandler db;
    private Date leaseStartDate, leaseEndDate;
    Lease currentLease;
    Tenant primaryTenant;
    Apartment apartment;
    Boolean isCurrent;
    Boolean isEdit;
    BigDecimal rentCost, deposit, depositWithheld;
    ArrayList<Tenant> availableTenants;
    ArrayList<Apartment> availableApartments;
    ArrayList<Tenant> secondaryTenants;
    MainArrayDataMethods dataMethods;
    UserInputValidation validation;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupUserAppTheme(MainActivity.curThemeChoice);
        setContentView(R.layout.activity_edit_lease);
        setupBasicToolbar();
        initializeVariables();
        setUpdateSelectedDateListeners();
        if (savedInstanceState != null) {
            if (savedInstanceState.getParcelable("apartment") != null) {
                this.apartment = savedInstanceState.getParcelable("apartment");
                setApartmentTextView();
            }
            if (savedInstanceState.getParcelable("primaryTenant") != null) {
                this.primaryTenant = savedInstanceState.getParcelable("primaryTenant");
                setPrimaryTenantTextView();
            }
            if (savedInstanceState.getParcelableArrayList("secondaryTenants") != null) {
                this.secondaryTenants = savedInstanceState.getParcelableArrayList("secondaryTenants");
                setSecondaryTenantsTV();
            }
            if (savedInstanceState.getString("leaseStartDate") != null) {
                SimpleDateFormat formatTo = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
                DateFormat formatFrom = new SimpleDateFormat("yyyy/MM/dd", Locale.US);
                try {
                    Date startDate = formatFrom.parse(savedInstanceState.getString("leaseStartDate"));
                    leaseStartDate = startDate;
                    changeLeaseStartTV.setText(formatTo.format(startDate));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            if (savedInstanceState.getString("leaseEndDate") != null) {
                SimpleDateFormat formatTo = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
                DateFormat formatFrom = new SimpleDateFormat("yyyy/MM/dd", Locale.US);
                try {
                    Date endDate = formatFrom.parse(savedInstanceState.getString("leaseEndDate"));
                    leaseEndDate = endDate;
                    changeLeaseEndTV.setText(formatTo.format(endDate));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            if (savedInstanceState.getParcelableArrayList("availableTenants") != null) {
                this.availableTenants = savedInstanceState.getParcelableArrayList("availableTenants");
            }
            if (savedInstanceState.getParcelableArrayList("availableApartments") != null) {
                this.availableApartments = savedInstanceState.getParcelableArrayList("availableApartments");
            }
            if (savedInstanceState.getParcelable("currentLease") != null) {
                this.currentLease = savedInstanceState.getParcelable("currentLease");
            }
            if (savedInstanceState.getString("depositString") != null) {
                String depositString = savedInstanceState.getString("depositString");
                this.deposit = new BigDecimal(depositString);
            }
            if (savedInstanceState.getString("depositWithheldString") != null) {
                String depositWithheldString = savedInstanceState.getString("depositWithheldString");
                this.depositWithheld = new BigDecimal(depositWithheldString);
            }
            if (savedInstanceState.getString("rentCostString") != null) {
                String rentCostString = savedInstanceState.getString("rentCostString");
                this.rentCost = new BigDecimal(rentCostString);
            }
            this.isEdit = savedInstanceState.getBoolean("isEdit");
            this.isCurrent = savedInstanceState.getBoolean("isCurrent");
            if (isEdit) {
                enableDepositWithheldET();
            }
        } else {
            Bundle extras = getIntent().getExtras();
            //Existing lease, launched by TenantView
            if (extras.get("existingLeaseTenant") != null) {
                Tenant passedTenant = extras.getParcelable("existingLeaseTenant");
                setUpForExistingLeaseWithTenant(passedTenant);
                setPrimaryTenantTextView();
                setSecondaryTenantsTV();
                setApartmentTextView();
                setLeaseStartTextView(currentLease.getLeaseStart());
                setLeaseEndTextView(currentLease.getLeaseEnd());
                setRentCostET();
                setDepositET();
                enableDepositWithheldET();
                setDepositWithheldET();
                isEdit = true;
            }
            //New lease, launched by TenantView, user wants passed Tenant to be secondary tenant
            else if (extras.get("secondaryTenant") != null) {
                Tenant passedTenant = extras.getParcelable("secondaryTenant");
                setUpForNewLeaseWithPassedSecondaryTenant(passedTenant);
                setSecondaryTenantsTV();
            }
            //New lease, launched by TenantView, user wants passed Tenant to be primary tenant
            else if (extras.get("primaryTenant") != null) {
                Tenant passedTenant = extras.getParcelable("primaryTenant");
                setUpForNewLeaseWithPassedPrimaryTenant(passedTenant);
                setPrimaryTenantTextView();
            }
            //Launched by ApartmentView
            else if (extras.get("apartment") != null) {
                Apartment passedApartment = extras.getParcelable("apartment");
                if (passedApartment.isRented()) {
                    //Existing lease
                    setUpForExistingLeaseWithApartment(passedApartment);
                    setPrimaryTenantTextView();
                    setSecondaryTenantsTV();
                    setApartmentTextView();
                    setLeaseStartTextView(currentLease.getLeaseStart());
                    setLeaseEndTextView(currentLease.getLeaseEnd());
                    setRentCostET();
                    setDepositET();
                    enableDepositWithheldET();
                    setDepositWithheldET();
                    isEdit = true;
                } else {
                    //New lease
                    setUpForNewLeaseWithPassedApartment(passedApartment);
                    setApartmentTextView();
                }
            }
            //Launched by main, used to add lease that is already completed to history(Can pick from ANY Apartment/Tenant)
            else if (extras.get("isLeaseForHistory") != null) {
                Boolean isLeaseForHistory = extras.getBoolean("isLeaseForHistory");
                if (isLeaseForHistory) {
                    this.isCurrent = false;
                    setUpForNewCompletedLease();
                    enableDepositWithheldET();
                } else {
                    getAvailableTenants();
                    getAvailableApartments();
                    this.rentCost = new BigDecimal(0);
                    this.deposit = new BigDecimal(0);
                    this.depositWithheld = new BigDecimal(0);
                }
                //Launched by lease view edit
            } else if (extras.get("leaseToEdit") != null) {
                this.isEdit = true;
                this.currentLease = extras.getParcelable("leaseToEdit");
                setUpForEditLease();
                setPrimaryTenantTextView();
                setSecondaryTenantsTV();
                setApartmentTextView();
                setLeaseStartTextView(currentLease.getLeaseStart());
                setLeaseEndTextView(currentLease.getLeaseEnd());
                setRentCostET();
                setDepositET();
                enableDepositWithheldET();
                setDepositWithheldET();
            }
            //Launched with no passed data
            else {
                getAvailableTenants();
                getAvailableApartments();
                this.rentCost = new BigDecimal(0);
                this.deposit = new BigDecimal(0);
                this.depositWithheld = new BigDecimal(0);
            }
        }

    }

    private void initializeVariables() {
        this.secondaryTenants = new ArrayList<>();
        this.availableTenants = new ArrayList<>();
        this.availableApartments = new ArrayList<>();
        this.db = new DatabaseHandler(this);
        this.dataMethods = new MainArrayDataMethods();
        this.validation = new UserInputValidation(this);

        this.secondaryTenantsTV = findViewById(R.id.editLeaseSecondaryTenantsListTV);
        this.depositET = findViewById(R.id.editLeaseDepositET);
        depositET.setSelection(depositET.getText().length());
        this.depositWithheldET = findViewById(R.id.leaseFormDepositWithheldET);
        depositWithheldET.setSelection(depositWithheldET.getText().length());
        this.rentCostET = findViewById(R.id.editLeaseRentCostET);
        rentCostET.setSelection(rentCostET.getText().length());
        this.changeLeaseStartTV = findViewById(R.id.editLeaseChangeLeaseStartTV);
        this.changeLeaseStartTV.setOnClickListener(this);

        this.changeLeaseEndTV = findViewById(R.id.editLeaseChangeLeaseEndTV);
        this.changeLeaseEndTV.setOnClickListener(this);

        this.primaryTenantTV = findViewById(R.id.editLeaseChangePrimaryTenantTV);
        this.primaryTenantTV.setOnClickListener(this);

        this.depositWithheldLL = findViewById(R.id.leaseFormDepositWithheldLL);
        this.isCurrent = true;
        this.isEdit = false;

        this.changeApartmentTV = findViewById(R.id.editLeaseChangeApartmentTV);
        this.changeApartmentTV.setOnClickListener(this);
        this.addSecondaryTenantBtn = findViewById(R.id.editLeaseSecondaryTenantsAddBtn);
        this.addSecondaryTenantBtn.setOnClickListener(this);
        this.removeSecondaryTenantBtn = findViewById(R.id.editLeaseSecondaryTenantsRemoveBtn);
        this.removeSecondaryTenantBtn.setOnClickListener(this);
        this.saveBtn = findViewById(R.id.leaseFormSaveBtn);
        this.saveBtn.setOnClickListener(this);
        this.cancelBtn = findViewById(R.id.leaseFormCancelBtn);
        this.cancelBtn.setOnClickListener(this);
        this.rentDueDateSpinner = findViewById(R.id.editLeaseRentDueDateSpinner);

        depositET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (depositET == null) return;
                String s = editable.toString();
                if (s.isEmpty()) return;
                depositET.removeTextChangedListener(this);
                String cleanString = s.replaceAll("[$,.]", "");
                deposit = new BigDecimal(cleanString).setScale(2, BigDecimal.ROUND_FLOOR).divide(new BigDecimal(100), BigDecimal.ROUND_FLOOR);
                String formatted = NumberFormat.getCurrencyInstance().format(deposit);
                depositET.setText(formatted);
                depositET.setSelection(formatted.length());
                depositET.addTextChangedListener(this);
            }
        });

        rentCostET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (rentCostET == null) return;
                String s = editable.toString();
                if (s.isEmpty()) return;
                rentCostET.removeTextChangedListener(this);
                String cleanString = s.replaceAll("[$,.]", "");
                rentCost = new BigDecimal(cleanString).setScale(2, BigDecimal.ROUND_FLOOR).divide(new BigDecimal(100), BigDecimal.ROUND_FLOOR);
                String formatted = NumberFormat.getCurrencyInstance().format(rentCost);
                rentCostET.setText(formatted);
                rentCostET.setSelection(formatted.length());
                rentCostET.addTextChangedListener(this);
            }
        });
    }

    private void setUpdateSelectedDateListeners() {
        dateSetLeaseStartListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                //Once user selects date from date picker pop-up,
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.YEAR, year);
                cal.set(Calendar.MONTH, month);
                cal.set(Calendar.DATE, day);
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);

                leaseStartDate = cal.getTime();

                SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
                changeLeaseStartTV.setText(formatter.format(leaseStartDate));
            }
        };
        dateSetLeaseEndListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                //Once user selects date from date picker pop-up,
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.YEAR, year);
                cal.set(Calendar.MONTH, month);
                cal.set(Calendar.DATE, day);
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);

                leaseEndDate = cal.getTime();

                SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
                changeLeaseEndTV.setText(formatter.format(leaseEndDate));
            }
        };
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.editLeaseChangeApartmentTV:
                //Create the dialog
                TenantOrApartmentChooserDialog dialog4 = new TenantOrApartmentChooserDialog(NewLeaseFormActivity.this, TenantOrApartmentChooserDialog.APARTMENT_TYPE, availableApartments);
                dialog4.show();
                dialog4.setDialogResult(new TenantOrApartmentChooserDialog.OnTenantChooserDialogResult() {
                    @Override
                    public void finish(Tenant tenantResult, Apartment apartmentResult, Lease leaseResult) {
                        if (apartment != null) {
                            availableApartments.add(apartment);
                        }
                        availableApartments.remove(apartmentResult);
                        apartment = apartmentResult;
                        setApartmentTextView();
                    }
                });
                break;

            case R.id.editLeaseChangeLeaseStartTV:
                Calendar cal = Calendar.getInstance();
                if (leaseStartDate != null) {
                    cal.setTime(leaseStartDate);
                }
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dialog = new DatePickerDialog(NewLeaseFormActivity.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, dateSetLeaseStartListener, year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
                break;

            case R.id.editLeaseChangeLeaseEndTV:
                Calendar cal2 = Calendar.getInstance();
                if (leaseEndDate != null) {
                    cal2.setTime(leaseEndDate);
                }
                int year2 = cal2.get(Calendar.YEAR);
                int month2 = cal2.get(Calendar.MONTH);
                int day2 = cal2.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dialog2 = new DatePickerDialog(NewLeaseFormActivity.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, dateSetLeaseEndListener, year2, month2, day2);
                dialog2.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog2.show();
                break;

            case R.id.editLeaseChangePrimaryTenantTV:
                //Create the dialog
                ArrayList<Tenant> tenants = getTenantListWithSelectedExcluded();
                TenantOrApartmentChooserDialog dialog3 = new TenantOrApartmentChooserDialog(NewLeaseFormActivity.this, TenantOrApartmentChooserDialog.TENANT_TYPE, tenants);
                dialog3.show();
                dialog3.setDialogResult(new TenantOrApartmentChooserDialog.OnTenantChooserDialogResult() {
                    @Override
                    public void finish(Tenant tenantResult, Apartment apartmentResult, Lease leaseResult) {
                        if (primaryTenant != null) {
                            availableTenants.add(primaryTenant);
                        }
                        availableTenants.remove(tenantResult);
                        primaryTenant = tenantResult;
                        setPrimaryTenantTextView();
                    }
                });
                break;

            case R.id.editLeaseSecondaryTenantsAddBtn:
                //Create the dialog
                ArrayList<Tenant> tenants1 = getTenantListWithSelectedExcluded();
                TenantOrApartmentChooserDialog dialog5 = new TenantOrApartmentChooserDialog(NewLeaseFormActivity.this, TenantOrApartmentChooserDialog.TENANT_TYPE, tenants1);
                dialog5.show();
                dialog5.setDialogResult(new TenantOrApartmentChooserDialog.OnTenantChooserDialogResult() {
                    @Override
                    public void finish(Tenant tenantResult, Apartment apartmentResult, Lease leaseResult) {
                        secondaryTenants.add(tenantResult);
                        availableTenants.remove(tenantResult);
                        setSecondaryTenantsTV();
                    }
                });
                break;

            case R.id.editLeaseSecondaryTenantsRemoveBtn:
                if (!secondaryTenants.isEmpty()) {
                    TenantOrApartmentChooserDialog dialog6 = new TenantOrApartmentChooserDialog(NewLeaseFormActivity.this, TenantOrApartmentChooserDialog.SECONDARY_TENANT_TYPE, secondaryTenants);
                    dialog6.show();
                    dialog6.setDialogResult(new TenantOrApartmentChooserDialog.OnTenantChooserDialogResult() {
                        @Override
                        public void finish(Tenant tenantResult, Apartment apartmentResult, Lease leaseResult) {
                            secondaryTenants.remove(tenantResult);
                            availableTenants.add(tenantResult);
                            setSecondaryTenantsTV();
                        }
                    });
                }
                break;

            case R.id.leaseFormSaveBtn:
                if (apartment != null && primaryTenant != null && leaseStartDate != null && leaseEndDate != null) {
                    if (validation.isFirstDateBeforeSecondDate(leaseStartDate, leaseEndDate, changeLeaseStartTV, "Lease start should be before end")) {
                        //primaryTenant.setApartmentID(apartment.getId());
                        //primaryTenant.setIsPrimary(true);
                        //primaryTenant.setLeaseStart(leaseStartDate);
                        //primaryTenant.setLeaseEnd(leaseEndDate);
                        //primaryTenant.setPaymentDay();
                        if (isCurrent) {
                            primaryTenant.setHasLease(true);
                            apartment.setRented(true);
                        }
                        ArrayList<Integer> secondaryTenantIDs = new ArrayList<>();
                        for (int i = 0; i < secondaryTenants.size(); i++) {
                            //secondaryTenants.get(i).setApartmentID(apartment.getId());
                            //secondaryTenants.get(i).setLeaseStart(leaseStartDate);
                            //secondaryTenants.get(i).setLeaseEnd(leaseEndDate);
                            if (isCurrent) {
                                secondaryTenants.get(i).setHasLease(true);
                            }
                            secondaryTenantIDs.add(secondaryTenants.get(i).getId());
                        }
                        int paymentDay = 7;
                        //String depositWithheldString = "1500.00";
                        //BigDecimal depositWithheld = new BigDecimal(depositWithheldString);

                        Lease lease = new Lease(0, primaryTenant.getId(), secondaryTenantIDs, apartment.getId(), leaseStartDate, leaseEndDate,
                                paymentDay, rentCost, deposit, "");
                        if (isEdit) {
                            lease.setId(this.currentLease.getId());
                            db.editLease(lease);
                        } else {
                            db.addLease(lease, MainActivity.user.getId());
                        }
                        Intent data = new Intent();

                        //data.putExtra("updatedApartmentID", apartment.getId());
                        //data.putExtra("updatedPrimaryTenantID", primaryTenant.getId());
                        //data.putParcelableArrayListExtra("updatedSecondaryTenants", secondaryTenants);

                        dataMethods.sortMainApartmentArray();
                        dataMethods.sortMainTenantArray();
                        MainActivity.currentLeasesList = db.getUsersActiveLeases(MainActivity.user);
                        //MainActivity5.apartmentList = db.getUsersApartments(MainActivity5.user);
                        //ApartmentListFragment.apartmentListAdapterNeedsRefreshed = true;
                       // TenantListFragment.tenantListAdapterNeedsRefreshed = true;
                       // LeaseListFragment.leaseListAdapterNeedsRefreshed = true;
                        setResult(RESULT_OK, data);
                        finish();
                    }
                }
                break;

            case R.id.leaseFormCancelBtn:
                finish();
                break;

            default:
                break;
        }
    }

    private void setSecondaryTenantsTV() {
        secondaryTenantsTV.setText("");
        for (int i = 0; i < secondaryTenants.size(); i++) {
            secondaryTenantsTV.append(secondaryTenants.get(i).getFirstName() +
                    " " + secondaryTenants.get(i).getLastName() +
                    "\n");
        }
    }

    private void getAvailableTenants() {
        for (int i = 0; i < MainActivity.tenantList.size(); i++) {
            if (!MainActivity.tenantList.get(i).getHasLease()) {
                availableTenants.add(MainActivity.tenantList.get(i));
            }
        }
    }

    private void getAvailableApartments() {
        for (int i = 0; i < MainActivity.apartmentList.size(); i++) {
            if (!MainActivity.apartmentList.get(i).isRented()) {
                availableApartments.add(MainActivity.apartmentList.get(i));
            }
        }
    }

    private ArrayList<Tenant> getTenantListWithSelectedExcluded() {
        ArrayList<Tenant> tenants = new ArrayList<>();
        tenants.addAll(availableTenants);
        if (primaryTenant != null) {
            tenants.remove(primaryTenant);
        }
        if (!secondaryTenants.isEmpty()) {
            tenants.removeAll(secondaryTenants);
        }
        return tenants;
    }

    public void setUpForExistingLeaseWithTenant(Tenant passedTenant) {
        currentLease = dataMethods.getCachedActiveLeaseByTenantID(passedTenant.getId());
        ArrayList<Integer> secondaryTenantIDs = currentLease.getSecondaryTenantIDs();
        int apartmentID = currentLease.getApartmentID();
        for (int i = 0; i < MainActivity.tenantList.size(); i++) {
            //If Tenant isn't currently renting, add to available tenants list
            if (!MainActivity.tenantList.get(i).getHasLease()) {
                availableTenants.add(MainActivity.tenantList.get(i));
                //If Tenant is currently renting selected apartment
            } else {
                //and is Primary, set primary tenant
                if (MainActivity.tenantList.get(i).getId() == currentLease.getPrimaryTenantID()) {
                    primaryTenant = MainActivity.tenantList.get(i);
                } else {
                    for (int y = 0; y < secondaryTenantIDs.size(); y++) {
                        if (secondaryTenantIDs.get(y) == MainActivity.tenantList.get(i).getId()) {
                            secondaryTenants.add(MainActivity.tenantList.get(i));
                        }
                    }
                }
            }
        }
        for (int i = 0; i < MainActivity.apartmentList.size(); i++) {
            if (MainActivity.apartmentList.get(i).getId() == apartmentID) {
                this.apartment = MainActivity.apartmentList.get(i);
            } else if (!MainActivity.apartmentList.get(i).isRented()) {
                availableApartments.add(MainActivity.apartmentList.get(i));
            }
        }
        this.leaseStartDate = currentLease.getLeaseStart();
        this.leaseEndDate = currentLease.getLeaseEnd();
        this.rentCost = currentLease.getMonthlyRentCost();
        this.deposit = currentLease.getDeposit();
        //this.depositWithheld = currentLease.getDepositWithheld();
    }

    public void setUpForNewLeaseWithPassedSecondaryTenant(Tenant passedTenant) {
        Tenant secondaryTenant = null;
        for (int i = 0; i < MainActivity.tenantList.size(); i++) {
            if (MainActivity.tenantList.get(i).getId() == passedTenant.getId()) {
                secondaryTenant = MainActivity.tenantList.get(i);
            } else if (!MainActivity.tenantList.get(i).getHasLease()) {
                availableTenants.add(MainActivity.tenantList.get(i));
            }
        }
        if (secondaryTenant != null) {
            secondaryTenants.add(secondaryTenant);
        }
        this.apartment = null;
        this.primaryTenant = null;
        this.leaseStartDate = null;
        this.leaseEndDate = null;
        this.currentLease = null;
        this.rentCost = new BigDecimal(0);
        this.deposit = new BigDecimal(0);
        this.depositWithheld = new BigDecimal(0);
        getAvailableApartments();
    }

    private void setUpForNewLeaseWithPassedPrimaryTenant(Tenant passedTenant) {
        Tenant primaryTenant = null;
        for (int i = 0; i < MainActivity.tenantList.size(); i++) {
            if (MainActivity.tenantList.get(i).getId() == passedTenant.getId()) {
                primaryTenant = MainActivity.tenantList.get(i);
            } else if (!MainActivity.tenantList.get(i).getHasLease()) {
                availableTenants.add(MainActivity.tenantList.get(i));
            }
        }
        if (primaryTenant != null) {
            this.primaryTenant = primaryTenant;
        }
        this.apartment = null;
        this.currentLease = null;
        this.leaseStartDate = null;
        this.leaseEndDate = null;
        this.rentCost = new BigDecimal(0);
        this.deposit = new BigDecimal(0);
        this.depositWithheld = new BigDecimal(0);
        getAvailableApartments();
    }

    private void setUpForExistingLeaseWithApartment(Apartment passedApartment) {
        int apartmentID = passedApartment.getId();
        currentLease = dataMethods.getCachedActiveLeaseByApartmentID(apartmentID);
        ArrayList<Integer> secondaryTenantIDs = currentLease.getSecondaryTenantIDs();
        for (int i = 0; i < MainActivity.tenantList.size(); i++) {
            //If Tenant isn't currently renting, add to available tenants list
            if (!MainActivity.tenantList.get(i).getHasLease()) {
                availableTenants.add(MainActivity.tenantList.get(i));
                //If Tenant is currently renting selected apartment
            } else {
                //and is Primary, set primary tenant
                if (MainActivity.tenantList.get(i).getId() == currentLease.getPrimaryTenantID()) {
                    primaryTenant = MainActivity.tenantList.get(i);
                } else {
                    for (int y = 0; y < secondaryTenantIDs.size(); y++) {
                        if (secondaryTenantIDs.get(y) == MainActivity.tenantList.get(i).getId()) {
                            secondaryTenants.add(MainActivity.tenantList.get(i));
                        }
                    }
                }
            }
        }
        for (int i = 0; i < MainActivity.apartmentList.size(); i++) {
            if (MainActivity.apartmentList.get(i).getId() == apartmentID) {
                this.apartment = MainActivity.apartmentList.get(i);
            } else if (!MainActivity.apartmentList.get(i).isRented()) {
                availableApartments.add(MainActivity.apartmentList.get(i));
            }
        }
        this.leaseStartDate = currentLease.getLeaseStart();
        this.leaseEndDate = currentLease.getLeaseEnd();
        this.rentCost = currentLease.getMonthlyRentCost();
        this.deposit = currentLease.getDeposit();
        //this.depositWithheld = currentLease.getDepositWithheld();
    }

    private void setUpForNewLeaseWithPassedApartment(Apartment passedApartment) {
        int apartmentID = passedApartment.getId();
        getAvailableTenants();
        for (int i = 0; i < MainActivity.apartmentList.size(); i++) {
            if (!MainActivity.apartmentList.get(i).isRented()) {
                if (MainActivity.apartmentList.get(i).getId() == apartmentID) {
                    this.apartment = MainActivity.apartmentList.get(i);
                } else {
                    availableApartments.add(MainActivity.apartmentList.get(i));
                }
            }
        }
        this.primaryTenant = null;
        this.currentLease = null;
        this.leaseStartDate = null;
        this.leaseEndDate = null;
        this.rentCost = new BigDecimal(0);
        this.deposit = new BigDecimal(0);
        this.depositWithheld = new BigDecimal(0);
    }

    private void setUpForNewCompletedLease() {
        availableApartments.addAll(MainActivity.apartmentList);
        availableTenants.addAll(MainActivity.tenantList);
        this.primaryTenant = null;
        this.apartment = null;
        this.currentLease = null;
        this.leaseStartDate = null;
        this.leaseEndDate = null;
        this.rentCost = new BigDecimal(0);
        this.deposit = new BigDecimal(0);
        this.depositWithheld = new BigDecimal(0);
    }

    private void setUpForEditLease() {
        Date currentTime = Calendar.getInstance().getTime();
        ArrayList<Integer> secondaryTenantIDs = currentLease.getSecondaryTenantIDs();
        if (currentTime.compareTo(currentLease.getLeaseStart()) > 0 && currentTime.compareTo(currentLease.getLeaseEnd()) < 0) {
            //is current
            for (int i = 0; i < MainActivity.tenantList.size(); i++) {
                //If Tenant isn't currently renting, add to available tenants list
                if (!MainActivity.tenantList.get(i).getHasLease()) {
                    availableTenants.add(MainActivity.tenantList.get(i));
                    //If Tenant is currently renting selected apartment
                } else {
                    //and is Primary, set primary tenant
                    if (MainActivity.tenantList.get(i).getId() == currentLease.getPrimaryTenantID()) {
                        primaryTenant = MainActivity.tenantList.get(i);
                    } else {
                        for (int y = 0; y < secondaryTenantIDs.size(); y++) {
                            if (secondaryTenantIDs.get(y) == MainActivity.tenantList.get(i).getId()) {
                                secondaryTenants.add(MainActivity.tenantList.get(i));

                            }
                        }
                    }
                }
            }
            for (int i = 0; i < MainActivity.apartmentList.size(); i++) {
                if (MainActivity.apartmentList.get(i).getId() == currentLease.getApartmentID()) {
                    this.apartment = MainActivity.apartmentList.get(i);
                } else if (!MainActivity.apartmentList.get(i).isRented()) {
                    availableApartments.add(MainActivity.apartmentList.get(i));
                }
            }
        } else {
            //is finished/future
            for (int i = 0; i < MainActivity.tenantList.size(); i++) {
                if (MainActivity.tenantList.get(i).getId() == currentLease.getPrimaryTenantID()) {
                    primaryTenant = MainActivity.tenantList.get(i);
                    continue;
                }
                boolean isSecondary = false;
                for (int y = 0; y < secondaryTenantIDs.size(); y++) {
                    if (secondaryTenantIDs.get(y) == MainActivity.tenantList.get(i).getId()) {
                        secondaryTenants.add(MainActivity.tenantList.get(i));
                        isSecondary = true;
                        break;
                    }
                }
                if(!isSecondary) {
                    availableTenants.add(MainActivity.tenantList.get(i));
                }
            }
            for (int i = 0; i < MainActivity.apartmentList.size(); i++) {
                if (MainActivity.apartmentList.get(i).getId() == currentLease.getApartmentID()) {
                    this.apartment = MainActivity.apartmentList.get(i);
                } else {
                    availableApartments.add(MainActivity.apartmentList.get(i));
                }
            }
            this.isCurrent = false;
        }
        this.leaseStartDate = currentLease.getLeaseStart();
        this.leaseEndDate = currentLease.getLeaseEnd();
        this.rentCost = currentLease.getMonthlyRentCost();
        this.deposit = currentLease.getDeposit();
        //this.depositWithheld = currentLease.getDepositWithheld();
    }

    public Tenant findPassedTenant(int tenantID) {
        for (int i = 0; i < MainActivity.tenantList.size(); i++) {
            if (MainActivity.tenantList.get(i).getId() == tenantID) {
                return MainActivity.tenantList.get(i);
            }
        }
        return null;
    }

    public Apartment findPassedApartment(int apartmentID) {
        for (int i = 0; i < MainActivity.apartmentList.size(); i++) {
            if (MainActivity.tenantList.get(i).getId() == apartmentID) {
                return MainActivity.apartmentList.get(i);
            }
        }
        return null;
    }

    private void setPrimaryTenantTextView() {
        primaryTenantTV.setText(primaryTenant.getFirstName());
        primaryTenantTV.append(" ");
        primaryTenantTV.append(primaryTenant.getLastName());
    }

    private void setApartmentTextView() {
        changeApartmentTV.setText(apartment.getStreet1());
        changeApartmentTV.append(" ");
        changeApartmentTV.append(apartment.getStreet2());
    }

    private void setLeaseStartTextView(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
        changeLeaseStartTV.setText(formatter.format(date));
    }

    private void setLeaseEndTextView(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
        changeLeaseEndTV.setText(formatter.format(date));
    }

    private void setRentCostET() {
        //currentAmount = expenseToEdit.getAmount();
        //rentCostET.setText(currentLease.getMonthlyRentCost().toPlainString());
        String s = currentLease.getMonthlyRentCost().toPlainString();
        if (s.isEmpty()) return;
        //String cleanString = s.replaceAll("[$,.]", "");
        // rentCost = new BigDecimal(cleanString).setScale(2, BigDecimal.ROUND_FLOOR).divide(new BigDecimal(100), BigDecimal.ROUND_FLOOR);
        String formatted = NumberFormat.getCurrencyInstance().format(rentCost);
        rentCostET.setText(formatted);
        rentCostET.setSelection(formatted.length());
    }

    private void setDepositET() {
        //currentAmount = expenseToEdit.getAmount();
        //rentCostET.setText(currentLease.getMonthlyRentCost().toPlainString());
        String s = currentLease.getDeposit().toPlainString();
        if (s.isEmpty()) return;
        //String cleanString = s.replaceAll("[$,.]", "");
        // rentCost = new BigDecimal(cleanString).setScale(2, BigDecimal.ROUND_FLOOR).divide(new BigDecimal(100), BigDecimal.ROUND_FLOOR);
        String formatted = NumberFormat.getCurrencyInstance().format(deposit);
        depositET.setText(formatted);
        depositET.setSelection(formatted.length());
    }

    private void setDepositWithheldET() {
        //currentAmount = expenseToEdit.getAmount();
        //rentCostET.setText(currentLease.getMonthlyRentCost().toPlainString());
        //String s = currentLease.getDepositWithheld().toPlainString();
        //if (s.isEmpty()) return;
        //String cleanString = s.replaceAll("[$,.]", "");
        // rentCost = new BigDecimal(cleanString).setScale(2, BigDecimal.ROUND_FLOOR).divide(new BigDecimal(100), BigDecimal.ROUND_FLOOR);
        //String formatted = NumberFormat.getCurrencyInstance().format(depositWithheld);
        //depositWithheldET.setText(formatted);
        //depositWithheldET.setSelection(formatted.length());
    }

    private void enableDepositWithheldET() {
        depositWithheldLL.setVisibility(View.VISIBLE);
        depositWithheldET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                //if (depositWithheldET == null) return;
                //String s = editable.toString();
                //if (s.isEmpty()) return;
                //depositWithheldET.removeTextChangedListener(this);
                //String cleanString = s.replaceAll("[$,.]", "");
                //depositWithheld = new BigDecimal(cleanString).setScale(2, BigDecimal.ROUND_FLOOR).divide(new BigDecimal(100), BigDecimal.ROUND_FLOOR);
                //String formatted = NumberFormat.getCurrencyInstance().format(depositWithheld);
                //depositWithheldET.setText(formatted);
                //depositWithheldET.setSelection(formatted.length());
                //depositWithheldET.addTextChangedListener(this);
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //Save the fragment's instance
        super.onSaveInstanceState(outState);
        if (apartment != null) {
            outState.putParcelable("apartment", apartment);
        }
        if (primaryTenant != null) {
            outState.putParcelable("primaryTenant", primaryTenant);
        }
        if (secondaryTenants != null) {
            outState.putParcelableArrayList("secondaryTenants", secondaryTenants);
        }
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd", Locale.US);
        if (leaseStartDate != null) {
            outState.putString("leaseStartDate", formatter.format(leaseStartDate));
        }
        if (leaseEndDate != null) {
            outState.putString("leaseEndDate", formatter.format(leaseEndDate));
        }
        if (availableTenants != null) {
            outState.putParcelableArrayList("availableTenants", availableTenants);
        }
        if (availableApartments != null) {
            outState.putParcelableArrayList("availableApartments", availableApartments);
        }
        if (currentLease != null) {
            outState.putParcelable("currentLease", currentLease);
        }
        if (deposit != null) {
            String depositString = deposit.toPlainString();
            outState.putString("depositString", depositString);
        }
        if (depositWithheld != null) {
            String depositWithheldString = depositWithheld.toPlainString();
            outState.putString("depositWithheldString", depositWithheldString);
        }
        if (rentCost != null) {
            String rentCostString = rentCost.toPlainString();
            outState.putString("rentCostString", rentCostString);
        }
        if (isEdit != null) {
            outState.putBoolean("isEdit", isEdit);
        }
        if (isCurrent != null) {
            outState.putBoolean("isCurrent", isCurrent);
        }
    }
}
