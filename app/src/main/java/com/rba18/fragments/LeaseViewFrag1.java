package com.rba18.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rba18.BuildConfig;
import com.rba18.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.rba18.activities.MainActivity;
import com.rba18.activities.NewLeaseWizard;
import com.rba18.helpers.DateAndCurrencyDisplayer;
import com.rba18.helpers.MainArrayDataMethods;
import com.rba18.model.Apartment;
import com.rba18.model.Lease;
import com.rba18.model.Tenant;
import com.rba18.sqlite.DatabaseHandler;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;
import static android.support.constraint.Constraints.TAG;

public class LeaseViewFrag1 extends Fragment {
    Lease lease;
    TextView apartmentAddressTV, rentCostTV, paymentFrequencyTV,
            primaryTenantNameTV, primaryTenantPhoneTV, primaryTenantEmailTV, leaseDurationTV, secondaryTenantsTV, notesTV, dueDateTV;
    LinearLayout secondaryTenantsLL, adViewLL;
    DatabaseHandler databaseHandler;
    MainArrayDataMethods dataMethods;
    Apartment apartment;
    Tenant primaryTenant;
    Button callPrimaryTenantBtn, smsPrimaryTenantBtn, emailPrimaryTenantBtn, emailAllBtn;
    ArrayList<Tenant> secondaryTenants;
    private SharedPreferences preferences;
    AdView adView;

    public LeaseViewFrag1() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.databaseHandler = new DatabaseHandler(getContext());
        this.preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
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
    }

    private void fillTextViews() {
        if (apartment != null) {
            apartmentAddressTV.setText(apartment.getFullAddressString());
        } else {
            apartmentAddressTV.setText(R.string.error_loading_apartment);
        }
        if (primaryTenant != null) {
            primaryTenantNameTV.setText(primaryTenant.getFirstAndLastNameString());
            primaryTenantPhoneTV.setText(primaryTenant.getPhone());
            primaryTenantEmailTV.setText(primaryTenant.getEmail());
        } else {
            primaryTenantNameTV.setText(R.string.error_loading_primary_tenant);
            primaryTenantPhoneTV.setText(R.string.error_loading_primary_tenant);
            primaryTenantEmailTV.setText(R.string.error_loading_primary_tenant);
        }
        secondaryTenantsTV.setText("");
        if (!secondaryTenants.isEmpty()) {
            for (int i = 0; i < secondaryTenants.size(); i++) {
                secondaryTenantsTV.append(secondaryTenants.get(i).getFirstAndLastNameString());
                if (i != secondaryTenants.size() - 1) {
                    secondaryTenantsTV.append("\n");
                }
            }
        } else {
                secondaryTenantsTV.setText(R.string.na);
        }
        if (lease.getLeaseStart() != null && lease.getLeaseEnd() != null) {
            int dateFormatCode = preferences.getInt("dateFormat", DateAndCurrencyDisplayer.DATE_MMDDYYYY);
            leaseDurationTV.setText(lease.getStartAndEndDatesString(dateFormatCode));
        } else {
            leaseDurationTV.setText(R.string.error_leading_lease);
        }
        int moneyFormatCode = preferences.getInt("currency", DateAndCurrencyDisplayer.CURRENCY_US);
        rentCostTV.setText(DateAndCurrencyDisplayer.getCurrencyToDisplay(moneyFormatCode, lease.getMonthlyRentCost()));
        notesTV.setText(lease.getNotes());
        String frequency = databaseHandler.getFrequencyByID(lease.getPaymentFrequencyID());
        String dueDate = databaseHandler.getDueDateByID(lease.getPaymentDayID());
        paymentFrequencyTV.setText(frequency);
        dueDateTV.setText(dueDate);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.lease_view_fragment_one, container, false);
        this.apartmentAddressTV = rootView.findViewById(R.id.leaseViewApartmentTextView);
        this.rentCostTV = rootView.findViewById(R.id.leaseViewRentTextView);
        this.paymentFrequencyTV = rootView.findViewById(R.id.leaseViewFrequencyTextView);
        this.dueDateTV = rootView.findViewById(R.id.leaseViewDueDateTV);
        this.primaryTenantNameTV = rootView.findViewById(R.id.leaseViewPrimaryTenantTextView);
        this.primaryTenantPhoneTV = rootView.findViewById(R.id.leaseViewPhoneTextView);
        this.primaryTenantEmailTV = rootView.findViewById(R.id.leaseViewEmailTextView);
        this.leaseDurationTV = rootView.findViewById(R.id.leaseViewDurationTextView);
        this.secondaryTenantsTV = rootView.findViewById(R.id.leaseViewOtherTenantsTextView);
        this.notesTV = rootView.findViewById(R.id.leaseViewNotesTextView);
        adViewLL = rootView.findViewById(R.id.adViewLL);
        callPrimaryTenantBtn = rootView.findViewById(R.id.leaseViewCallTenantBtn);
        callPrimaryTenantBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callPrimaryTenant();
            }
        });
        smsPrimaryTenantBtn = rootView.findViewById(R.id.leaseViewSMSTenantBtn);
        smsPrimaryTenantBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                smsPrimaryTenant();
            }
        });
        emailPrimaryTenantBtn = rootView.findViewById(R.id.leaseViewEmailTenantBtn);
        emailPrimaryTenantBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                emailPrimaryTenant();
            }
        });
        emailAllBtn = rootView.findViewById(R.id.leaseViewEmailAllTenantsBtn);
        emailAllBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                emailAllTenants();
            }
        });
        if (BuildConfig.FLAVOR.equals("free")) {
            adView = rootView.findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
            adView.loadAd(adRequest);
        } else {
            adViewLL.setVisibility(View.GONE);
        }
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

    public void updateLeaseData(Lease lease) {
        this.lease = lease;
        fillTextViews();
    }

    private void callPrimaryTenant() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE}, MainActivity.REQUEST_PHONE_CALL_PERMISSION);
        } else {
            if (!primaryTenant.getPhone().equals("")) {
                String phoneNumber = primaryTenant.getPhone();
                phoneNumber.replaceAll("[\\s\\-()]", "");
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneNumber, null));
                startActivity(intent);
            }
        }
    }

    private void smsPrimaryTenant() {
        if (!primaryTenant.getPhone().equals("")) {
            String phoneNumber = primaryTenant.getPhone();
            phoneNumber.replaceAll("[\\s\\-()]", "");
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", phoneNumber, null)));
        }
    }

    private void emailPrimaryTenant() {
        if (primaryTenant.getEmail() != null) {
            if (!primaryTenant.getEmail().equals("")) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{primaryTenant.getEmail()});
                intent.setType("application/octet-stream");
                startActivityForResult(Intent.createChooser(intent, getContext().getResources().getString(R.string.send_email)), MainActivity.REQUEST_EMAIL);
            }
        }
    }

    private void emailAllTenants() {
        if (secondaryTenants.isEmpty()) {
            emailPrimaryTenant();
        } else {
            ArrayList<String> emails = new ArrayList<>();
            if (primaryTenant.getEmail() != null) {
                if (!primaryTenant.getEmail().equals("")) {
                    emails.add(primaryTenant.getEmail());
                }
            }
            for (int x = 0; x < secondaryTenants.size(); x++) {
                if (secondaryTenants.get(x).getEmail() != null) {
                    if (!secondaryTenants.get(x).getEmail().equals("")) {
                        emails.add(secondaryTenants.get(x).getEmail());
                    }
                }
            }
            if (!emails.isEmpty()) {
                String[] emailArray = new String[secondaryTenants.size() + 1];
                for (int y = 0; y < emails.size(); y++) {
                    emailArray[y] = emails.get(y);
                }
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_EMAIL, emailArray);
                intent.setType("application/octet-stream");
                startActivityForResult(Intent.createChooser(intent, getContext().getResources().getString(R.string.send_email)), MainActivity.REQUEST_EMAIL);

            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MainActivity.REQUEST_NEW_LEASE_FORM) {
            //If successful(not cancelled, passed validation)
            if (resultCode == RESULT_OK) {
                //Re - query cached apartment array to update cache and refresh current fragment to display new data
                this.lease = databaseHandler.getLeaseByID(MainActivity.user, lease.getId());
                this.apartment = databaseHandler.getApartmentByID(lease.getApartmentID(), MainActivity.user);
                this.primaryTenant = databaseHandler.getTenantByID(lease.getPrimaryTenantID(), MainActivity.user);
                ArrayList<Integer> secondaryTenantIDs = lease.getSecondaryTenantIDs();
                secondaryTenants.clear();
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

