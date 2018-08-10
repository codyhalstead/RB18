package com.rentbud.fragments;

/**
 * Created by Cody on 1/10/2018.
 */

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.PopupWindow;

import com.example.cody.rentbud.R;
import com.rentbud.activities.MainActivity;
import com.rentbud.activities.SingleDateViewActivity;
import com.rentbud.adapters.CustomCalendarAdapter;
import com.rentbud.helpers.CustomDatePickerDialogLauncher;
import com.rentbud.sqlite.DatabaseHandler;
import com.roomorama.caldroid.CaldroidGridAdapter;
import com.roomorama.caldroid.CaldroidListener;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import static android.support.constraint.Constraints.TAG;

public class CalendarFragment extends android.support.v4.app.Fragment {
    private CustomCaldroidFragment caldroidFragment;
    private Date currentSelectedDate;
    private Date today;
    Button findDateBtn, goToTodayBtn;
    Button calendarKeyBtn;
    private HashMap<String, Integer> leaseStartDatesHM = new HashMap<>();
    private HashMap<String, Integer> leaseEndDatesHM = new HashMap<>();
    private HashMap<String, Integer> expenseDatesHM = new HashMap<>();
    private HashMap<String, Integer> incomeDatesHM = new HashMap<>();
    private DatabaseHandler databaseHandler;
    private ArrayList<CaldroidGridAdapter> adapters;
    private CustomDatePickerDialogLauncher datePickerDialogLauncher;
    private PopupWindow keyPopup;
    int selectedMonth, selectedYear;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        databaseHandler = new DatabaseHandler(getActivity());
        //caldroidFragment = new CustomCaldroidFragment();
        return inflater.inflate(R.layout.main_calendar_view, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(R.string.calendar_view);
        calendarKeyBtn = getActivity().findViewById(R.id.calendarKeyImageButton);
        findDateBtn = getActivity().findViewById(R.id.findDateBtn);
        goToTodayBtn = getActivity().findViewById(R.id.goToTodayBtn);
        // Setup caldroid fragment
        caldroidFragment = new CustomCaldroidFragment();
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        today = cal.getTime();

        // If Activity is created after rotation, get previous state and date selected
        if (savedInstanceState != null) {
            caldroidFragment.restoreStatesFromKey(savedInstanceState, "CALDROID_SAVED_STATE");
            currentSelectedDate = new Date(savedInstanceState.getLong("selected_Date"));
        } else {
            currentSelectedDate = today;
        }
        // Attach to the activity
        FragmentTransaction t = getActivity().getSupportFragmentManager().beginTransaction();
        t.replace(R.id.calendar1, caldroidFragment);
        t.commit();
        //If there was a selected date, go to and highlight date
        if (currentSelectedDate != null) {
            highlightDateCell(currentSelectedDate);
            caldroidFragment.refreshView();
        }
        setUpCaldroidListener();
        setUpCalendarKeyListener();
        datePickerDialogLauncher = new CustomDatePickerDialogLauncher(currentSelectedDate, false, getContext());
        datePickerDialogLauncher.setDateSelectedListener(new CustomDatePickerDialogLauncher.DateSelectedListener() {
            @Override
            public void onStartDateSelected(Date startDate, Date endDate) {

            }

            @Override
            public void onEndDateSelected(Date startDate, Date endDate) {

            }

            @Override
            public void onDateSelected(Date date) {
                //year = year - 1900;
                //Date date = new Date(year, month, day);
                highlightDateCell(date);
                caldroidFragment.moveToDate(date);
                caldroidFragment.refreshView();
            }
        });

        setUpFindDateBtnListener();
        setUpGoToTodayBtnListener();
        adapters = caldroidFragment.getDatePagerAdapters();
        selectedMonth = 1;
        selectedYear = 1;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //Save state
        if (caldroidFragment != null) {
            caldroidFragment.saveStatesToKey(outState, "CALDROID_SAVED_STATE");
        }
        //Save selected date
        if (currentSelectedDate != null) {
            outState.putLong("selected_Date", currentSelectedDate.getTime());
        }
    }

    public void showCalendarKeyPopup(View v) {
        //Display key pop-up
        LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View popupView = layoutInflater.inflate(R.layout.popup_calendar_key, null);
        keyPopup = new PopupWindow(
                popupView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        keyPopup.setBackgroundDrawable(new BitmapDrawable());
        keyPopup.setOutsideTouchable(true);
        keyPopup.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                //Nothing, just close
            }
        });
        keyPopup.showAsDropDown(v, 35, -435);
    }

    public void highlightDateCell(Date newDateToHighlight) {
        //If previous date selected, revert its text color to black
        if (currentSelectedDate != null && currentSelectedDate != newDateToHighlight) {
            caldroidFragment.setTextColorForDate(R.color.caldroid_black, currentSelectedDate);
            if (currentSelectedDate.equals(today)) {
                caldroidFragment.setBackgroundDrawableForDate(getResources().getDrawable(R.drawable.red_border), currentSelectedDate);
            } else {
                caldroidFragment.setBackgroundDrawableForDate(getResources().getDrawable(R.drawable.cell_bg), currentSelectedDate);
            }
        }
        //Set selected dates text color to red
        caldroidFragment.setBackgroundDrawableForDate(getResources().getDrawable(R.drawable.calendar_blue_border), newDateToHighlight);
        currentSelectedDate = newDateToHighlight;
    }

    private void setUpCalendarKeyListener() {
        //Calendar key button listener
        calendarKeyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCalendarKeyPopup(view);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        DateTime startRange = new DateTime(selectedYear, selectedMonth, 1, 0, 0);
        DateTime endRange = new DateTime(selectedYear, selectedMonth, 28, 0, 0);
        startRange = startRange.minusDays(14);
        endRange = endRange.plusDays(14);
        expenseDatesHM = databaseHandler.getExpensesHMForCalendar(startRange, endRange, MainActivity.user);
        incomeDatesHM = databaseHandler.getIncomeHMForCalendar(startRange, endRange, MainActivity.user);
        leaseEndDatesHM = databaseHandler.getLeaseEndHMForCalendar(startRange, endRange, MainActivity.user);
        leaseStartDatesHM = databaseHandler.getLeaseStartHMForCalendar(startRange, endRange, MainActivity.user);
        caldroidFragment.setEventIcons(leaseStartDatesHM, leaseEndDatesHM, expenseDatesHM, incomeDatesHM);
        for(int i = 0; i < adapters.size(); i++){
            CustomCalendarAdapter c = (CustomCalendarAdapter) adapters.get(i);
            c.updateDateData(leaseStartDatesHM, leaseEndDatesHM, expenseDatesHM, incomeDatesHM);
        }
    }

    private void setUpCaldroidListener() {
        //Caldroid listener()
        caldroidFragment.setCaldroidListener(new CaldroidListener() {

            @Override
            public void onSelectDate(Date date, View view) {
                //Launch SingleDateViewActivity and pass the dates information
                Intent intent = new Intent(getActivity(), SingleDateViewActivity.class);
                intent.putExtra("date", date);
                getActivity().startActivityForResult(intent, MainActivity.REQUEST_CALENDAR_VIEW);
            }

            @Override
            public void onChangeMonth(int month, int year) {
                selectedYear = year;
                selectedMonth = month;
                DateTime startRange = new DateTime(year, month, 1, 0, 0);
                DateTime endRange = new DateTime(year, month, 28, 0, 0);
                startRange = startRange.minusDays(14);
                endRange = endRange.plusDays(14);
                expenseDatesHM = databaseHandler.getExpensesHMForCalendar(startRange, endRange, MainActivity.user);
                incomeDatesHM = databaseHandler.getIncomeHMForCalendar(startRange, endRange, MainActivity.user);
                leaseEndDatesHM = databaseHandler.getLeaseEndHMForCalendar(startRange, endRange, MainActivity.user);
                leaseStartDatesHM = databaseHandler.getLeaseStartHMForCalendar(startRange, endRange, MainActivity.user);
                caldroidFragment.setEventIcons(leaseStartDatesHM, leaseEndDatesHM, expenseDatesHM, incomeDatesHM);
                for(int i = 0; i < adapters.size(); i++){
                    CustomCalendarAdapter c = (CustomCalendarAdapter) adapters.get(i);
                    c.updateDateData(leaseStartDatesHM, leaseEndDatesHM, expenseDatesHM, incomeDatesHM);
                }
            }

            @Override
            public void onLongClickDate(Date date, View view) {

            }

            @Override
            public void onCaldroidViewCreated() {

            }
        });
    }

    private void setUpFindDateBtnListener() {
        findDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerDialogLauncher.launchSingleDatePickerDialog();
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        datePickerDialogLauncher.dismissDatePickerDialog();
        if(keyPopup != null){
            keyPopup.dismiss();
        }
    }

    private void setUpGoToTodayBtnListener(){
        goToTodayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                highlightDateCell(today);
                caldroidFragment.moveToDate(today);
                caldroidFragment.refreshView();
            }
        });
    }
}