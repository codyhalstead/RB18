package com.rentbud.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
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
import com.rentbud.helpers.MainArrayDataMethods;
import com.rentbud.model.ExpenseLogEntry;
import com.rentbud.model.MoneyLogEntry;
import com.rentbud.model.PaymentLogEntry;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MoneyListAdapter extends BaseAdapter implements Filterable {
    private ArrayList<MoneyLogEntry> moneyArray;
    private ArrayList<MoneyLogEntry> filteredResults;
    private Context context;
    private String searchText;
    private ColorStateList highlightColor;
    MainArrayDataMethods dataMethods;
    private Date todaysDate;

    public MoneyListAdapter(Context context, ArrayList<MoneyLogEntry> moneyArray, ColorStateList highlightColor) {
        super();
        this.moneyArray = moneyArray;
        this.filteredResults = moneyArray;
        this.context = context;
        this.searchText = "";
        this.highlightColor = highlightColor;
        this.dataMethods = new MainArrayDataMethods();
        this.todaysDate = new Date(System.currentTimeMillis());
    }

    static class ViewHolder {
        TextView amountTV;
        TextView dateTV;
        TextView typeTV;
        TextView descriptionTV;
    }

    @Override
    public int getCount() {
        if(filteredResults != null) {
            return filteredResults.size();
        }
        return 0;
    }

    @Override
    public MoneyLogEntry getItem(int i) {
        return filteredResults.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        MoneyLogEntry moneyEntry = getItem(position);
        MoneyListAdapter.ViewHolder viewHolder;
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.row_expense, viewGroup, false);
            viewHolder = new MoneyListAdapter.ViewHolder();

            viewHolder.amountTV = convertView.findViewById(R.id.expenseRowAmountTV);
            viewHolder.dateTV = convertView.findViewById(R.id.expenseRowDateTV);
            viewHolder.typeTV = convertView.findViewById(R.id.expenseRowTypeTV);
            viewHolder.descriptionTV = convertView.findViewById(R.id.expenseRowDescriptionTV);
            //viewHolder.position = position;

            convertView.setTag(viewHolder);

        } else {
            viewHolder = (MoneyListAdapter.ViewHolder) convertView.getTag();
        }
        //viewHolder.amountTV.setTextColor(context.getResources().getColor(R.color.red));
        if (moneyEntry != null) {
            BigDecimal displayVal = moneyEntry.getAmount().setScale(2, RoundingMode.HALF_EVEN);
            NumberFormat usdCostFormat = NumberFormat.getCurrencyInstance(Locale.US);
            usdCostFormat.setMinimumFractionDigits(2);
            usdCostFormat.setMaximumFractionDigits(2);
            SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
            viewHolder.dateTV.setText(formatter.format(moneyEntry.getDate()));
            if( todaysDate.compareTo(moneyEntry.getDate()) < 0){
                convertView.setBackgroundColor(convertView.getResources().getColor(R.color.lightGrey));
            } else {
                convertView.setBackgroundColor(convertView.getResources().getColor(R.color.white));
            }

            if(moneyEntry instanceof ExpenseLogEntry){
                ExpenseLogEntry expense = (ExpenseLogEntry)moneyEntry;
                viewHolder.amountTV.setText(usdCostFormat.format(displayVal.doubleValue()));
                viewHolder.typeTV.setText(expense.getTypeLabel());
                viewHolder.amountTV.setTextColor(context.getResources().getColor(R.color.red));
            } else if(moneyEntry instanceof PaymentLogEntry){
                PaymentLogEntry income = (PaymentLogEntry) moneyEntry;
                viewHolder.amountTV.setText(usdCostFormat.format(displayVal.doubleValue()));
                viewHolder.typeTV.setText(income.getTypeLabel());
                viewHolder.amountTV.setTextColor(context.getResources().getColor(R.color.green_colorPrimaryDark));
            } else {
                viewHolder.amountTV.setText(usdCostFormat.format(displayVal.doubleValue()));
                viewHolder.typeTV.setText("");
                viewHolder.amountTV.setTextColor(context.getResources().getColor(R.color.caldroid_darker_gray));
            }

            setTextHighlightSearch(viewHolder.descriptionTV, moneyEntry.getDescription());
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
                ArrayList<MoneyLogEntry> FilteredArrayNames = new ArrayList<>();
                searchText = constraint.toString().toLowerCase();
                //Perform users search
                constraint = constraint.toString().toLowerCase();
                for (int i = 0; i < moneyArray.size(); i++) {
                    MoneyLogEntry dataNames = moneyArray.get(i);
                    //If users search matches any part of any apartment value, add to new filtered list
                    if (dataNames.getDescription().toLowerCase().contains(constraint.toString())) {
                        FilteredArrayNames.add(dataNames);
                    }
                }
                results.count = FilteredArrayNames.size();
                results.values = FilteredArrayNames;
                return results;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filteredResults = (ArrayList<MoneyLogEntry>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    //Retrieve filtered results
    public ArrayList<MoneyLogEntry> getFilteredResults() {
        return this.filteredResults;
    }

    public void updateResults(ArrayList<MoneyLogEntry> results) {
        moneyArray = results;
        filteredResults = results;

        //Triggers the list update
        notifyDataSetChanged();
    }

}
