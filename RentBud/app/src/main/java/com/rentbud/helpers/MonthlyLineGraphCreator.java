package com.rentbud.helpers;

import android.content.Context;
import android.graphics.Color;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.cody.rentbud.R;
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
    LineChart lineChart;
    Button leftBtn, rightBtn;
    TextView yearTV;
    Context context;
    Date initialYear;
    float[] expenseValues;
    float[] incomeValues;
    private LineData lineData;
    MonthlyLineGraphCreator.OnButtonsClickedListener onButtonsClickedListener;

    public MonthlyLineGraphCreator(Context context, LineChart lineChart, Button leftBtn, Button rightBtn, TextView yearTV, Date initialYear){
        this.lineChart = lineChart;
        this.leftBtn = leftBtn;
        this.rightBtn = rightBtn;
        this.yearTV = yearTV;
        this.context = context;
        this.initialYear = initialYear;
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
        this.onButtonsClickedListener = listener;
        leftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onLeftBtnClicked();
            }
        });
        rightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onRightBtnClicked();
            }
        });
        yearTV.setOnClickListener(new View.OnClickListener() {
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
        this.expenseValues = monthlyExpenseList;
        this.incomeValues = monthlyIncomeList;
        lineData = new LineData(getXValues(), getLineDataValues());
        yearTV.setText(year);
        lineChart.setData(lineData);
        lineChart.animateY(1000);
    }

    private List<ILineDataSet> getLineDataValues() {
        ArrayList<ILineDataSet> lineDataSets;
        ArrayList<Entry> expenseEntries = new ArrayList<>();
        ArrayList<Entry> incomeEntries = new ArrayList<>();
        for (int i = 0; i < expenseValues.length; i++) {
            expenseEntries.add(new Entry(expenseValues[i], i));
        }
        for (int i = 0; i < incomeValues.length; i++) {
            incomeEntries.add(new Entry(incomeValues[i], i));
        }
        LineDataSet incomeLineDataSet = new LineDataSet(incomeEntries, context.getResources().getString(R.string.income));
        incomeLineDataSet.setColor(Color.GREEN);
        incomeLineDataSet.setDrawValues(false);
        incomeLineDataSet.setCircleColor(Color.BLACK);
        incomeLineDataSet.setLineWidth(2);
        LineDataSet expenseLineDataSet = new LineDataSet(expenseEntries, context.getResources().getString(R.string.expenses));
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
        xValues.add(context.getResources().getString(R.string.jan));
        xValues.add(context.getResources().getString(R.string.feb));
        xValues.add(context.getResources().getString(R.string.mar));
        xValues.add(context.getResources().getString(R.string.apr));
        xValues.add(context.getResources().getString(R.string.may));
        xValues.add(context.getResources().getString(R.string.jun));
        xValues.add(context.getResources().getString(R.string.jul));
        xValues.add(context.getResources().getString(R.string.aug));
        xValues.add(context.getResources().getString(R.string.sep));
        xValues.add(context.getResources().getString(R.string.oct));
        xValues.add(context.getResources().getString(R.string.nov));
        xValues.add(context.getResources().getString(R.string.dec));
        return xValues;
    }
}
