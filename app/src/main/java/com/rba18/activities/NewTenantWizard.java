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
import com.rba18.helpers.MainArrayDataMethods;
import com.rba18.model.Tenant;
import com.rba18.model.TenantWizardModel;
import com.rba18.wizards.TenantWizardPage1;
import com.rba18.wizards.TenantWizardPage2;
import com.rba18.wizards.TenantWizardPage3;
import com.rba18.sqlite.DatabaseHandler;

import java.util.List;

public class NewTenantWizard extends BaseActivity implements
        PageFragmentCallbacks,
        ReviewFragmentCustom.Callbacks,
        ModelCallbacks {
    private ViewPager mPager;
    private MyPagerAdapter mPagerAdapter;
    private boolean mEditingAfterReview;
    private TenantWizardModel mWizardModel;
    private boolean mConsumePageSelectedEvent;
    private Button mNextButton;
    private Button mPrevButton;
    private List<Page> mCurrentPageSequence;
    private StepPagerStrip mStepPagerStrip;
    private DatabaseHandler mDBHandler;
    private MainArrayDataMethods mMainArrayDataMethods;
    private Tenant mTenantToEdit;
    private AlertDialog mAlertDialog;

    public void onCreate(Bundle savedInstanceState) {
        setupUserAppTheme(MainActivity.sCurThemeChoice);
        setContentView(R.layout.activity_fragment_wizard);
        mWizardModel = new TenantWizardModel(this);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mTenantToEdit = extras.getParcelable("mTenantToEdit");
            mWizardModel.preloadData(extras);
            this.setTitle(R.string.edit_tenant);
        } else {
            mTenantToEdit = null;
            this.setTitle(R.string.new_tenant_creation);
        }
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mWizardModel.load(savedInstanceState.getBundle("model"));
        }
        mWizardModel.registerListener(this);
        mDBHandler = new DatabaseHandler(this);
        mMainArrayDataMethods = new MainArrayDataMethods();
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
                    String firstName = mWizardModel.findByKey("Page1").getData().getString(TenantWizardPage1.TENANT_FIRST_NAME_DATA_KEY);
                    String lastName = mWizardModel.findByKey("Page1").getData().getString(TenantWizardPage1.TENANT_LAST_NAME_DATA_KEY);
                    String phone = mWizardModel.findByKey("Page1").getData().getString(TenantWizardPage1.TENANT_PHONE_DATA_KEY);
                    String email = mWizardModel.findByKey("Page1").getData().getString(TenantWizardPage1.TENANT_EMAIL_DATA_KEY);
                    String emergencyFirstName = mWizardModel.findByKey("Page2").getData().getString(TenantWizardPage2.TENANT_EMERGENCY_FIRST_NAME_DATA_KEY);
                    String emergencyLastName = mWizardModel.findByKey("Page2").getData().getString(TenantWizardPage2.TENANT_EMERGENCY_LAST_NAME_DATA_KEY);
                    String emergencyPhone = mWizardModel.findByKey("Page2").getData().getString(TenantWizardPage2.TENANT_EMERGENCY_PHONE_DATA_KEY);
                    String notes = mWizardModel.findByKey("Page3").getData().getString(TenantWizardPage3.TENANT_NOTES_DATA_KEY);
                    if (mTenantToEdit != null) {
                        //Is editing
                        Tenant tenant = mMainArrayDataMethods.getCachedTenantByTenantID(mTenantToEdit.getId());
                        tenant.setFirstName(firstName);
                        tenant.setLastName(lastName);
                        tenant.setPhone(phone);
                        tenant.setEmail(email);
                        tenant.setEmergencyFirstName(emergencyFirstName);
                        tenant.setEmergencyLastName(emergencyLastName);
                        tenant.setEmergencyPhone(emergencyPhone);
                        tenant.setNotes(notes);
                        mDBHandler.editTenant(tenant);
                        Intent data = new Intent();
                        data.putExtra("editedTenantID", mTenantToEdit.getId());
                        setResult(RESULT_OK, data);
                    } else {
                        //Is new
                        Tenant tenant = new Tenant(-1, firstName, lastName, phone, email, emergencyFirstName, emergencyLastName, emergencyPhone,
                                false, notes, true);
                        mDBHandler.addNewTenant(tenant, MainActivity.sUser.getId());
                        MainActivity.sTenantList.add(tenant);
                        setResult(RESULT_OK);
                    }
                    mMainArrayDataMethods.sortMainTenantArray();
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
            if(mTenantToEdit == null) {
                mNextButton.setText(R.string.create_tenant);
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
                NewTenantWizard.this.finish();
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
