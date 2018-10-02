package com.RB18.fragments;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.cody.rentbud.R;
import com.RB18.activities.LeaseViewActivity;
import com.RB18.activities.MainActivity;
import com.RB18.adapters.LeaseListAdapter;
import com.RB18.helpers.CustomDatePickerDialogLauncher;
import com.RB18.helpers.DateAndCurrencyDisplayer;
import com.RB18.helpers.MainViewModel;
import com.RB18.model.Lease;

import java.util.ArrayList;
import java.util.Date;

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
    Date filterDateStart, filterDateEnd;
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
        this.noLeasesTV = view.findViewById(R.id.emptyListTV);
        this.totalBarLL = view.findViewById(R.id.moneyListTotalBarLL);
        totalBarLL.setVisibility(View.GONE);
        this.searchBarET = view.findViewById(R.id.moneyListSearchET);
        this.dateRangeStartBtn = view.findViewById(R.id.moneyListDateRangeStartBtn);
        this.dateRangeStartBtn.setOnClickListener(this);
        this.dateRangeEndBtn = view.findViewById(R.id.moneyListDateRangeEndBtn);
        this.dateRangeEndBtn.setOnClickListener(this);
        this.listView = view.findViewById(R.id.mainMoneyListView);
        filterDateStart = ViewModelProviders.of(getActivity()).get(MainViewModel.class).getStartDateRangeDate().getValue();
        filterDateEnd = ViewModelProviders.of(getActivity()).get(MainViewModel.class).getEndDateRangeDate().getValue();
        this.preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        int dateFormatCode = preferences.getInt("dateFormat", DateAndCurrencyDisplayer.DATE_MMDDYYYY);
        dateRangeStartBtn.setText(DateAndCurrencyDisplayer.getDateToDisplay(dateFormatCode, filterDateStart));
        dateRangeEndBtn.setText(DateAndCurrencyDisplayer.getDateToDisplay(dateFormatCode, filterDateEnd));
        getActivity().setTitle(R.string.lease_list);
        //Get current theme accent color, which is passed into the list adapter for search highlighting
        TypedValue colorValue = new TypedValue();
        getActivity().getTheme().resolveAttribute(R.attr.colorAccent, colorValue, true);
        this.accentColor = getActivity().getResources().getColorStateList(colorValue.resourceId);
        setUpListAdapter();
        setUpSearchBar();
        needsRefreshedOnResume = false;
        datePickerDialogLauncher = new CustomDatePickerDialogLauncher(filterDateStart, filterDateEnd, true, getContext());
        datePickerDialogLauncher.setDateSelectedListener(new CustomDatePickerDialogLauncher.DateSelectedListener() {
            @Override
            public void onStartDateSelected(Date startDate, Date endDate) {
                filterDateStart = startDate;
                filterDateEnd = endDate;
                int dateFormatCode = preferences.getInt("dateFormat", DateAndCurrencyDisplayer.DATE_MMDDYYYY);
                dateRangeStartBtn.setText(DateAndCurrencyDisplayer.getDateToDisplay(dateFormatCode, filterDateStart));
                dateRangeEndBtn.setText(DateAndCurrencyDisplayer.getDateToDisplay(dateFormatCode, filterDateEnd));
                mCallback.onLeaseListDatesChanged(filterDateStart, filterDateEnd, LeaseListFragment.this);
            }

            @Override
            public void onEndDateSelected(Date startDate, Date endDate) {
                filterDateStart = startDate;
                filterDateEnd = endDate;
                int dateFormatCode = preferences.getInt("dateFormat", DateAndCurrencyDisplayer.DATE_MMDDYYYY);
                dateRangeStartBtn.setText(DateAndCurrencyDisplayer.getDateToDisplay(dateFormatCode, filterDateStart));
                dateRangeEndBtn.setText(DateAndCurrencyDisplayer.getDateToDisplay(dateFormatCode, filterDateEnd));
                mCallback.onLeaseListDatesChanged(filterDateStart, filterDateEnd, LeaseListFragment.this);
            }

            @Override
            public void onDateSelected(Date date) {

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (needsRefreshedOnResume) {
            leaseListAdapter.updateResults(ViewModelProviders.of(getActivity()).get(MainViewModel.class).getCachedLeases().getValue());
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
                if (leaseListAdapter != null) {
                    leaseListAdapter.getFilter().filter(cs);

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
        leaseListAdapter = new LeaseListAdapter(getActivity(), ViewModelProviders.of(getActivity()).get(MainViewModel.class).getCachedLeases().getValue(), accentColor, null);
        listView.setAdapter(leaseListAdapter);
        listView.setOnItemClickListener(this);
        noLeasesTV.setText(R.string.no_leases_to_display);
        this.listView.setEmptyView(noLeasesTV);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        //On listView row click, launch ApartmentViewActivity passing the rows data into it.
        Intent intent = new Intent(getContext(), LeaseViewActivity.class);
        //Uses filtered results to match what is on screen
        Lease lease = leaseListAdapter.getFilteredResults().get(i);
        intent.putExtra("leaseID", lease.getId());
        getActivity().startActivityForResult(intent, MainActivity.REQUEST_LEASE_VIEW);
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
        void onLeaseListDatesChanged(Date dateStart, Date dateEnd, LeaseListFragment fragment);
    }

    @Override
    public void onPause() {
        super.onPause();
        datePickerDialogLauncher.dismissDatePickerDialog();
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

    public void updateData(ArrayList<Lease> leaseList) {
        leaseListAdapter.updateResults(leaseList);
        leaseListAdapter.getFilter().filter(searchBarET.getText());
        leaseListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

}
