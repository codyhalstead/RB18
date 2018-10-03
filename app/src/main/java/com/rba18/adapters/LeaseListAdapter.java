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
    private ArrayList<Lease> leaseArray;
    private ArrayList<Lease> filteredResults;
    private SharedPreferences preferences;
    private Context context;
    private String searchText;
    private Date date;
    private Date todaysDate;
    private ColorStateList highlightColor;
    private int dateFormatCode;
    MainArrayDataMethods dataMethods;

    public LeaseListAdapter(Context context, ArrayList<Lease> leaseArray, ColorStateList highlightColor, @Nullable Date dateToHighlight) {
        super();
        this.preferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.leaseArray = leaseArray;
        this.filteredResults = leaseArray;
        this.context = context;
        this.searchText = "";
        this.date = dateToHighlight;
        this.highlightColor = highlightColor;
        this.dataMethods = new MainArrayDataMethods();
        this.todaysDate = new Date(System.currentTimeMillis());
        this.dateFormatCode = preferences.getInt("dateFormat", DateAndCurrencyDisplayer.DATE_MMDDYYYY);
    }

    static class ViewHolder {
        TextView leaseStartDateTV;
        TextView leaseEndDateTV;
        TextView primaryTenantNameTV;
        TextView apartmentAddressTV;
    }

    @Override
    public int getCount() {
        if(filteredResults != null) {
            return filteredResults.size();
        }
        return 0;
    }

    @Override
    public Lease getItem(int i) {
        return filteredResults.get(i);
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
            convertView = LayoutInflater.from(context).inflate(R.layout.row_lease, viewGroup, false);
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
            Apartment apartment = dataMethods.getCachedApartmentByApartmentID(lease.getApartmentID());
            Tenant primaryTenant = dataMethods.getCachedTenantByTenantID(lease.getPrimaryTenantID());
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
            viewHolder.leaseStartDateTV.setText(DateAndCurrencyDisplayer.getDateToDisplay(dateFormatCode, lease.getLeaseStart()));
            viewHolder.leaseEndDateTV.setText(DateAndCurrencyDisplayer.getDateToDisplay(dateFormatCode, lease.getLeaseEnd()));
            if(todaysDate.compareTo(lease.getLeaseEnd()) > 0 || todaysDate.compareTo(lease.getLeaseStart()) < 0){
                convertView.setBackgroundColor(convertView.getResources().getColor(R.color.rowDarkenedBackground));
            } else {
                convertView.setBackgroundColor(convertView.getResources().getColor(R.color.white));
            }
            if(date != null){
                if(lease.getLeaseStart().equals(date)){
                    viewHolder.leaseStartDateTV.setTextColor(context.getResources().getColor(R.color.green_colorPrimaryDark));
                } else {
                    viewHolder.leaseStartDateTV.setTextColor(context.getResources().getColor(R.color.text_light));
                }
                if(lease.getLeaseEnd().equals(date)){
                    viewHolder.leaseEndDateTV.setTextColor(context.getResources().getColor(R.color.red));
                } else {
                    viewHolder.leaseEndDateTV.setTextColor(context.getResources().getColor(R.color.text_light));
                }
            }
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
                ArrayList<Lease> FilteredArrayNames = new ArrayList<>();
                searchText = constraint.toString().toLowerCase();
                //Perform users search
                constraint = constraint.toString().toLowerCase();
                for (int i = 0; i < leaseArray.size(); i++) {
                    Lease dataNames = leaseArray.get(i);
                    Apartment apartment = dataMethods.getCachedApartmentByApartmentID(dataNames.getApartmentID());
                    Tenant primaryTenant = dataMethods.getCachedTenantByTenantID(dataNames.getPrimaryTenantID());
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

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filteredResults = (ArrayList<Lease>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    //Retrieve filtered results
    public ArrayList<Lease> getFilteredResults() {
        return this.filteredResults;
    }

    public void updateResults(ArrayList<Lease> results) {
        leaseArray = results;
        filteredResults = results;

        //Triggers the list update
        notifyDataSetChanged();
    }


}
