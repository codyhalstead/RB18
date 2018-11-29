package com.rba18.fragments;

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
import com.rba18.R;
import com.rba18.activities.MainActivity;
import com.rba18.helpers.DateAndCurrencyDisplayer;
import com.rba18.helpers.MainArrayDataMethods;
import com.rba18.helpers.TenantApartmentOrLeaseChooserDialog;
import com.rba18.model.Apartment;
import com.rba18.model.Lease;
import com.rba18.sqlite.DatabaseHandler;
import com.rba18.wizards.LeaseWizardPage2;
import com.rba18.model.Tenant;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;

public class LeaseWizardPage2Fragment extends android.support.v4.app.Fragment {
    private static final String ARG_KEY = "key";
    private PageFragmentCallbacks mCallbacks;
    private String mKey;
    private LeaseWizardPage2 mPage;
    private TextView mPrimaryTenantTV, mSecondaryTenantsTV;
    private EditText mDepositAmountET;
    private Tenant mPrimaryTenant;
    private ArrayList<Tenant> mSecondaryTenants, mAvailableTenants;
    private BigDecimal mDeposit;
    private Button mAddSecondaryTenantBtn, mRemoveSecondaryTenantBtn;
    private DatabaseHandler mDB;
    private MainArrayDataMethods mMainArrayDataMethods;
    private boolean mIsEdit;
    private TenantApartmentOrLeaseChooserDialog mTenantApartmentOrLeaseChooserDialog;
    private int mMoneyFormatCode;

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
        mSecondaryTenants = new ArrayList<>();
        mAvailableTenants = new ArrayList<>();
        mDB = new DatabaseHandler(getActivity());
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        mMoneyFormatCode = preferences.getInt("currency", DateAndCurrencyDisplayer.CURRENCY_US);
        mMainArrayDataMethods = new MainArrayDataMethods();
        mIsEdit = false;
        Bundle extras = mPage.getData();
        if (extras != null) {
            Lease leaseToEdit = extras.getParcelable("leaseToEdit");
            if (leaseToEdit != null) {
                loadDataForEdit(leaseToEdit);
                mIsEdit = true;
            } else {
                preloadData(extras);
            }
        } else {
            mPrimaryTenant = null;
            mDeposit = new BigDecimal(0);
            String formatted = DateAndCurrencyDisplayer.getCurrencyToDisplay(mMoneyFormatCode, mDeposit);
            mPage.getData().putString(LeaseWizardPage2.LEASE_DEPOSIT_FORMATTED_STRING_DATA_KEY, formatted);
            mPage.getData().putString(LeaseWizardPage2.LEASE_DEPOSIT_STRING_DATA_KEY, mDeposit.toPlainString());
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_lease_wizard_page_2, container, false);
        (rootView.findViewById(android.R.id.title)).setVisibility(View.GONE);
        mPrimaryTenantTV = rootView.findViewById(R.id.leaseWizardPrimaryTenantTV);
        mPrimaryTenantTV.setText(mPage.getData().getString(LeaseWizardPage2.LEASE_PRIMARY_TENANT_STRING_DATA_KEY));
        mPrimaryTenantTV.setScroller(new Scroller(getContext()));
        mPrimaryTenantTV.setMaxLines(5);
        mPrimaryTenantTV.setVerticalScrollBarEnabled(true);
        mPrimaryTenantTV.setMovementMethod(new ScrollingMovementMethod());

        mSecondaryTenantsTV = rootView.findViewById(R.id.leaseWizardSecondaryTenantsTV);
        mSecondaryTenantsTV.setText(mPage.getData().getString(LeaseWizardPage2.LEASE_SECONDARY_TENANTS_STRING_DATA_KEY));
        mDepositAmountET = rootView.findViewById(R.id.leaseWizardDepositET);
        if (mPage.getData().getString(LeaseWizardPage2.LEASE_DEPOSIT_FORMATTED_STRING_DATA_KEY) != null) {
            mDepositAmountET.setText(mPage.getData().getString(LeaseWizardPage2.LEASE_DEPOSIT_FORMATTED_STRING_DATA_KEY));
        }
        mDepositAmountET.setSelection(mDepositAmountET.getText().length());
        TextView depositHeaderTV = rootView.findViewById(R.id.leaseWizardDepositHeaderTV);
        if (mIsEdit) {
            mDepositAmountET.setVisibility(View.GONE);
            depositHeaderTV.setVisibility(View.GONE);
        }

        EditText depositWithheldET = rootView.findViewById(R.id.leaseWizardDepositWithheldET);
        depositWithheldET.setVisibility(View.GONE);

        mAddSecondaryTenantBtn = rootView.findViewById(R.id.leaseWizardSecondaryTenantsAddBtn);
        mRemoveSecondaryTenantBtn = rootView.findViewById(R.id.leaseWizardSecondaryTenantsRemoveBtn);

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
            mPrimaryTenant = mPage.getData().getParcelable(LeaseWizardPage2.LEASE_PRIMARY_TENANT_DATA_KEY);
            curTenantIDs.add(mPrimaryTenant.getId());
        }
        if (mPage.getData().getParcelableArrayList(LeaseWizardPage2.LEASE_SECONDARY_TENANTS_DATA_KEY) != null) {
            mSecondaryTenants = mPage.getData().getParcelableArrayList(LeaseWizardPage2.LEASE_SECONDARY_TENANTS_DATA_KEY);
            for (int z = 0; z < mSecondaryTenants.size(); z++) {
                curTenantIDs.add(mSecondaryTenants.get(z).getId());
            }
        }

        for (int i = 0; i < MainActivity.sTenantList.size(); i++) {
            if (MainActivity.sTenantList.get(i).isActive()) {
                boolean isUsed = false;
                for (int y = 0; y < curTenantIDs.size(); y++) {
                    if (MainActivity.sTenantList.get(i).getId() == curTenantIDs.get(y)) {
                        isUsed = true;
                        break;
                    }
                }
                if (!isUsed) {
                    mAvailableTenants.add(MainActivity.sTenantList.get(i));
                }
            }
        }

        mPrimaryTenantTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTenantApartmentOrLeaseChooserDialog = new TenantApartmentOrLeaseChooserDialog(getContext(), TenantApartmentOrLeaseChooserDialog.TENANT_TYPE, mAvailableTenants);
                mTenantApartmentOrLeaseChooserDialog.show();
                mTenantApartmentOrLeaseChooserDialog.changeCancelBtnText(getContext().getResources().getString(R.string.clear));
                mTenantApartmentOrLeaseChooserDialog.setDialogResult(new TenantApartmentOrLeaseChooserDialog.OnTenantChooserDialogResult() {
                    @Override
                    public void finish(Tenant tenantResult, Apartment apartmentResult, Lease leaseResult) {
                        if (mPrimaryTenant != null) {
                            mAvailableTenants.add(mPrimaryTenant);
                        }
                        if (tenantResult != null) {
                            mAvailableTenants.remove(tenantResult);
                            mPrimaryTenant = tenantResult;
                            mPrimaryTenantTV.setText(getTenantString());
                            mPage.getData().putString(LeaseWizardPage2.LEASE_PRIMARY_TENANT_STRING_DATA_KEY, mPrimaryTenantTV.getText().toString());
                            mPage.getData().putParcelable(LeaseWizardPage2.LEASE_PRIMARY_TENANT_DATA_KEY, mPrimaryTenant);
                        } else {
                            mPrimaryTenant = null;
                            mPrimaryTenantTV.setText("");
                            mPage.getData().putString(LeaseWizardPage2.LEASE_PRIMARY_TENANT_STRING_DATA_KEY, "");
                            mPage.getData().putParcelable(LeaseWizardPage2.LEASE_PRIMARY_TENANT_DATA_KEY, null);
                        }
                        mMainArrayDataMethods.sortTenantArrayAlphabetically(mAvailableTenants);
                        mPage.notifyDataChanged();
                    }
                });
            }
        });

        mAddSecondaryTenantBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTenantApartmentOrLeaseChooserDialog = new TenantApartmentOrLeaseChooserDialog(getContext(), TenantApartmentOrLeaseChooserDialog.TENANT_TYPE, mAvailableTenants);
                mTenantApartmentOrLeaseChooserDialog.show();
                mTenantApartmentOrLeaseChooserDialog.setDialogResult(new TenantApartmentOrLeaseChooserDialog.OnTenantChooserDialogResult() {
                    @Override
                    public void finish(Tenant tenantResult, Apartment apartmentResult, Lease leaseResult) {
                        if (tenantResult != null) {
                            mSecondaryTenants.add(tenantResult);
                            mAvailableTenants.remove(tenantResult);
                            mSecondaryTenantsTV.setText(getSecondaryTenantsString());
                            mPage.getData().putString(LeaseWizardPage2.LEASE_SECONDARY_TENANTS_STRING_DATA_KEY, mSecondaryTenantsTV.getText().toString());
                            mPage.getData().putParcelableArrayList(LeaseWizardPage2.LEASE_SECONDARY_TENANTS_DATA_KEY, mSecondaryTenants);
                            mPage.notifyDataChanged();
                            //mMainArrayDataMethods.sortTenantArrayAlphabetically(mAvailableTenants);
                        }
                    }
                });
            }
        });

        mRemoveSecondaryTenantBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mSecondaryTenants.isEmpty()) {
                    mTenantApartmentOrLeaseChooserDialog = new TenantApartmentOrLeaseChooserDialog(getContext(), TenantApartmentOrLeaseChooserDialog.SECONDARY_TENANT_TYPE, mSecondaryTenants);
                    mTenantApartmentOrLeaseChooserDialog.show();
                    mTenantApartmentOrLeaseChooserDialog.setDialogResult(new TenantApartmentOrLeaseChooserDialog.OnTenantChooserDialogResult() {
                        @Override
                        public void finish(Tenant tenantResult, Apartment apartmentResult, Lease leaseResult) {
                            if (tenantResult != null) {
                                mSecondaryTenants.remove(tenantResult);
                                mAvailableTenants.add(tenantResult);
                                mSecondaryTenantsTV.setText(getSecondaryTenantsString());
                                mPage.getData().putString(LeaseWizardPage2.LEASE_SECONDARY_TENANTS_STRING_DATA_KEY, mSecondaryTenantsTV.getText().toString());
                                mPage.getData().putParcelableArrayList(LeaseWizardPage2.LEASE_SECONDARY_TENANTS_DATA_KEY, mSecondaryTenants);
                                mPage.notifyDataChanged();
                                mMainArrayDataMethods.sortTenantArrayAlphabetically(mAvailableTenants);
                            }
                        }
                    });
                }
            }
        });

        mDepositAmountET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (mDepositAmountET == null) return;
                String s = editable.toString();
                if (s.isEmpty()) return;
                mDepositAmountET.removeTextChangedListener(this);
                String cleanString = DateAndCurrencyDisplayer.cleanMoneyString(s);
                mDeposit = new BigDecimal(cleanString).setScale(2, BigDecimal.ROUND_FLOOR).divide(new BigDecimal(100), BigDecimal.ROUND_FLOOR);
                String formatted = DateAndCurrencyDisplayer.getCurrencyToDisplay(mMoneyFormatCode, mDeposit);
                mDepositAmountET.setText(formatted);
                mDepositAmountET.setSelection(DateAndCurrencyDisplayer.getEndCursorPositionForMoneyInput(mDepositAmountET.getText().length(), mMoneyFormatCode));
                mPage.getData().putString(LeaseWizardPage2.LEASE_DEPOSIT_FORMATTED_STRING_DATA_KEY, formatted);
                mPage.getData().putString(LeaseWizardPage2.LEASE_DEPOSIT_STRING_DATA_KEY, mDeposit.toPlainString());
                mPage.notifyDataChanged();
                mDepositAmountET.addTextChangedListener(this);
            }
        });
        mDepositAmountET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDepositAmountET.setSelection(DateAndCurrencyDisplayer.getEndCursorPositionForMoneyInput(mDepositAmountET.getText().length(), mMoneyFormatCode));
            }
        });

        if (mPage.getData().getString(LeaseWizardPage2.LEASE_DEPOSIT_STRING_DATA_KEY) == null) {
            String formatted = NumberFormat.getCurrencyInstance().format(mDeposit);
            mPage.getData().putString(LeaseWizardPage2.LEASE_DEPOSIT_FORMATTED_STRING_DATA_KEY, formatted);
            mPage.getData().putString(LeaseWizardPage2.LEASE_DEPOSIT_STRING_DATA_KEY, mDeposit.toPlainString());
        }
        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                mPage.notifyDataChanged();
            }
        });
        mMainArrayDataMethods.sortTenantArrayAlphabetically(mAvailableTenants);
    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);

        // In a future update to the support library, this should override setUserVisibleHint
        // instead of setMenuVisibility.
        if (mPrimaryTenantTV != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            if (!menuVisible) {
                imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
            }
        }
    }

    private String getTenantString() {
        if (mPrimaryTenant != null) {
            StringBuilder builder = new StringBuilder(mPrimaryTenant.getFirstName());
            builder.append(" ");
            builder.append(mPrimaryTenant.getLastName());
            return builder.toString();
        } else {
            return "";
        }
    }

    private String getSecondaryTenantsString() {
        if (mSecondaryTenants != null) {
            StringBuilder builder = new StringBuilder("");
            for (int i = 0; i < mSecondaryTenants.size(); i++) {
                builder.append(mSecondaryTenants.get(i).getFirstName());
                builder.append(" ");
                builder.append(mSecondaryTenants.get(i).getLastName());
                if(i < mSecondaryTenants.size() - 1) {
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
            mPrimaryTenant = mPage.getData().getParcelable(LeaseWizardPage2.LEASE_PRIMARY_TENANT_DATA_KEY);
        } else if (bundle.getParcelable("preloadedPrimaryTenant") != null) {
            mPrimaryTenant = bundle.getParcelable("preloadedPrimaryTenant");
            mPage.getData().putString(LeaseWizardPage2.LEASE_PRIMARY_TENANT_STRING_DATA_KEY, getTenantString());
            mPage.getData().putParcelable(LeaseWizardPage2.LEASE_PRIMARY_TENANT_DATA_KEY, mPrimaryTenant);
        } else if (bundle.getInt("preloadedPrimaryTenantID") != 0) {
            mPrimaryTenant = mDB.getTenantByID(bundle.getInt("preloadedPrimaryTenantID"), MainActivity.sUser);
            //mPage.getData().putInt(IncomeWizardPage3.INCOME_RELATED_APT_ID_DATA_KEY, apartment.getId());
            mPage.getData().putString(LeaseWizardPage2.LEASE_PRIMARY_TENANT_STRING_DATA_KEY, getTenantString());
            mPage.getData().putParcelable(LeaseWizardPage2.LEASE_PRIMARY_TENANT_DATA_KEY, mPrimaryTenant);
        } else {
            mPrimaryTenant = null;
        }
    }

    private void preloadSecondaryTenant(Bundle bundle) {
        if (mPage.getData().getParcelableArrayList(LeaseWizardPage2.LEASE_SECONDARY_TENANTS_DATA_KEY) != null) {
            mSecondaryTenants = mPage.getData().getParcelableArrayList(LeaseWizardPage2.LEASE_SECONDARY_TENANTS_DATA_KEY);
        } else if (bundle.getParcelable("preloadedSecondaryTenant") != null) {
            Tenant tenant = bundle.getParcelable("preloadedSecondaryTenant");
            mSecondaryTenants.add(tenant);
            mPage.getData().putParcelableArrayList(LeaseWizardPage2.LEASE_SECONDARY_TENANTS_DATA_KEY, mSecondaryTenants);
            mPage.getData().putString(LeaseWizardPage2.LEASE_SECONDARY_TENANTS_STRING_DATA_KEY, getSecondaryTenantsString());
        } else if (bundle.getInt("preloadedSecondaryTenantID") != 0) {
            Tenant tenant = mDB.getTenantByID(bundle.getInt("preloadedSecondaryTenantID"), MainActivity.sUser);
            mSecondaryTenants.add(tenant);
            mPage.getData().putParcelableArrayList(LeaseWizardPage2.LEASE_SECONDARY_TENANTS_DATA_KEY, mSecondaryTenants);
            mPage.getData().putString(LeaseWizardPage2.LEASE_SECONDARY_TENANTS_STRING_DATA_KEY, getSecondaryTenantsString());
        }
    }

    private void preloadData(Bundle bundle) {
        preloadPrimaryTenant(bundle);
        preloadSecondaryTenant(bundle);
        if (mPage.getData().getString(LeaseWizardPage2.LEASE_DEPOSIT_STRING_DATA_KEY) != null) {
            mDeposit = new BigDecimal(mPage.getData().getString(LeaseWizardPage2.LEASE_DEPOSIT_STRING_DATA_KEY)).setScale(2, BigDecimal.ROUND_FLOOR).divide(new BigDecimal(100), BigDecimal.ROUND_FLOOR);
        } else {
            mDeposit = new BigDecimal(0);
            String formatted = DateAndCurrencyDisplayer.getCurrencyToDisplay(mMoneyFormatCode, mDeposit);
            mPage.getData().putString(LeaseWizardPage2.LEASE_DEPOSIT_FORMATTED_STRING_DATA_KEY, formatted);
            mPage.getData().putString(LeaseWizardPage2.LEASE_DEPOSIT_STRING_DATA_KEY, mDeposit.toPlainString());
        }
    }

    private void loadDataForEdit(Lease leaseToEdit) {
        if (!mPage.getData().getBoolean(LeaseWizardPage2.WAS_PRELOADED)) {
            //Primary tenant
            if (leaseToEdit.getPrimaryTenantID() != 0) {
                mPrimaryTenant = mDB.getTenantByID(leaseToEdit.getPrimaryTenantID(), MainActivity.sUser);
                if (mPrimaryTenant != null) {
                    //mPage.getData().putInt(IncomeWizardPage3.INCOME_RELATED_APT_ID_DATA_KEY, apartment.getId());
                    mPage.getData().putString(LeaseWizardPage2.LEASE_PRIMARY_TENANT_STRING_DATA_KEY, getTenantString());
                    mPage.getData().putParcelable(LeaseWizardPage2.LEASE_PRIMARY_TENANT_DATA_KEY, mPrimaryTenant);
                }
            }
            //Secondary tenants
            if (leaseToEdit.getSecondaryTenantIDs() != null) {
                ArrayList<Integer> secondaryTenantIDs = leaseToEdit.getSecondaryTenantIDs();
                for (int i = 0; i < secondaryTenantIDs.size(); i++) {
                    Tenant secondaryTenant = mDB.getTenantByID(secondaryTenantIDs.get(i), MainActivity.sUser);
                    mSecondaryTenants.add(secondaryTenant);
                }
                mPage.getData().putParcelableArrayList(LeaseWizardPage2.LEASE_SECONDARY_TENANTS_DATA_KEY, mSecondaryTenants);
                mPage.getData().putString(LeaseWizardPage2.LEASE_SECONDARY_TENANTS_STRING_DATA_KEY, getSecondaryTenantsString());
            }
            //Deposit
            mDeposit = new BigDecimal(0);
            String formatted = DateAndCurrencyDisplayer.getCurrencyToDisplay(mMoneyFormatCode, mDeposit);
            mPage.getData().putString(LeaseWizardPage2.LEASE_DEPOSIT_FORMATTED_STRING_DATA_KEY, formatted);
            mPage.getData().putString(LeaseWizardPage2.LEASE_DEPOSIT_STRING_DATA_KEY, mDeposit.toPlainString());
            mPage.getData().putBoolean(LeaseWizardPage2.WAS_PRELOADED, true);
        } else {
            preloadData(mPage.getData());
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(mTenantApartmentOrLeaseChooserDialog != null){
            mTenantApartmentOrLeaseChooserDialog.dismiss();
        }
    }
}

