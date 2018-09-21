package com.rentbud.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
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
import com.rentbud.helpers.DateAndCurrencyDisplayer;
import com.rentbud.helpers.MainArrayDataMethods;
import com.rentbud.helpers.TenantApartmentOrLeaseChooserDialog;
import com.rentbud.model.Apartment;
import com.rentbud.model.Lease;
import com.rentbud.sqlite.DatabaseHandler;
import com.rentbud.wizards.LeaseWizardPage2;
import com.rentbud.model.Tenant;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;

public class LeaseWizardPage2Fragment extends android.support.v4.app.Fragment {
    private static final String ARG_KEY = "key";

    private PageFragmentCallbacks mCallbacks;
    private String mKey;
    private LeaseWizardPage2 mPage;
    private TextView primaryTenantTV, primaryTenantLabelTV, secondaryTenantsTV, secondaryTenantsLabelTV, depositAmountLabelTV, depositWithheldLabelTV, depositHeaderTV;
    private EditText depositAmountET, depositWithheldET;
    private Tenant primaryTenant;
    private ArrayList<Tenant> secondaryTenants, availableTenants;
    private BigDecimal deposit;
    private Button addSecondaryTenantBtn, removeSecondaryTenantBtn;
    private DatabaseHandler db;
    private MainArrayDataMethods mainArrayDataMethods;
    private boolean isEdit;
    private TenantApartmentOrLeaseChooserDialog tenantApartmentOrLeaseChooserDialog;
    private SharedPreferences preferences;
    private int moneyFormatCode;

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
        db = new DatabaseHandler(getActivity());
        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        this.moneyFormatCode = preferences.getInt("currency", DateAndCurrencyDisplayer.CURRENCY_US);
        mainArrayDataMethods = new MainArrayDataMethods();
        //availableTenants.addAll(MainActivity.tenantList);
        //depositWithheld = new BigDecimal(0);
        isEdit = false;
        Bundle extras = mPage.getData();
        if (extras != null) {
            Lease leaseToEdit = extras.getParcelable("leaseToEdit");
            if (leaseToEdit != null) {
                loadDataForEdit(leaseToEdit);
                isEdit = true;
            } else {
                preloadData(extras);
            }
        } else {
            primaryTenant = null;
            deposit = new BigDecimal(0);
            String formatted = DateAndCurrencyDisplayer.getCurrencyToDisplay(moneyFormatCode, deposit);
            mPage.getData().putString(LeaseWizardPage2.LEASE_DEPOSIT_FORMATTED_STRING_DATA_KEY, formatted);
            mPage.getData().putString(LeaseWizardPage2.LEASE_DEPOSIT_STRING_DATA_KEY, deposit.toPlainString());
        }

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
        //primaryTenantLabelTV = rootView.findViewById(R.id.leaseWizardPrimaryTenantLabelTV);

        secondaryTenantsTV = rootView.findViewById(R.id.leaseWizardSecondaryTenantsTV);
        secondaryTenantsTV.setText(mPage.getData().getString(LeaseWizardPage2.LEASE_SECONDARY_TENANTS_STRING_DATA_KEY));
        //secondaryTenantsLabelTV = rootView.findViewById(R.id.leaseWizardSecondaryTenantLabelTV);

        //depositAmountLabelTV = rootView.findViewById(R.id.leaseWizardDepositLabelTV);
        //depositWithheldLabelTV = rootView.findViewById(R.id.leaseWizardDepositWithheldLabelTV);
        //depositWithheldLabelTV.setVisibility(View.GONE);

        depositAmountET = rootView.findViewById(R.id.leaseWizardDepositET);
        if (mPage.getData().getString(LeaseWizardPage2.LEASE_DEPOSIT_FORMATTED_STRING_DATA_KEY) != null) {
            depositAmountET.setText(mPage.getData().getString(LeaseWizardPage2.LEASE_DEPOSIT_FORMATTED_STRING_DATA_KEY));
        }
        depositAmountET.setSelection(depositAmountET.getText().length());
        depositHeaderTV = rootView.findViewById(R.id.leaseWizardDepositHeaderTV);
        if (isEdit) {
//            depositAmountLabelTV.setVisibility(View.GONE);
            depositAmountET.setVisibility(View.GONE);
            depositHeaderTV.setVisibility(View.GONE);
        }

        depositWithheldET = rootView.findViewById(R.id.leaseWizardDepositWithheldET);
        depositWithheldET.setVisibility(View.GONE);
        //if (mPage.getData().getString(LeaseWizardPage2.LEASE_DEPOSIT_WITHHELD_FORMATTED_STRING_DATA_KEY) != null) {
        //    depositWithheldET.setText(mPage.getData().getString(LeaseWizardPage2.LEASE_DEPOSIT_WITHHELD_FORMATTED_STRING_DATA_KEY));
        //}
        //depositWithheldET.setSelection(depositWithheldET.getText().length());

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
                for (int y = 0; y < curTenantIDs.size(); y++) {
                    if (MainActivity.tenantList.get(i).getId() == curTenantIDs.get(y)) {
                        isUsed = true;
                        break;
                    }
                }
                if (!isUsed) {
                    availableTenants.add(MainActivity.tenantList.get(i));
                }
                //    }
            }
        }

        primaryTenantTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tenantApartmentOrLeaseChooserDialog = new TenantApartmentOrLeaseChooserDialog(getContext(), TenantApartmentOrLeaseChooserDialog.TENANT_TYPE, availableTenants);
                tenantApartmentOrLeaseChooserDialog.show();
                tenantApartmentOrLeaseChooserDialog.changeCancelBtnText(getContext().getResources().getString(R.string.clear));
                tenantApartmentOrLeaseChooserDialog.setDialogResult(new TenantApartmentOrLeaseChooserDialog.OnTenantChooserDialogResult() {
                    @Override
                    public void finish(Tenant tenantResult, Apartment apartmentResult, Lease leaseResult) {
                        if (primaryTenant != null) {
                            availableTenants.add(primaryTenant);
                        }
                        if (tenantResult != null) {
                            availableTenants.remove(tenantResult);
                            //availableTenants.add(primaryTenant);
                            primaryTenant = tenantResult;
                            primaryTenantTV.setText(getTenantString());
                            mPage.getData().putString(LeaseWizardPage2.LEASE_PRIMARY_TENANT_STRING_DATA_KEY, primaryTenantTV.getText().toString());
                            mPage.getData().putParcelable(LeaseWizardPage2.LEASE_PRIMARY_TENANT_DATA_KEY, primaryTenant);
                        } else {
                            primaryTenant = null;
                            primaryTenantTV.setText("");
                            mPage.getData().putString(LeaseWizardPage2.LEASE_PRIMARY_TENANT_STRING_DATA_KEY, "");
                            mPage.getData().putParcelable(LeaseWizardPage2.LEASE_PRIMARY_TENANT_DATA_KEY, null);
                        }
                        mainArrayDataMethods.sortTenantArrayAlphabetically(availableTenants);
                        mPage.notifyDataChanged();
                    }
                });
            }
        });

        addSecondaryTenantBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tenantApartmentOrLeaseChooserDialog = new TenantApartmentOrLeaseChooserDialog(getContext(), TenantApartmentOrLeaseChooserDialog.TENANT_TYPE, availableTenants);
                tenantApartmentOrLeaseChooserDialog.show();
                tenantApartmentOrLeaseChooserDialog.setDialogResult(new TenantApartmentOrLeaseChooserDialog.OnTenantChooserDialogResult() {
                    @Override
                    public void finish(Tenant tenantResult, Apartment apartmentResult, Lease leaseResult) {
                        if (tenantResult != null) {
                            secondaryTenants.add(tenantResult);
                            availableTenants.remove(tenantResult);
                            secondaryTenantsTV.setText(getSecondaryTenantsString());
                            mPage.getData().putString(LeaseWizardPage2.LEASE_SECONDARY_TENANTS_STRING_DATA_KEY, secondaryTenantsTV.getText().toString());
                            mPage.getData().putParcelableArrayList(LeaseWizardPage2.LEASE_SECONDARY_TENANTS_DATA_KEY, secondaryTenants);
                            mPage.notifyDataChanged();
                            //mainArrayDataMethods.sortTenantArrayAlphabetically(availableTenants);
                        }
                    }
                });
            }
        });

        removeSecondaryTenantBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!secondaryTenants.isEmpty()) {
                    tenantApartmentOrLeaseChooserDialog = new TenantApartmentOrLeaseChooserDialog(getContext(), TenantApartmentOrLeaseChooserDialog.SECONDARY_TENANT_TYPE, secondaryTenants);
                    tenantApartmentOrLeaseChooserDialog.show();
                    tenantApartmentOrLeaseChooserDialog.setDialogResult(new TenantApartmentOrLeaseChooserDialog.OnTenantChooserDialogResult() {
                        @Override
                        public void finish(Tenant tenantResult, Apartment apartmentResult, Lease leaseResult) {
                            if (tenantResult != null) {
                                secondaryTenants.remove(tenantResult);
                                availableTenants.add(tenantResult);
                                secondaryTenantsTV.setText(getSecondaryTenantsString());
                                mPage.getData().putString(LeaseWizardPage2.LEASE_SECONDARY_TENANTS_STRING_DATA_KEY, secondaryTenantsTV.getText().toString());
                                mPage.getData().putParcelableArrayList(LeaseWizardPage2.LEASE_SECONDARY_TENANTS_DATA_KEY, secondaryTenants);
                                mPage.notifyDataChanged();
                                mainArrayDataMethods.sortTenantArrayAlphabetically(availableTenants);
                            }
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
                String cleanString = DateAndCurrencyDisplayer.cleanMoneyString(s);
                deposit = new BigDecimal(cleanString).setScale(2, BigDecimal.ROUND_FLOOR).divide(new BigDecimal(100), BigDecimal.ROUND_FLOOR);
                String formatted = DateAndCurrencyDisplayer.getCurrencyToDisplay(moneyFormatCode, deposit);
                depositAmountET.setText(formatted);
                depositAmountET.setSelection(DateAndCurrencyDisplayer.getEndCursorPositionForMoneyInput(depositAmountET.getText().length(), moneyFormatCode));
                mPage.getData().putString(LeaseWizardPage2.LEASE_DEPOSIT_FORMATTED_STRING_DATA_KEY, formatted);
                mPage.getData().putString(LeaseWizardPage2.LEASE_DEPOSIT_STRING_DATA_KEY, deposit.toPlainString());
                mPage.notifyDataChanged();
                //depositAmountET.setSelection(formatted.length());
                depositAmountET.addTextChangedListener(this);
            }
        });
        depositAmountET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                depositAmountET.setSelection(DateAndCurrencyDisplayer.getEndCursorPositionForMoneyInput(depositAmountET.getText().length(), moneyFormatCode));
            }
        });

        //depositWithheldET.addTextChangedListener(new TextWatcher() {
        //    @Override
        //    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        //    }

        //    @Override
        //    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        //    }

        //    @Override
        //    public void afterTextChanged(Editable editable) {
        //        if (depositWithheldET == null) return;
        //        String s = editable.toString();
        //        if (s.isEmpty()) return;
        //        depositWithheldET.removeTextChangedListener(this);
        //        String cleanString = s.replaceAll("[$,.]", "");
        //        depositWithheld = new BigDecimal(cleanString).setScale(2, BigDecimal.ROUND_FLOOR).divide(new BigDecimal(100), BigDecimal.ROUND_FLOOR);
        //        String formatted = NumberFormat.getCurrencyInstance().format(depositWithheld);
        //        depositWithheldET.setText(formatted);
        //        mPage.getData().putString(LeaseWizardPage2.LEASE_DEPOSIT_WITHHELD_FORMATTED_STRING_DATA_KEY, formatted);
        //        mPage.getData().putString(LeaseWizardPage2.LEASE_DEPOSIT_WITHHELD_STRING_DATA_KEY, depositWithheld.toPlainString());
        //        mPage.notifyDataChanged();
        //        depositWithheldET.setSelection(formatted.length());
        //        depositWithheldET.addTextChangedListener(this);
        //    }
        //});

        if (mPage.getData().getString(LeaseWizardPage2.LEASE_DEPOSIT_STRING_DATA_KEY) == null) {
            String formatted = NumberFormat.getCurrencyInstance().format(deposit);
            mPage.getData().putString(LeaseWizardPage2.LEASE_DEPOSIT_FORMATTED_STRING_DATA_KEY, formatted);
            mPage.getData().putString(LeaseWizardPage2.LEASE_DEPOSIT_STRING_DATA_KEY, deposit.toPlainString());
        }
        //if (mPage.getData().getString(LeaseWizardPage2.LEASE_DEPOSIT_WITHHELD_STRING_DATA_KEY) == null) {
        //    String formatted = NumberFormat.getCurrencyInstance().format(depositWithheld);
        //    mPage.getData().putString(LeaseWizardPage2.LEASE_DEPOSIT_WITHHELD_FORMATTED_STRING_DATA_KEY, formatted);
        //    mPage.getData().putString(LeaseWizardPage2.LEASE_DEPOSIT_WITHHELD_STRING_DATA_KEY, depositWithheld.toPlainString());
        //}
        //if (isEdit) {
        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                mPage.notifyDataChanged();
            }
        });
        //}
        mainArrayDataMethods.sortTenantArrayAlphabetically(availableTenants);
        //mPage.notifyDataChanged();
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

    private String getTenantString() {
        if (primaryTenant != null) {
            StringBuilder builder = new StringBuilder(primaryTenant.getFirstName());
            builder.append(" ");
            builder.append(primaryTenant.getLastName());
            return builder.toString();
        } else {
            return "";
        }
    }

    private String getSecondaryTenantsString() {
        if (secondaryTenants != null) {
            StringBuilder builder = new StringBuilder("");
            for (int i = 0; i < secondaryTenants.size(); i++) {
                builder.append(secondaryTenants.get(i).getFirstName());
                builder.append(" ");
                builder.append(secondaryTenants.get(i).getLastName());
                if(i < secondaryTenants.size() - 1) {
                    builder.append("\n");
                }
            }
            return builder.toString();
        } else {
            return "";
        }
    }

    private void preloadPrimaryTenant(Bundle bundle) {
        if (mPage.getData().getParcelable(LeaseWizardPage2.LEASE_PRIMARY_TENANT_DATA_KEY) != null) {
            primaryTenant = mPage.getData().getParcelable(LeaseWizardPage2.LEASE_PRIMARY_TENANT_DATA_KEY);
        } else if (bundle.getParcelable("preloadedPrimaryTenant") != null) {
            primaryTenant = bundle.getParcelable("preloadedPrimaryTenant");
            mPage.getData().putString(LeaseWizardPage2.LEASE_PRIMARY_TENANT_STRING_DATA_KEY, getTenantString());
            mPage.getData().putParcelable(LeaseWizardPage2.LEASE_PRIMARY_TENANT_DATA_KEY, primaryTenant);
        } else if (bundle.getInt("preloadedPrimaryTenantID") != 0) {
            primaryTenant = db.getTenantByID(bundle.getInt("preloadedPrimaryTenantID"), MainActivity.user);
            //mPage.getData().putInt(IncomeWizardPage3.INCOME_RELATED_APT_ID_DATA_KEY, apartment.getId());
            mPage.getData().putString(LeaseWizardPage2.LEASE_PRIMARY_TENANT_STRING_DATA_KEY, getTenantString());
            mPage.getData().putParcelable(LeaseWizardPage2.LEASE_PRIMARY_TENANT_DATA_KEY, primaryTenant);
        } else {
            primaryTenant = null;
        }
    }

    private void preloadSecondaryTenant(Bundle bundle) {
        if (mPage.getData().getParcelableArrayList(LeaseWizardPage2.LEASE_SECONDARY_TENANTS_DATA_KEY) != null) {
            secondaryTenants = mPage.getData().getParcelableArrayList(LeaseWizardPage2.LEASE_SECONDARY_TENANTS_DATA_KEY);
        } else if (bundle.getParcelable("preloadedSecondaryTenant") != null) {
            Tenant tenant = bundle.getParcelable("preloadedSecondaryTenant");
            secondaryTenants.add(tenant);
            mPage.getData().putParcelableArrayList(LeaseWizardPage2.LEASE_SECONDARY_TENANTS_DATA_KEY, secondaryTenants);
            mPage.getData().putString(LeaseWizardPage2.LEASE_SECONDARY_TENANTS_STRING_DATA_KEY, getSecondaryTenantsString());
        } else if (bundle.getInt("preloadedSecondaryTenantID") != 0) {
            Tenant tenant = db.getTenantByID(bundle.getInt("preloadedSecondaryTenantID"), MainActivity.user);
            secondaryTenants.add(tenant);
            mPage.getData().putParcelableArrayList(LeaseWizardPage2.LEASE_SECONDARY_TENANTS_DATA_KEY, secondaryTenants);
            mPage.getData().putString(LeaseWizardPage2.LEASE_SECONDARY_TENANTS_STRING_DATA_KEY, getSecondaryTenantsString());
        }
    }

    private void preloadData(Bundle bundle) {
        preloadPrimaryTenant(bundle);
        preloadSecondaryTenant(bundle);
        if (mPage.getData().getString(LeaseWizardPage2.LEASE_DEPOSIT_STRING_DATA_KEY) != null) {
            deposit = new BigDecimal(mPage.getData().getString(LeaseWizardPage2.LEASE_DEPOSIT_STRING_DATA_KEY)).setScale(2, BigDecimal.ROUND_FLOOR).divide(new BigDecimal(100), BigDecimal.ROUND_FLOOR);
        } else {
            deposit = new BigDecimal(0);
            String formatted = DateAndCurrencyDisplayer.getCurrencyToDisplay(moneyFormatCode, deposit);
            mPage.getData().putString(LeaseWizardPage2.LEASE_DEPOSIT_FORMATTED_STRING_DATA_KEY, formatted);
            mPage.getData().putString(LeaseWizardPage2.LEASE_DEPOSIT_STRING_DATA_KEY, deposit.toPlainString());
        }
    }

    private void loadDataForEdit(Lease leaseToEdit) {
        if (!mPage.getData().getBoolean(LeaseWizardPage2.WAS_PRELOADED)) {
            //Primary tenant
            if (leaseToEdit.getPrimaryTenantID() != 0) {
                primaryTenant = db.getTenantByID(leaseToEdit.getPrimaryTenantID(), MainActivity.user);
                if (primaryTenant != null) {
                    //mPage.getData().putInt(IncomeWizardPage3.INCOME_RELATED_APT_ID_DATA_KEY, apartment.getId());
                    mPage.getData().putString(LeaseWizardPage2.LEASE_PRIMARY_TENANT_STRING_DATA_KEY, getTenantString());
                    mPage.getData().putParcelable(LeaseWizardPage2.LEASE_PRIMARY_TENANT_DATA_KEY, primaryTenant);
                }
            }
            //Secondary tenants
            if (leaseToEdit.getSecondaryTenantIDs() != null) {
                ArrayList<Integer> secondaryTenantIDs = leaseToEdit.getSecondaryTenantIDs();
                for (int i = 0; i < secondaryTenantIDs.size(); i++) {
                    Tenant secondaryTenant = db.getTenantByID(secondaryTenantIDs.get(i), MainActivity.user);
                    secondaryTenants.add(secondaryTenant);
                }
                mPage.getData().putParcelableArrayList(LeaseWizardPage2.LEASE_SECONDARY_TENANTS_DATA_KEY, secondaryTenants);
                mPage.getData().putString(LeaseWizardPage2.LEASE_SECONDARY_TENANTS_STRING_DATA_KEY, getSecondaryTenantsString());
            }
            //Deposit
            deposit = new BigDecimal(0);
            String formatted = DateAndCurrencyDisplayer.getCurrencyToDisplay(moneyFormatCode, deposit);
            mPage.getData().putString(LeaseWizardPage2.LEASE_DEPOSIT_FORMATTED_STRING_DATA_KEY, formatted);
            mPage.getData().putString(LeaseWizardPage2.LEASE_DEPOSIT_STRING_DATA_KEY, deposit.toPlainString());
            mPage.getData().putBoolean(LeaseWizardPage2.WAS_PRELOADED, true);
        } else {
            preloadData(mPage.getData());
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(tenantApartmentOrLeaseChooserDialog != null){
            tenantApartmentOrLeaseChooserDialog.dismiss();
        }
    }
}

