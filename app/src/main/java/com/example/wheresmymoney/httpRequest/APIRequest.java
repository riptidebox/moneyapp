package com.example.wheresmymoney.httpRequest;

import com.example.wheresmymoney.model.API;

public interface APIRequest
{
    String getName();

    void makeRequest(API requestAPI);
}
