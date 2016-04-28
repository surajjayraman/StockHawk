package com.sam_chordas.android.stockhawk.modal;

/**
 * Created by S.Shivasurya on 4/8/2016.
 */public class Result
{
    private Query query;

    public Query getQuery ()
    {
        return query;
    }


    public void setQuery (Query query)
    {
        this.query = query;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [query = "+query+"]";
    }
}
