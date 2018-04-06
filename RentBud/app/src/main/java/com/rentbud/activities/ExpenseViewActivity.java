package com.rentbud.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.cody.rentbud.R;
import com.rentbud.adapters.RecyclerViewAdapter;
import com.rentbud.fragments.ExpenseListFragment;
import com.rentbud.fragments.TenantListFragment;
import com.rentbud.helpers.MainArrayDataMethods;
import com.rentbud.model.Apartment;
import com.rentbud.model.ExpenseLogEntry;
import com.rentbud.model.Tenant;
import com.rentbud.sqlite.DatabaseHandler;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Cody on 3/28/2018.
 */

public class ExpenseViewActivity extends BaseActivity {
    ExpenseLogEntry expense;
    TextView dateTV, amountTV, typeTV, descriptionTV;
    ImageView receiptPicIV;
    DatabaseHandler databaseHandler;
    String receiptPic;
    MainArrayDataMethods dataMethods;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupUserAppTheme(MainActivity.curThemeChoice);
        setContentView(R.layout.activity_expense_view);
        this.databaseHandler = new DatabaseHandler(this);
        dataMethods = new MainArrayDataMethods();
        //if recreated
        if (savedInstanceState != null) {

        } else {
            //If new
            Bundle bundle = getIntent().getExtras();
            //Get apartment item
            int expenseID = bundle.getInt("expenseID");
            this.expense = databaseHandler.getExpenseLogEntryByID(expenseID, MainActivity.user);
            if (expense.getReceiptPic() != null) {
                this.receiptPic = expense.getReceiptPic();
            }
        }
        this.dateTV = findViewById(R.id.expenseViewDateTV);
        this.amountTV = findViewById(R.id.expenseViewAmountTV);
        this.typeTV = findViewById(R.id.expenseViewTypeTV);
        this.descriptionTV = findViewById(R.id.expenseViewDescriptionTV);
        this.receiptPicIV = findViewById(R.id.expenseViewReceiptPicIV);
        fillTextViews();
        if (receiptPic != null) {
            Glide.with(this).load(receiptPic).into(receiptPicIV);
        } else {
            receiptPicIV.setVisibility(View.GONE);
        }
        setupBasicToolbar();
    }

    private void fillTextViews() {
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
        dateTV.setText(formatter.format(expense.getExpenseDate()));

        BigDecimal displayVal = expense.getAmount().setScale(2, RoundingMode.HALF_EVEN);
        NumberFormat usdCostFormat = NumberFormat.getCurrencyInstance(Locale.US);
        usdCostFormat.setMinimumFractionDigits( 2 );
        usdCostFormat.setMaximumFractionDigits( 2 );

        amountTV.setText(usdCostFormat.format(displayVal.doubleValue()));
        typeTV.setText(expense.getTypeLabel());
        descriptionTV.setText(expense.getDescription());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Get options menu
        getMenuInflater().inflate(R.menu.expense_view_edit_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    //Handle option menu actions
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.editExpense:
                Intent intent = new Intent(this, NewExpenseFormActivity.class);
                intent.putExtra("expenseToEdit", expense);
                startActivityForResult(intent, MainActivity.REQUEST_NEW_EXPENSE_FORM);
                return true;

            case R.id.deleteExpense:

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MainActivity.REQUEST_NEW_EXPENSE_FORM) {
            //If successful(not cancelled, passed validation)
            if (resultCode == RESULT_OK) {
                //Re-query cached apartment array to update cache and refresh current fragment to display new data
                int expenseID = data.getIntExtra("editedExpenseID", 0);
                this.expense = databaseHandler.getExpenseLogEntryByID(expenseID, MainActivity.user);
                fillTextViews();
                ExpenseListFragment.expenseListAdapterNeedsRefreshed = true;
            }
        }
    }
}