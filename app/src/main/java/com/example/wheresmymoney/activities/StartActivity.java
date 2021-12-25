package com.example.wheresmymoney.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.example.wheresmymoney.R;
import com.example.wheresmymoney.singleton.Global;

public class  StartActivity extends AppCompatActivity
{
    @Override
    public void onBackPressed()
    {
        finishAffinity();
        System.exit(0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setTheme(Global.getInstance().getRecentTheme());
        setContentView(R.layout.activity_start);
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        if(Global.getInstance().isThemeChanged())
        {
            Global.getInstance().setThemeChanged(false);
            recreate();
        }

        Global.getInstance().authenticated = false;
        Global.getInstance().databaseHelper.readActions();
        TextView balanceTextView = findViewById(R.id.textView2);

        String balanceText;

        balanceText = Global.getInstance().getMainCurrency().toNaturalLanguage(Global.getBalance());

        balanceTextView.setText(balanceText);
    }

    public void viewAccounts(View view)
    {
        final Intent intent = new Intent(this, AccountsActivity.class);
        startActivity(intent);
    }

    public void viewCurrencies(View view)
    {
        final Intent intent = new Intent(this, CurrenciesActivity.class);
        startActivity(intent);
    }

    public void viewSettings(View view)
    {
        final Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }
}