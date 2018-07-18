package com.rentbud.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
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
import com.rentbud.fragments.IncomeListFragment;
import com.rentbud.model.Apartment;
import com.rentbud.model.IncomeWizardModel;
import com.rentbud.model.Lease;
import com.rentbud.model.Tenant;
import com.rentbud.wizards.IncomeWizardPage1;
import com.rentbud.wizards.IncomeWizardPage2;
import com.rentbud.model.PaymentLogEntry;
import com.rentbud.sqlite.DatabaseHandler;
import com.rentbud.wizards.IncomeWizardPage3;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NewIncomeWizard extends BaseActivity implements
        PageFragmentCallbacks,
        ReviewFragment.Callbacks,
        ModelCallbacks {
    private ViewPager mPager;
    private NewIncomeWizard.MyPagerAdapter mPagerAdapter;

    private boolean mEditingAfterReview;

    private IncomeWizardModel mWizardModel;// = new IncomeWizardModel(this);

    private boolean mConsumePageSelectedEvent;

    private Button mNextButton;
    private Button mPrevButton;

    private List<Page> mCurrentPageSequence;
    private StepPagerStrip mStepPagerStrip;

    private DatabaseHandler dbHandler;
    //private MainArrayDataMethods dataMethods;
    public PaymentLogEntry incomeToEdit;

    public void onCreate(Bundle savedInstanceState) {
        setupUserAppTheme(MainActivity.curThemeChoice);
        setContentView(R.layout.activity_fragment_wizard);
        mWizardModel = new IncomeWizardModel(this);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            incomeToEdit = extras.getParcelable("incomeToEdit");
            mWizardModel.preloadData(extras);
        } else {
            incomeToEdit = null;
        }
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mWizardModel.load(savedInstanceState.getBundle("model"));
        }
        mWizardModel.registerListener(this);
        dbHandler = new DatabaseHandler(this);
        //dataMethods = new MainArrayDataMethods();
        mPagerAdapter = new NewIncomeWizard.MyPagerAdapter(getSupportFragmentManager());
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
                    String dateString = mWizardModel.findByKey("Page1").getData().getString(IncomeWizardPage1.INCOME_DATE_STRING_DATA_KEY);
                    Date date = null;
                    DateFormat formatFrom = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
                    try {
                        date = formatFrom.parse(dateString);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    String amountString = mWizardModel.findByKey("Page1").getData().getString(IncomeWizardPage1.INCOME_AMOUNT_STRING_DATA_KEY);
                    BigDecimal amount = new BigDecimal(amountString);
                    int apartmentID = 0;
                    int tenantID = 0;
                    int leaseID = 0;
                    //if(incomeToEdit != null){
                    //    apartmentID = incomeToEdit.getApartmentID();
                    //}
                    if (mWizardModel.findByKey("Page3") != null) {
                        if (mWizardModel.findByKey("Page3").getData().getParcelable(IncomeWizardPage3.INCOME_RELATED_APT_DATA_KEY) != null) {
                            Apartment apartment = mWizardModel.findByKey("Page3").getData().getParcelable(IncomeWizardPage3.INCOME_RELATED_APT_DATA_KEY);
                            apartmentID = apartment.getId();
                        }
                        if (mWizardModel.findByKey("Page3").getData().getParcelable(IncomeWizardPage3.INCOME_RELATED_TENANT_DATA_KEY) != null) {
                            Tenant tenant = mWizardModel.findByKey("Page3").getData().getParcelable(IncomeWizardPage3.INCOME_RELATED_TENANT_DATA_KEY);
                            tenantID = tenant.getId();
                        }
                        if (mWizardModel.findByKey("Page3").getData().getParcelable(IncomeWizardPage3.INCOME_RELATED_LEASE_DATA_KEY) != null) {
                            Lease lease = mWizardModel.findByKey("Page3").getData().getParcelable(IncomeWizardPage3.INCOME_RELATED_LEASE_DATA_KEY);
                            leaseID = lease.getId();
                        }
                    }
                    String description = mWizardModel.findByKey("Page2").getData().getString(IncomeWizardPage2.INCOME_DESCRIPTION_DATA_KEY);
                    int typeID = mWizardModel.findByKey("Page1").getData().getInt(IncomeWizardPage1.INCOME_TYPE_ID_DATA_KEY);
                    String type = mWizardModel.findByKey("Page1").getData().getString(IncomeWizardPage1.INCOME_TYPE_DATA_KEY);
                    String receiptPic = mWizardModel.findByKey("Page2").getData().getString(IncomeWizardPage2.INCOME_RECEIPT_PIC_DATA_KEY);

                    if (incomeToEdit != null) {
                        incomeToEdit.setDate(date);
                        incomeToEdit.setAmount(amount);
                        incomeToEdit.setTypeID(typeID);
                        incomeToEdit.setTypeLabel(type);
                        incomeToEdit.setDescription(description);
                        incomeToEdit.setReceiptPic(receiptPic);
                        incomeToEdit.setApartmentID(apartmentID);
                        incomeToEdit.setTenantID(tenantID);
                        incomeToEdit.setLeaseID(leaseID);

                        dbHandler.editPaymentLogEntry(incomeToEdit);
                        //dataMethods.sortMainIncomeArray();
                        Intent data = new Intent();
                        data.putExtra("editedIncomeID", incomeToEdit.getId());
                        setResult(RESULT_OK, data);
                    } else {
                        PaymentLogEntry income = new PaymentLogEntry(-1, date, typeID, type, tenantID, leaseID, apartmentID, amount, description, receiptPic);
                        dbHandler.addPaymentLogEntry(income, MainActivity.user.getId());
                        IncomeListFragment.incomeListAdapterNeedsRefreshed = true;
                        setResult(RESULT_OK);
                    }
                    //Create new Tenant object with input data and add it to the database
                    //MainActivity.apartmentList.add(apartment);
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
            if (incomeToEdit == null) {
                mNextButton.setText("Create Income");
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        if (fragments != null) {
            for (Fragment fragment : fragments) {
                fragment.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grants) {
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        if (fragments != null) {
            for (Fragment fragment : fragments) {
                if (fragment != null) {
                    fragment.onRequestPermissionsResult(requestCode, permissions, grants);
                }
            }
        }
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
