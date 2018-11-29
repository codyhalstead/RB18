package com.rba18.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.rba18.model.Apartment;
import com.rba18.model.IncomeEditingWizardModel;
import com.rba18.model.IncomeWizardModel;
import com.rba18.model.Lease;
import com.rba18.model.Tenant;
import com.rba18.wizards.IncomeWizardPage1;
import com.rba18.wizards.IncomeWizardPage2;
import com.rba18.model.PaymentLogEntry;
import com.rba18.sqlite.DatabaseHandler;
import com.rba18.wizards.IncomeWizardPage3;

import java.io.File;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NewIncomeWizard extends BaseActivity implements
        PageFragmentCallbacks,
        ReviewFragmentCustom.Callbacks,
        ModelCallbacks {
    private ViewPager mPager;
    private NewIncomeWizard.MyPagerAdapter mPagerAdapter;
    private boolean mEditingAfterReview;
    private AbstractWizardModel mWizardModel;
    private boolean mConsumePageSelectedEvent;
    private Button mNextButton;
    private Button mPrevButton;
    private List<Page> mCurrentPageSequence;
    private StepPagerStrip mStepPagerStrip;
    private DatabaseHandler mDBHandler;
    private PaymentLogEntry mIncomeToEdit;
    private AlertDialog mAlertDialog;

    public void onCreate(Bundle savedInstanceState) {
        setupUserAppTheme(MainActivity.sCurThemeChoice);
        setContentView(R.layout.activity_fragment_wizard);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mIncomeToEdit = extras.getParcelable("mIncomeToEdit");
        } else {
            mIncomeToEdit = null;
        }
        if(mIncomeToEdit != null){
            mWizardModel = new IncomeEditingWizardModel(this);
            this.setTitle(R.string.edit_income);
        } else {
            mWizardModel = new IncomeWizardModel(this);
            this.setTitle(R.string.new_income_creation);
        }
        if(extras != null){
            mWizardModel.preloadData(extras);
        }
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mWizardModel.load(savedInstanceState.getBundle("model"));
        }
        mWizardModel.registerListener(this);
        mDBHandler = new DatabaseHandler(this);
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
                    if (mIncomeToEdit != null) {
                        mIncomeToEdit.setDate(date);
                        mIncomeToEdit.setAmount(amount);
                        if(typeID != 0) {
                            mIncomeToEdit.setTypeID(typeID);
                        }
                        mIncomeToEdit.setTypeLabel(type);
                        mIncomeToEdit.setDescription(description);
                        mIncomeToEdit.setApartmentID(apartmentID);
                        mIncomeToEdit.setTenantID(tenantID);
                        mIncomeToEdit.setLeaseID(leaseID);
                        mDBHandler.editPaymentLogEntry(mIncomeToEdit);
                        Intent data = new Intent();
                        data.putExtra("editedIncomeID", mIncomeToEdit.getId());
                        setResult(RESULT_OK, data);
                    } else {
                        PaymentLogEntry income = new PaymentLogEntry(-1, date, typeID, type, tenantID, leaseID, apartmentID, amount, description, receiptPic, false);
                        mDBHandler.addPaymentLogEntry(income, MainActivity.sUser.getId());
                        setResult(RESULT_OK);
                    }
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
            if (mIncomeToEdit == null) {
                mNextButton.setText(R.string.create_income);
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
                if(mIncomeToEdit == null) {
                    if (mWizardModel.findByKey("Page2") != null) {
                        if (mWizardModel.findByKey("Page2").getData().getString(IncomeWizardPage2.INCOME_RECEIPT_PIC_DATA_KEY) != null) {
                            String receiptPic = mWizardModel.findByKey("Page2").getData().getString(IncomeWizardPage2.INCOME_RECEIPT_PIC_DATA_KEY);
                            File file = new File(receiptPic);
                            if (file.exists()) {
                                file.delete();
                            }
                        }
                    }
                }
                NewIncomeWizard.this.finish();
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
