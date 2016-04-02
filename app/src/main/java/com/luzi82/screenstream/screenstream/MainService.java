package com.luzi82.screenstream.screenstream;

import android.app.Notification;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by luzi82 on 16年3月28日.
 */
public class MainService extends Service {

    long birth = System.currentTimeMillis();
    public ServiceRuntime serviceRuntime = new ServiceRuntime(this);

    private final IBinder myBinder = new MyBinder();

    @Override
    public IBinder onBind(Intent intent) {
        return myBinder;
    }

    public String helloworld() {
        return "HelloWorld";
    }

    private class MyBinder extends Binder {
        MainService getService() {
            return MainService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(ActivityRuntime.TAG, "IOLQUAXI MainService.onCreate");
    }

    @Override
    public void onDestroy() {
        Log.d(ActivityRuntime.TAG, "FGKTSIGQ MainService.onDestroy");
        super.onDestroy();
    }

    //    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        super.onStartCommand(intent, flags, startId);
//        return START_STICKY;
//    }

    public static class Conn implements ServiceConnection {

        public MainService mainService;

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(ActivityRuntime.TAG, "PTMLDCGR onServiceConnected");
            MyBinder binder = (MyBinder) service;
            mainService = binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(ActivityRuntime.TAG, "GFVXRXRR onServiceDisconnected");
            mainService = null;
        }
    }
}
