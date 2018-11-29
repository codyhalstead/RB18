package com.rba18.fragments;

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

import com.rba18.R;
import com.rba18.activities.MainActivity;
import com.rba18.activities.NewExpenseWizard;
import com.rba18.activities.NewIncomeWizard;
import com.rba18.adapters.MoneyListAdapter;
import com.rba18.helpers.ApartmentTenantViewModel;
import com.rba18.helpers.DateAndCurrencyDisplayer;
import com.rba18.model.ExpenseLogEntry;
import com.rba18.model.MoneyLogEntry;
import com.rba18.model.PaymentLogEntry;
import com.rba18.sqlite.DatabaseHandler;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;

public class DateViewFrag1 extends android.support.v4.app.Fragment implements AdapterView.OnItemClickListener, View.OnClickListener {

    public DateViewFrag1() {
        // Required empty public constructor
    }

    private TextView mNoMoneyTV, mTotalAmountTV, mTotalAmountLabelTV;
    private MoneyListAdapter mMoneyListAdapter;
    private ColorStateList mAccentColor;
    private ListView mListView;
    private DatabaseHandler mDB;
    private BigDecimal mTotal;
    private MoneyLogEntry mSelectedMoneyEntry;
    private OnMoneyDataChangedListener mCallback;
    private AlertDialog mDialog;
    private PopupMenu mPopupMenu;
    private SharedPreferences mPreferences;
    private MoneyLogEntry mSelectedMoney;
    private boolean mCompletedOnly;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_view_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mNoMoneyTV = view.findViewById(R.id.emptyListTV);
        FloatingActionButton fab = view.findViewById(R.id.listFab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showNewIncomeOrExpenseAlertDialog();
            }
        });
        mTotalAmountLabelTV = view.findViewById(R.id.moneyListTotalAmountLabelTV);
        mTotalAmountTV = view.findViewById(R.id.moneyListTotalAmountTV);
        mListView = view.findViewById(R.id.mainMoneyListView);
        LinearLayout totalAmountLL = view.findViewById(R.id.moneyListTotalAmountLL);
        totalAmountLL.setOnClickListener(this);
        mDB = new DatabaseHandler(getContext());
        if(savedInstanceState != null){
            mCompletedOnly = savedInstanceState.getBoolean("mCompletedOnly");
        } else {
            mCompletedOnly = true;
        }
        mPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        mTotal = getTotal();

        setUpdateSelectedDateListeners();
        TypedValue colorValue = new TypedValue();
        getActivity().getTheme().resolveAttribute(R.attr.colorAccent, colorValue, true);
        mAccentColor = getActivity().getResources().getColorStateList(colorValue.resourceId);
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
            mMoneyListAdapter = new MoneyListAdapter(getActivity(), ViewModelProviders.of(getActivity()).get(ApartmentTenantViewModel.class).getMoneyArray().getValue(), mAccentColor, null, true);
            mListView.setAdapter(mMoneyListAdapter);
            mListView.setOnItemClickListener(this);
            mNoMoneyTV.setText(R.string.no_payments_to_display_date);
            mListView.setEmptyView(mNoMoneyTV);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        mPopupMenu = new PopupMenu(getActivity(), view);
        MenuInflater inflater = mPopupMenu.getMenuInflater();
        final int position = i;
        mPopupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.changeStatus:
                        //On mListView row click, launch ApartmentViewActivity passing the rows data into it.
                        mSelectedMoney = mMoneyListAdapter.getFilteredResults().get(position);
                        if (mSelectedMoney instanceof PaymentLogEntry) {
                            PaymentLogEntry selectedIncome = (PaymentLogEntry) mSelectedMoney;
                            if(selectedIncome.getIsCompleted()){
                                selectedIncome.setIsCompleted(false);
                            } else {
                                selectedIncome.setIsCompleted(true);
                            }
                            mDB.editPaymentLogEntry(selectedIncome);
                            mCallback.onMoneyDataChanged();
                            mCallback.onIncomeDataChanged();
                        } else {
                            ExpenseLogEntry selectedExpense = (ExpenseLogEntry) mSelectedMoney;
                            if(selectedExpense.getIsCompleted()){
                                selectedExpense.setIsCompleted(false);
                            } else {
                                selectedExpense.setIsCompleted(true);
                            }
                            mDB.editExpenseLogEntry(selectedExpense);
                            mCallback.onMoneyDataChanged();
                            mCallback.onExpenseDataChanged();
                        }
                        return true;

                    case R.id.edit:
                        //On mListView row click, launch ApartmentViewActivity passing the rows data into it.
                        mSelectedMoneyEntry = mMoneyListAdapter.getFilteredResults().get(position);
                        if (mSelectedMoneyEntry instanceof PaymentLogEntry) {
                            Intent intent = new Intent(getActivity(), NewIncomeWizard.class);
                            PaymentLogEntry selectedIncome = (PaymentLogEntry) mSelectedMoneyEntry;
                            intent.putExtra("incomeToEdit", selectedIncome);
                            startActivityForResult(intent, MainActivity.REQUEST_NEW_INCOME_FORM);
                        } else {
                            Intent intent = new Intent(getActivity(), NewExpenseWizard.class);
                            ExpenseLogEntry selectedExpense = (ExpenseLogEntry) mSelectedMoneyEntry;
                            intent.putExtra("expenseToEdit", selectedExpense);
                            startActivityForResult(intent, MainActivity.REQUEST_NEW_EXPENSE_FORM);
                        }

                        return true;

                    case R.id.remove:
                        mSelectedMoneyEntry = mMoneyListAdapter.getFilteredResults().get(position);
                        showDeleteConfirmationAlertDialog();
                        return true;

                    default:
                        return false;
                }
            }
        });
        inflater.inflate(R.menu.expense_income_click_menu, mPopupMenu.getMenu());
        mPopupMenu.show();
    }

    public void showDeleteConfirmationAlertDialog() {
        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        if (mSelectedMoneyEntry instanceof PaymentLogEntry) {
            builder.setMessage(R.string.income_deletion_confirmation);
            builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    mDB.setPaymentLogEntryInactive((PaymentLogEntry) mSelectedMoneyEntry);
                    mCallback.onMoneyDataChanged();
                    mCallback.onIncomeDataChanged();
                    mTotal = getTotal();
                    setTotalTV();
                }
            });
        } else {
            builder.setMessage(R.string.expense_deletion_confirmation);
            builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    mDB.setExpenseInactive((ExpenseLogEntry) mSelectedMoneyEntry);
                    mCallback.onMoneyDataChanged();
                    mCallback.onExpenseDataChanged();
                    mTotal = getTotal();
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
        // create and show the alert mDialog
        mDialog = builder.create();
        mDialog.show();
    }

    public void showNewIncomeOrExpenseAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.add_new_income_or_expense);
        builder.setPositiveButton(R.string.income, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(getActivity(), NewIncomeWizard.class);
                SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
                intent.putExtra("preloadedDate", formatter.format(ViewModelProviders.of(getActivity()).get(ApartmentTenantViewModel.class).getDate().getValue()));
                startActivityForResult(intent, MainActivity.REQUEST_NEW_INCOME_FORM);
            }
        });
        builder.setNegativeButton(R.string.expense, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(getActivity(), NewExpenseWizard.class);
                SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
                intent.putExtra("preloadedDate", formatter.format(ViewModelProviders.of(getActivity()).get(ApartmentTenantViewModel.class).getDate().getValue()));
                startActivityForResult(intent, MainActivity.REQUEST_NEW_EXPENSE_FORM);
            }
        });
        // create and show the alert mDialog
        mDialog = builder.create();
        mDialog.show();
    }

    @Override
    public void onPause() {
        super.onPause();
        if(mDialog != null){
            mDialog.dismiss();
        }
        if(mPopupMenu != null){
            mPopupMenu.dismiss();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MainActivity.REQUEST_NEW_EXPENSE_FORM) {
            //If successful(not cancelled, passed validation)
            if (resultCode == RESULT_OK) {
                mCallback.onMoneyDataChanged();
                mCallback.onExpenseDataChanged();
                mTotal = getTotal();
                setTotalTV();
            }
        }
        if (requestCode == MainActivity.REQUEST_NEW_INCOME_FORM) {
            //If successful(not cancelled, passed validation)
            if (resultCode == RESULT_OK) {
                mCallback.onMoneyDataChanged();
                mCallback.onIncomeDataChanged();
                mTotal = getTotal();
                setTotalTV();
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.moneyListTotalAmountLL:
                if(mCompletedOnly){
                    mCompletedOnly = false;
                    mTotal = getTotal();
                    setTotalTV();
                } else {
                    mCompletedOnly = true;
                    mTotal = getTotal();
                    setTotalTV();
                }
                break;

            default:
                break;
        }
    }

    private void setUpdateSelectedDateListeners() {

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("mCompletedOnly", mCompletedOnly);
    }

    private BigDecimal getTotal() {
        BigDecimal total = new BigDecimal(0);
        if (ViewModelProviders.of(getActivity()).get(ApartmentTenantViewModel.class).getMoneyArray().getValue() != null) {
            if (!ViewModelProviders.of(getActivity()).get(ApartmentTenantViewModel.class).getMoneyArray().getValue().isEmpty()) {
                if (mCompletedOnly) {
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
        if (mTotal != null) {
            int moneyFormatCode = mPreferences.getInt("currency", DateAndCurrencyDisplayer.CURRENCY_US);
            mTotalAmountTV.setText(DateAndCurrencyDisplayer.getCurrencyToDisplay(moneyFormatCode, mTotal));
            if (mTotal.compareTo(new BigDecimal(0)) < 0) {
                mTotalAmountTV.setTextColor(getActivity().getResources().getColor(R.color.red));
                if(mCompletedOnly){
                    mTotalAmountLabelTV.setText(R.string.total_paid);
                } else {
                    mTotalAmountLabelTV.setText(R.string.projected_total);
                }
            } else {
                mTotalAmountTV.setTextColor(getActivity().getResources().getColor(R.color.green_colorPrimaryDark));
                if(mCompletedOnly){
                    mTotalAmountLabelTV.setText(R.string.received_total);
                } else {
                    mTotalAmountLabelTV.setText(R.string.projected_total);
                }
            }
        }
    }

    public void updateData(){
        mMoneyListAdapter.updateResults(ViewModelProviders.of(getActivity()).get(ApartmentTenantViewModel.class).getMoneyArray().getValue());
        mMoneyListAdapter.notifyDataSetChanged();
        mTotal = getTotal();
        setTotalTV();
    }
}