package com.rentbud.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
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
import com.example.cody.rentbud.R;
import com.rentbud.wizards.LeaseWizardPage1;
import com.rentbud.wizards.LeaseWizardPage3;
import com.rentbud.wizards.LeaseWizardProratedRentPage;

import org.joda.time.Days;
import org.joda.time.LocalDate;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.NumberFormat;
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
    private TextView firstProratedLabelTV, lastProratedTV, firstProratedRentRecommendationTV, lastProratedRentRecommendationTV,
            firstProratedStartDateTV, lastProratedStartDateTV, firstProratedEndDateTV, lastProratedEndDateTV,
            firstProratedDayAmountTV, lastProratedDayAmountTV;
    private EditText firstProratedAmountET, lastProratedAmountET;
    private LinearLayout recommendedFirstPriceLL, recommendedLastPriceLL, firstDateInfoLL, lastDateInfoLL;

    private ArrayList<String> payments;
    private Date leaseEndDate;
    private int paymentDay, paymentMonthlyFrequency;
    private BigDecimal rentCost, proratedFirst, proratedLast;
    private Boolean hasFirstBeenModified, hasLastBeenModified;

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

        Bundle args = getArguments();
        mKey = args.getString(ARG_KEY);
        mPage = (LeaseWizardProratedRentPage) mCallbacks.onGetPage(mKey);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_lease_wizard_prorated_rent_page, container, false);
        (rootView.findViewById(android.R.id.title)).setVisibility(View.GONE);
        firstProratedLabelTV = rootView.findViewById(R.id.leaseWizardFirstProratedLabelTV);
        lastProratedTV = rootView.findViewById(R.id.leaseWizardLastProratedLabelTV);
        firstProratedRentRecommendationTV = rootView.findViewById(R.id.leaseWizardProratedFirstPaymentAmountRecTV);
        lastProratedRentRecommendationTV = rootView.findViewById(R.id.leaseWizardProratedLastPaymentAmountRecTV);
        firstProratedStartDateTV = rootView.findViewById(R.id.leaseWizardProratedFirstPaymentRangeStartTV);
        lastProratedStartDateTV = rootView.findViewById(R.id.leaseWizardProratedLastPaymentRangeStartTV);
        firstProratedEndDateTV = rootView.findViewById(R.id.leaseWizardProratedFirstPaymentRangeEndTV);
        lastProratedEndDateTV = rootView.findViewById(R.id.leaseWizardProratedLastPaymentRangeEndTV);
        firstProratedDayAmountTV = rootView.findViewById(R.id.leaseWizardFirstProratedDayAmountTV);
        lastProratedDayAmountTV = rootView.findViewById(R.id.leaseWizardLastProratedDayAmountTV);

        firstProratedAmountET = rootView.findViewById(R.id.leaseWizardFirstProratedAmountET);
        if (mPage.getData().getString(LeaseWizardProratedRentPage.LEASE_PRORATED_FIRST_PAYMENT_FORMATTED_STRING_DATA_KEY) != null) {
            firstProratedAmountET.setText(mPage.getData().getString(LeaseWizardProratedRentPage.LEASE_PRORATED_FIRST_PAYMENT_FORMATTED_STRING_DATA_KEY));
        }
        firstProratedAmountET.setSelection(firstProratedAmountET.getText().length());

        lastProratedAmountET = rootView.findViewById(R.id.leaseWizardLastProratedAmountET);
        if (mPage.getData().getString(LeaseWizardProratedRentPage.LEASE_PRORATED_LAST_PAYMENT_FORMATTED_STRING_DATA_KEY) != null) {
            lastProratedAmountET.setText(mPage.getData().getString(LeaseWizardProratedRentPage.LEASE_PRORATED_LAST_PAYMENT_FORMATTED_STRING_DATA_KEY));
        }
        lastProratedAmountET.setSelection(lastProratedAmountET.getText().length());

        recommendedFirstPriceLL = rootView.findViewById(R.id.leaseWizardProratedFirstRecommendedPriceLL);
        recommendedLastPriceLL = rootView.findViewById(R.id.leaseWizardProratedLastRecommendedPriceLL);
        firstDateInfoLL = rootView.findViewById(R.id.leaseWizardProratedFirstDateInfoLL);
        lastDateInfoLL = rootView.findViewById(R.id.leaseWizardProratedLastDateInfoLL);

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

        payments = mCallbacks.onGetPage("Page3").getData().getStringArrayList(LeaseWizardPage3.LEASE_PAYMENT_DATES_ARRAY_DATA_KEY);
        paymentMonthlyFrequency = mCallbacks.onGetPage("Page3").getData().getInt(LeaseWizardPage3.LEASE_PAYMENT_FREQUENCY_DATA_KEY);
        String rentCostString = mCallbacks.onGetPage("Page3").getData().getString(LeaseWizardPage3.LEASE_RENT_COST_DATA_KEY);
        rentCost = new BigDecimal(rentCostString);
        String endDate = mCallbacks.onGetPage("Page1").getData().getString(LeaseWizardPage1.LEASE_END_DATE_STRING_DATA_KEY);
        DateFormat formatFrom = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
        try {
            leaseEndDate = formatFrom.parse(endDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        LocalDate firstPaymentStart = new LocalDate(payments.get(0));
        LocalDate firstPaymentEnd = new LocalDate(payments.get(1));
        LocalDate lastPaymentStart = new LocalDate(payments.get(payments.size() - 1));
        LocalDate lastPaymentEnd = new LocalDate(leaseEndDate);
        int daysOfFirstPayment = Days.daysBetween(firstPaymentStart, firstPaymentEnd).getDays();
        int daysOfLastPayment = Days.daysBetween(lastPaymentStart, lastPaymentEnd).getDays();
        int totalDaysInFirstFullPeriod = getDaysOfCycleIfFirstWasntProrated(firstPaymentEnd, paymentMonthlyFrequency);
        int totalDaysInLastFullPeriod = getDaysOfCycleIfLastWasntProrated(lastPaymentStart, paymentMonthlyFrequency);

        BigDecimal recommendedProratedFirst = figureRecommendedProratedPayment(daysOfFirstPayment, totalDaysInFirstFullPeriod, rentCost);
        BigDecimal recommendedProratedLast = figureRecommendedProratedPayment(daysOfLastPayment, totalDaysInLastFullPeriod, rentCost);

        firstProratedStartDateTV.setText(firstPaymentStart.toString("MM/dd/yyyy"));
        firstProratedEndDateTV.setText(firstPaymentEnd.toString("MM/dd/yyyy"));
        lastProratedStartDateTV.setText(lastPaymentStart.toString("MM/dd/yyyy"));
        lastProratedEndDateTV.setText(lastPaymentEnd.toString("MM/dd/yyyy"));
        String formattedFirstRec = NumberFormat.getCurrencyInstance().format(recommendedProratedFirst);
        String formattedLastRec = NumberFormat.getCurrencyInstance().format(recommendedProratedLast);
        firstProratedRentRecommendationTV.setText(formattedFirstRec);
        lastProratedRentRecommendationTV.setText(formattedLastRec);

        firstProratedDayAmountTV.setText(daysOfFirstPayment + "");
        lastProratedDayAmountTV.setText(daysOfLastPayment + "");

        if (mPage.getData().getString(LeaseWizardProratedRentPage.LEASE_PRORATED_FIRST_PAYMENT_FORMATTED_STRING_DATA_KEY) != null) {
            if(mPage.getData().getBoolean(LeaseWizardProratedRentPage.LEASE_PRORATED_FIRST_PAYMENT_WAS_MODIFIED_DATA_KEY)) {
                proratedFirst = new BigDecimal(mPage.getData().getString(LeaseWizardProratedRentPage.LEASE_PRORATED_FIRST_PAYMENT_STRING_DATA_KEY));
                firstProratedAmountET.setText(mPage.getData().getString(LeaseWizardProratedRentPage.LEASE_PRORATED_FIRST_PAYMENT_FORMATTED_STRING_DATA_KEY));
                firstProratedAmountET.setSelection(firstProratedAmountET.getText().length());
            } else {
                proratedFirst = recommendedProratedFirst;
                String formatted = NumberFormat.getCurrencyInstance().format(proratedFirst);
                mPage.getData().putString(LeaseWizardProratedRentPage.LEASE_PRORATED_FIRST_PAYMENT_FORMATTED_STRING_DATA_KEY, formatted);
                mPage.getData().putString(LeaseWizardProratedRentPage.LEASE_PRORATED_FIRST_PAYMENT_STRING_DATA_KEY, proratedFirst.toPlainString());
                firstProratedAmountET.setText(formatted);
                firstProratedAmountET.setSelection(firstProratedAmountET.getText().length());
            }
        } else {
            proratedFirst = recommendedProratedFirst;
            String formatted = NumberFormat.getCurrencyInstance().format(proratedFirst);
            mPage.getData().putString(LeaseWizardProratedRentPage.LEASE_PRORATED_FIRST_PAYMENT_FORMATTED_STRING_DATA_KEY, formatted);
            mPage.getData().putString(LeaseWizardProratedRentPage.LEASE_PRORATED_FIRST_PAYMENT_STRING_DATA_KEY, proratedFirst.toPlainString());
            firstProratedAmountET.setText(formatted);
            firstProratedAmountET.setSelection(firstProratedAmountET.getText().length());
        }
        if (mPage.getData().getString(LeaseWizardProratedRentPage.LEASE_PRORATED_LAST_PAYMENT_FORMATTED_STRING_DATA_KEY) != null) {
            if(mPage.getData().getBoolean(LeaseWizardProratedRentPage.LEASE_PRORATED_LAST_PAYMENT_WAS_MODIFIED_DATA_KEY)) {
                proratedLast = new BigDecimal(mPage.getData().getString(LeaseWizardProratedRentPage.LEASE_PRORATED_LAST_PAYMENT_STRING_DATA_KEY));
                lastProratedAmountET.setText(mPage.getData().getString(LeaseWizardProratedRentPage.LEASE_PRORATED_LAST_PAYMENT_FORMATTED_STRING_DATA_KEY));
                lastProratedAmountET.setSelection(lastProratedAmountET.getText().length());
            } else {
                proratedLast = recommendedProratedLast;
                String formatted = NumberFormat.getCurrencyInstance().format(proratedLast);
                mPage.getData().putString(LeaseWizardProratedRentPage.LEASE_PRORATED_LAST_PAYMENT_FORMATTED_STRING_DATA_KEY, formatted);
                mPage.getData().putString(LeaseWizardProratedRentPage.LEASE_PRORATED_LAST_PAYMENT_STRING_DATA_KEY, proratedLast.toPlainString());
                lastProratedAmountET.setText(formatted);
                lastProratedAmountET.setSelection(lastProratedAmountET.getText().length());
            }
        } else {
            proratedLast = recommendedProratedLast;
            String formatted = NumberFormat.getCurrencyInstance().format(proratedLast);
            mPage.getData().putString(LeaseWizardProratedRentPage.LEASE_PRORATED_LAST_PAYMENT_FORMATTED_STRING_DATA_KEY, formatted);
            mPage.getData().putString(LeaseWizardProratedRentPage.LEASE_PRORATED_LAST_PAYMENT_STRING_DATA_KEY, proratedLast.toPlainString());
            lastProratedAmountET.setText(formatted);
            lastProratedAmountET.setSelection(lastProratedAmountET.getText().length());
        }

        firstProratedAmountET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (firstProratedAmountET == null) return;
                String s = editable.toString();
                if (s.isEmpty()) return;
                firstProratedAmountET.removeTextChangedListener(this);
                String cleanString = s.replaceAll("[$,.]", "");
                proratedFirst = new BigDecimal(cleanString).setScale(2, BigDecimal.ROUND_FLOOR).divide(new BigDecimal(100), BigDecimal.ROUND_FLOOR);
                String formatted = NumberFormat.getCurrencyInstance().format(proratedFirst);
                firstProratedAmountET.setText(formatted);
                mPage.getData().putString(LeaseWizardProratedRentPage.LEASE_PRORATED_FIRST_PAYMENT_FORMATTED_STRING_DATA_KEY, formatted);
                mPage.getData().putString(LeaseWizardProratedRentPage.LEASE_PRORATED_FIRST_PAYMENT_STRING_DATA_KEY, proratedLast.toPlainString());
                mPage.getData().putBoolean(LeaseWizardProratedRentPage.LEASE_PRORATED_FIRST_PAYMENT_WAS_MODIFIED_DATA_KEY, true);
                mPage.notifyDataChanged();
                firstProratedAmountET.setSelection(formatted.length());
                firstProratedAmountET.addTextChangedListener(this);
            }
        });

        lastProratedAmountET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (lastProratedAmountET == null) return;
                String s = editable.toString();
                if (s.isEmpty()) return;
                lastProratedAmountET.removeTextChangedListener(this);
                String cleanString = s.replaceAll("[$,.]", "");
                proratedLast = new BigDecimal(cleanString).setScale(2, BigDecimal.ROUND_FLOOR).divide(new BigDecimal(100), BigDecimal.ROUND_FLOOR);
                String formatted = NumberFormat.getCurrencyInstance().format(proratedLast);
                lastProratedAmountET.setText(formatted);
                mPage.getData().putString(LeaseWizardProratedRentPage.LEASE_PRORATED_LAST_PAYMENT_FORMATTED_STRING_DATA_KEY, formatted);
                mPage.getData().putString(LeaseWizardProratedRentPage.LEASE_PRORATED_LAST_PAYMENT_STRING_DATA_KEY, proratedLast.toPlainString());
                mPage.getData().putBoolean(LeaseWizardProratedRentPage.LEASE_PRORATED_LAST_PAYMENT_WAS_MODIFIED_DATA_KEY, true);
                mPage.notifyDataChanged();
                lastProratedAmountET.setSelection(formatted.length());
                lastProratedAmountET.addTextChangedListener(this);
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
        if (firstProratedAmountET != null) {
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
        BigDecimal dailyAmount = amountOfDaysRentedBD.divide(totalAmountOfDaysInPeriodBD, BigDecimal.ROUND_FLOOR).setScale(5, BigDecimal.ROUND_HALF_UP);
        return dailyAmount.multiply(rentCost).setScale(2, BigDecimal.ROUND_HALF_EVEN);
    }

    private int getDaysOfCycleIfFirstWasntProrated(LocalDate endOfFirstPayment, int monthlyFrequency) {
        LocalDate dateStartOfFullCycle = new LocalDate(endOfFirstPayment);
        dateStartOfFullCycle = dateStartOfFullCycle.minusMonths(monthlyFrequency);
        return Days.daysBetween(dateStartOfFullCycle, endOfFirstPayment).getDays();
    }

    private int getDaysOfCycleIfLastWasntProrated(LocalDate lastPaymentDate, int monthlyFrequency) {
        LocalDate dateEndOfFullCycle = lastPaymentDate.plusMonths(monthlyFrequency);
        return Days.daysBetween(lastPaymentDate, dateEndOfFullCycle).getDays();
    }
}