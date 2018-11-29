package com.rba18.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.android.wizardpager.wizard.ui.PageFragmentCallbacks;
import com.rba18.R;
import com.rba18.helpers.DateAndCurrencyDisplayer;
import com.rba18.wizards.LeaseWizardPage1;
import com.rba18.wizards.LeaseWizardPage3;
import com.rba18.wizards.LeaseWizardProratedRentPage;

import org.joda.time.Days;
import org.joda.time.LocalDate;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class LeaseWizardProratedRentPageFragment extends android.support.v4.app.Fragment {
    private static final String ARG_KEY = "key";
    private PageFragmentCallbacks mCallbacks;
    private String mKey;
    private LeaseWizardProratedRentPage mPage;
    private TextView mFirstProratedRentRecommendationTV, mLastProratedRentRecommendationTV,
            mFirstProratedStartDateTV, mLastProratedStartDateTV, mFirstProratedEndDateTV, mLastProratedEndDateTV,
            mFirstProratedDayAmountTV, mLastProratedDayAmountTV;
    private EditText mFirstProratedAmountET, mLastProratedAmountET;
    private Date mLeaseEndDate;
    private BigDecimal mProratedFirst, mProratedLast;
    private int mDateFormatCode, mMoneyFormatCode;

    public static LeaseWizardProratedRentPageFragment create(String key) {
        Bundle args = new Bundle();
        args.putString(ARG_KEY, key);

        LeaseWizardProratedRentPageFragment fragment = new LeaseWizardProratedRentPageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public LeaseWizardProratedRentPageFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        mDateFormatCode = preferences.getInt("dateFormat", DateAndCurrencyDisplayer.DATE_MMDDYYYY);
        mMoneyFormatCode = preferences.getInt("currency", DateAndCurrencyDisplayer.CURRENCY_US);
        Bundle args = getArguments();
        mKey = args.getString(ARG_KEY);
        mPage = (LeaseWizardProratedRentPage) mCallbacks.onGetPage(mKey);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_lease_wizard_prorated_rent_page, container, false);
        (rootView.findViewById(android.R.id.title)).setVisibility(View.GONE);
        mFirstProratedRentRecommendationTV = rootView.findViewById(R.id.leaseWizardProratedFirstPaymentAmountRecTV);
        mLastProratedRentRecommendationTV = rootView.findViewById(R.id.leaseWizardProratedLastPaymentAmountRecTV);
        mFirstProratedStartDateTV = rootView.findViewById(R.id.leaseWizardProratedFirstPaymentRangeStartTV);
        mLastProratedStartDateTV = rootView.findViewById(R.id.leaseWizardProratedLastPaymentRangeStartTV);
        mFirstProratedEndDateTV = rootView.findViewById(R.id.leaseWizardProratedFirstPaymentRangeEndTV);
        mLastProratedEndDateTV = rootView.findViewById(R.id.leaseWizardProratedLastPaymentRangeEndTV);
        mFirstProratedDayAmountTV = rootView.findViewById(R.id.leaseWizardFirstProratedDayAmountTV);
        mLastProratedDayAmountTV = rootView.findViewById(R.id.leaseWizardLastProratedDayAmountTV);
        LinearLayout firstProratedLL = rootView.findViewById(R.id.leaseWizardProratedFirstLL);
        mFirstProratedAmountET = rootView.findViewById(R.id.leaseWizardFirstProratedAmountET);
        if(mCallbacks.onGetPage("Page3").getData().getBoolean(LeaseWizardPage3.LEASE_IS_FIRST_PRORATED_REQUIRED_DATA_KEY)) {
            if (mPage.getData().getString(LeaseWizardProratedRentPage.LEASE_PRORATED_FIRST_PAYMENT_FORMATTED_STRING_DATA_KEY) != null) {
                mFirstProratedAmountET.setText(mPage.getData().getString(LeaseWizardProratedRentPage.LEASE_PRORATED_FIRST_PAYMENT_FORMATTED_STRING_DATA_KEY));
            }
            mFirstProratedAmountET.setSelection(mFirstProratedAmountET.getText().length());
            mPage.getData().putBoolean(LeaseWizardProratedRentPage.LEASE_PRORATED_FIRST_SHOW_IN_REVIEW_DATA_KEY, true);
        } else {
            firstProratedLL.setVisibility(View.GONE);
            mPage.getData().putBoolean(LeaseWizardProratedRentPage.LEASE_PRORATED_FIRST_SHOW_IN_REVIEW_DATA_KEY, false);
        }
        LinearLayout lastProratedLL = rootView.findViewById(R.id.leaseWizardProratedLastLL);
        mLastProratedAmountET = rootView.findViewById(R.id.leaseWizardLastProratedAmountET);
        if(mCallbacks.onGetPage("Page3").getData().getBoolean(LeaseWizardPage3.LEASE_IS_LAST_PRORATED_REQUIRED_DATA_KEY)) {
            if (mPage.getData().getString(LeaseWizardProratedRentPage.LEASE_PRORATED_LAST_PAYMENT_FORMATTED_STRING_DATA_KEY) != null) {
                mLastProratedAmountET.setText(mPage.getData().getString(LeaseWizardProratedRentPage.LEASE_PRORATED_LAST_PAYMENT_FORMATTED_STRING_DATA_KEY));
            }
            mLastProratedAmountET.setSelection(mLastProratedAmountET.getText().length());
            mPage.getData().putBoolean(LeaseWizardProratedRentPage.LEASE_PRORATED_LAST_SHOW_IN_REVIEW_DATA_KEY, true);
        } else {
            lastProratedLL.setVisibility(View.GONE);
            mPage.getData().putBoolean(LeaseWizardProratedRentPage.LEASE_PRORATED_LAST_SHOW_IN_REVIEW_DATA_KEY, false);
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
        ArrayList<String> payments = mCallbacks.onGetPage("Page3").getData().getStringArrayList(LeaseWizardPage3.LEASE_PAYMENT_DATES_ARRAY_DATA_KEY);
        int paymentMonthlyFrequency = mCallbacks.onGetPage("Page3").getData().getInt(LeaseWizardPage3.LEASE_PAYMENT_FREQUENCY_DATA_KEY);

        String rentCostString = mCallbacks.onGetPage("Page3").getData().getString(LeaseWizardPage3.LEASE_RENT_COST_DATA_KEY);
        BigDecimal rentCost = new BigDecimal(rentCostString);
        String endDate = mCallbacks.onGetPage("Page1").getData().getString(LeaseWizardPage1.LEASE_END_DATE_STRING_DATA_KEY);
        DateFormat formatFrom = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
        try {
            mLeaseEndDate = formatFrom.parse(endDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        LocalDate firstPaymentStart = new LocalDate(payments.get(0));
        LocalDate firstPaymentEnd = new LocalDate(payments.get(1));
        LocalDate lastPaymentStart = new LocalDate(payments.get(payments.size() - 1));
        LocalDate lastPaymentEnd = new LocalDate(mLeaseEndDate);
        int daysOfFirstPayment = Days.daysBetween(firstPaymentStart, firstPaymentEnd).getDays();
        int daysOfLastPayment = Days.daysBetween(lastPaymentStart, lastPaymentEnd).getDays();
        int totalDaysInFirstFullPeriod = getDaysOfCycleIfFirstWasntProrated(firstPaymentEnd, paymentMonthlyFrequency, mCallbacks.onGetPage("Page3").getData().getBoolean(LeaseWizardPage3.LEASE_ARE_PAYMENTS_WEEKLY_DATA_KEY));
        int totalDaysInLastFullPeriod = getDaysOfCycleIfLastWasntProrated(lastPaymentStart, paymentMonthlyFrequency, mCallbacks.onGetPage("Page3").getData().getBoolean(LeaseWizardPage3.LEASE_ARE_PAYMENTS_WEEKLY_DATA_KEY));

        BigDecimal recommendedProratedFirst = figureRecommendedProratedPayment(daysOfFirstPayment, totalDaysInFirstFullPeriod, rentCost);
        BigDecimal recommendedProratedLast = figureRecommendedProratedPayment(daysOfLastPayment, totalDaysInLastFullPeriod, rentCost);
        mFirstProratedStartDateTV.setText(DateAndCurrencyDisplayer.getDateToDisplay(mDateFormatCode, firstPaymentStart));
        mFirstProratedEndDateTV.setText(DateAndCurrencyDisplayer.getDateToDisplay(mDateFormatCode, firstPaymentEnd));
        mLastProratedStartDateTV.setText(DateAndCurrencyDisplayer.getDateToDisplay(mDateFormatCode, lastPaymentStart));
        mLastProratedEndDateTV.setText(DateAndCurrencyDisplayer.getDateToDisplay(mDateFormatCode, lastPaymentEnd));
        String formattedFirstRec = DateAndCurrencyDisplayer.getCurrencyToDisplay(mMoneyFormatCode, recommendedProratedFirst);
        String formattedLastRec = DateAndCurrencyDisplayer.getCurrencyToDisplay(mMoneyFormatCode, recommendedProratedLast);
        mFirstProratedRentRecommendationTV.setText(formattedFirstRec);
        mLastProratedRentRecommendationTV.setText(formattedLastRec);
        String daysOfFirstPaymentString = daysOfFirstPayment + "";
        String daysOfLastPaymentString = daysOfLastPayment + "";
        mFirstProratedDayAmountTV.setText(daysOfFirstPaymentString);
        mLastProratedDayAmountTV.setText(daysOfLastPaymentString);

        if (mPage.getData().getString(LeaseWizardProratedRentPage.LEASE_PRORATED_FIRST_PAYMENT_FORMATTED_STRING_DATA_KEY) != null) {
            if(mPage.getData().getBoolean(LeaseWizardProratedRentPage.LEASE_PRORATED_FIRST_PAYMENT_WAS_MODIFIED_DATA_KEY)) {
                mProratedFirst = new BigDecimal(mPage.getData().getString(LeaseWizardProratedRentPage.LEASE_PRORATED_FIRST_PAYMENT_STRING_DATA_KEY));
                mFirstProratedAmountET.setText(mPage.getData().getString(LeaseWizardProratedRentPage.LEASE_PRORATED_FIRST_PAYMENT_FORMATTED_STRING_DATA_KEY));
                mFirstProratedAmountET.setSelection(mFirstProratedAmountET.getText().length());
            } else {
                mProratedFirst = recommendedProratedFirst;
                String formatted = DateAndCurrencyDisplayer.getCurrencyToDisplay(mMoneyFormatCode, mProratedFirst);
                mPage.getData().putString(LeaseWizardProratedRentPage.LEASE_PRORATED_FIRST_PAYMENT_FORMATTED_STRING_DATA_KEY, formatted);
                mPage.getData().putString(LeaseWizardProratedRentPage.LEASE_PRORATED_FIRST_PAYMENT_STRING_DATA_KEY, mProratedFirst.toPlainString());
                mFirstProratedAmountET.setText(formatted);
                mFirstProratedAmountET.setSelection(mFirstProratedAmountET.getText().length());
            }
        } else {
            mProratedFirst = recommendedProratedFirst;
            String formatted = DateAndCurrencyDisplayer.getCurrencyToDisplay(mMoneyFormatCode, mProratedFirst);
            mPage.getData().putString(LeaseWizardProratedRentPage.LEASE_PRORATED_FIRST_PAYMENT_FORMATTED_STRING_DATA_KEY, formatted);
            mPage.getData().putString(LeaseWizardProratedRentPage.LEASE_PRORATED_FIRST_PAYMENT_STRING_DATA_KEY, mProratedFirst.toPlainString());
            mFirstProratedAmountET.setText(formatted);
            mFirstProratedAmountET.setSelection(mFirstProratedAmountET.getText().length());
        }
        if (mPage.getData().getString(LeaseWizardProratedRentPage.LEASE_PRORATED_LAST_PAYMENT_FORMATTED_STRING_DATA_KEY) != null) {
            if(mPage.getData().getBoolean(LeaseWizardProratedRentPage.LEASE_PRORATED_LAST_PAYMENT_WAS_MODIFIED_DATA_KEY)) {
                mProratedLast = new BigDecimal(mPage.getData().getString(LeaseWizardProratedRentPage.LEASE_PRORATED_LAST_PAYMENT_STRING_DATA_KEY));
                mLastProratedAmountET.setText(mPage.getData().getString(LeaseWizardProratedRentPage.LEASE_PRORATED_LAST_PAYMENT_FORMATTED_STRING_DATA_KEY));
                mLastProratedAmountET.setSelection(mLastProratedAmountET.getText().length());
            } else {
                mProratedLast = recommendedProratedLast;
                String formatted = DateAndCurrencyDisplayer.getCurrencyToDisplay(mMoneyFormatCode, mProratedLast);
                mPage.getData().putString(LeaseWizardProratedRentPage.LEASE_PRORATED_LAST_PAYMENT_FORMATTED_STRING_DATA_KEY, formatted);
                mPage.getData().putString(LeaseWizardProratedRentPage.LEASE_PRORATED_LAST_PAYMENT_STRING_DATA_KEY, mProratedLast.toPlainString());
                mLastProratedAmountET.setText(formatted);
                mLastProratedAmountET.setSelection(mLastProratedAmountET.getText().length());
            }
        } else {
            mProratedLast = recommendedProratedLast;
            String formatted = DateAndCurrencyDisplayer.getCurrencyToDisplay(mMoneyFormatCode, mProratedLast);
            mPage.getData().putString(LeaseWizardProratedRentPage.LEASE_PRORATED_LAST_PAYMENT_FORMATTED_STRING_DATA_KEY, formatted);
            mPage.getData().putString(LeaseWizardProratedRentPage.LEASE_PRORATED_LAST_PAYMENT_STRING_DATA_KEY, mProratedLast.toPlainString());
            mLastProratedAmountET.setText(formatted);
            mLastProratedAmountET.setSelection(mLastProratedAmountET.getText().length());
        }

        mFirstProratedAmountET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (mFirstProratedAmountET == null) return;
                String s = editable.toString();
                if (s.isEmpty()) return;
                mFirstProratedAmountET.removeTextChangedListener(this);
                String cleanString = DateAndCurrencyDisplayer.cleanMoneyString(s);
                mProratedFirst = new BigDecimal(cleanString).setScale(2, BigDecimal.ROUND_FLOOR).divide(new BigDecimal(100), BigDecimal.ROUND_FLOOR);
                String formatted = DateAndCurrencyDisplayer.getCurrencyToDisplay(mMoneyFormatCode, mProratedFirst);
                mFirstProratedAmountET.setText(formatted);
                mFirstProratedAmountET.setSelection(DateAndCurrencyDisplayer.getEndCursorPositionForMoneyInput(mFirstProratedAmountET.getText().length(), mMoneyFormatCode));
                mPage.getData().putString(LeaseWizardProratedRentPage.LEASE_PRORATED_FIRST_PAYMENT_FORMATTED_STRING_DATA_KEY, formatted);
                mPage.getData().putString(LeaseWizardProratedRentPage.LEASE_PRORATED_FIRST_PAYMENT_STRING_DATA_KEY, mProratedFirst.toPlainString());
                mPage.getData().putBoolean(LeaseWizardProratedRentPage.LEASE_PRORATED_FIRST_PAYMENT_WAS_MODIFIED_DATA_KEY, true);
                mPage.notifyDataChanged();
                //mFirstProratedAmountET.setSelection(formatted.length());
                mFirstProratedAmountET.addTextChangedListener(this);
            }
        });
        mFirstProratedAmountET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFirstProratedAmountET.setSelection(DateAndCurrencyDisplayer.getEndCursorPositionForMoneyInput(mFirstProratedAmountET.getText().length(), mMoneyFormatCode));
            }
        });

        mLastProratedAmountET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (mLastProratedAmountET == null) return;
                String s = editable.toString();
                if (s.isEmpty()) return;
                mLastProratedAmountET.removeTextChangedListener(this);
                String cleanString = DateAndCurrencyDisplayer.cleanMoneyString(s);
                mProratedLast = new BigDecimal(cleanString).setScale(2, BigDecimal.ROUND_FLOOR).divide(new BigDecimal(100), BigDecimal.ROUND_FLOOR);
                String formatted = DateAndCurrencyDisplayer.getCurrencyToDisplay(mMoneyFormatCode, mProratedLast);
                mLastProratedAmountET.setText(formatted);
                mLastProratedAmountET.setSelection(DateAndCurrencyDisplayer.getEndCursorPositionForMoneyInput(mLastProratedAmountET.getText().length(), mMoneyFormatCode));
                mPage.getData().putString(LeaseWizardProratedRentPage.LEASE_PRORATED_LAST_PAYMENT_FORMATTED_STRING_DATA_KEY, formatted);
                mPage.getData().putString(LeaseWizardProratedRentPage.LEASE_PRORATED_LAST_PAYMENT_STRING_DATA_KEY, mProratedLast.toPlainString());
                mPage.getData().putBoolean(LeaseWizardProratedRentPage.LEASE_PRORATED_LAST_PAYMENT_WAS_MODIFIED_DATA_KEY, true);
                mPage.notifyDataChanged();
                //mLastProratedAmountET.setSelection(formatted.length());
                mLastProratedAmountET.addTextChangedListener(this);
            }
        });
        mLastProratedAmountET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLastProratedAmountET.setSelection(DateAndCurrencyDisplayer.getEndCursorPositionForMoneyInput(mLastProratedAmountET.getText().length(), mMoneyFormatCode));
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);

        // In a future update to the support library, this should override setUserVisibleHint
        // instead of setMenuVisibility.
        if (mFirstProratedAmountET != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            if (!menuVisible) {
                imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
            }
        }
    }

    private BigDecimal figureRecommendedProratedPayment(int amountOfDaysRented, int totalAmountOfDaysInPeriod, BigDecimal rentCost) {
        BigDecimal totalAmountOfDaysInPeriodBD = new BigDecimal(totalAmountOfDaysInPeriod).setScale(5, BigDecimal.ROUND_HALF_UP);
        BigDecimal amountOfDaysRentedBD = new BigDecimal(amountOfDaysRented).setScale(5, BigDecimal.ROUND_HALF_UP);
        BigDecimal dailyAmount = new BigDecimal(0.00);
        if(totalAmountOfDaysInPeriodBD.compareTo(BigDecimal.ZERO) != 0 && amountOfDaysRentedBD.compareTo(BigDecimal.ZERO) != 0) {
            dailyAmount = amountOfDaysRentedBD.divide(totalAmountOfDaysInPeriodBD, BigDecimal.ROUND_FLOOR).setScale(5, BigDecimal.ROUND_HALF_UP);
        }
        return dailyAmount.multiply(rentCost).setScale(2, BigDecimal.ROUND_HALF_EVEN);
    }

    private int getDaysOfCycleIfFirstWasntProrated(LocalDate endOfFirstPayment, int frequency, boolean isWeekly) {
        LocalDate dateStartOfFullCycle = new LocalDate(endOfFirstPayment);
        if(isWeekly){
            dateStartOfFullCycle = dateStartOfFullCycle.minusWeeks(frequency);
        } else {
            dateStartOfFullCycle = dateStartOfFullCycle.minusMonths(frequency);
        }
        return Days.daysBetween(dateStartOfFullCycle, endOfFirstPayment).getDays();
    }

    private int getDaysOfCycleIfLastWasntProrated(LocalDate lastPaymentDate, int frequency, boolean isWeekly) {
        LocalDate dateEndOfFullCycle = new LocalDate(lastPaymentDate);
        if(isWeekly){
            dateEndOfFullCycle = dateEndOfFullCycle.plusWeeks(frequency);
        } else {
            dateEndOfFullCycle = dateEndOfFullCycle.plusMonths(frequency);
        }
        return Days.daysBetween(lastPaymentDate, dateEndOfFullCycle).getDays();
    }
}