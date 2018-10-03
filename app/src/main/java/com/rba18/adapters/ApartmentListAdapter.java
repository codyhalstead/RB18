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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
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

public class ApartmentListAdapter extends BaseAdapter implements Filterable {
    private ArrayList<Apartment> apartmentArray;
    private ArrayList<Apartment> filteredResults;
    private SharedPreferences preferences;
    private Context context;
    private String searchText;
    private ColorStateList highlightColor;
    private int dateFormatCode;
    MainArrayDataMethods dataMethods;

    public ApartmentListAdapter(Context context, ArrayList<Apartment> apartmentArray, ColorStateList highlightColor) {
        super();
        this.preferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.apartmentArray = apartmentArray;
        this.filteredResults = apartmentArray;
        this.context = context;
        this.searchText = "";
        this.highlightColor = highlightColor;
        this.dataMethods = new MainArrayDataMethods();
        this.dateFormatCode = preferences.getInt("dateFormat", DateAndCurrencyDisplayer.DATE_MMDDYYYY);
    }

    static class ViewHolder {
        TextView street1TV;
        TextView street2TV;
        TextView cityStateZipTV;
        TextView rentedByTV;
        TextView tenantNameTV;
        TextView leaseEndTV;
        LinearLayout leaseLL;
        ImageView mainPicIV;
    }

    @Override
    public int getCount() {
        if(filteredResults != null) {
            return filteredResults.size();
        }
        return 0;
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
        ViewHolder viewHolder;
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.row_apartment, parent, false);
            viewHolder = new ViewHolder();

            viewHolder.street1TV = convertView.findViewById(R.id.apartmentRowStreet1TV);
            viewHolder.street2TV = convertView.findViewById(R.id.apartmentRowStreet2TV);
            viewHolder.cityStateZipTV = convertView.findViewById(R.id.apartmentRowCityStateZipTV);
            viewHolder.rentedByTV = convertView.findViewById(R.id.apartmentRowRentedByTV);
            viewHolder.tenantNameTV = convertView.findViewById(R.id.apartmentRowTenantNameTV);
            viewHolder.leaseEndTV = convertView.findViewById(R.id.apartmentRowLeaseEndTV);
            viewHolder.leaseLL = convertView.findViewById(R.id.apartmentRowLeaseLL);
            viewHolder.mainPicIV = convertView.findViewById(R.id.apartmentRowMainPicIV);
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
                } else {
                    viewHolder.street2TV.setVisibility(View.VISIBLE);
                    setTextHighlightSearch(viewHolder.street2TV, apartment.getStreet2());
                }
            } else {
                viewHolder.street2TV.setText("");
                viewHolder.street2TV.setVisibility(View.GONE);
            }
            setTextHighlightSearch(viewHolder.cityStateZipTV, apartment.getCityStateZipString());
            Lease currentLease = null;
            Tenant primaryTenant = null;
            if (apartment.isRented()) {
                currentLease = dataMethods.getCachedActiveLeaseByApartmentID(apartment.getId());
                primaryTenant = dataMethods.getCachedPrimaryTenantByLease(currentLease);
            }
            if (primaryTenant != null && currentLease != null) {
                convertView.setBackgroundColor(convertView.getResources().getColor(R.color.white));
                viewHolder.rentedByTV.setText(R.string.rented_by);
                viewHolder.tenantNameTV.setText(primaryTenant.getFirstAndLastNameString());
                viewHolder.leaseLL.setVisibility(View.VISIBLE);
                String s = context.getResources().getString(R.string.until) + DateAndCurrencyDisplayer.getDateToDisplay(dateFormatCode, currentLease.getLeaseEnd());
                viewHolder.leaseEndTV.setText(s);
            } else {
                convertView.setBackgroundColor(convertView.getResources().getColor(R.color.rowDarkenedBackground));
                viewHolder.rentedByTV.setText(R.string.vacant);
                viewHolder.tenantNameTV.setText("");
                viewHolder.leaseLL.setVisibility(View.GONE);
                viewHolder.leaseEndTV.setText("");
            }
            viewHolder.mainPicIV.setImageBitmap(null);
            if (apartment.getMainPic() != null && !apartment.getMainPic().equals("")) {
                if (viewHolder.mainPicIV != null) {
                    Glide.with(context).load(apartment.getMainPic()).override(120, 120).centerCrop().placeholder(R.drawable.no_picture).into(viewHolder.mainPicIV);
                }
            } else {
                Glide.with(context).load(R.drawable.blank_home_pic).override(120, 120).centerCrop().into(viewHolder.mainPicIV);
            }
        }
        return convertView;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                //Update filtered results and notify change
                filteredResults = (ArrayList<Apartment>) results.values;
                notifyDataSetChanged();
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                ArrayList<Apartment> FilteredArrayNames = new ArrayList<>();
                searchText = constraint.toString().toLowerCase();
                //Perform users search
                constraint = constraint.toString().toLowerCase();
                for (int i = 0; i < apartmentArray.size(); i++) {
                    Apartment dataNames = apartmentArray.get(i);
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
    public ArrayList<Apartment> getFilteredResults() {
        return this.filteredResults;
    }

    public void updateResults(ArrayList<Apartment> results) {
        apartmentArray = results;
        filteredResults = results;

        //Triggers the list update
        notifyDataSetChanged();
    }

}
