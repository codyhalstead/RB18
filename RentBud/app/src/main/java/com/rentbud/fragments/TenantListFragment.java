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
import com.rentbud.model.Tenant;

/**
 * Created by Cody on 1/11/2018.
 */

public class TenantListFragment extends Fragment implements AdapterView.OnItemClickListener {
    TextView noTenantsTV;
    EditText searchBarET;
    TenantListAdapter tenantListAdapter;
    ColorStateList accentColor;
    ListView listView;

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
        getActivity().setTitle("Tenant View");
        //Get current theme accent color, which is passed into the list adapter for search highlighting
        TypedValue colorValue = new TypedValue();
        getActivity().getTheme().resolveAttribute(R.attr.colorAccent, colorValue, true);
        accentColor = getActivity().getResources().getColorStateList(colorValue.resourceId);
        setUpSearchBar();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        //On listView row click, launch TenantViewActivity passing the rows data into it.
        Intent intent = new Intent(getContext(), TenantViewActivity.class);
        //Uses filtered results to match what is on screen
        Tenant tenant = tenantListAdapter.getFilteredResults().get(i);
        intent.putExtra("tenant", tenant);
        startActivity(intent);
    }

    private void setUpSearchBar() {
        if (MainActivity.tenantList != null) {
            if (!MainActivity.tenantList.isEmpty()) {
                //If MainActivity.tenantList is not null or empty, set apartment list adapter
                tenantListAdapter = new TenantListAdapter(getActivity(), MainActivity.tenantList, accentColor);
                listView.setAdapter(tenantListAdapter);
                listView.setOnItemClickListener(this);
            } else {
                //If MainActivity.tenantList is not null but is empty, show empty list text
                noTenantsTV.setVisibility(View.VISIBLE);
            }
        } else {
            //If MainActivity.tenantList is null show empty list text
            noTenantsTV.setVisibility(View.VISIBLE);
        }
        searchBarET.addTextChangedListener(new TextWatcher() {
            //For updating search results as user types
            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                //When user changed the Text
                if (tenantListAdapter != null) {
                    tenantListAdapter.getFilter().filter(cs);
                    tenantListAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
                // TODO Auto-generated method stub

            }
        });
    }
}
