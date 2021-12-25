package com.example.wheresmymoney.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wheresmymoney.R;
import com.example.wheresmymoney.model.Account;
import com.example.wheresmymoney.model.Action;
import com.example.wheresmymoney.singleton.Global;

import java.util.ArrayList;

public class AccountActivity extends AppCompatActivity {
    private ListView transactionsList;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setTheme(Global.getInstance().getRecentTheme());
        setContentView(R.layout.activity_account);
    }

    protected void onResume()
    {
        super.onResume();

        Global.getInstance().databaseHelper.readAccounts();
        Global.getInstance().databaseHelper.readActions();

        for(Account account : Global.getInstance().getAccountsList())
        {
            if(Global.getInstance().getRecentAccount().getIdA() == account.getIdA())
            {
                Global.getInstance().setRecentAccount(account);
            }
        }

        TextView nameTextView = findViewById(R.id.textView9);
        nameTextView.setText(Global.getInstance().getRecentAccount().getName());

        TextView balanceTextView = findViewById(R.id.textView10);
        String text = Global.getInstance().getRecentAccount().getCurrency().toNaturalLanguage(Global.getInstance().getRecentAccount().getBalance());
        balanceTextView.setText(text);

        ArrayList<String> transactionsListData = new ArrayList<>();

        for(Action transaction : Global.getInstance().getRecentAccount().getActionHistory())
        {
            transactionsListData.add(transaction.toString());
        }

        transactionsList = findViewById(R.id.listViewTransactions);

        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, transactionsListData);
        transactionsList.setAdapter(adapter2);
        final Intent intent = new Intent(this, ActionActivity.class);
        transactionsList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Global.getInstance().setRecentAction(Global.getInstance().getRecentAccount().getActionHistory().get(position));
                startActivity(intent);
            }
        });

        transactionsList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
        {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id)
            {
                transactionsList.setOnItemClickListener(null);
                AlertDialog.Builder builder = new AlertDialog.Builder(AccountActivity.this, R.style.AlertDialog);

                builder.setTitle("Удалить действие");
                builder.setMessage("Ты уверен?");

                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing but close the dialog
                        Global.getInstance().getRecentAccount().removeTransaction(Global.getInstance().getRecentAccount().getActionHistory().get(position));
                        Global.getInstance().displayMessage("Удаление действия");
                        dialog.dismiss();
                        transactionsList.setOnItemClickListener(new AdapterView.OnItemClickListener()
                        {
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                            {
                                Global.getInstance().setRecentAction(Global.getInstance().getRecentAccount().getActionHistory().get(position));
                                startActivity(intent);
                            }
                        });
                        dialog.dismiss();
                        onResume();
                    }
                });

                builder.setNegativeButton("НЕТ", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        // Do nothing
                        Global.getInstance().displayMessage("Удаление отменено");
                        dialog.dismiss();
                        transactionsList.setOnItemClickListener(new AdapterView.OnItemClickListener()
                        {
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                            {
                                Global.getInstance().setRecentAction(Global.getInstance().getRecentAccount().getActionHistory().get(position));
                                startActivity(intent);
                            }
                        });
                        dialog.dismiss();
                        onResume();
                    }
                });

                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        //If the error flag was set to true then show the dialog again
                        transactionsList.setOnItemClickListener(new AdapterView.OnItemClickListener()
                        {
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                            {
                                final Intent intent = new Intent(getApplicationContext(), ActionActivity.class);
                                Global.getInstance().setRecentAction(Global.getInstance().getRecentAccount().getActionHistory().get(position));
                                startActivity(intent);
                            }
                        });
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();
                return false;
            }
        });
    }

    public void newIncome(View view)
    {
        final Intent intent = new Intent(this, IncomeActivity.class);
        startActivity(intent);
    }

    public void newExpense(View view)
    {
        final Intent intent = new Intent(this, ExpenseActivity.class);
        startActivity(intent);
    }

    public void editBalance(View view)
    {
        final Intent intent = new Intent(this, EditBalanceActivity.class);
        startActivity(intent);
    }

    public void updateAccount(View view)
    {
        Toast.makeText(getApplicationContext(),"Обновление профиля", Toast.LENGTH_SHORT).show();
        try
        {
            Global.getInstance().setRecentAPI(Global.getInstance().getApisList().get(1));
            Global.getInstance().getRecentAPI().makeRequest(Integer.toString(Global.getInstance().getRecentAccount().getIdA()), this);
        }
        catch (Exception e)
        {
            Toast.makeText(getApplicationContext(),"Эта функция пока недоступна", Toast.LENGTH_SHORT).show();
        }
    }
}
