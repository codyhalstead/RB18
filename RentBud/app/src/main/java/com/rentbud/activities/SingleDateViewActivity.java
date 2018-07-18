package com.rentbud.activities;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.cody.rentbud.R;
import com.rentbud.fragments.DateViewFrag1;
import com.rentbud.fragments.DateViewFrag2;
import com.rentbud.helpers.ApartmentTenantViewModel;
import com.rentbud.sqlite.DatabaseHandler;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Cody on 1/12/2018.
 */

public class SingleDateViewActivity extends BaseActivity implements DateViewFrag1.OnMoneyDataChangedListener,
        DateViewFrag2.OnLeaseDataChangedListener {
    TextView dateTV;
    Date date;
    ViewPager viewPager;
    SingleDateViewActivity.ViewPagerAdapter adapter;
    private DatabaseHandler databaseHandler;
    private ApartmentTenantViewModel viewModel;
    private DateViewFrag1 frag1;
    private DateViewFrag2 frag2;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupUserAppTheme(MainActivity.curThemeChoice);
        setContentView(R.layout.activity_single_date_view);

        viewPager = (ViewPager) findViewById(R.id.pager);
        adapter = new SingleDateViewActivity.ViewPagerAdapter(getSupportFragmentManager());
        databaseHandler = new DatabaseHandler(this);
        Bundle bundle = getIntent().getExtras();
        date = (Date) bundle.get("date");
        final SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM dd yyyy", Locale.US);
        dateTV = findViewById(R.id.selectedDateTV);
        dateTV.setText(formatter.format(date));
        setupBasicToolbar();

        viewModel = ViewModelProviders.of(this).get(ApartmentTenantViewModel.class);
        viewModel.init();
        viewModel.setDate(date);
        viewModel.setLeaseArray(databaseHandler.getLeasesStartingOrEndingOnDate(MainActivity.user, date));
        viewModel.setMoneyArray(databaseHandler.getIncomeAndExpensesForDate(MainActivity.user, date));
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setSelectedTabIndicatorColor(Color.parseColor("#000000"));
        tabLayout.setTabTextColors(Color.parseColor("#ffffff"), Color.parseColor("#4d4c4b"));
        tabLayout.setupWithViewPager(viewPager);
        setupBasicToolbar();
        toolbar.setTitle("Date View");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Uses apartment form to edit data

        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        if (fragments != null) {
            for (Fragment fragment : fragments) {
                if (fragment != null) {
                    fragment.onActivityResult(requestCode, resultCode, data);
                }
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onMoneyDataChanged() {
        viewModel.setMoneyArray(databaseHandler.getIncomeAndExpensesForDate(MainActivity.user, date));
        frag1.updateData();
    }

    @Override
    public void onLeaseDataChanged() {
        viewModel.setLeaseArray(databaseHandler.getLeasesStartingOrEndingOnDate(MainActivity.user, date));
        viewModel.setMoneyArray(databaseHandler.getIncomeAndExpensesForDate(MainActivity.user, date));
        frag1.updateData();
        frag2.updateData();
    }

    // Adapter for the viewpager using FragmentPagerAdapter
    class ViewPagerAdapter extends FragmentPagerAdapter {


        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            Bundle bundle = new Bundle();
            switch (position) {
                case 0:
                    DateViewFrag1 frg1 = new DateViewFrag1();
                    frg1.setArguments(bundle);
                    return frg1;
                case 1:
                    DateViewFrag2 frg2 = new DateViewFrag2();
                    frg2.setArguments(bundle);
                    return frg2;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment createdFragment = (Fragment) super.instantiateItem(container, position);
            // save the appropriate reference depending on position
            switch (position) {
                case 0:
                    frag1 = (DateViewFrag1) createdFragment;
                    break;
                case 1:
                    frag2 = (DateViewFrag2) createdFragment;
                    break;
            }
            return createdFragment;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Income And Expenses";
                case 1:
                    return "Lease Information";
            }
            return "";
        }
    }
}
