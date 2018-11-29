package com.rba18.fragments;

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
import com.rba18.R;
import com.rba18.activities.MainActivity;
import com.rba18.helpers.MainArrayDataMethods;
import com.rba18.helpers.TenantApartmentOrLeaseChooserDialog;
import com.rba18.model.Apartment;
import com.rba18.model.ExpenseLogEntry;
import com.rba18.model.Lease;
import com.rba18.model.Tenant;
import com.rba18.sqlite.DatabaseHandler;
import com.rba18.wizards.ExpenseWizardPage3;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class ExpenseWizardPage3Fragment extends android.support.v4.app.Fragment {
    private static final String ARG_KEY = "key";
    private PageFragmentCallbacks mCallbacks;
    private String mKey;
    private ExpenseWizardPage3 mPage;
    private TextView mLinkedAptTV, mLinkedTenantTV, mLinkedLeaseTV;
    private DatabaseHandler mDBHandler;
    private MainArrayDataMethods mMainArrayDataMethods;
    private Apartment mApartment;
    private ArrayList<Apartment> mAvailableApartments;
    private Tenant mTenant;
    private ArrayList<Tenant> mAvailableTenants;
    private Lease mLease;
    private ArrayList<Lease> mAvailableLeases;
    private boolean mIsInitializing;
    private TenantApartmentOrLeaseChooserDialog mTenantApartmentOrLeaseChooserDialog;

    public static ExpenseWizardPage3Fragment create(String key) {
        Bundle args = new Bundle();
        args.putString(ARG_KEY, key);

        ExpenseWizardPage3Fragment fragment = new ExpenseWizardPage3Fragment();
        fragment.setArguments(args);
        return fragment;
    }

    public ExpenseWizardPage3Fragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        mKey = args.getString(ARG_KEY);
        mPage = (ExpenseWizardPage3) mCallbacks.onGetPage(mKey);
        mDBHandler = new DatabaseHandler(getContext());
        mMainArrayDataMethods = new MainArrayDataMethods();
        mAvailableApartments = new ArrayList<>();
        mAvailableTenants = new ArrayList<>();
        mAvailableLeases = new ArrayList<>();
        mIsInitializing = true;
        Bundle extras = mPage.getData();
        if (extras != null) {
            ExpenseLogEntry expenseToEdit = extras.getParcelable("expenseToEdit");
            if (expenseToEdit != null) {
                loadDataForEdit(expenseToEdit);
            } else {
                preloadData(extras);
            }
        } else {
            mApartment = null;
            mTenant = null;
            mLease = null;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_expense_wizard_page_3, container, false);
        (rootView.findViewById(android.R.id.title)).setVisibility(View.GONE);
        mLinkedAptTV = rootView.findViewById(R.id.expenseWizardAptLinkingTV);
        if (mPage.getData().getParcelable(ExpenseWizardPage3.EXPENSE_RELATED_APT_DATA_KEY) != null) {
            mLinkedAptTV.setText(mPage.getData().getString(ExpenseWizardPage3.EXPENSE_RELATED_APT_TEXT_DATA_KEY));
            mApartment = mPage.getData().getParcelable(ExpenseWizardPage3.EXPENSE_RELATED_APT_DATA_KEY);
        }
        mLinkedAptTV.setScroller(new Scroller(getContext()));
        mLinkedAptTV.setMaxLines(5);
        mLinkedAptTV.setVerticalScrollBarEnabled(true);
        mLinkedAptTV.setMovementMethod(new ScrollingMovementMethod());

        mLinkedTenantTV = rootView.findViewById(R.id.expenseWizardTenantLinkingTV);
        if (mPage.getData().getParcelable(ExpenseWizardPage3.EXPENSE_RELATED_TENANT_DATA_KEY) != null) {
            mLinkedTenantTV.setText(mPage.getData().getString(ExpenseWizardPage3.EXPENSE_RELATED_TENANT_TEXT_DATA_KEY));
            mTenant = mPage.getData().getParcelable(ExpenseWizardPage3.EXPENSE_RELATED_TENANT_DATA_KEY);
        }
        mLinkedTenantTV.setScroller(new Scroller(getContext()));
        mLinkedTenantTV.setMaxLines(5);
        mLinkedTenantTV.setVerticalScrollBarEnabled(true);
        mLinkedTenantTV.setMovementMethod(new ScrollingMovementMethod());

        mLinkedLeaseTV = rootView.findViewById(R.id.expenseWizardLeaseLinkingTV);
        if (mPage.getData().getParcelable(ExpenseWizardPage3.EXPENSE_RELATED_LEASE_DATA_KEY) != null) {
            mLinkedLeaseTV.setText(mPage.getData().getString(ExpenseWizardPage3.EXPENSE_RELATED_LEASE_TEXT_DATA_KEY));
            mLease = mPage.getData().getParcelable(ExpenseWizardPage3.EXPENSE_RELATED_LEASE_DATA_KEY);
        }
        mLinkedLeaseTV.setScroller(new Scroller(getContext()));
        mLinkedLeaseTV.setMaxLines(5);
        mLinkedLeaseTV.setVerticalScrollBarEnabled(true);
        mLinkedLeaseTV.setMovementMethod(new ScrollingMovementMethod());

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
        if (mApartment != null) {
            curAptID = mApartment.getId();
        }
        for (int i = 0; i < MainActivity.sApartmentList.size(); i++) {
            if (MainActivity.sApartmentList.get(i).isActive() && MainActivity.sApartmentList.get(i).getId() != curAptID) {
                mAvailableApartments.add(MainActivity.sApartmentList.get(i));
            }
        }
        ArrayList<Integer> curTenantIDs = new ArrayList<>();
        if (mPage.getData().getParcelable(ExpenseWizardPage3.EXPENSE_RELATED_TENANT_DATA_KEY) != null) {
            mTenant = mPage.getData().getParcelable(ExpenseWizardPage3.EXPENSE_RELATED_TENANT_DATA_KEY);
            curTenantIDs.add(mTenant.getId());
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
        getAvailableLeases();
        mLinkedAptTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTenantApartmentOrLeaseChooserDialog = new TenantApartmentOrLeaseChooserDialog(getContext(), TenantApartmentOrLeaseChooserDialog.APARTMENT_TYPE, mAvailableApartments);
                mTenantApartmentOrLeaseChooserDialog.show();
                mTenantApartmentOrLeaseChooserDialog.changeCancelBtnText(getContext().getResources().getString(R.string.clear));
                mTenantApartmentOrLeaseChooserDialog.setDialogResult(new TenantApartmentOrLeaseChooserDialog.OnTenantChooserDialogResult() {
                    @Override
                    public void finish(Tenant tenantResult, Apartment apartmentResult, Lease leaseResult) {
                        if (mApartment != null) {
                            mAvailableApartments.add(mApartment);
                        }
                        if (apartmentResult != null) {
                            mAvailableApartments.remove(apartmentResult);
                            mApartment = apartmentResult;
                            mLinkedAptTV.setText(getApartmentString());
                            mPage.getData().putInt(ExpenseWizardPage3.EXPENSE_RELATED_APT_ID_DATA_KEY, mApartment.getId());
                            mPage.getData().putString(ExpenseWizardPage3.EXPENSE_RELATED_APT_TEXT_DATA_KEY, mLinkedAptTV.getText().toString());
                            mPage.getData().putParcelable(ExpenseWizardPage3.EXPENSE_RELATED_APT_DATA_KEY, mApartment);
                        } else {
                            mApartment = null;
                            mLinkedAptTV.setText("");
                            mPage.getData().putInt(ExpenseWizardPage3.EXPENSE_RELATED_APT_ID_DATA_KEY, 0);
                            mPage.getData().putString(ExpenseWizardPage3.EXPENSE_RELATED_APT_TEXT_DATA_KEY, "");
                            mPage.getData().putParcelable(ExpenseWizardPage3.EXPENSE_RELATED_APT_DATA_KEY, null);
                        }
                        mPage.notifyDataChanged();
                        if (!mIsInitializing) {
                            getAvailableLeases();
                            resetLeaseSelection();
                            mMainArrayDataMethods.sortApartmentArrayAlphabetically(mAvailableApartments);
                        }
                    }
                });
            }
        });
        mLinkedTenantTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTenantApartmentOrLeaseChooserDialog = new TenantApartmentOrLeaseChooserDialog(getContext(), TenantApartmentOrLeaseChooserDialog.TENANT_TYPE, mAvailableTenants);
                mTenantApartmentOrLeaseChooserDialog.show();
                mTenantApartmentOrLeaseChooserDialog.changeCancelBtnText(getContext().getResources().getString(R.string.clear));
                mTenantApartmentOrLeaseChooserDialog.setDialogResult(new TenantApartmentOrLeaseChooserDialog.OnTenantChooserDialogResult() {
                    @Override
                    public void finish(Tenant tenantResult, Apartment apartmentResult, Lease leaseResult) {
                        if (mTenant != null) {
                            mAvailableTenants.add(mTenant);
                        }
                        if (tenantResult != null) {
                            mAvailableTenants.remove(tenantResult);
                            mTenant = tenantResult;
                            mLinkedTenantTV.setText(getTenantString());
                            mPage.getData().putInt(ExpenseWizardPage3.EXPENSE_RELATED_TENANT_ID_DATA_KEY, mTenant.getId());
                            mPage.getData().putString(ExpenseWizardPage3.EXPENSE_RELATED_TENANT_TEXT_DATA_KEY, mLinkedTenantTV.getText().toString());
                            mPage.getData().putParcelable(ExpenseWizardPage3.EXPENSE_RELATED_TENANT_DATA_KEY, mTenant);
                        } else {
                            mTenant = null;
                            mLinkedTenantTV.setText("");
                            mPage.getData().putInt(ExpenseWizardPage3.EXPENSE_RELATED_TENANT_ID_DATA_KEY, 0);
                            mPage.getData().putString(ExpenseWizardPage3.EXPENSE_RELATED_TENANT_TEXT_DATA_KEY, "");
                            mPage.getData().putParcelable(ExpenseWizardPage3.EXPENSE_RELATED_TENANT_DATA_KEY, null);
                        }
                        mPage.notifyDataChanged();
                        if (!mIsInitializing) {
                            getAvailableLeases();
                            resetLeaseSelection();
                            mMainArrayDataMethods.sortTenantArrayAlphabetically(mAvailableTenants);
                        }
                    }
                });
            }
        });
        mLinkedLeaseTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTenantApartmentOrLeaseChooserDialog = new TenantApartmentOrLeaseChooserDialog(getContext(), TenantApartmentOrLeaseChooserDialog.LEASE_TYPE, mAvailableLeases);
                mTenantApartmentOrLeaseChooserDialog.show();
                mTenantApartmentOrLeaseChooserDialog.changeCancelBtnText(getContext().getResources().getString(R.string.clear));
                mTenantApartmentOrLeaseChooserDialog.setDialogResult(new TenantApartmentOrLeaseChooserDialog.OnTenantChooserDialogResult() {
                    @Override
                    public void finish(Tenant tenantResult, Apartment apartmentResult, Lease leaseResult) {
                        if (leaseResult != null) {
                            mLease = leaseResult;
                            if (mApartment == null) {
                                mApartment = mDBHandler.getApartmentByID(mLease.getApartmentID(), MainActivity.sUser);
                                mLinkedAptTV.setText(getApartmentString());
                                mPage.getData().putInt(ExpenseWizardPage3.EXPENSE_RELATED_APT_ID_DATA_KEY, mApartment.getId());
                                mPage.getData().putString(ExpenseWizardPage3.EXPENSE_RELATED_APT_TEXT_DATA_KEY, mLinkedAptTV.getText().toString());
                                mPage.getData().putParcelable(ExpenseWizardPage3.EXPENSE_RELATED_APT_DATA_KEY, mApartment);
                            }
                            if (mTenant == null) {
                                mTenant = mDBHandler.getTenantByID(mLease.getPrimaryTenantID(), MainActivity.sUser);
                                mLinkedTenantTV.setText(getTenantString());
                                mPage.getData().putInt(ExpenseWizardPage3.EXPENSE_RELATED_TENANT_ID_DATA_KEY, mTenant.getId());
                                mPage.getData().putString(ExpenseWizardPage3.EXPENSE_RELATED_TENANT_TEXT_DATA_KEY, mLinkedTenantTV.getText().toString());
                                mPage.getData().putParcelable(ExpenseWizardPage3.EXPENSE_RELATED_TENANT_DATA_KEY, mTenant);
                            }
                            getAvailableLeases();
                            mLinkedLeaseTV.setText(getLeaseString());
                            mPage.getData().putInt(ExpenseWizardPage3.EXPENSE_RELATED_LEASE_ID_DATA_KEY, mLease.getId());
                            mPage.getData().putString(ExpenseWizardPage3.EXPENSE_RELATED_LEASE_TEXT_DATA_KEY, mLinkedLeaseTV.getText().toString());
                            mPage.getData().putParcelable(ExpenseWizardPage3.EXPENSE_RELATED_LEASE_DATA_KEY, mLease);
                        } else {
                            mLease = null;
                            mLinkedLeaseTV.setText("");
                            mPage.getData().putInt(ExpenseWizardPage3.EXPENSE_RELATED_LEASE_ID_DATA_KEY, 0);
                            mPage.getData().putString(ExpenseWizardPage3.EXPENSE_RELATED_LEASE_TEXT_DATA_KEY, "");
                            mPage.getData().putParcelable(ExpenseWizardPage3.EXPENSE_RELATED_LEASE_DATA_KEY, null);
                            getAvailableLeases();
                        }
                        mMainArrayDataMethods.sortLeaseArrayByStartDateDesc(mAvailableLeases);
                        mPage.notifyDataChanged();
                    }
                });
            }
        });
        mMainArrayDataMethods.sortTenantArrayAlphabetically(mAvailableTenants);
        mMainArrayDataMethods.sortApartmentArrayAlphabetically(mAvailableApartments);
        mMainArrayDataMethods.sortLeaseArrayByStartDateDesc(mAvailableLeases);
        mIsInitializing = false;
    }

    private String getApartmentString() {
        if (mApartment != null) {
            StringBuilder builder = new StringBuilder(mApartment.getStreet1());
            if (mApartment.getStreet2() != null) {
                builder.append(" ");
                builder.append(mApartment.getStreet2());
            }
            return builder.toString();
        } else {
            return "";
        }
    }

    private String getTenantString() {
        if (mTenant != null) {
            StringBuilder builder = new StringBuilder(mTenant.getFirstName());
            builder.append(" ");
            builder.append(mTenant.getLastName());
            return builder.toString();
        } else {
            return "";
        }
    }

    private String getLeaseString() {
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
        if (mLease != null) {
            StringBuilder builder = new StringBuilder(formatter.format(mLease.getLeaseStart()));
            builder.append(" - ");
            builder.append(formatter.format(mLease.getLeaseEnd()));
            return builder.toString();
        } else {
            return "";
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(mTenantApartmentOrLeaseChooserDialog != null){
            mTenantApartmentOrLeaseChooserDialog.dismiss();
        }
    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);

        // In a future update to the support library, this should override setUserVisibleHint
        // instead of setMenuVisibility.
        if (mLinkedAptTV != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            if (!menuVisible) {
                imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
            }
        }
    }

    private void getAvailableLeases() {
        if (mApartment != null && mTenant != null) {
            mAvailableLeases = mDBHandler.getUsersLeasesForTenantAndApartment(MainActivity.sUser, mTenant.getId(), mApartment.getId());
        } else if (mApartment != null) {
            mAvailableLeases = mDBHandler.getUsersLeasesForApartment(MainActivity.sUser, mApartment.getId());
        } else if (mTenant != null) {
            mAvailableLeases = mDBHandler.getUsersLeasesForTenant(MainActivity.sUser, mTenant.getId());
        } else {
            mAvailableLeases = new ArrayList<>();
        }
        if(mLease != null) {
            for(int i = 0; i < mAvailableLeases.size(); i++){
                if(mAvailableLeases.get(i).getId() == mLease.getId()){
                    mAvailableLeases.remove(mAvailableLeases.get(i));
                    break;
                }
            }
        }
    }

    private void resetLeaseSelection() {
        mLease = null;
        mLinkedLeaseTV.setText("");
        mPage.getData().putInt(ExpenseWizardPage3.EXPENSE_RELATED_LEASE_ID_DATA_KEY, 0);
        mPage.getData().putString(ExpenseWizardPage3.EXPENSE_RELATED_LEASE_TEXT_DATA_KEY, mLinkedLeaseTV.getText().toString());
        mPage.getData().putParcelable(ExpenseWizardPage3.EXPENSE_RELATED_LEASE_DATA_KEY, null);
        mPage.notifyDataChanged();
    }

    private void preloadData(Bundle bundle) {
        preloadApartment(bundle);
        preloadTenant(bundle);
        preloadLease(bundle);
    }

    private void loadDataForEdit(ExpenseLogEntry expenseToEdit) {
        if (!mPage.getData().getBoolean(ExpenseWizardPage3.WAS_PRELOADED)) {
            if (expenseToEdit.getApartmentID() != 0) {
                mApartment = mDBHandler.getApartmentByID(expenseToEdit.getApartmentID(), MainActivity.sUser);
                if (mApartment != null) {
                    mPage.getData().putInt(ExpenseWizardPage3.EXPENSE_RELATED_APT_ID_DATA_KEY, mApartment.getId());
                    mPage.getData().putString(ExpenseWizardPage3.EXPENSE_RELATED_APT_TEXT_DATA_KEY, getApartmentString());
                    mPage.getData().putParcelable(ExpenseWizardPage3.EXPENSE_RELATED_APT_DATA_KEY, mApartment);
                }
            }
            if (expenseToEdit.getTenantID() != 0) {
                mTenant = mDBHandler.getTenantByID(expenseToEdit.getTenantID(), MainActivity.sUser);
                if (mTenant != null) {
                    mPage.getData().putInt(ExpenseWizardPage3.EXPENSE_RELATED_TENANT_ID_DATA_KEY, mTenant.getId());
                    mPage.getData().putString(ExpenseWizardPage3.EXPENSE_RELATED_TENANT_TEXT_DATA_KEY, getTenantString());
                    mPage.getData().putParcelable(ExpenseWizardPage3.EXPENSE_RELATED_TENANT_DATA_KEY, mTenant);

                }
            }
            if (expenseToEdit.getLeaseID() != 0) {
                mLease = mDBHandler.getLeaseByID(MainActivity.sUser, expenseToEdit.getLeaseID());
                if (mLease != null) {
                    mPage.getData().putInt(ExpenseWizardPage3.EXPENSE_RELATED_LEASE_ID_DATA_KEY, mLease.getId());
                    mPage.getData().putString(ExpenseWizardPage3.EXPENSE_RELATED_LEASE_TEXT_DATA_KEY, getLeaseString());
                    mPage.getData().putParcelable(ExpenseWizardPage3.EXPENSE_RELATED_LEASE_DATA_KEY, mLease);
                }
            }
            mPage.getData().putBoolean(ExpenseWizardPage3.WAS_PRELOADED, true);
        } else {
            preloadData(mPage.getData());
        }
    }

    private void preloadApartment(Bundle bundle) {
        if (mPage.getData().getInt(ExpenseWizardPage3.EXPENSE_RELATED_APT_ID_DATA_KEY, 0) != 0) {
            if (mPage.getData().getParcelable(ExpenseWizardPage3.EXPENSE_RELATED_APT_DATA_KEY) != null) {
                mApartment = mPage.getData().getParcelable(ExpenseWizardPage3.EXPENSE_RELATED_APT_DATA_KEY);
            } else {
                mApartment = mDBHandler.getApartmentByID(mPage.getData().getInt(ExpenseWizardPage3.EXPENSE_RELATED_APT_ID_DATA_KEY), MainActivity.sUser);
                mPage.getData().putString(ExpenseWizardPage3.EXPENSE_RELATED_APT_TEXT_DATA_KEY, getApartmentString());
                mPage.getData().putParcelable(ExpenseWizardPage3.EXPENSE_RELATED_APT_DATA_KEY, mApartment);
            }
        } else if (bundle.getParcelable("preloadedApartment") != null) {
            mApartment = bundle.getParcelable("preloadedApartment");
            mPage.getData().putInt(ExpenseWizardPage3.EXPENSE_RELATED_APT_ID_DATA_KEY, mApartment.getId());
            mPage.getData().putString(ExpenseWizardPage3.EXPENSE_RELATED_APT_TEXT_DATA_KEY, getApartmentString());
            mPage.getData().putParcelable(ExpenseWizardPage3.EXPENSE_RELATED_APT_DATA_KEY, mApartment);
        } else if (bundle.getInt("preloadedApartmentID") != 0) {
            mApartment = mDBHandler.getApartmentByID(bundle.getInt("preloadedApartmentID"), MainActivity.sUser);
            mPage.getData().putInt(ExpenseWizardPage3.EXPENSE_RELATED_APT_ID_DATA_KEY, mApartment.getId());
            mPage.getData().putString(ExpenseWizardPage3.EXPENSE_RELATED_APT_TEXT_DATA_KEY, getApartmentString());
            mPage.getData().putParcelable(ExpenseWizardPage3.EXPENSE_RELATED_APT_DATA_KEY, mApartment);
        } else {
            mApartment = null;
        }
    }

    private void preloadTenant(Bundle bundle) {
        if (mPage.getData().getInt(ExpenseWizardPage3.EXPENSE_RELATED_TENANT_ID_DATA_KEY, 0) != 0) {
            //If re-loaded with an id
            if (mPage.getData().getParcelable(ExpenseWizardPage3.EXPENSE_RELATED_TENANT_DATA_KEY) != null) {
                //and mTenant is not null
                mTenant = mPage.getData().getParcelable(ExpenseWizardPage3.EXPENSE_RELATED_TENANT_DATA_KEY);
            } else {
                //and mTenant is for some reason null
                mTenant = mDBHandler.getTenantByID(mPage.getData().getInt(ExpenseWizardPage3.EXPENSE_RELATED_TENANT_ID_DATA_KEY), MainActivity.sUser);
                mPage.getData().putString(ExpenseWizardPage3.EXPENSE_RELATED_TENANT_TEXT_DATA_KEY, getTenantString());
                mPage.getData().putParcelable(ExpenseWizardPage3.EXPENSE_RELATED_TENANT_DATA_KEY, mTenant);
            }
        } else if (bundle.getParcelable("preloadedTenant") != null) {
            //If loaded first time with preloaded mTenant
            mTenant = bundle.getParcelable("preloadedTenant");
            mPage.getData().putInt(ExpenseWizardPage3.EXPENSE_RELATED_TENANT_ID_DATA_KEY, mTenant.getId());
            mPage.getData().putString(ExpenseWizardPage3.EXPENSE_RELATED_TENANT_TEXT_DATA_KEY, getTenantString());
            mPage.getData().putParcelable(ExpenseWizardPage3.EXPENSE_RELATED_TENANT_DATA_KEY, mTenant);
        } else if (bundle.getInt("preloadedTenantID") != 0) {
            //If loaded first time with mTenant id
            mTenant = mDBHandler.getTenantByID(bundle.getInt("preloadedTenantID"), MainActivity.sUser);
            mPage.getData().putInt(ExpenseWizardPage3.EXPENSE_RELATED_TENANT_ID_DATA_KEY, mTenant.getId());
            mPage.getData().putString(ExpenseWizardPage3.EXPENSE_RELATED_TENANT_TEXT_DATA_KEY, getTenantString());
            mPage.getData().putParcelable(ExpenseWizardPage3.EXPENSE_RELATED_TENANT_DATA_KEY, mTenant);
        } else {
            //If no mTenant id
            mTenant = null;
        }
    }

    private void preloadLease(Bundle bundle) {
        if (mPage.getData().getInt(ExpenseWizardPage3.EXPENSE_RELATED_LEASE_ID_DATA_KEY, 0) != 0) {
            //If re-loaded with an id
            if (mPage.getData().getParcelable(ExpenseWizardPage3.EXPENSE_RELATED_LEASE_DATA_KEY) != null) {
                //and mLease is not null
                mLease = mPage.getData().getParcelable(ExpenseWizardPage3.EXPENSE_RELATED_LEASE_DATA_KEY);
            } else {
                //and mLease is for some reason null
                mLease = mDBHandler.getLeaseByID(MainActivity.sUser, mPage.getData().getInt(ExpenseWizardPage3.EXPENSE_RELATED_LEASE_ID_DATA_KEY));
                mPage.getData().putString(ExpenseWizardPage3.EXPENSE_RELATED_LEASE_TEXT_DATA_KEY, getLeaseString());
                mPage.getData().putParcelable(ExpenseWizardPage3.EXPENSE_RELATED_LEASE_DATA_KEY, mLease);
            }
        } else if (bundle.getParcelable("preloadedLease") != null) {
            //If loaded first time with preloaded mLease
            mLease = bundle.getParcelable("preloadedLease");
            if (mTenant == null) {
                preLoadTenantForLease(mLease);
            }
            if (mApartment == null) {
                preloadApartmentForLease(mLease);
            }
            mPage.getData().putInt(ExpenseWizardPage3.EXPENSE_RELATED_LEASE_ID_DATA_KEY, mLease.getId());
            mPage.getData().putString(ExpenseWizardPage3.EXPENSE_RELATED_LEASE_TEXT_DATA_KEY, getLeaseString());
            mPage.getData().putParcelable(ExpenseWizardPage3.EXPENSE_RELATED_LEASE_DATA_KEY, mLease);
        } else if (bundle.getInt("preloadedLeaseID") != 0) {
            //If loaded first time with mLease id
            mLease = mDBHandler.getLeaseByID(MainActivity.sUser, bundle.getInt("preloadedLeaseID"));
            if (mTenant == null) {
                preLoadTenantForLease(mLease);
            }
            if (mApartment == null) {
                preloadApartmentForLease(mLease);
            }
            mPage.getData().putInt(ExpenseWizardPage3.EXPENSE_RELATED_LEASE_ID_DATA_KEY, mLease.getId());
            mPage.getData().putString(ExpenseWizardPage3.EXPENSE_RELATED_LEASE_TEXT_DATA_KEY, getLeaseString());
            mPage.getData().putParcelable(ExpenseWizardPage3.EXPENSE_RELATED_LEASE_DATA_KEY, mLease);
        } else {
            //If no mLease id
            mLease = null;
        }
    }

    private void preLoadTenantForLease(Lease lease) {
        if (lease.getPrimaryTenantID() != 0) {
            mTenant = mDBHandler.getTenantByID(lease.getPrimaryTenantID(), MainActivity.sUser);
            mPage.getData().putInt(ExpenseWizardPage3.EXPENSE_RELATED_TENANT_ID_DATA_KEY, mTenant.getId());
            mPage.getData().putString(ExpenseWizardPage3.EXPENSE_RELATED_TENANT_TEXT_DATA_KEY, getTenantString());
            mPage.getData().putParcelable(ExpenseWizardPage3.EXPENSE_RELATED_TENANT_DATA_KEY, mTenant);
        }
    }

    private void preloadApartmentForLease(Lease lease) {
        if (lease.getApartmentID() != 0) {
            mApartment = mDBHandler.getApartmentByID(lease.getApartmentID(), MainActivity.sUser);
            mPage.getData().putInt(ExpenseWizardPage3.EXPENSE_RELATED_APT_ID_DATA_KEY, mApartment.getId());
            mPage.getData().putString(ExpenseWizardPage3.EXPENSE_RELATED_APT_TEXT_DATA_KEY, getApartmentString());
            mPage.getData().putParcelable(ExpenseWizardPage3.EXPENSE_RELATED_APT_DATA_KEY, mApartment);
        }
    }
}

