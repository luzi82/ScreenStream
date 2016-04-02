package com.luzi82.screenstream.screenstream;

import android.content.Intent;
import android.util.Log;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * Created by luzi82 on 16年3月28日.
 */
public class ServerRuntime {

    WeakReference<ServiceRuntime> serviceRuntime;
    int port;

    ScheduledExecutorService scheduledExecutorService;
    HttpServer httpServer;
    public ScreenshotManager screenshotManager;

    public ServerRuntime(ServiceRuntime serviceRuntime, int port){
        this.serviceRuntime = new WeakReference<ServiceRuntime>(serviceRuntime);
        this.port = port;

        this.scheduledExecutorService=new ScheduledThreadPoolExecutor(4);
    }

    public int getPort(){
        return port;
    }

    public void start(Intent intent) throws IOException {
        Log.d(ActivityRuntime.TAG, "EHCYWYFH ServerRuntime.start");

        ServiceRuntime serviceRuntime = getParent();
        MainService mainService = serviceRuntime.getMainService();

//        screenshotManager = new ScreenshotManager(mainService,scheduledExecutorService);
//        screenshotManager.start(intent);

        httpServer = new HttpServer(port,this);
        httpServer.start();
    }

    public void stop(){
        Log.d(ActivityRuntime.TAG, "RTZRSGZJ ServerRuntime.stop START");

        if(httpServer!=null) {
            httpServer.stop();
        }
        httpServer = null;

        Log.d(ActivityRuntime.TAG, "QORNQSWD ServerRuntime.stop mid");

        if(screenshotManager!=null) {
            screenshotManager.stop();
        }
        screenshotManager = null;

        Log.d(ActivityRuntime.TAG, "AHODVVTA ServerRuntime.stop mid");

        if(scheduledExecutorService!=null) {
            scheduledExecutorService.shutdown();
        }
        scheduledExecutorService=null;

        Log.d(ActivityRuntime.TAG, "TMCBLWWO ServerRuntime.stop mid");

        serviceRuntime = null;

        Log.d(ActivityRuntime.TAG, "DLBRRNAX ServerRuntime.stop END");
    }

    public ServiceRuntime getParent() {
        ServiceRuntime serviceRuntime = this.serviceRuntime.get();
        if(serviceRuntime==null){
            throw new NullPointerException("HFGSDNMT");
        }
        return serviceRuntime;
    }
}
