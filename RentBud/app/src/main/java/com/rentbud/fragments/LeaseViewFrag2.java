package com.rentbud.fragments;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import com.rentbud.model.PaymentLogEntry;
import com.rentbud.sqlite.DatabaseHandler;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static android.support.constraint.Constraints.TAG;

public class LeaseViewFrag2 extends android.support.v4.app.Fragment implements AdapterView.OnItemClickListener, View.OnClickListener {


    public LeaseViewFrag2() {
        // Required empty public constructor
    }

    TextView noIncomeTV;
    //  EditText searchBarET;
    //  Button dateRangeStartBtn, dateRangeEndBtn;
    IncomeListAdapter incomeListAdapter;
    ColorStateList accentColor;
    ListView listView;
    //public static boolean incomeListAdapterNeedsRefreshed;
    //  Date filterDateStart, filterDateEnd;
    //  private DatePickerDialog.OnDateSetListener dateSetFilterStartListener, dateSetFilterEndListener;
    private DatabaseHandler db;
    private ArrayList<PaymentLogEntry> currentFilteredIncome;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.lease_view_fragment_two, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //this.noIncomeTV = view.findViewById(R.id.moneyEmptyListTV);

        this.listView = view.findViewById(R.id.mainMoneyListView);
        this.db = new DatabaseHandler(getContext());
        if (savedInstanceState != null) {


        } else {
            Date endDate = Calendar.getInstance().getTime();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(endDate);
            calendar.add(Calendar.YEAR, -1);
            Date startDate = calendar.getTime();


        }
        Bundle bundle = getArguments();
        //Get apartment item
        int leaseID = bundle.getInt("leaseID");
        setUpdateSelectedDateListeners();
       // getActivity().setTitle("Income View");
       // ExpenseListFragment.expenseListAdapterNeedsRefreshed = false;
        //Get current theme accent color, which is passed into the list adapter for search highlighting
        TypedValue colorValue = new TypedValue();
        getActivity().getTheme().resolveAttribute(R.attr.colorAccent, colorValue, true);
        this.accentColor = getActivity().getResources().getColorStateList(colorValue.resourceId);
        currentFilteredIncome = db.getUsersIncomeByLeaseID(MainActivity.user, leaseID);
        setUpListAdapter();
        setUpSearchBar();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (IncomeListFragment.incomeListAdapterNeedsRefreshed) {
            if (this.incomeListAdapter != null) {
             //   incomeListAdapterNeedsRefreshed = false;
                incomeListAdapter.getFilter().filter("");
            }
        }
    }

    private void setUpSearchBar() {

    }

    private void setUpListAdapter() {
        if (currentFilteredIncome != null) {
            incomeListAdapter = new IncomeListAdapter(getActivity(), currentFilteredIncome, accentColor);
            listView.setAdapter(incomeListAdapter);
            listView.setOnItemClickListener(this);
            if (currentFilteredIncome.isEmpty()) {
           //     noIncomeTV.setVisibility(View.VISIBLE);
            //    noIncomeTV.setText("No Current Income");
            }
        } else {
            //If MainActivity5.expenseList is null show empty list text
          //  noIncomeTV.setVisibility(View.VISIBLE);
          //  noIncomeTV.setText("Error Loading Income");
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        //On listView row click, launch IncomeViewActivity passing the rows data into it.
        //Intent intent = new Intent(getContext(), IncomeViewActivity.class);
        //Uses filtered results to match what is on screen
        //PaymentLogEntry income = incomeListAdapter.getFilteredResults().get(i);
        //intent.putExtra("incomeID", income.getId());
        //startActivity(intent);
    }


    @Override
    public void onClick(View view) {

    }

    private void setUpdateSelectedDateListeners() {

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd", Locale.US);

        if (currentFilteredIncome != null) {
            outState.putParcelableArrayList("filteredIncome", currentFilteredIncome);
        }
    }
}



