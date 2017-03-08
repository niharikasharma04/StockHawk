package com.udacity.stockhawk.ui;


import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.udacity.stockhawk.MyXAxisValueFormatter;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.Utils;
import com.udacity.stockhawk.data.Contract;

import java.util.ArrayList;
import java.util.List;

import static com.udacity.stockhawk.data.Contract.Quote.QUOTE_COLUMNS;
import static com.udacity.stockhawk.data.Contract.Quote.makeUriForStock;


/**
 * Created by nihar on 2/21/2017.
 */

public class StockDetail extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private String symbol;
    private Uri stockUri;
    private LineChart lineChart;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stock_detail);
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
            symbol = intent.getStringExtra(Intent.EXTRA_TEXT);
        }
        stockUri = makeUriForStock(symbol);
        lineChart = (LineChart) findViewById(R.id.stocks_linechart);
        getSupportLoaderManager().initLoader(0, null, this);

    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this,stockUri, QUOTE_COLUMNS.toArray(new String[]{}),null,null,null);
        //return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {
            String stockHistory = data.getString(Contract.Quote.POSITION_HISTORY);

            ArrayList<Float> priceHistory = new ArrayList<>();
            ArrayList<String> dateHistory = new ArrayList<>();
            if( stockHistory !=null){
                String[] splitStockHistory = stockHistory.split("\\r?\\n|,");
                for(int i=0;i<splitStockHistory.length-1;i++)
                {
                    if (i % 2 == 0) {
                        long dateInMS = Long.parseLong(splitStockHistory[i]);
                        dateHistory.add(Utils.formatDate(dateInMS));
                    }
                    else {priceHistory.add(Float.valueOf(splitStockHistory[i]));
                    }
                }
            }

            List<Entry> entries = new ArrayList<Entry>();
            String[] datesArray = new String[dateHistory.size()];
            for (int i = 0; i < priceHistory.size(); i++) {
                // turn your data into Entry objects
                entries.add(new Entry(i, priceHistory.get(i)));
            }
            String[] values = new String[dateHistory.size()];
            for(int i = 0; i < dateHistory.size(); i++){
                values[i] = dateHistory.get(i);
            }


        LineDataSet lineDataset = new LineDataSet(entries, symbol);
        lineDataset.setDrawCircles(false);
        lineDataset.setColor(Color.BLUE);
            XAxis xAxis = lineChart.getXAxis();
            xAxis.setValueFormatter(new MyXAxisValueFormatter(values));

            List<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
            dataSets.add(lineDataset);

            LineData data1 = new LineData(dataSets);
            lineChart.setData(data1);
            applyChartSettings(lineChart); // Apply settings to the chart

            lineChart.setContentDescription("stock price");
            lineChart.invalidate();

        }
    }
    private void applyChartSettings(LineChart lineChart) {
        int textColor = getResources().getColor(R.color.colorAccent);


        lineChart.getAxisLeft().setTextColor(textColor);

        lineChart.getAxisRight().setTextColor(textColor);
        lineChart.getXAxis().setTextColor(textColor);
        lineChart.getLegend().setEnabled(false);

        lineChart.setDescription(null);

        lineChart.setPinchZoom(true);
    }



    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }


}

