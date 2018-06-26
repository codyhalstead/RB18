package com.rentbud.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.cody.rentbud.R;
import com.rentbud.activities.ApartmentViewActivity;
import com.rentbud.activities.BaseActivity;
import com.rentbud.activities.MainActivity;
import com.rentbud.activities.NewApartmentWizard;
import com.rentbud.activities.NewLeaseFormActivity;
import com.rentbud.adapters.RecyclerViewAdapter;
import com.rentbud.helpers.ImageViewDialog;
import com.rentbud.helpers.MainArrayDataMethods;
import com.rentbud.model.Apartment;
import com.rentbud.model.Lease;
import com.rentbud.model.Tenant;
import com.rentbud.sqlite.DatabaseHandler;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;

public class ApartmentViewFrag1 extends Fragment {
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
    Lease currentLease;

    RecyclerView recyclerView;
    RecyclerViewAdapter adapter;
    ArrayList<String> otherPics;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setupUserAppTheme(MainActivity.curThemeChoice);

        this.databaseHandler = new DatabaseHandler(getContext());
        dataMethods = new MainArrayDataMethods();
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
            if (savedInstanceState.getParcelable("currentLease") != null) {
                currentLease = savedInstanceState.getParcelable("currentLease");
            }
            if (savedInstanceState.getParcelable("primaryTenant") != null && savedInstanceState.getParcelableArrayList("secondaryTenants") != null) {
                primaryTenant = savedInstanceState.getParcelable("primaryTenant");
                secondaryTenants = savedInstanceState.getParcelableArrayList("secondaryTenants");
            } else {
                getTenants();
            }
        } else {
            //If new
            Bundle bundle = getArguments();
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
            currentLease = dataMethods.getCachedActiveLeaseByApartmentID(apartmentID);
            //Get all tenants
            Pair<Tenant, ArrayList<Tenant>> tenants = dataMethods.getCachedPrimaryAndSecondaryTenantsByLease(currentLease);
            this.primaryTenant = tenants.first;
            this.secondaryTenants = tenants.second;
        }
        getActivity().setTitle("Apartment View");
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.apartment_view_fragment_one, container, false);
        recyclerView = rootView.findViewById(R.id.recyclerView);

        street1TV = rootView.findViewById(R.id.apartmentViewStreet1TextView);
        street2TV = rootView.findViewById(R.id.apartmentViewStreet2TextView);
        cityTV = rootView.findViewById(R.id.apartmentViewCityTextView);
        stateTV = rootView.findViewById(R.id.apartmentViewStateTextView);
        zipTV = rootView.findViewById(R.id.apartmentViewZipTextView);
        descriptionTV = rootView.findViewById(R.id.apartmentViewDescriptionTextView);
        notesTV = rootView.findViewById(R.id.apartmentViewNotesTextView);
        primaryTenantFirstNameTV = rootView.findViewById(R.id.apartmentViewPrimaryTenantFirstNameTV);
        primaryTenantLastNAmeTV = rootView.findViewById(R.id.apartmentViewPrimaryTenantLastNameTV);
        primaryTenantDisplayTV = rootView.findViewById(R.id.apartmentViewPrimaryTenantDisplayTV);
        secondaryTenantsTV = rootView.findViewById(R.id.apartmentViewSecondaryTenantsTV);
        leaseStatusTV = rootView.findViewById(R.id.apartmentViewRentalLeaseTV);
        leaseStartTV = rootView.findViewById(R.id.apartmentViewLeaseStartTextView);
        leaseEndTV = rootView.findViewById(R.id.apartmentViewLeaseEndTextView);
        leaseHyphenTV = rootView.findViewById(R.id.apartmentViewLeaseHyphenTV);

        mainPicIV = rootView.findViewById(R.id.apartmentViewMainPicIV);

        primaryTenantLL = rootView.findViewById(R.id.apartmentViewPrimaryTenantLL);
        secondaryTenantsLL = rootView.findViewById(R.id.apartmentViewSecondaryTenantsLL);

        editLeaseBtn = rootView.findViewById(R.id.apartmentViewEditLeaseBtn);
        fillTextViews();
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fillTextViews();
        if (mainPic != null) {
            Glide.with(this).load(mainPic).into(mainPicIV);
        }
        mainPicIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mainPic != null) {
                    ImageViewDialog ivd = new ImageViewDialog(getContext(), mainPic);
                    ivd.show();
                }
            }
        });
        mainPicIV.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (mainPic != null) {
                    PopupMenu popup = new PopupMenu(getActivity(), view);
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
                                            getActivity(),
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
                    PopupMenu popup = new PopupMenu(getContext(), view);
                    MenuInflater inflater = popup.getMenuInflater();
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {

                                case R.id.changePic:
                                    ActivityCompat.requestPermissions(
                                            getActivity(),
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
        //setupBasicToolbar();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        adapter = new RecyclerViewAdapter(apartment.getOtherPics(), getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MainActivity.REQUEST_GALLERY_FOR_MAIN_PIC) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, MainActivity.REQUEST_GALLERY_FOR_MAIN_PIC);
            } else {
                Toast.makeText(getContext(), "You don't have permission to access file location", Toast.LENGTH_SHORT).show();
            }
            return;
        } else if (requestCode == MainActivity.REQUEST_GALLERY_FOR_OTHER_PICS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, MainActivity.REQUEST_GALLERY_FOR_OTHER_PICS);
            } else {
                Toast.makeText(getContext(), "You don't have permission to access file location", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
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

                Cursor cursor = getActivity().getContentResolver().query(selectedImage,
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
                //MainActivity5.apartmentList = databaseHandler.getUsersApartments(MainActivity5.user);
                ApartmentListFragment.apartmentListAdapterNeedsRefreshed = true;
            }
        }
        if (requestCode == MainActivity.REQUEST_GALLERY_FOR_OTHER_PICS) {
            if (resultCode == RESULT_OK && data != null) {
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                Cursor cursor = getActivity().getContentResolver().query(selectedImage,
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
                //MainActivity5.apartmentList = databaseHandler.getUsersApartments(MainActivity5.user);
                //ApartmentListFragment.apartmentListAdapterNeedsRefreshed = true;
                adapter.notifyDataSetChanged();
            }
        }
        if (requestCode == MainActivity.REQUEST_NEW_LEASE_FORM) {
            if (resultCode == RESULT_OK) {
                //int apartmentID = data.getIntExtra("updatedApartmentID", 0);
                //int primaryTenantID = data.getParcelableExtra("updatedPrimaryTenantID");
                this.apartment = dataMethods.getCachedApartmentByApartmentID(apartment.getId());
                Pair<Tenant, ArrayList<Tenant>> tenants = dataMethods.getCachedPrimaryAndSecondaryTenantsByLease(currentLease); //TODO
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
        if (apartment.getStreet2() != null) {
            if (apartment.getStreet2().equals("")) {
                street2TV.setVisibility(View.GONE);
            } else {
                street2TV.setVisibility(View.VISIBLE);
            }
        } else {
            street2TV.setVisibility(View.GONE);
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

            SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
            if (currentLease != null) {
                leaseStartTV.setText(formatter.format(currentLease.getLeaseStart()));
                leaseEndTV.setText(formatter.format(currentLease.getLeaseEnd()));
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
            Pair<Tenant, ArrayList<Tenant>> tenants = dataMethods.getCachedPrimaryAndSecondaryTenantsByLease(currentLease);
            this.primaryTenant = tenants.first;
            this.secondaryTenants = tenants.second;
        }
    }

    public void showDeleteConfirmationAlertDialog() {
        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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
                    for (int x = 0; x < secondaryTenants.size(); x++) {
                        //    secondaryTenants.get(x).setApartmentID(0);
                        //    secondaryTenants.get(x).setLeaseStart(null);
                        //    secondaryTenants.get(x).setLeaseEnd(null);
                        //    databaseHandler.editTenant(secondaryTenants.get(x));
                    }
                    apartment.setRented(false);
                    dataMethods.sortMainTenantArray();
                }
                MainActivity.apartmentList.remove(apartment);
                TenantListFragment.tenantListAdapterNeedsRefreshed = true;
                ApartmentListFragment.apartmentListAdapterNeedsRefreshed = true;
                getActivity().finish();
            }
        });

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
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
        if (currentLease != null) {
            outState.putParcelable("currentLease", currentLease);
        }
    }
}
