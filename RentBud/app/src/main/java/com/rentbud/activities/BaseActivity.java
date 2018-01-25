package com.rentbud.activities;

import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;

import com.example.cody.rentbud.R;

/**
 * Created by Cody on 1/25/2018.
 */

public class BaseActivity extends AppCompatActivity {
    //SharedPreferences preferences;
    public final static int THEME_PURPLE = 0;
    public final static int THEME_PURPLE_BLUE = 1;
    public final static int THEME_PINK = 2;
    public final static int THEME_PINK_BLUE = 3;
    public final static int THEME_ORANGE = 4;
    public final static int THEME_ORANGE_GREY = 5;
    public final static int THEME_GREEN = 6;
    public final static int THEME_RED = 7;
    public final static int THEME_BROWN = 8;
    public final static int THEME_BLUE = 9;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //preferences = PreferenceManager.getDefaultSharedPreferences(this);
        //String email = preferences.getString("last_user_email", "");
        //int theme = preferences.getInt(email, 0);
        //setupUserAppTheme(theme);
    }

    public int fetchPrimaryColor() {
        TypedValue typedValue = new TypedValue();
        TypedArray a = obtainStyledAttributes(typedValue.data, new int[]{R.attr.colorPrimary});
        int color = a.getColor(0, 0);
        a.recycle();
        return color;
    }

    public int fetchBackgroundColor() {
        TypedValue a = new TypedValue();
        getTheme().resolveAttribute(android.R.attr.windowBackground, a, true);
        return a.data;
    }

    public void setupUserAppTheme(int theme) {
        switch (theme) {
            case THEME_PURPLE:
                break;

            case THEME_PURPLE_BLUE:
                getTheme().applyStyle(R.style.AppTheme_purple_blue, true);
                break;

            case THEME_PINK:
                getTheme().applyStyle(R.style.AppTheme_pink, true);
                break;

            case THEME_PINK_BLUE:
                getTheme().applyStyle(R.style.AppTheme_pink_blue, true);
                break;

            case THEME_ORANGE:
                getTheme().applyStyle(R.style.AppTheme_orange, true);
                break;

            case THEME_ORANGE_GREY:
                getTheme().applyStyle(R.style.AppTheme_orange_grey, true);
                break;

            case THEME_GREEN:
                getTheme().applyStyle(R.style.AppTheme_green, true);
                break;

            case THEME_RED:
                getTheme().applyStyle(R.style.AppTheme_red, true);
                break;

            case THEME_BROWN:
                getTheme().applyStyle(R.style.AppTheme_brown, true);
                break;

            case THEME_BLUE:
                getTheme().applyStyle(R.style.AppTheme_blue, true);
                break;

            default:
                break;

        }
    }

    public void setupBasicToolbar(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

}
