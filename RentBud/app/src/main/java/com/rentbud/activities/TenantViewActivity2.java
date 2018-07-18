package com.rentbud.activities;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;

import com.example.cody.rentbud.R;
import com.rentbud.fragments.ApartmentListFragment;
import com.rentbud.fragments.ApartmentViewFrag1;
import com.rentbud.fragments.ApartmentViewFrag2;
import com.rentbud.fragments.ApartmentViewFrag3;
import com.rentbud.fragments.TenantListFragment;
import com.rentbud.fragments.TenantViewFrag1;
import com.rentbud.fragments.TenantViewFrag2;
import com.rentbud.fragments.TenantViewFrag3;
import com.rentbud.helpers.ApartmentTenantViewModel;
import com.rentbud.helpers.MainArrayDataMethods;
import com.rentbud.model.Apartment;
import com.rentbud.model.Lease;
import com.rentbud.model.Tenant;
import com.rentbud.sqlite.DatabaseHandler;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TenantViewActivity2 extends BaseActivity implements View.OnClickListener, TenantViewFrag2.OnMoneyDataChangedListener,
        TenantViewFrag3.OnLeaseDataChangedListener {
    private DatabaseHandler databaseHandler;
    private MainArrayDataMethods dataMethods;
    private ArrayList<Tenant> otherTenants;
    private Lease currentLease;
    private Tenant tenant;
    private Apartment apartment;
    ViewPager.OnPageChangeListener mPageChangeListener;
    ViewPager viewPager;
    TenantViewActivity2.ViewPagerAdapter adapter;
    LinearLayout dateSelectorLL;
    Date filterDateStart, filterDateEnd;
    private DatePickerDialog.OnDateSetListener dateSetFilterStartListener, dateSetFilterEndListener;
    Button dateRangeStartBtn, dateRangeEndBtn;
    private TenantViewFrag1 frag1;
    private TenantViewFrag2 frag2;
    private TenantViewFrag3 frag3;
    private ApartmentTenantViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupUserAppTheme(MainActivity.curThemeChoice);
        setContentView(R.layout.activity_lease_view_actual);
        dateSelectorLL = findViewById(R.id.moneyDateSelecterLL);
        dateSelectorLL.setVisibility(View.GONE);
        this.dateRangeStartBtn = findViewById(R.id.moneyListDateRangeStartBtn);
        this.dateRangeStartBtn.setOnClickListener(this);
        this.dateRangeEndBtn = findViewById(R.id.moneyListDateRangeEndBtn);
        this.dateRangeEndBtn.setOnClickListener(this);
        viewPager = (ViewPager) findViewById(R.id.pager);
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        databaseHandler = new DatabaseHandler(this);
        dataMethods = new MainArrayDataMethods();
        viewModel = ViewModelProviders.of(this).get(ApartmentTenantViewModel.class);
        viewModel.init();
        // Add Fragments to adapter one by one
        Bundle bundle = getIntent().getExtras();
        int tenantID = bundle.getInt("tenantID");
        tenant = databaseHandler.getTenantByID(tenantID, MainActivity.user);
        viewModel.setViewedTenant(tenant);
        currentLease = dataMethods.getCachedActiveLeaseByTenantID(tenant.getId());

        ArrayList<Tenant> secondaryTenants = new ArrayList<>();
        if (currentLease != null) {
            Tenant primaryTenant = databaseHandler.getTenantByID(currentLease.getPrimaryTenantID(), MainActivity.user);
            viewModel.setPrimaryTenant(primaryTenant);
            apartment = databaseHandler.getApartmentByID(currentLease.getApartmentID(), MainActivity.user);
            viewModel.setApartment(apartment);
            ArrayList<Integer> secondaryTenantIDs = currentLease.getSecondaryTenantIDs();
            for (int i = 0; i < secondaryTenantIDs.size(); i++) {
                Tenant secondaryTenant = databaseHandler.getTenantByID(secondaryTenantIDs.get(i), MainActivity.user);
                secondaryTenants.add(secondaryTenant);
            }
        }
        viewModel.setLease(currentLease);
        //viewModel.setPrimaryTenant(primaryTenant);
        viewModel.setSecondaryTenants(secondaryTenants);

        //bundle.putParcelable("tenant", tenant);
        currentLease = dataMethods.getCachedActiveLeaseByTenantID(tenantID);
        //   int apartmentID = dataMethods.getCachedApartmentByApartmentID(currentLease.getApartmentID());
       // if (!tenant.getHasLease()) {
       //     this.tenant = dataMethods.getCachedTenantByTenantID(tenantID);
       //     this.apartment = null;
       // } else {
       //     if (currentLease != null) {
       //         Pair<Tenant, ArrayList<Tenant>> tenants = dataMethods.getCachedSelectedTenantAndRoomMatesByLease(currentLease, tenantID);
       //         this.tenant = tenants.first;
       //         this.otherTenants = tenants.second;
       //         this.apartment = dataMethods.getCachedApartmentByApartmentID(currentLease.getApartmentID());
       //     } else {
       //         this.tenant = dataMethods.getCachedTenantByTenantID(tenantID);
       //         this.apartment = null;
       //     }
        //}
        //int leaseID = bundle.getInt("leaseID");

        if (savedInstanceState != null) {
            if (savedInstanceState.getString("filterDateStart") != null) {
                SimpleDateFormat formatTo = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
                DateFormat formatFrom = new SimpleDateFormat("yyyy/MM/dd", Locale.US);
                try {
                    Date startDate = formatFrom.parse(savedInstanceState.getString("filterDateStart"));
                    this.filterDateStart = startDate;
                    this.dateRangeStartBtn.setText(formatTo.format(startDate));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            if (savedInstanceState.getString("filterDateEnd") != null) {
                SimpleDateFormat formatTo = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
                DateFormat formatFrom = new SimpleDateFormat("yyyy/MM/dd", Locale.US);
                try {
                    Date endDate = formatFrom.parse(savedInstanceState.getString("filterDateEnd"));
                    this.filterDateEnd = endDate;
                    this.dateRangeEndBtn.setText(formatTo.format(endDate));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        } else {
            Date endDate = Calendar.getInstance().getTime();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(endDate);
            calendar.add(Calendar.YEAR, -1);
            Date startDate = calendar.getTime();
            this.filterDateEnd = endDate;
            this.filterDateStart = startDate;
        }
        // adapter.addFragment(new FragmentThree(), "FRAG3");
        viewPager.setAdapter(adapter);
        viewModel.setMoneyArray(databaseHandler.getIncomeAndExpensesByTenantIDWithinDates(MainActivity.user, tenant.getId(), filterDateStart, filterDateEnd));
        viewModel.setLeaseArray(databaseHandler.getUsersLeasesForTenant(MainActivity.user, tenant.getId()));


        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
        dateRangeStartBtn.setText(formatter.format(filterDateStart));
        dateRangeEndBtn.setText(formatter.format(filterDateEnd));

        mPageChangeListener = new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrollStateChanged(int arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onPageSelected(int pos) {
                if (pos == 0 || pos == 2) {
                    dateSelectorLL.setVisibility(View.GONE);
                } else {
                    dateSelectorLL.setVisibility(View.VISIBLE);
                }
            }

        };
        viewPager.addOnPageChangeListener(mPageChangeListener);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setSelectedTabIndicatorColor(Color.parseColor("#000000"));
        tabLayout.setTabTextColors(Color.parseColor("#ffffff"), Color.parseColor("#4d4c4b"));
        tabLayout.setupWithViewPager(viewPager);
        setupBasicToolbar();
        setUpdateSelectedDateListeners();
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
                this.tenant = databaseHandler.getTenantByID(tenant.getId(), MainActivity.user);
                viewModel.setViewedTenant(tenant);
                //fillTextViews();
                //TenantListFragment.tenantListAdapterNeedsRefreshed = true;
                // Fragment currentFragment = getSupportFragmentManager().findFragmentByTag("Tenant Info");
                List<Fragment> fragments = getSupportFragmentManager().getFragments();
                if (fragments != null) {
                    for (Fragment fragment : fragments) {
                        if (fragment != null) {
                            fragment.onActivityResult(requestCode, resultCode, data);
                        }
                    }
                }
            }
        } else {
            List<Fragment> fragments = getSupportFragmentManager().getFragments();
            if (fragments != null) {
                for (Fragment fragment : fragments) {
                    if (fragment != null) {
                        fragment.onActivityResult(requestCode, resultCode, data);
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

    private void setUpdateSelectedDateListeners() {
        dateSetFilterStartListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                //Once user selects date from date picker pop-up,
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.YEAR, year);
                cal.set(Calendar.MONTH, month);
                cal.set(Calendar.DATE, day);
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
                filterDateStart = cal.getTime();
                //currentFilteredExpenses = db.getUsersExpensesWithinDates(MainActivity.user, filterDateStart, filterDateEnd);
                //if(currentFilteredExpenses.isEmpty()){
                //    noExpensesTV.setVisibility(View.VISIBLE);
                //    noExpensesTV.setText("No Current Expenses");
                //} else {
                //    noExpensesTV.setVisibility(View.GONE);
                //    noExpensesTV.setText("No Current Expenses");
                //}
                SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
                dateRangeStartBtn.setText(formatter.format(filterDateStart));

                updateFragmentDates();
                //expenseListAdapter.updateResults(currentFilteredExpenses);
                //expenseListAdapter.getFilter().filter(searchBarET.getText());
            }
        };
        dateSetFilterEndListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                //Once user selects date from date picker pop-up,
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.YEAR, year);
                cal.set(Calendar.MONTH, month);
                cal.set(Calendar.DATE, day);
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
                filterDateEnd = cal.getTime();
                //currentFilteredExpenses = db.getUsersExpensesWithinDates(MainActivity.user, filterDateStart, filterDateEnd);
                //if(currentFilteredExpenses.isEmpty()){
                //    noExpensesTV.setVisibility(View.VISIBLE);
                //    noExpensesTV.setText("No Current Expenses");
                //} else {
                //    noExpensesTV.setVisibility(View.GONE);
                //    noExpensesTV.setText("No Current Expenses");
                //}
                SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
                dateRangeEndBtn.setText(formatter.format(filterDateEnd));

                updateFragmentDates();
                //expenseListAdapter.notifyDataSetChanged();
                //expenseListAdapter.updateResults(currentFilteredExpenses);
                //expenseListAdapter.getFilter().filter(searchBarET.getText());
            }
        };
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd", Locale.US);
        if (filterDateStart != null) {
            outState.putString("filterDateStart", formatter.format(filterDateStart));
        }
        if (filterDateEnd != null) {
            outState.putString("filterDateEnd", formatter.format(filterDateEnd));
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.moneyListDateRangeStartBtn:
                Calendar cal = Calendar.getInstance();
                if (filterDateStart != null) {
                    cal.setTime(filterDateStart);
                }
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dialog = new DatePickerDialog(this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, dateSetFilterStartListener, year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
                break;

            case R.id.moneyListDateRangeEndBtn:
                Calendar cal2 = Calendar.getInstance();
                if (filterDateEnd != null) {
                    cal2.setTime(filterDateEnd);
                }
                int year2 = cal2.get(Calendar.YEAR);
                int month2 = cal2.get(Calendar.MONTH);
                int day2 = cal2.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dialog2 = new DatePickerDialog(this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, dateSetFilterEndListener, year2, month2, day2);
                dialog2.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog2.show();
                break;

            default:
                break;
        }
    }

    private void updateFragmentDates() {
        if (frag2 != null) {
            frag2.updateData();
        }
        if (frag3 != null) {
            frag3.updateData();
        }
    }

    @Override
    public void onMoneyDataChanged() {
        viewModel.setMoneyArray(databaseHandler.getIncomeAndExpensesByTenantIDWithinDates(MainActivity.user, tenant.getId(), filterDateStart, filterDateEnd));
        frag2.updateData();
    }

    @Override
    public void onLeaseDataChanged() {
        this.tenant = databaseHandler.getTenantByID(tenant.getId(), MainActivity.user);
        viewModel.setViewedTenant(tenant);
        //viewModel.setLease(null); //TODO update lease
        viewModel.setLeaseArray(databaseHandler.getUsersLeasesForTenant(MainActivity.user, tenant.getId()));
        viewModel.setMoneyArray(databaseHandler.getIncomeAndExpensesByTenantIDWithinDates(MainActivity.user, tenant.getId(), filterDateStart, filterDateEnd));
        frag2.updateData();
        frag3.updateData();
    }


    // Adapter for the viewpager using FragmentPagerAdapter
    class ViewPagerAdapter extends FragmentStatePagerAdapter {

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            Bundle bundle = new Bundle();
            bundle.putParcelable("tenant", tenant);
            switch (position) {
                case 0:
                    TenantViewFrag1 frg1 = new TenantViewFrag1();
                    frg1.setArguments(bundle);
                    return frg1;
                case 1:
                    TenantViewFrag2 frg2 = new TenantViewFrag2();
                    frg2.setArguments(bundle);
                    return frg2;
                case 2:
                    TenantViewFrag3 frg3 = new TenantViewFrag3();
                    frg3.setArguments(bundle);
                    return frg3;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment createdFragment = (Fragment) super.instantiateItem(container, position);
            // save the appropriate reference depending on position
            switch (position) {
                case 0:
                    frag1 = (TenantViewFrag1) createdFragment;
                    break;
                case 1:
                    frag2 = (TenantViewFrag2) createdFragment;
                    break;
                case 2:
                    frag3 = (TenantViewFrag3) createdFragment;
                    break;
            }
            return createdFragment;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Info";
                case 1:
                    return "Payments";
                case 2:
                    return "History";
            }
            return "";
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
                if (currentLease != null) {
                    if (tenant.getId() == currentLease.getPrimaryTenantID()) {
                        //     tenant.setIsPrimary(false);
                        //     for (int x = 0; x < otherTenants.size(); x++) {
                        //         otherTenants.get(x).setApartmentID(0);
                        //         otherTenants.get(x).setLeaseStart(null);
                        //         otherTenants.get(x).setLeaseEnd(null);
                        //         databaseHandler.editTenant(otherTenants.get(x));
                        //     }
                        //apartment.setRented(false);
                        //ApartmentListFragment.apartmentListAdapterNeedsRefreshed = true;
                        //MainActivity.tenantList = databaseHandler.getUsersTenantsIncludingInactive(MainActivity.user);
                    }
                }
                tenant.setActive(false);
                //TODO
                dataMethods.sortMainApartmentArray();
                //MainActivity5.apartmentList = databaseHandler.getUsersApartments(MainActivity5.user);
                //TenantListFragment.tenantListAdapterNeedsRefreshed = true;

                TenantViewActivity2.this.finish();
            }
        });

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

}


