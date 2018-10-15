package com.szsszwl.opengl_proj.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;


import com.szsszwl.opengl_proj.R;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by DeskTop29 on 2018/5/16.
 */

public class HorizontalAutoScrollLayout extends ViewGroup {

    int rowCount;
    float horizontalMargin;


    LayoutInflater layoutInflater;

    BaseAdapter adapter;
    Queue<View> viewSet;        //一个先进先出的视图队列

    Runnable scroll = new Runnable() {
        @Override
        public void run() {
            loadView();
            scrollCircle();
        }
    };


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };


    public HorizontalAutoScrollLayout(Context context) {
        this(context, null);
    }

    public HorizontalAutoScrollLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HorizontalAutoScrollLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray mTypedArray = context.obtainStyledAttributes(attrs,
                R.styleable.HorizontalAutoScrollLayout);

        rowCount = mTypedArray.getInt(R.styleable.HorizontalAutoScrollLayout_rowCount, 3);
        horizontalMargin = mTypedArray.getDimension(R.styleable.HorizontalAutoScrollLayout_horizontalMargin, 10);


        layoutInflater = LayoutInflater.from(context);
        viewSet = new LinkedBlockingQueue<>();

        mTypedArray.recycle();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //获得此ViewGroup上级容器为其推荐的宽和高，以及计算模式
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);
        int layoutWidth = 0;
        int layoutHeight = 0;
        // 计算出所有的childView的宽和高
        measureChildren(widthMeasureSpec, heightMeasureSpec);

        int cWidth = 0;
        int cHeight = 0;
        int count = getChildCount();

        if (widthMode == MeasureSpec.EXACTLY) {
            //如果布局容器的宽度模式是确定的（具体的size或者match_parent），直接使用父窗体建议的宽度
            layoutWidth = sizeWidth;
        } else {
            //如果是未指定或者wrap_content，我们都按照包裹内容做，宽度方向上只需要拿到所有子控件中宽度做大的作为布局宽度
            for (int i = 0; i < count; i++) {
                View child = getChildAt(i);
                cWidth = child.getMeasuredWidth();
                //获取子控件最大宽度
                layoutWidth += cWidth;
                if (layoutWidth > sizeWidth) {
                    layoutWidth = sizeWidth;
                    break;
                }
            }
        }
        //高度选择高度最大的那一个
        if (heightMode == MeasureSpec.EXACTLY) {
            layoutHeight = sizeHeight;
        } else {
            for (int i = 0; i < count; i++) {
                View child = getChildAt(i);
                cHeight = child.getMeasuredHeight();
                layoutHeight = cHeight > layoutHeight ? cHeight : layoutHeight;
            }
        }

        // 测量并保存layout的宽高
        layoutWidth += (getPaddingLeft() + getPaddingRight() + (rowCount - 1) * horizontalMargin);
        layoutHeight += (getPaddingTop() + getPaddingBottom());
        setMeasuredDimension(layoutWidth, layoutHeight);
    }

    public int getRowCount() {
        return rowCount;
    }

    public float getHorizontalMargin() {
        return horizontalMargin;
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        final int count = getChildCount();
        int childMeasureWidth = 0;
        int childMeasureHeight = 0;
        int layoutWidth = getPaddingLeft();    // 容器已经占据的宽度
        int layoutHeight = getPaddingTop();   // 容器已经占据的宽度

        int finalCount = count < rowCount ? count : rowCount;

        for (int i = 0; i < finalCount; i++) {
            View child = getChildAt(i);
            //注意此处不能使用getWidth和getHeight，这两个方法必须在onLayout执行完，才能正确获取宽高
            childMeasureWidth = child.getMeasuredWidth();
            childMeasureHeight = child.getMeasuredHeight();
            if (layoutWidth < getWidth()) {
                //如果一行没有排满，继续往右排列
                left = layoutWidth;
                right = left + childMeasureWidth;
                top = layoutHeight;
                bottom = top + childMeasureHeight;
            }
            if (i != rowCount - 1) {
                layoutWidth += (childMeasureWidth + horizontalMargin);  //宽度累加
            } else {
                layoutWidth += childMeasureWidth;  //宽度累加
            }

            //确定子控件的位置，四个参数分别代表（左上右下）点的坐标值
            child.layout(left, top, right, bottom);
        }
    }


    public void setAdapter(BaseAdapter adapter) {
        this.adapter = adapter;
        insertView();


        this.adapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                Log.i("view", "监听到adapter数据发生变化");
                insertView();
                super.onChanged();
            }
        });
    }


    private void insertView() {
        //在装载视图前先将循环停止
        handler.removeCallbacks(scroll);

        //插入视图前先清空视图队列
        int viewSetSize = viewSet.size();
        for (int j = 0; j < viewSetSize; j++) {
            viewSet.poll();
        }

        for (int i = 0; i < adapter.getCount(); i++) {
            viewSet.offer(adapter.getView(i, null, this));
        }

        if (viewSet.size() > 0) {
            scrollCircle();
        }
    }


    private void scrollCircle() {
        handler.postDelayed(scroll, 3000);
    }


    private void loadView() {
        removeAllViews();

        if (viewSet.size() >= rowCount) {
            for (int i = 0; i < rowCount; i++) {
                View temp = viewSet.poll();
                addView(temp);
                viewSet.offer(temp);
            }

            Log.i("scroll","loadView后viewSet的size:"+viewSet.size());

        } else if (viewSet.size() > 0) {
            int size = viewSet.size();
            for (int i = 0; i < size; i++) {
                View temp = viewSet.poll();
                addView(temp);
                viewSet.offer(temp);
            }
            Log.i("scroll","loadView后viewSet的size:"+viewSet.size());
        }
        requestLayout();
    }


    //停止滚动
    public void removeCallback() {
        handler.removeCallbacks(scroll);
    }


    public void moveView(int offsetX, int offsetY) {
        int left = this.getLeft() + offsetX;
        int top = this.getTop() + offsetY;
        int right = this.getRight() + offsetX;
        int bottom = this.getBottom() + offsetY;
        this.layout(left, top, right, bottom);
    }


}
