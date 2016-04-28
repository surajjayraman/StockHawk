package com.sam_chordas.android.stockhawk.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.modal.Query;
import com.sam_chordas.android.stockhawk.modal.Quote;
import com.sam_chordas.android.stockhawk.modal.Result;
import com.sam_chordas.android.stockhawk.modal.Results;
import com.sam_chordas.android.stockhawk.singleton.QuoteService;
import com.sam_chordas.android.stockhawk.utility.CustomSpinnerAdapter;
import com.sam_chordas.android.stockhawk.utility.Utility;
import com.squareup.okhttp.OkHttpClient;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.util.ArrayList;
import java.util.Date;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.client.Response;

;

public class DetailActivity extends AppCompatActivity  {
    String result;
    public static String symbol;
    private RelativeLayout mChartHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        mChartHolder = (RelativeLayout)findViewById(R.id.container_main);
        symbol = getIntent().getExtras().getString("symbol");
        Log.d("symbol",symbol);
        getSupportActionBar().setTitle(symbol);
    }

    private void callRetrofitFetch(String symbol, String startDate, String endDate) {

        RestAdapter.Builder builder = new RestAdapter.Builder()
                .setEndpoint("https://query.yahooapis.com/v1")
                .setClient(new OkClient(new OkHttpClient()));

        RestAdapter adapter = builder.build();
        QuoteService QuoteApi = adapter.create(QuoteService.class);

        String q = "select * from yahoo.finance.historicaldata where symbol = \""+symbol+"\" and startDate = \""+endDate+"\" and endDate = \""+startDate+"\"";
        String diagnostics = "true";
        String env = "store://datatables.org/alltableswithkeys";
        String format = "json";
        QuoteApi.getHistoricalData(q, diagnostics, env, format, new Callback<Result>() {
            @Override
            public void success(Result result, Response response) {

                mChartHolder.removeAllViews();

                Log.d("result",result.toString());

                Query mQuery = result.getQuery();
                Results mResult = mQuery.getResults();
                Quote[] mQuote = mResult.getQuote();

                XYSeries series = new XYSeries("Stock value - Historic Data");
                int hour = 0;
                for (Quote hf : mQuote) {
                    series.add(hour++, Double.parseDouble(hf.getHigh()));
                    Log.d("value",hf.getHigh());
                }
                XYSeriesRenderer renderer = new XYSeriesRenderer();
                renderer.setLineWidth(2);
                renderer.setColor(Color.RED);
                renderer.setDisplayBoundingPoints(true);
                renderer.setPointStyle(PointStyle.CIRCLE);
                renderer.setPointStrokeWidth(3);
                XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();

                XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
                dataset.addSeries(series);

                mRenderer.addSeriesRenderer(renderer);
                mRenderer.setMarginsColor(Color.argb(0x00, 0xff, 0x00, 0x00)); // transparent margins
                mRenderer.setPanEnabled(false, false);
                mRenderer.setYAxisMax(100);
                mRenderer.setYAxisMin(0);
                mRenderer.setShowGrid(true); // we show the grid
                GraphicalView chartView = ChartFactory.getLineChartView(getApplicationContext(), dataset, mRenderer);

                mChartHolder.addView(chartView);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d("error",error.toString());
                error.printStackTrace();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail_activity, menu);
        MenuItem item = menu.findItem(R.id.spinner);
        Spinner spinner = (Spinner) MenuItemCompat.getActionView(item);

        ArrayList<String> list = new ArrayList<String>();
        list.add(getString(R.string.one_week));
        list.add(getString(R.string.one_month));
        list.add(getString(R.string.three_month));
        list.add(getString(R.string.six_month));
        list.add(getString(R.string.one_year));
        CustomSpinnerAdapter spinAdapter = new CustomSpinnerAdapter(
                getApplicationContext(), list);
        spinner.setAdapter(spinAdapter); // set the adapter to provide layout of rows and content
//        spinner.setOnItemSelectedListener(onItemSelectedListener);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapter, View v,
                                       int position, long id) {
                // On selecting a spinner item
                String item = adapter.getItemAtPosition(position).toString();
                String startDate = Utility.getFormattedDate(System.currentTimeMillis());
                Date date = new Date();
                switch (item)
                {
                    case "1W":
//                        String d = Utility.get1WeekBackDate(date);
                        callRetrofitFetch(symbol,startDate,Utility.get1WeekBackDate(date));
                        break;
                    case "1M":
//                        String d2 = Utility.get1MonthBackDate(date);
                        callRetrofitFetch(symbol,startDate,Utility.get1MonthBackDate(date));
                        break;
                    case "3M":
                        callRetrofitFetch(symbol,startDate,Utility.get3MonthsBackDate(date));
                        break;
                    case "6M":
                        callRetrofitFetch(symbol,startDate,Utility.get6MonthsBackDate(date));
                        break;
                    case "1Y":
                        callRetrofitFetch(symbol,startDate,Utility.get1YearBackDate(date));
                        break;

                }
                // Showing selected spinner item
//                Toast.makeText(getApplicationContext(), "Selected  : " + item,
//                        Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });
        return true;
    }
}
