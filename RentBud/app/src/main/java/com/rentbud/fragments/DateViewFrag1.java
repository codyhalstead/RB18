package com.rentbud.fragments;

import android.app.AlertDialog;
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
import com.rentbud.adapters.ExpenseListAdapter;
import com.rentbud.adapters.MoneyListAdapter;
import com.rentbud.model.Apartment;
import com.rentbud.model.ExpenseLogEntry;
import com.rentbud.model.MoneyLogEntry;
import com.rentbud.sqlite.DatabaseHandler;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static android.app.Activity.RESULT_OK;
import static android.support.constraint.Constraints.TAG;

public class DateViewFrag1 extends android.support.v4.app.Fragment implements AdapterView.OnItemClickListener, View.OnClickListener {


    public DateViewFrag1() {
        // Required empty public constructor
    }

    TextView noIncomeTV;
    FloatingActionButton fab;
    //  EditText searchBarET;
    //  Button dateRangeStartBtn, dateRangeEndBtn;
    MoneyListAdapter moneyListAdapter;
    ColorStateList accentColor;
    ListView listView;
    Date date;
    //public static boolean incomeListAdapterNeedsRefreshed;
    //  Date filterDateStart, filterDateEnd;
    //  private DatePickerDialog.OnDateSetListener dateSetFilterStartListener, dateSetFilterEndListener;
    private DatabaseHandler db;
    private ArrayList<MoneyLogEntry> currentFilteredIncomeAndExpenses;
    //private ExpenseLogEntry selectedExpense;

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
        fab.setVisibility(View.GONE);
        this.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent intent = new Intent(getActivity(), NewExpenseWizard.class);
                //startActivityForResult(intent, MainActivity.REQUEST_NEW_EXPENSE_FORM);
            }
        });
        this.listView = view.findViewById(R.id.mainMoneyListView);
        this.db = new DatabaseHandler(getContext());
        if (savedInstanceState != null) {


        } else {
            Date endDate = Calendar.getInstance().getTime();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(endDate);
            calendar.add(Calendar.YEAR, -1);
            Date startDate = calendar.getTime();


        }
        Bundle bundle = getArguments();
        //Get apartment item
        this.date = (Date) bundle.get("date");
        setUpdateSelectedDateListeners();
        // getActivity().setTitle("Income View");
        // ExpenseListFragment.expenseListAdapterNeedsRefreshed = false;
        //Get current theme accent color, which is passed into the list adapter for search highlighting
        TypedValue colorValue = new TypedValue();
        getActivity().getTheme().resolveAttribute(R.attr.colorAccent, colorValue, true);
        this.accentColor = getActivity().getResources().getColorStateList(colorValue.resourceId);
        currentFilteredIncomeAndExpenses = db.getIncomeAndExpensesForDate(MainActivity.user, date);
        setUpListAdapter();
        setUpSearchBar();
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
        if (currentFilteredIncomeAndExpenses != null) {
            moneyListAdapter = new MoneyListAdapter(getActivity(), currentFilteredIncomeAndExpenses, accentColor);
            listView.setAdapter(moneyListAdapter);
            listView.setOnItemClickListener(this);
            if (currentFilteredIncomeAndExpenses.isEmpty()) {
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
        // popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
        //     @Override
        //     public boolean onMenuItemClick(MenuItem item) {
        //         switch (item.getItemId()) {

        //             case R.id.edit:
        //                 //On listView row click, launch ApartmentViewActivity passing the rows data into it.
        //                 Intent intent = new Intent(getActivity(), NewExpenseWizard.class);
        //                 selectedExpense = expenseListAdapter.getFilteredResults().get(position);
        //                 intent.putExtra("expenseToEdit", selectedExpense);
        //                 startActivityForResult(intent, MainActivity.REQUEST_NEW_EXPENSE_FORM);
        //intent.putExtra("expenseID", expense.getId());
        //startActivity(intent);
        //                 return true;

        //            case R.id.remove:
        //                 selectedExpense = expenseListAdapter.getFilteredResults().get(position);
        //                 showDeleteConfirmationAlertDialog();
        //                 return true;

        //             default:
        //                 return false;
        //         }
        //     }
        // });
        // inflater.inflate(R.menu.expense_income_click_menu, popup.getMenu());
        // popup.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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

        if (currentFilteredIncomeAndExpenses != null) {
            outState.putParcelableArrayList("filteredIncomeAndExpenses", currentFilteredIncomeAndExpenses);
        }
    }
}