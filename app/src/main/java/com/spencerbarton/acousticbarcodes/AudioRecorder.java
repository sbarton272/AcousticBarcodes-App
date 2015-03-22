package com.spencerbarton.acousticbarcodes;

import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;

/**
 * Record audio to a time stamped file
 *
 * Created by Spencer on 3/22/2015.
 */
public class AudioRecorder {

    private static final String LOG_TAG = "AudioRecorder";
    private MediaRecorder mRecorder;
    private File mFile;

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
            Log.e(LOG_TAG, "prepare() failed");
        }

        mRecorder.start();
    }

    public File stopRecording() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
        return mFile;
    }

    private File newTimeStampAudio() {
        String fileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        fileName += "/";
        fileName += //TODO
        fileName += ".wav";
        return new File(fileName);
    }

}
