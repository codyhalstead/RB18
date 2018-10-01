package com.rentbud.activities;

import android.app.AlertDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.cody.rentbud.BuildConfig;
import com.example.cody.rentbud.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.rentbud.fragments.CalendarFragment;
import com.rentbud.fragments.ExpenseListFragment;
import com.rentbud.fragments.HomeFragment;
import com.rentbud.fragments.ApartmentListFragment;
import com.rentbud.fragments.IncomeListFragment;
import com.rentbud.fragments.LeaseListFragment;
import com.rentbud.fragments.TenantListFragment;
import com.rentbud.fragments.TotalsFragment;
import com.rentbud.helpers.MainArrayDataMethods;
import com.rentbud.helpers.MainViewModel;
import com.rentbud.model.Apartment;
import com.rentbud.model.Lease;
import com.rentbud.model.Tenant;
import com.rentbud.model.User;
import com.rentbud.sqlite.DatabaseHandler;

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

    public static final int RESULT_DATA_WAS_MODIFIED = 80;
    //Fragment tag
    public static final String CURRENT_FRAG_TAG = "current_frag_tag";
    //initialized with initializeVariables()
    public DatabaseHandler dbHandler;
    SharedPreferences preferences;
    public static int curThemeChoice;
    public static User user;
    Boolean isHomeFragDisplayed;
    Boolean isExpenseFragDisplayed;
    Boolean isLeaseFragDisplayed;
    MainArrayDataMethods dataMethods;
    //initialized with setUpDrawer
    DrawerLayout drawer;
    //initialized with setUpNavView()
    NavigationView navigationView;
    //initialized with setUpUser()
    //initialized with cacheUserDB()
    //public static TreeMap<String, Integer> stateMap;
    public static ArrayList<Tenant> tenantList;
    public static ArrayList<Apartment> apartmentList;
    public static ArrayList<Lease> currentLeasesList;
    private AlertDialog alertDialog;
    //public static ArrayList<ExpenseLogEntry> expenseList;
    //public static ArrayList<PaymentLogEntry> incomeList;
    //public static ArrayList<TypeTotal> expenseTypes;
    //public static ArrayList<TypeTotal> incomeTypes;
    public static TreeMap<String, Integer> expenseTypeLabels;
    public static TreeMap<String, Integer> incomeTypeLabels;
    //public static TreeMap<String, Integer> eventTypeLabels;
    //public Date filterDateStart, filterDateEnd;
    public MainViewModel viewModel;
    private InterstitialAd mInterstitialAd;

    int testTenants = 0;
    int testApartments = 0;
    int screenChanges = 0;
    int adFrequency = 4;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        //MobileAds.initialize(this, "ca-app-pub-3940256099942544/6300978111");
        initializeVariables();
        super.setupUserAppTheme(curThemeChoice);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_nav_drawer);
        setUpToolbar();
        setUpDrawer();
        setUpNavView();
        if (BuildConfig.FLAVOR.equals("free")) {
            prepareAd();
        }
        //Log.d("TAG", "onCreate: !!!!!!!!!!!!!" + stringFromJNI());
        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        //If user is empty (Last user logged out or fist time loading app), begin log in activity
        if (userIsEmpty()) {
            Intent intent = new Intent(this, LoginActivity.class);
            initializeCachedData();
            startActivityForResult(intent, REQUEST_SIGNIN);
            //User is still logged in, get full user data
        } else {
            setUpUser();
            //Keeps track if on home fragment (For back button modifier)
            if (savedInstanceState != null) {
                this.isHomeFragDisplayed = savedInstanceState.getBoolean("isHome");
                this.isExpenseFragDisplayed = savedInstanceState.getBoolean("isExpense");
                this.isLeaseFragDisplayed = savedInstanceState.getBoolean("isLease");
                this.screenChanges = savedInstanceState.getInt("screenChanges");
            } else {
                //Display home if initial load and user is logged in
                displaySelectedScreen(R.id.nav_home);
            }
            //Easy data loading for testing
            //dbHandler.addTestData(user);
            //Cache users data into arrayLists
            initializeCachedData();
            //deleteDir(Environment.getExternalStorageDirectory());
            //File f = new File(Environment.getExternalStorageDirectory(), "Rentbud");
            //if (!f.exists()) {
            //    f.mkdirs(); q
            //}
        }
    }

    //public native String stringFromJNI();

    @Override
    protected void onResume() {
        super.onResume();
        //If theme choice has changed, reload for new theme
        if (MainActivity.user != null) {
            if (preferences.getInt(MainActivity.user.getEmail(), 0) != curThemeChoice) {
                this.recreate();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (alertDialog != null) {
            alertDialog.dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            //If drawer is open, pressing back will close it
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (isHomeFragDisplayed) {
                //If on home fragment, app will minimize(super)
                super.onBackPressed();
            } else {
                //If not on home fragment, will go to home fragment
                displaySelectedScreen(R.id.nav_home);
                navigationView.setCheckedItem(R.id.nav_home);
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
                //Log-in user data passed to MainActivity5
                MainActivity.user = (User) data.getExtras().get("newUserInfo");
                //Save user info to shared preferences to stay logged in until user manually logs out
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("last_user_name", user.getName());
                editor.putString("last_user_email", user.getEmail());
                editor.putString("last_user_password", user.getPassword());
                editor.commit();
                //Cache newly logged users data into arrayLists
                //stateMap = dbHandler.getStateTreemap();
                cacheDataForNewUser();
                //Replace current frag with home frag
                navigationView.getMenu().getItem(0).setChecked(true);
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
                MainActivity.apartmentList = dbHandler.getUsersApartmentsIncludingInactive(MainActivity.user);
                //refreshFragView();
            }
        }
        //NewTenantFormActivity result
        if (requestCode == REQUEST_NEW_TENANT_FORM) {
            //If successful(not cancelled, passed validation)
            if (resultCode == RESULT_OK) {
                //Re-query cached tenant array to update cache and refresh current fragment to display new data
                MainActivity.tenantList = dbHandler.getUsersTenantsIncludingInactive(MainActivity.user);
                //refreshFragView();
            }
        }
        if (requestCode == REQUEST_NEW_EXPENSE_FORM) {
            //If successful(not cancelled, passed validation)
            if (resultCode == RESULT_OK) {
                //Re-query cached tenant array to update cache and refresh current fragment to display new data
                viewModel.setCachedExpenses(dbHandler.getUsersExpensesWithinDates(MainActivity.user, viewModel.getStartDateRangeDate().getValue(), viewModel.getEndDateRangeDate().getValue()));
                //dataMethods.sortMainExpenseArray();
                //refreshFragView();
            }
        }
        if (requestCode == REQUEST_NEW_INCOME_FORM) {
            //If successful(not cancelled, passed validation)
            if (resultCode == RESULT_OK) {
                viewModel.setCachedIncome(dbHandler.getUsersIncomeWithinDates(MainActivity.user, viewModel.getStartDateRangeDate().getValue(), viewModel.getEndDateRangeDate().getValue()));
                //Re-query cached tenant array to update cache and refresh current fragment to display new data
                //dataMethods.sortMainIncomeArray();
                //refreshFragView();
            }
        }
        if (requestCode == REQUEST_NEW_LEASE_FORM) {
            //If successful(not cancelled, passed validation)
            if (resultCode == RESULT_OK) {
                viewModel.setCachedLeases(dbHandler.getUsersActiveLeasesWithinDates(MainActivity.user, viewModel.getStartDateRangeDate().getValue(), viewModel.getEndDateRangeDate().getValue()));
                viewModel.setCachedIncome(dbHandler.getUsersIncomeWithinDates(MainActivity.user, viewModel.getStartDateRangeDate().getValue(), viewModel.getEndDateRangeDate().getValue()));
                viewModel.setCachedExpenses(dbHandler.getUsersExpensesWithinDates(MainActivity.user, viewModel.getStartDateRangeDate().getValue(), viewModel.getEndDateRangeDate().getValue()));
                MainActivity.tenantList = dbHandler.getUsersTenantsIncludingInactive(MainActivity.user);
                MainActivity.apartmentList = dbHandler.getUsersApartmentsIncludingInactive(MainActivity.user);
                //Re-query cached tenant array to update cache and refresh current fragment to display new data
                //dataMethods.sortMainIncomeArray();
                //refreshFragView();
            }
        }
        if (requestCode == REQUEST_INCOME_VIEW) {
            if (resultCode == RESULT_DATA_WAS_MODIFIED) {
                viewModel.setCachedIncome(dbHandler.getUsersIncomeWithinDates(MainActivity.user, viewModel.getStartDateRangeDate().getValue(), viewModel.getEndDateRangeDate().getValue()));
            }
        }
        if (requestCode == REQUEST_EXPENSE_VIEW) {
            if (resultCode == RESULT_DATA_WAS_MODIFIED) {
                viewModel.setCachedExpenses(dbHandler.getUsersExpensesWithinDates(MainActivity.user, viewModel.getStartDateRangeDate().getValue(), viewModel.getEndDateRangeDate().getValue()));
            }
        }
        if (requestCode == REQUEST_LEASE_VIEW) {
            if (resultCode == RESULT_DATA_WAS_MODIFIED) {
                if (data.getExtras().getBoolean("was_lease_edited")) {
                    viewModel.setCachedLeases(dbHandler.getUsersActiveLeasesWithinDates(MainActivity.user, viewModel.getStartDateRangeDate().getValue(), viewModel.getEndDateRangeDate().getValue()));
                    MainActivity.tenantList = dbHandler.getUsersTenantsIncludingInactive(MainActivity.user);
                    MainActivity.apartmentList = dbHandler.getUsersApartmentsIncludingInactive(MainActivity.user);
                }
                if (data.getExtras().getBoolean("was_income_edited")) {
                    viewModel.setCachedIncome(dbHandler.getUsersIncomeWithinDates(MainActivity.user, viewModel.getStartDateRangeDate().getValue(), viewModel.getEndDateRangeDate().getValue()));
                }
                if (data.getExtras().getBoolean("was_expense_edited")) {
                    viewModel.setCachedExpenses(dbHandler.getUsersExpensesWithinDates(MainActivity.user, viewModel.getStartDateRangeDate().getValue(), viewModel.getEndDateRangeDate().getValue()));
                }
            }
        }
        if (requestCode == REQUEST_TENANT_VIEW) {
            if (resultCode == RESULT_DATA_WAS_MODIFIED) {
                boolean wasTenantListRefreshed = false;
                if (data.getExtras().getBoolean("was_lease_edited")) {
                    viewModel.setCachedLeases(dbHandler.getUsersActiveLeasesWithinDates(MainActivity.user, viewModel.getStartDateRangeDate().getValue(), viewModel.getEndDateRangeDate().getValue()));
                    MainActivity.apartmentList = dbHandler.getUsersApartmentsIncludingInactive(MainActivity.user);
                    MainActivity.tenantList = dbHandler.getUsersTenantsIncludingInactive(MainActivity.user);
                    wasTenantListRefreshed = true;
                }
                if (data.getExtras().getBoolean("was_income_edited")) {
                    viewModel.setCachedIncome(dbHandler.getUsersIncomeWithinDates(MainActivity.user, viewModel.getStartDateRangeDate().getValue(), viewModel.getEndDateRangeDate().getValue()));
                }
                if (data.getExtras().getBoolean("was_expense_edited")) {
                    viewModel.setCachedExpenses(dbHandler.getUsersExpensesWithinDates(MainActivity.user, viewModel.getStartDateRangeDate().getValue(), viewModel.getEndDateRangeDate().getValue()));
                }
                if (data.getExtras().getBoolean("was_tenant_edited")) {
                    if (!wasTenantListRefreshed) {
                        MainActivity.tenantList = dbHandler.getUsersTenantsIncludingInactive(MainActivity.user);
                    }
                }
            }
        }
        if (requestCode == REQUEST_APARTMENT_VIEW) {
            if (resultCode == RESULT_DATA_WAS_MODIFIED) {
                boolean wasApartmentListRefreshed = false;
                if (data.getExtras().getBoolean("was_lease_edited")) {
                    viewModel.setCachedLeases(dbHandler.getUsersActiveLeasesWithinDates(MainActivity.user, viewModel.getStartDateRangeDate().getValue(), viewModel.getEndDateRangeDate().getValue()));
                    MainActivity.apartmentList = dbHandler.getUsersApartmentsIncludingInactive(MainActivity.user);
                    MainActivity.tenantList = dbHandler.getUsersTenantsIncludingInactive(MainActivity.user);
                    wasApartmentListRefreshed = true;
                }
                if (data.getExtras().getBoolean("was_income_edited")) {
                    viewModel.setCachedIncome(dbHandler.getUsersIncomeWithinDates(MainActivity.user, viewModel.getStartDateRangeDate().getValue(), viewModel.getEndDateRangeDate().getValue()));
                }
                if (data.getExtras().getBoolean("was_expense_edited")) {
                    viewModel.setCachedExpenses(dbHandler.getUsersExpensesWithinDates(MainActivity.user, viewModel.getStartDateRangeDate().getValue(), viewModel.getEndDateRangeDate().getValue()));
                }
                if (data.getExtras().getBoolean("was_apartment_edited")) {
                    if (!wasApartmentListRefreshed) {
                        MainActivity.apartmentList = dbHandler.getUsersApartmentsIncludingInactive(MainActivity.user);
                    }
                }
            }
        }
        if (requestCode == REQUEST_CALENDAR_VIEW) {
            if (resultCode == RESULT_DATA_WAS_MODIFIED) {
                if (data.getExtras().getBoolean("was_lease_edited")) {
                    viewModel.setCachedLeases(dbHandler.getUsersActiveLeasesWithinDates(MainActivity.user, viewModel.getStartDateRangeDate().getValue(), viewModel.getEndDateRangeDate().getValue()));
                    MainActivity.apartmentList = dbHandler.getUsersApartmentsIncludingInactive(MainActivity.user);
                    MainActivity.tenantList = dbHandler.getUsersTenantsIncludingInactive(MainActivity.user);
                }
                if (data.getExtras().getBoolean("was_income_edited")) {
                    viewModel.setCachedIncome(dbHandler.getUsersIncomeWithinDates(MainActivity.user, viewModel.getStartDateRangeDate().getValue(), viewModel.getEndDateRangeDate().getValue()));
                }
                if (data.getExtras().getBoolean("was_expense_edited")) {
                    viewModel.setCachedExpenses(dbHandler.getUsersExpensesWithinDates(MainActivity.user, viewModel.getStartDateRangeDate().getValue(), viewModel.getEndDateRangeDate().getValue()));
                }
            }
        }
        if (requestCode == REQUEST_EMAIL) {

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
        //Clear cached user information and reset shared preference data so user won't still be considered logged in on log out
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("last_user_name", "");
        editor.putString("last_user_email", "");
        editor.putString("last_user_password", "");
        editor.apply();
        MainActivity.user = null;
        MainActivity.apartmentList = null;
        MainActivity.tenantList = null;
        MainActivity.currentLeasesList = null;
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
                //Log out option, logs user out of app
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
        isExpenseFragDisplayed = false;
        isLeaseFragDisplayed = false;
        isHomeFragDisplayed = false;
        switch (id) {

            case R.id.nav_home:
                //Home fragment
                fragment = new HomeFragment();
                fragment.setArguments(bundle);
                isHomeFragDisplayed = true;
                break;

            case R.id.nav_calendar:
                //Calendar fragment
                fragment = new CalendarFragment();
                //Intent intent = new Intent(this, CaldroidTestFrag.class);
                //startActivity(intent);
                //fragment = new CaldroidTestFrag();
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
                isExpenseFragDisplayed = true;
                break;

            case R.id.nav_lease:
                fragment = new LeaseListFragment();
                isLeaseFragDisplayed = true;
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
        //Close nav drawer on selection
        this.drawer.closeDrawer(GravityCompat.START);
        handleAds();
    }

    private void handleAds() {
        if (BuildConfig.FLAVOR.equals("free")) {
            screenChanges++;
            if (screenChanges >= adFrequency) {
                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                    screenChanges = 0;
                    prepareAd();
                }
            }
        }
    }

    private void prepareAd() {
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        //Handles navigation view item clicks
        int id = item.getItemId();
        displaySelectedScreen(id);
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //Save the fragment's instance
        super.onSaveInstanceState(outState);
        outState.putBoolean("isHome", isHomeFragDisplayed);
        outState.putBoolean("isExpense", isExpenseFragDisplayed);
        outState.putBoolean("isLease", isLeaseFragDisplayed);
        outState.putInt("screenChanges", screenChanges);
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
        if (isExpenseFragDisplayed) {
            Intent intent = new Intent(this, NewExpenseWizard.class);
            startActivityForResult(intent, REQUEST_NEW_EXPENSE_FORM);
        } else if (isLeaseFragDisplayed) {
            // showNewOrOldLeaseAlertDialog(view);
            Intent intent = new Intent(MainActivity.this, NewLeaseWizard.class);
            startActivityForResult(intent, MainActivity.REQUEST_NEW_LEASE_FORM);
        } else {
            Intent intent = new Intent(this, NewIncomeWizard.class);
            startActivityForResult(intent, REQUEST_NEW_INCOME_FORM);
        }
    }

    private void refreshFragView() {
        //Refreshes current frag by detaching then re-attaching
        Fragment frg = getSupportFragmentManager().findFragmentByTag(CURRENT_FRAG_TAG);
        final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.detach(frg);
        ft.attach(frg);
        ft.commit();
    }

    private void initializeVariables() {
        //Initialises variables, used in onCreate
        this.preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String name = preferences.getString("last_user_name", "");
        String email = preferences.getString("last_user_email", "");
        String password = preferences.getString("last_user_password", "");
        MainActivity.user = new User(name, email, password);
        MainActivity.curThemeChoice = preferences.getInt(email, 0);
        this.dbHandler = new DatabaseHandler(this);
        this.isHomeFragDisplayed = true;
        this.isExpenseFragDisplayed = false;
        this.isLeaseFragDisplayed = false;
        this.dataMethods = new MainArrayDataMethods();
    }

    private void setUpToolbar() {
        //Set up MainActivity toolbar
        setupBasicToolbar();
        getToolbar().setBackground(new ColorDrawable(fetchPrimaryColor()));
    }

    private void setUpDrawer() {
        //Set up drawer, used in onCreate
        this.drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, this.drawer, getToolbar(), R.string.account_creation_success, R.string.account_creation_failed);
        //TODO change drawer description                ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void setUpNavView() {
        //Set up Nav View, used in onCreate
        this.navigationView = findViewById(R.id.nav_view);
        this.navigationView.setNavigationItemSelectedListener(this);
        this.navigationView.getMenu().getItem(0).setChecked(true);
        View headerView = navigationView.getHeaderView(0);
        TextView navHeader = headerView.findViewById(R.id.mainHeader);
        if (BuildConfig.FLAVOR.equals("pro")) {
            navHeader.setText(R.string.rentbud_pro);
        } else {
            navHeader.setText(R.string.rentbud);
        }
    }

    //Checks if no user currently logged in
    private Boolean userIsEmpty() {
        return (MainActivity.user.getEmail().equals(""));
    }

    private void setUpUser() {
        //Replaces partial user information with full user information, and sets profilePic variable. Used in onCreate
        MainActivity.user = dbHandler.getUser(MainActivity.user.getEmail(), MainActivity.user.getPassword());
    }

    private void initializeCachedData() {
        //Querys database and caches users data into array lists
        if (MainActivity.expenseTypeLabels == null) {
            MainActivity.expenseTypeLabels = dbHandler.getExpenseTypeLabelsTreeMap();
        }
        if (MainActivity.incomeTypeLabels == null) {
            MainActivity.incomeTypeLabels = dbHandler.getIncomeTypeLabelsTreeMap();
        }
        if (MainActivity.user != null) {
            if (MainActivity.tenantList == null) {
                MainActivity.tenantList = dbHandler.getUsersTenantsIncludingInactive(MainActivity.user);
            }
            if (MainActivity.apartmentList == null) {
                MainActivity.apartmentList = dbHandler.getUsersApartmentsIncludingInactive(MainActivity.user);
            }
            if (MainActivity.currentLeasesList == null) {
                MainActivity.currentLeasesList = dbHandler.getUsersActiveLeases(MainActivity.user);
            }
        }
        if (viewModel.getCachedApartments() == null) {
            viewModel.init();
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
            viewModel.setStartDateRange(startDate);
            viewModel.setEndDateRange(endDate);
            if (MainActivity.user != null) {
                viewModel.setCachedExpenses(dbHandler.getUsersExpensesWithinDates(MainActivity.user, startDate, endDate));
                viewModel.setCachedIncome(dbHandler.getUsersIncomeWithinDates(MainActivity.user, startDate, endDate));
                viewModel.setCachedLeases(dbHandler.getUsersActiveLeasesWithinDates(MainActivity.user, startDate, endDate));
            }
            viewModel.setHomeTabYearSelected(selectedYear);
        }
    }

    private void cacheDataForNewUser() {
        if (MainActivity.expenseTypeLabels == null) {
            MainActivity.expenseTypeLabels = dbHandler.getExpenseTypeLabelsTreeMap();
        }
        if (MainActivity.incomeTypeLabels == null) {
            MainActivity.incomeTypeLabels = dbHandler.getIncomeTypeLabelsTreeMap();
        }
        if (MainActivity.user != null) {
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
            if (viewModel.getCachedApartments() == null) {
                viewModel.init();
            }
            viewModel.setStartDateRange(startDate);
            viewModel.setEndDateRange(endDate);
            viewModel.setStartDateRange(startDate);
            viewModel.setEndDateRange(endDate);
            viewModel.setCachedExpenses(dbHandler.getUsersExpensesWithinDates(MainActivity.user, startDate, endDate));
            viewModel.setCachedIncome(dbHandler.getUsersIncomeWithinDates(MainActivity.user, startDate, endDate));
            viewModel.setCachedLeases(dbHandler.getUsersActiveLeasesWithinDates(MainActivity.user, startDate, endDate));
            viewModel.setHomeTabYearSelected(selectedYear);
            MainActivity.tenantList = dbHandler.getUsersTenantsIncludingInactive(MainActivity.user);
            MainActivity.apartmentList = dbHandler.getUsersApartmentsIncludingInactive(MainActivity.user);
            MainActivity.currentLeasesList = dbHandler.getUsersActiveLeases(MainActivity.user);
        }
    }

    public void add100Tenants(View view) {
        int i = 0;
        while (i < 100) {
            Tenant tenant = new Tenant(-1, "Frank", "Lascelles " + testTenants, "563-598-8965", "snappydude@hotmail.com", "Matt",
                    "Thurston", "568-785-8956", false, "Is frank " + testTenants, true);
            dbHandler.addNewTenant(tenant, user.getId());
            testTenants++;
            i++;
        }
    }

    public void add100Apartments(View view) {
        int i = 0;
        while (i < 100) {
            Apartment apartment = new Apartment(0, "2366 Lange Ave", "Apt." + testApartments, "Atalissa", "AL",
                    "53654", "2 bed 1 bath", false, "Big ol building", null, null, true);
            dbHandler.addNewApartment(apartment, user.getId());
            testApartments++;
            i++;
        }
    }

    @Override
    public void onLeaseListDatesChanged(Date dateStart, Date dateEnd, LeaseListFragment fragment) {
        viewModel.setStartDateRange(dateStart);
        viewModel.setEndDateRange(dateEnd);
        viewModel.setCachedLeases(dbHandler.getUsersActiveLeasesWithinDates(MainActivity.user, dateStart, dateEnd));
        viewModel.setCachedIncome(dbHandler.getUsersIncomeWithinDates(MainActivity.user, dateStart, dateEnd));
        viewModel.setCachedExpenses(dbHandler.getUsersExpensesWithinDates(MainActivity.user, dateStart, dateEnd));
        fragment.updateData(viewModel.getCachedLeases().getValue());
    }

    @Override
    public void onExpenseListDatesChanged(Date dateStart, Date dateEnd, ExpenseListFragment fragment) {
        viewModel.setStartDateRange(dateStart);
        viewModel.setEndDateRange(dateEnd);
        viewModel.setCachedLeases(dbHandler.getUsersActiveLeasesWithinDates(MainActivity.user, dateStart, dateEnd));
        viewModel.setCachedIncome(dbHandler.getUsersIncomeWithinDates(MainActivity.user, dateStart, dateEnd));
        viewModel.setCachedExpenses(dbHandler.getUsersExpensesWithinDates(MainActivity.user, dateStart, dateEnd));
        fragment.updateData(viewModel.getCachedExpenses().getValue());
    }

    @Override
    public void onIncomeListDatesChanged(Date dateStart, Date dateEnd, IncomeListFragment fragment) {
        viewModel.setStartDateRange(dateStart);
        viewModel.setEndDateRange(dateEnd);
        viewModel.setCachedLeases(dbHandler.getUsersActiveLeasesWithinDates(MainActivity.user, dateStart, dateEnd));
        viewModel.setCachedIncome(dbHandler.getUsersIncomeWithinDates(MainActivity.user, dateStart, dateEnd));
        viewModel.setCachedExpenses(dbHandler.getUsersExpensesWithinDates(MainActivity.user, dateStart, dateEnd));
        fragment.updateData(viewModel.getCachedIncome().getValue());
    }

    @Override
    public void onTotalsListDatesChanged(Date dateStart, Date dateEnd, TotalsFragment fragment) {
        viewModel.setStartDateRange(dateStart);
        viewModel.setEndDateRange(dateEnd);
        viewModel.setCachedLeases(dbHandler.getUsersActiveLeasesWithinDates(MainActivity.user, dateStart, dateEnd));
        viewModel.setCachedIncome(dbHandler.getUsersIncomeWithinDates(MainActivity.user, dateStart, dateEnd));
        viewModel.setCachedExpenses(dbHandler.getUsersExpensesWithinDates(MainActivity.user, dateStart, dateEnd));
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
