package com.rba18.adapters;

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

import com.rba18.R;
import com.rba18.model.Apartment;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by Cody on 3/1/2018.
 */

public class ApartmentDialogListAdapter extends BaseAdapter implements Filterable {
    private ArrayList<Apartment> mApartmentArray;
    private ArrayList<Apartment> mFilteredResults;
    private Context mContext;
    private String mSearchText;
    private ColorStateList mHighlightColor;

    public ApartmentDialogListAdapter(Context context, ArrayList<Apartment> apartmentArray, ColorStateList highlightColor) {
        mApartmentArray = apartmentArray;
        mFilteredResults = apartmentArray;
        mContext = context;
        mSearchText = "";
        mHighlightColor = highlightColor;
    }

    private static class ViewHolder {
        TextView street1TV;
        TextView street2TV;
        TextView cityStateZipTV;
        TextView extraSpaceTV;
    }

    @Override
    public int getCount() {
        if(mFilteredResults != null) {
            return mFilteredResults.size();
        }
        return 0;
    }

    @Override
    public Apartment getItem(int i) {
        return mFilteredResults.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Apartment apartment = getItem(position);
        ViewHolder viewHolder;
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.row_dialog_apartment, parent, false);

            viewHolder = new ViewHolder();

            viewHolder.street1TV = convertView.findViewById(R.id.address1TV);
            viewHolder.street2TV = convertView.findViewById(R.id.address2TV);
            viewHolder.cityStateZipTV = convertView.findViewById(R.id.cityStateZipTV);
            viewHolder.extraSpaceTV = convertView.findViewById(R.id.extraSpaceTV);

            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (apartment != null) {
            setTextHighlightSearch(viewHolder.street1TV, apartment.getStreet1());
            //If empty street 2, set invisible
            if (apartment.getStreet2() != null) {
                if (apartment.getStreet2().equals("")) {
                    viewHolder.street2TV.setVisibility(View.GONE);
                    viewHolder.extraSpaceTV.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.street2TV.setVisibility(View.VISIBLE);
                    setTextHighlightSearch(viewHolder.street2TV, apartment.getStreet2());
                    viewHolder.extraSpaceTV.setVisibility(View.GONE);
                }
            } else {
                viewHolder.street2TV.setVisibility(View.GONE);
            }
            setTextHighlightSearch(viewHolder.cityStateZipTV, apartment.getCityStateZipString());
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
                mFilteredResults = (ArrayList<Apartment>) results.values;
                notifyDataSetChanged();
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                ArrayList<Apartment> FilteredArrayNames = new ArrayList<>();
                mSearchText = constraint.toString().toLowerCase();
                //Perform users search
                constraint = constraint.toString().toLowerCase();
                for (int i = 0; i < mApartmentArray.size(); i++) {
                    Apartment dataNames = mApartmentArray.get(i);
                    String street2 = "";
                    if(dataNames.getStreet2() != null){
                        street2 = dataNames.getStreet2();
                    }
                    //If users search matches any part of any apartment value, add to new filtered list
                    if (dataNames.getStreet1().toLowerCase().contains(constraint.toString()) ||
                            street2.toLowerCase().contains(constraint.toString()) ||
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

    //Retrieve filtered results
    public ArrayList<Apartment> getFilteredResults() {
        return mFilteredResults;
    }
}
