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
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.android.wizardpager.wizard.ui.PageFragmentCallbacks;
import com.rba18.BuildConfig;
import com.rba18.R;
import com.rba18.activities.MainActivity;
import com.rba18.helpers.AppFileManagementHelper;
import com.rba18.model.PaymentLogEntry;
import com.rba18.wizards.IncomeWizardPage2;

import java.io.File;

import static android.app.Activity.RESULT_OK;

public class IncomeWizardPage2Fragment extends android.support.v4.app.Fragment {
    private static final String ARG_KEY = "key";
    private PageFragmentCallbacks mCallbacks;
    private String mKey, mCameraImageFilePath;
    private IncomeWizardPage2 mPage;
    private ImageView mReceiptPicIV;
    private Button mChangeReceiptPicBtn, mRemoveReceiptPicBtn;
    private EditText mDescriptionET;
    private boolean mIsEdit;
    private AlertDialog mDialog;

    public static IncomeWizardPage2Fragment create(String key) {
        Bundle args = new Bundle();
        args.putString(ARG_KEY, key);

        IncomeWizardPage2Fragment fragment = new IncomeWizardPage2Fragment();
        fragment.setArguments(args);
        return fragment;
    }

    public IncomeWizardPage2Fragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        mKey = args.getString(ARG_KEY);
        mPage = (IncomeWizardPage2) mCallbacks.onGetPage(mKey);
        if(savedInstanceState != null){
            mCameraImageFilePath = savedInstanceState.getString("camera_image_file_path");
        }
        mIsEdit = false;
        Bundle extras = mPage.getData();
        if (extras != null) {
            PaymentLogEntry incomeToEdit = extras.getParcelable("incomeToEdit");
            if (incomeToEdit != null) {
                loadDataForEdit(incomeToEdit);
                mIsEdit = true;
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_income_wizard_page_2, container, false);
        (rootView.findViewById(android.R.id.title)).setVisibility(View.GONE);
        mReceiptPicIV = rootView.findViewById(R.id.incomeWizardMainPicIV);

        mChangeReceiptPicBtn = rootView.findViewById(R.id.incomeWizardChangePicBtn);
        mRemoveReceiptPicBtn = rootView.findViewById(R.id.incomeWizardRemovePicBtn);

        mDescriptionET = rootView.findViewById(R.id.incomeWizardDescriptionET);
        mDescriptionET.setText(mPage.getData().getString(IncomeWizardPage2.INCOME_DESCRIPTION_DATA_KEY));
        mDescriptionET.setSelection(mDescriptionET.getText().length());

        LinearLayout editReceiptPicLL = rootView.findViewById(R.id.incomeWizardReceiptPicLL);
        if (mIsEdit) {
            editReceiptPicLL.setVisibility(View.GONE);
        }
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

        mChangeReceiptPicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchCameraOrGalleryDialog();
            }
        });
        mRemoveReceiptPicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(
                        getActivity(),
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MainActivity.REQUEST_IMAGE_DELETE_PERMISSION
                );
            }
        });
        mDescriptionET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                mPage.getData().putString(IncomeWizardPage2.INCOME_DESCRIPTION_DATA_KEY, editable.toString());
                mPage.notifyDataChanged();
            }
        });
        if (mPage.getData().getString(IncomeWizardPage2.INCOME_RECEIPT_PIC_DATA_KEY) != null) {
            if (mPage.getData().getString(IncomeWizardPage2.INCOME_RECEIPT_PIC_DATA_KEY).equals("")) {
                Glide.with(getContext()).load(R.drawable.no_picture).into(mReceiptPicIV);
                mPage.getData().putString(IncomeWizardPage2.INCOME_WAS_RECEIPT_PIC_ADDED_DATA_KEY, getContext().getResources().getString(R.string.no));
            } else {
                String path = mPage.getData().getString(IncomeWizardPage2.INCOME_RECEIPT_PIC_DATA_KEY);
                Glide.with(this).load(path).placeholder(R.drawable.no_picture)
                        .override(100, 100).centerCrop().into(mReceiptPicIV);
            }
        } else {
            Glide.with(getContext()).load(R.drawable.no_picture).into(mReceiptPicIV);
            mPage.getData().putString(IncomeWizardPage2.INCOME_RECEIPT_PIC_DATA_KEY, "");
            mPage.getData().putString(IncomeWizardPage2.INCOME_WAS_RECEIPT_PIC_ADDED_DATA_KEY, getContext().getResources().getString(R.string.no));
        }
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                mPage.notifyDataChanged();
            }
        });
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

    @Override
    public void onPause() {
        super.onPause();
        if(mDialog != null){
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
        } else if (requestCode == MainActivity.REQUEST_IMAGE_DELETE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Glide.with(getContext()).load(R.drawable.no_picture).into(mReceiptPicIV);
                if (mPage.getData().getString(IncomeWizardPage2.INCOME_RECEIPT_PIC_DATA_KEY) != null) {
                    new File(mPage.getData().getString(IncomeWizardPage2.INCOME_RECEIPT_PIC_DATA_KEY)).delete();
                }
                mPage.getData().putString(IncomeWizardPage2.INCOME_RECEIPT_PIC_DATA_KEY, "");
                mPage.getData().putString(IncomeWizardPage2.INCOME_WAS_RECEIPT_PIC_ADDED_DATA_KEY, getContext().getResources().getString(R.string.no));
                mPage.notifyDataChanged();
            } else {
                Toast.makeText(getContext(), R.string.permission_picture_denied, Toast.LENGTH_SHORT).show();
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
                if (mPage.getData().getString(IncomeWizardPage2.INCOME_RECEIPT_PIC_DATA_KEY) != null) {
                    oldPic = mPage.getData().getString(mPage.getData().getString(IncomeWizardPage2.INCOME_RECEIPT_PIC_DATA_KEY));
                }
                File copiedFile = AppFileManagementHelper.copyPictureFileToApp(filePath, oldPic);
                if (copiedFile != null) {
                    mPage.getData().putString(IncomeWizardPage2.INCOME_RECEIPT_PIC_DATA_KEY, copiedFile.getAbsolutePath());
                    Glide.with(this).load(copiedFile).placeholder(R.drawable.no_picture)
                            .override(100, 100).centerCrop().into(mReceiptPicIV);
                    mPage.getData().putString(IncomeWizardPage2.INCOME_WAS_RECEIPT_PIC_ADDED_DATA_KEY, getContext().getResources().getString(R.string.yes));
                }
                //file path of captured image
                cursor.close();
                mPage.notifyDataChanged();
            }
        } else if (requestCode == MainActivity.REQUEST_CAMERA_FOR_MAIN_PIC) {
            if (resultCode == RESULT_OK) {
                if (mPage.getData().getString(IncomeWizardPage2.INCOME_RECEIPT_PIC_DATA_KEY) != null) {
                    File oldPic = new File(mPage.getData().getString(IncomeWizardPage2.INCOME_RECEIPT_PIC_DATA_KEY));
                    if (oldPic.exists()) {
                        oldPic.delete();
                    }
                }
                mPage.getData().putString(IncomeWizardPage2.INCOME_RECEIPT_PIC_DATA_KEY, mCameraImageFilePath);
                Glide.with(this).load(mCameraImageFilePath).placeholder(R.drawable.no_picture)
                        .override(100, 100).centerCrop().into(mReceiptPicIV);
                mPage.getData().putString(IncomeWizardPage2.INCOME_WAS_RECEIPT_PIC_ADDED_DATA_KEY, getContext().getResources().getString(R.string.yes));
            }
        }
    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);

        // In a future update to the support library, this should override setUserVisibleHint
        // instead of setMenuVisibility.
        if (mReceiptPicIV != null) {
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

    private void loadDataForEdit(PaymentLogEntry IncomeToEdit) {
        if (!mPage.getData().getBoolean(IncomeWizardPage2.WAS_PRELOADED)) {
            if (IncomeToEdit != null) {
                mPage.getData().putString(IncomeWizardPage2.INCOME_DESCRIPTION_DATA_KEY, IncomeToEdit.getDescription());
                if (IncomeToEdit.getReceiptPic() != null) {
                    if (!IncomeToEdit.getReceiptPic().equals("")) {
                        mPage.getData().putString(IncomeWizardPage2.INCOME_RECEIPT_PIC_DATA_KEY, IncomeToEdit.getReceiptPic());
                        mPage.getData().putString(IncomeWizardPage2.INCOME_WAS_RECEIPT_PIC_ADDED_DATA_KEY, getContext().getResources().getString(R.string.yes));
                    } else {
                        mPage.getData().putString(IncomeWizardPage2.INCOME_WAS_RECEIPT_PIC_ADDED_DATA_KEY, getContext().getResources().getString(R.string.no));
                    }
                    mPage.getData().putString(IncomeWizardPage2.INCOME_WAS_RECEIPT_PIC_ADDED_DATA_KEY, getContext().getResources().getString(R.string.no));
                } else {
                    mPage.getData().putString(IncomeWizardPage2.INCOME_WAS_RECEIPT_PIC_ADDED_DATA_KEY, getContext().getResources().getString(R.string.no));
                }
                mPage.getData().putBoolean(IncomeWizardPage2.WAS_PRELOADED, true);
            }
        }
    }
}