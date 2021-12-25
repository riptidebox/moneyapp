package com.example.wheresmymoney.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.wheresmymoney.model.Account;
import com.example.wheresmymoney.model.Action;
import com.example.wheresmymoney.model.Currency;
import com.example.wheresmymoney.singleton.Global;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

public class DatabaseHelper extends SQLiteOpenHelper
{
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "WMMDatabase";

    private static final String CURRENCIES_TABLE_NAME = "Currencies";
    private static final String CURRENCIES_TABLE_COL1 = "IdC";
    private static final String CURRENCIES_TABLE_COL2 = "Name";
    private static final String CURRENCIES_TABLE_COL3 = "Tag";
    private static final String CURRENCIES_TABLE_COL4 = "LinkRatio";
    private static final String CURRENCIES_TABLE_COL5 = "PointPosition";
    private static final String CURRENCIES_TABLE_COL6 = "IdLink";

    private static final String ACCOUNTS_TABLE_NAME = "Accounts";
    private static final String ACCOUNTS_TABLE_COL1 = "IdA";
    private static final String ACCOUNTS_TABLE_COL2 = "Name";
    private static final String ACCOUNTS_TABLE_COL3 = "Balance";
    private static final String ACCOUNTS_TABLE_COL4 = "IdC";

    private static final String ACTIONS_TABLE_NAME = "Actions";
    private static final String ACTIONS_TABLE_COL1 = "IdAc";
    private static final String ACTIONS_TABLE_COL2 = "Amount";
    private static final String ACTIONS_TABLE_COL3 = "Type";
    private static final String ACTIONS_TABLE_COL4 = "Date";
    private static final String ACTIONS_TABLE_COL5 = "IdA";

    private static final String CREATE_CURRENCIES_TABLE = "create table if not exists "
            + CURRENCIES_TABLE_NAME
            + " ( "+CURRENCIES_TABLE_COL1+" integer primary key autoincrement, "+CURRENCIES_TABLE_COL2+"  TEXT NOT NULL, "+CURRENCIES_TABLE_COL3+" TEXT NOT NULL,"+CURRENCIES_TABLE_COL4+" STRING,"+CURRENCIES_TABLE_COL5+" INTEGER,"+CURRENCIES_TABLE_COL6+" INTEGER, CONSTRAINT tag_unique UNIQUE ("+CURRENCIES_TABLE_COL3+"));";

    private static final String CREATE_ACCOUNTS_TABLE = "create table if not exists "
            + ACCOUNTS_TABLE_NAME
            + " ( "+ACCOUNTS_TABLE_COL1+" integer primary key autoincrement, "+ACCOUNTS_TABLE_COL2+"  TEXT NOT NULL, "+ACCOUNTS_TABLE_COL3+" STRING,"+ACCOUNTS_TABLE_COL4+" INTEGER, CONSTRAINT name_unique UNIQUE ("+ACCOUNTS_TABLE_COL2+"));";

    private static final String CREATE_ACTIONS_TABLE = "create table if not exists "
            + ACTIONS_TABLE_NAME
            + " ( "+ACTIONS_TABLE_COL1+" integer primary key autoincrement, "+ACTIONS_TABLE_COL2+" STRING, "+ACTIONS_TABLE_COL3+" INTEGER, "+ACTIONS_TABLE_COL4+" TEXT NOT NULL, "+ACTIONS_TABLE_COL5+" INTEGER);";

    public DatabaseHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(CREATE_CURRENCIES_TABLE);
        db.execSQL(CREATE_ACCOUNTS_TABLE);
        db.execSQL(CREATE_ACTIONS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS " + CURRENCIES_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ACCOUNTS_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ACTIONS_TABLE_NAME);
        onCreate(db);
    }

    public void updateState()
    {
        readCurrencies();
        readAccounts();
        readActions();
    }

    public void addAction(Action newAction)
    {
        ContentValues values = new ContentValues();
        values.put(ACTIONS_TABLE_COL2, newAction.getAmount().toString());
        values.put(ACTIONS_TABLE_COL3, newAction.getType());
        values.put(ACTIONS_TABLE_COL4, newAction.getDate());
        values.put(ACTIONS_TABLE_COL5, newAction.getIdA());

        SQLiteDatabase db = this.getWritableDatabase();

        db.insert(ACTIONS_TABLE_NAME, null, values);
    }

    public void deleteSingleAction(Action action)
    {
        deleteAction(action);
        readAccounts();
    }

    private void deleteAction(Action action)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM "+ACTIONS_TABLE_NAME +" WHERE IdAc="+action.getIdAc());
    }

    public void readActions()
    {
        Global.getInstance().getActionsList().clear();

        SQLiteDatabase db = this.getWritableDatabase();

        String query = "Select * FROM " + ACTIONS_TABLE_NAME;

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst())
        {
            cursor.moveToFirst();
            while(!cursor.isAfterLast())
            {
                Action action = new Action();

                action.setIdAc(cursor.getInt(0));
                action.setAmount(new BigDecimal(cursor.getString(1)).toBigInteger());
                action.setType(cursor.getInt(2));
                action.setDate(cursor.getString(3));

                int IdA = cursor.getInt(4);
                Account actionsAccount = null;
                for(Account account : Global.getInstance().getAccountsList())
                {
                    if(account.getIdA()==IdA)
                    {
                        actionsAccount = account;
                    }
                }

                if(actionsAccount!=null)
                {
                    action.setAccount(actionsAccount);
                }

                Global.getInstance().getActionsList().add(action);
                cursor.moveToNext();
            }
            cursor.close();
        }

        for(Account account : Global.getInstance().getAccountsList())
        {
            account.getActionHistory().clear();
            account.setBalance(new BigInteger("0"));
            for(Action action : Global.getInstance().getActionsList())
            {
                if(account.getIdA()==action.getIdA())
                {
                    account.getActionHistory().add(action);

                    if(action.getType()==0)
                    {
                        account.setBalance(account.getBalance().add(action.getAmount()));
                    }
                    else if(action.getType()==1)
                    {
                        account.setBalance(account.getBalance().subtract(action.getAmount()));
                    }
                    else
                    {
                        account.setBalance(action.getAmount());
                    }
                }
            }
        }
    }

    public void addAccount(Account newAccount)
    {
        ContentValues values = new ContentValues();
        values.put(ACCOUNTS_TABLE_COL2, newAccount.getName());
        values.put(ACCOUNTS_TABLE_COL3, newAccount.getBalance().toString());
        values.put(ACCOUNTS_TABLE_COL4, newAccount.getCurrency().getIdC());

        SQLiteDatabase db = this.getWritableDatabase();

        db.insert(ACCOUNTS_TABLE_NAME, null, values);
    }

    public void deleteSingleAccount(Account account)
    {
        deleteAccount(account);
        readAccounts();
    }

    private void deleteAccount(Account account)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("DELETE FROM "+ACCOUNTS_TABLE_NAME +" WHERE IdA="+account.getIdA());

        for(Action action : account.getActionHistory())
        {
            deleteAction(action);
        }
    }

    private void editAccountsActions(Currency currency, boolean increase, BigInteger increaseAmount)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        if(increase)
        {
            for(Account account : Global.getInstance().getAccountsList())
            {
                if(account.getCurrency().equals(currency))
                {
                    for(Action action : account.getActionHistory())
                    {
                        ContentValues values = new ContentValues();
                        values.put(ACTIONS_TABLE_COL2, action.getAmount().multiply(increaseAmount).toString());
                        values.put(ACTIONS_TABLE_COL3, action.getType());
                        values.put(ACTIONS_TABLE_COL4, action.getDate());
                        values.put(ACTIONS_TABLE_COL5, action.getIdA());

                        db.update(ACTIONS_TABLE_NAME, values, "IdAc="+action.getIdAc(), null);
                    }
                }
            }
        }
        else
        {
            for(Account account : Global.getInstance().getAccountsList())
            {
                if(account.getCurrency().equals(currency))
                {
                    for(Action action : account.getActionHistory())
                    {
                        ContentValues values = new ContentValues();
                        values.put(ACTIONS_TABLE_COL2, new BigDecimal(action.getAmount()).divide(new BigDecimal(increaseAmount), 20, RoundingMode.HALF_UP).toBigInteger().toString());
                        values.put(ACTIONS_TABLE_COL3, action.getType());
                        values.put(ACTIONS_TABLE_COL4, action.getDate());
                        values.put(ACTIONS_TABLE_COL5, action.getIdA());

                        db.update(ACTIONS_TABLE_NAME, values, "IdAc="+action.getIdAc(), null);
                    }
                }
            }
        }
    }

    public void readAccounts()
    {
        Global.getInstance().getAccountsList().clear();

        SQLiteDatabase db = this.getWritableDatabase();

        String query = "Select * FROM " + ACCOUNTS_TABLE_NAME;

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst())
        {
            cursor.moveToFirst();
            while(!cursor.isAfterLast())
            {
                Account account = new Account();

                account.setIdA(cursor.getInt(0));
                account.setName(cursor.getString(1));
                account.setBalance(new BigInteger(cursor.getString(2)));

                int IdC = cursor.getInt(3);
                Currency accountCurrency = null;
                for(Currency currency : Global.getInstance().getCurrenciesList())
                {
                    if(currency.getIdC()==IdC)
                    {
                        accountCurrency = currency;
                    }
                }

                if(accountCurrency!=null)
                {
                    account.setCurrency(accountCurrency);
                }
                else
                {
                    account.setCurrency(Global.getInstance().getMainCurrency());
                }

                Global.getInstance().getAccountsList().add(account);
                cursor.moveToNext();
            }
            cursor.close();
        }

        readActions();
    }

    public void addCurrency(Currency newCurrency)
    {
        ContentValues values = new ContentValues();
        values.put(CURRENCIES_TABLE_COL2, newCurrency.getName());
        values.put(CURRENCIES_TABLE_COL3, newCurrency.getTag());
        values.put(CURRENCIES_TABLE_COL4, newCurrency.getLinkRatio().toString());
        values.put(CURRENCIES_TABLE_COL5, newCurrency.getPointPosition());
        values.put(CURRENCIES_TABLE_COL6, newCurrency.getLink().getIdC());

        SQLiteDatabase db = this.getWritableDatabase();

        db.insert(CURRENCIES_TABLE_NAME, null, values);
    }

    public void editCurrency(Currency currencyToEdit, String newName, String newTag, BigDecimal newLinkRatio, int newPointPosition, Currency newLink)
    {
        ContentValues values = new ContentValues();
        values.put(CURRENCIES_TABLE_COL2, newName);
        values.put(CURRENCIES_TABLE_COL3, newTag);
        values.put(CURRENCIES_TABLE_COL4, newLinkRatio.toString());
        values.put(CURRENCIES_TABLE_COL5, newPointPosition);
        values.put(CURRENCIES_TABLE_COL6, newLink.getIdC());

        int pointPositionDifference = newPointPosition - currencyToEdit.getPointPosition();
        BigInteger accountAmountChange = new BigInteger("1");
        for(int i=0; i<Math.abs(pointPositionDifference); i++)
        {
            accountAmountChange = accountAmountChange.multiply(new BigInteger("10"));
        }

        if(pointPositionDifference>0)
        {
            editAccountsActions(currencyToEdit, true, accountAmountChange);
        }
        else if(pointPositionDifference<0)
        {
            editAccountsActions(currencyToEdit, false, accountAmountChange);
        }

        SQLiteDatabase db = this.getWritableDatabase();

        db.update(CURRENCIES_TABLE_NAME, values, "IdC="+currencyToEdit.getIdC(), null);
    }

    public boolean deleteSingleCurrencyIfPossible(Currency currency)
    {
        Global.getInstance().getCurrenciesList().clear();

        SQLiteDatabase db = this.getWritableDatabase();

        if(!(currency.getTag().equals("EUR")||currency.equals(Global.getInstance().getMainCurrency())))
        {
            String queryAccounts = "Select COUNT(*) FROM " + ACCOUNTS_TABLE_NAME +" WHERE IdC="+currency.getIdC();

            Cursor cursor = db.rawQuery(queryAccounts, null);
            cursor.moveToFirst();
            int amount = cursor.getInt(0);

            String queryLinks = "Select COUNT(*) FROM " + CURRENCIES_TABLE_NAME +" WHERE IdLink="+currency.getIdC();

            Cursor cursor2 = db.rawQuery(queryLinks, null);
            cursor2.moveToFirst();
            amount = amount + cursor2.getInt(0);
            cursor2.close();
            cursor.close();
            if(amount==0)
            {
                deleteSingleCurrency(currency);
                readCurrencies();
                return true;
            }
            else
            {
                readCurrencies();
                return false;
            }
        }
        readCurrencies();
        return false;
    }

    private void deleteSingleCurrency(Currency currency)
    {
        deleteCurrency(currency);
    }

    private void deleteCurrency(Currency currency)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("DELETE FROM "+CURRENCIES_TABLE_NAME +" WHERE IdC="+currency.getIdC());

        for(Account account : Global.getInstance().getAccountsList())//bez sensu
        {
            if(account.getCurrency().getIdC()==currency.getIdC())
            {
                deleteAccount(account);
            }
        }
    }

    public void readCurrencies()
    {
        Global.getInstance().getCurrenciesList().clear();

        SQLiteDatabase db = this.getWritableDatabase();

        String query = "Select * FROM " + CURRENCIES_TABLE_NAME;

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst())
        {
            cursor.moveToFirst();
            while(!cursor.isAfterLast())
            {
                Currency currency = new Currency();

                currency.setIdC(cursor.getInt(0));
                currency.setName(cursor.getString(1));
                currency.setTag(cursor.getString(2));
                currency.setLinkRatio(new BigDecimal(cursor.getString(3)));
                currency.setPointPosition(cursor.getInt(4));
                currency.setIdLink(cursor.getInt(5));

                Global.getInstance().getCurrenciesList().add(currency);
                cursor.moveToNext();
            }

            for(Currency currency : Global.getInstance().getCurrenciesList())
            {
                for(Currency currencyToLink : Global.getInstance().getCurrenciesList())
                {
                    if(currency.getIdC()==currencyToLink.getIdLink())
                    {
                        currencyToLink.setLink(currency);
                    }
                }
                if(currency.getTag().equals("РУБ"))
                {
                    currency.setLink(currency);
                    currency.setIdLink(currency.getIdC());
                }
            }
            cursor.close();
        }
    }
}
