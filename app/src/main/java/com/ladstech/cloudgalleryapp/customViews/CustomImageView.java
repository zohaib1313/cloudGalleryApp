package com.ladstech.cloudgalleryapp.customViews;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.ladstech.cloudgalleryapp.R;

public class CustomImageView extends View{
    public CustomImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupPaint();

    }

    // setup initial color
    private final int paintColor = Color.RED;
    // defines paint and canvas
    private Paint drawPaint;

    private Bitmap buffer;
    private Canvas board;

    Path path = new Path();

    public void drawBitmap(Bitmap bitmap) {
        board.drawBitmap(bitmap, 0, 0, drawPaint);
    }
    // Setup paint with color and stroke styles
    private void setupPaint() {
        drawPaint = new Paint();
        drawPaint.setColor(paintColor);
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(5);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);
    }


    @Override
    protected void onDraw(Canvas canvas) {



        path.moveTo(getWidth() * 0.1f, 0.0f);
        path.lineTo(getWidth()*0.9f, 0.0f);
///control + end
        path.quadTo(getWidth(),0.0f,getWidth(),getHeight()*0.3f);
        path.lineTo(getWidth(),getHeight()*0.7f);


        path.quadTo(getWidth(),getHeight()*0.8f,getWidth()*0.9f,getHeight()*0.8f);
        path.lineTo(getWidth()*0.1f,getHeight()*0.8f);
        path.quadTo(getWidth()*0.05f,getHeight()*0.8f,0,getHeight());
        path.lineTo(0,getHeight()*0.2f);
        path.quadTo(0,0,getWidth()*0.1f,0);

      //  path.lineTo(0,getHeight());
//        RectF rect =new RectF(0f, 0f, 500f, 300f);
//        path.addArc(rect, 270f, 90f);
        canvas.drawPath(path, drawPaint);
//        canvas.drawBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.flag_peru),0,0,null);




//        Bitmap imageBitmap = getBitmapResource();
//        Bitmap temporaryBitmap;
//        if (imageBitmap != null) {
//            temporaryBitmap = imageBitmap.copy(Bitmap.Config.ARGB_8888, true);
//        } else {
//            return;
//        }
//
//        int w = getWidth();
//
//        Bitmap roundBitmap = getRoundedCroppedBitmap(cropBitmap(temporaryBitmap), w);
//        canvas.drawBitmap(roundBitmap, 0, 0, null);

    }
}
