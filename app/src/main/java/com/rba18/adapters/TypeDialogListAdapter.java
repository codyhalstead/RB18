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
import android.widget.Filterable;
import android.widget.TextView;

import com.rba18.R;
import com.rba18.model.TypeTotal;

import android.widget.Filter;

import java.util.ArrayList;
import java.util.Locale;

public class TypeDialogListAdapter extends BaseAdapter implements Filterable {
    private Context mContext;
    private String mSearchText;
    private ColorStateList mHighlightColor;
    private ArrayList<TypeTotal> mTypes, mFilteredResults;

    public TypeDialogListAdapter(Context context, ArrayList<TypeTotal> types, ColorStateList highlightColor) {
        mTypes = types;
        mFilteredResults = types;
        mContext = context;
        mSearchText = "";
        mHighlightColor = highlightColor;
    }

    private static class ViewHolder {
        TextView typeLabelTV;
    }

    @Override
    public int getCount() {
        if (mFilteredResults != null) {
            return mFilteredResults.size();
        }
        return 0;
    }

    @Override
    public TypeTotal getItem(int i) {
        return mFilteredResults.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        TypeTotal type = getItem(position);
        ViewHolder viewHolder;
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.row_dialog_type, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.typeLabelTV = convertView.findViewById(R.id.typeDialogRowTypeLabelTV);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (type != null) {
            setTextHighlightSearch(viewHolder.typeLabelTV, type.getTypeLabel());
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
                mFilteredResults = (ArrayList<TypeTotal>) results.values;
                notifyDataSetChanged();
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                ArrayList<TypeTotal> FilteredArrayNames = new ArrayList<>();
                mSearchText = constraint.toString().toLowerCase();
                //Perform users search
                constraint = constraint.toString().toLowerCase();
                for (int i = 0; i < mTypes.size(); i++) {
                    TypeTotal dataNames = mTypes.get(i);
                    //If users search matches any part of any apartment value, add to new filtered list
                    if (dataNames.getTypeLabel().toLowerCase().contains(constraint.toString())) {
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
    public ArrayList<TypeTotal> getFilteredResults() {
        return mFilteredResults;
    }

    public void updateResults(ArrayList<TypeTotal> results) {
        mTypes = results;
        mFilteredResults = results;

        //Triggers the list update
        notifyDataSetChanged();
    }
}