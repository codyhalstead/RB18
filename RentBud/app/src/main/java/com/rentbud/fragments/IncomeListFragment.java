package com.rentbud.fragments;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.rentbud.model.PaymentLogEntry;
import com.rentbud.sqlite.DatabaseHandler;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Cody on 3/23/2018.
 */

public class IncomeListFragment extends android.support.v4.app.Fragment implements AdapterView.OnItemClickListener, View.OnClickListener {
    TextView noIncomeTV;
    EditText searchBarET;
    Button dateRangeStartBtn, dateRangeEndBtn;
    IncomeListAdapter incomeListAdapter;
    ColorStateList accentColor;
    ListView listView;
    public static boolean incomeListAdapterNeedsRefreshed;
    Date filterDateStart, filterDateEnd;
    private DatePickerDialog.OnDateSetListener dateSetFilterStartListener, dateSetFilterEndListener;
    private DatabaseHandler db;
    private ArrayList<PaymentLogEntry> currentFilteredIncome;

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
        this.listView = view.findViewById(R.id.mainMoneyListView);
        this.db = new DatabaseHandler(getContext());
        if(savedInstanceState != null) {
            if (savedInstanceState.getString("filterDateStart") != null) {
                SimpleDateFormat formatTo = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
                DateFormat formatFrom = new SimpleDateFormat("yyyy/MM/dd", Locale.US);
                try {
                    Date startDate = formatFrom.parse(savedInstanceState.getString("filterDateStart"));
                    this.filterDateStart = startDate;
                    this.dateRangeStartBtn.setText(formatTo.format(startDate));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            if (savedInstanceState.getString("filterDateEnd") != null) {
                SimpleDateFormat formatTo = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
                DateFormat formatFrom = new SimpleDateFormat("yyyy/MM/dd", Locale.US);
                try {
                    Date endDate = formatFrom.parse(savedInstanceState.getString("filterDateEnd"));
                    this.filterDateEnd = endDate;
                    this.dateRangeEndBtn.setText(formatTo.format(endDate));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            if(savedInstanceState.getParcelableArrayList("filteredIncome") != null){
                this.currentFilteredIncome = savedInstanceState.getParcelableArrayList("filteredIncome");
            } else {
                this.currentFilteredIncome = new ArrayList<>();
            }
        } else {
            Date endDate = Calendar.getInstance().getTime();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(endDate);
            calendar.add(Calendar.YEAR, -1);
            Date startDate = calendar.getTime();

            this.currentFilteredIncome = db.getUsersIncomeWithinDates(MainActivity.user, startDate, endDate);
            this.filterDateEnd = endDate;
            this.filterDateStart = startDate;
            SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
            dateRangeStartBtn.setText(formatter.format(filterDateStart));
            dateRangeEndBtn.setText(formatter.format(filterDateEnd));

        }
        setUpdateSelectedDateListeners();
        getActivity().setTitle("Income View");
        ExpenseListFragment.expenseListAdapterNeedsRefreshed = false;
        //Get current theme accent color, which is passed into the list adapter for search highlighting
        TypedValue colorValue = new TypedValue();
        getActivity().getTheme().resolveAttribute(R.attr.colorAccent, colorValue, true);
        this.accentColor = getActivity().getResources().getColorStateList(colorValue.resourceId);
        setUpListAdapter();
        setUpSearchBar();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (IncomeListFragment.incomeListAdapterNeedsRefreshed) {
            //searchBarET.setText("");
            if(this.incomeListAdapter != null){
                this.currentFilteredIncome = db.getUsersIncomeWithinDates(MainActivity.user, filterDateStart, filterDateEnd);
                if(currentFilteredIncome.isEmpty()){
                    noIncomeTV.setVisibility(View.VISIBLE);
                    noIncomeTV.setText("No Current Income");
                } else {
                    noIncomeTV.setVisibility(View.GONE);
                }

                incomeListAdapter.updateResults(currentFilteredIncome);
                incomeListAdapterNeedsRefreshed = false;
                //incomeListAdapter.getFilter().filter("");
                searchBarET.setText(searchBarET.getText());
                searchBarET.setSelection(searchBarET.getText().length());
            }
        }
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
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {

            }
        });
    }

    private void setUpListAdapter() {
        if (currentFilteredIncome != null) {
            incomeListAdapter = new IncomeListAdapter(getActivity(), currentFilteredIncome, accentColor);
            listView.setAdapter(incomeListAdapter);
            listView.setOnItemClickListener(this);
            if (currentFilteredIncome.isEmpty()) {
                noIncomeTV.setVisibility(View.VISIBLE);
                noIncomeTV.setText("No Current Income");
            }
        } else {
            //If MainActivity5.expenseList is null show empty list text
            noIncomeTV.setVisibility(View.VISIBLE);
            noIncomeTV.setText("Error Loading Income");
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        //On listView row click, launch IncomeViewActivity passing the rows data into it.
        Intent intent = new Intent(getContext(), IncomeViewActivity.class);
        //Uses filtered results to match what is on screen
        PaymentLogEntry income = incomeListAdapter.getFilteredResults().get(i);
        intent.putExtra("incomeID", income.getId());
        startActivity(intent);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.moneyListDateRangeStartBtn:
                Calendar cal = Calendar.getInstance();
                if(filterDateStart != null) {
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
                if(filterDateEnd != null) {
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
                currentFilteredIncome = db.getUsersIncomeWithinDates(MainActivity.user, filterDateStart, filterDateEnd);
                if(currentFilteredIncome.isEmpty()){
                    noIncomeTV.setVisibility(View.VISIBLE);
                    noIncomeTV.setText("No Current Income");
                } else {
                    noIncomeTV.setVisibility(View.GONE);
                    noIncomeTV.setText("No Current Income");
                }
                SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
                dateRangeStartBtn.setText(formatter.format(filterDateStart));
                incomeListAdapter.updateResults(currentFilteredIncome);
                incomeListAdapter.getFilter().filter(searchBarET.getText());
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
                currentFilteredIncome = db.getUsersIncomeWithinDates(MainActivity.user, filterDateStart, filterDateEnd);
                if(currentFilteredIncome.isEmpty()){
                    noIncomeTV.setVisibility(View.VISIBLE);
                    noIncomeTV.setText("No Current Income");
                } else {
                    noIncomeTV.setVisibility(View.GONE);
                    noIncomeTV.setText("No Current Income");
                }
                SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
                dateRangeEndBtn.setText(formatter.format(filterDateEnd));
                incomeListAdapter.updateResults(currentFilteredIncome);
                incomeListAdapter.getFilter().filter(searchBarET.getText());
            }
        };
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd", Locale.US);
        if (filterDateStart != null) {
            outState.putString("filterDateStart", formatter.format(filterDateStart));
        }
        if (filterDateEnd != null) {
            outState.putString("filterDateEnd", formatter.format(filterDateEnd));
        }
        if (currentFilteredIncome != null) {
            outState.putParcelableArrayList("filteredIncome", currentFilteredIncome);
        }
    }


}