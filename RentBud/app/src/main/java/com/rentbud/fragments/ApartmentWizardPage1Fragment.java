package com.rentbud.fragments;

import android.app.Activity;

import android.content.Context;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Scroller;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.android.wizardpager.wizard.ui.PageFragmentCallbacks;
import com.example.cody.rentbud.R;
import com.rentbud.activities.MainActivity;
import com.rentbud.activities.NewApartmentWizard;
import com.rentbud.activities.NewTenantWizard;
import com.rentbud.model.Apartment;
import com.rentbud.wizards.ApartmentWizardPage1;
import com.rentbud.wizards.LeaseWizardPage2;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ApartmentWizardPage1Fragment extends android.support.v4.app.Fragment {
    private static final String ARG_KEY = "key";

    private PageFragmentCallbacks mCallbacks;
    private String mKey;
    private ApartmentWizardPage1 mPage;
    private TextView newApartmentHeader;
    private EditText addressLine1ET, addressLine2ET, cityET, zipET;
    private Spinner stateSpinner;
    private ArrayAdapter<String> adapter;
    private boolean isEdit;

    public static ApartmentWizardPage1Fragment create(String key) {
        Bundle args = new Bundle();
        args.putString(ARG_KEY, key);

        ApartmentWizardPage1Fragment fragment = new ApartmentWizardPage1Fragment();
        fragment.setArguments(args);
        return fragment;
    }

    public ApartmentWizardPage1Fragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        mKey = args.getString(ARG_KEY);
        mPage = (ApartmentWizardPage1) mCallbacks.onGetPage(mKey);
        isEdit = false;
        Bundle extras = mPage.getData();
        if (extras != null) {
            Apartment apartmentToEdit = extras.getParcelable("apartmentToEdit");
            if (apartmentToEdit != null) {
                loadDataForEdit(apartmentToEdit);
                isEdit = true;
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_apartment_wizard_page_1, container, false);
        (rootView.findViewById(android.R.id.title)).setVisibility(View.GONE);

        addressLine1ET = rootView.findViewById(R.id.apartmentWizardAddress1ET);
        addressLine1ET.setText(mPage.getData().getString(ApartmentWizardPage1.APARTMENT_ADDRESS_1_DATA_KEY));
        addressLine1ET.setScroller(new Scroller(getContext()));
        addressLine1ET.setMaxLines(5);
        addressLine1ET.setVerticalScrollBarEnabled(true);
        addressLine1ET.setMovementMethod(new ScrollingMovementMethod());
        addressLine1ET.setSelection(addressLine1ET.getText().length());

        addressLine2ET = rootView.findViewById(R.id.apartmentWizardAddress2ET);
        addressLine2ET.setText(mPage.getData().getString(ApartmentWizardPage1.APARTMENT_ADDRESS_2_DATA_KEY));
        addressLine2ET.setScroller(new Scroller(getContext()));
        addressLine2ET.setMaxLines(5);
        addressLine2ET.setVerticalScrollBarEnabled(true);
        addressLine2ET.setMovementMethod(new ScrollingMovementMethod());
        addressLine2ET.setSelection(addressLine2ET.getText().length());

        cityET = rootView.findViewById(R.id.apartmentWizardCityET);
        cityET.setText(mPage.getData().getString(ApartmentWizardPage1.APARTMENT_CITY_DATA_KEY));
        cityET.setScroller(new Scroller(getContext()));
        cityET.setMaxLines(5);
        cityET.setVerticalScrollBarEnabled(true);
        cityET.setMovementMethod(new ScrollingMovementMethod());
        cityET.setSelection(cityET.getText().length());

        zipET = rootView.findViewById(R.id.apartmentWizardZIPET);
        zipET.setText(mPage.getData().getString(ApartmentWizardPage1.APARTMENT_ZIP_DATA_KEY));
        zipET.setSelection(zipET.getText().length());

        stateSpinner = rootView.findViewById(R.id.apartmentWizardStateSpinner);
        stateSpinner.setSelection(mPage.getData().getInt(ApartmentWizardPage1.APARTMENT_STATE_ID_DATA_KEY));

        newApartmentHeader = rootView.findViewById(R.id.apartmentWizardPageOneHeader);
        if (isEdit) {
            newApartmentHeader.setText(R.string.edit_apt_info);
        }

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
        populateStateSpinner();
        addressLine1ET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                mPage.getData().putString(ApartmentWizardPage1.APARTMENT_ADDRESS_1_DATA_KEY, editable.toString());
                mPage.notifyDataChanged();
            }
        });
        addressLine2ET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                mPage.getData().putString(ApartmentWizardPage1.APARTMENT_ADDRESS_2_DATA_KEY, editable.toString());
                mPage.notifyDataChanged();
            }
        });
        cityET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                mPage.getData().putString(ApartmentWizardPage1.APARTMENT_CITY_DATA_KEY, editable.toString());
                mPage.notifyDataChanged();
            }
        });
        zipET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                mPage.getData().putString(ApartmentWizardPage1.APARTMENT_ZIP_DATA_KEY, editable.toString());
                mPage.notifyDataChanged();
            }
        });
        stateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                // paymentFrequency = position + 1;

                String state = stateSpinner.getSelectedItem().toString();
                int stateID = MainActivity.stateMap.get(state);
                mPage.getData().putInt(ApartmentWizardPage1.APARTMENT_STATE_ID_DATA_KEY, stateID);
                mPage.getData().putString(ApartmentWizardPage1.APARTMENT_STATE_DATA_KEY, state);

                if (getUserVisibleHint()) {
                    //  figurePayments(leaseStartDate, leaseEndDate, paymentDay, paymentFrequency);
                    mPage.notifyDataChanged();
                    //  paymentsAmountTV.setText(prorated + regular + "");
                    //  proratedPaymentsAmountTV.setText(prorated + "");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        if (mPage.getData().getString(ApartmentWizardPage1.APARTMENT_STATE_DATA_KEY) != null) {
            int spinnerPosition = adapter.getPosition(mPage.getData().getString(ApartmentWizardPage1.APARTMENT_STATE_DATA_KEY));
            stateSpinner.setSelection(spinnerPosition);
            //typeSpinner.setSelection(mPage.getData().getInt(IncomeWizardPage1.INCOME_TYPE_ID_DATA_KEY));
        }
        //String state = stateSpinner.getSelectedItem().toString();
        //int stateID = MainActivity.stateMap.get(state);
        //mPage.getData().putInt(LeaseWizardPage3.LEASE_PAYMENT_FREQUENCY_DATA_KEY, stateID);
        //mPage.getData().putString(LeaseWizardPage3.LEASE_PAYMENT_FREQUENCY_STRING_DATA_KEY, state);
    }


    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);

        // In a future update to the support library, this should override setUserVisibleHint
        // instead of setMenuVisibility.
        if (addressLine1ET != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            if (!menuVisible) {
                imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
            }
        }
    }


    private void populateStateSpinner() {
        //Create state array from MainActivity5.stateMap
        List<String> spinnerArray = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : MainActivity.stateMap.entrySet()) {
            spinnerArray.add(entry.getKey());
        }
        //Create ArrayAdapter with state array
        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, spinnerArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Set ArrayAdapter to stateSpinner
        this.stateSpinner.setAdapter(adapter);
    }

    private void loadDataForEdit(Apartment apartmentToEdit) {
        if (!mPage.getData().getBoolean(ApartmentWizardPage1.WAS_PRELOADED)) {
            //Address line 1
            mPage.getData().putString(ApartmentWizardPage1.APARTMENT_ADDRESS_1_DATA_KEY, apartmentToEdit.getStreet1());
            //Address line 2
            if (apartmentToEdit.getStreet2() != null) {
                mPage.getData().putString(ApartmentWizardPage1.APARTMENT_ADDRESS_2_DATA_KEY, apartmentToEdit.getStreet2());
            }
            //City
            mPage.getData().putString(ApartmentWizardPage1.APARTMENT_CITY_DATA_KEY, apartmentToEdit.getCity());
            //State
            mPage.getData().putInt(ApartmentWizardPage1.APARTMENT_STATE_ID_DATA_KEY, apartmentToEdit.getStateID());
            mPage.getData().putString(ApartmentWizardPage1.APARTMENT_STATE_DATA_KEY, apartmentToEdit.getState());
            //ZIP
            mPage.getData().putString(ApartmentWizardPage1.APARTMENT_ZIP_DATA_KEY, apartmentToEdit.getZip());
            mPage.getData().putBoolean(ApartmentWizardPage1.WAS_PRELOADED, true);
        }
    }
}


