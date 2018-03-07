package com.rentbud.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.cody.rentbud.R;
import com.rentbud.fragments.ApartmentListFragment;
import com.rentbud.model.Apartment;
import com.rentbud.model.Tenant;
import com.rentbud.model.User;
import com.rentbud.sqlite.DatabaseHandler;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Cody on 2/6/2018.
 */

public class ApartmentViewActivity extends BaseActivity {
    Apartment apartment;
    TextView street1TV, street2TV, cityTV, stateTV, zipTV, tenantStatusTV, descriptionTV, notesTV, primaryTenantFirstNameTV, primaryTennantLastNAmeTV, primaryTenantDisplayTV;
    TextView secondaryTenantsTV, leaseStatusTV, leaseStartTV, leaseEndTV, leaseHyphenTV;
    LinearLayout primaryTenantLL, secondaryTenantsLL;
    DatabaseHandler databaseHandler;
    Tenant primaryTenant;
    ArrayList<Tenant> secondaryTenants;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupUserAppTheme(MainActivity.curThemeChoice);
        setContentView(R.layout.activity_apartment_view);
        Bundle bundle = getIntent().getExtras();
        apartment = (Apartment) bundle.get("apartment");

        this.databaseHandler = new DatabaseHandler(this);

        street1TV = findViewById(R.id.apartmentViewStreet1TextView);
        street2TV = findViewById(R.id.apartmentViewStreet2TextView);
        cityTV = findViewById(R.id.apartmentViewCityTextView);
        stateTV = findViewById(R.id.apartmentViewStateTextView);
        zipTV = findViewById(R.id.apartmentViewZipTextView);
        //tenantStatusTV = findViewById(R.id.apartmentViewTenantStatusTextView);
        descriptionTV = findViewById(R.id.apartmentViewDescriptionTextView);
        notesTV = findViewById(R.id.apartmentViewNotesTextView);
        primaryTenantFirstNameTV = findViewById(R.id.apartmentViewPrimaryTenantFirstNameTV);
        primaryTennantLastNAmeTV = findViewById(R.id.apartmentViewPrimaryTenantLastNameTV);
        primaryTenantDisplayTV = findViewById(R.id.apartmentViewPrimaryTenantDisplayTV);
        secondaryTenantsTV = findViewById(R.id.apartmentViewSecondaryTenantsTV);
        leaseStatusTV = findViewById(R.id.apartmentViewRentalLeaseTV);
        leaseStartTV = findViewById(R.id.apartmentViewLeaseStartTextView);
        leaseEndTV = findViewById(R.id.apartmentViewLeaseEndTextView);
        leaseHyphenTV = findViewById(R.id.apartmentViewLeaseHyphenTV);

        primaryTenantLL = findViewById(R.id.apartmentViewPrimaryTenantLL);
        secondaryTenantsLL = findViewById(R.id.apartmentViewSecondaryTenantsLL);
        secondaryTenants = new ArrayList<>();
        getTenants();
        fillTextViews();

        setupBasicToolbar();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Get options menu
        getMenuInflater().inflate(R.menu.apartment_view_edit_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    //Handle option menu actions
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.editApartment:
                Intent intent = new Intent(this, NewApartmentFormActivity.class);
                intent.putExtra("apartmentToEdit", apartment);
                startActivityForResult(intent, MainActivity.REQUEST_NEW_APARTMENT_FORM);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MainActivity.REQUEST_NEW_APARTMENT_FORM) {
            //If successful(not cancelled, passed validation)
            if (resultCode == RESULT_OK) {
                //Re-query cached apartment array to update cache and refresh current fragment to display new data
                MainActivity.apartmentList = databaseHandler.getUsersApartments(MainActivity.user);
                this.apartment = (Apartment) data.getExtras().get("newApartmentInfo");
                fillTextViews();
                ApartmentListFragment.apartmentListAdapterNeedsRefreshed = true;
            }
        }
    }

    private void fillTextViews() {
        street1TV.setText(apartment.getStreet1());
        if (apartment.getStreet2().equals("")) {
            street2TV.setVisibility(View.GONE);
        } else {
            street2TV.setText(apartment.getStreet2());
        }
        cityTV.setText(apartment.getCity());
        stateTV.setText(apartment.getState());
        zipTV.setText(apartment.getZip());
        //tenantStatusTV.setText();
        descriptionTV.setText(apartment.getDescription());
        notesTV.setText(apartment.getNotes());
        if(primaryTenant != null){
            primaryTenantFirstNameTV.setText(primaryTenant.getFirstName());
            primaryTennantLastNAmeTV.setText(primaryTenant.getLastName());
            SimpleDateFormat formatTo = new SimpleDateFormat("MM-dd-yyyy");
            DateFormat formatFrom = new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy");
            try {
                Date startDate = formatFrom.parse(primaryTenant.getLeaseStart());
                Date endDate = formatFrom.parse(primaryTenant.getLeaseEnd());
                leaseStartTV.setText(formatTo.format(startDate));
                leaseEndTV.setText(formatTo.format(endDate));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if(!secondaryTenants.isEmpty()){
                for(int i = 0; i < secondaryTenants.size(); i++){
                    secondaryTenantsTV.append(secondaryTenants.get(i).getFirstName());
                    secondaryTenantsTV.append(" ");
                    secondaryTenantsTV.append(secondaryTenants.get(i).getLastName());
                    if(i != secondaryTenants.size() - 1){
                        secondaryTenantsTV.append("\n");
                    }
                }
            } else {
                secondaryTenantsLL.setVisibility(View.GONE);
            }
        } else {
            leaseStatusTV.setText("Vacant");
            leaseStartTV.setText("");
            leaseEndTV.setText("");
            leaseHyphenTV.setText("");
            primaryTenantLL.setVisibility(View.GONE);
            secondaryTenantsLL.setVisibility(View.GONE);
        }
    }

    private void getTenants() {
        for (int i = 0; i < MainActivity.tenantList.size(); i++) {
            if (MainActivity.tenantList.get(i).getApartmentID() == apartment.getId()) {
                if (MainActivity.tenantList.get(i).getIsPrimary()) {
                    primaryTenant = MainActivity.tenantList.get(i);
                } else {
                    secondaryTenants.add(MainActivity.tenantList.get(i));
                }
            }
        }
    }
}