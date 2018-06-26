package com.rentbud.fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Scroller;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.android.wizardpager.wizard.ui.PageFragmentCallbacks;
import com.example.cody.rentbud.R;
import com.rentbud.activities.MainActivity;
import com.rentbud.activities.NewExpenseWizard;
import com.rentbud.helpers.MainArrayDataMethods;
import com.rentbud.helpers.NewItemCreatorDialog;
import com.rentbud.helpers.TenantOrApartmentChooserDialog;
import com.rentbud.model.Apartment;
import com.rentbud.model.Tenant;
import com.rentbud.sqlite.DatabaseHandler;
import com.rentbud.wizards.ExpenseWizardPage1;
import com.rentbud.wizards.ExpenseWizardPage3;
import com.rentbud.wizards.LeaseWizardPage1;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static android.support.constraint.Constraints.TAG;

public class ExpenseWizardPage3Fragment extends android.support.v4.app.Fragment {
    private static final String ARG_KEY = "key";

    private PageFragmentCallbacks mCallbacks;
    private String mKey;
    private ExpenseWizardPage3 mPage;
    private TextView linkedAptTV;
    private DatabaseHandler dbHandler;
    private MainArrayDataMethods mainArrayDataMethods;
    private Apartment apartment;
    private ArrayList<Apartment> availableApartments;

    public static ExpenseWizardPage3Fragment create(String key) {
        Bundle args = new Bundle();
        args.putString(ARG_KEY, key);

        ExpenseWizardPage3Fragment fragment = new ExpenseWizardPage3Fragment();
        fragment.setArguments(args);
        return fragment;
    }

    public ExpenseWizardPage3Fragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        mKey = args.getString(ARG_KEY);
        mPage = (ExpenseWizardPage3) mCallbacks.onGetPage(mKey);
        dbHandler = new DatabaseHandler(getContext());
        mainArrayDataMethods = new MainArrayDataMethods();
        availableApartments = new ArrayList<>();
        //availableApartments.addAll(MainActivity.apartmentList);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_expense_wizard_page_3, container, false);
        (rootView.findViewById(android.R.id.title)).setVisibility(View.GONE);
        linkedAptTV = rootView.findViewById(R.id.expenseWizardAptLinkingTV);
        if(mPage.getData().getParcelable(ExpenseWizardPage3.EXPENSE_RELATED_APT_DATA_KEY) != null){
            linkedAptTV.setText(mPage.getData().getString(ExpenseWizardPage3.EXPENSE_RELATED_APT_TEXT_DATA_KEY));
            apartment = mPage.getData().getParcelable(ExpenseWizardPage3.EXPENSE_RELATED_APT_DATA_KEY);
        }
        linkedAptTV.setScroller(new Scroller(getContext()));
        linkedAptTV.setMaxLines(5);
        linkedAptTV.setVerticalScrollBarEnabled(true);
        linkedAptTV.setMovementMethod(new ScrollingMovementMethod());
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
        int curAptID = 0;
        if(apartment != null){
           curAptID = apartment.getId();
        }
        for(int i = 0; i < MainActivity.apartmentList.size(); i++){
            if(MainActivity.apartmentList.get(i).isActive() && MainActivity.apartmentList.get(i).getId() != curAptID){
                availableApartments.add(MainActivity.apartmentList.get(i));
            }
        }

        linkedAptTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TenantOrApartmentChooserDialog dialog = new TenantOrApartmentChooserDialog(getContext(), TenantOrApartmentChooserDialog.APARTMENT_TYPE, availableApartments);
                dialog.show();
                dialog.setDialogResult(new TenantOrApartmentChooserDialog.OnTenantChooserDialogResult() {
                    @Override
                    public void finish(Tenant tenantResult, Apartment apartmentResult) {
                        if (apartment != null) {
                            availableApartments.add(apartment);
                        }
                        availableApartments.remove(apartmentResult);
                        apartment = apartmentResult;
                        setApartmentTextView();
                        mPage.getData().putString(ExpenseWizardPage3.EXPENSE_RELATED_APT_TEXT_DATA_KEY, linkedAptTV.getText().toString());
                        mPage.getData().putParcelable(ExpenseWizardPage3.EXPENSE_RELATED_APT_DATA_KEY, apartment);
                        mPage.notifyDataChanged();
                    }
                });
            }
        });
        //TODO
        if(mPage.getData().getParcelable(ExpenseWizardPage3.EXPENSE_RELATED_APT_TEXT_DATA_KEY) == null){
            mPage.getData().putString(ExpenseWizardPage3.EXPENSE_RELATED_APT_TEXT_DATA_KEY, "");
         //   mPage.getData().putParcelable(ExpenseWizardPage3.EXPENSE_RELATED_APT_DATA_KEY, null);
        }
    }

    private void setApartmentTextView() {
        linkedAptTV.setText(apartment.getStreet1());
        if(apartment.getStreet2() != null) {
            linkedAptTV.append(" ");
            linkedAptTV.append(apartment.getStreet2());
        }
    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);

        // In a future update to the support library, this should override setUserVisibleHint
        // instead of setMenuVisibility.
        if (linkedAptTV != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            if (!menuVisible) {
                imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
            }
        }
    }
}

