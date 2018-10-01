package com.rentbud.fragments;

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
import android.util.Log;
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
import com.rentbud.activities.MainActivity;
import com.rentbud.activities.NewExpenseWizard;
import com.rentbud.activities.NewIncomeWizard;
import com.rentbud.adapters.MoneyListAdapter;
import com.rentbud.helpers.DateAndCurrencyDisplayer;
import com.rentbud.helpers.MainArrayDataMethods;
import com.rentbud.model.ExpenseLogEntry;
import com.rentbud.model.Lease;
import com.rentbud.model.MoneyLogEntry;
import com.rentbud.model.PaymentLogEntry;
import com.rentbud.sqlite.DatabaseHandler;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;

public class LeaseViewFrag2 extends android.support.v4.app.Fragment implements AdapterView.OnItemClickListener, View.OnClickListener {


    public LeaseViewFrag2() {
        // Required empty public constructor
    }

    TextView noPaymentsTV, totalAmountTV, totalAmountLabelTV;
    LinearLayout totalAmountLL;
    MoneyListAdapter moneyListAdapter;
    ColorStateList accentColor;
    ListView listView;
    MainArrayDataMethods dm;
    private DatabaseHandler db;
    private ArrayList<MoneyLogEntry> currentFilteredMoney;
    private BigDecimal total;
    private Lease lease;
    private MoneyLogEntry selectedMoney;
    FloatingActionButton fab;
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
        this.noPaymentsTV = view.findViewById(R.id.emptyListTV);
        this.totalAmountLabelTV = view.findViewById(R.id.moneyListTotalAmountLabelTV);
        this.totalAmountTV = view.findViewById(R.id.moneyListTotalAmountTV);
        this.fab = view.findViewById(R.id.listFab);
        this.listView = view.findViewById(R.id.mainMoneyListView);
        this.totalAmountLL = view.findViewById(R.id.moneyListTotalAmountLL);
        this.totalAmountLL.setOnClickListener(this);
        dm = new MainArrayDataMethods();
        this.db = new DatabaseHandler(getContext());
        Bundle bundle = getArguments();
        if (savedInstanceState != null) {
            if (savedInstanceState.getParcelable("lease") != null) {
                this.lease = savedInstanceState.getParcelable("lease");
            }
            if (savedInstanceState.getParcelableArrayList("filteredMoney") != null) {
                this.currentFilteredMoney = savedInstanceState.getParcelableArrayList("filteredMoney");
            }
            if (savedInstanceState.getString("totalString") != null) {
                String totalString = savedInstanceState.getString("totalString");
                this.total = new BigDecimal(totalString);
            }
            completedOnly = savedInstanceState.getBoolean("completedOnly");
        } else {
            completedOnly = true;
            this.lease = bundle.getParcelable("lease");
            currentFilteredMoney = new ArrayList<>();
            getFilteredMoney();
            total = getTotal();
        }
        this.preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        this.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showNewIncomeOrExpenseAlertDialog();
            }
        });
        //Get apartment item
        setUpdateSelectedDateListeners();
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
        if (currentFilteredMoney != null) {
            moneyListAdapter = new MoneyListAdapter(getActivity(), currentFilteredMoney, accentColor, null, true);
            listView.setAdapter(moneyListAdapter);
            listView.setOnItemClickListener(this);
            noPaymentsTV.setText(R.string.no_payments_to_display_lease);
            listView.setEmptyView(noPaymentsTV);
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
                            mCallback.onIncomeDataChanged();
                            Intent intent = new Intent(getActivity(), NewIncomeWizard.class);
                            PaymentLogEntry selectedIncome = (PaymentLogEntry) selectedMoney;
                            Log.d("TAG", "setUpRelatedInfoSection: ++++++++ " + selectedIncome.getTenantID());
                            Log.d("TAG", "setUpRelatedInfoSection: ++++++++ " + selectedIncome.getId());
                            intent.putExtra("incomeToEdit", selectedIncome);
                            startActivityForResult(intent, MainActivity.REQUEST_NEW_INCOME_FORM);
                        } else {
                            mCallback.onExpenseDataChanged();
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
                    mCallback.onIncomeDataChanged();
                   // IncomeListFragment.incomeListAdapterNeedsRefreshed = true;
                    getFilteredMoney();
                    moneyListAdapter.updateResults(currentFilteredMoney);
                    moneyListAdapter.notifyDataSetChanged();
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
                    mCallback.onExpenseDataChanged();
                   // ExpenseListFragment.expenseListAdapterNeedsRefreshed = true;
                    getFilteredMoney();
                    moneyListAdapter.updateResults(currentFilteredMoney);
                    moneyListAdapter.notifyDataSetChanged();
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

    public void updateData(){
        currentFilteredMoney = new ArrayList<>();
        getFilteredMoney();
        total = getTotal();
        moneyListAdapter.updateResults(currentFilteredMoney);
    }

    public void showNewIncomeOrExpenseAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.add_new_income_or_expense);
        builder.setPositiveButton(R.string.income, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(getActivity(), NewIncomeWizard.class);
                intent.putExtra("preloadedLease", lease);
                startActivityForResult(intent, MainActivity.REQUEST_NEW_INCOME_FORM);
            }
        });
        builder.setNegativeButton(R.string.expense, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(getActivity(), NewExpenseWizard.class);
                intent.putExtra("preloadedLease", lease);
                startActivityForResult(intent, MainActivity.REQUEST_NEW_EXPENSE_FORM);
            }
        });
        // create and show the alert dialog
        dialog = builder.create();
        dialog.show();
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MainActivity.REQUEST_NEW_INCOME_FORM) {
            //If successful(not cancelled, passed validation)
            if (resultCode == RESULT_OK) {
                getFilteredMoney();
                moneyListAdapter.updateResults(currentFilteredMoney);
                moneyListAdapter.notifyDataSetChanged();
                mCallback.onIncomeDataChanged();
                total = getTotal();
                setTotalTV();
            }
        }
        if (requestCode == MainActivity.REQUEST_NEW_EXPENSE_FORM) {
            //If successful(not cancelled, passed validation)
            if (resultCode == RESULT_OK) {
                getFilteredMoney();
                moneyListAdapter.updateResults(currentFilteredMoney);
                moneyListAdapter.notifyDataSetChanged();
                mCallback.onExpenseDataChanged();
                total = getTotal();
                setTotalTV();
            }
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

    private void setUpdateSelectedDateListeners() {

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd", Locale.US);
        outState.putBoolean("completedOnly", completedOnly);
        if (currentFilteredMoney != null) {
            outState.putParcelableArrayList("filteredMoney", currentFilteredMoney);
        }
        if (lease != null) {
            outState.putParcelable("lease", lease);
        }
        if (total != null) {
            String totalString = total.toPlainString();
            outState.putString("totalString", totalString);
        }
    }

    private BigDecimal getTotal() {
        BigDecimal total = new BigDecimal(0);
        if (currentFilteredMoney != null) {
            if (!currentFilteredMoney.isEmpty()) {
                if (completedOnly) {
                    for (int i = 0; i < currentFilteredMoney.size(); i++) {
                        if(currentFilteredMoney.get(i).getIsCompleted()) {
                            total = total.add(currentFilteredMoney.get(i).getAmount());
                        }
                    }
                } else {
                    for (int i = 0; i < currentFilteredMoney.size(); i++) {
                        total = total.add(currentFilteredMoney.get(i).getAmount());
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

    private void getFilteredMoney(){
        currentFilteredMoney.clear();
        currentFilteredMoney.addAll(db.getUsersIncomeByLeaseID(MainActivity.user, lease.getId()));
        currentFilteredMoney.addAll(db.getUsersExpensesByLeaseID(MainActivity.user, lease.getId()));
        dm.sortMoneyByDateDesc(currentFilteredMoney);
    }
}



