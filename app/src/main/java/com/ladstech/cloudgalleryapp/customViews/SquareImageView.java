package com.ladstech.cloudgalleryapp.customViews;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;


/**
 * A customized squared image view class.
 * <p>
 * Extra functions:
 * {@link #removeBorder(boolean)}
 * {@link #setBorderWidth(int)}
 * {@link #setBorderColor(int)}
 * {@link @setBitmap}
 * <p/>
 * The rest of the things is exactly the same as {@link ImageView}
 * <p/>
 * Created by amintavassolian on 15-02-12.
 */

public class SquareImageView extends androidx.appcompat.widget.AppCompatImageView {

    private static final float DEFAULT_BOARDER_STROKE = 5;

    private int borderColor;
    private int borderWidth;

    private Bitmap bitmap;

    private boolean removeBorder = false;

    public SquareImageView(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    @Override
    protected void onDraw(Canvas canvas) {

        Bitmap imageBitmap = getBitmapResource();
        Bitmap temporaryBitmap;
        if (imageBitmap != null) {
            temporaryBitmap = imageBitmap.copy(Bitmap.Config.ARGB_8888, true);
        } else {
            return;
        }

        int w = getWidth();

        Bitmap roundBitmap = getRoundedCroppedBitmap(cropBitmap(temporaryBitmap), getWidth(), getHeight());
        canvas.drawBitmap(roundBitmap, 0, 0, null);
    }

    private Bitmap getBitmapResource() {
        if (bitmap == null) {
            Drawable drawable = getDrawable();
            if (drawable == null) {
                return null;
            }

            if (getWidth() == 0 || getHeight() == 0) {
                return null;
            }

            return ((BitmapDrawable) drawable).getBitmap();
        } else {
            return bitmap;
        }
    }

    private Bitmap cropBitmap(Bitmap sourceBmp) {
        Bitmap outputBmp;
        if (sourceBmp.getWidth() > sourceBmp.getHeight()) {
            outputBmp = Bitmap.createBitmap(sourceBmp, 0, 0, sourceBmp.getHeight(), sourceBmp.getHeight());
        } else if (sourceBmp.getWidth() < sourceBmp.getHeight()) {
            outputBmp = Bitmap.createBitmap(sourceBmp, 0, 0, sourceBmp.getWidth(), sourceBmp.getWidth());
        } else {
        outputBmp = sourceBmp;
        }

        return outputBmp;
    }

    private Bitmap getRoundedCroppedBitmap(Bitmap bitmap, int width, int height) {
        Bitmap finalBitmap;
//        if (bitmap.getWidth() != width || bitmap.getHeight() != width) {
//            // Set the filter to false, because we don't need very smooth one! It's cheaper!
//        } else {
//            finalBitmap = bitmap;
//        }
        finalBitmap = Bitmap.createScaledBitmap(bitmap, width/2, height/3, true);
 //finalBitmap=tr
        Bitmap output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(output);

        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);

        Path path = new Path();
        path.moveTo(getWidth() * 0.1f, 0.0f);
        path.lineTo(getWidth() * 0.95f, 0.0f);
///control + end
        path.quadTo(getWidth(), 0.0f, getWidth(), getHeight() * 0.1f);
        path.lineTo(getWidth(), getHeight() * 0.7f);


        path.quadTo(getWidth(), getHeight() * 0.77f, getWidth() * 0.9f, getHeight() * 0.75f);
        path.lineTo(getWidth() * 0.1f, getHeight() * 0.75f);

        path.quadTo(getWidth() * 0.05f, getHeight() * 0.8f, 0, getHeight());
        path.lineTo(0, getHeight() * 0.1f);
        path.quadTo(0, 0, getWidth() * 0.05f, 0);

        //  path.lineTo(0,getHeight());
//        RectF rect =new RectF(0f, 0f, 500f, 300f);
//        path.addArc(rect, 270f, 90f);
        canvas.drawPath(path, paint);
        // It doesn't matter which color!
        //   paint.setColor(Color.WHITE);
        // canvas.drawRoundRect(new RectF(0, 0, finalBitmap.getWidth(), finalBitmap.getHeight()), 15, 15, paint);

        // The second drawing should only be visible of if overlapping with the first
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(finalBitmap, 0, 0, paint);


        return output;
    }

    private Paint getBorderPaint() {
        Paint borderPaint = new Paint();
        if (borderColor != 0) {
            borderPaint.setColor(borderColor);
        } else {
            borderPaint.setColor(Color.WHITE);
        }
        if (borderWidth != 0) {
            borderPaint.setStrokeWidth(borderWidth);
        } else {
            borderPaint.setStrokeWidth(DEFAULT_BOARDER_STROKE);
        }

        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setAntiAlias(true);

        return borderPaint;
    }

    /**
     * A method to set the bitmap for the image view
     *
     * @param bmp The target bitmap
     */
    public void setBitmap(Bitmap bmp) {
        this.bitmap = bmp;
    }

    /**
     * A method to se the color of the border of the image view
     *
     * @param colorResource The resource id of the favourite color
     */
    public void setBorderColor(int colorResource) {
        this.borderColor = colorResource;
    }

    /**
     * A method to set the thickness of the border of the image view
     *
     * @param width The width of the border stroke in pixels
     */
    public void setBorderWidth(int width) {
        this.borderWidth = width;
    }

    /**
     * A method to set whether show the image view with border or not
     *
     * @param removeBorder true to remove the border of the iamge view, otherwise it will have a border
     */
    public void removeBorder(boolean removeBorder) {
        this.removeBorder = removeBorder;
    }

}