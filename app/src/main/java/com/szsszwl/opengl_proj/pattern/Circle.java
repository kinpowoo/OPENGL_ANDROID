package com.szsszwl.opengl_proj.pattern;

/**
 * Created by DeskTop29 on 2018/5/10.
 */

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.view.View;

import com.szsszwl.opengl_proj.Shape;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class Circle extends Shape {

    private FloatBuffer vertexBuffer;
    private final String vertexShaderCode =
            "attribute vec4 vPosition;" +
            "uniform mat4 vMatrix;"+
            "varying vec4 vColor;"+
            "attribute vec4 aColor;"+
                    "void main() {" +
                    "  gl_Position = vMatrix*vPosition;" +
                    "  vColor=aColor;"+
                    "}";

    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "varying vec4 vColor;" +
                    "void main() {" +
                    "  gl_FragColor = vColor;" +
                    "}";

    //varying关键字一般用于从顶点着色器传入到片元着色器的量，片元着色器中相关的量也要用varying关键字，而不能再用uniform
    //uniform一般用于对同一组顶点组成的3D物体中各个顶点都相同的量
    //attribute一般用于每个顶点都各不相同的量
    //const 表常量

    private int mProgram;

    static final int COORDS_PER_VERTEX = 3;
    static float circleCoords[];

    private int mPositionHandle;
    private int mColorHandle;

    //相机位置偏移生成的矩阵
    private float[] mViewMatrix=new float[16];

    //透视投影变换生成的矩阵
    private float[] mProjectMatrix = new float[16];

    //矩阵相乘的结果矩囝
    private float[] mMVPMatrix = new float[16];

    //顶点个数
    private int vertexCount;
    //顶点之间的偏移量
    private final int vertexStride = COORDS_PER_VERTEX * 4; // 每个顶点四个字节

    private int mMatrixHandler;

    //设置颜色，依次为红绿蓝和透明通道
    float color[] = {
            0.0f, 1.0f, 0.5f, 1.0f,
            1.0f, 0.5f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f, 1.0f
    };


    private float[] createPositions(){
        ArrayList<Float> data=new ArrayList<>();
        data.add(0.0f);             //设置圆心坐标
        data.add(0.0f);
        data.add(0.0f);
        float angDegSpan=360f/36;
        for(float i=0;i<360+angDegSpan;i+=angDegSpan){
            data.add((float) (0.5*Math.sin(i*Math.PI/180f)));
            data.add((float)(0.5*Math.cos(i*Math.PI/180f)));
            data.add(0.0f);
        }
        float[] f=new float[data.size()];
        for (int i=0;i<f.length;i++){
            f[i]=data.get(i);
        }
        return f;
    }



    public Circle(View mView) {
        super(mView);
        circleCoords = createPositions();
        vertexCount = circleCoords.length / COORDS_PER_VERTEX;

        ByteBuffer bb = ByteBuffer.allocateDirect(
                circleCoords.length * 4);
        bb.order(ByteOrder.nativeOrder());

        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(circleCoords);
        vertexBuffer.position(0);
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER,
                vertexShaderCode);
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER,
                fragmentShaderCode);

        //创建一个空的OpenGLES程序
        mProgram = GLES20.glCreateProgram();
        //将顶点着色器加入到程序
        GLES20.glAttachShader(mProgram, vertexShader);
        //将片元着色器加入到程序中
        GLES20.glAttachShader(mProgram, fragmentShader);
        //连接到着色器程序
        GLES20.glLinkProgram(mProgram);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        //计算宽高比
        float ratio=(float)width/height;
        //设置透视投影
        Matrix.frustumM(mProjectMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
        //设置相机位置
        Matrix.setLookAtM(mViewMatrix, 0, 0f, 0f, 7.0f, 0f, 0f, 0f, 0f, 1f, 0.0f);
        //计算变换矩阵
        Matrix.multiplyMM(mMVPMatrix,0,mProjectMatrix,0,mViewMatrix,0);
    }

    @Override
    public void onDrawFrame(GL10 gl) {

        //将程序加入到OpenGLES2.0环境
        GLES20.glUseProgram(mProgram);
        //获取变换矩阵vMatrix成员句柄
        mMatrixHandler= GLES20.glGetUniformLocation(mProgram,"vMatrix");
        //指定vMatrix的值
        GLES20.glUniformMatrix4fv(mMatrixHandler,1,false,mMVPMatrix,0);

        //获取顶点着色器的vPosition成员句柄
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        //启用三角形顶点的句柄
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        //准备三角形的坐标数据
        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                vertexStride, vertexBuffer);



        ByteBuffer dd = ByteBuffer.allocateDirect(
                color.length * 4);
        dd.order(ByteOrder.nativeOrder());
        FloatBuffer colorBuffer = dd.asFloatBuffer();
        colorBuffer.put(color);
        colorBuffer.position(0);
        //获取片元着色器的vColor成员的句柄
        mColorHandle = GLES20.glGetAttribLocation(mProgram, "aColor");
        //设置绘制三角形的颜色
        GLES20.glEnableVertexAttribArray(mColorHandle);
        GLES20.glVertexAttribPointer(mColorHandle,4,
                GLES20.GL_FLOAT,false,
                0,colorBuffer);

        /**
        //获取片元着色器的vColor成员的句柄
        mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
        //设置绘制三角形的颜色
        GLES20.glUniform4fv(mColorHandle, 1, color, 0);
         */
        //绘制三角形
        /**
         *
         int GL_POINTS       //将传入的顶点坐标作为单独的点绘制
         int GL_LINES        //将传入的坐标作为单独线条绘制，ABCDEFG六个顶点，绘制AB、CD、EF三条线
         int GL_LINE_STRIP   //将传入的顶点作为折线绘制，ABCD四个顶点，绘制AB、BC、CD三条线
         int GL_LINE_LOOP    //将传入的顶点作为闭合折线绘制，ABCD四个顶点，绘制AB、BC、CD、DA四条线。
         int GL_TRIANGLES    //将传入的顶点作为单独的三角形绘制，ABCDEF绘制ABC,DEF两个三角形
         int GL_TRIANGLE_FAN    //将传入的顶点作为扇面绘制，ABCDEF绘制ABC,BCD,CDE,DEF四个三角形
         int GL_TRIANGLE_STRIP   //将传入的顶点作为三角条带绘制，ABCDEF绘制ABC、ACD、ADE、AEF四个三角形
         */


        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, vertexCount);
        //禁止顶点数组的句柄
        GLES20.glDisableVertexAttribArray(mPositionHandle);
    }

}