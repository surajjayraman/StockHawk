package com.sam_chordas.android.stockhawk.modal;

/**
 * Created by S.Shivasurya on 4/8/2016.
 */
public class Results
{
    private Quote[] quote;

    public Quote[] getQuote ()
    {
        return quote;
    }

    public void setQuote (Quote[] quote)
    {
        this.quote = quote;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [quote = "+quote+"]";
    }
}

