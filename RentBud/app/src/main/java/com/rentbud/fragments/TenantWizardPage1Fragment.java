package com.rentbud.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.TextView;

import com.example.android.wizardpager.wizard.ui.PageFragmentCallbacks;
import com.example.cody.rentbud.R;
import com.rentbud.activities.NewTenantWizard;
import com.rentbud.model.Tenant;
import com.rentbud.wizards.TenantWizardPage1;

public class TenantWizardPage1Fragment  extends android.support.v4.app.Fragment {
    private static final String ARG_KEY = "key";

    private PageFragmentCallbacks mCallbacks;
    private String mKey;
    private TenantWizardPage1 mPage;
    private EditText firstNameET, lastNameET, phoneET, emailET;
    private TextView newTenantHeaderTV;
    private boolean isFormatting;
    private boolean deletingHyphen;
    private int hyphenStart;
    private boolean deletingBackward;
    private boolean isEdit;


    public static TenantWizardPage1Fragment create(String key) {
        Bundle args = new Bundle();
        args.putString(ARG_KEY, key);

        TenantWizardPage1Fragment fragment = new TenantWizardPage1Fragment();
        fragment.setArguments(args);
        return fragment;
    }

    public TenantWizardPage1Fragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        mKey = args.getString(ARG_KEY);
        mPage = (TenantWizardPage1) mCallbacks.onGetPage(mKey);
        isEdit = false;
        Bundle extras = mPage.getData();
        if (extras != null) {
            Tenant tenantToEdit = extras.getParcelable("tenantToEdit");
            if (tenantToEdit != null) {
                loadDataForEdit(tenantToEdit);
                isEdit = true;
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tenant_wizard_page_1, container, false);
        (rootView.findViewById(android.R.id.title)).setVisibility(View.GONE);

        firstNameET = (rootView.findViewById(R.id.tenantWizardFirstNameET));
        firstNameET.setText(mPage.getData().getString(TenantWizardPage1.TENANT_FIRST_NAME_DATA_KEY));
        firstNameET.setScroller(new Scroller(getContext()));
        firstNameET.setMaxLines(5);
        firstNameET.setVerticalScrollBarEnabled(true);
        firstNameET.setMovementMethod(new ScrollingMovementMethod());
        firstNameET.setSelection(firstNameET.getText().length());

        lastNameET = (rootView.findViewById(R.id.tenantWizardLastNameET));
        lastNameET.setText(mPage.getData().getString(TenantWizardPage1.TENANT_LAST_NAME_DATA_KEY));
        lastNameET.setScroller(new Scroller(getContext()));
        lastNameET.setMaxLines(5);
        lastNameET.setVerticalScrollBarEnabled(true);
        lastNameET.setMovementMethod(new ScrollingMovementMethod());
        lastNameET.setSelection(lastNameET.getText().length());

        phoneET = (rootView.findViewById(R.id.tenantWizardPhoneET));
        phoneET.setText(mPage.getData().getString(TenantWizardPage1.TENANT_PHONE_DATA_KEY));
        phoneET.setSelection(phoneET.getText().length());

        emailET = (rootView.findViewById(R.id.tenantWizardEmailET));
        emailET.setText(mPage.getData().getString(TenantWizardPage1.TENANT_EMAIL_DATA_KEY));
        emailET.setScroller(new Scroller(getContext()));
        emailET.setMaxLines(5);
        emailET.setVerticalScrollBarEnabled(true);
        emailET.setMovementMethod(new ScrollingMovementMethod());
        emailET.setSelection(emailET.getText().length());

        newTenantHeaderTV = (rootView).findViewById(R.id.tenantWizardPageOneHeader);
        if(isEdit){
            newTenantHeaderTV.setText(R.string.edit_tenant_info);
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
        //setPhoneNumberEditTextHelper();
        firstNameET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                mPage.getData().putString(TenantWizardPage1.TENANT_FIRST_NAME_DATA_KEY, editable.toString());
                mPage.notifyDataChanged();
            }
        });
        lastNameET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                mPage.getData().putString(TenantWizardPage1.TENANT_LAST_NAME_DATA_KEY, editable.toString());
                mPage.notifyDataChanged();
            }
        });
        phoneET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                mPage.getData().putString(TenantWizardPage1.TENANT_PHONE_DATA_KEY, editable.toString());
                mPage.notifyDataChanged();
            }
        });
        emailET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                mPage.getData().putString(TenantWizardPage1.TENANT_EMAIL_DATA_KEY, editable.toString());
                mPage.notifyDataChanged();
            }
        });
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                mPage.notifyDataChanged();
            }
        });
    }


    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);

        // In a future update to the support library, this should override setUserVisibleHint
        // instead of setMenuVisibility.
        if (firstNameET != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            if (!menuVisible) {
                imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
            }
        }
    }

    //Automatically enters hyphens for the user
    private void setPhoneNumberEditTextHelper() {
        this.phoneET.addTextChangedListener(createPhoneNumberTextWatcher());
    }

    private TextWatcher createPhoneNumberTextWatcher(){
        return new TextWatcher() {
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
                mPage.getData().putString(TenantWizardPage1.TENANT_PHONE_DATA_KEY, text.toString());
                mPage.notifyDataChanged();
            }
        };
    }

    private void loadDataForEdit(Tenant tenantToEdit) {
        if (!mPage.getData().getBoolean(TenantWizardPage1.WAS_PRELOADED)) {
            //First name
            mPage.getData().putString(TenantWizardPage1.TENANT_FIRST_NAME_DATA_KEY, tenantToEdit.getFirstName());
            //Last name
            mPage.getData().putString(TenantWizardPage1.TENANT_LAST_NAME_DATA_KEY, tenantToEdit.getLastName());
            //Phone
            mPage.getData().putString(TenantWizardPage1.TENANT_PHONE_DATA_KEY, tenantToEdit.getPhone());
            //Email
            mPage.getData().putString(TenantWizardPage1.TENANT_EMAIL_DATA_KEY, tenantToEdit.getEmail());
            mPage.getData().putBoolean(TenantWizardPage1.WAS_PRELOADED, true);
        }
    }

}
