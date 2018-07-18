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
import com.rentbud.helpers.MainArrayDataMethods;
import com.rentbud.model.Apartment;
import com.rentbud.model.Lease;
import com.rentbud.model.Tenant;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class LeaseDialogListAdapter extends BaseAdapter implements Filterable {
    private ArrayList<Lease> leaseArray;
    private ArrayList<Lease> filteredResults;
    private Context context;
    private String searchText;
    private ColorStateList highlightColor;
    private MainArrayDataMethods dataMethods;

    public LeaseDialogListAdapter(Context context, ArrayList<Lease> leaseArray, ColorStateList highlightColor) {
        this.leaseArray = leaseArray;
        this.filteredResults = leaseArray;
        this.context = context;
        this.searchText = "";
        this.highlightColor = highlightColor;
        this.dataMethods = new MainArrayDataMethods();
    }

    static class ViewHolder {
        TextView street1TV;
        TextView street2TV;
        //TextView cityTV;
        //TextView stateTV;
        //TextView zipTV;
        TextView extraSpaceTV;

        TextView startDateTV;
        TextView endDateTV;

        TextView firstNameTV;
        TextView lastNameTV;
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

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Lease lease = getItem(position);
        ViewHolder viewHolder;
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.row_dialog_lease, parent, false);

            viewHolder = new ViewHolder();

            viewHolder.street1TV = convertView.findViewById(R.id.address1TV);
            viewHolder.street2TV = convertView.findViewById(R.id.address2TV);
            //viewHolder.cityTV = convertView.findViewById(R.id.cityTV);
            //viewHolder.stateTV = convertView.findViewById(R.id.stateTV);
            //viewHolder.zipTV = convertView.findViewById(R.id.zipTV);
            viewHolder.extraSpaceTV = convertView.findViewById(R.id.extraSpaceTV);

            viewHolder.startDateTV = convertView.findViewById(R.id.startDateTV);
            viewHolder.endDateTV = convertView.findViewById(R.id.endDateTV);

            viewHolder.firstNameTV = convertView.findViewById(R.id.firstNameTV);
            viewHolder.lastNameTV = convertView.findViewById(R.id.lastNameTV);

            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (lease != null) {
            Apartment apartment = dataMethods.getCachedApartmentByApartmentID(lease.getApartmentID());
            Tenant primaryTenant = dataMethods.getCachedTenantByTenantID(lease.getPrimaryTenantID());
            SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
            viewHolder.startDateTV.setText(formatter.format(lease.getLeaseStart()));
            viewHolder.endDateTV.setText(formatter.format(lease.getLeaseEnd()));
            if (apartment != null) {
                setTextHighlightSearch(viewHolder.street1TV, apartment.getStreet1());
                //If empty street 2, set invisible
                if (apartment.getStreet2() != null) {
                    if (apartment.getStreet2().equals("")) {
                        viewHolder.street2TV.setVisibility(View.GONE);
                    } else {
                        viewHolder.street2TV.setVisibility(View.VISIBLE);
                        setTextHighlightSearch(viewHolder.street2TV, apartment.getStreet2());
                    }
                } else {
                    viewHolder.street2TV.setVisibility(View.GONE);
                }
            } else {
                viewHolder.street1TV.setVisibility(View.INVISIBLE);
                viewHolder.street2TV.setVisibility(View.INVISIBLE);
            }
            if(primaryTenant != null){
                viewHolder.firstNameTV.setVisibility(View.VISIBLE);
                viewHolder.firstNameTV.setText(primaryTenant.getFirstName());
                viewHolder.lastNameTV.setVisibility(View.VISIBLE);
                viewHolder.lastNameTV.setText(primaryTenant.getLastName());
            } else {
                viewHolder.firstNameTV.setVisibility(View.INVISIBLE);
                viewHolder.lastNameTV.setVisibility(View.INVISIBLE);
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
                filteredResults = (ArrayList<Lease>) results.values;
                notifyDataSetChanged();
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                ArrayList<Lease> FilteredArrayNames = leaseArray;
                searchText = constraint.toString().toLowerCase();
                //Perform users search
                constraint = constraint.toString().toLowerCase();
                for (int i = 0; i < leaseArray.size(); i++) {
                    //    Lease dataNames = leaseArray.get(i);
                    //    String street2 = "";
                    //    if(dataNames.getStreet2() != null){
                    //        street2 = dataNames.getStreet2();
                    //    }
                    //If users search matches any part of any apartment value, add to new filtered list
                    //    if (dataNames.getStreet1().toLowerCase().contains(constraint.toString()) ||
                    //            street2.toLowerCase().contains(constraint.toString()) ||
                    //            dataNames.getCity().toLowerCase().contains(constraint.toString()) ||
                    //            dataNames.getState().toLowerCase().contains(constraint.toString()) ||
                    //            dataNames.getZip().toLowerCase().contains(constraint.toString())) {
                    //        FilteredArrayNames.add(dataNames);
                    //    }
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
    public ArrayList<Lease> getFilteredResults() {
        return filteredResults;
    }
}