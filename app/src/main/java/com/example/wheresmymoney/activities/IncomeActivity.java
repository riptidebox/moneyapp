package com.example.wheresmymoney.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.example.wheresmymoney.R;
import com.example.wheresmymoney.model.Action;
import com.example.wheresmymoney.singleton.Global;

import java.math.BigDecimal;
import java.math.BigInteger;

public class IncomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(Global.getInstance().getRecentTheme());
        setContentView(R.layout.activity_income);
    }

    public void submitNewIncome(View view)
    {

        EditText textBox = findViewById(R.id.incomeAmountText);

        try
        {
            BigInteger result = Global.getInstance().getRecentAccount().getBalance().add(new BigDecimal(textBox.getText().toString()).multiply(new BigDecimal(Global.getInstance().getRecentAccount().getCurrency().getDivision())).toBigInteger());

            if(result.signum()<=0 || new BigDecimal(textBox.getText().toString()).signum()<=0)
            {
                throw new Exception();
            }

            Global.getInstance().getRecentAccount().setBalance(result);
            BigInteger amount = new BigDecimal(textBox.getText().toString()).multiply(new BigDecimal(Global.getInstance().getRecentAccount().getCurrency().getDivision())).toBigInteger();
            Global.getInstance().getRecentAccount().addTransaction(new Action(amount,0));
            Global.getInstance().databaseHelper.updateState();
            Global.getInstance().displayMessage("ДОХОД ДОБАВЛЕН!");
            onBackPressed();
        }
        catch (Exception e)
        {
            Global.getInstance().displayMessage("Пожалуйста, введите действительную сумму");
        }
    }
}
