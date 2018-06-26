package com.rentbud.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Scroller;
import android.widget.TextView;

import com.example.android.wizardpager.wizard.ui.PageFragmentCallbacks;
import com.example.cody.rentbud.R;
import com.rentbud.activities.MainActivity;
import com.rentbud.helpers.TenantOrApartmentChooserDialog;
import com.rentbud.model.Apartment;
import com.rentbud.wizards.LeaseWizardPage2;
import com.rentbud.model.Tenant;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;

import static android.support.constraint.Constraints.TAG;

public class LeaseWizardPage2Fragment extends android.support.v4.app.Fragment {
    private static final String ARG_KEY = "key";

    private PageFragmentCallbacks mCallbacks;
    private String mKey;
    private LeaseWizardPage2 mPage;
    private TextView primaryTenantTV, primaryTenantLabelTV, secondaryTenantsTV, secondaryTenantsLabelTV, depositAmountLabelTV, depositWithheldLabelTV;
    private EditText depositAmountET, depositWithheldET;
    private Tenant primaryTenant;
    private ArrayList<Tenant> secondaryTenants, availableTenants;
    private BigDecimal deposit, depositWithheld;
    private Button addSecondaryTenantBtn, removeSecondaryTenantBtn;


    public static LeaseWizardPage2Fragment create(String key) {
        Bundle args = new Bundle();
        args.putString(ARG_KEY, key);

        LeaseWizardPage2Fragment fragment = new LeaseWizardPage2Fragment();
        fragment.setArguments(args);
        return fragment;
    }

    public LeaseWizardPage2Fragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        mKey = args.getString(ARG_KEY);
        mPage = (LeaseWizardPage2) mCallbacks.onGetPage(mKey);
        secondaryTenants = new ArrayList<>();
        availableTenants = new ArrayList<>();
        //availableTenants.addAll(MainActivity.tenantList);
        deposit = new BigDecimal(0);
        depositWithheld = new BigDecimal(0);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_lease_wizard_page_2, container, false);
        (rootView.findViewById(android.R.id.title)).setVisibility(View.GONE);
        primaryTenantTV = rootView.findViewById(R.id.leaseWizardPrimaryTenantTV);
        primaryTenantTV.setText(mPage.getData().getString(LeaseWizardPage2.LEASE_PRIMARY_TENANT_STRING_DATA_KEY));
        primaryTenantTV.setScroller(new Scroller(getContext()));
        primaryTenantTV.setMaxLines(5);
        primaryTenantTV.setVerticalScrollBarEnabled(true);
        primaryTenantTV.setMovementMethod(new ScrollingMovementMethod());
        primaryTenantLabelTV = rootView.findViewById(R.id.leaseWizardPrimaryTenantLabelTV);

        secondaryTenantsTV = rootView.findViewById(R.id.leaseWizardSecondaryTenantsTV);
        secondaryTenantsTV.setText(mPage.getData().getString(LeaseWizardPage2.LEASE_SECONDARY_TENANTS_STRING_DATA_KEY));
        secondaryTenantsLabelTV = rootView.findViewById(R.id.leaseWizardSecondaryTenantLabelTV);

        depositAmountLabelTV = rootView.findViewById(R.id.leaseWizardDepositLabelTV);
        depositWithheldLabelTV = rootView.findViewById(R.id.leaseWizardDepositWithheldLabelTV);

        depositAmountET = rootView.findViewById(R.id.leaseWizardDepositET);
        if (mPage.getData().getString(LeaseWizardPage2.LEASE_DEPOSIT_FORMATTED_STRING_DATA_KEY) != null) {
            depositAmountET.setText(mPage.getData().getString(LeaseWizardPage2.LEASE_DEPOSIT_FORMATTED_STRING_DATA_KEY));
        }
        depositAmountET.setSelection(depositAmountET.getText().length());

        depositWithheldET = rootView.findViewById(R.id.leaseWizardDepositWithheldET);
        if (mPage.getData().getString(LeaseWizardPage2.LEASE_DEPOSIT_WITHHELD_FORMATTED_STRING_DATA_KEY) != null) {
            depositWithheldET.setText(mPage.getData().getString(LeaseWizardPage2.LEASE_DEPOSIT_WITHHELD_FORMATTED_STRING_DATA_KEY));
        }
        depositWithheldET.setSelection(depositWithheldET.getText().length());

        addSecondaryTenantBtn = rootView.findViewById(R.id.leaseWizardSecondaryTenantsAddBtn);
        removeSecondaryTenantBtn = rootView.findViewById(R.id.leaseWizardSecondaryTenantsRemoveBtn);

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
        ArrayList<Integer> curTenantIDs = new ArrayList<>();
        if (mPage.getData().getParcelable(LeaseWizardPage2.LEASE_PRIMARY_TENANT_DATA_KEY) != null) {
            primaryTenant = mPage.getData().getParcelable(LeaseWizardPage2.LEASE_PRIMARY_TENANT_DATA_KEY);
            curTenantIDs.add(primaryTenant.getId());
        }
        if (mPage.getData().getParcelableArrayList(LeaseWizardPage2.LEASE_SECONDARY_TENANTS_DATA_KEY) != null) {
            secondaryTenants = mPage.getData().getParcelableArrayList(LeaseWizardPage2.LEASE_SECONDARY_TENANTS_DATA_KEY);
            for (int z = 0; z < secondaryTenants.size(); z++) {
                curTenantIDs.add(secondaryTenants.get(z).getId());
            }
        }

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

        primaryTenantTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TenantOrApartmentChooserDialog dialog = new TenantOrApartmentChooserDialog(getContext(), TenantOrApartmentChooserDialog.TENANT_TYPE, availableTenants);
                dialog.show();
                dialog.setDialogResult(new TenantOrApartmentChooserDialog.OnTenantChooserDialogResult() {
                    @Override
                    public void finish(Tenant tenantResult, Apartment apartmentResult) {
                        if (primaryTenant != null) {
                            availableTenants.add(primaryTenant);
                        }
                        availableTenants.remove(tenantResult);
                        //availableTenants.add(primaryTenant);
                        primaryTenant = tenantResult;
                        setPrimaryTenantTextView();
                        mPage.getData().putString(LeaseWizardPage2.LEASE_PRIMARY_TENANT_STRING_DATA_KEY, primaryTenantTV.getText().toString());
                        mPage.getData().putParcelable(LeaseWizardPage2.LEASE_PRIMARY_TENANT_DATA_KEY, primaryTenant);
                        mPage.notifyDataChanged();
                    }
                });
            }
        });

        addSecondaryTenantBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TenantOrApartmentChooserDialog dialog = new TenantOrApartmentChooserDialog(getContext(), TenantOrApartmentChooserDialog.TENANT_TYPE, availableTenants);
                dialog.show();
                dialog.setDialogResult(new TenantOrApartmentChooserDialog.OnTenantChooserDialogResult() {
                    @Override
                    public void finish(Tenant tenantResult, Apartment apartmentResult) {
                        secondaryTenants.add(tenantResult);
                        availableTenants.remove(tenantResult);
                        setSecondaryTenantsTV();
                        mPage.getData().putString(LeaseWizardPage2.LEASE_SECONDARY_TENANTS_STRING_DATA_KEY, secondaryTenantsTV.getText().toString());
                        mPage.getData().putParcelableArrayList(LeaseWizardPage2.LEASE_SECONDARY_TENANTS_DATA_KEY, secondaryTenants);
                        mPage.notifyDataChanged();
                    }
                });
            }
        });

        removeSecondaryTenantBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!secondaryTenants.isEmpty()) {
                    TenantOrApartmentChooserDialog dialog = new TenantOrApartmentChooserDialog(getContext(), TenantOrApartmentChooserDialog.SECONDARY_TENANT_TYPE, secondaryTenants);
                    dialog.show();
                    dialog.setDialogResult(new TenantOrApartmentChooserDialog.OnTenantChooserDialogResult() {
                        @Override
                        public void finish(Tenant tenantResult, Apartment apartmentResult) {
                            secondaryTenants.remove(tenantResult);
                            availableTenants.add(tenantResult);
                            setSecondaryTenantsTV();
                            mPage.getData().putString(LeaseWizardPage2.LEASE_SECONDARY_TENANTS_STRING_DATA_KEY, secondaryTenantsTV.getText().toString());
                            mPage.getData().putParcelableArrayList(LeaseWizardPage2.LEASE_SECONDARY_TENANTS_DATA_KEY, secondaryTenants);
                            mPage.notifyDataChanged();
                        }
                    });
                }
            }
        });

        depositAmountET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (depositAmountET == null) return;
                String s = editable.toString();
                if (s.isEmpty()) return;
                depositAmountET.removeTextChangedListener(this);
                String cleanString = s.replaceAll("[$,.]", "");
                deposit = new BigDecimal(cleanString).setScale(2, BigDecimal.ROUND_FLOOR).divide(new BigDecimal(100), BigDecimal.ROUND_FLOOR);
                String formatted = NumberFormat.getCurrencyInstance().format(deposit);
                depositAmountET.setText(formatted);
                mPage.getData().putString(LeaseWizardPage2.LEASE_DEPOSIT_FORMATTED_STRING_DATA_KEY, formatted);
                mPage.getData().putString(LeaseWizardPage2.LEASE_DEPOSIT_STRING_DATA_KEY, deposit.toPlainString());
                mPage.notifyDataChanged();
                depositAmountET.setSelection(formatted.length());
                depositAmountET.addTextChangedListener(this);
            }
        });

        depositWithheldET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (depositWithheldET == null) return;
                String s = editable.toString();
                if (s.isEmpty()) return;
                depositWithheldET.removeTextChangedListener(this);
                String cleanString = s.replaceAll("[$,.]", "");
                depositWithheld = new BigDecimal(cleanString).setScale(2, BigDecimal.ROUND_FLOOR).divide(new BigDecimal(100), BigDecimal.ROUND_FLOOR);
                String formatted = NumberFormat.getCurrencyInstance().format(depositWithheld);
                depositWithheldET.setText(formatted);
                mPage.getData().putString(LeaseWizardPage2.LEASE_DEPOSIT_WITHHELD_FORMATTED_STRING_DATA_KEY, formatted);
                mPage.getData().putString(LeaseWizardPage2.LEASE_DEPOSIT_WITHHELD_STRING_DATA_KEY, depositWithheld.toPlainString());
                mPage.notifyDataChanged();
                depositWithheldET.setSelection(formatted.length());
                depositWithheldET.addTextChangedListener(this);
            }
        });

        if (mPage.getData().getString(LeaseWizardPage2.LEASE_DEPOSIT_STRING_DATA_KEY) == null) {
            String formatted = NumberFormat.getCurrencyInstance().format(deposit);
            mPage.getData().putString(LeaseWizardPage2.LEASE_DEPOSIT_FORMATTED_STRING_DATA_KEY, formatted);
            mPage.getData().putString(LeaseWizardPage2.LEASE_DEPOSIT_STRING_DATA_KEY, deposit.toPlainString());
        }
        if (mPage.getData().getString(LeaseWizardPage2.LEASE_DEPOSIT_WITHHELD_STRING_DATA_KEY) == null) {
            String formatted = NumberFormat.getCurrencyInstance().format(depositWithheld);
            mPage.getData().putString(LeaseWizardPage2.LEASE_DEPOSIT_WITHHELD_FORMATTED_STRING_DATA_KEY, formatted);
            mPage.getData().putString(LeaseWizardPage2.LEASE_DEPOSIT_WITHHELD_STRING_DATA_KEY, depositWithheld.toPlainString());
        }
    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);

        // In a future update to the support library, this should override setUserVisibleHint
        // instead of setMenuVisibility.
        if (primaryTenantTV != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            if (!menuVisible) {
                imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
            }
        }
    }

    private void setPrimaryTenantTextView() {
        primaryTenantTV.setText(primaryTenant.getFirstName());
        primaryTenantTV.append(" ");
        primaryTenantTV.append(primaryTenant.getLastName());
    }

    private void setSecondaryTenantsTV() {
        secondaryTenantsTV.setText("");
        for (int i = 0; i < secondaryTenants.size(); i++) {
            secondaryTenantsTV.append(secondaryTenants.get(i).getFirstName() +
                    " " + secondaryTenants.get(i).getLastName() +
                    "\n");
        }
    }
}

