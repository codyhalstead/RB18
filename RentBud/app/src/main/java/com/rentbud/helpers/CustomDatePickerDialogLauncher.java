package com.rentbud.helpers;

import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import com.example.cody.rentbud.R;

import java.util.Calendar;
import java.util.Date;

public class CustomDatePickerDialogLauncher {
    Date startDatePreset, endDatePreset, singleDatePreset, startDateLimit, endDateLimit;
    DatePickerDialog datePickerDialog;
    Context context;
    Boolean limitToTwoYears, yearOnly;
    DatePickerDialog.OnDateSetListener onStartDateSet, onEndDateSet, onSingleDateSet;
    DateSelectedListener DateSelectedListener;

    public CustomDatePickerDialogLauncher(Date startDatePreset, Date endDatePreset, final boolean limitToTwoYears, Context context) {
        this.startDatePreset = startDatePreset;
        this.endDatePreset = endDatePreset;
        this.context = context;
        this.limitToTwoYears = limitToTwoYears;
        Calendar cal = Calendar.getInstance();
        cal.set(1971, 0, 0);
        this.startDateLimit = cal.getTime();
        cal.set(2037, 11, 31);
        this.endDateLimit = cal.getTime();
        onStartDateSet = new DatePickerDialog.OnDateSetListener() {
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
                CustomDatePickerDialogLauncher.this.startDatePreset = cal.getTime();
                if (limitToTwoYears) {
                    limitDateRangesToTwoYearsFromStart(cal);
                }
                if (DateSelectedListener != null) {
                    DateSelectedListener.onStartDateSelected(CustomDatePickerDialogLauncher.this.startDatePreset, CustomDatePickerDialogLauncher.this.endDatePreset);
                } else {
                    datePickerDialog.dismiss();
                }
            }
        };
        onEndDateSet = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.YEAR, year);
                cal.set(Calendar.MONTH, month);
                cal.set(Calendar.DATE, day);
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
                CustomDatePickerDialogLauncher.this.endDatePreset = cal.getTime();
                if (limitToTwoYears) {
                    limitDateRangesToTwoYearsFromEnd(cal);
                }
                if (DateSelectedListener != null) {
                    DateSelectedListener.onEndDateSelected(CustomDatePickerDialogLauncher.this.startDatePreset, CustomDatePickerDialogLauncher.this.endDatePreset);
                } else {
                    datePickerDialog.dismiss();
                }
            }
        };
    }

    public CustomDatePickerDialogLauncher(Date singleDatePreset, Boolean yearOnly, Context context) {
        this.singleDatePreset = singleDatePreset;
        this.context = context;
        this.yearOnly = yearOnly;
        Calendar cal = Calendar.getInstance();
        cal.set(1971, 0, 0);
        this.startDateLimit = cal.getTime();
        cal.set(2037, 11, 31);
        this.endDateLimit = cal.getTime();
        onSingleDateSet = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.YEAR, year);
                cal.set(Calendar.MONTH, month);
                cal.set(Calendar.DATE, day);
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
                CustomDatePickerDialogLauncher.this.singleDatePreset = cal.getTime();
                if (DateSelectedListener != null) {
                    DateSelectedListener.onDateSelected(CustomDatePickerDialogLauncher.this.singleDatePreset);
                } else {
                    datePickerDialog.dismiss();
                }
            }
        };
    }

    public interface DateSelectedListener {
        void onStartDateSelected(Date startDate, Date endDate);

        void onEndDateSelected(Date startDate, Date endDate);

        void onDateSelected(Date date);
    }

    public void setDateSelectedListener(DateSelectedListener listener) {
        this.DateSelectedListener = listener;
    }


    public void launchStartDatePickerDialog() {
        if (onStartDateSet != null) {
            Calendar cal = Calendar.getInstance();
            if (startDatePreset != null) {
                cal.setTime(startDatePreset);
            }
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);
            int day = cal.get(Calendar.DAY_OF_MONTH);
            datePickerDialog = new DatePickerDialog(context, android.R.style.Theme_Holo_Light_Dialog_MinWidth, onStartDateSet, year, month, day);
            datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            datePickerDialog.getDatePicker().setMaxDate(endDateLimit.getTime());
            datePickerDialog.getDatePicker().setMinDate(startDateLimit.getTime());
            datePickerDialog.show();
        }
    }

    public void launchEndDatePickerDialog() {
        if (onEndDateSet != null) {
            Calendar cal = Calendar.getInstance();
            if (endDatePreset != null) {
                cal.setTime(endDatePreset);
            }
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);
            int day = cal.get(Calendar.DAY_OF_MONTH);
            datePickerDialog = new DatePickerDialog(context, android.R.style.Theme_Holo_Light_Dialog_MinWidth, onEndDateSet, year, month, day);
            datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            datePickerDialog.getDatePicker().setMaxDate(endDateLimit.getTime());
            datePickerDialog.getDatePicker().setMinDate(startDateLimit.getTime());
            datePickerDialog.show();
        }
    }

    public void launchSingleDatePickerDialog() {
        if (onSingleDateSet != null) {
            if (yearOnly) {
                Calendar cal = Calendar.getInstance();
                if (singleDatePreset != null) {
                    cal.setTime(singleDatePreset);
                }
                int mYear = cal.get(Calendar.YEAR);
                int mMonth = cal.get(Calendar.MONTH);
                int mDay = cal.get(Calendar.DAY_OF_MONTH);
                datePickerDialog = new DatePickerDialog(context, android.R.style.Theme_Holo_Light_Dialog_MinWidth, onSingleDateSet, mYear, mMonth, mDay);
                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePickerDialog.getDatePicker().findViewById(context.getResources().getIdentifier("month","id","android")).setVisibility(View.GONE);
                datePickerDialog.getDatePicker().findViewById(context.getResources().getIdentifier("day","id","android")).setVisibility(View.GONE);
                datePickerDialog.show();
            } else {
                Calendar cal = Calendar.getInstance();
                if (singleDatePreset != null) {
                    cal.setTime(singleDatePreset);
                }
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);
                datePickerDialog = new DatePickerDialog(context, android.R.style.Theme_Holo_Light_Dialog_MinWidth, onSingleDateSet, year, month, day);
                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePickerDialog.getDatePicker().setMaxDate(endDateLimit.getTime());
                datePickerDialog.getDatePicker().setMinDate(startDateLimit.getTime());
                datePickerDialog.show();
            }
        }
    }

    public void dismissDatePickerDialog() {
        if (datePickerDialog != null) {
            datePickerDialog.dismiss();
        }
    }

    private void limitDateRangesToTwoYearsFromStart(Calendar calendar) {
        calendar.setTime(startDatePreset);
        calendar.add(Calendar.YEAR, 2);
        Date dateInTwoYears = calendar.getTime();
        if (endDatePreset.after(dateInTwoYears)) {
            endDatePreset = dateInTwoYears;
            Toast.makeText(context, R.string.max_dates_range, Toast.LENGTH_SHORT).show();
        }
    }

    private void limitDateRangesToTwoYearsFromEnd(Calendar calendar) {
        calendar.setTime(endDatePreset);
        calendar.add(Calendar.YEAR, -2);
        Date dateTwoYearsAgo = calendar.getTime();
        if (startDatePreset.before(dateTwoYearsAgo)) {
            startDatePreset = dateTwoYearsAgo;
            Toast.makeText(context, R.string.max_dates_range, Toast.LENGTH_SHORT).show();
        }
    }

    public void setSingleDatePreset(Date date){
        this.singleDatePreset = date;
    }
}
