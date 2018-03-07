package com.rentbud.activities;

import android.app.DatePickerDialog;
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
import com.rentbud.helpers.TenantOrApartmentChooserDialog;
import com.rentbud.model.Apartment;
import com.rentbud.model.Tenant;
import com.rentbud.sqlite.DatabaseHandler;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Cody on 2/27/2018.
 */

public class NewLeaseFormActivity extends BaseActivity implements View.OnClickListener {
    TextView changeLeaseStartTV, changeLeaseEndTV, primaryTenantTV, secondaryTenantsTV, apartmentTV, changeApartmentTV;
    EditText depositET, rentCostET;
    Button   addSecondaryTenantBtn, removeSecondaryTenantBtn, saveBtn, cancelBtn;
    Spinner rentDueDateSpinner;
    private DatePickerDialog.OnDateSetListener dateSetLeaseStartListener, dateSetLeaseEndListener;
    DatabaseHandler db;
    private Date leaseStartDate, leaseEndDate;
    Tenant tenant;
    Apartment apartment;
    ArrayList<Tenant> availableTenants;
    ArrayList<Apartment> availableApartments;
    ArrayList<Tenant> secondaryTenants;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupUserAppTheme(MainActivity.curThemeChoice);
        setContentView(R.layout.activity_edit_lease);
        setupBasicToolbar();
        initializeVariables();
        getAvailableTenants();
        getAvailableApartments();
        setUpdateSelectedDateListeners();
    }

    private void initializeVariables() {
        this.secondaryTenants = new ArrayList<>();
        this.availableTenants = new ArrayList<>();
        this.availableApartments = new ArrayList<>();
        this.db = new DatabaseHandler(this);


        this.apartmentTV = findViewById(R.id.editLeaseChangeApartmentTV);
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
                SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy");
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
                SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy");
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
                        apartment = apartmentResult;
                        changeApartmentTV.setText(apartment.getStreet1());
                        changeApartmentTV.append(" ");
                        changeApartmentTV.append(apartment.getStreet2());
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
                        tenant = tenantResult;
                        primaryTenantTV.setText(tenant.getFirstName());
                        primaryTenantTV.append(" ");
                        primaryTenantTV.append(tenant.getLastName());
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
                            setSecondaryTenantsTV();
                        }
                    });
                }
                break;

            case R.id.leaseFormSaveBtn:
                if(apartment != null && tenant != null && leaseStartDate != null && leaseEndDate != null){
                    tenant.setApartmentID(apartment.getId());
                    tenant.setIsPrimary(true);
                    tenant.setLeaseStart(leaseStartDate.toString());
                    tenant.setLeaseEnd(leaseEndDate.toString());
                    //tenant.setPaymentDay();
                    for(int i = 0; i < secondaryTenants.size(); i++){
                        secondaryTenants.get(i).setApartmentID(apartment.getId());
                        secondaryTenants.get(i).setLeaseStart(leaseStartDate.toString());
                        secondaryTenants.get(i).setLeaseEnd(leaseEndDate.toString());
                    }
                    db.createNewLease(apartment, tenant, secondaryTenants);
                    finish();
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
                Log.d("TAG", "getAvailableTenants: ADDED");
                availableTenants.add(MainActivity.tenantList.get(i));
            }
        }
    }

    private void getAvailableApartments() {
        for (int i = 0; i < MainActivity.apartmentList.size(); i++) {
            //TODO
            availableApartments.add(MainActivity.apartmentList.get(i));
        }
    }

    private ArrayList<Tenant> getTenantListWithSelectedExcluded(){
        ArrayList<Tenant> tenants = new ArrayList<>();
        tenants.addAll(availableTenants);
        if(tenant != null){
            tenants.remove(tenant);
        }
        if(!secondaryTenants.isEmpty()){
           tenants.removeAll(secondaryTenants);
        }
        return tenants;
    }
}
