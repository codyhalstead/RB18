package com.rentbud.activities;

import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.cody.rentbud.R;
import com.rentbud.fragments.CalendarFragment;
import com.rentbud.fragments.HomeFragment;
import com.rentbud.fragments.RentalListFragment;
import com.rentbud.fragments.RenterListFragment;
import com.rentbud.model.User;
import com.rentbud.sqlite.DatabaseHandler;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {
    public static final int REQUEST_SIGNIN = 77;
    public static DatabaseHandler dbHandler;
    SharedPreferences preferences;
    private User user;
    public int fragCode;
    Fragment fragment;
    NavigationView navigationView;
    int curThemeChoice;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String name = preferences.getString("last_user_name", "");
        String email = preferences.getString("last_user_email", "");
        String password = preferences.getString("last_user_password", "");
        this.user = new User(name, email, password);
        curThemeChoice = preferences.getInt(email, 0);
        super.setupUserAppTheme(curThemeChoice);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        if (Build.VERSION.SDK_INT > 15) {
            toolbar.setBackground(new ColorDrawable(fetchPrimaryColor()));
        }
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.account_creation_success, R.string.account_creation_failed);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(0).setChecked(true);

        dbHandler = new DatabaseHandler(this);

        if (user.getEmail().equals("")) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivityForResult(intent, REQUEST_SIGNIN);

        } else {
            if (savedInstanceState != null) {
                if (savedInstanceState.getInt("fragCode") == 1) {
                    fragCode = 1;
                    displaySelectedScreen(R.id.nav_calendar);
                } else if (savedInstanceState.getInt("fragCode") == 2) {
                    fragCode = 2;
                    displaySelectedScreen(R.id.nav_rental);
                } else if (savedInstanceState.getInt("fragCode") == 3) {
                    fragCode = 3;
                    displaySelectedScreen(R.id.nav_renter);
                } else {
                    fragCode = 0;
                    displaySelectedScreen(R.id.nav_home);
                }

            } else {
                fragCode = 0;
                displaySelectedScreen(R.id.nav_home);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        String name = preferences.getString("last_user_name", "");
        String email = preferences.getString("last_user_email", "");
        String password = preferences.getString("last_user_password", "");
        this.user = new User(name, email, password);
        if(preferences.getInt(user.getEmail(), 0) != curThemeChoice){
            this.recreate();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SIGNIN) {
            if (resultCode == RESULT_OK) {
                this.user = (User) data.getExtras().get("newUserInfo");

                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("last_user_name", user.getName());
                editor.putString("last_user_email", user.getEmail());
                editor.putString("last_user_password", user.getPassword());
                editor.commit();
                this.recreate();
                android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                displaySelectedScreen(R.id.nav_home);
                ft.commit();
            }
        }
    }

    public void logout() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("last_user_name", "");
        editor.putString("last_user_email", "");
        editor.putString("last_user_password", "");
        editor.apply();
        fragCode = 0;
        user = null;
        navigationView.getMenu().getItem(0).setChecked(true);
        Intent intent = new Intent(this, LoginActivity.class);
        startActivityForResult(intent, REQUEST_SIGNIN);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // handle button activities (collapse  button)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.moreOptions:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;

            case R.id.changeProfilePic:

                return true;

            case R.id.changeAppColors:

                return true;

            case R.id.verifyEmail:

                return true;

            case R.id.logout:
                logout();
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    private void displaySelectedScreen(int id) {
        fragment = null;
        switch (id) {

            case R.id.nav_home:
                fragment = new HomeFragment();
                Bundle bundle = new Bundle();
                bundle.putParcelable("UserInfo", user);
                fragment.setArguments(bundle);
                fragCode = 0;
                break;


            case R.id.nav_calendar:
                fragment = new CalendarFragment();
                fragCode = 1;
                overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
                break;

            case R.id.nav_rental:
                fragment = new RentalListFragment();
                fragCode = 2;
                break;

            case R.id.nav_renter:
                fragment = new RenterListFragment();
                fragCode = 3;
                break;

        }
        if (fragment != null) {
            android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.screen_area, fragment);
            ft.commit();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        displaySelectedScreen(id);
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //Save the fragment's instance
        outState.putInt("fragCode", fragCode);
        super.onSaveInstanceState(outState);
    }
}
