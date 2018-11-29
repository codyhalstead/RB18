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

public class ApartmentViewFrag1 extends Fragment {
    private Apartment mApartment;
    private TextView mAddressTV, mDescriptionTV, mNotesTV, mPrimaryTenantTV, mActiveLeaseDurationTV, mOtherTenantsTV,
            mPrimaryTenantLabelTV, mActiveLeaseDurationLabelTV, mOtherTenantsLabelTV, mActiveLeaseHeaderTV;
    private TableRow mDurationTR, mPrimaryTenantTR, mOtherTenantsTR;
    private ImageView mMainPicIV;
    private Button mCallPrimaryTenantBtn, mSMSPrimaryTenantBtn, mEmailPrimaryTenantBtn, mEmailAllBtn;
    private DatabaseHandler mDatabaseHandler;
    private Tenant mPrimaryTenant;
    private ArrayList<Tenant> mSecondaryTenants;
    private String mMainPic, mOtherPicToDelete, mCameraImageFilePath;
    private MainArrayDataMethods mDataMethods;
    private ArrayList<Lease> mActiveLeases;
    private RecyclerView mRecyclerView;
    private OtherPicsAdapter mAdapter;
    private ArrayList<String> mOtherPics;
    private SharedPreferences mPreferences;
    private OnPicDataChangedListener mCallback;
    private AlertDialog mDialog;
    private PopupMenu mPopup;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDatabaseHandler = new DatabaseHandler(getContext());
        mPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        mDataMethods = new MainArrayDataMethods();
        mSecondaryTenants = new ArrayList<>();
        //if recreated
        if (savedInstanceState != null) {
            mApartment = ViewModelProviders.of(getActivity()).get(ApartmentTenantViewModel.class).getApartment().getValue();
            mOtherPics = new ArrayList<>();
            if (savedInstanceState.getInt("otherPicsSize") > 0) {
                for (int i = 0; i < savedInstanceState.getInt("otherPicsSize"); i++) {
                    mOtherPics.add(savedInstanceState.getString("mOtherPics" + i));
                }
            }
            if (savedInstanceState.getString("mMainPic") != null) {
                mMainPic = savedInstanceState.getString("mMainPic");
            }
            if (savedInstanceState.getParcelable("mPrimaryTenant") != null && savedInstanceState.getParcelableArrayList("mSecondaryTenants") != null) {
                mPrimaryTenant = savedInstanceState.getParcelable("mPrimaryTenant");
                mSecondaryTenants = savedInstanceState.getParcelableArrayList("mSecondaryTenants");
            }
            mCameraImageFilePath = savedInstanceState.getString("camera_image_file_path");
        } else {
            //If new
            mApartment = ViewModelProviders.of(getActivity()).get(ApartmentTenantViewModel.class).getApartment().getValue();
            mPrimaryTenant = ViewModelProviders.of(getActivity()).get(ApartmentTenantViewModel.class).getPrimaryTenant().getValue();
            mSecondaryTenants = ViewModelProviders.of(getActivity()).get(ApartmentTenantViewModel.class).getSecondaryTenants().getValue();
            if (mApartment.getOtherPics() != null) {
                mOtherPics = mApartment.getOtherPics();
            } else {
                mOtherPics = new ArrayList<>();
            }
            //Get main pic
            if (mApartment.getMainPic() != null) {
                mMainPic = mApartment.getMainPic();
            }
        }
        mOtherPicToDelete = "";
        Date today = Calendar.getInstance().getTime();
        mActiveLeases = new ArrayList<>();
        for (int i = 0; i < ViewModelProviders.of(getActivity()).get(ApartmentTenantViewModel.class).getLeaseArray().getValue().size(); i++) {
            if (ViewModelProviders.of(getActivity()).get(ApartmentTenantViewModel.class).getLeaseArray().getValue().get(i).getLeaseStart().before(today) &&
                    ViewModelProviders.of(getActivity()).get(ApartmentTenantViewModel.class).getLeaseArray().getValue().get(i).getLeaseEnd().after(today)) {
                mActiveLeases.add(ViewModelProviders.of(getActivity()).get(ApartmentTenantViewModel.class).getLeaseArray().getValue().get(i));
            }
        }
        if (mActiveLeases.size() == 1) {
            mPrimaryTenant = mDatabaseHandler.getTenantByID(mActiveLeases.get(0).getPrimaryTenantID(), MainActivity.sUser);
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
                    + " must implement listeners");
        }
    }

    public void updateApartmentData(Apartment apartment) {
        mApartment = apartment;
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
        if (mDialog != null) {
            mDialog.dismiss();
        }
        if (mPopup != null) {
            mPopup.dismiss();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.apartment_view_fragment_one, container, false);
        mRecyclerView = rootView.findViewById(R.id.recyclerView);

        mAddressTV = rootView.findViewById(R.id.apartmentViewAddressTextView);
        mDescriptionTV = rootView.findViewById(R.id.apartmentViewDescriptionTextView);
        mNotesTV = rootView.findViewById(R.id.apartmentViewNotesTextView);
        mPrimaryTenantTV = rootView.findViewById(R.id.apartmentViewActiveLeasePrimaryTenantTextView);
        mPrimaryTenantLabelTV = rootView.findViewById(R.id.apartmentViewActiveLeasePrimaryTenantLabelTextView);
        mActiveLeaseDurationTV = rootView.findViewById(R.id.apartmentViewActiveLeaseDurationTextView);
        mActiveLeaseDurationLabelTV = rootView.findViewById(R.id.apartmentViewActiveLeaseDurationLabelTextView);
        mOtherTenantsTV = rootView.findViewById(R.id.apartmentViewActiveLeaseOtherTenantsTextView);
        mOtherTenantsLabelTV = rootView.findViewById(R.id.apartmentViewActiveLeaseOtherTenantsLabelTextView);
        mActiveLeaseHeaderTV = rootView.findViewById(R.id.apartmentViewActiveLeaseHeaderTV);
        mDurationTR = rootView.findViewById(R.id.apartmentViewActiveLeaseDurationTR);
        mPrimaryTenantTR = rootView.findViewById(R.id.apartmentViewActiveLeasePrimaryTenantTR);
        mOtherTenantsTR = rootView.findViewById(R.id.apartmentViewActiveLeaseOtherTenantsTR);
        mMainPicIV = rootView.findViewById(R.id.apartmentViewMainPicIV);
        LinearLayout adViewLL = rootView.findViewById(R.id.adViewLL);
        mCallPrimaryTenantBtn = rootView.findViewById(R.id.apartmentViewCallTenantBtn);
        mCallPrimaryTenantBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callPrimaryTenant();
            }
        });
        mSMSPrimaryTenantBtn = rootView.findViewById(R.id.apartmentViewSMSTenantBtn);
        mSMSPrimaryTenantBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                smsPrimaryTenant();
            }
        });
        mEmailPrimaryTenantBtn = rootView.findViewById(R.id.apartmentViewEmailTenantBtn);
        mEmailPrimaryTenantBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                emailPrimaryTenant();
            }
        });
        mEmailAllBtn = rootView.findViewById(R.id.apartmentViewEmailAllTenantsBtn);
        mEmailAllBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                emailAllTenants();
            }
        });
        if (BuildConfig.FLAVOR.equals("free")) {
            AdView adview = rootView.findViewById(R.id.adView);
            //TODO enable for release
            //AdRequest adRequest = new AdRequest.Builder().build();
            //adview.loadAd(adRequest);
        } else {
            adViewLL.setVisibility(View.GONE);
        }
        fillTextViews();
        setUpTenantContactButtons();
        return rootView;
    }

    public void setUpTenantContactButtons() {
        if (mApartment.isRented()) {
            if(mActiveLeases.size() == 1) {
                mCallPrimaryTenantBtn.setVisibility(View.VISIBLE);
                mSMSPrimaryTenantBtn.setVisibility(View.VISIBLE);
                mEmailPrimaryTenantBtn.setVisibility(View.VISIBLE);
                mEmailAllBtn.setVisibility(View.VISIBLE);
            } else {
                mCallPrimaryTenantBtn.setVisibility(View.GONE);
                mSMSPrimaryTenantBtn.setVisibility(View.GONE);
                mEmailPrimaryTenantBtn.setVisibility(View.GONE);
                mEmailAllBtn.setVisibility(View.GONE);
            }
        } else {
            mCallPrimaryTenantBtn.setVisibility(View.GONE);
            mSMSPrimaryTenantBtn.setVisibility(View.GONE);
            mEmailPrimaryTenantBtn.setVisibility(View.GONE);
            mEmailAllBtn.setVisibility(View.GONE);
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fillTextViews();
        if (mMainPic != null) {
            Glide.with(this).load(mApartment.getMainPic()).placeholder(R.drawable.blank_home_pic)
                    .override(200, 200).centerCrop().into(mMainPicIV);
        } else {
            Glide.with(this).load(R.drawable.blank_home_pic).override(200, 200).centerCrop().into(mMainPicIV);
        }
        mMainPicIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mMainPic != null) {
                    if (new File(mMainPic).exists()) {
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                            Intent intent = new Intent();
                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            intent.setAction(Intent.ACTION_VIEW);
                            intent.setDataAndType(Uri.fromFile(new File(mMainPic)), "image/*");
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent();
                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            intent.setAction(Intent.ACTION_VIEW);
                            Uri photoUri = FileProvider.getUriForFile(getActivity(), BuildConfig.APPLICATION_ID + ".helpers.fileprovider", new File(mMainPic));
                            intent.setData(photoUri);
                            startActivity(intent);
                        }
                    } else {
                        Toast.makeText(getContext(), R.string.could_not_find_file, Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
        mMainPicIV.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (mMainPic != null) {
                    mPopup = new PopupMenu(getActivity(), view);
                    MenuInflater inflater = mPopup.getMenuInflater();
                    mPopup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
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
                    inflater.inflate(R.menu.picture_long_click_menu, mPopup.getMenu());
                    mPopup.show();
                } else {
                    mPopup = new PopupMenu(getContext(), view);
                    MenuInflater inflater = mPopup.getMenuInflater();
                    mPopup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
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
                    inflater.inflate(R.menu.picture_long_click_no_pic_menu, mPopup.getMenu());
                    mPopup.show();
                }
                return true;
            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        mAdapter = new OtherPicsAdapter(mApartment.getOtherPics(), getContext());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setOnTouchListener(new View.OnTouchListener() {
            // Setting on Touch Listener for handling the touch inside ScrollView
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Disallow the touch request for parent scroll on touch of child view

                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }

        });
        mAdapter.setOnDataChangedListener(new OtherPicsAdapter.OnDataChangedListener() {
            @Override
            public void onPicSelectedToBeRemoved(String removedPicPath) {
                mOtherPicToDelete = removedPicPath;
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
        mDialog = builder.create();
        mDialog.show();
    }

    public void updateCameraUri(String uri) {
        mCameraImageFilePath = uri;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MainActivity.REQUEST_IMAGE_DELETE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mDatabaseHandler.removeApartmentMainPic(mApartment);
                Glide.with(getContext()).load(R.drawable.blank_home_pic).override(200, 200).centerCrop().into(mMainPicIV);
                new File(mMainPic).delete();
                mMainPic = null;
                mApartment.setMainPic(null);
                mCallback.onPicDataChanged();
            } else {
                Toast.makeText(getContext(), R.string.permission_picture_denied, Toast.LENGTH_SHORT).show();
            }
            return;
        } else if (requestCode == MainActivity.REQUEST_ADAPTER_IMAGE_DELETE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mApartment.getOtherPics().remove(mOtherPicToDelete);
                mDatabaseHandler.removeApartmentOtherPic(mOtherPicToDelete, MainActivity.sUser);
                File picToDelete = new File(mOtherPicToDelete);
                if (picToDelete.exists()) {
                    picToDelete.delete();
                }
                hideOtherPicsRecyclerViewIfEmpty();
                mAdapter.notifyDataSetChanged();
                mCallback.onPicDataChanged();
                mOtherPicToDelete = "";
            } else {
                Toast.makeText(getContext(), R.string.permission_picture_denied, Toast.LENGTH_SHORT).show();
                mOtherPicToDelete = "";
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
        //Uses mApartment form to edit data
        if (requestCode == MainActivity.REQUEST_NEW_APARTMENT_FORM) {
            //If successful(not cancelled, passed validation)
            if (resultCode == RESULT_OK) {
                //Re-query cached mApartment array to update cache and refresh current textViews to display new data. Re-query to sort list
                int apartmentID = data.getIntExtra("editedApartmentID", 0);
                mApartment = mDataMethods.getCachedApartmentByApartmentID(apartmentID);
                fillTextViews();
            }
        } else if (requestCode == MainActivity.REQUEST_CAMERA_FOR_MAIN_PIC) {
            if (resultCode == RESULT_OK) {
                mApartment.setMainPic(mCameraImageFilePath);
                mApartment = ViewModelProviders.of(getActivity()).get(ApartmentTenantViewModel.class).getApartment().getValue();
                updateMainPicIV(mApartment.getMainPic());
                //fillTextViews();
            }
        } else if (requestCode == MainActivity.REQUEST_CAMERA_FOR_OTHER_PICS) {
            if (resultCode == RESULT_OK) {
                mApartment = ViewModelProviders.of(getActivity()).get(ApartmentTenantViewModel.class).getApartment().getValue();
                mOtherPics = mApartment.getOtherPics();
                mAdapter.updateResults(mOtherPics);
                hideOtherPicsRecyclerViewIfEmpty();
            }

        } else if (requestCode == MainActivity.REQUEST_GALLERY_FOR_MAIN_PIC) {
            if (resultCode == RESULT_OK && data != null) {
                mApartment = ViewModelProviders.of(getActivity()).get(ApartmentTenantViewModel.class).getApartment().getValue();
                updateMainPicIV(mApartment.getMainPic());
            }
        } else if (requestCode == MainActivity.REQUEST_GALLERY_FOR_OTHER_PICS) {
            if (resultCode == RESULT_OK && data != null) {
                mApartment = ViewModelProviders.of(getActivity()).get(ApartmentTenantViewModel.class).getApartment().getValue();
                mOtherPics = mApartment.getOtherPics();
                mAdapter.updateResults(mOtherPics);
                hideOtherPicsRecyclerViewIfEmpty();
            }
        }
    }

    public void refreshPictureAdapter() {
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
        mApartment = ViewModelProviders.of(getActivity()).get(ApartmentTenantViewModel.class).getApartment().getValue();
        updateMainPicIV(mApartment.getMainPic());
    }

    private void fillTextViews() {
        mAddressTV.setText(mApartment.getFullAddressString());
        mDescriptionTV.setText(mApartment.getDescription());
        mNotesTV.setText(mApartment.getNotes());
        if (!mApartment.isRented()) {
            mActiveLeaseDurationTV.setVisibility(View.GONE);
            mActiveLeaseDurationLabelTV.setVisibility(View.GONE);
            mPrimaryTenantTV.setVisibility(View.GONE);
            mPrimaryTenantLabelTV.setVisibility(View.GONE);
            mOtherTenantsTV.setVisibility(View.GONE);
            mOtherTenantsLabelTV.setVisibility(View.GONE);
            mActiveLeaseHeaderTV.setVisibility(View.GONE);
            mDurationTR.setVisibility(View.GONE);
            mPrimaryTenantTR.setVisibility(View.GONE);
            mOtherTenantsTR.setVisibility(View.GONE);
        } else {
            mActiveLeaseHeaderTV.setVisibility(View.VISIBLE);
            if (mActiveLeases.size() > 1) {
                mActiveLeaseDurationTV.setVisibility(View.VISIBLE);
                mActiveLeaseDurationLabelTV.setVisibility(View.VISIBLE);
                mActiveLeaseDurationTV.setText(R.string.multiple_active_leases);
                mPrimaryTenantTV.setVisibility(View.GONE);
                mPrimaryTenantLabelTV.setVisibility(View.GONE);
                mOtherTenantsTV.setVisibility(View.GONE);
                mOtherTenantsLabelTV.setVisibility(View.GONE);
                mDurationTR.setVisibility(View.VISIBLE);
                mPrimaryTenantTR.setVisibility(View.GONE);
                mOtherTenantsTR.setVisibility(View.GONE);
            } else if (mActiveLeases.size() == 1) {
                Lease currentLease = mActiveLeases.get(0);
                mPrimaryTenantTV.setVisibility(View.VISIBLE);
                mPrimaryTenantLabelTV.setVisibility(View.VISIBLE);
                mActiveLeaseDurationTV.setVisibility(View.VISIBLE);
                mActiveLeaseDurationLabelTV.setVisibility(View.VISIBLE);
                mOtherTenantsTV.setVisibility(View.VISIBLE);
                mOtherTenantsLabelTV.setVisibility(View.VISIBLE);
                mDurationTR.setVisibility(View.VISIBLE);
                mPrimaryTenantTR.setVisibility(View.VISIBLE);
                mOtherTenantsTR.setVisibility(View.VISIBLE);
                if (mPrimaryTenant != null) {
                    mPrimaryTenantTV.setText(mPrimaryTenant.getFirstAndLastNameString());
                } else {
                    mPrimaryTenantTV.setText(R.string.error_loading_primary_tenant);
                }
                if (currentLease.getLeaseStart() != null && currentLease.getLeaseEnd() != null) {
                    int dateFormatCode = mPreferences.getInt("dateFormat", DateAndCurrencyDisplayer.DATE_MMDDYYYY);
                    mActiveLeaseDurationTV.setText(currentLease.getStartAndEndDatesString(dateFormatCode));
                } else {
                    mActiveLeaseDurationTV.setText(R.string.error_leading_lease);
                }
                if (!mSecondaryTenants.isEmpty()) {
                    mOtherTenantsTV.setText("");
                    for (int i = 0; i < mSecondaryTenants.size(); i++) {
                        mOtherTenantsTV.append(mSecondaryTenants.get(i).getFirstAndLastNameString());
                        if (i != mSecondaryTenants.size() - 1) {
                            mOtherTenantsTV.append("\n");
                        }
                    }
                } else {
                    mOtherTenantsTV.setText(R.string.na);
                }
            }
        }
    }

    public void updateMainPicIV(String picFileName) {
        mMainPic = picFileName;
        Glide.with(this).load(mMainPic).placeholder(R.drawable.blank_home_pic)
                .override(200, 200).centerCrop().into(mMainPicIV);
    }

    public void hideOtherPicsRecyclerViewIfEmpty() {
        if (mAdapter.getItemCount() > 0) {
            mRecyclerView.setVisibility(View.VISIBLE);
        } else {
            mRecyclerView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //Save the fragment's instance
        super.onSaveInstanceState(outState);
        outState.putParcelable("mApartment", mApartment);
        if (mOtherPics != null) {
            outState.putInt("otherPicsSize", mOtherPics.size());
            for (int i = 0; i < mOtherPics.size(); i++) {
                outState.putString("mOtherPics" + i, mOtherPics.get(i));
            }
        }
        if (mMainPic != null) {
            outState.putString("mMainPic", mMainPic);
        }
        if (mPrimaryTenant != null) {
            outState.putParcelable("mPrimaryTenant", mPrimaryTenant);
        }
        if (mSecondaryTenants != null) {
            outState.putParcelableArrayList("mSecondaryTenants", mSecondaryTenants);
        }
        outState.putString("camera_image_file_path", mCameraImageFilePath);
    }

    private void callPrimaryTenant() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE}, MainActivity.REQUEST_PHONE_CALL_PERMISSION);
        } else {
            if(!mPrimaryTenant.getPhone().equals("")) {
                String phoneNumber = mPrimaryTenant.getPhone();
                phoneNumber = phoneNumber.replaceAll("[\\s\\-()]", "");
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneNumber, null));
                startActivity(intent);
            }
        }
    }

    private void smsPrimaryTenant() {
        if (!mPrimaryTenant.getPhone().equals("")) {
            String phoneNumber = mPrimaryTenant.getPhone();
            phoneNumber = phoneNumber.replaceAll("[\\s\\-()]", "");
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", phoneNumber, null)));
        }
    }

    private void emailPrimaryTenant() {
        if (mPrimaryTenant.getEmail() != null) {
            if (!mPrimaryTenant.getEmail().equals("")) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{mPrimaryTenant.getEmail()});
                intent.setType("application/octet-stream");
                startActivityForResult(Intent.createChooser(intent, getContext().getResources().getString(R.string.send_email)), MainActivity.REQUEST_EMAIL);
            }
        }
    }

    private void emailAllTenants() {
        if (mSecondaryTenants.isEmpty()) {
            emailPrimaryTenant();
        } else {
            ArrayList<String> emails = new ArrayList<>();
            if (mPrimaryTenant.getEmail() != null) {
                if (!mPrimaryTenant.getEmail().equals("")) {
                    emails.add(mPrimaryTenant.getEmail());
                }
            }
            for (int x = 0; x < mSecondaryTenants.size(); x++) {
                if (mSecondaryTenants.get(x).getEmail() != null) {
                    if (!mSecondaryTenants.get(x).getEmail().equals("")) {
                        emails.add(mSecondaryTenants.get(x).getEmail());
                    }
                }
            }
            if (!emails.isEmpty()) {
                String[] emailArray = new String[mSecondaryTenants.size() + 1];
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
