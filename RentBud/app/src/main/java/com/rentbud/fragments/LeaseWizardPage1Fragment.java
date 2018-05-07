package com.rentbud.fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.android.wizardpager.wizard.ui.PageFragmentCallbacks;
import com.example.cody.rentbud.R;
import com.rentbud.activities.MainActivity;
import com.rentbud.helpers.TenantOrApartmentChooserDialog;
import com.rentbud.model.Apartment;
import com.rentbud.model.LeaseWizardPage1;
import com.rentbud.model.Tenant;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Months;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class LeaseWizardPage1Fragment extends android.support.v4.app.Fragment {
    private static final String ARG_KEY = "key";

    private PageFragmentCallbacks mCallbacks;
    private String mKey;
    private LeaseWizardPage1 mPage;
    private TextView leaseStartTV, leaseEndTV, leaseStartLabelTV, leaseEndLabelTV, apartmentTV, apartmentLabelTV,
            invalidDatesTV, leaseWasTV, monthsAndTV, daysLongTV, amountOfMonthsTV, amoundOfDaysTV;
    private LinearLayout durationLL;
    private DatePickerDialog.OnDateSetListener dateSetLeaseStartListener, dateSetLeaseEndListener;
    Date leaseStartDate, leaseEndDate;
    Apartment apartment;
    ArrayList<Apartment> availableApartments;

    public static LeaseWizardPage1Fragment create(String key) {
        Bundle args = new Bundle();
        args.putString(ARG_KEY, key);

        LeaseWizardPage1Fragment fragment = new LeaseWizardPage1Fragment();
        fragment.setArguments(args);
        return fragment;
    }

    public LeaseWizardPage1Fragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        mKey = args.getString(ARG_KEY);
        mPage = (LeaseWizardPage1) mCallbacks.onGetPage(mKey);
        availableApartments = new ArrayList<>();
        availableApartments.addAll(MainActivity.apartmentList);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_lease_wizard_page_1, container, false);
        (rootView.findViewById(android.R.id.title)).setVisibility(View.GONE);

        leaseStartTV = (rootView.findViewById(R.id.leaseWizardStartDateTV));
        leaseStartTV.setHint("Click To Select Date");
        leaseStartTV.setText(mPage.getData().getString(LeaseWizardPage1.LEASE_START_DATE_STRING_DATA_KEY));

        leaseEndTV = (rootView.findViewById(R.id.leaseWizardEndDateTV));
        leaseEndTV.setText(mPage.getData().getString(LeaseWizardPage1.LEASE_END_DATE_STRING_DATA_KEY));
        leaseEndTV.setHint("Click To Select Date");

        leaseStartLabelTV = (rootView.findViewById(R.id.leaseWizardStartDateLabelTV));
        leaseStartLabelTV.setText("*Lease Start Date");

        leaseEndLabelTV = (rootView.findViewById(R.id.leaseWizardEndDateLabelTV));
        leaseEndLabelTV.setText("*Lease End Date");

        apartmentTV = (rootView.findViewById(R.id.leaseWizardApartmentTV));
        apartmentTV.setHint("Click To Select Apartment");
        apartmentTV.setText(mPage.getData().getString(LeaseWizardPage1.LEASE_APARTMENT_STRING_DATA_KEY));

        apartmentLabelTV = (rootView.findViewById(R.id.leaseWizardApartmentLabelTV));
        apartmentLabelTV.setText("*Apartment");

        invalidDatesTV = rootView.findViewById(R.id.leaseWizardInvalidDatesTV);
        invalidDatesTV.setVisibility(View.GONE);

        leaseWasTV = rootView.findViewById(R.id.leaseWizardLeaseWasTV);
        amountOfMonthsTV = rootView.findViewById(R.id.leaseWizardMonthDurationNumberTV);
        monthsAndTV = rootView.findViewById(R.id.leaseWizardMonthsAndTV);
        amoundOfDaysTV = rootView.findViewById(R.id.leaseWizardDayDurationTV);
        daysLongTV = rootView.findViewById(R.id.leaseWizardDaysLongTV);

        durationLL = rootView.findViewById(R.id.leaseWizardDurationLL);
        durationLL.setVisibility(View.GONE);

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
        leaseStartTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                if (leaseStartDate != null) {
                    cal.setTime(leaseStartDate);
                }
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dialog = new DatePickerDialog(getContext(), android.R.style.Theme_Holo_Light_Dialog_MinWidth, dateSetLeaseStartListener, year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });
        leaseEndTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                if (leaseEndDate != null) {
                    cal.setTime(leaseEndDate);
                }
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dialog2 = new DatePickerDialog(getContext(), android.R.style.Theme_Holo_Light_Dialog_MinWidth, dateSetLeaseEndListener, year, month, day);
                dialog2.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog2.show();
            }
        });

        apartmentTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TenantOrApartmentChooserDialog dialog = new TenantOrApartmentChooserDialog(getContext(), TenantOrApartmentChooserDialog.APARTMENT_TYPE, availableApartments);
                dialog.show();
                dialog.setDialogResult(new TenantOrApartmentChooserDialog.OnTenantChooserDialogResult() {
                    @Override
                    public void finish(Tenant tenantResult, Apartment apartmentResult) {
                        if (apartment != null) {
                            availableApartments.add(apartment);
                        }
                        availableApartments.remove(apartmentResult);
                        apartment = apartmentResult;
                        setApartmentTextView();
                        mPage.getData().putString(LeaseWizardPage1.LEASE_APARTMENT_STRING_DATA_KEY, apartmentTV.getText().toString());
                        mPage.getData().putParcelable(LeaseWizardPage1.LEASE_APARTMENT_DATA_KEY, apartment);
                        mPage.notifyDataChanged();
                    }
                });
            }

            // mNameView.addTextChangedListener(new TextWatcher() {
            //     @Override
            //     public void beforeTextChanged(CharSequence charSequence, int i, int i1,
            //                                   int i2) {
            //     }

            //     @Override
            //     public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            //     }

            //     @Override
            //     public void afterTextChanged(Editable editable) {
            // mPage.getData().putString(CustomerInfoPage.NAME_DATA_KEY,
            //         (editable != null) ? editable.toString() : null);
            // mPage.notifyDataChanged();
            //     }
            // });

            // mEmailView.addTextChangedListener(new TextWatcher() {
            //    @Override
            //    public void beforeTextChanged(CharSequence charSequence, int i, int i1,
            //                                  int i2) {
            //    }

            //    @Override
            //    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            //    }

            ///    @Override
            //    public void afterTextChanged(Editable editable) {
            // mPage.getData().putString(CustomerInfoPage.EMAIL_DATA_KEY,
            //         (editable != null) ? editable.toString() : null);
            // mPage.notifyDataChanged();
            //   }
            //});

        });
        setUpdateSelectedDateListeners();
    }


    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);

        // In a future update to the support library, this should override setUserVisibleHint
        // instead of setMenuVisibility.
        if (leaseStartTV != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            if (!menuVisible) {
                imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
            }
        }
    }

    private void setUpdateSelectedDateListeners() {
        dateSetLeaseStartListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                //Once user selects date from date picker pop-up,
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.YEAR, year);
                cal.set(Calendar.MONTH, month);
                cal.set(Calendar.DATE, day);
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);

                leaseStartDate = cal.getTime();

                SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
                leaseStartTV.setText(formatter.format(leaseStartDate));
                mPage.getData().putString(LeaseWizardPage1.LEASE_START_DATE_STRING_DATA_KEY, formatter.format(leaseStartDate));
                checkDates();
                mPage.notifyDataChanged();
            }
        };
        dateSetLeaseEndListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                //Once user selects date from date picker pop-up,
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.YEAR, year);
                cal.set(Calendar.MONTH, month);
                cal.set(Calendar.DATE, day);
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);

                leaseEndDate = cal.getTime();

                SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
                leaseEndTV.setText(formatter.format(leaseEndDate));
                mPage.getData().putString(LeaseWizardPage1.LEASE_END_DATE_STRING_DATA_KEY, formatter.format(leaseEndDate));
                checkDates();
                mPage.notifyDataChanged();
            }
        };
    }

    private void setApartmentTextView() {
        apartmentTV.setText(apartment.getStreet1());
        apartmentTV.append(" ");
        apartmentTV.append(apartment.getStreet2());
    }

    private void checkDates(){
        if(leaseStartDate != null && leaseEndDate != null){
            if(leaseStartDate.after(leaseEndDate)){
                durationLL.setVisibility(View.GONE);
                invalidDatesTV.setVisibility(View.VISIBLE);
                mPage.getData().putBoolean(LeaseWizardPage1.LEASE_ARE_DATES_ACCEPTABLE, false);
            } else {
                durationLL.setVisibility(View.VISIBLE);
                invalidDatesTV.setVisibility((View.GONE));
                mPage.getData().putBoolean(LeaseWizardPage1.LEASE_ARE_DATES_ACCEPTABLE, true);
                setDurationTextViews();
            }
        }
    }

    private void setDurationTextViews(){
        if(leaseStartDate != null && leaseEndDate != null){
            DateTime start = new DateTime(leaseStartDate);
            DateTime end = new DateTime(leaseEndDate);
            int months = Months.monthsBetween(start, end).getMonths();
            // Subtract this number of months from the end date so we can calculate days
            DateTime remainingDays = end.minusMonths(months);
            // Get days
            int days = Days.daysBetween(start, remainingDays).getDays() +1;
            Date today = Calendar.getInstance().getTime();
            if(leaseEndDate.before(today)){
                leaseWasTV.setText("Lease Was ");
            } else {
                leaseWasTV.setText("Lease Is ");
            }
            amountOfMonthsTV.setText(months + "");
            if(months == 1){
                monthsAndTV.setText(" Month And ");
            } else {
                monthsAndTV.setText(" Months And ");
            }
            amoundOfDaysTV.setText(days + "");
            if(days == 1){
                daysLongTV.setText(" Day Long");
            } else {
                daysLongTV.setText(" Days Long");
            }
        }
    }
}
