package com.spencerbarton.acousticbarcodes.decoder;

import android.content.Context;
import android.util.Log;

import com.musicg.wave.Wave;
import com.spencerbarton.acousticbarcodes.R;

import java.io.Console;
import java.io.File;
import java.util.Arrays;

/**
 * Contains acoustic barcode algorithm
 * 
 * TODO consts obj
 * TODO different decoders
 * TODO settings control consts
 * TODO change record btn
 * TODO check audio works
 *
 * Created by Spencer on 3/22/2015.
 */
public class AcousticBarcodeDecoder {

    private static final String TAG = "AcousticBarcodeDecoder";

    // Consts
    private static final double ENCODING_UNIT_LEN_ONE = 1;
    private static final double ENCODING_UNIT_LEN_ZERO = 1.8;

    // Components
    private final Transform mTransform;
    private final TransientDetector mTransientDetector;
    private final Decoder mDecoder;
    private final ErrorChecker mErrorChecker;
    private final Context mContext;

    public AcousticBarcodeDecoder(Context context, int codeLen, int[] startBits, int[] stopBits) {
        mContext = context;
        mTransform = new Transform();
        mTransientDetector = new TransientDetector();
        mDecoder = new Decoder(ENCODING_UNIT_LEN_ONE, ENCODING_UNIT_LEN_ZERO, startBits, stopBits);
        mErrorChecker = new ErrorChecker(codeLen, startBits, stopBits);
    }

    public int[] decode(File file) {
        //Wave recording = new Wave(file.getAbsolutePath());
        Wave recording = new Wave(mContext.getResources().openRawResource(R.raw.test));

        // Prefilter
        double[] data = mTransform.filter(recording);
        
        // Transient Detection
        int[] transientLocs = mTransientDetector.detect(data);
        Log.i(TAG, "Transient Detector " + Arrays.toString(transientLocs));


        if (mErrorChecker.checkTransients(transientLocs)) {
            return null;
        }
        
        // Decoding
        int[] code = mDecoder.decode(transientLocs);
        Log.i(TAG, "Decoder " + Arrays.toString(code));

        // Error Detection
        if (mErrorChecker.checkCode(code)) {
        	return null;
        }

        return code;
    }

}
