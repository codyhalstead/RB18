package com.rentbud.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.android.wizardpager.wizard.ui.PageFragmentCallbacks;
import com.example.cody.rentbud.R;
import com.rentbud.wizards.LeaseWizardPage1;
import com.rentbud.wizards.LeaseWizardPage2;
import com.rentbud.wizards.LeaseWizardPage3;

import org.joda.time.LocalDate;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import static android.support.constraint.Constraints.TAG;

public class LeaseWizardPage3Fragment extends android.support.v4.app.Fragment {
    private static final String ARG_KEY = "key";

    private PageFragmentCallbacks mCallbacks;
    private String mKey;
    private LeaseWizardPage3 mPage;
    private TextView paymentsAmountTV, proratedPaymentsAmountTV;
    private EditText rentCostET;
    private BigDecimal rentCost;
    private Spinner paymentFrequencySpinner, paymentDateSpinner;
    private int regular, prorated, paymentDay, paymentFrequency;
    private ArrayList<String> paymentDates;
    private Boolean isFirstLoad;

    Date leaseStartDate, leaseEndDate;

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
        rentCost = new BigDecimal(0);
        paymentDates = new ArrayList<>();
        paymentDay = 1;
        paymentFrequency = 1;
        isFirstLoad = true;
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
        paymentDateSpinner.setSelection(mPage.getData().getInt(LeaseWizardPage3.LEASE_PAYMENT_FREQUENCY_DATA_KEY));

        paymentFrequencySpinner = rootView.findViewById(R.id.leaseWizardRentFrequencySpinner);
        paymentFrequencySpinner.setSelection(mPage.getData().getInt(LeaseWizardPage3.LEASE_PAYMENT_FREQUENCY_DATA_KEY));

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

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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
                String cleanString = s.replaceAll("[$,.]", "");
                rentCost = new BigDecimal(cleanString).setScale(2, BigDecimal.ROUND_FLOOR).divide(new BigDecimal(100), BigDecimal.ROUND_FLOOR);
                String formatted = NumberFormat.getCurrencyInstance().format(rentCost);
                rentCostET.setText(formatted);
                mPage.getData().putString(LeaseWizardPage3.LEASE_RENT_COST_FORMATTED_STRING_DATA_KEY, formatted);
                mPage.getData().putString(LeaseWizardPage3.LEASE_RENT_COST_DATA_KEY, rentCost.toPlainString());
                mPage.notifyDataChanged();
                rentCostET.setSelection(formatted.length());
                rentCostET.addTextChangedListener(this);
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
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                paymentDay = position + 1;
                if (!isFirstLoad) {
                    if (getUserVisibleHint()) {
                        new Handler().post(new Runnable() {
                            @Override
                            public void run() {
                                //       if (getUserVisibleHint()) {
                                figurePayments(leaseStartDate, leaseEndDate, paymentDay, paymentFrequency);
                                mPage.getData().putString(LeaseWizardPage3.LEASE_DUE_DATE_STRING_DATA_KEY, paymentDateSpinner.getSelectedItem().toString());
                                mPage.getData().putInt(LeaseWizardPage3.LEASE_DUE_DATE_DATA_KEY, paymentDay);

                                mPage.notifyDataChanged();
                                paymentsAmountTV.setText(prorated + regular + "");
                                proratedPaymentsAmountTV.setText(prorated + "");
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
                paymentFrequency = position + 1;
                if (!isFirstLoad) {
                    if (getUserVisibleHint()) {
                        new Handler().post(new Runnable() {
                            @Override
                            public void run() {
                                //       if (getUserVisibleHint()) {
                                figurePayments(leaseStartDate, leaseEndDate, paymentDay, paymentFrequency);
                                mPage.getData().putInt(LeaseWizardPage3.LEASE_PAYMENT_FREQUENCY_DATA_KEY, paymentFrequency);
                                mPage.getData().putString(LeaseWizardPage3.LEASE_PAYMENT_FREQUENCY_STRING_DATA_KEY, paymentFrequencySpinner.getSelectedItem().toString());
                                mPage.notifyDataChanged();
                                paymentsAmountTV.setText(prorated + regular + "");
                                proratedPaymentsAmountTV.setText(prorated + "");
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
        //paymentDay = 20;

        figurePayments(leaseStartDate, leaseEndDate, paymentDay, paymentFrequency);
        paymentsAmountTV.setText(prorated + regular + "");
        proratedPaymentsAmountTV.setText(prorated + "");

        if (mPage.getData().getString(LeaseWizardPage3.LEASE_RENT_COST_DATA_KEY) == null) {
            String formatted = NumberFormat.getCurrencyInstance().format(rentCost);
            mPage.getData().putString(LeaseWizardPage3.LEASE_RENT_COST_FORMATTED_STRING_DATA_KEY, formatted);
            mPage.getData().putString(LeaseWizardPage3.LEASE_RENT_COST_DATA_KEY, rentCost.toPlainString());
        }

        if (mPage.getData().getString(LeaseWizardPage3.LEASE_PAYMENT_FREQUENCY_STRING_DATA_KEY) == null) {
            mPage.getData().putInt(LeaseWizardPage3.LEASE_PAYMENT_FREQUENCY_DATA_KEY, paymentFrequency);
            mPage.getData().putString(LeaseWizardPage3.LEASE_PAYMENT_FREQUENCY_STRING_DATA_KEY, paymentFrequencySpinner.getSelectedItem().toString());
        }
        if (mPage.getData().getString(LeaseWizardPage3.LEASE_DUE_DATE_STRING_DATA_KEY) == null) {
            mPage.getData().putString(LeaseWizardPage3.LEASE_DUE_DATE_STRING_DATA_KEY, paymentDateSpinner.getSelectedItem().toString());
            mPage.getData().putInt(LeaseWizardPage3.LEASE_DUE_DATE_DATA_KEY, paymentDay);
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

    private void figurePayments(Date leaseStartDate, Date leaseEndDate, int paymentDay, int paymentFrequency) {
        prorated = 0;
        regular = 0;
        LocalDate startDate = new LocalDate(leaseStartDate);
        LocalDate endDate = new LocalDate(leaseEndDate);
        paymentDates.clear();

        LocalDate payment = new LocalDate(startDate.getYear(), startDate.getMonthOfYear(), paymentDay);
        paymentDates.add(startDate.toString("yyyy-MM-dd"));
        if (startDate.isBefore(payment)) {
            prorated++;
            //payment = payment.plusMonths(1);
            Log.d(TAG, "figurePayments: INITIAL PAYMENT ---- " + startDate + " --> " + payment + " PRO");
        } else if (startDate.isEqual(payment)) {
            regular++;
            payment = payment.plusMonths(paymentFrequency);
            payment = keepPaymentDayConsistentForEndOfMonth(paymentDay, payment);
            Log.d(TAG, "figurePayments: INITIAL PAYMENT ---- " + startDate + " --> " + payment);
        } else {
            payment = payment.plusMonths(paymentFrequency);
            payment = keepPaymentDayConsistentForEndOfMonth(paymentDay, payment);
            prorated++;
            Log.d(TAG, "figurePayments: INITIAL PAYMENT ---- " + startDate + " --> " + payment + " PRO");
        }

        while (payment.isBefore(endDate.minusMonths(paymentFrequency))) {
            regular++;
            LocalDate pdate = payment;
            paymentDates.add(pdate.toString("yyyy-MM-dd"));
            payment = payment.plusMonths(paymentFrequency);
            payment = keepPaymentDayConsistentForEndOfMonth(paymentDay, payment);
            Log.d(TAG, "figurePayments: PAYMENT ---- " + pdate + " --> " + payment);
        }

        if (payment.plusMonths(paymentFrequency).equals(endDate)) {
            regular++;
            paymentDates.add(payment.toString("yyyy-MM-dd"));
            Log.d(TAG, "figurePayments: FINAL PAYMENT ---- " + payment + " --> " + endDate);
        } else {
            prorated++;
            paymentDates.add(payment.toString("yyyy-MM-dd"));
            Log.d(TAG, "figurePayments: FINAL PAYMENT ---- " + payment + " --> " + endDate + " PRO");
        }
        mPage.getData().putStringArrayList(LeaseWizardPage3.LEASE_PAYMENT_DATES_ARRAY_DATA_KEY, paymentDates);
        if (prorated > 0) {
            mPage.getData().putString(LeaseWizardPage3.LEASE_NEED_BRANCH, "Yes");
        } else {
            mPage.getData().putString(LeaseWizardPage3.LEASE_NEED_BRANCH, "No");
        }
        //TODO problem
        //new Handler().post(new Runnable() {
        //    @Override
        //    public void run() {
        //       if (getUserVisibleHint()) {
        //           mPage.notifyDataChanged();
        //       }
        //   }
        // });

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

}