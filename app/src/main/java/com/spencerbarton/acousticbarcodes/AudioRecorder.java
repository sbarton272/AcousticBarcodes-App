package com.spencerbarton.acousticbarcodes;

import android.content.Context;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Record audio to a time stamped file
 *
 * Created by Spencer on 3/22/2015.
 */
public class AudioRecorder {

    private static final String TAG = "AudioRecorder";
    private static final String ROOT_DIR = "AcousticBarcodes";
    private final Context mContext;
    private MediaRecorder mRecorder;
    private File mFile;

    public AudioRecorder(Context context) {
        mContext = context;
    }
    
    public void onPause() {
        if (mRecorder != null) {
            mRecorder.release();
            mRecorder = null;
        }
    }

    public void startRecording() {
        mFile = newTimeStampAudio();
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mFile.getAbsolutePath());
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(TAG, "prepare() failed");
        }

        mRecorder.start();
    }

    public File stopRecording() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
        Log.i(TAG, "Saved " + mFile.getAbsolutePath());
        return mFile;
    }

    private File newTimeStampAudio() {
        // TODO handle if no SD card
        File path = new File(Environment.getExternalStorageDirectory(), ROOT_DIR);
        if (!path.exists() && !path.mkdirs()) {
            // Did not exist and could not create
            path = mContext.getFilesDir();
        }
        Log.i(TAG, "File root:" + path.getAbsolutePath());
        String fileName = File.separator;
        fileName += new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
        fileName += ".wav";
        return new File(path, fileName);
    }

}
