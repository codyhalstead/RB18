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
import android.widget.TextView;

import com.example.android.wizardpager.wizard.ui.PageFragmentCallbacks;
import com.rba18.R;
import com.rba18.model.Apartment;
import com.rba18.wizards.ApartmentWizardPage1;

public class ApartmentWizardPage1Fragment extends android.support.v4.app.Fragment {
    private static final String ARG_KEY = "key";

    private PageFragmentCallbacks mCallbacks;
    private String mKey;
    private ApartmentWizardPage1 mPage;
    private TextView mNewApartmentHeader;
    private EditText mAddressLine1ET, mAddressLine2ET, mCityET, mStateET, mZIPET;
    private boolean mIsEdit;

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
        mIsEdit = false;
        Bundle extras = mPage.getData();
        if (extras != null) {
            Apartment apartmentToEdit = extras.getParcelable("apartmentToEdit");
            if (apartmentToEdit != null) {
                loadDataForEdit(apartmentToEdit);
                mIsEdit = true;
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_apartment_wizard_page_1, container, false);
        (rootView.findViewById(android.R.id.title)).setVisibility(View.GONE);

        mAddressLine1ET = rootView.findViewById(R.id.apartmentWizardAddress1ET);
        mAddressLine1ET.setText(mPage.getData().getString(ApartmentWizardPage1.APARTMENT_ADDRESS_1_DATA_KEY));
        mAddressLine1ET.setScroller(new Scroller(getContext()));
        mAddressLine1ET.setMaxLines(5);
        mAddressLine1ET.setVerticalScrollBarEnabled(true);
        mAddressLine1ET.setMovementMethod(new ScrollingMovementMethod());
        mAddressLine1ET.setSelection(mAddressLine1ET.getText().length());

        mAddressLine2ET = rootView.findViewById(R.id.apartmentWizardAddress2ET);
        mAddressLine2ET.setText(mPage.getData().getString(ApartmentWizardPage1.APARTMENT_ADDRESS_2_DATA_KEY));
        mAddressLine2ET.setScroller(new Scroller(getContext()));
        mAddressLine2ET.setMaxLines(5);
        mAddressLine2ET.setVerticalScrollBarEnabled(true);
        mAddressLine2ET.setMovementMethod(new ScrollingMovementMethod());
        mAddressLine2ET.setSelection(mAddressLine2ET.getText().length());

        mCityET = rootView.findViewById(R.id.apartmentWizardCityET);
        mCityET.setText(mPage.getData().getString(ApartmentWizardPage1.APARTMENT_CITY_DATA_KEY));
        mCityET.setScroller(new Scroller(getContext()));
        mCityET.setMaxLines(5);
        mCityET.setVerticalScrollBarEnabled(true);
        mCityET.setMovementMethod(new ScrollingMovementMethod());
        mCityET.setSelection(mCityET.getText().length());

        mStateET = rootView.findViewById(R.id.apartmentWizardStateET);
        mStateET.setText(mPage.getData().getString(ApartmentWizardPage1.APARTMENT_STATE_DATA_KEY));
        mStateET.setScroller(new Scroller(getContext()));
        mStateET.setMaxLines(5);
        mStateET.setVerticalScrollBarEnabled(true);
        mStateET.setMovementMethod(new ScrollingMovementMethod());
        mStateET.setSelection(mStateET.getText().length());

        mZIPET = rootView.findViewById(R.id.apartmentWizardZIPET);
        mZIPET.setText(mPage.getData().getString(ApartmentWizardPage1.APARTMENT_ZIP_DATA_KEY));
        mZIPET.setSelection(mZIPET.getText().length());

        mNewApartmentHeader = rootView.findViewById(R.id.apartmentWizardPageOneHeader);
        if (mIsEdit) {
            mNewApartmentHeader.setText(R.string.edit_apt_info);
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
        mAddressLine1ET.addTextChangedListener(new TextWatcher() {
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
        mAddressLine2ET.addTextChangedListener(new TextWatcher() {
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
        mCityET.addTextChangedListener(new TextWatcher() {
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
        mStateET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                mPage.getData().putString(ApartmentWizardPage1.APARTMENT_STATE_DATA_KEY, editable.toString());
                mPage.notifyDataChanged();
            }
        });
        mZIPET.addTextChangedListener(new TextWatcher() {
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
        if (getUserVisibleHint()) {
            mPage.notifyDataChanged();
        }
    }


    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);

        // In a future update to the support library, this should override setUserVisibleHint
        // instead of setMenuVisibility.
        if (mAddressLine1ET != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            if (!menuVisible) {
                imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
            }
        }
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
            mPage.getData().putString(ApartmentWizardPage1.APARTMENT_STATE_DATA_KEY, apartmentToEdit.getState());
            //ZIP
            mPage.getData().putString(ApartmentWizardPage1.APARTMENT_ZIP_DATA_KEY, apartmentToEdit.getZip());
            mPage.getData().putBoolean(ApartmentWizardPage1.WAS_PRELOADED, true);
        }
    }
}


