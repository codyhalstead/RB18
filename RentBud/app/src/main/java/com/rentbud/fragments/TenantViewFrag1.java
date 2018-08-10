package com.rentbud.fragments;

import android.app.AlertDialog;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
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
import com.rentbud.helpers.ApartmentTenantViewModel;
import com.rentbud.helpers.MainArrayDataMethods;
import com.rentbud.model.Apartment;
import com.rentbud.model.Lease;
import com.rentbud.model.Tenant;
import com.rentbud.sqlite.DatabaseHandler;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;

public class TenantViewFrag1 extends Fragment {
    Tenant tenantViewed, primaryTenant;
    ArrayList<Tenant> otherTenants;
    ArrayList<Lease> activeLeases;
    TextView firstNameTV, lastNameTV, phoneTV, leaseStartTV, leaseEndTV, notesTV, apartmentAddressTV, apartmentAddress2TV,
            leaseHolderTypeTV, emailTV, emergencyFirstNameTV, emergencyLastNameTV, getEmergencyPhoneTV;
    Button editLeaseBtn;
    Apartment apartment;
    //Lease currentLease;
    LinearLayout leaseLL;
    DatabaseHandler databaseHandler;
    MainArrayDataMethods dataMethods;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseHandler = new DatabaseHandler(getContext());
        dataMethods = new MainArrayDataMethods();
        otherTenants = new ArrayList<>();
        if (savedInstanceState != null) {
            tenantViewed = savedInstanceState.getParcelable("tenant");
        } else {
            this.tenantViewed = ViewModelProviders.of(getActivity()).get(ApartmentTenantViewModel.class).getViewedTenant().getValue();
        }
        this.otherTenants = ViewModelProviders.of(getActivity()).get(ApartmentTenantViewModel.class).getSecondaryTenants().getValue();
        Date today = Calendar.getInstance().getTime();
        activeLeases = new ArrayList<>();
        for (int i = 0; i < ViewModelProviders.of(getActivity()).get(ApartmentTenantViewModel.class).getLeaseArray().getValue().size(); i++) {
            if (ViewModelProviders.of(getActivity()).get(ApartmentTenantViewModel.class).getLeaseArray().getValue().get(i).getLeaseStart().before(today) &&
                    ViewModelProviders.of(getActivity()).get(ApartmentTenantViewModel.class).getLeaseArray().getValue().get(i).getLeaseEnd().after(today)) {
                activeLeases.add(ViewModelProviders.of(getActivity()).get(ApartmentTenantViewModel.class).getLeaseArray().getValue().get(i));
            }
        }
        if (activeLeases.size() == 1) {
            apartment = databaseHandler.getApartmentByID(activeLeases.get(0).getApartmentID(), MainActivity.user);
        }
        // multipleLeases = false;
        // if(activeLeases.size() > 1){
        //    multipleLeases = true;
        // } else if(activeLeases.size() == 1){
        //    currentLease = activeLeases.get(0);
        //}
        //getActivity().setTitle("Tenant View");
    }


    public void fillTextViews() {
        if (tenantViewed != null) {
            firstNameTV.setText(tenantViewed.getFirstName());
            lastNameTV.setText(tenantViewed.getLastName());
            phoneTV.setText(tenantViewed.getPhone());
            emailTV.setText(tenantViewed.getTenantEmail());
            emergencyFirstNameTV.setText(tenantViewed.getEmergencyFirstName());
            emergencyLastNameTV.setText(tenantViewed.getEmergencyLastName());
            getEmergencyPhoneTV.setText(tenantViewed.getEmergencyPhone());
            notesTV.setText(tenantViewed.getNotes());
            if (!tenantViewed.getHasLease()) {
                //renterStatusTV.setText();
                //editLeaseBtn.setText("Create Lease");
                apartmentAddressTV.setText(R.string.not_currently_renting);
                apartmentAddress2TV.setVisibility(View.GONE);
                leaseLL.setVisibility(View.GONE);
                leaseHolderTypeTV.setVisibility(View.GONE);
            } else {
                //renterStatusTV.setText("Renting");
                //editLeaseBtn.setText("Edit Lease");
                if (activeLeases.size() > 1) {
                    //renterStatusTV.setText();
                    apartmentAddressTV.setText(R.string.multiple_active_leases);
                    apartmentAddress2TV.setVisibility(View.GONE);
                    leaseLL.setVisibility(View.GONE);
                    leaseHolderTypeTV.setVisibility(View.GONE);
                } else if (activeLeases.size() == 1) {
                    Lease currentLease = activeLeases.get(0);
                    if (tenantViewed.getId() == currentLease.getPrimaryTenantID()) {
                        leaseHolderTypeTV.setText(R.string.primary_tenant);
                    } else {
                        leaseHolderTypeTV.setText(R.string.secondary_tenant);
                    }
                    if (currentLease.getLeaseStart() != null) {
                        leaseLL.setVisibility(View.VISIBLE);
                        leaseHolderTypeTV.setVisibility(View.VISIBLE);

                        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
                        leaseStartTV.setText(formatter.format(currentLease.getLeaseStart()));
                        leaseEndTV.setText(formatter.format(currentLease.getLeaseEnd()));
                    } else {
                        //leaseLL.setVisibility(View.GONE);
                        //leaseHolderTypeTV.setVisibility(View.GONE);
                    }
                    if (apartment != null) {
                        apartmentAddressTV.setText(apartment.getStreet1());
                        if (apartment.getStreet2() != null) {
                            if (apartment.getStreet2().equals("")) {
                                apartmentAddress2TV.setVisibility(View.GONE);
                            } else {
                                apartmentAddress2TV.setVisibility(View.VISIBLE);
                                apartmentAddress2TV.setText(apartment.getStreet2());
                            }
                        } else {
                            apartmentAddress2TV.setVisibility(View.GONE);
                        }
                    } else {
                        apartmentAddressTV.setText(R.string.error_loading_apartment);
                        apartmentAddress2TV.setVisibility(View.GONE);
                    }
                } else {
                    //renterStatusTV.setText();
                    apartmentAddressTV.setText(R.string.error_leading_lease);
                    apartmentAddress2TV.setVisibility(View.GONE);
                    leaseLL.setVisibility(View.GONE);
                    leaseHolderTypeTV.setVisibility(View.GONE);
                }
            }
        }
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
        //renterStatusTV = rootView.findViewById(R.id.tenantViewRentingStatusTextView);
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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //Save the fragment's instance
        super.onSaveInstanceState(outState);
        outState.putParcelable("tenant", tenantViewed);
    }
}

