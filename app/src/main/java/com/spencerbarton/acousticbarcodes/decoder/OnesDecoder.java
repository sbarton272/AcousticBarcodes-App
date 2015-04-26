package com.spencerbarton.acousticbarcodes.decoder;

import android.util.Log;

import java.util.ArrayList;

import org.apache.commons.lang3.ArrayUtils;

public class OnesDecoder {

	// Consts
    private static final String TAG = "OnesDecoder";
    private static final int NO_UNIT_LEN_FOUND = -1;
	private static final Integer ONE = 1;
	private static final Integer ZERO = 0;
	private static final double PROPORTION_THRESH = 1.5;
	private static final double[] UNIT_LEN_W = {.2, .8};
    private int[] START_BITS = {1, 1};

    // Params
	private double mUnitLenOne;
	private double mUnitLenZero;
    private int[] mIterOnsetDelays;
    
    private double[] mUnitLenAvg;
    private ArrayList<Integer> mDecoded;
    
	public OnesDecoder(double unitLenOne, double unitLenZero) {
		mUnitLenOne = unitLenOne;
		mUnitLenZero = unitLenZero;
        mDecoded = new ArrayList<>();
    }

	public int[] decode(int[] transientLocs) {

		mDecoded.clear();
		mIterOnsetDelays = differences(transientLocs);
		mUnitLenAvg = new double[mIterOnsetDelays.length];
				
		int curIndx = findUnitLen();
		if (curIndx == NO_UNIT_LEN_FOUND) {

            // Try in reverse, note nothing yet added to unit len avg
            Log.i(TAG, "Trying reverse");
            ArrayUtils.reverse(mIterOnsetDelays);
            curIndx = findUnitLen();
            if (curIndx == NO_UNIT_LEN_FOUND) {
                return null;
            }
		}
		
		decodeRemainder(curIndx);
		
		Integer[] decoded = mDecoded.toArray(new Integer[mDecoded.size()]);
		return ArrayUtils.toPrimitive(decoded);
	}

    //------------------------------------------------------


    private int findUnitLen() {
        // Search for start bits to get unit len

        int curDelay, nextDelay;
        double unitLen;

        // Search for start bits up until end bits location
        int maxSearchLoc = mIterOnsetDelays.length - START_BITS.length - 1;
        for (int i = 0; i < maxSearchLoc; i++) {
            curDelay = mIterOnsetDelays[i];
            nextDelay = mIterOnsetDelays[i+1];

            // Once find [1,1] start code, save and return
            // Note assumes will find at the beginning
            if (isWithinThreshold(curDelay, nextDelay)) {
                unitLen = calcUnitLen(curDelay, nextDelay);
                mUnitLenAvg[i] = curDelay;
                mUnitLenAvg[i+1] = unitLen;
                for (int b : START_BITS) { mDecoded.add(b); }

                // Return next index
                return i+2;
            }
        }

        return NO_UNIT_LEN_FOUND;
    }

	private void decodeRemainder(int curIndx) {
		int addPrevDelay = 0;
		int curDelay;
		double oneLen, zeroLen, oneDist, zeroDist, curUnitLen;
		
		// Start with unit len from decoded start bits
		double unitLen = mUnitLenAvg[curIndx - 1];
				
		// Iterate through remaining delays and decode
		for (int i = curIndx; i < mIterOnsetDelays.length; i++) {
			curDelay = mIterOnsetDelays[i] + addPrevDelay;
			
			// Don't add to next delay unless not decoded  
			addPrevDelay = 0;
			
			oneLen = mUnitLenOne*unitLen;
			zeroLen = mUnitLenZero*unitLen;
			
			// Determine if valid one or zero dist
			oneDist = Math.abs(oneLen - curDelay);
			zeroDist = Math.abs(zeroLen - curDelay);
			
			if ((oneDist < zeroDist) && isWithinThreshold(oneLen, curDelay)) {
				
				// Decoded a 1
				mDecoded.add(ONE);
				curUnitLen = curDelay / mUnitLenOne;
				
			} else if (isWithinThreshold(zeroLen, curDelay)) {
				
				// Decoded a 0
				mDecoded.add(ZERO);
				curUnitLen = curDelay / mUnitLenZero;
				
			} else {
				
				// Did not detect a valid delay, so assume echo and
				//  add to next delay instead of counting
				//  Ignore if too large, only add smaller
                if (curDelay < oneDist) {
                    addPrevDelay = curDelay;
                }
                continue;
			}
			
			// Update unit len
			unitLen = calcUnitLen(unitLen, curUnitLen);
			mUnitLenAvg[i] = unitLen;
		}		
	}

	private int[] differences(int[] transientLocs) {
		int[] diffs = new int[transientLocs.length-1];
		for (int i = 0; i < diffs.length; i++) {
			diffs[i] = transientLocs[i+1] - transientLocs[i];
		}
		return diffs;
	}
	
	private double calcUnitLen(double prevUnitLen, double curUnitLen) {
		return UNIT_LEN_W[0]*prevUnitLen + UNIT_LEN_W[1]*curUnitLen;
	}

	private boolean isWithinThreshold(double x, double y) {
		double d = x / y;
		if ((d < PROPORTION_THRESH) && ((1 / d) < PROPORTION_THRESH)) {
			return true;
		}
		return false;
	}
	
}
