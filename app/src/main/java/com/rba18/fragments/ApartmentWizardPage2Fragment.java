package com.rba18.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Scroller;

import com.example.android.wizardpager.wizard.ui.PageFragmentCallbacks;
import com.rba18.R;
import com.rba18.model.Apartment;
import com.rba18.wizards.ApartmentWizardPage1;
import com.rba18.wizards.ApartmentWizardPage2;

import java.math.BigDecimal;
import java.text.NumberFormat;

public class ApartmentWizardPage2Fragment extends android.support.v4.app.Fragment {
    private static final String ARG_KEY = "key";

    private PageFragmentCallbacks mCallbacks;
    private String mKey;
    private ApartmentWizardPage2 mPage;
    private EditText preferredRentET, descriptionET, notesET;
    //private BigDecimal preferredRent;

    public static ApartmentWizardPage2Fragment create(String key) {
        Bundle args = new Bundle();
        args.putString(ARG_KEY, key);

        ApartmentWizardPage2Fragment fragment = new ApartmentWizardPage2Fragment();
        fragment.setArguments(args);
        return fragment;
    }

    public ApartmentWizardPage2Fragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        mKey = args.getString(ARG_KEY);
        mPage = (ApartmentWizardPage2) mCallbacks.onGetPage(mKey);
        //preferredRent = new BigDecimal(0);
        Bundle extras = mPage.getData();
        if (extras != null) {
            Apartment apartmentToEdit = extras.getParcelable("apartmentToEdit");
            if (apartmentToEdit != null) {
                loadDataForEdit(apartmentToEdit);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_apartment_wizard_page_2, container, false);
        (rootView.findViewById(android.R.id.title)).setVisibility(View.GONE);

        //preferredRentET = rootView.findViewById(R.id.apartmentWizardPreferredRentET);
        //if(mPage.getData().getString(ApartmentWizardPage2.APARTMENT_PREFERRED_RENT_COST_FORMATTED_STRING_DATA_KEY) != null) {
        //    preferredRentET.setText(mPage.getData().getString(ApartmentWizardPage2.APARTMENT_PREFERRED_RENT_COST_FORMATTED_STRING_DATA_KEY));
        //}
        //preferredRentET.setSelection(preferredRentET.getText().length());

        descriptionET = rootView.findViewById(R.id.apartmentWizardDescriptionET);
        descriptionET.setText(mPage.getData().getString(ApartmentWizardPage2.APARTMENT_DESCRIPTION_DATA_KEY));
        descriptionET.setSelection(descriptionET.getText().length());

        notesET = rootView.findViewById(R.id.apartmentWizardNotesET);
        notesET.setText(mPage.getData().getString(ApartmentWizardPage2.APARTMENT_NOTES_DATA_KEY));
        notesET.setSelection(notesET.getText().length());

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
        //preferredRentET.addTextChangedListener(new TextWatcher() {
        //    @Override
        //    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        //    }

        //    @Override
        //    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        //    }

        //    @Override
        //    public void afterTextChanged(Editable editable) {
        //        if (preferredRentET == null) return;

        //        String s = editable.toString();
        //        if (s.isEmpty()) return;
        //        preferredRentET.removeTextChangedListener(this);
        //        String cleanString = s.replaceAll("[$,.]", "");
        //        preferredRent = new BigDecimal(cleanString).setScale(2, BigDecimal.ROUND_FLOOR).divide(new BigDecimal(100), BigDecimal.ROUND_FLOOR);
        //        String formatted = NumberFormat.getCurrencyInstance().format(preferredRent);
        //        preferredRentET.setText(formatted);
        //        mPage.getData().putString(ApartmentWizardPage2.APARTMENT_PREFERRED_RENT_COST_FORMATTED_STRING_DATA_KEY, formatted);
        //        mPage.getData().putString(ApartmentWizardPage2.APARTMENT_PREFERRED_RENT_COST_DATA_KEY, preferredRent.toPlainString());
        //        mPage.notifyDataChanged();
        //        preferredRentET.setSelection(formatted.length());
        //        preferredRentET.addTextChangedListener(this);
        //    }
        //});
        descriptionET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                mPage.getData().putString(ApartmentWizardPage2.APARTMENT_DESCRIPTION_DATA_KEY, editable.toString());
                mPage.notifyDataChanged();
            }
        });
        notesET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                mPage.getData().putString(ApartmentWizardPage2.APARTMENT_NOTES_DATA_KEY, editable.toString());
                mPage.notifyDataChanged();
            }
        });
        //if(mPage.getData().getString(ApartmentWizardPage2.APARTMENT_PREFERRED_RENT_COST_DATA_KEY) != null) {
        //    String amountString = mPage.getData().getString(ApartmentWizardPage2.APARTMENT_PREFERRED_RENT_COST_DATA_KEY);
        //    preferredRent = new BigDecimal(amountString);
        //} else {
        //    String formatted = NumberFormat.getCurrencyInstance().format(preferredRent);
        //    mPage.getData().putString(ApartmentWizardPage2.APARTMENT_PREFERRED_RENT_COST_FORMATTED_STRING_DATA_KEY, formatted);
        //    mPage.getData().putString(ApartmentWizardPage2.APARTMENT_PREFERRED_RENT_COST_DATA_KEY, preferredRent.toPlainString());
        //}

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
        if (notesET != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            if (!menuVisible) {
                imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
            }
        }
    }

    private void loadDataForEdit(Apartment apartmentToEdit) {
        if (!mPage.getData().getBoolean(ApartmentWizardPage2.WAS_PRELOADED)) {
            //Rent cost
            //preferredRent = apartmentToEdit.getPreferredRentCost();
            //String formatted = NumberFormat.getCurrencyInstance().format(preferredRent);
            //mPage.getData().putString(ApartmentWizardPage2.APARTMENT_PREFERRED_RENT_COST_FORMATTED_STRING_DATA_KEY, formatted);
            //mPage.getData().putString(ApartmentWizardPage2.APARTMENT_PREFERRED_RENT_COST_DATA_KEY, preferredRent.toPlainString());
            //Description
            mPage.getData().putString(ApartmentWizardPage2.APARTMENT_DESCRIPTION_DATA_KEY, apartmentToEdit.getDescription());
            //Notes
            mPage.getData().putString(ApartmentWizardPage2.APARTMENT_NOTES_DATA_KEY, apartmentToEdit.getNotes());
            mPage.getData().putBoolean(ApartmentWizardPage2.WAS_PRELOADED, true);
        }
    }
}
