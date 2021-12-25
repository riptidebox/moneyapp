package com.example.wheresmymoney.model;

import android.support.v7.app.AppCompatActivity;

import com.example.wheresmymoney.httpRequest.APIRequest;

import java.util.ArrayList;

public class API
{
    private int ID;
    private String name;
    private String apiKey;
    private AppCompatActivity requestingActivity;

    private final ArrayList<APIRequest> apiRequestsList;

    public API()
    {
        this.apiRequestsList = new ArrayList<>();
    }

    public API(String name, String apiKey)
    {
        this.name = name;
        this.apiKey = apiKey;
        this.apiRequestsList = new ArrayList<>();
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

    public void setName(String newName)
    {
        this.name = newName;
    }

    public String getApiKey()
    {
        return apiKey;
    }

    public void setApiKey(String newApiKey)
    {
        this.apiKey = newApiKey;
    }

    public AppCompatActivity getRequestingActivity()
    {
        return requestingActivity;
    }

    public void addAPIRequest(APIRequest newRequest)
    {
        apiRequestsList.add(newRequest);
    }

    public void makeRequest(String name, AppCompatActivity requestingActivity)
    {
        this.requestingActivity = requestingActivity;
        for(APIRequest request : apiRequestsList)
        {
            if(request.getName().equals(name))
            {
                request.makeRequest(this);
            }
        }
    }
}
