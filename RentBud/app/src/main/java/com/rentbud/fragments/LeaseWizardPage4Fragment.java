package com.rentbud.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.example.android.wizardpager.wizard.ui.PageFragmentCallbacks;
import com.example.cody.rentbud.R;
import com.rentbud.model.Lease;
import com.rentbud.wizards.LeaseWizardPage3;
import com.rentbud.wizards.LeaseWizardPage4;

import static android.support.constraint.Constraints.TAG;

public class LeaseWizardPage4Fragment extends Fragment {
    private static final String ARG_KEY = "key";
    private PageFragmentCallbacks mCallbacks;
    private String mKey;
    private LeaseWizardPage4 mPage;
    private EditText notesET;


    public static LeaseWizardPage4Fragment create(String key) {
        Bundle args = new Bundle();
        args.putString(ARG_KEY, key);
        LeaseWizardPage4Fragment fragment = new LeaseWizardPage4Fragment();
        fragment.setArguments(args);
        return fragment;
    }

    public LeaseWizardPage4Fragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        mKey = args.getString(ARG_KEY);
        mPage = (LeaseWizardPage4) mCallbacks.onGetPage(mKey);
        Bundle extras = mPage.getData();
        if (extras != null) {
            Lease leaseToEdit = extras.getParcelable("leaseToEdit");
            if (leaseToEdit != null) {
                loadDataForEdit(leaseToEdit);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_lease_wizard_page_4, container, false);
        (rootView.findViewById(android.R.id.title)).setVisibility(View.GONE);

        notesET = (rootView.findViewById(R.id.leaseWizardNotesET));
        notesET.setText(mPage.getData().getString(LeaseWizardPage4.LEASE_NOTES_DATA_KEY));
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

        notesET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                mPage.getData().putString(LeaseWizardPage4.LEASE_NOTES_DATA_KEY, editable.toString());
                mPage.notifyDataChanged();
            }
        });
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

    private void loadDataForEdit(Lease leaseToEdit) {
        if (!mPage.getData().getBoolean(LeaseWizardPage4.WAS_PRELOADED)) {
            //Notes
            mPage.getData().putString(LeaseWizardPage4.LEASE_NOTES_DATA_KEY, leaseToEdit.getNotes());
            mPage.getData().putBoolean(LeaseWizardPage4.WAS_PRELOADED, true);
        }
    }
}