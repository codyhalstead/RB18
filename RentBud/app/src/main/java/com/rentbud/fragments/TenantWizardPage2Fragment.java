package com.rentbud.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Scroller;

import com.example.android.wizardpager.wizard.ui.PageFragmentCallbacks;
import com.example.cody.rentbud.R;
import com.rentbud.model.Tenant;
import com.rentbud.wizards.TenantWizardPage1;
import com.rentbud.wizards.TenantWizardPage2;

public class TenantWizardPage2Fragment extends android.support.v4.app.Fragment {
    private static final String ARG_KEY = "key";

    private PageFragmentCallbacks mCallbacks;
    private String mKey;
    private TenantWizardPage2 mPage;
    private EditText emerFirstNameET, emerLastNameET, emerPhoneET;
    private boolean isFormatting;
    private boolean deletingHyphen;
    private int hyphenStart;
    private boolean deletingBackward;


    public static TenantWizardPage2Fragment create(String key) {
        Bundle args = new Bundle();
        args.putString(ARG_KEY, key);

        TenantWizardPage2Fragment fragment = new TenantWizardPage2Fragment();
        fragment.setArguments(args);
        return fragment;
    }

    public TenantWizardPage2Fragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        mKey = args.getString(ARG_KEY);
        mPage = (TenantWizardPage2) mCallbacks.onGetPage(mKey);
        Bundle extras = mPage.getData();
        if (extras != null) {
            Tenant tenantToEdit = extras.getParcelable("tenantToEdit");
            if (tenantToEdit != null) {
                loadDataForEdit(tenantToEdit);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tenant_wizard_page_2, container, false);
        (rootView.findViewById(android.R.id.title)).setVisibility(View.GONE);

        emerFirstNameET = (rootView.findViewById(R.id.tenantWizardEFirstNameET));
        emerFirstNameET.setText(mPage.getData().getString(TenantWizardPage2.TENANT_EMERGENCY_FIRST_NAME_DATA_KEY));
        emerFirstNameET.setScroller(new Scroller(getContext()));
        emerFirstNameET.setMaxLines(5);
        emerFirstNameET.setVerticalScrollBarEnabled(true);
        emerFirstNameET.setMovementMethod(new ScrollingMovementMethod());
        emerFirstNameET.setSelection(emerFirstNameET.getText().length());

        emerLastNameET = (rootView.findViewById(R.id.tenantWizardELastNameET));
        emerLastNameET.setText(mPage.getData().getString(TenantWizardPage2.TENANT_EMERGENCY_LAST_NAME_DATA_KEY));
        emerLastNameET.setScroller(new Scroller(getContext()));
        emerLastNameET.setMaxLines(5);
        emerLastNameET.setVerticalScrollBarEnabled(true);
        emerLastNameET.setMovementMethod(new ScrollingMovementMethod());
        emerLastNameET.setSelection(emerLastNameET.getText().length());

        emerPhoneET = (rootView.findViewById(R.id.tenantWizardEPhoneET));
        emerPhoneET.setText(mPage.getData().getString(TenantWizardPage2.TENANT_EMERGENCY_PHONE_DATA_KEY));
        emerPhoneET.setSelection(emerPhoneET.getText().length());

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
        setPhoneNumberEditTextHelper();
        emerFirstNameET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                mPage.getData().putString(TenantWizardPage2.TENANT_EMERGENCY_FIRST_NAME_DATA_KEY, editable.toString());
                mPage.notifyDataChanged();
            }
        });
        emerLastNameET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                mPage.getData().putString(TenantWizardPage2.TENANT_EMERGENCY_LAST_NAME_DATA_KEY, editable.toString());
                mPage.notifyDataChanged();
            }
        });
    }


    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);

        // In a future update to the support library, this should override setUserVisibleHint
        // instead of setMenuVisibility.
        if (emerFirstNameET != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            if (!menuVisible) {
                imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
            }
        }
    }

    //Automatically enters hyphens for the user
    private void setPhoneNumberEditTextHelper() {
        this.emerPhoneET.addTextChangedListener(createPhoneNumberTextWatcher());
    }

    private TextWatcher createPhoneNumberTextWatcher() {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (isFormatting)
                    return;
                // Make sure user is deleting one char, without a selection
                final int selStart = Selection.getSelectionStart(charSequence);
                final int selEnd = Selection.getSelectionEnd(charSequence);
                if (charSequence.length() > 1 // Can delete another character
                        && i1 == 1 // Deleting only one character
                        && i2 == 0 // Deleting
                        && charSequence.charAt(i) == '-' // a hyphen
                        && selStart == selEnd) { // no selection
                    deletingHyphen = true;
                    hyphenStart = i;
                    // Check if the user is deleting forward or backward
                    if (selStart == i + 1) {
                        deletingBackward = true;
                    } else {
                        deletingBackward = false;
                    }
                } else {
                    deletingHyphen = false;
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable text) {
                if (isFormatting)
                    return;
                isFormatting = true;
                // If deleting hyphen, also delete character before or after it
                if (deletingHyphen && hyphenStart > 0) {
                    if (deletingBackward) {
                        if (hyphenStart - 1 < text.length()) {
                            text.delete(hyphenStart - 1, hyphenStart);
                        }
                    } else if (hyphenStart < text.length()) {
                        text.delete(hyphenStart, hyphenStart + 1);
                    }
                }
                if (text.length() == 3 || text.length() == 7) {
                    text.append('-');
                }
                isFormatting = false;
                mPage.getData().putString(TenantWizardPage2.TENANT_EMERGENCY_PHONE_DATA_KEY, text.toString());
                mPage.notifyDataChanged();
            }
        };
        return textWatcher;
    }

    private void loadDataForEdit(Tenant tenantToEdit) {
        if (!mPage.getData().getBoolean(TenantWizardPage2.WAS_PRELOADED)) {
        //E.First name
        mPage.getData().putString(TenantWizardPage2.TENANT_EMERGENCY_FIRST_NAME_DATA_KEY, tenantToEdit.getEmergencyFirstName());
        //E.Last name
        mPage.getData().putString(TenantWizardPage2.TENANT_EMERGENCY_LAST_NAME_DATA_KEY, tenantToEdit.getEmergencyLastName());
        //E.Phone
        mPage.getData().putString(TenantWizardPage2.TENANT_EMERGENCY_PHONE_DATA_KEY, tenantToEdit.getEmergencyPhone());
        mPage.getData().putBoolean(TenantWizardPage2.WAS_PRELOADED, true);
    }
}

}
