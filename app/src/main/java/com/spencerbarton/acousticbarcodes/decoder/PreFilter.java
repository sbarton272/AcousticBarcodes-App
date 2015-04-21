package com.spencerbarton.acousticbarcodes.decoder;

import android.util.Log;

import com.musicg.wave.Wave;
import com.musicg.wave.extension.Spectrogram;

import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.util.FastMath;

/**
 * Created by Spencer on 4/6/2015.
 */
public class PreFilter {

    private static final String TAG = "PreFilter";

    private static final int LOWEST_FREQ = 10;
    private static final int FFT_SZ = 256;
    private static final int OVERLAP_FACTOR = 2;
    private boolean mNormalize = false;

    public double[] filter(Wave recording) {

        // Spectrogram
        Spectrogram spectrogram = recording.getSpectrogram(FFT_SZ, OVERLAP_FACTOR);
        double[][] data = spectrogram.getAbsoluteSpectrogramData();

        Log.i(TAG, "Spec Len " + spectrogram.getNumFrames() + " Num Frq " + spectrogram.getNumFrequencyUnit());

        // Normalize to unit normal in each freq
        if (mNormalize) {
            data = normalizeSpec(data, LOWEST_FREQ);
        }

        // Sum freq bins
        double[] out = sumSpectrum(data, LOWEST_FREQ);

        return out;
    }

    private double[] sumSpectrum(double[][] data, int lowestFreq) {
        double[] summedSpectrum = new double[data.length];
        for (int i = lowestFreq; i < data.length; i++) {
            summedSpectrum[i] = StatUtils.sum(data[i]);
        }
        return summedSpectrum;
    }

    private double[][] normalizeSpec(double[][] data, int lowestFreq) {

        if (data.length <= 0) {
            return null;
        }

        // Zero mean unit var for all spectrum
        double[] spectrum = new double[data.length];
        int numFreq = data[0].length;
        for (int freq = lowestFreq; freq < numFreq; freq++) {

            // Collect spectrum across time
            for (int time = 0; time < spectrum.length; time++) {
                spectrum[time] = data[time][freq];
            }

            // Stats
            double mean = StatUtils.mean(spectrum);
            double std = FastMath.sqrt(StatUtils.variance(spectrum));

            // Apply stats
            for (int time = 0; time < spectrum.length; time++) {
                data[time][freq] = (data[time][freq] - mean) / std;
            }
        }
        return data;
    }
}
