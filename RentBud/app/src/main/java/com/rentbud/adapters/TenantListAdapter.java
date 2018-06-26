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
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.example.cody.rentbud.R;
import com.rentbud.activities.MainActivity;
import com.rentbud.helpers.MainArrayDataMethods;
import com.rentbud.model.Apartment;
import com.rentbud.model.ExpenseLogEntry;
import com.rentbud.model.Lease;
import com.rentbud.model.Tenant;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Cody on 1/11/2018.
 */

public class TenantListAdapter extends BaseAdapter implements Filterable {
    private ArrayList<Tenant> tenantArray;
    private ArrayList<Tenant> filteredResults;
    private Context context;
    private String searchText;
    private ColorStateList highlightColor;
    MainArrayDataMethods dataMethods;

    public TenantListAdapter(Context context, ArrayList<Tenant> tenantArray, ColorStateList highlightColor) {
        this.tenantArray = tenantArray;
        this.filteredResults = tenantArray;
        this.context = context;
        this.searchText = "";
        this.highlightColor = highlightColor;
        this.dataMethods = new MainArrayDataMethods();
    }

    static class ViewHolder {
        TextView firstNameTV;
        TextView lastNameTV;
        TextView phoneNumberTV;
        TextView leaseEndsTextDisplayTV;
        TextView leaseEndsTV;
        TextView isPrimaryTV;
        TextView rentingStatusTV;
        TextView apartmentStreet1;
        TextView apartmentStreet2;
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
        ViewHolder viewHolder;
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.row_tenant, parent, false);
            viewHolder = new ViewHolder();

            viewHolder.firstNameTV = convertView.findViewById(R.id.firstNameTV);
            viewHolder.lastNameTV = convertView.findViewById(R.id.lastNameTV);
            viewHolder.phoneNumberTV = convertView.findViewById(R.id.phoneNumberTV);
            viewHolder.leaseEndsTextDisplayTV = convertView.findViewById(R.id.tenantRowLeaseEndDisplayTV);
            viewHolder.leaseEndsTV = convertView.findViewById(R.id.tenantRowLeaseEndTV);
            viewHolder.isPrimaryTV = convertView.findViewById(R.id.tenantRowLeaseIsPrimaryTV);
            viewHolder.rentingStatusTV = convertView.findViewById(R.id.tenantRowRentStatusTV);
            viewHolder.apartmentStreet1 = convertView.findViewById(R.id.tenantRowApartmentStreet1TV);
            viewHolder.apartmentStreet2 = convertView.findViewById(R.id.tenantRowApartmentStreet2TV);

            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (tenant != null) {
            setTextHighlightSearch(viewHolder.firstNameTV, tenant.getFirstName());
            setTextHighlightSearch(viewHolder.lastNameTV, tenant.getLastName());
            setTextHighlightSearch(viewHolder.phoneNumberTV, tenant.getPhone());
            Lease currentLease = null;
            if (!tenant.getHasLease()) {
                viewHolder.leaseEndsTV.setText("");
                viewHolder.leaseEndsTextDisplayTV.setText("");
                viewHolder.isPrimaryTV.setText("");
            } else {
                SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
                currentLease = dataMethods.getCachedActiveLeaseByTenantID(tenant.getId());
                viewHolder.leaseEndsTV.setText("ERROR");
                viewHolder.isPrimaryTV.setText("ERROR");
                if(currentLease != null) {
                    viewHolder.leaseEndsTV.setText(formatter.format(currentLease.getLeaseEnd()));
                    if (tenant.getId() == currentLease.getPrimaryTenantID()) {
                        viewHolder.isPrimaryTV.setText("Primary");
                    } else {
                        viewHolder.isPrimaryTV.setText("Secondary");
                    }
                }
            }

            if (!tenant.getHasLease()) {
                convertView.setBackgroundColor(convertView.getResources().getColor(R.color.lightGrey));
                viewHolder.rentingStatusTV.setText("Not Currently Renting");
                viewHolder.apartmentStreet1.setText("");
                viewHolder.apartmentStreet2.setText("");
            } else {
                viewHolder.rentingStatusTV.setText("Renting ");
                if(currentLease != null) {
                    convertView.setBackgroundColor(convertView.getResources().getColor(R.color.white));
                    Apartment apartment = dataMethods.getCachedApartmentByApartmentID(currentLease.getApartmentID());
                    if (apartment != null) {
                        viewHolder.apartmentStreet1.setText(apartment.getStreet1());
                        viewHolder.apartmentStreet2.setText(apartment.getStreet2());
                    } else {
                        viewHolder.apartmentStreet1.setText("");
                        viewHolder.apartmentStreet2.setText("");
                    }
                }
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
                filteredResults = (ArrayList<Tenant>) results.values;
                notifyDataSetChanged();
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                ArrayList<Tenant> FilteredArrayNames = new ArrayList<Tenant>();
                searchText = constraint.toString().toLowerCase();
                //Perform users search
                constraint = constraint.toString().toLowerCase();
                for (int i = 0; i < tenantArray.size(); i++) {
                    Tenant dataNames = tenantArray.get(i);
                    //If users search matches any part of any apartment value, add to new filtered list
                    if (dataNames.getFirstName().toLowerCase().contains(constraint.toString()) ||
                            dataNames.getLastName().toLowerCase().contains(constraint.toString()) ||
                            dataNames.getPhone().toLowerCase().contains(constraint.toString())) {
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
    public ArrayList<Tenant> getFilteredResults() {
        return filteredResults;
    }

    public void updateResults(ArrayList<Tenant> results) {
        tenantArray = results;
        filteredResults = results;

        //Triggers the list update
        notifyDataSetChanged();
    }
}
