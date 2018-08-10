package com.rentbud.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.cody.rentbud.R;
import com.rentbud.activities.MainActivity;
import com.rentbud.activities.NewLeaseWizard;
import com.rentbud.helpers.MainArrayDataMethods;
import com.rentbud.model.Apartment;
import com.rentbud.model.Lease;
import com.rentbud.model.Tenant;
import com.rentbud.sqlite.DatabaseHandler;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;

public class LeaseViewFrag1 extends Fragment {
    Lease lease;
    TextView apartmentStreet1TV, apartmentStreet2TV, apartmentCityTV, apartmentStateTV, apartmentZIPTV, primaryTenantFirstNameTV,
            primaryTenantLastNameTV, primaryTenantPhoneTV, primaryTenantEmailTV, leaseStartTV, leaseEndTV, secondaryTenantsTV, notesTV;
    LinearLayout secondaryTenantsLL;
    DatabaseHandler databaseHandler;
    MainArrayDataMethods dataMethods;
    Apartment apartment;
    Tenant primaryTenant;
    ArrayList<Tenant> secondaryTenants;

    public LeaseViewFrag1() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setupUserAppTheme(MainActivity.curThemeChoice);
        // setContentView(R.layout.activity_lease_view);
        this.databaseHandler = new DatabaseHandler(getContext());
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
            Bundle bundle = getArguments();
            //Get apartment item
            // int leaseID = bundle.getInt("leaseID");
            this.lease = bundle.getParcelable("lease");
            this.apartment = databaseHandler.getApartmentByID(lease.getApartmentID(), MainActivity.user);
            this.primaryTenant = databaseHandler.getTenantByID(lease.getPrimaryTenantID(), MainActivity.user);
            secondaryTenants = new ArrayList<>();
            ArrayList<Integer> secondaryTenantIDs = lease.getSecondaryTenantIDs();
            for (int i = 0; i < secondaryTenantIDs.size(); i++) {
                Tenant secondaryTenant = databaseHandler.getTenantByID(secondaryTenantIDs.get(i), MainActivity.user);
                secondaryTenants.add(secondaryTenant);
            }
        }
        //getActivity().setTitle("Lease View");
        //  setupBasicToolbar();
    }

    private void fillTextViews() {
        if (apartment != null) {
            apartmentStreet1TV.setText(apartment.getStreet1());
            if (apartment.getStreet2() != null) {
                if (apartment.getStreet2().equals("")) {
                    apartmentStreet2TV.setVisibility(View.GONE);
                } else {
                    apartmentStreet2TV.setText(apartment.getStreet2());
                }
            } else {
                apartmentStreet2TV.setVisibility(View.GONE);
            }
            String city = apartment.getCity();
            //If city not empty, add comma
            if (!apartment.getCity().equals("")) {
                city += ",";
            }
            apartmentCityTV.setText(city);
            apartmentStateTV.setText(apartment.getState());
            apartmentZIPTV.setText(apartment.getZip());
        } else {
            apartmentStreet1TV.setText("");
            apartmentStreet2TV.setText("");
            apartmentCityTV.setText("");
            apartmentStateTV.setText("");
            apartmentZIPTV.setText("");
        }
        if (primaryTenant != null) {
            primaryTenantFirstNameTV.setText(primaryTenant.getFirstName());
            primaryTenantLastNameTV.setText(primaryTenant.getLastName());
            primaryTenantPhoneTV.setText(primaryTenant.getPhone());
            primaryTenantEmailTV.setText(primaryTenant.getEmail());
        } else {
            primaryTenantFirstNameTV.setText("");
            primaryTenantLastNameTV.setText("");
            primaryTenantPhoneTV.setText("");
            primaryTenantEmailTV.setText("");
        }

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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.lease_view_fragment_one, container, false);
        this.apartmentStreet1TV = rootView.findViewById(R.id.leaseViewStreet1TV);
        this.apartmentStreet2TV = rootView.findViewById(R.id.leaseViewStreet2TV);
        this.apartmentCityTV = rootView.findViewById(R.id.leaseViewCityTV);
        this.apartmentStateTV = rootView.findViewById(R.id.leaseViewStateTV);
        this.apartmentZIPTV = rootView.findViewById(R.id.leaseViewZipTV);
        this.primaryTenantFirstNameTV = rootView.findViewById(R.id.leaseViewPrimaryTenantFirstNameTV);
        this.primaryTenantLastNameTV = rootView.findViewById(R.id.leaseViewPrimaryTenantLastNameTV);
        this.primaryTenantPhoneTV = rootView.findViewById(R.id.leaseViewPrimaryTenantPhoneTV);
        this.primaryTenantEmailTV = rootView.findViewById(R.id.leaseViewPrimaryTenantEmailTV);
        this.leaseStartTV = rootView.findViewById(R.id.leaseViewLeaseStartTV);
        this.leaseEndTV = rootView.findViewById(R.id.leaseViewLeaseEndTV);
        this.secondaryTenantsTV = rootView.findViewById(R.id.leaseViewSecondaryTenantsTV);
        this.notesTV = rootView.findViewById(R.id.leaseViewNotesTV);
        this.secondaryTenantsLL = rootView.findViewById(R.id.leaseViewSecondaryTenantsLL);
        fillTextViews();
        return rootView;
    }

    @Override
    //Handle option menu actions
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.editLease:
                Intent intent = new Intent(getContext(), NewLeaseWizard.class);
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MainActivity.REQUEST_NEW_LEASE_FORM) {
            //If successful(not cancelled, passed validation)
            if (resultCode == RESULT_OK) {
                //Re - query cached apartment array to update cache and refresh current fragment to display new data
                int leaseID = data.getIntExtra("editedLeaseID", 0);
                this.lease = databaseHandler.getLeaseByID(MainActivity.user, lease.getId());
                //this.apartment = dataMethods.getCachedApartmentByApartmentID(lease.getApartmentID());
                //Pair<Tenant, ArrayList<Tenant>> tenants = dataMethods.getCachedPrimaryAndSecondaryTenantsByLease(lease);
                //this.primaryTenant = tenants.first;
                //this.secondaryTenants = tenants.second;
                this.apartment = databaseHandler.getApartmentByID(lease.getApartmentID(), MainActivity.user);
                this.primaryTenant = databaseHandler.getTenantByID(lease.getPrimaryTenantID(), MainActivity.user);
                ArrayList<Integer> secondaryTenantIDs = lease.getSecondaryTenantIDs();
                for (int i = 0; i < secondaryTenantIDs.size(); i++) {
                    Tenant secondaryTenant = databaseHandler.getTenantByID(secondaryTenantIDs.get(i), MainActivity.user);
                    secondaryTenants.add(secondaryTenant);
                }
                fillTextViews();
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
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

