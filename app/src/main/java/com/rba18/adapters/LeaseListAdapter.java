package com.rba18.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
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
import java.util.Date;
import java.util.Locale;

/**
 * Created by Cody on 4/14/2018.
 */

public class LeaseListAdapter extends BaseAdapter implements Filterable {
    private ArrayList<Lease> mLeaseArray;
    private ArrayList<Lease> mFilteredResults;
    private Context mContext;
    private String mSearchText;
    private Date mDate;
    private Date mTodaysDate;
    private ColorStateList mHighlightColor;
    private int mDateFormatCode;
    private MainArrayDataMethods mDataMethods;

    public LeaseListAdapter(Context context, ArrayList<Lease> leaseArray, ColorStateList highlightColor, @Nullable Date dateToHighlight) {
        super();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        mLeaseArray = leaseArray;
        mFilteredResults = leaseArray;
        mContext = context;
        mSearchText = "";
        mDate = dateToHighlight;
        mHighlightColor = highlightColor;
        mDataMethods = new MainArrayDataMethods();
        mTodaysDate = new Date(System.currentTimeMillis());
        mDateFormatCode = preferences.getInt("dateFormat", DateAndCurrencyDisplayer.DATE_MMDDYYYY);
    }

    private static class ViewHolder {
        TextView leaseStartDateTV;
        TextView leaseEndDateTV;
        TextView primaryTenantNameTV;
        TextView apartmentAddressTV;
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

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        Lease lease = getItem(position);
        LeaseListAdapter.ViewHolder viewHolder;
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.row_lease, viewGroup, false);
            viewHolder = new LeaseListAdapter.ViewHolder();

            viewHolder.leaseStartDateTV = convertView.findViewById(R.id.leaseRowStartDateTV);
            viewHolder.leaseEndDateTV = convertView.findViewById(R.id.leaseRowEndDateTV);
            viewHolder.primaryTenantNameTV = convertView.findViewById(R.id.leaseRowNameTV);
            viewHolder.apartmentAddressTV = convertView.findViewById(R.id.leaseRowApartmentAddressTV);

            convertView.setTag(viewHolder);

        } else {
            viewHolder = (LeaseListAdapter.ViewHolder) convertView.getTag();
        }
        if (lease != null) {
            Apartment apartment = mDataMethods.getCachedApartmentByApartmentID(lease.getApartmentID());
            Tenant primaryTenant = mDataMethods.getCachedTenantByTenantID(lease.getPrimaryTenantID());
            if(apartment != null){
                setTextHighlightSearch(viewHolder.apartmentAddressTV, apartment.getStreet1AndStreet2String());
            } else {
                setTextHighlightSearch(viewHolder.apartmentAddressTV, "");
            }
            if(primaryTenant != null){
                setTextHighlightSearch(viewHolder.primaryTenantNameTV, primaryTenant.getFirstAndLastNameString());
            } else {
                setTextHighlightSearch(viewHolder.primaryTenantNameTV, "");
            }
            viewHolder.leaseStartDateTV.setText(DateAndCurrencyDisplayer.getDateToDisplay(mDateFormatCode, lease.getLeaseStart()));
            viewHolder.leaseEndDateTV.setText(DateAndCurrencyDisplayer.getDateToDisplay(mDateFormatCode, lease.getLeaseEnd()));
            if(mTodaysDate.compareTo(lease.getLeaseEnd()) > 0 || mTodaysDate.compareTo(lease.getLeaseStart()) < 0){
                convertView.setBackgroundColor(convertView.getResources().getColor(R.color.rowDarkenedBackground));
            } else {
                convertView.setBackgroundColor(convertView.getResources().getColor(R.color.white));
            }
            if(mDate != null){
                if(lease.getLeaseStart().equals(mDate)){
                    viewHolder.leaseStartDateTV.setTextColor(mContext.getResources().getColor(R.color.green_colorPrimaryDark));
                } else {
                    viewHolder.leaseStartDateTV.setTextColor(mContext.getResources().getColor(R.color.text_light));
                }
                if(lease.getLeaseEnd().equals(mDate)){
                    viewHolder.leaseEndDateTV.setTextColor(mContext.getResources().getColor(R.color.red));
                } else {
                    viewHolder.leaseEndDateTV.setTextColor(mContext.getResources().getColor(R.color.text_light));
                }
            }
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
                ArrayList<Lease> FilteredArrayNames = new ArrayList<>();
                mSearchText = constraint.toString().toLowerCase();
                //Perform users search
                constraint = constraint.toString().toLowerCase();
                for (int i = 0; i < mLeaseArray.size(); i++) {
                    Lease dataNames = mLeaseArray.get(i);
                    Apartment apartment = mDataMethods.getCachedApartmentByApartmentID(dataNames.getApartmentID());
                    Tenant primaryTenant = mDataMethods.getCachedTenantByTenantID(dataNames.getPrimaryTenantID());
                    String address = "";
                    String name = "";
                    if(apartment != null) {
                        address = apartment.getStreet1AndStreet2String();
                    }
                    if(primaryTenant != null){
                        name = primaryTenant.getFirstAndLastNameString();
                    }
                    //If users search matches any part of any apartment value, add to new filtered list
                    if (address.toLowerCase().contains(constraint.toString()) ||
                            name.toLowerCase().contains(constraint.toString())) {
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
                mFilteredResults = (ArrayList<Lease>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    //Retrieve filtered results
    public ArrayList<Lease> getFilteredResults() {
        return mFilteredResults;
    }

    public void updateResults(ArrayList<Lease> results) {
        mLeaseArray = results;
        mFilteredResults = results;

        //Triggers the list update
        notifyDataSetChanged();
    }


}
