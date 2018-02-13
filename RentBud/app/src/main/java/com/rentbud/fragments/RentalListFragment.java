package com.rentbud.fragments;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cody.rentbud.R;
import com.rentbud.activities.MainActivity;
import com.rentbud.activities.NewRentalFormActivity;
import com.rentbud.activities.RentalViewActivity;
import com.rentbud.activities.RenterViewActivity;
import com.rentbud.adapters.RentalListAdapter;
import com.rentbud.model.Apartment;
import com.rentbud.model.Tenant;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

/**
 * Created by Cody on 1/11/2018.
 */

public class RentalListFragment extends Fragment implements AdapterView.OnItemClickListener{
    //ArrayList<Apartment> apartmentArray;
    TextView noApartmentsTV;
    EditText searchBarET;
    RentalListAdapter rentalListAdapter;
    ColorStateList accentColor;
    FloatingActionButton rentalFAB;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_rental_list, container, false);
        noApartmentsTV = rootView.findViewById(R.id.noRentalEmptyListTV);
        searchBarET = rootView.findViewById(R.id.rentalListSearchET);
        ListView listView = (ListView) rootView.findViewById(R.id.mainRentalListView);
        Log.d(TAG, "onCreateView: CALLED");
        TypedValue colorValue = new TypedValue();
        getActivity().getTheme().resolveAttribute(R.attr.colorAccent, colorValue, true);
        accentColor = getActivity().getResources().getColorStateList(colorValue.resourceId);

        if(savedInstanceState != null) {
           // MainActivity.apartmentList = savedInstanceState.getParcelableArrayList("apartments");
            Log.d(TAG, "onCreateView: yo");
        } else {
            Bundle bundle = this.getArguments();
            if (bundle != null) {
                //MainActivity.apartmentList = bundle.getParcelableArrayList("RentalList");
            }
        }

        if (MainActivity.apartmentList != null) {
            if (!MainActivity.apartmentList.isEmpty()) {
                rentalListAdapter = new RentalListAdapter(getActivity(), MainActivity.apartmentList, accentColor);
                listView.setAdapter(rentalListAdapter);
                listView.setOnItemClickListener(this);
            } else {
                noApartmentsTV.setVisibility(View.VISIBLE);
            }
        } else {
            noApartmentsTV.setVisibility(View.VISIBLE);
        }


        // Inflate the layout for this fragment

        searchBarET.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                rentalListAdapter.getFilter().filter(cs);
                rentalListAdapter.notifyDataSetChanged();
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

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (rentalListAdapter != null){
            rentalListAdapter.notifyDataSetChanged();
            Log.d(TAG, "onResume: RESUMED");
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Rental View");
        this.rentalFAB = getActivity().findViewById(R.id.rentalFab);
        //rentalFAB.setOnClickListener(new View.OnClickListener() {
        //    @Override
        //    public void onClick(View view) {
        //        rentalFABClick();
       //     }
      //  });
    }

    //public void rentalFABClick() {
    //    Intent intent = new Intent(getActivity(), NewRentalFormActivity.class);
    //    startActivity(intent);
    //}

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent = new Intent(getContext(), RentalViewActivity.class);
        Apartment apartment = rentalListAdapter.getFilteredResults().get(i);
        intent.putExtra("apartment", apartment);
        startActivity(intent);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //outState.putParcelableArrayList("apartments", this.apartmentArray);
    }

}