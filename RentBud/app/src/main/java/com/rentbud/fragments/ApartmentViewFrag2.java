package com.rentbud.fragments;

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
import android.widget.ListView;
import android.widget.TextView;

import com.example.cody.rentbud.R;
import com.rentbud.activities.MainActivity;
import com.rentbud.activities.NewExpenseWizard;
import com.rentbud.activities.NewIncomeWizard;
import com.rentbud.adapters.MoneyListAdapter;
import com.rentbud.helpers.ApartmentTenantViewModel;
import com.rentbud.helpers.DateAndCurrencyDisplayer;
import com.rentbud.model.Apartment;
import com.rentbud.model.ExpenseLogEntry;
import com.rentbud.model.MoneyLogEntry;
import com.rentbud.model.PaymentLogEntry;
import com.rentbud.sqlite.DatabaseHandler;

import java.math.BigDecimal;

import static android.app.Activity.RESULT_OK;

public class ApartmentViewFrag2 extends android.support.v4.app.Fragment implements AdapterView.OnItemClickListener, View.OnClickListener {


    public ApartmentViewFrag2() {
        // Required empty public constructor
    }

    TextView noPaymentsTV, totalAmountTV, totalAmountLabelTV;
    FloatingActionButton fab;
    //  EditText searchBarET;
    //  Button dateRangeStartBtn, dateRangeEndBtn;
    MoneyListAdapter moneyListAdapter;
    ColorStateList accentColor;
    ListView listView;
    //Date startDateRange, endDateRange;
    //public static boolean incomeListAdapterNeedsRefreshed;
    //  Date filterDateStart, filterDateEnd;
    //  private DatePickerDialog.OnDateSetListener dateSetFilterStartListener, dateSetFilterEndListener;
    private DatabaseHandler db;
    //private ArrayList<MoneyLogEntry> currentFilteredMoney;
    private MoneyLogEntry selectedMoney;
    private Apartment apartment;
    private BigDecimal total;
    private OnMoneyDataChangedListener mCallback;
    private SharedPreferences preferences;
    private AlertDialog dialog;
    private PopupMenu popupMenu;

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
        this.db = new DatabaseHandler(getContext());
        this.preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        this.apartment = ViewModelProviders.of(getActivity()).get(ApartmentTenantViewModel.class).getApartment().getValue();

        total = getTotal();
        this.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showNewIncomeOrExpenseAlertDialog();
            }
        });
        //Get apartment item
        setUpdateSelectedDateListeners();
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
                    + " must implement OnLeaseDataChangedListener");
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
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }

    private void setUpListAdapter() {
        if (ViewModelProviders.of(getActivity()).get(ApartmentTenantViewModel.class).getMoneyArray().getValue() != null) {
            moneyListAdapter = new MoneyListAdapter(getActivity(), ViewModelProviders.of(getActivity()).get(ApartmentTenantViewModel.class).getMoneyArray().getValue(), accentColor, null, true);
            listView.setAdapter(moneyListAdapter);
            listView.setOnItemClickListener(this);
            noPaymentsTV.setText(R.string.no_payments_to_display_apartment);
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

                    case R.id.edit:
                        //On listView row click, launch ApartmentViewActivity passing the rows data into it.
                        selectedMoney = moneyListAdapter.getFilteredResults().get(position);
                        if (selectedMoney instanceof PaymentLogEntry) {
                            //mCallback.onIncomeDataChanged();
                            Intent intent = new Intent(getActivity(), NewIncomeWizard.class);
                            PaymentLogEntry selectedIncome = (PaymentLogEntry) selectedMoney;
                            intent.putExtra("incomeToEdit", selectedIncome);
                            startActivityForResult(intent, MainActivity.REQUEST_NEW_INCOME_FORM);
                        } else {
                            //mCallback.onExpenseDataChanged();
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
                    //currentFilteredMoney = db.getIncomeAndExpensesByApartmentIDWithinDates(MainActivity.user, apartment.getId(), startDateRange, endDateRange);
                    //moneyListAdapter.updateResults(currentFilteredMoney);
                    mCallback.onMoneyDataChanged();
                    mCallback.onIncomeDataChanged();
                    //moneyListAdapter.notifyDataSetChanged();
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
                   // ExpenseListFragment.expenseListAdapterNeedsRefreshed = true;
                    //currentFilteredMoney = db.getIncomeAndExpensesByApartmentIDWithinDates(MainActivity.user, apartment.getId(), startDateRange, endDateRange);
                    //moneyListAdapter.updateResults(currentFilteredMoney);
                    mCallback.onMoneyDataChanged();
                    mCallback.onExpenseDataChanged();
                    //moneyListAdapter.notifyDataSetChanged();
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
                intent.putExtra("preloadedApartment", apartment);
                startActivityForResult(intent, MainActivity.REQUEST_NEW_INCOME_FORM);
            }
        });
        builder.setNegativeButton(R.string.expense, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(getActivity(), NewExpenseWizard.class);
                intent.putExtra("preloadedApartment", apartment);
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
                //this.currentFilteredMoney = db.getIncomeAndExpensesByApartmentIDWithinDates(MainActivity.user, apartment.getId(), startDateRange, endDateRange);
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
                //this.currentFilteredMoney = db.getIncomeAndExpensesByApartmentIDWithinDates(MainActivity.user, apartment.getId(), startDateRange, endDateRange);
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
            int moneyFormatCode = preferences.getInt("currency", DateAndCurrencyDisplayer.CURRENCY_US);
            totalAmountTV.setText(DateAndCurrencyDisplayer.getCurrencyToDisplay(moneyFormatCode, total));
            if (total.compareTo(new BigDecimal(0)) < 0) {
                totalAmountTV.setTextColor(getActivity().getResources().getColor(R.color.red));
            } else {
                totalAmountTV.setTextColor(getActivity().getResources().getColor(R.color.green_colorPrimaryDark));
            }
        }
    }

    public void updateData() {
        moneyListAdapter.updateResults(ViewModelProviders.of(getActivity()).get(ApartmentTenantViewModel.class).getMoneyArray().getValue());
        moneyListAdapter.notifyDataSetChanged();
        this.total = getTotal();
        setTotalTV();
    }
}