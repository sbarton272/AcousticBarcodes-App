package com.spencerbarton.acousticbarcodes;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

// TODO audio recording
// TODO decode
public class MainActivity extends Activity {

    private static final int BARCODE_LEN = 8;
    private static final int[] BARCODE_START_BITS = {1,1};
    private static final int[] BARCODE_STOP_BITS = {1,0};

    private AcousticBarcodeDecoder mDecoder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDecoder = new AcousticBarcodeDecoder(BARCODE_LEN, BARCODE_START_BITS, BARCODE_STOP_BITS);

    }

    public void onScanClick(View view) {


    }

    public void onSettingsClick(View view) {
    }
}
