package com.rentbud.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
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
import com.example.android.wizardpager.wizard.ui.ReviewFragment;
import com.example.android.wizardpager.wizard.ui.StepPagerStrip;
import com.example.cody.rentbud.R;
import com.rentbud.fragments.TenantListFragment;
import com.rentbud.helpers.MainArrayDataMethods;
import com.rentbud.model.Tenant;
import com.rentbud.model.TenantWizardModel;
import com.rentbud.wizards.TenantWizardPage1;
import com.rentbud.wizards.TenantWizardPage2;
import com.rentbud.wizards.TenantWizardPage3;
import com.rentbud.sqlite.DatabaseHandler;

import java.util.List;

public class NewTenantWizard extends BaseActivity implements
        PageFragmentCallbacks,
        ReviewFragment.Callbacks,
        ModelCallbacks {
    private ViewPager mPager;
    private MyPagerAdapter mPagerAdapter;

    private boolean mEditingAfterReview;

    private TenantWizardModel mWizardModel;// = new TenantWizardModel(this);

    private boolean mConsumePageSelectedEvent;

    private Button mNextButton;
    private Button mPrevButton;

    private List<Page> mCurrentPageSequence;
    private StepPagerStrip mStepPagerStrip;

    private DatabaseHandler dbhandler;
    private MainArrayDataMethods mainArrayDataMethods;
    public Tenant tenantToEdit;

    public void onCreate(Bundle savedInstanceState) {
        setupUserAppTheme(MainActivity.curThemeChoice);
        setContentView(R.layout.activity_fragment_wizard);
        mWizardModel = new TenantWizardModel(this);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            tenantToEdit = extras.getParcelable("tenantToEdit");
            mWizardModel.preloadData(extras);
        } else {
            tenantToEdit = null;
        }
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mWizardModel.load(savedInstanceState.getBundle("model"));
        }
        mWizardModel.registerListener(this);
        dbhandler = new DatabaseHandler(this);
        mainArrayDataMethods = new MainArrayDataMethods();
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
                    String firstName = mWizardModel.findByKey("Page1").getData().getString(TenantWizardPage1.TENANT_FIRST_NAME_DATA_KEY);
                    String lastName = mWizardModel.findByKey("Page1").getData().getString(TenantWizardPage1.TENANT_LAST_NAME_DATA_KEY);
                    String phone = mWizardModel.findByKey("Page1").getData().getString(TenantWizardPage1.TENANT_PHONE_DATA_KEY);
                    String email = mWizardModel.findByKey("Page1").getData().getString(TenantWizardPage1.TENANT_EMAIL_DATA_KEY);
                    String emergencyFirstName = mWizardModel.findByKey("Page2").getData().getString(TenantWizardPage2.TENANT_EMERGENCY_FIRST_NAME_DATA_KEY);
                    String emergencyLastName = mWizardModel.findByKey("Page2").getData().getString(TenantWizardPage2.TENANT_EMERGENCY_LAST_NAME_DATA_KEY);
                    String emergencyPhone = mWizardModel.findByKey("Page2").getData().getString(TenantWizardPage2.TENANT_EMERGENCY_PHONE_DATA_KEY);
                    String notes = mWizardModel.findByKey("Page3").getData().getString(TenantWizardPage3.TENANT_NOTES_DATA_KEY);
                    //Create new Tenant object with input data and add it to the database

                    if (tenantToEdit != null) {
                        //Is editing
                        Tenant tenant = mainArrayDataMethods.getCachedTenantByTenantID(tenantToEdit.getId());
                        tenant.setFirstName(firstName);
                        tenant.setLastName(lastName);
                        tenant.setPhone(phone);
                        tenant.setEmail(email);
                        tenant.setEmergencyFirstName(emergencyFirstName);
                        tenant.setEmergencyLastName(emergencyLastName);
                        tenant.setEmergencyPhone(emergencyPhone);
                        tenant.setNotes(notes);
                        dbhandler.editTenant(tenant);
                        Intent data = new Intent();
                        data.putExtra("editedTenantID", tenantToEdit.getId());
                        setResult(RESULT_OK, data);
                    } else {
                        //Is new
                        Tenant tenant = new Tenant(-1, firstName, lastName, phone, email, emergencyFirstName, emergencyLastName, emergencyPhone,
                                false, notes, true);
                        dbhandler.addNewTenant(tenant, MainActivity.user.getId());
                        MainActivity.tenantList.add(tenant);
                        setResult(RESULT_OK);
                    }
                    mainArrayDataMethods.sortMainTenantArray();
                    TenantListFragment.tenantListAdapterNeedsRefreshed = true;
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
            if(tenantToEdit == null) {
                mNextButton.setText("Create Tenant");
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
