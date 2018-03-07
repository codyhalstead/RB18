package com.rentbud.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.cody.rentbud.R;
import com.rentbud.helpers.ColorChooserDialog;


/**
 * Created by Cody on 1/18/2018.
 */

public class SettingsActivity extends BaseActivity {
    ImageButton colorBtn;
    SharedPreferences preferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Preferences must be initialized before setContentView because it is used in determining activities theme
        //Will be different from static MainActivity.currentThemeChoice when user selects themes within this activity
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        int theme = preferences.getInt(MainActivity.user.getEmail(), 0);
        setupUserAppTheme(theme);
        setContentView(R.layout.activity_settings);
        initializeVariables();
        setupBasicToolbar();
        //Color theme selection button to current theme choice
        Colorize(colorBtn);
        setOnClickListeners();
    }

    private void Colorize(ImageView colorBtn) {
        //Get current theme colors
        int[] colors = new int[2];
        colors[0] = fetchBackgroundColor();
        colors[1] = fetchPrimaryColor();
        //Sets color button colors to match current theme
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

    //Shows theme chooser dialog
    public void showColorPopup(View v) {
        //Create the dialog
        ColorChooserDialog dialog = new ColorChooserDialog(SettingsActivity.this);
        dialog.setColorListener(new ColorChooserDialog.ColorListener() {
            @Override
            public void OnColorClick(View v, int color) {
                //On selection, change current theme choice saved in shared preferences
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt(MainActivity.user.getEmail(), color);
                editor.commit();
                //Re-create activity with new theme
                Intent intent = new Intent(SettingsActivity.this, SettingsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
        dialog.show();
    }

    private void initializeVariables() {
        colorBtn = findViewById(R.id.button_color);
    }

    private void setOnClickListeners() {
        colorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Show theme chooser dialog onClick
                showColorPopup(view);
            }
        });
    }
}
