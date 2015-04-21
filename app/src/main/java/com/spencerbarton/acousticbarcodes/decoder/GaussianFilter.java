package com.spencerbarton.acousticbarcodes.decoder;

import org.apache.commons.math3.util.FastMath;

/**
 * Created by Spencer on 4/20/2015.
 */
public class GaussianFilter {

    private double[] mFilter;

    public GaussianFilter(int fltLen, double fltSigma) {
        mFilter = initGaussian(fltLen, fltSigma);
    }

    private double[] initGaussian(int len, double sigma) {
        double[] filter = new double[len];
        double x;
        double a = 1 / (2 * sigma * sigma);
        double b = 1 / (sigma * FastMath.sqrt(2*Math.PI));
        double sum = 0;
        for (int i = 0; i < filter.length; i++) {
            x = -((double)len - 1) / 2 + i;
            filter[i] = b * Math.exp(-x*x*a);
            sum += filter[i];
        }

        // Normalize
        for (int i = 0; i < filter.length; i++) {
            filter[i] = filter[i] / sum;
        }

        return filter;
    }

    public double[] filter(double[] data) {
        double[] result = new double[data.length];

        // Circular convolution
        double sum;
        int ind;
        for (int t = 0; t < data.length; t++) {
            sum = 0;
            for (int k = 0; k < mFilter.length; k++) {
                ind = Math.abs(t - k) % data.length;
                sum += mFilter[k] * data[ind];
            }
            result[t] = sum;
        }
        return result;
    }
}
