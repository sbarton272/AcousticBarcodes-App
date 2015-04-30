package com.spencerbarton.acousticbarcodes.Settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;

import com.spencerbarton.acousticbarcodes.R;

/**
 * Created by Spencer on 4/29/2015.
 */
public class AppParameters {

    private String KEY_DEBUG;
    private String KEY_CODE_LEN;
    private String KEY_START_BITS;
    private String KEY_STOP_BITS;
    private String KEY_SPEC_LOW;
    private String KEY_SPEC_FFT;
    private String KEY_SPEC_OVERLAP;
    private String KEY_FLT_LEN;
    private String KEY_FLT_SIGMA;

    private SharedPreferences mSharedPref;

    public AppParameters(Context context) {
        mSharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        Resources resources = context.getResources();
        KEY_DEBUG = resources.getString(R.string.pref_debug_plots);
        KEY_CODE_LEN = resources.getString(R.string.pref_code_len);
        KEY_START_BITS = resources.getString(R.string.pref_start_bits);
        KEY_STOP_BITS = resources.getString(R.string.pref_stop_bits);
        KEY_SPEC_LOW = resources.getString(R.string.pref_spec_low_freq);
        KEY_SPEC_FFT = resources.getString(R.string.pref_spec_fft_sz);
        KEY_SPEC_OVERLAP = resources.getString(R.string.pref_spec_overlap_factor);
        KEY_FLT_LEN = resources.getString(R.string.pref_flt_len);
        KEY_FLT_SIGMA = resources.getString(R.string.pref_flt_sigma);
    }

    public boolean getDebug() {
        return mSharedPref.getBoolean(KEY_DEBUG,false);
    }

    public int getCodeLen() {
        int len = getIntPref(KEY_CODE_LEN);
        len += getStartBits().length;
        len += getStopBits().length;
        return len;
    }

    public int[] getStartBits() {
        return getBits(mSharedPref.getString(KEY_START_BITS, ""));
    }

    public int[] getStopBits() {
        return getBits(mSharedPref.getString(KEY_STOP_BITS, ""));
    }

    private int[] getBits(String choice) {
        switch (choice) {
            case "1":
                return new int[]{1, 1};
            case "2":
                return new int[]{0, 1};
            default:
                return new int[]{1, 1};
        }
    }

    public int getSpecLow() {
        return getIntPref(KEY_SPEC_LOW);
    }

    public int getSpecFft() {
        return getIntPref(KEY_SPEC_FFT);
    }

    public int getSpecOverlap() {
        return getIntPref(KEY_SPEC_OVERLAP);
    }

    public int getFltLen() {
        return getIntPref(KEY_FLT_LEN);
    }

    public int getFltSigma() {
        return getIntPref(KEY_FLT_SIGMA);
    }

    private int getIntPref(String key) {
        return Integer.parseInt(mSharedPref.getString(key, "1"));
    }
}
