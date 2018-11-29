package com.rba18.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.rba18.R;
import com.rba18.helpers.DateAndCurrencyDisplayer;
import com.rba18.model.Lease;
import com.rba18.model.WizardDueDate;
import com.rba18.sqlite.DatabaseHandler;
import com.rba18.wizards.LeaseWizardPage1;
import com.rba18.wizards.LeaseWizardPage3;

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

public class LeaseWizardPage3Fragment extends android.support.v4.app.Fragment {
    private static final String ARG_KEY = "key";
    private PageFragmentCallbacks mCallbacks;
    private String mKey;
    private LeaseWizardPage3 mPage;
    private TextView mPaymentsAmountTV, mProratedPaymentsAmountTV;
    private EditText mRentCostET;
    private BigDecimal mRentCost;
    private Spinner mPaymentFrequencySpinner, mPaymentDateSpinner, mPaymentDaySpinner, mCycleStartSpinner;
    private int mRegular, mProrated, mPaymentDate, mWeeklyPaymentDay, mPaymentFrequency, mMoneyFormatCode, mDateFormatCode;
    private ArrayList<String> mPaymentDates;
    private ArrayAdapter<String> mFrequencyAdapter, mDateAdapter, mDayAdapter, mCycleAdapter;
    private Boolean mIsFirstLoad;
    private LinkedHashMap<String, Integer> mFrequencyMap;
    private WizardDueDate[] mWeeklyDatesMap, mMonthlyDatesMap;
    private RelativeLayout mPaymentDateRL, mPaymentDayRL, mCycleStartRL;
    private boolean mIsEdit;
    private Date mLeaseStartDate, mLeaseEndDate, mSelectedCycleStartDate;

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
        DatabaseHandler databaseHandler = new DatabaseHandler(getContext());
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        mMoneyFormatCode = preferences.getInt("currency", DateAndCurrencyDisplayer.CURRENCY_US);
        mDateFormatCode = preferences.getInt("dateFormat", DateAndCurrencyDisplayer.DATE_MMDDYYYY);
        mFrequencyMap = databaseHandler.getFrequencyLabelsMap();
        mWeeklyDatesMap = databaseHandler.getWeeklyDateOptions();
        mMonthlyDatesMap = databaseHandler.getMonthlyDateOptions();
        mPaymentDates = new ArrayList<>();
        mPaymentDate = 1;
        mWeeklyPaymentDay = 1;
        mPaymentFrequency = 1;
        mIsFirstLoad = true;
        if (mPage.getData().getString(LeaseWizardPage3.LEASE_NEED_BRANCH) == null) {
            mPage.getData().putString(LeaseWizardPage3.LEASE_NEED_BRANCH, "Yes");
        }
        mIsEdit = false;
        Bundle extras = mPage.getData();
        if (extras != null) {
            Lease leaseToEdit = extras.getParcelable("leaseToEdit");
            if (leaseToEdit != null) {
                loadDataForEdit(leaseToEdit);
                mIsEdit = true;
            } else {
                preloadData(extras);
            }
        } else {
            mRentCost = new BigDecimal(0);
            String formatted = DateAndCurrencyDisplayer.getCurrencyToDisplay(mMoneyFormatCode, mRentCost);
            mPage.getData().putString(LeaseWizardPage3.LEASE_RENT_COST_FORMATTED_STRING_DATA_KEY, formatted);
            mPage.getData().putString(LeaseWizardPage3.LEASE_RENT_COST_DATA_KEY, mRentCost.toPlainString());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_lease_wizard_page_3, container, false);
        (rootView.findViewById(android.R.id.title)).setVisibility(View.GONE);
        mRentCostET = rootView.findViewById(R.id.leaseWizardRentCostET);
        if (mPage.getData().getString(LeaseWizardPage3.LEASE_RENT_COST_FORMATTED_STRING_DATA_KEY) != null) {
            mRentCostET.setText(mPage.getData().getString(LeaseWizardPage3.LEASE_RENT_COST_FORMATTED_STRING_DATA_KEY));
        }
        mRentCostET.setSelection(mRentCostET.getText().length());

        mPaymentsAmountTV = rootView.findViewById(R.id.leaseWizardPaymentAmountsTV);
        mProratedPaymentsAmountTV = rootView.findViewById(R.id.leaseWizardProratedPaymentAmountTV);

        mPaymentDateSpinner = rootView.findViewById(R.id.leaseWizardRentDueDateSpinner);
        mPaymentDateRL = rootView.findViewById(R.id.leaseWizardRentDueDateRL);

        mPaymentDaySpinner = rootView.findViewById(R.id.leaseWizardWeeklyDueDaySpinner);
        mPaymentDayRL = rootView.findViewById(R.id.leaseWizardWeeklyDueDayRL);

        mPaymentFrequencySpinner = rootView.findViewById(R.id.leaseWizardRentFrequencySpinner);

        TextView cycleStartLabelTV = rootView.findViewById(R.id.leaseWizardStartOfCycleTV);
        mCycleStartSpinner = rootView.findViewById(R.id.leaseWizardStartOfCycleSpinner);
        mCycleStartRL = rootView.findViewById(R.id.leaseWizardStartOfCycleRL);

        TextView newChangeWarningTV = rootView.findViewById(R.id.leaseWizardNewChangeWarningTV);
        TextView editChangeWarningTV = rootView.findViewById(R.id.leaseWizardEditChangeWarningTV);
        LinearLayout paymentInfoLL = rootView.findViewById(R.id.leaseWizardPaymentInfoRowLL);
        LinearLayout proratedPaymentInfoLL = rootView.findViewById(R.id.leaseWizardProratedPaymentInfoRowLL);
        if (mIsEdit) {
            newChangeWarningTV.setVisibility(View.GONE);
            editChangeWarningTV.setVisibility(View.VISIBLE);
            paymentInfoLL.setVisibility(View.GONE);
            proratedPaymentInfoLL.setVisibility(View.GONE);
            cycleStartLabelTV.setVisibility(View.GONE);
            mCycleStartSpinner.setVisibility(View.GONE);
            mCycleStartRL.setVisibility(View.GONE);
        } else {
            newChangeWarningTV.setVisibility(View.VISIBLE);
            editChangeWarningTV.setVisibility(View.GONE);
            paymentInfoLL.setVisibility(View.VISIBLE);
            proratedPaymentInfoLL.setVisibility(View.VISIBLE);
            cycleStartLabelTV.setVisibility(View.VISIBLE);
            mCycleStartSpinner.setVisibility(View.VISIBLE);
            mCycleStartRL.setVisibility(View.VISIBLE);
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
        List<String> spinnerArray = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : mFrequencyMap.entrySet()) {
            spinnerArray.add(entry.getKey());
        }
        mFrequencyAdapter = new ArrayAdapter<>(
                getContext(), android.R.layout.simple_spinner_item, spinnerArray);
        mFrequencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mPaymentFrequencySpinner.setAdapter(mFrequencyAdapter);
    }

    private void populateMonthlyDatesSpinner() {
        List<String> spinnerArray = new ArrayList<>();
        for (int i = 0; i < mMonthlyDatesMap.length; i++) {
            spinnerArray.add(mMonthlyDatesMap[i].getLabel());
        }
        mDateAdapter = new ArrayAdapter<>(
                getContext(), android.R.layout.simple_spinner_item, spinnerArray);
        mDateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mPaymentDateSpinner.setAdapter(mDateAdapter);
    }

    private void populateWeeklyDatesSpinner() {
        List<String> spinnerArray = new ArrayList<>();
        for (int i = 0; i < mWeeklyDatesMap.length; i++) {
            spinnerArray.add(mWeeklyDatesMap[i].getLabel());
        }
        mDayAdapter = new ArrayAdapter<>(
                getContext(), android.R.layout.simple_spinner_item, spinnerArray);
        mDayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mPaymentDaySpinner.setAdapter(mDayAdapter);
    }

    private void populateCycleOptionsSpinner(boolean isWeekly) {
        ArrayList<String> spinnerArray = new ArrayList<>();
        ArrayList<LocalDate> dateOptions;
        if (isWeekly) {
            dateOptions = getCycleDateOptionsWeekly(mPaymentFrequency, mWeeklyPaymentDay, mLeaseStartDate, mLeaseEndDate);
        } else {
            dateOptions = getCycleDateOptionsMonthly(mPaymentFrequency, mPaymentDate, mLeaseStartDate, mLeaseEndDate);
        }
        for (int i = 0; i < dateOptions.size(); i++) {
            spinnerArray.add(DateAndCurrencyDisplayer.getDateToDisplay(mDateFormatCode, dateOptions.get(i)));
        }
        mCycleAdapter = new ArrayAdapter<>(
                getContext(), android.R.layout.simple_spinner_item, spinnerArray);
        mCycleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mCycleStartSpinner.setAdapter(mCycleAdapter);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (mPage.getData().getInt(LeaseWizardPage3.LEASE_DUE_DATE_ID_DATA_KEY) != 0) {
            if(mPage.getData().getInt(LeaseWizardPage3.LEASE_DUE_DATE_ID_DATA_KEY) > 31){
                mWeeklyPaymentDay = mPage.getData().getInt(LeaseWizardPage3.LEASE_DUE_DATE_ID_DATA_KEY) - 31;
            } else {
                mPaymentDate = mPage.getData().getInt(LeaseWizardPage3.LEASE_DUE_DATE_ID_DATA_KEY);
            }
        }
        if (mPage.getData().getInt(LeaseWizardPage3.LEASE_PAYMENT_FREQUENCY_DATA_KEY) == 0) {
            mPage.getData().putInt(LeaseWizardPage3.LEASE_PAYMENT_FREQUENCY_DATA_KEY, 1);
        }
        if (mPage.getData().getString(LeaseWizardPage3.LEASE_PAYMENT_CYCLE_DATA_KEY) != null) {
            mSelectedCycleStartDate = DateAndCurrencyDisplayer.getDateFromDisplay(mDateFormatCode, mPage.getData().getString(LeaseWizardPage3.LEASE_PAYMENT_CYCLE_DATA_KEY));
        }
        mPaymentFrequency = mPage.getData().getInt(LeaseWizardPage3.LEASE_PAYMENT_FREQUENCY_DATA_KEY);
        populateFrequencySpinner();
        populateMonthlyDatesSpinner();
        populateWeeklyDatesSpinner();
        mRentCostET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (mRentCostET == null) return;
                String s = editable.toString();
                if (s.isEmpty()) return;
                mRentCostET.removeTextChangedListener(this);
                String cleanString = DateAndCurrencyDisplayer.cleanMoneyString(s);

                mRentCost = new BigDecimal(cleanString).setScale(2, BigDecimal.ROUND_FLOOR).divide(new BigDecimal(100), BigDecimal.ROUND_FLOOR);

                String formatted = DateAndCurrencyDisplayer.getCurrencyToDisplay(mMoneyFormatCode, mRentCost);
                mRentCostET.setText(formatted);
                mRentCostET.setSelection(DateAndCurrencyDisplayer.getEndCursorPositionForMoneyInput(mRentCostET.getText().length(), mMoneyFormatCode));
                mPage.getData().putString(LeaseWizardPage3.LEASE_RENT_COST_FORMATTED_STRING_DATA_KEY, formatted);
                mPage.getData().putString(LeaseWizardPage3.LEASE_RENT_COST_DATA_KEY, mRentCost.toPlainString());
                if (getUserVisibleHint()) {
                    mPage.notifyDataChanged();
                }
                mRentCostET.addTextChangedListener(this);
            }
        });
        mRentCostET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRentCostET.setSelection(DateAndCurrencyDisplayer.getEndCursorPositionForMoneyInput(mRentCostET.getText().length(), mMoneyFormatCode));
            }
        });

        String startDate = mCallbacks.onGetPage("Page1").getData().getString(LeaseWizardPage1.LEASE_START_DATE_STRING_DATA_KEY);
        String endDate = mCallbacks.onGetPage("Page1").getData().getString(LeaseWizardPage1.LEASE_END_DATE_STRING_DATA_KEY);

        DateFormat formatFrom = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
        try {
            mLeaseStartDate = formatFrom.parse(startDate);
            mLeaseEndDate = formatFrom.parse(endDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        mPaymentDateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, final int position, long l) {
                mPaymentDate = position + 1;
                if (!mIsFirstLoad) {
                    if (getUserVisibleHint()) {
                        new Handler().post(new Runnable() {
                            @Override
                            public void run() {
                                //       if (getUserVisibleHint()) {
                                if (!mIsEdit) {
                                    figurePayments(mLeaseStartDate, mLeaseEndDate, mPaymentDate, mPaymentFrequency, mSelectedCycleStartDate);
                                }
                                mPage.getData().putString(LeaseWizardPage3.LEASE_DUE_DATE_STRING_DATA_KEY, mPaymentDateSpinner.getSelectedItem().toString());

                                mPage.getData().putInt(LeaseWizardPage3.LEASE_DUE_DATE_ID_DATA_KEY, mMonthlyDatesMap[position].getDatabaseID());

                                mPage.notifyDataChanged();
                                String paymentsAmount = mProrated + mRegular + "";
                                String proratedPaymentsAmount = mProrated + "";
                                mPaymentsAmountTV.setText(paymentsAmount);
                                mProratedPaymentsAmountTV.setText(proratedPaymentsAmount);
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

        mPaymentDaySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, final int position, long l) {
                mWeeklyPaymentDay = position + 1;
                if (!mIsFirstLoad) {
                    if (getUserVisibleHint()) {
                        new Handler().post(new Runnable() {
                            @Override
                            public void run() {
                                //       if (getUserVisibleHint()) {
                                if (!mIsEdit) {
                                    figureWeeklyPayments(mLeaseStartDate, mLeaseEndDate, mWeeklyPaymentDay, mPaymentFrequency, mSelectedCycleStartDate);
                                }
                                mPage.getData().putString(LeaseWizardPage3.LEASE_DUE_DATE_STRING_DATA_KEY, mPaymentDaySpinner.getSelectedItem().toString());
                                int id = 1;
                                if (mWeeklyDatesMap[position] != null) {
                                    id = mWeeklyDatesMap[position].getDatabaseID();
                                }
                                mPage.getData().putInt(LeaseWizardPage3.LEASE_DUE_DATE_ID_DATA_KEY, id);
                                mPage.notifyDataChanged();
                                String paymentsAmount = mProrated + mRegular + "";
                                String proratedPaymentsAmount = mProrated + "";
                                mPaymentsAmountTV.setText(paymentsAmount);
                                mProratedPaymentsAmountTV.setText(proratedPaymentsAmount);
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

        mPaymentFrequencySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                final String frequency = mPaymentFrequencySpinner.getSelectedItem().toString();
                int frequencyID = 1;
                if (mFrequencyMap.get(frequency) != null) {
                    frequencyID = mFrequencyMap.get(frequency);
                }
                mPage.getData().putInt(LeaseWizardPage3.LEASE_PAYMENT_FREQUENCY_ID_DATA_KEY, frequencyID);
                mPaymentFrequency = getFrequencyNumber(frequencyID);
                if (frequencyID > 7) {
                    mPage.getData().putBoolean(LeaseWizardPage3.LEASE_ARE_PAYMENTS_WEEKLY_DATA_KEY, true);
                } else {
                    mPage.getData().putBoolean(LeaseWizardPage3.LEASE_ARE_PAYMENTS_WEEKLY_DATA_KEY, false);
                }
                if (!mIsFirstLoad) {
                    if (getUserVisibleHint()) {
                        new Handler().post(new Runnable() {
                            @Override
                            public void run() {
                                if (mPage.getData().getBoolean(LeaseWizardPage3.LEASE_ARE_PAYMENTS_WEEKLY_DATA_KEY)) {
                                    mPaymentDayRL.setVisibility(View.VISIBLE);
                                    mPaymentDateRL.setVisibility(View.GONE);
                                    if (!mIsEdit) {
                                        figureWeeklyPayments(mLeaseStartDate, mLeaseEndDate, mWeeklyPaymentDay, mPaymentFrequency, mSelectedCycleStartDate);
                                        populateCycleOptionsSpinner(true);
                                    }
                                } else {
                                    mPaymentDayRL.setVisibility(View.GONE);
                                    mPaymentDateRL.setVisibility(View.VISIBLE);
                                    if (!mIsEdit) {
                                        figurePayments(mLeaseStartDate, mLeaseEndDate, mPaymentDate, mPaymentFrequency, mSelectedCycleStartDate);
                                        populateCycleOptionsSpinner(false);
                                    }
                                }
                                mPage.getData().putInt(LeaseWizardPage3.LEASE_PAYMENT_FREQUENCY_DATA_KEY, mPaymentFrequency);
                                mPage.getData().putString(LeaseWizardPage3.LEASE_PAYMENT_FREQUENCY_STRING_DATA_KEY, mPaymentFrequencySpinner.getSelectedItem().toString());
                                String paymentsAmount = mProrated + mRegular + "";
                                String proratedPaymentsAmount = mProrated + "";
                                mPaymentsAmountTV.setText(paymentsAmount);
                                mProratedPaymentsAmountTV.setText(proratedPaymentsAmount);
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
        if (!mIsEdit) {
            mCycleStartSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, final int position, long l) {

                    if (!mIsFirstLoad) {
                        if (getUserVisibleHint()) {
                            new Handler().post(new Runnable() {
                                @Override
                                public void run() {
                                    String dateString = mCycleStartSpinner.getSelectedItem().toString();
                                    mSelectedCycleStartDate = DateAndCurrencyDisplayer.getDateFromDisplay(mDateFormatCode, dateString);
                                    mPage.getData().putString(LeaseWizardPage3.LEASE_PAYMENT_CYCLE_DATA_KEY, dateString);

                                    if(mPage.getData().getBoolean(LeaseWizardPage3.LEASE_ARE_PAYMENTS_WEEKLY_DATA_KEY)){
                                        figureWeeklyPayments(mLeaseStartDate, mLeaseEndDate, mWeeklyPaymentDay, mPaymentFrequency, mSelectedCycleStartDate);
                                    } else {
                                        figurePayments(mLeaseStartDate, mLeaseEndDate, mPaymentDate, mPaymentFrequency, mSelectedCycleStartDate);
                                    }
                                    mPage.notifyDataChanged();
                                    String paymentsAmount = mProrated + mRegular + "";
                                    String proratedPaymentsAmount = mProrated + "";
                                    mPaymentsAmountTV.setText(paymentsAmount);
                                    mProratedPaymentsAmountTV.setText(proratedPaymentsAmount);
                                    mIsFirstLoad = false;
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
        if (mPage.getData().getString(LeaseWizardPage3.LEASE_RENT_COST_DATA_KEY) == null) {
            String formatted = NumberFormat.getCurrencyInstance().format(mRentCost);
            mPage.getData().putString(LeaseWizardPage3.LEASE_RENT_COST_FORMATTED_STRING_DATA_KEY, formatted);
            mPage.getData().putString(LeaseWizardPage3.LEASE_RENT_COST_DATA_KEY, mRentCost.toPlainString());
        }

        if (mPage.getData().getString(LeaseWizardPage3.LEASE_PAYMENT_FREQUENCY_STRING_DATA_KEY) != null) {
            int spinnerPosition = mFrequencyAdapter.getPosition(mPage.getData().getString(LeaseWizardPage3.LEASE_PAYMENT_FREQUENCY_STRING_DATA_KEY));
            mPaymentFrequencySpinner.setSelection(spinnerPosition);
        }
        if (mPage.getData().getBoolean(LeaseWizardPage3.LEASE_ARE_PAYMENTS_WEEKLY_DATA_KEY)) {
            if (mPage.getData().getString(LeaseWizardPage3.LEASE_DUE_DATE_STRING_DATA_KEY) != null) {
                int spinnerPosition = mDayAdapter.getPosition(mPage.getData().getString(LeaseWizardPage3.LEASE_DUE_DATE_STRING_DATA_KEY));
                mPaymentDaySpinner.setSelection(spinnerPosition);
            }
        } else {
            if (mPage.getData().getString(LeaseWizardPage3.LEASE_DUE_DATE_STRING_DATA_KEY) != null) {
                int spinnerPosition = mDateAdapter.getPosition(mPage.getData().getString(LeaseWizardPage3.LEASE_DUE_DATE_STRING_DATA_KEY));
                mPaymentDateSpinner.setSelection(spinnerPosition);
            }
        }

        if (mPage.getData().getString(LeaseWizardPage3.LEASE_PAYMENT_FREQUENCY_STRING_DATA_KEY) == null) {
            mPage.getData().putInt(LeaseWizardPage3.LEASE_PAYMENT_FREQUENCY_ID_DATA_KEY, mPaymentFrequency);
            mPage.getData().putString(LeaseWizardPage3.LEASE_PAYMENT_FREQUENCY_STRING_DATA_KEY, mPaymentFrequencySpinner.getSelectedItem().toString());
        }
        if (mPage.getData().getString(LeaseWizardPage3.LEASE_DUE_DATE_STRING_DATA_KEY) == null) {
            mPage.getData().putString(LeaseWizardPage3.LEASE_DUE_DATE_STRING_DATA_KEY, mPaymentDateSpinner.getSelectedItem().toString());
            mPage.getData().putInt(LeaseWizardPage3.LEASE_DUE_DATE_ID_DATA_KEY, mPaymentDate);
        }
        populateCycleOptionsSpinner(mPage.getData().getBoolean(LeaseWizardPage3.LEASE_ARE_PAYMENTS_WEEKLY_DATA_KEY));
        if(mPage.getData().getString(LeaseWizardPage3.LEASE_PAYMENT_CYCLE_DATA_KEY) == null){
            mSelectedCycleStartDate = DateAndCurrencyDisplayer.getDateFromDisplay(mDateFormatCode, mCycleStartSpinner.getItemAtPosition(0).toString());
            mPage.getData().putString(LeaseWizardPage3.LEASE_PAYMENT_CYCLE_DATA_KEY, DateAndCurrencyDisplayer.getDateToDisplay(mDateFormatCode, mSelectedCycleStartDate));
        }
        if (mPage.getData().getString(LeaseWizardPage3.LEASE_PAYMENT_CYCLE_DATA_KEY) != null) {
            int spinnerPosition = mCycleAdapter.getPosition(mPage.getData().getString(LeaseWizardPage3.LEASE_PAYMENT_CYCLE_DATA_KEY));
            mCycleStartSpinner.setSelection(spinnerPosition);
        }
        String isOtherBranchNeeded = mPage.getData().getString(LeaseWizardPage3.LEASE_NEED_BRANCH);
        if (mPage.getData().getBoolean(LeaseWizardPage3.LEASE_ARE_PAYMENTS_WEEKLY_DATA_KEY)) {
            if (!mIsEdit) {
                figureWeeklyPayments(mLeaseStartDate, mLeaseEndDate, mWeeklyPaymentDay, mPaymentFrequency, mSelectedCycleStartDate);
            }
            mPaymentDayRL.setVisibility(View.VISIBLE);
            mPaymentDateRL.setVisibility(View.GONE);
        } else {
            if (!mIsEdit) {
                figurePayments(mLeaseStartDate, mLeaseEndDate, mPaymentDate, mPaymentFrequency, mSelectedCycleStartDate);
            }
            mPaymentDayRL.setVisibility(View.GONE);
            mPaymentDateRL.setVisibility(View.VISIBLE);
        }
        String paymentsAmount = mProrated + mRegular + "";
        String proratedPaymentsAmount = mProrated + "";
        mPaymentsAmountTV.setText(paymentsAmount);
        mProratedPaymentsAmountTV.setText(proratedPaymentsAmount);
        if(!isOtherBranchNeeded.equals(mPage.getData().getString(LeaseWizardPage3.LEASE_NEED_BRANCH))){
            Handler handler = new Handler();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    mPage.notifyDataChanged();
                }
            });
        }
        mIsFirstLoad = false;
    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);

        // In a future update to the support library, this should override setUserVisibleHint
        // instead of setMenuVisibility.
        if (mRentCostET != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            if (!menuVisible) {
                imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
            }
        }
    }

    private void figureWeeklyPayments(Date leaseStartDate, Date leaseEndDate, int paymentDay, int paymentFrequency, Date cycleStartDate) {
        mProrated = 0;
        mRegular = 0;
        LocalDate startDate = new LocalDate(leaseStartDate);
        LocalDate endDate = new LocalDate(leaseEndDate);
        mPaymentDates.clear();
        LocalDate payment = new LocalDate(cycleStartDate);
        if (startDate.plusWeeks(paymentFrequency).isAfter(endDate)) {
            //Not full cycle, need 2 mProrated
            if (payment.isAfter(startDate) && payment.isBefore(endDate)) {
                mProrated = mProrated + 2;
                mPage.getData().putBoolean(LeaseWizardPage3.LEASE_IS_FIRST_PRORATED_REQUIRED_DATA_KEY, true);
                mPage.getData().putBoolean(LeaseWizardPage3.LEASE_IS_LAST_PRORATED_REQUIRED_DATA_KEY, true);
                mPaymentDates.add(startDate.toString("yyyy-MM-dd"));
                mPaymentDates.add(payment.toString("yyyy-MM-dd"));
            } else if (payment.plusWeeks(paymentFrequency).isBefore(endDate)) {
                payment = payment.plusMonths(paymentFrequency);
                mProrated = mProrated + 2;
                mPage.getData().putBoolean(LeaseWizardPage3.LEASE_IS_FIRST_PRORATED_REQUIRED_DATA_KEY, true);
                mPage.getData().putBoolean(LeaseWizardPage3.LEASE_IS_LAST_PRORATED_REQUIRED_DATA_KEY, true);
                mPaymentDates.add(startDate.toString("yyyy-MM-dd"));
                mPaymentDates.add(payment.toString("yyyy-MM-dd"));
            } else {
                mProrated++;
                mPage.getData().putBoolean(LeaseWizardPage3.LEASE_IS_FIRST_PRORATED_REQUIRED_DATA_KEY, true);
                mPage.getData().putBoolean(LeaseWizardPage3.LEASE_IS_LAST_PRORATED_REQUIRED_DATA_KEY, false);
                mPaymentDates.add(startDate.toString("yyyy-MM-dd"));
                mPaymentDates.add(endDate.toString("yyyy-MM-dd"));
            }
        } else if (startDate.isEqual(payment) && endDate.isEqual(payment.plusWeeks(paymentFrequency))) {
            //Full cycle, but only 1. 1 mRegular payment
            mRegular++;
            mPage.getData().putBoolean(LeaseWizardPage3.LEASE_IS_FIRST_PRORATED_REQUIRED_DATA_KEY, false);
            mPage.getData().putBoolean(LeaseWizardPage3.LEASE_IS_LAST_PRORATED_REQUIRED_DATA_KEY, false);
            mPaymentDates.add(startDate.toString("yyyy-MM-dd"));
            mPaymentDates.add(payment.toString("yyyy-MM-dd"));

        } else {
            mPaymentDates.add(startDate.toString("yyyy-MM-dd"));
            if (startDate.isBefore(payment)) {
                mProrated++;
                mPage.getData().putBoolean(LeaseWizardPage3.LEASE_IS_FIRST_PRORATED_REQUIRED_DATA_KEY, true);
            } else if (startDate.isEqual(payment)) {
                mRegular++;
                payment = payment.plusWeeks(paymentFrequency);
                mPage.getData().putBoolean(LeaseWizardPage3.LEASE_IS_FIRST_PRORATED_REQUIRED_DATA_KEY, false);
            } else {
                payment = payment.plusWeeks(paymentFrequency);
                mProrated++;
                mPage.getData().putBoolean(LeaseWizardPage3.LEASE_IS_FIRST_PRORATED_REQUIRED_DATA_KEY, true);
            }
            while (payment.isBefore(endDate.minusWeeks(paymentFrequency))) {
                mRegular++;
                LocalDate pdate = payment;
                mPaymentDates.add(pdate.toString("yyyy-MM-dd"));
                payment = payment.plusWeeks(paymentFrequency);
            }
            if (payment.plusWeeks(paymentFrequency).equals(endDate)) {
                mRegular++;
                mPaymentDates.add(payment.toString("yyyy-MM-dd"));
                mPage.getData().putBoolean(LeaseWizardPage3.LEASE_IS_LAST_PRORATED_REQUIRED_DATA_KEY, false);
            } else {
                mProrated++;
                mPaymentDates.add(payment.toString("yyyy-MM-dd"));
                mPage.getData().putBoolean(LeaseWizardPage3.LEASE_IS_LAST_PRORATED_REQUIRED_DATA_KEY, true);
            }
        }
        mPage.getData().putStringArrayList(LeaseWizardPage3.LEASE_PAYMENT_DATES_ARRAY_DATA_KEY, mPaymentDates);
        if (mProrated > 0) {
            mPage.getData().putString(LeaseWizardPage3.LEASE_NEED_BRANCH, "Yes");
        } else {
            mPage.getData().putString(LeaseWizardPage3.LEASE_NEED_BRANCH, "No");
        }
    }

    private void figurePayments(Date leaseStartDate, Date leaseEndDate, int paymentDay, int paymentFrequency, Date cycleStartDate) {
        mProrated = 0;
        mRegular = 0;
        LocalDate startDate = new LocalDate(leaseStartDate);
        LocalDate endDate = new LocalDate(leaseEndDate);
        mPaymentDates.clear();
        LocalDate payment = new LocalDate(cycleStartDate);
        if (startDate.plusMonths(paymentFrequency).isAfter(endDate)) {
            //Not full cycle, need 2 mProrated
            if (payment.isAfter(startDate) && payment.isBefore(endDate)) {
                mProrated = mProrated + 2;
                mPage.getData().putBoolean(LeaseWizardPage3.LEASE_IS_FIRST_PRORATED_REQUIRED_DATA_KEY, true);
                mPage.getData().putBoolean(LeaseWizardPage3.LEASE_IS_LAST_PRORATED_REQUIRED_DATA_KEY, true);
                mPaymentDates.add(startDate.toString("yyyy-MM-dd"));
                mPaymentDates.add(payment.toString("yyyy-MM-dd"));
            } else if (payment.plusMonths(paymentFrequency).isBefore(endDate)) {
                payment = payment.plusMonths(paymentFrequency);
                mProrated = mProrated + 2;
                mPage.getData().putBoolean(LeaseWizardPage3.LEASE_IS_FIRST_PRORATED_REQUIRED_DATA_KEY, true);
                mPage.getData().putBoolean(LeaseWizardPage3.LEASE_IS_LAST_PRORATED_REQUIRED_DATA_KEY, true);
                mPaymentDates.add(startDate.toString("yyyy-MM-dd"));
                mPaymentDates.add(payment.toString("yyyy-MM-dd"));
            } else {
                mProrated++;
                mPage.getData().putBoolean(LeaseWizardPage3.LEASE_IS_FIRST_PRORATED_REQUIRED_DATA_KEY, true);
                mPage.getData().putBoolean(LeaseWizardPage3.LEASE_IS_LAST_PRORATED_REQUIRED_DATA_KEY, false);
                mPaymentDates.add(startDate.toString("yyyy-MM-dd"));
                mPaymentDates.add(endDate.toString("yyyy-MM-dd"));
            }
        } else if (startDate.isEqual(payment) && endDate.isEqual(payment.plusMonths(paymentFrequency))) {
            //Full cycle, but only 1. 1 mRegular payment
            mRegular++;
            mPage.getData().putBoolean(LeaseWizardPage3.LEASE_IS_FIRST_PRORATED_REQUIRED_DATA_KEY, false);
            mPage.getData().putBoolean(LeaseWizardPage3.LEASE_IS_LAST_PRORATED_REQUIRED_DATA_KEY, false);
            mPaymentDates.add(startDate.toString("yyyy-MM-dd"));
        } else {
            mPaymentDates.add(startDate.toString("yyyy-MM-dd"));
            if (startDate.isBefore(payment)) {
                mProrated++;
                mPage.getData().putBoolean(LeaseWizardPage3.LEASE_IS_FIRST_PRORATED_REQUIRED_DATA_KEY, true);
            } else if (startDate.isEqual(payment)) {
                mRegular++;
                payment = payment.plusMonths(paymentFrequency);
                payment = keepPaymentDayConsistentForEndOfMonth(paymentDay, payment);
                mPage.getData().putBoolean(LeaseWizardPage3.LEASE_IS_FIRST_PRORATED_REQUIRED_DATA_KEY, false);
            } else {
                payment = payment.plusMonths(paymentFrequency);
                payment = keepPaymentDayConsistentForEndOfMonth(paymentDay, payment);
                mProrated++;
                mPage.getData().putBoolean(LeaseWizardPage3.LEASE_IS_FIRST_PRORATED_REQUIRED_DATA_KEY, true);
            }
            while (payment.isBefore(endDate.minusMonths(paymentFrequency))) {
                mRegular++;
                LocalDate pdate = payment;
                mPaymentDates.add(pdate.toString("yyyy-MM-dd"));
                payment = payment.plusMonths(paymentFrequency);
                payment = keepPaymentDayConsistentForEndOfMonth(paymentDay, payment);
            }
            if (payment.plusMonths(paymentFrequency).equals(endDate)) { //|| startDate.plusMonths(1).isAfter(payment)) {
                mRegular++;
                mPaymentDates.add(payment.toString("yyyy-MM-dd"));
                mPage.getData().putBoolean(LeaseWizardPage3.LEASE_IS_LAST_PRORATED_REQUIRED_DATA_KEY, false);
            } else {
                mProrated++;
                mPaymentDates.add(payment.toString("yyyy-MM-dd"));
                mPage.getData().putBoolean(LeaseWizardPage3.LEASE_IS_LAST_PRORATED_REQUIRED_DATA_KEY, true);
            }
        }
        mPage.getData().putStringArrayList(LeaseWizardPage3.LEASE_PAYMENT_DATES_ARRAY_DATA_KEY, mPaymentDates);
        if (mProrated > 0) {
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
            mRentCost = new BigDecimal(mPage.getData().getString(LeaseWizardPage3.LEASE_RENT_COST_DATA_KEY)).setScale(2, BigDecimal.ROUND_FLOOR).divide(new BigDecimal(100), BigDecimal.ROUND_FLOOR);
        } else {
            mRentCost = new BigDecimal(0);
            String formatted = DateAndCurrencyDisplayer.getCurrencyToDisplay(mMoneyFormatCode, mRentCost);
            mPage.getData().putString(LeaseWizardPage3.LEASE_RENT_COST_FORMATTED_STRING_DATA_KEY, formatted);
            mPage.getData().putString(LeaseWizardPage3.LEASE_RENT_COST_DATA_KEY, mRentCost.toPlainString());
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
            String formatted = DateAndCurrencyDisplayer.getCurrencyToDisplay(mMoneyFormatCode, leaseToEdit.getMonthlyRentCost());
            mPage.getData().putString(LeaseWizardPage3.LEASE_RENT_COST_FORMATTED_STRING_DATA_KEY, formatted);
            mPage.getData().putString(LeaseWizardPage3.LEASE_RENT_COST_DATA_KEY, leaseToEdit.getMonthlyRentCost().toPlainString());
            mRentCost = leaseToEdit.getMonthlyRentCost();
            //Frequency
            mPage.getData().putInt(LeaseWizardPage3.LEASE_PAYMENT_FREQUENCY_ID_DATA_KEY, leaseToEdit.getPaymentFrequencyID());
            mPage.getData().putString(LeaseWizardPage3.LEASE_PAYMENT_FREQUENCY_STRING_DATA_KEY, getKeyByValue(mFrequencyMap, leaseToEdit.getPaymentFrequencyID()));
            mPage.getData().putInt(LeaseWizardPage3.LEASE_PAYMENT_FREQUENCY_DATA_KEY, getFrequencyNumber(leaseToEdit.getPaymentFrequencyID()));
            mPaymentFrequency = leaseToEdit.getPaymentFrequencyID();
            //Day/Date
            if (leaseToEdit.getPaymentDayID() > 31) {
                mPage.getData().putBoolean(LeaseWizardPage3.LEASE_ARE_PAYMENTS_WEEKLY_DATA_KEY, true);
                String stringDataKey = "";
                for (int i = 0; i < mWeeklyDatesMap.length; i++) {
                    if (mWeeklyDatesMap[i].getDatabaseID() == leaseToEdit.getPaymentDayID()) {
                        stringDataKey = mWeeklyDatesMap[i].getLabel();
                    }
                }
                mPage.getData().putString(LeaseWizardPage3.LEASE_DUE_DATE_STRING_DATA_KEY, stringDataKey);
                mPage.getData().putInt(LeaseWizardPage3.LEASE_DUE_DATE_ID_DATA_KEY, leaseToEdit.getPaymentDayID());

                mWeeklyPaymentDay = leaseToEdit.getPaymentDayID() - 31;
            } else {
                mPage.getData().putBoolean(LeaseWizardPage3.LEASE_ARE_PAYMENTS_WEEKLY_DATA_KEY, false);
                String stringDataKey = "";
                for (int i = 0; i < mMonthlyDatesMap.length; i++) {
                    if (mMonthlyDatesMap[i].getDatabaseID() == leaseToEdit.getPaymentDayID()) {
                        stringDataKey = mMonthlyDatesMap[i].getLabel();
                    }
                }
                mPage.getData().putString(LeaseWizardPage3.LEASE_DUE_DATE_STRING_DATA_KEY, stringDataKey);
                mPage.getData().putInt(LeaseWizardPage3.LEASE_DUE_DATE_ID_DATA_KEY, leaseToEdit.getPaymentDayID());
                mPaymentDate = leaseToEdit.getPaymentDayID();
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