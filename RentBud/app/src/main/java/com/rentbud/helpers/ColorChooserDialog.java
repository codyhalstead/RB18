package com.rentbud.helpers;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.example.cody.rentbud.R;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

/**
 * Created by Cody on 1/18/2018.
 */

public class ColorChooserDialog extends Dialog {
    public ColorChooserDialog(@NonNull Context context) {
        super(context);
    }

    private ImageButton one;
    private ImageButton two;
    private ImageButton three;
    private ImageButton four;
    private ImageButton five;
    private ImageButton six;
    private ImageButton seven;
    private ImageButton eight;
    private ImageButton nine;
    private ImageButton ten;
    private ArrayList<Integer> themePrimaryColors;
    private ArrayList<Integer> themeBackgroundColors;
    private ArrayList<ImageButton> buttons;
    private ColorListener colorListener;
    private View.OnClickListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_color_picker);

        buttons = new ArrayList<>();
        themePrimaryColors = new ArrayList<>();
        themeBackgroundColors = new ArrayList<>();
        //Initialize ImageButtons
        one = findViewById(R.id.b1);
        two = findViewById(R.id.b2);
        three = findViewById(R.id.b3);
        four = findViewById(R.id.b4);
        five = findViewById(R.id.b5);
        six = findViewById(R.id.b6);
        seven = findViewById(R.id.b7);
        eight = findViewById(R.id.b8);
        nine = findViewById(R.id.b9);
        ten = findViewById(R.id.b10);
        //Add image buttons to button array
        buttons.add(one);
        buttons.add(two);
        buttons.add(three);
        buttons.add(four);
        buttons.add(five);
        buttons.add(six);
        buttons.add(seven);
        buttons.add(eight);
        buttons.add(nine);
        buttons.add(ten);
        setOnClickListener();
        loadThemeArrays();
        colorize();
        setListeners();
    }

    private void setOnClickListener(){
        //Sets onClick listener
        this.listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (colorListener != null)
                    colorListener.OnColorClick(v, (int) v.getTag());
                dismiss();
            }
        };
    }

    private void colorize() {
        //Sets button colors to match the themes they represent
        for (int i = 0; i < buttons.size(); i++) {
            int[] colors = new int[2];
            colors[0] = themeBackgroundColors.get(i);
            colors[1] = themePrimaryColors.get(i);

            GradientDrawable d = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, colors);
            d.setGradientType(GradientDrawable.SWEEP_GRADIENT);
            d.setGradientCenter(-1, 0.3f);
            d.setBounds(58, 58, 58, 58);
            d.setStroke(2, Color.BLACK);
            if (Build.VERSION.SDK_INT > 15) {
                buttons.get(i).setBackground(d);
            } else {
                buttons.get(i).setBackgroundDrawable(d);
            }
        }
    }

    //Loads theme colors into array
    private void loadThemeArrays() {
        //Purple white
        themePrimaryColors.add(purple);
        themeBackgroundColors.add(whiteBackground);

        //Purple blue
        themePrimaryColors.add(purple);
        themeBackgroundColors.add(blueBackground);

        //Pink white
        themePrimaryColors.add(pink);
        themeBackgroundColors.add(whiteBackground);

        //Pink blue
        themePrimaryColors.add(pink);
        themeBackgroundColors.add(blueBackground);

        //Orange white
        themePrimaryColors.add(orange);
        themeBackgroundColors.add(whiteBackground);

        //Orange grey
        themePrimaryColors.add(orange);
        themeBackgroundColors.add(greyBackground);

        //Green white
        themePrimaryColors.add(green);
        themeBackgroundColors.add(whiteBackground);

        //Red white
        themePrimaryColors.add(red);
        themeBackgroundColors.add(whiteBackground);

        //Brown white
        themePrimaryColors.add(brown);
        themeBackgroundColors.add(whiteBackground);

        //Blue white
        themePrimaryColors.add(blue);
        themeBackgroundColors.add(whiteBackground);

    }

    public interface ColorListener {
        //Interface, to customize OnColorClick
        void OnColorClick(View v, int color);
    }

    private void setListeners() {
        //Set listeners and tags for all buttons
        for (int i = 0; i < buttons.size(); i++) {
            buttons.get(i).setTag(i);
            buttons.get(i).setOnClickListener(listener);
        }
    }

    public void setColorListener(ColorListener listener) {
        //Sets color listener
        this.colorListener = listener;
    }

    //Theme primary colors
    private final int purple = Color.argb(255, 76, 0, 142);
    private final int pink = Color.argb(255, 233, 30, 90);
    private final int orange = Color.argb(255, 244, 155, 3);
    private final int green = Color.argb(255, 76, 175, 80);
    private final int red = Color.argb(255, 255, 0, 0);
    private final int brown = Color.argb(255, 121, 85, 72);
    private final int blue = Color.argb(255, 37, 62, 206);

    //Theme backgrounds
    private final int blueBackground = Color.argb(255, 41, 182, 246);
    private final int greyBackground = Color.argb(255, 77, 76, 75);
    private final int whiteBackground = Color.argb(255, 255, 255, 255);

}
