package com.vatsi.locus;
import java.util.regex.Pattern;

import android.app.Activity;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class EditTextValidator extends Activity {

    private EditText mUsername;

    private Drawable error_indicator;

    public EditTextValidator(EditText un) {

        // Setting custom drawable instead of red error indicator,
        //error_indicator = getResources().getDrawable(R.drawable.error_indicator);

        int left = 0;
        int top = 0;

        //int right = error_indicator.getIntrinsicHeight();
        //int bottom = error_indicator.getIntrinsicWidth();

        //error_indicator.setBounds(new Rect(left, top, right, bottom));

        mUsername = un;

        // Called when user type in EditText
        mUsername.addTextChangedListener(new InputValidator(mUsername));

        // Called when an action is performed on the EditText
        mUsername.setOnEditorActionListener(new EmptyTextListener(mUsername));
    }

    private class InputValidator implements TextWatcher {
        private EditText et;

        private InputValidator(EditText editText) {
            this.et = editText;
        }

        @Override
        public void afterTextChanged(Editable s) {

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                int count) {
            if (s.length() != 0) {
                switch (et.getId()) {
                case R.id.etunReg: {
                    if (!Pattern.matches("^[a-z]{1,16}$", s)) {
                        et.setError("Oops! Username must have only a-z");
                    }
                }
                    break;

                case R.id.etpwReg: {
                    if (!Pattern.matches("^[a-zA-Z]{1,16}$", s)) {
                        et.setError("Oops! Password must have only a-z and A-Z");
                    }
                }
                    break;
            case R.id.unTextField: {
                if (!Pattern.matches("^[a-z]{1,16}$", s)) {
                    et.setError("Oops! Username id empty!");
                }
            }
                break;

            case R.id.passwordTextField: {
                if (!Pattern.matches("^{}$", s)) {
                    et.setError("Oops! Password is empty");
                }
            }
                break;
            }
            }
        }
    }

    private class EmptyTextListener implements OnEditorActionListener {
        private EditText et;

        public EmptyTextListener(EditText editText) {
            this.et = editText;
        }

        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                // Called when user press Next button on the soft keyboard

                if (et.getText().toString().equals(""))
                    et.setError("Oops! empty.");
            }
            return false;
        }
    }
}