package com.rentbud.helpers;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.cody.rentbud.R;
import com.rentbud.model.Apartment;
import com.rentbud.model.Tenant;

/**
 * Created by Cody on 4/4/2018.
 */

public class NewItemCreatorDialog extends Dialog {
    private Context context;
    TextView instructionsTV;
    EditText newItemET;
    Button saveBtn, cancelBtn;
    NewItemDialogResult dialogResult;

    public NewItemCreatorDialog(@NonNull Context context) {
        super(context);
        this.context = context;
        setContentView(R.layout.popup_new_item_creator);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.instructionsTV = findViewById(R.id.newItemCreationInstructionsTV);
        this.newItemET = findViewById(R.id.newItemCreationET);
        this.saveBtn = findViewById(R.id.newItemCreationSaveBtn);
        this.cancelBtn = findViewById(R.id.newItemCreationCancelBtn);
        setOnClickListeners();
    }

    public interface NewItemDialogResult {
        void finish(String string);
    }

    public void setDialogResult(NewItemCreatorDialog.NewItemDialogResult dialogResult) {
        this.dialogResult = dialogResult;
    }

    private void setOnClickListeners(){
        this.cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancel();
            }
        });
        this.saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!newItemET.getText().toString().equals("")){
                    newItemET.setError(null);
                    String result = newItemET.getText().toString();
                    dialogResult.finish(result);
                    NewItemCreatorDialog.this.dismiss();
                } else {
                    newItemET.setError(context.getResources().getString(R.string.cannot_be_empty));
                }
            }
        });
    }
}
