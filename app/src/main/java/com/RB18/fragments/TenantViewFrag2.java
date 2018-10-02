package com.RB18.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.PopupMenu;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.cody.rentbud.R;
import com.RB18.activities.MainActivity;
import com.RB18.activities.NewExpenseWizard;
import com.RB18.activities.NewIncomeWizard;
import com.RB18.adapters.MoneyListAdapter;
import com.RB18.helpers.ApartmentTenantViewModel;
import com.RB18.helpers.DateAndCurrencyDisplayer;
import com.RB18.model.ExpenseLogEntry;
import com.RB18.model.MoneyLogEntry;
import com.RB18.model.PaymentLogEntry;
import com.RB18.model.Tenant;
import com.RB18.sqlite.DatabaseHandler;

import java.math.BigDecimal;

import static android.app.Activity.RESULT_OK;

public class TenantViewFrag2 extends android.support.v4.app.Fragment implements AdapterView.OnItemClickListener, View.OnClickListener {


    public TenantViewFrag2() {
        // Required empty public constructor
    }

    TextView nopaymentsTV, totalAmountTV, totalAmountLabelTV;
    LinearLayout totalAmountLL;
    FloatingActionButton fab;
    MoneyListAdapter moneyListAdapter;
    ColorStateList accentColor;
    ListView listView;
    private DatabaseHandler db;
    private MoneyLogEntry selectedMoney;
    private Tenant tenant;
    private BigDecimal total;
    private OnMoneyDataChangedListener mCallback;
    private SharedPreferences preferences;
    private AlertDialog dialog;
    private PopupMenu popupMenu;
    private boolean completedOnly;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_view_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.nopaymentsTV = view.findViewById(R.id.emptyListTV);
        this.totalAmountLabelTV = view.findViewById(R.id.moneyListTotalAmountLabelTV);
        this.totalAmountTV = view.findViewById(R.id.moneyListTotalAmountTV);
        this.fab = view.findViewById(R.id.listFab);
        this.listView = view.findViewById(R.id.mainMoneyListView);
        this.totalAmountLL = view.findViewById(R.id.moneyListTotalAmountLL);
        this.totalAmountLL.setOnClickListener(this);
        this.db = new DatabaseHandler(getContext());
        if(savedInstanceState != null){
            completedOnly = savedInstanceState.getBoolean("completedOnly");
        } else {
            completedOnly = true;
        }
        this.preferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        this.tenant = ViewModelProviders.of(getActivity()).get(ApartmentTenantViewModel.class).getViewedTenant().getValue();
        total = getTotal();
        this.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showNewIncomeOrExpenseAlertDialog();
            }
        });
        // getActivity().setTitle("Income View");
        // ExpenseListFragment.expenseListAdapterNeedsRefreshed = false;
        //Get current theme accent color, which is passed into the list adapter for search highlighting
        TypedValue colorValue = new TypedValue();
        getActivity().getTheme().resolveAttribute(R.attr.colorAccent, colorValue, true);
        this.accentColor = getActivity().getResources().getColorStateList(colorValue.resourceId);
        setUpListAdapter();
        setUpSearchBar();
        setTotalTV();
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    private void setUpSearchBar() {

    }

    public interface OnMoneyDataChangedListener {
        void onMoneyDataChanged();
        void onIncomeDataChanged();
        void onExpenseDataChanged();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnMoneyDataChangedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnMoneyDataChangedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }

    private void setUpListAdapter() {
        if (ViewModelProviders.of(getActivity()).get(ApartmentTenantViewModel.class).getMoneyArray().getValue() != null) {
            moneyListAdapter = new MoneyListAdapter(getActivity(), ViewModelProviders.of(getActivity()).get(ApartmentTenantViewModel.class).getMoneyArray().getValue(), accentColor, null, true);
            listView.setAdapter(moneyListAdapter);
            listView.setOnItemClickListener(this);
            nopaymentsTV.setText(R.string.no_payments_to_display_tenant);
            listView.setEmptyView(nopaymentsTV);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        popupMenu = new PopupMenu(getActivity(), view);
        MenuInflater inflater = popupMenu.getMenuInflater();
        final int position = i;
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.changeStatus:
                        //On listView row click, launch ApartmentViewActivity passing the rows data into it.
                        selectedMoney = moneyListAdapter.getFilteredResults().get(position);
                        if (selectedMoney instanceof PaymentLogEntry) {
                            //mCallback.onIncomeDataChanged();
                            PaymentLogEntry selectedIncome = (PaymentLogEntry) selectedMoney;
                            if(selectedIncome.getIsCompleted()){
                                selectedIncome.setIsCompleted(false);
                            } else {
                                selectedIncome.setIsCompleted(true);
                            }
                            db.editPaymentLogEntry(selectedIncome);
                            mCallback.onMoneyDataChanged();
                            mCallback.onIncomeDataChanged();
                        } else {
                            //mCallback.onExpenseDataChanged();
                            ExpenseLogEntry selectedExpense = (ExpenseLogEntry) selectedMoney;
                            if(selectedExpense.getIsCompleted()){
                                selectedExpense.setIsCompleted(false);
                            } else {
                                selectedExpense.setIsCompleted(true);
                            }
                            db.editExpenseLogEntry(selectedExpense);
                            mCallback.onMoneyDataChanged();
                            mCallback.onExpenseDataChanged();

                        }
                        return true;

                    case R.id.edit:
                        //On listView row click, launch ApartmentViewActivity passing the rows data into it.
                        selectedMoney = moneyListAdapter.getFilteredResults().get(position);
                        if (selectedMoney instanceof PaymentLogEntry) {
                           // mCallback.onIncomeDataChanged();
                            Intent intent = new Intent(getActivity(), NewIncomeWizard.class);
                            PaymentLogEntry selectedIncome = (PaymentLogEntry) selectedMoney;
                            intent.putExtra("incomeToEdit", selectedIncome);
                            startActivityForResult(intent, MainActivity.REQUEST_NEW_INCOME_FORM);
                        } else {
                          //  mCallback.onExpenseDataChanged();
                            Intent intent = new Intent(getActivity(), NewExpenseWizard.class);
                            ExpenseLogEntry selectedExpense = (ExpenseLogEntry) selectedMoney;
                            intent.putExtra("expenseToEdit", selectedExpense);
                            startActivityForResult(intent, MainActivity.REQUEST_NEW_EXPENSE_FORM);
                        }

                        return true;

                    case R.id.remove:
                        selectedMoney = moneyListAdapter.getFilteredResults().get(position);
                        showDeleteConfirmationAlertDialog();
                        return true;

                    default:
                        return false;
                }
            }
        });
        inflater.inflate(R.menu.expense_income_click_menu, popupMenu.getMenu());
        popupMenu.show();
    }

    public void showDeleteConfirmationAlertDialog() {
        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        if (selectedMoney instanceof PaymentLogEntry) {
            builder.setMessage(R.string.income_deletion_confirmation);
            builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    db.setPaymentLogEntryInactive((PaymentLogEntry) selectedMoney);
                    //IncomeListFragment.incomeListAdapterNeedsRefreshed = true;
                    //currentFilteredMoney = db.getIncomeAndExpensesByTenantIDWithinDates(MainActivity.user, tenant.getId(), startDateRange, endDateRange);
                    //moneyListAdapter.updateResults(currentFilteredMoney);
                    //moneyListAdapter.notifyDataSetChanged();
                    mCallback.onMoneyDataChanged();
                    mCallback.onIncomeDataChanged();
                    total = getTotal();
                    setTotalTV();
                }
            });
        } else {
            builder.setMessage(R.string.expense_deletion_confirmation);
            builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    db.setExpenseInactive((ExpenseLogEntry) selectedMoney);
                    //ExpenseListFragment.expenseListAdapterNeedsRefreshed = true;
                    //currentFilteredMoney = db.getIncomeAndExpensesByTenantIDWithinDates(MainActivity.user, tenant.getId(), startDateRange, endDateRange);
                    //moneyListAdapter.updateResults(currentFilteredMoney);
                    //moneyListAdapter.notifyDataSetChanged();
                    mCallback.onMoneyDataChanged();
                    mCallback.onExpenseDataChanged();
                    total = getTotal();
                    setTotalTV();
                }
            });
        }
        // add the buttons
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        // create and show the alert dialog
        dialog = builder.create();
        dialog.show();
    }

    public void showNewIncomeOrExpenseAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.add_new_income_or_expense);
        builder.setPositiveButton(R.string.income, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(getActivity(), NewIncomeWizard.class);
                intent.putExtra("preloadedTenant", tenant);
                startActivityForResult(intent, MainActivity.REQUEST_NEW_INCOME_FORM);
            }
        });
        builder.setNegativeButton(R.string.expense, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(getActivity(), NewExpenseWizard.class);
                intent.putExtra("preloadedTenant", tenant);
                startActivityForResult(intent, MainActivity.REQUEST_NEW_EXPENSE_FORM);
            }
        });
        // create and show the alert dialog
        dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MainActivity.REQUEST_NEW_INCOME_FORM) {
            //If successful(not cancelled, passed validation)
            if (resultCode == RESULT_OK) {
                //this.currentFilteredMoney = db.getIncomeAndExpensesByTenantIDWithinDates(MainActivity.user, tenant.getId(), startDateRange, endDateRange);
                //moneyListAdapter.updateResults(currentFilteredMoney);
                //moneyListAdapter.notifyDataSetChanged();
                mCallback.onMoneyDataChanged();
                mCallback.onIncomeDataChanged();
                total = getTotal();
                setTotalTV();
            }
        }
        if (requestCode == MainActivity.REQUEST_NEW_EXPENSE_FORM) {
            //If successful(not cancelled, passed validation)
            if (resultCode == RESULT_OK) {
                //this.currentFilteredMoney = db.getIncomeAndExpensesByTenantIDWithinDates(MainActivity.user, tenant.getId(), startDateRange, endDateRange);
                //moneyListAdapter.updateResults(currentFilteredMoney);
                //moneyListAdapter.notifyDataSetChanged();
                mCallback.onMoneyDataChanged();
                mCallback.onExpenseDataChanged();
                total = getTotal();
                setTotalTV();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(popupMenu != null){
            popupMenu.dismiss();
        }
        if(dialog != null){
            dialog.dismiss();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.moneyListTotalAmountLL:
                if(completedOnly){
                    completedOnly = false;
                    total = getTotal();
                    setTotalTV();
                } else {
                    completedOnly = true;
                    total = getTotal();
                    setTotalTV();
                }
                break;

            default:
                break;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("completedOnly", completedOnly);
    }

    private BigDecimal getTotal() {
        BigDecimal total = new BigDecimal(0);
        if (ViewModelProviders.of(getActivity()).get(ApartmentTenantViewModel.class).getMoneyArray().getValue() != null) {
            if (!ViewModelProviders.of(getActivity()).get(ApartmentTenantViewModel.class).getMoneyArray().getValue().isEmpty()) {
                if (completedOnly) {
                    for (int i = 0; i < ViewModelProviders.of(getActivity()).get(ApartmentTenantViewModel.class).getMoneyArray().getValue().size(); i++) {
                        if(ViewModelProviders.of(getActivity()).get(ApartmentTenantViewModel.class).getMoneyArray().getValue().get(i).getIsCompleted()) {
                            total = total.add(ViewModelProviders.of(getActivity()).get(ApartmentTenantViewModel.class).getMoneyArray().getValue().get(i).getAmount());
                        }
                    }
                } else {
                    for (int i = 0; i < ViewModelProviders.of(getActivity()).get(ApartmentTenantViewModel.class).getMoneyArray().getValue().size(); i++) {
                        total = total.add(ViewModelProviders.of(getActivity()).get(ApartmentTenantViewModel.class).getMoneyArray().getValue().get(i).getAmount());
                    }
                }
            }
        }
        return total;
    }

    private void setTotalTV() {
        if (total != null) {
            int moneyFormatCode = preferences.getInt("currency", DateAndCurrencyDisplayer.CURRENCY_US);
            totalAmountTV.setText(DateAndCurrencyDisplayer.getCurrencyToDisplay(moneyFormatCode, total));
            if (total.compareTo(new BigDecimal(0)) < 0) {
                totalAmountTV.setTextColor(getActivity().getResources().getColor(R.color.red));
                if(completedOnly){
                    totalAmountLabelTV.setText(R.string.total_paid);
                } else {
                    totalAmountLabelTV.setText(R.string.projected_total);
                }
            } else {
                totalAmountTV.setTextColor(getActivity().getResources().getColor(R.color.green_colorPrimaryDark));
                if(completedOnly){
                    totalAmountLabelTV.setText(R.string.received_total);
                } else {
                    totalAmountLabelTV.setText(R.string.projected_total);
                }
            }
        }
    }

    public void updateData(){
        moneyListAdapter.updateResults(ViewModelProviders.of(getActivity()).get(ApartmentTenantViewModel.class).getMoneyArray().getValue());
        moneyListAdapter.notifyDataSetChanged();
        this.total = getTotal();
        setTotalTV();
    }
}



