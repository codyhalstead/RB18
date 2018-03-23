package com.rentbud.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.example.cody.rentbud.R;
import com.rentbud.adapters.RecyclerViewAdapter;
import com.rentbud.fragments.ApartmentListFragment;
import com.rentbud.fragments.TenantListFragment;
import com.rentbud.helpers.ImageViewDialog;
import com.rentbud.helpers.MainArrayDataMethods;
import com.rentbud.model.Apartment;
import com.rentbud.model.Tenant;
import com.rentbud.sqlite.DatabaseHandler;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Cody on 2/6/2018.
 */

public class ApartmentViewActivity extends BaseActivity {
    Apartment apartment;
    TextView street1TV, street2TV, cityTV, stateTV, zipTV, descriptionTV, notesTV, primaryTenantFirstNameTV, primaryTenantLastNAmeTV, primaryTenantDisplayTV;
    TextView secondaryTenantsTV, leaseStatusTV, leaseStartTV, leaseEndTV, leaseHyphenTV;
    LinearLayout primaryTenantLL, secondaryTenantsLL;
    ImageView mainPicIV;
    Button editLeaseBtn;
    DatabaseHandler databaseHandler;
    Tenant primaryTenant;
    ArrayList<Tenant> secondaryTenants;
    String mainPic;
    MainArrayDataMethods dataMethods;

    RecyclerView recyclerView;
    RecyclerViewAdapter adapter;
    ArrayList<String> otherPics;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupUserAppTheme(MainActivity.curThemeChoice);
        setContentView(R.layout.activity_apartment_view);
        this.databaseHandler = new DatabaseHandler(this);
        dataMethods = new MainArrayDataMethods();
        recyclerView = findViewById(R.id.recyclerView);
        secondaryTenants = new ArrayList<>();
        //if recreated
        if (savedInstanceState != null) {
            apartment = savedInstanceState.getParcelable("apartment");
            otherPics = new ArrayList<>();
            if (savedInstanceState.getInt("otherPicsSize") > 0) {
                for (int i = 0; i < savedInstanceState.getInt("otherPicsSize"); i++) {
                    otherPics.add(savedInstanceState.getString("otherPics" + i));
                }
            }
            if (savedInstanceState.getString("mainPic") != null) {
                mainPic = savedInstanceState.getString("mainPic");
            }
            if (savedInstanceState.getParcelable("primaryTenant") != null && savedInstanceState.getParcelableArrayList("secondaryTenants") != null) {
                primaryTenant = savedInstanceState.getParcelable("primaryTenant");
                secondaryTenants = savedInstanceState.getParcelableArrayList("secondaryTenants");
            } else {
                getTenants();
            }
        } else {
            //If new
            Bundle bundle = getIntent().getExtras();
            //Get apartment item
            int apartmentID = bundle.getInt("apartmentID");
            this.apartment = dataMethods.getCachedApartmentByApartmentID(apartmentID);
            //Get other pics
            if (apartment.getOtherPics() != null) {
                otherPics = apartment.getOtherPics();
            } else {
                otherPics = new ArrayList<>();
            }
            //Get main pic
            if (apartment.getMainPic() != null) {
                mainPic = apartment.getMainPic();
            }
            //Get all tenants
            Pair<Tenant, ArrayList<Tenant>> tenants = dataMethods.getCachedPrimaryAndSecondaryTenantsByApartmentID(apartmentID);
            this.primaryTenant = tenants.first;
            this.secondaryTenants = tenants.second;
        }
        street1TV = findViewById(R.id.apartmentViewStreet1TextView);
        street2TV = findViewById(R.id.apartmentViewStreet2TextView);
        cityTV = findViewById(R.id.apartmentViewCityTextView);
        stateTV = findViewById(R.id.apartmentViewStateTextView);
        zipTV = findViewById(R.id.apartmentViewZipTextView);
        descriptionTV = findViewById(R.id.apartmentViewDescriptionTextView);
        notesTV = findViewById(R.id.apartmentViewNotesTextView);
        primaryTenantFirstNameTV = findViewById(R.id.apartmentViewPrimaryTenantFirstNameTV);
        primaryTenantLastNAmeTV = findViewById(R.id.apartmentViewPrimaryTenantLastNameTV);
        primaryTenantDisplayTV = findViewById(R.id.apartmentViewPrimaryTenantDisplayTV);
        secondaryTenantsTV = findViewById(R.id.apartmentViewSecondaryTenantsTV);
        leaseStatusTV = findViewById(R.id.apartmentViewRentalLeaseTV);
        leaseStartTV = findViewById(R.id.apartmentViewLeaseStartTextView);
        leaseEndTV = findViewById(R.id.apartmentViewLeaseEndTextView);
        leaseHyphenTV = findViewById(R.id.apartmentViewLeaseHyphenTV);

        mainPicIV = findViewById(R.id.apartmentViewMainPicIV);

        primaryTenantLL = findViewById(R.id.apartmentViewPrimaryTenantLL);
        secondaryTenantsLL = findViewById(R.id.apartmentViewSecondaryTenantsLL);

        editLeaseBtn = findViewById(R.id.apartmentViewEditLeaseBtn);
        editLeaseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ApartmentViewActivity.this, NewLeaseFormActivity.class);
                //Uses filtered results to match what is on screen
                intent.putExtra("apartment", apartment);
                startActivityForResult(intent, MainActivity.REQUEST_NEW_LEASE_FORM);
            }
        });

        fillTextViews();
        if (mainPic != null) {
            Glide.with(this).load(mainPic).into(mainPicIV);
        }
        mainPicIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mainPic != null) {
                    ImageViewDialog ivd = new ImageViewDialog(ApartmentViewActivity.this, mainPic);
                    ivd.show();
                }
            }
        });
        mainPicIV.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (mainPic != null) {
                    PopupMenu popup = new PopupMenu(ApartmentViewActivity.this, view);
                    MenuInflater inflater = popup.getMenuInflater();
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {

                                case R.id.removePic:
                                    databaseHandler.removeApartmentMainPic(apartment);
                                    mainPicIV.setImageDrawable(getResources().getDrawable(R.drawable.blank_home_pic));
                                    mainPic = null;
                                    apartment.setMainPic(null);
                                    ApartmentListFragment.apartmentListAdapterNeedsRefreshed = true;
                                    return true;

                                case R.id.changePic:
                                    ActivityCompat.requestPermissions(
                                            ApartmentViewActivity.this,
                                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                            MainActivity.REQUEST_GALLERY_FOR_MAIN_PIC
                                    );
                                    return true;

                                default:
                                    return false;
                            }
                        }
                    });
                    inflater.inflate(R.menu.picture_long_click_menu, popup.getMenu());
                    popup.show();
                } else {
                    PopupMenu popup = new PopupMenu(ApartmentViewActivity.this, view);
                    MenuInflater inflater = popup.getMenuInflater();
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {

                                case R.id.changePic:
                                    ActivityCompat.requestPermissions(
                                            ApartmentViewActivity.this,
                                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                            MainActivity.REQUEST_GALLERY_FOR_MAIN_PIC
                                    );
                                    return true;

                                default:
                                    return false;
                            }
                        }
                    });
                    inflater.inflate(R.menu.picture_long_click_no_pic_menu, popup.getMenu());
                    popup.show();
                }
                return true;
            }
        });
        setupBasicToolbar();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        adapter = new RecyclerViewAdapter(apartment.getOtherPics(), this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
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
                Intent intent = new Intent(this, NewApartmentFormActivity.class);
                intent.putExtra("apartmentToEdit", apartment);
                startActivityForResult(intent, MainActivity.REQUEST_NEW_APARTMENT_FORM);
                return true;

            case R.id.editMainPic:
                ActivityCompat.requestPermissions(
                        ApartmentViewActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MainActivity.REQUEST_GALLERY_FOR_MAIN_PIC
                );
                return true;

            case R.id.editotherPics:
                ActivityCompat.requestPermissions(
                        ApartmentViewActivity.this,
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MainActivity.REQUEST_GALLERY_FOR_MAIN_PIC) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, MainActivity.REQUEST_GALLERY_FOR_MAIN_PIC);
            } else {
                Toast.makeText(this, "You don't have permission to access file location", Toast.LENGTH_SHORT).show();
            }
            return;
        } else if (requestCode == MainActivity.REQUEST_GALLERY_FOR_OTHER_PICS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, MainActivity.REQUEST_GALLERY_FOR_OTHER_PICS);
            } else {
                Toast.makeText(this, "You don't have permission to access file location", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Uses apartment form to edit data
        if (requestCode == MainActivity.REQUEST_NEW_APARTMENT_FORM) {
            //If successful(not cancelled, passed validation)
            if (resultCode == RESULT_OK) {
                //Re-query cached apartment array to update cache and refresh current textViews to display new data. Re-query to sort list

                int apartmentID = data.getIntExtra("editedApartmentID", 0);
                this.apartment = dataMethods.getCachedApartmentByApartmentID(apartmentID);
                fillTextViews();
                ApartmentListFragment.apartmentListAdapterNeedsRefreshed = true;
            }
        }
        if (requestCode == MainActivity.REQUEST_GALLERY_FOR_MAIN_PIC) {
            if (resultCode == RESULT_OK && data != null) {
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);

                String filePath = cursor.getString(columnIndex);
                //file path of captured image
                cursor.close();
                Glide.with(this).load(filePath).into(mainPicIV);
                this.apartment.setMainPic(filePath);
                this.mainPic = filePath;
                databaseHandler.changeApartmentMainPic(this.apartment);
                //MainActivity.apartmentList = databaseHandler.getUsersApartments(MainActivity.user);
                ApartmentListFragment.apartmentListAdapterNeedsRefreshed = true;
            }
        }
        if (requestCode == MainActivity.REQUEST_GALLERY_FOR_OTHER_PICS) {
            if (resultCode == RESULT_OK && data != null) {
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);

                String filePath = cursor.getString(columnIndex);
                //file path of captured image
                File f = new File(filePath);
                String filename = f.getName();

                cursor.close();
                this.apartment.addOtherPic(filePath);
                databaseHandler.addApartmentOtherPic(apartment, filePath, MainActivity.user);
                //MainActivity.apartmentList = databaseHandler.getUsersApartments(MainActivity.user);
                //ApartmentListFragment.apartmentListAdapterNeedsRefreshed = true;
                adapter.notifyDataSetChanged();
            }
        }
        if (requestCode == MainActivity.REQUEST_NEW_LEASE_FORM) {
            if (resultCode == RESULT_OK) {
                int apartmentID = data.getIntExtra("updatedApartmentID", 0);
                //int primaryTenantID = data.getParcelableExtra("updatedPrimaryTenantID");
                this.apartment = dataMethods.getCachedApartmentByApartmentID(apartmentID);
                Pair<Tenant, ArrayList<Tenant>> tenants = dataMethods.getCachedPrimaryAndSecondaryTenantsByApartmentID(apartmentID);
                this.primaryTenant = tenants.first;
                this.secondaryTenants = tenants.second;
                //this.secondaryTenants = data.getParcelableArrayListExtra("updatedSecondaryTenants");
                fillTextViews();
                ApartmentListFragment.apartmentListAdapterNeedsRefreshed = true;
            }
        }
    }

    private void fillTextViews() {
        street1TV.setText(apartment.getStreet1());
        if (apartment.getStreet2().equals("")) {
            street2TV.setVisibility(View.GONE);
        } else {
            street2TV.setText(apartment.getStreet2());
        }
        String city = apartment.getCity();
        //If city not empty, add comma
        if (!apartment.getCity().equals("")) {
            city += ",";
        }
        cityTV.setText(city);
        stateTV.setText(apartment.getState());
        zipTV.setText(apartment.getZip());
        //tenantStatusTV.setText();
        descriptionTV.setText(apartment.getDescription());
        notesTV.setText(apartment.getNotes());
        if (primaryTenant != null) {
            editLeaseBtn.setText("Edit Lease");
            primaryTenantLL.setVisibility(View.VISIBLE);
            leaseStatusTV.setText("Lease : ");
            leaseHyphenTV.setText(" - ");
            primaryTenantFirstNameTV.setText(primaryTenant.getFirstName());
            primaryTenantLastNAmeTV.setText(primaryTenant.getLastName());
            SimpleDateFormat formatTo = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
            DateFormat formatFrom = new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy", Locale.US);
            try {
                Date startDate = formatFrom.parse(primaryTenant.getLeaseStart());
                Date endDate = formatFrom.parse(primaryTenant.getLeaseEnd());
                leaseStartTV.setText(formatTo.format(startDate));
                leaseEndTV.setText(formatTo.format(endDate));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (!secondaryTenants.isEmpty()) {
                secondaryTenantsLL.setVisibility(View.VISIBLE);
                secondaryTenantsTV.setText("");
                for (int i = 0; i < secondaryTenants.size(); i++) {
                    secondaryTenantsTV.append(secondaryTenants.get(i).getFirstName());
                    secondaryTenantsTV.append(" ");
                    secondaryTenantsTV.append(secondaryTenants.get(i).getLastName());
                    if (i != secondaryTenants.size() - 1) {
                        secondaryTenantsTV.append("\n");
                    }
                }
            } else {
                secondaryTenantsLL.setVisibility(View.GONE);
            }
        } else {
            editLeaseBtn.setText("Create Lease");
            leaseStatusTV.setText("Vacant");
            leaseStartTV.setText("");
            leaseEndTV.setText("");
            leaseHyphenTV.setText("");
            primaryTenantLL.setVisibility(View.GONE);
            secondaryTenantsLL.setVisibility(View.GONE);
        }
    }

    private void getTenants() {
        if (apartment.isRented()) {
            for (int i = 0; i < MainActivity.tenantList.size(); i++) {
                if (MainActivity.tenantList.get(i).getApartmentID() == apartment.getId()) {
                    if (MainActivity.tenantList.get(i).getIsPrimary()) {
                        primaryTenant = MainActivity.tenantList.get(i);
                    } else {
                        secondaryTenants.add(MainActivity.tenantList.get(i));
                    }
                }
            }
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
                    primaryTenant.setApartmentID(0);
                    primaryTenant.setLeaseStart("");
                    primaryTenant.setLeaseEnd("");
                    databaseHandler.editTenant(primaryTenant);
                    for (int x = 0; x < secondaryTenants.size(); x++) {
                        secondaryTenants.get(x).setApartmentID(0);
                        secondaryTenants.get(x).setLeaseStart("");
                        secondaryTenants.get(x).setLeaseEnd("");
                        databaseHandler.editTenant(secondaryTenants.get(x));
                    }
                    apartment.setRented(false);
                    dataMethods.sortMainTenantArray();
                }
                MainActivity.apartmentList.remove(apartment);
                TenantListFragment.tenantListAdapterNeedsRefreshed = true;
                ApartmentListFragment.apartmentListAdapterNeedsRefreshed = true;
                ApartmentViewActivity.this.finish();
            }
        });

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //Save the fragment's instance
        super.onSaveInstanceState(outState);
        outState.putParcelable("apartment", apartment);
        if (otherPics != null) {
            outState.putInt("otherPicsSize", otherPics.size());
            for (int i = 0; i < otherPics.size(); i++) {
                outState.putString("otherPics" + i, otherPics.get(i));
            }
        }
        if (mainPic != null) {
            outState.putString("mainPic", mainPic);
        }
        if (primaryTenant != null) {
            outState.putParcelable("primaryTenant", primaryTenant);
        }
        if (secondaryTenants != null) {
            outState.putParcelableArrayList("secondaryTenants", secondaryTenants);
        }
    }
}