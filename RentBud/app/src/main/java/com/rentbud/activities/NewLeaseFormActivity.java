package com.rentbud.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.cody.rentbud.R;
import com.rentbud.helpers.MainArrayDataMethods;
import com.rentbud.helpers.TenantOrApartmentChooserDialog;
import com.rentbud.helpers.UserInputValidation;
import com.rentbud.model.Apartment;
import com.rentbud.model.Tenant;
import com.rentbud.sqlite.DatabaseHandler;

import java.text.DateFormat;
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
    EditText depositET, rentCostET;
    Button addSecondaryTenantBtn, removeSecondaryTenantBtn, saveBtn, cancelBtn;
    Spinner rentDueDateSpinner;
    private DatePickerDialog.OnDateSetListener dateSetLeaseStartListener, dateSetLeaseEndListener;
    DatabaseHandler db;
    private Date leaseStartDate, leaseEndDate;
    Tenant primaryTenant;
    Apartment apartment;
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
                DateFormat formatFrom = new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy", Locale.US);
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
                DateFormat formatFrom = new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy", Locale.US);
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
        } else {
            Bundle extras = getIntent().getExtras();
            //Existing lease, launched by TenantView
            if (extras.get("existingLeaseTenant") != null) {
                Tenant passedTenant = extras.getParcelable("existingLeaseTenant");
                setUpForExistingLeaseWithTenant(passedTenant);
                setPrimaryTenantTextView();
                setSecondaryTenantsTV();
                setApartmentTextView();
                setLeaseStartTextView(primaryTenant);
                setLeaseEndTextView(primaryTenant);
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
                    setLeaseStartTextView(primaryTenant);
                    setLeaseEndTextView(primaryTenant);
                } else {
                    //New lease
                    setUpForNewLeaseWithPassedApartment(passedApartment);
                    setApartmentTextView();
                }
            }
            //Launched with no passed data
            else {
                getAvailableTenants();
                getAvailableApartments();
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
        this.rentCostET = findViewById(R.id.editLeaseRentCostET);
        this.changeLeaseStartTV = findViewById(R.id.editLeaseChangeLeaseStartTV);
        this.changeLeaseStartTV.setOnClickListener(this);

        this.changeLeaseEndTV = findViewById(R.id.editLeaseChangeLeaseEndTV);
        this.changeLeaseEndTV.setOnClickListener(this);

        this.primaryTenantTV = findViewById(R.id.editLeaseChangePrimaryTenantTV);
        this.primaryTenantTV.setOnClickListener(this);

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
                    public void finish(Tenant tenantResult, Apartment apartmentResult) {
                        if(apartment != null) {
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
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dialog = new DatePickerDialog(NewLeaseFormActivity.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, dateSetLeaseStartListener, year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
                break;

            case R.id.editLeaseChangeLeaseEndTV:
                Calendar cal2 = Calendar.getInstance();
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
                    public void finish(Tenant tenantResult, Apartment apartmentResult) {
                        if(primaryTenant != null){
                            availableTenants.add(primaryTenant);
                        }
                        availableTenants.remove(apartmentResult);
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
                    public void finish(Tenant tenantResult, Apartment apartmentResult) {
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
                        public void finish(Tenant tenantResult, Apartment apartmentResult) {
                            secondaryTenants.remove(tenantResult);
                            availableTenants.add(tenantResult);
                            setSecondaryTenantsTV();
                        }
                    });
                }
                break;

            case R.id.leaseFormSaveBtn:
                if (apartment != null && primaryTenant != null && leaseStartDate != null && leaseEndDate != null) {
                    if(validation.isFirstDateBeforeSecondDate(leaseStartDate, leaseEndDate, changeLeaseStartTV, "Lease start should be before end")) {
                        primaryTenant.setApartmentID(apartment.getId());
                        primaryTenant.setIsPrimary(true);
                        primaryTenant.setLeaseStart(leaseStartDate.toString());
                        primaryTenant.setLeaseEnd(leaseEndDate.toString());
                        //primaryTenant.setPaymentDay();
                        for (int i = 0; i < secondaryTenants.size(); i++) {
                            secondaryTenants.get(i).setApartmentID(apartment.getId());
                            secondaryTenants.get(i).setLeaseStart(leaseStartDate.toString());
                            secondaryTenants.get(i).setLeaseEnd(leaseEndDate.toString());
                        }
                        db.createNewLease(apartment, primaryTenant, secondaryTenants);
                        Intent data = new Intent();
                        apartment.setRented(true);
                        data.putExtra("updatedApartmentID", apartment.getId());
                        data.putExtra("updatedPrimaryTenantID", primaryTenant.getId());
                        //data.putParcelableArrayListExtra("updatedSecondaryTenants", secondaryTenants);

                        dataMethods.sortMainApartmentArray();
                        dataMethods.sortMainTenantArray();
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
            if (MainActivity.tenantList.get(i).getApartmentID() == 0) {
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
        int apartmentID = passedTenant.getApartmentID();
        for (int i = 0; i < MainActivity.tenantList.size(); i++) {
            //If Tenant isn't currently renting, add to available tenants list
            if (MainActivity.tenantList.get(i).getApartmentID() == 0) {
                availableTenants.add(MainActivity.tenantList.get(i));
                //If Tenant is currently renting selected apartment
            } else if (MainActivity.tenantList.get(i).getApartmentID() == apartmentID) {
                //and is Primary, set primary tenant
                if (MainActivity.tenantList.get(i).getIsPrimary()) {
                    this.primaryTenant = MainActivity.tenantList.get(i);
                    //and is secondary, add to secondary tenant list
                } else {
                    secondaryTenants.add(MainActivity.tenantList.get(i));
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
    }

    public void setUpForNewLeaseWithPassedSecondaryTenant(Tenant passedTenant) {
        Tenant secondaryTenant = null;
        for (int i = 0; i < MainActivity.tenantList.size(); i++) {
            if (MainActivity.tenantList.get(i).getId() == passedTenant.getId()) {
                secondaryTenant = MainActivity.tenantList.get(i);
            } else if (MainActivity.tenantList.get(i).getApartmentID() == 0) {
                availableTenants.add(MainActivity.tenantList.get(i));
            }
        }
        if (secondaryTenant != null) {
            secondaryTenants.add(secondaryTenant);
        }
        apartment = null;
        this.primaryTenant = null;
        this.leaseStartDate = null;
        this.leaseEndDate = null;
        getAvailableApartments();
    }

    private void setUpForNewLeaseWithPassedPrimaryTenant(Tenant passedTenant) {
        Tenant primaryTenant = null;
        for (int i = 0; i < MainActivity.tenantList.size(); i++) {
            if (MainActivity.tenantList.get(i).getId() == passedTenant.getId()) {
                primaryTenant = MainActivity.tenantList.get(i);
            } else if (MainActivity.tenantList.get(i).getApartmentID() == 0) {
                availableTenants.add(MainActivity.tenantList.get(i));
            }
        }
        if (primaryTenant != null) {
            this.primaryTenant = primaryTenant;
        }
        apartment = null;
        this.leaseStartDate = null;
        this.leaseEndDate = null;
        getAvailableApartments();
    }

    private void setUpForExistingLeaseWithApartment(Apartment passedApartment) {
        int apartmentID = passedApartment.getId();
        for (int i = 0; i < MainActivity.tenantList.size(); i++) {
            //If Tenant isn't currently renting, add to available tenants list
            if (MainActivity.tenantList.get(i).getApartmentID() == 0) {
                availableTenants.add(MainActivity.tenantList.get(i));
                //If Tenant is currently renting selected apartment
            } else if (MainActivity.tenantList.get(i).getApartmentID() == apartmentID) {
                //and is Primary, set primary tenant
                if (MainActivity.tenantList.get(i).getIsPrimary()) {
                    this.primaryTenant = MainActivity.tenantList.get(i);
                    //and is secondary, add to secondary tenant list
                } else {
                    secondaryTenants.add(MainActivity.tenantList.get(i));
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

    public void setPrimaryTenantTextView() {
        primaryTenantTV.setText(primaryTenant.getFirstName());
        primaryTenantTV.append(" ");
        primaryTenantTV.append(primaryTenant.getLastName());
    }

    public void setApartmentTextView() {
        changeApartmentTV.setText(apartment.getStreet1());
        changeApartmentTV.append(" ");
        changeApartmentTV.append(apartment.getStreet2());
    }

    public void setLeaseStartTextView(Tenant tenant) {
        SimpleDateFormat formatTo = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
        DateFormat formatFrom = new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy", Locale.US);
        try {
            Date startDate = formatFrom.parse(tenant.getLeaseStart());
            leaseStartDate = startDate;
            changeLeaseStartTV.setText(formatTo.format(startDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void setLeaseEndTextView(Tenant tenant) {
        SimpleDateFormat formatTo = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
        DateFormat formatFrom = new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy", Locale.US);
        try {
            Date endDate = formatFrom.parse(tenant.getLeaseEnd());
            leaseEndDate = endDate;
            changeLeaseEndTV.setText(formatTo.format(endDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
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
        if (leaseStartDate != null) {
            outState.putString("leaseStartDate", leaseStartDate.toString());
        }
        if (leaseEndDate != null) {
            outState.putString("leaseEndDate", leaseEndDate.toString());
        }
        if (availableTenants != null) {
            outState.putParcelableArrayList("availableTenants", availableTenants);
        }
        if (availableApartments != null) {
            outState.putParcelableArrayList("availableApartments", availableApartments);
        }
    }
}
