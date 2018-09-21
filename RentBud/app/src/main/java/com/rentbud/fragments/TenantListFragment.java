package com.rentbud.fragments;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.cody.rentbud.R;
import com.rentbud.activities.MainActivity;
import com.rentbud.activities.TenantViewActivity;
import com.rentbud.adapters.TenantListAdapter;
import com.rentbud.helpers.MainArrayDataMethods;
import com.rentbud.model.Tenant;

import java.util.ArrayList;

/**
 * Created by Cody on 1/11/2018.
 */

public class TenantListFragment extends Fragment implements AdapterView.OnItemClickListener {
    TextView noTenantsTV;
    EditText searchBarET;
    TenantListAdapter tenantListAdapter;
    ColorStateList accentColor;
    ListView listView;
    MainArrayDataMethods dataMethods;
    private boolean needsRefreshedOnResume;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_tenant_list, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.noTenantsTV = view.findViewById(R.id.notenantEmptyListTV);
        this.searchBarET = view.findViewById(R.id.tenantListSearchET);
        this.listView = view.findViewById(R.id.maintenantListView);
        getActivity().setTitle(R.string.tenant_list);
        dataMethods = new MainArrayDataMethods();
        //TenantListFragment.tenantListAdapterNeedsRefreshed = false;
        //Get current theme accent color, which is passed into the list adapter for search highlighting
        TypedValue colorValue = new TypedValue();
        getActivity().getTheme().resolveAttribute(R.attr.colorAccent, colorValue, true);
        accentColor = getActivity().getResources().getColorStateList(colorValue.resourceId);
        setUpListAdapter();
        setUpSearchBar();
        needsRefreshedOnResume = false;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (needsRefreshedOnResume) {
            ArrayList<Tenant> activeTenantArray = new ArrayList<>();
            for (int i = 0; i < MainActivity.tenantList.size(); i++) {
                if (MainActivity.tenantList.get(i).isActive()) {
                    activeTenantArray.add(MainActivity.tenantList.get(i));
                }
            }
            tenantListAdapter.updateResults(activeTenantArray);
            searchBarET.setText(searchBarET.getText());
            searchBarET.setSelection(searchBarET.getText().length());
        }
        needsRefreshedOnResume = true;

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        //On listView row click, launch TenantViewActivity passing the rows data into it.
        Intent intent = new Intent(getContext(), TenantViewActivity.class);
        //Uses filtered results to match what is on screen
        Tenant tenant = tenantListAdapter.getFilteredResults().get(i);
        //    Lease currentLease = dataMethods.getCachedActiveLeaseByTenantID(tenant.getId());
        intent.putExtra("tenantID", tenant.getId());
        //    intent.putExtra("apartmentID", currentLease.getApartmentID());
        getActivity().startActivityForResult(intent, MainActivity.REQUEST_TENANT_VIEW);
    }

    private void setUpSearchBar() {
        searchBarET.addTextChangedListener(new TextWatcher() {
            //For updating search results as user types
            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                //When user changed the Text
                if (tenantListAdapter != null) {
                    listView.setFilterText(cs.toString());//.replace(" ", ""));
                    tenantListAdapter.getFilter().filter(cs.toString());//.replace(" ", ""));
                    //   tenantListAdapter.notifyDataSetChanged();
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

    private void setUpListAdapter() {
        ArrayList<Tenant> activeTenantArray = new ArrayList<>();
        for (int i = 0; i < MainActivity.tenantList.size(); i++) {
            if (MainActivity.tenantList.get(i).isActive()) {
                activeTenantArray.add(MainActivity.tenantList.get(i));
            }
        }
        tenantListAdapter = new TenantListAdapter(getActivity(), activeTenantArray, accentColor);
        listView.setAdapter(tenantListAdapter);
        listView.setOnItemClickListener(this);
        noTenantsTV.setText(R.string.no_tenants_to_display);
        listView.setEmptyView(noTenantsTV);
    }
}
