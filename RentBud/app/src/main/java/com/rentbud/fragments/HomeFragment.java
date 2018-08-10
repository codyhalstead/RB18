package com.rentbud.fragments;

import android.arch.lifecycle.ViewModelProviders;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.cody.rentbud.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.LineData;
import com.rentbud.activities.MainActivity;
import com.rentbud.adapters.LeaseListAdapter;
import com.rentbud.adapters.MoneyListAdapter;
import com.rentbud.helpers.CustomDatePickerDialogLauncher;
import com.rentbud.helpers.MainViewModel;
import com.rentbud.helpers.MonthlyLineGraphCreator;
import com.rentbud.sqlite.DatabaseHandler;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Cody on 1/5/2018.
 */

public class HomeFragment extends android.support.v4.app.Fragment {
    private LineChart lineChart;
    private LineData lineData;
    Button graphLeftArrowBtn, graphRightArrowBtn;
    TextView graphYearTV, emptyLeasesTV, emptyMoneyTV;
    DatabaseHandler databaseHandler;
    float[] expenseValues;
    float[] incomeValues;
    Date startOfYear, endOfYear;
    CustomDatePickerDialogLauncher datePickerDialogLauncher;
    MonthlyLineGraphCreator monthlyLineGraphCreator;
    MoneyListAdapter moneyListAdapter;
    LeaseListAdapter leaseListAdapter;
    LinearLayout linegraphLL, upcomingListsLL;
    ListView upcomingPaymentsLV, upcomingLeasesLV;
    ColorStateList accentColor;
    Date today, startRange, endRange;
    //ImageView profilePic;
    //TextView usernameTV, emailbox, passbox;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.content_initial, container, false);
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
        databaseHandler = new DatabaseHandler(getContext());

        TabLayout tabLayout = view.findViewById(R.id.tabs);
        tabLayout.setSelectedTabIndicatorColor(Color.parseColor("#000000"));
        tabLayout.setTabTextColors(Color.parseColor("#ffffff"), Color.parseColor("#4d4c4b"));
        tabLayout.addTab(tabLayout.newTab().setText(getContext().getString(R.string.upcoming_events_tab_title)));
        tabLayout.addTab(tabLayout.newTab().setText(getContext().getString(R.string.yearly_revenue_tab_title)));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(tab.getPosition() == 0) {
                    linegraphLL.setVisibility(View.GONE);
                    upcomingListsLL.setVisibility(View.VISIBLE);
                    ViewModelProviders.of(getActivity()).get(MainViewModel.class).setHomeTabSelection(0);
                } else if(tab.getPosition() == 1){
                    linegraphLL.setVisibility(View.VISIBLE);
                    upcomingListsLL.setVisibility(View.GONE);
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
                incomeValues = databaseHandler.getIncomeTotalsForLineGraph(MainActivity.user, startOfYear, endOfYear);
                expenseValues = databaseHandler.getExpenseTotalsForLineGraph(MainActivity.user, startOfYear, endOfYear);
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
                incomeValues = databaseHandler.getIncomeTotalsForLineGraph(MainActivity.user, startOfYear, endOfYear);
                expenseValues = databaseHandler.getExpenseTotalsForLineGraph(MainActivity.user, startOfYear, endOfYear);
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
                incomeValues = databaseHandler.getIncomeTotalsForLineGraph(MainActivity.user, startOfYear, endOfYear);
                expenseValues = databaseHandler.getExpenseTotalsForLineGraph(MainActivity.user, startOfYear, endOfYear);
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
        incomeValues = databaseHandler.getIncomeTotalsForLineGraph(MainActivity.user, startOfYear, endOfYear);
        expenseValues = databaseHandler.getExpenseTotalsForLineGraph(MainActivity.user, startOfYear, endOfYear);
        monthlyLineGraphCreator.setIncomeExpenseData(incomeValues, expenseValues, ViewModelProviders.of(getActivity()).get(MainViewModel.class).getHomeTabYearSelected());
        getActivity().setTitle(R.string.home);

        setTextBoxes(MainActivity.user.getName(), MainActivity.user.getEmail(), MainActivity.user.getPassword());
        TypedValue colorValue = new TypedValue();
        getActivity().getTheme().resolveAttribute(R.attr.colorAccent, colorValue, true);
        this.accentColor = getActivity().getResources().getColorStateList(colorValue.resourceId);
        setUpMoneyListAdaptor();
        setUpLeaseListAdaptor();
        //If user has a profile pic, set that pic
        if (MainActivity.user.getProfilePic() != null && !MainActivity.user.getProfilePic().isEmpty()) {
            //profilePic.setImageURI(Uri.parse(MainActivity.user.getProfilePic()));
        }
    }

    public void setTextBoxes(String name, String email, String password) {
        //Sets user info to text boxes, TEMPORARY
        //this.usernameTV.setText(name);
        //this.emailbox.setText(email);
        //this.passbox.setText(password);
    }

    private void setUpMoneyListAdaptor() {
        moneyListAdapter = new MoneyListAdapter(getContext(), databaseHandler.getIncomeAndExpensesBetweenDates(MainActivity.user, this.startRange, this.endRange), accentColor, today);
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
        //upcomingPaymentsLV.setOnItemClickListener(this);
    }
}
