package com.rentbud.fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.res.ColorStateList;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;

import com.example.cody.rentbud.R;
import com.rentbud.activities.MainActivity;
import com.rentbud.adapters.IncomeListAdapter;
import com.rentbud.adapters.TotalsListAdapter;
import com.rentbud.helpers.MainArrayDataMethods;
import com.rentbud.helpers.MainViewModel;
import com.rentbud.model.TypeTotal;
import com.rentbud.sqlite.DatabaseHandler;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TotalsFragment extends android.support.v4.app.Fragment implements AdapterView.OnItemClickListener, View.OnClickListener {
    ArrayList<TypeTotal> typeTotals;
    DatabaseHandler db;
    Date filterDateStart, filterDateEnd;
    Button dateRangeStartBtn, dateRangeEndBtn;
    TotalsListAdapter totalsListAdapter;
    ColorStateList accentColor;
    ListView listView;
    MainArrayDataMethods dataMethods;
    private DatePickerDialog.OnDateSetListener dateSetFilterStartListener, dateSetFilterEndListener;
    private OnDatesChangedListener mCallback;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_totals, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.dateRangeStartBtn = view.findViewById(R.id.totalsDateRangeStartBtn);
        this.dateRangeStartBtn.setOnClickListener(this);
        this.dateRangeEndBtn = view.findViewById(R.id.totalsDateRangeEndBtn);
        this.dateRangeEndBtn.setOnClickListener(this);
        this.listView = view.findViewById(R.id.totalsListView);
        db = new DatabaseHandler(getActivity());
        dataMethods = new MainArrayDataMethods();
        this.filterDateEnd = ViewModelProviders.of(getActivity()).get(MainViewModel.class).getEndDateRangeDate().getValue();
        this.filterDateStart = ViewModelProviders.of(getActivity()).get(MainViewModel.class).getStartDateRangeDate().getValue();
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
        dateRangeStartBtn.setText(formatter.format(filterDateStart));
        dateRangeEndBtn.setText(formatter.format(filterDateEnd));
        if (savedInstanceState != null) {
            if(savedInstanceState.getParcelableArrayList("filteredTotals") != null){
                this.typeTotals = savedInstanceState.getParcelableArrayList("filteredTotals");
            } else {
                this.typeTotals = new ArrayList<>();
            }
        } else {
            typeTotals = new ArrayList<>();
            typeTotals.addAll(db.getTotalForExpenseTypesWithinDates(MainActivity.user, MainActivity.expenseTypeLabels, filterDateStart, filterDateEnd));
            typeTotals.addAll(db.getTotalForIncomeTypesWithinDates(MainActivity.user, MainActivity.incomeTypeLabels, filterDateStart, filterDateEnd));
            dataMethods.sortTypeTotalsArrayByTotalAmountDesc(typeTotals);
        }
        TypedValue colorValue = new TypedValue();
        getActivity().getTheme().resolveAttribute(R.attr.colorAccent, colorValue, true);
        this.accentColor = getActivity().getResources().getColorStateList(colorValue.resourceId);
        setUpListAdapter();
        setUpdateSelectedDateListeners();
        getActivity().setTitle("Totals");
    }

    private void setUpListAdapter() {
        if (typeTotals != null) {
            totalsListAdapter = new TotalsListAdapter(getActivity(), typeTotals, accentColor);
            listView.setAdapter(totalsListAdapter);
            listView.setOnItemClickListener(this);
            if (typeTotals.isEmpty()) {
                //noIncomeTV.setVisibility(View.VISIBLE);
                //noIncomeTV.setText("No Current Income");
            }
        } else {
            //If MainActivity5.expenseList is null show empty list text
            //noIncomeTV.setVisibility(View.VISIBLE);
            //noIncomeTV.setText("Error Loading Income");
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        switch (view.getId()) {

        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.totalsDateRangeStartBtn:
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

            case R.id.totalsDateRangeEndBtn:
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
                typeTotals.clear();
                typeTotals.addAll(db.getTotalForExpenseTypesWithinDates(MainActivity.user, MainActivity.expenseTypeLabels, filterDateStart, filterDateEnd));
                typeTotals.addAll(db.getTotalForIncomeTypesWithinDates(MainActivity.user, MainActivity.incomeTypeLabels, filterDateStart, filterDateEnd));
                dataMethods.sortTypeTotalsArrayByTotalAmountDesc(typeTotals);
                SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
                dateRangeStartBtn.setText(formatter.format(filterDateStart));
                mCallback.onTotalsListDatesChanged(filterDateStart, filterDateEnd, TotalsFragment.this);
                //totalsListAdapter.updateResults(typeTotals);
                totalsListAdapter.notifyDataSetChanged();
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
                typeTotals.clear();
                typeTotals.addAll(db.getTotalForExpenseTypesWithinDates(MainActivity.user, MainActivity.expenseTypeLabels, filterDateStart, filterDateEnd));
                typeTotals.addAll(db.getTotalForIncomeTypesWithinDates(MainActivity.user, MainActivity.incomeTypeLabels, filterDateStart, filterDateEnd));
                dataMethods.sortTypeTotalsArrayByTotalAmountDesc(typeTotals);
                SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
                dateRangeEndBtn.setText(formatter.format(filterDateEnd));
                mCallback.onTotalsListDatesChanged(filterDateStart, filterDateEnd, TotalsFragment.this);
                //totalsListAdapter.updateResults(typeTotals);
                totalsListAdapter.notifyDataSetChanged();
            }
        };
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (typeTotals != null) {
            outState.putParcelableArrayList("filteredTotals", typeTotals);
        }
    }

}
