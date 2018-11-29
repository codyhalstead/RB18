package com.rba18.adapters;

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

import com.rba18.R;
import com.rba18.helpers.DateAndCurrencyDisplayer;
import com.rba18.model.ExpenseLogEntry;

import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Cody on 3/27/2018.
 */

public class ExpenseListAdapter extends BaseAdapter implements Filterable {
    private ArrayList<ExpenseLogEntry> mExpenseArray;
    private ArrayList<ExpenseLogEntry> mFilteredResults;
    private Context mContext;
    private String mSearchText;
    private ColorStateList mHighlightColor;
    private Date mTodaysDate;
    private int mDateFormatCode, mMoneyFormatCode;
    private OnDataChangeListener mOnDataChangeListener;

    public ExpenseListAdapter(Context context, ArrayList<ExpenseLogEntry> expenseArray, ColorStateList highlightColor) {
        super();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        mExpenseArray = expenseArray;
        mFilteredResults = expenseArray;
        mContext = context;
        mSearchText = "";
        mHighlightColor = highlightColor;
        mTodaysDate = new Date(System.currentTimeMillis());
        mDateFormatCode = preferences.getInt("dateFormat", DateAndCurrencyDisplayer.DATE_MMDDYYYY);
        mMoneyFormatCode = preferences.getInt("currency", DateAndCurrencyDisplayer.CURRENCY_US);
    }

    public void setOnDataChangeListener(ExpenseListAdapter.OnDataChangeListener onDataChangeListener){
        mOnDataChangeListener = onDataChangeListener;
    }

    private static class ViewHolder {
        TextView amountTV;
        TextView dateTV;
        TextView typeTV;
        TextView descriptionTV;
        TextView wasPaidTV;
    }

    @Override
    public int getCount() {
        if(mFilteredResults != null) {
            return mFilteredResults.size();
        }
        return 0;
    }

    @Override
    public ExpenseLogEntry getItem(int i) {
        return mFilteredResults.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ExpenseLogEntry expense = getItem(position);
        ExpenseListAdapter.ViewHolder viewHolder;
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.row_expense, viewGroup, false);
            viewHolder = new ExpenseListAdapter.ViewHolder();

            viewHolder.amountTV = convertView.findViewById(R.id.expenseRowAmountTV);
            viewHolder.dateTV = convertView.findViewById(R.id.expenseRowDateTV);
            viewHolder.typeTV = convertView.findViewById(R.id.expenseRowTypeTV);
            viewHolder.descriptionTV = convertView.findViewById(R.id.expenseRowDescriptionTV);
            viewHolder.wasPaidTV = convertView.findViewById(R.id.expenseRowWasPaidTV);

            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ExpenseListAdapter.ViewHolder) convertView.getTag();
        }
        viewHolder.amountTV.setTextColor(mContext.getResources().getColor(R.color.red));
        if (expense != null) {
            viewHolder.amountTV.setText(DateAndCurrencyDisplayer.getCurrencyToDisplay(mMoneyFormatCode, expense.getAmount()));
            viewHolder.dateTV.setText(DateAndCurrencyDisplayer.getDateToDisplay(mDateFormatCode, expense.getDate()));
            if( mTodaysDate.compareTo(expense.getDate()) < 0){
                convertView.setBackgroundColor(convertView.getResources().getColor(R.color.rowDarkenedBackground));
            } else {
                convertView.setBackgroundColor(convertView.getResources().getColor(R.color.white));
            }
            if(expense.getIsCompleted()) {
                viewHolder.wasPaidTV.setText(R.string.paid);
                viewHolder.wasPaidTV.setTextColor(convertView.getResources().getColor(R.color.caldroid_black));
            } else {
                viewHolder.wasPaidTV.setText(R.string.not_paid);
                viewHolder.wasPaidTV.setTextColor(convertView.getResources().getColor(R.color.red));
            }
            setTextHighlightSearch(viewHolder.typeTV, expense.getTypeLabel());
            setTextHighlightSearch(viewHolder.descriptionTV, expense.getDescription());
        }
        return convertView;
    }

    //Used to change color of any text matching search
    private void setTextHighlightSearch(TextView textView, String theTextToSet) {
        //If user has any text in the search bar
        if (mSearchText != null && !mSearchText.isEmpty()) {
            int startPos = theTextToSet.toLowerCase(Locale.US).indexOf(mSearchText.toLowerCase(Locale.US));
            int endPos = startPos + mSearchText.length();
            if (startPos != -1) {
                //If theTextToSet contains match, highlight match
                Spannable spannable = new SpannableString(theTextToSet);
                TextAppearanceSpan highlightSpan = new TextAppearanceSpan(null, Typeface.BOLD, -1, mHighlightColor, null);
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
                ArrayList<ExpenseLogEntry> FilteredArrayNames = new ArrayList<>();
                mSearchText = constraint.toString().toLowerCase();
                //Perform users search
                constraint = constraint.toString().toLowerCase();
                for (int i = 0; i < mExpenseArray.size(); i++) {
                    ExpenseLogEntry dataNames = mExpenseArray.get(i);
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

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mFilteredResults = (ArrayList<ExpenseLogEntry>) filterResults.values;
                if(mOnDataChangeListener != null){
                    mOnDataChangeListener.onDataChanged(mFilteredResults);
                }
                notifyDataSetChanged();
            }
        };
    }

    public interface OnDataChangeListener{
        void onDataChanged(ArrayList<ExpenseLogEntry> filteredExpenses);
    }

    //Retrieve filtered results
    public ArrayList<ExpenseLogEntry> getFilteredResults() {
        return mFilteredResults;
    }

    public void updateResults(ArrayList<ExpenseLogEntry> results) {
        mExpenseArray = results;
        mFilteredResults = results;

        //Triggers the list update
        notifyDataSetChanged();
    }

}
