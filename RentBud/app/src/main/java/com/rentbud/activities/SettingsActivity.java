package com.rentbud.activities;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.cody.rentbud.R;
import com.rentbud.helpers.ColorChooserDialog;
import com.rentbud.helpers.ColorListener;

import java.util.ArrayList;

/**
 * Created by Cody on 1/18/2018.
 */

public class SettingsActivity extends BaseActivity {
    ImageButton colorBtn;
    SharedPreferences preferences;
    private String email;
    private int theme;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        email = preferences.getString("last_user_email", "");
        theme = preferences.getInt(email, 0);
        setupUserAppTheme(theme);
        setContentView(R.layout.activity_settings);
        colorBtn = findViewById(R.id.button_color);
        Colorize(colorBtn);
        setupBasicToolbar();
        colorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopup(view);
            }
        });
    }

    private void Colorize(ImageView colorBtn) {
        int[] colors = new int[2];
        colors[0] = fetchBackgroundColor();
        colors[1] = fetchPrimaryColor();

        GradientDrawable d = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, colors);
        d.setGradientType(GradientDrawable.SWEEP_GRADIENT);
        d.setGradientCenter(-1, 0.3f);
        d.setBounds(58, 58, 58, 58);
        d.setStroke(2, Color.BLACK);
        if (Build.VERSION.SDK_INT > 15) {
            colorBtn.setBackground(d);
        } else {
            colorBtn.setBackgroundDrawable(d);
        }
    }

    public void showPopup(View v) {
        ColorChooserDialog dialog = new ColorChooserDialog(SettingsActivity.this);
        dialog.setColorListener(new ColorListener() {
            @Override
            public void OnColorClick(View v, int color) {
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt(email, color);
                editor.commit();

                Intent intent = new Intent(SettingsActivity.this, SettingsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
        dialog.show();
    }
}
