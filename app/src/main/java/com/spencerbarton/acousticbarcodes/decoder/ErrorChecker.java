package com.spencerbarton.acousticbarcodes.decoder;

import java.util.Arrays;

public class ErrorChecker {
	
	private final int mCodeLen;
	private final int[] mStartBits;
	private final int[] mStopBits;

	public ErrorChecker(int codeLen, int[] startBits, int[] stopBits) {
		mCodeLen = codeLen;
		mStartBits = startBits;
		mStopBits = stopBits;
	}

    public boolean checkTransients(int[] trans) {
        return trans.length < mCodeLen;
    }

	public boolean checkCode(int[] code) {
		// Returns true if there is an error
		
		if (code.length != mCodeLen) {
			return true;
		}
		
		int[] startBits = Arrays.copyOfRange(code, 0, mStartBits.length);
		int len = mStopBits.length;
		int[] stopBits = Arrays.copyOfRange(code, code.length - len, code.length);
		
		if (!Arrays.equals(startBits, mStartBits)) {
			return true;
		} else if (!Arrays.equals(stopBits, mStopBits)) {
			return true;
		}
		
		return false;
	}
}
