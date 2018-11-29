package com.rba18.fragments;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.rba18.R;
import com.rba18.activities.IncomeViewActivity;
import com.rba18.activities.MainActivity;
import com.rba18.adapters.IncomeListAdapter;
import com.rba18.helpers.CustomDatePickerDialogLauncher;
import com.rba18.helpers.DateAndCurrencyDisplayer;
import com.rba18.helpers.MainViewModel;
import com.rba18.model.PaymentLogEntry;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Cody on 3/23/2018.
 */

public class IncomeListFragment extends android.support.v4.app.Fragment implements AdapterView.OnItemClickListener, View.OnClickListener {
    private TextView mNoIncomeTV, mTotalAmountTV, mTotalAmountLabelTV;
    private EditText mSearchBarET;
    private Button mDateRangeStartBtn, mDateRangeEndBtn;
    private IncomeListAdapter mIncomeListAdapter;
    private ColorStateList mAccentColor;
    private ListView mListView;
    private Date mFilterDateStart, mFilterDateEnd;
    private BigDecimal mTotal;
    private OnDatesChangedListener mCallback;
    private boolean mNeedsRefreshedOnResume, mCompletedOnly;
    private SharedPreferences mPreferences;
    private CustomDatePickerDialogLauncher mDatePickerDialogLauncher;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_money_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mNoIncomeTV = view.findViewById(R.id.emptyListTV);
        mSearchBarET = view.findViewById(R.id.moneyListSearchET);
        mDateRangeStartBtn = view.findViewById(R.id.moneyListDateRangeStartBtn);
        mDateRangeStartBtn.setOnClickListener(this);
        mDateRangeEndBtn = view.findViewById(R.id.moneyListDateRangeEndBtn);
        mDateRangeEndBtn.setOnClickListener(this);
        mTotalAmountLabelTV = view.findViewById(R.id.moneyListTotalAmountLabelTV);
        mTotalAmountTV = view.findViewById(R.id.moneyListTotalAmountTV);
        mListView = view.findViewById(R.id.mainMoneyListView);
        LinearLayout totalAmountLL = view.findViewById(R.id.moneyListTotalAmountLL);
        totalAmountLL.setOnClickListener(this);
        mFilterDateEnd = ViewModelProviders.of(getActivity()).get(MainViewModel.class).getEndDateRangeDate().getValue();
        mFilterDateStart = ViewModelProviders.of(getActivity()).get(MainViewModel.class).getStartDateRangeDate().getValue();
        mPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        int dateFormatCode = mPreferences.getInt("dateFormat", DateAndCurrencyDisplayer.DATE_MMDDYYYY);
        mDateRangeStartBtn.setText(DateAndCurrencyDisplayer.getDateToDisplay(dateFormatCode, mFilterDateStart));
        mDateRangeEndBtn.setText(DateAndCurrencyDisplayer.getDateToDisplay(dateFormatCode, mFilterDateEnd));
        if(savedInstanceState != null){
            mCompletedOnly = savedInstanceState.getBoolean("mCompletedOnly");
        } else {
            mCompletedOnly = true;
        }
        mTotal = getTotal(ViewModelProviders.of(getActivity()).get(MainViewModel.class).getCachedIncome().getValue());
        mDatePickerDialogLauncher = new CustomDatePickerDialogLauncher(mFilterDateStart, mFilterDateEnd, true, getContext());
        mDatePickerDialogLauncher.setDateSelectedListener(new CustomDatePickerDialogLauncher.DateSelectedListener() {
            @Override
            public void onStartDateSelected(Date startDate, Date endDate) {
                mFilterDateStart = startDate;
                mFilterDateEnd = endDate;
                int dateFormatCode = mPreferences.getInt("dateFormat", DateAndCurrencyDisplayer.DATE_MMDDYYYY);
                mDateRangeStartBtn.setText(DateAndCurrencyDisplayer.getDateToDisplay(dateFormatCode, mFilterDateStart));
                mDateRangeEndBtn.setText(DateAndCurrencyDisplayer.getDateToDisplay(dateFormatCode, mFilterDateEnd));
                mCallback.onIncomeListDatesChanged(mFilterDateStart, mFilterDateEnd, IncomeListFragment.this);
            }

            @Override
            public void onEndDateSelected(Date startDate, Date endDate) {
                mFilterDateStart = startDate;
                mFilterDateEnd = endDate;
                int dateFormatCode = mPreferences.getInt("dateFormat", DateAndCurrencyDisplayer.DATE_MMDDYYYY);
                mDateRangeStartBtn.setText(DateAndCurrencyDisplayer.getDateToDisplay(dateFormatCode, mFilterDateStart));
                mDateRangeEndBtn.setText(DateAndCurrencyDisplayer.getDateToDisplay(dateFormatCode, mFilterDateEnd));
                mCallback.onIncomeListDatesChanged(mFilterDateStart, mFilterDateEnd, IncomeListFragment.this);
            }

            @Override
            public void onDateSelected(Date date) {

            }
        });
        getActivity().setTitle(R.string.income_list);
        //Get current theme accent color, which is passed into the list adapter for search highlighting
        TypedValue colorValue = new TypedValue();
        getActivity().getTheme().resolveAttribute(R.attr.colorAccent, colorValue, true);
        mAccentColor = getActivity().getResources().getColorStateList(colorValue.resourceId);
        setUpListAdapter();
        setUpSearchBar();
        setTotalTV();
        mIncomeListAdapter.setOnDataChangeListener(new IncomeListAdapter.OnDataChangeListener() {
            public void onDataChanged(ArrayList<PaymentLogEntry> filteredResults) {
                mTotal = getTotal(filteredResults);
                setTotalTV();
            }
        });
        mNeedsRefreshedOnResume = false;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mNeedsRefreshedOnResume) {
            mIncomeListAdapter.updateResults(ViewModelProviders.of(getActivity()).get(MainViewModel.class).getCachedIncome().getValue());
            mSearchBarET.setText(mSearchBarET.getText());
            mSearchBarET.setSelection(mSearchBarET.getText().length());
        }
        mNeedsRefreshedOnResume = true;
    }

    private void setUpSearchBar() {
        mSearchBarET.addTextChangedListener(new TextWatcher() {
            //For updating search results as sUser types
            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                //When sUser changed the Text
                if (mIncomeListAdapter != null) {
                    mIncomeListAdapter.getFilter().filter(cs);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {

            }
        });
    }


    private void setUpListAdapter() {
        mIncomeListAdapter = new IncomeListAdapter(getActivity(), ViewModelProviders.of(getActivity()).get(MainViewModel.class).getCachedIncome().getValue(), mAccentColor);
        mListView.setAdapter(mIncomeListAdapter);
        mListView.setOnItemClickListener(this);
        mNoIncomeTV.setText(R.string.no_income_to_display);
        mListView.setEmptyView(mNoIncomeTV);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        //On mListView row click, launch IncomeViewActivity passing the rows data into it.
        Intent intent = new Intent(getContext(), IncomeViewActivity.class);
        //Uses filtered results to match what is on screen
        PaymentLogEntry income = mIncomeListAdapter.getFilteredResults().get(i);
        intent.putExtra("incomeID", income.getId());
        getActivity().startActivityForResult(intent, MainActivity.REQUEST_INCOME_VIEW);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.moneyListDateRangeStartBtn:
                mDatePickerDialogLauncher.launchStartDatePickerDialog();
                break;

            case R.id.moneyListDateRangeEndBtn:
                mDatePickerDialogLauncher.launchEndDatePickerDialog();
                break;

            case R.id.moneyListTotalAmountLL:
                if(mCompletedOnly){
                    mCompletedOnly = false;
                    mTotal = getTotal(ViewModelProviders.of(getActivity()).get(MainViewModel.class).getCachedIncome().getValue());
                    setTotalTV();
                } else {
                    mCompletedOnly = true;
                    mTotal = getTotal(ViewModelProviders.of(getActivity()).get(MainViewModel.class).getCachedIncome().getValue());
                    setTotalTV();
                }
                break;

            default:
                break;
        }
    }

    public interface OnDatesChangedListener {
        void onIncomeListDatesChanged(Date dateStart, Date dateEnd, IncomeListFragment fragment);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnDatesChangedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnIncomeDatesChangedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }

    @Override
    public void onPause() {
        super.onPause();
        mDatePickerDialogLauncher.dismissDatePickerDialog();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("mCompletedOnly", mCompletedOnly);

    }

    private BigDecimal getTotal(ArrayList<PaymentLogEntry> filteredIncomeArray) {
        BigDecimal total = new BigDecimal(0);
        if (filteredIncomeArray != null) {
            if (!filteredIncomeArray.isEmpty()) {
                if (mCompletedOnly) {
                    for (int i = 0; i < filteredIncomeArray.size(); i++) {
                        if(filteredIncomeArray.get(i).getIsCompleted()) {
                            total = total.add(filteredIncomeArray.get(i).getAmount());
                        }
                    }
                } else {
                    for (int i = 0; i < filteredIncomeArray.size(); i++) {
                        total = total.add(filteredIncomeArray.get(i).getAmount());
                    }
                }
            }
        }
        return total;
    }

    private void setTotalTV() {
        if(getActivity() != null) {
            mTotalAmountTV.setTextColor(getActivity().getResources().getColor(R.color.green_colorPrimaryDark));
        }
        if(mCompletedOnly){
            mTotalAmountLabelTV.setText(R.string.received_total);
        } else {
            mTotalAmountLabelTV.setText(R.string.projected_total);
        }
        if (mTotal != null) {
            int moneyFormatCode = mPreferences.getInt("currency", DateAndCurrencyDisplayer.CURRENCY_US);
            mTotalAmountTV.setText(DateAndCurrencyDisplayer.getCurrencyToDisplay(moneyFormatCode, mTotal));
        }
    }

    public void updateData(ArrayList<PaymentLogEntry> incomeList) {
        mIncomeListAdapter.updateResults(incomeList);
        mIncomeListAdapter.getFilter().filter(mSearchBarET.getText());
        mIncomeListAdapter.notifyDataSetChanged();
    }
}