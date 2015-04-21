package com.spencerbarton.acousticbarcodes.decoder;

import android.content.Context;
import android.util.Log;

import com.musicg.wave.Wave;
import com.spencerbarton.acousticbarcodes.MainActivity;
import com.spencerbarton.acousticbarcodes.R;

import java.io.File;

/**
 * Contains acoustic barcode algorithm
 *
 * Created by Spencer on 3/22/2015.
 */
public class AcousticBarcodeDecoder {

    private static final String TAG = "AcousticBarcodeDecoder";

    // Consts
    private static final int ENCODING_UNIT_LEN_ONE = 1;
    private static final int ENCODING_UNIT_LEN_ZERO = 2;
    private final Context mContext;

    // Params
    private int mCodeLen;
    private int[] mStartBits;
    private int[] mStopBits;

    // Components
    private final PreFilter mPreFilter;
    private final TransientDetector mTransientDetector;

    public AcousticBarcodeDecoder(Context context, int codeLen, int[] startBits, int[] stopBits) {
        mContext = context;
        mCodeLen = codeLen;
        mStartBits = startBits;
        mStopBits = stopBits;
        mPreFilter = new PreFilter();
        mTransientDetector = new TransientDetector();
    }

    public int[] decode(File file) {
        Wave recording = new Wave(file.getAbsolutePath());
        Log.i(TAG, recording.toString());

        // TODO debug
        recording = new Wave(mContext.getResources().openRawResource(R.raw.test));
        Log.i(TAG, recording.toString());

        // Prefilter
        double[] data = mPreFilter.filter(recording);

        // Transient Detection
        double[] transientLocs = mTransientDetector.detect(data);
        ((MainActivity)mContext).drawDebugPlot(transientLocs);

        // Decoding

        // Error Detection

        return mStartBits;
    }

}
