package com.rba18.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
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
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.rba18.BuildConfig;
import com.rba18.R;
import com.google.android.gms.ads.AdView;
import com.rba18.activities.MainActivity;
import com.rba18.helpers.ApartmentTenantViewModel;
import com.rba18.helpers.DateAndCurrencyDisplayer;
import com.rba18.helpers.MainArrayDataMethods;
import com.rba18.model.Apartment;
import com.rba18.model.Lease;
import com.rba18.model.Tenant;
import com.rba18.sqlite.DatabaseHandler;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static android.app.Activity.RESULT_OK;

public class TenantViewFrag1 extends Fragment {
    private Tenant mTenantViewed, mPrimaryTenant;
    private ArrayList<Tenant> mOtherTenants;
    private ArrayList<Lease> mActiveLeases;
    private TextView mNameTV, mPhoneTV, mActiveLeaseDurationTV, mActiveLeaseDurationLabelTV, mNotesTV, mApartmentAddressTV,
            mApartmentAddressLabelTV, mEmailTV, mEmergencyNameTV, mEmergencyPhoneTV,
            mPrimaryTenantTV, mPrimaryTenantLabelTV, mOtherTenantsTV, mOtherTenantsLabelTV, mActiveLeasesHeaderTV;
    private Button mCallTenantBtn, mSMSTenantBtn, mEmailTenantBtn, mCallEContactBtn, mSMSEContactBtn;
    private TableRow mDurationTR, mApartmentTR, mPrimaryTenantTR, mOtherTenantsTR;
    private Apartment mApartment;
    private MainArrayDataMethods mDataMethods;
    private AlertDialog mAlertDialog;
    private SharedPreferences mPreferences;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DatabaseHandler databaseHandler = new DatabaseHandler(getContext());
        mPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        mDataMethods = new MainArrayDataMethods();
        mOtherTenants = new ArrayList<>();
        if (savedInstanceState != null) {
            mTenantViewed = savedInstanceState.getParcelable("tenant");
        } else {
            mTenantViewed = ViewModelProviders.of(getActivity()).get(ApartmentTenantViewModel.class).getViewedTenant().getValue();
        }
        Date today = Calendar.getInstance().getTime();
        mActiveLeases = new ArrayList<>();
        for (int i = 0; i < ViewModelProviders.of(getActivity()).get(ApartmentTenantViewModel.class).getLeaseArray().getValue().size(); i++) {
            if (ViewModelProviders.of(getActivity()).get(ApartmentTenantViewModel.class).getLeaseArray().getValue().get(i).getLeaseStart().before(today) &&
                    ViewModelProviders.of(getActivity()).get(ApartmentTenantViewModel.class).getLeaseArray().getValue().get(i).getLeaseEnd().after(today)) {
                mActiveLeases.add(ViewModelProviders.of(getActivity()).get(ApartmentTenantViewModel.class).getLeaseArray().getValue().get(i));
            }
        }
        if (mActiveLeases.size() == 1) {
            mApartment = databaseHandler.getApartmentByID(mActiveLeases.get(0).getApartmentID(), MainActivity.sUser);
            if(mActiveLeases.get(0) != null) {
                Pair<Tenant, ArrayList<Tenant>> tenants = mDataMethods.getCachedPrimaryAndSecondaryTenantsByLease(mActiveLeases.get(0));
                mPrimaryTenant = tenants.first;
                mOtherTenants = tenants.second;
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Uses tenant form to edit data
        if (requestCode == MainActivity.REQUEST_NEW_TENANT_FORM) {
            //If successful(not cancelled, passed validation)
            if (resultCode == RESULT_OK) {
                int tenantID = data.getIntExtra("editedTenantID", 0);
                mTenantViewed = mDataMethods.getCachedTenantByTenantID(tenantID);
                fillTextViews();
            }
        }
    }

    public void fillTextViews() {
        if (mTenantViewed != null) {
            mNameTV.setText(mTenantViewed.getFirstAndLastNameString());
            mPhoneTV.setText(mTenantViewed.getPhone());
            mEmailTV.setText(mTenantViewed.getTenantEmail());
            mEmergencyNameTV.setText(mTenantViewed.getEmergencyFirstAndLastNameString());
            mEmergencyPhoneTV.setText(mTenantViewed.getEmergencyPhone());
            mNotesTV.setText(mTenantViewed.getNotes());
            if (!mTenantViewed.getHasLease()) {
                mActiveLeaseDurationTV.setVisibility(View.GONE);
                mActiveLeaseDurationLabelTV.setVisibility(View.GONE);
                mApartmentAddressTV.setVisibility(View.GONE);
                mApartmentAddressLabelTV.setVisibility(View.GONE);
                mPrimaryTenantTV.setVisibility(View.GONE);
                mPrimaryTenantLabelTV.setVisibility(View.GONE);
                mOtherTenantsTV.setVisibility(View.GONE);
                mOtherTenantsLabelTV.setVisibility(View.GONE);
                mActiveLeasesHeaderTV.setVisibility(View.GONE);
                mDurationTR.setVisibility(View.GONE);
                mApartmentTR.setVisibility(View.GONE);
                mPrimaryTenantTV.setVisibility(View.GONE);
                mPrimaryTenantLabelTV.setVisibility(View.GONE);
                mPrimaryTenantTR.setVisibility(View.GONE);
                mPrimaryTenantTR.setVisibility(View.GONE);
                mOtherTenantsTR.setVisibility(View.GONE);
            } else {
                mActiveLeasesHeaderTV.setVisibility(View.VISIBLE);
                if (mActiveLeases.size() > 1) {
                    mActiveLeaseDurationTV.setVisibility(View.VISIBLE);
                    mActiveLeaseDurationLabelTV.setVisibility(View.VISIBLE);
                    mActiveLeaseDurationTV.setText(R.string.multiple_active_leases);
                    mApartmentAddressTV.setVisibility(View.GONE);
                    mApartmentAddressLabelTV.setVisibility(View.GONE);
                    mPrimaryTenantTV.setVisibility(View.GONE);
                    mPrimaryTenantLabelTV.setVisibility(View.GONE);
                    mOtherTenantsTV.setVisibility(View.GONE);
                    mOtherTenantsLabelTV.setVisibility(View.GONE);
                    mDurationTR.setVisibility(View.VISIBLE);
                    mApartmentTR.setVisibility(View.GONE);
                    mPrimaryTenantTR.setVisibility(View.GONE);
                    mOtherTenantsTR.setVisibility(View.GONE);
                    mPrimaryTenantTV.setVisibility(View.GONE);
                    mPrimaryTenantLabelTV.setVisibility(View.GONE);
                    mPrimaryTenantTR.setVisibility(View.GONE);
                } else if (mActiveLeases.size() == 1) {
                    Lease currentLease = mActiveLeases.get(0);
                    mActiveLeaseDurationTV.setVisibility(View.VISIBLE);
                    mActiveLeaseDurationLabelTV.setVisibility(View.VISIBLE);
                    mApartmentAddressTV.setVisibility(View.VISIBLE);
                    mApartmentAddressLabelTV.setVisibility(View.VISIBLE);
                    mOtherTenantsTV.setVisibility(View.VISIBLE);
                    mOtherTenantsLabelTV.setVisibility(View.VISIBLE);
                    mDurationTR.setVisibility(View.VISIBLE);
                    mApartmentTR.setVisibility(View.VISIBLE);
                    mPrimaryTenantTV.setVisibility(View.VISIBLE);
                    mPrimaryTenantLabelTV.setVisibility(View.VISIBLE);
                    mPrimaryTenantTR.setVisibility(View.VISIBLE);
                    mOtherTenantsTR.setVisibility(View.VISIBLE);
                    if (currentLease.getLeaseStart() != null && currentLease.getLeaseEnd() != null) {
                        int dateFormatCode = mPreferences.getInt("dateFormat", DateAndCurrencyDisplayer.DATE_MMDDYYYY);
                        mActiveLeaseDurationTV.setText(currentLease.getStartAndEndDatesString(dateFormatCode));
                    } else {
                        mActiveLeaseDurationTV.setText(R.string.error_leading_lease);
                    }
                    if (mApartment != null) {
                        mApartmentAddressTV.setText(mApartment.getFullAddressString());
                    } else {
                        mApartmentAddressTV.setText(R.string.error_loading_apartment);
                    }
                    if(mPrimaryTenant != null){
                        mPrimaryTenantTV.setText(mPrimaryTenant.getFirstAndLastNameString());
                    } else {
                        mPrimaryTenantTV.setText(R.string.error_loading_primary_tenant);
                    } if (!mOtherTenants.isEmpty()) {
                        mOtherTenantsTV.setText("");
                        for (int i = 0; i < mOtherTenants.size(); i++) {
                            mOtherTenantsTV.append(mOtherTenants.get(i).getFirstAndLastNameString());
                            if (i != mOtherTenants.size() - 1) {
                                mOtherTenantsTV.append("\n");
                            }
                        }
                    } else {
                        mOtherTenantsTV.setText(R.string.na);
                    }
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
        mNameTV = rootView.findViewById(R.id.tenantViewNameTextView);
        mPhoneTV = rootView.findViewById(R.id.tenantViewPhoneTextView);
        mActiveLeaseDurationTV = rootView.findViewById(R.id.tenantViewActiveLeaseDurationTextView);
        mActiveLeaseDurationLabelTV = rootView.findViewById(R.id.tenantViewActiveLeaseDurationLabelTextView);
        mNotesTV = rootView.findViewById(R.id.tenantViewNotesTextView);
        mApartmentAddressTV = rootView.findViewById(R.id.tenantViewActiveLeaseApartmentTextView);
        mApartmentAddressLabelTV = rootView.findViewById(R.id.tenantViewActiveLeaseApartmentLabelTextView);
        mEmailTV = rootView.findViewById(R.id.tenantViewEmailTextView);
        mEmergencyNameTV = rootView.findViewById(R.id.tenantViewEmergencyNameTextView);
        mEmergencyPhoneTV = rootView.findViewById(R.id.tenantViewEmergencyPhoneTextView);
        mPrimaryTenantTV = rootView.findViewById(R.id.tenantViewPrimaryTenantTextView);
        mPrimaryTenantLabelTV = rootView.findViewById(R.id.tenantViewPrimaryTenantLabelTextView);
        mOtherTenantsTV = rootView.findViewById(R.id.tenantViewActiveLeaseOtherTenantsTextView);
        mOtherTenantsLabelTV = rootView.findViewById(R.id.tenantViewActiveLeaseOtherTenantsLabelTextView);
        mActiveLeasesHeaderTV = rootView.findViewById(R.id.tenantViewActiveLeaseHeaderTV);
        LinearLayout adViewLL = rootView.findViewById(R.id.adViewLL);
        mCallTenantBtn = rootView.findViewById(R.id.tenantViewCallTenantBtn);
        mCallTenantBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callTenant();
            }
        });
        mSMSTenantBtn = rootView.findViewById(R.id.tenantViewSMSTenantBtn);
        mSMSTenantBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                smsTenant();
            }
        });
        mEmailTenantBtn = rootView.findViewById(R.id.tenantViewEmailTenantBtn);
        mEmailTenantBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                emailTenant();
            }
        });
        mCallEContactBtn = rootView.findViewById(R.id.tenantViewCallEContactBtn);
        mCallEContactBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callEContact();
            }
        });
        mSMSEContactBtn = rootView.findViewById(R.id.tenantViewSMSEContactBtn);
        mSMSEContactBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                smsEContact();
            }
        });
        mDurationTR = rootView.findViewById(R.id.tenantViewActiveLeaseDurationTR);
        mApartmentTR = rootView.findViewById(R.id.tenantViewActiveLeaseApartmentTR);
        mPrimaryTenantTR = rootView.findViewById(R.id.tenantViewActiveLeasePrimaryTenantTR);
        mOtherTenantsTR = rootView.findViewById(R.id.tenantViewActiveLeaseOtherTenantsTR);
        if (BuildConfig.FLAVOR.equals("free")) {
            AdView adView = rootView.findViewById(R.id.adView);
            //TODO enable for release
            //AdRequest adRequest = new AdRequest.Builder().build();
            //adView.loadAd(adRequest);
        } else {
            adViewLL.setVisibility(View.GONE);
        }
        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //Save the fragment's instance
        super.onSaveInstanceState(outState);
        outState.putParcelable("tenant", mTenantViewed);
    }

    public void updateTenantData(Tenant tenant) {
        mTenantViewed = tenant;
        fillTextViews();
    }

    private void callTenant() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE}, MainActivity.REQUEST_PHONE_CALL_PERMISSION);
        } else {
            if (!mTenantViewed.getPhone().equals("")) {
                String phoneNumber = mTenantViewed.getPhone();
                phoneNumber = phoneNumber.replaceAll("[\\s\\-()]", "");
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneNumber, null));
                startActivity(intent);
            }
        }
    }

    private void smsTenant() {
        if (!mTenantViewed.getPhone().equals("")) {
            String phoneNumber = mTenantViewed.getPhone();
            phoneNumber = phoneNumber.replaceAll("[\\s\\-()]", "");
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", phoneNumber, null)));
        }
    }

    private void callEContact() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE}, MainActivity.REQUEST_PHONE_CALL_PERMISSION);
        } else {
            if (!mTenantViewed.getEmergencyPhone().equals("")) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage(R.string.call_e_contact_confirmation);
                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String phoneNumber = mTenantViewed.getEmergencyPhone();
                        phoneNumber = phoneNumber.replaceAll("[\\s\\-()]", "");
                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneNumber, null));
                        startActivity(intent);
                    }
                });
                builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                // create and show the alert dialog
                mAlertDialog = builder.create();
                mAlertDialog.show();
            }
        }
    }

    private void smsEContact() {
        if (!mTenantViewed.getEmergencyPhone().equals("")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setMessage(R.string.sms_e_contact_confirmation);
            builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    String phoneNumber = mTenantViewed.getEmergencyPhone();
                    phoneNumber = phoneNumber.replaceAll("[\\s\\-()]", "");
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", phoneNumber, null)));
                }
            });
            builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            // create and show the alert dialog
            mAlertDialog = builder.create();
            mAlertDialog.show();
        }
    }

    private void emailTenant() {
        if (mTenantViewed.getEmail() != null) {
            if (!mTenantViewed.getEmail().equals("")) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{mTenantViewed.getEmail()});
                intent.setType("application/octet-stream");
                startActivityForResult(Intent.createChooser(intent, getContext().getResources().getString(R.string.send_email)), MainActivity.REQUEST_EMAIL);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mAlertDialog != null) {
            mAlertDialog.dismiss();
        }
    }
}

