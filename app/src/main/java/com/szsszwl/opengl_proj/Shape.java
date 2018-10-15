package com.szsszwl.opengl_proj;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.view.View;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by DeskTop29 on 2018/5/10.
 */


public abstract class Shape implements GLSurfaceView.Renderer {

    protected View mView;
    protected Bitmap b;
    public float mAngleX = 0.0f;
    public float mAngleZ = 0.0f;
    public float mAngleY = 0.0f;

    public Shape(View mView) {
        this.mView = mView;
    }

    public int loadShader(int type, String shaderCode) {
        //根据type创建顶点着色器或者片元着色器
        int shader = GLES20.glCreateShader(type);
        //将资源加入到着色器中，并编译
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        return shader;
    }


    public void setBitmap(Bitmap b){
        this.b = b;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(1.0f,1.0f,1.0f,1.0f);
    }
}

