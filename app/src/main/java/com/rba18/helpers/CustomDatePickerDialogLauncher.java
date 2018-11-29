package com.rba18.helpers;

import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import com.rba18.R;

import java.util.Calendar;
import java.util.Date;

public class CustomDatePickerDialogLauncher {
    private Date mStartDatePreset, mEndDatePreset, mSingleDatePreset, mStartDateLimit, mEndDateLimit;
    private DatePickerDialog mDatePickerDialog;
    private Context mContext;
    private Boolean mLimitToTwoYears, mYearOnly;
    private DatePickerDialog.OnDateSetListener mOnStartDateSet, mOnEndDateSet, mOnSingleDateSet;
    private DateSelectedListener mDateSelectedListener;

    public CustomDatePickerDialogLauncher(Date startDatePreset, Date endDatePreset, final boolean limitToTwoYears, Context context) {
        mStartDatePreset = startDatePreset;
        mEndDatePreset = endDatePreset;
        mContext = context;
        mLimitToTwoYears = limitToTwoYears;
        Calendar cal = Calendar.getInstance();
        cal.set(1971, 0, 0);
        mStartDateLimit = cal.getTime();
        cal.set(2037, 11, 31);
        this.mEndDateLimit = cal.getTime();
        mOnStartDateSet = new DatePickerDialog.OnDateSetListener() {
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
                CustomDatePickerDialogLauncher.this.mStartDatePreset = cal.getTime();
                if (limitToTwoYears) {
                    limitDateRangesToTwoYearsFromStart(cal);
                }
                if (mDateSelectedListener != null) {
                    mDateSelectedListener.onStartDateSelected(CustomDatePickerDialogLauncher.this.mStartDatePreset, CustomDatePickerDialogLauncher.this.mEndDatePreset);
                } else {
                    mDatePickerDialog.dismiss();
                }
            }
        };
        mOnEndDateSet = new DatePickerDialog.OnDateSetListener() {
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
                CustomDatePickerDialogLauncher.this.mEndDatePreset = cal.getTime();
                if (limitToTwoYears) {
                    limitDateRangesToTwoYearsFromEnd(cal);
                }
                if (mDateSelectedListener != null) {
                    mDateSelectedListener.onEndDateSelected(CustomDatePickerDialogLauncher.this.mStartDatePreset, CustomDatePickerDialogLauncher.this.mEndDatePreset);
                } else {
                    mDatePickerDialog.dismiss();
                }
            }
        };
    }

    public CustomDatePickerDialogLauncher(Date singleDatePreset, Boolean yearOnly, Context context) {
        mSingleDatePreset = singleDatePreset;
        mContext = context;
        mYearOnly = yearOnly;
        Calendar cal = Calendar.getInstance();
        cal.set(1971, 0, 0);
        mStartDateLimit = cal.getTime();
        cal.set(2037, 11, 31);
        mEndDateLimit = cal.getTime();
        mOnSingleDateSet = new DatePickerDialog.OnDateSetListener() {
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
                CustomDatePickerDialogLauncher.this.mSingleDatePreset = cal.getTime();
                if (mDateSelectedListener != null) {
                    mDateSelectedListener.onDateSelected(CustomDatePickerDialogLauncher.this.mSingleDatePreset);
                } else {
                    mDatePickerDialog.dismiss();
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
        mDateSelectedListener = listener;
    }


    public void launchStartDatePickerDialog() {
        if (mOnStartDateSet != null) {
            Calendar cal = Calendar.getInstance();
            if (mStartDatePreset != null) {
                cal.setTime(mStartDatePreset);
            }
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);
            int day = cal.get(Calendar.DAY_OF_MONTH);
            mDatePickerDialog = new DatePickerDialog(mContext, android.R.style.Theme_Holo_Light_Dialog_MinWidth, mOnStartDateSet, year, month, day);
            mDatePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            mDatePickerDialog.getDatePicker().setMaxDate(mEndDateLimit.getTime());
            mDatePickerDialog.getDatePicker().setMinDate(mStartDateLimit.getTime());
            mDatePickerDialog.show();
        }
    }

    public void launchEndDatePickerDialog() {
        if (mOnEndDateSet != null) {
            Calendar cal = Calendar.getInstance();
            if (mEndDatePreset != null) {
                cal.setTime(mEndDatePreset);
            }
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);
            int day = cal.get(Calendar.DAY_OF_MONTH);
            mDatePickerDialog = new DatePickerDialog(mContext, android.R.style.Theme_Holo_Light_Dialog_MinWidth, mOnEndDateSet, year, month, day);
            mDatePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            mDatePickerDialog.getDatePicker().setMaxDate(mEndDateLimit.getTime());
            mDatePickerDialog.getDatePicker().setMinDate(mStartDateLimit.getTime());
            mDatePickerDialog.show();
        }
    }

    public void launchSingleDatePickerDialog() {
        if (mOnSingleDateSet != null) {
            if (mYearOnly) {
                Calendar cal = Calendar.getInstance();
                if (mSingleDatePreset != null) {
                    cal.setTime(mSingleDatePreset);
                }
                int mYear = cal.get(Calendar.YEAR);
                int mMonth = cal.get(Calendar.MONTH);
                int mDay = cal.get(Calendar.DAY_OF_MONTH);
                mDatePickerDialog = new DatePickerDialog(mContext, android.R.style.Theme_Holo_Light_Dialog_MinWidth, mOnSingleDateSet, mYear, mMonth, mDay);
                mDatePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                mDatePickerDialog.getDatePicker().findViewById(mContext.getResources().getIdentifier("month","id","android")).setVisibility(View.GONE);
                mDatePickerDialog.getDatePicker().findViewById(mContext.getResources().getIdentifier("day","id","android")).setVisibility(View.GONE);
                mDatePickerDialog.show();
            } else {
                Calendar cal = Calendar.getInstance();
                if (mSingleDatePreset != null) {
                    cal.setTime(mSingleDatePreset);
                }
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);
                mDatePickerDialog = new DatePickerDialog(mContext, android.R.style.Theme_Holo_Light_Dialog_MinWidth, mOnSingleDateSet, year, month, day);
                mDatePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                mDatePickerDialog.getDatePicker().setMaxDate(mEndDateLimit.getTime());
                mDatePickerDialog.getDatePicker().setMinDate(mStartDateLimit.getTime());
                mDatePickerDialog.show();
            }
        }
    }

    public void dismissDatePickerDialog() {
        if (mDatePickerDialog != null) {
            mDatePickerDialog.dismiss();
        }
    }

    private void limitDateRangesToTwoYearsFromStart(Calendar calendar) {
        calendar.setTime(mStartDatePreset);
        calendar.add(Calendar.YEAR, 2);
        Date dateInTwoYears = calendar.getTime();
        if (mEndDatePreset.after(dateInTwoYears)) {
            mEndDatePreset = dateInTwoYears;
            Toast.makeText(mContext, R.string.max_dates_range, Toast.LENGTH_SHORT).show();
        }
    }

    private void limitDateRangesToTwoYearsFromEnd(Calendar calendar) {
        calendar.setTime(mEndDatePreset);
        calendar.add(Calendar.YEAR, -2);
        Date dateTwoYearsAgo = calendar.getTime();
        if (mStartDatePreset.before(dateTwoYearsAgo)) {
            mStartDatePreset = dateTwoYearsAgo;
            Toast.makeText(mContext, R.string.max_dates_range, Toast.LENGTH_SHORT).show();
        }
    }

    public void setSingleDatePreset(Date date){
        mSingleDatePreset = date;
    }
}
