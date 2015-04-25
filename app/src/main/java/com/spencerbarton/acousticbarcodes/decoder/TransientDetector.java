package com.spencerbarton.acousticbarcodes.decoder;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.util.FastMath;

/**
 * Created by Spencer on 4/20/2015.
 */
public class TransientDetector {
    private static final String TAG = "TransientDetector";

    private static final int FLT_LEN = 8;
    private static final double FLT_SIGMA = 2;
	private static final double PEAK_SCALE = 0.5;
	private static final double THRESH_SCALE = 1.0;

    private final GaussianFilter mGaussFlt = new GaussianFilter(FLT_LEN, FLT_SIGMA);;

    public int[] detect(double[] data) {

        // Lowpass filter
        double[] fltData = mGaussFlt.filter(data);
        
        // Find transient peaks
        int[] peaks = findPeaks(fltData);
        
        Log.i(TAG, "Peaks(" + peaks.length + ") " + Arrays.toString(peaks));
        
        return peaks;
    }

    private int[] findPeaks(double[] data) {

    	// Setup
    	List<Integer> peakLocs = new ArrayList<>();
    	boolean isRising = true;
    	double lastMax = 0;
    	double lastMin = 0;
    	int lastMaxI = 0;
    	double[] peaks = new double[data.length];
    	
    	double mean = StatUtils.mean(data);
    	double std = FastMath.sqrt(StatUtils.variance(data));
    	double thresh = mean + std;
    	System.out.println("Peak mean " + mean + " Peak std " + std);
    	
    	// Iter through and find valid max
    	double val, nextVal, prominence,curMin;
    	for (int i = 0; i < data.length-1; i++) {
    		val = data[i];
    		nextVal = data[i+1];
    		
    		// Set default to no peak
    		peaks[i] = 0;
    		
    		if (isRising && (val > nextVal)) {
    			
    			// Cur val is greater then next so not rising
    			isRising = false;
    			lastMaxI = i;
    			lastMax = val;
    			
    		} else if (!isRising && (val < nextVal)) {
    			
    			// Wasn't rising and next val is larger so look at difference
    			isRising = true;
    			
    			// Prominence checks that peak larger than both surrounding mins 
    			curMin = val;
    			prominence = lastMax - Math.max(lastMin, curMin);
    			
    			// Prominence much be large enough as well as the peak must be larger than the mean
    			if ((lastMax > thresh*THRESH_SCALE) && (prominence > lastMax*PEAK_SCALE)) {
    				peaks[lastMaxI] = 1;
    				peakLocs.add(lastMaxI);
    			}
    			
    			// Now in min before next peak
    			lastMin = curMin;
    		}
    	}
    	
        Integer[] vals = peakLocs.toArray(new Integer[peakLocs.size()]);
        return ArrayUtils.toPrimitive(vals);
    }


}
