package com.sam_chordas.android.stockhawk.widget;

import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;
import com.sam_chordas.android.stockhawk.ui.MyStocksActivity;

/**
 * Created by Praneeth on 4/29/2016.
 */
public class StockWidgetRemoteViewsService extends RemoteViewsService {

    String LOG_TAG = StockWidgetRemoteViewsService.class.getSimpleName();

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewsFactory() {

            public final String[] STOCK_COLUMNS = {
                    QuoteColumns._ID,
                    QuoteColumns.SYMBOL,
                    QuoteColumns.PERCENT_CHANGE,
                    QuoteColumns.BIDPRICE,
                    QuoteColumns.CHANGE,
                    QuoteColumns.ISUP,
            };

            public static final int COL_STOCK_ID = 0;
            public static final int COL_STOCK_SYMBOL = 1;
            public static final int COL_STOCK_PERCENT_CHANGE = 2;
            public static final int COL_STOCK_BIDPRICE =3 ;
            public static final int COL_STOCK_CHANGE = 4;
            public static final int COL_STOCK_ISUP = 5;


            private Cursor data = null;
            @Override
            public void onCreate() {
                // Nothing to do
            }

            @Override
            public void onDataSetChanged() {
                if (data != null) {
                    data.close();
                }

                // This method is called by the app hosting the widget (e.g., the launcher)
                // However, our ContentProvider is not exported so it doesn't have access to the
                // data. Therefore we need to clear (and finally restore) the calling identity so
                // that calls use our process and permission
                final long identityToken = Binder.clearCallingIdentity();

                data = getContentResolver().query(
                        QuoteProvider.Quotes.CONTENT_URI,
                        STOCK_COLUMNS,
                        QuoteColumns.ISCURRENT + " = ?",
                        new String[]{"1"},
                        null);
                Binder.restoreCallingIdentity(identityToken);
            }

            @Override
            public void onDestroy() {

            }

            @Override
            public int getCount() {
                return data == null ? 0 : data.getCount();
            }

            @Override
            public RemoteViews getViewAt(int position) {
                if (position == AdapterView.INVALID_POSITION ||
                        data == null || !data.moveToPosition(position)) {
                    return null;
                }

                RemoteViews views = new RemoteViews(getPackageName(), R.layout.widget_detail_list_item);

                views.setTextViewText(R.id.stock_symbol, data.getString(COL_STOCK_SYMBOL));

                if (data.getInt(COL_STOCK_ISUP) == 1) {
                    views.setInt(R.id.change, getResources().getString(R.string.string_set_background_resource), R.drawable.percent_change_pill_green);
                } else {
                    views.setInt(R.id.change, getResources().getString(R.string.string_set_background_resource), R.drawable.percent_change_pill_red);
                }

                views.setTextViewText(R.id.change, data.getString(COL_STOCK_CHANGE));

                final Intent fillInIntent = new Intent();
                fillInIntent.putExtra(MyStocksActivity.TAG_STOCK_SYMBOL, data.getString(COL_STOCK_SYMBOL));
                views.setOnClickFillInIntent(R.id.widget_list_item, fillInIntent);

                return views;
            }

            @Override
            public RemoteViews getLoadingView() {
                return null; // use the default loading view
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int position) {
                // Get the row ID for the view at the specified position
                if (data != null && data.moveToPosition(position)) {
                    return data.getLong(COL_STOCK_ID);
                }
                return position;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }
}
/**
 * Creates an IntentService.  Invoked by your subclass's constructor.
 *
 * @param name Used to name the worker thread, important only for debugging.
 */
//    public StockWidgetIntentService() {
//        super("StockWidgetIntentService");
//    }
//
//    @Override
//    protected void onHandleIntent(Intent intent) {
//        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
//        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this,
//                StockWidgetProvider.class));
//
//        Cursor data = getContentResolver().query(QuoteProvider.Quotes.CONTENT_URI,
//                MyStocksActivity.STOCK_COLUMNS,
//                QuoteColumns.ISCURRENT + " = ?",
//                new String[]{"1"},
//                null);
//
//        if (data == null) {
//            return;
//        }
//        if (!data.moveToFirst()) {
//            data.close();
//            return;
//        }
//
//        String symbol = data.getString(MyStocksActivity.COL_STOCK_SYMBOL);
//        float bidPrice = data.getFloat(MyStocksActivity.COL_STOCK_BIDPRICE);
//        int isUP = data.getInt(MyStocksActivity.COL_STOCK_ISUP);
//
//        Log.d(LOG_TAG, "data is: "+data);
//
//        data.close();
//
//        for(int appWidgetId : appWidgetIds) {
//            int layoutId = R.layout.widget_stock;
//            RemoteViews views = new RemoteViews(getPackageName(), layoutId);
//
//            views.setTextViewText(R.id.widget_stock_symbol, symbol);
//            views.setTextViewText(R.id.widget_bid_price, Float.toString(bidPrice));
//            if (isUP == 1) {
//                views.setInt(R.id.widget_change, getResources().getString(R.string.string_set_background_resource), R.drawable.percent_change_pill_green);
//            } else {
//                views.setInt(R.id.widget_change, getResources().getString(R.string.string_set_background_resource), R.drawable.percent_change_pill_red);
//            }
//
//            Intent launchIntent = new Intent(this, MyStocksActivity.class);
//            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, launchIntent, 0);
//            views.setOnClickPendingIntent(R.id.widget, pendingIntent);
//
//            appWidgetManager.updateAppWidget(appWidgetId, views);
//
//
//        }
//
//
//
//    }

