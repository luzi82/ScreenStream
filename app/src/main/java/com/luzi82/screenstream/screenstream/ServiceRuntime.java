package com.luzi82.screenstream.screenstream;

import android.app.Activity;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.media.projection.MediaProjectionManager;
import android.util.Log;

import java.io.IOException;
import java.lang.ref.WeakReference;

/**
 * Created by luzi82 on 16年3月28日.
 */
public class ServiceRuntime {

    WeakReference<MainService> mainServiceWeakReference;
    ServerRuntime serverRuntime;
    Intent permissionIntent;

    public ServiceRuntime(MainService mainServiceWeakReference) {
        this.mainServiceWeakReference = new WeakReference<MainService>(mainServiceWeakReference);
    }

    boolean preparePermissionIntentBusy = false;

    public synchronized void preparePermissionIntent(MainActivity mainActivity) {
        if (permissionIntent != null)
            return;
        if (preparePermissionIntentBusy)
            return;
        preparePermissionIntentBusy = true;
        MediaProjectionManager mediaProjectionManager = (MediaProjectionManager) getMainService().getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        Intent permissionIntent = mediaProjectionManager.createScreenCaptureIntent();
//        mainActivity.setActivityResultListener(activityResultListener);
        mainActivity.startActivityForResult(permissionIntent, 1);
    }

    public void start() throws IOException {
        Log.d(ActivityRuntime.TAG, "CPBKELXT ServiceRuntime.start");
        if (serverRuntime != null) {
            stop();
        }
        MainService mainService = getMainService();
        if (permissionIntent == null) {
            throw new NullPointerException("LQULXCLZ");
        }

        Notification notification = new Notification.Builder(mainService) //
                .setSmallIcon(R.drawable.ic_notifications_black_24dp) //
                .setContentTitle(mainService.getText(R.string.app_name)) //
                .build();
        mainService.startForeground(2, notification);

        serverRuntime = new ServerRuntime(this, ActivityRuntime.getPreferencePort(mainService));
        serverRuntime.start(permissionIntent);
    }

    public void stop() {
        Log.d(ActivityRuntime.TAG, "CPBKELXT ServiceRuntime.stop");
        if (serverRuntime != null) {
            serverRuntime.stop();
            ;
        }
        serverRuntime = null;

        try {
            MainService mainService = getMainService();
            mainService.stopForeground(true);
        } catch (Throwable t) {
        }
    }

    public MainService getMainService() {
        MainService mainService1 = mainServiceWeakReference.get();
        if (mainService1 == null) {
            throw new NullPointerException("CSFUQWUW");
        }
        return mainService1;
    }

    public synchronized void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            permissionIntent = data;
        }
        preparePermissionIntentBusy = false;
    }

}
