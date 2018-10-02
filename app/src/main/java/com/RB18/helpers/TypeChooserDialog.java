package com.RB18.helpers;

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

import com.example.cody.rentbud.R;
import com.RB18.adapters.TypeDialogListAdapter;
import com.RB18.model.TypeTotal;

import java.util.ArrayList;

public class TypeChooserDialog extends Dialog implements AdapterView.OnItemClickListener {
    Context context;
    ArrayList<TypeTotal> types;

    public TypeChooserDialog(Context context, ArrayList<TypeTotal> types) {
        super(context);
        this.context = context;
        this.types = types;
    }

    private TextView cancelTV, selectionTypeTV, emptyListTV;
    private EditText searchBarET;
    private ListView listView;
    private ColorStateList accentColor;
    private TypeDialogListAdapter listAdapter;
    private OnTypeChooserDialogResult dialogResult;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_list_chooser);
        this.searchBarET = findViewById(R.id.popupListSearchET);
        this.listView = findViewById(R.id.popupListListView);
        this.cancelTV = findViewById(R.id.popupListCancelTV);
        this.selectionTypeTV = findViewById(R.id.popupListSelectTypeTV);
        this.emptyListTV = findViewById(R.id.popupListEmptyListTV);
        selectionTypeTV.setText(R.string.select_a_type_to_delete);
        emptyListTV.setText(R.string.no_types_to_display);
        TypedValue colorValue = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.colorAccent, colorValue, true);
        this.accentColor = context.getResources().getColorStateList(colorValue.resourceId);
        setUpTypeListAdapter();
        setUpSearchBar();
        cancelTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogResult.finish(null);
                TypeChooserDialog.this.dismiss();
            }
        });
    }

    private void setUpTypeListAdapter() {
        listAdapter = new TypeDialogListAdapter(context, types, accentColor);
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener(this);
        this.listView.setEmptyView(emptyListTV);
    }

    public void changeCancelBtnText(String string) {
        this.cancelTV.setText(string);
    }

    private void setUpSearchBar() {
        searchBarET.addTextChangedListener(new TextWatcher() {
            //For updating search results as user fileNames
            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                //When user changed the Text
                if (listAdapter != null) {
                    listAdapter.getFilter().filter(cs);
                    listAdapter.notifyDataSetChanged();
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
        this.dialogResult = dialogResult;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        TypeTotal type = listAdapter.getFilteredResults().get(i);
        dialogResult.finish(type);
        TypeChooserDialog.this.dismiss();
    }

}