package com.rentbud.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.widget.TextView;

import com.example.cody.rentbud.R;
import com.rentbud.model.Apartment;
import com.rentbud.model.Tenant;

/**
 * Created by Cody on 2/6/2018.
 */

public class ApartmentViewActivity extends BaseActivity {
    Apartment apartment;
    TextView addressLine1TV;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupUserAppTheme(MainActivity.curThemeChoice);
        setContentView(R.layout.activity_apartment_view);
        Bundle bundle = getIntent().getExtras();
        apartment = (Apartment) bundle.get("apartment");

        addressLine1TV = findViewById(R.id.addressLine1);
        addressLine1TV.setText(apartment.getStreet1());
        setupBasicToolbar();
    }
}
