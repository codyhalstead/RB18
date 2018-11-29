package com.rba18.fragments;

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
import com.rba18.R;
import com.rba18.model.Tenant;
import com.rba18.wizards.TenantWizardPage2;

public class TenantWizardPage2Fragment extends android.support.v4.app.Fragment {

    private static final String ARG_KEY = "key";
    private PageFragmentCallbacks mCallbacks;
    private String mKey;
    private TenantWizardPage2 mPage;
    private EditText mEmerFirstNameET, mEmerLastNameET, mEmerPhoneET;
    private boolean mIsFormatting, mDeletingBackward, mDeletingHyphen;
    private int mHyphenStart;

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

        mEmerFirstNameET = (rootView.findViewById(R.id.tenantWizardEFirstNameET));
        mEmerFirstNameET.setText(mPage.getData().getString(TenantWizardPage2.TENANT_EMERGENCY_FIRST_NAME_DATA_KEY));
        mEmerFirstNameET.setScroller(new Scroller(getContext()));
        mEmerFirstNameET.setMaxLines(5);
        mEmerFirstNameET.setVerticalScrollBarEnabled(true);
        mEmerFirstNameET.setMovementMethod(new ScrollingMovementMethod());
        mEmerFirstNameET.setSelection(mEmerFirstNameET.getText().length());

        mEmerLastNameET = (rootView.findViewById(R.id.tenantWizardELastNameET));
        mEmerLastNameET.setText(mPage.getData().getString(TenantWizardPage2.TENANT_EMERGENCY_LAST_NAME_DATA_KEY));
        mEmerLastNameET.setScroller(new Scroller(getContext()));
        mEmerLastNameET.setMaxLines(5);
        mEmerLastNameET.setVerticalScrollBarEnabled(true);
        mEmerLastNameET.setMovementMethod(new ScrollingMovementMethod());
        mEmerLastNameET.setSelection(mEmerLastNameET.getText().length());

        mEmerPhoneET = (rootView.findViewById(R.id.tenantWizardEPhoneET));
        mEmerPhoneET.setText(mPage.getData().getString(TenantWizardPage2.TENANT_EMERGENCY_PHONE_DATA_KEY));
        mEmerPhoneET.setSelection(mEmerPhoneET.getText().length());

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
        mEmerFirstNameET.addTextChangedListener(new TextWatcher() {
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
        mEmerLastNameET.addTextChangedListener(new TextWatcher() {
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
        mEmerPhoneET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                mPage.getData().putString(TenantWizardPage2.TENANT_EMERGENCY_PHONE_DATA_KEY, editable.toString());
                mPage.notifyDataChanged();
            }
        });
    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);

        // In a future update to the support library, this should override setUserVisibleHint
        // instead of setMenuVisibility.
        if (mEmerFirstNameET != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            if (!menuVisible) {
                imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
            }
        }
    }

    //Automatically enters hyphens for the sUser
    private void setPhoneNumberEditTextHelper() {
        mEmerPhoneET.addTextChangedListener(createPhoneNumberTextWatcher());
    }

    private TextWatcher createPhoneNumberTextWatcher() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (mIsFormatting)
                    return;
                // Make sure sUser is deleting one char, without a selection
                final int selStart = Selection.getSelectionStart(charSequence);
                final int selEnd = Selection.getSelectionEnd(charSequence);
                if (charSequence.length() > 1 // Can delete another character
                        && i1 == 1 // Deleting only one character
                        && i2 == 0 // Deleting
                        && charSequence.charAt(i) == '-' // a hyphen
                        && selStart == selEnd) { // no selection
                    mDeletingHyphen = true;
                    mHyphenStart = i;
                    // Check if the sUser is deleting forward or backward
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
                mPage.getData().putString(TenantWizardPage2.TENANT_EMERGENCY_PHONE_DATA_KEY, text.toString());
                mPage.notifyDataChanged();
            }
        };
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
