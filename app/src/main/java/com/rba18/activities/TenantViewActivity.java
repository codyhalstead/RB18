package com.rba18.activities;

import android.app.AlertDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.InputFilter;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.rba18.R;
import com.rba18.fragments.TenantViewFrag1;
import com.rba18.fragments.TenantViewFrag2;
import com.rba18.fragments.TenantViewFrag3;
import com.rba18.helpers.ApartmentTenantViewModel;
import com.rba18.helpers.CustomDatePickerDialogLauncher;
import com.rba18.helpers.DateAndCurrencyDisplayer;
import com.rba18.model.Tenant;
import com.rba18.sqlite.DatabaseHandler;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TenantViewActivity extends BaseActivity implements View.OnClickListener, TenantViewFrag2.OnMoneyDataChangedListener,
        TenantViewFrag3.OnLeaseDataChangedListener {
    private DatabaseHandler mDatabaseHandler;
    private Tenant mTenant;
    private LinearLayout mDateSelectorLL;
    private Date mFilterDateStart, mFilterDateEnd;
    private Button mDateRangeStartBtn, mDateRangeEndBtn;
    private AlertDialog mAlertDialog;
    private TenantViewFrag1 mFrag1;
    private TenantViewFrag2 mFrag2;
    private TenantViewFrag3 mFrag3;
    private ApartmentTenantViewModel mViewModel;
    private Boolean mWasLeaseEdited, mWasIncomeEdited, mWasExpenseEdited, mWasTenantEdited;
    private CustomDatePickerDialogLauncher mDatePickerDialogLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupUserAppTheme(MainActivity.sCurThemeChoice);
        setContentView(R.layout.activity_lease_view_actual);
        mDateSelectorLL = findViewById(R.id.moneyDateSelecterLL);
        mDateSelectorLL.setVisibility(View.GONE);
        mDateRangeStartBtn = findViewById(R.id.moneyListDateRangeStartBtn);
        mDateRangeStartBtn.setOnClickListener(this);
        mDateRangeEndBtn = findViewById(R.id.moneyListDateRangeEndBtn);
        mDateRangeEndBtn.setOnClickListener(this);
        ViewPager viewPager = findViewById(R.id.pager);
        TenantViewActivity.ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        mDatabaseHandler = new DatabaseHandler(this);
        mViewModel = ViewModelProviders.of(this).get(ApartmentTenantViewModel.class);
        mViewModel.init();
        Bundle bundle = getIntent().getExtras();
        int tenantID = bundle.getInt("tenantID");
        mTenant = mDatabaseHandler.getTenantByID(tenantID, MainActivity.sUser);
        int dateFormatCode = preferences.getInt("dateFormat", DateAndCurrencyDisplayer.DATE_MMDDYYYY);
        mViewModel.setViewedTenant(mTenant);
        if (savedInstanceState != null) {
            if (savedInstanceState.getString("mFilterDateStart") != null) {
                DateFormat formatFrom = new SimpleDateFormat("yyyy/MM/dd", Locale.US);
                try {
                    mFilterDateStart = formatFrom.parse(savedInstanceState.getString("mFilterDateStart"));
                    mDateRangeStartBtn.setText(DateAndCurrencyDisplayer.getDateToDisplay(dateFormatCode, mFilterDateStart));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            if (savedInstanceState.getString("mFilterDateEnd") != null) {
                DateFormat formatFrom = new SimpleDateFormat("yyyy/MM/dd", Locale.US);
                try {
                    mFilterDateEnd = formatFrom.parse(savedInstanceState.getString("mFilterDateEnd"));
                    mDateRangeEndBtn.setText(DateAndCurrencyDisplayer.getDateToDisplay(dateFormatCode, mFilterDateEnd));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            mWasLeaseEdited = savedInstanceState.getBoolean("was_lease_edited");
            mWasIncomeEdited = savedInstanceState.getBoolean("was_income_edited");
            mWasExpenseEdited = savedInstanceState.getBoolean("was_expense_edited");
            mWasTenantEdited = savedInstanceState.getBoolean("was_tenant_edited");
        } else {
            Calendar calendar = Calendar.getInstance();
            mFilterDateEnd = calendar.getTime();
            calendar.add(Calendar.YEAR, -1);
            mFilterDateStart = calendar.getTime();
            mDateRangeStartBtn.setText(DateAndCurrencyDisplayer.getDateToDisplay(dateFormatCode, mFilterDateStart));
            mDateRangeEndBtn.setText(DateAndCurrencyDisplayer.getDateToDisplay(dateFormatCode, mFilterDateEnd));
            mWasLeaseEdited = false;
            mWasIncomeEdited = false;
            mWasExpenseEdited = false;
            mWasTenantEdited = false;
        }
        mViewModel.setMoneyArray(mDatabaseHandler.getIncomeAndExpensesByTenantIDWithinDates(MainActivity.sUser, mTenant.getId(), mFilterDateStart, mFilterDateEnd));
        mViewModel.setLeaseArray(mDatabaseHandler.getPrimaryAndSecondaryLeasesForTenant(MainActivity.sUser, mTenant.getId()));
        ArrayList<Tenant> secondaryTenants = new ArrayList<>();
        mViewModel.setSecondaryTenants(secondaryTenants);
        viewPager.setAdapter(adapter);
        ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrollStateChanged(int arg0) {

            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageSelected(int pos) {
                if (pos == 0 || pos == 2) {
                    mDateSelectorLL.setVisibility(View.GONE);
                } else {
                    mDateSelectorLL.setVisibility(View.VISIBLE);
                }
            }
        };
        viewPager.addOnPageChangeListener(pageChangeListener);
        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        setupBasicToolbar();
        addToolbarBackButton();
        mDatePickerDialogLauncher = new CustomDatePickerDialogLauncher(mFilterDateStart, mFilterDateEnd, true, this);
        mDatePickerDialogLauncher.setDateSelectedListener(new CustomDatePickerDialogLauncher.DateSelectedListener() {
            @Override
            public void onStartDateSelected(Date startDate, Date endDate) {
                mFilterDateStart = startDate;
                mFilterDateEnd = endDate;
                mViewModel.setMoneyArray(mDatabaseHandler.getIncomeAndExpensesByTenantIDWithinDates(MainActivity.sUser, mTenant.getId(), mFilterDateStart, mFilterDateEnd));
                int dateFormatCode = preferences.getInt("dateFormat", DateAndCurrencyDisplayer.DATE_MMDDYYYY);
                mDateRangeEndBtn.setText(DateAndCurrencyDisplayer.getDateToDisplay(dateFormatCode, mFilterDateEnd));
                mDateRangeStartBtn.setText(DateAndCurrencyDisplayer.getDateToDisplay(dateFormatCode, mFilterDateStart));
                updateFragmentDates();
            }
            @Override
            public void onEndDateSelected(Date startDate, Date endDate) {
                mFilterDateStart = startDate;
                mFilterDateEnd = endDate;
                mViewModel.setMoneyArray(mDatabaseHandler.getIncomeAndExpensesByTenantIDWithinDates(MainActivity.sUser, mTenant.getId(), mFilterDateStart, mFilterDateEnd));
                int dateFormatCode = preferences.getInt("dateFormat", DateAndCurrencyDisplayer.DATE_MMDDYYYY);
                mDateRangeEndBtn.setText(DateAndCurrencyDisplayer.getDateToDisplay(dateFormatCode, mFilterDateEnd));
                mDateRangeStartBtn.setText(DateAndCurrencyDisplayer.getDateToDisplay(dateFormatCode, mFilterDateStart));
                updateFragmentDates();
            }
            @Override
            public void onDateSelected(Date date) {

            }
        });
        this.setTitle(R.string.tenant_view);
        if (mWasLeaseEdited || mWasIncomeEdited || mWasExpenseEdited || mWasTenantEdited) {
            setResultToEdited();
        } else {
            setResult(RESULT_OK);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Get options menu
        getMenuInflater().inflate(R.menu.tenant_view_edit_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mWasLeaseEdited || mWasIncomeEdited || mWasExpenseEdited || mWasTenantEdited) {
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
        intent.putExtra("was_tenant_edited", mWasTenantEdited);
        setResult(MainActivity.RESULT_DATA_WAS_MODIFIED, intent);
    }

    @Override
    //Handle option menu actions
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.editTenant:
                Intent intent = new Intent(this, NewTenantWizard.class);
                intent.putExtra("tenantToEdit", mTenant);
                mWasTenantEdited = true;
                setResultToEdited();
                startActivityForResult(intent, MainActivity.REQUEST_NEW_TENANT_FORM);
                return true;

            case R.id.editNotes:
                showEditNotesDialog();
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
                mTenant = mDatabaseHandler.getTenantByID(mTenant.getId(), MainActivity.sUser);
                mViewModel.setViewedTenant(mTenant);
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
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd", Locale.US);
        if (mFilterDateStart != null) {
            outState.putString("mFilterDateStart", formatter.format(mFilterDateStart));
        }
        if (mFilterDateEnd != null) {
            outState.putString("mFilterDateEnd", formatter.format(mFilterDateEnd));
        }
        outState.putBoolean("was_lease_edited", mWasLeaseEdited);
        outState.putBoolean("was_income_edited", mWasIncomeEdited);
        outState.putBoolean("was_expense_edited", mWasExpenseEdited);
        outState.putBoolean("was_tenant_edited", mWasTenantEdited);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.moneyListDateRangeStartBtn:
                mDatePickerDialogLauncher.launchStartDatePickerDialog();
                break;

            case R.id.moneyListDateRangeEndBtn:
                mDatePickerDialogLauncher.launchEndDatePickerDialog();

            default:
                break;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mDatePickerDialogLauncher.dismissDatePickerDialog();
        if(mAlertDialog != null){
            mAlertDialog.dismiss();
        }
    }

    private void updateFragmentDates() {
        if (mFrag2 != null) {
            mFrag2.updateData();
        }
        if (mFrag3 != null) {
            mFrag3.updateData();
        }
    }

    @Override
    public void onMoneyDataChanged() {
        mViewModel.setMoneyArray(mDatabaseHandler.getIncomeAndExpensesByTenantIDWithinDates(MainActivity.sUser, mTenant.getId(), mFilterDateStart, mFilterDateEnd));
        mFrag2.updateData();
    }

    @Override
    public void onLeaseDataChanged() {
        mTenant = mDatabaseHandler.getTenantByID(mTenant.getId(), MainActivity.sUser);
        mViewModel.setViewedTenant(mTenant);
        mViewModel.setLeaseArray(mDatabaseHandler.getUsersLeasesForTenant(MainActivity.sUser, mTenant.getId()));
        mViewModel.setMoneyArray(mDatabaseHandler.getIncomeAndExpensesByTenantIDWithinDates(MainActivity.sUser, mTenant.getId(), mFilterDateStart, mFilterDateEnd));
        mFrag2.updateData();
        mFrag3.updateData();
        mWasLeaseEdited = true;
        setResultToEdited();
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
    public void onLeasePaymentsChanged(){
        mWasExpenseEdited = true;
        mWasIncomeEdited = true;
        setResultToEdited();
    }

    public void showEditNotesDialog() {
        final EditText editText = new EditText(TenantViewActivity.this);
        int maxLength = 500;
        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
        editText.setSingleLine(false);
        editText.setImeOptions(EditorInfo.IME_FLAG_NO_ENTER_ACTION);
        editText.setText(mTenant.getNotes());
        editText.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        editText.setSelection(editText.getText().length());
        // create the AlertDialog as final
        mAlertDialog = new AlertDialog.Builder(TenantViewActivity.this)
                .setTitle(R.string.edit_notes)
                .setView(editText)
                // Set the action buttons
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String input = editText.getText().toString();
                        mTenant.setNotes(input);
                        mDatabaseHandler.editTenant(mTenant);
                        mWasTenantEdited = true;
                        setResultToEdited();
                        mViewModel.setViewedTenant(mTenant);
                        if (mFrag1 != null) {
                            mFrag1.updateTenantData(mTenant);
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // removes the AlertDialog in the screen
                    }
                })
                .create();
        mAlertDialog.show();
    }

    // Adapter for the viewpager using FragmentPagerAdapter
    class ViewPagerAdapter extends FragmentStatePagerAdapter {

        private ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            Bundle bundle = new Bundle();
            bundle.putParcelable("mTenant", mTenant);
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
                    mFrag1 = (TenantViewFrag1) createdFragment;
                    break;
                case 1:
                    mFrag2 = (TenantViewFrag2) createdFragment;
                    break;
                case 2:
                    mFrag3 = (TenantViewFrag3) createdFragment;
                    break;
            }
            return createdFragment;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getResources().getString(R.string.info_tab_title);
                case 1:
                    return getResources().getString(R.string.payments_tab_title);
                case 2:
                    return getResources().getString(R.string.lease_history_tab_title);
            }
            return "";
        }
    }

    public void showDeleteConfirmationAlertDialog() {
        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //builder.setTitle("AlertDialog");
        builder.setMessage(R.string.tenant_deletion_confirmation);
        // add the buttons
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mDatabaseHandler.setTenantInactive(mTenant);
                mWasTenantEdited = true;
                setResultToEdited();
                TenantViewActivity.this.finish();
            }
        });
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        // create and show the alert dialog
        mAlertDialog = builder.create();
        mAlertDialog.show();
    }
}


