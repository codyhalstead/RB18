package com.rentbud.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.rentbud.fragments.ApartmentListFragment;
import com.rentbud.helpers.MainArrayDataMethods;
import com.rentbud.model.Apartment;
import com.rentbud.model.ApartmentWizardModel;
import com.rentbud.wizards.ApartmentWizardPage1;
import com.rentbud.wizards.ApartmentWizardPage2;
import com.rentbud.wizards.ApartmentWizardPage3;
import com.rentbud.sqlite.DatabaseHandler;

import java.util.ArrayList;
import java.util.List;

public class NewApartmentWizard extends BaseActivity implements
        PageFragmentCallbacks,
        ReviewFragment.Callbacks,
        ModelCallbacks {
    private ViewPager mPager;
    private NewApartmentWizard.MyPagerAdapter mPagerAdapter;

    private boolean mEditingAfterReview;

    private ApartmentWizardModel mWizardModel;// = new ApartmentWizardModel(this);

    private boolean mConsumePageSelectedEvent;

    private Button mNextButton;
    private Button mPrevButton;

    private List<Page> mCurrentPageSequence;
    private StepPagerStrip mStepPagerStrip;

    private DatabaseHandler dbhandler;
    private MainArrayDataMethods mainArrayDataMethods;
    public Apartment apartmentToEdit;

    public void onCreate(Bundle savedInstanceState) {
        setupUserAppTheme(MainActivity.curThemeChoice);
        setContentView(R.layout.activity_fragment_wizard);
        mWizardModel = new ApartmentWizardModel(this);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            apartmentToEdit = extras.getParcelable("apartmentToEdit");
            mWizardModel.preloadData(extras);
        } else {
            apartmentToEdit = null;
        }
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mWizardModel.load(savedInstanceState.getBundle("model"));
        }
        mWizardModel.registerListener(this);
        dbhandler = new DatabaseHandler(this);
        mainArrayDataMethods = new MainArrayDataMethods();
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
                    String street1 = mWizardModel.findByKey("Page1").getData().getString(ApartmentWizardPage1.APARTMENT_ADDRESS_1_DATA_KEY);
                    String street2 = mWizardModel.findByKey("Page1").getData().getString(ApartmentWizardPage1.APARTMENT_ADDRESS_2_DATA_KEY);
                    String city = mWizardModel.findByKey("Page1").getData().getString(ApartmentWizardPage1.APARTMENT_CITY_DATA_KEY);
                    int stateID = mWizardModel.findByKey("Page1").getData().getInt(ApartmentWizardPage1.APARTMENT_STATE_ID_DATA_KEY);
                    String state = mWizardModel.findByKey("Page1").getData().getString(ApartmentWizardPage1.APARTMENT_STATE_DATA_KEY);
                    String zip = mWizardModel.findByKey("Page1").getData().getString(ApartmentWizardPage1.APARTMENT_ZIP_DATA_KEY);
                    String description = mWizardModel.findByKey("Page2").getData().getString(ApartmentWizardPage2.APARTMENT_DESCRIPTION_DATA_KEY);
                    String notes = mWizardModel.findByKey("Page2").getData().getString(ApartmentWizardPage2.APARTMENT_NOTES_DATA_KEY);
                    String mainPic = mWizardModel.findByKey("Page3").getData().getString(ApartmentWizardPage3.APARTMENT_MAIN_PIC_DATA_KEY);
                    ArrayList<String> otherPics = mWizardModel.findByKey("Page3").getData().getStringArrayList(ApartmentWizardPage3.APARTMENT_OTHER_PICS_DATA_KEY);
                    //Create new Tenant object with input data and add it to the database
                    if(apartmentToEdit != null){
                        Apartment apartment = mainArrayDataMethods.getCachedApartmentByApartmentID(apartmentToEdit.getId());
                        apartment.setStreet1(street1);
                        apartment.setStreet2(street2);
                        apartment.setCity(city);
                        apartment.setStateID(stateID);
                        apartment.setState(state);
                        apartment.setZip(zip);
                        apartment.setDescription(description);
                        apartment.setNotes(notes);
                        apartment.setMainPic(mainPic);
                        apartment.setOtherPics(otherPics);
                        //TODO handle changing otherPics in the database, will not do currently
                        dbhandler.editApartment(apartment, MainActivity.user.getId());
                        Intent data = new Intent();
                        data.putExtra("editedApartmentID", apartmentToEdit.getId());
                        setResult(RESULT_OK, data);
                    } else {
                        Apartment apartment = new Apartment(-1, street1, street2, city, stateID, state, zip, description, false,
                                notes, mainPic, otherPics, true);
                        dbhandler.addNewApartment(apartment, MainActivity.user.getId());
                        MainActivity.apartmentList.add(apartment);
                        setResult(RESULT_OK);
                    }
                    mainArrayDataMethods.sortMainApartmentArray();
                    //ApartmentListFragment.apartmentListAdapterNeedsRefreshed = true;
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
            if(apartmentToEdit == null) {
                mNextButton.setText("Create Apartment");
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
        if(fragments != null){
            for(Fragment fragment : fragments){
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
