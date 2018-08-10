package com.rentbud.fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.Scroller;
import android.widget.TextView;

import com.example.android.wizardpager.wizard.ui.PageFragmentCallbacks;
import com.example.cody.rentbud.R;
import com.rentbud.activities.MainActivity;
import com.rentbud.helpers.CustomDatePickerDialogLauncher;
import com.rentbud.helpers.MainArrayDataMethods;
import com.rentbud.helpers.TenantOrApartmentChooserDialog;
import com.rentbud.model.Apartment;
import com.rentbud.model.ExpenseLogEntry;
import com.rentbud.model.Lease;
import com.rentbud.sqlite.DatabaseHandler;
import com.rentbud.wizards.ExpenseWizardPage1;
import com.rentbud.wizards.LeaseWizardPage1;
import com.rentbud.model.Tenant;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Months;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
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
            invalidDatesTV, leaseWasTV, monthsAndTV, daysLongTV, amountOfMonthsTV, amountOfDaysTV;
    private LinearLayout durationLL;
    Date leaseStartDate, leaseEndDate;
    Apartment apartment;
    ArrayList<Apartment> availableApartments;
    boolean isEdit;
    DatabaseHandler db;
    MainArrayDataMethods mainArrayDataMethods;
    CustomDatePickerDialogLauncher datePickerDialogLauncher;
    TenantOrApartmentChooserDialog tenantOrApartmentChooserDialog;

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
        db = new DatabaseHandler(getActivity());
        mainArrayDataMethods = new MainArrayDataMethods();
        isEdit = false;
        Bundle extras = mPage.getData();
        if (extras != null) {
            Lease leaseToEdit = extras.getParcelable("leaseToEdit");
            if (leaseToEdit != null) {
                loadDataForEdit(leaseToEdit);
                isEdit = true;
            } else {
                preloadData(extras);
            }
        } else {
            apartment = null;
            leaseStartDate = null;
            leaseEndDate = null;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_lease_wizard_page_1, container, false);
        (rootView.findViewById(android.R.id.title)).setVisibility(View.GONE);

        leaseStartTV = (rootView.findViewById(R.id.leaseWizardStartDateTV));
        leaseStartTV.setHint(R.string.click_to_select_date);
        if (mPage.getData().getString(LeaseWizardPage1.LEASE_START_DATE_STRING_DATA_KEY) != null) {
            String dateString = mPage.getData().getString(LeaseWizardPage1.LEASE_START_DATE_STRING_DATA_KEY);
            DateFormat formatFrom = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
            try {
                leaseStartDate = formatFrom.parse(dateString);
                leaseStartTV.setText(mPage.getData().getString(LeaseWizardPage1.LEASE_START_DATE_STRING_DATA_KEY));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        //leaseStartTV.setText(mPage.getData().getString(LeaseWizardPage1.LEASE_START_DATE_STRING_DATA_KEY));

        leaseEndTV = (rootView.findViewById(R.id.leaseWizardEndDateTV));
        //leaseEndTV.setText(mPage.getData().getString(LeaseWizardPage1.LEASE_END_DATE_STRING_DATA_KEY));
        leaseEndTV.setHint(R.string.click_to_select_date);
        if (mPage.getData().getString(LeaseWizardPage1.LEASE_END_DATE_STRING_DATA_KEY) != null) {
            String dateString = mPage.getData().getString(LeaseWizardPage1.LEASE_END_DATE_STRING_DATA_KEY);
            DateFormat formatFrom = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
            try {
                leaseEndDate = formatFrom.parse(dateString);
                leaseEndTV.setText(mPage.getData().getString(LeaseWizardPage1.LEASE_END_DATE_STRING_DATA_KEY));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        leaseStartLabelTV = (rootView.findViewById(R.id.leaseWizardStartDateLabelTV));
        //leaseStartLabelTV.setText("*Lease Start Date");

        leaseEndLabelTV = (rootView.findViewById(R.id.leaseWizardEndDateLabelTV));
        //leaseEndLabelTV.setText("*Lease End Date");

        apartmentTV = (rootView.findViewById(R.id.leaseWizardApartmentTV));
        apartmentTV.setHint(R.string.click_to_select_apartment);
        if (mPage.getData().getString(LeaseWizardPage1.LEASE_APARTMENT_STRING_DATA_KEY) != null) {
            apartmentTV.setText(mPage.getData().getString(LeaseWizardPage1.LEASE_APARTMENT_STRING_DATA_KEY));
            apartment = mPage.getData().getParcelable(LeaseWizardPage1.LEASE_APARTMENT_DATA_KEY);
        }
        apartmentTV.setScroller(new Scroller(getContext()));
        apartmentTV.setMaxLines(5);
        apartmentTV.setVerticalScrollBarEnabled(true);
        apartmentTV.setMovementMethod(new ScrollingMovementMethod());

        apartmentLabelTV = (rootView.findViewById(R.id.leaseWizardApartmentLabelTV));
        apartmentLabelTV.setText(R.string.req_apartment);

        invalidDatesTV = rootView.findViewById(R.id.leaseWizardInvalidDatesTV);
        invalidDatesTV.setVisibility(View.GONE);

        leaseWasTV = rootView.findViewById(R.id.leaseWizardLeaseWasTV);
        amountOfMonthsTV = rootView.findViewById(R.id.leaseWizardMonthDurationNumberTV);
        monthsAndTV = rootView.findViewById(R.id.leaseWizardMonthsAndTV);
        amountOfDaysTV = rootView.findViewById(R.id.leaseWizardDayDurationTV);
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
        int curAptID = 0;
        if (apartment != null) {
            curAptID = apartment.getId();
        }
        for (int i = 0; i < MainActivity.apartmentList.size(); i++) {
            if (MainActivity.apartmentList.get(i).isActive() && MainActivity.apartmentList.get(i).getId() != curAptID) {
                availableApartments.add(MainActivity.apartmentList.get(i));
            }
        }
        leaseStartTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerDialogLauncher.launchStartDatePickerDialog();
            }
        });
        leaseEndTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerDialogLauncher.launchEndDatePickerDialog();
            }
        });

        apartmentTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tenantOrApartmentChooserDialog = new TenantOrApartmentChooserDialog(getContext(), TenantOrApartmentChooserDialog.APARTMENT_TYPE, availableApartments);
                tenantOrApartmentChooserDialog.show();
                tenantOrApartmentChooserDialog.changeCancelBtnText(getContext().getResources().getString(R.string.clear));
                tenantOrApartmentChooserDialog.setDialogResult(new TenantOrApartmentChooserDialog.OnTenantChooserDialogResult() {
                    @Override
                    public void finish(Tenant tenantResult, Apartment apartmentResult, Lease leaseResult) {
                        if (apartment != null) {
                            availableApartments.add(apartment);
                        }
                        if (apartmentResult != null) {
                            availableApartments.remove(apartmentResult);
                            apartment = apartmentResult;
                            apartmentTV.setText(getApartmentString());
                            mPage.getData().putString(LeaseWizardPage1.LEASE_APARTMENT_STRING_DATA_KEY, apartmentTV.getText().toString());
                            mPage.getData().putParcelable(LeaseWizardPage1.LEASE_APARTMENT_DATA_KEY, apartment);
                        } else {
                            apartment = null;
                            apartmentTV.setText("");
                            mPage.getData().putString(LeaseWizardPage1.LEASE_APARTMENT_STRING_DATA_KEY, "");
                            mPage.getData().putParcelable(LeaseWizardPage1.LEASE_APARTMENT_DATA_KEY, null);
                        }
                        mainArrayDataMethods.sortApartmentArrayAlphabetically(availableApartments);
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
        datePickerDialogLauncher = new CustomDatePickerDialogLauncher(leaseStartDate, leaseEndDate, false, getContext());
        datePickerDialogLauncher.setDateSelectedListener(new CustomDatePickerDialogLauncher.DateSelectedListener() {
            @Override
            public void onStartDateSelected(Date startDate, Date endDate) {
                leaseStartDate = startDate;
                SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
                leaseStartTV.setText(formatter.format(leaseStartDate));
                mPage.getData().putString(LeaseWizardPage1.LEASE_START_DATE_STRING_DATA_KEY, formatter.format(leaseStartDate));
                checkDates();
                mPage.notifyDataChanged();
            }

            @Override
            public void onEndDateSelected(Date startDate, Date endDate) {
                leaseEndDate = endDate;
                SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
                leaseEndTV.setText(formatter.format(leaseEndDate));
                mPage.getData().putString(LeaseWizardPage1.LEASE_END_DATE_STRING_DATA_KEY, formatter.format(leaseEndDate));
                checkDates();
                mPage.notifyDataChanged();
            }

            @Override
            public void onDateSelected(Date date) {

            }
        });
        mainArrayDataMethods.sortApartmentArrayAlphabetically(availableApartments);
        checkDates();
        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                mPage.notifyDataChanged();
            }
        });
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

    private String getApartmentString() {
        if (apartment != null) {
            StringBuilder builder = new StringBuilder(apartment.getStreet1());
            if (apartment.getStreet2() != null) {
                builder.append(" ");
                builder.append(apartment.getStreet2());
            }
            return builder.toString();
        } else {
            return "";
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(tenantOrApartmentChooserDialog != null){
            tenantOrApartmentChooserDialog.dismiss();
        }
        datePickerDialogLauncher.dismissDatePickerDialog();
    }

    private void checkDates() {
        if (leaseStartDate != null && leaseEndDate != null) {
            if (leaseStartDate.after(leaseEndDate) || leaseStartDate.equals(leaseEndDate)) {
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

    private void setDurationTextViews() {
        if (leaseStartDate != null && leaseEndDate != null) {
            DateTime start = new DateTime(leaseStartDate);
            DateTime end = new DateTime(leaseEndDate);
            int months = Months.monthsBetween(start, end).getMonths();
            // Subtract this number of months from the end date so we can calculate days
            DateTime remainingDays = end.minusMonths(months);
            // Get days
            int days = Days.daysBetween(start, remainingDays).getDays();
            Date today = Calendar.getInstance().getTime();
            if (leaseEndDate.before(today)) {
                leaseWasTV.setText(R.string.lease_was);
            } else {
                leaseWasTV.setText(R.string.lease_is);
            }
            String monthsString = months + "";
            amountOfMonthsTV.setText(monthsString);
            if (months == 1) {
                monthsAndTV.setText(R.string.month_and);
            } else {
                monthsAndTV.setText(R.string.months_and);
            }
            String daysString = days + "";
            amountOfDaysTV.setText(daysString);
            if (days == 1) {
                daysLongTV.setText(R.string.day_long);
            } else {
                daysLongTV.setText(R.string.days_long);
            }
        }
    }

    private void loadDataForEdit(Lease leaseToEdit) {
        if (!mPage.getData().getBoolean(LeaseWizardPage1.WAS_PRELOADED)) {
            //Start date
            SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
            String startDateString = formatter.format(leaseToEdit.getLeaseStart());
            mPage.getData().putString(LeaseWizardPage1.LEASE_START_DATE_STRING_DATA_KEY, startDateString);
            leaseStartDate = leaseToEdit.getLeaseStart();
            //End date
            String endDateString = formatter.format(leaseToEdit.getLeaseEnd());
            mPage.getData().putString(LeaseWizardPage1.LEASE_END_DATE_STRING_DATA_KEY, endDateString);
            leaseEndDate = leaseToEdit.getLeaseEnd();
            //checkDates();
            //Apartment
            if (leaseToEdit.getApartmentID() != 0) {
                apartment = db.getApartmentByID(leaseToEdit.getApartmentID(), MainActivity.user);
                if (apartment != null) {
                    //mPage.getData().putInt(IncomeWizardPage3.INCOME_RELATED_APT_ID_DATA_KEY, apartment.getId());
                    mPage.getData().putString(LeaseWizardPage1.LEASE_APARTMENT_STRING_DATA_KEY, getApartmentString());
                    mPage.getData().putParcelable(LeaseWizardPage1.LEASE_APARTMENT_DATA_KEY, apartment);
                }
            }
            mPage.getData().putBoolean(LeaseWizardPage1.WAS_PRELOADED, true);
        } else {
            preloadData(mPage.getData());
        }
    }

    private void preloadStartDate(Bundle bundle) {
        if (mPage.getData().getString(LeaseWizardPage1.LEASE_START_DATE_STRING_DATA_KEY) != null) {
            //If date exists (Was reloaded)
            String dateString = mPage.getData().getString(LeaseWizardPage1.LEASE_START_DATE_STRING_DATA_KEY);
            DateFormat formatFrom = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
            try {
                leaseStartDate = formatFrom.parse(dateString);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else if (bundle.getString("preloadedStartDate") != null) {
            //Date does not exist, check if need to preload
            String dateString = bundle.getString("preloadedStartDate");
            mPage.getData().putString(LeaseWizardPage1.LEASE_START_DATE_STRING_DATA_KEY, dateString);
            DateFormat formatFrom = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
            try {
                leaseStartDate = formatFrom.parse(dateString);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            leaseStartDate = null;
        }
    }

    private void preloadEndDate(Bundle bundle) {
        if (mPage.getData().getString(LeaseWizardPage1.LEASE_END_DATE_STRING_DATA_KEY) != null) {
            //If date exists (Was reloaded)
            String dateString = mPage.getData().getString(LeaseWizardPage1.LEASE_END_DATE_STRING_DATA_KEY);
            DateFormat formatFrom = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
            try {
                leaseEndDate = formatFrom.parse(dateString);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else if (bundle.getString("preloadedEndDate") != null) {
            //Date does not exist, check if need to preload
            String dateString = bundle.getString("preloadedEndDate");
            mPage.getData().putString(LeaseWizardPage1.LEASE_END_DATE_STRING_DATA_KEY, dateString);
            DateFormat formatFrom = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
            try {
                leaseEndDate = formatFrom.parse(dateString);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            leaseEndDate = null;
        }
    }

    private void preloadApartment(Bundle bundle) {
        if (mPage.getData().getParcelable(LeaseWizardPage1.LEASE_APARTMENT_DATA_KEY) != null) {
            //and apartment is not null
            apartment = mPage.getData().getParcelable(LeaseWizardPage1.LEASE_APARTMENT_DATA_KEY);
        } else if (bundle.getParcelable("preloadedApartment") != null) {
            //If loaded first time with preloaded apartment
            apartment = bundle.getParcelable("preloadedApartment");
            //mPage.getData().putInt(IncomeWizardPage3.INCOME_RELATED_APT_ID_DATA_KEY, apartment.getId());
            mPage.getData().putString(LeaseWizardPage1.LEASE_APARTMENT_STRING_DATA_KEY, getApartmentString());
            mPage.getData().putParcelable(LeaseWizardPage1.LEASE_APARTMENT_DATA_KEY, apartment);
        } else if (bundle.getInt("preloadedApartmentID") != 0) {
            //If loaded first time with apartment id
            apartment = db.getApartmentByID(bundle.getInt("preloadedApartmentID"), MainActivity.user);
            //mPage.getData().putInt(IncomeWizardPage3.INCOME_RELATED_APT_ID_DATA_KEY, apartment.getId());
            mPage.getData().putString(LeaseWizardPage1.LEASE_APARTMENT_STRING_DATA_KEY, getApartmentString());
            mPage.getData().putParcelable(LeaseWizardPage1.LEASE_APARTMENT_DATA_KEY, apartment);
        } else {
            //If no apartment id
            apartment = null;
        }
    }

    private void preloadData(Bundle bundle) {
        preloadStartDate(bundle);
        preloadEndDate(bundle);
        //checkDates();
        preloadApartment(bundle);
    }
}
