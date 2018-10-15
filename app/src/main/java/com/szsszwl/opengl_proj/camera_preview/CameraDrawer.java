package com.szsszwl.opengl_proj.camera_preview;

import android.content.res.Resources;
import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;

import com.szsszwl.opengl_proj.utils.Gl2Utils;
import com.szsszwl.opengl_proj.utils.MatrixUtils;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Arrays;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by DeskTop29 on 2018/6/12.
 */

public class CameraDrawer implements GLSurfaceView.Renderer {

    private SurfaceTexture surfaceTexture;
    int cameraId = 1;
    private int width,height;
    private int dataWidth,dataHeight;


    private static final String TAG="Drawer";
    public static boolean DEBUG=true;

    /**
     * 单位矩阵
     */
    public static final float[] OM= MatrixUtils.getOriginalMatrix();

    //变换矩阵 用来实现旋转，缩放，平移等效果
    private float[] matrix= Arrays.copyOf(OM,16);

    //纹理坐标变换矩阵，用来调整相机预览画面的方向
    private float[] mCoordMatrix= Arrays.copyOf(OM,16);

    private int textureType=0;      //默认使用Texture2D0
    private int textureId=0;


    private final String vertexShader =
            "attribute vec4 vPosition;\n" +
            "attribute vec2 vCoord;\n" +
            "uniform mat4 vMatrix;\n" +
            "uniform mat4 vCoordMatrix;\n" +
            "varying vec2 textureCoordinate;\n" +
            "\n" +
            "void main(){\n" +
            "    gl_Position = vMatrix*vPosition;\n" +
            "    textureCoordinate = (vCoordMatrix*vec4(vCoord,0,1)).xy;\n" +
            "}";


    private final String fragmentShader =
            "#extension GL_OES_EGL_image_external : require\n" +
            "precision mediump float;\n" +
            "varying vec2 textureCoordinate;\n" +
            "uniform samplerExternalOES vTexture;\n" +
            "void main() {\n" +
            "    gl_FragColor = texture2D( vTexture, textureCoordinate );\n" +
            "}";



    //程序句柄
    protected int mProgram;

    //顶点坐标句柄
    protected int mHPosition;

    //纹理坐标句柄
    protected int mHCoord;

    //纹理坐标变换矩阵句柄
    private int mHCoordMatrix;

    //总变换矩阵句柄
    protected int mHMatrix;

    //默认纹理贴图句柄
    protected int mHTexture;




    //顶点坐标Buffer
    protected FloatBuffer mVerBuffer;

    //纹理坐标Buffer
    protected FloatBuffer mTexBuffer;

    //顶点坐标
    private float pos[] = {
            -1.0f,  1.0f,    //左上角
            -1.0f, -1.0f,    //左下角
            1.0f, 1.0f,      //右上角
            1.0f,  -1.0f,    //右下角
    };

    //纹理坐标
    private float[] coord={
            0.0f, 0.0f,    //左上角
            0.0f,  1.0f,   //左下角
            1.0f,  0.0f,   //右上角
            1.0f, 1.0f,    //右下角
    };


    public CameraDrawer(){
        initBuffer();
    }




    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        int texture = createTextureID();
        this.surfaceTexture=new SurfaceTexture(texture);
        createProgram(vertexShader,fragmentShader);
        setTextureId(texture);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        setViewSize(width,height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        if(surfaceTexture!=null){
            surfaceTexture.updateTexImage();
        }
        draw();
    }


    public void setDataSize(int dataWidth,int dataHeight){
        this.dataWidth=dataWidth;
        this.dataHeight=dataHeight;
        calculateMatrix();
    }

    public void setViewSize(int width,int height){
        this.width=width;
        this.height=height;
        calculateMatrix();
    }



    private void calculateMatrix(){
        Gl2Utils.getShowMatrix(matrix,this.dataWidth,this.dataHeight,this.width,this.height);
        if(cameraId==1){
            Gl2Utils.flip(matrix,true,false);
            Gl2Utils.rotate(matrix,90);
        }else{
            Gl2Utils.rotate(matrix,270);
        }
        setMatrix(matrix);
    }

    public SurfaceTexture getSurfaceTexture(){
        return surfaceTexture;
    }

    public void setCameraId(int id){
        this.cameraId=id;
        calculateMatrix();
    }


    protected final void createProgram(String vertex,String fragment){
        mProgram= uCreateGlProgram(vertex,fragment);
        mHPosition= GLES20.glGetAttribLocation(mProgram, "vPosition");
        mHCoord=GLES20.glGetAttribLocation(mProgram,"vCoord");
        mHMatrix=GLES20.glGetUniformLocation(mProgram,"vMatrix");
        mHTexture=GLES20.glGetUniformLocation(mProgram,"vTexture");
        mHCoordMatrix=GLES20.glGetUniformLocation(mProgram,"vCoordMatrix");
    }


    /**
     * Buffer初始化
     */
    protected void initBuffer(){
        ByteBuffer a=ByteBuffer.allocateDirect(32);
        a.order(ByteOrder.nativeOrder());
        mVerBuffer=a.asFloatBuffer();
        mVerBuffer.put(pos);
        mVerBuffer.position(0);
        ByteBuffer b=ByteBuffer.allocateDirect(32);
        b.order(ByteOrder.nativeOrder());
        mTexBuffer=b.asFloatBuffer();
        mTexBuffer.put(coord);
        mTexBuffer.position(0);
    }


    private int createTextureID(){
        int[] texture = new int[1];
        GLES20.glGenTextures(1, texture, 0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, texture[0]);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_MIN_FILTER,GL10.GL_LINEAR);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);
        return texture[0];
    }


    protected void onDraw(){
        GLES20.glEnableVertexAttribArray(mHPosition);
        GLES20.glVertexAttribPointer(mHPosition,2, GLES20.GL_FLOAT, false, 0,mVerBuffer);
        GLES20.glEnableVertexAttribArray(mHCoord);
        GLES20.glVertexAttribPointer(mHCoord, 2, GLES20.GL_FLOAT, false, 0, mTexBuffer);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP,0,4);

        GLES20.glDisableVertexAttribArray(mHPosition);
        GLES20.glDisableVertexAttribArray(mHCoord);
    }




    /**
     * 清除画布
     */
    protected void onClear(){
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
    }
    protected void onUseProgram(){
        GLES20.glUseProgram(mProgram);
    }


    //设置其他扩展数据
    protected void onSetExpandData(){
        GLES20.glUniformMatrix4fv(mHMatrix,1,false,matrix,0);
        GLES20.glUniformMatrix4fv(mHCoordMatrix,1,false,mCoordMatrix,0);
    }

    //绑定默认纹理
    protected void onBindTexture(){
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0+textureType);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,getTextureId());
        GLES20.glUniform1i(mHTexture,textureType);
    }





    public static void glError(int code,Object index){
        if(DEBUG&&code!=0){
            Log.e(TAG,"glError:"+code+"---"+index);
        }
    }


    //通过路径加载Assets中的文本内容
    public static String uRes(Resources mRes, String path){
        StringBuilder result=new StringBuilder();
        try{
            InputStream is=mRes.getAssets().open(path);
            int ch;
            byte[] buffer=new byte[1024];
            while (-1!=(ch=is.read(buffer))){
                result.append(new String(buffer,0,ch));
            }
        }catch (Exception e){
            return null;
        }
        return result.toString().replaceAll("\\r\\n","\n");
    }

    //创建GL程序
    public static int uCreateGlProgram(String vertexSource, String fragmentSource){
        int vertex=uLoadShader(GLES20.GL_VERTEX_SHADER,vertexSource);
        if(vertex==0)return 0;
        int fragment=uLoadShader(GLES20.GL_FRAGMENT_SHADER,fragmentSource);
        if(fragment==0)return 0;
        int program= GLES20.glCreateProgram();
        if(program!=0){
            GLES20.glAttachShader(program,vertex);
            GLES20.glAttachShader(program,fragment);
            GLES20.glLinkProgram(program);
            int[] linkStatus=new int[1];
            GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS,linkStatus,0);
            if(linkStatus[0]!= GLES20.GL_TRUE){
                glError(1,"Could not link program:"+ GLES20.glGetProgramInfoLog(program));
                GLES20.glDeleteProgram(program);
                program=0;
            }
        }
        return program;
    }

    //加载shader
    public static int uLoadShader(int shaderType,String source){
        int shader= GLES20.glCreateShader(shaderType);
        if(0!=shader){
            GLES20.glShaderSource(shader,source);
            GLES20.glCompileShader(shader);
            int[] compiled=new int[1];
            GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS,compiled,0);
            if(compiled[0]==0){
                glError(1,"Could not compile shader:"+shaderType);
                glError(1,"GLES20 Error:"+ GLES20.glGetShaderInfoLog(shader));
                GLES20.glDeleteShader(shader);
                shader=0;
            }
        }
        return shader;
    }


    public void draw(){
        onClear();
        onUseProgram();
        onSetExpandData();
        onBindTexture();
        onDraw();
    }

    public final int getTextureType(){
        return textureType;
    }

    public final int getTextureId(){
        return textureId;
    }
    public final void setTextureId(int textureId){
        this.textureId=textureId;
    }

    public void setMatrix(float[] matrix){
        this.matrix=matrix;
    }

    public float[] getMatrix(){
        return matrix;
    }


}
