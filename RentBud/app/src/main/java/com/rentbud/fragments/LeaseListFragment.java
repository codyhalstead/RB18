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
import android.support.v4.app.Fragment;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.cody.rentbud.R;
import com.rentbud.activities.ExpenseViewActivity;
import com.rentbud.activities.LeaseViewActivity;
import com.rentbud.activities.LeaseViewActivity2;
import com.rentbud.activities.MainActivity;
import com.rentbud.adapters.ExpenseListAdapter;
import com.rentbud.adapters.LeaseListAdapter;
import com.rentbud.helpers.ApartmentTenantViewModel;
import com.rentbud.helpers.MainViewModel;
import com.rentbud.model.ExpenseLogEntry;
import com.rentbud.model.Lease;
import com.rentbud.sqlite.DatabaseHandler;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Cody on 4/14/2018.
 */

public class LeaseListFragment extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener {
    TextView noLeasesTV;
    EditText searchBarET;
    LinearLayout totalBarLL;
    LeaseListAdapter leaseListAdapter;
    ColorStateList accentColor;
    ListView listView;
    Button dateRangeStartBtn, dateRangeEndBtn;
    //public static boolean leaseListAdapterNeedsRefreshed;
    Date filterDateStart, filterDateEnd;
    private DatePickerDialog.OnDateSetListener dateSetFilterStartListener, dateSetFilterEndListener;
    //private DatabaseHandler db;
    private OnDatesChangedListener mCallback;
    // private ArrayList<Lease> currentFilteredLeases;
    private boolean needsRefreshedOnResume;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_money_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.noLeasesTV = view.findViewById(R.id.moneyEmptyListTV);
        this.totalBarLL = view.findViewById(R.id.moneyListTotalBarLL);
        totalBarLL.setVisibility(View.GONE);
        this.searchBarET = view.findViewById(R.id.moneyListSearchET);
        this.dateRangeStartBtn = view.findViewById(R.id.moneyListDateRangeStartBtn);
        this.dateRangeStartBtn.setOnClickListener(this);
        this.dateRangeEndBtn = view.findViewById(R.id.moneyListDateRangeEndBtn);
        this.dateRangeEndBtn.setOnClickListener(this);
        this.listView = view.findViewById(R.id.mainMoneyListView);
        // this.db = new DatabaseHandler(getContext());
        //if(savedInstanceState != null) {
        //    if (savedInstanceState.getString("filterDateStart") != null) {
        //        SimpleDateFormat formatTo = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
        //        DateFormat formatFrom = new SimpleDateFormat("yyyy/MM/dd", Locale.US);
        //        try {
        //            Date startDate = formatFrom.parse(savedInstanceState.getString("filterDateStart"));
        //            this.filterDateStart = startDate;
        //             this.dateRangeStartBtn.setText(formatTo.format(startDate));
        //         } catch (ParseException e) {
        //            e.printStackTrace();
        //        }
        //   }
        //   if (savedInstanceState.getString("filterDateEnd") != null) {
        //       SimpleDateFormat formatTo = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
        //       DateFormat formatFrom = new SimpleDateFormat("yyyy/MM/dd", Locale.US);
        //       try {
        //           Date endDate = formatFrom.parse(savedInstanceState.getString("filterDateEnd"));
        //           this.filterDateEnd = endDate;
        //          this.dateRangeEndBtn.setText(formatTo.format(endDate));
        //      } catch (ParseException e) {
        //          e.printStackTrace();
        //      }
        //  }
        // if(savedInstanceState.getParcelableArrayList("filteredLeases") != null){
        //     this.currentFilteredLeases = savedInstanceState.getParcelableArrayList("filteredLeases");
        // } else {
        //     this.currentFilteredLeases = new ArrayList<>();
        // }
        //} else {
        // Date endDate = Calendar.getInstance().getTime();
        // Calendar calendar = Calendar.getInstance();
        // calendar.setTime(endDate);
        // calendar.add(Calendar.YEAR, -1);
        // Date startDate = calendar.getTime();

        //this.currentFilteredLeases = db.getUsersActiveLeasesWithinDates(MainActivity.user, startDate, endDate );
        //this.filterDateEnd = endDate;
        //this.filterDateStart = startDate;


        //}
        filterDateStart = ViewModelProviders.of(getActivity()).get(MainViewModel.class).getStartDateRangeDate().getValue();
        filterDateEnd = ViewModelProviders.of(getActivity()).get(MainViewModel.class).getEndDateRangeDate().getValue();
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
        dateRangeStartBtn.setText(formatter.format(filterDateStart));
        dateRangeEndBtn.setText(formatter.format(filterDateEnd));
        setUpdateSelectedDateListeners();
        getActivity().setTitle("Lease View");
        //LeaseListFragment.leaseListAdapterNeedsRefreshed = false;
        //Get current theme accent color, which is passed into the list adapter for search highlighting
        TypedValue colorValue = new TypedValue();
        getActivity().getTheme().resolveAttribute(R.attr.colorAccent, colorValue, true);
        this.accentColor = getActivity().getResources().getColorStateList(colorValue.resourceId);
        setUpListAdapter();
        setUpSearchBar();
        needsRefreshedOnResume = false;
    }

    @Override
    public void onResume() {
        super.onResume();
        //   if (LeaseListFragment.leaseListAdapterNeedsRefreshed) {
        //       searchBarET.setText("");
        //       if(this.leaseListAdapter != null){
        //          // this.currentFilteredLeases = db.getUsersActiveLeasesWithinDates(MainActivity.user, filterDateStart, filterDateEnd);
        //leaseListAdapter.updateResults(ViewModelProviders.of(getActivity()).get(MainViewModel.class).getCachedLeases().getValue());
        //           leaseListAdapterNeedsRefreshed = false;
        //           leaseListAdapter.getFilter().filter("");
        //       }
        //   }
        if (needsRefreshedOnResume) {
            leaseListAdapter.updateResults(ViewModelProviders.of(getActivity()).get(MainViewModel.class).getCachedLeases().getValue());
            searchBarET.setText(searchBarET.getText());
            searchBarET.setSelection(searchBarET.getText().length());
            // incomeListAdapter.getFilter().filter(searchBarET.getText());
            //expenseListAdapter.getFilter().filter("");
            //total = getTotal(ViewModelProviders.of(getActivity()).get(MainViewModel.class).getCachedIncome().getValue());
            //setTotalTV();
        }
        needsRefreshedOnResume = true;
    }

    private void setUpSearchBar() {
        searchBarET.addTextChangedListener(new TextWatcher() {
            //For updating search results as user types
            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                //When user changed the Text
                if (leaseListAdapter != null) {
                    leaseListAdapter.getFilter().filter(cs);

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
        leaseListAdapter = new LeaseListAdapter(getActivity(), ViewModelProviders.of(getActivity()).get(MainViewModel.class).getCachedLeases().getValue(), accentColor, null);
        listView.setAdapter(leaseListAdapter);
        listView.setOnItemClickListener(this);
        noLeasesTV.setText("No Leases To Display");
        this.listView.setEmptyView(noLeasesTV);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        //On listView row click, launch ApartmentViewActivity passing the rows data into it.
        Intent intent = new Intent(getContext(), LeaseViewActivity2.class);
        //Uses filtered results to match what is on screen
        Lease lease = leaseListAdapter.getFilteredResults().get(i);
        intent.putExtra("leaseID", lease.getId());
        getActivity().startActivityForResult(intent, MainActivity.REQUEST_LEASE_VIEW);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.moneyListDateRangeStartBtn:
                Calendar cal = Calendar.getInstance();
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
        void onLeaseListDatesChanged(Date dateStart, Date dateEnd, LeaseListFragment fragment);
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
                    + " must implement OnLeaseDatesChangedListener");
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
                //currentFilteredLeases = db.getUsersActiveLeasesWithinDates(MainActivity.user, filterDateStart, filterDateEnd);
                //if(ViewModelProviders.of(getActivity()).get(MainViewModel.class).getCachedLeases().getValue().isEmpty()){
                //    noLeasesTV.setVisibility(View.VISIBLE);
                //    noLeasesTV.setText("No Current Leases");
                //} else {
                //    noLeasesTV.setVisibility(View.GONE);
                //    noLeasesTV.setText("No Current Leases");
                //}
                SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
                dateRangeStartBtn.setText(formatter.format(filterDateStart));
                mCallback.onLeaseListDatesChanged(filterDateStart, filterDateEnd, LeaseListFragment.this);
                //leaseListAdapter.updateResults(currentFilteredLeases);
                //leaseListAdapter.getFilter().filter(searchBarET.getText());
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
                //currentFilteredLeases = db.getUsersActiveLeasesWithinDates(MainActivity.user, filterDateStart, filterDateEnd);
                //if(currentFilteredLeases.isEmpty()){
                //    noLeasesTV.setVisibility(View.VISIBLE);
                //    noLeasesTV.setText("No Current Leases");
                //} else {
                //    noLeasesTV.setVisibility(View.GONE);
                //    noLeasesTV.setText("No Current Leases");
                //}
                SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
                dateRangeEndBtn.setText(formatter.format(filterDateEnd));
                //leaseListAdapter.notifyDataSetChanged();
                //leaseListAdapter.updateResults(currentFilteredLeases);
                //leaseListAdapter.getFilter().filter(searchBarET.getText());
                mCallback.onLeaseListDatesChanged(filterDateStart, filterDateEnd, LeaseListFragment.this);
            }
        };
    }

    public void updateData(ArrayList<Lease> leaseList) {
        leaseListAdapter.updateResults(leaseList);
        leaseListAdapter.getFilter().filter(searchBarET.getText());
        leaseListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd", Locale.US);
        //if (filterDateStart != null) {
        //    outState.putString("filterDateStart", formatter.format(filterDateStart));
        // }
        // if (filterDateEnd != null) {
        //     outState.putString("filterDateEnd", formatter.format(filterDateEnd));
        //}
        //if (currentFilteredLeases != null) {
        //    outState.putParcelableArrayList("filteredLeases", currentFilteredLeases);
        //}
    }

}
