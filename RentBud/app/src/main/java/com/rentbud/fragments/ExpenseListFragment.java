package com.rentbud.fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.cody.rentbud.R;
import com.rentbud.activities.ExpenseViewActivity;
import com.rentbud.activities.MainActivity;
import com.rentbud.adapters.ExpenseListAdapter;
import com.rentbud.helpers.CustomDatePickerDialogLauncher;
import com.rentbud.helpers.MainViewModel;
import com.rentbud.model.ExpenseLogEntry;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

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
    private CustomDatePickerDialogLauncher datePickerDialogLauncher;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_money_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.noExpensesTV = view.findViewById(R.id.moneyEmptyListTV);
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
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
        dateRangeStartBtn.setText(formatter.format(filterDateStart));
        dateRangeEndBtn.setText(formatter.format(filterDateEnd));
        total = getTotal(ViewModelProviders.of(getActivity()).get(MainViewModel.class).getCachedExpenses().getValue());
        datePickerDialogLauncher = new CustomDatePickerDialogLauncher(filterDateStart, filterDateEnd, true, getContext());
        datePickerDialogLauncher.setDateSelectedListener(new CustomDatePickerDialogLauncher.DateSelectedListener() {
            @Override
            public void onStartDateSelected(Date startDate, Date endDate) {
                filterDateStart = startDate;
                filterDateEnd = endDate;
                SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
                dateRangeEndBtn.setText(formatter.format(filterDateEnd));
                dateRangeStartBtn.setText(formatter.format(filterDateStart));
                mCallback.onExpenseListDatesChanged(filterDateStart, filterDateEnd, ExpenseListFragment.this);
            }

            @Override
            public void onEndDateSelected(Date startDate, Date endDate) {
                filterDateStart = startDate;
                filterDateEnd = endDate;
                SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
                dateRangeEndBtn.setText(formatter.format(filterDateEnd));
                dateRangeStartBtn.setText(formatter.format(filterDateStart));
                mCallback.onExpenseListDatesChanged(filterDateStart, filterDateEnd, ExpenseListFragment.this);
            }

            @Override
            public void onDateSelected(Date date) {

            }
        });
        getActivity().setTitle(R.string.expense_view);
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
        totalAmountTV.setTextColor(getActivity().getResources().getColor(R.color.red));
        if (total != null) {
            BigDecimal displayVal = total.setScale(2, RoundingMode.HALF_EVEN);
            NumberFormat usdCostFormat = NumberFormat.getCurrencyInstance(Locale.US);
            usdCostFormat.setMinimumFractionDigits(2);
            usdCostFormat.setMaximumFractionDigits(2);
            totalAmountTV.setText(usdCostFormat.format(displayVal.doubleValue()));
        }
    }

    public void updateData(ArrayList<ExpenseLogEntry> expenseList) {
        expenseListAdapter.updateResults(expenseList);
        expenseListAdapter.getFilter().filter(searchBarET.getText());
        expenseListAdapter.notifyDataSetChanged();
    }

}
