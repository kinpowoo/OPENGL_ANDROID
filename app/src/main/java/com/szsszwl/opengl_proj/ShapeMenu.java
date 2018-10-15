package com.szsszwl.opengl_proj;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.szsszwl.opengl_proj.pattern.Circle;
import com.szsszwl.opengl_proj.pattern.Cone;
import com.szsszwl.opengl_proj.pattern.ConeWithoutCircle;
import com.szsszwl.opengl_proj.pattern.Cube;
import com.szsszwl.opengl_proj.pattern.Cylinder;
import com.szsszwl.opengl_proj.pattern.Oval;
import com.szsszwl.opengl_proj.pattern.Sphere;
import com.szsszwl.opengl_proj.pattern.Square;
import com.szsszwl.opengl_proj.pattern.SquareCoor;
import com.szsszwl.opengl_proj.pattern.Triangle;
import com.szsszwl.opengl_proj.pattern.Triangle3D;
import com.szsszwl.opengl_proj.pattern.Triangle3DWithTexture;

/**
 * Created by DeskTop29 on 2018/10/10.
 */

public class ShapeMenu extends Activity {

    ListView listView;
    ArrayAdapter arrayAdapter;

    String[] shapes = new String[]{"Circle","Cone","ConeWithoutCircle","Cube","Cylinder","Oval",
            "Sphere","Square","SquareCoor","Triangle","Triangle3D","TriangleTexture"};

    Class[] shapeClass = new Class[]{Circle.class,Cone.class,ConeWithoutCircle.class,Cube.class,
            Cylinder.class,Oval.class, Sphere.class,Square.class,SquareCoor.class,Triangle.class,
            Triangle3D.class,Triangle3DWithTexture.class};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_list);

        listView = (ListView) findViewById(R.id.lv);

        arrayAdapter = new ArrayAdapter(this,R.layout.activity_menu_list_item,R.id.shape,shapes);

        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent();
                intent.putExtra("name",shapeClass[position]);
                setResult(RESULT_OK,intent);
                finish();
            }
        });
    }
}
