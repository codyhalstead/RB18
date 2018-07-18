package com.rentbud.helpers;

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

import com.example.cody.rentbud.R;
import com.rentbud.activities.MainActivity;
import com.rentbud.adapters.ApartmentDialogListAdapter;
import com.rentbud.adapters.LeaseDialogListAdapter;
import com.rentbud.adapters.TenantDialogListAdapter;
import com.rentbud.model.Apartment;
import com.rentbud.model.Lease;
import com.rentbud.model.Tenant;

import java.util.ArrayList;

/**
 * Created by Cody on 2/27/2018.
 */

public class TenantOrApartmentChooserDialog extends Dialog implements AdapterView.OnItemClickListener {
    public TenantOrApartmentChooserDialog(@NonNull Context context, int type, ArrayList<?> theList) {
        super(context);
        this.context = context;
        this.type = type;
        if (type == TENANT_TYPE) {
            availableTenants = (ArrayList<Tenant>) theList;
        } else if (type == APARTMENT_TYPE) {
            availableApartments = (ArrayList<Apartment>) theList;
        } else if (type == SECONDARY_TENANT_TYPE) {
            availableTenants = (ArrayList<Tenant>) theList;
        } else if (type == LEASE_TYPE) {
            availableLeases = (ArrayList<Lease>) theList;
        }
    }

    private Context context;
    private int type;
    private ArrayList<Tenant> availableTenants;
    private ArrayList<Apartment> availableApartments;
    private ArrayList<Lease> availableLeases;

    private TextView cancelTV, selectionTypeTV, emptyListTV;
    private EditText searchBarET;
    private ListView listView;
    private ColorStateList accentColor;
    private TenantDialogListAdapter tenantListAdapter;
    private ApartmentDialogListAdapter apartmentListAdapter;
    private LeaseDialogListAdapter leaseListAdapter;
    private OnTenantChooserDialogResult dialogResult;

    public static final int TENANT_TYPE = 45;
    public static final int APARTMENT_TYPE = 54;
    public static final int SECONDARY_TENANT_TYPE = 63;
    public static final int LEASE_TYPE = 36;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_list_chooser);
        this.searchBarET = findViewById(R.id.popupListSearchET);
        this.listView = findViewById(R.id.popupListListView);
        this.cancelTV = findViewById(R.id.popupListCancelTV);
        this.selectionTypeTV = findViewById(R.id.popupListSelectTypeTV);
        this.emptyListTV = findViewById(R.id.popupListEmptyListTV);
        if (type == TENANT_TYPE) {
            selectionTypeTV.setText("Select A Tenant");
            emptyListTV.setText("No Available Tenants");
        }
        if (type == APARTMENT_TYPE) {
            selectionTypeTV.setText("Select An Apartment");
            emptyListTV.setText("No Available Apartments");
        }
        if (type == SECONDARY_TENANT_TYPE) {
            selectionTypeTV.setText("Select Secondary Tenant");
            emptyListTV.setText("No Available Tenants");
        }
        if (type == LEASE_TYPE) {
            selectionTypeTV.setText("Select A Lease");
            emptyListTV.setText("No Available Leases");
        }
        TypedValue colorValue = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.colorAccent, colorValue, true);
        this.accentColor = context.getResources().getColorStateList(colorValue.resourceId);
        setUpListAdapter();
        setUpSearchBar();
        cancelTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogResult.finish(null, null, null);
                TenantOrApartmentChooserDialog.this.dismiss();
            }
        });
    }

    private void setUpListAdapter() {
        if (type == TENANT_TYPE) {
            setUpTenantListAdapter();
        } else if (type == APARTMENT_TYPE) {
            setUpApartmentListAdapter();
        } else if (type == SECONDARY_TENANT_TYPE) {
            setUpTenantListAdapter();
        } else if (type == LEASE_TYPE) {
            setUpLeaseListAdapter();
        }
    }

    private void setUpTenantListAdapter() {
        if (availableTenants != null) {
            if (!availableTenants.isEmpty()) {
                //If MainActivity5.tenantList is not null or empty, set apartment list adapter
                tenantListAdapter = new TenantDialogListAdapter(context, availableTenants, accentColor);
                listView.setAdapter(tenantListAdapter);
                listView.setOnItemClickListener(this);
            } else {
                //If MainActivity5.tenantList is not null but is empty, show empty list text
                emptyListTV.setVisibility(View.VISIBLE);
            }
        } else {
            //If MainActivity5.tenantList is null show empty list text
            emptyListTV.setVisibility(View.VISIBLE);
        }
    }

    public void changeCancelBtnText(String string){
        this.cancelTV.setText(string);
    }

    private void setUpApartmentListAdapter() {
        if (availableApartments != null) {
            if (!availableApartments.isEmpty()) {
                apartmentListAdapter = new ApartmentDialogListAdapter(context, availableApartments, accentColor);
                listView.setAdapter(apartmentListAdapter);
                listView.setOnItemClickListener(this);
            } else {
                emptyListTV.setVisibility(View.VISIBLE);
            }
        } else {
            emptyListTV.setVisibility(View.VISIBLE);
        }
    }

    private void setUpLeaseListAdapter() {
        if (availableLeases != null) {
            if (!availableLeases.isEmpty()) {
                //If MainActivity5.tenantList is not null or empty, set apartment list adapter
                leaseListAdapter = new LeaseDialogListAdapter(context, availableLeases, accentColor);
                listView.setAdapter(leaseListAdapter);
                listView.setOnItemClickListener(this);
            } else {
                //If MainActivity5.tenantList is not null but is empty, show empty list text
                emptyListTV.setVisibility(View.VISIBLE);
            }
        } else {
            //If MainActivity5.tenantList is null show empty list text
            emptyListTV.setVisibility(View.VISIBLE);
        }
    }


    private void setUpSearchBar() {
        if (type == LEASE_TYPE) {
            searchBarET.setVisibility(View.GONE);
        } else {
            searchBarET.addTextChangedListener(new TextWatcher() {
                //For updating search results as user types
                @Override
                public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                    //When user changed the Text
                    if (type == TENANT_TYPE) {
                        if (tenantListAdapter != null) {
                            tenantListAdapter.getFilter().filter(cs);
                            tenantListAdapter.notifyDataSetChanged();
                        }
                    } else if (type == APARTMENT_TYPE) {
                        if (apartmentListAdapter != null) {
                            apartmentListAdapter.getFilter().filter(cs);
                            apartmentListAdapter.notifyDataSetChanged();
                        }
                    } else if (type == SECONDARY_TENANT_TYPE) {
                        if (tenantListAdapter != null) {
                            tenantListAdapter.getFilter().filter(cs);
                            tenantListAdapter.notifyDataSetChanged();
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
        this.dialogResult = dialogResult;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if (type == TENANT_TYPE) {
            Tenant tenant = tenantListAdapter.getFilteredResults().get(i);
            dialogResult.finish(tenant, null, null);
            TenantOrApartmentChooserDialog.this.dismiss();
        } else if (type == APARTMENT_TYPE) {
            Apartment apartment = apartmentListAdapter.getFilteredResults().get(i);
            dialogResult.finish(null, apartment, null);
            TenantOrApartmentChooserDialog.this.dismiss();
        } else if (type == SECONDARY_TENANT_TYPE) {
            Tenant tenant = tenantListAdapter.getFilteredResults().get(i);
            dialogResult.finish(tenant, null, null);
            TenantOrApartmentChooserDialog.this.dismiss();
        } else if (type == LEASE_TYPE){
            Lease lease = leaseListAdapter.getFilteredResults().get(i);
            dialogResult.finish(null, null, lease);
            TenantOrApartmentChooserDialog.this.dismiss();
        }
    }

}