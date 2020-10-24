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
        //initialise Text Input layouts and editText
        cardNumberLayout = findViewById(R.id.cardNumberLayout);
        inputCardNum = findViewById(R.id.cardNumber);

        expiryDateLayout = findViewById(R.id.expiryDateLayout);
        inputExpiryDate = findViewById(R.id.expiryDate);

        securityCodeLayout = findViewById(R.id.securityCodeLayout);
        inputCode = findViewById(R.id.securityCode);

        firstNameLayout = findViewById(R.id.firstNameLayout);
        inputFirstName = findViewById(R.id.firstName);

        lastNameLayout = findViewById(R.id.lastNameLayout);
        inputLastName = findViewById(R.id.lastName);

        submit = findViewById(R.id.submitPay);
        submit.setOnClickListener(this);

        cardNumberReader();     // manage the editText card number
        expiryDateReader();     // manage the editText Expiry Date
        securityCodeReader();   // manage the editText security code
        firstNameReader();      // manage the editText first name
        lastNameReader();       // manage the editText last name


    }


    //manage the editText card number
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
                if (cardNumber != null) {                   //change security code type according to card number!
                    cardNumCount = getCount(cardNumber);
                    if (cardNumCount == 15) {               //American Express credit card has 15 digit number
                        codeLength = 4;                     //American Express credit card has 4 digit security code
                        changeSecurityCodeType();
                    } else if (cardNumCount == 16) {        //other has 16 digit card number and 3 digit code
                        codeLength = 3;
                        changeSecurityCodeType();
                    }
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
                if (!isCorrectStr(s, TOTAL_SIZE, DIVIDER_MOD, DIVIDER_CHAR)) {
                    s.replace(0, s.length(), getCorrectString(cardNumber, DIVIDER_CHAR, DIVIDER_POS)); //adding space between every 4 digits
                }
            }
        });
    }

    //return count of card number inserted
    private int getCount(char[] num) {
        int count=0;
        for (char c : num) {
            if (Character.isDigit(c))
                count++;
        }
        return count;
    }

    //check the editable s is in correct format
    public boolean isCorrectStr(Editable s, int totalSymbols, int dividerPos, char dividerChar) {
        boolean isCorrect = s.length() <= totalSymbols;
        for (int i = 0; i < s.length(); i++) {
            if (i > 0 && (i + 1) % dividerPos == 0)
                isCorrect &= dividerChar == s.charAt(i);
            else {
                isCorrect &= Character.isDigit(s.charAt(i));
            }
        }
        return isCorrect;
    }

    //add ' ' to card number and '/' to expiry date
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
    //return card number without decorations
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

    // manage the editText card number
    private void securityCodeReader() {
        changeSecurityCodeType();
        String null_val = "";

        inputCode.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                inputCode.setHint(hasFocus ? codeType : null_val);         //change the hint on focusing the view
                securityCode = Objects.requireNonNull(inputCode.getText()).toString().toCharArray(); //update the security code on focus change
                isCodeValid = securityCode != null && codeLength == securityCode.length; //check if the security code is valid or not
            }
        });
    }
    //change the hint and maxLength of editText security code
    private void changeSecurityCodeType() {
        codeType = getString(codeLength == 3 ? R.string.cvv : R.string.cid);
        inputCode.setFilters(new InputFilter[]{new InputFilter.LengthFilter(codeLength)});
    }

    //manage the editText Exp. date
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
                    String[] date = expiryDate.split("/");          //split expiry date into month and year
                    if (date.length == 2) {
                        int m = Integer.parseInt(date[0]), y = Integer.parseInt(date[1]);
                        int currentMonth = Calendar.getInstance().get(Calendar.MONTH), currentYear = Calendar.getInstance().get(Calendar.YEAR);//get current month and year
                        if (m > 12 || m < 0) {                              //check entered month is in valid range
                            inputExpiryDate.setError("Invalid Date");
                        } else if ((m < currentMonth + 1 && y == currentYear % 100) || y < currentYear % 100) {// check card is expired or not
                            inputExpiryDate.setError("Card Expired!");
                        } else
                            isExpDateValid = true;                          //else valid the expiry date
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
                if (!isCorrectStr(s, DIGIT_COUNT, DIVIDER_MOD, DIVIDER_CHAR)) {
                    s.replace(0, s.length(), getCorrectString(getCardNum(s, DIGIT_COUNT), DIVIDER_CHAR, DIVIDER_POS));//add '/' to the expiry date
                }
            }
        });
    }


    //manage edittext first name
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
                if (!(isNameValid = isValid(s.toString())))     //check first name is valid or not
                    inputFirstName.setError("First Name can only contains alphabets and spaces");
                firstName = s.toString();
            }
        });
    }

    //manage the editText last name
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
                if (!(isNameValid = isValid(s.toString())))     //check last name is in valid format or not
                    inputLastName.setError("First Name can only contains alphabets and spaces");
                lastName = s.toString();
            }
        });
    }

    //che4ck the first and last name contains character other tha alphabets and spaces
    private boolean isValid(String s) {
        boolean valid = true;
        for (int i = 0; i < s.length(); i++) {
            char current = s.charAt(i);
            if (!Character.isAlphabetic(current) && current != ' ')     //check is current is an alphabet or a space
                valid = false;
        }
        return valid;
    }

    //submit button on click listener
    @Override
    public void onClick(View v) {

        //check card number is in valid format or not
        if (cardNumber == null||cardNumCount==0)                        //check card number is empty or not
            cardNumberLayout.setError("can't be Empty");
        else if (!luhnTest(cardNumber))                                 //check card number pass luhn algorithm
            cardNumberLayout.setError("Invalid Credit Card Number");
        else
            isCardValid = true;                                         //else save as valid

        //validate expiry date
        if (expiryDate == null||expiryDate.length()==0)                 //check expiry date is empty is not and in the valid format or not
            expiryDateLayout.setError("can't be Empty");

        if (firstName == null||firstName.length()==0)                   //check first name is empty or not and is in the valid format
            firstNameLayout.setError("can't be Empty");
        if (lastName == null||lastName.length()==0)                     //check last name is empty or not and is in the valid format
            lastNameLayout.setError("can't be Empty");

        if (securityCode == null)                                       //check security code is empty or not
            securityCodeLayout.setError("can't be Empty");
        else if (!isCodeValid)                                          //check security coe is in valid format or not
            securityCodeLayout.setError("Please Enter " + codeLength + " digits code");

        if (isCardValid && isCodeValid && isNameValid && isExpDateValid)      //check all values and valid and if valid toast a message
            Toast.makeText(this, "Payment Successful!", Toast.LENGTH_SHORT).show();
    }

    //luhn algorithm
    private boolean luhnTest(char[] num) {
        long sum = 0, d, m = 1;
        for (int i = cardNumCount; i >= 0; i--) {
            d = m * Integer.parseInt(String.valueOf(num[i]));
            if (d>9){
            d-=9;
            }
            sum = sum + d;
            m = m == 2 ? 1 : 2;
        }
        return sum % 10 == 0;
    }
}
