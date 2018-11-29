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
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rba18.BuildConfig;
import com.rba18.R;
import com.google.android.gms.ads.AdView;
import com.rba18.activities.MainActivity;
import com.rba18.activities.NewLeaseWizard;
import com.rba18.helpers.DateAndCurrencyDisplayer;
import com.rba18.model.Apartment;
import com.rba18.model.Lease;
import com.rba18.model.Tenant;
import com.rba18.sqlite.DatabaseHandler;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

public class LeaseViewFrag1 extends Fragment {
    private Lease mLease;
    private TextView mApartmentAddressTV, mRentCostTV, mPaymentFrequencyTV,
            mPrimaryTenantNameTV, mPrimaryTenantPhoneTV, mPrimaryTenantEmailTV, mLeaseDurationTV, mSecondaryTenantsTV, mNotesTV, mDueDateTV;
    private DatabaseHandler mDatabaseHandler;
    private Apartment mApartment;
    private Tenant mPrimaryTenant;
    private Button mCallPrimaryTenantBtn, mSMSPrimaryTenantBtn, mEmailPrimaryTenantBtn, mEmailAllBtn;
    private ArrayList<Tenant> mSecondaryTenants;
    private SharedPreferences mPreferences;

    public LeaseViewFrag1() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDatabaseHandler = new DatabaseHandler(getContext());
        mPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        //if recreated
        if (savedInstanceState != null) {
            if (savedInstanceState.getParcelable("mLease") != null) {
                mLease = savedInstanceState.getParcelable("mLease");
            }
            if (savedInstanceState.getParcelable("mApartment") != null) {
                mApartment = savedInstanceState.getParcelable("mApartment");
            }
            if (savedInstanceState.getParcelable("mPrimaryTenant") != null) {
                mPrimaryTenant = savedInstanceState.getParcelable("mPrimaryTenant");
            }
            if (savedInstanceState.getParcelableArrayList("mSecondaryTenants") != null) {
                mSecondaryTenants = savedInstanceState.getParcelableArrayList("mSecondaryTenants");
            }
        } else {
            //If new
            Bundle bundle = getArguments();
            mLease = bundle.getParcelable("mLease");
            mApartment = mDatabaseHandler.getApartmentByID(mLease.getApartmentID(), MainActivity.sUser);
            mPrimaryTenant = mDatabaseHandler.getTenantByID(mLease.getPrimaryTenantID(), MainActivity.sUser);
            mSecondaryTenants = new ArrayList<>();
            ArrayList<Integer> secondaryTenantIDs = mLease.getSecondaryTenantIDs();
            for (int i = 0; i < secondaryTenantIDs.size(); i++) {
                Tenant secondaryTenant = mDatabaseHandler.getTenantByID(secondaryTenantIDs.get(i), MainActivity.sUser);
                mSecondaryTenants.add(secondaryTenant);
            }
        }
    }

    private void fillTextViews() {
        if (mApartment != null) {
            mApartmentAddressTV.setText(mApartment.getFullAddressString());
        } else {
            mApartmentAddressTV.setText(R.string.error_loading_apartment);
        }
        if (mPrimaryTenant != null) {
            mPrimaryTenantNameTV.setText(mPrimaryTenant.getFirstAndLastNameString());
            mPrimaryTenantPhoneTV.setText(mPrimaryTenant.getPhone());
            mPrimaryTenantEmailTV.setText(mPrimaryTenant.getEmail());
        } else {
            mPrimaryTenantNameTV.setText(R.string.error_loading_primary_tenant);
            mPrimaryTenantPhoneTV.setText(R.string.error_loading_primary_tenant);
            mPrimaryTenantEmailTV.setText(R.string.error_loading_primary_tenant);
        }
        mSecondaryTenantsTV.setText("");
        if (!mSecondaryTenants.isEmpty()) {
            for (int i = 0; i < mSecondaryTenants.size(); i++) {
                mSecondaryTenantsTV.append(mSecondaryTenants.get(i).getFirstAndLastNameString());
                if (i != mSecondaryTenants.size() - 1) {
                    mSecondaryTenantsTV.append("\n");
                }
            }
        } else {
                mSecondaryTenantsTV.setText(R.string.na);
        }
        if (mLease.getLeaseStart() != null && mLease.getLeaseEnd() != null) {
            int dateFormatCode = mPreferences.getInt("dateFormat", DateAndCurrencyDisplayer.DATE_MMDDYYYY);
            mLeaseDurationTV.setText(mLease.getStartAndEndDatesString(dateFormatCode));
        } else {
            mLeaseDurationTV.setText(R.string.error_leading_lease);
        }
        int moneyFormatCode = mPreferences.getInt("currency", DateAndCurrencyDisplayer.CURRENCY_US);
        mRentCostTV.setText(DateAndCurrencyDisplayer.getCurrencyToDisplay(moneyFormatCode, mLease.getMonthlyRentCost()));
        mNotesTV.setText(mLease.getNotes());
        String frequency = mDatabaseHandler.getFrequencyByID(mLease.getPaymentFrequencyID());
        String dueDate = mDatabaseHandler.getDueDateByID(mLease.getPaymentDayID());
        mPaymentFrequencyTV.setText(frequency);
        mDueDateTV.setText(dueDate);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.lease_view_fragment_one, container, false);
        mApartmentAddressTV = rootView.findViewById(R.id.leaseViewApartmentTextView);
        mRentCostTV = rootView.findViewById(R.id.leaseViewRentTextView);
        mPaymentFrequencyTV = rootView.findViewById(R.id.leaseViewFrequencyTextView);
        mDueDateTV = rootView.findViewById(R.id.leaseViewDueDateTV);
        mPrimaryTenantNameTV = rootView.findViewById(R.id.leaseViewPrimaryTenantTextView);
        mPrimaryTenantPhoneTV = rootView.findViewById(R.id.leaseViewPhoneTextView);
        mPrimaryTenantEmailTV = rootView.findViewById(R.id.leaseViewEmailTextView);
        mLeaseDurationTV = rootView.findViewById(R.id.leaseViewDurationTextView);
        mSecondaryTenantsTV = rootView.findViewById(R.id.leaseViewOtherTenantsTextView);
        mNotesTV = rootView.findViewById(R.id.leaseViewNotesTextView);
        LinearLayout adViewLL = rootView.findViewById(R.id.adViewLL);
        mCallPrimaryTenantBtn = rootView.findViewById(R.id.leaseViewCallTenantBtn);
        mCallPrimaryTenantBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callPrimaryTenant();
            }
        });
        mSMSPrimaryTenantBtn = rootView.findViewById(R.id.leaseViewSMSTenantBtn);
        mSMSPrimaryTenantBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                smsPrimaryTenant();
            }
        });
        mEmailPrimaryTenantBtn = rootView.findViewById(R.id.leaseViewEmailTenantBtn);
        mEmailPrimaryTenantBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                emailPrimaryTenant();
            }
        });
        mEmailAllBtn = rootView.findViewById(R.id.leaseViewEmailAllTenantsBtn);
        mEmailAllBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                emailAllTenants();
            }
        });
        if (BuildConfig.FLAVOR.equals("free")) {
            //TODO enable for release
            AdView adView = rootView.findViewById(R.id.adView);
            //AdRequest adRequest = new AdRequest.Builder().build();
            //adView.loadAd(adRequest);
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
                intent.putExtra("leaseToEdit", mLease);
                startActivityForResult(intent, MainActivity.REQUEST_NEW_LEASE_FORM);
                return true;

            case R.id.deleteLease:

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void updateLeaseData(Lease lease) {
        mLease = lease;
        fillTextViews();
    }

    private void callPrimaryTenant() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE}, MainActivity.REQUEST_PHONE_CALL_PERMISSION);
        } else {
            if (!mPrimaryTenant.getPhone().equals("")) {
                String phoneNumber = mPrimaryTenant.getPhone();
                phoneNumber = phoneNumber.replaceAll("[\\s\\-()]", "");
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneNumber, null));
                startActivity(intent);
            }
        }
    }

    private void smsPrimaryTenant() {
        if (!mPrimaryTenant.getPhone().equals("")) {
            String phoneNumber = mPrimaryTenant.getPhone();
            phoneNumber = phoneNumber.replaceAll("[\\s\\-()]", "");
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", phoneNumber, null)));
        }
    }

    private void emailPrimaryTenant() {
        if (mPrimaryTenant.getEmail() != null) {
            if (!mPrimaryTenant.getEmail().equals("")) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{mPrimaryTenant.getEmail()});
                intent.setType("application/octet-stream");
                startActivityForResult(Intent.createChooser(intent, getContext().getResources().getString(R.string.send_email)), MainActivity.REQUEST_EMAIL);
            }
        }
    }

    private void emailAllTenants() {
        if (mSecondaryTenants.isEmpty()) {
            emailPrimaryTenant();
        } else {
            ArrayList<String> emails = new ArrayList<>();
            if (mPrimaryTenant.getEmail() != null) {
                if (!mPrimaryTenant.getEmail().equals("")) {
                    emails.add(mPrimaryTenant.getEmail());
                }
            }
            for (int x = 0; x < mSecondaryTenants.size(); x++) {
                if (mSecondaryTenants.get(x).getEmail() != null) {
                    if (!mSecondaryTenants.get(x).getEmail().equals("")) {
                        emails.add(mSecondaryTenants.get(x).getEmail());
                    }
                }
            }
            if (!emails.isEmpty()) {
                String[] emailArray = new String[mSecondaryTenants.size() + 1];
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
                //Re - query cached mApartment array to update cache and refresh current fragment to display new data
                mLease = mDatabaseHandler.getLeaseByID(MainActivity.sUser, mLease.getId());
                mApartment = mDatabaseHandler.getApartmentByID(mLease.getApartmentID(), MainActivity.sUser);
                mPrimaryTenant = mDatabaseHandler.getTenantByID(mLease.getPrimaryTenantID(), MainActivity.sUser);
                ArrayList<Integer> secondaryTenantIDs = mLease.getSecondaryTenantIDs();
                mSecondaryTenants.clear();
                for (int i = 0; i < secondaryTenantIDs.size(); i++) {
                    Tenant secondaryTenant = mDatabaseHandler.getTenantByID(secondaryTenantIDs.get(i), MainActivity.sUser);
                    mSecondaryTenants.add(secondaryTenant);
                }
                fillTextViews();
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //Save the fragment's instance
        super.onSaveInstanceState(outState);
        outState.putParcelable("mLease", mLease);
        if (mApartment != null) {
            outState.putParcelable("mApartment", mApartment);
        }
        if (mPrimaryTenant != null) {
            outState.putParcelable("mPrimaryTenant", mPrimaryTenant);
        }
        if (mSecondaryTenants != null) {
            outState.putParcelableArrayList("mSecondaryTenants", mSecondaryTenants);
        }
    }

}

