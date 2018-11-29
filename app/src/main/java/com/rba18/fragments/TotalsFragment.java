package com.rba18.fragments;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.rba18.R;
import com.rba18.activities.MainActivity;
import com.rba18.adapters.TotalsListAdapter;
import com.rba18.helpers.CustomDatePickerDialogLauncher;
import com.rba18.helpers.DateAndCurrencyDisplayer;
import com.rba18.helpers.MainArrayDataMethods;
import com.rba18.helpers.MainViewModel;
import com.rba18.model.TypeTotal;
import com.rba18.sqlite.DatabaseHandler;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;

public class TotalsFragment extends android.support.v4.app.Fragment implements AdapterView.OnItemClickListener, View.OnClickListener {
    private ArrayList<TypeTotal> mTypeTotals;
    private TextView mNoTotalsTV, mIsCompletedToggleFilterTV, mIncomeTotalTV, mExpenseTotalTV;
    private DatabaseHandler mDB;
    private Date mFilterDateStart, mFilterDateEnd;
    private Button mDateRangeStartBtn, mDateRangeEndBtn;
    private TotalsListAdapter mTotalsListAdapter;
    private ColorStateList mAccentColor;
    private ListView mListView;
    private MainArrayDataMethods mDataMethods;
    private OnDatesChangedListener mCallback;
    private SharedPreferences mPreferences;
    private CustomDatePickerDialogLauncher mDatePickerDialogLauncher;
    private BigDecimal mIncomeTotals, mExpenseTotals;
    private boolean mCompletedOnly;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_totals, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mNoTotalsTV = view.findViewById(R.id.totalsEmptyListTV);
        mIncomeTotalTV = view.findViewById(R.id.totalsListIncomeAmountTV);
        mExpenseTotalTV = view.findViewById(R.id.totalsListExpenseAmountTV);
        mDateRangeStartBtn = view.findViewById(R.id.totalsDateRangeStartBtn);
        mDateRangeStartBtn.setOnClickListener(this);
        mDateRangeEndBtn = view.findViewById(R.id.totalsDateRangeEndBtn);
        mDateRangeEndBtn.setOnClickListener(this);
        mIsCompletedToggleFilterTV = view.findViewById(R.id.totalsIsCompletedFilterTV);
        mIsCompletedToggleFilterTV.setOnClickListener(this);
        mListView = view.findViewById(R.id.totalsListView);
        mDB = new DatabaseHandler(getActivity());
        mDataMethods = new MainArrayDataMethods();
        mFilterDateEnd = ViewModelProviders.of(getActivity()).get(MainViewModel.class).getEndDateRangeDate().getValue();
        mFilterDateStart = ViewModelProviders.of(getActivity()).get(MainViewModel.class).getStartDateRangeDate().getValue();
        mPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        int dateFormatCode = mPreferences.getInt("dateFormat", DateAndCurrencyDisplayer.DATE_MMDDYYYY);
        mDateRangeStartBtn.setText(DateAndCurrencyDisplayer.getDateToDisplay(dateFormatCode, mFilterDateStart));
        mDateRangeEndBtn.setText(DateAndCurrencyDisplayer.getDateToDisplay(dateFormatCode, mFilterDateEnd));
        if (savedInstanceState != null) {
            if (savedInstanceState.getParcelableArrayList("filteredTotals") != null) {
                mTypeTotals = savedInstanceState.getParcelableArrayList("filteredTotals");
            } else {
                mTypeTotals = new ArrayList<>();
            }
            mCompletedOnly = savedInstanceState.getBoolean("mCompletedOnly");
        } else {
            mTypeTotals = new ArrayList<>();
            mTypeTotals.addAll(mDB.getTotalForExpenseTypesWithinDatesOnlyPaid(MainActivity.sUser, mFilterDateStart, mFilterDateEnd));
            mTypeTotals.addAll(mDB.getTotalForIncomeTypesWithinDatesOnlyReceived(MainActivity.sUser, mFilterDateStart, mFilterDateEnd));
            mDataMethods.sortTypeTotalsArrayByTotalAmountDesc(mTypeTotals);
            mCompletedOnly = true;
        }
        if(mCompletedOnly){
            mIsCompletedToggleFilterTV.setText(R.string.paid_received_only);
        } else {
            mIsCompletedToggleFilterTV.setText(R.string.projected);
        }
        TypedValue colorValue = new TypedValue();
        getActivity().getTheme().resolveAttribute(R.attr.colorAccent, colorValue, true);
        mAccentColor = getActivity().getResources().getColorStateList(colorValue.resourceId);
        setUpListAdapter();
        figureIncomeAndExpenseTotals();
        displayIncomeAndExpenseTotals();
        mDatePickerDialogLauncher = new CustomDatePickerDialogLauncher(mFilterDateStart, mFilterDateEnd, true, getContext());
        mDatePickerDialogLauncher.setDateSelectedListener(new CustomDatePickerDialogLauncher.DateSelectedListener() {
            @Override
            public void onStartDateSelected(Date startDate, Date endDate) {
                mFilterDateStart = startDate;
                mFilterDateEnd = endDate;
                int dateFormatCode = mPreferences.getInt("dateFormat", DateAndCurrencyDisplayer.DATE_MMDDYYYY);
                mDateRangeStartBtn.setText(DateAndCurrencyDisplayer.getDateToDisplay(dateFormatCode, mFilterDateStart));
                mDateRangeEndBtn.setText(DateAndCurrencyDisplayer.getDateToDisplay(dateFormatCode, mFilterDateEnd));
                mTypeTotals.clear();
                if(mCompletedOnly){
                    mTypeTotals.addAll(mDB.getTotalForExpenseTypesWithinDatesOnlyPaid(MainActivity.sUser, mFilterDateStart, mFilterDateEnd));
                    mTypeTotals.addAll(mDB.getTotalForIncomeTypesWithinDatesOnlyReceived(MainActivity.sUser, mFilterDateStart, mFilterDateEnd));
                } else {
                    mTypeTotals.addAll(mDB.getTotalForExpenseTypesWithinDates(MainActivity.sUser, mFilterDateStart, mFilterDateEnd));
                    mTypeTotals.addAll(mDB.getTotalForIncomeTypesWithinDates(MainActivity.sUser, mFilterDateStart, mFilterDateEnd));
                }
                mDataMethods.sortTypeTotalsArrayByTotalAmountDesc(mTypeTotals);
                figureIncomeAndExpenseTotals();
                displayIncomeAndExpenseTotals();
                mTotalsListAdapter.notifyDataSetChanged();
                mCallback.onTotalsListDatesChanged(mFilterDateStart, mFilterDateEnd, TotalsFragment.this);
            }

            @Override
            public void onEndDateSelected(Date startDate, Date endDate) {
                mFilterDateStart = startDate;
                mFilterDateEnd = endDate;
                int dateFormatCode = mPreferences.getInt("dateFormat", DateAndCurrencyDisplayer.DATE_MMDDYYYY);
                mDateRangeStartBtn.setText(DateAndCurrencyDisplayer.getDateToDisplay(dateFormatCode, mFilterDateStart));
                mDateRangeEndBtn.setText(DateAndCurrencyDisplayer.getDateToDisplay(dateFormatCode, mFilterDateEnd));
                mTypeTotals.clear();
                if(mCompletedOnly){
                    mTypeTotals.addAll(mDB.getTotalForExpenseTypesWithinDatesOnlyPaid(MainActivity.sUser, mFilterDateStart, mFilterDateEnd));
                    mTypeTotals.addAll(mDB.getTotalForIncomeTypesWithinDatesOnlyReceived(MainActivity.sUser, mFilterDateStart, mFilterDateEnd));
                } else {
                    mTypeTotals.addAll(mDB.getTotalForExpenseTypesWithinDates(MainActivity.sUser, mFilterDateStart, mFilterDateEnd));
                    mTypeTotals.addAll(mDB.getTotalForIncomeTypesWithinDates(MainActivity.sUser, mFilterDateStart, mFilterDateEnd));
                }
                mDataMethods.sortTypeTotalsArrayByTotalAmountDesc(mTypeTotals);
                figureIncomeAndExpenseTotals();
                displayIncomeAndExpenseTotals();
                mTotalsListAdapter.notifyDataSetChanged();
                mCallback.onTotalsListDatesChanged(mFilterDateStart, mFilterDateEnd, TotalsFragment.this);
            }

            @Override
            public void onDateSelected(Date date) {

            }
        });
        getActivity().setTitle(R.string.totals);
    }

    private void setUpListAdapter() {
        mTotalsListAdapter = new TotalsListAdapter(getActivity(), mTypeTotals, mAccentColor);
        mListView.setAdapter(mTotalsListAdapter);
        mListView.setOnItemClickListener(this);
        mNoTotalsTV.setText(R.string.no_data_to_display);
        mListView.setEmptyView(mNoTotalsTV);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }

    private void figureIncomeAndExpenseTotals() {
        mIncomeTotals = new BigDecimal(0);
        mExpenseTotals = new BigDecimal(0);
        if (mTypeTotals != null) {
            if (!mTypeTotals.isEmpty()) {
                for (int i = 0; i < mTypeTotals.size(); i++) {
                    if (mTypeTotals.get(i).getTotalAmount().compareTo(new BigDecimal(0)) < 0) {
                        mExpenseTotals = mExpenseTotals.add(mTypeTotals.get(i).getTotalAmount());
                    } else {
                        mIncomeTotals = mIncomeTotals.add(mTypeTotals.get(i).getTotalAmount());
                    }
                }
            }
        }
    }

    private void displayIncomeAndExpenseTotals() {
        int moneyFormatCode = mPreferences.getInt("currency", DateAndCurrencyDisplayer.CURRENCY_US);
        if (mIncomeTotals != null) {
            mIncomeTotalTV.setText(DateAndCurrencyDisplayer.getCurrencyToDisplay(moneyFormatCode, mIncomeTotals));
        }
        if (mExpenseTotals != null) {
            mExpenseTotalTV.setText(DateAndCurrencyDisplayer.getCurrencyToDisplay(moneyFormatCode, mExpenseTotals));
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.totalsDateRangeStartBtn:
                mDatePickerDialogLauncher.launchStartDatePickerDialog();
                break;

            case R.id.totalsDateRangeEndBtn:
                mDatePickerDialogLauncher.launchEndDatePickerDialog();
                break;

            case R.id.totalsIsCompletedFilterTV:
                mTypeTotals.clear();
                if(mCompletedOnly){
                    mCompletedOnly = false;
                    mIsCompletedToggleFilterTV.setText(R.string.projected);
                    mTypeTotals.addAll(mDB.getTotalForExpenseTypesWithinDates(MainActivity.sUser, mFilterDateStart, mFilterDateEnd));
                    mTypeTotals.addAll(mDB.getTotalForIncomeTypesWithinDates(MainActivity.sUser, mFilterDateStart, mFilterDateEnd));
                } else {
                    mCompletedOnly = true;
                    mIsCompletedToggleFilterTV.setText(R.string.paid_received_only);
                    mTypeTotals.addAll(mDB.getTotalForExpenseTypesWithinDatesOnlyPaid(MainActivity.sUser, mFilterDateStart, mFilterDateEnd));
                    mTypeTotals.addAll(mDB.getTotalForIncomeTypesWithinDatesOnlyReceived(MainActivity.sUser, mFilterDateStart, mFilterDateEnd));
                }
                mDataMethods.sortTypeTotalsArrayByTotalAmountDesc(mTypeTotals);
                figureIncomeAndExpenseTotals();
                displayIncomeAndExpenseTotals();
                mTotalsListAdapter.notifyDataSetChanged();
                mCallback.onTotalsListDatesChanged(mFilterDateStart, mFilterDateEnd, TotalsFragment.this);
                break;

            default:
                break;
        }
    }

    public interface OnDatesChangedListener {
        void onTotalsListDatesChanged(Date dateStart, Date dateEnd, TotalsFragment fragment);
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
                    + " must implement OnTotalsDatesChangedListener");
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
        if (mTypeTotals != null) {
            outState.putParcelableArrayList("filteredTotals", mTypeTotals);
        }
        outState.putBoolean("mCompletedOnly", mCompletedOnly);
    }
}
