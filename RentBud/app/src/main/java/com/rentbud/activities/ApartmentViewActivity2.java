package com.rentbud.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;

import com.bumptech.glide.Glide;
import com.example.cody.rentbud.R;
import com.rentbud.fragments.ApartmentListFragment;
import com.rentbud.fragments.ApartmentViewFrag1;
import com.rentbud.fragments.ApartmentViewFrag2;
import com.rentbud.fragments.ApartmentViewFrag3;
import com.rentbud.fragments.ExpenseListFragment;
import com.rentbud.fragments.LeaseViewFrag1;
import com.rentbud.fragments.LeaseViewFrag2;
import com.rentbud.fragments.TenantListFragment;
import com.rentbud.helpers.MainArrayDataMethods;
import com.rentbud.model.Apartment;
import com.rentbud.model.Tenant;
import com.rentbud.sqlite.DatabaseHandler;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ApartmentViewActivity2 extends BaseActivity {
    Apartment apartment;
    MainArrayDataMethods dataMethods;
    DatabaseHandler databaseHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupUserAppTheme(MainActivity.curThemeChoice);
        setContentView(R.layout.activity_lease_view_actual);

        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        ApartmentViewActivity2.ViewPagerAdapter adapter = new ApartmentViewActivity2.ViewPagerAdapter(getSupportFragmentManager());

        // Add Fragments to adapter one by one
        //this.dataMethods = new MainArrayDataMethods();
        this.databaseHandler = new DatabaseHandler(this);
        Bundle bundle = getIntent().getExtras();
        int apartmentID = bundle.getInt("apartmentID");
        this.apartment = dataMethods.getCachedApartmentByApartmentID(apartmentID);
        bundle.putParcelable("apartment", apartment);
        //Get apartment item
        //int leaseID = bundle.getInt("leaseID");
        Fragment frag1 = new ApartmentViewFrag1();
        Fragment frag2 = new ApartmentViewFrag2();
        Fragment frag3 = new ApartmentViewFrag3();
        frag1.setArguments(bundle);
        frag2.setArguments(bundle);
        frag3.setArguments(bundle);
        adapter.addFragment(frag1, "Apartment Info");
        adapter.addFragment(frag2, "Expenses");
        adapter.addFragment(frag3, "Income");
        // adapter.addFragment(new FragmentThree(), "FRAG3");
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setSelectedTabIndicatorColor(Color.parseColor("#000000"));
        tabLayout.setTabTextColors(Color.parseColor("#ffffff"), Color.parseColor("#4d4c4b"));
        tabLayout.setupWithViewPager(viewPager);
        setupBasicToolbar();
        toolbar.setTitle("Date View");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Get options menu
        getMenuInflater().inflate(R.menu.apartment_view_edit_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    //Handle option menu actions
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.editApartment:
                Intent intent = new Intent(this, NewApartmentWizard.class);
                intent.putExtra("apartmentToEdit", apartment);
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
        builder.setMessage("Are you sure you want to remove this apartment?");

        // add the buttons
        builder.setPositiveButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                databaseHandler.setApartmentInactive(apartment);
                if (apartment.isRented()) {
                    //TODO update lease
                    //primaryTenant.setApartmentID(0);
                    //primaryTenant.setLeaseStart(null);
                    //primaryTenant.setLeaseEnd(null);
                    //databaseHandler.editTenant(primaryTenant);
                    //for (int x = 0; x < secondaryTenants.size(); x++) {
                    //    secondaryTenants.get(x).setApartmentID(0);
                    //    secondaryTenants.get(x).setLeaseStart(null);
                    //    secondaryTenants.get(x).setLeaseEnd(null);
                    //    databaseHandler.editTenant(secondaryTenants.get(x));
                    //}
                    apartment.setRented(false);
                    dataMethods.sortMainTenantArray();
                }
                MainActivity.apartmentList.remove(apartment);
                TenantListFragment.tenantListAdapterNeedsRefreshed = true;
                ApartmentListFragment.apartmentListAdapterNeedsRefreshed = true;
                ApartmentViewActivity2.this.finish();
            }
        });

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("TAG", "onActivityResult: AHHHHHHHHHHHHHHHHHHHHHHHHHHH" + requestCode);
        //Uses apartment form to edit data
        if (requestCode == MainActivity.REQUEST_NEW_APARTMENT_FORM) {
            //If successful(not cancelled, passed validation)
            if (resultCode == RESULT_OK) {
                //Re-query cached apartment array to update cache and refresh current textViews to display new data. Re-query to sort list

                int apartmentID = data.getIntExtra("editedApartmentID", 0);
                this.apartment = dataMethods.getCachedApartmentByApartmentID(apartmentID);
                //fillTextViews();
                ApartmentListFragment.apartmentListAdapterNeedsRefreshed = true;
                List<Fragment> fragments = getSupportFragmentManager().getFragments();
                if (fragments != null) {
                    for (Fragment fragment : fragments) {
                        if (fragment != null) {
                            android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                            fragmentTransaction.detach(fragment);
                            fragmentTransaction.attach(fragment);
                            fragmentTransaction.commit();
                        }
                    }
                }
            }
        }
        if (requestCode == MainActivity.REQUEST_NEW_EXPENSE_FORM) {
            Log.d("TAG", "onActivityResult: AHHHHHHHHHHHHHHHHHHHHHHHHHHH");
            //If successful(not cancelled, passed validation)
            if (resultCode == RESULT_OK) {
                //Re-query cached apartment array to update cache and refresh current fragment to display new data
                //int expenseID = data.getIntExtra("editedExpenseID", 0);
                //this.expense = databaseHandler.getExpenseLogEntryByID(expenseID, MainActivity.user);
                //fillTextViews();
                Log.d("TAG", "onActivityResult: AHHHHHHHHHHHHHHHHHHHHHHHHHHH");
                ExpenseListFragment.expenseListAdapterNeedsRefreshed = true;
                List<Fragment> fragments = getSupportFragmentManager().getFragments();
                if (fragments != null) {
                    for (Fragment fragment : fragments) {
                        if (fragment != null) {
                            android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                            fragmentTransaction.detach(fragment);
                            fragmentTransaction.attach(fragment);
                            fragmentTransaction.commit();
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


