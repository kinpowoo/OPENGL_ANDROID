package com.szsszwl.opengl_proj.pattern;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.view.View;

import com.szsszwl.opengl_proj.Shape;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by DeskTop29 on 2018/6/11.
 */

public class Triangle3D extends Shape {


    private final String vertexShader =
            "attribute vec4 vPosition;"+
            "uniform mat4 vMatrix;"+
            "attribute vec4 vColor;"+
            "varying vec4 aColor;"+
            "void main(){"+
                "    gl_Position=vMatrix*vPosition;"+
                "    aColor=vColor;"+
            "}";

    private final String fragmentShader =
            "precision mediump float;"+
            "varying vec4 aColor;"+
            "void main(){"+
            "    gl_FragColor=aColor;"+
            "}";


    //相机位置偏移生成的矩阵
    private float[] mViewMatrix=new float[16];

    //透视投影变换生成的矩阵
    private float[] mProjectMatrix = new float[16];

    //矩阵相乘的结果矩囝
    private float[] mMVPMatrix = new float[16];

    //链接程序
    int program;

    //顶点坐标
    private final float[] trianglePos = {
        0f,0.5f,0f,      //三角锥顶点
        -0.5f,-0.5f,0.5f,   //三角锥左下角顶点
        0.5f,-0.5f,0.5f,    //三角锥右下角顶点
        -0.5f,-0.5f,-0.5f,  //三角锥后左下角顶点
        0.5f,-0.5f,-0.5f      //三角锥后右下角顶点
    };

    //顶点索引
    private final short[] triangleIndex = {
         0,1,2,    //正面
         0,1,3,    //左侧面
         0,2,4,
         0,3,4,    //右侧面
         1,2,3,
         2,3,4     //底面
    };


    //颜色
    private final float[] color ={
            0.1f,0f,0f,0f,
            0f,1f,0f,0.5f,
            0f,0f,01f,1f,
            0.86f,0.86f,1f,1f,
            0.36f,0.89f,0.68f,0.5f
    };



    private FloatBuffer posBuffer;
    private FloatBuffer colorBuffer;
    private ShortBuffer indexBuffer;


    private int vertexIndex,colorIndex,matrixIndex;


    public Triangle3D(View mView) {
        super(mView);

        ByteBuffer aa = ByteBuffer.allocateDirect(trianglePos.length*4);
        aa.order(ByteOrder.nativeOrder());

        posBuffer = aa.asFloatBuffer();
        posBuffer.put(trianglePos);
        posBuffer.position(0);




        ByteBuffer bb = ByteBuffer.allocateDirect(triangleIndex.length*2);
        bb.order(ByteOrder.nativeOrder());

        indexBuffer = bb.asShortBuffer();
        indexBuffer.put(triangleIndex);
        indexBuffer.position(0);


        ByteBuffer cc = ByteBuffer.allocateDirect(color.length*4);
        cc.order(ByteOrder.nativeOrder());

        colorBuffer = cc.asFloatBuffer();
        colorBuffer.put(color);
        colorBuffer.position(0);





        program = GLES20.glCreateProgram();
        GLES20.glAttachShader(program,loadShader(GLES20.GL_VERTEX_SHADER,vertexShader));
        GLES20.glAttachShader(program,loadShader(GLES20.GL_FRAGMENT_SHADER,fragmentShader));
        GLES20.glLinkProgram(program);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        //开启深度测试
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        //gl.glViewport(0,0,width,height);

        float ratio = (float) width/height;
        Matrix.frustumM(mProjectMatrix,0,-ratio,ratio,-1,1,3,20);
        Matrix.setLookAtM(mViewMatrix,0,0,0,10f,0,0,0,0,1.0f,0);
        Matrix.multiplyMM(mMVPMatrix,0,mProjectMatrix,0,mViewMatrix,0);

        Matrix.scaleM(mMVPMatrix,0,3f,3f,3f);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        gl.glClear(GLES20.GL_COLOR_BUFFER_BIT|GLES20.GL_DEPTH_BUFFER_BIT);

        GLES20.glUseProgram(program);



        Matrix.rotateM(mMVPMatrix,0,mAngleX,1f,0,0f);
        Matrix.rotateM(mMVPMatrix,0,mAngleY,0f,1f,0f);
        Matrix.rotateM(mMVPMatrix,0,mAngleZ,0f,0f,1f);


        matrixIndex = GLES20.glGetUniformLocation(program,"vMatrix");
        GLES20.glUniformMatrix4fv(matrixIndex,1,false,mMVPMatrix,0);

        vertexIndex = GLES20.glGetAttribLocation(program,"vPosition");
        GLES20.glEnableVertexAttribArray(vertexIndex);
        GLES20.glVertexAttribPointer(vertexIndex,3,GLES20.GL_FLOAT,false,0,posBuffer);

        colorIndex = GLES20.glGetAttribLocation(program,"vColor");
        GLES20.glEnableVertexAttribArray(colorIndex);
        GLES20.glVertexAttribPointer(colorIndex,4,GLES20.GL_FLOAT,false,0,colorBuffer);


        GLES20.glDrawElements(GLES20.GL_TRIANGLES,triangleIndex.length,GLES20.GL_UNSIGNED_SHORT,indexBuffer);


        GLES20.glDisableVertexAttribArray(vertexIndex);
        GLES20.glDisableVertexAttribArray(colorIndex);
    }



    public void createTextureId(){

    }



}
