package com.rba18.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.android.wizardpager.wizard.ui.PageFragmentCallbacks;
import com.rba18.R;
import com.rba18.activities.MainActivity;
import com.rba18.helpers.CustomDatePickerDialogLauncher;
import com.rba18.helpers.DateAndCurrencyDisplayer;
import com.rba18.model.ExpenseLogEntry;
import com.rba18.wizards.ExpenseWizardPage1;
import com.rba18.sqlite.DatabaseHandler;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ExpenseWizardPage1Fragment extends android.support.v4.app.Fragment {
    private static final String ARG_KEY = "key";

    private PageFragmentCallbacks mCallbacks;
    private String mKey;
    private ExpenseWizardPage1 mPage;
    private TextView dateTV, newExpenseHeaderTV;
    private EditText amountET;
    private Spinner typeSpinner;
    private Button addNewTypeBtn;
    private ArrayAdapter<String> adapter;
    private BigDecimal amount;
    private Date expenseDate;
    private DatabaseHandler dbHandler;
    private boolean isEdit;
    private CustomDatePickerDialogLauncher datePickerDialogLauncher;
    //private NewItemCreatorDialog newItemCreatorDialog;
    private SharedPreferences preferences;
    private int dateFormatCode, moneyFormatCode;
    private AlertDialog alertDialog;

    public static ExpenseWizardPage1Fragment create(String key) {
        Bundle args = new Bundle();
        args.putString(ARG_KEY, key);

        ExpenseWizardPage1Fragment fragment = new ExpenseWizardPage1Fragment();
        fragment.setArguments(args);
        return fragment;
    }

    public ExpenseWizardPage1Fragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        mKey = args.getString(ARG_KEY);
        mPage = (ExpenseWizardPage1) mCallbacks.onGetPage(mKey);
        //amount = new BigDecimal(0);
        dbHandler = new DatabaseHandler(getContext());
        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        this.dateFormatCode = preferences.getInt("dateFormat", DateAndCurrencyDisplayer.DATE_MMDDYYYY);
        this.moneyFormatCode = preferences.getInt("currency", DateAndCurrencyDisplayer.CURRENCY_US);
        isEdit = false;
        Bundle extras = mPage.getData();
        if (extras != null) {
            ExpenseLogEntry expenseToEdit = extras.getParcelable("expenseToEdit");
            if (expenseToEdit != null) {
                loadDataForEdit(expenseToEdit);
                isEdit = true;
            } else {
                preloadData(extras);
            }
        } else {
            expenseDate = null;
            amount = new BigDecimal(0);
            String formatted = DateAndCurrencyDisplayer.getCurrencyToDisplay(moneyFormatCode, amount);
            mPage.getData().putString(ExpenseWizardPage1.EXPENSE_AMOUNT_FORMATTED_STRING_DATA_KEY, formatted);
            mPage.getData().putString(ExpenseWizardPage1.EXPENSE_AMOUNT_STRING_DATA_KEY, amount.toPlainString());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_expense_wizard_page_1, container, false);
        (rootView.findViewById(android.R.id.title)).setVisibility(View.GONE);

        dateTV = rootView.findViewById(R.id.expenseWizardDateTV);
        if (mPage.getData().getString(ExpenseWizardPage1.EXPENSE_DATE_STRING_DATA_KEY) != null) {
            String dateString = mPage.getData().getString(ExpenseWizardPage1.EXPENSE_DATE_STRING_DATA_KEY);
            DateFormat formatFrom = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
            try {
                expenseDate = formatFrom.parse(dateString);
                dateTV.setText(DateAndCurrencyDisplayer.getDateToDisplay(dateFormatCode, expenseDate));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        amountET = rootView.findViewById(R.id.expenseWizardAmountET);
        if (mPage.getData().getString(ExpenseWizardPage1.EXPENSE_AMOUNT_FORMATTED_STRING_DATA_KEY) != null) {
            amountET.setText(mPage.getData().getString(ExpenseWizardPage1.EXPENSE_AMOUNT_FORMATTED_STRING_DATA_KEY));
        }
        amountET.setSelection(amountET.getText().length());

        typeSpinner = rootView.findViewById(R.id.expenseWizardTypeSpinner);
        typeSpinner.setSelection(mPage.getData().getInt(ExpenseWizardPage1.EXPENSE_TYPE_ID_DATA_KEY));

        addNewTypeBtn = rootView.findViewById(R.id.expenseWizardAddNewTypeBtn);

        newExpenseHeaderTV = rootView.findViewById(R.id.expenseWizardPageOneHeader);
        if (isEdit) {
            newExpenseHeaderTV.setText(R.string.edit_expense_info);
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
        datePickerDialogLauncher = new CustomDatePickerDialogLauncher(expenseDate, false, getContext());
        datePickerDialogLauncher.setDateSelectedListener(new CustomDatePickerDialogLauncher.DateSelectedListener() {
            @Override
            public void onStartDateSelected(Date startDate, Date endDate) {

            }

            @Override
            public void onEndDateSelected(Date startDate, Date endDate) {

            }

            @Override
            public void onDateSelected(Date date) {
                expenseDate = date;
                SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
                dateTV.setText(DateAndCurrencyDisplayer.getDateToDisplay(dateFormatCode, expenseDate));
                mPage.getData().putString(ExpenseWizardPage1.EXPENSE_DATE_STRING_FORMATTED_DATA_KEY, DateAndCurrencyDisplayer.getDateToDisplay(dateFormatCode, expenseDate));
                mPage.getData().putString(ExpenseWizardPage1.EXPENSE_DATE_STRING_DATA_KEY, formatter.format(expenseDate));
                mPage.notifyDataChanged();
            }
        });
        populateExpenseTypeSpinner();
        dateTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerDialogLauncher.launchSingleDatePickerDialog();
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
                String cleanString = DateAndCurrencyDisplayer.cleanMoneyString(s);
                amount = new BigDecimal(cleanString).setScale(2, BigDecimal.ROUND_FLOOR).divide(new BigDecimal(100), BigDecimal.ROUND_FLOOR);
                String formatted = DateAndCurrencyDisplayer.getCurrencyToDisplay(moneyFormatCode, amount);
                amountET.setText(formatted);
                amountET.setSelection(DateAndCurrencyDisplayer.getEndCursorPositionForMoneyInput(amountET.getText().length(), moneyFormatCode));
                mPage.getData().putString(ExpenseWizardPage1.EXPENSE_AMOUNT_FORMATTED_STRING_DATA_KEY, formatted);
                //formatted = formatted.replaceAll("[€\\s]", "");
                mPage.getData().putString(ExpenseWizardPage1.EXPENSE_AMOUNT_STRING_DATA_KEY, amount.toPlainString());
                mPage.notifyDataChanged();
                //amountET.setSelection(formatted.length());
                amountET.addTextChangedListener(this);
            }
        });
        amountET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                amountET.setSelection(DateAndCurrencyDisplayer.getEndCursorPositionForMoneyInput(amountET.getText().length(), moneyFormatCode));
            }
        });

        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                // paymentFrequency = position + 1;

                String type = typeSpinner.getSelectedItem().toString();
                int typeID = 0;
                if(MainActivity.expenseTypeLabels.get(type) != null) {
                    typeID = MainActivity.expenseTypeLabels.get(type);
                }
                mPage.getData().putInt(ExpenseWizardPage1.EXPENSE_TYPE_ID_DATA_KEY, typeID);
                mPage.getData().putString(ExpenseWizardPage1.EXPENSE_TYPE_DATA_KEY, type);

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
                final EditText editText = new EditText(getContext());
                int maxLength = 25;
                editText.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLength)});
                editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);

                // create the AlertDialog as final
                 alertDialog = new AlertDialog.Builder(getContext())
                        //.setMessage("You are ready to type")
                        .setTitle(R.string.create_new_type)
                        .setView(editText)

                        // Set the action buttons
                        .setPositiveButton(R.string.create, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                dbHandler.addNewExpenseType(editText.getText().toString());
                                MainActivity.expenseTypeLabels = dbHandler.getExpenseTypeLabelsTreeMap();
                                updateExpenseTypeSpinner();
                                int spinnerPosition = adapter.getPosition(editText.getText().toString());
                                typeSpinner.setSelection(spinnerPosition);
                            }
                        })

                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                // removes the AlertDialog in the screen
                            }
                        })
                        .create();

                // set the focus change listener of the EditText
                // this part will make the soft keyboard automatically visible
                editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (hasFocus) {
                            alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                        }
                    }
                });
                alertDialog.show();
            }
        });
        //if (mPage.getData().getString(ExpenseWizardPage1.EXPENSE_AMOUNT_STRING_DATA_KEY) == null) {
        //    String formatted = NumberFormat.getCurrencyInstance().format(amount);
        //    mPage.getData().putString(ExpenseWizardPage1.EXPENSE_AMOUNT_FORMATTED_STRING_DATA_KEY, formatted);
        //    mPage.getData().putString(ExpenseWizardPage1.EXPENSE_AMOUNT_STRING_DATA_KEY, amount.toPlainString());
        //}
        if (mPage.getData().getString(ExpenseWizardPage1.EXPENSE_TYPE_DATA_KEY) != null) {
            int spinnerPosition = adapter.getPosition(mPage.getData().getString(ExpenseWizardPage1.EXPENSE_TYPE_DATA_KEY));
            if (isEdit && spinnerPosition == -1) {
                adapter.add(mPage.getData().getString(ExpenseWizardPage1.EXPENSE_TYPE_DATA_KEY));
                spinnerPosition = adapter.getPosition(mPage.getData().getString(ExpenseWizardPage1.EXPENSE_TYPE_DATA_KEY));
            }
            typeSpinner.setSelection(spinnerPosition);
        }
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
        if (dateTV != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            if (!menuVisible) {
                imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        datePickerDialogLauncher.dismissDatePickerDialog();
        if(alertDialog != null){
            alertDialog.dismiss();
        }
    }

    private void populateExpenseTypeSpinner() {
        //Create state array from MainActivity5.stateMap
        List<String> spinnerArray = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : MainActivity.expenseTypeLabels.entrySet()) {
            spinnerArray.add(entry.getKey());
        }
        //Create ArrayAdapter with state array
        adapter = new ArrayAdapter<>(
                getContext(), android.R.layout.simple_spinner_item, spinnerArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Set ArrayAdapter to stateSpinner
        this.typeSpinner.setAdapter(adapter);
    }

    public void updateExpenseTypeSpinner() {
        List<String> spinnerArray = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : MainActivity.expenseTypeLabels.entrySet()) {
            spinnerArray.add(entry.getKey());
        }
        adapter.clear();
        adapter.addAll(spinnerArray);
    }

    private void preloadDate(Bundle bundle) {
        if (mPage.getData().getString(ExpenseWizardPage1.EXPENSE_DATE_STRING_DATA_KEY) != null) {
            //If date exists (Was reloaded)
            String dateString = mPage.getData().getString(ExpenseWizardPage1.EXPENSE_DATE_STRING_DATA_KEY);
            DateFormat formatFrom = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
            try {
                expenseDate = formatFrom.parse(dateString);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else if (bundle.getString("preloadedDate") != null) {
            //Date does not exist, check if need to preload
            String dateString = bundle.getString("preloadedDate");
            mPage.getData().putString(ExpenseWizardPage1.EXPENSE_DATE_STRING_DATA_KEY, dateString);
            DateFormat formatFrom = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
            try {
                expenseDate = formatFrom.parse(dateString);
                mPage.getData().putString(ExpenseWizardPage1.EXPENSE_DATE_STRING_FORMATTED_DATA_KEY, DateAndCurrencyDisplayer.getDateToDisplay(dateFormatCode, expenseDate));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            expenseDate = null;
        }
    }

    private void preloadAmount(Bundle bundle) {
        if (mPage.getData().getString(ExpenseWizardPage1.EXPENSE_AMOUNT_STRING_DATA_KEY) != null) {
            amount = new BigDecimal(mPage.getData().getString(ExpenseWizardPage1.EXPENSE_AMOUNT_STRING_DATA_KEY)).setScale(2, BigDecimal.ROUND_FLOOR).divide(new BigDecimal(100), BigDecimal.ROUND_FLOOR);
        } else {
            amount = new BigDecimal(0);
            String formatted = DateAndCurrencyDisplayer.getCurrencyToDisplay(moneyFormatCode, amount);
            mPage.getData().putString(ExpenseWizardPage1.EXPENSE_AMOUNT_FORMATTED_STRING_DATA_KEY, formatted);
            mPage.getData().putString(ExpenseWizardPage1.EXPENSE_AMOUNT_STRING_DATA_KEY, amount.toPlainString());
        }
    }

    private void preloadType(Bundle bundle) {

    }

    private void preloadData(Bundle bundle) {
        preloadDate(bundle);
        preloadAmount(bundle);
        preloadType(bundle);
    }

    private void loadDataForEdit(ExpenseLogEntry expenseToEdit) {
        if (!mPage.getData().getBoolean(ExpenseWizardPage1.WAS_PRELOADED)) {
            //Date
            SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
            String dateString = formatter.format(expenseToEdit.getDate());
            mPage.getData().putString(ExpenseWizardPage1.EXPENSE_DATE_STRING_DATA_KEY, dateString);
            expenseDate = expenseToEdit.getDate();
            mPage.getData().putString(ExpenseWizardPage1.EXPENSE_DATE_STRING_FORMATTED_DATA_KEY, DateAndCurrencyDisplayer.getDateToDisplay(dateFormatCode, expenseDate));
            //Amount
            BigDecimal amountBD = expenseToEdit.getAmount().multiply(new BigDecimal(-1));
            String formatted = DateAndCurrencyDisplayer.getCurrencyToDisplay(moneyFormatCode, amountBD);
            mPage.getData().putString(ExpenseWizardPage1.EXPENSE_AMOUNT_FORMATTED_STRING_DATA_KEY, formatted);
            mPage.getData().putString(ExpenseWizardPage1.EXPENSE_AMOUNT_STRING_DATA_KEY, amountBD.toPlainString());
            amount = expenseToEdit.getAmount();
            //Type
            mPage.getData().putInt(ExpenseWizardPage1.EXPENSE_TYPE_ID_DATA_KEY, expenseToEdit.getTypeID());
            mPage.getData().putString(ExpenseWizardPage1.EXPENSE_TYPE_DATA_KEY, expenseToEdit.getTypeLabel());
            mPage.getData().putBoolean(ExpenseWizardPage1.WAS_PRELOADED, true);
        } else {
            preloadData(mPage.getData());
        }
    }
}