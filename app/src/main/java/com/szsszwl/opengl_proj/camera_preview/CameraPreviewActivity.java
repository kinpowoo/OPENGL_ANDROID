package com.szsszwl.opengl_proj.camera_preview;

import android.Manifest;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.szsszwl.opengl_proj.R;

/**
 * Created by DeskTop29 on 2018/6/12.
 */

public class CameraPreviewActivity extends AppCompatActivity{

    //CameraGLSurfaceView mCameraView;

    CameraSurfaceHolderView holderView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preview_activity);


        //mCameraView = (CameraGLSurfaceView) findViewById(R.id.preview_box);

        holderView = (CameraSurfaceHolderView) findViewById(R.id.preview_holder);

        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA
                ,Manifest.permission.WRITE_EXTERNAL_STORAGE},0x22);

    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //newConfig.orientation获得当前屏幕状态是横向或者竖向
        //Configuration.ORIENTATION_PORTRAIT 表示竖向
        //Configuration.ORIENTATION_LANDSCAPE 表示横屏
        if(newConfig.orientation==Configuration.ORIENTATION_PORTRAIT){

        }
        if(newConfig.orientation==Configuration.ORIENTATION_LANDSCAPE) {

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //mCameraView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //mCameraView.onPause();
    }






    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("切换摄像头").setTitle("切换摄像头").setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String name=item.getTitle().toString();
        if(name.equals("切换摄像头")){
            holderView.switchCamera();
        }
        return super.onOptionsItemSelected(item);
    }

}
