package com.example.wheresmymoney.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.example.wheresmymoney.R;
import com.example.wheresmymoney.model.Currency;
import com.example.wheresmymoney.singleton.Global;

import java.math.BigDecimal;
import java.util.ArrayList;

public class AddCurrencyActivity extends AppCompatActivity {
    private Currency newLink = Global.getInstance().getMainCurrency();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(Global.getInstance().getRecentTheme());
        setContentView(R.layout.activity_add_currency);
    }

    protected void onResume()
    {
        super.onResume();
        ListView list;
        ArrayList<String> currenciesListData = new ArrayList<>();
        for (Currency currency : Global.getInstance().getCurrenciesList()) {
            currenciesListData.add(currency.getTag());
        }
        list = findViewById(R.id.currenciesListView);

        ArrayAdapter<String> adapter3 = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, currenciesListData);
        list.setAdapter(adapter3);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                newLink = Global.getInstance().getCurrenciesList().get(position);
                Global.getInstance().displayMessage(newLink.getTag());
            }
        });
    }

    public void addCurrency(View view)
    {
        Global.getInstance().displayMessage("Добавление валюты!");
        EditText nameEdit = findViewById(R.id.editText2);
        EditText tagEdit = findViewById(R.id.editText3);
        EditText ratioEdit = findViewById(R.id.editText4);
        EditText pointEdit = findViewById(R.id.editText5);

        String name = nameEdit.getText().toString();
        String tag = tagEdit.getText().toString();

        BigDecimal linkRatio;
        try
        {
             linkRatio = new BigDecimal(ratioEdit.getText().toString());
        }
        catch (Exception e)
        {
            linkRatio = new BigDecimal(1);
        }

        try
        {
            int pointPosition = Integer.valueOf(pointEdit.getText().toString());

            if(pointPosition>=0)
            {
                Currency newCurrency = new Currency(name, tag, linkRatio, pointPosition, newLink);

                Global.getInstance().databaseHelper.addCurrency(newCurrency);
                Global.getInstance().databaseHelper.readCurrencies();

                Global.getInstance().displayMessage("успешно добавлена валюта");
                onBackPressed();
            }
            else
            {
                Global.getInstance().displayMessage("выберите число, большее или равное 0");
            }
        }
        catch (Exception e)
        {
            Global.getInstance().displayMessage("Пожалуйста, снова введите положение точки, произошла ошибка");
        }
    }
}
