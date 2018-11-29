package com.rba18.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.android.wizardpager.wizard.model.AbstractWizardModel;
import com.example.android.wizardpager.wizard.model.ModelCallbacks;
import com.example.android.wizardpager.wizard.model.Page;
import com.example.android.wizardpager.wizard.ui.PageFragmentCallbacks;
import com.example.android.wizardpager.wizard.ui.StepPagerStrip;
import com.rba18.R;
import com.rba18.fragments.ReviewFragmentCustom;
import com.rba18.helpers.DateAndCurrencyDisplayer;
import com.rba18.helpers.MainArrayDataMethods;
import com.rba18.model.Apartment;
import com.rba18.model.ExpenseLogEntry;
import com.rba18.model.Lease;
import com.rba18.model.LeaseEditingWizardModel;
import com.rba18.model.LeaseWizardModel;
import com.rba18.model.PaymentLogEntry;
import com.rba18.wizards.LeaseWizardPage1;
import com.rba18.wizards.LeaseWizardPage2;
import com.rba18.wizards.LeaseWizardPage3;
import com.rba18.model.Tenant;
import com.rba18.sqlite.DatabaseHandler;
import com.rba18.wizards.LeaseWizardPage4;
import com.rba18.wizards.LeaseWizardProratedRentPage;

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
        ReviewFragmentCustom.Callbacks,
        ModelCallbacks {
    private ViewPager mPager;
    private MyPagerAdapter mPagerAdapter;
    private boolean mEditingAfterReview;
    private AbstractWizardModel mWizardModel;
    private boolean mConsumePageSelectedEvent;
    private Button mNextButton;
    private Button mPrevButton;
    private DatabaseHandler mDBHandler;
    private MainArrayDataMethods mDataMethods;
    private Lease mLeaseToEdit;
    private List<Page> mCurrentPageSequence;
    private StepPagerStrip mStepPagerStrip;
    private AlertDialog mAlertDialog;

    public void onCreate(Bundle savedInstanceState) {
        setupUserAppTheme(MainActivity.sCurThemeChoice);
        setContentView(R.layout.activity_fragment_wizard);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mLeaseToEdit = extras.getParcelable("mLeaseToEdit");
        } else {
            mLeaseToEdit = null;
        }
        if(mLeaseToEdit == null){
            mWizardModel = new LeaseWizardModel(this);
            this.setTitle(R.string.new_lease_creation);
        } else {
            mWizardModel = new LeaseEditingWizardModel(this);
            this.setTitle(R.string.edit_lease);
        }
        if(extras != null) {
            mWizardModel.preloadData(extras);
        }
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mWizardModel.load(savedInstanceState.getBundle("model"));
        }
        mWizardModel.registerListener(this);
        mDBHandler = new DatabaseHandler(this);
        mDataMethods = new MainArrayDataMethods();
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

        mNextButton = findViewById(R.id.next_button);
        mPrevButton = findViewById(R.id.prev_button);
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
                    BigDecimal rentCost = new BigDecimal(0);
                    BigDecimal deposit = new BigDecimal(0);
                    int paymentFrequencyID = 1;
                    int paymentDateID = 1;
                    if(mWizardModel.findByKey("Page3") != null) {
                        String rentCostString = mWizardModel.findByKey("Page3").getData().getString(LeaseWizardPage3.LEASE_RENT_COST_DATA_KEY);
                        rentCost = new BigDecimal(rentCostString);
                        String depositString = mWizardModel.findByKey("Page2").getData().getString(LeaseWizardPage2.LEASE_DEPOSIT_STRING_DATA_KEY);
                        deposit = new BigDecimal(depositString);
                        paymentFrequencyID = mWizardModel.findByKey("Page3").getData().getInt(LeaseWizardPage3.LEASE_PAYMENT_FREQUENCY_ID_DATA_KEY);
                        if(mWizardModel.findByKey("Page3").getData().getBoolean(LeaseWizardPage3.LEASE_ARE_PAYMENTS_WEEKLY_DATA_KEY)){
                            paymentDateID = mWizardModel.findByKey("Page3").getData().getInt(LeaseWizardPage3.LEASE_DUE_DATE_ID_DATA_KEY);
                        } else {
                            paymentDateID = mWizardModel.findByKey("Page3").getData().getInt(LeaseWizardPage3.LEASE_DUE_DATE_ID_DATA_KEY);
                        }
                    }
                    String notes = mWizardModel.findByKey("Page4").getData().getString(LeaseWizardPage4.LEASE_NOTES_DATA_KEY);
                    if (mLeaseToEdit != null) {
                        mLeaseToEdit.setPrimaryTenantID(primaryTenant.getId());
                        mLeaseToEdit.setSecondaryTenantIDs(secondaryTenantIDs);
                        mLeaseToEdit.setApartmentID(apartment.getId());
                        mLeaseToEdit.setLeaseStart(leaseStartDate);
                        mLeaseToEdit.setLeaseEnd(leaseEndDate);
                        mLeaseToEdit.setNotes(notes);
                        mLeaseToEdit.setMonthlyRentCost(rentCost);
                        mLeaseToEdit.setPaymentDayID(paymentDateID);
                        mLeaseToEdit.setPaymentFreuencyID(paymentFrequencyID);
                        mDBHandler.editLease(mLeaseToEdit);
                        Intent data = new Intent();
                        data.putExtra("editedLeaseID", mLeaseToEdit.getId());
                        setResult(RESULT_OK, data);
                    } else {
                        Lease lease = new Lease(0, primaryTenant.getId(), secondaryTenantIDs, apartment.getId(), leaseStartDate, leaseEndDate,
                                 paymentDateID, rentCost, deposit, paymentFrequencyID, notes);
                        int leaseID = mDBHandler.addLeaseAndReturnID(lease, MainActivity.sUser.getId());
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
                        ArrayList<PaymentLogEntry> paymentsArray;
                        ArrayList<String> paymentDates = mWizardModel.findByKey("Page3").getData().getStringArrayList(LeaseWizardPage3.LEASE_PAYMENT_DATES_ARRAY_DATA_KEY);
                        paymentsArray = createLeasePayments(paymentDates, leaseEndDate, isFirstProrated, proratedFirst, isLastProrated, proratedLast, rentCost, primaryTenant, apartment, leaseID);
                        mDBHandler.addPaymentLogEntryArray(paymentsArray, MainActivity.sUser.getId());
                        createAndSaveDeposit(mDBHandler, leaseStartDate, leaseEndDate, deposit, apartment, primaryTenant, leaseID);
                        setResult(RESULT_OK);
                    }
                    mDataMethods.sortMainApartmentArray();
                    mDataMethods.sortMainTenantArray();
                    MainActivity.sCurrentLeasesList = mDBHandler.getUsersActiveLeases(MainActivity.sUser);
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
        addToolbarBackButton();
        onPageTreeChanged();
        updateBottomBar();
    }

    private ArrayList<PaymentLogEntry> createLeasePayments(ArrayList<String> DatesStringArray, Date leaseEndDate, Boolean isFirstProrated, BigDecimal proratedFirstPayment,
                                                           Boolean isLastProrated, BigDecimal proratedLastPayment, BigDecimal rentCost, Tenant primaryTenant, Apartment apartment, int leaseID) {
        DateFormat formatFrom = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        ArrayList<PaymentLogEntry> paymentLogEntries = new ArrayList<>();
        String apartmentAddress = apartment.getStreet1AndStreet2String();
        String tenantName = primaryTenant.getFirstAndLastNameString();
        int dateFormatCode = preferences.getInt("dateFormat", DateAndCurrencyDisplayer.DATE_MMDDYYYY);
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
                String description = generateAutoRentDescription(paymentDate, paymentEnd, dateFormatCode, tenantName, apartmentAddress, true);
                PaymentLogEntry payment = new PaymentLogEntry(-1, paymentDate, 1, "", primaryTenant.getId(), leaseID, apartment.getId(),
                        proratedFirstPayment, description, "", false);
                paymentLogEntries.add(payment);
            } else if (i == DatesStringArray.size() - 1 && isLastProrated) {
                String description = generateAutoRentDescription(paymentDate, leaseEndDate, dateFormatCode, tenantName, apartmentAddress, true);
                PaymentLogEntry payment = new PaymentLogEntry(-1, paymentDate, 1, "", primaryTenant.getId(), leaseID, apartment.getId(),
                        proratedLastPayment, description, "", false);
                paymentLogEntries.add(payment);
            } else if(i == DatesStringArray.size() - 1 && DatesStringArray.size() != 2) {
                String description = generateAutoRentDescription(paymentDate, leaseEndDate, dateFormatCode, tenantName, apartmentAddress, false);
                PaymentLogEntry payment = new PaymentLogEntry(-1, paymentDate, 1, "", primaryTenant.getId(), leaseID, apartment.getId(),
                        rentCost, description, "", false);
                paymentLogEntries.add(payment);
            } else if(DatesStringArray.size() != 2){
                String description = generateAutoRentDescription(paymentDate, paymentEnd, dateFormatCode, tenantName, apartmentAddress, false);
                PaymentLogEntry payment = new PaymentLogEntry(-1, paymentDate, 1, "", primaryTenant.getId(), leaseID, apartment.getId(),
                        rentCost, description, "", false);
                paymentLogEntries.add(payment);
            }
        }
        return paymentLogEntries;
    }

    private void createAndSaveDeposit(DatabaseHandler databaseHandler, Date startDate, Date endDate, BigDecimal depositAmount,
                                      Apartment apartment, Tenant primaryTenant, int leaseID){
        int dateFormatCode = preferences.getInt("dateFormat", DateAndCurrencyDisplayer.DATE_MMDDYYYY);
        String apartmentAddress = apartment.getStreet1AndStreet2String();
        String tenantName = primaryTenant.getFirstAndLastNameString();
        String description = generateAutoDepositDescription(startDate, endDate, dateFormatCode, tenantName, apartmentAddress, false);
        PaymentLogEntry deposit = new PaymentLogEntry(-1, startDate, 2, "", primaryTenant.getId(), leaseID, apartment.getId(), depositAmount, description, "", false);
        description = generateAutoDepositDescription(startDate, endDate, dateFormatCode, tenantName, apartmentAddress, true);
        ExpenseLogEntry depositWithheld = new ExpenseLogEntry(-1, endDate, depositAmount.multiply(new BigDecimal(-1)), apartment.getId(), leaseID, primaryTenant.getId(), description, 4, "", "", false);
        databaseHandler.addPaymentLogEntry(deposit, MainActivity.sUser.getId());
        databaseHandler.addExpenseLogEntry(depositWithheld, MainActivity.sUser.getId());
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
            if (mLeaseToEdit == null) {
                mNextButton.setText(R.string.create_lease);
            } else {
                mNextButton.setText(R.string.save_changes);
            }
            mNextButton.setBackgroundResource(com.example.android.wizardpager.R.drawable.finish_background);
            mNextButton.setTextAppearance(this, com.example.android.wizardpager.R.style.TextAppearanceFinish);
            mNextButton.setBackgroundColor(fetchPrimaryColor());
        } else {
            mNextButton.setText(mEditingAfterReview
                    ? R.string.review
                    : R.string.next);
            mNextButton.setBackgroundResource(com.example.android.wizardpager.R.drawable.selectable_item_background);
            TypedValue v = new TypedValue();
            getTheme().resolveAttribute(android.R.attr.textAppearanceMedium, v, true);
            mNextButton.setTextAppearance(this, v.resourceId);
            mNextButton.setEnabled(position != mPagerAdapter.getCutOffPage());
        }
        mPrevButton.setVisibility(position <= 0 ? View.INVISIBLE : View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
        showCancelConfirmation();
    }

    public void showCancelConfirmation(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.exit_wizard_confirmation);
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                NewLeaseWizard.this.finish();
            }
        });
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        // create and show the alert dialog
        mAlertDialog = builder.create();
        mAlertDialog.show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mAlertDialog != null){
            mAlertDialog.dismiss();
        }
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

    private String generateAutoRentDescription(Date paymentDate, Date paymentEndDate, int dateFormatCode, String tenantName, String address, boolean isProrated){
        StringBuilder descriptionStringBuilder = new StringBuilder("");
        descriptionStringBuilder.append(getResources().getText(R.string.from_s_cap));
        descriptionStringBuilder.append(tenantName);
        descriptionStringBuilder.append("\n");
        descriptionStringBuilder.append(getResources().getText(R.string.renting_s));
        descriptionStringBuilder.append(address);
        descriptionStringBuilder.append("\n");
        descriptionStringBuilder.append(getResources().getText(R.string.for_s));
        descriptionStringBuilder.append(DateAndCurrencyDisplayer.getDateToDisplay(dateFormatCode, paymentDate));
        descriptionStringBuilder.append(" - ");
        descriptionStringBuilder.append(DateAndCurrencyDisplayer.getDateToDisplay(dateFormatCode, paymentEndDate));
        descriptionStringBuilder.append("\n");
        descriptionStringBuilder.append(getResources().getText(R.string.auto_generated));
        descriptionStringBuilder.append("\n");
        if(isProrated){
            descriptionStringBuilder.append(getResources().getText(R.string.prorated_rent_payment));
        } else {
            descriptionStringBuilder.append(getResources().getText(R.string.rent_payment));
        }
        return descriptionStringBuilder.toString();
    }

    private String generateAutoDepositDescription(Date leaseStartDate, Date leaseEndDate, int dateFormatCode, String tenantName, String address, boolean isReturned){
        StringBuilder descriptionStringBuilder = new StringBuilder("");
        if(isReturned){
            descriptionStringBuilder.append(getResources().getText(R.string.to_s_cap));
        } else {
            descriptionStringBuilder.append(getResources().getText(R.string.from_s_cap));
        }
        descriptionStringBuilder.append(tenantName);
        descriptionStringBuilder.append("\n");
        descriptionStringBuilder.append(getResources().getText(R.string.renting_s));
        descriptionStringBuilder.append(address);
        descriptionStringBuilder.append("\n");
        descriptionStringBuilder.append(getResources().getText(R.string.for_s));
        descriptionStringBuilder.append(DateAndCurrencyDisplayer.getDateToDisplay(dateFormatCode, leaseStartDate));
        descriptionStringBuilder.append(" - ");
        descriptionStringBuilder.append(DateAndCurrencyDisplayer.getDateToDisplay(dateFormatCode, leaseEndDate));
        descriptionStringBuilder.append("\n");
        descriptionStringBuilder.append(getResources().getText(R.string.auto_generated));
        descriptionStringBuilder.append("\n");
        if(isReturned){
            descriptionStringBuilder.append(getResources().getText(R.string.deposit_returned));
        } else {
            descriptionStringBuilder.append(getResources().getText(R.string.deposit));
        }
        return descriptionStringBuilder.toString();
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

        private MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            if (i >= mCurrentPageSequence.size()) {
                return new ReviewFragmentCustom();
            }
            return mCurrentPageSequence.get(i).createFragment();
        }

        @Override
        public int getItemPosition(Object object) {
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

        private void setCutOffPage(int cutOffPage) {
            if (cutOffPage < 0) {
                cutOffPage = Integer.MAX_VALUE;
            }
            mCutOffPage = cutOffPage;
        }

        private int getCutOffPage() {
            return mCutOffPage;
        }
    }
}
