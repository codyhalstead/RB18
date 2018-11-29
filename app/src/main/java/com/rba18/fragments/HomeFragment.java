package com.rba18.fragments;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.rba18.R;
import com.github.mikephil.charting.charts.LineChart;
import com.google.android.gms.ads.MobileAds;
import com.rba18.activities.ExpenseViewActivity;
import com.rba18.activities.IncomeViewActivity;
import com.rba18.activities.LeaseViewActivity;
import com.rba18.activities.MainActivity;
import com.rba18.adapters.LeaseListAdapter;
import com.rba18.adapters.MoneyListAdapter;
import com.rba18.helpers.CustomDatePickerDialogLauncher;
import com.rba18.helpers.MainViewModel;
import com.rba18.helpers.MonthlyLineGraphCreator;
import com.rba18.model.ExpenseLogEntry;
import com.rba18.model.Lease;
import com.rba18.model.MoneyLogEntry;
import com.rba18.model.PaymentLogEntry;
import com.rba18.sqlite.DatabaseHandler;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Cody on 1/5/2018.
 */

public class HomeFragment extends android.support.v4.app.Fragment {
    private TextView mEmptyLeasesTV, mEmptyMoneyTV, mIsCompletedToggleFilterTV;
    private DatabaseHandler mDatabaseHandler;
    private float[] mExpenseValues, mIncomeValues;
    private Date mStartOfYear, mEndOfYear, mToday, mStartRange, mEndRange;
    private CustomDatePickerDialogLauncher mDatePickerDialogLauncher;
    private MonthlyLineGraphCreator mMonthlyLineGraphCreator;
    private MoneyListAdapter mMoneyListAdapter;
    private LeaseListAdapter mLeaseListAdapter;
    private LinearLayout mLineGraphLL, mUpcomingListsLL, mIsCompetedToggleFilterLL;
    private ListView mUpcomingPaymentsLV, mUpcomingLeasesLV;
    private ColorStateList mAccentColor;
    private Boolean mFragDataNeedsRefreshed, mIsCompletedOnly;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MobileAds.initialize(getActivity(), "ca-app-pub-3940256099942544~3347511713");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button graphLeftArrowBtn = view.findViewById(R.id.line_graph_left_arrow);
        Button graphRightArrowBtn = view.findViewById(R.id.line_graph_right_arrow);
        TextView graphYearTV = view.findViewById(R.id.line_graph_year_textview);
        mEmptyLeasesTV = view.findViewById(R.id.homeFragmentEmptyLeasesTV);
        mEmptyMoneyTV = view.findViewById(R.id.homeFragmentEmptyMoneyTV);
        mUpcomingPaymentsLV = view.findViewById(R.id.homeFragmentUpcomingPaymentsLV);
        mUpcomingLeasesLV = view.findViewById(R.id.homeFragmentUpcomingLeasesLV);
        LineChart lineChart = view.findViewById(R.id.homeLineChart);
        mLineGraphLL = view.findViewById(R.id.homeFragmentLineGraphLL);
        mUpcomingListsLL = view.findViewById(R.id.homeFragmentUpcomingListsLL);
        mIsCompletedToggleFilterTV = view.findViewById(R.id.homeIsCompletedFilterTV);
        mIsCompetedToggleFilterLL = view.findViewById(R.id.homeIsCompletedFilterLL);
        mDatabaseHandler = new DatabaseHandler(getContext());
        mFragDataNeedsRefreshed = false;
        mIsCompletedToggleFilterTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mIsCompletedOnly) {
                    mIncomeValues = mDatabaseHandler.getIncomeTotalsForLineGraph(MainActivity.sUser, mStartOfYear, mEndOfYear);
                    mExpenseValues = mDatabaseHandler.getExpenseTotalsForLineGraph(MainActivity.sUser, mStartOfYear, mEndOfYear);
                    mIsCompletedOnly = false;
                    mIsCompletedToggleFilterTV.setText(R.string.projected);
                } else {
                    mIncomeValues = mDatabaseHandler.getIncomeTotalsForLineGraphOnlyReceived(MainActivity.sUser, mStartOfYear, mEndOfYear);
                    mExpenseValues = mDatabaseHandler.getExpenseTotalsForLineGraphOnlyPaid(MainActivity.sUser, mStartOfYear, mEndOfYear);
                    mIsCompletedOnly = true;
                    mIsCompletedToggleFilterTV.setText(R.string.paid_received_only);
                }
                mMonthlyLineGraphCreator.setIncomeExpenseData(mIncomeValues, mExpenseValues, mStartOfYear);
            }
        });
        TabLayout tabLayout = view.findViewById(R.id.tabs);
        tabLayout.addTab(tabLayout.newTab().setText(getContext().getString(R.string.upcoming_events_tab_title)));
        tabLayout.addTab(tabLayout.newTab().setText(getContext().getString(R.string.yearly_revenue_tab_title)));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(tab.getPosition() == 0) {
                    mLineGraphLL.setVisibility(View.GONE);
                    mUpcomingListsLL.setVisibility(View.VISIBLE);
                    mIsCompetedToggleFilterLL.setVisibility(View.GONE);
                    ViewModelProviders.of(getActivity()).get(MainViewModel.class).setHomeTabSelection(0);
                } else if(tab.getPosition() == 1){
                    mLineGraphLL.setVisibility(View.VISIBLE);
                    mUpcomingListsLL.setVisibility(View.GONE);
                    mIsCompetedToggleFilterLL.setVisibility(View.VISIBLE);
                    ViewModelProviders.of(getActivity()).get(MainViewModel.class).setHomeTabSelection(1);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        if(ViewModelProviders.of(getActivity()).get(MainViewModel.class).getHomeTabSelection() == 0){
            tabLayout.getTabAt(1).select();
            tabLayout.getTabAt(0).select();
        } else {
            tabLayout.getTabAt(1).select();
        }
        if(savedInstanceState != null){
            mIsCompletedOnly = savedInstanceState.getBoolean("completedOnly");
        } else {
            mIsCompletedOnly = true;
        }
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        mToday = cal.getTime();
        mDatePickerDialogLauncher = new CustomDatePickerDialogLauncher(ViewModelProviders.of(getActivity()).get(MainViewModel.class).getHomeTabYearSelected(), true, getContext());
        mMonthlyLineGraphCreator = new MonthlyLineGraphCreator(getContext(), lineChart, graphLeftArrowBtn, graphRightArrowBtn, graphYearTV, ViewModelProviders.of(getActivity()).get(MainViewModel.class).getHomeTabYearSelected());
        mMonthlyLineGraphCreator.setDateSelectedListener(new MonthlyLineGraphCreator.OnButtonsClickedListener() {
            @Override
            public void onLeftBtnClicked() {
                Calendar cal = Calendar.getInstance();
                cal.setTime(mStartOfYear);
                cal.add(Calendar.YEAR, -1);
                mStartOfYear = cal.getTime();
                cal.set(Calendar.MONTH, 11);
                cal.set(Calendar.DAY_OF_MONTH, 31);
                mEndOfYear = cal.getTime();
                mDatePickerDialogLauncher.setSingleDatePreset(mStartOfYear);
                if(mIsCompletedOnly) {
                    mIncomeValues = mDatabaseHandler.getIncomeTotalsForLineGraphOnlyReceived(MainActivity.sUser, mStartOfYear, mEndOfYear);
                    mExpenseValues = mDatabaseHandler.getExpenseTotalsForLineGraphOnlyPaid(MainActivity.sUser, mStartOfYear, mEndOfYear);
                } else {
                    mIncomeValues = mDatabaseHandler.getIncomeTotalsForLineGraph(MainActivity.sUser, mStartOfYear, mEndOfYear);
                    mExpenseValues = mDatabaseHandler.getExpenseTotalsForLineGraph(MainActivity.sUser, mStartOfYear, mEndOfYear);
                }
                mMonthlyLineGraphCreator.setIncomeExpenseData(mIncomeValues, mExpenseValues, mStartOfYear);
                ViewModelProviders.of(getActivity()).get(MainViewModel.class).setHomeTabYearSelected(mStartOfYear);
            }

            @Override
            public void onRightBtnClicked() {
                Calendar cal = Calendar.getInstance();
                cal.setTime(mStartOfYear);
                cal.add(Calendar.YEAR, 1);
                mStartOfYear = cal.getTime();
                cal.set(Calendar.MONTH, 11);
                cal.set(Calendar.DAY_OF_MONTH, 31);
                mEndOfYear = cal.getTime();
                mDatePickerDialogLauncher.setSingleDatePreset(mStartOfYear);
                if(mIsCompletedOnly) {
                    mIncomeValues = mDatabaseHandler.getIncomeTotalsForLineGraphOnlyReceived(MainActivity.sUser, mStartOfYear, mEndOfYear);
                    mExpenseValues = mDatabaseHandler.getExpenseTotalsForLineGraphOnlyPaid(MainActivity.sUser, mStartOfYear, mEndOfYear);
                } else {
                    mIncomeValues = mDatabaseHandler.getIncomeTotalsForLineGraph(MainActivity.sUser, mStartOfYear, mEndOfYear);
                    mExpenseValues = mDatabaseHandler.getExpenseTotalsForLineGraph(MainActivity.sUser, mStartOfYear, mEndOfYear);
                }
                mMonthlyLineGraphCreator.setIncomeExpenseData(mIncomeValues, mExpenseValues, mStartOfYear);
                ViewModelProviders.of(getActivity()).get(MainViewModel.class).setHomeTabYearSelected(mStartOfYear);
            }

            @Override
            public void onDateTVClicked() {
                mDatePickerDialogLauncher.launchSingleDatePickerDialog();
            }
        });
        mDatePickerDialogLauncher.setDateSelectedListener(new CustomDatePickerDialogLauncher.DateSelectedListener() {
            @Override
            public void onStartDateSelected(Date startDate, Date endDate) {

            }

            @Override
            public void onEndDateSelected(Date startDate, Date endDate) {

            }

            @Override
            public void onDateSelected(Date date) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                cal.set(Calendar.DAY_OF_YEAR, 1);
                mStartOfYear = cal.getTime();
                cal.set(Calendar.MONTH, 11);
                cal.set(Calendar.DAY_OF_MONTH, 31);
                mEndOfYear = cal.getTime();
                if(mIsCompletedOnly) {
                    mIncomeValues = mDatabaseHandler.getIncomeTotalsForLineGraphOnlyReceived(MainActivity.sUser, mStartOfYear, mEndOfYear);
                    mExpenseValues = mDatabaseHandler.getExpenseTotalsForLineGraphOnlyPaid(MainActivity.sUser, mStartOfYear, mEndOfYear);
                } else {
                    mIncomeValues = mDatabaseHandler.getIncomeTotalsForLineGraph(MainActivity.sUser, mStartOfYear, mEndOfYear);
                    mExpenseValues = mDatabaseHandler.getExpenseTotalsForLineGraph(MainActivity.sUser, mStartOfYear, mEndOfYear);
                }
                mMonthlyLineGraphCreator.setIncomeExpenseData(mIncomeValues, mExpenseValues, mStartOfYear);
                ViewModelProviders.of(getActivity()).get(MainViewModel.class).setHomeTabYearSelected(mStartOfYear);
            }
        });
        mStartRange = cal.getTime();
        cal.add(Calendar.DAY_OF_YEAR, 14);
        mEndRange = cal.getTime();
        cal.setTime(ViewModelProviders.of(getActivity()).get(MainViewModel.class).getHomeTabYearSelected());
        cal.set(Calendar.DAY_OF_YEAR, 1);
        mStartOfYear = cal.getTime();
        cal.set(Calendar.MONTH, 11);
        cal.set(Calendar.DAY_OF_MONTH, 31);
        mEndOfYear = cal.getTime();
        if(mIsCompletedOnly) {
            mIncomeValues = mDatabaseHandler.getIncomeTotalsForLineGraphOnlyReceived(MainActivity.sUser, mStartOfYear, mEndOfYear);
            mExpenseValues = mDatabaseHandler.getExpenseTotalsForLineGraphOnlyPaid(MainActivity.sUser, mStartOfYear, mEndOfYear);
        } else {
            mIncomeValues = mDatabaseHandler.getIncomeTotalsForLineGraph(MainActivity.sUser, mStartOfYear, mEndOfYear);
            mExpenseValues = mDatabaseHandler.getExpenseTotalsForLineGraph(MainActivity.sUser, mStartOfYear, mEndOfYear);
        }
        mMonthlyLineGraphCreator.setIncomeExpenseData(mIncomeValues, mExpenseValues, ViewModelProviders.of(getActivity()).get(MainViewModel.class).getHomeTabYearSelected());
        getActivity().setTitle(R.string.home);
        TypedValue colorValue = new TypedValue();
        getActivity().getTheme().resolveAttribute(R.attr.colorAccent, colorValue, true);
        mAccentColor = getActivity().getResources().getColorStateList(colorValue.resourceId);
        setUpMoneyListAdaptor();
        setUpLeaseListAdaptor();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mFragDataNeedsRefreshed){
            mMoneyListAdapter.updateResults(mDatabaseHandler.getIncomeAndExpensesBetweenDatesNotCompleted(MainActivity.sUser, mStartRange, mEndRange));
            mLeaseListAdapter.updateResults(mDatabaseHandler.getLeasesStartingOrEndingInDateRange(MainActivity.sUser, mStartRange, mEndRange));
            mIncomeValues = mDatabaseHandler.getIncomeTotalsForLineGraph(MainActivity.sUser, mStartOfYear, mEndOfYear);
            mExpenseValues = mDatabaseHandler.getExpenseTotalsForLineGraph(MainActivity.sUser, mStartOfYear, mEndOfYear);
            mMonthlyLineGraphCreator.setIncomeExpenseData(mIncomeValues, mExpenseValues, mStartOfYear);
        }
    }

    private void setUpMoneyListAdaptor() {
        mMoneyListAdapter = new MoneyListAdapter(getContext(), mDatabaseHandler.getIncomeAndExpensesBetweenDatesNotCompleted(MainActivity.sUser, mStartRange, mEndRange), mAccentColor, mToday, false);
        mUpcomingPaymentsLV.setAdapter(mMoneyListAdapter);
        mUpcomingPaymentsLV.setOnTouchListener(new View.OnTouchListener() {
            // Setting on Touch Listener for handling the touch inside ScrollView
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Disallow the touch request for parent scroll on touch of child view

                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }

        });
        mEmptyMoneyTV.setText(R.string.no_payments_within_14_days);
        mUpcomingPaymentsLV.setEmptyView(mEmptyMoneyTV);
        mUpcomingPaymentsLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                MoneyLogEntry mle = mMoneyListAdapter.getFilteredResults().get(i);
                mFragDataNeedsRefreshed = true;
                if(mle instanceof PaymentLogEntry){
                    PaymentLogEntry income = (PaymentLogEntry) mle;
                    Intent intent = new Intent(getContext(), IncomeViewActivity.class);
                    intent.putExtra("incomeID", income.getId());
                    getActivity().startActivityForResult(intent, MainActivity.REQUEST_INCOME_VIEW);
                } else if (mle instanceof ExpenseLogEntry){
                    ExpenseLogEntry expense = (ExpenseLogEntry) mle;
                    Intent intent = new Intent(getContext(), ExpenseViewActivity.class);
                    intent.putExtra("expenseID", expense.getId());
                    getActivity().startActivityForResult(intent, MainActivity.REQUEST_EXPENSE_VIEW);
                }
            }
        });
    }

    private void setUpLeaseListAdaptor() {
        mLeaseListAdapter = new LeaseListAdapter(getContext(), mDatabaseHandler.getLeasesStartingOrEndingInDateRange(MainActivity.sUser, mStartRange, mEndRange), mAccentColor, mToday);
        mUpcomingLeasesLV.setAdapter(mLeaseListAdapter);
        mUpcomingLeasesLV.setOnTouchListener(new View.OnTouchListener() {
            // Setting on Touch Listener for handling the touch inside ScrollView
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Disallow the touch request for parent scroll on touch of child view
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }

        });
        mEmptyLeasesTV.setText(R.string.no_leases_within_14_days);
        mUpcomingLeasesLV.setEmptyView(mEmptyLeasesTV);
        mUpcomingLeasesLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mFragDataNeedsRefreshed = true;
                //On listView row click, launch ApartmentViewActivity passing the rows data into it.
                Intent intent = new Intent(getContext(), LeaseViewActivity.class);
                //Uses filtered results to match what is on screen
                Lease lease = mLeaseListAdapter.getFilteredResults().get(i);
                intent.putExtra("leaseID", lease.getId());
                getActivity().startActivityForResult(intent, MainActivity.REQUEST_LEASE_VIEW);
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("completedOnly", mIsCompletedOnly);
    }
}
