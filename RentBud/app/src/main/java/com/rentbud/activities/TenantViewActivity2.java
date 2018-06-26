package com.rentbud.activities;

import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;

import com.example.cody.rentbud.R;
import com.rentbud.fragments.ApartmentListFragment;
import com.rentbud.fragments.TenantListFragment;
import com.rentbud.fragments.TenantViewFrag1;
import com.rentbud.fragments.TenantViewFrag2;
import com.rentbud.helpers.MainArrayDataMethods;
import com.rentbud.model.Apartment;
import com.rentbud.model.Lease;
import com.rentbud.model.Tenant;
import com.rentbud.sqlite.DatabaseHandler;

import java.util.ArrayList;
import java.util.List;

public class TenantViewActivity2 extends BaseActivity {
    private DatabaseHandler databaseHandler;
    private MainArrayDataMethods dataMethods;
    private ArrayList<Tenant> otherTenants;
    private Lease currentLease;
    private Tenant tenant;
    private Apartment apartment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupUserAppTheme(MainActivity.curThemeChoice);
        setContentView(R.layout.activity_lease_view_actual);

        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        databaseHandler = new DatabaseHandler(this);
        dataMethods = new MainArrayDataMethods();

        // Add Fragments to adapter one by one
        Bundle bundle = getIntent().getExtras();
        int tenantID = bundle.getInt("tenantID");
        tenant = dataMethods.getCachedTenantByTenantID(tenantID);
        currentLease = dataMethods.getCachedActiveLeaseByTenantID(tenantID);
        //   int apartmentID = dataMethods.getCachedApartmentByApartmentID(currentLease.getApartmentID());
        if (!tenant.getHasLease()) {
            this.tenant = dataMethods.getCachedTenantByTenantID(tenantID);
            this.apartment = null;
        } else {
            Pair<Tenant, ArrayList<Tenant>> tenants = dataMethods.getCachedSelectedTenantAndRoomMatesByLease(currentLease, tenantID);
            this.tenant = tenants.first;
            this.otherTenants = tenants.second;
            this.apartment = dataMethods.getCachedApartmentByApartmentID(currentLease.getApartmentID());
        }
        //int leaseID = bundle.getInt("leaseID");
        Fragment frag1 = new TenantViewFrag1();
        Fragment frag2 = new TenantViewFrag2();
        frag1.setArguments(bundle);
        frag2.setArguments(bundle);
        adapter.addFragment(frag1, "Tenant Info");
        adapter.addFragment(frag2, "Payments");
        // adapter.addFragment(new FragmentThree(), "FRAG3");
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setSelectedTabIndicatorColor(Color.parseColor("#000000"));
        tabLayout.setTabTextColors(Color.parseColor("#ffffff"), Color.parseColor("#4d4c4b"));
        tabLayout.setupWithViewPager(viewPager);
        setupBasicToolbar();
        toolbar.setTitle("Tenant View");
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
                Intent intent = new Intent(this, NewTenantWizard.class);
                intent.putExtra("tenantToEdit", tenant);
                startActivityForResult(intent, MainActivity.REQUEST_NEW_TENANT_FORM);
                return true;

            case R.id.deleteTenant:
                showDeleteConfirmationAlertDialog();
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
                //int tenantID  = data.getIntExtra("editedTenantID", 0);
                this.tenant = dataMethods.getCachedTenantByTenantID(tenant.getId());
                //fillTextViews();
                TenantListFragment.tenantListAdapterNeedsRefreshed = true;
               // Fragment currentFragment = getSupportFragmentManager().findFragmentByTag("Tenant Info");


                List<Fragment> fragments = getSupportFragmentManager().getFragments();
                if (fragments != null) {
                    for (Fragment fragment : fragments) {
                        if (fragment != null) {
                            android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                            fragmentTransaction.detach(fragment);
                            fragmentTransaction.attach(fragment);
                            fragmentTransaction.commit();
                        }
                    }
                }
            }
        }
        //if (requestCode == MainActivity.REQUEST_NEW_LEASE_FORM) {
        //    if (resultCode == RESULT_OK) {
        //        //int apartmentID = data.getIntExtra("updatedApartmentID", 0);
        //        int tenantID = this.tenant.getId();
        //        this.currentLease = dataMethods.getCachedActiveLeaseByTenantID(tenantID);
        //        if(currentLease != null) {
        //            this.apartment = dataMethods.getCachedApartmentByApartmentID(currentLease.getApartmentID());
        //            Pair<Tenant, ArrayList<Tenant>> tenants = dataMethods.getCachedSelectedTenantAndRoomMatesByLease(currentLease, tenantID);
        //            this.tenant = tenants.first;
        //            this.otherTenants = tenants.second;
         //       } else {
        //            this.apartment = null;
        //            this.tenant = dataMethods.getCachedTenantByTenantID(tenantID);
        //            this.otherTenants = null;
        //        }
        //    }
           // fillTextViews();
         //   TenantListFragment.tenantListAdapterNeedsRefreshed = true;
        //}
    }

    // Adapter for the viewpager using FragmentPagerAdapter
    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    public void showDeleteConfirmationAlertDialog() {
        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //builder.setTitle("AlertDialog");
        builder.setMessage("Are you sure you want to remove this tenant?");

        // add the buttons
        builder.setPositiveButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                databaseHandler.setTenantInactive(tenant);
                //MainActivity.tenantList.remove(tenant);
                if(currentLease != null) {
                    if (tenant.getId() == currentLease.getPrimaryTenantID()) {
                        //     tenant.setIsPrimary(false);
                        //     for (int x = 0; x < otherTenants.size(); x++) {
                        //         otherTenants.get(x).setApartmentID(0);
                        //         otherTenants.get(x).setLeaseStart(null);
                        //         otherTenants.get(x).setLeaseEnd(null);
                        //         databaseHandler.editTenant(otherTenants.get(x));
                        //     }
                        apartment.setRented(false);
                        ApartmentListFragment.apartmentListAdapterNeedsRefreshed = true;
                        //MainActivity.tenantList = databaseHandler.getUsersTenantsIncludingInactive(MainActivity.user);
                    }
                }
                tenant.setActive(false);
                //TODO
                dataMethods.sortMainApartmentArray();
                //MainActivity5.apartmentList = databaseHandler.getUsersApartments(MainActivity5.user);
                TenantListFragment.tenantListAdapterNeedsRefreshed = true;

                TenantViewActivity2.this.finish();
            }
        });

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

}


