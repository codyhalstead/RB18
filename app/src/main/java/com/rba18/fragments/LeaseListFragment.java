package com.rba18.fragments;

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

import com.rba18.R;
import com.rba18.activities.LeaseViewActivity;
import com.rba18.activities.MainActivity;
import com.rba18.adapters.LeaseListAdapter;
import com.rba18.helpers.CustomDatePickerDialogLauncher;
import com.rba18.helpers.DateAndCurrencyDisplayer;
import com.rba18.helpers.MainViewModel;
import com.rba18.model.Lease;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Cody on 4/14/2018.
 */

public class LeaseListFragment extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener {
    private TextView mNoLeasesTV;
    private EditText mSearchBarET;
    private LeaseListAdapter mLeaseListAdapter;
    private ColorStateList mAccentColor;
    private ListView mListView;
    private Button mDateRangeStartBtn, mDateRangeEndBtn;
    private Date mFilterDateStart, mFilterDateEnd;
    private OnDatesChangedListener mCallback;
    private boolean mNeedsRefreshedOnResume;
    private SharedPreferences mPreferences;
    private CustomDatePickerDialogLauncher mDatePickerDialogLauncher;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_money_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mNoLeasesTV = view.findViewById(R.id.emptyListTV);
        LinearLayout totalBarLL = view.findViewById(R.id.moneyListTotalBarLL);
        totalBarLL.setVisibility(View.GONE);
        mSearchBarET = view.findViewById(R.id.moneyListSearchET);
        mDateRangeStartBtn = view.findViewById(R.id.moneyListDateRangeStartBtn);
        mDateRangeStartBtn.setOnClickListener(this);
        mDateRangeEndBtn = view.findViewById(R.id.moneyListDateRangeEndBtn);
        mDateRangeEndBtn.setOnClickListener(this);
        mListView = view.findViewById(R.id.mainMoneyListView);
        mFilterDateStart = ViewModelProviders.of(getActivity()).get(MainViewModel.class).getStartDateRangeDate().getValue();
        mFilterDateEnd = ViewModelProviders.of(getActivity()).get(MainViewModel.class).getEndDateRangeDate().getValue();
        mPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        int dateFormatCode = mPreferences.getInt("dateFormat", DateAndCurrencyDisplayer.DATE_MMDDYYYY);
        mDateRangeStartBtn.setText(DateAndCurrencyDisplayer.getDateToDisplay(dateFormatCode, mFilterDateStart));
        mDateRangeEndBtn.setText(DateAndCurrencyDisplayer.getDateToDisplay(dateFormatCode, mFilterDateEnd));
        getActivity().setTitle(R.string.lease_list);
        //Get current theme accent color, which is passed into the list adapter for search highlighting
        TypedValue colorValue = new TypedValue();
        getActivity().getTheme().resolveAttribute(R.attr.colorAccent, colorValue, true);
        mAccentColor = getActivity().getResources().getColorStateList(colorValue.resourceId);
        setUpListAdapter();
        setUpSearchBar();
        mNeedsRefreshedOnResume = false;
        mDatePickerDialogLauncher = new CustomDatePickerDialogLauncher(mFilterDateStart, mFilterDateEnd, true, getContext());
        mDatePickerDialogLauncher.setDateSelectedListener(new CustomDatePickerDialogLauncher.DateSelectedListener() {
            @Override
            public void onStartDateSelected(Date startDate, Date endDate) {
                mFilterDateStart = startDate;
                mFilterDateEnd = endDate;
                int dateFormatCode = mPreferences.getInt("dateFormat", DateAndCurrencyDisplayer.DATE_MMDDYYYY);
                mDateRangeStartBtn.setText(DateAndCurrencyDisplayer.getDateToDisplay(dateFormatCode, mFilterDateStart));
                mDateRangeEndBtn.setText(DateAndCurrencyDisplayer.getDateToDisplay(dateFormatCode, mFilterDateEnd));
                mCallback.onLeaseListDatesChanged(mFilterDateStart, mFilterDateEnd, LeaseListFragment.this);
            }

            @Override
            public void onEndDateSelected(Date startDate, Date endDate) {
                mFilterDateStart = startDate;
                mFilterDateEnd = endDate;
                int dateFormatCode = mPreferences.getInt("dateFormat", DateAndCurrencyDisplayer.DATE_MMDDYYYY);
                mDateRangeStartBtn.setText(DateAndCurrencyDisplayer.getDateToDisplay(dateFormatCode, mFilterDateStart));
                mDateRangeEndBtn.setText(DateAndCurrencyDisplayer.getDateToDisplay(dateFormatCode, mFilterDateEnd));
                mCallback.onLeaseListDatesChanged(mFilterDateStart, mFilterDateEnd, LeaseListFragment.this);
            }

            @Override
            public void onDateSelected(Date date) {

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mNeedsRefreshedOnResume) {
            mLeaseListAdapter.updateResults(ViewModelProviders.of(getActivity()).get(MainViewModel.class).getCachedLeases().getValue());
            mSearchBarET.setText(mSearchBarET.getText());
            mSearchBarET.setSelection(mSearchBarET.getText().length());
        }
        mNeedsRefreshedOnResume = true;
    }

    private void setUpSearchBar() {
        mSearchBarET.addTextChangedListener(new TextWatcher() {
            //For updating search results as sUser types
            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                //When sUser changed the Text
                if (mLeaseListAdapter != null) {
                    mLeaseListAdapter.getFilter().filter(cs);

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
        mLeaseListAdapter = new LeaseListAdapter(getActivity(), ViewModelProviders.of(getActivity()).get(MainViewModel.class).getCachedLeases().getValue(), mAccentColor, null);
        mListView.setAdapter(mLeaseListAdapter);
        mListView.setOnItemClickListener(this);
        mNoLeasesTV.setText(R.string.no_leases_to_display);
        mListView.setEmptyView(mNoLeasesTV);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        //On listView row click, launch ApartmentViewActivity passing the rows data into it.
        Intent intent = new Intent(getContext(), LeaseViewActivity.class);
        //Uses filtered results to match what is on screen
        Lease lease = mLeaseListAdapter.getFilteredResults().get(i);
        intent.putExtra("leaseID", lease.getId());
        getActivity().startActivityForResult(intent, MainActivity.REQUEST_LEASE_VIEW);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.moneyListDateRangeStartBtn:
                mDatePickerDialogLauncher.launchStartDatePickerDialog();
                break;

            case R.id.moneyListDateRangeEndBtn:
                mDatePickerDialogLauncher.launchEndDatePickerDialog();
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
        mDatePickerDialogLauncher.dismissDatePickerDialog();
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
        mLeaseListAdapter.updateResults(leaseList);
        mLeaseListAdapter.getFilter().filter(mSearchBarET.getText());
        mLeaseListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

}
