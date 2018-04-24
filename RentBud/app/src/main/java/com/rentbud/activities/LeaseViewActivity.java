package com.rentbud.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.cody.rentbud.R;
import com.rentbud.fragments.IncomeListFragment;
import com.rentbud.fragments.LeaseListFragment;
import com.rentbud.helpers.MainArrayDataMethods;
import com.rentbud.model.Apartment;
import com.rentbud.model.Lease;
import com.rentbud.model.Tenant;
import com.rentbud.sqlite.DatabaseHandler;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by Cody on 4/14/2018.
 */

public class LeaseViewActivity extends BaseActivity {
    Lease lease;
    TextView apartmentStreet1TV, apartmentStreet2TV, apartmentCityTV, apartmentStateTV, apartmentZIPTV, primaryTenantFirstNameTV,
            primaryTenantLastNameTV, primaryTenantPhoneTV, primaryTenantEmailTV, leaseStartTV, leaseEndTV, secondaryTenantsTV, notesTV;
    LinearLayout secondaryTenantsLL;
    DatabaseHandler databaseHandler;
    MainArrayDataMethods dataMethods;
    Apartment apartment;
    Tenant primaryTenant;
    ArrayList<Tenant> secondaryTenants;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupUserAppTheme(MainActivity.curThemeChoice);
        setContentView(R.layout.activity_lease_view);
        this.databaseHandler = new DatabaseHandler(this);
        dataMethods = new MainArrayDataMethods();
        //if recreated
        if (savedInstanceState != null) {
            if (savedInstanceState.getParcelable("lease") != null) {
                this.lease = savedInstanceState.getParcelable("lease");
            }
            if (savedInstanceState.getParcelable("apartment") != null) {
                this.apartment = savedInstanceState.getParcelable("apartment");
            }
            if (savedInstanceState.getParcelable("primaryTenant") != null) {
                this.primaryTenant = savedInstanceState.getParcelable("primaryTenant");
            }
            if (savedInstanceState.getParcelableArrayList("secondaryTenants") != null) {
                this.secondaryTenants = savedInstanceState.getParcelableArrayList("secondaryTenants");
            }
        } else {
            //If new
            Bundle bundle = getIntent().getExtras();
            //Get apartment item
            int leaseID = bundle.getInt("leaseID");
            this.lease = databaseHandler.getLeaseByID(MainActivity.user, leaseID);
            this.apartment = dataMethods.getCachedApartmentByApartmentID(lease.getApartmentID());
            Pair<Tenant, ArrayList<Tenant>> tenants = dataMethods.getCachedPrimaryAndSecondaryTenantsByLease(lease);
            this.primaryTenant = tenants.first;
            this.secondaryTenants = tenants.second;
        }
        this.apartmentStreet1TV = findViewById(R.id.leaseViewStreet1TV);
        this.apartmentStreet2TV = findViewById(R.id.leaseViewStreet2TV);
        this.apartmentCityTV = findViewById(R.id.leaseViewCityTV);
        this.apartmentStateTV = findViewById(R.id.leaseViewStateTV);
        this.apartmentZIPTV = findViewById(R.id.leaseViewZipTV);
        this.primaryTenantFirstNameTV = findViewById(R.id.leaseViewPrimaryTenantFirstNameTV);
        this.primaryTenantLastNameTV = findViewById(R.id.leaseViewPrimaryTenantLastNameTV);
        this.primaryTenantPhoneTV = findViewById(R.id.leaseViewPrimaryTenantPhoneTV);
        this.primaryTenantEmailTV = findViewById(R.id.leaseViewPrimaryTenantEmailTV);
        this.leaseStartTV = findViewById(R.id.leaseViewLeaseStartTV);
        this.leaseEndTV = findViewById(R.id.leaseViewLeaseEndTV);
        this.secondaryTenantsTV = findViewById(R.id.leaseViewSecondaryTenantsTV);
        this.notesTV = findViewById(R.id.leaseViewNotesTV);
        this.secondaryTenantsLL = findViewById(R.id.leaseViewSecondaryTenantsLL);
        fillTextViews();
        setupBasicToolbar();
    }

    private void fillTextViews(){
        apartmentStreet1TV.setText(apartment.getStreet1());
        if (apartment.getStreet2().equals("")) {
            apartmentStreet2TV.setVisibility(View.GONE);
        } else {
            apartmentStreet2TV.setText(apartment.getStreet2());
        }
        String city = apartment.getCity();
        //If city not empty, add comma
        if (!apartment.getCity().equals("")) {
            city += ",";
        }
        apartmentCityTV.setText(city);
        apartmentStateTV.setText(apartment.getState());
        apartmentZIPTV.setText(apartment.getZip());

        primaryTenantFirstNameTV.setText(primaryTenant.getFirstName());
        primaryTenantLastNameTV.setText(primaryTenant.getLastName());
        primaryTenantPhoneTV.setText(primaryTenant.getPhone());
        primaryTenantEmailTV.setText(primaryTenant.getEmail());

        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
        leaseStartTV.setText(formatter.format(lease.getLeaseStart()));
        leaseEndTV.setText(formatter.format(lease.getLeaseEnd()));
        if (!secondaryTenants.isEmpty()) {
            secondaryTenantsLL.setVisibility(View.VISIBLE);
            secondaryTenantsTV.setText("");
            for (int i = 0; i < secondaryTenants.size(); i++) {
                secondaryTenantsTV.append(secondaryTenants.get(i).getFirstName());
                secondaryTenantsTV.append(" ");
                secondaryTenantsTV.append(secondaryTenants.get(i).getLastName());
                if (i != secondaryTenants.size() - 1) {
                    secondaryTenantsTV.append("\n");
                }
            }
        } else {
            secondaryTenantsLL.setVisibility(View.GONE);
        }
        notesTV.setText(lease.getNotes());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Get options menu
        getMenuInflater().inflate(R.menu.lease_view_edit_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    //Handle option menu actions
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.editLease:
                Intent intent = new Intent(this, NewLeaseFormActivity.class);
                intent.putExtra("leaseToEdit", lease);
                startActivityForResult(intent, MainActivity.REQUEST_NEW_LEASE_FORM);
                return true;

            case R.id.deleteLease:

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MainActivity.REQUEST_NEW_LEASE_FORM) {
            //If successful(not cancelled, passed validation)
            if (resultCode == RESULT_OK) {
                //Re-query cached apartment array to update cache and refresh current fragment to display new data
                //int leaseID = data.getIntExtra("editedLeaseID", 0);
                this.lease = databaseHandler.getLeaseByID(MainActivity.user, lease.getId());
                this.apartment = dataMethods.getCachedApartmentByApartmentID(lease.getApartmentID());
                Pair<Tenant, ArrayList<Tenant>> tenants = dataMethods.getCachedPrimaryAndSecondaryTenantsByLease(lease);
                this.primaryTenant = tenants.first;
                this.secondaryTenants = tenants.second;
                fillTextViews();
                LeaseListFragment.leaseListAdapterNeedsRefreshed = true;
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //Save the fragment's instance
        super.onSaveInstanceState(outState);
        outState.putParcelable("lease", lease);
        if (apartment != null) {
            outState.putParcelable("apartment", apartment);
        }
        if (primaryTenant != null) {
            outState.putParcelable("primaryTenant", primaryTenant);
        }
        if (secondaryTenants != null) {
            outState.putParcelableArrayList("secondaryTenants", secondaryTenants);
        }
    }
}
