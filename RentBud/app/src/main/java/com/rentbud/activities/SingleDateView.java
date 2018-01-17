package com.rentbud.activities;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.example.cody.rentbud.R;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Cody on 1/12/2018.
 */

public class SingleDateView extends AppCompatActivity {
    TextView dateTV;
    Date date;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_date_view);
        Bundle bundle = getIntent().getExtras();
        date = (Date)bundle.get("date");
        final SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM dd yyyy");
        dateTV = findViewById(R.id.selectedDateTV);
        dateTV.setText(formatter.format(date));
    }
}
