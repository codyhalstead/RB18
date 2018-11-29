package com.rba18.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.android.wizardpager.wizard.ui.PageFragmentCallbacks;
import com.rba18.BuildConfig;
import com.rba18.R;
import com.rba18.activities.MainActivity;
import com.rba18.adapters.OtherPicsAdapter;
import com.rba18.helpers.AppFileManagementHelper;
import com.rba18.model.Apartment;
import com.rba18.wizards.ApartmentWizardPage3;

import java.io.File;
import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

public class ApartmentWizardPage3Fragment extends android.support.v4.app.Fragment implements OtherPicsAdapter.OnDataChangedListener {
    private static final String ARG_KEY = "key";

    private PageFragmentCallbacks mCallbacks;
    private String mKey;
    private ApartmentWizardPage3 mPage;
    private ImageView mMainPicIV;
    private Button mChangeMainPicBtn, mRemoveMainPicBtn, mAddOtherPicBtn, mRemoveOtherPicBtn;
    private RecyclerView mRecyclerView;
    private OtherPicsAdapter mAdapter;
    private ArrayList<String> mOtherPics;
    private String mOtherImageToRemove;
    private String mCameraImageFilePath;
    private AlertDialog mDialog;

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
        if(savedInstanceState != null){
            mCameraImageFilePath = savedInstanceState.getString("camera_image_file_path");
        }
        mOtherImageToRemove = "";
        mOtherPics = new ArrayList<>();
        Bundle extras = mPage.getData();
        if (extras != null) {
            Apartment apartmentToEdit = extras.getParcelable("apartmentToEdit");
            if (apartmentToEdit != null) {
                loadDataForEdit(apartmentToEdit);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_apartment_wizard_page_3, container, false);
        (rootView.findViewById(android.R.id.title)).setVisibility(View.GONE);
        mMainPicIV = rootView.findViewById(R.id.apartmentWizardMainPicIV);
        mChangeMainPicBtn = rootView.findViewById(R.id.apartmentWizardChangeMainPicBtn);
        mRemoveMainPicBtn = rootView.findViewById(R.id.apartmentWizardRemoveMainPicBtn);
        mAddOtherPicBtn = rootView.findViewById(R.id.apartmentWizardAddOtherPicBtn);
        mRemoveOtherPicBtn = rootView.findViewById(R.id.apartmentWizardRemoveOtherPicBtn);
        mRecyclerView = rootView.findViewById(R.id.apartmentWizardOtherPicsRecyclerView);
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
        if(mPage.getData().getStringArrayList(ApartmentWizardPage3.APARTMENT_OTHER_PICS_DATA_KEY) != null){
            mOtherPics = mPage.getData().getStringArrayList(ApartmentWizardPage3.APARTMENT_OTHER_PICS_DATA_KEY);
        }
        mChangeMainPicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchCameraOrGalleryDialog(true);
            }
        });
        mRemoveMainPicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(
                        getActivity(),
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MainActivity.REQUEST_IMAGE_DELETE_PERMISSION
                );
            }
        });
        mAddOtherPicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mAdapter.getItemCount() > 9) {
                    Toast.makeText(getContext(), R.string.pic_limit, Toast.LENGTH_LONG).show();
                } else {
                    launchCameraOrGalleryDialog(false);
                }
            }
        });
        mRemoveOtherPicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(
                        getActivity(),
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MainActivity.REQUEST_ADAPTER_IMAGE_DELETE_PERMISSION
                );
            }
        });
        if (mPage.getData().getString(ApartmentWizardPage3.APARTMENT_MAIN_PIC_DATA_KEY) != null) {
            if (mPage.getData().getString(ApartmentWizardPage3.APARTMENT_MAIN_PIC_DATA_KEY).equals("")) {
                Glide.with(getContext()).load(R.drawable.blank_home_pic).override(100, 100).centerCrop().into(mMainPicIV);
            } else {
                Glide.with(getContext()).load(mPage.getData().getString(ApartmentWizardPage3.APARTMENT_MAIN_PIC_DATA_KEY)).placeholder(R.drawable.blank_home_pic)
                        .override(100, 100).centerCrop().into(mMainPicIV);
            }
        } else {
            Glide.with(getContext()).load(R.drawable.blank_home_pic).override(100, 100).centerCrop().into(mMainPicIV);
            mPage.getData().putString(ApartmentWizardPage3.APARTMENT_MAIN_PIC_DATA_KEY, "");
            mPage.getData().putString(ApartmentWizardPage3.APARTMENT_WAS_MAIN_PIC_ADDED_DATA_KEY, getContext().getResources().getString(R.string.no));
        }
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        mAdapter = new OtherPicsAdapter(mOtherPics, getContext());
        mAdapter.setPhotoClick(false);
        mAdapter.setOnDataChangedListener(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void launchCameraOrGalleryDialog(final boolean isMain) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(R.string.camera_or_gallery);
        builder.setPositiveButton(R.string.gallery,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        int requestCode;
                        if (isMain) {
                            requestCode = MainActivity.REQUEST_GALLERY_FOR_MAIN_PIC;
                        } else {
                            requestCode = MainActivity.REQUEST_GALLERY_FOR_OTHER_PICS;
                        }
                        ActivityCompat.requestPermissions(
                                getActivity(),
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                requestCode
                        );
                    }
                });

        builder.setNegativeButton(R.string.camera,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        int requestCode;
                        if (isMain) {
                            requestCode = MainActivity.REQUEST_CAMERA_FOR_MAIN_PIC;
                        } else {
                            requestCode = MainActivity.REQUEST_CAMERA_FOR_OTHER_PICS;
                        }
                        ActivityCompat.requestPermissions(
                                getActivity(),
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                                requestCode
                        );
                    }
                });
        mDialog = builder.create();
        mDialog.show();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mDialog != null) {
            mDialog.dismiss();
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
                Toast.makeText(getContext(), R.string.permission_picture_denied, Toast.LENGTH_SHORT).show();
            }
            return;
        } else if (requestCode == MainActivity.REQUEST_GALLERY_FOR_OTHER_PICS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, MainActivity.REQUEST_GALLERY_FOR_OTHER_PICS);
            } else {
                Toast.makeText(getContext(), R.string.permission_picture_denied, Toast.LENGTH_SHORT).show();
            }
            return;
        } else if (requestCode == MainActivity.REQUEST_IMAGE_DELETE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Glide.with(getContext()).load(R.drawable.blank_home_pic).override(100, 100).centerCrop().into(mMainPicIV);
                if (mPage.getData().getString(ApartmentWizardPage3.APARTMENT_MAIN_PIC_DATA_KEY) != null) {
                    new File(mPage.getData().getString(ApartmentWizardPage3.APARTMENT_MAIN_PIC_DATA_KEY)).delete();
                }
                mPage.getData().putString(ApartmentWizardPage3.APARTMENT_MAIN_PIC_DATA_KEY, "");
                mPage.getData().putString(ApartmentWizardPage3.APARTMENT_WAS_MAIN_PIC_ADDED_DATA_KEY, getContext().getResources().getString(R.string.no));
                mPage.notifyDataChanged();
            } else {
                Toast.makeText(getContext(), R.string.permission_picture_denied, Toast.LENGTH_SHORT).show();
            }
            return;
        } else if (requestCode == MainActivity.REQUEST_ADAPTER_IMAGE_DELETE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (mOtherImageToRemove.equals("")) {
                    if (!mOtherPics.isEmpty()) {
                        new File(mOtherPics.get(mOtherPics.size() - 1)).delete();
                        mOtherPics.remove(mOtherPics.size() - 1);
                        mPage.getData().putInt(ApartmentWizardPage3.APARTMENT_AMOUNT_OF_OTHER_PICS_DATA_KEY, mAdapter.getItemCount());
                        mPage.getData().putStringArrayList(ApartmentWizardPage3.APARTMENT_OTHER_PICS_DATA_KEY, mOtherPics);
                        mAdapter.notifyDataSetChanged();
                        mPage.notifyDataChanged();
                    }
                } else {
                    mOtherPics.remove(mOtherImageToRemove);
                    mAdapter.notifyDataSetChanged();
                    mPage.getData().putInt(ApartmentWizardPage3.APARTMENT_AMOUNT_OF_OTHER_PICS_DATA_KEY, mAdapter.getItemCount());
                    mPage.getData().putStringArrayList(ApartmentWizardPage3.APARTMENT_OTHER_PICS_DATA_KEY, mAdapter.getImagePaths());
                    mPage.notifyDataChanged();
                    mOtherImageToRemove = "";
                }
            } else {
                Toast.makeText(getContext(), R.string.permission_picture_denied, Toast.LENGTH_SHORT).show();
                mOtherImageToRemove = "";
            }
            return;
        } else if (requestCode == MainActivity.REQUEST_CAMERA_FOR_MAIN_PIC) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File photoFile = AppFileManagementHelper.createImageFileFromCamera();
                mCameraImageFilePath = photoFile.getAbsolutePath();
                Uri photoUri = FileProvider.getUriForFile(getContext(), BuildConfig.APPLICATION_ID + ".helpers.fileprovider", photoFile);
                pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(pictureIntent, MainActivity.REQUEST_CAMERA_FOR_MAIN_PIC);
            } else {
                Toast.makeText(getContext(), R.string.permission_picture_denied, Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == MainActivity.REQUEST_CAMERA_FOR_OTHER_PICS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File photoFile = AppFileManagementHelper.createImageFileFromCamera();
                mCameraImageFilePath = photoFile.getAbsolutePath();
                Uri photoUri = FileProvider.getUriForFile(getContext(), BuildConfig.APPLICATION_ID + ".helpers.fileprovider", photoFile);
                pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(pictureIntent, MainActivity.REQUEST_CAMERA_FOR_OTHER_PICS);
            } else {
                Toast.makeText(getContext(), R.string.permission_picture_denied, Toast.LENGTH_SHORT).show();
            }
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
                Cursor cursor = getContext().getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String filePath = cursor.getString(columnIndex);
                cursor.close();
                String oldPic = null;
                if (mPage.getData().getString(ApartmentWizardPage3.APARTMENT_MAIN_PIC_DATA_KEY) != null) {
                    oldPic = mPage.getData().getString(ApartmentWizardPage3.APARTMENT_MAIN_PIC_DATA_KEY);
                }
                File copiedFile = AppFileManagementHelper.copyPictureFileToApp(filePath, oldPic);
                if (copiedFile != null) {
                    mPage.getData().putString(ApartmentWizardPage3.APARTMENT_MAIN_PIC_DATA_KEY, copiedFile.getAbsolutePath());
                    Glide.with(this).load(copiedFile).placeholder(R.drawable.blank_home_pic)
                            .override(100, 100).centerCrop().into(mMainPicIV);
                    mPage.getData().putString(ApartmentWizardPage3.APARTMENT_WAS_MAIN_PIC_ADDED_DATA_KEY, getContext().getResources().getString(R.string.yes));
                }
                //file path of captured image
                cursor.close();
                mPage.notifyDataChanged();
            }
        } else if (requestCode == MainActivity.REQUEST_GALLERY_FOR_OTHER_PICS) {
            if (resultCode == RESULT_OK && data != null) {
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContext().getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String filePath = cursor.getString(columnIndex);
                cursor.close();
                File copiedFile = AppFileManagementHelper.copyPictureFileToApp(filePath, null);
                if (copiedFile != null) {
                    mOtherPics.add(filePath);
                    mAdapter.notifyDataSetChanged();
                    mPage.getData().putInt(ApartmentWizardPage3.APARTMENT_AMOUNT_OF_OTHER_PICS_DATA_KEY, mAdapter.getItemCount());
                    mPage.getData().putStringArrayList(ApartmentWizardPage3.APARTMENT_OTHER_PICS_DATA_KEY, mOtherPics);
                }
                cursor.close();
                mPage.notifyDataChanged();
            }
        } else if (requestCode == MainActivity.REQUEST_CAMERA_FOR_MAIN_PIC) {
            if (resultCode == RESULT_OK) {
                if (mPage.getData().getString(ApartmentWizardPage3.APARTMENT_MAIN_PIC_DATA_KEY) != null) {
                    File oldPic = new File(mPage.getData().getString(ApartmentWizardPage3.APARTMENT_MAIN_PIC_DATA_KEY));
                    if (oldPic.exists()) {
                        oldPic.delete();
                    }
                }
                mPage.getData().putString(ApartmentWizardPage3.APARTMENT_MAIN_PIC_DATA_KEY, mCameraImageFilePath);
                Glide.with(this).load(mCameraImageFilePath).placeholder(R.drawable.no_picture)
                        .override(100, 100).centerCrop().into(mMainPicIV);
                mPage.getData().putString(ApartmentWizardPage3.APARTMENT_WAS_MAIN_PIC_ADDED_DATA_KEY, getContext().getResources().getString(R.string.yes));
            }
        } else if (requestCode == MainActivity.REQUEST_CAMERA_FOR_OTHER_PICS) {
            if (resultCode == RESULT_OK) {
                mOtherPics.add(mCameraImageFilePath);
                mAdapter.notifyDataSetChanged();
                mPage.getData().putInt(ApartmentWizardPage3.APARTMENT_AMOUNT_OF_OTHER_PICS_DATA_KEY, mAdapter.getItemCount());
                mPage.getData().putStringArrayList(ApartmentWizardPage3.APARTMENT_OTHER_PICS_DATA_KEY, mOtherPics);
                mPage.notifyDataChanged();
            }
        }
    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);

        // In a future update to the support library, this should override setUserVisibleHint
        // instead of setMenuVisibility.
        if (mMainPicIV != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            if (!menuVisible) {
                imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("camera_image_file_path", mCameraImageFilePath);
        super.onSaveInstanceState(outState);
    }

    private void loadDataForEdit(Apartment apartmentToEdit) {
        if (!mPage.getData().getBoolean(ApartmentWizardPage3.WAS_PRELOADED)) {
            //Main pic
            if (apartmentToEdit.getMainPic() != null) {
                if (!apartmentToEdit.getMainPic().equals("")) {
                    mPage.getData().putString(ApartmentWizardPage3.APARTMENT_MAIN_PIC_DATA_KEY, apartmentToEdit.getMainPic());
                    mPage.getData().putString(ApartmentWizardPage3.APARTMENT_WAS_MAIN_PIC_ADDED_DATA_KEY, getContext().getResources().getString(R.string.yes));
                } else {
                    mPage.getData().putString(ApartmentWizardPage3.APARTMENT_WAS_MAIN_PIC_ADDED_DATA_KEY, getContext().getResources().getString(R.string.no));
                }
            } else {
                mPage.getData().putString(ApartmentWizardPage3.APARTMENT_WAS_MAIN_PIC_ADDED_DATA_KEY, getContext().getResources().getString(R.string.no));
            }
            //Other pics
            if (apartmentToEdit.getOtherPics() != null) {
                mOtherPics = apartmentToEdit.getOtherPics();
                mPage.getData().putStringArrayList(ApartmentWizardPage3.APARTMENT_OTHER_PICS_DATA_KEY, mOtherPics);
                mPage.getData().putInt(ApartmentWizardPage3.APARTMENT_AMOUNT_OF_OTHER_PICS_DATA_KEY, mOtherPics.size());
            } else {
                mPage.getData().putStringArrayList(ApartmentWizardPage3.APARTMENT_OTHER_PICS_DATA_KEY, mOtherPics);
                mPage.getData().putInt(ApartmentWizardPage3.APARTMENT_AMOUNT_OF_OTHER_PICS_DATA_KEY, mOtherPics.size());
            }
            mPage.getData().putBoolean(ApartmentWizardPage3.WAS_PRELOADED, true);
        }
    }

    @Override
    public void onPicSelectedToBeRemoved(String removedPicPath) {
        mOtherImageToRemove = removedPicPath;
        ActivityCompat.requestPermissions(
                getActivity(),
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                MainActivity.REQUEST_ADAPTER_IMAGE_DELETE_PERMISSION
        );
    }
}
