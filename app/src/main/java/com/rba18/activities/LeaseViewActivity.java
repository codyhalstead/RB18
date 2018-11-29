package com.rba18.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.InputFilter;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.rba18.R;
import com.rba18.fragments.LeaseViewFrag1;
import com.rba18.fragments.LeaseViewFrag2;
import com.rba18.model.Lease;
import com.rba18.sqlite.DatabaseHandler;

import java.util.List;

public class LeaseViewActivity extends BaseActivity implements LeaseViewFrag2.OnMoneyDataChangedListener {
    private Lease mLease;
    private DatabaseHandler mDatabaseHandler;
    private LeaseViewFrag1 mFrag1;
    private LeaseViewFrag2 mFrag2;
    private boolean mWasLeaseEdited, mWasIncomeEdited, mWasExpenseEdited;
    private AlertDialog mAlertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupUserAppTheme(MainActivity.sCurThemeChoice);
        setContentView(R.layout.activity_lease_view_actual);
        LinearLayout dateSelectorLL = findViewById(R.id.moneyDateSelecterLL);
        dateSelectorLL.setVisibility(View.GONE);
        ViewPager viewPager = findViewById(R.id.pager);
        LeaseViewActivity.ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        mDatabaseHandler = new DatabaseHandler(this);
        Bundle bundle = getIntent().getExtras();
        int leaseID = bundle.getInt("leaseID");
        mLease = mDatabaseHandler.getLeaseByID(MainActivity.sUser, leaseID);
        bundle.putParcelable("mLease", mLease);
        viewPager.setAdapter(adapter);
        if (savedInstanceState != null) {
            mWasLeaseEdited = savedInstanceState.getBoolean("was_lease_edited");
            mWasIncomeEdited = savedInstanceState.getBoolean("was_income_edited");
            mWasExpenseEdited = savedInstanceState.getBoolean("was_expense_edited");
        } else {
            mWasLeaseEdited = false;
            mWasIncomeEdited = false;
            mWasExpenseEdited = false;
        }
        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        setupBasicToolbar();
        addToolbarBackButton();
        setTitle(R.string.lease_view);
        if (mWasLeaseEdited || mWasIncomeEdited || mWasExpenseEdited) {
            setResultToEdited();
        } else {
            setResult(RESULT_OK);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Get options menu
        getMenuInflater().inflate(R.menu.lease_view_edit_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    //Handle option menu actions
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.editLease:
                Intent intent = new Intent(this, NewLeaseWizard.class);
                intent.putExtra("leaseToEdit", mLease);
                mWasLeaseEdited = true;
                setResultToEdited();
                setResult(MainActivity.RESULT_DATA_WAS_MODIFIED);
                startActivityForResult(intent, MainActivity.REQUEST_NEW_LEASE_FORM);
                return true;

            case R.id.editNotes:
                showEditNotesDialog();
                return true;


            case R.id.deleteLease:
                showDeleteConfirmationAlertDialog();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void showDeleteConfirmationAlertDialog() {
        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //builder.setTitle("AlertDialog");
        builder.setMessage(R.string.lease_deletion_confirmation);

        // add the buttons
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mDatabaseHandler.setLeaseInactive(mLease);
                MainActivity.sApartmentList = mDatabaseHandler.getUsersApartmentsIncludingInactive(MainActivity.sUser);
                MainActivity.sTenantList = mDatabaseHandler.getUsersTenantsIncludingInactive(MainActivity.sUser);
                showDeleteAllRelatedMoneyAlertDialog();
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
    public void onMoneyDataChanged() {
        mFrag2.updateData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mWasLeaseEdited || mWasIncomeEdited || mWasExpenseEdited) {
            setResultToEdited();
        } else {
            setResult(RESULT_OK);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(mAlertDialog != null){
            mAlertDialog.dismiss();
        }
    }


    private void setResultToEdited() {
        Intent intent = new Intent();
        intent.putExtra("was_lease_edited", mWasLeaseEdited);
        intent.putExtra("was_income_edited", mWasIncomeEdited);
        intent.putExtra("was_expense_edited", mWasExpenseEdited);
        setResult(MainActivity.RESULT_DATA_WAS_MODIFIED, intent);
    }

    public void showDeleteAllRelatedMoneyAlertDialog() {
        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //builder.setTitle("AlertDialog");
        builder.setMessage(R.string.lease_related_money_deletion_confirmation);

        // add the buttons
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mDatabaseHandler.setAllExpensesRelatedToLeaseInactive(mLease.getId());
                mDatabaseHandler.setAllIncomeRelatedToLeaseInactive(mLease.getId());
                mWasLeaseEdited = true;
                mWasExpenseEdited = true;
                mWasIncomeEdited = true;
                setResultToEdited();
                LeaseViewActivity.this.finish();
            }
        });
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mWasLeaseEdited = true;
                setResultToEdited();
                LeaseViewActivity.this.finish();
            }
        });

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        setResult(MainActivity.RESULT_DATA_WAS_MODIFIED);
        if (requestCode == MainActivity.REQUEST_NEW_LEASE_FORM) {
            //If successful(not cancelled, passed validation)
            if (resultCode == RESULT_OK) {
                mLease = mDatabaseHandler.getLeaseByID(MainActivity.sUser, mLease.getId());
                List<Fragment> fragments = getSupportFragmentManager().getFragments();
                if (fragments != null) {
                    for (Fragment fragment : fragments) {
                        if (fragment != null) {
                            fragment.onActivityResult(requestCode, resultCode, data);
                        }
                    }
                }
            }

        } else {
            List<Fragment> fragments = getSupportFragmentManager().getFragments();
            if (fragments != null) {
                for (Fragment fragment : fragments) {
                    if (fragment != null) {
                        fragment.onActivityResult(requestCode, resultCode, data);
                    }
                }
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("was_lease_edited", mWasLeaseEdited);
        outState.putBoolean("was_income_edited", mWasIncomeEdited);
        outState.putBoolean("was_expense_edited", mWasExpenseEdited);
    }

    @Override
    public void onIncomeDataChanged() {
        mWasIncomeEdited = true;
        setResultToEdited();
    }

    @Override
    public void onExpenseDataChanged() {
        mWasExpenseEdited = true;
        setResultToEdited();
    }

    public void showEditNotesDialog() {
        final EditText editText = new EditText(LeaseViewActivity.this);
        int maxLength = 500;
        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
        editText.setSingleLine(false);
        editText.setImeOptions(EditorInfo.IME_FLAG_NO_ENTER_ACTION);
        editText.setText(mLease.getNotes());
        editText.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        editText.setSelection(editText.getText().length());

        // create the AlertDialog as final
        mAlertDialog = new AlertDialog.Builder(LeaseViewActivity.this)
                .setTitle(R.string.edit_notes)
                .setView(editText)

                // Set the action buttons
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String input = editText.getText().toString();
                        mLease.setNotes(input);
                        mDatabaseHandler.editLease(mLease);
                        mWasLeaseEdited = true;
                        setResultToEdited();
                        //viewModel.setApartment(apartment);
                        if (mFrag1 != null) {
                            mFrag1.updateLeaseData(mLease);
                        }
                    }
                })

                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // removes the AlertDialog in the screen
                    }
                })
                .create();

        mAlertDialog.show();
    }

    // Adapter for the viewpager using FragmentPagerAdapter
    class ViewPagerAdapter extends FragmentStatePagerAdapter {

        private ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            Bundle bundle = new Bundle();
            bundle.putParcelable("mLease", mLease);
            switch (position) {
                case 0:
                    LeaseViewFrag1 frg1 = new LeaseViewFrag1();
                    frg1.setArguments(bundle);
                    return frg1;
                case 1:
                    LeaseViewFrag2 frg2 = new LeaseViewFrag2();
                    frg2.setArguments(bundle);
                    return frg2;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment createdFragment = (Fragment) super.instantiateItem(container, position);
            // save the appropriate reference depending on position
            switch (position) {
                case 0:
                    mFrag1 = (LeaseViewFrag1) createdFragment;
                    break;
                case 1:
                    mFrag2 = (LeaseViewFrag2) createdFragment;
                    break;
                //case 2:
                //    frag3 = (LeaseViewFrag3) createdFragment;
                //    break;
            }
            return createdFragment;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getResources().getString(R.string.info_tab_title);
                case 1:
                    return getResources().getString(R.string.payments_tab_title);
            }
            return "";
        }
    }
}


