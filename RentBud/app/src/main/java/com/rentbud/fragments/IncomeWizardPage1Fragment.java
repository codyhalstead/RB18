package com.rentbud.fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.android.wizardpager.wizard.ui.PageFragmentCallbacks;
import com.example.cody.rentbud.R;
import com.rentbud.activities.MainActivity;
import com.rentbud.activities.NewIncomeWizard;
import com.rentbud.helpers.NewItemCreatorDialog;
import com.rentbud.wizards.ExpenseWizardPage1;
import com.rentbud.wizards.IncomeWizardPage1;
import com.rentbud.sqlite.DatabaseHandler;

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

public class IncomeWizardPage1Fragment extends android.support.v4.app.Fragment {
    private static final String ARG_KEY = "key";

    private PageFragmentCallbacks mCallbacks;
    private String mKey;
    private IncomeWizardPage1 mPage;
    private TextView dateTV, newIncomeHeaderTV;
    private EditText amountET;
    private Spinner typeSpinner;
    private Button addNewTypeBtn;
    private ArrayAdapter<String> adapter;
    private DatePickerDialog.OnDateSetListener setIncomeDateListener;
    private BigDecimal amount;
    private Date incomeDate;
    private DatabaseHandler dbHandler;

    public static IncomeWizardPage1Fragment create(String key) {
        Bundle args = new Bundle();
        args.putString(ARG_KEY, key);

        IncomeWizardPage1Fragment fragment = new IncomeWizardPage1Fragment();
        fragment.setArguments(args);
        return fragment;
    }

    public IncomeWizardPage1Fragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        mKey = args.getString(ARG_KEY);
        mPage = (IncomeWizardPage1) mCallbacks.onGetPage(mKey);
        amount = new BigDecimal(0);
        dbHandler = new DatabaseHandler(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_income_wizard_page_1, container, false);
        (rootView.findViewById(android.R.id.title)).setVisibility(View.GONE);

        dateTV = rootView.findViewById(R.id.incomeWizardDateTV);
        if(mPage.getData().getString(IncomeWizardPage1.INCOME_DATE_STRING_DATA_KEY) != null) {
            String dateString = mPage.getData().getString(IncomeWizardPage1.INCOME_DATE_STRING_DATA_KEY);
            DateFormat formatFrom = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
            try {
                incomeDate = formatFrom.parse(dateString);
                dateTV.setText(mPage.getData().getString(IncomeWizardPage1.INCOME_DATE_STRING_DATA_KEY));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        amountET = rootView.findViewById(R.id.incomeWizardAmountET);
        if(mPage.getData().getString(IncomeWizardPage1.INCOME_AMOUNT_FORMATTED_STRING_DATA_KEY) != null) {
            amountET.setText(mPage.getData().getString(IncomeWizardPage1.INCOME_AMOUNT_FORMATTED_STRING_DATA_KEY));
        }
        amountET.setSelection(amountET.getText().length());

        typeSpinner = rootView.findViewById(R.id.incomeWizardTypeSpinner);
        typeSpinner.setSelection(mPage.getData().getInt(IncomeWizardPage1.INCOME_TYPE_ID_DATA_KEY));

        addNewTypeBtn = rootView.findViewById(R.id.incomeWizardAddNewTypeBtn);

        newIncomeHeaderTV = rootView.findViewById(R.id.incomeWizardPageOneHeader);
        if(NewIncomeWizard.incomeToEdit != null){
            newIncomeHeaderTV.setText("Edit Income Info");
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
        setUpdateSelectedDateListeners();
        populateIncomeTypeSpinner();
        dateTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                if (incomeDate != null) {
                    cal.setTime(incomeDate);
                }
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dialog = new DatePickerDialog(getContext(), android.R.style.Theme_Holo_Light_Dialog_MinWidth, setIncomeDateListener, year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });
        amountET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (amountET == null) return;
                String s = editable.toString();
                if (s.isEmpty()) return;
                amountET.removeTextChangedListener(this);
                String cleanString = s.replaceAll("[$,.]", "");
                amount = new BigDecimal(cleanString).setScale(2, BigDecimal.ROUND_FLOOR).divide(new BigDecimal(100), BigDecimal.ROUND_FLOOR);
                String formatted = NumberFormat.getCurrencyInstance().format(amount);
                amountET.setText(formatted);
                mPage.getData().putString(IncomeWizardPage1.INCOME_AMOUNT_FORMATTED_STRING_DATA_KEY, formatted);
                mPage.getData().putString(IncomeWizardPage1.INCOME_AMOUNT_STRING_DATA_KEY, amount.toPlainString());
                mPage.notifyDataChanged();
                amountET.setSelection(formatted.length());
                amountET.addTextChangedListener(this);
            }
        });

        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                // paymentFrequency = position + 1;

                String type = typeSpinner.getSelectedItem().toString();
                int typeID = MainActivity.incomeTypeLabels.get(type);
                mPage.getData().putInt(IncomeWizardPage1.INCOME_TYPE_ID_DATA_KEY, typeID);
                mPage.getData().putString(IncomeWizardPage1.INCOME_TYPE_DATA_KEY, type);

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
        addNewTypeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NewItemCreatorDialog dialog = new NewItemCreatorDialog(getContext());
                dialog.show();
                dialog.setDialogResult(new NewItemCreatorDialog.NewItemDialogResult(){
                    @Override
                    public void finish(String string) {
                        dbHandler.addNewIncomeType(string);
                        MainActivity.incomeTypeLabels = dbHandler.getIncomeTypeLabels();
                        updateIncomeTypeSpinner();
                        int spinnerPosition = adapter.getPosition(string);
                        typeSpinner.setSelection(spinnerPosition);
                    }
                });
            }
        });
        if(mPage.getData().getString(IncomeWizardPage1.INCOME_AMOUNT_STRING_DATA_KEY) == null) {
            String formatted = NumberFormat.getCurrencyInstance().format(amount);
            mPage.getData().putString(IncomeWizardPage1.INCOME_AMOUNT_FORMATTED_STRING_DATA_KEY, formatted);
            mPage.getData().putString(IncomeWizardPage1.INCOME_AMOUNT_STRING_DATA_KEY, amount.toPlainString());
        }
        if(mPage.getData().getString(IncomeWizardPage1.INCOME_TYPE_DATA_KEY) != null){
            int spinnerPosition = adapter.getPosition(mPage.getData().getString(IncomeWizardPage1.INCOME_TYPE_DATA_KEY));
            typeSpinner.setSelection(spinnerPosition);
            //typeSpinner.setSelection(mPage.getData().getInt(IncomeWizardPage1.INCOME_TYPE_ID_DATA_KEY));
        }
        //String state = stateSpinner.getSelectedItem().toString();
        //int stateID = MainActivity.stateMap.get(state);
        //mPage.getData().putInt(LeaseWizardPage3.LEASE_PAYMENT_FREQUENCY_DATA_KEY, stateID);
        //mPage.getData().putString(LeaseWizardPage3.LEASE_PAYMENT_FREQUENCY_STRING_DATA_KEY, state);
    }

    private void setUpdateSelectedDateListeners() {
        setIncomeDateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                //Once user selects date from date picker pop-up,
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.YEAR, year);
                cal.set(Calendar.MONTH, month);
                cal.set(Calendar.DATE, day);
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);

                incomeDate = cal.getTime();

                SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
                dateTV.setText(formatter.format(incomeDate));
                mPage.getData().putString(IncomeWizardPage1.INCOME_DATE_STRING_DATA_KEY, formatter.format(incomeDate));
                mPage.notifyDataChanged();
            }
        };
    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);

        // In a future update to the support library, this should override setUserVisibleHint
        // instead of setMenuVisibility.
        if (dateTV != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            if (!menuVisible) {
                imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
            }
        }
    }

    private void populateIncomeTypeSpinner() {
        //Create state array from MainActivity5.stateMap
        List<String> spinnerArray = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : MainActivity.incomeTypeLabels.entrySet()) {
            spinnerArray.add(entry.getKey());
        }
        //Create ArrayAdapter with state array
        adapter = new ArrayAdapter<>(
                getContext(), android.R.layout.simple_spinner_item, spinnerArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Set ArrayAdapter to stateSpinner
        this.typeSpinner.setAdapter(adapter);
    }

    public void updateIncomeTypeSpinner(){
        List<String> spinnerArray = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : MainActivity.incomeTypeLabels.entrySet()) {
            spinnerArray.add(entry.getKey());
        }
        adapter.clear();
        adapter.addAll(spinnerArray);
    }

}