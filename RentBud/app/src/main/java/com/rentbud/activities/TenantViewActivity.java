package com.rentbud.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;

import com.example.cody.rentbud.R;
import com.rentbud.model.Tenant;

/**
 * Created by Cody on 2/6/2018.
 */

public class TenantViewActivity extends BaseActivity{
    Tenant tenant;
    TextView tenantNameTV;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupUserAppTheme(MainActivity.curThemeChoice);
        setContentView(R.layout.activity_tenant_view);
        Bundle bundle = getIntent().getExtras();
        tenant = (Tenant) bundle.get("tenant");

        tenantNameTV = findViewById(R.id.tenantViewTenantNameTextView);
        tenantNameTV.setText(tenant.getFirstName());
        setupBasicToolbar();
    }
}
