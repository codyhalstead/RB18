package com.RB18.fragments;

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
import android.widget.TableRow;
import android.widget.TextView;

import com.example.cody.rentbud.BuildConfig;
import com.example.cody.rentbud.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.RB18.activities.MainActivity;
import com.RB18.helpers.ApartmentTenantViewModel;
import com.RB18.helpers.DateAndCurrencyDisplayer;
import com.RB18.helpers.MainArrayDataMethods;
import com.RB18.model.Apartment;
import com.RB18.model.Lease;
import com.RB18.model.Tenant;
import com.RB18.sqlite.DatabaseHandler;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static android.app.Activity.RESULT_OK;

public class TenantViewFrag1 extends Fragment {
    Tenant tenantViewed, primaryTenant;
    ArrayList<Tenant> otherTenants;
    ArrayList<Lease> activeLeases;
    TextView nameTV, phoneTV, activeLeaseDurationTV, activeLeaseDurationLabelTV, notesTV, apartmentAddressTV,
            apartmentAddressLabelTV, emailTV, emergencyNameTV, emergencyPhoneTV,
            primaryTenantTV, primaryTenantLabelTV, otherTenantsTV, otherTenantsLabelTV, activeLeasesHeaderTV;
    Button callTenantBtn, smsTenantBtn, emailTenantBtn, callEContactBtn, smsEContactBtn;
    TableRow durationTR, apartmentTR, primaryTenantTR, otherTenantsTR;
    Apartment apartment;
    //Lease currentLease;
    //LinearLayout leaseLL;
    DatabaseHandler databaseHandler;
    MainArrayDataMethods dataMethods;
    private AlertDialog alertDialog;
    private SharedPreferences preferences;
    AdView adView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseHandler = new DatabaseHandler(getContext());
        this.preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        dataMethods = new MainArrayDataMethods();
        otherTenants = new ArrayList<>();
        if (savedInstanceState != null) {
            tenantViewed = savedInstanceState.getParcelable("tenant");
        } else {
            this.tenantViewed = ViewModelProviders.of(getActivity()).get(ApartmentTenantViewModel.class).getViewedTenant().getValue();
        }
        //this.otherTenants = ViewModelProviders.of(getActivity()).get(ApartmentTenantViewModel.class).getSecondaryTenants().getValue();
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
            if(activeLeases.get(0) != null) {
                Pair<Tenant, ArrayList<Tenant>> tenants = dataMethods.getCachedPrimaryAndSecondaryTenantsByLease(activeLeases.get(0));
                this.primaryTenant = tenants.first;
                this.otherTenants = tenants.second;
            }
        }
        // multipleLeases = false;
        // if(activeLeases.size() > 1){
        //    multipleLeases = true;
        // } else if(activeLeases.size() == 1){
        //    currentLease = activeLeases.get(0);
        //}
        //getActivity().setTitle("Tenant View");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Uses apartment form to edit data
        if (requestCode == MainActivity.REQUEST_NEW_TENANT_FORM) {
            //If successful(not cancelled, passed validation)
            if (resultCode == RESULT_OK) {
                //Re-query cached apartment array to update cache and refresh current textViews to display new data. Re-query to sort list

                int tenantID = data.getIntExtra("editedTenantID", 0);
                this.tenantViewed = dataMethods.getCachedTenantByTenantID(tenantID);
                fillTextViews();
                //ApartmentListFragment.apartmentListAdapterNeedsRefreshed = true;
            }
        }
    }

    public void fillTextViews() {
        if (tenantViewed != null) {
            nameTV.setText(tenantViewed.getFirstAndLastNameString());
            phoneTV.setText(tenantViewed.getPhone());
            emailTV.setText(tenantViewed.getTenantEmail());
            emergencyNameTV.setText(tenantViewed.getEmergencyFirstAndLastNameString());
            emergencyPhoneTV.setText(tenantViewed.getEmergencyPhone());
            notesTV.setText(tenantViewed.getNotes());
            if (!tenantViewed.getHasLease()) {
                activeLeaseDurationTV.setVisibility(View.GONE);
                activeLeaseDurationLabelTV.setVisibility(View.GONE);
                apartmentAddressTV.setVisibility(View.GONE);
                apartmentAddressLabelTV.setVisibility(View.GONE);
                //leaseHolderTypeTV.setVisibility(View.GONE);
                //leaseHolderTypeLabelTV.setVisibility(View.GONE);
                primaryTenantTV.setVisibility(View.GONE);
                primaryTenantLabelTV.setVisibility(View.GONE);
                otherTenantsTV.setVisibility(View.GONE);
                otherTenantsLabelTV.setVisibility(View.GONE);
                activeLeasesHeaderTV.setVisibility(View.GONE);
                durationTR.setVisibility(View.GONE);
                apartmentTR.setVisibility(View.GONE);
                primaryTenantTV.setVisibility(View.GONE);
                primaryTenantLabelTV.setVisibility(View.GONE);
                primaryTenantTR.setVisibility(View.GONE);
                //primarySecondaryTR.setVisibility(View.GONE);
                primaryTenantTR.setVisibility(View.GONE);
                otherTenantsTR.setVisibility(View.GONE);
            } else {
                activeLeasesHeaderTV.setVisibility(View.VISIBLE);
                if (activeLeases.size() > 1) {
                    activeLeaseDurationTV.setVisibility(View.VISIBLE);
                    activeLeaseDurationLabelTV.setVisibility(View.VISIBLE);
                    activeLeaseDurationTV.setText(R.string.multiple_active_leases);
                    apartmentAddressTV.setVisibility(View.GONE);
                    apartmentAddressLabelTV.setVisibility(View.GONE);
                    //leaseHolderTypeTV.setVisibility(View.GONE);
                    //leaseHolderTypeLabelTV.setVisibility(View.GONE);
                    primaryTenantTV.setVisibility(View.GONE);
                    primaryTenantLabelTV.setVisibility(View.GONE);
                    otherTenantsTV.setVisibility(View.GONE);
                    otherTenantsLabelTV.setVisibility(View.GONE);
                    durationTR.setVisibility(View.VISIBLE);
                    apartmentTR.setVisibility(View.GONE);
                    //primarySecondaryTR.setVisibility(View.GONE);
                    primaryTenantTR.setVisibility(View.GONE);
                    otherTenantsTR.setVisibility(View.GONE);
                    primaryTenantTV.setVisibility(View.GONE);
                    primaryTenantLabelTV.setVisibility(View.GONE);
                    primaryTenantTR.setVisibility(View.GONE);
                } else if (activeLeases.size() == 1) {
                    Lease currentLease = activeLeases.get(0);
                    activeLeaseDurationTV.setVisibility(View.VISIBLE);
                    activeLeaseDurationLabelTV.setVisibility(View.VISIBLE);
                    apartmentAddressTV.setVisibility(View.VISIBLE);
                    apartmentAddressLabelTV.setVisibility(View.VISIBLE);
                    //leaseHolderTypeTV.setVisibility(View.VISIBLE);
                    //leaseHolderTypeLabelTV.setVisibility(View.VISIBLE);
                    otherTenantsTV.setVisibility(View.VISIBLE);
                    otherTenantsLabelTV.setVisibility(View.VISIBLE);
                    durationTR.setVisibility(View.VISIBLE);
                    apartmentTR.setVisibility(View.VISIBLE);
                    primaryTenantTV.setVisibility(View.VISIBLE);
                    primaryTenantLabelTV.setVisibility(View.VISIBLE);
                    primaryTenantTR.setVisibility(View.VISIBLE);
                    //primarySecondaryTR.setVisibility(View.VISIBLE);
                    otherTenantsTR.setVisibility(View.VISIBLE);
                    //if (tenantViewed.getId() == currentLease.getPrimaryTenantID()) {
                        //leaseHolderTypeTV.setText(R.string.primary_tenant);
                    //} else {
                        //leaseHolderTypeTV.setText(R.string.secondary_tenant);
                    //}
                    if (currentLease.getLeaseStart() != null && currentLease.getLeaseEnd() != null) {
                        int dateFormatCode = preferences.getInt("dateFormat", DateAndCurrencyDisplayer.DATE_MMDDYYYY);
                        activeLeaseDurationTV.setText(currentLease.getStartAndEndDatesString(dateFormatCode));
                    } else {
                        activeLeaseDurationTV.setText(R.string.error_leading_lease);
                    }
                    if (apartment != null) {
                        apartmentAddressTV.setText(apartment.getFullAddressString());
                    } else {
                        apartmentAddressTV.setText(R.string.error_loading_apartment);
                    }
                    if(primaryTenant != null){
                        primaryTenantTV.setText(primaryTenant.getFirstAndLastNameString());
                    } else {
                        primaryTenantTV.setText(R.string.error_loading_primary_tenant);
                    } if (!otherTenants.isEmpty()) {
                        otherTenantsTV.setText("");
                        for (int i = 0; i < otherTenants.size(); i++) {
                            otherTenantsTV.append(otherTenants.get(i).getFirstAndLastNameString());
                            if (i != otherTenants.size() - 1) {
                                otherTenantsTV.append("\n");
                            }
                        }
                    } else {
                        otherTenantsTV.setText(R.string.na);
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
        nameTV = rootView.findViewById(R.id.tenantViewNameTextView);
        //renterStatusTV = rootView.findViewById(R.id.tenantViewRentingStatusTextView);
        phoneTV = rootView.findViewById(R.id.tenantViewPhoneTextView);
        activeLeaseDurationTV = rootView.findViewById(R.id.tenantViewActiveLeaseDurationTextView);
        activeLeaseDurationLabelTV = rootView.findViewById(R.id.tenantViewActiveLeaseDurationLabelTextView);
        notesTV = rootView.findViewById(R.id.tenantViewNotesTextView);
        apartmentAddressTV = rootView.findViewById(R.id.tenantViewActiveLeaseApartmentTextView);
        apartmentAddressLabelTV = rootView.findViewById(R.id.tenantViewActiveLeaseApartmentLabelTextView);
        //leaseHolderTypeTV = rootView.findViewById(R.id.tenantViewActiveLeasePrimarySecondaryTextView);
        //leaseHolderTypeLabelTV = rootView.findViewById(R.id.tenantViewActiveLeasePrimarySecondaryLabelTextView);
        emailTV = rootView.findViewById(R.id.tenantViewEmailTextView);
        emergencyNameTV = rootView.findViewById(R.id.tenantViewEmergencyNameTextView);
        emergencyPhoneTV = rootView.findViewById(R.id.tenantViewEmergencyPhoneTextView);
        primaryTenantTV = rootView.findViewById(R.id.tenantViewPrimaryTenantTextView);
        primaryTenantLabelTV = rootView.findViewById(R.id.tenantViewPrimaryTenantLabelTextView);
        otherTenantsTV = rootView.findViewById(R.id.tenantViewActiveLeaseOtherTenantsTextView);
        otherTenantsLabelTV = rootView.findViewById(R.id.tenantViewActiveLeaseOtherTenantsLabelTextView);
        activeLeasesHeaderTV = rootView.findViewById(R.id.tenantViewActiveLeaseHeaderTV);
        //leaseLL = rootView.findViewById(R.id.tenantViewLeaseLL);
        callTenantBtn = rootView.findViewById(R.id.tenantViewCallTenantBtn);
        callTenantBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callTenant();
            }
        });
        smsTenantBtn = rootView.findViewById(R.id.tenantViewSMSTenantBtn);
        smsTenantBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                smsTenant();
            }
        });
        emailTenantBtn = rootView.findViewById(R.id.tenantViewEmailTenantBtn);
        emailTenantBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                emailTenant();
            }
        });
        callEContactBtn = rootView.findViewById(R.id.tenantViewCallEContactBtn);
        callEContactBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callEContact();
            }
        });
        smsEContactBtn = rootView.findViewById(R.id.tenantViewSMSEContactBtn);
        smsEContactBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                smsEContact();
            }
        });
        durationTR = rootView.findViewById(R.id.tenantViewActiveLeaseDurationTR);
        apartmentTR = rootView.findViewById(R.id.tenantViewActiveLeaseApartmentTR);
        //primarySecondaryTR = rootView.findViewById(R.id.tenantViewActiveLeasePrimarySecondaryTR);
        primaryTenantTR = rootView.findViewById(R.id.tenantViewActiveLeasePrimaryTenantTR);
        otherTenantsTR = rootView.findViewById(R.id.tenantViewActiveLeaseOtherTenantsTR);
        if (BuildConfig.FLAVOR.equals("free")) {
            adView = rootView.findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
            adView.loadAd(adRequest);
        }
        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //Save the fragment's instance
        super.onSaveInstanceState(outState);
        outState.putParcelable("tenant", tenantViewed);
    }

    public void updateTenantData(Tenant tenant) {
        this.tenantViewed = tenant;
        fillTextViews();
    }

    private void callTenant() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE}, MainActivity.REQUEST_PHONE_CALL_PERMISSION);
        } else {
            if (!tenantViewed.getPhone().equals("")) {
                String phoneNumber = tenantViewed.getPhone();
                phoneNumber.replaceAll("[\\s\\-()]", "");
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneNumber, null));
                startActivity(intent);
            }
        }
    }

    private void smsTenant() {
        if (!tenantViewed.getPhone().equals("")) {
            String phoneNumber = tenantViewed.getPhone();
            phoneNumber.replaceAll("[\\s\\-()]", "");
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", phoneNumber, null)));
        }
    }

    private void callEContact() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE}, MainActivity.REQUEST_PHONE_CALL_PERMISSION);
        } else {
            if (!tenantViewed.getEmergencyPhone().equals("")) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage(R.string.call_e_contact_confirmation);
                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String phoneNumber = tenantViewed.getEmergencyPhone();
                        phoneNumber.replaceAll("[\\s\\-()]", "");
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
                alertDialog = builder.create();
                alertDialog.show();
            }
        }
    }

    private void smsEContact() {
        if (!tenantViewed.getEmergencyPhone().equals("")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setMessage(R.string.sms_e_contact_confirmation);
            builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    String phoneNumber = tenantViewed.getEmergencyPhone();
                    phoneNumber.replaceAll("[\\s\\-()]", "");
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", phoneNumber, null)));
                }
            });
            builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            // create and show the alert dialog
            alertDialog = builder.create();
            alertDialog.show();
        }
    }

    private void emailTenant() {
        if (tenantViewed.getEmail() != null) {
            if (!tenantViewed.getEmail().equals("")) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{tenantViewed.getEmail()});
                intent.setType("application/octet-stream");
                startActivityForResult(Intent.createChooser(intent, getContext().getResources().getString(R.string.send_email)), MainActivity.REQUEST_EMAIL);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (alertDialog != null) {
            alertDialog.dismiss();
        }
    }
}

