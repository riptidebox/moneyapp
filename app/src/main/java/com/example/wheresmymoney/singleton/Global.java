package com.example.wheresmymoney.singleton;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.example.wheresmymoney.R;
import com.example.wheresmymoney.activities.MainActivity;
import com.example.wheresmymoney.httpRequest.RatesRequest;
import com.example.wheresmymoney.model.API;
import com.example.wheresmymoney.model.Account;
import com.example.wheresmymoney.model.Action;
import com.example.wheresmymoney.model.Currency;
import com.example.wheresmymoney.utils.DatabaseHelper;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;

public class Global {
    public DatabaseHelper databaseHelper;

    private MainActivity mainActivity;
    public boolean authenticated = false;

    private String currenciesAPIKey = "7f1e42734df9c2b398a26e895321ec20";

    private ArrayList<Currency> currenciesList;
    private Currency mainCurrency;
    private Currency recentCurrency;
    private Currency rootCurrency;

    private ArrayList<Account> accountsList;
    private Account recentAccount;

    private ArrayList<Action> actionsList;
    private Action recentAction;

    private ArrayList<API> apisList;
    private API recentAPI;

    private ArrayList<Integer> themesList;
    private Integer recentTheme;
    private Integer recentThemeId;
    private boolean themeChanged = false;

    private static Global instance;
    public static Global getInstance()
    {
        return instance;
    }
    private Global(){}

    public static void initiateGlobal(MainActivity mainActivity)
    {
        instance = new Global();

        instance.currenciesList = new ArrayList<>();
        instance.accountsList = new ArrayList<>();
        instance.actionsList = new ArrayList<>();
        instance.apisList = new ArrayList<>();
        instance.themesList = new ArrayList<>();
        instance.themesList.add(R.style.AppTheme);
        instance.themesList.add(R.style.AppThemeDark);
        instance.recentTheme = R.style.AppTheme;
        instance.mainActivity = mainActivity;
        instance.databaseHelper = new DatabaseHelper(instance.mainActivity);
        instance.databaseHelper.readCurrencies();

        if(instance.currenciesList.size()==0)
        {
            Currency eur = new Currency();
            eur.setName("Euro");
            eur.setTag("EUR");
            eur.setLinkRatio(new BigDecimal(1.0));
            eur.setPointPosition(2);
            eur.setLink(eur);
            instance.databaseHelper.addCurrency(eur);
            instance.databaseHelper.readCurrencies();
        }
        instance.databaseHelper.updateState();

        instance.mainCurrency = instance.currenciesList.get(0);
        instance.rootCurrency = instance.currenciesList.get(0);

        Currency.sortCurrencies(instance.currenciesList);

        //
        API currenciesAPI = new API("Currencies",instance.currenciesAPIKey);
        instance.apisList.add(currenciesAPI);
        currenciesAPI.addAPIRequest(new RatesRequest());

        instance.readPreferences();
    }

    public void setTheme(int themeId)
    {
        instance.recentTheme = instance.themesList.get(themeId);
        instance.recentThemeId = themeId;
        instance.themeChanged = true;
    }

    public DatabaseHelper getDatabaseHelper() {
        return databaseHelper;
    }

    public void setDatabaseHelper(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    public MainActivity getMainActivity() {
        return mainActivity;
    }

    public void setMainActivity(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }

    public String getCurrenciesAPIKey() {
        return currenciesAPIKey;
    }

    public void setCurrenciesAPIKey(String currenciesAPIKey) {
        this.currenciesAPIKey = currenciesAPIKey;
    }

    public ArrayList<Currency> getCurrenciesList() {
        return currenciesList;
    }

    public void setCurrenciesList(ArrayList<Currency> currenciesList) {
        this.currenciesList = currenciesList;
    }

    public Currency getMainCurrency() {
        return mainCurrency;
    }

    public void setMainCurrency(Currency mainCurrency) {
        this.mainCurrency = mainCurrency;
    }

    public Currency getRecentCurrency() {
        return recentCurrency;
    }

    public void setRecentCurrency(Currency recentCurrency) {
        this.recentCurrency = recentCurrency;
    }

    public Currency getRootCurrency() {
        return rootCurrency;
    }

    public void setRootCurrency(Currency rootCurrency) {
        this.rootCurrency = rootCurrency;
    }

    public ArrayList<Account> getAccountsList() {
        return accountsList;
    }

    public void setAccountsList(ArrayList<Account> accountsList) {
        this.accountsList = accountsList;
    }

    public Account getRecentAccount() {
        return recentAccount;
    }

    public void setRecentAccount(Account recentAccount) {
        this.recentAccount = recentAccount;
    }

    public ArrayList<Action> getActionsList() {
        return actionsList;
    }

    public void setActionsList(ArrayList<Action> actionsList) {
        this.actionsList = actionsList;
    }

    public Action getRecentAction() {
        return recentAction;
    }

    public void setRecentAction(Action recentAction) {
        this.recentAction = recentAction;
    }

    public ArrayList<API> getApisList() {
        return apisList;
    }

    public void setApisList(ArrayList<API> apisList) {
        this.apisList = apisList;
    }

    public API getRecentAPI() {
        return recentAPI;
    }

    public void setRecentAPI(API recentAPI) {
        this.recentAPI = recentAPI;
    }

    public ArrayList<Integer> getThemesList() {
        return themesList;
    }

    public void setThemesList(ArrayList<Integer> themesList) {
        this.themesList = themesList;
    }

    public Integer getRecentTheme() {
        return recentTheme;
    }

    public void setRecentTheme(Integer recentTheme) {
        this.recentTheme = recentTheme;
    }

    public Integer getRecentThemeId() {
        return recentThemeId;
    }

    public void setRecentThemeId(Integer recentThemeId) {
        this.recentThemeId = recentThemeId;
    }

    public boolean isThemeChanged() {
        return themeChanged;
    }

    public void setThemeChanged(boolean themeChanged) {
        this.themeChanged = themeChanged;
    }

    private void readPreferences()
    {
        try
        {
            SharedPreferences sharedPref = instance.mainActivity.getPreferences(Context.MODE_PRIVATE);
            int mainCurrencyId = sharedPref.getInt("MainCurrency", 0);
            for(Currency currency : instance.currenciesList)
            {
                if(currency.getIdC()==mainCurrencyId)
                {
                    instance.mainCurrency = currency;
                }
            }

            instance.recentThemeId = sharedPref.getInt("RecentTheme", 0);
            instance.recentTheme = instance.themesList.get(instance.recentThemeId);
        } catch (Exception e) {
            displayMessage("Cannot read shared preferences");
        }
    }

    public void savePreferences()
    {
        SharedPreferences sharedPref = instance.mainActivity.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("MainCurrency", instance.mainCurrency.getIdC());
        editor.putInt("RecentTheme", instance.recentThemeId);
        editor.apply();
    }

    public static BigInteger getBalance()
    {
        BigInteger result = new BigInteger("0");
        for (Account account:instance.accountsList)
        {
            if(account.getCurrency().equals(instance.mainCurrency))
            {
                result = result.add(account.getBalance());
            }
            else
            {
                result = result.add(
                        (
                                (
                                        (
                                                (new BigDecimal(
                                                        account.getBalance().multiply(
                                                                instance.mainCurrency.getDivision())).divide(
                                                                        new BigDecimal(
                                                                                account.getCurrency().getDivision()), 20, RoundingMode.HALF_UP))).divide(
                                                                                        account.getCurrency().toMainRatio(), 20, RoundingMode.HALF_UP))).round(
                                                                                                new MathContext(20,RoundingMode.HALF_UP)
                        ).toBigInteger()
                );
            }
        }
        return result;
    }

    public void displayMessage(String messageText)
    {
        Toast.makeText(mainActivity.getApplicationContext(),messageText, Toast.LENGTH_SHORT).show();
    }
}
