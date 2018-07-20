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
import com.rentbud.activities.IncomeViewActivity;
import com.rentbud.activities.MainActivity;
import com.rentbud.adapters.IncomeListAdapter;
import com.rentbud.helpers.MainViewModel;
import com.rentbud.model.PaymentLogEntry;

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

public class IncomeListFragment extends android.support.v4.app.Fragment implements AdapterView.OnItemClickListener, View.OnClickListener {
    TextView noIncomeTV, totalAmountTV, totalAmountLabelTV;
    EditText searchBarET;
    Button dateRangeStartBtn, dateRangeEndBtn;
    IncomeListAdapter incomeListAdapter;
    ColorStateList accentColor;
    ListView listView;
    //public static boolean incomeListAdapterNeedsRefreshed;
    Date filterDateStart, filterDateEnd;
    private DatePickerDialog.OnDateSetListener dateSetFilterStartListener, dateSetFilterEndListener;
    //private DatabaseHandler db;
    //private ArrayList<PaymentLogEntry> currentFilteredIncome;
    private BigDecimal total;
    private OnDatesChangedListener mCallback;
    private boolean needsRefreshedOnResume;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_money_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.noIncomeTV = view.findViewById(R.id.moneyEmptyListTV);
        this.searchBarET = view.findViewById(R.id.moneyListSearchET);
        this.dateRangeStartBtn = view.findViewById(R.id.moneyListDateRangeStartBtn);
        this.dateRangeStartBtn.setOnClickListener(this);
        this.dateRangeEndBtn = view.findViewById(R.id.moneyListDateRangeEndBtn);
        this.dateRangeEndBtn.setOnClickListener(this);
        this.totalAmountLabelTV = view.findViewById(R.id.moneyListTotalAmountLabelTV);
        this.totalAmountTV = view.findViewById(R.id.moneyListTotalAmountTV);
        this.listView = view.findViewById(R.id.mainMoneyListView);
        // this.db = new DatabaseHandler(getContext());

        this.filterDateEnd = ViewModelProviders.of(getActivity()).get(MainViewModel.class).getEndDateRangeDate().getValue();
        this.filterDateStart = ViewModelProviders.of(getActivity()).get(MainViewModel.class).getStartDateRangeDate().getValue();
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
        dateRangeStartBtn.setText(formatter.format(filterDateStart));
        dateRangeEndBtn.setText(formatter.format(filterDateEnd));
        total = getTotal(ViewModelProviders.of(getActivity()).get(MainViewModel.class).getCachedIncome().getValue());
        setUpdateSelectedDateListeners();
        getActivity().setTitle("Income View");
        //ExpenseListFragment.expenseListAdapterNeedsRefreshed = false;
        //Get current theme accent color, which is passed into the list adapter for search highlighting
        TypedValue colorValue = new TypedValue();
        getActivity().getTheme().resolveAttribute(R.attr.colorAccent, colorValue, true);
        this.accentColor = getActivity().getResources().getColorStateList(colorValue.resourceId);
        setUpListAdapter();
        setUpSearchBar();
        setTotalTV();
        incomeListAdapter.setOnDataChangeListener(new IncomeListAdapter.OnDataChangeListener() {
            public void onDataChanged(ArrayList<PaymentLogEntry> filteredResults) {
                total = getTotal(filteredResults);
                setTotalTV();
            }
        });
        needsRefreshedOnResume = false;
    }

    @Override
    public void onResume() {
        super.onResume();
        //  if (IncomeListFragment.incomeListAdapterNeedsRefreshed) {
        //      //searchBarET.setText("");
        //      if(this.incomeListAdapter != null){
        //          if(ViewModelProviders.of(getActivity()).get(MainViewModel.class).getCachedIncome().getValue().isEmpty()){
        //              noIncomeTV.setVisibility(View.VISIBLE);
        //              noIncomeTV.setText("No Current Income");
        //          } else {
        //              noIncomeTV.setVisibility(View.GONE);
        //          }
        if (needsRefreshedOnResume) {
            incomeListAdapter.updateResults(ViewModelProviders.of(getActivity()).get(MainViewModel.class).getCachedIncome().getValue());
            searchBarET.setText(searchBarET.getText());
            searchBarET.setSelection(searchBarET.getText().length());
           // incomeListAdapter.getFilter().filter(searchBarET.getText());
            //expenseListAdapter.getFilter().filter("");
            //total = getTotal(ViewModelProviders.of(getActivity()).get(MainViewModel.class).getCachedIncome().getValue());
            //setTotalTV();
        }
        needsRefreshedOnResume = true;
        //        incomeListAdapter.updateResults(ViewModelProviders.of(getActivity()).get(MainViewModel.class).getCachedIncome().getValue());
        //        incomeListAdapterNeedsRefreshed = false;
        //moneyListAdapter.getFilter().filter("");
        //        searchBarET.setText(searchBarET.getText());
        //        searchBarET.setSelection(searchBarET.getText().length());
        //  }
        //}
    }

    private void setUpSearchBar() {
        searchBarET.addTextChangedListener(new TextWatcher() {
            //For updating search results as user types
            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                //When user changed the Text
                if (incomeListAdapter != null) {
                    incomeListAdapter.getFilter().filter(cs);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                //apartmentListAdapter.notifyDataSetChanged();
                //moneyListAdapter.notifyDataSetChanged();
                //total = getTotal(moneyListAdapter.getFilteredResults());
                //setTotalTV();
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {

            }
        });
    }


    private void setUpListAdapter() {
        incomeListAdapter = new IncomeListAdapter(getActivity(), ViewModelProviders.of(getActivity()).get(MainViewModel.class).getCachedIncome().getValue(), accentColor);
        listView.setAdapter(incomeListAdapter);
        listView.setOnItemClickListener(this);
        noIncomeTV.setText("No Income To Display");
        this.listView.setEmptyView(noIncomeTV);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        //On listView row click, launch IncomeViewActivity passing the rows data into it.
        Intent intent = new Intent(getContext(), IncomeViewActivity.class);
        //Uses filtered results to match what is on screen
        PaymentLogEntry income = incomeListAdapter.getFilteredResults().get(i);
        intent.putExtra("incomeID", income.getId());
        getActivity().startActivityForResult(intent, MainActivity.REQUEST_INCOME_VIEW);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.moneyListDateRangeStartBtn:
                Calendar cal = Calendar.getInstance();
                //needsRefreshedOnResume = false;
                if (filterDateStart != null) {
                    cal.setTime(filterDateStart);
                }
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dialog = new DatePickerDialog(this.getContext(), android.R.style.Theme_Holo_Light_Dialog_MinWidth, dateSetFilterStartListener, year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
                break;

            case R.id.moneyListDateRangeEndBtn:
                Calendar cal2 = Calendar.getInstance();
                //needsRefreshedOnResume = false;
                if (filterDateEnd != null) {
                    cal2.setTime(filterDateEnd);
                }
                int year2 = cal2.get(Calendar.YEAR);
                int month2 = cal2.get(Calendar.MONTH);
                int day2 = cal2.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dialog2 = new DatePickerDialog(this.getContext(), android.R.style.Theme_Holo_Light_Dialog_MinWidth, dateSetFilterEndListener, year2, month2, day2);
                dialog2.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog2.show();
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

    private void setUpdateSelectedDateListeners() {
        dateSetFilterStartListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                //Once user selects date from date picker pop-up,
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.YEAR, year);
                cal.set(Calendar.MONTH, month);
                cal.set(Calendar.DATE, day);
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
                filterDateStart = cal.getTime();
                //currentFilteredIncome = db.getUsersIncomeWithinDates(MainActivity.user, filterDateStart, filterDateEnd);
                //if(currentFilteredIncome.isEmpty()){
                //    noIncomeTV.setVisibility(View.VISIBLE);
                //    noIncomeTV.setText("No Current Income");
                //} else {
                //    noIncomeTV.setVisibility(View.GONE);
                //    noIncomeTV.setText("No Current Income");
                // }
                SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
                dateRangeStartBtn.setText(formatter.format(filterDateStart));
                mCallback.onIncomeListDatesChanged(filterDateStart, filterDateEnd, IncomeListFragment.this);
                //incomeListAdapter.updateResults(currentFilteredIncome);
                //incomeListAdapter.getFilter().filter(searchBarET.getText());
                //total = getTotal(moneyListAdapter.getFilteredResults());
                //setTotalTV();
            }
        };
        dateSetFilterEndListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                //Once user selects date from date picker pop-up,
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.YEAR, year);
                cal.set(Calendar.MONTH, month);
                cal.set(Calendar.DATE, day);
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
                filterDateEnd = cal.getTime();
                //currentFilteredIncome = db.getUsersIncomeWithinDates(MainActivity.user, filterDateStart, filterDateEnd);
                //if(currentFilteredIncome.isEmpty()){
                //    noIncomeTV.setVisibility(View.VISIBLE);
                //    noIncomeTV.setText("No Current Income");
                //} else {
                //    noIncomeTV.setVisibility(View.GONE);
                //    noIncomeTV.setText("No Current Income");
                //}
                SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
                dateRangeEndBtn.setText(formatter.format(filterDateEnd));
                mCallback.onIncomeListDatesChanged(filterDateStart, filterDateEnd, IncomeListFragment.this);
                //incomeListAdapter.updateResults(currentFilteredIncome);
                //incomeListAdapter.getFilter().filter(searchBarET.getText());
                // total = getTotal(moneyListAdapter.getFilteredResults());
                //setTotalTV();
            }
        };
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    private BigDecimal getTotal(ArrayList<PaymentLogEntry> filteredIncomeArray) {
        BigDecimal total = new BigDecimal(0);
        if (filteredIncomeArray != null) {
            if (!filteredIncomeArray.isEmpty()) {
                for (int i = 0; i < filteredIncomeArray.size(); i++) {
                    total = total.add(filteredIncomeArray.get(i).getAmount());
                }
            }
        }
        return total;
    }

    private void setTotalTV() {
        totalAmountTV.setTextColor(getActivity().getResources().getColor(R.color.green_colorPrimaryDark));
        if (total != null) {
            BigDecimal displayVal = total.setScale(2, RoundingMode.HALF_EVEN);
            NumberFormat usdCostFormat = NumberFormat.getCurrencyInstance(Locale.US);
            usdCostFormat.setMinimumFractionDigits(2);
            usdCostFormat.setMaximumFractionDigits(2);
            totalAmountTV.setText(usdCostFormat.format(displayVal.doubleValue()));
        }
    }

    public void updateData(ArrayList<PaymentLogEntry> incomeList) {
        incomeListAdapter.updateResults(incomeList);
        incomeListAdapter.getFilter().filter(searchBarET.getText());
        incomeListAdapter.notifyDataSetChanged();
    }

}