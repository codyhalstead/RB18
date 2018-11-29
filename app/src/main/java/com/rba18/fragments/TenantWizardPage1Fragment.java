package com.rba18.fragments;

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
import com.rba18.R;
import com.rba18.model.Tenant;
import com.rba18.wizards.TenantWizardPage1;

public class TenantWizardPage1Fragment  extends android.support.v4.app.Fragment {

    private static final String ARG_KEY = "key";
    private PageFragmentCallbacks mCallbacks;
    private String mKey;
    private TenantWizardPage1 mPage;
    private EditText mFirstNameET, mLastNameET, mPhoneET, mEmailET;
    private boolean mIsFormatting, mDeletingHyphen, mDeletingBackward, mIsEdit;
    private int mHyphenStart;

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
        mIsEdit = false;
        Bundle extras = mPage.getData();
        if (extras != null) {
            Tenant tenantToEdit = extras.getParcelable("tenantToEdit");
            if (tenantToEdit != null) {
                loadDataForEdit(tenantToEdit);
                mIsEdit = true;
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tenant_wizard_page_1, container, false);
        (rootView.findViewById(android.R.id.title)).setVisibility(View.GONE);

        mFirstNameET = (rootView.findViewById(R.id.tenantWizardFirstNameET));
        mFirstNameET.setText(mPage.getData().getString(TenantWizardPage1.TENANT_FIRST_NAME_DATA_KEY));
        mFirstNameET.setScroller(new Scroller(getContext()));
        mFirstNameET.setMaxLines(5);
        mFirstNameET.setVerticalScrollBarEnabled(true);
        mFirstNameET.setMovementMethod(new ScrollingMovementMethod());
        mFirstNameET.setSelection(mFirstNameET.getText().length());

        mLastNameET = (rootView.findViewById(R.id.tenantWizardLastNameET));
        mLastNameET.setText(mPage.getData().getString(TenantWizardPage1.TENANT_LAST_NAME_DATA_KEY));
        mLastNameET.setScroller(new Scroller(getContext()));
        mLastNameET.setMaxLines(5);
        mLastNameET.setVerticalScrollBarEnabled(true);
        mLastNameET.setMovementMethod(new ScrollingMovementMethod());
        mLastNameET.setSelection(mLastNameET.getText().length());

        mPhoneET = (rootView.findViewById(R.id.tenantWizardPhoneET));
        mPhoneET.setText(mPage.getData().getString(TenantWizardPage1.TENANT_PHONE_DATA_KEY));
        mPhoneET.setSelection(mPhoneET.getText().length());

        mEmailET = (rootView.findViewById(R.id.tenantWizardEmailET));
        mEmailET.setText(mPage.getData().getString(TenantWizardPage1.TENANT_EMAIL_DATA_KEY));
        mEmailET.setScroller(new Scroller(getContext()));
        mEmailET.setMaxLines(5);
        mEmailET.setVerticalScrollBarEnabled(true);
        mEmailET.setMovementMethod(new ScrollingMovementMethod());
        mEmailET.setSelection(mEmailET.getText().length());

        TextView newTenantHeaderTV = (rootView).findViewById(R.id.tenantWizardPageOneHeader);
        if(mIsEdit){
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
        mFirstNameET.addTextChangedListener(new TextWatcher() {
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
        mLastNameET.addTextChangedListener(new TextWatcher() {
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
        mPhoneET.addTextChangedListener(new TextWatcher() {
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
        mEmailET.addTextChangedListener(new TextWatcher() {
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
        if (mFirstNameET != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            if (!menuVisible) {
                imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
            }
        }
    }

    //Automatically enters hyphens for the user
    private void setPhoneNumberEditTextHelper() {
        mPhoneET.addTextChangedListener(createPhoneNumberTextWatcher());
    }

    private TextWatcher createPhoneNumberTextWatcher(){
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (mIsFormatting)
                    return;
                // Make sure user is deleting one char, without a selection
                final int selStart = Selection.getSelectionStart(charSequence);
                final int selEnd = Selection.getSelectionEnd(charSequence);
                if (charSequence.length() > 1 // Can delete another character
                        && i1 == 1 // Deleting only one character
                        && i2 == 0 // Deleting
                        && charSequence.charAt(i) == '-' // a hyphen
                        && selStart == selEnd) { // no selection
                    mDeletingHyphen = true;
                    mHyphenStart = i;
                    // Check if the user is deleting forward or backward
                    if (selStart == i + 1) {
                        mDeletingBackward = true;
                    } else {
                        mDeletingBackward = false;
                    }
                } else {
                    mDeletingHyphen = false;
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable text) {
                if (mIsFormatting)
                    return;
                mIsFormatting = true;
                // If deleting hyphen, also delete character before or after it
                if (mDeletingHyphen && mHyphenStart > 0) {
                    if (mDeletingBackward) {
                        if (mHyphenStart - 1 < text.length()) {
                            text.delete(mHyphenStart - 1, mHyphenStart);
                        }
                    } else if (mHyphenStart < text.length()) {
                        text.delete(mHyphenStart, mHyphenStart + 1);
                    }
                }
                if (text.length() == 3 || text.length() == 7) {
                    text.append('-');
                }
                mIsFormatting = false;
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
