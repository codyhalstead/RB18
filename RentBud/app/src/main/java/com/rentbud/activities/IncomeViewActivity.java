package com.rentbud.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.cody.rentbud.R;
import com.rentbud.fragments.ExpenseListFragment;
import com.rentbud.fragments.IncomeListFragment;
import com.rentbud.helpers.MainArrayDataMethods;
import com.rentbud.model.ExpenseLogEntry;
import com.rentbud.model.PaymentLogEntry;
import com.rentbud.sqlite.DatabaseHandler;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Cody on 3/28/2018.
 */

public class IncomeViewActivity extends BaseActivity {
    PaymentLogEntry income;
    TextView dateTV, amountTV, typeTV, descriptionTV;
    DatabaseHandler databaseHandler;
    MainArrayDataMethods dataMethods;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupUserAppTheme(MainActivity.curThemeChoice);
        setContentView(R.layout.activity_income_view);
        this.databaseHandler = new DatabaseHandler(this);
        dataMethods = new MainArrayDataMethods();
        //if recreated
        if (savedInstanceState != null) {

        } else {
            //If new
            Bundle bundle = getIntent().getExtras();
            //Get apartment item
            int incomeID = bundle.getInt("incomeID");
            this.income = databaseHandler.getPaymentLogEntryByID(incomeID, MainActivity.user);
        }
        this.dateTV = findViewById(R.id.incomeViewDateTV);
        this.amountTV = findViewById(R.id.incomeViewAmountTV);
        this.typeTV = findViewById(R.id.incomeViewTypeTV);
        this.descriptionTV = findViewById(R.id.incomeViewDescriptionTV);
        fillTextViews();
        setupBasicToolbar();
    }

    private void fillTextViews() {
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
        dateTV.setText(formatter.format(income.getPaymentDate()));

        BigDecimal displayVal = income.getAmount().setScale(2, RoundingMode.HALF_EVEN);
        NumberFormat usdCostFormat = NumberFormat.getCurrencyInstance(Locale.US);
        usdCostFormat.setMinimumFractionDigits( 2 );
        usdCostFormat.setMaximumFractionDigits( 2 );

        amountTV.setText(usdCostFormat.format(displayVal.doubleValue()));

        typeTV.setText(income.getTypeLabel());
        descriptionTV.setText(income.getDescription());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Get options menu
        getMenuInflater().inflate(R.menu.income_view_edit_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    //Handle option menu actions
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.editIncome:
                Intent intent = new Intent(this, NewIncomeFormActivity.class);
                intent.putExtra("incomeToEdit", income);
                startActivityForResult(intent, MainActivity.REQUEST_NEW_INCOME_FORM);
                return true;

            case R.id.deleteIncome:

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MainActivity.REQUEST_NEW_INCOME_FORM) {
            //If successful(not cancelled, passed validation)
            if (resultCode == RESULT_OK) {
                //Re-query cached apartment array to update cache and refresh current fragment to display new data
                int incomeID = data.getIntExtra("editedIncomeID", 0);
                this.income = databaseHandler.getPaymentLogEntryByID(incomeID, MainActivity.user);
                fillTextViews();
                IncomeListFragment.incomeListAdapterNeedsRefreshed = true;
            }
        }
    }
}