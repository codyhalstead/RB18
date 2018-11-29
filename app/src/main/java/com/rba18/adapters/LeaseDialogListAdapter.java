package com.rba18.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
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
import com.rba18.helpers.DateAndCurrencyDisplayer;
import com.rba18.helpers.MainArrayDataMethods;
import com.rba18.model.Apartment;
import com.rba18.model.Lease;
import com.rba18.model.Tenant;

import java.util.ArrayList;
import java.util.Locale;

public class LeaseDialogListAdapter extends BaseAdapter implements Filterable {
    private ArrayList<Lease> mLeaseArray;
    private ArrayList<Lease> mFilteredResults;
    private Context mContext;
    private String mSearchText;
    private ColorStateList mHighlightColor;
    private MainArrayDataMethods mDataMethods;
    private int mDateFormatCode;

    public LeaseDialogListAdapter(Context context, ArrayList<Lease> leaseArray, ColorStateList highlightColor) {
        mLeaseArray = leaseArray;
        mFilteredResults = leaseArray;
        mContext = context;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        mSearchText = "";
        mHighlightColor = highlightColor;
        mDataMethods = new MainArrayDataMethods();
        mDateFormatCode = preferences.getInt("dateFormat", DateAndCurrencyDisplayer.DATE_MMDDYYYY);
    }

    private static class ViewHolder {
        TextView street1TV;
        TextView street2TV;
        TextView extraSpaceTV;
        TextView startEndDateTV;
        TextView nameTV;
    }

    @Override
    public int getCount() {
        if(mFilteredResults != null) {
            return mFilteredResults.size();
        }
        return 0;
    }

    @Override
    public Lease getItem(int i) {
        return mFilteredResults.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Lease lease = getItem(position);
        ViewHolder viewHolder;
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.row_dialog_lease, parent, false);

            viewHolder = new ViewHolder();

            viewHolder.street1TV = convertView.findViewById(R.id.address1TV);
            viewHolder.street2TV = convertView.findViewById(R.id.address2TV);
            viewHolder.extraSpaceTV = convertView.findViewById(R.id.extraSpaceTV);
            viewHolder.startEndDateTV = convertView.findViewById(R.id.startEndDateTV);
            viewHolder.nameTV = convertView.findViewById(R.id.nameTV);

            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (lease != null) {
            Apartment apartment = mDataMethods.getCachedApartmentByApartmentID(lease.getApartmentID());
            Tenant primaryTenant = mDataMethods.getCachedTenantByTenantID(lease.getPrimaryTenantID());
            viewHolder.startEndDateTV.setText(lease.getStartAndEndDatesString(mDateFormatCode));
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
            } else {
                viewHolder.street1TV.setVisibility(View.INVISIBLE);
                viewHolder.street2TV.setVisibility(View.INVISIBLE);
            }
            if(primaryTenant != null){
                viewHolder.nameTV.setVisibility(View.VISIBLE);
                viewHolder.nameTV.setText(primaryTenant.getFirstAndLastNameString());
            } else {
                viewHolder.nameTV.setVisibility(View.INVISIBLE);
            }
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
                mFilteredResults = (ArrayList<Lease>) results.values;
                notifyDataSetChanged();
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                ArrayList<Lease> FilteredArrayNames = mLeaseArray;
                mSearchText = constraint.toString().toLowerCase();
                //Perform users search
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
    public ArrayList<Lease> getFilteredResults() {
        return mFilteredResults;
    }
}