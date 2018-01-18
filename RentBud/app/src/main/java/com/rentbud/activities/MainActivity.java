package com.rentbud.activities;

import android.content.Intent;
import android.content.SharedPreferences;
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

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    public static final int REQUEST_SIGNIN = 77;
    public static DatabaseHandler dbHandler;
    SharedPreferences preferences;
    private User user;
    public int fragCode;
    Fragment fragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.account_creation_success, R.string.account_creation_failed);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(0).setChecked(true);

        dbHandler = new DatabaseHandler(this);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String name = preferences.getString("last_user_name", "");
        String email = preferences.getString("last_user_email", "");
        String password = preferences.getString("last_user_password", "");
        this.user = new User(name, email, password);

        if (user.getEmail().equals("")) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivityForResult(intent, REQUEST_SIGNIN);

        } else {
            if (savedInstanceState != null) {
                if(savedInstanceState.getInt("fragCode") == 1){
                    fragCode = 1;
                    displaySelectedScreen(R.id.nav_calendar);
                }
                else if(savedInstanceState.getInt("fragCode") == 2){
                    fragCode = 2;
                    displaySelectedScreen(R.id.nav_rental);
                }
                else if(savedInstanceState.getInt("fragCode") == 3){
                    fragCode = 3;
                    displaySelectedScreen(R.id.nav_renter);
                }
                else {
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
                android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                displaySelectedScreen(R.id.nav_home);
            }
        }
    }

    public void logout() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("last_user_name", "");
        editor.putString("last_user_email", "");
        editor.putString("last_user_password", "");
        editor.commit();
        fragCode = 0;
        user = null;
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


    public void newScreenPreview(View view) {
        Intent intent = new Intent(this, CalendarViewActivity.class);
        this.setTitle("Calendar View");
        startActivity(intent);
    }


    public void newScreenPreview2(View view) {
        setContentView(R.layout.activity_renter_view);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //Save the fragment's instance
        outState.putInt("fragCode", fragCode);
        super.onSaveInstanceState(outState);
    }
}
