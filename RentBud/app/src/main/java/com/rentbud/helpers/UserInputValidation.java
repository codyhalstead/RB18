package com.rentbud.helpers;

import android.app.Activity;
import android.content.Context;
import android.support.design.widget.TextInputEditText;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * Created by Cody on 12/13/2017.
 */

public class UserInputValidation {
    private Context context;

    public UserInputValidation(Context context) {
        this.context = context;
    }

    //Used to check if edit text input is not empty with custom message error
    public boolean isInputEditTextFilled(TextInputEditText editText, String message) {
        String value = editText.getText().toString().trim();
        if (value.isEmpty()) {
            editText.setError(message);
            hideKeyboardFrom(editText);
            return false;
        } else {
            editText.setError(null);
        }
        return true;
    }

    //Used to check if edit text input is not empty and fits Email criteria (ex: O@O.O) with custom message error
    public boolean isInputEditTextEmail(TextInputEditText editText, String message) {
        String value = editText.getText().toString().trim();
        if (value.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(value).matches()) {
            editText.setError(message);
            hideKeyboardFrom(editText);
            return false;
        } else {
            editText.setError(null);
        }
        return true;
    }

    //Used to check if edit text input is not empty and fits password criteria (Between 4 and 15 characters) with custom message error
    public boolean isInputEditTextPassword(TextInputEditText editText, String message){
        String value = editText.getText().toString().trim();
        if (value.isEmpty() || value.length() < 4 || value.length() > 15 ){
            editText.setError(message);
            hideKeyboardFrom(editText);
            return false;
        } else {
            editText.setError(null);
        }
        return true;
    }

    //Used to check if edit text input 1 matches edit text input 2, with custom message error set to edit text 2
    public boolean isInputEditTextMatches(TextInputEditText editText1, TextInputEditText editText2, String message) {
        String value1 = editText1.getText().toString().trim();
        String value2 = editText2.getText().toString().trim();
        if (!value1.contentEquals(value2)) {
            editText2.setError(message);
            hideKeyboardFrom(editText2);
            return false;
        } else {
            editText2.setError(null);
        }
        return true;
    }

    //Used to check if edit text fits name criteria (Not empty) with custom message error
    public boolean isInputEditTextName(TextInputEditText editText, String message){
        String value = editText.getText().toString().trim();
        if(value.isEmpty()){
            editText.setError(message);
            hideKeyboardFrom(editText);
            return false;
        }else{
            editText.setError(null);
        }
        return true;
    }

    private void hideKeyboardFrom(View view) {
        //Hide keyboard
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }
}
