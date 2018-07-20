package com.rentbud.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
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
import android.widget.ListView;
import android.widget.TextView;

import com.example.cody.rentbud.R;
import com.rentbud.activities.MainActivity;
import com.rentbud.activities.NewExpenseWizard;
import com.rentbud.activities.NewIncomeWizard;
import com.rentbud.adapters.ExpenseListAdapter;
import com.rentbud.adapters.MoneyListAdapter;
import com.rentbud.helpers.ApartmentTenantViewModel;
import com.rentbud.model.Apartment;
import com.rentbud.model.ExpenseLogEntry;
import com.rentbud.model.MoneyLogEntry;
import com.rentbud.model.PaymentLogEntry;
import com.rentbud.sqlite.DatabaseHandler;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;
import static android.support.constraint.Constraints.TAG;

public class DateViewFrag1 extends android.support.v4.app.Fragment implements AdapterView.OnItemClickListener, View.OnClickListener {


    public DateViewFrag1() {
        // Required empty public constructor
    }

    TextView noIncomeTV, totalAmountTV, totalAmountLabelTV;
    ;
    FloatingActionButton fab;
    //  EditText searchBarET;
    //  Button dateRangeStartBtn, dateRangeEndBtn;
    MoneyListAdapter moneyListAdapter;
    ColorStateList accentColor;
    ListView listView;
    //Date date;
    //public static boolean incomeListAdapterNeedsRefreshed;
    //  Date filterDateStart, filterDateEnd;
    //  private DatePickerDialog.OnDateSetListener dateSetFilterStartListener, dateSetFilterEndListener;
    private DatabaseHandler db;
    //private ArrayList<MoneyLogEntry> currentFilteredIncomeAndExpenses;
    BigDecimal total;
    private MoneyLogEntry selectedMoneyEntry;
    private OnMoneyDataChangedListener mCallback;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.lease_view_fragment_two, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //this.noIncomeTV = view.findViewById(R.id.moneyEmptyListTV);
        this.fab = view.findViewById(R.id.listFab);
        //fab.setVisibility(View.GONE);
        this.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showNewIncomeOrExpenseAlertDialog();
            }
        });
        this.totalAmountLabelTV = view.findViewById(R.id.moneyListTotalAmountLabelTV);
        this.totalAmountTV = view.findViewById(R.id.moneyListTotalAmountTV);
        this.listView = view.findViewById(R.id.mainMoneyListView);
        this.db = new DatabaseHandler(getContext());

        total = getTotal();

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
       // if (IncomeListFragment.incomeListAdapterNeedsRefreshed) {
       //     if (this.moneyListAdapter != null) {
       //         //   incomeListAdapterNeedsRefreshed = false;
       //         moneyListAdapter.getFilter().filter("");
       //     }
       // }
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
            moneyListAdapter = new MoneyListAdapter(getActivity(), ViewModelProviders.of(getActivity()).get(ApartmentTenantViewModel.class).getMoneyArray().getValue(), accentColor);
            listView.setAdapter(moneyListAdapter);
            listView.setOnItemClickListener(this);
            if (ViewModelProviders.of(getActivity()).get(ApartmentTenantViewModel.class).getMoneyArray().getValue().isEmpty()) {
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
                        selectedMoneyEntry = moneyListAdapter.getFilteredResults().get(position);
                        if (selectedMoneyEntry instanceof PaymentLogEntry) {
                            Intent intent = new Intent(getActivity(), NewIncomeWizard.class);
                            PaymentLogEntry selectedIncome = (PaymentLogEntry) selectedMoneyEntry;
                            intent.putExtra("incomeToEdit", selectedIncome);
                            startActivityForResult(intent, MainActivity.REQUEST_NEW_INCOME_FORM);
                        } else {
                            Intent intent = new Intent(getActivity(), NewExpenseWizard.class);
                            ExpenseLogEntry selectedExpense = (ExpenseLogEntry) selectedMoneyEntry;
                            intent.putExtra("expenseToEdit", selectedExpense);
                            startActivityForResult(intent, MainActivity.REQUEST_NEW_EXPENSE_FORM);
                        }

                        return true;

                    case R.id.remove:
                        selectedMoneyEntry = moneyListAdapter.getFilteredResults().get(position);
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
        if (selectedMoneyEntry instanceof PaymentLogEntry) {
            builder.setMessage("Are you sure you want to remove this income?");
            builder.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    db.setPaymentLogEntryInactive((PaymentLogEntry) selectedMoneyEntry);
                    //IncomeListFragment.incomeListAdapterNeedsRefreshed = true;
                    //currentFilteredIncomeAndExpenses = db.getIncomeAndExpensesForDate(MainActivity.user, date);
                    //moneyListAdapter.updateResults(currentFilteredIncomeAndExpenses);
                    //moneyListAdapter.notifyDataSetChanged();
                    mCallback.onMoneyDataChanged();
                    mCallback.onIncomeDataChanged();
                    total = getTotal();
                    setTotalTV();
                }
            });
        } else {
            builder.setMessage("Are you sure you want to remove this expense?");
            builder.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    db.setExpenseInactive((ExpenseLogEntry) selectedMoneyEntry);
                   // ExpenseListFragment.expenseListAdapterNeedsRefreshed = true;
                   // currentFilteredIncomeAndExpenses = db.getIncomeAndExpensesForDate(MainActivity.user, date);
                   // moneyListAdapter.updateResults(currentFilteredIncomeAndExpenses);
                   // moneyListAdapter.notifyDataSetChanged();
                    mCallback.onMoneyDataChanged();
                    mCallback.onExpenseDataChanged();
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
                SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
                intent.putExtra("preloadedDate", formatter.format(ViewModelProviders.of(getActivity()).get(ApartmentTenantViewModel.class).getDate().getValue()));
                startActivityForResult(intent, MainActivity.REQUEST_NEW_INCOME_FORM);
            }
        });
        builder.setPositiveButton("Expense", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(getActivity(), NewExpenseWizard.class);
                SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
                intent.putExtra("preloadedDate", formatter.format(ViewModelProviders.of(getActivity()).get(ApartmentTenantViewModel.class).getDate().getValue()));
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
        if (requestCode == MainActivity.REQUEST_NEW_EXPENSE_FORM) {
            //If successful(not cancelled, passed validation)
            if (resultCode == RESULT_OK) {
                //currentFilteredIncomeAndExpenses = db.getIncomeAndExpensesForDate(MainActivity.user, date);
                //moneyListAdapter.updateResults(currentFilteredIncomeAndExpenses);
                //moneyListAdapter.notifyDataSetChanged();
                mCallback.onMoneyDataChanged();
                mCallback.onExpenseDataChanged();
                total = getTotal();
                setTotalTV();
            }
        }
        if (requestCode == MainActivity.REQUEST_NEW_INCOME_FORM) {
            //If successful(not cancelled, passed validation)
            if (resultCode == RESULT_OK) {
                //currentFilteredIncomeAndExpenses = db.getIncomeAndExpensesForDate(MainActivity.user, date);
                //moneyListAdapter.updateResults(currentFilteredIncomeAndExpenses);
                //moneyListAdapter.notifyDataSetChanged();
                mCallback.onMoneyDataChanged();
                mCallback.onIncomeDataChanged();
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

    }

    private BigDecimal getTotal() {
        BigDecimal total = new BigDecimal(0);
        if (ViewModelProviders.of(getActivity()).get(ApartmentTenantViewModel.class).getMoneyArray().getValue() != null) {
            if (!ViewModelProviders.of(getActivity()).get(ApartmentTenantViewModel.class).getMoneyArray().getValue().isEmpty()) {
                for (int i = 0; i < ViewModelProviders.of(getActivity()).get(ApartmentTenantViewModel.class).getMoneyArray().getValue().size(); i++) {
                    total = total.add(ViewModelProviders.of(getActivity()).get(ApartmentTenantViewModel.class).getMoneyArray().getValue().get(i).getAmount());
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

    public void updateData(){
        moneyListAdapter.updateResults(ViewModelProviders.of(getActivity()).get(ApartmentTenantViewModel.class).getMoneyArray().getValue());
        moneyListAdapter.notifyDataSetChanged();
        this.total = getTotal();
        setTotalTV();
    }
}