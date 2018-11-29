package com.rba18.helpers;

import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.rba18.R;
import com.rba18.adapters.TypeDialogListAdapter;
import com.rba18.model.TypeTotal;

import java.util.ArrayList;

public class TypeChooserDialog extends Dialog implements AdapterView.OnItemClickListener {
    private Context mContext;
    private ArrayList<TypeTotal> mTypes;
    private TextView mCancelTV, mSelectionTypeTV, mEmptyListTV;
    private EditText mSearchBarET;
    private ListView mListView;
    private ColorStateList mAccentColor;
    private TypeDialogListAdapter mListAdapter;
    private OnTypeChooserDialogResult mDialogResult;

    public TypeChooserDialog(Context context, ArrayList<TypeTotal> types) {
        super(context);
        mContext = context;
        mTypes = types;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_list_chooser);
        mSearchBarET = findViewById(R.id.popupListSearchET);
        mListView = findViewById(R.id.popupListListView);
        mCancelTV = findViewById(R.id.popupListCancelTV);
        mSelectionTypeTV = findViewById(R.id.popupListSelectTypeTV);
        mEmptyListTV = findViewById(R.id.popupListEmptyListTV);
        mSelectionTypeTV.setText(R.string.select_a_type_to_delete);
        mEmptyListTV.setText(R.string.no_types_to_display);
        TypedValue colorValue = new TypedValue();
        mContext.getTheme().resolveAttribute(R.attr.colorAccent, colorValue, true);
        mAccentColor = mContext.getResources().getColorStateList(colorValue.resourceId);
        setUpTypeListAdapter();
        setUpSearchBar();
        mCancelTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialogResult.finish(null);
                TypeChooserDialog.this.dismiss();
            }
        });
    }

    private void setUpTypeListAdapter() {
        mListAdapter = new TypeDialogListAdapter(mContext, mTypes, mAccentColor);
        mListView.setAdapter(mListAdapter);
        mListView.setOnItemClickListener(this);
        mListView.setEmptyView(mEmptyListTV);
    }

    public void changeCancelBtnText(String string) {
        mCancelTV.setText(string);
    }

    private void setUpSearchBar() {
        mSearchBarET.addTextChangedListener(new TextWatcher() {
            //For updating search results as user fileNames
            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                //When user changed the Text
                if (mListAdapter != null) {
                    mListAdapter.getFilter().filter(cs);
                    mListAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {

            }
        });
    }

    public interface OnTypeChooserDialogResult {
        void finish(TypeTotal selectedType);
    }

    public void setDialogResult(OnTypeChooserDialogResult dialogResult) {
        mDialogResult = dialogResult;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        TypeTotal type = mListAdapter.getFilteredResults().get(i);
        mDialogResult.finish(type);
        TypeChooserDialog.this.dismiss();
    }

}