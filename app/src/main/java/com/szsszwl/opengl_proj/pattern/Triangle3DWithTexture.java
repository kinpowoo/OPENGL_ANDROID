package com.szsszwl.opengl_proj.pattern;

import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.view.View;

import com.szsszwl.opengl_proj.Shape;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES10.GL_FLOAT;
import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.GL_TEXTURE_2D;

/**
 * Created by DeskTop29 on 2018/6/11.
 */

public class Triangle3DWithTexture extends Shape {


    private final String vertexShader =
            "attribute vec4 vPosition;"+
            "uniform mat4 vMatrix;"+
            "attribute vec2 vCoordinate;"+
            "varying vec2 aCoordinate;"+
            "attribute vec4 vColor;"+
            "varying vec4 aColor;"+
            "void main(){"+
                "    gl_Position=vMatrix*vPosition;"+
                "    aCoordinate=vCoordinate;"+
                "    aColor=vColor;"+
            "}";

    private final String fragmentShader =
            "precision mediump float;"+
            "varying vec2 aCoordinate;"+
            "varying vec4 aColor;"+
            "uniform sampler2D vTexture;"+
            "void main(){"+
            "    vec4 nColor=texture2D(vTexture,aCoordinate)*aColor;"+
            "    gl_FragColor=nColor;"+
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
         0,2,4,    //背面
         0,3,4,    //右侧面
         1,2,3,     //底面,由二个三角形组成
         2,3,4     //底面
    };


    //纹理索引侍标，与顶点一一对应
    private final float[] texture2DIndex = {
            0.5f,0.25f,
            0.25f,0.75f,
            0.75f,0.75f,
            0.25f,0.75f,
            0.75f,0.75f,
    };


    //颜色
    private final float[] color ={
            0f,1f,0.68f,1f,
            1f,0f,0f,1f,
            0f,0f,1f,1f,
            0.5f,0.5f,0.68f,1f,
            0.86f,0.89f,0.68f,1f
    };



    private FloatBuffer posBuffer;
    private FloatBuffer textureBuffer;
    private ShortBuffer indexBuffer;
    private FloatBuffer colorBuffer;


    private int vertexIndex,colorIndex,textureCoordIndex,matrixIndex,hTextureIndex,textureId;


    public Triangle3DWithTexture(View mView) {
        super(mView);

        ByteBuffer aa = ByteBuffer.allocateDirect(trianglePos.length*4);
        aa.order(ByteOrder.nativeOrder());

        posBuffer = aa.asFloatBuffer();
        posBuffer.put(trianglePos);
        posBuffer.position(0);

        ByteBuffer cc= ByteBuffer.allocateDirect(texture2DIndex.length*4);
        cc.order(ByteOrder.nativeOrder());
        textureBuffer=cc.asFloatBuffer();
        textureBuffer.put(texture2DIndex);
        textureBuffer.position(0);


        ByteBuffer bb = ByteBuffer.allocateDirect(triangleIndex.length*2);
        bb.order(ByteOrder.nativeOrder());

        indexBuffer = bb.asShortBuffer();
        indexBuffer.put(triangleIndex);
        indexBuffer.position(0);


        ByteBuffer dd = ByteBuffer.allocateDirect(color.length*4);
        dd.order(ByteOrder.nativeOrder());

        colorBuffer = dd.asFloatBuffer();
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

        int w=b.getWidth();
        int h=b.getHeight();
        float sWH=w/(float)h;
        float sWidthHeight=width/(float)height;

        if(width>height){
            if(sWH>sWidthHeight){
                Matrix.orthoM(mProjectMatrix, 0, -sWidthHeight*sWH,sWidthHeight*sWH, -1,1, 3, 5);
            }else{
                Matrix.orthoM(mProjectMatrix, 0, -sWidthHeight/sWH,sWidthHeight/sWH, -1,1, 3, 5);
            }
        }else{
            if(sWH>sWidthHeight){
                Matrix.orthoM(mProjectMatrix, 0, -1, 1, -1/sWidthHeight*sWH, 1/sWidthHeight*sWH,3, 5);
            }else{
                Matrix.orthoM(mProjectMatrix, 0, -1, 1, -sWH/sWidthHeight, sWH/sWidthHeight,3, 5);
            }
        }
        //设置相机位置
        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, 5.0f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        //计算变换矩阵
        Matrix.multiplyMM(mMVPMatrix,0,mProjectMatrix,0,mViewMatrix,0);

       // Matrix.scaleM(mMVPMatrix,0,3f,3f,3f);
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

        textureCoordIndex = GLES20.glGetAttribLocation(program,"vCoordinate");
        GLES20.glEnableVertexAttribArray(textureCoordIndex);
        GLES20.glVertexAttribPointer(textureCoordIndex,2,GLES20.GL_FLOAT,false,0,textureBuffer);

        colorIndex = GLES20.glGetAttribLocation(program,"vColor");
        GLES20.glEnableVertexAttribArray(colorIndex);
        GLES20.glVertexAttribPointer(colorIndex,4,GL_FLOAT,false,0,colorBuffer);



        hTextureIndex = GLES20.glGetUniformLocation(program,"vTexture");  //获得纹理对象Sampler2D的位置
        textureId = createTexture();   //获理纹素首地址下标

        /**
         * GLES20 至少可以创建16个纹理单元，分别为GL_TEXTURE0 到 GL_TEXTURE15
         * 纹理单元最大支持数目可以通过查询GL_MAX_COMBINED_TEXTURE_IMAGE_UNITS常量获取
         *
         使用1号纹理单元
         glActiveTexture(GL_TEXTURE1);
         glBindTexture(GL_TEXTURE_2D, textureId2);
         glUniform1i(glGetUniformLocation(shader.programId, "tex2"), 1);
         */
        GLES20.glActiveTexture(GL_TEXTURE0);   //激活纹理单元0
        GLES20.glBindTexture(GL_TEXTURE_2D,textureId);  //绑定纹素到纹理单元0上
        GLES20.glUniform1i(hTextureIndex, 0);        //给纹理对象赋值，0就是纹理单元0




        vertexIndex = GLES20.glGetAttribLocation(program,"vPosition");
        GLES20.glEnableVertexAttribArray(vertexIndex);
        GLES20.glVertexAttribPointer(vertexIndex,3,GLES20.GL_FLOAT,false,0,posBuffer);




        GLES20.glDrawElements(GLES20.GL_TRIANGLES,triangleIndex.length,GLES20.GL_UNSIGNED_SHORT,indexBuffer);


        GLES20.glDisableVertexAttribArray(vertexIndex);
        GLES20.glDisableVertexAttribArray(colorIndex);
        GLES20.glDisableVertexAttribArray(textureCoordIndex);
    }



    private int createTexture(){
        int[] texture=new int[1];
        if(b!=null&&!b.isRecycled()){
            //生成纹理
            GLES20.glGenTextures(1,texture,0);
            //生成纹理
            GLES20.glBindTexture(GL_TEXTURE_2D,texture[0]);
            //设置缩小过滤为使用纹理中坐标最接近的一个像素的颜色作为需要绘制的像素颜色
            GLES20.glTexParameterf(GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
            //设置放大过滤为使用纹理中坐标最接近的若干个颜色，通过加权平均算法得到需要绘制的像素颜色
            GLES20.glTexParameterf(GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            //设置环绕方向S，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
            GLES20.glTexParameterf(GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
            //设置环绕方向T，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
            GLES20.glTexParameterf(GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);
            //根据以上指定的参数，生成一个2D纹理
            GLUtils.texImage2D(GL_TEXTURE_2D,0, b, 0);

            return texture[0];
        }
        return 0;
    }



}
