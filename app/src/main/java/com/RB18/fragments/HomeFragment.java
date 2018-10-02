package com.RB18.fragments;

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

import com.example.cody.rentbud.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.LineData;
import com.google.android.gms.ads.MobileAds;
import com.RB18.activities.ExpenseViewActivity;
import com.RB18.activities.IncomeViewActivity;
import com.RB18.activities.LeaseViewActivity;
import com.RB18.activities.MainActivity;
import com.RB18.adapters.LeaseListAdapter;
import com.RB18.adapters.MoneyListAdapter;
import com.RB18.helpers.CustomDatePickerDialogLauncher;
import com.RB18.helpers.MainViewModel;
import com.RB18.helpers.MonthlyLineGraphCreator;
import com.RB18.model.ExpenseLogEntry;
import com.RB18.model.Lease;
import com.RB18.model.MoneyLogEntry;
import com.RB18.model.PaymentLogEntry;
import com.RB18.sqlite.DatabaseHandler;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Cody on 1/5/2018.
 */

public class HomeFragment extends android.support.v4.app.Fragment {
    private LineChart lineChart;
    private LineData lineData;
    Button graphLeftArrowBtn, graphRightArrowBtn;
    TextView graphYearTV, emptyLeasesTV, emptyMoneyTV, isCompletedToggleFilterTV;
    DatabaseHandler databaseHandler;
    float[] expenseValues;
    float[] incomeValues;
    Date startOfYear, endOfYear;
    CustomDatePickerDialogLauncher datePickerDialogLauncher;
    MonthlyLineGraphCreator monthlyLineGraphCreator;
    MoneyListAdapter moneyListAdapter;
    LeaseListAdapter leaseListAdapter;
    LinearLayout linegraphLL, upcomingListsLL, isCompetedToggleFilterLL;
    ListView upcomingPaymentsLV, upcomingLeasesLV;
    ColorStateList accentColor;
    Date today, startRange, endRange;
    Boolean fragDataNeedsRefreshed, isCompletedOnly;
    //ImageView profilePic;
    //TextView usernameTV, emailbox, passbox;


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
        graphLeftArrowBtn = view.findViewById(R.id.line_graph_left_arrow);
        graphRightArrowBtn = view.findViewById(R.id.line_graph_right_arrow);
        graphYearTV = view.findViewById(R.id.line_graph_year_textview);
        emptyLeasesTV = view.findViewById(R.id.homeFragmentEmptyLeasesTV);
        emptyMoneyTV = view.findViewById(R.id.homeFragmentEmptyMoneyTV);
        upcomingPaymentsLV = view.findViewById(R.id.homeFragmentUpcomingPaymentsLV);
        upcomingLeasesLV = view.findViewById(R.id.homeFragmentUpcomingLeasesLV);
        lineChart = view.findViewById(R.id.homeLineChart);
        linegraphLL = view.findViewById(R.id.homeFragmentLineGraphLL);
        upcomingListsLL = view.findViewById(R.id.homeFragmentUpcomingListsLL);
        isCompletedToggleFilterTV = view.findViewById(R.id.homeIsCompletedFilterTV);
        isCompetedToggleFilterLL = view.findViewById(R.id.homeIsCompletedFilterLL);
        databaseHandler = new DatabaseHandler(getContext());
        fragDataNeedsRefreshed = false;
        isCompletedToggleFilterTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isCompletedOnly) {
                    incomeValues = databaseHandler.getIncomeTotalsForLineGraph(MainActivity.user, startOfYear, endOfYear);
                    expenseValues = databaseHandler.getExpenseTotalsForLineGraph(MainActivity.user, startOfYear, endOfYear);
                    isCompletedOnly = false;
                    isCompletedToggleFilterTV.setText(R.string.projected);
                } else {
                    incomeValues = databaseHandler.getIncomeTotalsForLineGraphOnlyReceived(MainActivity.user, startOfYear, endOfYear);
                    expenseValues = databaseHandler.getExpenseTotalsForLineGraphOnlyPaid(MainActivity.user, startOfYear, endOfYear);
                    isCompletedOnly = true;
                    isCompletedToggleFilterTV.setText(R.string.paid_received_only);
                }
                monthlyLineGraphCreator.setIncomeExpenseData(incomeValues, expenseValues, startOfYear);
            }
        });
        TabLayout tabLayout = view.findViewById(R.id.tabs);
        tabLayout.addTab(tabLayout.newTab().setText(getContext().getString(R.string.upcoming_events_tab_title)));
        tabLayout.addTab(tabLayout.newTab().setText(getContext().getString(R.string.yearly_revenue_tab_title)));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(tab.getPosition() == 0) {
                    linegraphLL.setVisibility(View.GONE);
                    upcomingListsLL.setVisibility(View.VISIBLE);
                    isCompetedToggleFilterLL.setVisibility(View.GONE);
                    ViewModelProviders.of(getActivity()).get(MainViewModel.class).setHomeTabSelection(0);
                } else if(tab.getPosition() == 1){
                    linegraphLL.setVisibility(View.VISIBLE);
                    upcomingListsLL.setVisibility(View.GONE);
                    isCompetedToggleFilterLL.setVisibility(View.VISIBLE);
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
            isCompletedOnly = savedInstanceState.getBoolean("completedOnly");
        } else {
            isCompletedOnly = true;
        }
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        today = cal.getTime();

        datePickerDialogLauncher = new CustomDatePickerDialogLauncher(ViewModelProviders.of(getActivity()).get(MainViewModel.class).getHomeTabYearSelected(), true, getContext());
        monthlyLineGraphCreator = new MonthlyLineGraphCreator(getContext(), lineChart, graphLeftArrowBtn, graphRightArrowBtn, graphYearTV, ViewModelProviders.of(getActivity()).get(MainViewModel.class).getHomeTabYearSelected());
        monthlyLineGraphCreator.setDateSelectedListener(new MonthlyLineGraphCreator.OnButtonsClickedListener() {
            @Override
            public void onLeftBtnClicked() {
                Calendar cal = Calendar.getInstance();
                cal.setTime(startOfYear);
                cal.add(Calendar.YEAR, -1);
                startOfYear = cal.getTime();
                cal.set(Calendar.MONTH, 11);
                cal.set(Calendar.DAY_OF_MONTH, 31);
                endOfYear = cal.getTime();
                datePickerDialogLauncher.setSingleDatePreset(startOfYear);
                if(isCompletedOnly) {
                    incomeValues = databaseHandler.getIncomeTotalsForLineGraphOnlyReceived(MainActivity.user, startOfYear, endOfYear);
                    expenseValues = databaseHandler.getExpenseTotalsForLineGraphOnlyPaid(MainActivity.user, startOfYear, endOfYear);
                } else {
                    incomeValues = databaseHandler.getIncomeTotalsForLineGraph(MainActivity.user, startOfYear, endOfYear);
                    expenseValues = databaseHandler.getExpenseTotalsForLineGraph(MainActivity.user, startOfYear, endOfYear);
                }
                monthlyLineGraphCreator.setIncomeExpenseData(incomeValues, expenseValues, startOfYear);
                ViewModelProviders.of(getActivity()).get(MainViewModel.class).setHomeTabYearSelected(startOfYear);
            }

            @Override
            public void onRightBtnClicked() {
                Calendar cal = Calendar.getInstance();
                cal.setTime(startOfYear);
                cal.add(Calendar.YEAR, 1);
                startOfYear = cal.getTime();
                cal.set(Calendar.MONTH, 11);
                cal.set(Calendar.DAY_OF_MONTH, 31);
                endOfYear = cal.getTime();
                datePickerDialogLauncher.setSingleDatePreset(startOfYear);
                if(isCompletedOnly) {
                    incomeValues = databaseHandler.getIncomeTotalsForLineGraphOnlyReceived(MainActivity.user, startOfYear, endOfYear);
                    expenseValues = databaseHandler.getExpenseTotalsForLineGraphOnlyPaid(MainActivity.user, startOfYear, endOfYear);
                } else {
                    incomeValues = databaseHandler.getIncomeTotalsForLineGraph(MainActivity.user, startOfYear, endOfYear);
                    expenseValues = databaseHandler.getExpenseTotalsForLineGraph(MainActivity.user, startOfYear, endOfYear);
                }
                monthlyLineGraphCreator.setIncomeExpenseData(incomeValues, expenseValues, startOfYear);
                ViewModelProviders.of(getActivity()).get(MainViewModel.class).setHomeTabYearSelected(startOfYear);
            }

            @Override
            public void onDateTVClicked() {
                datePickerDialogLauncher.launchSingleDatePickerDialog();
            }
        });
        datePickerDialogLauncher.setDateSelectedListener(new CustomDatePickerDialogLauncher.DateSelectedListener() {
            @Override
            public void onStartDateSelected(Date startDate, Date endDate) {

            }

            @Override
            public void onEndDateSelected(Date startDate, Date endDate) {

            }

            @Override
            public void onDateSelected(Date date) {
                Calendar cal = Calendar.getInstance();
                //Date today = cal.getTime();
                cal.setTime(date);
                cal.set(Calendar.DAY_OF_YEAR, 1);
                startOfYear = cal.getTime();
                cal.set(Calendar.MONTH, 11);
                cal.set(Calendar.DAY_OF_MONTH, 31);
                endOfYear = cal.getTime();
                if(isCompletedOnly) {
                    incomeValues = databaseHandler.getIncomeTotalsForLineGraphOnlyReceived(MainActivity.user, startOfYear, endOfYear);
                    expenseValues = databaseHandler.getExpenseTotalsForLineGraphOnlyPaid(MainActivity.user, startOfYear, endOfYear);
                } else {
                    incomeValues = databaseHandler.getIncomeTotalsForLineGraph(MainActivity.user, startOfYear, endOfYear);
                    expenseValues = databaseHandler.getExpenseTotalsForLineGraph(MainActivity.user, startOfYear, endOfYear);
                }
                monthlyLineGraphCreator.setIncomeExpenseData(incomeValues, expenseValues, startOfYear);
                ViewModelProviders.of(getActivity()).get(MainViewModel.class).setHomeTabYearSelected(startOfYear);
            }
        });
        this.startRange = cal.getTime();
        cal.add(Calendar.DAY_OF_YEAR, 14);
        this.endRange = cal.getTime();
        cal.setTime(ViewModelProviders.of(getActivity()).get(MainViewModel.class).getHomeTabYearSelected());
        cal.set(Calendar.DAY_OF_YEAR, 1);
        startOfYear = cal.getTime();
        cal.set(Calendar.MONTH, 11);
        //String year = (String) DateFormat.format("yyyy", startOfYear);
        cal.set(Calendar.DAY_OF_MONTH, 31);
        endOfYear = cal.getTime();
        if(isCompletedOnly) {
            incomeValues = databaseHandler.getIncomeTotalsForLineGraphOnlyReceived(MainActivity.user, startOfYear, endOfYear);
            expenseValues = databaseHandler.getExpenseTotalsForLineGraphOnlyPaid(MainActivity.user, startOfYear, endOfYear);
        } else {
            incomeValues = databaseHandler.getIncomeTotalsForLineGraph(MainActivity.user, startOfYear, endOfYear);
            expenseValues = databaseHandler.getExpenseTotalsForLineGraph(MainActivity.user, startOfYear, endOfYear);
        }
        monthlyLineGraphCreator.setIncomeExpenseData(incomeValues, expenseValues, ViewModelProviders.of(getActivity()).get(MainViewModel.class).getHomeTabYearSelected());
        getActivity().setTitle(R.string.home);

        setTextBoxes(MainActivity.user.getName(), MainActivity.user.getEmail(), MainActivity.user.getPassword());
        TypedValue colorValue = new TypedValue();
        getActivity().getTheme().resolveAttribute(R.attr.colorAccent, colorValue, true);
        this.accentColor = getActivity().getResources().getColorStateList(colorValue.resourceId);
        setUpMoneyListAdaptor();
        setUpLeaseListAdaptor();
    }

    public void setTextBoxes(String name, String email, String password) {
        //Sets user info to text boxes, TEMPORARY
        //this.usernameTV.setText(name);
        //this.emailbox.setText(email);
        //this.passbox.setText(password);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(fragDataNeedsRefreshed){
            moneyListAdapter.updateResults(databaseHandler.getIncomeAndExpensesBetweenDatesNotCompleted(MainActivity.user, this.startRange, this.endRange));
            leaseListAdapter.updateResults(databaseHandler.getLeasesStartingOrEndingInDateRange(MainActivity.user, this.startRange, this.endRange));
            incomeValues = databaseHandler.getIncomeTotalsForLineGraph(MainActivity.user, startOfYear, endOfYear);
            expenseValues = databaseHandler.getExpenseTotalsForLineGraph(MainActivity.user, startOfYear, endOfYear);
            monthlyLineGraphCreator.setIncomeExpenseData(incomeValues, expenseValues, startOfYear);
        }
    }

    private void setUpMoneyListAdaptor() {
        moneyListAdapter = new MoneyListAdapter(getContext(), databaseHandler.getIncomeAndExpensesBetweenDatesNotCompleted(MainActivity.user, this.startRange, this.endRange), accentColor, today, false);
        upcomingPaymentsLV.setAdapter(moneyListAdapter);
        upcomingPaymentsLV.setOnTouchListener(new View.OnTouchListener() {
            // Setting on Touch Listener for handling the touch inside ScrollView
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Disallow the touch request for parent scroll on touch of child view

                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }

        });
        emptyMoneyTV.setText(R.string.no_payments_within_14_days);
        this.upcomingPaymentsLV.setEmptyView(emptyMoneyTV);
        upcomingPaymentsLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                MoneyLogEntry mle = moneyListAdapter.getFilteredResults().get(i);
                fragDataNeedsRefreshed = true;
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
        //upcomingPaymentsLV.setOnItemClickListener(this);
    }

    private void setUpLeaseListAdaptor() {
        leaseListAdapter = new LeaseListAdapter(getContext(), databaseHandler.getLeasesStartingOrEndingInDateRange(MainActivity.user, this.startRange, this.endRange), accentColor, today);
        upcomingLeasesLV.setAdapter(leaseListAdapter);
        upcomingLeasesLV.setOnTouchListener(new View.OnTouchListener() {
            // Setting on Touch Listener for handling the touch inside ScrollView
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Disallow the touch request for parent scroll on touch of child view
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }

        });
        emptyLeasesTV.setText(R.string.no_leases_within_14_days);
        this.upcomingLeasesLV.setEmptyView(emptyLeasesTV);
        upcomingLeasesLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                fragDataNeedsRefreshed = true;
                //On listView row click, launch ApartmentViewActivity passing the rows data into it.
                Intent intent = new Intent(getContext(), LeaseViewActivity.class);
                //Uses filtered results to match what is on screen
                Lease lease = leaseListAdapter.getFilteredResults().get(i);
                intent.putExtra("leaseID", lease.getId());
                getActivity().startActivityForResult(intent, MainActivity.REQUEST_LEASE_VIEW);
            }
        });
        //upcomingPaymentsLV.setOnItemClickListener(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("completedOnly", isCompletedOnly);
    }
}
