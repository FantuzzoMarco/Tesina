package com.pitteriimpiantisrl.app;

import android.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by Utente on 27/05/2017.
 */

public class ButtonDisablerTextWatcher implements TextWatcher {

    private final Button button;
    private final EditText[] editTexts;

    public ButtonDisablerTextWatcher(Button button, EditText[] editTexts) {
        this.button = button;
        this.editTexts = editTexts;
        for(EditText editText: editTexts){
            editText.addTextChangedListener(this);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        for (EditText e : editTexts) {
            if (e.getText().toString().trim().isEmpty()) {
                button.setEnabled(false);
                return;
            }
        }
        button.setEnabled(true);
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
