package com.rentbud.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
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
import com.rentbud.activities.ApartmentViewActivity;
import com.rentbud.activities.ExpenseViewActivity;
import com.rentbud.activities.IncomeViewActivity;
import com.rentbud.activities.MainActivity;
import com.rentbud.activities.NewExpenseWizard;
import com.rentbud.adapters.ExpenseListAdapter;
import com.rentbud.adapters.IncomeListAdapter;
import com.rentbud.helpers.MainArrayDataMethods;
import com.rentbud.model.Apartment;
import com.rentbud.model.ExpenseLogEntry;
import com.rentbud.model.PaymentLogEntry;
import com.rentbud.sqlite.DatabaseHandler;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;

public class ApartmentViewFrag2 extends android.support.v4.app.Fragment implements AdapterView.OnItemClickListener, View.OnClickListener {


    public ApartmentViewFrag2() {
        // Required empty public constructor
    }

    TextView noIncomeTV;
    FloatingActionButton fab;
    //  EditText searchBarET;
    //  Button dateRangeStartBtn, dateRangeEndBtn;
    ExpenseListAdapter expenseListAdapter;
    ColorStateList accentColor;
    ListView listView;
    Apartment apartment;
    //public static boolean incomeListAdapterNeedsRefreshed;
    //  Date filterDateStart, filterDateEnd;
    //  private DatePickerDialog.OnDateSetListener dateSetFilterStartListener, dateSetFilterEndListener;
    private DatabaseHandler db;
    private ArrayList<ExpenseLogEntry> currentFilteredExpenses;
    private ExpenseLogEntry selectedExpense;

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
        this.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), NewExpenseWizard.class);
                startActivityForResult(intent, MainActivity.REQUEST_NEW_EXPENSE_FORM);
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
        this.apartment = bundle.getParcelable("apartment");
        setUpdateSelectedDateListeners();
        // getActivity().setTitle("Income View");
        // ExpenseListFragment.expenseListAdapterNeedsRefreshed = false;
        //Get current theme accent color, which is passed into the list adapter for search highlighting
        TypedValue colorValue = new TypedValue();
        getActivity().getTheme().resolveAttribute(R.attr.colorAccent, colorValue, true);
        this.accentColor = getActivity().getResources().getColorStateList(colorValue.resourceId);
        currentFilteredExpenses = db.getUsersExpensesByApartmentID(MainActivity.user, apartment.getId());
        setUpListAdapter();
        setUpSearchBar();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (IncomeListFragment.incomeListAdapterNeedsRefreshed) {
            if (this.expenseListAdapter != null) {
                //   incomeListAdapterNeedsRefreshed = false;
                expenseListAdapter.getFilter().filter("");
            }
        }
    }

    private void setUpSearchBar() {

    }

    private void setUpListAdapter() {
        if (currentFilteredExpenses != null) {
            expenseListAdapter = new ExpenseListAdapter(getActivity(), currentFilteredExpenses, accentColor);
            listView.setAdapter(expenseListAdapter);
            listView.setOnItemClickListener(this);
            if (currentFilteredExpenses.isEmpty()) {
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
                        Intent intent = new Intent(getActivity(), NewExpenseWizard.class);
                        selectedExpense = expenseListAdapter.getFilteredResults().get(position);
                        intent.putExtra("expenseToEdit", selectedExpense);
                        startActivityForResult(intent, MainActivity.REQUEST_NEW_EXPENSE_FORM);
                        //intent.putExtra("expenseID", expense.getId());
                        //startActivity(intent);
                        return true;

                    case R.id.remove:
                        selectedExpense = expenseListAdapter.getFilteredResults().get(position);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MainActivity.REQUEST_NEW_EXPENSE_FORM) {
            //If successful(not cancelled, passed validation)
            if (resultCode == RESULT_OK) {
                currentFilteredExpenses = db.getUsersExpensesByApartmentID(MainActivity.user, apartment.getId());
                expenseListAdapter.updateResults(currentFilteredExpenses);
                expenseListAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onClick(View view) {

    }

    private void setUpdateSelectedDateListeners() {

    }

    public void showDeleteConfirmationAlertDialog() {
        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        //builder.setTitle("AlertDialog");
        builder.setMessage("Are you sure you want to remove this expense?");

        // add the buttons
        builder.setPositiveButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                db.setExpenseInactive(selectedExpense);
                ExpenseListFragment.expenseListAdapterNeedsRefreshed = true;
                currentFilteredExpenses = db.getUsersExpensesByApartmentID(MainActivity.user, apartment.getId());
                expenseListAdapter.updateResults(currentFilteredExpenses);
                expenseListAdapter.notifyDataSetChanged();
                //ExpenseViewActivity.this.finish();
            }
        });

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd", Locale.US);

        if (currentFilteredExpenses != null) {
            outState.putParcelableArrayList("filteredExpenses", currentFilteredExpenses);
        }
    }
}
