package com.RB18.fragments;

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
import com.example.cody.rentbud.BuildConfig;
import com.example.cody.rentbud.R;
import com.RB18.activities.MainActivity;
import com.RB18.helpers.AppFileManagementHelper;
import com.RB18.model.ExpenseLogEntry;
import com.RB18.wizards.ExpenseWizardPage2;

import java.io.File;

import static android.app.Activity.RESULT_OK;

public class ExpenseWizardPage2Fragment extends android.support.v4.app.Fragment {
    private static final String ARG_KEY = "key";

    private PageFragmentCallbacks mCallbacks;
    private String mKey;
    private ExpenseWizardPage2 mPage;
    private ImageView receiptPicIV;
    private Button changeReceiptPicBtn, removeReceiptPicBtn;
    private EditText descriptionET;
    private LinearLayout editReceiptPicLL;
    private boolean isEdit;
    private String cameraImageFilePath;
    private AlertDialog dialog;

    public static ExpenseWizardPage2Fragment create(String key) {
        Bundle args = new Bundle();
        args.putString(ARG_KEY, key);

        ExpenseWizardPage2Fragment fragment = new ExpenseWizardPage2Fragment();
        fragment.setArguments(args);
        return fragment;
    }

    public ExpenseWizardPage2Fragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        mKey = args.getString(ARG_KEY);
        mPage = (ExpenseWizardPage2) mCallbacks.onGetPage(mKey);
        if(savedInstanceState != null){
            cameraImageFilePath = savedInstanceState.getString("camera_image_file_path");
        }
        isEdit = false;
        Bundle extras = mPage.getData();
        if (extras != null) {
            ExpenseLogEntry expenseToEdit = extras.getParcelable("expenseToEdit");
            if (expenseToEdit != null) {
                loadDataForEdit(expenseToEdit);
                isEdit = true;
            }  else {
                //mPage.getData().putString(ExpenseWizardPage2.EXPENSE_RECEIPT_PIC_DATA_KEY, "");
                //mPage.getData().putString(ExpenseWizardPage2.EXPENSE_WAS_RECEIPT_PIC_ADDED_DATA_KEY, getContext().getResources().getString(R.string.no));
            }
        } else {
            //mPage.getData().putString(ExpenseWizardPage2.EXPENSE_RECEIPT_PIC_DATA_KEY, "");
           // mPage.getData().putString(ExpenseWizardPage2.EXPENSE_WAS_RECEIPT_PIC_ADDED_DATA_KEY, getContext().getResources().getString(R.string.no));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_expense_wizard_page_2, container, false);
        (rootView.findViewById(android.R.id.title)).setVisibility(View.GONE);
        receiptPicIV = rootView.findViewById(R.id.expenseWizardMainPicIV);
        changeReceiptPicBtn = rootView.findViewById(R.id.expenseWizardChangePicBtn);
        removeReceiptPicBtn = rootView.findViewById(R.id.expenseWizardRemovePicBtn);

        descriptionET = rootView.findViewById(R.id.expenseWizardDescriptionET);
        descriptionET.setText(mPage.getData().getString(ExpenseWizardPage2.EXPENSE_DESCRIPTION_DATA_KEY));
        descriptionET.setSelection(descriptionET.getText().length());

        editReceiptPicLL = rootView.findViewById(R.id.expenseWizardReceiptPicLL);
        if(isEdit){
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

        changeReceiptPicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchCameraOrGalleryDialog();
            }
        });
        removeReceiptPicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(
                        getActivity(),
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MainActivity.REQUEST_IMAGE_DELETE_PERMISSION
                );
            }
        });
        descriptionET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                mPage.getData().putString(ExpenseWizardPage2.EXPENSE_DESCRIPTION_DATA_KEY, editable.toString());
                mPage.notifyDataChanged();
            }
        });
        if (mPage.getData().getString(ExpenseWizardPage2.EXPENSE_RECEIPT_PIC_DATA_KEY) != null) {
            if (mPage.getData().getString(ExpenseWizardPage2.EXPENSE_RECEIPT_PIC_DATA_KEY).equals("")) {
                mPage.getData().putString(ExpenseWizardPage2.EXPENSE_WAS_RECEIPT_PIC_ADDED_DATA_KEY, getContext().getResources().getString(R.string.no));
                Glide.with(getContext()).load(R.drawable.no_picture).override(100, 100).centerCrop().into(receiptPicIV);
            } else {
                Glide.with(getContext()).load(mPage.getData().getString(ExpenseWizardPage2.EXPENSE_RECEIPT_PIC_DATA_KEY)).placeholder(R.drawable.no_picture)
                        .override(100, 100).centerCrop().into(receiptPicIV);
            }
        } else {
            Glide.with(getContext()).load(R.drawable.no_picture).override(100, 100).centerCrop().into(receiptPicIV);
            mPage.getData().putString(ExpenseWizardPage2.EXPENSE_RECEIPT_PIC_DATA_KEY, "");
            mPage.getData().putString(ExpenseWizardPage2.EXPENSE_WAS_RECEIPT_PIC_ADDED_DATA_KEY, getContext().getResources().getString(R.string.no));
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
        dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onPause() {
        super.onPause();
        if(dialog != null){
            dialog.dismiss();
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
                Glide.with(getContext()).load(R.drawable.no_picture).override(100, 100).centerCrop().into(receiptPicIV);
                if(mPage.getData().getString(ExpenseWizardPage2.EXPENSE_RECEIPT_PIC_DATA_KEY) != null){
                    new File(mPage.getData().getString(ExpenseWizardPage2.EXPENSE_RECEIPT_PIC_DATA_KEY)).delete();
                }
                mPage.getData().putString(ExpenseWizardPage2.EXPENSE_RECEIPT_PIC_DATA_KEY, "");
                mPage.getData().putString(ExpenseWizardPage2.EXPENSE_WAS_RECEIPT_PIC_ADDED_DATA_KEY, getContext().getResources().getString(R.string.no));
                mPage.notifyDataChanged();
            } else {
                Toast.makeText(getContext(), R.string.permission_picture_denied, Toast.LENGTH_SHORT).show();
            }
            return;
        } else if (requestCode == MainActivity.REQUEST_CAMERA_FOR_MAIN_PIC) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                ;
                Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File photoFile = AppFileManagementHelper.createImageFileFromCamera();
                cameraImageFilePath = photoFile.getAbsolutePath();
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
                if (mPage.getData().getString(ExpenseWizardPage2.EXPENSE_RECEIPT_PIC_DATA_KEY) != null) {
                    oldPic = mPage.getData().getString(mPage.getData().getString(ExpenseWizardPage2.EXPENSE_RECEIPT_PIC_DATA_KEY));
                }
                File copiedFile = AppFileManagementHelper.copyPictureFileToApp(filePath, oldPic);
                if (copiedFile != null) {
                    mPage.getData().putString(ExpenseWizardPage2.EXPENSE_RECEIPT_PIC_DATA_KEY, copiedFile.getAbsolutePath());
                    Glide.with(this).load(copiedFile).placeholder(R.drawable.no_picture)
                            .override(100, 100).centerCrop().into(receiptPicIV);
                    mPage.getData().putString(ExpenseWizardPage2.EXPENSE_WAS_RECEIPT_PIC_ADDED_DATA_KEY, getContext().getResources().getString(R.string.yes));
                }
                //file path of captured image
                cursor.close();
                //this.apartment.setMainPic(filePath);
                mPage.notifyDataChanged();
            }
        } else if (requestCode == MainActivity.REQUEST_CAMERA_FOR_MAIN_PIC) {
            if (resultCode == RESULT_OK) {
                if (mPage.getData().getString(ExpenseWizardPage2.EXPENSE_RECEIPT_PIC_DATA_KEY) != null) {
                    File oldPic = new File(mPage.getData().getString(ExpenseWizardPage2.EXPENSE_RECEIPT_PIC_DATA_KEY));
                    if (oldPic.exists()) {
                        oldPic.delete();
                    }
                }
                mPage.getData().putString(ExpenseWizardPage2.EXPENSE_RECEIPT_PIC_DATA_KEY, cameraImageFilePath);
                Glide.with(this).load(cameraImageFilePath).placeholder(R.drawable.no_picture)
                        .override(100, 100).centerCrop().into(receiptPicIV);
                mPage.getData().putString(ExpenseWizardPage2.EXPENSE_WAS_RECEIPT_PIC_ADDED_DATA_KEY, getContext().getResources().getString(R.string.yes));
            }
        }

    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);

        // In a future update to the support library, this should override setUserVisibleHint
        // instead of setMenuVisibility.
        if (receiptPicIV != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            if (!menuVisible) {
                imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
            }
        }
    }

    //private void preloadData(Bundle bundle){
    //
    //}


    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("camera_image_file_path", cameraImageFilePath);
        super.onSaveInstanceState(outState);
    }

    private void loadDataForEdit(ExpenseLogEntry expenseToEdit) {
        if (!mPage.getData().getBoolean(ExpenseWizardPage2.WAS_PRELOADED)) {
            mPage.getData().putString(ExpenseWizardPage2.EXPENSE_DESCRIPTION_DATA_KEY, expenseToEdit.getDescription());
            if (expenseToEdit.getReceiptPic() != null) {
                if (!expenseToEdit.getReceiptPic().equals("")) {
                    mPage.getData().putString(ExpenseWizardPage2.EXPENSE_RECEIPT_PIC_DATA_KEY, expenseToEdit.getReceiptPic());
                    mPage.getData().putString(ExpenseWizardPage2.EXPENSE_WAS_RECEIPT_PIC_ADDED_DATA_KEY, getContext().getResources().getString(R.string.yes));
                } else {
                    mPage.getData().putString(ExpenseWizardPage2.EXPENSE_WAS_RECEIPT_PIC_ADDED_DATA_KEY, getContext().getResources().getString(R.string.no));
                }
                mPage.getData().putString(ExpenseWizardPage2.EXPENSE_WAS_RECEIPT_PIC_ADDED_DATA_KEY, getContext().getResources().getString(R.string.no));
            } else {
                mPage.getData().putString(ExpenseWizardPage2.EXPENSE_WAS_RECEIPT_PIC_ADDED_DATA_KEY, getContext().getResources().getString(R.string.no));
            }
            mPage.getData().putBoolean(ExpenseWizardPage2.WAS_PRELOADED, true);
        }
    }
}