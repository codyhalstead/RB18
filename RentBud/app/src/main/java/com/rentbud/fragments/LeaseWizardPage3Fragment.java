package com.rentbud.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.android.wizardpager.wizard.ui.PageFragmentCallbacks;
import com.example.cody.rentbud.R;
import com.rentbud.helpers.AppFileManagementHelper;
import com.rentbud.helpers.DateAndCurrencyDisplayer;
import com.rentbud.model.Lease;
import com.rentbud.model.WizardDueDate;
import com.rentbud.sqlite.DatabaseHandler;
import com.rentbud.wizards.LeaseWizardPage1;
import com.rentbud.wizards.LeaseWizardPage3;

import org.joda.time.LocalDate;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static android.support.constraint.Constraints.TAG;

public class LeaseWizardPage3Fragment extends android.support.v4.app.Fragment {
    private static final String ARG_KEY = "key";

    private PageFragmentCallbacks mCallbacks;
    private String mKey;
    private LeaseWizardPage3 mPage;
    private TextView paymentsAmountTV, proratedPaymentsAmountTV, paymentsLabelTV, proratedPaymentsLabelTV, cycleStartLabelTV, newChangeWarningTV, editChangeWarningTV;
    private LinearLayout paymentInfoLL, proratedPaymentInfoLL;
    private EditText rentCostET;
    private BigDecimal rentCost;
    private Spinner paymentFrequencySpinner, paymentDateSpinner, paymentDaySpinner, cycleStartSpinner;
    private int regular, prorated, paymentDate, weeklyPaymentDay, paymentFrequency;
    private ArrayList<String> paymentDates;
    private ArrayAdapter<String> frequencyAdapter, dateAdapter, dayAdapter, cycleAdapter;
    private Boolean isFirstLoad;
    private DatabaseHandler databaseHandler;
    private LinkedHashMap<String, Integer> frequencyMap;
    private WizardDueDate[] weeklyDatesMap, monthlyDatesMap;
    private RelativeLayout paymentDateRL, paymentDayRL, cycleStartRL;
    private SharedPreferences preferences;
    private boolean isEdit;
    private int moneyFormatCode, dateFormatCode;
    Date leaseStartDate, leaseEndDate, selectedCycleStartDate;

    public static LeaseWizardPage3Fragment create(String key) {
        Bundle args = new Bundle();
        args.putString(ARG_KEY, key);

        LeaseWizardPage3Fragment fragment = new LeaseWizardPage3Fragment();
        fragment.setArguments(args);
        return fragment;
    }

    public LeaseWizardPage3Fragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        mKey = args.getString(ARG_KEY);
        mPage = (LeaseWizardPage3) mCallbacks.onGetPage(mKey);
        databaseHandler = new DatabaseHandler(getContext());
        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        this.moneyFormatCode = preferences.getInt("currency", DateAndCurrencyDisplayer.CURRENCY_US);
        this.dateFormatCode = preferences.getInt("dateFormat", DateAndCurrencyDisplayer.DATE_MMDDYYYY);
        frequencyMap = databaseHandler.getFrequencyLabelsMap();
        weeklyDatesMap = databaseHandler.getWeeklyDateOptions();
        monthlyDatesMap = databaseHandler.getMonthlyDateOptions();
        paymentDates = new ArrayList<>();
        paymentDate = 1;
        weeklyPaymentDay = 1;
        paymentFrequency = 1;
        isFirstLoad = true;
        if (mPage.getData().getString(LeaseWizardPage3.LEASE_NEED_BRANCH) == null) {
            mPage.getData().putString(LeaseWizardPage3.LEASE_NEED_BRANCH, "Yes");
        }
        isEdit = false;
        Bundle extras = mPage.getData();
        if (extras != null) {
            Lease leaseToEdit = extras.getParcelable("leaseToEdit");
            if (leaseToEdit != null) {
                loadDataForEdit(leaseToEdit);
                isEdit = true;
            } else {
                preloadData(extras);
            }
        } else {
            rentCost = new BigDecimal(0);
            String formatted = DateAndCurrencyDisplayer.getCurrencyToDisplay(moneyFormatCode, rentCost);
            mPage.getData().putString(LeaseWizardPage3.LEASE_RENT_COST_FORMATTED_STRING_DATA_KEY, formatted);
            mPage.getData().putString(LeaseWizardPage3.LEASE_RENT_COST_DATA_KEY, rentCost.toPlainString());
        }
        //isEdit = false;
        //Bundle extras = mPage.getData();
        //if (extras != null) {
        //    Lease leaseToEdit = extras.getParcelable("leaseToEdit");
        //    if (leaseToEdit != null) {
        //        loadDataForEdit(leaseToEdit);
        //        isEdit = true;
        //    } else {
        //        preloadData(extras);
        //    }
        //} else {

        //}
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_lease_wizard_page_3, container, false);
        //((TextView) rootView.findViewById(android.R.id.title)).setText(mPage.getTitle());
        (rootView.findViewById(android.R.id.title)).setVisibility(View.GONE);
        rentCostET = rootView.findViewById(R.id.leaseWizardRentCostET);
        if (mPage.getData().getString(LeaseWizardPage3.LEASE_RENT_COST_FORMATTED_STRING_DATA_KEY) != null) {
            rentCostET.setText(mPage.getData().getString(LeaseWizardPage3.LEASE_RENT_COST_FORMATTED_STRING_DATA_KEY));
        }
        rentCostET.setSelection(rentCostET.getText().length());

        paymentsAmountTV = rootView.findViewById(R.id.leaseWizardPaymentAmountsTV);
        proratedPaymentsAmountTV = rootView.findViewById(R.id.leaseWizardProratedPaymentAmountTV);

        paymentDateSpinner = rootView.findViewById(R.id.leaseWizardRentDueDateSpinner);
        //paymentDateSpinner.setSelection(mPage.getData().getInt(LeaseWizardPage3.LEASE_DUE_DATE_ID_DATA_KEY));
        paymentDateRL = rootView.findViewById(R.id.leaseWizardRentDueDateRL);

        paymentDaySpinner = rootView.findViewById(R.id.leaseWizardWeeklyDueDaySpinner);
        //paymentDaySpinner.setSelection(mPage.getData().getInt(LeaseWizardPage3.LEASE_DUE_DAY_ID_DATA_KEY));
        paymentDayRL = rootView.findViewById(R.id.leaseWizardWeeklyDueDayRL);

        paymentFrequencySpinner = rootView.findViewById(R.id.leaseWizardRentFrequencySpinner);
        //paymentFrequencySpinner.setSelection(mPage.getData().getInt(LeaseWizardPage3.LEASE_PAYMENT_FREQUENCY_ID_DATA_KEY));

        cycleStartLabelTV = rootView.findViewById(R.id.leaseWizardStartOfCycleTV);
        cycleStartSpinner = rootView.findViewById(R.id.leaseWizardStartOfCycleSpinner);
        cycleStartRL = rootView.findViewById(R.id.leaseWizardStartOfCycleRL);

        newChangeWarningTV = rootView.findViewById(R.id.leaseWizardNewChangeWarningTV);
        editChangeWarningTV = rootView.findViewById(R.id.leaseWizardEditChangeWarningTV);
        paymentInfoLL = rootView.findViewById(R.id.leaseWizardPaymentInfoRowLL);
        proratedPaymentInfoLL = rootView.findViewById(R.id.leaseWizardProratedPaymentInfoRowLL);
        if (isEdit) {
            newChangeWarningTV.setVisibility(View.GONE);
            editChangeWarningTV.setVisibility(View.VISIBLE);
            paymentInfoLL.setVisibility(View.GONE);
            proratedPaymentInfoLL.setVisibility(View.GONE);
            cycleStartLabelTV.setVisibility(View.GONE);
            cycleStartSpinner.setVisibility(View.GONE);
            cycleStartRL.setVisibility(View.GONE);
        } else {
            newChangeWarningTV.setVisibility(View.VISIBLE);
            editChangeWarningTV.setVisibility(View.GONE);
            paymentInfoLL.setVisibility(View.VISIBLE);
            proratedPaymentInfoLL.setVisibility(View.VISIBLE);
            cycleStartLabelTV.setVisibility(View.VISIBLE);
            cycleStartSpinner.setVisibility(View.VISIBLE);
            cycleStartRL.setVisibility(View.VISIBLE);
        }

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        Activity activity;
        if (context instanceof Activity) {
            activity = (Activity) context;
            if (!(activity instanceof PageFragmentCallbacks)) {
                throw new ClassCastException("Activity must implement PageFragmentCallbacks");
            }
            mCallbacks = (PageFragmentCallbacks) activity;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    private void populateFrequencySpinner() {
        //Create state array from MainActivity5.stateMap
        List<String> spinnerArray = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : frequencyMap.entrySet()) {
            spinnerArray.add(entry.getKey());
        }
        //Create ArrayAdapter with state array
        frequencyAdapter = new ArrayAdapter<>(
                getContext(), android.R.layout.simple_spinner_item, spinnerArray);
        frequencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Set ArrayAdapter to stateSpinner
        this.paymentFrequencySpinner.setAdapter(frequencyAdapter);
    }

    private void populateMonthlyDatesSpinner() {
        //Create state array from MainActivity5.stateMap
        List<String> spinnerArray = new ArrayList<>();
        //for (Map.Entry<String, Integer> entry : monthlyDatesMap.entrySet()) {
        //    spinnerArray.add(entry.getKey());
        //}
        for (int i = 0; i < monthlyDatesMap.length; i++) {
            spinnerArray.add(monthlyDatesMap[i].getLabel());
        }
        //Create ArrayAdapter with state array
        dateAdapter = new ArrayAdapter<>(
                getContext(), android.R.layout.simple_spinner_item, spinnerArray);
        dateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Set ArrayAdapter to stateSpinner
        this.paymentDateSpinner.setAdapter(dateAdapter);
    }

    private void populateWeeklyDatesSpinner() {
        //Create state array from MainActivity5.stateMap
        List<String> spinnerArray = new ArrayList<>();
        //for (Map.Entry<String, Integer> entry : weeklyDatesMap.entrySet()) {
        //    spinnerArray.add(entry.getKey());
        //}
        for (int i = 0; i < weeklyDatesMap.length; i++) {
            spinnerArray.add(weeklyDatesMap[i].getLabel());
        }
        //Create ArrayAdapter with state array
        dayAdapter = new ArrayAdapter<>(
                getContext(), android.R.layout.simple_spinner_item, spinnerArray);
        dayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Set ArrayAdapter to stateSpinner
        this.paymentDaySpinner.setAdapter(dayAdapter);
    }

    private void populateCycleOptionsSpinner(boolean isWeekly) {
        ArrayList<String> spinnerArray = new ArrayList<>();
        ArrayList<LocalDate> dateOptions;
        if (isWeekly) {
            dateOptions = getCycleDateOptionsWeekly(paymentFrequency, weeklyPaymentDay, leaseStartDate, leaseEndDate);
        } else {
            dateOptions = getCycleDateOptionsMonthly(paymentFrequency, paymentDate, leaseStartDate, leaseEndDate);
        }
        //for (Map.Entry<String, Integer> entry : weeklyDatesMap.entrySet()) {
        //    spinnerArray.add(entry.getKey());
        //}

        for (int i = 0; i < dateOptions.size(); i++) {
            spinnerArray.add(DateAndCurrencyDisplayer.getDateToDisplay(dateFormatCode, dateOptions.get(i)));
        }
        //Create ArrayAdapter with state array
        cycleAdapter = new ArrayAdapter<>(
                getContext(), android.R.layout.simple_spinner_item, spinnerArray);
        cycleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Set ArrayAdapter to stateSpinner
        this.cycleStartSpinner.setAdapter(cycleAdapter);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (mPage.getData().getInt(LeaseWizardPage3.LEASE_DUE_DATE_ID_DATA_KEY) != 0) {
            if(mPage.getData().getInt(LeaseWizardPage3.LEASE_DUE_DATE_ID_DATA_KEY) > 31){
                weeklyPaymentDay = mPage.getData().getInt(LeaseWizardPage3.LEASE_DUE_DATE_ID_DATA_KEY) - 31;
            } else {
                paymentDate = mPage.getData().getInt(LeaseWizardPage3.LEASE_DUE_DATE_ID_DATA_KEY);
            }
            //weeklyPaymentDay = mPage.getData().getInt(LeaseWizardPage3.LEASE_DUE_DATE_ID_DATA_KEY);
        }
        //if (mPage.getData().getInt(LeaseWizardPage3.LEASE_DUE_DAY_ID_DATA_KEY) != 0) {
        //    weeklyPaymentDay = mPage.getData().getInt(LeaseWizardPage3.LEASE_DUE_DAY_ID_DATA_KEY) - 31;
        //}
        if (mPage.getData().getInt(LeaseWizardPage3.LEASE_PAYMENT_FREQUENCY_DATA_KEY) == 0) {
            mPage.getData().putInt(LeaseWizardPage3.LEASE_PAYMENT_FREQUENCY_DATA_KEY, 1);
        }
        if (mPage.getData().getString(LeaseWizardPage3.LEASE_PAYMENT_CYCLE_DATA_KEY) != null) {
            selectedCycleStartDate = DateAndCurrencyDisplayer.getDateFromDisplay(dateFormatCode, mPage.getData().getString(LeaseWizardPage3.LEASE_PAYMENT_CYCLE_DATA_KEY));
        }
        paymentFrequency = mPage.getData().getInt(LeaseWizardPage3.LEASE_PAYMENT_FREQUENCY_DATA_KEY);
        populateFrequencySpinner();
        populateMonthlyDatesSpinner();
        populateWeeklyDatesSpinner();
        rentCostET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (rentCostET == null) return;
                String s = editable.toString();
                if (s.isEmpty()) return;
                rentCostET.removeTextChangedListener(this);
                String cleanString = DateAndCurrencyDisplayer.cleanMoneyString(s);
                rentCost = new BigDecimal(cleanString).setScale(2, BigDecimal.ROUND_FLOOR).divide(new BigDecimal(100), BigDecimal.ROUND_FLOOR);
                String formatted = DateAndCurrencyDisplayer.getCurrencyToDisplay(moneyFormatCode, rentCost);
                rentCostET.setText(formatted);
                rentCostET.setSelection(DateAndCurrencyDisplayer.getEndCursorPositionForMoneyInput(rentCostET.getText().length(), moneyFormatCode));
                mPage.getData().putString(LeaseWizardPage3.LEASE_RENT_COST_FORMATTED_STRING_DATA_KEY, formatted);
                mPage.getData().putString(LeaseWizardPage3.LEASE_RENT_COST_DATA_KEY, rentCost.toPlainString());
                if (getUserVisibleHint()) {
                    mPage.notifyDataChanged();
                }
                //rentCostET.setSelection(formatted.length());
                rentCostET.addTextChangedListener(this);
            }
        });
        rentCostET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rentCostET.setSelection(DateAndCurrencyDisplayer.getEndCursorPositionForMoneyInput(rentCostET.getText().length(), moneyFormatCode));
            }
        });

        String startDate = mCallbacks.onGetPage("Page1").getData().getString(LeaseWizardPage1.LEASE_START_DATE_STRING_DATA_KEY);
        String endDate = mCallbacks.onGetPage("Page1").getData().getString(LeaseWizardPage1.LEASE_END_DATE_STRING_DATA_KEY);

        DateFormat formatFrom = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
        try {
            leaseStartDate = formatFrom.parse(startDate);
            leaseEndDate = formatFrom.parse(endDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        paymentDateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, final int position, long l) {
                paymentDate = position + 1;
                if (!isFirstLoad) {
                    if (getUserVisibleHint()) {
                        new Handler().post(new Runnable() {
                            @Override
                            public void run() {
                                //       if (getUserVisibleHint()) {
                                if (!isEdit) {
                                    figurePayments(leaseStartDate, leaseEndDate, paymentDate, paymentFrequency, selectedCycleStartDate);
                                }
                                mPage.getData().putString(LeaseWizardPage3.LEASE_DUE_DATE_STRING_DATA_KEY, paymentDateSpinner.getSelectedItem().toString());

                                mPage.getData().putInt(LeaseWizardPage3.LEASE_DUE_DATE_ID_DATA_KEY, monthlyDatesMap[position].getDatabaseID());

                                mPage.notifyDataChanged();
                                String paymentsAmount = prorated + regular + "";
                                String proratedPaymentsAmount = prorated + "";
                                paymentsAmountTV.setText(paymentsAmount);
                                proratedPaymentsAmountTV.setText(proratedPaymentsAmount);
                                populateCycleOptionsSpinner(false);
                                //       }
                            }
                        });

                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        paymentDaySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, final int position, long l) {
                weeklyPaymentDay = position + 1;
                if (!isFirstLoad) {
                    if (getUserVisibleHint()) {
                        new Handler().post(new Runnable() {
                            @Override
                            public void run() {
                                //       if (getUserVisibleHint()) {
                                if (!isEdit) {
                                    figureWeeklyPayments(leaseStartDate, leaseEndDate, weeklyPaymentDay, paymentFrequency, selectedCycleStartDate);
                                }
                                mPage.getData().putString(LeaseWizardPage3.LEASE_DUE_DATE_STRING_DATA_KEY, paymentDaySpinner.getSelectedItem().toString());
                                int id = 1;
                                if (weeklyDatesMap[position] != null) {
                                    id = weeklyDatesMap[position].getDatabaseID();
                                }
                                mPage.getData().putInt(LeaseWizardPage3.LEASE_DUE_DATE_ID_DATA_KEY, id);
                                mPage.notifyDataChanged();
                                String paymentsAmount = prorated + regular + "";
                                String proratedPaymentsAmount = prorated + "";
                                paymentsAmountTV.setText(paymentsAmount);
                                proratedPaymentsAmountTV.setText(proratedPaymentsAmount);
                                populateCycleOptionsSpinner(true);
                                //       }
                            }
                        });

                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        paymentFrequencySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                final String frequency = paymentFrequencySpinner.getSelectedItem().toString();
                int frequencyID = 1;
                if (frequencyMap.get(frequency) != null) {
                    frequencyID = frequencyMap.get(frequency);
                }
                mPage.getData().putInt(LeaseWizardPage3.LEASE_PAYMENT_FREQUENCY_ID_DATA_KEY, frequencyID);
                paymentFrequency = getFrequencyNumber(frequencyID);
                if (frequencyID > 7) {
                    mPage.getData().putBoolean(LeaseWizardPage3.LEASE_ARE_PAYMENTS_WEEKLY_DATA_KEY, true);
                } else {
                    mPage.getData().putBoolean(LeaseWizardPage3.LEASE_ARE_PAYMENTS_WEEKLY_DATA_KEY, false);
                }
                if (!isFirstLoad) {
                    if (getUserVisibleHint()) {
                        new Handler().post(new Runnable() {
                            @Override
                            public void run() {
                                if (mPage.getData().getBoolean(LeaseWizardPage3.LEASE_ARE_PAYMENTS_WEEKLY_DATA_KEY)) {
                                    paymentDayRL.setVisibility(View.VISIBLE);
                                    paymentDateRL.setVisibility(View.GONE);
                                    if (!isEdit) {
                                        figureWeeklyPayments(leaseStartDate, leaseEndDate, weeklyPaymentDay, paymentFrequency, selectedCycleStartDate);
                                        populateCycleOptionsSpinner(true);
                                    }
                                } else {
                                    paymentDayRL.setVisibility(View.GONE);
                                    paymentDateRL.setVisibility(View.VISIBLE);
                                    if (!isEdit) {
                                        figurePayments(leaseStartDate, leaseEndDate, paymentDate, paymentFrequency, selectedCycleStartDate);
                                        populateCycleOptionsSpinner(false);
                                    }
                                }
                                mPage.getData().putInt(LeaseWizardPage3.LEASE_PAYMENT_FREQUENCY_DATA_KEY, paymentFrequency);
                                mPage.getData().putString(LeaseWizardPage3.LEASE_PAYMENT_FREQUENCY_STRING_DATA_KEY, paymentFrequencySpinner.getSelectedItem().toString());
                                String paymentsAmount = prorated + regular + "";
                                String proratedPaymentsAmount = prorated + "";
                                paymentsAmountTV.setText(paymentsAmount);
                                proratedPaymentsAmountTV.setText(proratedPaymentsAmount);
                                mPage.notifyDataChanged();
                                //       }
                            }
                        });
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        if (!isEdit) {
            cycleStartSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, final int position, long l) {

                    if (!isFirstLoad) {
                        if (getUserVisibleHint()) {
                            new Handler().post(new Runnable() {
                                @Override
                                public void run() {
                                    //       if (getUserVisibleHint()) {
                                    //if (!isEdit) {

                                    //}
                                    String dateString = cycleStartSpinner.getSelectedItem().toString();
                                    selectedCycleStartDate = DateAndCurrencyDisplayer.getDateFromDisplay(dateFormatCode, dateString);
                                    mPage.getData().putString(LeaseWizardPage3.LEASE_PAYMENT_CYCLE_DATA_KEY, dateString);
                                    //mPage.notifyDataChanged();
                                    //String paymentsAmount = prorated + regular + "";
                                    //String proratedPaymentsAmount = prorated + "";

                                    if(mPage.getData().getBoolean(LeaseWizardPage3.LEASE_ARE_PAYMENTS_WEEKLY_DATA_KEY)){
                                        figureWeeklyPayments(leaseStartDate, leaseEndDate, weeklyPaymentDay, paymentFrequency, selectedCycleStartDate);
                                    } else {
                                        figurePayments(leaseStartDate, leaseEndDate, paymentDate, paymentFrequency, selectedCycleStartDate);
                                    }
                                    mPage.notifyDataChanged();
                                    String paymentsAmount = prorated + regular + "";
                                    String proratedPaymentsAmount = prorated + "";
                                    paymentsAmountTV.setText(paymentsAmount);
                                    proratedPaymentsAmountTV.setText(proratedPaymentsAmount);
                                    isFirstLoad = false;
                                    //paymentsAmountTV.setText(paymentsAmount);
                                    //proratedPaymentsAmountTV.setText(proratedPaymentsAmount);
                                    //populateCycleOptionsSpinner(false);
                                    //       }
                                }
                            });

                        }
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        }
        //paymentDate = 20;



        if (mPage.getData().getString(LeaseWizardPage3.LEASE_RENT_COST_DATA_KEY) == null) {
            String formatted = NumberFormat.getCurrencyInstance().format(rentCost);
            mPage.getData().putString(LeaseWizardPage3.LEASE_RENT_COST_FORMATTED_STRING_DATA_KEY, formatted);
            mPage.getData().putString(LeaseWizardPage3.LEASE_RENT_COST_DATA_KEY, rentCost.toPlainString());
        }

        if (mPage.getData().getString(LeaseWizardPage3.LEASE_PAYMENT_FREQUENCY_STRING_DATA_KEY) != null) {
            int spinnerPosition = frequencyAdapter.getPosition(mPage.getData().getString(LeaseWizardPage3.LEASE_PAYMENT_FREQUENCY_STRING_DATA_KEY));
            paymentFrequencySpinner.setSelection(spinnerPosition);
        }
        if (mPage.getData().getBoolean(LeaseWizardPage3.LEASE_ARE_PAYMENTS_WEEKLY_DATA_KEY)) {
            if (mPage.getData().getString(LeaseWizardPage3.LEASE_DUE_DATE_STRING_DATA_KEY) != null) {
                int spinnerPosition = dayAdapter.getPosition(mPage.getData().getString(LeaseWizardPage3.LEASE_DUE_DATE_STRING_DATA_KEY));
                paymentDaySpinner.setSelection(spinnerPosition);
            }
        } else {
            if (mPage.getData().getString(LeaseWizardPage3.LEASE_DUE_DATE_STRING_DATA_KEY) != null) {
                int spinnerPosition = dateAdapter.getPosition(mPage.getData().getString(LeaseWizardPage3.LEASE_DUE_DATE_STRING_DATA_KEY));
                paymentDateSpinner.setSelection(spinnerPosition);
            }
        }

        if (mPage.getData().getString(LeaseWizardPage3.LEASE_PAYMENT_FREQUENCY_STRING_DATA_KEY) == null) {
            mPage.getData().putInt(LeaseWizardPage3.LEASE_PAYMENT_FREQUENCY_ID_DATA_KEY, paymentFrequency);
            mPage.getData().putString(LeaseWizardPage3.LEASE_PAYMENT_FREQUENCY_STRING_DATA_KEY, paymentFrequencySpinner.getSelectedItem().toString());
        }
        if (mPage.getData().getString(LeaseWizardPage3.LEASE_DUE_DATE_STRING_DATA_KEY) == null) {
            mPage.getData().putString(LeaseWizardPage3.LEASE_DUE_DATE_STRING_DATA_KEY, paymentDateSpinner.getSelectedItem().toString());
            mPage.getData().putInt(LeaseWizardPage3.LEASE_DUE_DATE_ID_DATA_KEY, paymentDate);
        }
        //Handler handler = new Handler();
        //handler.post(new Runnable() {
        //    @Override
        //    public void run() {
        //        mPage.notifyDataChanged();
        //    }
        //});
        populateCycleOptionsSpinner(mPage.getData().getBoolean(LeaseWizardPage3.LEASE_ARE_PAYMENTS_WEEKLY_DATA_KEY));
        if(mPage.getData().getString(LeaseWizardPage3.LEASE_PAYMENT_CYCLE_DATA_KEY) == null){
            selectedCycleStartDate = DateAndCurrencyDisplayer.getDateFromDisplay(dateFormatCode, cycleStartSpinner.getItemAtPosition(0).toString());
            mPage.getData().putString(LeaseWizardPage3.LEASE_PAYMENT_CYCLE_DATA_KEY, DateAndCurrencyDisplayer.getDateToDisplay(dateFormatCode, selectedCycleStartDate));
        }
        if (mPage.getData().getString(LeaseWizardPage3.LEASE_PAYMENT_CYCLE_DATA_KEY) != null) {
            int spinnerPosition = cycleAdapter.getPosition(mPage.getData().getString(LeaseWizardPage3.LEASE_PAYMENT_CYCLE_DATA_KEY));
            cycleStartSpinner.setSelection(spinnerPosition);
        }
        String isOtherBranchNeeded = mPage.getData().getString(LeaseWizardPage3.LEASE_NEED_BRANCH);
        if (mPage.getData().getBoolean(LeaseWizardPage3.LEASE_ARE_PAYMENTS_WEEKLY_DATA_KEY)) {
            if (!isEdit) {
                figureWeeklyPayments(leaseStartDate, leaseEndDate, weeklyPaymentDay, paymentFrequency, selectedCycleStartDate);
            }
            paymentDayRL.setVisibility(View.VISIBLE);
            paymentDateRL.setVisibility(View.GONE);
        } else {
            if (!isEdit) {
                figurePayments(leaseStartDate, leaseEndDate, paymentDate, paymentFrequency, selectedCycleStartDate);
            }
            paymentDayRL.setVisibility(View.GONE);
            paymentDateRL.setVisibility(View.VISIBLE);
        }
        String paymentsAmount = prorated + regular + "";
        String proratedPaymentsAmount = prorated + "";
        paymentsAmountTV.setText(paymentsAmount);
        proratedPaymentsAmountTV.setText(proratedPaymentsAmount);
        if(!isOtherBranchNeeded.equals(mPage.getData().getString(LeaseWizardPage3.LEASE_NEED_BRANCH))){
            Handler handler = new Handler();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    mPage.notifyDataChanged();
                }
            });
        }
        isFirstLoad = false;
    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);

        // In a future update to the support library, this should override setUserVisibleHint
        // instead of setMenuVisibility.
        if (rentCostET != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            if (!menuVisible) {
                imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
            }
        }
    }

    private void figureWeeklyPayments(Date leaseStartDate, Date leaseEndDate, int paymentDay, int paymentFrequency, Date cycleStartDate) {
        prorated = 0;
        regular = 0;
        LocalDate startDate = new LocalDate(leaseStartDate);
        LocalDate endDate = new LocalDate(leaseEndDate);
        paymentDates.clear();
        LocalDate payment = new LocalDate(cycleStartDate);
        //if(paymentDay > 7) {
        //    paymentDay = paymentDaySpinner.getSelectedItemPosition() + 1;
        //}
        //>//while (payment.getDayOfWeek() != paymentDay) {
        //>//    payment = payment.plusDays(1);
        //Log.d(TAG, "figureWeeklyPayments: " + payment.getDayOfWeek());
        //Log.d(TAG, "figureWeeklyPayments: " + paymentDay);
        //>//}
        if (startDate.plusWeeks(paymentFrequency).isAfter(endDate)) {
            //Not full cycle, need 2 prorated
            if (payment.isAfter(startDate) && payment.isBefore(endDate)) {
                prorated = prorated + 2;
                mPage.getData().putBoolean(LeaseWizardPage3.LEASE_IS_FIRST_PRORATED_REQUIRED_DATA_KEY, true);
                mPage.getData().putBoolean(LeaseWizardPage3.LEASE_IS_LAST_PRORATED_REQUIRED_DATA_KEY, true);
                paymentDates.add(startDate.toString("yyyy-MM-dd"));
                paymentDates.add(payment.toString("yyyy-MM-dd"));
                //paymentDates.add(endDate.toString("yyyy-MM-dd"));
                //Log.d(TAG, "figurePayments: INITIAL PAYMENT ---- " + startDate + " --> " + payment + " PRO");
                //Log.d(TAG, "figurePayments: FINAL PAYMENT ---- " + payment + " --> " + endDate + " PRO");
            } else if (payment.plusWeeks(paymentFrequency).isBefore(endDate)) {
                payment = payment.plusMonths(paymentFrequency);
                prorated = prorated + 2;
                mPage.getData().putBoolean(LeaseWizardPage3.LEASE_IS_FIRST_PRORATED_REQUIRED_DATA_KEY, true);
                mPage.getData().putBoolean(LeaseWizardPage3.LEASE_IS_LAST_PRORATED_REQUIRED_DATA_KEY, true);
                paymentDates.add(startDate.toString("yyyy-MM-dd"));
                paymentDates.add(payment.toString("yyyy-MM-dd"));
                //Log.d(TAG, "figureWeeklyPayments: Oh");
                //paymentDates.add(endDate.toString("yyyy-MM-dd"));
                //Log.d(TAG, "figurePayments: INITIAL PAYMENT ---- " + startDate + " --> " + payment + " PRO");
                //Log.d(TAG, "figurePayments: FINAL PAYMENT ---- " + payment + " --> " + endDate + " PRO");
            } else {
                prorated++;
                mPage.getData().putBoolean(LeaseWizardPage3.LEASE_IS_FIRST_PRORATED_REQUIRED_DATA_KEY, true);
                mPage.getData().putBoolean(LeaseWizardPage3.LEASE_IS_LAST_PRORATED_REQUIRED_DATA_KEY, false);
                paymentDates.add(startDate.toString("yyyy-MM-dd"));
                paymentDates.add(endDate.toString("yyyy-MM-dd"));
                //Log.d(TAG, "figureWeeklyPayments: Ohsha");
                //paymentDates.add(endDate.toString("yyyy-MM-dd"));
                //Log.d(TAG, "figurePayments: INITIAL AND FINAL PAYMENT ---- " + startDate + " --> " + endDate + " PRO");
            }
        } else if (startDate.isEqual(payment) && endDate.isEqual(payment.plusWeeks(paymentFrequency))) {
            //Full cycle, but only 1. 1 regular payment
            regular++;
            mPage.getData().putBoolean(LeaseWizardPage3.LEASE_IS_FIRST_PRORATED_REQUIRED_DATA_KEY, false);
            mPage.getData().putBoolean(LeaseWizardPage3.LEASE_IS_LAST_PRORATED_REQUIRED_DATA_KEY, false);
            paymentDates.add(startDate.toString("yyyy-MM-dd"));
            paymentDates.add(payment.toString("yyyy-MM-dd"));

        } else {
            paymentDates.add(startDate.toString("yyyy-MM-dd"));
            if (startDate.isBefore(payment)) {
                prorated++;
                //payment = payment.plusMonths(1);
                mPage.getData().putBoolean(LeaseWizardPage3.LEASE_IS_FIRST_PRORATED_REQUIRED_DATA_KEY, true);
                //Log.d(TAG, "figurePayments: INITIAL PAYMENT ---- " + startDate + " --> " + payment + " PRO");
            } else if (startDate.isEqual(payment)) {
                regular++;
                payment = payment.plusWeeks(paymentFrequency);
                //payment = keepPaymentDayConsistentForEndOfMonth(paymentDate, payment);
                mPage.getData().putBoolean(LeaseWizardPage3.LEASE_IS_FIRST_PRORATED_REQUIRED_DATA_KEY, false);
                //Log.d(TAG, "figurePayments: INITIAL PAYMENT ---- " + startDate + " --> " + payment);
            } else {
                payment = payment.plusWeeks(paymentFrequency);
                //payment = keepPaymentDayConsistentForEndOfMonth(paymentDate, payment);
                prorated++;
                mPage.getData().putBoolean(LeaseWizardPage3.LEASE_IS_FIRST_PRORATED_REQUIRED_DATA_KEY, true);
                //Log.d(TAG, "figurePayments: INITIAL PAYMENT ---- " + startDate + " --> " + payment + " PRO");
            }
            while (payment.isBefore(endDate.minusWeeks(paymentFrequency))) {
                regular++;
                LocalDate pdate = payment;
                paymentDates.add(pdate.toString("yyyy-MM-dd"));
                payment = payment.plusWeeks(paymentFrequency);
                //payment = keepPaymentDayConsistentForEndOfMonth(paymentDate, payment);
                //Log.d(TAG, "figurePayments: PAYMENT ---- " + pdate + " --> " + payment);
            }
            if (payment.plusWeeks(paymentFrequency).equals(endDate)) { //|| startDate.plusMonths(1).isAfter(payment)) {
                regular++;
                paymentDates.add(payment.toString("yyyy-MM-dd"));
                //Log.d(TAG, "figurePayments: FINAL PAYMENT ---- " + payment + " --> " + endDate);
                mPage.getData().putBoolean(LeaseWizardPage3.LEASE_IS_LAST_PRORATED_REQUIRED_DATA_KEY, false);
            } else {
                prorated++;
                paymentDates.add(payment.toString("yyyy-MM-dd"));
                //Log.d(TAG, "figurePayments: FINAL PAYMENT ---- " + payment + " --> " + endDate + " PRO");
                mPage.getData().putBoolean(LeaseWizardPage3.LEASE_IS_LAST_PRORATED_REQUIRED_DATA_KEY, true);
            }
        }
        //paymentDates.add(endDate.toString("yyyy-MM-dd"));
        mPage.getData().putStringArrayList(LeaseWizardPage3.LEASE_PAYMENT_DATES_ARRAY_DATA_KEY, paymentDates);
        if (prorated > 0) {
            mPage.getData().putString(LeaseWizardPage3.LEASE_NEED_BRANCH, "Yes");
        } else {
            mPage.getData().putString(LeaseWizardPage3.LEASE_NEED_BRANCH, "No");
        }
    }

    private void figurePayments(Date leaseStartDate, Date leaseEndDate, int paymentDay, int paymentFrequency, Date cycleStartDate) {
        prorated = 0;
        regular = 0;
        Log.d(TAG, "figurePayments: " + paymentDay);
        LocalDate startDate = new LocalDate(leaseStartDate);
        LocalDate endDate = new LocalDate(leaseEndDate);
        paymentDates.clear();
        LocalDate payment = new LocalDate(cycleStartDate);
        //if (paymentDay > 27) {
        //    payment = new LocalDate(startDate.getYear(), startDate.getMonthOfYear(), 27);
        //    payment = keepPaymentDayConsistentForEndOfMonth(paymentDay, payment);
        //} else {
        //    payment = new LocalDate(startDate.getYear(), startDate.getMonthOfYear(), paymentDay);
        //}
        if (startDate.plusMonths(paymentFrequency).isAfter(endDate)) {
            //Not full cycle, need 2 prorated
            if (payment.isAfter(startDate) && payment.isBefore(endDate)) {
                prorated = prorated + 2;
                mPage.getData().putBoolean(LeaseWizardPage3.LEASE_IS_FIRST_PRORATED_REQUIRED_DATA_KEY, true);
                mPage.getData().putBoolean(LeaseWizardPage3.LEASE_IS_LAST_PRORATED_REQUIRED_DATA_KEY, true);
                paymentDates.add(startDate.toString("yyyy-MM-dd"));
                paymentDates.add(payment.toString("yyyy-MM-dd"));
                //paymentDates.add(endDate.toString("yyyy-MM-dd"));
                //Log.d(TAG, "figurePayments: INITIAL PAYMENT ---- " + startDate + " --> " + payment + " PRO");
                //Log.d(TAG, "figurePayments: FINAL PAYMENT ---- " + payment + " --> " + endDate + " PRO");
            } else if (payment.plusMonths(paymentFrequency).isBefore(endDate)) {
                payment = payment.plusMonths(paymentFrequency);
                prorated = prorated + 2;
                mPage.getData().putBoolean(LeaseWizardPage3.LEASE_IS_FIRST_PRORATED_REQUIRED_DATA_KEY, true);
                mPage.getData().putBoolean(LeaseWizardPage3.LEASE_IS_LAST_PRORATED_REQUIRED_DATA_KEY, true);
                paymentDates.add(startDate.toString("yyyy-MM-dd"));
                paymentDates.add(payment.toString("yyyy-MM-dd"));
                //paymentDates.add(endDate.toString("yyyy-MM-dd"));
                //Log.d(TAG, "figurePayments: INITIAL PAYMENT ---- " + startDate + " --> " + payment + " PRO");
                //Log.d(TAG, "figurePayments: FINAL PAYMENT ---- " + payment + " --> " + endDate + " PRO");
            } else {
                prorated++;
                mPage.getData().putBoolean(LeaseWizardPage3.LEASE_IS_FIRST_PRORATED_REQUIRED_DATA_KEY, true);
                mPage.getData().putBoolean(LeaseWizardPage3.LEASE_IS_LAST_PRORATED_REQUIRED_DATA_KEY, false);
                paymentDates.add(startDate.toString("yyyy-MM-dd"));
                paymentDates.add(endDate.toString("yyyy-MM-dd"));
                //paymentDates.add(endDate.toString("yyyy-MM-dd"));
                //Log.d(TAG, "figurePayments: INITIAL AND FINAL PAYMENT ---- " + startDate + " --> " + endDate + " PRO");
            }
        } else if (startDate.isEqual(payment) && endDate.isEqual(payment.plusMonths(paymentFrequency))) {
            //Full cycle, but only 1. 1 regular payment
            regular++;
            mPage.getData().putBoolean(LeaseWizardPage3.LEASE_IS_FIRST_PRORATED_REQUIRED_DATA_KEY, false);
            mPage.getData().putBoolean(LeaseWizardPage3.LEASE_IS_LAST_PRORATED_REQUIRED_DATA_KEY, false);
            paymentDates.add(startDate.toString("yyyy-MM-dd"));
            //paymentDates.add(endDate.toString("yyyy-MM-dd"));
            //Log.d(TAG, "figurePayments: INITIAL AND FINAL PAYMENT ---- " + startDate + " --> " + endDate);
        } else {
            paymentDates.add(startDate.toString("yyyy-MM-dd"));
            if (startDate.isBefore(payment)) {
                prorated++;
                //payment = payment.plusMonths(1);
                mPage.getData().putBoolean(LeaseWizardPage3.LEASE_IS_FIRST_PRORATED_REQUIRED_DATA_KEY, true);
                //Log.d(TAG, "figurePayments: INITIAL PAYMENT ---- " + startDate + " --> " + payment + " PRO");
            } else if (startDate.isEqual(payment)) {
                regular++;
                payment = payment.plusMonths(paymentFrequency);
                payment = keepPaymentDayConsistentForEndOfMonth(paymentDay, payment);
                mPage.getData().putBoolean(LeaseWizardPage3.LEASE_IS_FIRST_PRORATED_REQUIRED_DATA_KEY, false);
                //Log.d(TAG, "figurePayments: INITIAL PAYMENT ---- " + startDate + " --> " + payment);
            } else {
                payment = payment.plusMonths(paymentFrequency);
                payment = keepPaymentDayConsistentForEndOfMonth(paymentDay, payment);
                prorated++;
                mPage.getData().putBoolean(LeaseWizardPage3.LEASE_IS_FIRST_PRORATED_REQUIRED_DATA_KEY, true);
                //Log.d(TAG, "figurePayments: INITIAL PAYMENT ---- " + startDate + " --> " + payment + " PRO");
            }

            while (payment.isBefore(endDate.minusMonths(paymentFrequency))) {
                regular++;
                LocalDate pdate = payment;
                paymentDates.add(pdate.toString("yyyy-MM-dd"));
                payment = payment.plusMonths(paymentFrequency);
                payment = keepPaymentDayConsistentForEndOfMonth(paymentDay, payment);
                //Log.d(TAG, "figurePayments: PAYMENT ---- " + pdate + " --> " + payment);
            }
            if (payment.plusMonths(paymentFrequency).equals(endDate)) { //|| startDate.plusMonths(1).isAfter(payment)) {
                regular++;
                paymentDates.add(payment.toString("yyyy-MM-dd"));
                //Log.d(TAG, "figurePayments: FINAL PAYMENT ---- " + payment + " --> " + endDate);
                mPage.getData().putBoolean(LeaseWizardPage3.LEASE_IS_LAST_PRORATED_REQUIRED_DATA_KEY, false);
            } else {
                prorated++;
                paymentDates.add(payment.toString("yyyy-MM-dd"));
                //Log.d(TAG, "figurePayments: FINAL PAYMENT ---- " + payment + " --> " + endDate + " PRO");
                mPage.getData().putBoolean(LeaseWizardPage3.LEASE_IS_LAST_PRORATED_REQUIRED_DATA_KEY, true);
            }
            //paymentDates.add(endDate.toString("yyyy-MM-dd"));
        }
        mPage.getData().putStringArrayList(LeaseWizardPage3.LEASE_PAYMENT_DATES_ARRAY_DATA_KEY, paymentDates);
        if (prorated > 0) {
            mPage.getData().putString(LeaseWizardPage3.LEASE_NEED_BRANCH, "Yes");
        } else {
            mPage.getData().putString(LeaseWizardPage3.LEASE_NEED_BRANCH, "No");
        }
    }

    private LocalDate keepPaymentDayConsistentForEndOfMonth(int paymentDay, LocalDate payment) {
        if (paymentDay > 28) {
            if (paymentDay != payment.getDayOfMonth()) {
                payment = payment.dayOfMonth().withMaximumValue();
                while (payment.getDayOfMonth() > 28) {
                    if (payment.getDayOfMonth() <= paymentDay) {
                        return payment;
                    } else {
                        payment = payment.minusDays(1);
                    }
                }
            }
        }
        return payment;
    }

    private ArrayList<LocalDate> getCycleDateOptionsMonthly(int paymentFrequency, int dueDate, Date startDate, Date endDate) {
        ArrayList<LocalDate> cycleOptions = new ArrayList<>();
        LocalDate startDateLD = new LocalDate(startDate);
        LocalDate endDateLD = new LocalDate(endDate);
        LocalDate option;
        if (dueDate > 27) {
            option = new LocalDate(startDateLD.getYear(), startDateLD.getMonthOfYear(), 27);
            option = keepPaymentDayConsistentForEndOfMonth(dueDate, option);
        } else {
            option = new LocalDate(startDateLD.getYear(), startDateLD.getMonthOfYear(), dueDate);
        }
        if (option.isBefore(startDateLD)) {
            option = option.plusMonths(1);
            option = keepPaymentDayConsistentForEndOfMonth(dueDate, option);
        }
        if (endDateLD.isBefore(option)) {
            cycleOptions.add(startDateLD);
        } else {
            for (int i = 0; i < paymentFrequency; i++) {
                if (option.isBefore(endDateLD)) {
                    cycleOptions.add(option);
                    option = option.plusMonths(1);
                } else {
                    break;
                }
            }
        }
        return cycleOptions;
    }

    private ArrayList<LocalDate> getCycleDateOptionsWeekly(int paymentFrequency, int dueDay, Date startDate, Date endDate) {
        ArrayList<LocalDate> cycleOptions = new ArrayList<>();
        LocalDate startDateLD = new LocalDate(startDate);
        LocalDate endDateLD = new LocalDate(endDate);
        LocalDate option = new LocalDate(startDateLD);
        while (option.getDayOfWeek() != dueDay) {
            option = option.plusDays(1);
        }
        if (endDateLD.isBefore(option)) {
            cycleOptions.add(startDateLD);
        } else {
            for (int i = 0; i < paymentFrequency; i++) {
                if (option.isBefore(endDateLD)) {
                    cycleOptions.add(option);
                    option = option.plusWeeks(1);
                } else {
                    break;
                }
            }
        }
        return cycleOptions;
    }

    private int getFrequencyNumber(int frequencyID) {
        int frequencyNumber = 1;
        if (frequencyID == 1) {

        } else if (frequencyID == 2) {
            frequencyNumber = 2;
        } else if (frequencyID == 3) {
            frequencyNumber = 3;
        } else if (frequencyID == 4) {
            frequencyNumber = 4;
        } else if (frequencyID == 5) {
            frequencyNumber = 6;
        } else if (frequencyID == 6) {
            frequencyNumber = 9;
        } else if (frequencyID == 7) {
            frequencyNumber = 12;
        } else if (frequencyID == 8) {

        } else if (frequencyID == 9) {
            frequencyNumber = 2;
        } else if (frequencyID == 10) {
            frequencyNumber = 3;
        }
        return frequencyNumber;
    }

    private void preloadAmount(Bundle bundle) {
        if (mPage.getData().getString(LeaseWizardPage3.LEASE_RENT_COST_DATA_KEY) != null) {
            rentCost = new BigDecimal(mPage.getData().getString(LeaseWizardPage3.LEASE_RENT_COST_DATA_KEY)).setScale(2, BigDecimal.ROUND_FLOOR).divide(new BigDecimal(100), BigDecimal.ROUND_FLOOR);
        } else {
            rentCost = new BigDecimal(0);
            String formatted = DateAndCurrencyDisplayer.getCurrencyToDisplay(moneyFormatCode, rentCost);
            mPage.getData().putString(LeaseWizardPage3.LEASE_RENT_COST_FORMATTED_STRING_DATA_KEY, formatted);
            mPage.getData().putString(LeaseWizardPage3.LEASE_RENT_COST_DATA_KEY, rentCost.toPlainString());
        }
    }

    private void preloadFrequency(Bundle bundle) {

    }

    private void preloadDueDate(Bundle bundle) {

    }

    private void preloadData(Bundle bundle) {
        preloadAmount(bundle);
        preloadFrequency(bundle);
        preloadDueDate(bundle);
    }

    private void loadDataForEdit(Lease leaseToEdit) {
        if (!mPage.getData().getBoolean(LeaseWizardPage3.WAS_PRELOADED)) {
            //Rent cost
            String formatted = DateAndCurrencyDisplayer.getCurrencyToDisplay(moneyFormatCode, leaseToEdit.getMonthlyRentCost());
            mPage.getData().putString(LeaseWizardPage3.LEASE_RENT_COST_FORMATTED_STRING_DATA_KEY, formatted);
            mPage.getData().putString(LeaseWizardPage3.LEASE_RENT_COST_DATA_KEY, leaseToEdit.getMonthlyRentCost().toPlainString());
            rentCost = leaseToEdit.getMonthlyRentCost();
            //Frequency
            mPage.getData().putInt(LeaseWizardPage3.LEASE_PAYMENT_FREQUENCY_ID_DATA_KEY, leaseToEdit.getPaymentFrequencyID());
            mPage.getData().putString(LeaseWizardPage3.LEASE_PAYMENT_FREQUENCY_STRING_DATA_KEY, getKeyByValue(this.frequencyMap, leaseToEdit.getPaymentFrequencyID()));
            mPage.getData().putInt(LeaseWizardPage3.LEASE_PAYMENT_FREQUENCY_DATA_KEY, getFrequencyNumber(leaseToEdit.getPaymentFrequencyID()));
            paymentFrequency = leaseToEdit.getPaymentFrequencyID();
            //Day/Date
            if (leaseToEdit.getPaymentDayID() > 31) {
                mPage.getData().putBoolean(LeaseWizardPage3.LEASE_ARE_PAYMENTS_WEEKLY_DATA_KEY, true);
                String stringDataKey = "";
                for (int i = 0; i < weeklyDatesMap.length; i++) {
                    if (weeklyDatesMap[i].getDatabaseID() == leaseToEdit.getPaymentDayID()) {
                        stringDataKey = weeklyDatesMap[i].getLabel();
                    }
                }
                mPage.getData().putString(LeaseWizardPage3.LEASE_DUE_DATE_STRING_DATA_KEY, stringDataKey);
                mPage.getData().putInt(LeaseWizardPage3.LEASE_DUE_DATE_ID_DATA_KEY, leaseToEdit.getPaymentDayID());

                weeklyPaymentDay = leaseToEdit.getPaymentDayID() - 31;
            } else {
                mPage.getData().putBoolean(LeaseWizardPage3.LEASE_ARE_PAYMENTS_WEEKLY_DATA_KEY, false);
                String stringDataKey = "";
                for (int i = 0; i < monthlyDatesMap.length; i++) {
                    if (monthlyDatesMap[i].getDatabaseID() == leaseToEdit.getPaymentDayID()) {
                        stringDataKey = monthlyDatesMap[i].getLabel();
                    }
                }
                mPage.getData().putString(LeaseWizardPage3.LEASE_DUE_DATE_STRING_DATA_KEY, stringDataKey);
                mPage.getData().putInt(LeaseWizardPage3.LEASE_DUE_DATE_ID_DATA_KEY, leaseToEdit.getPaymentDayID());
                paymentDate = leaseToEdit.getPaymentDayID();
            }
            mPage.getData().putBoolean(LeaseWizardPage3.WAS_PRELOADED, true);
        } else {
            preloadData(mPage.getData());
        }
    }

    public String getKeyByValue(Map<String, Integer> map, Integer value) {
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            if (entry.getValue().equals(value)) {
                return entry.getKey();
            }
        }
        return null;
    }
}