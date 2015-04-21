package com.spencerbarton.acousticbarcodes;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.spencerbarton.acousticbarcodes.decoder.AcousticBarcodeDecoder;

import java.io.File;

// TODO audio recording
// TODO decode
// TODO hit btn to start, hit btn to stop
public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";

    private static final int BARCODE_LEN = 8;
    private static final int[] BARCODE_START_BITS = {1,1};
    private static final int[] BARCODE_STOP_BITS = {1,0};

    private AcousticBarcodeDecoder mDecoder;
    private AudioRecorder mRecorder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDecoder = new AcousticBarcodeDecoder(this, BARCODE_LEN, BARCODE_START_BITS, BARCODE_STOP_BITS);
        mRecorder = new AudioRecorder(this);

        addScanBtn();
    }

    private void addScanBtn() {
        Button scanBtn = (Button) findViewById(R.id.scan_btn);
        scanBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startScan();
                        return true;
                    case MotionEvent.ACTION_UP:
                        stopScan();
                        return true;
                }
                return false;
            }
        });
    }

    private void startScan() {
        mRecorder.startRecording();
        setMsg(getString(R.string.scan_btn_recording));
    }

    private void stopScan() {
        File recording = mRecorder.stopRecording();
        int[] decoded = mDecoder.decode(recording);
        setMsg("Decoded " + intArrayToString(decoded) + "\n" + getString(R.string.scan_btn_done)
                + recording.getName());
    }

    public void onSettingsClick(View view) {
        Log.i(TAG, "Settings");
    }

    private void setMsg(String msg) {
        TextView msgBanner = (TextView) findViewById(R.id.message_banner);
        msgBanner.setText(msg);
    }

    private String intArrayToString(int[] array) {
        String strRet="";
        for(int i : array) {
            strRet+=Integer.toString(i);
        }
        return strRet;
    }
}
