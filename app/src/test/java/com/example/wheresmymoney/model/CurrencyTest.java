package com.example.wheresmymoney.model;


import org.junit.Test;

import java.math.BigDecimal;
import java.math.BigInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CurrencyTest
{
    @Test
    public void toNaturalLanguageIsCorrect()
    {
        Currency currency = new Currency("Name", "N", new BigDecimal("1"), 5, new Currency());
        assertEquals("0,10000 N", currency.toNaturalLanguage(new BigInteger("10000")));
    }

    @Test
    public void equalsIsCorrect()
    {
        Currency currency1 = new Currency("Name", "N", new BigDecimal("1"), 5, new Currency());
        Currency currency2 = new Currency("Name", "N", new BigDecimal("14"), 50, new Currency());
        Currency currency3 = new Currency("Name", "N1", new BigDecimal("14"), 50, new Currency());
        Currency currency4 = new Currency("Name2", "N", new BigDecimal("14"), 50, new Currency());
        assertTrue(currency1.equals(currency2));
        assertFalse(currency1.equals(currency3));
        assertFalse(currency1.equals(currency4));
    }

    @Test
    public void getDivisionIsCorrect() {
        Currency currency = new Currency("A", "A", new BigDecimal("1.0"), 2, new Currency());

        assertEquals("100", currency.getDivision().toString());
    }
}
