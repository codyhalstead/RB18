package com.rba18.helpers;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageButton;

import com.rba18.R;

import java.util.ArrayList;

/**
 * Created by Cody on 1/18/2018.
 */

public class ColorChooserDialog extends Dialog {
    public ColorChooserDialog(@NonNull Context context) {
        super(context);
    }

    private ImageButton mOne;
    private ImageButton mTwo;
    private ImageButton mThree;
    private ImageButton mFour;
    private ImageButton mFive;
    private ImageButton mSix;
    private ImageButton mSeven;
    private ImageButton mEight;
    private ImageButton mNine;
    private ImageButton mTen;
    private ArrayList<Integer> mThemePrimaryColors;
    private ArrayList<Integer> mThemeBackgroundColors;
    private ArrayList<ImageButton> mButtons;
    private ColorListener mColorListener;
    private View.OnClickListener mListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_color_picker);

        mButtons = new ArrayList<>();
        mThemePrimaryColors = new ArrayList<>();
        mThemeBackgroundColors = new ArrayList<>();
        //Initialize ImageButtons
        mOne = findViewById(R.id.b1);
        mTwo = findViewById(R.id.b2);
        mThree = findViewById(R.id.b3);
        mFour = findViewById(R.id.b4);
        mFive = findViewById(R.id.b5);
        mSix = findViewById(R.id.b6);
        mSeven = findViewById(R.id.b7);
        mEight = findViewById(R.id.b8);
        mNine = findViewById(R.id.b9);
        mTen = findViewById(R.id.b10);
        //Add image mButtons to button array
        mButtons.add(mOne);
        mButtons.add(mTwo);
        mButtons.add(mThree);
        mButtons.add(mFour);
        mButtons.add(mFive);
        mButtons.add(mSix);
        mButtons.add(mSeven);
        mButtons.add(mEight);
        mButtons.add(mNine);
        mButtons.add(mTen);
        setOnClickListener();
        loadThemeArrays();
        colorize();
        setListeners();
    }

    private void setOnClickListener(){
        //Sets onClick mListener
        mListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mColorListener != null)
                    mColorListener.OnColorClick(v, (int) v.getTag());
                dismiss();
            }
        };
    }

    private void colorize() {
        //Sets button colors to match the themes they represent
        for (int i = 0; i < mButtons.size(); i++) {
            int[] colors = new int[2];
            colors[0] = mThemeBackgroundColors.get(i);
            colors[1] = mThemePrimaryColors.get(i);

            GradientDrawable d = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, colors);
            d.setGradientType(GradientDrawable.SWEEP_GRADIENT);
            d.setGradientCenter(-1, 0.3f);
            d.setBounds(58, 58, 58, 58);
            d.setStroke(2, Color.BLACK);
            if (Build.VERSION.SDK_INT > 15) {
                mButtons.get(i).setBackground(d);
            } else {
                mButtons.get(i).setBackgroundDrawable(d);
            }
        }
    }

    //Loads theme colors into array
    private void loadThemeArrays() {
        //Purple white
        mThemePrimaryColors.add(purple);
        mThemeBackgroundColors.add(whiteBackground);

        //Purple blue
        mThemePrimaryColors.add(gold);
        mThemeBackgroundColors.add(whiteBackground);

        //Pink white
        mThemePrimaryColors.add(pink);
        mThemeBackgroundColors.add(whiteBackground);

        //Pink blue
        mThemePrimaryColors.add(black);
        mThemeBackgroundColors.add(whiteBackground);

        //Orange white
        mThemePrimaryColors.add(orange);
        mThemeBackgroundColors.add(whiteBackground);

        //Orange grey
        mThemePrimaryColors.add(turquoise);
        mThemeBackgroundColors.add(whiteBackground);

        //Green white
        mThemePrimaryColors.add(green);
        mThemeBackgroundColors.add(whiteBackground);

        //Red white
        mThemePrimaryColors.add(red);
        mThemeBackgroundColors.add(whiteBackground);

        //Brown white
        mThemePrimaryColors.add(brown);
        mThemeBackgroundColors.add(whiteBackground);

        //Blue white
        mThemePrimaryColors.add(blue);
        mThemeBackgroundColors.add(whiteBackground);

    }

    public interface ColorListener {
        //Interface, to customize OnColorClick
        void OnColorClick(View v, int color);
    }

    private void setListeners() {
        //Set listeners and tags for all mButtons
        for (int i = 0; i < mButtons.size(); i++) {
            mButtons.get(i).setTag(i);
            mButtons.get(i).setOnClickListener(mListener);
        }
    }

    public void setColorListener(ColorListener listener) {
        //Sets color mListener
        mColorListener = listener;
    }

    //Theme primary colors
    private final int purple = Color.argb(255, 76, 0, 142);
    private final int pink = Color.argb(255, 193, 0, 166);
    private final int orange = Color.argb(255, 244, 115, 3);
    private final int green = Color.argb(255, 76, 175, 80);
    private final int red = Color.argb(255, 162, 2, 2);
    private final int brown = Color.argb(255, 80, 56, 48);
    private final int blue = Color.argb(255, 37, 62, 206);
    private final int black = Color.argb(255, 33, 31, 30);
    private final int gold = Color.argb(255, 170, 141, 11);
    private final int turquoise = Color.argb(255, 13, 156, 113);

    //Theme backgrounds
    //private final int blueBackground = Color.argb(255, 41, 182, 246);
    //private final int greyBackground = Color.argb(255, 77, 76, 75);
    private final int whiteBackground = Color.argb(255, 255, 255, 255);

}
