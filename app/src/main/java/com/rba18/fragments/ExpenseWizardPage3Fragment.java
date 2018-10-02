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
    private TextView linkedAptTV, linkedTenantTV, linkedLeaseTV;
    private DatabaseHandler dbHandler;
    private MainArrayDataMethods mainArrayDataMethods;
    private Apartment apartment;
    private ArrayList<Apartment> availableApartments;
    private Tenant tenant;
    private ArrayList<Tenant> availableTenants;
    private Lease lease;
    private ArrayList<Lease> availableLeases;
    private boolean isInitializing;
    private TenantApartmentOrLeaseChooserDialog tenantApartmentOrLeaseChooserDialog;

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
        dbHandler = new DatabaseHandler(getContext());
        mainArrayDataMethods = new MainArrayDataMethods();
        availableApartments = new ArrayList<>();
        availableTenants = new ArrayList<>();
        availableLeases = new ArrayList<>();
        isInitializing = true;
        Bundle extras = mPage.getData();
        if (extras != null) {
            ExpenseLogEntry expenseToEdit = extras.getParcelable("expenseToEdit");
            if (expenseToEdit != null) {
                loadDataForEdit(expenseToEdit);
            } else {
                preloadData(extras);
            }
        } else {
            apartment = null;
            tenant = null;
            lease = null;
        }
        //availableApartments.addAll(MainActivity.apartmentList);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_expense_wizard_page_3, container, false);
        (rootView.findViewById(android.R.id.title)).setVisibility(View.GONE);
        linkedAptTV = rootView.findViewById(R.id.expenseWizardAptLinkingTV);
        if (mPage.getData().getParcelable(ExpenseWizardPage3.EXPENSE_RELATED_APT_DATA_KEY) != null) {
            linkedAptTV.setText(mPage.getData().getString(ExpenseWizardPage3.EXPENSE_RELATED_APT_TEXT_DATA_KEY));
            apartment = mPage.getData().getParcelable(ExpenseWizardPage3.EXPENSE_RELATED_APT_DATA_KEY);
        }
        linkedAptTV.setScroller(new Scroller(getContext()));
        linkedAptTV.setMaxLines(5);
        linkedAptTV.setVerticalScrollBarEnabled(true);
        linkedAptTV.setMovementMethod(new ScrollingMovementMethod());

        linkedTenantTV = rootView.findViewById(R.id.expenseWizardTenantLinkingTV);
        if (mPage.getData().getParcelable(ExpenseWizardPage3.EXPENSE_RELATED_TENANT_DATA_KEY) != null) {
            linkedTenantTV.setText(mPage.getData().getString(ExpenseWizardPage3.EXPENSE_RELATED_TENANT_TEXT_DATA_KEY));
            tenant = mPage.getData().getParcelable(ExpenseWizardPage3.EXPENSE_RELATED_TENANT_DATA_KEY);
        }
        linkedTenantTV.setScroller(new Scroller(getContext()));
        linkedTenantTV.setMaxLines(5);
        linkedTenantTV.setVerticalScrollBarEnabled(true);
        linkedTenantTV.setMovementMethod(new ScrollingMovementMethod());

        linkedLeaseTV = rootView.findViewById(R.id.expenseWizardLeaseLinkingTV);
        if (mPage.getData().getParcelable(ExpenseWizardPage3.EXPENSE_RELATED_LEASE_DATA_KEY) != null) {
            linkedLeaseTV.setText(mPage.getData().getString(ExpenseWizardPage3.EXPENSE_RELATED_LEASE_TEXT_DATA_KEY));
            lease = mPage.getData().getParcelable(ExpenseWizardPage3.EXPENSE_RELATED_LEASE_DATA_KEY);
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
        if (apartment != null) {
            curAptID = apartment.getId();
        }
        for (int i = 0; i < MainActivity.apartmentList.size(); i++) {
            if (MainActivity.apartmentList.get(i).isActive() && MainActivity.apartmentList.get(i).getId() != curAptID) {
                availableApartments.add(MainActivity.apartmentList.get(i));
            }
        }
        ArrayList<Integer> curTenantIDs = new ArrayList<>();
        if (mPage.getData().getParcelable(ExpenseWizardPage3.EXPENSE_RELATED_TENANT_DATA_KEY) != null) {
            tenant = mPage.getData().getParcelable(ExpenseWizardPage3.EXPENSE_RELATED_TENANT_DATA_KEY);
            curTenantIDs.add(tenant.getId());
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
        getAvailableLeases();
        linkedAptTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tenantApartmentOrLeaseChooserDialog = new TenantApartmentOrLeaseChooserDialog(getContext(), TenantApartmentOrLeaseChooserDialog.APARTMENT_TYPE, availableApartments);
                tenantApartmentOrLeaseChooserDialog.show();
                tenantApartmentOrLeaseChooserDialog.changeCancelBtnText(getContext().getResources().getString(R.string.clear));
                tenantApartmentOrLeaseChooserDialog.setDialogResult(new TenantApartmentOrLeaseChooserDialog.OnTenantChooserDialogResult() {
                    @Override
                    public void finish(Tenant tenantResult, Apartment apartmentResult, Lease leaseResult) {
                        if (apartment != null) {
                            availableApartments.add(apartment);
                        }
                        if (apartmentResult != null) {
                            availableApartments.remove(apartmentResult);
                            apartment = apartmentResult;
                            linkedAptTV.setText(getApartmentString());
                            mPage.getData().putInt(ExpenseWizardPage3.EXPENSE_RELATED_APT_ID_DATA_KEY, apartment.getId());
                            mPage.getData().putString(ExpenseWizardPage3.EXPENSE_RELATED_APT_TEXT_DATA_KEY, linkedAptTV.getText().toString());
                            mPage.getData().putParcelable(ExpenseWizardPage3.EXPENSE_RELATED_APT_DATA_KEY, apartment);
                        } else {
                            apartment = null;
                            linkedAptTV.setText("");
                            mPage.getData().putInt(ExpenseWizardPage3.EXPENSE_RELATED_APT_ID_DATA_KEY, 0);
                            mPage.getData().putString(ExpenseWizardPage3.EXPENSE_RELATED_APT_TEXT_DATA_KEY, "");
                            mPage.getData().putParcelable(ExpenseWizardPage3.EXPENSE_RELATED_APT_DATA_KEY, null);
                        }
                        mPage.notifyDataChanged();
                        if (!isInitializing) {
                            getAvailableLeases();
                            resetLeaseSelection();
                            mainArrayDataMethods.sortApartmentArrayAlphabetically(availableApartments);
                        }
                    }
                });
            }
        });
        linkedTenantTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tenantApartmentOrLeaseChooserDialog = new TenantApartmentOrLeaseChooserDialog(getContext(), TenantApartmentOrLeaseChooserDialog.TENANT_TYPE, availableTenants);
                tenantApartmentOrLeaseChooserDialog.show();
                tenantApartmentOrLeaseChooserDialog.changeCancelBtnText(getContext().getResources().getString(R.string.clear));
                tenantApartmentOrLeaseChooserDialog.setDialogResult(new TenantApartmentOrLeaseChooserDialog.OnTenantChooserDialogResult() {
                    @Override
                    public void finish(Tenant tenantResult, Apartment apartmentResult, Lease leaseResult) {
                        if (tenant != null) {
                            availableTenants.add(tenant);
                        }
                        if (tenantResult != null) {
                            availableTenants.remove(tenantResult);
                            tenant = tenantResult;
                            linkedTenantTV.setText(getTenantString());
                            mPage.getData().putInt(ExpenseWizardPage3.EXPENSE_RELATED_TENANT_ID_DATA_KEY, tenant.getId());
                            mPage.getData().putString(ExpenseWizardPage3.EXPENSE_RELATED_TENANT_TEXT_DATA_KEY, linkedTenantTV.getText().toString());
                            mPage.getData().putParcelable(ExpenseWizardPage3.EXPENSE_RELATED_TENANT_DATA_KEY, tenant);
                        } else {
                            tenant = null;
                            linkedTenantTV.setText("");
                            mPage.getData().putInt(ExpenseWizardPage3.EXPENSE_RELATED_TENANT_ID_DATA_KEY, 0);
                            mPage.getData().putString(ExpenseWizardPage3.EXPENSE_RELATED_TENANT_TEXT_DATA_KEY, "");
                            mPage.getData().putParcelable(ExpenseWizardPage3.EXPENSE_RELATED_TENANT_DATA_KEY, null);
                        }
                        mPage.notifyDataChanged();
                        if (!isInitializing) {
                            getAvailableLeases();
                            resetLeaseSelection();
                            mainArrayDataMethods.sortTenantArrayAlphabetically(availableTenants);
                        }
                    }
                });
            }
        });
        linkedLeaseTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tenantApartmentOrLeaseChooserDialog = new TenantApartmentOrLeaseChooserDialog(getContext(), TenantApartmentOrLeaseChooserDialog.LEASE_TYPE, availableLeases);
                tenantApartmentOrLeaseChooserDialog.show();
                tenantApartmentOrLeaseChooserDialog.changeCancelBtnText(getContext().getResources().getString(R.string.clear));
                tenantApartmentOrLeaseChooserDialog.setDialogResult(new TenantApartmentOrLeaseChooserDialog.OnTenantChooserDialogResult() {
                    @Override
                    public void finish(Tenant tenantResult, Apartment apartmentResult, Lease leaseResult) {
                        //if (lease != null) {
                        //    availableLeases.add(lease);
                        //}
                        //availableTenants.remove(tenantResult);
                        //availableTenants.add(primaryTenant);
                        if (leaseResult != null) {
                            lease = leaseResult;
                            if (apartment == null) {
                                apartment = dbHandler.getApartmentByID(lease.getApartmentID(), MainActivity.user);
                                linkedAptTV.setText(getApartmentString());
                                mPage.getData().putInt(ExpenseWizardPage3.EXPENSE_RELATED_APT_ID_DATA_KEY, apartment.getId());
                                mPage.getData().putString(ExpenseWizardPage3.EXPENSE_RELATED_APT_TEXT_DATA_KEY, linkedAptTV.getText().toString());
                                mPage.getData().putParcelable(ExpenseWizardPage3.EXPENSE_RELATED_APT_DATA_KEY, apartment);
                            }
                            if (tenant == null) {
                                tenant = dbHandler.getTenantByID(lease.getPrimaryTenantID(), MainActivity.user);
                                linkedTenantTV.setText(getTenantString());
                                mPage.getData().putInt(ExpenseWizardPage3.EXPENSE_RELATED_TENANT_ID_DATA_KEY, tenant.getId());
                                mPage.getData().putString(ExpenseWizardPage3.EXPENSE_RELATED_TENANT_TEXT_DATA_KEY, linkedTenantTV.getText().toString());
                                mPage.getData().putParcelable(ExpenseWizardPage3.EXPENSE_RELATED_TENANT_DATA_KEY, tenant);
                            }
                            getAvailableLeases();
                            linkedLeaseTV.setText(getLeaseString());
                            mPage.getData().putInt(ExpenseWizardPage3.EXPENSE_RELATED_LEASE_ID_DATA_KEY, lease.getId());
                            mPage.getData().putString(ExpenseWizardPage3.EXPENSE_RELATED_LEASE_TEXT_DATA_KEY, linkedLeaseTV.getText().toString());
                            mPage.getData().putParcelable(ExpenseWizardPage3.EXPENSE_RELATED_LEASE_DATA_KEY, lease);
                        } else {
                            lease = null;
                            linkedLeaseTV.setText("");
                            mPage.getData().putInt(ExpenseWizardPage3.EXPENSE_RELATED_LEASE_ID_DATA_KEY, 0);
                            mPage.getData().putString(ExpenseWizardPage3.EXPENSE_RELATED_LEASE_TEXT_DATA_KEY, "");
                            mPage.getData().putParcelable(ExpenseWizardPage3.EXPENSE_RELATED_LEASE_DATA_KEY, null);
                            getAvailableLeases();
                        }
                        mainArrayDataMethods.sortLeaseArrayByStartDateDesc(availableLeases);
                        mPage.notifyDataChanged();
                    }
                });
            }
        });
        mainArrayDataMethods.sortTenantArrayAlphabetically(availableTenants);
        mainArrayDataMethods.sortApartmentArrayAlphabetically(availableApartments);
        mainArrayDataMethods.sortLeaseArrayByStartDateDesc(availableLeases);
        isInitializing = false;
    }

    private String getApartmentString() {
        if (apartment != null) {
            StringBuilder builder = new StringBuilder(apartment.getStreet1());
            if (apartment.getStreet2() != null) {
                builder.append(" ");
                builder.append(apartment.getStreet2());
            }
            return builder.toString();
        } else {
            return "";
        }
    }

    private String getTenantString() {
        if (tenant != null) {
            StringBuilder builder = new StringBuilder(tenant.getFirstName());
            builder.append(" ");
            builder.append(tenant.getLastName());
            return builder.toString();
        } else {
            return "";
        }
    }

    private String getLeaseString() {
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
        if (lease != null) {
            StringBuilder builder = new StringBuilder(formatter.format(lease.getLeaseStart()));
            builder.append(" - ");
            builder.append(formatter.format(lease.getLeaseEnd()));
            //if (tenant != null) {
            //    builder.append(tenant.getFirstName());
            //    builder.append(" ");
            //    builder.append(tenant.getLastName());
            //    builder.append("\n");
            //} else {
            //    builder.append("\n");
            //}
            //if (apartment != null) {
            //    builder.append(apartment.getStreet1());
            //    builder.append("\n");
            //    if (apartment.getStreet2() != null) {
            //        builder.append(apartment.getStreet2());
            //    }
            //} else {
            //    builder.append("\n");
            //}
            return builder.toString();
        } else {
            return "";
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(tenantApartmentOrLeaseChooserDialog != null){
            tenantApartmentOrLeaseChooserDialog.dismiss();
        }
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

    private void getAvailableLeases() {
        if (apartment != null && tenant != null) {
            this.availableLeases = dbHandler.getUsersLeasesForTenantAndApartment(MainActivity.user, tenant.getId(), apartment.getId());
        } else if (apartment != null) {
            this.availableLeases = dbHandler.getUsersLeasesForApartment(MainActivity.user, apartment.getId());
        } else if (tenant != null) {
            this.availableLeases = dbHandler.getUsersLeasesForTenant(MainActivity.user, tenant.getId());
        } else {
            this.availableLeases = new ArrayList<>();
        }
        if(lease != null) {
            for(int i = 0; i < availableLeases.size(); i++){
                if(availableLeases.get(i).getId() == lease.getId()){
                    availableLeases.remove(availableLeases.get(i));
                    break;
                }
            }
        }
    }

    private void resetLeaseSelection() {
        //if(lease != null) {
        //    availableLeases.add(lease);
        //    mainArrayDataMethods.sortLeaseArrayByStartDateDesc(availableLeases);
        //}
        lease = null;
        linkedLeaseTV.setText("");
        mPage.getData().putInt(ExpenseWizardPage3.EXPENSE_RELATED_LEASE_ID_DATA_KEY, 0);
        mPage.getData().putString(ExpenseWizardPage3.EXPENSE_RELATED_LEASE_TEXT_DATA_KEY, linkedLeaseTV.getText().toString());
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
                apartment = dbHandler.getApartmentByID(expenseToEdit.getApartmentID(), MainActivity.user);
                if (apartment != null) {
                    mPage.getData().putInt(ExpenseWizardPage3.EXPENSE_RELATED_APT_ID_DATA_KEY, apartment.getId());
                    mPage.getData().putString(ExpenseWizardPage3.EXPENSE_RELATED_APT_TEXT_DATA_KEY, getApartmentString());
                    mPage.getData().putParcelable(ExpenseWizardPage3.EXPENSE_RELATED_APT_DATA_KEY, apartment);
                }
            }
            if (expenseToEdit.getTenantID() != 0) {
                tenant = dbHandler.getTenantByID(expenseToEdit.getTenantID(), MainActivity.user);
                if (tenant != null) {
                    mPage.getData().putInt(ExpenseWizardPage3.EXPENSE_RELATED_TENANT_ID_DATA_KEY, tenant.getId());
                    mPage.getData().putString(ExpenseWizardPage3.EXPENSE_RELATED_TENANT_TEXT_DATA_KEY, getTenantString());
                    mPage.getData().putParcelable(ExpenseWizardPage3.EXPENSE_RELATED_TENANT_DATA_KEY, tenant);

                }
            }
            if (expenseToEdit.getLeaseID() != 0) {
                lease = dbHandler.getLeaseByID(MainActivity.user, expenseToEdit.getLeaseID());
                if (lease != null) {
                    mPage.getData().putInt(ExpenseWizardPage3.EXPENSE_RELATED_LEASE_ID_DATA_KEY, lease.getId());
                    mPage.getData().putString(ExpenseWizardPage3.EXPENSE_RELATED_LEASE_TEXT_DATA_KEY, getLeaseString());
                    mPage.getData().putParcelable(ExpenseWizardPage3.EXPENSE_RELATED_LEASE_DATA_KEY, lease);
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
                apartment = mPage.getData().getParcelable(ExpenseWizardPage3.EXPENSE_RELATED_APT_DATA_KEY);
            } else {
                apartment = dbHandler.getApartmentByID(mPage.getData().getInt(ExpenseWizardPage3.EXPENSE_RELATED_APT_ID_DATA_KEY), MainActivity.user);
                mPage.getData().putString(ExpenseWizardPage3.EXPENSE_RELATED_APT_TEXT_DATA_KEY, getApartmentString());
                mPage.getData().putParcelable(ExpenseWizardPage3.EXPENSE_RELATED_APT_DATA_KEY, apartment);
            }
        } else if (bundle.getParcelable("preloadedApartment") != null) {
            apartment = bundle.getParcelable("preloadedApartment");
            mPage.getData().putInt(ExpenseWizardPage3.EXPENSE_RELATED_APT_ID_DATA_KEY, apartment.getId());
            mPage.getData().putString(ExpenseWizardPage3.EXPENSE_RELATED_APT_TEXT_DATA_KEY, getApartmentString());
            mPage.getData().putParcelable(ExpenseWizardPage3.EXPENSE_RELATED_APT_DATA_KEY, apartment);
        } else if (bundle.getInt("preloadedApartmentID") != 0) {
            apartment = dbHandler.getApartmentByID(bundle.getInt("preloadedApartmentID"), MainActivity.user);
            mPage.getData().putInt(ExpenseWizardPage3.EXPENSE_RELATED_APT_ID_DATA_KEY, apartment.getId());
            mPage.getData().putString(ExpenseWizardPage3.EXPENSE_RELATED_APT_TEXT_DATA_KEY, getApartmentString());
            mPage.getData().putParcelable(ExpenseWizardPage3.EXPENSE_RELATED_APT_DATA_KEY, apartment);
        } else {
            apartment = null;
        }
    }

    private void preloadTenant(Bundle bundle) {
        if (mPage.getData().getInt(ExpenseWizardPage3.EXPENSE_RELATED_TENANT_ID_DATA_KEY, 0) != 0) {
            //If re-loaded with an id
            if (mPage.getData().getParcelable(ExpenseWizardPage3.EXPENSE_RELATED_TENANT_DATA_KEY) != null) {
                //and tenant is not null
                tenant = mPage.getData().getParcelable(ExpenseWizardPage3.EXPENSE_RELATED_TENANT_DATA_KEY);
            } else {
                //and tenant is for some reason null
                tenant = dbHandler.getTenantByID(mPage.getData().getInt(ExpenseWizardPage3.EXPENSE_RELATED_TENANT_ID_DATA_KEY), MainActivity.user);
                mPage.getData().putString(ExpenseWizardPage3.EXPENSE_RELATED_TENANT_TEXT_DATA_KEY, getTenantString());
                mPage.getData().putParcelable(ExpenseWizardPage3.EXPENSE_RELATED_TENANT_DATA_KEY, tenant);
            }
        } else if (bundle.getParcelable("preloadedTenant") != null) {
            //If loaded first time with preloaded tenant
            tenant = bundle.getParcelable("preloadedTenant");
            mPage.getData().putInt(ExpenseWizardPage3.EXPENSE_RELATED_TENANT_ID_DATA_KEY, tenant.getId());
            mPage.getData().putString(ExpenseWizardPage3.EXPENSE_RELATED_TENANT_TEXT_DATA_KEY, getTenantString());
            mPage.getData().putParcelable(ExpenseWizardPage3.EXPENSE_RELATED_TENANT_DATA_KEY, tenant);
        } else if (bundle.getInt("preloadedTenantID") != 0) {
            //If loaded first time with tenant id
            tenant = dbHandler.getTenantByID(bundle.getInt("preloadedTenantID"), MainActivity.user);
            mPage.getData().putInt(ExpenseWizardPage3.EXPENSE_RELATED_TENANT_ID_DATA_KEY, tenant.getId());
            mPage.getData().putString(ExpenseWizardPage3.EXPENSE_RELATED_TENANT_TEXT_DATA_KEY, getTenantString());
            mPage.getData().putParcelable(ExpenseWizardPage3.EXPENSE_RELATED_TENANT_DATA_KEY, tenant);
        } else {
            //If no tenant id
            tenant = null;
        }
    }

    private void preloadLease(Bundle bundle) {
        if (mPage.getData().getInt(ExpenseWizardPage3.EXPENSE_RELATED_LEASE_ID_DATA_KEY, 0) != 0) {
            //If re-loaded with an id
            if (mPage.getData().getParcelable(ExpenseWizardPage3.EXPENSE_RELATED_LEASE_DATA_KEY) != null) {
                //and lease is not null
                lease = mPage.getData().getParcelable(ExpenseWizardPage3.EXPENSE_RELATED_LEASE_DATA_KEY);
            } else {
                //and lease is for some reason null
                lease = dbHandler.getLeaseByID(MainActivity.user, mPage.getData().getInt(ExpenseWizardPage3.EXPENSE_RELATED_LEASE_ID_DATA_KEY));
                mPage.getData().putString(ExpenseWizardPage3.EXPENSE_RELATED_LEASE_TEXT_DATA_KEY, getLeaseString());
                mPage.getData().putParcelable(ExpenseWizardPage3.EXPENSE_RELATED_LEASE_DATA_KEY, lease);
            }
        } else if (bundle.getParcelable("preloadedLease") != null) {
            //If loaded first time with preloaded lease
            lease = bundle.getParcelable("preloadedLease");
            if (tenant == null) {
                preLoadTenantForLease(lease);
            }
            if (apartment == null) {
                preloadApartmentForLease(lease);
            }
            mPage.getData().putInt(ExpenseWizardPage3.EXPENSE_RELATED_LEASE_ID_DATA_KEY, lease.getId());
            mPage.getData().putString(ExpenseWizardPage3.EXPENSE_RELATED_LEASE_TEXT_DATA_KEY, getLeaseString());
            mPage.getData().putParcelable(ExpenseWizardPage3.EXPENSE_RELATED_LEASE_DATA_KEY, lease);
        } else if (bundle.getInt("preloadedLeaseID") != 0) {
            //If loaded first time with lease id
            lease = dbHandler.getLeaseByID(MainActivity.user, bundle.getInt("preloadedLeaseID"));
            if (tenant == null) {
                preLoadTenantForLease(lease);
            }
            if (apartment == null) {
                preloadApartmentForLease(lease);
            }
            mPage.getData().putInt(ExpenseWizardPage3.EXPENSE_RELATED_LEASE_ID_DATA_KEY, lease.getId());
            mPage.getData().putString(ExpenseWizardPage3.EXPENSE_RELATED_LEASE_TEXT_DATA_KEY, getLeaseString());
            mPage.getData().putParcelable(ExpenseWizardPage3.EXPENSE_RELATED_LEASE_DATA_KEY, lease);
        } else {
            //If no lease id
            lease = null;
        }
    }

    private void preLoadTenantForLease(Lease lease) {
        if (lease.getPrimaryTenantID() != 0) {
            tenant = dbHandler.getTenantByID(lease.getPrimaryTenantID(), MainActivity.user);
            mPage.getData().putInt(ExpenseWizardPage3.EXPENSE_RELATED_TENANT_ID_DATA_KEY, tenant.getId());
            mPage.getData().putString(ExpenseWizardPage3.EXPENSE_RELATED_TENANT_TEXT_DATA_KEY, getTenantString());
            mPage.getData().putParcelable(ExpenseWizardPage3.EXPENSE_RELATED_TENANT_DATA_KEY, tenant);
        }
    }

    private void preloadApartmentForLease(Lease lease) {
        if (lease.getApartmentID() != 0) {
            apartment = dbHandler.getApartmentByID(lease.getApartmentID(), MainActivity.user);
            mPage.getData().putInt(ExpenseWizardPage3.EXPENSE_RELATED_APT_ID_DATA_KEY, apartment.getId());
            mPage.getData().putString(ExpenseWizardPage3.EXPENSE_RELATED_APT_TEXT_DATA_KEY, getApartmentString());
            mPage.getData().putParcelable(ExpenseWizardPage3.EXPENSE_RELATED_APT_DATA_KEY, apartment);
        }
    }
}

