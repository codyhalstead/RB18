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
import android.widget.TextView;

import com.example.cody.rentbud.R;
import com.rentbud.activities.MainActivity;
import com.rentbud.adapters.IncomeListAdapter;
import com.rentbud.adapters.TotalsListAdapter;
import com.rentbud.helpers.CustomDatePickerDialogLauncher;
import com.rentbud.helpers.MainArrayDataMethods;
import com.rentbud.helpers.MainViewModel;
import com.rentbud.model.PaymentLogEntry;
import com.rentbud.model.TypeTotal;
import com.rentbud.sqlite.DatabaseHandler;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TreeMap;

public class TotalsFragment extends android.support.v4.app.Fragment implements AdapterView.OnItemClickListener, View.OnClickListener {
    ArrayList<TypeTotal> typeTotals;
    TextView noTotalsTV;
    DatabaseHandler db;
    Date filterDateStart, filterDateEnd;
    Button dateRangeStartBtn, dateRangeEndBtn;
    TotalsListAdapter totalsListAdapter;
    ColorStateList accentColor;
    ListView listView;
    MainArrayDataMethods dataMethods;
    private OnDatesChangedListener mCallback;
    private CustomDatePickerDialogLauncher datePickerDialogLauncher;

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
            if (savedInstanceState.getParcelableArrayList("filteredTotals") != null) {
                this.typeTotals = savedInstanceState.getParcelableArrayList("filteredTotals");
            } else {
                this.typeTotals = new ArrayList<>();
            }
        } else {
            typeTotals = new ArrayList<>();
            typeTotals.addAll(db.getTotalForExpenseTypesWithinDates(MainActivity.user, filterDateStart, filterDateEnd));
            typeTotals.addAll(db.getTotalForIncomeTypesWithinDates(MainActivity.user, filterDateStart, filterDateEnd));
            dataMethods.sortTypeTotalsArrayByTotalAmountDesc(typeTotals);
        }
        TypedValue colorValue = new TypedValue();
        getActivity().getTheme().resolveAttribute(R.attr.colorAccent, colorValue, true);
        this.accentColor = getActivity().getResources().getColorStateList(colorValue.resourceId);
        setUpListAdapter();
        datePickerDialogLauncher = new CustomDatePickerDialogLauncher(filterDateStart, filterDateEnd, true, getContext());
        datePickerDialogLauncher.setDateSelectedListener(new CustomDatePickerDialogLauncher.DateSelectedListener() {
            @Override
            public void onStartDateSelected(Date startDate, Date endDate) {
                filterDateStart = startDate;
                filterDateEnd = endDate;
                SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
                dateRangeEndBtn.setText(formatter.format(filterDateEnd));
                dateRangeStartBtn.setText(formatter.format(filterDateStart));
                typeTotals.clear();
                typeTotals.addAll(db.getTotalForExpenseTypesWithinDates(MainActivity.user, filterDateStart, filterDateEnd));
                typeTotals.addAll(db.getTotalForIncomeTypesWithinDates(MainActivity.user, filterDateStart, filterDateEnd));
                dataMethods.sortTypeTotalsArrayByTotalAmountDesc(typeTotals);
                mCallback.onTotalsListDatesChanged(filterDateStart, filterDateEnd, TotalsFragment.this);
            }

            @Override
            public void onEndDateSelected(Date startDate, Date endDate) {
                filterDateStart = startDate;
                filterDateEnd = endDate;
                SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
                dateRangeEndBtn.setText(formatter.format(filterDateEnd));
                dateRangeStartBtn.setText(formatter.format(filterDateStart));
                typeTotals.clear();
                typeTotals.addAll(db.getTotalForExpenseTypesWithinDates(MainActivity.user, filterDateStart, filterDateEnd));
                typeTotals.addAll(db.getTotalForIncomeTypesWithinDates(MainActivity.user, filterDateStart, filterDateEnd));
                dataMethods.sortTypeTotalsArrayByTotalAmountDesc(typeTotals);
                mCallback.onTotalsListDatesChanged(filterDateStart, filterDateEnd, TotalsFragment.this);
            }

            @Override
            public void onDateSelected(Date date) {

            }
        });
        getActivity().setTitle(R.string.totals_view);
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


    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.totalsDateRangeStartBtn:
                datePickerDialogLauncher.launchStartDatePickerDialog();
                break;

            case R.id.totalsDateRangeEndBtn:
                datePickerDialogLauncher.launchEndDatePickerDialog();
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
    }
}
