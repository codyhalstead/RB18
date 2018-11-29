package com.rba18.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.rba18.R;
import com.rba18.helpers.DateAndCurrencyDisplayer;
import com.rba18.model.TypeTotal;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

public class TotalsListAdapter extends BaseAdapter {
    private ArrayList<TypeTotal> mTypeTotals;
    private Context mContext;
    private ColorStateList mHighlightColor;
    private int mMoneyFormatCode;

    public TotalsListAdapter(Context context, ArrayList<TypeTotal> typeTotals, ColorStateList highlightColor) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        mTypeTotals = typeTotals;
        mContext = context;
        mHighlightColor = highlightColor;
        mMoneyFormatCode = preferences.getInt("currency", DateAndCurrencyDisplayer.CURRENCY_US);
    }

    private static class ViewHolder {
        TextView typeLabelTV;
        TextView typeTotalAmountTV;
        TextView numberOfItemsTV;
        TextView numberOfItemsLabelTV;
    }

    @Override
    public int getCount() {
        if (mTypeTotals != null) {
            return mTypeTotals.size();
        }
        return 0;
    }

    @Override
    public TypeTotal getItem(int i) {
        return mTypeTotals.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        TypeTotal typeTotal = getItem(i);
        TotalsListAdapter.ViewHolder viewHolder;
        // Check if an existing view is being reused, otherwise inflate the view
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.row_total, viewGroup, false);
            viewHolder = new TotalsListAdapter.ViewHolder();

            viewHolder.typeLabelTV = view.findViewById(R.id.totalsRowTypeLabelTV);
            viewHolder.typeTotalAmountTV = view.findViewById(R.id.totalsRowTotalTV);
            viewHolder.numberOfItemsTV = view.findViewById(R.id.totalsRowOccurrenceNumberTV);
            viewHolder.numberOfItemsLabelTV = view.findViewById(R.id.totalsRowOccurrencesLabelTV);
            view.setTag(viewHolder);

        } else {
            viewHolder = (TotalsListAdapter.ViewHolder) view.getTag();
        }
        if (typeTotal != null) {
            viewHolder.typeLabelTV.setText(typeTotal.getTypeLabel());
            BigDecimal displayVal = typeTotal.getTotalAmount().setScale(2, RoundingMode.HALF_EVEN);
            if(displayVal.compareTo(new BigDecimal(0)) < 0){
                viewHolder.typeTotalAmountTV.setTextColor(mContext.getResources().getColor(R.color.red));
            } else {
                viewHolder.typeTotalAmountTV.setTextColor(mContext.getResources().getColor(R.color.green_colorPrimaryDark));
            }
            viewHolder.typeTotalAmountTV.setText(DateAndCurrencyDisplayer.getCurrencyToDisplay(mMoneyFormatCode, typeTotal.getTotalAmount()));
            String numberOfItemsString = typeTotal.getNumberOfItems() + "";
            viewHolder.numberOfItemsTV.setText(numberOfItemsString);
            if(typeTotal.getNumberOfItems() == 1) {
                viewHolder.numberOfItemsLabelTV.setText(R.string.space_occurrence);
            } else {
                viewHolder.numberOfItemsLabelTV.setText(R.string.space_occurrences);
            }
        }
        return view;
    }
}
