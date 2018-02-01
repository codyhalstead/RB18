package com.rentbud.adapters;

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
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.example.cody.rentbud.R;
import com.rentbud.model.Apartment;
import com.rentbud.model.Tenant;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by Cody on 1/11/2018.
 */

public class RenterListAdapter extends BaseAdapter implements Filterable {
    private ArrayList<Tenant> tenantArray;
    private ArrayList<Tenant> filteredResults;
    private Context context;
    private String searchText;
    private ColorStateList highlightColor;

    public RenterListAdapter(Context context, ArrayList<Tenant> tenantArray, ColorStateList highlightColor) {
        this.tenantArray = tenantArray;
        this.filteredResults = tenantArray;
        this.context = context;
        this.searchText = "";
        this.highlightColor = highlightColor;
    }

    @Override
    public int getCount() {
        return filteredResults.size();
    }

    @Override
    public Tenant getItem(int i) {
        return filteredResults.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Tenant tenant = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.row_renter, parent, false);
        }
        TextView firstNameTV = (TextView) convertView.findViewById(R.id.firstNameTV);
        TextView lastNameTV = (TextView) convertView.findViewById(R.id.lastNameTV);
        TextView phoneNumberTV = (TextView) convertView.findViewById(R.id.phoneNumberTV);

        if (tenant != null) {
            setTextHighlightSearch(firstNameTV, tenant.getFirstName());
            setTextHighlightSearch(lastNameTV, tenant.getLastName());
            setTextHighlightSearch(phoneNumberTV, tenant.getPhone());
        }

        return convertView;
    }

    @Override
    public Filter getFilter() {

        Filter filter = new Filter() {

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                filteredResults = (ArrayList<Tenant>) results.values;
                notifyDataSetChanged();
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                ArrayList<Tenant> FilteredArrayNames = new ArrayList<Tenant>();
                searchText = constraint.toString().toLowerCase();
                // perform your search here using the searchConstraint String.

                constraint = constraint.toString().toLowerCase();
                for (int i = 0; i < tenantArray.size(); i++) {
                    Tenant dataNames = tenantArray.get(i);
                    if (dataNames.getFirstName().toLowerCase().contains(constraint.toString()) ||
                            dataNames.getLastName().toLowerCase().contains(constraint.toString()) ||
                            dataNames.getPhone().toLowerCase().contains(constraint.toString()) ) {

                            FilteredArrayNames.add(dataNames);
                    }
                }

                results.count = FilteredArrayNames.size();
                results.values = FilteredArrayNames;

                return results;
            }
        };

        return filter;
    }

    private void setTextHighlightSearch(TextView textView, String theTextToSet) {
        if (searchText != null && !searchText.isEmpty()) {
            int startPos = theTextToSet.toLowerCase(Locale.US).indexOf(searchText.toLowerCase(Locale.US));
            int endPos = startPos + searchText.length();

            if (startPos != -1) {
                Spannable spannable = new SpannableString(theTextToSet);
                //ColorStateList blueColor = new ColorStateList(new int[][]{new int[]{}}, new int[]{Color.BLUE});
                TextAppearanceSpan highlightSpan = new TextAppearanceSpan(null, Typeface.BOLD, -1, highlightColor, null);
                spannable.setSpan(highlightSpan, startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                textView.setText(spannable);
            } else {
                textView.setText(theTextToSet);
            }
        } else {
            textView.setText(theTextToSet);
        }
    }
}
