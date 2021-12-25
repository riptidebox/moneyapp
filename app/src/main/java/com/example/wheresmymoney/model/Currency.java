package com.example.wheresmymoney.model;

import android.support.annotation.NonNull;

import com.example.wheresmymoney.singleton.Global;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Currency
{
    private int IdC;
    private int IdLink;
    private Currency link;
    private String tag;
    private String name;
    private BigDecimal linkRatio;
    private int pointPosition;

    public Currency()
    {
        this.link = this;
    }

    public Currency(String name, String tag, BigDecimal linkRatio, int pointPosition, Currency link)
    {
        this.name = name;
        this.tag = tag;
        this.linkRatio = linkRatio;
        this.pointPosition = pointPosition;
        this.link = link;
    }

    public String toNaturalLanguage(BigInteger amount)
    {
        StringBuilder result = new StringBuilder();
        String amountString = amount.toString();

        if(amount.signum()>=0)
        {
            String recentDigit;

            for(int recentPos = 1; recentPos<amountString.length(); recentPos++)
            {
                recentDigit = amount.divideAndRemainder(new BigInteger("10"))[1].toString();
                amount = amount.divideAndRemainder(new BigInteger("10"))[0];
                if(recentPos == pointPosition)
                {
                    result.insert(0, "," + recentDigit);
                }
                else
                {
                    result.insert(0, recentDigit);
                }
            }

            result.insert(0, amount.toString());

            if(result.length()<pointPosition+2)
            {
                for(int i = result.length(); i<pointPosition; i++)
                {
                    result.insert(0, "0");
                }

                if(pointPosition!=0)
                {
                    result.insert(0, "0,");
                }
            }
        }
        else
        {
            amount = amount.abs();

            String recentDigit;

            for(int recentPos = 1; recentPos<amountString.length(); recentPos++)
            {
                recentDigit = amount.divideAndRemainder(new BigInteger("10"))[1].toString();
                amount = amount.divideAndRemainder(new BigInteger("10"))[0];
                if(recentPos == pointPosition)
                {
                    result.insert(0, "," + recentDigit);
                }
                else
                {
                    result.insert(0, recentDigit);
                }
            }

            result.insert(0, amount.toString());

            if(result.length()<pointPosition+2)
            {
                for(int i = result.length(); i<pointPosition; i++)
                {
                    result.insert(0, "0");
                }

                if(pointPosition!=0)
                {
                    result.insert(0, "0,");
                }
            }

            result.insert(0, "-");
        }
        return result +" " + getTag();
    }

    public boolean equals(Currency other)
    {
        return this.toString().equals((other).toString());
    }

    @NonNull
    public String toString() {
        return name+", "+tag;
    }

    public String getTag()
    {
        return tag;
    }

    public void setTag(String newTag)
    {
        this.tag = newTag;
    }

    public BigDecimal getLinkRatio() {
        return linkRatio;
    }

    public void setLinkRatio(BigDecimal linkRatio) {
        this.linkRatio = linkRatio;
    }

    public BigDecimal toOtherRatio(Currency other)
    {
        if(this.equals(other))
        {
            return new BigDecimal("1.0");
        }
        return this.toRootRatio().divide(other.toRootRatio(), 20, RoundingMode.HALF_UP);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigInteger getDivision()
    {
        BigInteger div = new BigInteger("1");
        for(int i=0; i<pointPosition; i++)
        {
            div = div.multiply(new BigInteger("10"));
        }
        return div;
    }

    public int getPointPosition() {
        return pointPosition;
    }

    public void setPointPosition(int pointPosition) {
        this.pointPosition = pointPosition;
    }

    public int getIdC() {
        return IdC;
    }

    public void setIdC(int idC) {
        IdC = idC;
    }

    public static void sortCurrencies(ArrayList<Currency> listToSort)
    {
        Collections.sort(listToSort, new Comparator<Currency>() {
            @Override
            public int compare(Currency cur1, Currency cur2)
            {
                return  cur1.getTag().compareTo(cur2.getTag());
            }
        });
    }

    public void setLink(Currency link)
    {
        this.link = link;
    }

    public Currency getLink()
    {
        return link;
    }

    public void setIdLink(int newIdLink)
    {
        this.IdLink = newIdLink;
    }

    public int getIdLink()
    {
        return IdLink;
    }

    public BigDecimal toRootRatio()
    {
        Currency recentMedium = this;
        BigDecimal result = new BigDecimal("1");
        while(!recentMedium.getTag().equals("РУБ"))
        {
            result = result.multiply(recentMedium.linkRatio);
            recentMedium = recentMedium.getLink();
        }
        result = result.multiply(recentMedium.linkRatio);
        return result;
    }

    public BigDecimal toMainRatio()
    {
        if(equals(Global.getInstance().getMainCurrency()))
        {
            return new BigDecimal("1.0");
        }
        else if(Global.getInstance().getMainCurrency().isLinkedTo(this))
        {
            Currency recentMedium = Global.getInstance().getMainCurrency();
            BigDecimal result = new BigDecimal("1.0");
            while(!recentMedium.equals(this))
            {
                result = result.multiply(recentMedium.linkRatio);
                recentMedium = recentMedium.getLink();
            }
            return (new BigDecimal("1.0").divide(result, 20, RoundingMode.HALF_UP));
        }
        else if(isLinkedTo(Global.getInstance().getMainCurrency()))
        {
            Currency recentMedium = this;
            BigDecimal result = new BigDecimal("1.0");
            while(!recentMedium.equals(Global.getInstance().getMainCurrency()))
            {
                result = result.multiply(recentMedium.linkRatio);
                recentMedium = recentMedium.getLink();
            }
            return result;
        }
        else
        {
            return toRootRatio().divide(Global.getInstance().getMainCurrency().toRootRatio(), 20, RoundingMode.HALF_UP);
        }
    }

    public boolean isLinkedTo(Currency otherCurrency)
    {
        if(equals(otherCurrency))
        {
            return true;
        }
        else if(getTag().equals("РУБ"))
        {
            return false;
        }
        else
        {
            return link.isLinkedTo(otherCurrency);
        }
    }
}