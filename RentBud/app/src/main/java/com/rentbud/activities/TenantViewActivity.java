package com.rentbud.activities;

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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.example.cody.rentbud.R;
import com.rentbud.fragments.TenantViewFrag1;
import com.rentbud.fragments.TenantViewFrag2;
import com.rentbud.fragments.TenantViewFrag3;
import com.rentbud.helpers.ApartmentTenantViewModel;
import com.rentbud.helpers.CustomDatePickerDialogLauncher;
import com.rentbud.helpers.DateAndCurrencyDisplayer;
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

public class TenantViewActivity extends BaseActivity implements View.OnClickListener, TenantViewFrag2.OnMoneyDataChangedListener,
        TenantViewFrag3.OnLeaseDataChangedListener {
    private DatabaseHandler databaseHandler;
    private MainArrayDataMethods dataMethods;
    private ArrayList<Tenant> otherTenants;
    private Lease currentLease;
    private Tenant tenant;
    private Apartment apartment;
    ViewPager.OnPageChangeListener mPageChangeListener;
    ViewPager viewPager;
    TenantViewActivity.ViewPagerAdapter adapter;
    LinearLayout dateSelectorLL;
    Date filterDateStart, filterDateEnd;
    Button dateRangeStartBtn, dateRangeEndBtn;
    private AlertDialog alertDialog;
    private TenantViewFrag1 frag1;
    private TenantViewFrag2 frag2;
    private TenantViewFrag3 frag3;
    private ApartmentTenantViewModel viewModel;
    private Boolean wasLeaseEdited, wasIncomeEdited, wasExpenseEdited, wasTenantEdited;
    private CustomDatePickerDialogLauncher datePickerDialogLauncher;

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
        Bundle bundle = getIntent().getExtras();
        int tenantID = bundle.getInt("tenantID");
        tenant = databaseHandler.getTenantByID(tenantID, MainActivity.user);
        int dateFormatCode = preferences.getInt("dateFormat", DateAndCurrencyDisplayer.DATE_MMDDYYYY);
        viewModel.setViewedTenant(tenant);
        if (savedInstanceState != null) {
            if (savedInstanceState.getString("filterDateStart") != null) {
                DateFormat formatFrom = new SimpleDateFormat("yyyy/MM/dd", Locale.US);
                try {
                    Date startDate = formatFrom.parse(savedInstanceState.getString("filterDateStart"));
                    this.filterDateStart = startDate;
                    this.dateRangeStartBtn.setText(DateAndCurrencyDisplayer.getDateToDisplay(dateFormatCode, startDate));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            if (savedInstanceState.getString("filterDateEnd") != null) {
                DateFormat formatFrom = new SimpleDateFormat("yyyy/MM/dd", Locale.US);
                try {
                    Date endDate = formatFrom.parse(savedInstanceState.getString("filterDateEnd"));
                    this.filterDateEnd = endDate;
                    this.dateRangeEndBtn.setText(DateAndCurrencyDisplayer.getDateToDisplay(dateFormatCode, endDate));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            wasLeaseEdited = savedInstanceState.getBoolean("was_lease_edited");
            wasIncomeEdited = savedInstanceState.getBoolean("was_income_edited");
            wasExpenseEdited = savedInstanceState.getBoolean("was_expense_edited");
            wasTenantEdited = savedInstanceState.getBoolean("was_tenant_edited");
        } else {
            Date endDate = Calendar.getInstance().getTime();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(endDate);
            calendar.add(Calendar.YEAR, -1);
            Date startDate = calendar.getTime();
            this.filterDateEnd = endDate;
            this.filterDateStart = startDate;
            dateRangeStartBtn.setText(DateAndCurrencyDisplayer.getDateToDisplay(dateFormatCode, filterDateStart));
            dateRangeEndBtn.setText(DateAndCurrencyDisplayer.getDateToDisplay(dateFormatCode, filterDateEnd));
            wasLeaseEdited = false;
            wasIncomeEdited = false;
            wasExpenseEdited = false;
            wasTenantEdited = false;
        }

        viewModel.setMoneyArray(databaseHandler.getIncomeAndExpensesByTenantIDWithinDates(MainActivity.user, tenant.getId(), filterDateStart, filterDateEnd));
        viewModel.setLeaseArray(databaseHandler.getPrimaryAndSecondaryLeasesForTenant(MainActivity.user, tenant.getId()));
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
        viewPager.setAdapter(adapter);
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
        tabLayout.setupWithViewPager(viewPager);
        setupBasicToolbar();
        datePickerDialogLauncher = new CustomDatePickerDialogLauncher(filterDateStart, filterDateEnd, true, this);
        datePickerDialogLauncher.setDateSelectedListener(new CustomDatePickerDialogLauncher.DateSelectedListener() {
            @Override
            public void onStartDateSelected(Date startDate, Date endDate) {
                filterDateStart = startDate;
                filterDateEnd = endDate;
                viewModel.setMoneyArray(databaseHandler.getIncomeAndExpensesByTenantIDWithinDates(MainActivity.user, tenant.getId(), filterDateStart, filterDateEnd));
                int dateFormatCode = preferences.getInt("dateFormat", DateAndCurrencyDisplayer.DATE_MMDDYYYY);
                dateRangeEndBtn.setText(DateAndCurrencyDisplayer.getDateToDisplay(dateFormatCode, filterDateEnd));
                dateRangeStartBtn.setText(DateAndCurrencyDisplayer.getDateToDisplay(dateFormatCode, filterDateStart));
                updateFragmentDates();
            }

            @Override
            public void onEndDateSelected(Date startDate, Date endDate) {
                filterDateStart = startDate;
                filterDateEnd = endDate;
                viewModel.setMoneyArray(databaseHandler.getIncomeAndExpensesByTenantIDWithinDates(MainActivity.user, tenant.getId(), filterDateStart, filterDateEnd));
                int dateFormatCode = preferences.getInt("dateFormat", DateAndCurrencyDisplayer.DATE_MMDDYYYY);
                dateRangeEndBtn.setText(DateAndCurrencyDisplayer.getDateToDisplay(dateFormatCode, filterDateEnd));
                dateRangeStartBtn.setText(DateAndCurrencyDisplayer.getDateToDisplay(dateFormatCode, filterDateStart));
                updateFragmentDates();
            }

            @Override
            public void onDateSelected(Date date) {

            }
        });
        this.setTitle(R.string.tenant_view);
        if (wasLeaseEdited || wasIncomeEdited || wasExpenseEdited || wasTenantEdited) {
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
        if (wasLeaseEdited || wasIncomeEdited || wasExpenseEdited || wasTenantEdited) {
            setResultToEdited();
        } else {
            setResult(RESULT_OK);
        }
    }

    private void setResultToEdited() {
        Intent intent = new Intent();
        intent.putExtra("was_lease_edited", wasLeaseEdited);
        intent.putExtra("was_income_edited", wasIncomeEdited);
        intent.putExtra("was_expense_edited", wasExpenseEdited);
        intent.putExtra("was_tenant_edited", wasTenantEdited);
        setResult(MainActivity.RESULT_DATA_WAS_MODIFIED, intent);
    }

    @Override
    //Handle option menu actions
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.editTenant:
                Intent intent = new Intent(this, NewTenantWizard.class);
                intent.putExtra("tenantToEdit", tenant);
                wasTenantEdited = true;
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
                this.tenant = databaseHandler.getTenantByID(tenant.getId(), MainActivity.user);
                viewModel.setViewedTenant(tenant);
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
        if (filterDateStart != null) {
            outState.putString("filterDateStart", formatter.format(filterDateStart));
        }
        if (filterDateEnd != null) {
            outState.putString("filterDateEnd", formatter.format(filterDateEnd));
        }
        outState.putBoolean("was_lease_edited", wasLeaseEdited);
        outState.putBoolean("was_income_edited", wasIncomeEdited);
        outState.putBoolean("was_expense_edited", wasExpenseEdited);
        outState.putBoolean("was_tenant_edited", wasTenantEdited);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.moneyListDateRangeStartBtn:
                datePickerDialogLauncher.launchStartDatePickerDialog();
                break;

            case R.id.moneyListDateRangeEndBtn:
                datePickerDialogLauncher.launchEndDatePickerDialog();

            default:
                break;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        datePickerDialogLauncher.dismissDatePickerDialog();
        if(alertDialog != null){
            alertDialog.dismiss();
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
        viewModel.setLeaseArray(databaseHandler.getUsersLeasesForTenant(MainActivity.user, tenant.getId()));
        viewModel.setMoneyArray(databaseHandler.getIncomeAndExpensesByTenantIDWithinDates(MainActivity.user, tenant.getId(), filterDateStart, filterDateEnd));
        frag2.updateData();
        frag3.updateData();
        wasLeaseEdited = true;
        setResultToEdited();
    }

    @Override
    public void onIncomeDataChanged() {
        wasIncomeEdited = true;
        setResultToEdited();
    }

    @Override
    public void onExpenseDataChanged() {
        wasExpenseEdited = true;
        setResultToEdited();
    }

    @Override
    public void onLeasePaymentsChanged(){
        wasExpenseEdited = true;
        wasIncomeEdited = true;
        setResultToEdited();
    }

    public void showEditNotesDialog() {
        final EditText editText = new EditText(TenantViewActivity.this);
        int maxLength = 500;
        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
        editText.setSingleLine(false);
        editText.setImeOptions(EditorInfo.IME_FLAG_NO_ENTER_ACTION);
        editText.setText(tenant.getNotes());
        editText.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        editText.setSelection(editText.getText().length());

        // create the AlertDialog as final
        alertDialog = new AlertDialog.Builder(TenantViewActivity.this)
                //.setMessage(R.string.comfirm_pass_to_delete_account_message)
                .setTitle(R.string.edit_notes)
                .setView(editText)

                // Set the action buttons
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String input = editText.getText().toString();
                        tenant.setNotes(input);
                        databaseHandler.editTenant(tenant);
                        wasTenantEdited = true;
                        setResultToEdited();
                        viewModel.setViewedTenant(tenant);
                        if (frag1 != null) {
                            frag1.updateTenantData(tenant);
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

        alertDialog.show();
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
                databaseHandler.setTenantInactive(tenant);
                wasTenantEdited = true;
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
        alertDialog = builder.create();
        alertDialog.show();
    }

}


