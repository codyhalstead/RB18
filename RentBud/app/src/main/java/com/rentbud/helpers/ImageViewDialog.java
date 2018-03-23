package com.rentbud.helpers;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.cody.rentbud.R;

/**
 * Created by Cody on 3/21/2018.
 */

public class ImageViewDialog extends Dialog{
    private Context context;
    private String imagePath;
    private Button exitBtn;
    private ImageView imageView;

    public ImageViewDialog(@NonNull Context context, String imagePath) {
        super(context);
        this.context = context;
        this.imagePath = imagePath;
        setContentView(R.layout.popup_image_dialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.exitBtn = findViewById(R.id.popupImageExitBtn);
        this.imageView = findViewById(R.id.popupImageIV);
        exitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        Glide.with(context).load(this.imagePath).into(imageView);
    }
}
