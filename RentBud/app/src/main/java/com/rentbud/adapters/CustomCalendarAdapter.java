package com.rentbud.adapters;

/**
 * Created by Cody on 1/10/2018.
 */

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.cody.rentbud.R;
import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidGridAdapter;
import com.roomorama.caldroid.CellView;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import hirondelle.date4j.DateTime;

import static android.support.constraint.Constraints.TAG;

public class CustomCalendarAdapter extends CaldroidGridAdapter {
    //Key = date, value = amount
    private HashMap<String, Integer> leaseStartDatesHM;
    private HashMap<String, Integer> leaseEndDatesHM;
    private HashMap<String, Integer> expenseDatesHM;
    private HashMap<String, Integer> incomeDatesHM;


    public CustomCalendarAdapter(Context context, int month, int year,
                                 Map<String, Object> caldroidData,
                                 Map<String, Object> extraData,
                                 HashMap<String, Integer> leaseStartDatesAndAmounts,
                                 HashMap<String , Integer> leaseEndDatesAndAmounts,
                                 HashMap<String, Integer> expenseDatesAndAmounts,
                                 HashMap<String, Integer> incomeDatesAndAmounts) {
        super(context, month, year, caldroidData, extraData);
        leaseStartDatesHM = leaseStartDatesAndAmounts;
        //leaseStartDatesHM.put(7, 8);
        leaseEndDatesHM = leaseEndDatesAndAmounts;
        expenseDatesHM = expenseDatesAndAmounts;
        incomeDatesHM = incomeDatesAndAmounts;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View cellView = convertView;

        // For reuse
        if (convertView == null) {
            cellView = inflater.inflate(R.layout.custom_calendar_cell, null);
        }

        int topPadding = cellView.getPaddingTop();
        int leftPadding = cellView.getPaddingLeft();
        int bottomPadding = cellView.getPaddingBottom();
        int rightPadding = cellView.getPaddingRight();
    
        TextView dateTV = cellView.findViewById(R.id.customCalCellDateTV);
        TextView leaseBeginAmountTV = cellView.findViewById(R.id.customCalCellLeaseBeginAmountTV);
        TextView leaseEndAmountTV = cellView.findViewById(R.id.customCalCellLeaseEndAmountTV);
        TextView expenseAmountTV = cellView.findViewById(R.id.customCalCellExpenseAmountTV);
        TextView incomeAmountTV = cellView.findViewById(R.id.customCalCellIncomeAmountTV);
        LinearLayout leaseBeginLL = cellView.findViewById(R.id.customCalCellLeaseBeginLL);
        LinearLayout leaseEndLL = cellView.findViewById(R.id.customCalCellLeaseEndLL);
        LinearLayout expenseLL = cellView.findViewById(R.id.customCalCellExpenseLL);
        LinearLayout incomeLL = cellView.findViewById(R.id.customCalCellIncomeLL);
        TextView spacer1 = cellView.findViewById(R.id.spacer1);
        TextView spacer2 = cellView.findViewById(R.id.spacer2);
        TextView spacer3 = cellView.findViewById(R.id.spacer3);
        TextView spacer4 = cellView.findViewById(R.id.spacer4);
        dateTV.setTextColor(Color.BLACK);
        // Get dateTime of this cell
        DateTime dateTime = this.datetimeList.get(position);
        Date date = new Date(dateTime.getYear() - 1900, dateTime.getMonth() - 1, dateTime.getDay(), 0, 0);
        Resources resources = context.getResources();
        // Set color of the dates in previous / next month
        if (dateTime.getMonth() != month) {
            dateTV.setTextColor(resources
                    .getColor(com.caldroid.R.color.caldroid_darker_gray));
            leaseBeginLL.setVisibility(View.INVISIBLE);
            leaseEndLL.setVisibility(View.INVISIBLE);
            expenseLL.setVisibility(View.INVISIBLE);
            incomeLL.setVisibility(View.INVISIBLE);
            spacer1.setVisibility(View.GONE);
            spacer2.setVisibility(View.GONE);
            spacer3.setVisibility(View.GONE);
            spacer4.setVisibility(View.GONE);
        }
        ////////////////////////////////////////////////////////////////////////////////////////////
        int amount = 0;
        if (leaseStartDatesHM != null) {
            if (leaseStartDatesHM.containsKey(date.toString())) {
                leaseBeginLL.setVisibility(View.VISIBLE);
                amount = leaseStartDatesHM.get(date.toString());
                if (amount <= 99) {
                    String amountString = amount + "";
                    leaseBeginAmountTV.setText(amountString);
                } else {
                    leaseBeginAmountTV.setText(R.string.over99);
                }
                spacer1.setVisibility(View.GONE);
            } else {
                leaseBeginLL.setVisibility(View.GONE);
                spacer1.setVisibility(View.INVISIBLE);
            }
        }
        if (leaseEndDatesHM != null) {
            if (leaseEndDatesHM.containsKey(date.toString())) {
                leaseEndLL.setVisibility(View.VISIBLE);
                amount = leaseEndDatesHM.get(date.toString());
                if (amount <= 99) {
                    String amountString = amount + "";
                    leaseEndAmountTV.setText(amountString);
                } else {
                    leaseEndAmountTV.setText(R.string.over99);
                }
                spacer2.setVisibility(View.GONE);
            } else {
                leaseEndLL.setVisibility(View.GONE);
                spacer2.setVisibility(View.INVISIBLE);
            }
        }
        if (expenseDatesHM != null) {
            if (expenseDatesHM.containsKey(date.toString())) {
                expenseLL.setVisibility(View.VISIBLE);
                amount = expenseDatesHM.get(date.toString());
                if (amount <= 99) {
                    String amountString = amount + "";
                    expenseAmountTV.setText(amountString);
                } else {
                    expenseAmountTV.setText(R.string.over99);
                }
                spacer3.setVisibility(View.GONE);
            } else {
                expenseLL.setVisibility(View.GONE);
                spacer3.setVisibility(View.INVISIBLE);
            }
        }
        if (incomeDatesHM != null) {
            if (incomeDatesHM.containsKey(date.toString())) {
                incomeLL.setVisibility(View.VISIBLE);
                amount =  incomeDatesHM.get(date.toString());
                if (amount <= 99) {
                    String amountString = amount + "";
                    incomeAmountTV.setText(amountString);
                } else {
                    incomeAmountTV.setText(R.string.over99);
                }
                spacer4.setVisibility(View.GONE);
            } else {
                incomeLL.setVisibility(View.GONE);
                spacer4.setVisibility(View.INVISIBLE);
            }
        }

        ////////////////////////////////////////////////////////////////////////////////////////////
        boolean shouldResetDiabledView = false;
        boolean shouldResetSelectedView = false;
        // Customize for disabled dates and date outside min/max dates
        if ((minDateTime != null && dateTime.lt(minDateTime))
                || (maxDateTime != null && dateTime.gt(maxDateTime))
                || (disableDates != null && disableDates.indexOf(dateTime) != -1)) {
            dateTV.setTextColor(CaldroidFragment.disabledTextColor);
            if (CaldroidFragment.disabledBackgroundDrawable == -1) {
                cellView.setBackgroundResource(com.caldroid.R.drawable.disable_cell);
            } else {
                cellView.setBackgroundResource(CaldroidFragment.disabledBackgroundDrawable);
            }
            if (dateTime.equals(getToday())) {
                cellView.setBackgroundResource(com.caldroid.R.drawable.red_border_gray_bg);
            }
        } else {
            shouldResetDiabledView = true;
        }
        // Customize for selected dates
        if (selectedDates != null && selectedDates.indexOf(dateTime) != -1) {
            cellView.setBackgroundColor(resources
                    .getColor(com.caldroid.R.color.caldroid_sky_blue));
            dateTV.setTextColor(Color.BLACK);
        } else {
            shouldResetSelectedView = true;
        }
        if (shouldResetDiabledView && shouldResetSelectedView) {
            // Customize for today
            if (dateTime.equals(getToday())) {
                cellView.setBackgroundResource(com.caldroid.R.drawable.red_border);
            } else {
                cellView.setBackgroundResource(com.caldroid.R.drawable.cell_bg);
            }
        }
        //Set text
        String dateTimeString = "" + dateTime.getDay();
        dateTV.setText(dateTimeString);
        //tv2.setText("");
        // Somehow after setBackgroundResource, the padding collapse.
        // This is to recover the padding
        cellView.setPadding(leftPadding, topPadding, rightPadding,
                bottomPadding);
        // Set custom color if required
        setCustomResources(dateTime, cellView, dateTV);
        return cellView;
    }

    public void updateDateData(HashMap<String, Integer> leaseStartDatesAndAmounts,
                               HashMap<String, Integer> leaseEndDatesAndAmounts,
                               HashMap<String, Integer> expenseDatesAndAmounts,
                               HashMap<String, Integer> incomeDatesAndAmounts) {
        leaseStartDatesHM = leaseStartDatesAndAmounts;
        leaseEndDatesHM = leaseEndDatesAndAmounts;
        expenseDatesHM = expenseDatesAndAmounts;
        incomeDatesHM = incomeDatesAndAmounts;
        this.notifyDataSetChanged();
    }

}