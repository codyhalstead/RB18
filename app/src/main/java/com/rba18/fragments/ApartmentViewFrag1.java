package com.rba18.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.rba18.BuildConfig;
import com.rba18.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.rba18.activities.MainActivity;
import com.rba18.adapters.OtherPicsAdapter;
import com.rba18.helpers.ApartmentTenantViewModel;
import com.rba18.helpers.DateAndCurrencyDisplayer;
import com.rba18.helpers.MainArrayDataMethods;
import com.rba18.model.Apartment;
import com.rba18.model.Lease;
import com.rba18.model.Tenant;
import com.rba18.sqlite.DatabaseHandler;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static android.app.Activity.RESULT_OK;
import static android.support.constraint.Constraints.TAG;

public class ApartmentViewFrag1 extends Fragment {
    Apartment apartment;
    TextView addressTV, descriptionTV, notesTV, primaryTenantTV, activeLeaseDurationTV, otherTenantsTV,
            primaryTenantLabelTV, activeLeaseDurationLabelTV, otherTenantsLabelTV, activeLeaseHeaderTV;
    LinearLayout adViewLL;
    TableRow durationTR, primarytenantTR, otherTenantsTR;
    ImageView mainPicIV;
    Button callPrimaryTenantBtn, smsPrimaryTenantBtn, emailPrimaryTenantBtn, emailAllBtn;
    DatabaseHandler databaseHandler;
    Tenant primaryTenant;
    ArrayList<Tenant> secondaryTenants;
    String mainPic;
    MainArrayDataMethods dataMethods;
    ArrayList<Lease> activeLeases;
    RecyclerView recyclerView;
    OtherPicsAdapter adapter;
    ArrayList<String> otherPics;
    String otherPicToDelete;
    private SharedPreferences preferences;
    private OnPicDataChangedListener mCallback;
    private AlertDialog dialog;
    private PopupMenu popup;
    private String cameraImageFilePath;
    AdView adview;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.databaseHandler = new DatabaseHandler(getContext());
        this.preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        dataMethods = new MainArrayDataMethods();
        secondaryTenants = new ArrayList<>();
        //if recreated
        if (savedInstanceState != null) {
            this.apartment = ViewModelProviders.of(getActivity()).get(ApartmentTenantViewModel.class).getApartment().getValue();
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
            }
            cameraImageFilePath = savedInstanceState.getString("camera_image_file_path");
        } else {
            //If new
            this.apartment = ViewModelProviders.of(getActivity()).get(ApartmentTenantViewModel.class).getApartment().getValue();
            this.primaryTenant = ViewModelProviders.of(getActivity()).get(ApartmentTenantViewModel.class).getPrimaryTenant().getValue();
            this.secondaryTenants = ViewModelProviders.of(getActivity()).get(ApartmentTenantViewModel.class).getSecondaryTenants().getValue();
            if (apartment.getOtherPics() != null) {
                otherPics = apartment.getOtherPics();
            } else {
                otherPics = new ArrayList<>();
            }
            //Get main pic
            if (apartment.getMainPic() != null) {
                mainPic = apartment.getMainPic();
            }
        }
        otherPicToDelete = "";
        Date today = Calendar.getInstance().getTime();
        activeLeases = new ArrayList<>();
        for (int i = 0; i < ViewModelProviders.of(getActivity()).get(ApartmentTenantViewModel.class).getLeaseArray().getValue().size(); i++) {
            if (ViewModelProviders.of(getActivity()).get(ApartmentTenantViewModel.class).getLeaseArray().getValue().get(i).getLeaseStart().before(today) &&
                    ViewModelProviders.of(getActivity()).get(ApartmentTenantViewModel.class).getLeaseArray().getValue().get(i).getLeaseEnd().after(today)) {
                activeLeases.add(ViewModelProviders.of(getActivity()).get(ApartmentTenantViewModel.class).getLeaseArray().getValue().get(i));
            }
        }
        if (activeLeases.size() == 1) {
            primaryTenant = databaseHandler.getTenantByID(activeLeases.get(0).getPrimaryTenantID(), MainActivity.user);
        }
        ViewModelProviders.of(getActivity()).get(ApartmentTenantViewModel.class).getLease().observe(this, new Observer<Lease>() {
            @Override
            public void onChanged(@Nullable Lease changedLease) {
            }
        });
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnPicDataChangedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnPicDataChangedListener");
        }
    }

    public void updateApartmentData(Apartment apartment) {
        this.apartment = apartment;
        fillTextViews();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (dialog != null) {
            dialog.dismiss();
        }
        if (popup != null) {
            popup.dismiss();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.apartment_view_fragment_one, container, false);
        recyclerView = rootView.findViewById(R.id.recyclerView);

        addressTV = rootView.findViewById(R.id.apartmentViewAddressTextView);
        descriptionTV = rootView.findViewById(R.id.apartmentViewDescriptionTextView);
        notesTV = rootView.findViewById(R.id.apartmentViewNotesTextView);
        primaryTenantTV = rootView.findViewById(R.id.apartmentViewActiveLeasePrimaryTenantTextView);
        primaryTenantLabelTV = rootView.findViewById(R.id.apartmentViewActiveLeasePrimaryTenantLabelTextView);
        activeLeaseDurationTV = rootView.findViewById(R.id.apartmentViewActiveLeaseDurationTextView);
        activeLeaseDurationLabelTV = rootView.findViewById(R.id.apartmentViewActiveLeaseDurationLabelTextView);
        otherTenantsTV = rootView.findViewById(R.id.apartmentViewActiveLeaseOtherTenantsTextView);
        otherTenantsLabelTV = rootView.findViewById(R.id.apartmentViewActiveLeaseOtherTenantsLabelTextView);
        activeLeaseHeaderTV = rootView.findViewById(R.id.apartmentViewActiveLeaseHeaderTV);
        durationTR = rootView.findViewById(R.id.apartmentViewActiveLeaseDurationTR);
        primarytenantTR = rootView.findViewById(R.id.apartmentViewActiveLeasePrimaryTenantTR);
        otherTenantsTR = rootView.findViewById(R.id.apartmentViewActiveLeaseOtherTenantsTR);
        mainPicIV = rootView.findViewById(R.id.apartmentViewMainPicIV);
        adViewLL = rootView.findViewById(R.id.adViewLL);
        callPrimaryTenantBtn = rootView.findViewById(R.id.apartmentViewCallTenantBtn);
        callPrimaryTenantBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callPrimaryTenant();
            }
        });
        smsPrimaryTenantBtn = rootView.findViewById(R.id.apartmentViewSMSTenantBtn);
        smsPrimaryTenantBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                smsPrimaryTenant();
            }
        });
        emailPrimaryTenantBtn = rootView.findViewById(R.id.apartmentViewEmailTenantBtn);
        emailPrimaryTenantBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                emailPrimaryTenant();
            }
        });
        emailAllBtn = rootView.findViewById(R.id.apartmentViewEmailAllTenantsBtn);
        emailAllBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                emailAllTenants();
            }
        });
        if (BuildConfig.FLAVOR.equals("free")) {
            adview = rootView.findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
            adview.loadAd(adRequest);
        } else {
            adViewLL.setVisibility(View.GONE);
        }
        fillTextViews();
        setUpTenantContactButtons();
        return rootView;
    }

    public void setUpTenantContactButtons() {
        if (apartment.isRented()) {
            if(activeLeases.size() == 1) {
                callPrimaryTenantBtn.setVisibility(View.VISIBLE);
                smsPrimaryTenantBtn.setVisibility(View.VISIBLE);
                emailPrimaryTenantBtn.setVisibility(View.VISIBLE);
                emailAllBtn.setVisibility(View.VISIBLE);
            } else {
                callPrimaryTenantBtn.setVisibility(View.GONE);
                smsPrimaryTenantBtn.setVisibility(View.GONE);
                emailPrimaryTenantBtn.setVisibility(View.GONE);
                emailAllBtn.setVisibility(View.GONE);
            }
        } else {
            callPrimaryTenantBtn.setVisibility(View.GONE);
            smsPrimaryTenantBtn.setVisibility(View.GONE);
            emailPrimaryTenantBtn.setVisibility(View.GONE);
            emailAllBtn.setVisibility(View.GONE);
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fillTextViews();
        if (mainPic != null) {
            Glide.with(this).load(apartment.getMainPic()).placeholder(R.drawable.blank_home_pic)
                    .override(200, 200).centerCrop().into(mainPicIV);
        } else {
            Glide.with(this).load(R.drawable.blank_home_pic).override(200, 200).centerCrop().into(mainPicIV);
        }
        mainPicIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mainPic != null) {
                    if (new File(mainPic).exists()) {
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                            Intent intent = new Intent();
                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            intent.setAction(Intent.ACTION_VIEW);
                            intent.setDataAndType(Uri.fromFile(new File(mainPic)), "image/*");
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent();
                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            intent.setAction(Intent.ACTION_VIEW);
                            Uri photoUri = FileProvider.getUriForFile(getActivity(), BuildConfig.APPLICATION_ID + ".helpers.fileprovider", new File(mainPic));
                            intent.setData(photoUri);
                            startActivity(intent);
                        }
                    } else {
                        Toast.makeText(getContext(), R.string.could_not_find_file, Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
        mainPicIV.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (mainPic != null) {
                    popup = new PopupMenu(getActivity(), view);
                    MenuInflater inflater = popup.getMenuInflater();
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {

                                case R.id.removePic:
                                    ActivityCompat.requestPermissions(
                                            getActivity(),
                                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                            MainActivity.REQUEST_IMAGE_DELETE_PERMISSION
                                    );
                                    return true;

                                case R.id.changePic:
                                    launchCameraOrGalleryDialog();
                                    return true;

                                default:
                                    return false;
                            }
                        }
                    });
                    inflater.inflate(R.menu.picture_long_click_menu, popup.getMenu());
                    popup.show();
                } else {
                    popup = new PopupMenu(getContext(), view);
                    MenuInflater inflater = popup.getMenuInflater();
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {

                                case R.id.changePic:
                                    launchCameraOrGalleryDialog();
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
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        adapter = new OtherPicsAdapter(apartment.getOtherPics(), getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            // Setting on Touch Listener for handling the touch inside ScrollView
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Disallow the touch request for parent scroll on touch of child view

                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }

        });
        adapter.setOnDataChangedListener(new OtherPicsAdapter.OnDataChangedListener() {
            @Override
            public void onPicSelectedToBeRemoved(String removedPicPath) {
                otherPicToDelete = removedPicPath;
                ActivityCompat.requestPermissions(
                        getActivity(),
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MainActivity.REQUEST_ADAPTER_IMAGE_DELETE_PERMISSION
                );
            }
        });
        hideOtherPicsRecyclerViewIfEmpty();
    }

    public interface OnPicDataChangedListener {
        void onPicDataChanged();
    }

    private void launchCameraOrGalleryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(R.string.camera_or_gallery);
        builder.setPositiveButton(R.string.gallery,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        ActivityCompat.requestPermissions(
                                getActivity(),
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                MainActivity.REQUEST_GALLERY_FOR_MAIN_PIC
                        );
                    }
                });
        builder.setNegativeButton(R.string.camera,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        ActivityCompat.requestPermissions(
                                getActivity(),
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                                MainActivity.REQUEST_CAMERA_FOR_MAIN_PIC
                        );
                    }
                });
        dialog = builder.create();
        dialog.show();
    }

    public void updateCameraUri(String uri) {
        cameraImageFilePath = uri;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MainActivity.REQUEST_IMAGE_DELETE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                databaseHandler.removeApartmentMainPic(apartment);
                Glide.with(getContext()).load(R.drawable.blank_home_pic).override(200, 200).centerCrop().into(mainPicIV);
                new File(mainPic).delete();
                mainPic = null;
                apartment.setMainPic(null);
                mCallback.onPicDataChanged();
            } else {
                Toast.makeText(getContext(), R.string.permission_picture_denied, Toast.LENGTH_SHORT).show();
            }
            return;
        } else if (requestCode == MainActivity.REQUEST_ADAPTER_IMAGE_DELETE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                apartment.getOtherPics().remove(otherPicToDelete);
                databaseHandler.removeApartmentOtherPic(otherPicToDelete, MainActivity.user);
                File picToDelete = new File(otherPicToDelete);
                if (picToDelete.exists()) {
                    picToDelete.delete();
                }
                hideOtherPicsRecyclerViewIfEmpty();
                adapter.notifyDataSetChanged();
                mCallback.onPicDataChanged();
                otherPicToDelete = "";
            } else {
                Toast.makeText(getContext(), R.string.permission_picture_denied, Toast.LENGTH_SHORT).show();
                otherPicToDelete = "";
            }
            return;
        } else if (requestCode == MainActivity.REQUEST_CAMERA_FOR_MAIN_PIC) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {
                Toast.makeText(getActivity(), R.string.permission_picture_denied, Toast.LENGTH_SHORT).show();
            }
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
            }
        } else if (requestCode == MainActivity.REQUEST_CAMERA_FOR_MAIN_PIC) {
            if (resultCode == RESULT_OK) {
                apartment.setMainPic(cameraImageFilePath);
                this.apartment = ViewModelProviders.of(getActivity()).get(ApartmentTenantViewModel.class).getApartment().getValue();
                updateMainPicIV(apartment.getMainPic());
                //fillTextViews();
            }
        } else if (requestCode == MainActivity.REQUEST_CAMERA_FOR_OTHER_PICS) {
            if (resultCode == RESULT_OK) {
                this.apartment = ViewModelProviders.of(getActivity()).get(ApartmentTenantViewModel.class).getApartment().getValue();
                otherPics = apartment.getOtherPics();
                adapter.updateResults(otherPics);
                hideOtherPicsRecyclerViewIfEmpty();
            }

        } else if (requestCode == MainActivity.REQUEST_GALLERY_FOR_MAIN_PIC) {
            if (resultCode == RESULT_OK && data != null) {
                this.apartment = ViewModelProviders.of(getActivity()).get(ApartmentTenantViewModel.class).getApartment().getValue();
                updateMainPicIV(apartment.getMainPic());
            }
        } else if (requestCode == MainActivity.REQUEST_GALLERY_FOR_OTHER_PICS) {
            if (resultCode == RESULT_OK && data != null) {
                this.apartment = ViewModelProviders.of(getActivity()).get(ApartmentTenantViewModel.class).getApartment().getValue();
                otherPics = apartment.getOtherPics();
                adapter.updateResults(otherPics);
                hideOtherPicsRecyclerViewIfEmpty();
            }
        }
    }

    public void refreshPictureAdapter() {
        this.adapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
        this.apartment = ViewModelProviders.of(getActivity()).get(ApartmentTenantViewModel.class).getApartment().getValue();
        updateMainPicIV(apartment.getMainPic());
    }

    private void fillTextViews() {
        addressTV.setText(apartment.getFullAddressString());
        descriptionTV.setText(apartment.getDescription());
        notesTV.setText(apartment.getNotes());
        if (!apartment.isRented()) {
            activeLeaseDurationTV.setVisibility(View.GONE);
            activeLeaseDurationLabelTV.setVisibility(View.GONE);
            primaryTenantTV.setVisibility(View.GONE);
            primaryTenantLabelTV.setVisibility(View.GONE);
            otherTenantsTV.setVisibility(View.GONE);
            otherTenantsLabelTV.setVisibility(View.GONE);
            activeLeaseHeaderTV.setVisibility(View.GONE);
            durationTR.setVisibility(View.GONE);
            primarytenantTR.setVisibility(View.GONE);
            otherTenantsTR.setVisibility(View.GONE);
        } else {
            activeLeaseHeaderTV.setVisibility(View.VISIBLE);
            if (activeLeases.size() > 1) {
                activeLeaseDurationTV.setVisibility(View.VISIBLE);
                activeLeaseDurationLabelTV.setVisibility(View.VISIBLE);
                activeLeaseDurationTV.setText(R.string.multiple_active_leases);
                primaryTenantTV.setVisibility(View.GONE);
                primaryTenantLabelTV.setVisibility(View.GONE);
                otherTenantsTV.setVisibility(View.GONE);
                otherTenantsLabelTV.setVisibility(View.GONE);
                durationTR.setVisibility(View.VISIBLE);
                primarytenantTR.setVisibility(View.GONE);
                otherTenantsTR.setVisibility(View.GONE);
            } else if (activeLeases.size() == 1) {
                Lease currentLease = activeLeases.get(0);
                primaryTenantTV.setVisibility(View.VISIBLE);
                primaryTenantLabelTV.setVisibility(View.VISIBLE);
                activeLeaseDurationTV.setVisibility(View.VISIBLE);
                activeLeaseDurationLabelTV.setVisibility(View.VISIBLE);
                otherTenantsTV.setVisibility(View.VISIBLE);
                otherTenantsLabelTV.setVisibility(View.VISIBLE);
                durationTR.setVisibility(View.VISIBLE);
                primarytenantTR.setVisibility(View.VISIBLE);
                otherTenantsTR.setVisibility(View.VISIBLE);
                if (primaryTenant != null) {
                    primaryTenantTV.setText(primaryTenant.getFirstAndLastNameString());
                } else {
                    primaryTenantTV.setText(R.string.error_loading_primary_tenant);
                }
                if (currentLease.getLeaseStart() != null && currentLease.getLeaseEnd() != null) {
                    int dateFormatCode = preferences.getInt("dateFormat", DateAndCurrencyDisplayer.DATE_MMDDYYYY);
                    activeLeaseDurationTV.setText(currentLease.getStartAndEndDatesString(dateFormatCode));
                } else {
                    activeLeaseDurationTV.setText(R.string.error_leading_lease);
                }
                if (!secondaryTenants.isEmpty()) {
                    otherTenantsTV.setText("");
                    for (int i = 0; i < secondaryTenants.size(); i++) {
                        otherTenantsTV.append(secondaryTenants.get(i).getFirstAndLastNameString());
                        if (i != secondaryTenants.size() - 1) {
                            otherTenantsTV.append("\n");
                        }
                    }
                } else {
                    otherTenantsTV.setText(R.string.na);
                }
            }
        }
    }

    public void updateMainPicIV(String picFileName) {
        this.mainPic = picFileName;
        Glide.with(this).load(mainPic).placeholder(R.drawable.blank_home_pic)
                .override(200, 200).centerCrop().into(mainPicIV);
    }

    public void hideOtherPicsRecyclerViewIfEmpty() {
        if (adapter.getItemCount() > 0) {
            recyclerView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.GONE);
        }
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
        outState.putString("camera_image_file_path", cameraImageFilePath);
    }

    private void callPrimaryTenant() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE}, MainActivity.REQUEST_PHONE_CALL_PERMISSION);
        } else {
            if(!primaryTenant.getPhone().equals("")) {
                String phoneNumber = primaryTenant.getPhone();
                phoneNumber.replaceAll("[\\s\\-()]", "");
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneNumber, null));
                startActivity(intent);
            }
        }
    }

    private void smsPrimaryTenant() {
        if (!primaryTenant.getPhone().equals("")) {
            String phoneNumber = primaryTenant.getPhone();
            phoneNumber.replaceAll("[\\s\\-()]", "");
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", phoneNumber, null)));
        }
    }

    private void emailPrimaryTenant() {
        if (primaryTenant.getEmail() != null) {
            if (!primaryTenant.getEmail().equals("")) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{primaryTenant.getEmail()});
                intent.setType("application/octet-stream");
                startActivityForResult(Intent.createChooser(intent, getContext().getResources().getString(R.string.send_email)), MainActivity.REQUEST_EMAIL);
            }
        }
    }

    private void emailAllTenants() {
        if (secondaryTenants.isEmpty()) {
            emailPrimaryTenant();
        } else {
            ArrayList<String> emails = new ArrayList<>();
            if (primaryTenant.getEmail() != null) {
                if (!primaryTenant.getEmail().equals("")) {
                    emails.add(primaryTenant.getEmail());
                }
            }
            for (int x = 0; x < secondaryTenants.size(); x++) {
                if (secondaryTenants.get(x).getEmail() != null) {
                    if (!secondaryTenants.get(x).getEmail().equals("")) {
                        emails.add(secondaryTenants.get(x).getEmail());
                    }
                }
            }
            if (!emails.isEmpty()) {
                String[] emailArray = new String[secondaryTenants.size() + 1];
                for (int y = 0; y < emails.size(); y++) {
                    emailArray[y] = emails.get(y);
                }
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_EMAIL, emailArray);
                intent.setType("application/octet-stream");
                startActivityForResult(Intent.createChooser(intent, getContext().getResources().getString(R.string.send_email)), MainActivity.REQUEST_EMAIL);

            }
        }
    }
}
