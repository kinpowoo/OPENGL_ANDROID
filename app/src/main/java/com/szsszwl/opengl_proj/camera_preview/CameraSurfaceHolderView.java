package com.szsszwl.opengl_proj.camera_preview;

import android.content.Context;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


/**
 * Created by DeskTop29 on 2018/6/12.
 */

public class CameraSurfaceHolderView extends SurfaceView implements SurfaceHolder.Callback {
    ICamera camera;
    int cameraId = 1 ;
    SurfaceHolder holder;
    Context mContext;


    public CameraSurfaceHolderView(Context context, AttributeSet attributeSet) {
        super(context,attributeSet);
        mContext = context;
        camera = new ICamera();
        holder = getHolder();
        holder.setFormat(PixelFormat.TRANSPARENT);//translucent半透明 transparent透明
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        holder.addCallback(this);
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        this.holder = holder;
        camera.open(cameraId);
        camera.setSurfaceHolder(holder);
        camera.startPreview();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        camera.close();
    }



    public void switchCamera(){
        camera.close();
        cameraId=cameraId==1?0:1;
        camera.open(cameraId);
        camera.setSurfaceHolder(holder);
        camera.startPreview();
    }

}
