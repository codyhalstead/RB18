package com.RB18.activities;

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

import com.example.cody.rentbud.R;
import com.RB18.fragments.LeaseViewFrag1;
import com.RB18.fragments.LeaseViewFrag2;
import com.RB18.model.Lease;
import com.RB18.sqlite.DatabaseHandler;

import java.util.List;

public class LeaseViewActivity extends BaseActivity implements LeaseViewFrag2.OnMoneyDataChangedListener {
    private Lease lease;
    private DatabaseHandler databaseHandler;
    ViewPager viewPager;
    LeaseViewActivity.ViewPagerAdapter adapter;
    LinearLayout dateSelectorLL;
    private LeaseViewFrag1 frag1;
    private LeaseViewFrag2 frag2;
    private boolean wasLeaseEdited, wasIncomeEdited, wasExpenseEdited;
    private AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupUserAppTheme(MainActivity.curThemeChoice);
        setContentView(R.layout.activity_lease_view_actual);
        dateSelectorLL = findViewById(R.id.moneyDateSelecterLL);
        dateSelectorLL.setVisibility(View.GONE);
        viewPager = (ViewPager) findViewById(R.id.pager);
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        this.databaseHandler = new DatabaseHandler(this);
        // Add Fragments to adapter one by one
        Bundle bundle = getIntent().getExtras();
        int leaseID = bundle.getInt("leaseID");
        this.lease = databaseHandler.getLeaseByID(MainActivity.user, leaseID);
        bundle.putParcelable("lease", lease);
        viewPager.setAdapter(adapter);
        if (savedInstanceState != null) {
            wasLeaseEdited = savedInstanceState.getBoolean("was_lease_edited");
            wasIncomeEdited = savedInstanceState.getBoolean("was_income_edited");
            wasExpenseEdited = savedInstanceState.getBoolean("was_expense_edited");
        } else {
            wasLeaseEdited = false;
            wasIncomeEdited = false;
            wasExpenseEdited = false;
        }
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        setupBasicToolbar();
        this.setTitle(R.string.lease_view);
        if (wasLeaseEdited || wasIncomeEdited || wasExpenseEdited) {
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
                intent.putExtra("leaseToEdit", lease);
                wasLeaseEdited = true;
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
                databaseHandler.setLeaseInactive(lease);
                MainActivity.apartmentList = databaseHandler.getUsersApartmentsIncludingInactive(MainActivity.user);
                MainActivity.tenantList = databaseHandler.getUsersTenantsIncludingInactive(MainActivity.user);
                showDeleteAllRelatedMoneyAlertDialog();
            }
        });
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        // create and show the alert dialog
        alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onMoneyDataChanged() {
        frag2.updateData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (wasLeaseEdited || wasIncomeEdited || wasExpenseEdited) {
            setResultToEdited();
        } else {
            setResult(RESULT_OK);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(alertDialog != null){
            alertDialog.dismiss();
        }
    }


    private void setResultToEdited() {
        Intent intent = new Intent();
        intent.putExtra("was_lease_edited", wasLeaseEdited);
        intent.putExtra("was_income_edited", wasIncomeEdited);
        intent.putExtra("was_expense_edited", wasExpenseEdited);
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
                databaseHandler.setAllExpensesRelatedToLeaseInactive(lease.getId());
                databaseHandler.setAllIncomeRelatedToLeaseInactive(lease.getId());
                wasLeaseEdited = true;
                wasExpenseEdited = true;
                wasIncomeEdited = true;
                setResultToEdited();
                LeaseViewActivity.this.finish();
            }
        });
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                wasLeaseEdited = true;
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
                //Re-query cached apartment array to update cache and refresh current fragment to display new data
                //int leaseID = data.getIntExtra("editedLeaseID", 0);
                this.lease = databaseHandler.getLeaseByID(MainActivity.user, lease.getId());
                //this.apartment = dataMethods.getCachedApartmentByApartmentID(lease.getApartmentID());
                //Pair<Tenant, ArrayList<Tenant>> tenants = dataMethods.getCachedPrimaryAndSecondaryTenantsByLease(lease);
                //this.primaryTenant = tenants.first;
                //this.secondaryTenants = tenants.second;
                //fillTextViews();
                //LeaseListFragment.leaseListAdapterNeedsRefreshed = true;
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
        outState.putBoolean("was_lease_edited", wasLeaseEdited);
        outState.putBoolean("was_income_edited", wasIncomeEdited);
        outState.putBoolean("was_expense_edited", wasExpenseEdited);
    }

    @Override
    public void onIncomeDataChanged() {
        wasIncomeEdited = true;
        setResultToEdited();
    }

    @Override
    public void onExpenseDataChanged() {
        wasExpenseEdited = true;
        setResultToEdited();
    }

    public void showEditNotesDialog() {
        final EditText editText = new EditText(LeaseViewActivity.this);
        int maxLength = 500;
        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
        editText.setSingleLine(false);
        editText.setImeOptions(EditorInfo.IME_FLAG_NO_ENTER_ACTION);
        editText.setText(lease.getNotes());
        editText.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        editText.setSelection(editText.getText().length());
        //editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);

        // create the AlertDialog as final
        alertDialog = new AlertDialog.Builder(LeaseViewActivity.this)
                //.setMessage(R.string.comfirm_pass_to_delete_account_message)
                .setTitle(R.string.edit_notes)
                .setView(editText)

                // Set the action buttons
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String input = editText.getText().toString();
                        lease.setNotes(input);
                        databaseHandler.editLease(lease);
                        wasLeaseEdited = true;
                        setResultToEdited();
                        //viewModel.setApartment(apartment);
                        if (frag1 != null) {
                            frag1.updateLeaseData(lease);
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

        alertDialog.show();
    }

    // Adapter for the viewpager using FragmentPagerAdapter
    class ViewPagerAdapter extends FragmentStatePagerAdapter {

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            Bundle bundle = new Bundle();
            bundle.putParcelable("lease", lease);
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
                    frag1 = (LeaseViewFrag1) createdFragment;
                    break;
                case 1:
                    frag2 = (LeaseViewFrag2) createdFragment;
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


