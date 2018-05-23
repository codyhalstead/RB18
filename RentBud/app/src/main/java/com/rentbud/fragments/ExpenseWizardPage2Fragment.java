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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.android.wizardpager.wizard.ui.PageFragmentCallbacks;
import com.example.cody.rentbud.R;
import com.rentbud.activities.MainActivity;
import com.rentbud.wizards.ExpenseWizardPage2;
import com.rentbud.wizards.IncomeWizardPage2;

import static android.app.Activity.RESULT_OK;

public class ExpenseWizardPage2Fragment extends android.support.v4.app.Fragment {
    private static final String ARG_KEY = "key";

    private PageFragmentCallbacks mCallbacks;
    private String mKey;
    private ExpenseWizardPage2 mPage;
    private ImageView receiptPicIV;
    private Button changeReceiptPicBtn, removeReceiptPicBtn;
    private EditText descriptionET;

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
                ActivityCompat.requestPermissions(
                        getActivity(),
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MainActivity.REQUEST_GALLERY_FOR_MAIN_PIC
                );
            }
        });
        removeReceiptPicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Glide.with(getContext()).load(R.drawable.no_picture).into(receiptPicIV);
                mPage.getData().putString(ExpenseWizardPage2.EXPENSE_RECEIPT_PIC_DATA_KEY, "");
                mPage.getData().putString(ExpenseWizardPage2.EXPENSE_WAS_RECEIPT_PIC_ADDED_DATA_KEY, "No");
                mPage.notifyDataChanged();
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
            Glide.with(getContext()).load(mPage.getData().getString(ExpenseWizardPage2.EXPENSE_RECEIPT_PIC_DATA_KEY)).into(receiptPicIV);
        } else {
            Glide.with(getContext()).load(R.drawable.no_picture).into(receiptPicIV);
            mPage.getData().putString(ExpenseWizardPage2.EXPENSE_RECEIPT_PIC_DATA_KEY, "");
            mPage.getData().putString(ExpenseWizardPage2.EXPENSE_WAS_RECEIPT_PIC_ADDED_DATA_KEY, "No");
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
                Glide.with(this).load(filePath).into(receiptPicIV);
                //this.apartment.setMainPic(filePath);
                mPage.getData().putString(ExpenseWizardPage2.EXPENSE_RECEIPT_PIC_DATA_KEY, filePath);
                mPage.getData().putString(ExpenseWizardPage2.EXPENSE_WAS_RECEIPT_PIC_ADDED_DATA_KEY, "Yes");
                mPage.notifyDataChanged();
                //this.mainPic = filePath;
                //databaseHandler.changeApartmentMainPic(this.apartment);
                //MainActivity5.apartmentList = databaseHandler.getUsersApartments(MainActivity5.user);
                //ApartmentListFragment.apartmentListAdapterNeedsRefreshed = true;
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
}