package com.rba18.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.example.android.wizardpager.wizard.ui.PageFragmentCallbacks;
import com.rba18.R;
import com.rba18.model.Apartment;
import com.rba18.wizards.ApartmentWizardPage2;

public class ApartmentWizardPage2Fragment extends android.support.v4.app.Fragment {
    private static final String ARG_KEY = "key";

    private PageFragmentCallbacks mCallbacks;
    private String mKey;
    private ApartmentWizardPage2 mPage;
    private EditText mDescriptionET, mNotesET;

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

        mDescriptionET = rootView.findViewById(R.id.apartmentWizardDescriptionET);
        mDescriptionET.setText(mPage.getData().getString(ApartmentWizardPage2.APARTMENT_DESCRIPTION_DATA_KEY));
        mDescriptionET.setSelection(mDescriptionET.getText().length());

        mNotesET = rootView.findViewById(R.id.apartmentWizardNotesET);
        mNotesET.setText(mPage.getData().getString(ApartmentWizardPage2.APARTMENT_NOTES_DATA_KEY));
        mNotesET.setSelection(mNotesET.getText().length());

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
        mDescriptionET.addTextChangedListener(new TextWatcher() {
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
        mNotesET.addTextChangedListener(new TextWatcher() {
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
    }


    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);

        // In a future update to the support library, this should override setUserVisibleHint
        // instead of setMenuVisibility.
        if (mNotesET != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            if (!menuVisible) {
                imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
            }
        }
    }

    private void loadDataForEdit(Apartment apartmentToEdit) {
        if (!mPage.getData().getBoolean(ApartmentWizardPage2.WAS_PRELOADED)) {
            //Description
            mPage.getData().putString(ApartmentWizardPage2.APARTMENT_DESCRIPTION_DATA_KEY, apartmentToEdit.getDescription());
            //Notes
            mPage.getData().putString(ApartmentWizardPage2.APARTMENT_NOTES_DATA_KEY, apartmentToEdit.getNotes());
            mPage.getData().putBoolean(ApartmentWizardPage2.WAS_PRELOADED, true);
        }
    }
}
