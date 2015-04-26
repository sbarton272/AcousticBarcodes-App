package com.spencerbarton.acousticbarcodes;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;
import com.spencerbarton.acousticbarcodes.decoder.AcousticBarcodeDecoder;

import org.apache.commons.lang3.ArrayUtils;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends ActionBarActivity {

    private static final String TAG = "MainActivity";

    private static final int BARCODE_LEN = 7;
    private static final int[] BARCODE_START_BITS = {1,1};
    private static final int[] BARCODE_STOP_BITS = {0,1};

    private boolean mRecording = false;

    private AcousticBarcodeDecoder mDecoder;
    private AudioRecorder mRecorder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDecoder = new AcousticBarcodeDecoder(this, BARCODE_LEN, BARCODE_START_BITS, BARCODE_STOP_BITS);
        mRecorder = new AudioRecorder(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //=================================================

    public void onScanBtn(View view) {
        if (!mRecording) {
            mRecording = true;
            startScan();
        } else {
            mRecording = false;
            stopScan();
        }
    }

    private void startScan() {
        mRecorder.startRecording();
        setMsg(getString(R.string.scan_btn_recording), false);
        setBtn("Recording", true);
    }

    private void stopScan() {
        File recording = mRecorder.stopRecording();
        int[] decoded = mDecoder.decode(recording);
        if (decoded == null) {
            setMsg("Unable to decode", true);
        } else {
            setMsg("Decoded " + intArrayToString(decoded) + "\n" + getString(R.string.scan_btn_done)
                    + recording.getName(), false);
        }
        setBtn(getString(R.string.barcode_btn_start), false);
    }

    private String intArrayToString(int[] array) {
        String strRet="";
        for(int i : array) {
            strRet+=Integer.toString(i);
        }
        return strRet;
    }

    //=================================================

    private void setBtn(String txt, boolean recording) {
        Button btn = (Button) findViewById(R.id.scan_btn);
        btn.setText(txt);
        if (recording) {
            btn.setTextColor(Color.RED);
        } else {
            btn.setTextColor(Color.BLACK);
        }
    }

    private void setMsg(String msg, boolean errMsg) {
        TextView msgBanner = (TextView) findViewById(R.id.message_banner);
        if (errMsg) {
            msgBanner.setTextColor(Color.RED);
        } else {
            msgBanner.setTextColor(Color.BLACK);
        }
        msgBanner.setText(msg);
    }

    //=================================================

    // From http://android1plot.com/docs/a-simple-xy-plot/
    public void drawDebugPlot(double[] data) {
        // initialize our XYPlot reference:
        XYPlot mySimpleXYPlot = (XYPlot) findViewById(R.id.plot_1);
        mySimpleXYPlot.clear();

        // Turn the above arrays into XYSeries':
        Double[] doubleArray = ArrayUtils.toObject(data);
        List<Double> vals = Arrays.asList(doubleArray);
        XYSeries series = new SimpleXYSeries(
                vals,          // SimpleXYSeries takes a List so turn our array into a List
                SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, // Y_VALS_ONLY means use the element index as the x value
                "Debug");                             // Set the display title of the series

        // Create a formatter to use for drawing a series using LineAndPointRenderer:
        LineAndPointFormatter seriesFormat = new LineAndPointFormatter(
                Color.rgb(0, 200, 0),                   // line color
                null,                                   // point color
                null,                                   // fill color (none)
                null);                                   // text color

        // add a new series' to the xyplot:
        mySimpleXYPlot.addSeries(series, seriesFormat);

        // reduce the number of range labels
        mySimpleXYPlot.setTicksPerRangeLabel(3);
    }
}
