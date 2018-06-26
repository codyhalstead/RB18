package com.rentbud.activities;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.example.cody.rentbud.R;
import com.rentbud.fragments.ApartmentViewFrag1;
import com.rentbud.fragments.ApartmentViewFrag2;
import com.rentbud.fragments.ApartmentViewFrag3;
import com.rentbud.fragments.DateViewFrag1;
import com.rentbud.fragments.DateViewFrag2;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Cody on 1/12/2018.
 */

public class SingleDateViewActivity extends BaseActivity {
    TextView dateTV;
    Date date;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupUserAppTheme(MainActivity.curThemeChoice);
        setContentView(R.layout.activity_single_date_view);

        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        SingleDateViewActivity.ViewPagerAdapter adapter = new SingleDateViewActivity.ViewPagerAdapter(getSupportFragmentManager());

        Bundle bundle = getIntent().getExtras();
        date = (Date) bundle.get("date");
        final SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM dd yyyy", Locale.US);
        dateTV = findViewById(R.id.selectedDateTV);
        dateTV.setText(formatter.format(date));
        setupBasicToolbar();

        Fragment frag1 = new DateViewFrag1();
        Fragment frag2 = new DateViewFrag2();
        frag1.setArguments(bundle);
        frag2.setArguments(bundle);
        adapter.addFragment(frag1, "Income And Expenses");
        adapter.addFragment(frag2, "Lease Information");
        // adapter.addFragment(new FragmentThree(), "FRAG3");
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setSelectedTabIndicatorColor(Color.parseColor("#000000"));
        tabLayout.setTabTextColors(Color.parseColor("#ffffff"), Color.parseColor("#4d4c4b"));
        tabLayout.setupWithViewPager(viewPager);
        setupBasicToolbar();
        toolbar.setTitle("Date View");
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
}
