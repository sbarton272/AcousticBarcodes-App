package com.spencerbarton.acousticbarcodes.decoder;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.musicg.wave.Wave;
import com.spencerbarton.acousticbarcodes.MainActivity;
import com.spencerbarton.acousticbarcodes.R;

import org.apache.commons.math3.stat.StatUtils;

import java.io.Console;
import java.io.File;
import java.util.Arrays;

/**
 * Contains acoustic barcode algorithm
 * 
 * TODO consts obj
 * TODO different decoders
 * TODO settings control consts
 * TODO not save file
 * TODO Save to local storage temp
 * TODO another thread
 * TODO more robust decoder
 * TODO mic settings
 * TODO better unit len detection
 * TODO removed dont add dist if not an echo
 * TODO better numbers on where fails (print to screen)
 *
 * Created by Spencer on 3/22/2015.
 */
public class AcousticBarcodeDecoder {

    private static final String TAG = "AcousticBarcodeDecoder";

    // Consts
    private static final double ENCODING_UNIT_LEN_ONE = 1;
    private static final double ENCODING_UNIT_LEN_ZERO = 1.8;
    private static final int FLT_LEN = 10;
    private static final double FLT_SIGMA = 4;
    private static final int VIZ_BUFFER = 40;

    private final int[] mStartCode;
    private final int[] mStopCode;
    private int mCodeLen;

    // Components
    private final Transform mTransform;
    private final GaussianFilter mFilter;
    private final TransientDetector mTransientDetector;
    private final OnesDecoder mDecoder;
    private final ErrorChecker mErrorChecker;
    private final MainActivity mActivity;

    public AcousticBarcodeDecoder(MainActivity activity, int codeLen, int[] startBits, int[] stopBits) {
        mCodeLen = codeLen;
        mStartCode = startBits;
        mStopCode = stopBits;
        mActivity = activity;
        mTransform = new Transform();
        mFilter = new GaussianFilter(FLT_LEN, FLT_SIGMA);
        mTransientDetector = new TransientDetector();
        mDecoder = new OnesDecoder(ENCODING_UNIT_LEN_ONE, ENCODING_UNIT_LEN_ZERO);
        mErrorChecker = new ErrorChecker(codeLen, startBits, stopBits);
    }

    public int[] decode(File file) {
        Wave recording = new Wave(file.getAbsolutePath());
        Log.i(TAG, recording.toString());

        // Transform
        double[] data = mTransform.transform(recording);

        // Filter
        double[] fltData = mFilter.filter(data);

        // Transient Detection
        int[] transientLocs = mTransientDetector.detect(fltData);
        String msg = "Transient Detector(" + transientLocs.length + ") " + Arrays.toString(transientLocs);
        Log.i(TAG, msg);
        mActivity.setDebugText(msg, 1);

        // TODO add debug
        plotTrans(fltData, transientLocs);

        if (mErrorChecker.checkTransients(transientLocs)) {
            mActivity.setDebugText("Not enough transients needed " + mCodeLen, 2);
            return null;
        }

        // Decoding
        int[] code = mDecoder.decode(transientLocs);
        msg = "Decoder " + Arrays.toString(code);
        Log.i(TAG, msg);
        mActivity.setDebugText(msg, 2);

        // TODO add debug
        mActivity.drawDebugPlot(mDecoder.getInterOnsetDelays(), 2);

        // TODO add debug
        mActivity.drawDebugPlot(mDecoder.getUnitLenAvg(), 3);

        mActivity.setDebugText("Settings CodeLen:" + mCodeLen + " StartCode " + mStartCode + " StopCode " + mStopCode, 3);

        // Error Detection
        if (mErrorChecker.checkCode(code)) {
            mActivity.setDebugText("Error at final stage", 3);
        	return null;
        }

        return code;
    }

    private void plotTrans(double[] data, int[] vals) {
        if (vals != null && vals.length > 0) {
            double[] interestingData = Arrays.copyOfRange(data, vals[0] - VIZ_BUFFER,
                    vals[vals.length - 1] + VIZ_BUFFER);
            mActivity.drawDebugPlot(interestingData, 1);
        }
    }

}
