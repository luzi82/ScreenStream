package com.luzi82.screenstream.screenstream;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * Created by luzi82 on 16年3月28日.
 */
public class ActivityRuntime {

    public static final String TAG = "SSZKPBPH";

    WeakReference<MainActivity> mainActivityWeakReference;
//    ScheduledExecutorService scheduledExecutorService;
    SharedPreferences sharedPreferences;
//    HttpServer httpServer;
    SharedPreferences.OnSharedPreferenceChangeListener onSharedPreferenceChangeListener;

    public ActivityRuntime(MainActivity mainActivity) {
        this.mainActivityWeakReference = new WeakReference<MainActivity>(mainActivity);
//        this.scheduledExecutorService = new ScheduledThreadPoolExecutor(4);
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mainActivity);

//        Preference preference=mainActivity.findPreference()

        onSharedPreferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                try {
                    Log.d(TAG, key);
                    if (key.equals("socket_enable")) {
                        boolean enable = sharedPreferences.getBoolean(key, false);
                        if (enable) {
                            startServer();
                        } else {
                            stopServer();
                        }
                    } else if (key.equals("socket_port")) {
                        stopServer();
                        startServer();
                    }
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        };
        sharedPreferences.registerOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener);
    }

    public static int getPreferencePort(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return getPreferencePort(sharedPreferences);
    }

    public static int getPreferencePort(SharedPreferences sharedPreferences) {
        String pre = sharedPreferences.getString("socket_port", "8888");
        int ret = 8888;
        try {
            ret = Integer.parseInt(pre);
        } catch (Throwable t) {
        }
        return ret;
    }

    private synchronized void startServer() throws IOException {
//        if(httpServer !=null){
//            httpServer.stop();
//            httpServer =null;
//        }
//        try {
//            int port = getPreferencePort();
//            Log.d(TAG,String.format("YSTTXLDI %d",port));
////            httpServer = new HttpServer(port, this);
//            httpServer.start();
//        }catch(IOException e){
//            httpServer.stop();
//            httpServer =null;
//            e.printStackTrace();
//        }
        getMainActivity().serviceConn.mainService.serviceRuntime.start();
    }

    private synchronized void stopServer() {
        getMainActivity().serviceConn.mainService.serviceRuntime.stop();
    }

    public MainActivity getMainActivity(){
        MainActivity ret = mainActivityWeakReference.get();
        if(ret==null){
            throw new NullPointerException();
        }
        return ret;
    }

    public void release() {
//        stopServer();
//        if (scheduledExecutorService != null) {
//            scheduledExecutorService.shutdown();
//            scheduledExecutorService = null;
//        }
        if (sharedPreferences != null) {
            sharedPreferences.unregisterOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener);
            sharedPreferences = null;
        }
        onSharedPreferenceChangeListener=null;
        mainActivityWeakReference=null;
    }
}
