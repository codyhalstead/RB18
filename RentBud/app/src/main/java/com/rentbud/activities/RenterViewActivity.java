package com.rentbud.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.example.cody.rentbud.R;
import com.rentbud.model.Tenant;

import java.util.Date;

/**
 * Created by Cody on 2/6/2018.
 */

public class RenterViewActivity extends BaseActivity{
    SharedPreferences preferences;
    String email;
    int theme;
    Tenant tenant;
    TextView tenantNameTV;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        email = preferences.getString("last_user_email", "");
        theme = preferences.getInt(email, 0);
        setupUserAppTheme(theme);
        setContentView(R.layout.activity_renter_view);
        Bundle bundle = getIntent().getExtras();
        tenant = (Tenant) bundle.get("tenant");

        tenantNameTV = findViewById(R.id.renterViewTenantNameTextView);
        tenantNameTV.setText(tenant.getFirstName());
        setupBasicToolbar();
    }
}
