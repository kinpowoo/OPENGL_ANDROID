package com.szsszwl.opengl_proj.camera_preview;


import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by DeskTop29 on 2018/6/12.
 */

public class ICamera implements Camera.AutoFocusCallback{

    Camera camera;

    private Config mConfig;
    private CameraSizeComparator sizeComparator;

    private Camera.Size picSize;
    private Camera.Size preSize;

    private Point mPicSize;
    private Point mPreSize;


    public ICamera(){
        this.mConfig=new Config();
        mConfig.minPreviewWidth=720;
        mConfig.minPictureWidth=720;
        mConfig.rate=1.778f;
        sizeComparator=new CameraSizeComparator();
    }



    public void open(int cameraId){
        camera = Camera.open(cameraId);

        if(camera!=null){
            Camera.Parameters param=camera.getParameters();
            picSize=getPropPictureSize(param.getSupportedPictureSizes(),mConfig.rate,
                    mConfig.minPictureWidth);
            preSize=getPropPreviewSize(param.getSupportedPreviewSizes(),mConfig.rate,mConfig
                    .minPreviewWidth);
            param.setPictureSize(picSize.width,picSize.height);
            param.setPreviewSize(preSize.width,preSize.height);

            camera.setDisplayOrientation(getCameraDisplayOrientation(cameraId,camera));
            camera.setParameters(param);
            Camera.Size pre=param.getPreviewSize();
            Camera.Size pic=param.getPictureSize();
            mPicSize=new Point(pic.height,pic.width);
            mPreSize=new Point(pre.height,pre.width);
            Log.e("wuwang","camera previewSize:"+mPreSize.x+"/"+mPreSize.y);
        }
    }



    public void focus(){
        if(camera!=null){
            camera.autoFocus(this);
        }
    }



    public void close(){
        if(camera!=null){
            camera.stopPreview();
            camera.release();
        }
    }

    public void stopPreview(){
        if(camera!=null){
            camera.stopPreview();
        }
    }




    public void startPreview(){
        if(camera!=null){
            camera.startPreview();
        }
    }


    public void onScreenSizeChanged(int w,int h){
        if(camera!=null) {
        }
    }




    public void setSurfaceTexture(SurfaceTexture texture){
        if(camera!=null) {
            try {
                camera.setPreviewTexture(texture);
            }catch (IOException E){
                E.printStackTrace();
            }

        }
    }


    public void setSurfaceHolder(SurfaceHolder holder){
        if(camera!=null){
            try {
                camera.setPreviewDisplay(holder);
            }catch (IOException E){
                E.printStackTrace();
            }
        }
    }



    public Point getPreviewSize() {
        return mPreSize;
    }

    public Point getPictureSize() {
        return mPicSize;
    }



    private Camera.Size getPropPreviewSize(List<Camera.Size> list, float th, int minWidth){
        Collections.sort(list, sizeComparator);

        int i = 0;
        for(Camera.Size s:list){
            if((s.height >= minWidth) && equalRate(s, th)){
                break;
            }
            i++;
        }
        if(i == list.size()){
            i = 0;
        }
        return list.get(i);
    }

    private Camera.Size getPropPictureSize(List<Camera.Size> list, float th, int minWidth){
        Collections.sort(list, sizeComparator);

        int i = 0;
        for(Camera.Size s:list){
            if((s.height >= minWidth) && equalRate(s, th)){
                break;
            }
            i++;
        }
        if(i == list.size()){
            i = 0;
        }
        return list.get(i);
    }


    private boolean equalRate(Camera.Size s, float rate){
        float r = (float)(s.width)/(float)(s.height);
        if(Math.abs(r - rate) <= 0.03)
        {
            return true;
        }
        else{
            return false;
        }
    }

    @Override
    public void onAutoFocus(boolean success, Camera camera) {
        Log.i("camera","自动对焦"+(success?"成功":"失败"));
    }



    public int getCameraDisplayOrientation (int cameraId,android.hardware.Camera camera) {
        android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo (cameraId , info);
        int rotation = 90;
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }
        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;   // compensate the mirror
        } else {
            // back-facing
            result = ( info.orientation - degrees + 360) % 360;
        }
        return result;
    }




    class Config{
        float rate; //宽高比
        int minPreviewWidth;
        int minPictureWidth;
    }

    private class CameraSizeComparator implements Comparator<Camera.Size> {
        public int compare(Camera.Size lhs, Camera.Size rhs) {
            // TODO Auto-generated method stub
            if(lhs.height == rhs.height){
                return 0;
            }
            else if(lhs.height > rhs.height){
                return 1;
            }
            else{
                return -1;
            }
        }

    }
}
