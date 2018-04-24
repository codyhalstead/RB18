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
import com.rentbud.model.Apartment;
import com.rentbud.model.ExpenseLogEntry;
import com.rentbud.model.Lease;
import com.rentbud.model.Tenant;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by Cody on 4/14/2018.
 */

public class LeaseListAdapter extends BaseAdapter implements Filterable {
    private ArrayList<Lease> leaseArray;
    private ArrayList<Lease> filteredResults;
    private Context context;
    private String searchText;
    private ColorStateList highlightColor;
    MainArrayDataMethods dataMethods;

    public LeaseListAdapter(Context context, ArrayList<Lease> leaseArray, ColorStateList highlightColor) {
        super();
        this.leaseArray = leaseArray;
        this.filteredResults = leaseArray;
        this.context = context;
        this.searchText = "";
        this.highlightColor = highlightColor;
        this.dataMethods = new MainArrayDataMethods();
    }

    static class ViewHolder {
        TextView leaseStartDateTV;
        TextView leaseEndDateTV;
        TextView primaryTenantFirstNameTV;
        TextView primaryTenantLastNameTV;
        TextView apartmentStreet1TV;
        TextView apartmentStreet2TV;
    }

    @Override
    public int getCount() {
        return filteredResults.size();
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
            viewHolder.primaryTenantFirstNameTV = convertView.findViewById(R.id.leaseRowFirstNameTV);
            viewHolder.primaryTenantLastNameTV = convertView.findViewById(R.id.leaseRowLastNameTV);
            viewHolder.apartmentStreet1TV = convertView.findViewById(R.id.leaseRowApartmentStreet1TV);
            viewHolder.apartmentStreet2TV = convertView.findViewById(R.id.leaseRowApartmentStreet2TV);
            //viewHolder.position = position;

            convertView.setTag(viewHolder);

        } else {
            viewHolder = (LeaseListAdapter.ViewHolder) convertView.getTag();
        }
        if (lease != null) {
            Apartment apartment = dataMethods.getCachedApartmentByApartmentID(lease.getApartmentID());
            Tenant primaryTenant = dataMethods.getCachedTenantByTenantID(lease.getPrimaryTenantID());
            SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
            viewHolder.leaseStartDateTV.setText(formatter.format(lease.getLeaseStart()));
            viewHolder.leaseEndDateTV.setText(formatter.format(lease.getLeaseEnd()));
            setTextHighlightSearch(viewHolder.primaryTenantFirstNameTV, primaryTenant.getFirstName());
            setTextHighlightSearch(viewHolder.primaryTenantLastNameTV, primaryTenant.getLastName());
            setTextHighlightSearch(viewHolder.apartmentStreet1TV, apartment.getStreet1());
            setTextHighlightSearch(viewHolder.apartmentStreet2TV, apartment.getStreet2());
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
                    //If users search matches any part of any apartment value, add to new filtered list
                    if (apartment.getStreet1().toLowerCase().contains(constraint.toString()) ||
                            apartment.getStreet2().toLowerCase().contains(constraint.toString()) ||
                            primaryTenant.getFirstName().toLowerCase().contains(constraint.toString()) ||
                            primaryTenant.getLastName().toLowerCase().contains(constraint.toString())) {
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