package com.example.wheresmymoney.httpRequest;

import android.support.v7.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.wheresmymoney.model.API;
import com.example.wheresmymoney.model.Currency;
import com.example.wheresmymoney.singleton.Global;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

public class RatesRequest implements APIRequest
{
    private int ID;
    private final String name;
    private String requestURL;

    private static String requestTag;

    private RequestQueue requestQueue;
    private AppCompatActivity requestingActivity;

    public RatesRequest()
    {
        this.name = "Rates";
        this.requestURL = "http://data.fixer.io/api/latest?access_key=";
    }

    public synchronized AppCompatActivity getRequestingActivity() {
        return requestingActivity;
    }

    public int getID()
    {
        return ID;
    }

    public void setID(int newID)
    {
        this.ID = newID;
    }

    public String getName()
    {
        return name;
    }

    public String getRequestURL()
    {
        return requestURL;
    }

    public void setRequestURL(String newRequestURL)
    {
        this.requestURL = newRequestURL;
    }

    public void makeRequest(API requestAPI)
    {
        requestingActivity = requestAPI.getRequestingActivity();
        requestTag = requestAPI.getRequestingActivity().getClass().getSimpleName();

        StringRequest strReq = new StringRequest(Request.Method.GET, requestURL+requestAPI.getApiKey(), new Response.Listener<String>() {

            @Override
            public void onResponse(String response)
            {
                String responseTemp = response.split("[{]")[2];
                String result = responseTemp.split("[}]")[0];

                String[] currenciesRawData = result.split(",");
                for (String currenciesRawRecord : currenciesRawData)
                {
                    String[] currencyRawData = currenciesRawRecord.split(":");
                    String[] currencyTagData = currencyRawData[0].split("\"");

                    String tag = currencyTagData[1];
                    for(Currency currency : Global.getInstance().getCurrenciesList())
                    {
                        if(currency.getTag().equals(tag))
                        {
                            BigDecimal euroRatio = new BigDecimal(currencyRawData[1]);
                            Global.getInstance().databaseHelper.editCurrency(currency, currency.getName(), currency.getTag(),euroRatio, currency.getPointPosition(), Global.getInstance().getRootCurrency());
                        }
                    }
                }

                if(response.contains("\"success\":false"))
                {
                    Global.getInstance().displayMessage("Could not update currencies\nConsider changing currenciesAPIKey in the Global class");
                }
                else
                {
                    Global.getInstance().databaseHelper.updateState();
                    Global.getInstance().displayMessage("Currencies updated successfully");
                }
            }

            private void setRates(ArrayList<Currency> temporaryCurrenciesList, Currency currencyToSet)
            {
                for(Currency currency : temporaryCurrenciesList)
                {
                    if(currency.getLink().equals(currencyToSet) && !currency.getTag().equals("руб"))
                    {
                        currency.setLinkRatio(currency.getLinkRatio().divide(currencyToSet.toRootRatio(), 20, RoundingMode.HALF_UP));
                        Global.getInstance().databaseHelper.editCurrency(currency, currency.getName(), currency.getTag(),currency.getLinkRatio(), currency.getPointPosition(), currency.getLink());

                        setRates(temporaryCurrenciesList, currency);

                        if(currency.getTag().equals(Global.getInstance().getMainCurrency().getTag()))
                        {
                            Global.getInstance().setMainCurrency(currency);
                        }
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override


            public void onErrorResponse(VolleyError error) {
                Global.getInstance().displayMessage("Could not update currencies\nCheck your internet connection");
            }
        }) {

        };

        addToRequestQueue(strReq);
    }

    private RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(requestingActivity.getApplicationContext());
        }

        return requestQueue;
    }

    private <T> void addToRequestQueue(Request<T> req) {
        req.setTag(requestTag);
        getRequestQueue().add(req);
    }
}
