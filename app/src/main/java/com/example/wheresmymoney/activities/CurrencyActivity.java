package com.example.wheresmymoney.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.example.wheresmymoney.R;
import com.example.wheresmymoney.singleton.Global;

public class CurrencyActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setTheme(Global.getInstance().getRecentTheme());
        setContentView(R.layout.activity_currency);
    }

    protected void onResume()
    {
        super.onResume();

        Global.getInstance().databaseHelper.readCurrencies();

        TextView nameTextView = findViewById(R.id.textView23);
        TextView tagTextView = findViewById(R.id.textView19);
        TextView linkTextView = findViewById(R.id.textView20);
        TextView linkRatioTextView = findViewById(R.id.textView21);
        TextView pointPosTextView = findViewById(R.id.textView33);

        nameTextView.setText(Global.getInstance().getRecentCurrency().getName());
        tagTextView.setText("Тэг: "+Global.getInstance().getRecentCurrency().getTag());
        linkTextView.setText("Ссылка на тэг: "+Global.getInstance().getRecentCurrency().getLink().getTag());
        linkRatioTextView.setText(Global.getInstance().getRecentCurrency().getLink().getName()+" расположение: "+Global.getInstance().getRecentCurrency().getLinkRatio());
        pointPosTextView.setText("Позиция точки: " + (Integer.valueOf(Global.getInstance().getRecentCurrency().getPointPosition())).toString());
    }

    public void editCurrency(View view)
    {
        final Intent intent = new Intent(this, EditCurrencyActivity.class);
        startActivity(intent);
    }

    public void setAsMainCurrency(View view)
    {
        Global.getInstance().setMainCurrency(Global.getInstance().getRecentCurrency());
        Global.getInstance().savePreferences();
        Global.getInstance().displayMessage("основная валюта:\n"+Global.getInstance().getMainCurrency().getTag());
    }
}
