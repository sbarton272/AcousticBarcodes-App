package com.spencerbarton.acousticbarcodes.decoder;

import android.util.Log;

import com.musicg.wave.Wave;
import com.musicg.wave.extension.Spectrogram;

import org.apache.commons.math3.stat.StatUtils;

/**
 * Created by Spencer on 4/6/2015.
 */
public class Transform {

    private static final String TAG = "Transform";

    private static final int LOWEST_FREQ = 6;
    private static final int FFT_SZ = 64;
    private static final int OVERLAP_FACTOR = 32;
    private static final double MIN_SPEC_VAL = 0;
    private boolean mNormalize = false;

    public double[] transform(Wave recording) {

        // Spectrogram
        Spectrogram spectrogram = recording.getSpectrogram(FFT_SZ, OVERLAP_FACTOR);
        double[][] data = spectrogram.getAbsoluteSpectrogramData();

        double r = (double)data.length / recording.size();
        Log.i(TAG, "Spec samples " + data.length + " freq " + data[0].length + " ratio " + r);
        
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
        int numFreq;
        for (int i = 0; i < data.length; i++) {
        	// Sum only upper so many frequencies
        	numFreq = data[i].length - lowestFreq;
            summedSpectrum[i] = StatUtils.sum(data[i], lowestFreq, numFreq);
            // Value floor
            summedSpectrum[i] = Math.max(summedSpectrum[i], MIN_SPEC_VAL);
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
            double max = StatUtils.max(spectrum);

            // Apply stats
            for (int time = 0; time < spectrum.length; time++) {
                data[time][freq] = data[time][freq] / max;
            }
        }
        return data;
    }
}
