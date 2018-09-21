package com.rentbud.fragments;

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
import android.widget.ListView;
import android.widget.TextView;

import com.example.cody.rentbud.R;
import com.rentbud.activities.ExpenseViewActivity;
import com.rentbud.activities.MainActivity;
import com.rentbud.adapters.ExpenseListAdapter;
import com.rentbud.helpers.CustomDatePickerDialogLauncher;
import com.rentbud.helpers.DateAndCurrencyDisplayer;
import com.rentbud.helpers.MainViewModel;
import com.rentbud.model.ExpenseLogEntry;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Cody on 3/23/2018.
 */

public class ExpenseListFragment extends android.support.v4.app.Fragment implements AdapterView.OnItemClickListener, View.OnClickListener {
    TextView noExpensesTV, totalAmountTV, totalAmountLabelTV;
    EditText searchBarET;
    ExpenseListAdapter expenseListAdapter;
    ColorStateList accentColor;
    ListView listView;
    Button dateRangeStartBtn, dateRangeEndBtn;
    Date filterDateStart, filterDateEnd;
    private BigDecimal total;
    private OnDatesChangedListener mCallback;
    private boolean needsRefreshedOnResume;
    private SharedPreferences preferences;
    private CustomDatePickerDialogLauncher datePickerDialogLauncher;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_money_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.noExpensesTV = view.findViewById(R.id.emptyListTV);
        this.searchBarET = view.findViewById(R.id.moneyListSearchET);
        this.dateRangeStartBtn = view.findViewById(R.id.moneyListDateRangeStartBtn);
        this.dateRangeStartBtn.setOnClickListener(this);
        this.dateRangeEndBtn = view.findViewById(R.id.moneyListDateRangeEndBtn);
        this.dateRangeEndBtn.setOnClickListener(this);
        this.totalAmountLabelTV = view.findViewById(R.id.moneyListTotalAmountLabelTV);
        this.totalAmountTV = view.findViewById(R.id.moneyListTotalAmountTV);
        this.listView = view.findViewById(R.id.mainMoneyListView);
        this.filterDateEnd = ViewModelProviders.of(getActivity()).get(MainViewModel.class).getEndDateRangeDate().getValue();
        this.filterDateStart = ViewModelProviders.of(getActivity()).get(MainViewModel.class).getStartDateRangeDate().getValue();
        this.preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        int dateFormatCode = preferences.getInt("dateFormat", DateAndCurrencyDisplayer.DATE_MMDDYYYY);
        dateRangeStartBtn.setText(DateAndCurrencyDisplayer.getDateToDisplay(dateFormatCode, filterDateStart));
        dateRangeEndBtn.setText(DateAndCurrencyDisplayer.getDateToDisplay(dateFormatCode, filterDateEnd));
        total = getTotal(ViewModelProviders.of(getActivity()).get(MainViewModel.class).getCachedExpenses().getValue());
        datePickerDialogLauncher = new CustomDatePickerDialogLauncher(filterDateStart, filterDateEnd, true, getContext());
        datePickerDialogLauncher.setDateSelectedListener(new CustomDatePickerDialogLauncher.DateSelectedListener() {
            @Override
            public void onStartDateSelected(Date startDate, Date endDate) {
                filterDateStart = startDate;
                filterDateEnd = endDate;
                int dateFormatCode = preferences.getInt("dateFormat", DateAndCurrencyDisplayer.DATE_MMDDYYYY);
                dateRangeStartBtn.setText(DateAndCurrencyDisplayer.getDateToDisplay(dateFormatCode, filterDateStart));
                dateRangeEndBtn.setText(DateAndCurrencyDisplayer.getDateToDisplay(dateFormatCode, filterDateEnd));
                mCallback.onExpenseListDatesChanged(filterDateStart, filterDateEnd, ExpenseListFragment.this);
            }

            @Override
            public void onEndDateSelected(Date startDate, Date endDate) {
                filterDateStart = startDate;
                filterDateEnd = endDate;
                int dateFormatCode = preferences.getInt("dateFormat", DateAndCurrencyDisplayer.DATE_MMDDYYYY);
                dateRangeStartBtn.setText(DateAndCurrencyDisplayer.getDateToDisplay(dateFormatCode, filterDateStart));
                dateRangeEndBtn.setText(DateAndCurrencyDisplayer.getDateToDisplay(dateFormatCode, filterDateEnd));
                mCallback.onExpenseListDatesChanged(filterDateStart, filterDateEnd, ExpenseListFragment.this);
            }

            @Override
            public void onDateSelected(Date date) {

            }
        });
        getActivity().setTitle(R.string.expense_list);
        //Get current theme accent color, which is passed into the list adapter for search highlighting
        TypedValue colorValue = new TypedValue();
        getActivity().getTheme().resolveAttribute(R.attr.colorAccent, colorValue, true);
        this.accentColor = getActivity().getResources().getColorStateList(colorValue.resourceId);
        setUpListAdapter();
        setUpSearchBar();
        setTotalTV();
        expenseListAdapter.setOnDataChangeListener(new ExpenseListAdapter.OnDataChangeListener() {
            public void onDataChanged(ArrayList<ExpenseLogEntry> filteredResults) {
                total = getTotal(filteredResults);
                setTotalTV();
            }
        });
        needsRefreshedOnResume = false;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (needsRefreshedOnResume) {
            expenseListAdapter.updateResults(ViewModelProviders.of(getActivity()).get(MainViewModel.class).getCachedExpenses().getValue());
            searchBarET.setText(searchBarET.getText());
            searchBarET.setSelection(searchBarET.getText().length());
        }
        needsRefreshedOnResume = true;
    }

    private void setUpSearchBar() {
        searchBarET.addTextChangedListener(new TextWatcher() {
            //For updating search results as user types
            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                //When user changed the Text
                if (expenseListAdapter != null) {
                    expenseListAdapter.getFilter().filter(cs);

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
        expenseListAdapter = new ExpenseListAdapter(getActivity(), ViewModelProviders.of(getActivity()).get(MainViewModel.class).getCachedExpenses().getValue(), accentColor);
        listView.setAdapter(expenseListAdapter);
        listView.setOnItemClickListener(this);
        noExpensesTV.setText(R.string.no_expenses_to_display);
        this.listView.setEmptyView(noExpensesTV);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        //On listView row click, launch ApartmentViewActivity passing the rows data into it.
        Intent intent = new Intent(getContext(), ExpenseViewActivity.class);
        ExpenseLogEntry expense = expenseListAdapter.getFilteredResults().get(i);
        intent.putExtra("expenseID", expense.getId());
        getActivity().startActivityForResult(intent, MainActivity.REQUEST_EXPENSE_VIEW);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.moneyListDateRangeStartBtn:
                datePickerDialogLauncher.launchStartDatePickerDialog();
                break;

            case R.id.moneyListDateRangeEndBtn:
                datePickerDialogLauncher.launchEndDatePickerDialog();
                break;

            default:
                break;
        }
    }

    public interface OnDatesChangedListener {
        void onExpenseListDatesChanged(Date dateStart, Date dateEnd, ExpenseListFragment fragment);
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
                    + " must implement OnExpenseDatesChangedListener");
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

    }

    private BigDecimal getTotal(ArrayList<ExpenseLogEntry> filteredExpenseArray) {
        BigDecimal total = new BigDecimal(0);
        if (filteredExpenseArray != null) {
            if (!filteredExpenseArray.isEmpty()) {
                for (int i = 0; i < filteredExpenseArray.size(); i++) {
                    total = total.add(filteredExpenseArray.get(i).getAmount());
                }
            }
        }
        return total;
    }

    private void setTotalTV() {
        if(getActivity() != null) {
            totalAmountTV.setTextColor(getActivity().getResources().getColor(R.color.red));
        }
        if (total != null) {
            int moneyFormatCode = preferences.getInt("currency", DateAndCurrencyDisplayer.CURRENCY_US);
            totalAmountTV.setText(DateAndCurrencyDisplayer.getCurrencyToDisplay(moneyFormatCode, total));
        }
    }

    public void updateData(ArrayList<ExpenseLogEntry> expenseList) {
        expenseListAdapter.updateResults(expenseList);
        expenseListAdapter.getFilter().filter(searchBarET.getText());
        expenseListAdapter.notifyDataSetChanged();
    }

}
