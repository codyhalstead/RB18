package com.rentbud.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.cody.rentbud.R;
import com.rentbud.fragments.ApartmentListFragment;
import com.rentbud.fragments.TenantListFragment;
import com.rentbud.model.Apartment;
import com.rentbud.model.Tenant;
import com.rentbud.sqlite.DatabaseHandler;

/**
 * Created by Cody on 2/6/2018.
 */

public class TenantViewActivity extends BaseActivity {
    Tenant tenant;
    TextView firstNameTV, lastNameTV, renterStatusTV, phoneTV, leaseStartTV, leaseEndTV, notesTV;
    DatabaseHandler databaseHandler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupUserAppTheme(MainActivity.curThemeChoice);
        setContentView(R.layout.activity_tenant_view);
        Bundle bundle = getIntent().getExtras();
        tenant = (Tenant) bundle.get("tenant");
        databaseHandler = new DatabaseHandler(this);

        firstNameTV = findViewById(R.id.tenantViewFirstNameTextView);
        lastNameTV = findViewById(R.id.tenantViewLastNameTextView);
        renterStatusTV = findViewById(R.id.tenantViewRentingStatusTextView);
        phoneTV = findViewById(R.id.tenantViewPhoneTextView);
        leaseStartTV = findViewById(R.id.tenantViewLeaseStartTextView);
        leaseEndTV = findViewById(R.id.tenantViewLeaseEndTextView);
        notesTV = findViewById(R.id.tenantViewNotesTextView);

        fillTextViews();

        setupBasicToolbar();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Get options menu
        getMenuInflater().inflate(R.menu.tenant_view_edit_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    //Handle option menu actions
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.editTenant:
                Intent intent = new Intent(this, NewTenantFormActivity.class);
                intent.putExtra("tenantToEdit", tenant);
                startActivityForResult(intent, MainActivity.REQUEST_NEW_TENANT_FORM);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MainActivity.REQUEST_NEW_TENANT_FORM) {
            //If successful(not cancelled, passed validation)
            if (resultCode == RESULT_OK) {
                //Re-query cached apartment array to update cache and refresh current fragment to display new data
                MainActivity.tenantList = databaseHandler.getUsersTenants(MainActivity.user);
                this.tenant = (Tenant) data.getExtras().get("newTenantInfo");
                fillTextViews();
                TenantListFragment.tenantListAdapterNeedsRefreshed = true;
            }
        }
    }

    public void fillTextViews() {
        firstNameTV.setText(tenant.getFirstName());
        lastNameTV.setText(tenant.getLastName());
        //renterStatusTV.setText();
        phoneTV.setText(tenant.getPhone());
        leaseStartTV.setText(tenant.getLeaseStart());
        leaseEndTV.setText(tenant.getLeaseEnd());
        notesTV.setText(tenant.getNotes());
    }
}
