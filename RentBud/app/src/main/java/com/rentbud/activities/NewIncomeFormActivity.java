package com.rentbud.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.cody.rentbud.R;
import com.rentbud.helpers.MainArrayDataMethods;
import com.rentbud.helpers.NewItemCreatorDialog;
import com.rentbud.helpers.UserInputValidation;
import com.rentbud.model.Apartment;
import com.rentbud.model.ExpenseLogEntry;
import com.rentbud.model.PaymentLogEntry;
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

public class NewIncomeFormActivity extends BaseActivity {
    EditText amountET, descriptionET;
    TextView dateTV;
    Button saveBtn, cancelBtn, newTypeBtn;
    Spinner incomeTypeSpinner;
    DatabaseHandler databaseHandler;
    Boolean isEdit;
    PaymentLogEntry incomeToEdit;
    UserInputValidation validation;
    MainArrayDataMethods dataMethods;
    Date incomeDate;
    BigDecimal currentAmount;
    ArrayAdapter<String> adapter;
    private DatePickerDialog.OnDateSetListener setIncomeDateListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupUserAppTheme(MainActivity.curThemeChoice);
        setContentView(R.layout.activity_new_income_form);
        setupBasicToolbar();
        initializeVariables();
        setOnClickListeners();
        populateIncomeTypeSpinner();
        loadIncomeDataIfEditing();
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
        this.dateTV = findViewById(R.id.incomeFormDateTV);
        this.amountET = findViewById(R.id.incomeFormAmountET);
        amountET.setSelection(amountET.getText().length());
        this.descriptionET = findViewById(R.id.incomeFormDescriptionET);
        this.saveBtn = findViewById(R.id.incomeFormSaveBtn);
        this.cancelBtn = findViewById(R.id.incomeFormCancelBtn);
        this.newTypeBtn = findViewById(R.id.incomeFormTypeBtn);
        this.incomeTypeSpinner = findViewById(R.id.incomeFormTypeSpinner);
        this.isEdit = false;
        this.currentAmount = new BigDecimal(0);
    }

    private void loadIncomeDataIfEditing() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            this.isEdit = true;
            incomeToEdit = extras.getParcelable("incomeToEdit");
            if(incomeToEdit.getPaymentDate() != null) {
                this.incomeDate = incomeToEdit.getPaymentDate();
                SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
                dateTV.setText(formatter.format(incomeDate));
            }
            //currentAmount = expenseToEdit.getAmount();
            //amountET.setText(incomeToEdit.getAmount().toPlainString());
            String s = incomeToEdit.getAmount().toPlainString();
            if (s.isEmpty()) return;
            String cleanString = s.replaceAll("[$,.]", "");
            currentAmount = new BigDecimal(cleanString).setScale(2, BigDecimal.ROUND_FLOOR).divide(new BigDecimal(100), BigDecimal.ROUND_FLOOR);
            String formatted = NumberFormat.getCurrencyInstance().format(currentAmount);
            amountET.setText(formatted);
            amountET.setSelection(formatted.length());
            String label = incomeToEdit.getTypeLabel();
            if (label != null) {
                int spinnerPosition = adapter.getPosition(label);
                incomeTypeSpinner.setSelection(spinnerPosition);
            }
            descriptionET.setText(incomeToEdit.getDescription());
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
                //BigDecimal amount = new BigDecimal(amountString);
                String typeLabel = incomeTypeSpinner.getSelectedItem().toString();
                int typeID = MainActivity.incomeTypeLabels.get(typeLabel);
                String description = descriptionET.getText().toString().trim();
                int id = -1;
                if (incomeToEdit != null) {
                    id = incomeToEdit.getId();
                }
                //Create new Tenant object with input data and add it to the database

                //Set result success, close this activity
                if (!isEdit) {
                    PaymentLogEntry income = new PaymentLogEntry(id, incomeDate, typeID, typeLabel, 0, 0, currentAmount, description, "");
                    databaseHandler.addPaymentLogEntry(income, MainActivity.user.getId());
                    //MainActivity5.incomeList = databaseHandler.getUsersIncome(MainActivity5.user);
                    setResult(RESULT_OK);
                    finish();
                } else {
                    //PaymentLogEntry originalIncome = dataMethods.getCachedIncomeByID(incomeToEdit.getId());
                    incomeToEdit.setPaymentDate(incomeDate);
                    incomeToEdit.setAmount(currentAmount);
                    incomeToEdit.setTypeID(typeID);
                    incomeToEdit.setTypeLabel(typeLabel);
                    incomeToEdit.setDescription(description);

                    databaseHandler.editPaymentLogEntry(incomeToEdit);
                    //dataMethods.sortMainIncomeArray();
                    Intent data = new Intent();
                    data.putExtra("editedIncomeID", incomeToEdit.getId());
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
        setIncomeDateListener = new DatePickerDialog.OnDateSetListener() {
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
                incomeDate = cal.getTime();

                SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
                dateTV.setText(formatter.format(incomeDate));
            }
        };

        dateTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                if(incomeDate != null) {
                    cal.setTime(incomeDate);
                }
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dialog = new DatePickerDialog(NewIncomeFormActivity.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, setIncomeDateListener, year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        newTypeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NewItemCreatorDialog dialog = new NewItemCreatorDialog(NewIncomeFormActivity.this);
                dialog.show();
                dialog.setDialogResult(new NewItemCreatorDialog.NewItemDialogResult(){
                    @Override
                    public void finish(String string) {
                        databaseHandler.addNewIncomeType(string);
                        MainActivity.incomeTypeLabels = databaseHandler.getIncomeTypeLabels();
                        updateIncomeTypeSpinner();
                        int spinnerPosition = adapter.getPosition(string);
                        incomeTypeSpinner.setSelection(spinnerPosition);
                    }
                });
            }
        });
    }

    public boolean validate() {
        boolean valid = true;

        return valid;
    }

    private void populateIncomeTypeSpinner() {
        //Create state array from MainActivity5.stateMap
        List<String> spinnerArray = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : MainActivity.incomeTypeLabels.entrySet()) {
            spinnerArray.add(entry.getKey());
        }
        //Create ArrayAdapter with state array
        adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, spinnerArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Set ArrayAdapter to stateSpinner
        this.incomeTypeSpinner.setAdapter(adapter);
    }

    public void updateIncomeTypeSpinner(){
        List<String> spinnerArray = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : MainActivity.incomeTypeLabels.entrySet()) {
            spinnerArray.add(entry.getKey());
        }
        adapter.clear();
        adapter.addAll(spinnerArray);
    }
}
