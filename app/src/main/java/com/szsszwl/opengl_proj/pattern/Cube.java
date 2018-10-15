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
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class Cube extends Shape {

    private FloatBuffer vertexBuffer;
    private FloatBuffer colorBuffer;
    private ShortBuffer indexBuffer;
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
    final float cubePositions[] = {
            -1.0f,1.0f,1.0f,    //正面左上0
            -1.0f,-1.0f,1.0f,   //正面左下1
            1.0f,-1.0f,1.0f,    //正面右下2
            1.0f,1.0f,1.0f,     //正面右上3
            -1.0f,1.0f,-1.0f,    //反面左上4
            -1.0f,-1.0f,-1.0f,   //反面左下5
            1.0f,-1.0f,-1.0f,    //反面右下6
            1.0f,1.0f,-1.0f,     //反面右上7
    };

    final short index[]={
            0,3,2,0,2,1,    //正面
            0,1,5,0,5,4,    //左面
            0,7,3,0,4,7,    //上面
            6,7,4,6,4,5,    //后面
            6,3,7,6,2,3,    //右面
            6,5,1,6,1,2     //下面
    };


    //八个顶点的颜色，与顶点坐标一一对应
    float color[] = {
            0f,1f,0f,1f,
            0f,1f,0f,1f,
            0f,1f,0f,1f,
            0f,1f,0f,1f,
            1f,0f,0f,1f,
            1f,0f,0f,1f,
            1f,0f,0f,1f,
            1f,0f,0f,1f,
    };


    private int mPositionHandle;
    private int mColorHandle;

    //相机位置偏移生成的矩阵
    private float[] mViewMatrix=new float[16];

    //透视投影变换生成的矩阵
    private float[] mProjectMatrix = new float[16];

    //矩阵相乘的结果矩囝
    private float[] mMVPMatrix = new float[16];


    private int mMatrixHandler;



    //顶点个数
    private final int vertexCount = cubePositions.length / COORDS_PER_VERTEX;
    //顶点之间的偏移量
    private final int vertexStride = COORDS_PER_VERTEX * 4; // 每个顶点四个字节





    public Cube(View mView) {
        super(mView);
        ByteBuffer bb = ByteBuffer.allocateDirect(
                cubePositions.length * 4);
        bb.order(ByteOrder.nativeOrder());

        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(cubePositions);
        vertexBuffer.position(0);



        ByteBuffer bb2 = ByteBuffer.allocateDirect(color.length*4);
        bb2.order(ByteOrder.nativeOrder());

        colorBuffer = bb2.asFloatBuffer();
        colorBuffer.put(color);
        colorBuffer.position(0);


        ByteBuffer bb3 = ByteBuffer.allocateDirect(index.length*2);
        bb3.order(ByteOrder.nativeOrder());

        indexBuffer = bb3.asShortBuffer();
        indexBuffer.put(index);
        indexBuffer.position(0);



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
        //开启深度测试
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        //计算宽高比
        float ratio=(float)width/height;
        //设置透视投影
        Matrix.frustumM(mProjectMatrix, 0, -ratio, ratio, -1, 1, 3, 20);
        //设置相机位置
        Matrix.setLookAtM(mViewMatrix, 0, 5.0f, 5.0f, 10.0f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        //计算变换矩阵
        Matrix.multiplyMM(mMVPMatrix,0,mProjectMatrix,0,mViewMatrix,0);
    }

    @Override
    public void onDrawFrame(GL10 gl) {


        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT|GLES20.GL_DEPTH_BUFFER_BIT);

        //将程序加入到OpenGLES2.0环境
        GLES20.glUseProgram(mProgram);
        //获取变换矩阵vMatrix成员句柄
        mMatrixHandler= GLES20.glGetUniformLocation(mProgram,"vMatrix");

        Matrix.rotateM(mMVPMatrix,0,mAngleX,1f,0,0f);
        Matrix.rotateM(mMVPMatrix,0,mAngleZ,0f,0f,1f);
        //指定vMatrix的值
        GLES20.glUniformMatrix4fv(mMatrixHandler,1,false,mMVPMatrix,0);

        //获取顶点着色器的vPosition成员句柄
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        //启用三角形顶点的句柄
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        //准备三角形的坐标数据
        GLES20.glVertexAttribPointer(mPositionHandle,3,
                GLES20.GL_FLOAT, false,
                0, vertexBuffer);



        //获取片元着色器的vColor成员的句柄
        mColorHandle = GLES20.glGetAttribLocation(mProgram, "aColor");
        //设置绘制三角形的颜色
        GLES20.glEnableVertexAttribArray(mColorHandle);
        GLES20.glVertexAttribPointer(mColorHandle,4,
                GLES20.GL_FLOAT,false,
                0,colorBuffer);

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


        //索引法绘制正方形
        GLES20.glDrawElements(GLES20.GL_TRIANGLES,index.length,GLES20.GL_UNSIGNED_SHORT,indexBuffer);
        //禁止顶点数组的句柄
        GLES20.glDisableVertexAttribArray(mPositionHandle);
    }

}