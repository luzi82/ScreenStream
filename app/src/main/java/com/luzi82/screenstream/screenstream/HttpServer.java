package com.luzi82.screenstream.screenstream;

import android.util.Log;

import java.io.ByteArrayInputStream;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

/**
 * Created by luzi82 on 16年3月28日.
 */
public class HttpServer extends NanoHTTPD {

    ServerRuntime serverRuntime;
//    ScreenStream screenStream;

    public HttpServer(int port, ServerRuntime serverRuntime) {
        super(port);
        Log.d(ActivityRuntime.TAG,"IEMDWIGG HttpServer.new");
        this.serverRuntime = serverRuntime;
    }

    @Override
    public Response serve(IHTTPSession session) {
        try {
            Log.d(ActivityRuntime.TAG,"CDIJJNOH HttpServer.serve START");
//            long start = System.currentTimeMillis();
//            byte[] imageBin = serverRuntime.screenshotManager.getImageBin();
//            if(imageBin==null){
//                throw new NullPointerException("SRVAPNLE");
//            }
//            int binLen = imageBin.length;
//            Log.d(ActivityRuntime.TAG,String.format("CDIJJNOH HttpServer.serve time %d length %d",System.currentTimeMillis()-start,binLen));
//            ByteArrayInputStream bais = new ByteArrayInputStream(imageBin);
//            return newFixedLengthResponse(Response.Status.OK, "image/jpeg", bais, imageBin.length);
            Map<String,String> parms=session.getParms();
            int bitRate=getInt(parms,"bitRate",400000);
            int width=getInt(parms,"width",640);
            int height=getInt(parms,"height",360);
            int iFrameInterval=getInt(parms,"iFrameInterval",1);
            ScreenStream screenStream=new ScreenStream(serverRuntime,bitRate,width,height,iFrameInterval);
            screenStream.start();
            return newChunkedResponse(Response.Status.OK, ScreenStream.VIDEO_MIME_TYPE, screenStream.pipedInputStream);
        }catch(Throwable t){
            t.printStackTrace();
            return newFixedLengthResponse(Response.Status.INTERNAL_ERROR,"text/plain","INTERNAL_ERROR");
        }
    }

    private int getInt(Map<String,String> map,String key,int def){
        try {
            String retStr = map.get(key);
            if (retStr == null)
                return def;
            return Integer.parseInt(retStr);
        }catch(Throwable t){
            return def;
        }
    }
}
