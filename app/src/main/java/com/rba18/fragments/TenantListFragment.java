package com.rba18.fragments;

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

import com.rba18.R;
import com.rba18.activities.MainActivity;
import com.rba18.activities.TenantViewActivity;
import com.rba18.adapters.TenantListAdapter;
import com.rba18.model.Tenant;

import java.util.ArrayList;

/**
 * Created by Cody on 1/11/2018.
 */

public class TenantListFragment extends Fragment implements AdapterView.OnItemClickListener {
    private TextView mNoTenantsTV;
    private EditText mSearchBarET;
    private TenantListAdapter mTenantListAdapter;
    private ColorStateList mAccentColor;
    private ListView mListView;
    private boolean mNeedsRefreshedOnResume;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_tenant_list, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mNoTenantsTV = view.findViewById(R.id.notenantEmptyListTV);
        mSearchBarET = view.findViewById(R.id.tenantListSearchET);
        mListView = view.findViewById(R.id.maintenantListView);
        getActivity().setTitle(R.string.tenant_list);
        //Get current theme accent color, which is passed into the list adapter for search highlighting
        TypedValue colorValue = new TypedValue();
        getActivity().getTheme().resolveAttribute(R.attr.colorAccent, colorValue, true);
        mAccentColor = getActivity().getResources().getColorStateList(colorValue.resourceId);
        setUpListAdapter();
        setUpSearchBar();
        mNeedsRefreshedOnResume = false;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mNeedsRefreshedOnResume) {
            ArrayList<Tenant> activeTenantArray = new ArrayList<>();
            for (int i = 0; i < MainActivity.sTenantList.size(); i++) {
                if (MainActivity.sTenantList.get(i).isActive()) {
                    activeTenantArray.add(MainActivity.sTenantList.get(i));
                }
            }
            mTenantListAdapter.updateResults(activeTenantArray);
            mSearchBarET.setText(mSearchBarET.getText());
            mSearchBarET.setSelection(mSearchBarET.getText().length());
        }
        mNeedsRefreshedOnResume = true;

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        //On mListView row click, launch TenantViewActivity passing the rows data into it.
        Intent intent = new Intent(getContext(), TenantViewActivity.class);
        //Uses filtered results to match what is on screen
        Tenant tenant = mTenantListAdapter.getFilteredResults().get(i);
        intent.putExtra("tenantID", tenant.getId());
        getActivity().startActivityForResult(intent, MainActivity.REQUEST_TENANT_VIEW);
    }

    private void setUpSearchBar() {
        mSearchBarET.addTextChangedListener(new TextWatcher() {
            //For updating search results as sUser types
            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                //When sUser changed the Text
                if (mTenantListAdapter != null) {
                    mListView.setFilterText(cs.toString());
                    mTenantListAdapter.getFilter().filter(cs.toString());
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
        for (int i = 0; i < MainActivity.sTenantList.size(); i++) {
            if (MainActivity.sTenantList.get(i).isActive()) {
                activeTenantArray.add(MainActivity.sTenantList.get(i));
            }
        }
        mTenantListAdapter = new TenantListAdapter(getActivity(), activeTenantArray, mAccentColor);
        mListView.setAdapter(mTenantListAdapter);
        mListView.setOnItemClickListener(this);
        mNoTenantsTV.setText(R.string.no_tenants_to_display);
        mListView.setEmptyView(mNoTenantsTV);
    }
}
