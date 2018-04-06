package com.rentbud.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.cody.rentbud.R;
import com.rentbud.fragments.ApartmentListFragment;
import com.rentbud.fragments.TenantListFragment;
import com.rentbud.helpers.MainArrayDataMethods;
import com.rentbud.model.Apartment;
import com.rentbud.model.Tenant;
import com.rentbud.sqlite.DatabaseHandler;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Cody on 2/6/2018.
 */

public class TenantViewActivity extends BaseActivity {
    Tenant tenant;
    ArrayList<Tenant> otherTenants;
    TextView firstNameTV, lastNameTV, renterStatusTV, phoneTV, leaseStartTV, leaseEndTV, notesTV, apartmentAddressTV, apartmentAddress2TV,
            leaseHolderTypeTV, emailTV, emergencyFirstNameTV, emergencyLastNameTV, getEmergencyPhoneTV;
    Button editLeaseBtn;
    Apartment apartment;
    LinearLayout leaseLL;
    DatabaseHandler databaseHandler;
    MainArrayDataMethods dataMethods;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupUserAppTheme(MainActivity.curThemeChoice);
        setContentView(R.layout.activity_tenant_view);
        databaseHandler = new DatabaseHandler(this);
        dataMethods = new MainArrayDataMethods();
        otherTenants = new ArrayList<>();
        if (savedInstanceState != null) {
            tenant = savedInstanceState.getParcelable("tenant");
            apartment = savedInstanceState.getParcelable("apartment");
        } else {
            Bundle bundle = getIntent().getExtras();
            int tenantID = bundle.getInt("tenantID");
            int apartmentID = bundle.getInt("apartmentID");
            if (apartmentID == 0) {
                this.tenant = dataMethods.getCachedTenantByTenantID(tenantID);
                this.apartment = null;
            } else {
                Pair<Tenant, ArrayList<Tenant>> tenants = dataMethods.getCachedSelectedTenantAndRoomMatesByIDs(apartmentID, tenantID);
                this.tenant = tenants.first;
                this.otherTenants = tenants.second;
                this.apartment = dataMethods.getCachedApartmentByApartmentID(apartmentID);
            }
        }

        firstNameTV = findViewById(R.id.tenantViewFirstNameTextView);
        lastNameTV = findViewById(R.id.tenantViewLastNameTextView);
        renterStatusTV = findViewById(R.id.tenantViewRentingStatusTextView);
        phoneTV = findViewById(R.id.tenantViewPhoneTextView);
        leaseStartTV = findViewById(R.id.tenantViewLeaseStartTextView);
        leaseEndTV = findViewById(R.id.tenantViewLeaseEndTextView);
        notesTV = findViewById(R.id.tenantViewNotesTextView);
        apartmentAddressTV = findViewById(R.id.tenantViewRentingAddressTextView);
        apartmentAddress2TV = findViewById(R.id.tenantViewRentingAddress2TextView);
        leaseHolderTypeTV = findViewById(R.id.tenantViewLeaseHolderType);
        emailTV = findViewById(R.id.tenantViewEmailTextView);
        emergencyFirstNameTV = findViewById(R.id.tenantViewEmergencyFirstNameTextView);
        emergencyLastNameTV = findViewById(R.id.tenantViewEmergencyLastNameTextView);
        getEmergencyPhoneTV = findViewById(R.id.tenantViewEmergencyPhoneTextView);
        editLeaseBtn = findViewById(R.id.tenantViewEditLeaseBtn);
        leaseLL = findViewById(R.id.tenantViewLeaseLL);

        fillTextViews();
        setOnClickListeners();
        setupBasicToolbar();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Get options menu
        getMenuInflater().inflate(R.menu.tenant_view_edit_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    //Handle option menu actions
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.editTenant:
                Intent intent = new Intent(this, NewTenantFormActivity.class);
                intent.putExtra("tenantToEdit", tenant);
                startActivityForResult(intent, MainActivity.REQUEST_NEW_TENANT_FORM);
                return true;

            case R.id.deleteTenant:
                showDeleteConfirmationAlertDialog();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MainActivity.REQUEST_NEW_TENANT_FORM) {
            //If successful(not cancelled, passed validation)
            if (resultCode == RESULT_OK) {
                //Re-query cached apartment array to update cache and refresh current fragment to display new data
                int tenantID  = data.getIntExtra("editedTenantID", 0);
                this.tenant = dataMethods.getCachedTenantByTenantID(tenantID);
                fillTextViews();
                TenantListFragment.tenantListAdapterNeedsRefreshed = true;
            }
        }
        if (requestCode == MainActivity.REQUEST_NEW_LEASE_FORM) {
            if (resultCode == RESULT_OK) {
                int apartmentID = data.getIntExtra("updatedApartmentID", 0);
                int tenantID = this.tenant.getId();
                this.apartment = dataMethods.getCachedApartmentByApartmentID(apartmentID);
                Pair<Tenant, ArrayList<Tenant>> tenants = dataMethods.getCachedSelectedTenantAndRoomMatesByIDs(apartmentID, tenantID);
                this.tenant = tenants.first;
                this.otherTenants = tenants.second;
            }
            fillTextViews();
            TenantListFragment.tenantListAdapterNeedsRefreshed = true;
        }
    }

    public void fillTextViews() {
        firstNameTV.setText(tenant.getFirstName());
        lastNameTV.setText(tenant.getLastName());
        phoneTV.setText(tenant.getPhone());
        emailTV.setText(tenant.getTenantEmail());
        emergencyFirstNameTV.setText(tenant.getEmergencyFirstName());
        emergencyLastNameTV.setText(tenant.getEmergencyLastName());
        getEmergencyPhoneTV.setText(tenant.getEmergencyPhone());
        Log.d("TAG", "fillTextViews: " + tenant.getApartmentID());
        if (tenant.getApartmentID() == 0 ) {
            renterStatusTV.setText("Not Currently Renting");
            editLeaseBtn.setText("Create Lease");
            apartmentAddressTV.setText("");
            apartmentAddress2TV.setVisibility(View.GONE);
            leaseLL.setVisibility(View.GONE);
            leaseHolderTypeTV.setVisibility(View.GONE);

        } else {
            renterStatusTV.setText("Renting");
            editLeaseBtn.setText("Edit Lease");
            if (apartment != null) {
                apartmentAddressTV.setText(apartment.getStreet1());
                if (apartment.getStreet2().equals("")) {
                    apartmentAddress2TV.setVisibility(View.GONE);
                } else {
                    apartmentAddress2TV.setVisibility(View.VISIBLE);
                    apartmentAddress2TV.setText(apartment.getStreet2());
                }
            }
            if (tenant.getIsPrimary()) {
                leaseHolderTypeTV.setText("Primary Tenant");
            } else {
                leaseHolderTypeTV.setText("Secondary Tenant");
            }
            if (tenant.getLeaseStart() != null) {
                leaseLL.setVisibility(View.VISIBLE);
                leaseHolderTypeTV.setVisibility(View.VISIBLE);

                SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
                leaseStartTV.setText(formatter.format(tenant.getLeaseStart()));
                leaseEndTV.setText(formatter.format(tenant.getLeaseEnd()));


            } else {
                leaseLL.setVisibility(View.GONE);
                leaseHolderTypeTV.setVisibility(View.GONE);
            }
        }
        notesTV.setText(tenant.getNotes());
    }


    private void setOnClickListeners() {
        this.editLeaseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tenant.getApartmentID() > 0) {
                    Intent intent = new Intent(TenantViewActivity.this, NewLeaseFormActivity.class);
                    //Uses filtered results to match what is on screen
                    intent.putExtra("existingLeaseTenant", tenant);
                    startActivityForResult(intent, MainActivity.REQUEST_NEW_LEASE_FORM);
                } else {
                    showNewLeaseAlertDialog(view);
                }
            }
        });
    }

    public void showNewLeaseAlertDialog(View view) {
        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //builder.setTitle("AlertDialog");
        builder.setMessage("Will this be the primary tenant?");

        // add the buttons
        builder.setPositiveButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(TenantViewActivity.this, NewLeaseFormActivity.class);
                //Uses filtered results to match what is on screen
                intent.putExtra("secondaryTenant", tenant);
                startActivityForResult(intent, MainActivity.REQUEST_NEW_LEASE_FORM);
                ;
            }
        });
        builder.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(TenantViewActivity.this, NewLeaseFormActivity.class);
                //Uses filtered results to match what is on screen
                intent.putExtra("primaryTenant", tenant);
                startActivityForResult(intent, MainActivity.REQUEST_NEW_LEASE_FORM);
            }
        });

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //Save the fragment's instance
        super.onSaveInstanceState(outState);
        outState.putParcelable("tenant", tenant);
        if (apartment != null) {
            outState.putParcelable("apartment", apartment);
        }
    }

    public void showDeleteConfirmationAlertDialog() {
        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //builder.setTitle("AlertDialog");
        builder.setMessage("Are you sure you want to remove this tenant?");

        // add the buttons
        builder.setPositiveButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                databaseHandler.setTenantInactive(tenant);
                MainActivity.tenantList.remove(tenant);
                if (tenant.getIsPrimary()) {
                    tenant.setIsPrimary(false);
                    for (int x = 0; x < otherTenants.size(); x++) {
                        otherTenants.get(x).setApartmentID(0);
                        otherTenants.get(x).setLeaseStart(null);
                        otherTenants.get(x).setLeaseEnd(null);
                        databaseHandler.editTenant(otherTenants.get(x));
                    }
                    apartment.setRented(false);
                    MainActivity.tenantList = databaseHandler.getUsersTenants(MainActivity.user);
                }
                tenant.setApartmentID(0);
                tenant.setLeaseStart(null);
                tenant.setLeaseEnd(null);
                databaseHandler.editTenant(tenant);

                dataMethods.sortMainApartmentArray();
                //MainActivity.apartmentList = databaseHandler.getUsersApartments(MainActivity.user);
                TenantListFragment.tenantListAdapterNeedsRefreshed = true;
                ApartmentListFragment.apartmentListAdapterNeedsRefreshed = true;
                TenantViewActivity.this.finish();
            }
        });

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
