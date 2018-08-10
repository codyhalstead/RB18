package com.rentbud.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
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
import com.rentbud.model.ExpenseLogEntry;
import com.rentbud.model.PaymentLogEntry;
import com.rentbud.wizards.ExpenseWizardPage2;
import com.rentbud.wizards.IncomeWizardPage2;

import static android.app.Activity.RESULT_OK;

public class IncomeWizardPage2Fragment extends android.support.v4.app.Fragment {
    private static final String ARG_KEY = "key";

    private PageFragmentCallbacks mCallbacks;
    private String mKey;
    private IncomeWizardPage2 mPage;
    private ImageView receiptPicIV;
    private Button changeReceiptPicBtn, removeReceiptPicBtn;
    private EditText descriptionET;

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
        Bundle extras = mPage.getData();
        if (extras != null) {
            PaymentLogEntry incomeToEdit = extras.getParcelable("incomeToEdit");
            if (incomeToEdit != null) {
                loadDataForEdit(incomeToEdit);
            } else {
                mPage.getData().putString(IncomeWizardPage2.INCOME_RECEIPT_PIC_DATA_KEY, "");
                mPage.getData().putString(IncomeWizardPage2.INCOME_WAS_RECEIPT_PIC_ADDED_DATA_KEY, getContext().getResources().getString(R.string.no));
            }
        } else {
            mPage.getData().putString(IncomeWizardPage2.INCOME_RECEIPT_PIC_DATA_KEY, "");
            mPage.getData().putString(IncomeWizardPage2.INCOME_WAS_RECEIPT_PIC_ADDED_DATA_KEY, getContext().getResources().getString(R.string.no));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_income_wizard_page_2, container, false);
        (rootView.findViewById(android.R.id.title)).setVisibility(View.GONE);
        receiptPicIV = rootView.findViewById(R.id.incomeWizardMainPicIV);
        changeReceiptPicBtn = rootView.findViewById(R.id.incomeWizardChangePicBtn);
        removeReceiptPicBtn = rootView.findViewById(R.id.incomeWizardRemovePicBtn);

        descriptionET = rootView.findViewById(R.id.incomeWizardDescriptionET);
        descriptionET.setText(mPage.getData().getString(IncomeWizardPage2.INCOME_DESCRIPTION_DATA_KEY));
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
                mPage.getData().putString(IncomeWizardPage2.INCOME_RECEIPT_PIC_DATA_KEY, "");
                mPage.getData().putString(IncomeWizardPage2.INCOME_WAS_RECEIPT_PIC_ADDED_DATA_KEY, getContext().getResources().getString(R.string.no));
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
                mPage.getData().putString(IncomeWizardPage2.INCOME_DESCRIPTION_DATA_KEY, editable.toString());
                mPage.notifyDataChanged();
            }
        });
        if (mPage.getData().getString(IncomeWizardPage2.INCOME_RECEIPT_PIC_DATA_KEY) != null) {
            if(mPage.getData().getString(IncomeWizardPage2.INCOME_RECEIPT_PIC_DATA_KEY).equals("")){
                Glide.with(getContext()).load(R.drawable.no_picture).into(receiptPicIV);
                mPage.getData().putString(IncomeWizardPage2.INCOME_WAS_RECEIPT_PIC_ADDED_DATA_KEY, getContext().getResources().getString(R.string.no));
            } else {
                Glide.with(getContext()).load(mPage.getData().getString(IncomeWizardPage2.INCOME_RECEIPT_PIC_DATA_KEY)).into(receiptPicIV);
            }
        } else {
            Glide.with(getContext()).load(R.drawable.no_picture).into(receiptPicIV);
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
                mPage.getData().putString(IncomeWizardPage2.INCOME_RECEIPT_PIC_DATA_KEY, filePath);
                mPage.getData().putString(IncomeWizardPage2.INCOME_WAS_RECEIPT_PIC_ADDED_DATA_KEY, getContext().getResources().getString(R.string.yes));
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


    //private void preloadData(Bundle bundle){
    //
    //}

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
                mPage.getData().putBoolean(ExpenseWizardPage2.WAS_PRELOADED, true);
            }
        }
    }
}