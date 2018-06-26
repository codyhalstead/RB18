package com.rentbud.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Scroller;
import android.widget.TextView;

import com.example.android.wizardpager.wizard.ui.PageFragmentCallbacks;
import com.example.cody.rentbud.R;
import com.rentbud.activities.MainActivity;
import com.rentbud.helpers.MainArrayDataMethods;
import com.rentbud.helpers.TenantOrApartmentChooserDialog;
import com.rentbud.model.Apartment;
import com.rentbud.model.Lease;
import com.rentbud.model.Tenant;
import com.rentbud.sqlite.DatabaseHandler;
import com.rentbud.wizards.ExpenseWizardPage3;
import com.rentbud.wizards.IncomeWizardPage3;

import java.util.ArrayList;

public class IncomeWizardPage3Fragment  extends android.support.v4.app.Fragment {
    private static final String ARG_KEY = "key";

    private PageFragmentCallbacks mCallbacks;
    private String mKey;
    private IncomeWizardPage3 mPage;
    private TextView linkedAptTV, linkedTenantTV, linkedLeaseTV;
    private DatabaseHandler dbHandler;
    private MainArrayDataMethods mainArrayDataMethods;
    private Apartment apartment;
    private ArrayList<Apartment> availableApartments;
    private Tenant tenant;
    private ArrayList<Tenant> availableTenants;
    private Lease lease;
    private ArrayList<Lease> availableLeases;

    public static IncomeWizardPage3Fragment create(String key) {
        Bundle args = new Bundle();
        args.putString(ARG_KEY, key);

        IncomeWizardPage3Fragment fragment = new IncomeWizardPage3Fragment();
        fragment.setArguments(args);
        return fragment;
    }

    public IncomeWizardPage3Fragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        mKey = args.getString(ARG_KEY);
        mPage = (IncomeWizardPage3) mCallbacks.onGetPage(mKey);
        dbHandler = new DatabaseHandler(getContext());
        mainArrayDataMethods = new MainArrayDataMethods();
        availableApartments = new ArrayList<>();
        availableTenants = new ArrayList<>();
        availableLeases = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_income_wizard_page_3, container, false);
        (rootView.findViewById(android.R.id.title)).setVisibility(View.GONE);
        linkedAptTV = rootView.findViewById(R.id.incomeWizardAptLinkingTV);
        if(mPage.getData().getParcelable(IncomeWizardPage3.INCOME_RELATED_APT_DATA_KEY) != null){
            linkedAptTV.setText(mPage.getData().getString(IncomeWizardPage3.INCOME_RELATED_APT_TEXT_DATA_KEY));
            apartment = mPage.getData().getParcelable(IncomeWizardPage3.INCOME_RELATED_APT_DATA_KEY);
        }
        linkedAptTV.setScroller(new Scroller(getContext()));
        linkedAptTV.setMaxLines(5);
        linkedAptTV.setVerticalScrollBarEnabled(true);
        linkedAptTV.setMovementMethod(new ScrollingMovementMethod());

        linkedTenantTV = rootView.findViewById(R.id.incomeWizardTenantLinkingTV);
        if(mPage.getData().getParcelable(IncomeWizardPage3.INCOME_RELATED_TENANT_DATA_KEY) != null){
            linkedTenantTV.setText(mPage.getData().getString(IncomeWizardPage3.INCOME_RELATED_TENANT_TEXT_DATA_KEY));
            tenant = mPage.getData().getParcelable(IncomeWizardPage3.INCOME_RELATED_TENANT_DATA_KEY);
        }
        linkedTenantTV.setScroller(new Scroller(getContext()));
        linkedTenantTV.setMaxLines(5);
        linkedTenantTV.setVerticalScrollBarEnabled(true);
        linkedTenantTV.setMovementMethod(new ScrollingMovementMethod());

        linkedLeaseTV = rootView.findViewById(R.id.incomeWizardLeaseLinkingTV);
        if(mPage.getData().getParcelable(IncomeWizardPage3.INCOME_RELATED_LEASE_DATA_KEY) != null){
            linkedLeaseTV.setText(mPage.getData().getString(IncomeWizardPage3.INCOME_RELATED_LEASE_TEXT_DATA_KEY));
            lease = mPage.getData().getParcelable(IncomeWizardPage3.INCOME_RELATED_LEASE_DATA_KEY);
        }
        linkedLeaseTV.setScroller(new Scroller(getContext()));
        linkedLeaseTV.setMaxLines(5);
        linkedLeaseTV.setVerticalScrollBarEnabled(true);
        linkedLeaseTV.setMovementMethod(new ScrollingMovementMethod());
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (!(activity instanceof PageFragmentCallbacks)) {
            throw new ClassCastException("Activity must implement PageFragmentCallbacks");
        }

        mCallbacks = (PageFragmentCallbacks) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        int curAptID = 0;
        if(apartment != null){
            curAptID = apartment.getId();
        }
        for(int i = 0; i < MainActivity.apartmentList.size(); i++){
            if(MainActivity.apartmentList.get(i).isActive() && MainActivity.apartmentList.get(i).getId() != curAptID){
                availableApartments.add(MainActivity.apartmentList.get(i));
            }
        }

        ArrayList<Integer> curTenantIDs = new ArrayList<>();
        if (mPage.getData().getParcelable(IncomeWizardPage3.INCOME_RELATED_TENANT_DATA_KEY) != null) {
            tenant = mPage.getData().getParcelable(IncomeWizardPage3.INCOME_RELATED_TENANT_DATA_KEY);
            curTenantIDs.add(tenant.getId());
        }
        //if (mPage.getData().getParcelableArrayList(LeaseWizardPage2.LEASE_SECONDARY_TENANTS_DATA_KEY) != null) {
        //    secondaryTenants = mPage.getData().getParcelableArrayList(LeaseWizardPage2.LEASE_SECONDARY_TENANTS_DATA_KEY);
        //    for (int z = 0; z < secondaryTenants.size(); z++) {
        //        curTenantIDs.add(secondaryTenants.get(z).getId());
        //    }
        //}

        for (int i = 0; i < MainActivity.tenantList.size(); i++) {
            if (MainActivity.tenantList.get(i).isActive()) {
                // if (!MainActivity.tenantList.get(i).getHasLease()) {
                //     availableTenants.add(MainActivity.tenantList.get(i));
                // } else {
                boolean isUsed = false;
                for (int y = 0; y < curTenantIDs.size(); y++){
                    if (MainActivity.tenantList.get(i).getId() == curTenantIDs.get(y)){
                        isUsed = true;
                        break;
                    }
                }
                if(!isUsed){
                    availableTenants.add(MainActivity.tenantList.get(i));
                }
                //    }
            }
        }

        linkedAptTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TenantOrApartmentChooserDialog dialog = new TenantOrApartmentChooserDialog(getContext(), TenantOrApartmentChooserDialog.APARTMENT_TYPE, availableApartments);
                dialog.show();
                dialog.setDialogResult(new TenantOrApartmentChooserDialog.OnTenantChooserDialogResult() {
                    @Override
                    public void finish(Tenant tenantResult, Apartment apartmentResult) {
                        if (apartment != null) {
                            availableApartments.add(apartment);
                        }
                        availableApartments.remove(apartmentResult);
                        apartment = apartmentResult;
                        setApartmentTextView();
                        mPage.getData().putString(IncomeWizardPage3.INCOME_RELATED_APT_TEXT_DATA_KEY, linkedAptTV.getText().toString());
                        mPage.getData().putParcelable(IncomeWizardPage3.INCOME_RELATED_APT_DATA_KEY, apartment);
                        mPage.notifyDataChanged();
                    }
                });
            }
        });
        linkedTenantTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TenantOrApartmentChooserDialog dialog = new TenantOrApartmentChooserDialog(getContext(), TenantOrApartmentChooserDialog.TENANT_TYPE, availableTenants);
                dialog.show();
                dialog.setDialogResult(new TenantOrApartmentChooserDialog.OnTenantChooserDialogResult() {
                    @Override
                    public void finish(Tenant tenantResult, Apartment apartmentResult) {
                        if (tenant != null) {
                            availableTenants.add(tenant);
                        }
                        availableTenants.remove(tenantResult);
                        //availableTenants.add(primaryTenant);
                        tenant = tenantResult;
                        setTenantTextView();
                        mPage.getData().putString(IncomeWizardPage3.INCOME_RELATED_TENANT_TEXT_DATA_KEY, linkedTenantTV.getText().toString());
                        mPage.getData().putParcelable(IncomeWizardPage3.INCOME_RELATED_TENANT_DATA_KEY, tenant);
                        mPage.notifyDataChanged();
                    }
                });
            }
        });

        //TODO
        if(mPage.getData().getParcelable(IncomeWizardPage3.INCOME_RELATED_APT_DATA_KEY) == null){
            mPage.getData().putString(IncomeWizardPage3.INCOME_RELATED_APT_TEXT_DATA_KEY, "");
            //   mPage.getData().putParcelable(IncomeWizardPage3.INCOME_RELATED_APT_DATA_KEY, null);
        }
        if(mPage.getData().getParcelable(IncomeWizardPage3.INCOME_RELATED_TENANT_DATA_KEY) == null){
            mPage.getData().putString(IncomeWizardPage3.INCOME_RELATED_TENANT_TEXT_DATA_KEY, "");
        }
        if(mPage.getData().getParcelable(IncomeWizardPage3.INCOME_RELATED_LEASE_DATA_KEY) == null){
            mPage.getData().putString(IncomeWizardPage3.INCOME_RELATED_LEASE_TEXT_DATA_KEY, "");
        }
    }

    private void setApartmentTextView() {
        linkedAptTV.setText(apartment.getStreet1());
        if(apartment.getStreet2() != null) {
            linkedAptTV.append(" ");
            linkedAptTV.append(apartment.getStreet2());
        }
    }

    private void setTenantTextView() {
        linkedTenantTV.setText(tenant.getFirstName());
        linkedTenantTV.append(" ");
        linkedTenantTV.append(tenant.getLastName());
    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);

        // In a future update to the support library, this should override setUserVisibleHint
        // instead of setMenuVisibility.
        if (linkedAptTV != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            if (!menuVisible) {
                imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
            }
        }
    }
}

