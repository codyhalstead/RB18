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

/**
 * Created by Cody on 1/11/2018.
 */

public class TenantListAdapter extends BaseAdapter implements Filterable {
    private ArrayList<Tenant> mTenantArray;
    private ArrayList<Tenant> mFilteredResults;
    private Context mContext;
    private String mSearchText;
    private ColorStateList mHighlightColor;
    private MainArrayDataMethods mDataMethods;
    private int mDateFormatCode;

    public TenantListAdapter(Context context, ArrayList<Tenant> tenantArray, ColorStateList highlightColor) {
        mTenantArray = tenantArray;
        mFilteredResults = tenantArray;
        mContext = context;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        mSearchText = "";
        mHighlightColor = highlightColor;
        mDataMethods = new MainArrayDataMethods();
        mDateFormatCode = preferences.getInt("dateFormat", DateAndCurrencyDisplayer.DATE_MMDDYYYY);
    }

    private static class ViewHolder {
        TextView nameTV;
        TextView phoneNumberTV;
        TextView leaseEndsTextDisplayTV;
        TextView leaseEndsTV;
        TextView isPrimaryTV;
        TextView rentingStatusTV;
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
    public Tenant getItem(int i) {
        return mFilteredResults.get(i);
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.row_tenant, parent, false);
            viewHolder = new ViewHolder();

            viewHolder.nameTV = convertView.findViewById(R.id.nameTV);
            viewHolder.phoneNumberTV = convertView.findViewById(R.id.phoneNumberTV);
            viewHolder.leaseEndsTextDisplayTV = convertView.findViewById(R.id.tenantRowLeaseEndDisplayTV);
            viewHolder.leaseEndsTV = convertView.findViewById(R.id.tenantRowLeaseEndTV);
            viewHolder.isPrimaryTV = convertView.findViewById(R.id.tenantRowLeaseIsPrimaryTV);
            viewHolder.rentingStatusTV = convertView.findViewById(R.id.tenantRowRentStatusTV);
            viewHolder.apartmentAddressTV = convertView.findViewById(R.id.tenantRowApartmentAddressTV);

            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (tenant != null) {
            setTextHighlightSearch(viewHolder.nameTV, tenant.getFirstAndLastNameString());
            setTextHighlightSearch(viewHolder.phoneNumberTV, tenant.getPhone());
            Lease currentLease = null;
            if (!tenant.getHasLease()) {
                viewHolder.leaseEndsTV.setText("");
                viewHolder.leaseEndsTextDisplayTV.setText("");
                viewHolder.isPrimaryTV.setText("");
            } else {
                currentLease = mDataMethods.getCachedActiveLeaseByTenantID(tenant.getId());
                viewHolder.leaseEndsTV.setText(R.string.error);
                viewHolder.isPrimaryTV.setText(R.string.error);
                if(currentLease != null) {
                    viewHolder.leaseEndsTV.setText(DateAndCurrencyDisplayer.getDateToDisplay(mDateFormatCode, currentLease.getLeaseEnd()));
                    if (tenant.getId() == currentLease.getPrimaryTenantID()) {
                        viewHolder.isPrimaryTV.setText(R.string.primary);
                    } else {
                        viewHolder.isPrimaryTV.setText(R.string.secondary);
                    }
                }
            }
            if (!tenant.getHasLease()) {
                convertView.setBackgroundColor(convertView.getResources().getColor(R.color.rowDarkenedBackground));
                viewHolder.rentingStatusTV.setText(R.string.not_currently_renting);
                viewHolder.apartmentAddressTV.setText("");
            } else {
                String renting = mContext.getResources().getString(R.string.renting) + " ";
                viewHolder.rentingStatusTV.setText(renting);
                if(currentLease != null) {
                    convertView.setBackgroundColor(convertView.getResources().getColor(R.color.white));
                    Apartment apartment = mDataMethods.getCachedApartmentByApartmentID(currentLease.getApartmentID());
                    if (apartment != null) {
                        viewHolder.apartmentAddressTV.setText(apartment.getStreet1AndStreet2String());
                    } else {
                        viewHolder.apartmentAddressTV.setText("");
                    }
                } else {
                    viewHolder.apartmentAddressTV.setText(R.string.error);
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
                mFilteredResults = (ArrayList<Tenant>) results.values;
                notifyDataSetChanged();
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                ArrayList<Tenant> FilteredArrayNames = new ArrayList<>();
                mSearchText = constraint.toString().toLowerCase();
                //Perform users search
                constraint = constraint.toString().toLowerCase();
                for (int i = 0; i < mTenantArray.size(); i++) {
                    Tenant dataNames = mTenantArray.get(i);
                    String phone = "";
                    if(dataNames.getPhone() != null){
                        phone = dataNames.getPhone();
                    }
                    String name = dataNames.getFirstAndLastNameString();
                    //If users search matches any part of any apartment value, add to new filtered list
                    if (dataNames.getFirstName().toLowerCase().contains(constraint.toString()) ||
                            dataNames.getLastName().toLowerCase().contains(constraint.toString()) ||
                            phone.toLowerCase().contains(constraint.toString()) ||
                            name.toLowerCase().contains(constraint.toString())) {
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
    public ArrayList<Tenant> getFilteredResults() {
        return mFilteredResults;
    }

    public void updateResults(ArrayList<Tenant> results) {
        mTenantArray = results;
        mFilteredResults = results;

        //Triggers the list update
        notifyDataSetChanged();
    }
}
