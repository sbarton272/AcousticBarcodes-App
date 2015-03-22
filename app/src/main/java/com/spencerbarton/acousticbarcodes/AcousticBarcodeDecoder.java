package com.spencerbarton.acousticbarcodes;

import java.io.File;

/**
 * Contains acoustic barcode algorithm
 *
 * Created by Spencer on 3/22/2015.
 */
public class AcousticBarcodeDecoder {

    private static final int ENCODING_UNIT_LEN_ONE = 1;
    private static final int ENCODING_UNIT_LEN_ZERO = 2;

    private int mCodeLen;
    private int[] mStartBits;
    private int[] mStopBits;

    public AcousticBarcodeDecoder(int codeLen, int[] startBits, int[] stopBits) {
        mCodeLen = codeLen;
        mStartBits = startBits;
        mStopBits = stopBits;
    }

    public int[] decode(File recording) {
        return null;
    }

}
