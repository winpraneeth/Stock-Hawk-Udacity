package com.sam_chordas.android.stockhawk.ui;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;

import com.db.chart.Tools;
import com.db.chart.model.LineSet;
import com.db.chart.view.AxisController;
import com.db.chart.view.LineChartView;
import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;

import java.util.ArrayList;
import java.util.Collections;

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    String LOG_TAG = DetailActivity.class.getSimpleName();
    private static final int CURSOR_LOADER_ID = 0;
    String data;
    private LineChartView lineChartView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_graph);

        lineChartView = (LineChartView) findViewById(R.id.linechart);

        Bundle args = new Bundle();
        data = getIntent().getStringExtra(MyStocksActivity.TAG_STOCK_SYMBOL);
        args.putString(MyStocksActivity.TAG_STOCK_SYMBOL, data);

        getSupportLoaderManager().initLoader(CURSOR_LOADER_ID, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this,
                QuoteProvider.Quotes.CONTENT_URI,
                MyStocksActivity.STOCK_COLUMNS,
                QuoteColumns.SYMBOL + " = ?",
                new String[]{data},
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        stockGraph(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private void stockGraph(Cursor data) {

        LineSet lineSet = new LineSet();
        ArrayList<Float> mList = new ArrayList<Float>();

        for (data.moveToFirst(); !data.isAfterLast(); data.moveToNext()) {

            String bidPrice = data.getString(MyStocksActivity.COL_STOCK_BIDPRICE);
            float price = Float.parseFloat(bidPrice);
            mList.add(price);

            lineSet.addPoint(bidPrice, price);

        }

        float maxBid = Math.round(Collections.max(mList));
        float minBid = Math.round(Collections.min(mList));

        lineSet.setColor(getResources().getColor(R.color.graph_blue))
                .setFill(getResources().getColor(R.color.graph_light_blue))
                .setThickness(4)
                .isSmooth();

        lineChartView.setBorderSpacing(Tools.fromDpToPx(15))
                .setYLabels(AxisController.LabelPosition.OUTSIDE)
                .setXLabels(AxisController.LabelPosition.NONE)
                .setLabelsColor(getResources().getColor(R.color.graph_dark_blue))
                .setXAxis(false)
                .setYAxis(false)
                .setAxisBorderValues(Math.round(Math.max(0f, minBid - 5f)), Math.round(maxBid + 5f))
                .addData(lineSet);

        lineChartView.show();

    }
}
