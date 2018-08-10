package com.rentbud.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;

import com.example.cody.rentbud.R;
import com.rentbud.fragments.ApartmentListFragment;
import com.rentbud.fragments.ApartmentViewFrag1;
import com.rentbud.fragments.ApartmentViewFrag3;
import com.rentbud.fragments.ApartmentViewFrag2;
import com.rentbud.fragments.TenantListFragment;
import com.rentbud.helpers.ApartmentTenantViewModel;
import com.rentbud.helpers.CustomDatePickerDialogLauncher;
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

public class ApartmentViewActivity2 extends BaseActivity implements View.OnClickListener,
        ApartmentViewFrag3.OnLeaseDataChangedListener,
        ApartmentViewFrag2.OnMoneyDataChangedListener {
    Apartment apartment;
    MainArrayDataMethods dataMethods;
    DatabaseHandler databaseHandler;
    ViewPager.OnPageChangeListener mPageChangeListener;
    ViewPager viewPager;
    ApartmentViewActivity2.ViewPagerAdapter adapter;
    LinearLayout dateSelectorLL;
    Date filterDateStart, filterDateEnd;
    Tenant primaryTenant;
    ArrayList<Tenant> secondaryTenants;
    Lease currentLease;
    private Boolean wasLeaseEdited, wasIncomeEdited, wasExpenseEdited, wasApartmentEdited;
    private CustomDatePickerDialogLauncher datePickerDialogLauncher;
    Button dateRangeStartBtn, dateRangeEndBtn;
    private ApartmentViewFrag1 frag1;
    private ApartmentViewFrag2 frag2;
    private ApartmentViewFrag3 frag3;
    private ApartmentTenantViewModel viewModel;

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

        viewPager = findViewById(R.id.pager);
        adapter = new ApartmentViewActivity2.ViewPagerAdapter(getSupportFragmentManager());
        // Add Fragments to adapter one by one
        this.databaseHandler = new DatabaseHandler(this);
        this.dataMethods = new MainArrayDataMethods();
        Bundle bundle = getIntent().getExtras();
        int apartmentID = bundle.getInt("apartmentID");
        this.apartment = databaseHandler.getApartmentByID(apartmentID, MainActivity.user);
        bundle.putParcelable("apartment", apartment);
        viewModel = ViewModelProviders.of(this).get(ApartmentTenantViewModel.class);
        viewModel.init();
        viewModel.setApartment(apartment);
        currentLease = dataMethods.getCachedActiveLeaseByApartmentID(apartment.getId());
        secondaryTenants = new ArrayList<>();
        if(currentLease != null){
            primaryTenant = databaseHandler.getTenantByID(currentLease.getPrimaryTenantID(), MainActivity.user);
            ArrayList<Integer> secondaryTenantIDs = currentLease.getSecondaryTenantIDs();
            for (int i = 0; i < secondaryTenantIDs.size(); i++) {
                Tenant secondaryTenant = databaseHandler.getTenantByID(secondaryTenantIDs.get(i), MainActivity.user);
                secondaryTenants.add(secondaryTenant);
            }
        }
        viewModel.setLease(currentLease);
        viewModel.setPrimaryTenant(primaryTenant);
        viewModel.setSecondaryTenants(secondaryTenants);
        if (savedInstanceState != null) {
            if (savedInstanceState.getString("filterDateStart") != null) {
                SimpleDateFormat formatTo = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
                DateFormat formatFrom = new SimpleDateFormat("yyyy/MM/dd", Locale.US);
                try {
                    Date startDate = formatFrom.parse(savedInstanceState.getString("filterDateStart"));
                    this.filterDateStart = startDate;
                    this.dateRangeStartBtn.setText(formatTo.format(startDate));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            if (savedInstanceState.getString("filterDateEnd") != null) {
                SimpleDateFormat formatTo = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
                DateFormat formatFrom = new SimpleDateFormat("yyyy/MM/dd", Locale.US);
                try {
                    Date endDate = formatFrom.parse(savedInstanceState.getString("filterDateEnd"));
                    this.filterDateEnd = endDate;
                    this.dateRangeEndBtn.setText(formatTo.format(endDate));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            wasLeaseEdited = savedInstanceState.getBoolean("was_lease_edited");
            wasIncomeEdited = savedInstanceState.getBoolean("was_income_edited");
            wasExpenseEdited = savedInstanceState.getBoolean("was_expense_edited");
            wasApartmentEdited = savedInstanceState.getBoolean("was_apartment_edited");
        } else {
            Date endDate = Calendar.getInstance().getTime();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(endDate);
            calendar.add(Calendar.YEAR, -1);
            Date startDate = calendar.getTime();
            this.filterDateEnd = endDate;
            this.filterDateStart = startDate;
            wasLeaseEdited = false;
            wasIncomeEdited = false;
            wasExpenseEdited = false;
            wasApartmentEdited = false;
        }
        //ApartmentViewFrag3 ap2 = (ApartmentViewFrag3) frag2;
        //ap2.updateDates();
        //((ApartmentViewFrag3) frag2).updateDates();
        // adapter.addFragment(new FragmentThree(), "FRAG3");
        viewPager.setAdapter(adapter);
        viewModel.setMoneyArray(databaseHandler.getIncomeAndExpensesByApartmentIDWithinDates(MainActivity.user, apartment.getId(), filterDateStart, filterDateEnd));
        viewModel.setLeaseArray(databaseHandler.getUsersLeasesForApartment(MainActivity.user, apartment.getId()));

        //this.currentFilteredExpenses = db.getUsersExpensesWithinDates(MainActivity.user, startDate, endDate );

        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
        dateRangeStartBtn.setText(formatter.format(filterDateStart));
        dateRangeEndBtn.setText(formatter.format(filterDateEnd));
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
        tabLayout.setSelectedTabIndicatorColor(Color.parseColor("#000000"));
        tabLayout.setTabTextColors(Color.parseColor("#ffffff"), Color.parseColor("#4d4c4b"));
        tabLayout.setupWithViewPager(viewPager);
        setupBasicToolbar();
        datePickerDialogLauncher = new CustomDatePickerDialogLauncher(filterDateStart, filterDateEnd,true , this);
        datePickerDialogLauncher.setDateSelectedListener(new CustomDatePickerDialogLauncher.DateSelectedListener() {
            @Override
            public void onStartDateSelected(Date startDate, Date endDate) {
                filterDateStart = startDate;
                filterDateEnd = endDate;
                SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
                dateRangeEndBtn.setText(formatter.format(filterDateEnd));
                dateRangeStartBtn.setText(formatter.format(filterDateStart));
                updateFragmentDates();
            }

            @Override
            public void onEndDateSelected(Date startDate, Date endDate) {
                filterDateStart = startDate;
                filterDateEnd = endDate;
                SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
                dateRangeEndBtn.setText(formatter.format(filterDateEnd));
                dateRangeStartBtn.setText(formatter.format(filterDateStart));
                updateFragmentDates();
            }

            @Override
            public void onDateSelected(Date date) {

            }
        });
        if (wasLeaseEdited || wasIncomeEdited || wasExpenseEdited || wasApartmentEdited) {
            setResultToEdited();
        } else {
            setResult(RESULT_OK);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Get options menu
        getMenuInflater().inflate(R.menu.apartment_view_edit_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (wasLeaseEdited || wasIncomeEdited || wasExpenseEdited || wasApartmentEdited) {
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
        intent.putExtra("was_apartment_edited", wasApartmentEdited);
        setResult(MainActivity.RESULT_DATA_WAS_MODIFIED, intent);
    }

    @Override
    //Handle option menu actions
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.editApartment:
                Intent intent = new Intent(this, NewApartmentWizard.class);
                intent.putExtra("apartmentToEdit", apartment);
                wasApartmentEdited = true;
                setResultToEdited();
                startActivityForResult(intent, MainActivity.REQUEST_NEW_APARTMENT_FORM);
                return true;

            case R.id.editMainPic:
                ActivityCompat.requestPermissions(
                        ApartmentViewActivity2.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MainActivity.REQUEST_GALLERY_FOR_MAIN_PIC
                );
                return true;

            case R.id.editotherPics:
                ActivityCompat.requestPermissions(
                        ApartmentViewActivity2.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MainActivity.REQUEST_GALLERY_FOR_OTHER_PICS
                );
                return true;

            case R.id.deleteApartment:
                showDeleteConfirmationAlertDialog();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void showDeleteConfirmationAlertDialog() {
        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //builder.setTitle("AlertDialog");
        builder.setMessage(R.string.apartment_deletion_confirmation);

        // add the buttons
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                databaseHandler.setApartmentInactive(apartment);
                wasApartmentEdited = true;
                setResultToEdited();
                // ApartmentListFragment.apartmentListAdapterNeedsRefreshed = true;
                ApartmentViewActivity2.this.finish();

            }
        });
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }

        });

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Uses apartment form to edit data
        if (requestCode == MainActivity.REQUEST_NEW_APARTMENT_FORM) {
            //If successful(not cancelled, passed validation)
            if (resultCode == RESULT_OK) {
                //Re-query cached apartment array to update cache and refresh current textViews to display new data. Re-query to sort list

                //int apartmentID = data.getIntExtra("editedApartmentID", 0);
                this.apartment = databaseHandler.getApartmentByID(apartment.getId(), MainActivity.user);
                viewModel.setApartment(apartment);
                //fillTextViews();
                //ApartmentListFragment.apartmentListAdapterNeedsRefreshed = true;
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
        //if (requestCode == MainActivity.REQUEST_NEW_EXPENSE_FORM) {
        //    //If successful(not cancelled, passed validation)
        //    if (resultCode == RESULT_OK) {
        //        //Re-query cached apartment array to update cache and refresh current fragment to display new data
        //        //int expenseID = data.getIntExtra("editedExpenseID", 0);
        //        //this.expense = databaseHandler.getExpenseLogEntryByID(expenseID, MainActivity.user);
        //        //fillTextViews();
        //        Log.d("TAG", "onActivityResult: FUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUU");
        //        ExpenseListFragment.expenseListAdapterNeedsRefreshed = true;
        //        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        //        if (fragments != null) {
        //            for (Fragment fragment : fragments) {
        //                if (fragment != null) {
        //                    android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        //                    fragmentTransaction.detach(fragment);
        //                    fragmentTransaction.attach(fragment);
        //                    fragmentTransaction.commit();
        //                }
        //            }
        //        }
        //    }
        //}
        else {
            List<Fragment> fragments = getSupportFragmentManager().getFragments();
            if (fragments != null) {
                for (Fragment fragment : fragments) {
                    if (fragment != null) {
                        fragment.onActivityResult(requestCode, resultCode, data);
                    }
                }
            }
        }

        //if (requestCode == MainActivity.REQUEST_GALLERY_FOR_MAIN_PIC) {
        //    if (resultCode == RESULT_OK && data != null) {
        //        Uri selectedImage = data.getData();
        //        String[] filePathColumn = {MediaStore.Images.Media.DATA};

        //        Cursor cursor = getContentResolver().query(selectedImage,
        //                filePathColumn, null, null, null);
        //        cursor.moveToFirst();

        //        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);

        //        String filePath = cursor.getString(columnIndex);
        //        //file path of captured image
        //        cursor.close();
        //        Glide.with(this).load(filePath).into(mainPicIV);
        //        this.apartment.setMainPic(filePath);
        //        this.mainPic = filePath;
        //        databaseHandler.changeApartmentMainPic(this.apartment);
        //MainActivity5.apartmentList = databaseHandler.getUsersApartments(MainActivity5.user);
        //        ApartmentListFragment.apartmentListAdapterNeedsRefreshed = true;
        //    }
        //}
        //if (requestCode == MainActivity.REQUEST_GALLERY_FOR_OTHER_PICS) {
        //    if (resultCode == RESULT_OK && data != null) {
        //        Uri selectedImage = data.getData();
        //        String[] filePathColumn = {MediaStore.Images.Media.DATA};

        //        Cursor cursor = getContentResolver().query(selectedImage,
        //                filePathColumn, null, null, null);
        //        cursor.moveToFirst();

        //        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);

        //        String filePath = cursor.getString(columnIndex);
        //        //file path of captured image
        //        File f = new File(filePath);
        //       String filename = f.getName();

        //        cursor.close();
        //        this.apartment.addOtherPic(filePath);
        //        databaseHandler.addApartmentOtherPic(apartment, filePath, MainActivity.user);
        //MainActivity5.apartmentList = databaseHandler.getUsersApartments(MainActivity5.user);
        //ApartmentListFragment.apartmentListAdapterNeedsRefreshed = true;
        //        adapter.notifyDataSetChanged();
        //    }
        //}
        //if (requestCode == MainActivity.REQUEST_NEW_LEASE_FORM) {
        //    if (resultCode == RESULT_OK) {
        //int apartmentID = data.getIntExtra("updatedApartmentID", 0);
        //int primaryTenantID = data.getParcelableExtra("updatedPrimaryTenantID");
        //        this.apartment = dataMethods.getCachedApartmentByApartmentID(apartment.getId());
        //        Pair<Tenant, ArrayList<Tenant>> tenants = dataMethods.getCachedPrimaryAndSecondaryTenantsByLease(currentLease); //TODO
        //        this.primaryTenant = tenants.first;
        //        this.secondaryTenants = tenants.second;
        //this.secondaryTenants = data.getParcelableArrayListExtra("updatedSecondaryTenants");
        //        fillTextViews();
        //        ApartmentListFragment.apartmentListAdapterNeedsRefreshed = true;
        //    }
        //}
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
        outState.putBoolean("was_apartment_edited", wasApartmentEdited);
        //getSupportFragmentManager().putFragment(outState, "frag1",  (adapter.getItem(0)));
        //getSupportFragmentManager().putFragment(outState, "frag2",  (adapter.getItem(1)));
        //getSupportFragmentManager().putFragment(outState, "frag3",  (adapter.getItem(2)));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.moneyListDateRangeStartBtn:
                datePickerDialogLauncher.launchStartDatePickerDialog();
                break;

            case R.id.moneyListDateRangeEndBtn:
                datePickerDialogLauncher.launchEndDatePickerDialog();
                break;

            default:
                break;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        datePickerDialogLauncher.dismissDatePickerDialog();
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
    public void onLeaseDataChanged() {
        this.apartment = databaseHandler.getApartmentByID(apartment.getId(), MainActivity.user);
        viewModel.setApartment(apartment);
        //viewModel.setLease(null); //TODO update lease
        viewModel.setLeaseArray(databaseHandler.getUsersLeasesForApartment(MainActivity.user, apartment.getId()));
        viewModel.setMoneyArray(databaseHandler.getIncomeAndExpensesByApartmentIDWithinDates(MainActivity.user, apartment.getId(), filterDateStart, filterDateEnd));
        frag2.updateData();
        frag3.updateData();
        wasLeaseEdited = true;
        setResultToEdited();
    }

    @Override
    public void onLeasePaymentsChanged(){
        wasExpenseEdited = true;
        wasIncomeEdited = true;
        setResultToEdited();
    }

    @Override
    public void onMoneyDataChanged() {
        viewModel.setMoneyArray(databaseHandler.getIncomeAndExpensesByApartmentIDWithinDates(MainActivity.user, apartment.getId(), filterDateStart, filterDateEnd));
        frag2.updateData();
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

    // Adapter for the viewpager using FragmentPagerAdapter
    class ViewPagerAdapter extends FragmentStatePagerAdapter {

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            Bundle bundle = new Bundle();
            bundle.putParcelable("apartment", apartment);
            switch (position) {
                case 0:
                    ApartmentViewFrag1 frg1 = new ApartmentViewFrag1();
                    frg1.setArguments(bundle);
                    return frg1;
                case 1:
                    ApartmentViewFrag2 frg2 = new ApartmentViewFrag2();
                    frg2.setArguments(bundle);
                    return frg2;
                case 2:
                    ApartmentViewFrag3 frg3 = new ApartmentViewFrag3();
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
                    frag1 = (ApartmentViewFrag1) createdFragment;
                    break;
                case 1:
                    frag2 = (ApartmentViewFrag2) createdFragment;
                    break;
                case 2:
                    frag3 = (ApartmentViewFrag3) createdFragment;
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
}


