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
import com.rentbud.activities.ApartmentViewActivity;
import com.rentbud.activities.MainActivity;
import com.rentbud.adapters.ApartmentListAdapter;
import com.rentbud.model.Apartment;

import java.util.ArrayList;

/**
 * Created by Cody on 1/11/2018.
 */

public class ApartmentListFragment extends Fragment implements AdapterView.OnItemClickListener {
    TextView noApartmentsTV;
    EditText searchBarET;
    ApartmentListAdapter apartmentListAdapter;
    ColorStateList accentColor;
    ListView listView;
    //public static boolean apartmentListAdapterNeedsRefreshed;
    private boolean needsRefreshedOnResume;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_apartment_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.noApartmentsTV = view.findViewById(R.id.noapartmentEmptyListTV);
        this.searchBarET = view.findViewById(R.id.apartmentListSearchET);
        this.listView = view.findViewById(R.id.mainApartmentListView);
        getActivity().setTitle(R.string.apartment_list);
        //ApartmentListFragment.apartmentListAdapterNeedsRefreshed = false;
        //Get current theme accent color, which is passed into the list adapter for search highlighting
        TypedValue colorValue = new TypedValue();
        getActivity().getTheme().resolveAttribute(R.attr.colorAccent, colorValue, true);
        this.accentColor = getActivity().getResources().getColorStateList(colorValue.resourceId);
        setUpListAdapter();
        setUpSearchBar();
        needsRefreshedOnResume = false;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (needsRefreshedOnResume) {
            ArrayList<Apartment> activeApartmentArray = new ArrayList<>();
            for (int i = 0; i < MainActivity.apartmentList.size(); i++) {
                if (MainActivity.apartmentList.get(i).isActive()) {
                    activeApartmentArray.add(MainActivity.apartmentList.get(i));
                }
            }
            apartmentListAdapter.updateResults(activeApartmentArray);
            searchBarET.setText(searchBarET.getText());
            searchBarET.setSelection(searchBarET.getText().length());
        }
        needsRefreshedOnResume = true;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        //On listView row click, launch ApartmentViewActivity passing the rows data into it.
        Intent intent = new Intent(getContext(), ApartmentViewActivity.class);
        //Uses filtered results to match what is on screen
        Apartment apartment = apartmentListAdapter.getFilteredResults().get(i);
        intent.putExtra("apartmentID", apartment.getId());
        getActivity().startActivityForResult(intent, MainActivity.REQUEST_APARTMENT_VIEW);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private void setUpSearchBar() {
        searchBarET.addTextChangedListener(new TextWatcher() {
            //For updating search results as user types
            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                //When user changed the Text
                if (apartmentListAdapter != null) {
                    apartmentListAdapter.getFilter().filter(cs);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                //apartmentListAdapter.notifyDataSetChanged();
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {

            }
        });
    }

    private void setUpListAdapter() {
        ArrayList<Apartment> activeApartmentArray = new ArrayList<>();
        for (int i = 0; i < MainActivity.apartmentList.size(); i++) {
            if (MainActivity.apartmentList.get(i).isActive()) {
                activeApartmentArray.add(MainActivity.apartmentList.get(i));
            }
        }
        apartmentListAdapter = new ApartmentListAdapter(getActivity(), activeApartmentArray, accentColor);
        listView.setAdapter(apartmentListAdapter);
        listView.setOnItemClickListener(this);
        noApartmentsTV.setText(R.string.no_apartments_to_display);
        this.listView.setEmptyView(noApartmentsTV);
    }

}