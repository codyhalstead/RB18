package com.rentbud.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
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
import com.rentbud.model.LeaseWizardPage1;
import com.rentbud.model.LeaseWizardPage2;
import com.rentbud.model.LeaseWizardPage3;
import com.rentbud.model.Tenant;
import com.rentbud.sqlite.DatabaseHandler;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NewLeaseWizard extends FragmentActivity implements
        PageFragmentCallbacks,
        ReviewFragment.Callbacks,
        ModelCallbacks {
    private ViewPager mPager;
    private MyPagerAdapter mPagerAdapter;

    private boolean mEditingAfterReview;

    private AbstractWizardModel mWizardModel = new LeaseWizardModel(this);

    private boolean mConsumePageSelectedEvent;

    private Button mNextButton;
    private Button mPrevButton;

    private DatabaseHandler dbhandler;

    private List<Page> mCurrentPageSequence;
    private StepPagerStrip mStepPagerStrip;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_new_lease_wizard);

        if (savedInstanceState != null) {
            mWizardModel.load(savedInstanceState.getBundle("model"));
        }

        mWizardModel.registerListener(this);
        dbhandler = new DatabaseHandler(this);
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
                   // DialogFragment dg = new DialogFragment() {
                   //     @Override
                   //     public Dialog onCreateDialog(Bundle savedInstanceState) {
                   //         return new AlertDialog.Builder(getActivity())
                   //                 .setMessage(com.example.android.wizardpager.R.string.submit_confirm_message)
                   //                 .setPositiveButton(com.example.android.wizardpager.R.string.submit_confirm_button, null)
                   //                 .setNegativeButton(android.R.string.cancel, null)
                   //                 .create();
                   //     }
                   // };
                   // dg.show(getSupportFragmentManager(), "place_order_dialog");
                    Log.d("TAG", "onClick: WAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAa");
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

                    Lease lease = new Lease(0, primaryTenant.getId(), secondaryTenantIDs, apartment.getId(), leaseStartDate, leaseEndDate,
                            paymentDay, rentCost, deposit, depositWithheld, "");

                    dbhandler.addLease(lease, MainActivity.user.getId());
                    MainArrayDataMethods dataMethods = new MainArrayDataMethods();
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

        onPageTreeChanged();
        updateBottomBar();
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
            mNextButton.setText("Create Lease");
            mNextButton.setBackgroundResource(com.example.android.wizardpager.R.drawable.finish_background);
            mNextButton.setTextAppearance(this, com.example.android.wizardpager.R.style.TextAppearanceFinish);
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
