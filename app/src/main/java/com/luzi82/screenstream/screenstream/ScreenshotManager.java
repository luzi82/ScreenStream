package com.luzi82.screenstream.screenstream;

import android.app.Activity;
import android.content.AsyncQueryHandler;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ImageFormat;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by luzi82 on 16年3月28日.
 */
public class ScreenshotManager {

    private static final String TAG = "ScreenshotManager";

    WeakReference<Context> context;

    ScheduledExecutorService scheduledExecutorService;

//    MediaProjectionManager mediaProjectionManager;
    MediaProjection mediaProjection;

    //    SurfaceView surfaceView;
    ImageReader imageReader;
    int surfaceWidth;
    int surfaceHeight;
    //    Allocation allocation;
    Bitmap surfaceBitmap;
    Bitmap compressBitmap;
    Canvas compressBitmapCanvas;
    VirtualDisplay virtualDisplay;

    public ScreenshotManager(Context context, ScheduledExecutorService scheduledExecutorService) {
        this.context = new WeakReference<Context>(context);
        this.scheduledExecutorService = scheduledExecutorService;
    }

    public void start(Intent intent) {
        Log.d(TAG, "GRLWTVXL");
        MediaProjectionManager mediaProjectionManager = (MediaProjectionManager) getContent().getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        mediaProjection = mediaProjectionManager.getMediaProjection(Activity.RESULT_OK, intent);
        start_WVLKEUEN();
    }

    private Context getContent() {
        Context context = this.context.get();
        if(context==null){
            throw new NullPointerException("CTWLLFIN");
        }
        return context;
    }

    boolean dirty = false;

    private void start_WVLKEUEN() {
        scheduledExecutorService.schedule(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.d(TAG, "WVLKEUEN");
                    synchronized (this) {
                        if (imageReader != null)
                            return;
                        Context context1 = context.get();
                        if (context1 == null) {
                            throw new NullPointerException("VVEWUYGY");
                        }
                        Log.d(TAG, "GARWZMNM");
                        final DisplayMetrics displayMetrics = new DisplayMetrics();
                        WindowManager windowManager = (WindowManager) context1.getSystemService(Context.WINDOW_SERVICE);
                        windowManager.getDefaultDisplay().getRealMetrics(displayMetrics);
                        surfaceWidth = displayMetrics.widthPixels;
                        surfaceHeight = displayMetrics.heightPixels;
                        surfaceWidth = 640;
                        surfaceHeight = 360;
                        imageReader = ImageReader.newInstance(surfaceWidth, surfaceHeight, ImageFormat.RGB_565, 8);
                        imageReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {
                            @Override
                            public void onImageAvailable(ImageReader reader) {
                                dirty = true;
                            }
                        },new Handler(Looper.getMainLooper()));
                        Log.d(TAG, String.format("UXIUWWQO %d %d", surfaceWidth, surfaceHeight));
                        virtualDisplay = mediaProjection.createVirtualDisplay("LMIGSPAL", surfaceWidth, surfaceHeight, displayMetrics.densityDpi, DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR, imageReader.getSurface(), null, null);
                        Log.d(TAG, "ZCLCOUMM");
//                        start_UVGXNBSJ();
                    }
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        }, 0, TimeUnit.MILLISECONDS);
    }

//    private void start_UVGXNBSJ() {
//        Log.d(TAG, "UVGXNBSJ 0");
//        scheduledExecutorService.schedule(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    Log.d(TAG, "UVGXNBSJ 1");
//                    long start = System.currentTimeMillis();
//                    Image image = imageReader.acquireLatestImage();
//                    Image.Plane plane = image.getPlanes()[0];
//                    ByteBuffer buffer = plane.getBuffer();
//
//                    if (surfaceBitmap == null) {
//                        int offset = 0;
//                        int pixelStride = plane.getPixelStride();
//                        int rowStride = plane.getRowStride();
//                        surfaceBitmap = Bitmap.createBitmap(rowStride / pixelStride, image.getHeight(), Bitmap.Config.RGB_565);
//                    }
//                    surfaceBitmap.copyPixelsFromBuffer(buffer);
//                    image.close();
//                    Log.d(TAG, String.format("SYRYJWCJ screenshot %d", (int) (System.currentTimeMillis() - start)));
//                    image = null;
//
////                            surfaceView.draw(canvas);
////                    allocation.copyTo(surfaceBitmap);
//                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//                    start = System.currentTimeMillis();
//                    surfaceBitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
//                    Log.d(TAG, String.format("ZSESFVCQ compress %d", (int) (System.currentTimeMillis() - start)));
//                    byteArrayOutputStream.flush();
//                    byte[] outAry = byteArrayOutputStream.toByteArray();
//                    lastImg = outAry;
//                    Log.d(TAG, String.format("HTKZODYM %d", outAry.length));
//                    FileOutputStream fileOutputStream = new FileOutputStream("/sdcard/out.jpg");
//                    fileOutputStream.write(byteArrayOutputStream.toByteArray());
//                    fileOutputStream.flush();
//                    fileOutputStream.close();
//                    byteArrayOutputStream.close();
//                    Log.d(TAG, "GHNGBQRE");
//                } catch (Throwable e) {
//                    e.printStackTrace();
//                }
//            }
//        }, 5000, TimeUnit.MILLISECONDS);
//    }

    int quality = 80;
    int maxSize = 100000;
    int minSize = 90000;
    byte[] lastImg = null;

    public synchronized byte[] getImageBin() throws IOException {
        Log.d(ActivityRuntime.TAG,String.format("AHEIAFHC getImageBin quality=%d",quality));
        if(!dirty)
            return lastImg;
        dirty = false;
        long start;
        start=System.currentTimeMillis();
        if(imageReader==null){
            return null;
        }
        Thread t=Thread.currentThread();
//        int oldP=t.getPriority();
//        t.setPriority(Thread.MAX_PRIORITY);
        Image image = imageReader.acquireLatestImage();
//        t.setPriority(oldP);
        Log.d(ActivityRuntime.TAG,String.format("VSRWQMRZ image %d",System.currentTimeMillis()-start));
        if (image == null) {
            if (lastImg != null) {
                return lastImg;
            } else {
                image = imageReader.acquireNextImage();
            }
        }
        Image.Plane plane = image.getPlanes()[0];
        Log.d(ActivityRuntime.TAG,String.format("UORZSEKI plane %d",System.currentTimeMillis()-start));
        ByteBuffer buffer = plane.getBuffer();
        Log.d(ActivityRuntime.TAG,String.format("IABEWDTB buffer %d",System.currentTimeMillis()-start));
        if (surfaceBitmap == null) {
//            int offset = 0;
            int pixelStride = plane.getPixelStride();
            int rowStride = plane.getRowStride();
            surfaceBitmap = Bitmap.createBitmap(rowStride / pixelStride, image.getHeight(), Bitmap.Config.RGB_565);
        }
        surfaceBitmap.copyPixelsFromBuffer(buffer);
        image.close();
        Log.d(ActivityRuntime.TAG,String.format("ZFSOQFZI copy %d",System.currentTimeMillis()-start));

        if(compressBitmap==null){
            compressBitmap = Bitmap.createBitmap(surfaceWidth,surfaceHeight,Bitmap.Config.RGB_565);
        }
        if(compressBitmapCanvas==null){
            compressBitmapCanvas = new Canvas(compressBitmap);
        }
        start=System.currentTimeMillis();
        compressBitmapCanvas.drawBitmap(surfaceBitmap, 0, 0, null);
//        Log.d(ActivityRuntime.TAG, String.format("LCIQWXXV bitmap %d", System.currentTimeMillis() - start));

        start=System.currentTimeMillis();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//        start = System.currentTimeMillis();
        compressBitmap.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayOutputStream);
        byteArrayOutputStream.flush();
        byte[] outAry = byteArrayOutputStream.toByteArray();
        byteArrayOutputStream.close();
//        Log.d(ActivityRuntime.TAG, String.format("ZDNIYYXB compress %d", System.currentTimeMillis() - start));
        lastImg = outAry;
        if(lastImg.length>maxSize){
            --quality;
        }else if(lastImg.length<minSize){
            ++quality;
        }
        quality=Math.min(95,quality);
        quality=Math.max(10,quality);
        return outAry;
    }

    public void stop() {
        Log.d(ActivityRuntime.TAG, "EDBCBZGC ScreenshotManager.stop START");
        if (virtualDisplay != null) {
            virtualDisplay.release();
        }
        virtualDisplay = null;
        Log.d(ActivityRuntime.TAG, "SUBNBOBV ScreenshotManager.stop mid");
        if (imageReader != null) {
            imageReader.close();
        }
        imageReader = null;
        Log.d(ActivityRuntime.TAG, "FCORHWYZ ScreenshotManager.stop mid");
        if (mediaProjection != null) {
            mediaProjection.stop();
        }
        mediaProjection = null;
        Log.d(ActivityRuntime.TAG, "JDRWEUYG ScreenshotManager.stop END");
    }
}
