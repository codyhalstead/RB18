package com.rentbud.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.TextAppearanceSpan;
import android.util.Log;
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
import com.example.cody.rentbud.R;
import com.rentbud.activities.MainActivity;
import com.rentbud.helpers.MainArrayDataMethods;
import com.rentbud.model.Apartment;
import com.rentbud.model.ExpenseLogEntry;
import com.rentbud.model.Lease;
import com.rentbud.model.Tenant;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.lang.ref.WeakReference;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import static android.content.ContentValues.TAG;

/**
 * Created by Cody on 1/11/2018.
 */

public class ApartmentListAdapter extends BaseAdapter implements Filterable {
    private ArrayList<Apartment> apartmentArray;
    private ArrayList<Apartment> filteredResults;
    private Context context;
    private String searchText;
    private ColorStateList highlightColor;
    MainArrayDataMethods dataMethods;

    public ApartmentListAdapter(Context context, ArrayList<Apartment> apartmentArray, ColorStateList highlightColor) {
        super();
        this.apartmentArray = apartmentArray;
        this.filteredResults = apartmentArray;
        this.context = context;
        this.searchText = "";
        this.highlightColor = highlightColor;
        this.dataMethods = new MainArrayDataMethods();
    }

    static class ViewHolder {
        TextView street1TV;
        TextView street2TV;
        TextView cityTV;
        TextView stateTV;
        TextView zipTV;
        TextView rentedByTV;
        TextView tenantFirstName;
        TextView tenantLastName;
        TextView leaseEndTV;
        LinearLayout leaseLL;
        ImageView mainPicIV;
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
        ViewHolder viewHolder;
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.row_apartment, parent, false);
            viewHolder = new ViewHolder();

            viewHolder.street1TV = convertView.findViewById(R.id.apartmentRowStreet1TV);
            viewHolder.street2TV = convertView.findViewById(R.id.apartmentRowStreet2TV);
            viewHolder.cityTV = convertView.findViewById(R.id.apartmentRowCityTV);
            viewHolder.stateTV = convertView.findViewById(R.id.apartmentRowStateTV);
            viewHolder.zipTV = convertView.findViewById(R.id.apartmentRowZipTV);
            viewHolder.rentedByTV = convertView.findViewById(R.id.apartmentRowRentedByTV);
            viewHolder.tenantFirstName = convertView.findViewById(R.id.apartmentRowTenantFirstNameTV);
            viewHolder.tenantLastName = convertView.findViewById(R.id.apartmentRowTenantLastNameTV);
            viewHolder.leaseEndTV = convertView.findViewById(R.id.apartmentRowLeaseEndTV);
            viewHolder.leaseLL = convertView.findViewById(R.id.apartmentRowLeaseLL);
            viewHolder.mainPicIV = convertView.findViewById(R.id.apartmentRowMainPicIV);
            //viewHolder.position = position;

            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            // viewHolder.position = position;
            //  if(viewHolder.imageDownloaderTask != null){
            //      viewHolder.imageDownloaderTask.cancel(true);
            //    viewHolder.imageDownloaderTask = null;
            // }

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
            String city = apartment.getCity();
            //If city not empty, add comma
            if (!apartment.getCity().equals("")) {
                city += ",";
            }
            setTextHighlightSearch(viewHolder.cityTV, city);
            setTextHighlightSearch(viewHolder.stateTV, apartment.getState());
            setTextHighlightSearch(viewHolder.zipTV, apartment.getZip());
            Lease currentLease = null;
            Tenant primaryTenant = null;
            if (apartment.isRented()) {
                currentLease = dataMethods.getCachedActiveLeaseByApartmentID(apartment.getId());
                primaryTenant = dataMethods.getCachedPrimaryTenantByLease(currentLease);
            }
            if (primaryTenant != null && currentLease != null) {
                convertView.setBackgroundColor(convertView.getResources().getColor(R.color.white));
                viewHolder.rentedByTV.setText("Rented By:");
                viewHolder.tenantFirstName.setText(primaryTenant.getFirstName());
                viewHolder.tenantLastName.setText(primaryTenant.getLastName());
                viewHolder.leaseLL.setVisibility(View.VISIBLE);

                SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
                viewHolder.leaseEndTV.setText(formatter.format(currentLease.getLeaseEnd()));

            } else {
                convertView.setBackgroundColor(convertView.getResources().getColor(R.color.lightGrey));
                viewHolder.rentedByTV.setText("Vacant");
                viewHolder.tenantFirstName.setText("");
                viewHolder.tenantLastName.setText("");
                viewHolder.leaseLL.setVisibility(View.GONE);
                viewHolder.leaseEndTV.setText("");
            }
            viewHolder.mainPicIV.setImageBitmap(null);
            if (apartment.getMainPic() != null && !apartment.getMainPic().equals("")) {
                if (viewHolder.mainPicIV != null) {
                    //viewHolder.mainPicIV.setImageResource(R.drawable.blank_home_pic);
                    // viewHolder.shouldHavePic = true;
                    // ImageDownloaderTask imageDownloaderTask = new ImageDownloaderTask(viewHolder.mainPicIV, apartment.getMainPic(), viewHolder, position);
                    /////// imageDownloaderTask.execute();
                    Glide.with(context).load(apartment.getMainPic()).into(viewHolder.mainPicIV);
                    //imageDownloaderTask.cancel(true);
                    // new ImageLoaderTask(viewHolder.mainPicIV, apartment.getMainPic(), viewHolder, position).execute();
                }
            } else {
                viewHolder.mainPicIV.setImageResource(R.drawable.blank_home_pic);
                //viewHolder.shouldHavePic = false;
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
