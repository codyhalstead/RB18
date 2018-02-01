package com.rentbud.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.TextAppearanceSpan;
import android.util.Log;
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
import java.util.List;
import java.util.Locale;

/**
 * Created by Cody on 1/11/2018.
 */

public class RentalListAdapter extends BaseAdapter implements Filterable {
    private ArrayList<Apartment> apartmentArray;
    private ArrayList<Apartment> filteredResults;
    private Context context;
    private String searchText;
    private ColorStateList highlightColor;

    public RentalListAdapter(Context context, ArrayList<Apartment> apartmentArray, ColorStateList highlightColor) {
        super();
        this.apartmentArray = apartmentArray;
        this.filteredResults = apartmentArray;
        this.context = context;
        this.searchText = "";
        this.highlightColor = highlightColor;
    }

    @Override
    public int getCount() {
        return filteredResults.size();
    }

    @Override
    public Apartment getItem(int i) {
        return filteredResults.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Apartment apartment = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.row_rental, parent, false);
        }
        TextView street1TV = (TextView) convertView.findViewById(R.id.rentalRowStreet1TV);
        TextView street2TV = (TextView) convertView.findViewById(R.id.rentalRowStreet2TV);
        TextView cityTV = (TextView) convertView.findViewById(R.id.rentalRowCityTV);
        TextView stateTV = (TextView) convertView.findViewById(R.id.rentalRowStateTV);
        TextView zipTV = (TextView) convertView.findViewById(R.id.rentalRowZipTV);

        if (apartment != null) {
            setTextHighlightSearch(street1TV, apartment.getStreet1());
            if (apartment.getStreet2().equals("")) {
                street2TV.setVisibility(View.GONE);
            } else {
                street2TV.setVisibility(View.VISIBLE);
            }
            setTextHighlightSearch(street2TV, apartment.getStreet2());
            String city = apartment.getCity() + ",";
            setTextHighlightSearch(cityTV, city);
            setTextHighlightSearch(stateTV, apartment.getState());
            setTextHighlightSearch(zipTV, apartment.getZip());
        }

        return convertView;
    }

    @Override
    public Filter getFilter() {

        Filter filter = new Filter() {

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                filteredResults = (ArrayList<Apartment>) results.values;
                notifyDataSetChanged();
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                ArrayList<Apartment> FilteredArrayNames = new ArrayList<Apartment>();
                searchText = constraint.toString().toLowerCase();
                // perform your search here using the searchConstraint String.

                constraint = constraint.toString().toLowerCase();
                for (int i = 0; i < apartmentArray.size(); i++) {
                    Apartment dataNames = apartmentArray.get(i);
                    if (dataNames.getStreet1().toLowerCase().contains(constraint.toString()) ||
                            dataNames.getStreet2().toLowerCase().contains(constraint.toString()) ||
                            dataNames.getCity().toLowerCase().contains(constraint.toString()) ||
                            dataNames.getState().toLowerCase().contains(constraint.toString()) ||
                            dataNames.getZip().toLowerCase().contains(constraint.toString())) {

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

    private void setTextHighlightSearch(TextView textView, String theTextToSet){
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
