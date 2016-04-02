package com.luzi82.screenstream.screenstream;

import android.app.Activity;
import android.content.Context;
import android.hardware.display.VirtualDisplay;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Surface;
import android.view.WindowManager;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.Writer;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Created by luzi82 on 16年3月30日.
 */
public class ScreenStream {

    private static final String TAG = ActivityRuntime.TAG;

    int bitRate;
    int width;
    int height;
    int iFrameInterval;

    WeakReference<ServerRuntime> parentReference;

    PipedInputStream pipedInputStream;
    PipedOutputStream pipedOutputStream;

    ScheduledExecutorService scheduledExecutorService;
    MediaProjection mediaProjection;
    VirtualDisplay virtualDisplay;

    //    Writer fout;
    byte[] buf = new byte[1000000];

    public ScreenStream(ServerRuntime parentReference, int bitRate, int width, int height, int iFrameInterval) {
        this.parentReference = new WeakReference<ServerRuntime>(parentReference);
        this.bitRate = bitRate;
        this.width = width;
        this.height = height;
        this.iFrameInterval = iFrameInterval;

        this.scheduledExecutorService = parentReference.scheduledExecutorService;
        this.buf = new byte[bitRate];

//        try {
//            FileOutputStream fos = new FileOutputStream("/sdcard/v.out");
//            BufferedOutputStream bos = new BufferedOutputStream(fos);
//            OutputStreamWriter osw = new OutputStreamWriter(bos);
//            fout = osw;
//        }catch(Throwable t){
//            throw new Error(t);
//        }
    }

    public void start() throws IOException {
        ServerRuntime parent = getParent();
        ServiceRuntime serviceRuntime = parent.getParent();
        MainService mainService = serviceRuntime.getMainService();

        pipedInputStream = new PipedInputStream();
        pipedOutputStream = new PipedOutputStream(pipedInputStream);

        MediaProjectionManager mediaProjectionManager = (MediaProjectionManager) mainService.getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        mediaProjection = mediaProjectionManager.getMediaProjection(Activity.RESULT_OK, serviceRuntime.permissionIntent);
        if (mediaProjection == null) {
            throw new NullPointerException();
        }

        prepareVideoEncoder();

        final DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) mainService.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getRealMetrics(displayMetrics);

        int screenWidth = width;
        int screenHeight = height;
        int screenDensity = displayMetrics.densityDpi;
        virtualDisplay = mediaProjection.createVirtualDisplay("Recording Display", screenWidth,
                screenHeight, screenDensity, 0 /* flags */, mInputSurface,
                null /* callback */, null /* handler */);

        scheduledExecutorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    while (drainEncoder()) ;
                } catch (Throwable t) {
                    t.printStackTrace();
                }
                releaseEncoders();
            }
        });
    }

    // in the same activity
    public static final String VIDEO_MIME_TYPE = MediaFormat.MIMETYPE_VIDEO_AVC;
    // …
//    private boolean mMuxerStarted = false;
//    private MediaProjection mMediaProjection;
    private Surface mInputSurface;
    //    private MediaMuxer mMuxer;
    private MediaCodec mVideoEncoder;
    private MediaCodec.BufferInfo mVideoBufferInfo;
//    private int mTrackIndex = -1;


    private void prepareVideoEncoder() {
        mVideoBufferInfo = new MediaCodec.BufferInfo();

        MediaFormat format = MediaFormat.createVideoFormat(VIDEO_MIME_TYPE, width, height);
        int frameRate = 30;

        // Set some required properties. The media codec may fail if these aren't defined.
        format.setInteger(MediaFormat.KEY_COLOR_FORMAT,
                MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
        format.setInteger(MediaFormat.KEY_BIT_RATE, bitRate); // 6Mbps
        format.setInteger(MediaFormat.KEY_FRAME_RATE, frameRate);
        format.setInteger(MediaFormat.KEY_CAPTURE_RATE, frameRate);
        format.setInteger(MediaFormat.KEY_REPEAT_PREVIOUS_FRAME_AFTER, 1000 / frameRate);
        format.setInteger(MediaFormat.KEY_CHANNEL_COUNT, 1);
        format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, iFrameInterval);

        // Create a MediaCodec encoder and configure it. Get a Surface we can use for recording into.
        try {
            mVideoEncoder = MediaCodec.createEncoderByType(VIDEO_MIME_TYPE);
            mVideoEncoder.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
            mInputSurface = mVideoEncoder.createInputSurface();
            mVideoEncoder.start();
        } catch (IOException e) {
            e.printStackTrace();
            releaseEncoders();
        }
    }

    long last = 0;

//    int limit = 1000;

    private boolean drainEncoder() throws IOException {
        int bufferIndex = mVideoEncoder.dequeueOutputBuffer(mVideoBufferInfo, 500000);

//        Log.d(TAG, String.format("XMKQFPAZ drainEncoder %d", bufferIndex));

        if (bufferIndex == MediaCodec.INFO_TRY_AGAIN_LATER) {
            // nothing available yet
            return true;
        } else if (bufferIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
        } else if (bufferIndex < 0) {
            // not sure what's going on, ignore it
        } else {
            ByteBuffer encodedData = mVideoEncoder.getOutputBuffer(bufferIndex);
            if (encodedData == null) {

                throw new RuntimeException("couldn't fetch buffer at index " + bufferIndex);
            }


//            if ((mVideoBufferInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) {
//                mVideoBufferInfo.size = 0;
//            }

            if (mVideoBufferInfo.size != 0) {
//                    if (mMuxerStarted) {
                encodedData.position(mVideoBufferInfo.offset);
                encodedData.limit(mVideoBufferInfo.offset + mVideoBufferInfo.size);

//                    if(fout!=null) {
//                        encodedData.get(buf,0,mVideoBufferInfo.size);
//                        String b64 = Base64.encodeToString(buf, 0, mVideoBufferInfo.size, Base64.NO_WRAP);
//                        fout.write(b64);
//                        fout.write("\n");
//                        fout.flush();
//                    }
                encodedData.get(buf, 0, mVideoBufferInfo.size);
                pipedOutputStream.write(buf, 0, mVideoBufferInfo.size);

                long now = System.currentTimeMillis();
                String t = "           " + (now - last);
                t = t.substring(t.length() - 8);
                String s = "           " + mVideoBufferInfo.size;
                s = s.substring(s.length() - 8);
                Log.d(TAG, String.format("DTLVLDDF %s %s %d %d\n", t, s, pipedInputStream.available(), mVideoBufferInfo.flags));
                last = now;

//                        mMuxer.writeSampleData(mTrackIndex, encodedData, mVideoBufferInfo);
//                    } else {
//                        // muxer not started
//                    }
            }

            mVideoEncoder.releaseOutputBuffer(bufferIndex, false);


            if ((mVideoBufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                return false;
            }

        }

        return true;
    }


    private void releaseEncoders() {
        Log.d(TAG, "ZKYOQNTE releaseEncoders START");
//        mDrainHandler.removeCallbacks(mDrainEncoderRunnable);
        if (virtualDisplay != null) {
            virtualDisplay.release();
            virtualDisplay = null;
        }
        if (mVideoEncoder != null) {
            mVideoEncoder.stop();
        }
        if (mInputSurface != null) {
            mInputSurface.release();
            mInputSurface = null;
        }
        if (mVideoEncoder != null) {
            mVideoEncoder.release();
            mVideoEncoder = null;
        }
        if (mediaProjection != null) {
            mediaProjection.stop();
            mediaProjection = null;
        }
        mVideoBufferInfo = null;
//        mDrainEncoderRunnable = null;
//        mTrackIndex = -1;
        Log.d(TAG, "AYLSFTXN releaseEncoders END");
    }

    private ServerRuntime getParent() {
        ServerRuntime ret = parentReference.get();
        if (ret == null) {
            throw new NullPointerException();
        }
        return ret;
    }

}
