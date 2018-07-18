package com.rentbud.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.android.wizardpager.wizard.ui.PageFragmentCallbacks;
import com.example.cody.rentbud.R;
import com.rentbud.activities.MainActivity;
import com.rentbud.adapters.RecyclerViewAdapter;
import com.rentbud.model.Apartment;
import com.rentbud.model.ExpenseLogEntry;
import com.rentbud.wizards.ApartmentWizardPage2;
import com.rentbud.wizards.ApartmentWizardPage3;
import com.rentbud.wizards.ExpenseWizardPage2;

import java.io.File;
import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;
import static android.support.constraint.Constraints.TAG;

public class ApartmentWizardPage3Fragment extends android.support.v4.app.Fragment {
    private static final String ARG_KEY = "key";

    private PageFragmentCallbacks mCallbacks;
    private String mKey;
    private ApartmentWizardPage3 mPage;
    private ImageView mainPicIV;
    private Button changeMainPicBtn, removeMainPicBtn, addOtherPicBtn, removeOtherPicBtn;
    private RecyclerView recyclerView;
    private RecyclerViewAdapter adapter;
    private ArrayList<String> otherPics;
    private int numberOfOtherPics;

    public static ApartmentWizardPage3Fragment create(String key) {
        Bundle args = new Bundle();
        args.putString(ARG_KEY, key);

        ApartmentWizardPage3Fragment fragment = new ApartmentWizardPage3Fragment();
        fragment.setArguments(args);
        return fragment;
    }

    public ApartmentWizardPage3Fragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        mKey = args.getString(ARG_KEY);
        mPage = (ApartmentWizardPage3) mCallbacks.onGetPage(mKey);
        otherPics = new ArrayList<>();
        numberOfOtherPics = 0;
        Bundle extras = mPage.getData();
        if (extras != null) {
            Apartment apartmentToEdit = extras.getParcelable("apartmentToEdit");
            if (apartmentToEdit != null) {
                loadDataForEdit(apartmentToEdit);
            } else {
                mPage.getData().putString(ApartmentWizardPage3.APARTMENT_MAIN_PIC_DATA_KEY, "");
                mPage.getData().putString(ApartmentWizardPage3.APARTMENT_WAS_MAIN_PIC_ADDED_DATA_KEY, "No");
                mPage.getData().putInt(ApartmentWizardPage3.APARTMENT_AMOUNT_OF_OTHER_PICS_DATA_KEY, numberOfOtherPics);
            }
        } else {
            mPage.getData().putString(ApartmentWizardPage3.APARTMENT_MAIN_PIC_DATA_KEY, "");
            mPage.getData().putString(ApartmentWizardPage3.APARTMENT_WAS_MAIN_PIC_ADDED_DATA_KEY, "No");
            mPage.getData().putInt(ApartmentWizardPage3.APARTMENT_AMOUNT_OF_OTHER_PICS_DATA_KEY, numberOfOtherPics);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_apartment_wizard_page_3, container, false);
        (rootView.findViewById(android.R.id.title)).setVisibility(View.GONE);
        mainPicIV = rootView.findViewById(R.id.apartmentWizardMainPicIV);
        changeMainPicBtn = rootView.findViewById(R.id.apartmentWizardChangeMainPicBtn);
        removeMainPicBtn = rootView.findViewById(R.id.apartmentWizardRemoveMainPicBtn);
        addOtherPicBtn = rootView.findViewById(R.id.apartmentWizardAddOtherPicBtn);
        removeOtherPicBtn = rootView.findViewById(R.id.apartmentWizardRemoveOtherPicBtn);
        recyclerView = rootView.findViewById(R.id.apartmentWizardOtherPicsRecyclerView);
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (!(activity instanceof PageFragmentCallbacks)) {
            throw new ClassCastException("Activity must implement PageFragmentCallbacks");
        }

        mCallbacks = (PageFragmentCallbacks) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        changeMainPicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(
                        getActivity(),
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MainActivity.REQUEST_GALLERY_FOR_MAIN_PIC
                );
            }
        });
        removeMainPicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Glide.with(getContext()).load(R.drawable.blank_home_pic).into(mainPicIV);
                mPage.getData().putString(ApartmentWizardPage3.APARTMENT_MAIN_PIC_DATA_KEY, "");
                mPage.getData().putString(ApartmentWizardPage3.APARTMENT_WAS_MAIN_PIC_ADDED_DATA_KEY, "No");
                mPage.notifyDataChanged();
            }
        });
        addOtherPicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(
                        getActivity(),
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MainActivity.REQUEST_GALLERY_FOR_OTHER_PICS);
            }
        });
        removeOtherPicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!otherPics.isEmpty()) {
                    otherPics.remove(otherPics.size() - 1);
                    // this.apartment.addOtherPic(filePath);
                    // databaseHandler.addApartmentOtherPic(apartment, filePath, MainActivity.user);
                    //MainActivity5.apartmentList = databaseHandler.getUsersApartments(MainActivity5.user);
                    //ApartmentListFragment.apartmentListAdapterNeedsRefreshed = true;
                    numberOfOtherPics--;
                    mPage.getData().putInt(ApartmentWizardPage3.APARTMENT_AMOUNT_OF_OTHER_PICS_DATA_KEY, numberOfOtherPics);
                    mPage.getData().putStringArrayList(ApartmentWizardPage3.APARTMENT_OTHER_PICS_DATA_KEY, otherPics);
                    adapter.notifyDataSetChanged();
                    mPage.notifyDataChanged();
                }
            }
        });
        if (mPage.getData().getString(ApartmentWizardPage3.APARTMENT_MAIN_PIC_DATA_KEY) != null) {
            if(mPage.getData().getString(ApartmentWizardPage3.APARTMENT_MAIN_PIC_DATA_KEY).equals("")){
                Glide.with(getContext()).load(R.drawable.blank_home_pic).into(mainPicIV);
            } else {
                Glide.with(getContext()).load(mPage.getData().getString(ApartmentWizardPage3.APARTMENT_MAIN_PIC_DATA_KEY)).into(mainPicIV);
            }
        } else {
            Glide.with(getContext()).load(R.drawable.blank_home_pic).into(mainPicIV);
            mPage.getData().putString(ApartmentWizardPage3.APARTMENT_MAIN_PIC_DATA_KEY, "");
            mPage.getData().putString(ApartmentWizardPage3.APARTMENT_WAS_MAIN_PIC_ADDED_DATA_KEY, "No");
        }

        //if(mPage.getData().getStringArrayList(ApartmentWizardPage3.APARTMENT_OTHER_PICS_DATA_KEY) != null){
        //    otherPics = mPage.getData().getStringArrayList(ApartmentWizardPage3.APARTMENT_OTHER_PICS_DATA_KEY);
        //    numberOfOtherPics = mPage.getData().getInt(ApartmentWizardPage3.APARTMENT_AMOUNT_OF_OTHER_PICS_DATA_KEY);
        //} else {
        //    mPage.getData().putInt(ApartmentWizardPage3.APARTMENT_AMOUNT_OF_OTHER_PICS_DATA_KEY, numberOfOtherPics);
        //}

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        adapter = new RecyclerViewAdapter(otherPics, getContext());
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
                //this.apartment.setMainPic(filePath);
                mPage.getData().putString(ApartmentWizardPage3.APARTMENT_MAIN_PIC_DATA_KEY, filePath);
                mPage.getData().putString(ApartmentWizardPage3.APARTMENT_WAS_MAIN_PIC_ADDED_DATA_KEY, "Yes");
                mPage.notifyDataChanged();
                //this.mainPic = filePath;
                //databaseHandler.changeApartmentMainPic(this.apartment);
                //MainActivity5.apartmentList = databaseHandler.getUsersApartments(MainActivity5.user);
                //ApartmentListFragment.apartmentListAdapterNeedsRefreshed = true;
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
                otherPics.add(filePath);
                // this.apartment.addOtherPic(filePath);
                // databaseHandler.addApartmentOtherPic(apartment, filePath, MainActivity.user);
                //MainActivity5.apartmentList = databaseHandler.getUsersApartments(MainActivity5.user);
                //ApartmentListFragment.apartmentListAdapterNeedsRefreshed = true;
                adapter.notifyDataSetChanged();
                numberOfOtherPics++;
                mPage.getData().putInt(ApartmentWizardPage3.APARTMENT_AMOUNT_OF_OTHER_PICS_DATA_KEY, numberOfOtherPics);
                mPage.getData().putStringArrayList(ApartmentWizardPage3.APARTMENT_OTHER_PICS_DATA_KEY, otherPics);
                mPage.notifyDataChanged();
            }
        }
    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);

        // In a future update to the support library, this should override setUserVisibleHint
        // instead of setMenuVisibility.
        if (mainPicIV != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            if (!menuVisible) {
                imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
            }
        }
    }

    private void loadDataForEdit(Apartment apartmentToEdit) {
        if (!mPage.getData().getBoolean(ApartmentWizardPage3.WAS_PRELOADED)) {
        //Main pic
        if (apartmentToEdit.getMainPic() != null) {
            if (!apartmentToEdit.getMainPic().equals("")) {
                mPage.getData().putString(ApartmentWizardPage3.APARTMENT_MAIN_PIC_DATA_KEY, apartmentToEdit.getMainPic());
                mPage.getData().putString(ApartmentWizardPage3.APARTMENT_WAS_MAIN_PIC_ADDED_DATA_KEY, "Yes");
            } else {
                mPage.getData().putString(ApartmentWizardPage3.APARTMENT_WAS_MAIN_PIC_ADDED_DATA_KEY, "No");
            }
            //mPage.getData().putString(ApartmentWizardPage3.APARTMENT_WAS_MAIN_PIC_ADDED_DATA_KEY, "No");
        } else {
            mPage.getData().putString(ApartmentWizardPage3.APARTMENT_WAS_MAIN_PIC_ADDED_DATA_KEY, "No");
        }
        //Other pics
        if (apartmentToEdit.getOtherPics() != null) {
            this.otherPics = apartmentToEdit.getOtherPics();
            mPage.getData().putStringArrayList(ApartmentWizardPage3.APARTMENT_OTHER_PICS_DATA_KEY, otherPics);
            mPage.getData().putInt(ApartmentWizardPage3.APARTMENT_AMOUNT_OF_OTHER_PICS_DATA_KEY, otherPics.size());
        } else {
            mPage.getData().putStringArrayList(ApartmentWizardPage3.APARTMENT_OTHER_PICS_DATA_KEY, otherPics);
            mPage.getData().putInt(ApartmentWizardPage3.APARTMENT_AMOUNT_OF_OTHER_PICS_DATA_KEY, otherPics.size());
        }
        mPage.getData().putBoolean(ApartmentWizardPage3.WAS_PRELOADED, true);
    }
    }
}
