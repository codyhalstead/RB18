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
    ArrayList<TypeTotal> typeTotals;
    TextView noTotalsTV, isCompletedToggleFilterTV;
    DatabaseHandler db;
    Date filterDateStart, filterDateEnd;
    Button dateRangeStartBtn, dateRangeEndBtn;
    TotalsListAdapter totalsListAdapter;
    ColorStateList accentColor;
    ListView listView;
    MainArrayDataMethods dataMethods;
    TextView incomeTotalTV, expenseTotalTV;
    private OnDatesChangedListener mCallback;
    private SharedPreferences preferences;
    private CustomDatePickerDialogLauncher datePickerDialogLauncher;
    private BigDecimal incomeTotals, expenseTotals;
    private boolean completedOnly;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_totals, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        noTotalsTV = view.findViewById(R.id.totalsEmptyListTV);
        this.incomeTotalTV = view.findViewById(R.id.totalsListIncomeAmountTV);
        this.expenseTotalTV = view.findViewById(R.id.totalsListExpenseAmountTV);
        this.dateRangeStartBtn = view.findViewById(R.id.totalsDateRangeStartBtn);
        this.dateRangeStartBtn.setOnClickListener(this);
        this.dateRangeEndBtn = view.findViewById(R.id.totalsDateRangeEndBtn);
        this.dateRangeEndBtn.setOnClickListener(this);
        this.isCompletedToggleFilterTV = view.findViewById(R.id.totalsIsCompletedFilterTV);
        this.isCompletedToggleFilterTV.setOnClickListener(this);
        this.listView = view.findViewById(R.id.totalsListView);
        db = new DatabaseHandler(getActivity());
        dataMethods = new MainArrayDataMethods();
        this.filterDateEnd = ViewModelProviders.of(getActivity()).get(MainViewModel.class).getEndDateRangeDate().getValue();
        this.filterDateStart = ViewModelProviders.of(getActivity()).get(MainViewModel.class).getStartDateRangeDate().getValue();
        this.preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        int dateFormatCode = preferences.getInt("dateFormat", DateAndCurrencyDisplayer.DATE_MMDDYYYY);
        dateRangeStartBtn.setText(DateAndCurrencyDisplayer.getDateToDisplay(dateFormatCode, filterDateStart));
        dateRangeEndBtn.setText(DateAndCurrencyDisplayer.getDateToDisplay(dateFormatCode, filterDateEnd));
        if (savedInstanceState != null) {
            if (savedInstanceState.getParcelableArrayList("filteredTotals") != null) {
                this.typeTotals = savedInstanceState.getParcelableArrayList("filteredTotals");
            } else {
                this.typeTotals = new ArrayList<>();
            }
            completedOnly = savedInstanceState.getBoolean("completedOnly");
        } else {
            typeTotals = new ArrayList<>();
            typeTotals.addAll(db.getTotalForExpenseTypesWithinDatesOnlyPaid(MainActivity.user, filterDateStart, filterDateEnd));
            typeTotals.addAll(db.getTotalForIncomeTypesWithinDatesOnlyReceived(MainActivity.user, filterDateStart, filterDateEnd));
            dataMethods.sortTypeTotalsArrayByTotalAmountDesc(typeTotals);
            completedOnly = true;
        }
        if(completedOnly){
            isCompletedToggleFilterTV.setText(R.string.paid_received_only);
        } else {
            isCompletedToggleFilterTV.setText(R.string.projected);
        }
        TypedValue colorValue = new TypedValue();
        getActivity().getTheme().resolveAttribute(R.attr.colorAccent, colorValue, true);
        this.accentColor = getActivity().getResources().getColorStateList(colorValue.resourceId);
        setUpListAdapter();
        figureIncomeAndExpenseTotals();
        displayIncomeAndExpenseTotals();
        datePickerDialogLauncher = new CustomDatePickerDialogLauncher(filterDateStart, filterDateEnd, true, getContext());
        datePickerDialogLauncher.setDateSelectedListener(new CustomDatePickerDialogLauncher.DateSelectedListener() {
            @Override
            public void onStartDateSelected(Date startDate, Date endDate) {
                filterDateStart = startDate;
                filterDateEnd = endDate;
                int dateFormatCode = preferences.getInt("dateFormat", DateAndCurrencyDisplayer.DATE_MMDDYYYY);
                dateRangeStartBtn.setText(DateAndCurrencyDisplayer.getDateToDisplay(dateFormatCode, filterDateStart));
                dateRangeEndBtn.setText(DateAndCurrencyDisplayer.getDateToDisplay(dateFormatCode, filterDateEnd));
                typeTotals.clear();
                if(completedOnly){
                    typeTotals.addAll(db.getTotalForExpenseTypesWithinDatesOnlyPaid(MainActivity.user, filterDateStart, filterDateEnd));
                    typeTotals.addAll(db.getTotalForIncomeTypesWithinDatesOnlyReceived(MainActivity.user, filterDateStart, filterDateEnd));
                } else {
                    typeTotals.addAll(db.getTotalForExpenseTypesWithinDates(MainActivity.user, filterDateStart, filterDateEnd));
                    typeTotals.addAll(db.getTotalForIncomeTypesWithinDates(MainActivity.user, filterDateStart, filterDateEnd));
                }
                dataMethods.sortTypeTotalsArrayByTotalAmountDesc(typeTotals);
                figureIncomeAndExpenseTotals();
                displayIncomeAndExpenseTotals();
                totalsListAdapter.notifyDataSetChanged();
                mCallback.onTotalsListDatesChanged(filterDateStart, filterDateEnd, TotalsFragment.this);
            }

            @Override
            public void onEndDateSelected(Date startDate, Date endDate) {
                filterDateStart = startDate;
                filterDateEnd = endDate;
                int dateFormatCode = preferences.getInt("dateFormat", DateAndCurrencyDisplayer.DATE_MMDDYYYY);
                dateRangeStartBtn.setText(DateAndCurrencyDisplayer.getDateToDisplay(dateFormatCode, filterDateStart));
                dateRangeEndBtn.setText(DateAndCurrencyDisplayer.getDateToDisplay(dateFormatCode, filterDateEnd));
                typeTotals.clear();
                if(completedOnly){
                    typeTotals.addAll(db.getTotalForExpenseTypesWithinDatesOnlyPaid(MainActivity.user, filterDateStart, filterDateEnd));
                    typeTotals.addAll(db.getTotalForIncomeTypesWithinDatesOnlyReceived(MainActivity.user, filterDateStart, filterDateEnd));
                } else {
                    typeTotals.addAll(db.getTotalForExpenseTypesWithinDates(MainActivity.user, filterDateStart, filterDateEnd));
                    typeTotals.addAll(db.getTotalForIncomeTypesWithinDates(MainActivity.user, filterDateStart, filterDateEnd));
                }
                dataMethods.sortTypeTotalsArrayByTotalAmountDesc(typeTotals);
                figureIncomeAndExpenseTotals();
                displayIncomeAndExpenseTotals();
                totalsListAdapter.notifyDataSetChanged();
                mCallback.onTotalsListDatesChanged(filterDateStart, filterDateEnd, TotalsFragment.this);
            }

            @Override
            public void onDateSelected(Date date) {

            }
        });
        getActivity().setTitle(R.string.totals);
    }

    private void setUpListAdapter() {
        totalsListAdapter = new TotalsListAdapter(getActivity(), typeTotals, accentColor);
        listView.setAdapter(totalsListAdapter);
        listView.setOnItemClickListener(this);
        noTotalsTV.setText(R.string.no_data_to_display);
        this.listView.setEmptyView(noTotalsTV);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }

    private void figureIncomeAndExpenseTotals() {
        incomeTotals = new BigDecimal(0);
        expenseTotals = new BigDecimal(0);
        if (typeTotals != null) {
            if (!typeTotals.isEmpty()) {
                for (int i = 0; i < typeTotals.size(); i++) {
                    if (typeTotals.get(i).getTotalAmount().compareTo(new BigDecimal(0)) < 0) {
                        expenseTotals = expenseTotals.add(typeTotals.get(i).getTotalAmount());
                    } else {
                        incomeTotals = incomeTotals.add(typeTotals.get(i).getTotalAmount());
                    }
                }
            }
        }
    }

    private void displayIncomeAndExpenseTotals() {
        int moneyFormatCode = preferences.getInt("currency", DateAndCurrencyDisplayer.CURRENCY_US);
        if (incomeTotals != null) {
            incomeTotalTV.setText(DateAndCurrencyDisplayer.getCurrencyToDisplay(moneyFormatCode, incomeTotals));
        }
        if (expenseTotals != null) {
            expenseTotalTV.setText(DateAndCurrencyDisplayer.getCurrencyToDisplay(moneyFormatCode, expenseTotals));
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.totalsDateRangeStartBtn:
                datePickerDialogLauncher.launchStartDatePickerDialog();
                break;

            case R.id.totalsDateRangeEndBtn:
                datePickerDialogLauncher.launchEndDatePickerDialog();
                break;

            case R.id.totalsIsCompletedFilterTV:
                typeTotals.clear();
                if(completedOnly){
                    completedOnly = false;
                    isCompletedToggleFilterTV.setText(R.string.projected);
                    typeTotals.addAll(db.getTotalForExpenseTypesWithinDates(MainActivity.user, filterDateStart, filterDateEnd));
                    typeTotals.addAll(db.getTotalForIncomeTypesWithinDates(MainActivity.user, filterDateStart, filterDateEnd));
                } else {
                    completedOnly = true;
                    isCompletedToggleFilterTV.setText(R.string.paid_received_only);
                    typeTotals.addAll(db.getTotalForExpenseTypesWithinDatesOnlyPaid(MainActivity.user, filterDateStart, filterDateEnd));
                    typeTotals.addAll(db.getTotalForIncomeTypesWithinDatesOnlyReceived(MainActivity.user, filterDateStart, filterDateEnd));
                }
                dataMethods.sortTypeTotalsArrayByTotalAmountDesc(typeTotals);
                figureIncomeAndExpenseTotals();
                displayIncomeAndExpenseTotals();
                totalsListAdapter.notifyDataSetChanged();
                mCallback.onTotalsListDatesChanged(filterDateStart, filterDateEnd, TotalsFragment.this);
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
        datePickerDialogLauncher.dismissDatePickerDialog();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (typeTotals != null) {
            outState.putParcelableArrayList("filteredTotals", typeTotals);
        }
        outState.putBoolean("completedOnly", completedOnly);
    }
}
