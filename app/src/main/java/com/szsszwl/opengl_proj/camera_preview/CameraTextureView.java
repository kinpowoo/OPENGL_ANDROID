package com.szsszwl.opengl_proj.camera_preview;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.util.AttributeSet;
import android.view.TextureView;

/**
 * Created by DeskTop29 on 2018/6/12.
 */

public class CameraTextureView extends TextureView implements TextureView.SurfaceTextureListener {

    Context mContext;
    SurfaceTexture mSurface;
    ICamera camera;
    int cameraId = 1 ;

    public CameraTextureView(Context context, AttributeSet attributeSet) {
        super(context,attributeSet);
        mContext = context;
        camera = new ICamera();
        this.setSurfaceTextureListener(this);
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        this.mSurface = surface;
        camera.open(cameraId);
        camera.setSurfaceTexture(surface);
        camera.startPreview();
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        camera.onScreenSizeChanged(width,height);
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        camera.close();
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }



    public void autoFocus(){
        if(camera!=null){
            camera.focus();
        }
    }


    public void switchCamera(){
        camera.close();
        cameraId=cameraId==1?0:1;
        camera.open(cameraId);
        camera.setSurfaceTexture(mSurface);
        camera.startPreview();
    }
}
