package com.rentbud.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.android.wizardpager.wizard.model.AbstractWizardModel;
import com.example.android.wizardpager.wizard.model.ModelCallbacks;
import com.example.android.wizardpager.wizard.model.Page;
import com.example.android.wizardpager.wizard.ui.PageFragmentCallbacks;
import com.example.android.wizardpager.wizard.ui.ReviewFragment;
import com.example.android.wizardpager.wizard.ui.StepPagerStrip;
import com.example.cody.rentbud.R;
import com.rentbud.fragments.ApartmentListFragment;
import com.rentbud.fragments.LeaseListFragment;
import com.rentbud.fragments.TenantListFragment;
import com.rentbud.helpers.MainArrayDataMethods;
import com.rentbud.model.Apartment;
import com.rentbud.model.Lease;
import com.rentbud.model.LeaseWizardModel;
import com.rentbud.model.PaymentLogEntry;
import com.rentbud.wizards.LeaseWizardPage1;
import com.rentbud.wizards.LeaseWizardPage2;
import com.rentbud.wizards.LeaseWizardPage3;
import com.rentbud.model.Tenant;
import com.rentbud.sqlite.DatabaseHandler;
import com.rentbud.wizards.LeaseWizardProratedRentPage;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NewLeaseWizard extends BaseActivity implements
        PageFragmentCallbacks,
        ReviewFragment.Callbacks,
        ModelCallbacks {
    private ViewPager mPager;
    private MyPagerAdapter mPagerAdapter;

    private boolean mEditingAfterReview;

    private AbstractWizardModel mWizardModel;// = new LeaseWizardModel(this);

    private boolean mConsumePageSelectedEvent;

    private Button mNextButton;
    private Button mPrevButton;

    private DatabaseHandler dbhandler;
    MainArrayDataMethods dataMethods;
    public static Lease leaseToEdit;

    private List<Page> mCurrentPageSequence;
    private StepPagerStrip mStepPagerStrip;

    public void onCreate(Bundle savedInstanceState) {
        setupUserAppTheme(MainActivity.curThemeChoice);
        setContentView(R.layout.activity_fragment_wizard);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            NewLeaseWizard.leaseToEdit = extras.getParcelable("leaseToEdit");
        } else {
            NewLeaseWizard.leaseToEdit = null;
        }

        mWizardModel = new LeaseWizardModel(this);
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mWizardModel.load(savedInstanceState.getBundle("model"));
        }

        mWizardModel.registerListener(this);
        dbhandler = new DatabaseHandler(this);
        dataMethods = new MainArrayDataMethods();
        mPagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
        mPager = findViewById(R.id.pager);
        mPager.setAdapter(mPagerAdapter);
        mStepPagerStrip = findViewById(R.id.strip);
        mStepPagerStrip.setOnPageSelectedListener(new StepPagerStrip.OnPageSelectedListener() {
            @Override
            public void onPageStripSelected(int position) {
                position = Math.min(mPagerAdapter.getCount() - 1, position);
                if (mPager.getCurrentItem() != position) {
                    mPager.setCurrentItem(position);
                }
            }
        });

        mNextButton = (Button) findViewById(R.id.next_button);
        mPrevButton = (Button) findViewById(R.id.prev_button);

        mPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                mStepPagerStrip.setCurrentPage(position);

                if (mConsumePageSelectedEvent) {
                    mConsumePageSelectedEvent = false;
                    return;
                }

                mEditingAfterReview = false;
                updateBottomBar();
            }
        });

        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mPager.getCurrentItem() == mCurrentPageSequence.size()) {
                    Tenant primaryTenant = mWizardModel.findByKey("Page2").getData().getParcelable(LeaseWizardPage2.LEASE_PRIMARY_TENANT_DATA_KEY);
                    ArrayList<Tenant> secondaryTenants = mWizardModel.findByKey("Page2").getData().getParcelableArrayList(LeaseWizardPage2.LEASE_SECONDARY_TENANTS_DATA_KEY);
                    ArrayList<Integer> secondaryTenantIDs = new ArrayList<>();
                    if (secondaryTenants != null) {
                        for (int i = 0; i < secondaryTenants.size(); i++) {
                            secondaryTenantIDs.add(secondaryTenants.get(i).getId());
                        }
                    }
                    Apartment apartment = mWizardModel.findByKey("Page1").getData().getParcelable(LeaseWizardPage1.LEASE_APARTMENT_DATA_KEY);
                    String startDateString = mWizardModel.findByKey("Page1").getData().getString(LeaseWizardPage1.LEASE_START_DATE_STRING_DATA_KEY);
                    String endDateString = mWizardModel.findByKey("Page1").getData().getString(LeaseWizardPage1.LEASE_END_DATE_STRING_DATA_KEY);
                    Date leaseStartDate = null;
                    Date leaseEndDate = null;
                    DateFormat formatFrom = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
                    try {
                        leaseStartDate = formatFrom.parse(startDateString);
                        leaseEndDate = formatFrom.parse(endDateString);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    int paymentDay = mWizardModel.findByKey("Page3").getData().getInt(LeaseWizardPage3.LEASE_DUE_DATE_DATA_KEY);
                    String rentCostString = mWizardModel.findByKey("Page3").getData().getString(LeaseWizardPage3.LEASE_RENT_COST_DATA_KEY);
                    BigDecimal rentCost = new BigDecimal(rentCostString);
                    String depositString = mWizardModel.findByKey("Page2").getData().getString(LeaseWizardPage2.LEASE_DEPOSIT_STRING_DATA_KEY);
                    BigDecimal deposit = new BigDecimal(depositString);
                    String depositWithheldString = mWizardModel.findByKey("Page2").getData().getString(LeaseWizardPage2.LEASE_DEPOSIT_WITHHELD_STRING_DATA_KEY);
                    BigDecimal depositWithheld = new BigDecimal(depositWithheldString);
                    String notes = "";

                    if (NewLeaseWizard.leaseToEdit != null) {
                        NewLeaseWizard.leaseToEdit.setPrimaryTenantID(primaryTenant.getId());
                        NewLeaseWizard.leaseToEdit.setSecondaryTenantIDs(secondaryTenantIDs);
                        NewLeaseWizard.leaseToEdit.setApartmentID(apartment.getId());
                        NewLeaseWizard.leaseToEdit.setLeaseStart(leaseStartDate);
                        NewLeaseWizard.leaseToEdit.setLeaseEnd(leaseEndDate);
                        NewLeaseWizard.leaseToEdit.setPaymentDay(paymentDay);
                        NewLeaseWizard.leaseToEdit.setMonthlyRentCost(rentCost);
                        NewLeaseWizard.leaseToEdit.setDeposit(deposit);
                        NewLeaseWizard.leaseToEdit.setDepositWithheld(depositWithheld);
                        NewLeaseWizard.leaseToEdit.setNotes(notes);
                        dbhandler.editLease(NewLeaseWizard.leaseToEdit);
                        Intent data = new Intent();
                        data.putExtra("editedLeaseID", NewLeaseWizard.leaseToEdit.getId());
                        setResult(RESULT_OK, data);
                    } else {
                        Lease lease = new Lease(0, primaryTenant.getId(), secondaryTenantIDs, apartment.getId(), leaseStartDate, leaseEndDate,
                                paymentDay, rentCost, deposit, depositWithheld, notes);
                        int leaseID = dbhandler.addLeaseAndReturnID(lease, MainActivity.user.getId());
                        Boolean isFirstProrated = mWizardModel.findByKey("Page3").getData().getBoolean(LeaseWizardPage3.LEASE_IS_FIRST_PRORATED_REQUIRED_DATA_KEY);
                        Boolean isLastProrated = mWizardModel.findByKey("Page3").getData().getBoolean(LeaseWizardPage3.LEASE_IS_LAST_PRORATED_REQUIRED_DATA_KEY);
                        BigDecimal proratedFirst = new BigDecimal(-1);
                        BigDecimal proratedLast = new BigDecimal(-1);
                        if (mWizardModel.findByKey("Yes:ProratedRentPage") != null) {
                            if (mWizardModel.findByKey("Yes:ProratedRentPage").getData().getString(LeaseWizardProratedRentPage.LEASE_PRORATED_FIRST_PAYMENT_STRING_DATA_KEY) != null) {
                                String proratedFirstString = mWizardModel.findByKey("Yes:ProratedRentPage").getData().getString(LeaseWizardProratedRentPage.LEASE_PRORATED_FIRST_PAYMENT_STRING_DATA_KEY);
                                proratedFirst = new BigDecimal(proratedFirstString);
                            }
                            if (mWizardModel.findByKey("Yes:ProratedRentPage").getData().getString(LeaseWizardProratedRentPage.LEASE_PRORATED_LAST_PAYMENT_STRING_DATA_KEY) != null) {
                                String proratedLastString = mWizardModel.findByKey("Yes:ProratedRentPage").getData().getString(LeaseWizardProratedRentPage.LEASE_PRORATED_LAST_PAYMENT_STRING_DATA_KEY);
                                proratedLast = new BigDecimal(proratedLastString);
                            }
                        }
                        //BigDecimal proratedFirst = new BigDecimal(500);
                        //BigDecimal proratedLast = new BigDecimal(400);
                        ArrayList<PaymentLogEntry> paymentsArray = new ArrayList<>();
                        ArrayList<String> paymentDates = mWizardModel.findByKey("Page3").getData().getStringArrayList(LeaseWizardPage3.LEASE_PAYMENT_DATES_ARRAY_DATA_KEY);

                        paymentsArray = createLeasePayments(paymentDates, leaseEndDate, isFirstProrated, proratedFirst, isLastProrated, proratedLast, rentCost, primaryTenant, apartment, leaseID);
                        dbhandler.addPaymentLogEntryArray(paymentsArray, MainActivity.user.getId());
                        setResult(RESULT_OK);
                    }
                    dataMethods.sortMainApartmentArray();
                    dataMethods.sortMainTenantArray();
                    MainActivity.currentLeasesList = dbhandler.getUsersActiveLeases(MainActivity.user);
                    //MainActivity5.apartmentList = db.getUsersApartments(MainActivity5.user);
                    ApartmentListFragment.apartmentListAdapterNeedsRefreshed = true;
                    TenantListFragment.tenantListAdapterNeedsRefreshed = true;
                    LeaseListFragment.leaseListAdapterNeedsRefreshed = true;
                    finish();

                } else {
                    if (mEditingAfterReview) {
                        mPager.setCurrentItem(mPagerAdapter.getCount() - 1);
                    } else {
                        mPager.setCurrentItem(mPager.getCurrentItem() + 1);
                    }
                }
            }
        });

        mPrevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPager.setCurrentItem(mPager.getCurrentItem() - 1);
            }
        });
        mStepPagerStrip.setProgressColors(fetchPrimaryColor(), fetchPrimaryColor(), fetchAccentColor());
        setupBasicToolbar();
        onPageTreeChanged();
        updateBottomBar();
    }

    private ArrayList<PaymentLogEntry> createLeasePayments(ArrayList<String> DatesStringArray, Date leaseEndDate, Boolean isFirstProrated, BigDecimal proratedFirstPayment,
                                                           Boolean isLastProrated, BigDecimal proratedLastPayment, BigDecimal rentCost, Tenant primaryTenant, Apartment apartment, int leaseID) {
        DateFormat formatFrom = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        SimpleDateFormat timeFormat = new SimpleDateFormat("MM-dd-yyyy", Locale.US);
        ArrayList<PaymentLogEntry> paymentLogEntries = new ArrayList<>();
        String street2 = "";
        if (apartment.getStreet2() != null) {
            street2 = apartment.getStreet2();
        }
        for (int i = 0; i < DatesStringArray.size(); i++) {
            Date paymentDate = null;
            Date paymentEnd = null;
            try {
                paymentDate = formatFrom.parse(DatesStringArray.get(i));
                if (i < DatesStringArray.size() - 1) {
                    paymentEnd = formatFrom.parse(DatesStringArray.get(i + 1));
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (i == 0 && isFirstProrated) {
                PaymentLogEntry payment = new PaymentLogEntry(-1, paymentDate, 1, "", primaryTenant.getId(), leaseID, apartment.getId(),
                        proratedFirstPayment, "Prorated rent payment for dates " + timeFormat.format(paymentDate) + " through " + timeFormat.format(paymentEnd) + " by " + primaryTenant.getFirstName() + " " + primaryTenant.getLastName() + " for renting " + apartment.getStreet1() + " " + street2 + ".",
                        "");
                paymentLogEntries.add(payment);
            } else if (i == DatesStringArray.size() - 1 && isLastProrated) {
                PaymentLogEntry payment = new PaymentLogEntry(-1, paymentDate, 1, "", primaryTenant.getId(), leaseID, apartment.getId(),
                        proratedLastPayment, "Prorated rent payment for dates " + timeFormat.format(paymentDate) + " through " + timeFormat.format(leaseEndDate) + " by " + primaryTenant.getFirstName() + " " + primaryTenant.getLastName() + " for renting " + apartment.getStreet1() + " " + street2 + ".",
                        "");
                paymentLogEntries.add(payment);
            } else if(i == DatesStringArray.size() - 1) {
                PaymentLogEntry payment = new PaymentLogEntry(-1, paymentDate, 1, "", primaryTenant.getId(), leaseID, apartment.getId(),
                        rentCost, "Rent payment for dates " + timeFormat.format(paymentDate) + " through " + timeFormat.format(leaseEndDate) + " by " + primaryTenant.getFirstName() + " " + primaryTenant.getLastName() + " for renting " + apartment.getStreet1() + " " + street2 + ".",
                        "");
                paymentLogEntries.add(payment);
            } else {
                PaymentLogEntry payment = new PaymentLogEntry(-1, paymentDate, 1, "", primaryTenant.getId(), leaseID, apartment.getId(),
                        rentCost, "Rent payment for dates " + timeFormat.format(paymentDate) + " through " + timeFormat.format(paymentEnd) + " by " + primaryTenant.getFirstName() + " " + primaryTenant.getLastName() + " for renting " + apartment.getStreet1() + " " + street2 + ".",
                        "");
                paymentLogEntries.add(payment);
            }


        }
        return paymentLogEntries;
    }

    @Override
    public void onPageTreeChanged() {
        mCurrentPageSequence = mWizardModel.getCurrentPageSequence();
        recalculateCutOffPage();
        mStepPagerStrip.setPageCount(mCurrentPageSequence.size() + 1); // + 1 = review step
        mPagerAdapter.notifyDataSetChanged();
        updateBottomBar();
    }

    private void updateBottomBar() {
        int position = mPager.getCurrentItem();
        if (position == mCurrentPageSequence.size()) {
            if (NewLeaseWizard.leaseToEdit == null) {
                mNextButton.setText("Create Lease");
            } else {
                mNextButton.setText("Save Changes");
            }
            mNextButton.setBackgroundResource(com.example.android.wizardpager.R.drawable.finish_background);
            mNextButton.setTextAppearance(this, com.example.android.wizardpager.R.style.TextAppearanceFinish);
            mNextButton.setBackgroundColor(fetchPrimaryColor());
        } else {
            mNextButton.setText(mEditingAfterReview
                    ? com.example.android.wizardpager.R.string.review
                    : com.example.android.wizardpager.R.string.next);
            mNextButton.setBackgroundResource(com.example.android.wizardpager.R.drawable.selectable_item_background);
            TypedValue v = new TypedValue();
            getTheme().resolveAttribute(android.R.attr.textAppearanceMedium, v, true);
            mNextButton.setTextAppearance(this, v.resourceId);
            mNextButton.setEnabled(position != mPagerAdapter.getCutOffPage());
        }

        mPrevButton.setVisibility(position <= 0 ? View.INVISIBLE : View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mWizardModel.unregisterListener(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBundle("model", mWizardModel.save());
    }

    @Override
    public AbstractWizardModel onGetModel() {
        return mWizardModel;
    }

    @Override
    public void onEditScreenAfterReview(String key) {
        for (int i = mCurrentPageSequence.size() - 1; i >= 0; i--) {
            if (mCurrentPageSequence.get(i).getKey().equals(key)) {
                mConsumePageSelectedEvent = true;
                mEditingAfterReview = true;
                mPager.setCurrentItem(i);
                updateBottomBar();
                break;
            }
        }
    }

    @Override
    public void onPageDataChanged(Page page) {
        if (page.isRequired()) {
            if (recalculateCutOffPage()) {
                mPagerAdapter.notifyDataSetChanged();
                updateBottomBar();
            }
        }
    }

    @Override
    public Page onGetPage(String key) {
        return mWizardModel.findByKey(key);
    }

    private boolean recalculateCutOffPage() {
        // Cut off the pager adapter at first required page that isn't completed
        int cutOffPage = mCurrentPageSequence.size() + 1;
        for (int i = 0; i < mCurrentPageSequence.size(); i++) {
            Page page = mCurrentPageSequence.get(i);
            if (page.isRequired() && !page.isCompleted()) {
                cutOffPage = i;
                break;
            }
        }

        if (mPagerAdapter.getCutOffPage() != cutOffPage) {
            mPagerAdapter.setCutOffPage(cutOffPage);
            return true;
        }

        return false;
    }

    public class MyPagerAdapter extends FragmentStatePagerAdapter {
        private int mCutOffPage;
        private Fragment mPrimaryItem;

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            if (i >= mCurrentPageSequence.size()) {
                return new ReviewFragment();
            }

            return mCurrentPageSequence.get(i).createFragment();
        }

        @Override
        public int getItemPosition(Object object) {
            // TODO: be smarter about this
            if (object == mPrimaryItem) {
                // Re-use the current fragment (its position never changes)
                return POSITION_UNCHANGED;
            }

            return POSITION_NONE;
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);
            mPrimaryItem = (Fragment) object;
        }

        @Override
        public int getCount() {
            if (mCurrentPageSequence == null) {
                return 0;
            }
            return Math.min(mCutOffPage + 1, mCurrentPageSequence.size() + 1);
        }

        public void setCutOffPage(int cutOffPage) {
            if (cutOffPage < 0) {
                cutOffPage = Integer.MAX_VALUE;
            }
            mCutOffPage = cutOffPage;
        }

        public int getCutOffPage() {
            return mCutOffPage;
        }
    }
}
