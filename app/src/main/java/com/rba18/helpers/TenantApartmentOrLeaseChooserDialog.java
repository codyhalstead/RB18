package com.rba18.helpers;

import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.rba18.R;
import com.rba18.adapters.ApartmentDialogListAdapter;
import com.rba18.adapters.LeaseDialogListAdapter;
import com.rba18.adapters.TenantDialogListAdapter;
import com.rba18.model.Apartment;
import com.rba18.model.Lease;
import com.rba18.model.Tenant;

import java.util.ArrayList;

/**
 * Created by Cody on 2/27/2018.
 */

public class TenantApartmentOrLeaseChooserDialog extends Dialog implements AdapterView.OnItemClickListener {
    private Context mContext;
    private int mType;
    private ArrayList<Tenant> mAvailableTenants;
    private ArrayList<Apartment> mAvailableApartments;
    private ArrayList<Lease> mAvailableLeases;
    private TextView mCancelTV, mSelectionTypeTV, mEmptyListTV;
    private EditText mSearchBarET;
    private ListView mListView;
    private ColorStateList mAccentColor;
    private TenantDialogListAdapter mTenantListAdapter;
    private ApartmentDialogListAdapter mApartmentListAdapter;
    private LeaseDialogListAdapter mLeaseListAdapter;
    private OnTenantChooserDialogResult mDialogResult;

    public static final int TENANT_TYPE = 45;
    public static final int APARTMENT_TYPE = 54;
    public static final int SECONDARY_TENANT_TYPE = 63;
    public static final int LEASE_TYPE = 36;

    public TenantApartmentOrLeaseChooserDialog(@NonNull Context context, int type, ArrayList<?> theList) {
        super(context);
        mContext = context;
        mType = type;
        if (type == TENANT_TYPE) {
            mAvailableTenants = (ArrayList<Tenant>) theList;
        } else if (type == APARTMENT_TYPE) {
            mAvailableApartments = (ArrayList<Apartment>) theList;
        } else if (type == SECONDARY_TENANT_TYPE) {
            mAvailableTenants = (ArrayList<Tenant>) theList;
        } else if (type == LEASE_TYPE) {
            mAvailableLeases = (ArrayList<Lease>) theList;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_list_chooser);
        mSearchBarET = findViewById(R.id.popupListSearchET);
        mListView = findViewById(R.id.popupListListView);
        mCancelTV = findViewById(R.id.popupListCancelTV);
        mSelectionTypeTV = findViewById(R.id.popupListSelectTypeTV);
        mEmptyListTV = findViewById(R.id.popupListEmptyListTV);
        if (mType == TENANT_TYPE) {
            mSelectionTypeTV.setText(R.string.select_a_tenant);
            mEmptyListTV.setText(R.string.no_available_tenants);
        }
        if (mType == APARTMENT_TYPE) {
            mSelectionTypeTV.setText(R.string.select_an_apartment);
            mEmptyListTV.setText(R.string.no_available_apartments);
        }
        if (mType == SECONDARY_TENANT_TYPE) {
            mSelectionTypeTV.setText(R.string.select_secondary_tenant);
            mEmptyListTV.setText(R.string.no_available_tenants);
        }
        if (mType == LEASE_TYPE) {
            mSelectionTypeTV.setText(R.string.select_a_lease);
            mEmptyListTV.setText(R.string.no_available_leases);
        }
        TypedValue colorValue = new TypedValue();
        mContext.getTheme().resolveAttribute(R.attr.colorAccent, colorValue, true);
        mAccentColor = mContext.getResources().getColorStateList(colorValue.resourceId);
        setUpListAdapter();
        setUpSearchBar();
        mCancelTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialogResult.finish(null, null, null);
                TenantApartmentOrLeaseChooserDialog.this.dismiss();
            }
        });
    }

    private void setUpListAdapter() {
        if (mType == TENANT_TYPE) {
            setUpTenantListAdapter();
        } else if (mType == APARTMENT_TYPE) {
            setUpApartmentListAdapter();
        } else if (mType == SECONDARY_TENANT_TYPE) {
            setUpTenantListAdapter();
        } else if (mType == LEASE_TYPE) {
            setUpLeaseListAdapter();
        }
    }

    private void setUpTenantListAdapter() {
        mTenantListAdapter = new TenantDialogListAdapter(mContext, mAvailableTenants, mAccentColor);
        mListView.setAdapter(mTenantListAdapter);
        mListView.setOnItemClickListener(this);
        mListView.setEmptyView(mEmptyListTV);
    }

    public void changeCancelBtnText(String string){
        mCancelTV.setText(string);
    }

    private void setUpApartmentListAdapter() {
        mApartmentListAdapter = new ApartmentDialogListAdapter(mContext, mAvailableApartments, mAccentColor);
        mListView.setAdapter(mApartmentListAdapter);
        mListView.setOnItemClickListener(this);
        mListView.setEmptyView(mEmptyListTV);
    }

    private void setUpLeaseListAdapter() {
        mLeaseListAdapter = new LeaseDialogListAdapter(mContext, mAvailableLeases, mAccentColor);
        mListView.setAdapter(mLeaseListAdapter);
        mListView.setOnItemClickListener(this);
        mListView.setEmptyView(mEmptyListTV);
    }


    private void setUpSearchBar() {
        if (mType == LEASE_TYPE) {
            mSearchBarET.setVisibility(View.GONE);
        } else {
            mSearchBarET.addTextChangedListener(new TextWatcher() {
                //For updating search results as user fileNames
                @Override
                public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                    //When user changed the Text
                    if (mType == TENANT_TYPE) {
                        if (mTenantListAdapter != null) {
                            mTenantListAdapter.getFilter().filter(cs);
                            mTenantListAdapter.notifyDataSetChanged();
                        }
                    } else if (mType == APARTMENT_TYPE) {
                        if (mApartmentListAdapter != null) {
                            mApartmentListAdapter.getFilter().filter(cs);
                            mApartmentListAdapter.notifyDataSetChanged();
                        }
                    } else if (mType == SECONDARY_TENANT_TYPE) {
                        if (mTenantListAdapter != null) {
                            mTenantListAdapter.getFilter().filter(cs);
                            mTenantListAdapter.notifyDataSetChanged();
                        }
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }

                @Override
                public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                              int arg3) {

                }
            });
        }
    }

    public interface OnTenantChooserDialogResult {
        void finish(Tenant tenant, Apartment apartment, Lease lease);
    }

    public void setDialogResult(OnTenantChooserDialogResult dialogResult) {
        mDialogResult = dialogResult;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if (mType == TENANT_TYPE) {
            Tenant tenant = mTenantListAdapter.getFilteredResults().get(i);
            mDialogResult.finish(tenant, null, null);
            TenantApartmentOrLeaseChooserDialog.this.dismiss();
        } else if (mType == APARTMENT_TYPE) {
            Apartment apartment = mApartmentListAdapter.getFilteredResults().get(i);
            mDialogResult.finish(null, apartment, null);
            TenantApartmentOrLeaseChooserDialog.this.dismiss();
        } else if (mType == SECONDARY_TENANT_TYPE) {
            Tenant tenant = mTenantListAdapter.getFilteredResults().get(i);
            mDialogResult.finish(tenant, null, null);
            TenantApartmentOrLeaseChooserDialog.this.dismiss();
        } else if (mType == LEASE_TYPE){
            Lease lease = mLeaseListAdapter.getFilteredResults().get(i);
            mDialogResult.finish(null, null, lease);
            TenantApartmentOrLeaseChooserDialog.this.dismiss();
        }
    }
}