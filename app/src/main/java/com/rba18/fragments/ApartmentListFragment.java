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
import com.rba18.activities.ApartmentViewActivity;
import com.rba18.activities.MainActivity;
import com.rba18.adapters.ApartmentListAdapter;
import com.rba18.model.Apartment;

import java.util.ArrayList;

/**
 * Created by Cody on 1/11/2018.
 */

public class ApartmentListFragment extends Fragment implements AdapterView.OnItemClickListener {
    private TextView mNoApartmentsTV;
    private EditText mSearchBarET;
    private ApartmentListAdapter mApartmentListAdapter;
    private ColorStateList mAccentColor;
    private ListView mListView;
    private boolean mNeedsRefreshedOnResume;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_apartment_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mNoApartmentsTV = view.findViewById(R.id.noapartmentEmptyListTV);
        mSearchBarET = view.findViewById(R.id.apartmentListSearchET);
        mListView = view.findViewById(R.id.mainApartmentListView);
        getActivity().setTitle(R.string.apartment_list);
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
            ArrayList<Apartment> activeApartmentArray = new ArrayList<>();
            for (int i = 0; i < MainActivity.sApartmentList.size(); i++) {
                if (MainActivity.sApartmentList.get(i).isActive()) {
                    activeApartmentArray.add(MainActivity.sApartmentList.get(i));
                }
            }
            mApartmentListAdapter.updateResults(activeApartmentArray);
            mSearchBarET.setText(mSearchBarET.getText());
            mSearchBarET.setSelection(mSearchBarET.getText().length());
        }
        mNeedsRefreshedOnResume = true;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        //On mListView row click, launch ApartmentViewActivity passing the rows data into it.
        Intent intent = new Intent(getContext(), ApartmentViewActivity.class);
        //Uses filtered results to match what is on screen
        Apartment apartment = mApartmentListAdapter.getFilteredResults().get(i);
        intent.putExtra("apartmentID", apartment.getId());
        getActivity().startActivityForResult(intent, MainActivity.REQUEST_APARTMENT_VIEW);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private void setUpSearchBar() {
        mSearchBarET.addTextChangedListener(new TextWatcher() {
            //For updating search results as sUser types
            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                //When sUser changed the Text
                if (mApartmentListAdapter != null) {
                    mApartmentListAdapter.getFilter().filter(cs);
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
        ArrayList<Apartment> activeApartmentArray = new ArrayList<>();
        for (int i = 0; i < MainActivity.sApartmentList.size(); i++) {
            if (MainActivity.sApartmentList.get(i).isActive()) {
                activeApartmentArray.add(MainActivity.sApartmentList.get(i));
            }
        }
        mApartmentListAdapter = new ApartmentListAdapter(getActivity(), activeApartmentArray, mAccentColor);
        mListView.setAdapter(mApartmentListAdapter);
        mListView.setOnItemClickListener(this);
        mNoApartmentsTV.setText(R.string.no_apartments_to_display);
        mListView.setEmptyView(mNoApartmentsTV);
    }

}