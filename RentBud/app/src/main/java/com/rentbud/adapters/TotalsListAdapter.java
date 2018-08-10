package com.rentbud.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.cody.rentbud.R;
import com.rentbud.helpers.MainArrayDataMethods;
import com.rentbud.model.Tenant;
import com.rentbud.model.TypeTotal;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class TotalsListAdapter extends BaseAdapter {
    private ArrayList<TypeTotal> typeTotals;
    private Context context;
    private ColorStateList highlightColor;

    public TotalsListAdapter(Context context, ArrayList<TypeTotal> typeTotals, ColorStateList highlightColor) {
        this.typeTotals = typeTotals;
        this.context = context;
        this.highlightColor = highlightColor;
    }

    static class ViewHolder {
        TextView typeLabelTV;
        TextView typeTotalAmountTV;
        TextView numberOfItemsTV;
        TextView numberOfItemsLabelTV;
    }

    @Override
    public int getCount() {
        if (typeTotals != null) {
            return typeTotals.size();
        }
        return 0;
    }

    @Override
    public TypeTotal getItem(int i) {
        return typeTotals.get(i);
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
            view = LayoutInflater.from(context).inflate(R.layout.row_total, viewGroup, false);
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
                viewHolder.typeTotalAmountTV.setTextColor(context.getResources().getColor(R.color.red));
            } else {
                viewHolder.typeTotalAmountTV.setTextColor(context.getResources().getColor(R.color.green_colorPrimaryDark));
            }
            NumberFormat usdCostFormat = NumberFormat.getCurrencyInstance(Locale.US);
            usdCostFormat.setMinimumFractionDigits(2);
            usdCostFormat.setMaximumFractionDigits(2);
            viewHolder.typeTotalAmountTV.setText(usdCostFormat.format(displayVal.doubleValue()));
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
