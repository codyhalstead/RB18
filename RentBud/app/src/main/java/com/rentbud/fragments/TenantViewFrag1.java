package com.rentbud.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.cody.rentbud.R;
import com.rentbud.activities.BaseActivity;
import com.rentbud.activities.MainActivity;
import com.rentbud.activities.NewLeaseFormActivity;
import com.rentbud.activities.NewTenantWizard;
import com.rentbud.helpers.MainArrayDataMethods;
import com.rentbud.model.Apartment;
import com.rentbud.model.Lease;
import com.rentbud.model.Tenant;
import com.rentbud.sqlite.DatabaseHandler;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;

public class TenantViewFrag1 extends Fragment {
    Tenant tenant;
    ArrayList<Tenant> otherTenants;
    TextView firstNameTV, lastNameTV, renterStatusTV, phoneTV, leaseStartTV, leaseEndTV, notesTV, apartmentAddressTV, apartmentAddress2TV,
            leaseHolderTypeTV, emailTV, emergencyFirstNameTV, emergencyLastNameTV, getEmergencyPhoneTV;
    Button editLeaseBtn;
    Apartment apartment;
    Lease currentLease;
    LinearLayout leaseLL;
    DatabaseHandler databaseHandler;
    MainArrayDataMethods dataMethods;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setupUserAppTheme(MainActivity.curThemeChoice);
        //setContentView(R.layout.activity_tenant_view);
        databaseHandler = new DatabaseHandler(getContext());
        dataMethods = new MainArrayDataMethods();
        otherTenants = new ArrayList<>();
        if (savedInstanceState != null) {
            tenant = savedInstanceState.getParcelable("tenant");
            apartment = savedInstanceState.getParcelable("apartment");
            currentLease = savedInstanceState.getParcelable("currentLease");
        } else {
            Bundle bundle = getArguments();
            int tenantID = bundle.getInt("tenantID");
            tenant = dataMethods.getCachedTenantByTenantID(tenantID);
            currentLease = dataMethods.getCachedActiveLeaseByTenantID(tenantID);
            //   int apartmentID = dataMethods.getCachedApartmentByApartmentID(currentLease.getApartmentID());
            if (!tenant.getHasLease()) {
                this.tenant = dataMethods.getCachedTenantByTenantID(tenantID);
                this.apartment = null;
            } else {
                Pair<Tenant, ArrayList<Tenant>> tenants = dataMethods.getCachedSelectedTenantAndRoomMatesByLease(currentLease, tenantID);
                this.tenant = tenants.first;
                this.otherTenants = tenants.second;
                this.apartment = dataMethods.getCachedApartmentByApartmentID(currentLease.getApartmentID());
            }
        }
        getActivity().setTitle("Tenant View");
    }


    public void fillTextViews() {
        firstNameTV.setText(tenant.getFirstName());
        lastNameTV.setText(tenant.getLastName());
        phoneTV.setText(tenant.getPhone());
        emailTV.setText(tenant.getTenantEmail());
        emergencyFirstNameTV.setText(tenant.getEmergencyFirstName());
        emergencyLastNameTV.setText(tenant.getEmergencyLastName());
        getEmergencyPhoneTV.setText(tenant.getEmergencyPhone());
        if (!tenant.getHasLease()) {
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
                if(apartment.getStreet2() != null) {
                    if (apartment.getStreet2().equals("")) {
                        apartmentAddress2TV.setVisibility(View.GONE);
                    } else {
                        apartmentAddress2TV.setVisibility(View.VISIBLE);
                        apartmentAddress2TV.setText(apartment.getStreet2());
                    }
                } else {
                    apartmentAddress2TV.setVisibility(View.GONE);
                }
            }
            if (tenant.getId() == currentLease.getPrimaryTenantID()) {
                leaseHolderTypeTV.setText("Primary Tenant");
            } else {
                leaseHolderTypeTV.setText("Secondary Tenant");
            }
            if (currentLease.getLeaseStart() != null) {
                leaseLL.setVisibility(View.VISIBLE);
                leaseHolderTypeTV.setVisibility(View.VISIBLE);

                SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
                leaseStartTV.setText(formatter.format(currentLease.getLeaseStart()));
                leaseEndTV.setText(formatter.format(currentLease.getLeaseEnd()));


            } else {
                leaseLL.setVisibility(View.GONE);
                leaseHolderTypeTV.setVisibility(View.GONE);
            }
        }
        notesTV.setText(tenant.getNotes());
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fillTextViews();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tenant_view_fragment_one, container, false);
        firstNameTV = rootView.findViewById(R.id.tenantViewFirstNameTextView);
        lastNameTV = rootView.findViewById(R.id.tenantViewLastNameTextView);
        renterStatusTV = rootView.findViewById(R.id.tenantViewRentingStatusTextView);
        phoneTV = rootView.findViewById(R.id.tenantViewPhoneTextView);
        leaseStartTV = rootView.findViewById(R.id.tenantViewLeaseStartTextView);
        leaseEndTV = rootView.findViewById(R.id.tenantViewLeaseEndTextView);
        notesTV = rootView.findViewById(R.id.tenantViewNotesTextView);
        apartmentAddressTV = rootView.findViewById(R.id.tenantViewRentingAddressTextView);
        apartmentAddress2TV = rootView.findViewById(R.id.tenantViewRentingAddress2TextView);
        leaseHolderTypeTV = rootView.findViewById(R.id.tenantViewLeaseHolderType);
        emailTV = rootView.findViewById(R.id.tenantViewEmailTextView);
        emergencyFirstNameTV = rootView.findViewById(R.id.tenantViewEmergencyFirstNameTextView);
        emergencyLastNameTV = rootView.findViewById(R.id.tenantViewEmergencyLastNameTextView);
        getEmergencyPhoneTV = rootView.findViewById(R.id.tenantViewEmergencyPhoneTextView);
        editLeaseBtn = rootView.findViewById(R.id.tenantViewEditLeaseBtn);
        leaseLL = rootView.findViewById(R.id.tenantViewLeaseLL);
        return rootView;
    }

    public void showNewLeaseAlertDialog(View view) {
        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        //builder.setTitle("AlertDialog");
        builder.setMessage("Will this be the primary tenant?");

        // add the buttons
        builder.setPositiveButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(getContext(), NewLeaseFormActivity.class);
                //Uses filtered results to match what is on screen
                intent.putExtra("secondaryTenant", tenant);
                startActivityForResult(intent, MainActivity.REQUEST_NEW_LEASE_FORM);
                ;
            }
        });
        builder.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(getContext(), NewLeaseFormActivity.class);
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
    public void onSaveInstanceState(Bundle outState) {
        //Save the fragment's instance
        super.onSaveInstanceState(outState);
        outState.putParcelable("tenant", tenant);
        if (apartment != null) {
            outState.putParcelable("apartment", apartment);
        }
        if(currentLease != null){
            outState.putParcelable("currentLease", currentLease);
        }
    }

    public void showDeleteConfirmationAlertDialog() {
        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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
                if(currentLease != null) {
                    if (tenant.getId() == currentLease.getPrimaryTenantID()) {
                        //     tenant.setIsPrimary(false);
                        //     for (int x = 0; x < otherTenants.size(); x++) {
                        //         otherTenants.get(x).setApartmentID(0);
                        //         otherTenants.get(x).setLeaseStart(null);
                        //         otherTenants.get(x).setLeaseEnd(null);
                        //         databaseHandler.editTenant(otherTenants.get(x));
                        //     }
                        apartment.setRented(false);
                        MainActivity.tenantList = databaseHandler.getUsersTenantsIncludingInactive(MainActivity.user);
                    }
                }
                //tenant.setApartmentID(0);
                //tenant.setLeaseStart(null);
                //tenant.setLeaseEnd(null);
                //databaseHandler.editTenant(tenant);
                //TODO
                dataMethods.sortMainApartmentArray();
                //MainActivity5.apartmentList = databaseHandler.getUsersApartments(MainActivity5.user);
                TenantListFragment.tenantListAdapterNeedsRefreshed = true;
                ApartmentListFragment.apartmentListAdapterNeedsRefreshed = true;
                getActivity().finish();
            }
        });
        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
