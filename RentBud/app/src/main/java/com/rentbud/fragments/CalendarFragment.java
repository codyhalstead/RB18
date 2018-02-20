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
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.PopupWindow;

import com.example.cody.rentbud.R;
import com.rentbud.activities.SingleDateViewActivity;
import com.roomorama.caldroid.CaldroidListener;

import java.util.Calendar;
import java.util.Date;

public class CalendarFragment extends android.support.v4.app.Fragment {
    private CustomCaldroidFragment caldroidFragment;
    private DatePickerDialog.OnDateSetListener dateSetListener;
    private Date currentSelectedDate;
    Button findDateBtn;
    ImageButton calendarKeyBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        caldroidFragment = new CustomCaldroidFragment();
        return inflater.inflate(R.layout.main_calendar_view, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Calendar View");
        calendarKeyBtn = getActivity().findViewById(R.id.calendarKeyImageButton);
        findDateBtn = getActivity().findViewById(R.id.findDateBtn);
        // Setup caldroid fragment
        caldroidFragment = new CustomCaldroidFragment();
        // If Activity is created after rotation, get previous state and date selected
        if (savedInstanceState != null) {
            caldroidFragment.restoreStatesFromKey(savedInstanceState,"CALDROID_SAVED_STATE");
            currentSelectedDate = new Date(savedInstanceState.getLong("selected_Date"));
        }
        //TODO May need later
        // If activity is created from fresh
        //else {
            //Bundle args = new Bundle();
            //Calendar cal = Calendar.getInstance();
            //args.putInt(CaldroidFragment.MONTH, cal.get(Calendar.MONTH) + 1);
            //args.putInt(CaldroidFragment.YEAR, cal.get(Calendar.YEAR));
            //args.putBoolean(CaldroidFragment.ENABLE_SWIPE, true);
            //args.putBoolean(CaldroidFragment.SIX_WEEKS_IN_CALENDAR, true);
        //}
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
        setUpdateSelectedDateListener();
        setUpFindDateBtnListener();
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
        PopupWindow popupWindow;
        LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View popupView = layoutInflater.inflate(R.layout.popup_calendar_key, null);
        popupWindow = new PopupWindow(
                popupView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setOutsideTouchable(true);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
            //Nothing, just close
            }
        });
        popupWindow.showAsDropDown(v);
    }

    public void highlightDateCell(Date newDateToHighlight) {
        //If previous date selected, revert its text color to black
        if (currentSelectedDate != null && currentSelectedDate != newDateToHighlight) {
            caldroidFragment.setTextColorForDate(R.color.caldroid_black, currentSelectedDate);
        }
        //Set selected dates text color to red
        caldroidFragment.setTextColorForDate(R.color.red, newDateToHighlight);
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

    private void setUpCaldroidListener(){
        //Caldroid listener()
        caldroidFragment.setCaldroidListener(new CaldroidListener() {

            @Override
            public void onSelectDate(Date date, View view) {
                //Launch SingleDateViewActivity and pass the dates information
                Intent intent = new Intent(getActivity(), SingleDateViewActivity.class);
                intent.putExtra("date", date);
                startActivity(intent);
            }

            @Override
            public void onChangeMonth(int month, int year) {

            }

            @Override
            public void onLongClickDate(Date date, View view) {

            }

            @Override
            public void onCaldroidViewCreated() {

            }
        });
    }

    private void setUpFindDateBtnListener(){
        findDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Show date picker pop-up
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dialog = new DatePickerDialog(getContext(), android.R.style.Theme_Holo_Light_Dialog_MinWidth, dateSetListener, year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });
    }

    private void setUpdateSelectedDateListener(){
        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                //Once user selects date from date picker pop-up, highlight and go to date
                year = year - 1900;
                Date date = new Date(year, month, day);
                highlightDateCell(date);
                caldroidFragment.moveToDate(date);
                caldroidFragment.refreshView();
            }
        };
    }
}