package com.rentbud.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.cody.rentbud.R;
import com.rentbud.fragments.TenantListFragment;
import com.rentbud.model.Tenant;
import com.rentbud.sqlite.DatabaseHandler;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Cody on 2/6/2018.
 */

public class TenantViewActivity extends BaseActivity {
    Tenant tenant;
    TextView firstNameTV, lastNameTV, renterStatusTV, phoneTV, leaseStartTV, leaseEndTV, notesTV, apartmentAddressTV, apartmentAddress2TV, leaseHolderTypeTV;
    Button editLeaseBtn;
    LinearLayout leaseLL;
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
        apartmentAddressTV = findViewById(R.id.tenantViewRentingAddressTextView);
        apartmentAddress2TV = findViewById(R.id.tenantViewRentingAddress2TextView);
        leaseHolderTypeTV = findViewById(R.id.tenantViewLeaseHolderType);
        editLeaseBtn = findViewById(R.id.tenantViewEditLeaseBtn);
        leaseLL = findViewById(R.id.tenantViewLeaseLL);

        fillTextViews();
        setOnClickListeners();
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
        phoneTV.setText(tenant.getPhone());
        if (tenant.getApartmentID() == 0) {
            renterStatusTV.setText("Not Currently Renting");
            apartmentAddressTV.setText("");
            apartmentAddress2TV.setVisibility(View.GONE);
        } else {
            renterStatusTV.setText("Renting");
            for (int i = 0; i < MainActivity.apartmentList.size(); i++) {
                if (MainActivity.apartmentList.get(i).getId() == tenant.getApartmentID()) {
                    apartmentAddressTV.setText(MainActivity.apartmentList.get(i).getStreet1());
                    if (MainActivity.apartmentList.get(i).getStreet2().equals("")) {
                        apartmentAddress2TV.setVisibility(View.GONE);
                    } else {
                        apartmentAddress2TV.setText(MainActivity.apartmentList.get(i).getStreet2());
                    }
                    break;
                }
            }
        }
        if (!tenant.getLeaseStart().equals(" ")) {
            SimpleDateFormat formatTo = new SimpleDateFormat("MM-dd-yyyy");
            DateFormat formatFrom = new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy");
            try {
                Date startDate = formatFrom.parse(tenant.getLeaseStart());
                Date endDate = formatFrom.parse(tenant.getLeaseEnd());
                leaseStartTV.setText(formatTo.format(startDate));
                leaseEndTV.setText(formatTo.format(endDate));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if(tenant.getIsPrimary()){
                leaseHolderTypeTV.setText("Primary Tenant");
            }else {
                leaseHolderTypeTV.setText("Secondary Tenant");
            }
        } else {
            leaseLL.setVisibility(View.GONE);
            leaseHolderTypeTV.setVisibility(View.GONE);
        }
        notesTV.setText(tenant.getNotes());
    }


    private void setOnClickListeners() {
        this.editLeaseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TenantViewActivity.this, NewLeaseFormActivity.class);
                //Uses filtered results to match what is on screen
                intent.putExtra("tenant", tenant);
                startActivity(intent);
            }
        });
    }
}
