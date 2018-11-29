package com.rba18.fragments;

import android.app.Activity;
import android.app.AlertDialog;
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
import com.rba18.helpers.DateAndCurrencyDisplayer;
import com.rba18.helpers.MainArrayDataMethods;
import com.rba18.model.ExpenseLogEntry;
import com.rba18.model.Lease;
import com.rba18.model.MoneyLogEntry;
import com.rba18.model.PaymentLogEntry;
import com.rba18.sqlite.DatabaseHandler;

import java.math.BigDecimal;
import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

public class LeaseViewFrag2 extends android.support.v4.app.Fragment implements AdapterView.OnItemClickListener, View.OnClickListener {

    public LeaseViewFrag2() {
        // Required empty public constructor
    }

    private TextView mNoPaymentsTV, mTotalAmountTV, mTotalAmountLabelTV;
    private MoneyListAdapter mMoneyListAdapter;
    private ColorStateList mAccentColor;
    private ListView mListView;
    private MainArrayDataMethods mDM;
    private DatabaseHandler mDB;
    private ArrayList<MoneyLogEntry> mCurrentFilteredMoney;
    private BigDecimal mTotal;
    private Lease mLease;
    private MoneyLogEntry mSelectedMoney;
    private OnMoneyDataChangedListener mCallback;
    private SharedPreferences mPreferences;
    private AlertDialog mDialog;
    private PopupMenu mPopupMenu;
    private boolean mCompletedOnly;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_view_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mNoPaymentsTV = view.findViewById(R.id.emptyListTV);
        mTotalAmountLabelTV = view.findViewById(R.id.moneyListTotalAmountLabelTV);
        mTotalAmountTV = view.findViewById(R.id.moneyListTotalAmountTV);
        FloatingActionButton fab = view.findViewById(R.id.listFab);
        mListView = view.findViewById(R.id.mainMoneyListView);
        LinearLayout totalAmountLL = view.findViewById(R.id.moneyListTotalAmountLL);
        totalAmountLL.setOnClickListener(this);
        mDM = new MainArrayDataMethods();
        mDB = new DatabaseHandler(getContext());
        Bundle bundle = getArguments();
        if (savedInstanceState != null) {
            if (savedInstanceState.getParcelable("mLease") != null) {
                mLease = savedInstanceState.getParcelable("mLease");
            }
            if (savedInstanceState.getParcelableArrayList("filteredMoney") != null) {
                mCurrentFilteredMoney = savedInstanceState.getParcelableArrayList("filteredMoney");
            }
            if (savedInstanceState.getString("totalString") != null) {
                String totalString = savedInstanceState.getString("totalString");
                mTotal = new BigDecimal(totalString);
            }
            mCompletedOnly = savedInstanceState.getBoolean("mCompletedOnly");
        } else {
            mCompletedOnly = true;
            mLease = bundle.getParcelable("mLease");
            mCurrentFilteredMoney = new ArrayList<>();
            getFilteredMoney();
            mTotal = getTotal();
        }
        mPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showNewIncomeOrExpenseAlertDialog();
            }
        });
        setUpdateSelectedDateListeners();
        //Get current theme accent color, which is passed into the list adapter for search highlighting
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

    public interface OnMoneyDataChangedListener{
        void onIncomeDataChanged();
        void onExpenseDataChanged();
        void onMoneyDataChanged();
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
                    + " must implement OnLeaseDataChangedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }

    private void setUpListAdapter() {
        if (mCurrentFilteredMoney != null) {
            mMoneyListAdapter = new MoneyListAdapter(getActivity(), mCurrentFilteredMoney, mAccentColor, null, true);
            mListView.setAdapter(mMoneyListAdapter);
            mListView.setOnItemClickListener(this);
            mNoPaymentsTV.setText(R.string.no_payments_to_display_lease);
            mListView.setEmptyView(mNoPaymentsTV);
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
                        mSelectedMoney = mMoneyListAdapter.getFilteredResults().get(position);
                        if (mSelectedMoney instanceof PaymentLogEntry) {
                            mCallback.onIncomeDataChanged();
                            Intent intent = new Intent(getActivity(), NewIncomeWizard.class);
                            PaymentLogEntry selectedIncome = (PaymentLogEntry) mSelectedMoney;
                            intent.putExtra("incomeToEdit", selectedIncome);
                            startActivityForResult(intent, MainActivity.REQUEST_NEW_INCOME_FORM);
                        } else {
                            mCallback.onExpenseDataChanged();
                            Intent intent = new Intent(getActivity(), NewExpenseWizard.class);
                            ExpenseLogEntry selectedExpense = (ExpenseLogEntry) mSelectedMoney;
                            intent.putExtra("expenseToEdit", selectedExpense);
                            startActivityForResult(intent, MainActivity.REQUEST_NEW_EXPENSE_FORM);
                        }

                        return true;

                    case R.id.remove:
                        mSelectedMoney = mMoneyListAdapter.getFilteredResults().get(position);
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
        if (mSelectedMoney instanceof PaymentLogEntry) {
            builder.setMessage(R.string.income_deletion_confirmation);
            builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    mDB.setPaymentLogEntryInactive((PaymentLogEntry) mSelectedMoney);
                    mCallback.onIncomeDataChanged();
                    getFilteredMoney();
                    mMoneyListAdapter.updateResults(mCurrentFilteredMoney);
                    mMoneyListAdapter.notifyDataSetChanged();
                    mTotal = getTotal();
                    setTotalTV();
                }
            });
        } else {
            builder.setMessage(R.string.expense_deletion_confirmation);
            builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    mDB.setExpenseInactive((ExpenseLogEntry) mSelectedMoney);
                    mCallback.onExpenseDataChanged();
                    getFilteredMoney();
                    mMoneyListAdapter.updateResults(mCurrentFilteredMoney);
                    mMoneyListAdapter.notifyDataSetChanged();
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

    public void updateData(){
        mCurrentFilteredMoney = new ArrayList<>();
        getFilteredMoney();
        mTotal = getTotal();
        mMoneyListAdapter.updateResults(mCurrentFilteredMoney);
    }

    public void showNewIncomeOrExpenseAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.add_new_income_or_expense);
        builder.setPositiveButton(R.string.income, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(getActivity(), NewIncomeWizard.class);
                intent.putExtra("preloadedLease", mLease);
                startActivityForResult(intent, MainActivity.REQUEST_NEW_INCOME_FORM);
            }
        });
        builder.setNegativeButton(R.string.expense, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(getActivity(), NewExpenseWizard.class);
                intent.putExtra("preloadedLease", mLease);
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
        if(mPopupMenu != null){
            mPopupMenu.dismiss();
        }
        if(mDialog != null){
            mDialog.dismiss();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MainActivity.REQUEST_NEW_INCOME_FORM) {
            //If successful(not cancelled, passed validation)
            if (resultCode == RESULT_OK) {
                getFilteredMoney();
                mMoneyListAdapter.updateResults(mCurrentFilteredMoney);
                mMoneyListAdapter.notifyDataSetChanged();
                mCallback.onIncomeDataChanged();
                mTotal = getTotal();
                setTotalTV();
            }
        }
        if (requestCode == MainActivity.REQUEST_NEW_EXPENSE_FORM) {
            //If successful(not cancelled, passed validation)
            if (resultCode == RESULT_OK) {
                getFilteredMoney();
                mMoneyListAdapter.updateResults(mCurrentFilteredMoney);
                mMoneyListAdapter.notifyDataSetChanged();
                mCallback.onExpenseDataChanged();
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
        //SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd", Locale.US);
        outState.putBoolean("mCompletedOnly", mCompletedOnly);
        if (mCurrentFilteredMoney != null) {
            outState.putParcelableArrayList("filteredMoney", mCurrentFilteredMoney);
        }
        if (mLease != null) {
            outState.putParcelable("mLease", mLease);
        }
        if (mTotal != null) {
            String totalString = mTotal.toPlainString();
            outState.putString("totalString", totalString);
        }
    }

    private BigDecimal getTotal() {
        BigDecimal total = new BigDecimal(0);
        if (mCurrentFilteredMoney != null) {
            if (!mCurrentFilteredMoney.isEmpty()) {
                if (mCompletedOnly) {
                    for (int i = 0; i < mCurrentFilteredMoney.size(); i++) {
                        if(mCurrentFilteredMoney.get(i).getIsCompleted()) {
                            total = total.add(mCurrentFilteredMoney.get(i).getAmount());
                        }
                    }
                } else {
                    for (int i = 0; i < mCurrentFilteredMoney.size(); i++) {
                        total = total.add(mCurrentFilteredMoney.get(i).getAmount());
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

    private void getFilteredMoney(){
        mCurrentFilteredMoney.clear();
        mCurrentFilteredMoney.addAll(mDB.getUsersIncomeByLeaseID(MainActivity.sUser, mLease.getId()));
        mCurrentFilteredMoney.addAll(mDB.getUsersExpensesByLeaseID(MainActivity.sUser, mLease.getId()));
        mDM.sortMoneyByDateDesc(mCurrentFilteredMoney);
    }
}



