package com.rentbud.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.cody.rentbud.R;
import com.rentbud.fragments.ExpenseListFragment;
import com.rentbud.helpers.MainArrayDataMethods;
import com.rentbud.helpers.NewItemCreatorDialog;
import com.rentbud.helpers.UserInputValidation;
import com.rentbud.model.Apartment;
import com.rentbud.model.ExpenseLogEntry;
import com.rentbud.model.Tenant;
import com.rentbud.sqlite.DatabaseHandler;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Cody on 3/28/2018.
 */

public class NewExpenseFormActivity extends BaseActivity {
    EditText amountET, descriptionET;
    TextView dateTV;
    Button saveBtn, cancelBtn, newTypeBtn;
    Spinner expenseTypeSpinner;
    DatabaseHandler databaseHandler;
    Boolean isEdit;
    ExpenseLogEntry expenseToEdit;
    UserInputValidation validation;
    MainArrayDataMethods dataMethods;
    Date expenseDate;
    BigDecimal currentAmount;
    ArrayAdapter<String> adapter;
    private DatePickerDialog.OnDateSetListener setExpenseDateListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupUserAppTheme(MainActivity.curThemeChoice);
        setContentView(R.layout.activity_new_expense_form);
        setupBasicToolbar();
        initializeVariables();
        setOnClickListeners();
        populateExpenseTypeSpinner();
        loadExpenseDataIfEditing();
        amountET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (amountET == null) return;
                String s = editable.toString();
                if (s.isEmpty()) return;
                amountET.removeTextChangedListener(this);
                String cleanString = s.replaceAll("[$,.]", "");
                currentAmount = new BigDecimal(cleanString).setScale(2, BigDecimal.ROUND_FLOOR).divide(new BigDecimal(100), BigDecimal.ROUND_FLOOR);
                String formatted = NumberFormat.getCurrencyInstance().format(currentAmount);
                amountET.setText(formatted);
                amountET.setSelection(formatted.length());
                amountET.addTextChangedListener(this);
            }
        });
    }

    private void initializeVariables() {
        this.dataMethods = new MainArrayDataMethods();
        this.databaseHandler = new DatabaseHandler(this);
        this.validation = new UserInputValidation(this);
        this.dateTV = findViewById(R.id.expenseFormDateTV);
        this.amountET = findViewById(R.id.expenseFormAmountET);
        amountET.setSelection(amountET.getText().length());
        this.descriptionET = findViewById(R.id.expenseFormDescriptionET);
        this.saveBtn = findViewById(R.id.expenseFormSaveBtn);
        this.cancelBtn = findViewById(R.id.expenseFormCancelBtn);
        this.newTypeBtn = findViewById(R.id.expenseFormTypeBtn);
        this.expenseTypeSpinner = findViewById(R.id.expenseFormTypeSpinner);
        this.isEdit = false;
        this.currentAmount = new BigDecimal(0);
    }

    private void loadExpenseDataIfEditing() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            this.isEdit = true;
            expenseToEdit = extras.getParcelable("expenseToEdit");
            if(expenseToEdit.getExpenseDate() != null) {
                this.expenseDate = expenseToEdit.getExpenseDate();
                SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
                dateTV.setText(formatter.format(expenseDate));
            }
            //currentAmount = expenseToEdit.getAmount();
            //amountET.setText(expenseToEdit.getAmount().toPlainString());
            String s = expenseToEdit.getAmount().toPlainString();
            if (s.isEmpty()) return;
            String cleanString = s.replaceAll("[$,.]", "");
            currentAmount = new BigDecimal(cleanString).setScale(2, BigDecimal.ROUND_FLOOR).divide(new BigDecimal(100), BigDecimal.ROUND_FLOOR);
            String formatted = NumberFormat.getCurrencyInstance().format(currentAmount);
            amountET.setText(formatted);
            amountET.setSelection(formatted.length());
            //amountET.setSelection(amountET.getText().length());
            String label = expenseToEdit.getTypeLabel();
            if (label != null) {
                int spinnerPosition = adapter.getPosition(label);
                expenseTypeSpinner.setSelection(spinnerPosition);
            }
            descriptionET.setText(expenseToEdit.getDescription());
        }
    }

    private void setOnClickListeners() {
        //Sets onClickListener to saveBtn
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validate()) {
                    return;
                }
                //Get users input data
                String amountString = amountET.getText().toString().trim();
               // BigDecimal amount = new BigDecimal(amountString);
                String typeLabel = expenseTypeSpinner.getSelectedItem().toString();
                int typeID = MainActivity.expenseTypeLabels.get(typeLabel);
                String description = descriptionET.getText().toString().trim();
                int id = -1;
                if (expenseToEdit != null) {
                    id = expenseToEdit.getId();
                }
                //Create new Tenant object with input data and add it to the database
                //Set result success, close this activity
                if (!isEdit) {
                    ExpenseLogEntry expense = new ExpenseLogEntry(id, expenseDate, currentAmount, 0, description, typeID, typeLabel, null);
                    databaseHandler.addExpenseLogEntry(expense, MainActivity.user.getId());
                    //MainActivity.expenseList = databaseHandler.getUsersExpenses(MainActivity.user);
                    setResult(RESULT_OK);
                    finish();
                } else {
                    //ExpenseLogEntry originalExpense = dataMethods.getCachedExpenseByID(expenseToEdit.getId());
                    expenseToEdit.setExpenseDate(expenseDate);
                    expenseToEdit.setAmount(currentAmount);
                    expenseToEdit.setTypeID(typeID);
                    expenseToEdit.setTypeLabel(typeLabel);
                    expenseToEdit.setDescription(description);

                    databaseHandler.editExpenseLogEntry(expenseToEdit);
                    //dataMethods.sortMainExpenseArray();
                    Intent data = new Intent();
                    data.putExtra("editedExpenseID", expenseToEdit.getId());
                    setResult(RESULT_OK, data);
                    finish();
                }
            }
        });
        //Sets onClickListener to cancelBtn
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Set result to cancelled and close this activity
                Intent data = new Intent();
                setResult(RESULT_CANCELED, data);
                finish();
            }
        });
        setExpenseDateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                //Once user selects date from date picker pop-up,
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.YEAR, year);
                cal.set(Calendar.MONTH, month);
                cal.set(Calendar.DATE, day);
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
                expenseDate = cal.getTime();

                SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
                dateTV.setText(formatter.format(expenseDate));
            }
        };

        dateTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                if(expenseDate != null) {
                    cal.setTime(expenseDate);
                }
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dialog = new DatePickerDialog(NewExpenseFormActivity.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, setExpenseDateListener, year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        newTypeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NewItemCreatorDialog dialog = new NewItemCreatorDialog(NewExpenseFormActivity.this);
                dialog.show();
                dialog.setDialogResult(new NewItemCreatorDialog.NewItemDialogResult(){
                    @Override
                    public void finish(String string) {
                        databaseHandler.addNewExpenseType(string);
                        MainActivity.expenseTypeLabels = databaseHandler.getExpenseTypeLabels();
                        updateExpenseTypeSpinner();
                        int spinnerPosition = adapter.getPosition(string);
                        expenseTypeSpinner.setSelection(spinnerPosition);
                    }
                });
            }
        });
    }

    public boolean validate() {
        boolean valid = true;

        return valid;
    }

    private void populateExpenseTypeSpinner() {
        //Create state array from MainActivity.stateMap
        List<String> spinnerArray = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : MainActivity.expenseTypeLabels.entrySet()) {
            spinnerArray.add(entry.getKey());
        }
        //Create ArrayAdapter with state array
        adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, spinnerArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Set ArrayAdapter to stateSpinner
        this.expenseTypeSpinner.setAdapter(adapter);
    }

    public void updateExpenseTypeSpinner(){
        List<String> spinnerArray = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : MainActivity.expenseTypeLabels.entrySet()) {
            spinnerArray.add(entry.getKey());
        }
        adapter.clear();
        adapter.addAll(spinnerArray);
    }
}
