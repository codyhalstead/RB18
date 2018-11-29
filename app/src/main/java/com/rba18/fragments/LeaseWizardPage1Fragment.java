package com.rba18.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.Scroller;
import android.widget.TextView;

import com.example.android.wizardpager.wizard.ui.PageFragmentCallbacks;
import com.rba18.R;
import com.rba18.activities.MainActivity;
import com.rba18.helpers.CustomDatePickerDialogLauncher;
import com.rba18.helpers.DateAndCurrencyDisplayer;
import com.rba18.helpers.MainArrayDataMethods;
import com.rba18.helpers.TenantApartmentOrLeaseChooserDialog;
import com.rba18.model.Apartment;
import com.rba18.model.Lease;
import com.rba18.sqlite.DatabaseHandler;
import com.rba18.wizards.LeaseWizardPage1;
import com.rba18.model.Tenant;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Months;

import java.text.DateFormat;
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
    private TextView mLeaseStartTV, mLeaseEndTV, mApartmentTV,
            mInvalidDatesTV, mMaxDurationTV, mLeaseWasTV, mMonthsAndTV, mDaysLongTV, mAmountOfMonthsTV, mAmountOfDaysTV;
    private LinearLayout mDurationLL;
    private Date mLeaseStartDate, mLeaseEndDate;
    private Apartment mApartment;
    private ArrayList<Apartment> mAvailableApartments;
    private boolean mIsEdit;
    private DatabaseHandler mDB;
    private MainArrayDataMethods mMainArrayDataMethods;
    private CustomDatePickerDialogLauncher mDatePickerDialogLauncher;
    private TenantApartmentOrLeaseChooserDialog mTenantApartmentOrLeaseChooserDialog;
    private int mDateFormatCode;

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
        mAvailableApartments = new ArrayList<>();
        mDB = new DatabaseHandler(getActivity());
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        mDateFormatCode = preferences.getInt("dateFormat", DateAndCurrencyDisplayer.DATE_MMDDYYYY);
        mMainArrayDataMethods = new MainArrayDataMethods();
        mIsEdit = false;
        Bundle extras = mPage.getData();
        if (extras != null) {
            Lease leaseToEdit = extras.getParcelable("leaseToEdit");
            if (leaseToEdit != null) {
                loadDataForEdit(leaseToEdit);
                mIsEdit = true;
            } else {
                preloadData(extras);
            }
        } else {
            mApartment = null;
            mLeaseStartDate = null;
            mLeaseEndDate = null;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_lease_wizard_page_1, container, false);
        (rootView.findViewById(android.R.id.title)).setVisibility(View.GONE);

        mLeaseStartTV = (rootView.findViewById(R.id.leaseWizardStartDateTV));
        if (mPage.getData().getString(LeaseWizardPage1.LEASE_START_DATE_STRING_DATA_KEY) != null) {
            String dateString = mPage.getData().getString(LeaseWizardPage1.LEASE_START_DATE_STRING_DATA_KEY);
            DateFormat formatFrom = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
            try {
                mLeaseStartDate = formatFrom.parse(dateString);
                mLeaseStartTV.setText(DateAndCurrencyDisplayer.getDateToDisplay(mDateFormatCode, mLeaseStartDate));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        mLeaseEndTV = (rootView.findViewById(R.id.leaseWizardEndDateTV));
        if (mPage.getData().getString(LeaseWizardPage1.LEASE_END_DATE_STRING_DATA_KEY) != null) {
            String dateString = mPage.getData().getString(LeaseWizardPage1.LEASE_END_DATE_STRING_DATA_KEY);
            DateFormat formatFrom = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
            try {
                mLeaseEndDate = formatFrom.parse(dateString);
                mLeaseEndTV.setText(DateAndCurrencyDisplayer.getDateToDisplay(mDateFormatCode, mLeaseEndDate));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        mApartmentTV = (rootView.findViewById(R.id.leaseWizardApartmentTV));
        if (mPage.getData().getString(LeaseWizardPage1.LEASE_APARTMENT_STRING_DATA_KEY) != null) {
            mApartmentTV.setText(mPage.getData().getString(LeaseWizardPage1.LEASE_APARTMENT_STRING_DATA_KEY));
            mApartment = mPage.getData().getParcelable(LeaseWizardPage1.LEASE_APARTMENT_DATA_KEY);
        }
        mApartmentTV.setScroller(new Scroller(getContext()));
        mApartmentTV.setMaxLines(5);
        mApartmentTV.setVerticalScrollBarEnabled(true);
        mApartmentTV.setMovementMethod(new ScrollingMovementMethod());

        mInvalidDatesTV = rootView.findViewById(R.id.leaseWizardInvalidDatesTV);
        mInvalidDatesTV.setVisibility(View.GONE);

        mMaxDurationTV = rootView.findViewById(R.id.leaseWizardMaxDurationTV);
        mMaxDurationTV.setVisibility(View.GONE);

        mLeaseWasTV = rootView.findViewById(R.id.leaseWizardLeaseWasTV);
        mAmountOfMonthsTV = rootView.findViewById(R.id.leaseWizardMonthDurationNumberTV);
        mMonthsAndTV = rootView.findViewById(R.id.leaseWizardMonthsAndTV);
        mAmountOfDaysTV = rootView.findViewById(R.id.leaseWizardDayDurationTV);
        mDaysLongTV = rootView.findViewById(R.id.leaseWizardDaysLongTV);

        mDurationLL = rootView.findViewById(R.id.leaseWizardDurationLL);
        mDurationLL.setVisibility(View.GONE);

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
        if (mApartment != null) {
            curAptID = mApartment.getId();
        }
        for (int i = 0; i < MainActivity.sApartmentList.size(); i++) {
            if (MainActivity.sApartmentList.get(i).isActive() && MainActivity.sApartmentList.get(i).getId() != curAptID) {
                mAvailableApartments.add(MainActivity.sApartmentList.get(i));
            }
        }
        mLeaseStartTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDatePickerDialogLauncher.launchStartDatePickerDialog();
            }
        });
        mLeaseEndTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDatePickerDialogLauncher.launchEndDatePickerDialog();
            }
        });

        mApartmentTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTenantApartmentOrLeaseChooserDialog = new TenantApartmentOrLeaseChooserDialog(getContext(), TenantApartmentOrLeaseChooserDialog.APARTMENT_TYPE, mAvailableApartments);
                mTenantApartmentOrLeaseChooserDialog.show();
                mTenantApartmentOrLeaseChooserDialog.changeCancelBtnText(getContext().getResources().getString(R.string.clear));
                mTenantApartmentOrLeaseChooserDialog.setDialogResult(new TenantApartmentOrLeaseChooserDialog.OnTenantChooserDialogResult() {
                    @Override
                    public void finish(Tenant tenantResult, Apartment apartmentResult, Lease leaseResult) {
                        if (mApartment != null) {
                            mAvailableApartments.add(mApartment);
                        }
                        if (apartmentResult != null) {
                            mAvailableApartments.remove(apartmentResult);
                            mApartment = apartmentResult;
                            mApartmentTV.setText(getApartmentString());
                            mPage.getData().putString(LeaseWizardPage1.LEASE_APARTMENT_STRING_DATA_KEY, mApartmentTV.getText().toString());
                            mPage.getData().putParcelable(LeaseWizardPage1.LEASE_APARTMENT_DATA_KEY, mApartment);
                        } else {
                            mApartment = null;
                            mApartmentTV.setText("");
                            mPage.getData().putString(LeaseWizardPage1.LEASE_APARTMENT_STRING_DATA_KEY, "");
                            mPage.getData().putParcelable(LeaseWizardPage1.LEASE_APARTMENT_DATA_KEY, null);
                        }
                        mMainArrayDataMethods.sortApartmentArrayAlphabetically(mAvailableApartments);
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
        mDatePickerDialogLauncher = new CustomDatePickerDialogLauncher(mLeaseStartDate, mLeaseEndDate, false, getContext());
        mDatePickerDialogLauncher.setDateSelectedListener(new CustomDatePickerDialogLauncher.DateSelectedListener() {
            @Override
            public void onStartDateSelected(Date startDate, Date endDate) {
                mLeaseStartDate = startDate;
                SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
                mLeaseStartTV.setText(DateAndCurrencyDisplayer.getDateToDisplay(mDateFormatCode, mLeaseStartDate));
                mPage.getData().putString(LeaseWizardPage1.LEASE_START_DATE_STRING_FORMATTED_DATA_KEY, DateAndCurrencyDisplayer.getDateToDisplay(mDateFormatCode, mLeaseStartDate));
                mPage.getData().putString(LeaseWizardPage1.LEASE_START_DATE_STRING_DATA_KEY, formatter.format(mLeaseStartDate));
                checkDates();
                mPage.notifyDataChanged();
            }

            @Override
            public void onEndDateSelected(Date startDate, Date endDate) {
                mLeaseEndDate = endDate;
                SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
                mLeaseEndTV.setText(DateAndCurrencyDisplayer.getDateToDisplay(mDateFormatCode, mLeaseEndDate));
                mPage.getData().putString(LeaseWizardPage1.LEASE_END_DATE_STRING_FORMATTED_DATA_KEY, DateAndCurrencyDisplayer.getDateToDisplay(mDateFormatCode, mLeaseEndDate));
                mPage.getData().putString(LeaseWizardPage1.LEASE_END_DATE_STRING_DATA_KEY, formatter.format(mLeaseEndDate));
                checkDates();
                mPage.notifyDataChanged();
            }

            @Override
            public void onDateSelected(Date date) {

            }
        });
        mMainArrayDataMethods.sortApartmentArrayAlphabetically(mAvailableApartments);
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
        if (mLeaseStartTV != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            if (!menuVisible) {
                imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
            }
        }
    }

    private String getApartmentString() {
        if (mApartment != null) {
            StringBuilder builder = new StringBuilder(mApartment.getStreet1());
            if (mApartment.getStreet2() != null) {
                builder.append(" ");
                builder.append(mApartment.getStreet2());
            }
            return builder.toString();
        } else {
            return "";
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mTenantApartmentOrLeaseChooserDialog != null) {
            mTenantApartmentOrLeaseChooserDialog.dismiss();
        }
        mDatePickerDialogLauncher.dismissDatePickerDialog();
    }

    private void checkDates() {
        if (mLeaseStartDate != null && mLeaseEndDate != null) {
            DateTime start = new DateTime(mLeaseStartDate);
            DateTime end = new DateTime(mLeaseEndDate);
            if (mLeaseStartDate.after(mLeaseEndDate) || mLeaseStartDate.equals(mLeaseEndDate)) {
                mDurationLL.setVisibility(View.GONE);
                mInvalidDatesTV.setVisibility(View.VISIBLE);
                mMaxDurationTV.setVisibility(View.GONE);
                mPage.getData().putBoolean(LeaseWizardPage1.LEASE_ARE_DATES_ACCEPTABLE, false);
            } else if (start.plusYears(3).isBefore(end)) {
                mDurationLL.setVisibility(View.GONE);
                mInvalidDatesTV.setVisibility(View.GONE);
                mMaxDurationTV.setVisibility(View.VISIBLE);
                mPage.getData().putBoolean(LeaseWizardPage1.LEASE_ARE_DATES_ACCEPTABLE, false);
            } else {
                mDurationLL.setVisibility(View.VISIBLE);
                mInvalidDatesTV.setVisibility((View.GONE));
                mMaxDurationTV.setVisibility(View.GONE);
                mPage.getData().putBoolean(LeaseWizardPage1.LEASE_ARE_DATES_ACCEPTABLE, true);
                setDurationTextViews();
            }
        }
    }

    private void setDurationTextViews() {
        if (mLeaseStartDate != null && mLeaseEndDate != null) {
            DateTime start = new DateTime(mLeaseStartDate);
            DateTime end = new DateTime(mLeaseEndDate);
            int months = Months.monthsBetween(start, end).getMonths();
            // Subtract this number of months from the end date so we can calculate days
            DateTime remainingDays = end.minusMonths(months);
            // Get days
            int days = Days.daysBetween(start, remainingDays).getDays();
            Date today = Calendar.getInstance().getTime();
            if (mLeaseEndDate.before(today)) {
                mLeaseWasTV.setText(R.string.lease_was);
            } else {
                mLeaseWasTV.setText(R.string.lease_is);
            }
            String monthsString = months + "";
            mAmountOfMonthsTV.setText(monthsString);
            if (months == 1) {
                mMonthsAndTV.setText(R.string.month_and);
            } else {
                mMonthsAndTV.setText(R.string.months_and);
            }
            String daysString = days + "";
            mAmountOfDaysTV.setText(daysString);
            if (days == 1) {
                mDaysLongTV.setText(R.string.day_long);
            } else {
                mDaysLongTV.setText(R.string.days_long);
            }
        }
    }

    private void loadDataForEdit(Lease leaseToEdit) {
        if (!mPage.getData().getBoolean(LeaseWizardPage1.WAS_PRELOADED)) {
            //Start date
            SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
            String startDateString = formatter.format(leaseToEdit.getLeaseStart());
            mPage.getData().putString(LeaseWizardPage1.LEASE_START_DATE_STRING_DATA_KEY, startDateString);
            mLeaseStartDate = leaseToEdit.getLeaseStart();
            mPage.getData().putString(LeaseWizardPage1.LEASE_START_DATE_STRING_FORMATTED_DATA_KEY, DateAndCurrencyDisplayer.getDateToDisplay(mDateFormatCode, mLeaseStartDate));
            //End date
            String endDateString = formatter.format(leaseToEdit.getLeaseEnd());
            mPage.getData().putString(LeaseWizardPage1.LEASE_END_DATE_STRING_DATA_KEY, endDateString);
            mLeaseEndDate = leaseToEdit.getLeaseEnd();
            mPage.getData().putString(LeaseWizardPage1.LEASE_END_DATE_STRING_FORMATTED_DATA_KEY, DateAndCurrencyDisplayer.getDateToDisplay(mDateFormatCode, mLeaseEndDate));
            //checkDates();
            //Apartment
            if (leaseToEdit.getApartmentID() != 0) {
                mApartment = mDB.getApartmentByID(leaseToEdit.getApartmentID(), MainActivity.sUser);
                if (mApartment != null) {
                    //mPage.getData().putInt(IncomeWizardPage3.INCOME_RELATED_APT_ID_DATA_KEY, mApartment.getId());
                    mPage.getData().putString(LeaseWizardPage1.LEASE_APARTMENT_STRING_DATA_KEY, getApartmentString());
                    mPage.getData().putParcelable(LeaseWizardPage1.LEASE_APARTMENT_DATA_KEY, mApartment);
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
                mLeaseStartDate = formatFrom.parse(dateString);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else if (bundle.getString("preloadedStartDate") != null) {
            //Date does not exist, check if need to preload
            String dateString = bundle.getString("preloadedStartDate");
            mPage.getData().putString(LeaseWizardPage1.LEASE_START_DATE_STRING_DATA_KEY, dateString);
            DateFormat formatFrom = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
            try {
                mLeaseStartDate = formatFrom.parse(dateString);
                mPage.getData().putString(LeaseWizardPage1.LEASE_START_DATE_STRING_FORMATTED_DATA_KEY, DateAndCurrencyDisplayer.getDateToDisplay(mDateFormatCode, mLeaseStartDate));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            mLeaseStartDate = null;
        }
    }

    private void preloadEndDate(Bundle bundle) {
        if (mPage.getData().getString(LeaseWizardPage1.LEASE_END_DATE_STRING_DATA_KEY) != null) {
            //If date exists (Was reloaded)
            String dateString = mPage.getData().getString(LeaseWizardPage1.LEASE_END_DATE_STRING_DATA_KEY);
            DateFormat formatFrom = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
            try {
                mLeaseEndDate = formatFrom.parse(dateString);
                mPage.getData().putString(LeaseWizardPage1.LEASE_END_DATE_STRING_FORMATTED_DATA_KEY, DateAndCurrencyDisplayer.getDateToDisplay(mDateFormatCode, mLeaseEndDate));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else if (bundle.getString("preloadedEndDate") != null) {
            //Date does not exist, check if need to preload
            String dateString = bundle.getString("preloadedEndDate");
            mPage.getData().putString(LeaseWizardPage1.LEASE_END_DATE_STRING_DATA_KEY, dateString);
            DateFormat formatFrom = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
            try {
                mLeaseEndDate = formatFrom.parse(dateString);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            mLeaseEndDate = null;
        }
    }

    private void preloadApartment(Bundle bundle) {
        if (mPage.getData().getParcelable(LeaseWizardPage1.LEASE_APARTMENT_DATA_KEY) != null) {
            //and mApartment is not null
            mApartment = mPage.getData().getParcelable(LeaseWizardPage1.LEASE_APARTMENT_DATA_KEY);
        } else if (bundle.getParcelable("preloadedApartment") != null) {
            //If loaded first time with preloaded mApartment
            mApartment = bundle.getParcelable("preloadedApartment");
            //mPage.getData().putInt(IncomeWizardPage3.INCOME_RELATED_APT_ID_DATA_KEY, mApartment.getId());
            mPage.getData().putString(LeaseWizardPage1.LEASE_APARTMENT_STRING_DATA_KEY, getApartmentString());
            mPage.getData().putParcelable(LeaseWizardPage1.LEASE_APARTMENT_DATA_KEY, mApartment);
        } else if (bundle.getInt("preloadedApartmentID") != 0) {
            //If loaded first time with mApartment id
            mApartment = mDB.getApartmentByID(bundle.getInt("preloadedApartmentID"), MainActivity.sUser);
            //mPage.getData().putInt(IncomeWizardPage3.INCOME_RELATED_APT_ID_DATA_KEY, mApartment.getId());
            mPage.getData().putString(LeaseWizardPage1.LEASE_APARTMENT_STRING_DATA_KEY, getApartmentString());
            mPage.getData().putParcelable(LeaseWizardPage1.LEASE_APARTMENT_DATA_KEY, mApartment);
        } else {
            //If no mApartment id
            mApartment = null;
        }
    }

    private void preloadData(Bundle bundle) {
        preloadStartDate(bundle);
        preloadEndDate(bundle);
        //checkDates();
        preloadApartment(bundle);
    }
}
