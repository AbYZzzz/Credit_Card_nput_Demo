package com.aj.android.CreditCardInputDemo;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

import java.util.Arrays;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private TextInputEditText inputCode;
    private String firstName, lastName, expiryDate, codeType;
    private char[] cardNumber,securityCode;
    private int codeLength = 3,cardNumCount;
    private boolean isNameValid=false,isCodeValid=false,isExpDateValid=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cardNumberReader();
        expiryDateReader();
        firstNameReader();
        lastNameReader();
        securityCodeReader();
        submitPayButton();


    }

    private void submitPayButton() {
        Button submit = findViewById(R.id.submitPay);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (luhnTest(cardNumber))
                    Toast.makeText(MainActivity.this, "Payment Successful", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean luhnTest(char[] num) {
        long sum = 0, d, m = 1;
        for (int i = num.length - 1; i >= 0; i--) {
            d = m * Integer.parseInt(String.valueOf(num[i]));
            sum = sum + d;
            m = m == 2 ? 1 : 2;
        }
        return sum % 10 == 0;
    }

    private void firstNameReader() {
        TextInputEditText inputFirstName = findViewById(R.id.firstName);
        inputFirstName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!isValid(s.toString()))
                    inputFirstName.setError("First Name can only contains alphabets and spaces");
                firstName = s.toString();
            }
        });
    }

    private void lastNameReader() {
        TextInputEditText inputLastName = findViewById(R.id.lastName);
        inputLastName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!isValid(s.toString()))
                    inputLastName.setError("First Name can only contains alphabets and spaces");
                lastName = s.toString();
            }
        });
    }

    private boolean isValid(String s) {
        boolean valid = true;
        for (int i = 0; i < s.length(); i++) {
            char current = s.charAt(i);
            if (!Character.isAlphabetic(current) && current != ' ')
                valid = false;
        }
        return valid;
    }

    private void securityCodeReader() {
        inputCode = findViewById(R.id.securityCode);
        changeSecurityCodeType();
        String null_val = "";

        inputCode.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                inputCode.setHint(hasFocus ? codeType : null_val);
                securityCode = Objects.requireNonNull(inputCode.getText()).toString().toCharArray();
            }
        });
    }

    private void changeSecurityCodeType() {
        inputCode.setFilters(new InputFilter[]{new InputFilter.LengthFilter(codeLength)});
        codeType = getString(codeLength == 3 ? R.string.cvv : R.string.cid);
    }

    private void cardNumberReader() {
        final int DIGIT_COUNT = 16;
        final int TOTAL_SIZE = 19;
        final int DIVIDER_POS = 4;
        final int DIVIDER_MOD = DIVIDER_POS + 1;
        final char DIVIDER_CHAR = ' ';
        TextInputEditText inputCardNum = findViewById(R.id.cardNumber);
        inputCardNum.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                inputCardNum.setHint(hasFocus ? R.string.card_hint : R.string.null_val);
                if (cardNumber!=null)
                if (cardNumber.length==15) {
                    codeLength = 4;
                    changeSecurityCodeType();
                }else if (cardNumber.length==16) {
                    codeLength = 3;
                    changeSecurityCodeType();
                }
            }
        });
        inputCardNum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                cardNumber = getCardNum(s, DIGIT_COUNT);
                Log.d(TAG, "afterTextChanged: "+ cardNumber.length);
                if (!isCorrectStr(s, TOTAL_SIZE, DIVIDER_MOD, DIVIDER_CHAR)) {
                    s.replace(0, s.length(), getCorrectString(cardNumber, DIVIDER_CHAR, DIVIDER_POS));
                }
            }
        });
    }

    private void expiryDateReader() {
        final int DIGIT_COUNT = 5;
        final int DIVIDER_POS = 2;
        final int DIVIDER_MOD = DIVIDER_POS + 1;
        final char DIVIDER_CHAR = '/';
        TextInputEditText inputExpiryDate = findViewById(R.id.expiryDate);
        inputExpiryDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                inputExpiryDate.setHint(getString(hasFocus ? R.string.expiry_date : R.string.expiry));
            }
        });
        inputExpiryDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                char[] expDate = getCardNum(s, DIGIT_COUNT);
                expiryDate = Arrays.toString(expDate);
                if (!isCorrectStr(s, DIGIT_COUNT, DIVIDER_MOD, DIVIDER_CHAR)) {
                    s.replace(0, s.length(), getCorrectString(expDate, DIVIDER_CHAR, DIVIDER_POS));
                }
            }
        });
    }

    public boolean isCorrectStr(Editable s, int totalSymbols, int dividerPos, char dividerChar) {
        boolean isCorrect = s.length() <= totalSymbols;
        for (int i = 0; i < s.length(); i++) {
            if (i > 0 && (i + 1) % dividerPos == 0)
                isCorrect &= dividerChar == s.charAt(i);
            else
                isCorrect &= Character.isDigit(s.charAt(i));
        }
        return isCorrect;
    }

    public String getCorrectString(char[] digits, char dividerChar, int dividerPos) {
        final StringBuilder formatter = new StringBuilder();
        for (int i = 0; i < digits.length; i++) {
            if (digits[i] != 0) {
                formatter.append(digits[i]);
                if (i > 0 && i < digits.length - 1 && (i + 1) % dividerPos == 0)
                    formatter.append(dividerChar);
            }
        }
        return formatter.toString();
    }

    public char[] getCardNum(Editable s, int digitCount) {
        char[] digits = new char[s.length()];
        for (int i = 0, index = 0; i < s.length() && index < digitCount; i++) {
            char current = s.charAt(i);
            if (Character.isDigit(current)) {
                digits[index] = current;
                index++;
            }
        }
        return digits;
    }
}