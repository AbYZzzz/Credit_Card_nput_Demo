package com.aj.android.CreditCardInputDemo;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Calendar;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";
    private TextInputEditText inputCardNum;
    private TextInputEditText inputExpiryDate;
    private TextInputEditText inputCode;
    private TextInputEditText inputFirstName;
    private TextInputEditText inputLastName;
    private TextInputLayout cardNumberLayout;
    private TextInputLayout expiryDateLayout;
    private TextInputLayout securityCodeLayout;
    private TextInputLayout firstNameLayout;
    private TextInputLayout lastNameLayout;

    private Button submit;
    private String firstName, lastName, expiryDate, codeType;
    private char[] cardNumber, securityCode;
    private int codeLength = 3, cardNumCount;
    private boolean isNameValid = false, isCodeValid = false, isExpDateValid = false, isCardValid = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cardNumberLayout=findViewById(R.id.cardNumberLayout);
        inputCardNum = findViewById(R.id.cardNumber);

        expiryDateLayout = findViewById(R.id.expiryDateLayout);
        inputExpiryDate = findViewById(R.id.expiryDate);

        securityCodeLayout=findViewById(R.id.securityCodeLayout);
        inputCode = findViewById(R.id.securityCode);

        firstNameLayout=findViewById(R.id.firstNameLayout);
        inputFirstName = findViewById(R.id.firstName);

        lastNameLayout=findViewById(R.id.lastNameLayout);
        inputLastName = findViewById(R.id.lastName);

        submit = findViewById(R.id.submitPay);
        submit.setOnClickListener(this);

        cardNumberReader();
        expiryDateReader();
        firstNameReader();
        lastNameReader();
        securityCodeReader();


    }

    private void securityCodeReader() {
        changeSecurityCodeType();
        String null_val = "";

        inputCode.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                inputCode.setHint(hasFocus ? codeType : null_val);
                securityCode = Objects.requireNonNull(inputCode.getText()).toString().toCharArray();
                isCodeValid = securityCode != null && codeLength == securityCode.length;
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

        inputCardNum.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                inputCardNum.setHint(hasFocus ? R.string.card_hint : R.string.null_val);
                if (cardNumber != null)
                    if (cardNumber.length == 15) {
                        codeLength = 4;
                        changeSecurityCodeType();
                    } else if (cardNumber.length == 16) {
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
                if (isCorrectStr(s, TOTAL_SIZE, DIVIDER_MOD, DIVIDER_CHAR) != -1) {
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
        inputExpiryDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                expiryDateLayout.setHint(getString(hasFocus ? R.string.expiry_date : R.string.expiry));
                if (!hasFocus && expiryDate != null) {
                    String[] date = expiryDate.split("/");
                    if (date.length==2) {
                        int m = Integer.parseInt(date[0]), y = Integer.parseInt(date[1]);
                        int currentMonth = Calendar.getInstance().get(Calendar.MONTH), currentYear = Calendar.getInstance().get(Calendar.YEAR);
                        if (m > 12 || m < 0) {
                            expiryDateLayout.setError("Invalid Date");
                        } else if ((m < currentMonth + 1 && y == currentYear % 100) || y < currentYear % 100) {
                            expiryDateLayout.setError("Card Expired!");
                        } else
                            isExpDateValid = true;
                    }
                }
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
                expiryDate = s.toString();
                if ((cardNumCount = isCorrectStr(s, DIGIT_COUNT, DIVIDER_MOD, DIVIDER_CHAR)) != -1) {
                    s.replace(0, s.length(), getCorrectString(getCardNum(s, DIGIT_COUNT), DIVIDER_CHAR, DIVIDER_POS));
                }
            }
        });
    }

    public int isCorrectStr(Editable s, int totalSymbols, int dividerPos, char dividerChar) {
        boolean isCorrect = s.length() <= totalSymbols;
        int count = 0;
        for (int i = 0; i < s.length(); i++) {
            if (i > 0 && (i + 1) % dividerPos == 0)
                isCorrect &= dividerChar == s.charAt(i);
            else {
                count++;
                isCorrect &= Character.isDigit(s.charAt(i));
            }
        }
        return isCorrect ? -1 : count;
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

    private void firstNameReader() {
        inputFirstName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!(isNameValid = isValid(s.toString())))
                    firstNameLayout.setError("First Name can only contains alphabets and spaces");
                firstName = s.toString();
            }
        });
    }

    private void lastNameReader() {

        inputLastName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!(isNameValid = isValid(s.toString())))
                    lastNameLayout.setError("First Name can only contains alphabets and spaces");
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

    @Override
    public void onClick(View v) {
        if (cardNumber==null)
            cardNumberLayout.setError("can't be Empty");
        else if (!luhnTest(cardNumber))
            cardNumberLayout.setError("Invalid Credit Card Number");
        else
            isCardValid=true;

        if (expiryDate==null)
            expiryDateLayout.setError("can't be Empty");

        if (firstName==null)
            firstNameLayout.setError("can't be Empty");
        if (lastName==null)
            lastNameLayout.setError("can't be Empty");

        if (securityCode==null)
            securityCodeLayout.setError("can't be Empty");
        else if (!isCodeValid)
            securityCodeLayout.setError("Please Enter " + codeLength + " digits code");

        if (isCardValid&&isCodeValid&&isNameValid&&isExpDateValid)
            Toast.makeText(this, "Payment Successful!", Toast.LENGTH_SHORT).show();
    }

    private boolean luhnTest(char[] num) {
        long sum = 0, d, m = 1;
        for (int i = cardNumCount; i >= 0; i--) {
            d = m * Integer.parseInt(String.valueOf(num[i]));
            sum = sum + d;
            m = m == 2 ? 1 : 2;
        }
        return sum % 10 == 0;
    }
}