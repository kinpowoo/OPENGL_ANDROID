package com.szsszwl.opengl_proj.vary;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.szsszwl.opengl_proj.R;

/**
 * Created by wuwang on 2016/10/30
 */

public class VaryActivity extends AppCompatActivity {

    private GLSurfaceView mGLView;
    private VaryRender render;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opengl);
        initGL();
    }

    public void initGL(){
        mGLView= (GLSurfaceView) findViewById(R.id.mGLView);
        mGLView.setEGLContextClientVersion(2);
        render=new VaryRender(getResources());
        mGLView.setRenderer(render);
        mGLView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    @Override
    public void onBackPressed() {
        Log.i("tag","物体Matrix变换的onBackPressed被触发");
        finish();
        super.onBackPressed();

    }
}
