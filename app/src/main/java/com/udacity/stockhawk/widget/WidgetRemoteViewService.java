package com.udacity.stockhawk.widget;


import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.os.Build;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Created by nihar on 2/23/2017.
 */

public class WidgetRemoteViewService extends RemoteViewsService {

    private static final String[] QUOTE_DATA = {
            Contract.Quote._ID,
            Contract.Quote.COLUMN_SYMBOL,
            Contract.Quote.COLUMN_PRICE,
            Contract.Quote.COLUMN_PERCENTAGE_CHANGE
    };
    //indices as per projection
    private static final int id_index = 0;
    private static final int symbol_index = 1;
    private static final int price_index = 2;
    private static final int percent_change_index = 3;
    private DecimalFormat priceFormat;
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {

        return new RemoteViewsFactory() {
            private Cursor data = null;

            @Override
            public void onCreate() {

            }

            @Override
            public void onDataSetChanged() {
                if (data != null) {
                    data.close();
                }
                final long identityToken = Binder.clearCallingIdentity();
                data = getContentResolver().query(Contract.Quote.URI,
                        QUOTE_DATA,
                        null,
                        null,
                        Contract.Quote.COLUMN_SYMBOL);
                Binder.restoreCallingIdentity(identityToken);

            }

            @Override
            public void onDestroy() {
                if (data != null) {
                    data.close();
                    data = null;
                }

            }

            @Override
            public int getCount() {
                if (data != null)
                    return data.getCount();
                return 0;
            }



            @Override
            public RemoteViews getViewAt(int position) {
                if (position == AdapterView.INVALID_POSITION || data == null ||
                        !data.moveToPosition(position)) {
                    return null;
                }
                RemoteViews views = new RemoteViews(getPackageName(),
                        R.layout.widget_detail_list);

                String stockSymbol = data.getString(symbol_index);
                float price = data.getFloat(price_index);
                priceFormat = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
                float change = data.getFloat(percent_change_index);
                DecimalFormat percentageFormat = (DecimalFormat) NumberFormat.getPercentInstance(Locale.getDefault());
                percentageFormat.setMaximumFractionDigits(2);
                percentageFormat.setMinimumFractionDigits(2);
                percentageFormat.setPositivePrefix("+");
                if (Build.VERSION.SDK_INT >=23) {


                    if (change > 0) {
                        views.setTextColor(R.id.change, getColor(R.color.material_green_700));

                    } else {
                        views.setTextColor(R.id.change, getColor(R.color.material_red_700));

                    }
                }

                // Add the data to the RemoteViews

                views.setTextViewText(R.id.symbol, stockSymbol);
                views.setContentDescription(R.id.symbol, stockSymbol);
                views.setTextViewText(R.id.price, priceFormat.format(price));
                views.setTextViewText(R.id.change, percentageFormat.format(change / 100));

                // Extra info accompanies the intent
                Intent fillInIntent = new Intent();
                fillInIntent.putExtra(Intent.EXTRA_TEXT, stockSymbol);
                views.setOnClickFillInIntent(R.id.widget_item, fillInIntent);

                return views;
            }

            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(), R.layout.widget_detail_list);
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int i) {
                if (data.moveToPosition(i))
                    return data.getLong(id_index);
                return i;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }


}
