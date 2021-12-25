package com.example.wheresmymoney.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.example.wheresmymoney.R;
import com.example.wheresmymoney.singleton.Global;

public class ActionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setTheme(Global.getInstance().getRecentTheme());
        setContentView(R.layout.activity_action);

        String typeText;
        TextView typeTextView = findViewById(R.id.textView7);
        switch(Global.getInstance().getRecentAction().getType())
        {
            case 0:
                typeText = "доход";
                break;
            case 1:
                typeText = "расход";
                break;
            default:
                typeText = "Установить в";
                break;
        }
        typeTextView.setText(typeText);

        TextView amountTextView = findViewById(R.id.textView8);
        String text = Global.getInstance().getRecentAccount().getCurrency().toNaturalLanguage(Global.getInstance().getRecentAction().getAmount());
        amountTextView.setText(text);

        TextView dateTextView = findViewById(R.id.textView9);
        dateTextView.setText(Global.getInstance().getRecentAction().getDateString());
    }
}
