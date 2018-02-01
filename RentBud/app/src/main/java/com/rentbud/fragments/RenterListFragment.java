package com.rentbud.fragments;

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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.cody.rentbud.R;
import com.rentbud.adapters.RentalListAdapter;
import com.rentbud.adapters.RenterListAdapter;
import com.rentbud.model.Tenant;

import java.util.ArrayList;

/**
 * Created by Cody on 1/11/2018.
 */

public class RenterListFragment extends Fragment{
    ArrayList<Tenant> tenantArray;
    TextView noTenantsTV;
    EditText searchBarET;
    RenterListAdapter renterListAdapter;
    ColorStateList accentColor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_renter_list, container, false);
        noTenantsTV = rootView.findViewById(R.id.noRenterEmptyListTV);
        searchBarET = rootView.findViewById(R.id.renterListSearchET);
        ListView listView = (ListView) rootView.findViewById(R.id.mainRenterListView);

        TypedValue colorValue = new TypedValue();
        getActivity().getTheme().resolveAttribute(R.attr.colorAccent, colorValue, true);
        accentColor = getActivity().getResources().getColorStateList(colorValue.resourceId);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            tenantArray = bundle.getParcelableArrayList("RenterList");
        }
        if (tenantArray != null) {
            if (!tenantArray.isEmpty()) {
                renterListAdapter = new RenterListAdapter(getActivity(), tenantArray, accentColor);
                listView.setAdapter(renterListAdapter);
            } else {
                noTenantsTV.setVisibility(View.VISIBLE);
            }
        } else {
            noTenantsTV.setVisibility(View.VISIBLE);
        }

        searchBarET.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                renterListAdapter.getFilter().filter(cs);
                renterListAdapter.notifyDataSetChanged();
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
        // Inflate the layout for this fragment
        return rootView;
    }



    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Renter View");
    }
}
