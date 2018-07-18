package com.rentbud.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
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
import android.widget.ListView;
import android.widget.TextView;

import com.example.cody.rentbud.R;
import com.rentbud.activities.MainActivity;
import com.rentbud.activities.NewExpenseWizard;
import com.rentbud.activities.NewIncomeWizard;
import com.rentbud.adapters.IncomeListAdapter;
import com.rentbud.adapters.MoneyListAdapter;
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

    TextView noIncomeTV, totalAmountTV, totalAmountLabelTV;
    //  EditText searchBarET;
    //  Button dateRangeStartBtn, dateRangeEndBtn;
    MoneyListAdapter moneyListAdapter;
    ColorStateList accentColor;
    ListView listView;
    MainArrayDataMethods dm;
    //public static boolean incomeListAdapterNeedsRefreshed;
    //  Date filterDateStart, filterDateEnd;
    //  private DatePickerDialog.OnDateSetListener dateSetFilterStartListener, dateSetFilterEndListener;
    private DatabaseHandler db;
    private ArrayList<MoneyLogEntry> currentFilteredMoney;
    private BigDecimal total;
    private Lease lease;
    private MoneyLogEntry selectedMoney;
    FloatingActionButton fab;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.lease_view_fragment_two, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //this.noIncomeTV = view.findViewById(R.id.moneyEmptyListTV);
        this.totalAmountLabelTV = view.findViewById(R.id.moneyListTotalAmountLabelTV);
        this.totalAmountTV = view.findViewById(R.id.moneyListTotalAmountTV);
        this.fab = view.findViewById(R.id.listFab);
        this.listView = view.findViewById(R.id.mainMoneyListView);
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
        } else {
            this.lease = bundle.getParcelable("lease");
            currentFilteredMoney = new ArrayList<>();
            getFilteredMoney();
            total = getTotal();
        }
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
        if (IncomeListFragment.incomeListAdapterNeedsRefreshed) {
            if (this.moneyListAdapter != null) {
                //   incomeListAdapterNeedsRefreshed = false;
                moneyListAdapter.getFilter().filter("");
            }
        }
    }

    private void setUpSearchBar() {

    }

    private void setUpListAdapter() {
        if (currentFilteredMoney != null) {
            moneyListAdapter = new MoneyListAdapter(getActivity(), currentFilteredMoney, accentColor);
            listView.setAdapter(moneyListAdapter);
            listView.setOnItemClickListener(this);
            if (currentFilteredMoney.isEmpty()) {
                //     noIncomeTV.setVisibility(View.VISIBLE);
                //    noIncomeTV.setText("No Current Income");
            }
        } else {
            //If MainActivity5.expenseList is null show empty list text
            //  noIncomeTV.setVisibility(View.VISIBLE);
            //  noIncomeTV.setText("Error Loading Income");
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        PopupMenu popup = new PopupMenu(getActivity(), view);
        MenuInflater inflater = popup.getMenuInflater();
        final int position = i;
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.edit:
                        //On listView row click, launch ApartmentViewActivity passing the rows data into it.
                        selectedMoney = moneyListAdapter.getFilteredResults().get(position);
                        if (selectedMoney instanceof PaymentLogEntry) {
                            Intent intent = new Intent(getActivity(), NewIncomeWizard.class);
                            PaymentLogEntry selectedIncome = (PaymentLogEntry) selectedMoney;
                            intent.putExtra("incomeToEdit", selectedIncome);
                            startActivityForResult(intent, MainActivity.REQUEST_NEW_INCOME_FORM);
                        } else {
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
        inflater.inflate(R.menu.expense_income_click_menu, popup.getMenu());
        popup.show();
    }

    public void showDeleteConfirmationAlertDialog() {
        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        if (selectedMoney instanceof PaymentLogEntry) {
            builder.setMessage("Are you sure you want to remove this income?");
            builder.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    db.setPaymentLogEntryInactive((PaymentLogEntry) selectedMoney);
                    IncomeListFragment.incomeListAdapterNeedsRefreshed = true;
                    getFilteredMoney();
                    moneyListAdapter.updateResults(currentFilteredMoney);
                    moneyListAdapter.notifyDataSetChanged();
                    total = getTotal();
                    setTotalTV();
                }
            });
        } else {
            builder.setMessage("Are you sure you want to remove this expense?");
            builder.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    db.setExpenseInactive((ExpenseLogEntry) selectedMoney);
                    ExpenseListFragment.expenseListAdapterNeedsRefreshed = true;
                    getFilteredMoney();
                    moneyListAdapter.updateResults(currentFilteredMoney);
                    moneyListAdapter.notifyDataSetChanged();
                    total = getTotal();
                    setTotalTV();
                }
            });
        }
        // add the buttons
        builder.setPositiveButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void showNewIncomeOrExpenseAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Add new Income or Expense?");
        builder.setNegativeButton("Income", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(getActivity(), NewIncomeWizard.class);
                intent.putExtra("preloadedLease", lease);
                startActivityForResult(intent, MainActivity.REQUEST_NEW_INCOME_FORM);
            }
        });
        builder.setPositiveButton("Expense", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(getActivity(), NewExpenseWizard.class);
                intent.putExtra("preloadedLease", lease);
                startActivityForResult(intent, MainActivity.REQUEST_NEW_EXPENSE_FORM);
            }
        });
        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
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
                total = getTotal();
                setTotalTV();
            }
        }
    }

    @Override
    public void onClick(View view) {

    }

    private void setUpdateSelectedDateListeners() {

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd", Locale.US);

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
                for (int i = 0; i < currentFilteredMoney.size(); i++) {
                    total = total.add(currentFilteredMoney.get(i).getAmount());
                }
            }
        }
        return total;
    }

    private void setTotalTV() {
        if (total != null) {
            BigDecimal displayVal = total.setScale(2, RoundingMode.HALF_EVEN);
            NumberFormat usdCostFormat = NumberFormat.getCurrencyInstance(Locale.US);
            usdCostFormat.setMinimumFractionDigits(2);
            usdCostFormat.setMaximumFractionDigits(2);
            totalAmountTV.setText(usdCostFormat.format(displayVal.doubleValue()));
            if (total.compareTo(new BigDecimal(0)) < 0) {
                totalAmountTV.setTextColor(getActivity().getResources().getColor(R.color.red));
            } else {
                totalAmountTV.setTextColor(getActivity().getResources().getColor(R.color.green_colorPrimaryDark));
            }
        }
    }

    private void getFilteredMoney(){
        currentFilteredMoney.clear();
        currentFilteredMoney.addAll(db.getUsersIncomeByLeaseID(MainActivity.user, lease.getId()));
        currentFilteredMoney.addAll(db.getUsersExpensesByLeaseID(MainActivity.user, lease.getId()));
        dm.sortMoneyByDate(currentFilteredMoney);
    }
}



