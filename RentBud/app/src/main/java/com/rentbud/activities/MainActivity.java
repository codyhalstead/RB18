package com.rentbud.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.android.wizardpager.MainActivity5;
import com.example.cody.rentbud.R;
import com.rentbud.fragments.CalendarFragment;
import com.rentbud.fragments.ExpenseListFragment;
import com.rentbud.fragments.HomeFragment;
import com.rentbud.fragments.ApartmentListFragment;
import com.rentbud.fragments.IncomeListFragment;
import com.rentbud.fragments.LeaseListFragment;
import com.rentbud.fragments.TenantListFragment;
import com.rentbud.helpers.MainArrayDataMethods;
import com.rentbud.model.Apartment;
import com.rentbud.model.Lease;
import com.rentbud.model.Tenant;
import com.rentbud.model.User;
import com.rentbud.sqlite.DatabaseHandler;

import java.util.ArrayList;
import java.util.TreeMap;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {
    //Request codes
    public static final int REQUEST_SIGNIN = 77;
    public static final int REQUEST_GALLERY_FOR_MAIN_PIC = 20;
    public static final int REQUEST_GALLERY_FOR_OTHER_PICS = 21;
    public static final int REQUEST_NEW_APARTMENT_FORM = 36;
    public static final int REQUEST_NEW_TENANT_FORM = 37;
    public static final int REQUEST_NEW_LEASE_FORM = 38;
    public static final int REQUEST_NEW_EXPENSE_FORM = 39;
    public static final int REQUEST_NEW_INCOME_FORM = 40;
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
    public Uri profilePic;
    //initialized with cacheUserDB()
    public static TreeMap<String, Integer> stateMap;
    public static ArrayList<Tenant> tenantList;
    public static ArrayList<Apartment> apartmentList;
    public static ArrayList<Lease> currentLeasesList;
    //public static ArrayList<ExpenseLogEntry> expenseList;
    //public static ArrayList<PaymentLogEntry> incomeList;
    public static TreeMap<String, Integer> expenseTypeLabels;
    public static TreeMap<String, Integer> incomeTypeLabels;
    public static TreeMap<String, Integer> eventTypeLabels;

    int testTenants = 0;
    int testApartments = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        initializeVariables();
        super.setupUserAppTheme(curThemeChoice);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpToolbar();
        setUpDrawer();
        setUpNavView();
        //If user is empty (Last user logged out or fist time loading app), begin log in activity
        if (userIsEmpty()) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivityForResult(intent, REQUEST_SIGNIN);
            //User is still logged in, get full user data
        } else {
            setUpUser();
            //Keeps track if on home fragment (For back button modifier)
            if (savedInstanceState != null) {
                this.isHomeFragDisplayed = savedInstanceState.getBoolean("isHome");
                this.isExpenseFragDisplayed = savedInstanceState.getBoolean("isExpense");
                this.isLeaseFragDisplayed = savedInstanceState.getBoolean("isLease");
            } else {
                //Display home if initial load and user is logged in
                displaySelectedScreen(R.id.nav_home);
            }

            //Easy data loading for testing
            //dbHandler.addTestData(user);

            //Cache users data into arrayLists
            cacheUsersDB();
            //TODO change caching so it doesn't re-cache on phone flip
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //If theme choice has changed, reload for new theme
        if (preferences.getInt(MainActivity.user.getEmail(), 0) != curThemeChoice) {
            this.recreate();
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
                cacheUsersDB();
                //Replace current frag with home frag
                navigationView.getMenu().getItem(0).setChecked(true);
                android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                displaySelectedScreen(R.id.nav_home);
                ft.commit();
            }
        }
        //Get picture from gallery result
        //TODO unfinished
        if (requestCode == REQUEST_GALLERY_FOR_MAIN_PIC) {
            //If picture selection  successfully completed
            if (resultCode == RESULT_OK) {
                // profilePic = data.getData();
                //If picture not null
                if (data.getData() != null) {
                    //Save pic to database and refresh fragment to display new profile pic
                    user.setProfilePic(data.getData().toString());
                    dbHandler.changeProfilePic(user, user.getProfilePic());
                    refreshFragView();
                }
            }
        }
        //NewApartmentFormActivity result
        if (requestCode == REQUEST_NEW_APARTMENT_FORM) {
            //If successful(not cancelled, passed validation)
            if (resultCode == RESULT_OK) {
                //Re-query cached apartment array to update cache and refresh current fragment to display new data
                dataMethods.sortMainApartmentArray();
                refreshFragView();
            }
        }
        //NewTenantFormActivity result
        if (requestCode == REQUEST_NEW_TENANT_FORM) {
            //If successful(not cancelled, passed validation)
            if (resultCode == RESULT_OK) {
                //Re-query cached tenant array to update cache and refresh current fragment to display new data
                dataMethods.sortMainTenantArray();
                refreshFragView();
            }
        }
        if (requestCode == REQUEST_NEW_EXPENSE_FORM) {
            //If successful(not cancelled, passed validation)
            if (resultCode == RESULT_OK) {
                //Re-query cached tenant array to update cache and refresh current fragment to display new data
                //dataMethods.sortMainExpenseArray();
                //refreshFragView();
            }
        }
        if (requestCode == REQUEST_NEW_INCOME_FORM) {
            //If successful(not cancelled, passed validation)
            if (resultCode == RESULT_OK) {
                //Re-query cached tenant array to update cache and refresh current fragment to display new data
                //dataMethods.sortMainIncomeArray();
                //refreshFragView();
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
        MainActivity.apartmentList.clear();
        MainActivity.tenantList.clear();
        MainActivity.currentLeasesList.clear();
        //MainActivity5.incomeList.clear();
        //MainActivity5.expenseList.clear();
        //TODO maybe not clear stateMap
        MainActivity.stateMap.clear();
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
                startActivity(intent);
                return true;

            case R.id.changeProfilePic:
                //Change profile picture, handles changing users profile picture
                //TODO unfinished
                Intent intent2;
                //Launch gallery for result, so user can select a pic
                if (Build.VERSION.SDK_INT < 19) {
                    intent2 = new Intent();
                    intent2.setAction(Intent.ACTION_GET_CONTENT);
                    intent2.setType("*/*");
                    startActivityForResult(intent2, REQUEST_GALLERY_FOR_MAIN_PIC);
                } else {
                    intent2 = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    intent2.addCategory(Intent.CATEGORY_OPENABLE);
                    intent2.setType("*/*");
                    startActivityForResult(intent2, REQUEST_GALLERY_FOR_MAIN_PIC);
                }
                //  intent2.setType("image/*");
                //  startActivityForResult(intent2, REQUEST_GALLERY);
                return true;

            case R.id.verifyEmail:
                //Handles Email verification
                //TODO not started
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
                isHomeFragDisplayed = false;
                break;

            case R.id.nav_apartment:
                //Apartment list fragment
                fragment = new ApartmentListFragment();
                isHomeFragDisplayed = false;
                break;

            case R.id.nav_tenant:
                //Tenant list fragment
                fragment = new TenantListFragment();
                isHomeFragDisplayed = false;
                break;

            case R.id.nav_income:
                fragment = new IncomeListFragment();
                isHomeFragDisplayed = false;
                break;

            case R.id.nav_expenses:
                fragment = new ExpenseListFragment();
                isHomeFragDisplayed = false;
                isExpenseFragDisplayed = true;
                break;

            case R.id.nav_lease:
                fragment = new LeaseListFragment();
                isHomeFragDisplayed = false;
                isLeaseFragDisplayed = true;

            case R.id.nav_totals:

                isHomeFragDisplayed = false;
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
        } else if(isLeaseFragDisplayed) {
           // showNewOrOldLeaseAlertDialog(view);
            Intent intent = new Intent(MainActivity.this, NewLeaseWizard.class);
            startActivityForResult(intent, MainActivity.REQUEST_NEW_LEASE_FORM);
        }else {
            Intent intent = new Intent(this, NewIncomeWizard.class);
            startActivityForResult(intent, REQUEST_NEW_INCOME_FORM);
        }
    }

    private void showNewOrOldLeaseAlertDialog(View view) {
        // setup the alert builder
        //AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //builder.setTitle("AlertDialog");
        //builder.setMessage("Is this lease current, or completed and for record keeping?");

        // add the buttons
       // builder.setPositiveButton("Current", new DialogInterface.OnClickListener() {
       //     @Override
       //     public void onClick(DialogInterface dialogInterface, int i) {
                //Intent intent = new Intent(MainActivity.this, NewLeaseFormActivity.class);
                //Uses filtered results to match what is on screen
                //intent.putExtra("isLeaseForHistory", false);
                //startActivityForResult(intent, MainActivity.REQUEST_NEW_LEASE_FORM);
                Intent intent = new Intent(MainActivity.this, NewLeaseWizard.class);
                startActivityForResult(intent, MainActivity.REQUEST_NEW_LEASE_FORM);
       //     }
       // });
       // builder.setNegativeButton("Completed", new DialogInterface.OnClickListener() {
       //     @Override
       //     public void onClick(DialogInterface dialogInterface, int i) {
       //         Intent intent = new Intent(MainActivity.this, NewLeaseFormActivity.class);
                //Uses filtered results to match what is on screen
       //         intent.putExtra("isLeaseForHistory", true);
       //         startActivityForResult(intent, MainActivity.REQUEST_NEW_LEASE_FORM);
       //     }
       // });

        // create and show the alert dialog
       // AlertDialog dialog = builder.create();
       // dialog.show();
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
        //Set up MainActivity5 toolbar
        setupBasicToolbar();
        if (Build.VERSION.SDK_INT > 15) {
            //Set toolbar color to match users theme
            //SDK_INT 15 will keep bar black
            getToolbar().setBackground(new ColorDrawable(fetchPrimaryColor()));
        }
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
    }

    //Checks if no user currently logged in
    private Boolean userIsEmpty() {
        return (MainActivity.user.getEmail().equals(""));
    }

    private void setUpUser() {
        //Replaces partial user information with full user information, and sets profilePic variable. Used in onCreate
        MainActivity.user = dbHandler.getUser(MainActivity.user.getEmail(), MainActivity.user.getPassword());
        if (MainActivity.user.getProfilePic() != null) {
            profilePic = Uri.parse(MainActivity.user.getProfilePic());
        }
    }

    private void cacheUsersDB() {
        //Querys database and caches users data into array lists
        MainActivity.stateMap = dbHandler.getStateTreemap();
        MainActivity.expenseTypeLabels = dbHandler.getExpenseTypeLabels();
        MainActivity.incomeTypeLabels = dbHandler.getIncomeTypeLabels();
        MainActivity.eventTypeLabels = dbHandler.getEventTypeLabels();
        MainActivity.tenantList = dbHandler.getUsersTenants(MainActivity.user);
        MainActivity.apartmentList = dbHandler.getUsersApartments(MainActivity.user);
        MainActivity.currentLeasesList = dbHandler.getUsersActiveLeases(MainActivity.user);
        //Date endDate = Calendar.getInstance().getTime();
        //Calendar calendar = Calendar.getInstance();
        //calendar.setTime(endDate);
        //calendar.add(Calendar.YEAR, -1);
        //Date startDate = calendar.getTime();
        //MainActivity5.expenseList = dbHandler.getUsersExpensesWithinDates(MainActivity5.user, startDate, endDate);
        //MainActivity5.incomeList = dbHandler.getUsersIncomeWithinDates(MainActivity5.user, startDate, endDate);
    }

    public static void updateApartmentList(ArrayList<Apartment> apartmentList) {
        MainActivity.apartmentList.clear();
        for (int i = 0; i < apartmentList.size(); i++) {
            MainActivity.apartmentList.add(apartmentList.get(i));
        }
    }

    public void add100Tenants(View view) {
        int i = 0;
        while (i < 100) {
            Tenant tenant = new Tenant(-1, "Frank", "Lascelles " + testTenants, "563-598-8965", "snappydude@hotmail.com", "Matt",
                    "Thurston", "568-785-8956",  false, "Is frank " + testTenants);
            dbHandler.addNewTenant(tenant, user.getId());
            testTenants++;
            i++;
        }
    }

    public void add100Apartments(View view) {
        int i = 0;
        while (i < 100) {
            Apartment apartment = new Apartment(0, "2366 Lange Ave", "Apt." + testApartments, "Atalissa", 1, "AL",
                    "53654", "2 bed 1 bath", false, "Big ol building", null, null);
            dbHandler.addNewApartment(apartment, user.getId());
            testApartments++;
            i++;
        }
    }

    //public void add100Expenses(View view) {
    // int i = 0;
    //  while(i < 100){
    //  Apartment apartment = new ExpenseLogEntry(0, "", 154.54, 0, "description", 1, "");
    //  dbHandler.addNewApartment(apartment, user.getId());
    //  testApartments++;
    //  i++;
    // }
    //}

}
