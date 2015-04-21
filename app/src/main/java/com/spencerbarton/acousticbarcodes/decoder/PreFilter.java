package com.spencerbarton.acousticbarcodes.decoder;

import android.util.Log;

import com.musicg.wave.Wave;
import com.musicg.wave.extension.Spectrogram;

import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.util.FastMath;

/**
 * Created by Spencer on 4/6/2015.
 */
public class PreFilter {

    private static final String TAG = "PreFilter";

    private static final int FFT_SZ = 512;
    private static final int OVERLAP_FACTOR = 2;
    private boolean mNormalize = true;

    public double[] filter(Wave recording) {

        // Spectrogram
        Spectrogram spectrogram = recording.getSpectrogram(FFT_SZ, OVERLAP_FACTOR);
        double[][] data = spectrogram.getAbsoluteSpectrogramData();

        Log.i(TAG, "Spec " + spectrogram.getNumFrames() + " " + data.length + " " + data[0].length);

        // Normalize to unit normal in each freq
        if (mNormalize) {
            data = normalizeSpec(data);
        }

        // Sum freq bins
        double[] out = sumSpectrum(data);

        return out;
    }

    private double[] sumSpectrum(double[][] data) {
        double[] summedSpectrum = new double[data.length];
        for (int i = 0; i < data.length; i++) {
            summedSpectrum[i] = StatUtils.sum(data[i]);
        }
        return summedSpectrum;
    }

    private double[][] normalizeSpec(double[][] data) {

        // Zero mean unit var for all spectrum
        double[] spectrum;
        for (int i = 0; i < data.length; i++) {
            spectrum = data[i];
            double mean = StatUtils.mean(spectrum);
            double std = FastMath.sqrt(StatUtils.variance(spectrum));
            for (int j = 0; j < spectrum.length; j++) {
                data[i][j] = (data[i][j] - mean) / std;
                Log.i(TAG, "Val: " + spectrum[j] + " to " + data[i][j]);
            }
        }
        return data;
    }
}
