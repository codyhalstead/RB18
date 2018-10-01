package com.rentbud.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.TextAppearanceSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.example.cody.rentbud.R;
import com.rentbud.helpers.DateAndCurrencyDisplayer;
import com.rentbud.helpers.MainArrayDataMethods;
import com.rentbud.model.PaymentLogEntry;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Cody on 3/27/2018.
 */

public class IncomeListAdapter extends BaseAdapter implements Filterable {
    private ArrayList<PaymentLogEntry> paymentArray;
    private ArrayList<PaymentLogEntry> filteredResults;
    private SharedPreferences preferences;
    private Context context;
    private String searchText;
    private ColorStateList highlightColor;
    MainArrayDataMethods dataMethods;
    private Date todaysDate;
    OnDataChangeListener mOnDataChangeListener;
    private int dateFormatCode, moneyFormatCode;

    public IncomeListAdapter(Context context, ArrayList<PaymentLogEntry> paymentArray, ColorStateList highlightColor) {
        super();
        this.preferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.paymentArray = paymentArray;
        this.filteredResults = paymentArray;
        this.context = context;
        this.searchText = "";
        this.highlightColor = highlightColor;
        this.dataMethods = new MainArrayDataMethods();
        this.todaysDate = new Date(System.currentTimeMillis());
        this.dateFormatCode = preferences.getInt("dateFormat", DateAndCurrencyDisplayer.DATE_MMDDYYYY);
        this.moneyFormatCode = preferences.getInt("currency", DateAndCurrencyDisplayer.CURRENCY_US);
    }

    public void setOnDataChangeListener(OnDataChangeListener onDataChangeListener){
        mOnDataChangeListener = onDataChangeListener;
    }

    static class ViewHolder{
        TextView amountTV;
        TextView dateTV;
        TextView typeTV;
        TextView descriptionTV;
        TextView wasReceivedTV;
    }

    @Override
    public int getCount() {
        if(filteredResults != null) {
            return filteredResults.size();
        }
        return 0;
    }

    @Override
    public PaymentLogEntry getItem(int i) {
        return filteredResults.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        PaymentLogEntry income = getItem(position);
        IncomeListAdapter.ViewHolder viewHolder;
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.row_income, viewGroup, false);
            viewHolder = new IncomeListAdapter.ViewHolder();

            viewHolder.amountTV = convertView.findViewById(R.id.incomeRowAmountTV);
            viewHolder.dateTV = convertView.findViewById(R.id.incomeRowDateTV);
            viewHolder.typeTV = convertView.findViewById(R.id.incomeRowTypeTV);
            viewHolder.descriptionTV = convertView.findViewById(R.id.incomeRowDescriptionTV);
            viewHolder.wasReceivedTV = convertView.findViewById(R.id.incomeRowWasReceivedTV);
            //viewHolder.position = position;

            convertView.setTag(viewHolder);

        } else {
            viewHolder = (IncomeListAdapter.ViewHolder) convertView.getTag();
        }
        viewHolder.amountTV.setTextColor(context.getResources().getColor(R.color.green_colorPrimaryDark));
        if(income != null){
            viewHolder.amountTV.setText(DateAndCurrencyDisplayer.getCurrencyToDisplay(moneyFormatCode, income.getAmount()));
            viewHolder.dateTV.setText(DateAndCurrencyDisplayer.getDateToDisplay(dateFormatCode, income.getDate()));
            if( todaysDate.compareTo(income.getDate()) < 0){
                convertView.setBackgroundColor(convertView.getResources().getColor(R.color.rowDarkenedBackground));
            } else {
                convertView.setBackgroundColor(convertView.getResources().getColor(R.color.white));
            }
            if(income.getIsCompleted()) {
                viewHolder.wasReceivedTV.setText(R.string.received);
                viewHolder.wasReceivedTV.setTextColor(convertView.getResources().getColor(R.color.caldroid_black));
            } else {
                viewHolder.wasReceivedTV.setText(R.string.not_received);
                viewHolder.wasReceivedTV.setTextColor(convertView.getResources().getColor(R.color.red));
            }
            setTextHighlightSearch(viewHolder.typeTV, income.getTypeLabel());
            setTextHighlightSearch(viewHolder.descriptionTV, income.getDescription());
        }
        return convertView;
    }

    //Used to change color of any text matching search
    private void setTextHighlightSearch(TextView textView, String theTextToSet) {
        //If user has any text in the search bar
        if (searchText != null && !searchText.isEmpty()) {
            int startPos = theTextToSet.toLowerCase(Locale.US).indexOf(searchText.toLowerCase(Locale.US));
            int endPos = startPos + searchText.length();
            if (startPos != -1) {
                //If theTextToSet contains match, highlight match
                Spannable spannable = new SpannableString(theTextToSet);
                TextAppearanceSpan highlightSpan = new TextAppearanceSpan(null, Typeface.BOLD, -1, highlightColor, null);
                spannable.setSpan(highlightSpan, startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                textView.setText(spannable);
            } else {
                //Set regular text if not
                textView.setText(theTextToSet);
            }
        } else {
            //Set regular text if search bar is empty
            textView.setText(theTextToSet);
        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                ArrayList<PaymentLogEntry> FilteredArrayNames = new ArrayList<>();
                searchText = constraint.toString().toLowerCase();
                //Perform users search
                constraint = constraint.toString().toLowerCase();
                for (int i = 0; i < paymentArray.size(); i++) {
                    PaymentLogEntry dataNames = paymentArray.get(i);
                    //If users search matches any part of any apartment value, add to new filtered list
                    if (dataNames.getDescription().toLowerCase().contains(constraint.toString()) ||
                            dataNames.getTypeLabel().toLowerCase().contains(constraint.toString())) {
                        FilteredArrayNames.add(dataNames);
                    }
                }
                results.count = FilteredArrayNames.size();
                results.values = FilteredArrayNames;
                return results;
            }
            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filteredResults = (ArrayList<PaymentLogEntry>) filterResults.values;
                if(mOnDataChangeListener != null){
                    mOnDataChangeListener.onDataChanged(filteredResults);
                }
                notifyDataSetChanged();

            }
        };
    }

    public interface OnDataChangeListener{
       void onDataChanged(ArrayList<PaymentLogEntry> filteredIncome);
    }

    //Retrieve filtered results
    public ArrayList<PaymentLogEntry> getFilteredResults() {
        return this.filteredResults;
    }

    public void updateResults(ArrayList<PaymentLogEntry> results) {
        paymentArray = results;
        filteredResults = results;

        //Triggers the list update
        notifyDataSetChanged();
    }
}
