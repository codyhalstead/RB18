package com.rba18.activities;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.ads.MobileAds;
import com.rba18.BuildConfig;
import com.rba18.R;
import com.google.android.gms.ads.InterstitialAd;
import com.rba18.fragments.CalendarFragment;
import com.rba18.fragments.ExpenseListFragment;
import com.rba18.fragments.HomeFragment;
import com.rba18.fragments.ApartmentListFragment;
import com.rba18.fragments.IncomeListFragment;
import com.rba18.fragments.LeaseListFragment;
import com.rba18.fragments.TenantListFragment;
import com.rba18.fragments.TotalsFragment;
import com.rba18.helpers.MainViewModel;
import com.rba18.model.Apartment;
import com.rba18.model.Lease;
import com.rba18.model.Tenant;
import com.rba18.model.User;
import com.rba18.sqlite.DatabaseHandler;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TreeMap;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener,
        LeaseListFragment.OnDatesChangedListener,
        ExpenseListFragment.OnDatesChangedListener,
        IncomeListFragment.OnDatesChangedListener,
        TotalsFragment.OnDatesChangedListener {
    //Request codes
    public static final int REQUEST_SIGNIN = 77;
    public static final int REQUEST_GALLERY_FOR_MAIN_PIC = 20;
    public static final int REQUEST_GALLERY_FOR_OTHER_PICS = 21;
    public static final int REQUEST_NEW_APARTMENT_FORM = 36;
    public static final int REQUEST_NEW_TENANT_FORM = 37;
    public static final int REQUEST_NEW_LEASE_FORM = 38;
    public static final int REQUEST_NEW_EXPENSE_FORM = 39;
    public static final int REQUEST_NEW_INCOME_FORM = 40;
    public static final int REQUEST_LEASE_VIEW = 41;
    public static final int REQUEST_APARTMENT_VIEW = 42;
    public static final int REQUEST_TENANT_VIEW = 43;
    public static final int REQUEST_INCOME_VIEW = 44;
    public static final int REQUEST_EXPENSE_VIEW = 45;
    public static final int REQUEST_CALENDAR_VIEW = 46;
    public static final int REQUEST_EMAIL = 47;
    public static final int REQUEST_SETTINGS = 48;
    public static final int REQUEST_IMAGE_DELETE_PERMISSION = 49;
    public static final int REQUEST_ADAPTER_IMAGE_DELETE_PERMISSION = 50;
    public static final int REQUEST_FILE_PERMISSION = 51;
    public static final int REQUEST_PHONE_CALL_PERMISSION = 52;
    public static final int REQUEST_CAMERA_FOR_MAIN_PIC = 53;
    public static final int REQUEST_CAMERA_FOR_OTHER_PICS = 54;
    public static final int REQUEST_GOOGLE_SIGN_IN = 55;

    public static final int RESULT_DATA_WAS_MODIFIED = 80;
    //Fragment tag
    public static final String CURRENT_FRAG_TAG = "current_frag_tag";
    //initialized with initializeVariables()
    private DatabaseHandler mDBHandler;
    public static int sCurThemeChoice;
    public static User sUser;
    private Boolean mIsHomeFragDisplayed;
    private Boolean mIsExpenseFragDisplayed;
    private Boolean mIsLeaseFragDisplayed;
    //initialized with setUpDrawer
    private DrawerLayout mDrawer;
    //initialized with setUpNavView()z
    private NavigationView mNavigationView;
    //initialized with setUpUser()
    //initialized with cacheUserDB()
    public static ArrayList<Tenant> sTenantList;
    public static ArrayList<Apartment> sApartmentList;
    public static ArrayList<Lease> sCurrentLeasesList;
    public static TreeMap<String, Integer> sExpenseTypeLabels;
    public static TreeMap<String, Integer> sIncomeTypeLabels;
    private MainViewModel mViewModel;
    private InterstitialAd mInterstitialAd;

    private int mScreenChanges = 0;
    private int mAdFrequency = 6;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        initializeVariables();
        super.setupUserAppTheme(sCurThemeChoice);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main_nav_drawer);
        setUpToolbar();
        setUpDrawer();
        setUpNavView();
        if (BuildConfig.FLAVOR.equals("free")) {
            //TODO enable for release
            //MobileAds.initialize(this, ""); //TODO insert ID
            //prepareAd();
        }
        mViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        //If sUser is empty (Last sUser logged out or fist time loading app), begin log in activity
        if (userIsEmpty()) {
            Intent intent = new Intent(this, LoginActivity.class);
            initializeCachedData();
            startActivityForResult(intent, REQUEST_SIGNIN);
            //User is still logged in, get full sUser data
        } else {
            setUpUser();
            //Keeps track if on home fragment (For back button modifier)
            if (savedInstanceState != null) {
                mIsHomeFragDisplayed = savedInstanceState.getBoolean("isHome");
                mIsExpenseFragDisplayed = savedInstanceState.getBoolean("isExpense");
                mIsLeaseFragDisplayed = savedInstanceState.getBoolean("isLease");
                mScreenChanges = savedInstanceState.getInt("mScreenChanges");
            } else {
                //Display home if initial load and sUser is logged in
                displaySelectedScreen(R.id.nav_home);
            }
            //Cache users data into arrayLists
            initializeCachedData();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //If theme choice has changed, reload for new theme
        if (MainActivity.sUser != null) {
            if (preferences.getInt(MainActivity.sUser.getEmail(), 0) != sCurThemeChoice) {
                this.recreate();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            //If mDrawer is open, pressing back will close it
            mDrawer.closeDrawer(GravityCompat.START);
        } else {
            if (mIsHomeFragDisplayed) {
                //If on home fragment, app will minimize(super)
                super.onBackPressed();
            } else {
                //If not on home fragment, will go to home fragment
                displaySelectedScreen(R.id.nav_home);
                mNavigationView.setCheckedItem(R.id.nav_home);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //LoginActivity result
        if (requestCode == REQUEST_SIGNIN) {
            //If log-in successful
            if (resultCode == RESULT_OK) {
                //Log-in sUser data passed to MainActivity5
                MainActivity.sUser = (User) data.getExtras().get("newUserInfo");
                //Save sUser info to shared preferences to stay logged in until sUser manually logs out
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("last_user_name", sUser.getName());
                editor.putString("last_user_email", sUser.getEmail());
                editor.putString("last_user_password", sUser.getPassword());
                editor.commit();
                //Cache newly logged users data into arrayLists
                cacheDataForNewUser();
                //Replace current frag with home frag
                mNavigationView.getMenu().getItem(0).setChecked(true);
                android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                displaySelectedScreen(R.id.nav_home);
                ft.commit();
            }
        }
        //NewApartmentFormActivity result
        if (requestCode == REQUEST_NEW_APARTMENT_FORM) {
            //If successful(not cancelled, passed validation)
            if (resultCode == RESULT_OK) {
                //Re-query cached apartment array to update cache and refresh current fragment to display new data
                MainActivity.sApartmentList = mDBHandler.getUsersApartmentsIncludingInactive(MainActivity.sUser);
            }
        }
        //NewTenantFormActivity result
        if (requestCode == REQUEST_NEW_TENANT_FORM) {
            //If successful(not cancelled, passed validation)
            if (resultCode == RESULT_OK) {
                //Re-query cached tenant array to update cache and refresh current fragment to display new data
                MainActivity.sTenantList = mDBHandler.getUsersTenantsIncludingInactive(MainActivity.sUser);
            }
        }
        if (requestCode == REQUEST_NEW_EXPENSE_FORM) {
            //If successful(not cancelled, passed validation)
            if (resultCode == RESULT_OK) {
                //Re-query cached tenant array to update cache and refresh current fragment to display new data
                mViewModel.setCachedExpenses(mDBHandler.getUsersExpensesWithinDates(MainActivity.sUser, mViewModel.getStartDateRangeDate().getValue(), mViewModel.getEndDateRangeDate().getValue()));
            }
        }
        if (requestCode == REQUEST_NEW_INCOME_FORM) {
            //If successful(not cancelled, passed validation)
            if (resultCode == RESULT_OK) {
                mViewModel.setCachedIncome(mDBHandler.getUsersIncomeWithinDates(MainActivity.sUser, mViewModel.getStartDateRangeDate().getValue(), mViewModel.getEndDateRangeDate().getValue()));
                //Re-query cached tenant array to update cache and refresh current fragment to display new data
            }
        }
        if (requestCode == REQUEST_NEW_LEASE_FORM) {
            //If successful(not cancelled, passed validation)
            if (resultCode == RESULT_OK) {
                mViewModel.setCachedLeases(mDBHandler.getUsersActiveLeasesWithinDates(MainActivity.sUser, mViewModel.getStartDateRangeDate().getValue(), mViewModel.getEndDateRangeDate().getValue()));
                mViewModel.setCachedIncome(mDBHandler.getUsersIncomeWithinDates(MainActivity.sUser, mViewModel.getStartDateRangeDate().getValue(), mViewModel.getEndDateRangeDate().getValue()));
                mViewModel.setCachedExpenses(mDBHandler.getUsersExpensesWithinDates(MainActivity.sUser, mViewModel.getStartDateRangeDate().getValue(), mViewModel.getEndDateRangeDate().getValue()));
                MainActivity.sTenantList = mDBHandler.getUsersTenantsIncludingInactive(MainActivity.sUser);
                MainActivity.sApartmentList = mDBHandler.getUsersApartmentsIncludingInactive(MainActivity.sUser);
                //Re-query cached tenant array to update cache and refresh current fragment to display new data
            }
        }
        if (requestCode == REQUEST_INCOME_VIEW) {
            if (resultCode == RESULT_DATA_WAS_MODIFIED) {
                mViewModel.setCachedIncome(mDBHandler.getUsersIncomeWithinDates(MainActivity.sUser, mViewModel.getStartDateRangeDate().getValue(), mViewModel.getEndDateRangeDate().getValue()));
            }
        }
        if (requestCode == REQUEST_EXPENSE_VIEW) {
            if (resultCode == RESULT_DATA_WAS_MODIFIED) {
                mViewModel.setCachedExpenses(mDBHandler.getUsersExpensesWithinDates(MainActivity.sUser, mViewModel.getStartDateRangeDate().getValue(), mViewModel.getEndDateRangeDate().getValue()));
            }
        }
        if (requestCode == REQUEST_LEASE_VIEW) {
            if (resultCode == RESULT_DATA_WAS_MODIFIED) {
                if (data.getExtras().getBoolean("was_lease_edited")) {
                    mViewModel.setCachedLeases(mDBHandler.getUsersActiveLeasesWithinDates(MainActivity.sUser, mViewModel.getStartDateRangeDate().getValue(), mViewModel.getEndDateRangeDate().getValue()));
                    MainActivity.sTenantList = mDBHandler.getUsersTenantsIncludingInactive(MainActivity.sUser);
                    MainActivity.sApartmentList = mDBHandler.getUsersApartmentsIncludingInactive(MainActivity.sUser);
                }
                if (data.getExtras().getBoolean("was_income_edited")) {
                    mViewModel.setCachedIncome(mDBHandler.getUsersIncomeWithinDates(MainActivity.sUser, mViewModel.getStartDateRangeDate().getValue(), mViewModel.getEndDateRangeDate().getValue()));
                }
                if (data.getExtras().getBoolean("was_expense_edited")) {
                    mViewModel.setCachedExpenses(mDBHandler.getUsersExpensesWithinDates(MainActivity.sUser, mViewModel.getStartDateRangeDate().getValue(), mViewModel.getEndDateRangeDate().getValue()));
                }
            }
        }
        if (requestCode == REQUEST_TENANT_VIEW) {
            if (resultCode == RESULT_DATA_WAS_MODIFIED) {
                boolean wasTenantListRefreshed = false;
                if (data.getExtras().getBoolean("was_lease_edited")) {
                    mViewModel.setCachedLeases(mDBHandler.getUsersActiveLeasesWithinDates(MainActivity.sUser, mViewModel.getStartDateRangeDate().getValue(), mViewModel.getEndDateRangeDate().getValue()));
                    MainActivity.sApartmentList = mDBHandler.getUsersApartmentsIncludingInactive(MainActivity.sUser);
                    MainActivity.sTenantList = mDBHandler.getUsersTenantsIncludingInactive(MainActivity.sUser);
                    wasTenantListRefreshed = true;
                }
                if (data.getExtras().getBoolean("was_income_edited")) {
                    mViewModel.setCachedIncome(mDBHandler.getUsersIncomeWithinDates(MainActivity.sUser, mViewModel.getStartDateRangeDate().getValue(), mViewModel.getEndDateRangeDate().getValue()));
                }
                if (data.getExtras().getBoolean("was_expense_edited")) {
                    mViewModel.setCachedExpenses(mDBHandler.getUsersExpensesWithinDates(MainActivity.sUser, mViewModel.getStartDateRangeDate().getValue(), mViewModel.getEndDateRangeDate().getValue()));
                }
                if (data.getExtras().getBoolean("was_tenant_edited")) {
                    if (!wasTenantListRefreshed) {
                        MainActivity.sTenantList = mDBHandler.getUsersTenantsIncludingInactive(MainActivity.sUser);
                    }
                }
            }
        }
        if (requestCode == REQUEST_APARTMENT_VIEW) {
            if (resultCode == RESULT_DATA_WAS_MODIFIED) {
                boolean wasApartmentListRefreshed = false;
                if (data.getExtras().getBoolean("was_lease_edited")) {
                    mViewModel.setCachedLeases(mDBHandler.getUsersActiveLeasesWithinDates(MainActivity.sUser, mViewModel.getStartDateRangeDate().getValue(), mViewModel.getEndDateRangeDate().getValue()));
                    MainActivity.sApartmentList = mDBHandler.getUsersApartmentsIncludingInactive(MainActivity.sUser);
                    MainActivity.sTenantList = mDBHandler.getUsersTenantsIncludingInactive(MainActivity.sUser);
                    wasApartmentListRefreshed = true;
                }
                if (data.getExtras().getBoolean("was_income_edited")) {
                    mViewModel.setCachedIncome(mDBHandler.getUsersIncomeWithinDates(MainActivity.sUser, mViewModel.getStartDateRangeDate().getValue(), mViewModel.getEndDateRangeDate().getValue()));
                }
                if (data.getExtras().getBoolean("was_expense_edited")) {
                    mViewModel.setCachedExpenses(mDBHandler.getUsersExpensesWithinDates(MainActivity.sUser, mViewModel.getStartDateRangeDate().getValue(), mViewModel.getEndDateRangeDate().getValue()));
                }
                if (data.getExtras().getBoolean("was_apartment_edited")) {
                    if (!wasApartmentListRefreshed) {
                        MainActivity.sApartmentList = mDBHandler.getUsersApartmentsIncludingInactive(MainActivity.sUser);
                    }
                }
            }
        }
        if (requestCode == REQUEST_CALENDAR_VIEW) {
            if (resultCode == RESULT_DATA_WAS_MODIFIED) {
                if (data.getExtras().getBoolean("was_lease_edited")) {
                    mViewModel.setCachedLeases(mDBHandler.getUsersActiveLeasesWithinDates(MainActivity.sUser, mViewModel.getStartDateRangeDate().getValue(), mViewModel.getEndDateRangeDate().getValue()));
                    MainActivity.sApartmentList = mDBHandler.getUsersApartmentsIncludingInactive(MainActivity.sUser);
                    MainActivity.sTenantList = mDBHandler.getUsersTenantsIncludingInactive(MainActivity.sUser);
                }
                if (data.getExtras().getBoolean("was_income_edited")) {
                    mViewModel.setCachedIncome(mDBHandler.getUsersIncomeWithinDates(MainActivity.sUser, mViewModel.getStartDateRangeDate().getValue(), mViewModel.getEndDateRangeDate().getValue()));
                }
                if (data.getExtras().getBoolean("was_expense_edited")) {
                    mViewModel.setCachedExpenses(mDBHandler.getUsersExpensesWithinDates(MainActivity.sUser, mViewModel.getStartDateRangeDate().getValue(), mViewModel.getEndDateRangeDate().getValue()));
                }
            }
        }
        if (requestCode == REQUEST_SETTINGS) {
            if (resultCode == RESULT_DATA_WAS_MODIFIED) {
                if (data != null) {
                    if (data.getExtras().getBoolean("need_to_log_out")) {
                        logout();
                    }
                } else {
                    displaySelectedScreen(R.id.nav_home);
                }
            }
        }
    }

    public void logout() {
        //Clear cached sUser information and reset shared preference data so sUser won't still be considered logged in on log out
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("last_user_name", "");
        editor.putString("last_user_email", "");
        editor.putString("last_user_password", "");
        editor.apply();
        MainActivity.sUser = null;
        MainActivity.sApartmentList = null;
        MainActivity.sTenantList = null;
        MainActivity.sCurrentLeasesList = null;
        //Launch LoginActivity for result
        Intent intent = new Intent(this, LoginActivity.class);
        startActivityForResult(intent, REQUEST_SIGNIN);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Get options menu
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    //Handle option menu actions
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.moreOptions:
                //More options, launches SettingsActivity
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivityForResult(intent, MainActivity.REQUEST_SETTINGS);
                return true;

            case R.id.logout:
                //Log out option, logs sUser out of app
                logout();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void displaySelectedScreen(int id) {
        //Method to swap display fragments
        Fragment fragment = null;
        Bundle bundle = new Bundle();
        mIsExpenseFragDisplayed = false;
        mIsLeaseFragDisplayed = false;
        mIsHomeFragDisplayed = false;
        switch (id) {

            case R.id.nav_home:
                //Home fragment
                fragment = new HomeFragment();
                fragment.setArguments(bundle);
                mIsHomeFragDisplayed = true;
                break;

            case R.id.nav_calendar:
                //Calendar fragment
                fragment = new CalendarFragment();
                break;

            case R.id.nav_apartment:
                //Apartment list fragment
                fragment = new ApartmentListFragment();
                break;

            case R.id.nav_tenant:
                //Tenant list fragment
                fragment = new TenantListFragment();
                break;

            case R.id.nav_income:
                fragment = new IncomeListFragment();
                break;

            case R.id.nav_expenses:
                fragment = new ExpenseListFragment();
                mIsExpenseFragDisplayed = true;
                break;

            case R.id.nav_lease:
                fragment = new LeaseListFragment();
                mIsLeaseFragDisplayed = true;
                break;

            case R.id.nav_totals:
                fragment = new TotalsFragment();
                break;
        }
        if (fragment != null) {
            //Replace previous frag with selection
            android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.screen_area, fragment, CURRENT_FRAG_TAG);
            ft.commit();

        }
        //Close nav mDrawer on selection
        mDrawer.closeDrawer(GravityCompat.START);
        handleAds();
    }

    private void handleAds() {
        if (BuildConfig.FLAVOR.equals("free")) {
            mScreenChanges++;
            if (mScreenChanges >= mAdFrequency) {
            //    if (mInterstitialAd.isLoaded()) {
            //        mInterstitialAd.show();
            //        mScreenChanges = 0;
                    //TODO enable for release
                    //prepareAd();
            //    }
            }
        }
    }

    //TODO enable for release
    //private void prepareAd(){
    //    mInterstitialAd = new InterstitialAd(this);
    //    mInterstitialAd.setAdUnitId(""); //TODO insert ad ID
    //    mInterstitialAd.loadAd(new AdRequest.Builder().build());
    //}

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        //Handles navigation view item clicks
        int id = item.getItemId();
        displaySelectedScreen(id);
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //Save the fragment's instance
        super.onSaveInstanceState(outState);
        outState.putBoolean("isHome", mIsHomeFragDisplayed);
        outState.putBoolean("isExpense", mIsExpenseFragDisplayed);
        outState.putBoolean("isLease", mIsLeaseFragDisplayed);
        outState.putInt("mScreenChanges", mScreenChanges);
    }

    public void apartmentFABClick(View view) {
        //Launch NewApartmentFormActivity for result
        //onClick set in xml (ApartmentList fragment FAB)
        Intent intent = new Intent(this, NewApartmentWizard.class);
        startActivityForResult(intent, REQUEST_NEW_APARTMENT_FORM);
    }

    public void tenantFABClick(View view) {
        //Launch NewTenantFormActivity for result
        //onClick set in xml (TenantList fragment FAB)
        Intent intent = new Intent(this, NewTenantWizard.class);
        startActivityForResult(intent, REQUEST_NEW_TENANT_FORM);
    }

    public void moneyFABClick(View view) {
        if (mIsExpenseFragDisplayed) {
            Intent intent = new Intent(this, NewExpenseWizard.class);
            startActivityForResult(intent, REQUEST_NEW_EXPENSE_FORM);
        } else if (mIsLeaseFragDisplayed) {
            Intent intent = new Intent(MainActivity.this, NewLeaseWizard.class);
            startActivityForResult(intent, MainActivity.REQUEST_NEW_LEASE_FORM);
        } else {
            Intent intent = new Intent(this, NewIncomeWizard.class);
            startActivityForResult(intent, REQUEST_NEW_INCOME_FORM);
        }
    }

    //private void refreshFragView() {
        //Refreshes current frag by detaching then re-attaching
    //    Fragment frg = getSupportFragmentManager().findFragmentByTag(CURRENT_FRAG_TAG);
    //    final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
    //    ft.detach(frg);
    //    ft.attach(frg);
    //    ft.commit();
    //}

    private void initializeVariables() {
        //Initialises variables, used in onCreate
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String name = preferences.getString("last_user_name", "");
        String email = preferences.getString("last_user_email", "");
        String password = preferences.getString("last_user_password", "");
        Boolean isGoogleAccount = preferences.getBoolean("last_user_is_google", false);
        MainActivity.sUser = new User(name, email, password, isGoogleAccount);
        MainActivity.sCurThemeChoice = preferences.getInt(email, 0);
        mDBHandler = new DatabaseHandler(this);
        mIsHomeFragDisplayed = true;
        mIsExpenseFragDisplayed = false;
        mIsLeaseFragDisplayed = false;
    }

    private void setUpToolbar() {
        //Set up MainActivity toolbar
        setupBasicToolbar();
        getToolbar().setBackground(new ColorDrawable(fetchPrimaryColor()));
    }

    private void setUpDrawer() {
        //Set up mDrawer, used in onCreate
        mDrawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, getToolbar(), R.string.blank, R.string.blank);
        mDrawer.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void setUpNavView() {
        //Set up Nav View, used in onCreate
        mNavigationView = findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);
        mNavigationView.getMenu().getItem(0).setChecked(true);
        View headerView = mNavigationView.getHeaderView(0);
        TextView navUsername = headerView.findViewById(R.id.navHeaderTV);
        if (BuildConfig.FLAVOR.equals("pro")) {
            navUsername.setText(R.string.rentbud_pro);
        } else {
            navUsername.setText(R.string.rentbud);
        }
    }

    //Checks if no sUser currently logged in
    private Boolean userIsEmpty() {
        return (MainActivity.sUser.getEmail().equals(""));
    }

    private void setUpUser() {
        //Replaces partial sUser information with full sUser information, and sets profilePic variable. Used in onCreate
        MainActivity.sUser = mDBHandler.getUser(MainActivity.sUser.getEmail(), MainActivity.sUser.getPassword());
    }

    private void initializeCachedData() {
        //Queries database and caches users data into array lists
        if (MainActivity.sExpenseTypeLabels == null) {
            MainActivity.sExpenseTypeLabels = mDBHandler.getExpenseTypeLabelsTreeMap();
        }
        if (MainActivity.sIncomeTypeLabels == null) {
            MainActivity.sIncomeTypeLabels = mDBHandler.getIncomeTypeLabelsTreeMap();
        }
        if (MainActivity.sUser != null) {
            if (MainActivity.sTenantList == null) {
                MainActivity.sTenantList = mDBHandler.getUsersTenantsIncludingInactive(MainActivity.sUser);
            }
            if (MainActivity.sApartmentList == null) {
                MainActivity.sApartmentList = mDBHandler.getUsersApartmentsIncludingInactive(MainActivity.sUser);
            }
            if (MainActivity.sCurrentLeasesList == null) {
                MainActivity.sCurrentLeasesList = mDBHandler.getUsersActiveLeases(MainActivity.sUser);
            }
        }
        if (mViewModel.getCachedApartments() == null) {
            mViewModel.init();
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            Date selectedYear = calendar.getTime();
            calendar.add(Calendar.MONTH, 1);
            Date endDate = calendar.getTime();
            calendar.add(Calendar.YEAR, -1);
            Date startDate = calendar.getTime();
            mViewModel.setStartDateRange(startDate);
            mViewModel.setEndDateRange(endDate);
            if (MainActivity.sUser != null) {
                mViewModel.setCachedExpenses(mDBHandler.getUsersExpensesWithinDates(MainActivity.sUser, startDate, endDate));
                mViewModel.setCachedIncome(mDBHandler.getUsersIncomeWithinDates(MainActivity.sUser, startDate, endDate));
                mViewModel.setCachedLeases(mDBHandler.getUsersActiveLeasesWithinDates(MainActivity.sUser, startDate, endDate));
            }
            mViewModel.setHomeTabYearSelected(selectedYear);
        }
    }

    private void cacheDataForNewUser() {
        if (MainActivity.sExpenseTypeLabels == null) {
            MainActivity.sExpenseTypeLabels = mDBHandler.getExpenseTypeLabelsTreeMap();
        }
        if (MainActivity.sIncomeTypeLabels == null) {
            MainActivity.sIncomeTypeLabels = mDBHandler.getIncomeTypeLabelsTreeMap();
        }
        if (MainActivity.sUser != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            Date selectedYear = calendar.getTime();
            calendar.add(Calendar.MONTH, 1);
            Date endDate = calendar.getTime();
            calendar.add(Calendar.YEAR, -1);
            Date startDate = calendar.getTime();
            if (mViewModel.getCachedApartments() == null) {
                mViewModel.init();
            }
            mViewModel.setStartDateRange(startDate);
            mViewModel.setEndDateRange(endDate);
            mViewModel.setStartDateRange(startDate);
            mViewModel.setEndDateRange(endDate);
            mViewModel.setCachedExpenses(mDBHandler.getUsersExpensesWithinDates(MainActivity.sUser, startDate, endDate));
            mViewModel.setCachedIncome(mDBHandler.getUsersIncomeWithinDates(MainActivity.sUser, startDate, endDate));
            mViewModel.setCachedLeases(mDBHandler.getUsersActiveLeasesWithinDates(MainActivity.sUser, startDate, endDate));
            mViewModel.setHomeTabYearSelected(selectedYear);
            MainActivity.sTenantList = mDBHandler.getUsersTenantsIncludingInactive(MainActivity.sUser);
            MainActivity.sApartmentList = mDBHandler.getUsersApartmentsIncludingInactive(MainActivity.sUser);
            MainActivity.sCurrentLeasesList = mDBHandler.getUsersActiveLeases(MainActivity.sUser);
        }
    }

    //public void add100Tenants(View view) {
    //    int i = 0;
    //    while (i < 100) {
    //        Tenant tenant = new Tenant(-1, "Frank", "Lascelles " + testTenants, "563-598-8965", "snappydude@hotmail.com", "Matt",
    //                "Thurston", "568-785-8956", false, "Is frank " + testTenants, true);
    //        mDBHandler.addNewTenant(tenant, sUser.getId());
    //        testTenants++;
    //        i++;
    //    }
    //}

    //public void add100Apartments(View view) {
    //    int i = 0;
    //    while (i < 100) {
    //        Apartment apartment = new Apartment(0, "2366 Lange Ave", "Apt." + testApartments, "Atalissa", "AL",
    //                "53654", "2 bed 1 bath", false, "Big ol building", null, null, true);
    //        mDBHandler.addNewApartment(apartment, sUser.getId());
    //        testApartments++;
    //        i++;
    //    }
    //}

    @Override
    public void onLeaseListDatesChanged(Date dateStart, Date dateEnd, LeaseListFragment fragment) {
        mViewModel.setStartDateRange(dateStart);
        mViewModel.setEndDateRange(dateEnd);
        mViewModel.setCachedLeases(mDBHandler.getUsersActiveLeasesWithinDates(MainActivity.sUser, dateStart, dateEnd));
        mViewModel.setCachedIncome(mDBHandler.getUsersIncomeWithinDates(MainActivity.sUser, dateStart, dateEnd));
        mViewModel.setCachedExpenses(mDBHandler.getUsersExpensesWithinDates(MainActivity.sUser, dateStart, dateEnd));
        fragment.updateData(mViewModel.getCachedLeases().getValue());
    }

    @Override
    public void onExpenseListDatesChanged(Date dateStart, Date dateEnd, ExpenseListFragment fragment) {
        mViewModel.setStartDateRange(dateStart);
        mViewModel.setEndDateRange(dateEnd);
        mViewModel.setCachedLeases(mDBHandler.getUsersActiveLeasesWithinDates(MainActivity.sUser, dateStart, dateEnd));
        mViewModel.setCachedIncome(mDBHandler.getUsersIncomeWithinDates(MainActivity.sUser, dateStart, dateEnd));
        mViewModel.setCachedExpenses(mDBHandler.getUsersExpensesWithinDates(MainActivity.sUser, dateStart, dateEnd));
        fragment.updateData(mViewModel.getCachedExpenses().getValue());
    }

    @Override
    public void onIncomeListDatesChanged(Date dateStart, Date dateEnd, IncomeListFragment fragment) {
        mViewModel.setStartDateRange(dateStart);
        mViewModel.setEndDateRange(dateEnd);
        mViewModel.setCachedLeases(mDBHandler.getUsersActiveLeasesWithinDates(MainActivity.sUser, dateStart, dateEnd));
        mViewModel.setCachedIncome(mDBHandler.getUsersIncomeWithinDates(MainActivity.sUser, dateStart, dateEnd));
        mViewModel.setCachedExpenses(mDBHandler.getUsersExpensesWithinDates(MainActivity.sUser, dateStart, dateEnd));
        fragment.updateData(mViewModel.getCachedIncome().getValue());
    }

    @Override
    public void onTotalsListDatesChanged(Date dateStart, Date dateEnd, TotalsFragment fragment) {
        mViewModel.setStartDateRange(dateStart);
        mViewModel.setEndDateRange(dateEnd);
        mViewModel.setCachedLeases(mDBHandler.getUsersActiveLeasesWithinDates(MainActivity.sUser, dateStart, dateEnd));
        mViewModel.setCachedIncome(mDBHandler.getUsersIncomeWithinDates(MainActivity.sUser, dateStart, dateEnd));
        mViewModel.setCachedExpenses(mDBHandler.getUsersExpensesWithinDates(MainActivity.sUser, dateStart, dateEnd));
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
}
