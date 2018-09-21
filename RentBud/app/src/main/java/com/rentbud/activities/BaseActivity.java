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

//Base activity with theme and toolbar set-ups
public class BaseActivity extends AppCompatActivity {
    //Theme constants
    public final static int THEME_PURPLE = 0;
    public final static int THEME_GOLD = 1;
    public final static int THEME_PINK = 2;
    public final static int THEME_BLACK = 3;
    public final static int THEME_ORANGE = 4;
    public final static int THEME_TURQUOISE = 5;
    public final static int THEME_GREEN = 6;
    public final static int THEME_RED = 7;
    public final static int THEME_BROWN = 8;
    public final static int THEME_BLUE = 9;

    public SharedPreferences preferences;
    public Toolbar toolbar;

    //onCreate
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.preferences = PreferenceManager.getDefaultSharedPreferences(this);
        this.toolbar = null;
    }

    //gets current primary theme color
    public int fetchPrimaryColor() {
        TypedValue typedValue = new TypedValue();
        TypedArray a = obtainStyledAttributes(typedValue.data, new int[]{R.attr.colorPrimary});
        int color = a.getColor(0, 0);
        a.recycle();
        return color;
    }

    //gets current background theme color
    public int fetchBackgroundColor() {
        TypedValue a = new TypedValue();
        getTheme().resolveAttribute(android.R.attr.windowBackground, a, true);
        return a.data;
    }

    public int fetchAccentColor() {
        TypedValue typedValue = new TypedValue();
        TypedArray a = obtainStyledAttributes(typedValue.data, new int[] { R.attr.colorAccent });
        int color = a.getColor(0, 0);
        a.recycle();

        return color;
    }

    //Sets theme choice, use before setContentView
    public void setupUserAppTheme(int theme) {
        switch (theme) {
            case THEME_PURPLE:
                break;

            case THEME_GOLD:
                getTheme().applyStyle(R.style.AppTheme_gold, true);
                break;

            case THEME_PINK:
                getTheme().applyStyle(R.style.AppTheme_pink, true);
                break;

            case THEME_BLACK:
                getTheme().applyStyle(R.style.AppTheme_black, true);
                break;

            case THEME_ORANGE:
                getTheme().applyStyle(R.style.AppTheme_orange, true);
                break;

            case THEME_TURQUOISE:
                getTheme().applyStyle(R.style.AppTheme_turquoise, true);
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

    //sets up a basic toolbar, layout xml must include R.id.toolbar
    public void setupBasicToolbar(){
        this.toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    public Toolbar getToolbar(){
        return this.toolbar;
    }

}
