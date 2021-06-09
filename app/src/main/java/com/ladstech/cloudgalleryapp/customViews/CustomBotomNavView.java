package com.ladstech.cloudgalleryapp.customViews;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

public class CustomBotomNavView extends View {

    private Paint mPaint;


    public CustomBotomNavView(Context context) {
        super(context);
        setWillNotDraw(false);
        createPaint();
    }

    public CustomBotomNavView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setWillNotDraw(false);
        createPaint();
    }

    public CustomBotomNavView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setWillNotDraw(false);
        createPaint();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);


    }



    private void createPaint(){
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.RED);
        mPaint.setStrokeWidth(5f);
    }
}