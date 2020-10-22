package com.aj.android.CreditCardInputDemo;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class MainActivity extends AppCompatActivity {
    private TextInputEditText inputFirstName, inputLastName, inputCode, inputExpiryDate, inputCardNum;
    private TextInputLayout expiryDateLayout, securityCodeLayout;
    private Button submit;
    private String name, expiryDate, codeType;
    private long cardNumber;
    private int securityCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        inputCardNum = findViewById(R.id.cardNumber);
        inputExpiryDate = findViewById(R.id.expiryDate);
        inputCode = findViewById(R.id.securityCode);
        inputFirstName = findViewById(R.id.firstName);
        inputLastName = findViewById(R.id.lastName);
        expiryDateLayout = findViewById(R.id.expiryDateLayout);
        securityCodeLayout = findViewById(R.id.securityCodeLayout);
        codeType = getString(R.string.security_code);
        submit = findViewById(R.id.submitPay);

        inputExpiryDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                expiryDateLayout.setHint(getString(hasFocus ? R.string.expiry_date : R.string.expiry));
            }
        });
        inputCode.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                inputCode.setHint(hasFocus ? R.string.cvv : R.string.security_code);
            }
        });
        securityCodeLayout.setCounterEnabled(true);
        securityCodeLayout.setCounterMaxLength(codeType.equals(getString(R.string.cvv)) ? 3 : 4);
        inputCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
}