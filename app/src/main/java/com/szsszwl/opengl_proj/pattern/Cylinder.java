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

public class Cylinder extends Shape {

    private Oval oval;
    private Oval bottomOval;
    private FloatBuffer vertexBuffer;
    private final String vertexShaderCode =
            "attribute vec4 vPosition;" +
            "uniform mat4 vMatrix;"+
            "varying vec4 vColor;"+
               "void main() {" +
                    "  gl_Position = vMatrix*vPosition;" +
                      "if(vPosition.z!=0.0){"+
                          "vColor=vec4(0.0,0.0,0.0,1.0);"+
                      "}else{"+
                        "vColor=vec4(0.9,0.9,0.9,1.0);"+
                      "}"+
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

    //相机位置偏移生成的矩阵
    private float[] mViewMatrix=new float[16];

    //透视投影变换生成的矩阵
    private float[] mProjectMatrix = new float[16];

    //矩阵相乘的结果矩囝
    private float[] mMVPMatrix = new float[16];

    //顶点之间的偏移量
    private final int vertexStride = COORDS_PER_VERTEX * 4; // 每个顶点四个字节

    private int mMatrixHandler;




    private int n=360;  //切割份数
    private float height=2.0f;  //圆锥高度
    private float radius=1.0f;  //圆锥底面半径


    private int vSize;


    private float[] createPositions(){
        ArrayList<Float> data=new ArrayList<>();
        //data.add(0.0f);             //设置圆心坐标
        //data.add(0.0f);
        //data.add(height);             //给圆心增加高度，使之形成锥面
        float angDegSpan=360f/n;
        for(float i=0;i<360+angDegSpan;i+=angDegSpan){
            data.add((float) (radius*Math.sin(i*Math.PI/180f)));
            data.add((float)(radius*Math.cos(i*Math.PI/180f)));
            data.add(height);
            data.add((float) (radius*Math.sin(i*Math.PI/180f)));
            data.add((float)(radius*Math.cos(i*Math.PI/180f)));
            data.add(0.0f);
        }
        float[] f=new float[data.size()];
        for (int i=0;i<f.length;i++){
            f[i]=data.get(i);
        }

        vSize = f.length/3;
        return f;
    }



    public Cylinder(View mView) {
        super(mView);
        float[] bottomColor = new float[]{0.67f,0.67f,0.67f,1.0f};
        oval = new Oval(mView,0.0f,bottomColor);
        float[] topColor = new float[]{
                0.16f,0.16f,0.16f,0.0f,
                0.25f,0.25f,0.25f,0.0f,
                0f,0f,0f,0f
        };
        bottomOval = new Oval(mView,2.0f,topColor);
        circleCoords = createPositions();


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
        oval.onSurfaceCreated(gl,config);
        bottomOval.onSurfaceCreated(gl,config);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        //计算宽高比
        float ratio=(float)width/height;
        //设置透视投影
        Matrix.frustumM(mProjectMatrix, 0, -ratio, ratio, -1, 1, 3, 20);
        //设置相机位置
        //设置相机位置
        Matrix.setLookAtM(mViewMatrix, 0, 0f, 0f, 5f, 0f, 1f, 0f, 0f, 1.0f, 0.0f);
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

        Matrix.rotateM(mMVPMatrix,0,mAngleX,1f,0,0f);
        Matrix.rotateM(mMVPMatrix,0,mAngleZ,0f,0f,1f);

        GLES20.glUniformMatrix4fv(mMatrixHandler,1,false,mMVPMatrix,0);

        //获取顶点着色器的vPosition成员句柄
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        //启用三角形顶点的句柄
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        //准备三角形的坐标数据
        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                vertexStride, vertexBuffer);



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


        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, vSize);
        //禁止顶点数组的句柄
        GLES20.glDisableVertexAttribArray(mPositionHandle);

        oval.setMatrix(mMVPMatrix);
        oval.onDrawFrame(gl);

        bottomOval.setMatrix(mMVPMatrix);
        bottomOval.onDrawFrame(gl);
    }

}