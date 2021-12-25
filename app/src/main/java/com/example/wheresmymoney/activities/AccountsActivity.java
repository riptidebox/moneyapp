package com.example.wheresmymoney.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.example.wheresmymoney.R;
import com.example.wheresmymoney.model.Account;
import com.example.wheresmymoney.singleton.Global;

import java.util.ArrayList;

public class AccountsActivity extends AppCompatActivity
{
    private ListView accountsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(Global.getInstance().getRecentTheme());
        setContentView(R.layout.activity_accounts);

    }

    protected void onResume()
    {
        super.onResume();
        Global.getInstance().databaseHelper.readAccounts();

        Button button1 = findViewById(R.id.addAccountButton);
        final Intent intent1 = new Intent(this, AddAccountActivity.class);
        button1.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
                startActivity(intent1);
            }
        });

        ArrayList<String> accountsListData = new ArrayList<>();
        for(Account account: Global.getInstance().getAccountsList())
        {
            String text = account.getCurrency().toNaturalLanguage(account.getBalance());
            accountsListData.add(account.getName() + "      " + text);
        }

        accountsList = findViewById(R.id.listView);

        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, accountsListData);
        accountsList.setAdapter(adapter2);
        final Intent intent2 = new Intent(this, AccountActivity.class);
        accountsList.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                { Global.getInstance().setRecentAccount(Global.getInstance().getAccountsList().get(position));
                    startActivity(intent2);
                }
            });

        accountsList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
        {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id)
            {
                accountsList.setOnItemClickListener(null);
                final AlertDialog.Builder builder = new AlertDialog.Builder(AccountsActivity.this, R.style.AlertDialog);

                builder.setTitle("Удалить "+Global.getInstance().getAccountsList().get(position).getName());
                builder.setMessage("Вы уверенны?");

                builder.setPositiveButton("ДА", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {// Do nothing but close the dialog
                            Global.getInstance().databaseHelper.deleteSingleAccount(Global.getInstance().getAccountsList().get(position));
                            Global.getInstance().displayMessage("Удаление");
                            dialog.dismiss();
                            onResume();
                        }
                    });

                builder.setNegativeButton("НЕТ", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Do nothing
                            Global.getInstance().displayMessage("удаление отменено");
                            dialog.dismiss();
                            onResume();
                        }
                    });

                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                                @Override
                                public void onDismiss(DialogInterface dialog) {//If the error flag was set to true then show the dialog again
                                    accountsList.setOnItemClickListener(new AdapterView.OnItemClickListener()
                                    {
                                        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                                        {
                                            Global.getInstance().setRecentAccount(Global.getInstance().getAccountsList().get(position));
                                            startActivity(intent2);
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
}
