/*
 *
 * FGLView.java
 * 
 * Created by Wuwang on 2016/9/29
 */
package com.szsszwl.opengl_proj.basic;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.szsszwl.opengl_proj.Shape;

import java.io.IOException;


/**
 * Description:
 */
public class FGLView extends GLSurfaceView {

    private FGLRender renderer;

    private float shangY;//上次的触控位置Y坐标
    private float shangX;//上次的触控位置Y坐标

    private final float suo = 360.0f/360;//角度缩放比例


    public FGLView(Context context) {
        this(context,null);
    }

    public FGLView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init(){
        setEGLContextClientVersion(2);
        renderer=new FGLRender(this);
        setRenderer(renderer);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

        try {
            renderer.setBitmap(BitmapFactory.decodeStream(getResources().getAssets().open("texture/girl.jpg")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setShape(Class<? extends Shape> clazz){
        try {
            renderer.setShape(clazz);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }




    //触摸事件回调方法
    @Override
    public boolean onTouchEvent(MotionEvent e) {
        float y = e.getY();
        float x = e.getX();
        switch (e.getAction()) {
            case MotionEvent.ACTION_MOVE:
                float dy = (y - shangY);//计算触控笔Y位移
                float dx = (x - shangX);//计算触控笔Y位移

                dy *=0.2;
                dx *=0.2;

                if(Math.abs(dx)>3&&Math.abs(dy)<100){
                    renderer.shape.mAngleX = dx * suo;//设置沿x轴旋转角度

                    renderer.shape.mAngleY = 0f;
                    renderer.shape.mAngleZ = 0f;
                }

                if(Math.abs(dy)>3&&Math.abs(dx)<100) {
                    renderer.shape.mAngleY = dy * suo;//设置沿z轴旋转角度

                    renderer.shape.mAngleX = 0f;
                    renderer.shape.mAngleZ = 0f;
                }

                if(Math.abs(dx)>5&&Math.abs(dy)>5){
                    renderer.shape.mAngleZ = dy * suo;//设置沿x轴旋转角度

                    renderer.shape.mAngleX = 0f;
                    renderer.shape.mAngleY = 0f;
                }

                requestRender();
        }
        shangY = y;//记录触控笔位置
        shangX = x;//记录触控笔位置
        return true;
    }

}
