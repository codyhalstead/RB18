package com.rba18.activities;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rba18.R;
import com.rba18.fragments.DateViewFrag1;
import com.rba18.fragments.DateViewFrag2;
import com.rba18.helpers.ApartmentTenantViewModel;
import com.rba18.sqlite.DatabaseHandler;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Cody on 1/12/2018.
 */

public class SingleDateViewActivity extends BaseActivity implements DateViewFrag1.OnMoneyDataChangedListener,
        DateViewFrag2.OnLeaseDataChangedListener {
    private TextView mDateTV;
    private Date mDate;
    private DatabaseHandler mDatabaseHandler;
    private ApartmentTenantViewModel mViewModel;
    private DateViewFrag1 mFrag1;
    private DateViewFrag2 mFrag2;
    private Boolean mWasLeaseEdited, mWasIncomeEdited, mWasExpenseEdited;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupUserAppTheme(MainActivity.sCurThemeChoice);
        setContentView(R.layout.activity_single_date_view);
        ViewPager viewPager = findViewById(R.id.pager);
        SingleDateViewActivity.ViewPagerAdapter adapter = new SingleDateViewActivity.ViewPagerAdapter(getSupportFragmentManager());
        mDatabaseHandler = new DatabaseHandler(this);
        Bundle bundle = getIntent().getExtras();
        mDate = (Date) bundle.get("date");
        final SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM dd yyyy", Locale.US);
        mDateTV = findViewById(R.id.selectedDateTV);
        mDateTV.setText(formatter.format(mDate));
        setupBasicToolbar();
        if (savedInstanceState != null) {
            mWasLeaseEdited = savedInstanceState.getBoolean("was_lease_edited");
            mWasIncomeEdited = savedInstanceState.getBoolean("was_income_edited");
            mWasExpenseEdited = savedInstanceState.getBoolean("was_expense_edited");
        } else {
            mWasLeaseEdited = false;
            mWasIncomeEdited = false;
            mWasExpenseEdited = false;
        }
        mViewModel = ViewModelProviders.of(this).get(ApartmentTenantViewModel.class);
        mViewModel.init();
        mViewModel.setDate(mDate);
        mViewModel.setLeaseArray(mDatabaseHandler.getLeasesStartingOrEndingOnDate(MainActivity.sUser, mDate));
        mViewModel.setMoneyArray(mDatabaseHandler.getIncomeAndExpensesForDate(MainActivity.sUser, mDate));
        viewPager.setAdapter(adapter);
        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        setupBasicToolbar();
        addToolbarBackButton();
        this.setTitle(R.string.calendar_date_view);
        if (mWasLeaseEdited || mWasIncomeEdited || mWasExpenseEdited) {
            setResultToEdited();
        } else {
            setResult(RESULT_OK);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mWasLeaseEdited || mWasIncomeEdited || mWasExpenseEdited) {
            setResultToEdited();
        } else {
            setResult(RESULT_OK);
        }
    }

    private void setResultToEdited() {
        Intent intent = new Intent();
        intent.putExtra("was_lease_edited", mWasLeaseEdited);
        intent.putExtra("was_income_edited", mWasIncomeEdited);
        intent.putExtra("was_expense_edited", mWasExpenseEdited);
        setResult(MainActivity.RESULT_DATA_WAS_MODIFIED, intent);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("was_lease_edited", mWasLeaseEdited);
        outState.putBoolean("was_income_edited", mWasIncomeEdited);
        outState.putBoolean("was_expense_edited", mWasExpenseEdited);
    }

    @Override
    public void onMoneyDataChanged() {
        mViewModel.setMoneyArray(mDatabaseHandler.getIncomeAndExpensesForDate(MainActivity.sUser, mDate));
        mFrag1.updateData();
    }

    @Override
    public void onIncomeDataChanged() {
        mWasIncomeEdited = true;
        setResultToEdited();
    }

    @Override
    public void onExpenseDataChanged() {
        mWasExpenseEdited = true;
        setResultToEdited();
    }

    @Override
    public void onLeaseDataChanged() {
        mViewModel.setLeaseArray(mDatabaseHandler.getLeasesStartingOrEndingOnDate(MainActivity.sUser, mDate));
        mViewModel.setMoneyArray(mDatabaseHandler.getIncomeAndExpensesForDate(MainActivity.sUser, mDate));
        mFrag1.updateData();
        mFrag2.updateData();
        mWasLeaseEdited = true;
        setResultToEdited();
    }

    @Override
    public void onLeasePaymentsChanged(){
        mWasExpenseEdited = true;
        mWasIncomeEdited = true;
        setResultToEdited();
    }

    // Adapter for the viewpager using FragmentPagerAdapter
    class ViewPagerAdapter extends FragmentPagerAdapter {

        private ViewPagerAdapter(FragmentManager manager) {
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
                    mFrag1 = (DateViewFrag1) createdFragment;
                    break;
                case 1:
                    mFrag2 = (DateViewFrag2) createdFragment;
                    break;
            }
            return createdFragment;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getResources().getString(R.string.payments_tab_title);
                case 1:
                    return getResources().getString(R.string.leases_tab_title);
            }
            return "";
        }
    }
}
