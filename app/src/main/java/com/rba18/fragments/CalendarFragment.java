package com.rba18.fragments;

/*
 * Created by Cody on 1/10/2018.
 */

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;

import com.rba18.R;
import com.rba18.activities.MainActivity;
import com.rba18.activities.SingleDateViewActivity;
import com.rba18.adapters.CustomCalendarAdapter;
import com.rba18.helpers.CustomDatePickerDialogLauncher;
import com.rba18.sqlite.DatabaseHandler;
import com.roomorama.caldroid.CaldroidGridAdapter;
import com.roomorama.caldroid.CaldroidListener;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class CalendarFragment extends android.support.v4.app.Fragment {
    private CustomCaldroidFragment mCaldroidFragment;
    private Date mCurrentSelectedDate, mToday;
    private Button mFindDateBtn, mGoToTodayBtn, mCalendarKeyBtn;
    private HashMap<String, Integer> mLeaseStartDatesHM = new HashMap<>();
    private HashMap<String, Integer> mLeaseEndDatesHM = new HashMap<>();
    private HashMap<String, Integer> mExpenseDatesHM = new HashMap<>();
    private HashMap<String, Integer> mIncomeDatesHM = new HashMap<>();
    private DatabaseHandler mDatabaseHandler;
    private ArrayList<CaldroidGridAdapter> mAdapters;
    private CustomDatePickerDialogLauncher mDatePickerDialogLauncher;
    private PopupWindow mKeyPopup;
    private int mSelectedMonth, mSelectedYear;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mDatabaseHandler = new DatabaseHandler(getActivity());
        return inflater.inflate(R.layout.main_calendar_view, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(R.string.calendar);
        mCalendarKeyBtn = getActivity().findViewById(R.id.calendarKeyImageButton);
        mFindDateBtn = getActivity().findViewById(R.id.findDateBtn);
        mGoToTodayBtn = getActivity().findViewById(R.id.goToTodayBtn);
        // Setup caldroid fragment
        mCaldroidFragment = new CustomCaldroidFragment();
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        mToday = cal.getTime();

        // If Activity is created after rotation, get previous state and date selected
        if (savedInstanceState != null) {
            mCaldroidFragment.restoreStatesFromKey(savedInstanceState, "CALDROID_SAVED_STATE");
            mCurrentSelectedDate = new Date(savedInstanceState.getLong("selected_Date"));
        } else {
            mCurrentSelectedDate = mToday;
        }
        // Attach to the activity
        FragmentTransaction t = getActivity().getSupportFragmentManager().beginTransaction();
        t.replace(R.id.calendar1, mCaldroidFragment);
        t.commit();
        //If there was a selected date, go to and highlight date
        if (mCurrentSelectedDate != null) {
            highlightDateCell(mCurrentSelectedDate);
            mCaldroidFragment.refreshView();
        }
        setUpCaldroidListener();
        setUpCalendarKeyListener();
        mDatePickerDialogLauncher = new CustomDatePickerDialogLauncher(mCurrentSelectedDate, false, getContext());
        mDatePickerDialogLauncher.setDateSelectedListener(new CustomDatePickerDialogLauncher.DateSelectedListener() {
            @Override
            public void onStartDateSelected(Date startDate, Date endDate) {

            }

            @Override
            public void onEndDateSelected(Date startDate, Date endDate) {

            }

            @Override
            public void onDateSelected(Date date) {
                highlightDateCell(date);
                mCaldroidFragment.moveToDate(date);
                mCaldroidFragment.refreshView();
            }
        });
        setUpFindDateBtnListener();
        setUpGoToTodayBtnListener();
        mAdapters = mCaldroidFragment.getDatePagerAdapters();
        mSelectedMonth = 1;
        mSelectedYear = 1;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //Save state
        if (mCaldroidFragment != null) {
            mCaldroidFragment.saveStatesToKey(outState, "CALDROID_SAVED_STATE");
        }
        //Save selected date
        if (mCurrentSelectedDate != null) {
            outState.putLong("selected_Date", mCurrentSelectedDate.getTime());
        }
    }

    public void showCalendarKeyPopup(View v) {
        //Display key pop-up
        LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View popupView = layoutInflater.inflate(R.layout.popup_calendar_key, null);
        mKeyPopup = new PopupWindow(
                popupView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        mKeyPopup.setBackgroundDrawable(new BitmapDrawable());
        mKeyPopup.setOutsideTouchable(true);
        mKeyPopup.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                //Nothing, just close
            }
        });
        mKeyPopup.showAsDropDown(v, 35, -435);
    }

    public void highlightDateCell(Date newDateToHighlight) {
        //If previous date selected, revert its text color to black
        if (mCurrentSelectedDate != null && mCurrentSelectedDate != newDateToHighlight) {
            mCaldroidFragment.setTextColorForDate(R.color.caldroid_black, mCurrentSelectedDate);
            if (mCurrentSelectedDate.equals(mToday)) {
                mCaldroidFragment.setBackgroundDrawableForDate(getResources().getDrawable(R.drawable.red_border), mCurrentSelectedDate);
            } else {
                mCaldroidFragment.setBackgroundDrawableForDate(getResources().getDrawable(R.drawable.cell_bg), mCurrentSelectedDate);
            }
        }
        //Set selected dates text color to red
        mCaldroidFragment.setBackgroundDrawableForDate(getResources().getDrawable(R.drawable.calendar_blue_border), newDateToHighlight);
        mCurrentSelectedDate = newDateToHighlight;
    }

    private void setUpCalendarKeyListener() {
        //Calendar key button listener
        mCalendarKeyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCalendarKeyPopup(view);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        DateTime startRange = new DateTime(mSelectedYear, mSelectedMonth, 1, 0, 0);
        DateTime endRange = new DateTime(mSelectedYear, mSelectedMonth, 28, 0, 0);
        startRange = startRange.minusDays(14);
        endRange = endRange.plusDays(14);
        mExpenseDatesHM = mDatabaseHandler.getExpensesHMForCalendar(startRange, endRange, MainActivity.sUser);
        mIncomeDatesHM = mDatabaseHandler.getIncomeHMForCalendar(startRange, endRange, MainActivity.sUser);
        mLeaseEndDatesHM = mDatabaseHandler.getLeaseEndHMForCalendar(startRange, endRange, MainActivity.sUser);
        mLeaseStartDatesHM = mDatabaseHandler.getLeaseStartHMForCalendar(startRange, endRange, MainActivity.sUser);
        mCaldroidFragment.setEventIcons(mLeaseStartDatesHM, mLeaseEndDatesHM, mExpenseDatesHM, mIncomeDatesHM);
        for(int i = 0; i < mAdapters.size(); i++){
            CustomCalendarAdapter c = (CustomCalendarAdapter) mAdapters.get(i);
            c.updateDateData(mLeaseStartDatesHM, mLeaseEndDatesHM, mExpenseDatesHM, mIncomeDatesHM);
        }
    }

    private void setUpCaldroidListener() {
        //Caldroid listener()
        mCaldroidFragment.setCaldroidListener(new CaldroidListener() {

            @Override
            public void onSelectDate(Date date, View view) {
                //Launch SingleDateViewActivity and pass the dates information
                Intent intent = new Intent(getActivity(), SingleDateViewActivity.class);
                intent.putExtra("date", date);
                getActivity().startActivityForResult(intent, MainActivity.REQUEST_CALENDAR_VIEW);
            }

            @Override
            public void onChangeMonth(int month, int year) {
                mSelectedYear = year;
                mSelectedMonth = month;
                DateTime startRange = new DateTime(year, month, 1, 0, 0);
                DateTime endRange = new DateTime(year, month, 28, 0, 0);
                startRange = startRange.minusDays(14);
                endRange = endRange.plusDays(14);
                mExpenseDatesHM = mDatabaseHandler.getExpensesHMForCalendar(startRange, endRange, MainActivity.sUser);
                mIncomeDatesHM = mDatabaseHandler.getIncomeHMForCalendar(startRange, endRange, MainActivity.sUser);
                mLeaseEndDatesHM = mDatabaseHandler.getLeaseEndHMForCalendar(startRange, endRange, MainActivity.sUser);
                mLeaseStartDatesHM = mDatabaseHandler.getLeaseStartHMForCalendar(startRange, endRange, MainActivity.sUser);
                mCaldroidFragment.setEventIcons(mLeaseStartDatesHM, mLeaseEndDatesHM, mExpenseDatesHM, mIncomeDatesHM);
                for(int i = 0; i < mAdapters.size(); i++){
                    CustomCalendarAdapter c = (CustomCalendarAdapter) mAdapters.get(i);
                    c.updateDateData(mLeaseStartDatesHM, mLeaseEndDatesHM, mExpenseDatesHM, mIncomeDatesHM);
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
        mFindDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDatePickerDialogLauncher.launchSingleDatePickerDialog();
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        mDatePickerDialogLauncher.dismissDatePickerDialog();
        if(mKeyPopup != null){
            mKeyPopup.dismiss();
        }
    }

    private void setUpGoToTodayBtnListener(){
        mGoToTodayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                highlightDateCell(mToday);
                mCaldroidFragment.moveToDate(mToday);
                mCaldroidFragment.refreshView();
            }
        });
    }
}