package com.szsszwl.opengl_proj.camera_preview;

import android.content.Context;
import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by DeskTop29 on 2018/6/11.
 */

public class CameraGLSurfaceView extends GLSurfaceView implements GLSurfaceView.Renderer{

    ICamera camera;
    private int cameraId=1;
    private Runnable mRunnable;

    CameraDrawer mCameraDrawer;


    public CameraGLSurfaceView(Context context) {
        this(context,null);
    }

    public CameraGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }


    public void init(){
        setEGLContextClientVersion(2);
        setRenderer(this);
        setRenderMode(RENDERMODE_WHEN_DIRTY);
        camera = new ICamera();
        mCameraDrawer = new CameraDrawer();
    }



    public void switchCamera(){
        mRunnable=new Runnable() {
            @Override
            public void run() {
                //camera.close();
                cameraId=cameraId==1?0:1;
            }
        };
        onPause();
        onResume();
    }




    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        mCameraDrawer.onSurfaceCreated(gl,config);
        if(mRunnable!=null){
            mRunnable.run();
            mRunnable=null;
        }
        camera.open(cameraId);
        mCameraDrawer.setCameraId(cameraId);
        Point point=camera.getPreviewSize();
        mCameraDrawer.setDataSize(point.x,point.y);
        camera.setSurfaceTexture(mCameraDrawer.getSurfaceTexture());
        mCameraDrawer.getSurfaceTexture().setOnFrameAvailableListener(new SurfaceTexture.OnFrameAvailableListener() {
            @Override
            public void onFrameAvailable(SurfaceTexture surfaceTexture) {
                requestRender();
                Log.i("ta","request Render执行");
            }
        });
        camera.startPreview();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        mCameraDrawer.setViewSize(width,height);
        GLES20.glViewport(0,0,width,height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        mCameraDrawer.onDrawFrame(gl);
    }



    @Override
    public void onPause() {
        super.onPause();
        camera.close();
    }

}
