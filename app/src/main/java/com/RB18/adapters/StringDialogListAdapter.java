package com.RB18.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.RB18.helpers.MainArrayDataMethods;

import java.util.ArrayList;
import java.util.Locale;

public class StringDialogListAdapter extends BaseAdapter implements Filterable {
    private Context context;
    private String searchText;
    private ColorStateList highlightColor;
    private MainArrayDataMethods dataMethods;
    private ArrayList<String> strings, filteredResults;

    public StringDialogListAdapter(Context context, ArrayList<String> strings, ColorStateList highlightColor) {
        this.strings = strings;
        this.filteredResults = strings;
        this.context = context;
        this.searchText = "";
        this.highlightColor = highlightColor;
        this.dataMethods = new MainArrayDataMethods();
    }

    static class ViewHolder {
        TextView typeLabelTV;
    }

    @Override
    public int getCount() {
        if (filteredResults != null) {
            return filteredResults.size();
        }
        return 0;
    }

    @Override
    public String getItem(int i) {
        return filteredResults.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String string = getItem(position);
        ViewHolder viewHolder;
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.row_dialog_type, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.typeLabelTV = convertView.findViewById(R.id.typeDialogRowTypeLabelTV);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (string != null) {
            setTextHighlightSearch(viewHolder.typeLabelTV, string);
        }
        return convertView;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                //Update filtered results and notify change
                filteredResults = (ArrayList<String>) results.values;
                notifyDataSetChanged();
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                ArrayList<String> FilteredArrayNames = new ArrayList<>();
                searchText = constraint.toString().toLowerCase();
                //Perform users search
                constraint = constraint.toString().toLowerCase();
                for (int i = 0; i < strings.size(); i++) {
                    //TypeTotal dataNames = strings.get(i);
                    //If users search matches any part of any apartment value, add to new filtered list
                    if (strings.get(i).contains(constraint.toString())) {
                        FilteredArrayNames.add(strings.get(i));
                    }
                }
                results.count = FilteredArrayNames.size();
                results.values = FilteredArrayNames;
                return results;
            }
        };
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

    //Retrieve filtered results
    public ArrayList<String> getFilteredResults() {
        return filteredResults;
    }

    public void updateResults(ArrayList<String> results) {
        strings = results;
        filteredResults = results;

        //Triggers the list update
        notifyDataSetChanged();
    }
}