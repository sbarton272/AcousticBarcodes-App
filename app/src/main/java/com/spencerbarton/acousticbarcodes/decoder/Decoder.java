package com.spencerbarton.acousticbarcodes.decoder;

import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.lang3.ArrayUtils;

public class Decoder {

	// Consts
	private static final int NO_UNIT_LEN_FOUND = -1;
	private static final Integer ONE = 1;
	private static final Integer ZERO = 0;
	private static final double PROPORTION_THRESH = 1.5;
	private static final double[] UNIT_LEN_W = {.2, .8};
	
    // Params
	private double mUnitLenOne;
	private double mUnitLenZero;
    private int[] mStartBits = {1, 1};
    private int[] mStopBits;
    
    private double[] mUnitLenAvg;
    private ArrayList<Integer> mDecoded;
    
	public Decoder(double unitLenOne, double unitLenZero, int[] startBits, int[] stopBits) {
		mUnitLenOne = unitLenOne;
		mUnitLenZero = unitLenZero;
        mStopBits = stopBits;
        mDecoded = new ArrayList<>();
    }

	public int[] decode(int[] transientLocs) {
		
		// TODO interonset delays member var
		
		mDecoded.clear();
		int[] interOnsetDelays = differences(transientLocs);
		
		mUnitLenAvg = new double[interOnsetDelays.length];
				
		// TODO handle reverse
		int curIndx = findUnitLen(interOnsetDelays);
		if (curIndx == NO_UNIT_LEN_FOUND) {
			return null;
		}
		
		decodeRemainder(interOnsetDelays, curIndx);
		
		Integer[] decoded = mDecoded.toArray(new Integer[mDecoded.size()]);
		return ArrayUtils.toPrimitive(decoded);
	}

	private void decodeRemainder(int[] interOnsetDelays, int curIndx) {
		int addPrevDelay = 0;
		int curDelay;
		double oneLen, zeroLen, oneDist, zeroDist, curUnitLen;
		
		// Start with unit len from decoded start bits
		double unitLen = mUnitLenAvg[curIndx - 1];
				
		// Iterate through remaining delays and decode
		for (int i = curIndx; i < interOnsetDelays.length; i++) {
			curDelay = interOnsetDelays[i] + addPrevDelay;
			
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
				// TODO ignore if too large, only add smaller
				addPrevDelay = curDelay;
				continue;
			}
			
			// Update unit len
			unitLen = calcUnitLen(unitLen, curUnitLen);
			mUnitLenAvg[i] = unitLen;
		}		
	}

	private int findUnitLen(int[] interOnsetDelays) {
		// Search for start bits to get unit len
		
		int curDelay, nextDelay;
		double unitLen;
		for (int i = 0; i < interOnsetDelays.length-1; i++) {
			curDelay = interOnsetDelays[i];
			nextDelay = interOnsetDelays[i+1];
			
			// Once find [1,1] start code, save and return
			// Note assumes will find at the beginning
			if (isWithinThreshold(curDelay, nextDelay)) {
				unitLen = calcUnitLen(curDelay, nextDelay);
				mUnitLenAvg[i] = curDelay;
				mUnitLenAvg[i+1] = unitLen;
				for (int b : mStartBits) { mDecoded.add(b); }
				
				// Return next index
				return i+2;
			}
		}
		
		return NO_UNIT_LEN_FOUND;
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
