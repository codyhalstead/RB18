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
import com.rba18.helpers.MainArrayDataMethods;
import com.rba18.model.Apartment;
import com.rba18.model.ApartmentEditingWizardModel;
import com.rba18.model.ApartmentWizardModel;
import com.rba18.wizards.ApartmentWizardPage1;
import com.rba18.wizards.ApartmentWizardPage2;
import com.rba18.wizards.ApartmentWizardPage3;
import com.rba18.sqlite.DatabaseHandler;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class NewApartmentWizard extends BaseActivity implements
        PageFragmentCallbacks,
        ReviewFragmentCustom.Callbacks,
        ModelCallbacks {
    private ViewPager mPager;
    private NewApartmentWizard.MyPagerAdapter mPagerAdapter;
    private boolean mEditingAfterReview;
    private AbstractWizardModel mWizardModel;
    private boolean mConsumePageSelectedEvent;
    private Button mNextButton;
    private Button mPrevButton;
    private List<Page> mCurrentPageSequence;
    private StepPagerStrip mStepPagerStrip;
    private DatabaseHandler mDBHandler;
    private MainArrayDataMethods mMainArrayDataMethods;
    private Apartment mApartmentToEdit;
    private AlertDialog mAlertDialog;

    public void onCreate(Bundle savedInstanceState) {
        setupUserAppTheme(MainActivity.sCurThemeChoice);
        setContentView(R.layout.activity_fragment_wizard);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mApartmentToEdit = extras.getParcelable("mApartmentToEdit");
        } else {
            mApartmentToEdit = null;
        }
        if (mApartmentToEdit != null) {
            mWizardModel = new ApartmentEditingWizardModel(this);
            setTitle(R.string.edit_apartment);
        } else {
            mWizardModel = new ApartmentWizardModel(this);
            setTitle(R.string.new_apartment_creation);
        }
        if (extras != null) {
            mWizardModel.preloadData(extras);
        }
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mWizardModel.load(savedInstanceState.getBundle("model"));
        }
        mWizardModel.registerListener(this);
        mDBHandler = new DatabaseHandler(this);
        mMainArrayDataMethods = new MainArrayDataMethods();
        mPagerAdapter = new NewApartmentWizard.MyPagerAdapter(getSupportFragmentManager());
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
                    String street1 = mWizardModel.findByKey("Page1").getData().getString(ApartmentWizardPage1.APARTMENT_ADDRESS_1_DATA_KEY);
                    String street2 = mWizardModel.findByKey("Page1").getData().getString(ApartmentWizardPage1.APARTMENT_ADDRESS_2_DATA_KEY);
                    String city = mWizardModel.findByKey("Page1").getData().getString(ApartmentWizardPage1.APARTMENT_CITY_DATA_KEY);
                    //int stateID = mWizardModel.findByKey("Page1").getData().getInt(ApartmentWizardPage1.APARTMENT_STATE_ID_DATA_KEY);
                    String state = mWizardModel.findByKey("Page1").getData().getString(ApartmentWizardPage1.APARTMENT_STATE_DATA_KEY);
                    String zip = mWizardModel.findByKey("Page1").getData().getString(ApartmentWizardPage1.APARTMENT_ZIP_DATA_KEY);
                    String description = mWizardModel.findByKey("Page2").getData().getString(ApartmentWizardPage2.APARTMENT_DESCRIPTION_DATA_KEY);
                    String notes = mWizardModel.findByKey("Page2").getData().getString(ApartmentWizardPage2.APARTMENT_NOTES_DATA_KEY);
                    String mainPic = "";
                    ArrayList<String> otherPics = new ArrayList<>();
                    if (mWizardModel.findByKey("Page3") != null) {
                        mainPic = mWizardModel.findByKey("Page3").getData().getString(ApartmentWizardPage3.APARTMENT_MAIN_PIC_DATA_KEY);
                        otherPics = mWizardModel.findByKey("Page3").getData().getStringArrayList(ApartmentWizardPage3.APARTMENT_OTHER_PICS_DATA_KEY);
                    }
                    if (mApartmentToEdit != null) {
                        Apartment apartment = mMainArrayDataMethods.getCachedApartmentByApartmentID(mApartmentToEdit.getId());
                        apartment.setStreet1(street1);
                        apartment.setStreet2(street2);
                        apartment.setCity(city);
                        apartment.setState(state);
                        apartment.setZip(zip);
                        apartment.setDescription(description);
                        apartment.setNotes(notes);
                        mDBHandler.editApartment(apartment, MainActivity.sUser.getId());
                        Intent data = new Intent();
                        data.putExtra("editedApartmentID", mApartmentToEdit.getId());
                        setResult(RESULT_OK, data);
                    } else {
                        Apartment apartment = new Apartment(-1, street1, street2, city, state, zip, description, false,
                                notes, mainPic, otherPics, true);
                        mDBHandler.addNewApartment(apartment, MainActivity.sUser.getId());
                        MainActivity.sApartmentList.add(apartment);
                        setResult(RESULT_OK);
                    }
                    mMainArrayDataMethods.sortMainApartmentArray();
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
            if (mApartmentToEdit == null) {
                mNextButton.setText(R.string.create_apartment);
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

    public void showCancelConfirmation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.exit_wizard_confirmation);
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (mApartmentToEdit != null) {
                    if (mWizardModel.findByKey("Page3") != null) {
                        String mainPic = mWizardModel.findByKey("Page3").getData().getString(ApartmentWizardPage3.APARTMENT_MAIN_PIC_DATA_KEY);
                        ArrayList<String> otherPics = mWizardModel.findByKey("Page3").getData().getStringArrayList(ApartmentWizardPage3.APARTMENT_OTHER_PICS_DATA_KEY);
                        if (mainPic != null) {
                            if (!mainPic.equals("")) {
                                new File(mainPic).delete();
                            }
                        }
                        if (!otherPics.isEmpty()) {
                            for (int z = 0; z < otherPics.size(); z++) {
                                new File(otherPics.get(z)).delete();
                            }
                        }
                    }
                }
                NewApartmentWizard.this.finish();
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
        if (mAlertDialog != null) {
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

    private class MyPagerAdapter extends FragmentStatePagerAdapter {
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
