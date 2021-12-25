package com.example.wheresmymoney.model;

import com.example.wheresmymoney.singleton.Global;

import java.math.BigInteger;
import java.util.ArrayList;

public class Account
{
    private int IdA;
    private String name;
    private BigInteger balance;
    private final ArrayList<Action> actionHistory;
    private Currency currency;

    public Account()
    {
        this.actionHistory = new ArrayList<>();
    }

    public Account(String name, Currency currency)
    {
        this.name = name;
        this.balance = new BigInteger("0");
        this.actionHistory = new ArrayList<>();
        this.currency = currency;
    }

    public void addTransaction(Action action)
    {
        action.setAccount(this);
        Global.getInstance().databaseHelper.addAction(action);
    }

    public void removeTransaction(Action action)
    {
        Global.getInstance().databaseHelper.deleteSingleAction(action);
    }

    public String getName()
    {
        return this.name;
    }

    public BigInteger getBalance()
    {
        return this.balance;
    }

    public ArrayList<Action> getActionHistory()
    {
        return this.actionHistory;
    }

    public Currency getCurrency()
    {
        return currency;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void setBalance(BigInteger balance)
    {
        this.balance = balance;
    }

    public void setCurrency(Currency currency)
    {
        this.currency = currency;
    }

    public int getIdA() {
        return IdA;
    }

    public void setIdA(int idA) {
        IdA = idA;
    }
}
