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
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }
}
