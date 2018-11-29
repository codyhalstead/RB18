package com.rba18.helpers;

import android.content.Context;
import android.graphics.Color;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.rba18.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MonthlyLineGraphCreator {
    private LineChart mLineChart;
    private Button mLeftBtn, mRightBtn;
    private TextView mYearTV;
    private Context mContext;
    private Date mInitialYear;
    private float[] mExpenseValues, mIncomeValues;
    private LineData mLineData;
    private MonthlyLineGraphCreator.OnButtonsClickedListener mOnButtonsClickedListener;

    public MonthlyLineGraphCreator(Context context, LineChart lineChart, Button leftBtn, Button rightBtn, TextView yearTV, Date initialYear){
        this.mLineChart = lineChart;
        this.mLeftBtn = leftBtn;
        this.mRightBtn = rightBtn;
        this.mYearTV = yearTV;
        this.mContext = context;
        this.mInitialYear = initialYear;
        lineChart.setDescription("");
        lineChart.setClickable(false);
        lineChart.setHighlightPerTapEnabled(false);
        lineChart.setHighlightPerDragEnabled(false);
        lineChart.getAxisLeft().setAxisMinValue(0);
        lineChart.getAxisRight().setAxisMinValue(0);
        YAxis yAxisLeft = lineChart.getAxisLeft();
        yAxisLeft.setEnabled(false);
    }

    public void setDateSelectedListener(final OnButtonsClickedListener listener) {
        //Sets color listener
        this.mOnButtonsClickedListener = listener;
        mLeftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onLeftBtnClicked();
            }
        });
        mRightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onRightBtnClicked();
            }
        });
        mYearTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onDateTVClicked();
            }
        });
    }

    public interface OnButtonsClickedListener{
        void onLeftBtnClicked();
        void onRightBtnClicked();
        void onDateTVClicked();
    }

    public void setIncomeExpenseData(float[] monthlyIncomeList, float[] monthlyExpenseList, Date yearChangedTo){
        String year = (String) DateFormat.format("yyyy", yearChangedTo);
        this.mExpenseValues = monthlyExpenseList;
        this.mIncomeValues = monthlyIncomeList;
        mLineData = new LineData(getXValues(), getLineDataValues());
        mYearTV.setText(year);
        mLineChart.setData(mLineData);
        mLineChart.animateY(1000);
    }

    private List<ILineDataSet> getLineDataValues() {
        ArrayList<ILineDataSet> lineDataSets;
        ArrayList<Entry> expenseEntries = new ArrayList<>();
        ArrayList<Entry> incomeEntries = new ArrayList<>();
        for (int i = 0; i < mExpenseValues.length; i++) {
            expenseEntries.add(new Entry(mExpenseValues[i], i));
        }
        for (int i = 0; i < mIncomeValues.length; i++) {
            incomeEntries.add(new Entry(mIncomeValues[i], i));
        }
        LineDataSet incomeLineDataSet = new LineDataSet(incomeEntries, mContext.getResources().getString(R.string.income));
        incomeLineDataSet.setColor(Color.GREEN);
        incomeLineDataSet.setDrawValues(false);
        incomeLineDataSet.setCircleColor(Color.BLACK);
        incomeLineDataSet.setLineWidth(2);
        LineDataSet expenseLineDataSet = new LineDataSet(expenseEntries, mContext.getResources().getString(R.string.expenses));
        expenseLineDataSet.setColor(Color.RED);
        expenseLineDataSet.setDrawValues(false);
        expenseLineDataSet.setCircleColor(Color.BLACK);
        expenseLineDataSet.setLineWidth(2);
        lineDataSets = new ArrayList<>();
        lineDataSets.add(incomeLineDataSet);
        lineDataSets.add(expenseLineDataSet);
        return lineDataSets;
    }

    private List<String> getXValues() {
        ArrayList<String> xValues = new ArrayList<>();
        xValues.add(mContext.getResources().getString(R.string.jan));
        xValues.add(mContext.getResources().getString(R.string.feb));
        xValues.add(mContext.getResources().getString(R.string.mar));
        xValues.add(mContext.getResources().getString(R.string.apr));
        xValues.add(mContext.getResources().getString(R.string.may));
        xValues.add(mContext.getResources().getString(R.string.jun));
        xValues.add(mContext.getResources().getString(R.string.jul));
        xValues.add(mContext.getResources().getString(R.string.aug));
        xValues.add(mContext.getResources().getString(R.string.sep));
        xValues.add(mContext.getResources().getString(R.string.oct));
        xValues.add(mContext.getResources().getString(R.string.nov));
        xValues.add(mContext.getResources().getString(R.string.dec));
        return xValues;
    }
}
