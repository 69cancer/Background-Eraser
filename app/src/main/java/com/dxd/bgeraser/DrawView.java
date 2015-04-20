package com.dxd.bgeraser;
import android.annotation.SuppressLint;
import android.app.usage.UsageEvents;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.util.LruCache;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class DrawView extends View {
    public Bitmap  mBitmap;
    public Canvas  mCanvas;
    private Path    mPath;


    private Paint   mBitmapPaint;
    private Paint   mPaint;
    private Bitmap imgF;
    private static float MIN_ZOOM = 1f;
    private static float MAX_ZOOM = 5f;
    private float scaleFactor = 1.f;
    private ScaleGestureDetector detector;

    public DrawView(Context c, AttributeSet attrs) {
        super(c, attrs);
        detector = new ScaleGestureDetector(getContext(), new ScaleListener());

        mPath = new Path();
        mBitmapPaint = new Paint(Paint.DITHER_FLAG);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(Color.parseColor("#000000"));
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(9);

    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        canvas.scale(scaleFactor, scaleFactor);
        // Image manipulation
        /*{

            Bitmap crp = MainActivity.gImg;



            int srcWidth = crp.getWidth();
            int srcHeight = crp.getHeight();
            int targetW = canvas.getWidth();
        // Only scale if the source is big enough. This code is just trying to fit a image into a certain width.
            if (targetW > srcWidth)
                targetW = srcWidth;


         // Calculate the correct inSampleSize/scale value. This helps reduce memory use. It should be a power of 2

            int inSampleSize = 1;
            while (srcWidth / 2 > targetW) {
                srcWidth /= 2;
                srcHeight /= 2;
                inSampleSize *= 2;
            }

            float desiredScale = (float) targetW / srcWidth;

        // Decode with inSampleSize


        // Resize
            Matrix matrix = new Matrix();
            matrix.postScale(desiredScale, desiredScale);
            imgF = Bitmap.createBitmap(crp, 0, 0, crp.getWidth(), crp.getHeight(), matrix, true);

        }*/
        imgF = Bitmap.createScaledBitmap(MainActivity.gImg,canvas.getWidth(),canvas.getHeight(),true);
        int centerX = (canvas.getWidth() - imgF.getWidth()) / 2;
        int centreY = (canvas.getHeight()-imgF.getHeight()) / 2;
        canvas.drawColor(Color.parseColor("#000000"));
        canvas.drawBitmap(imgF,centerX,centreY,null);

        canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);



        canvas.drawPath(mPath, mPaint);
        canvas.restore();
     }

    private float mX, mY;
    private static final float TOUCH_TOLERANCE = 4;

    private void touch_start(float x, float y) {

        mPath.reset();
        mPath.moveTo(x, y);
        mX = x;
        mY = y;



     }
    private void touch_move(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mPath.quadTo(mX, mY, (x + mX)/2, (y + mY)/2);
            mX = x;
            mY = y;


        }

    }


    private void touch_up() {
        mPath.lineTo(mX, mY);
        // commit the path to our offscreen
        mCanvas.drawPath(mPath, mPaint);
        // kill this so we don't double draw
        mPath = new Path();



    }

    public void im(float X,float Y){
             int colorToReplace = getBitmap().getPixel((int)X,(int)Y);
            int width = getBitmap().getWidth();
            int height =getBitmap().getHeight();
            int[] pixels = new int[width * height];
            // get pixel array from source
            getBitmap().getPixels(pixels, 0, width, 0, 0, width, height);

            Bitmap bmOut = Bitmap.createBitmap(width, height, getBitmap().getConfig());

            int A, R, G, B,rA,rR,rG,rB;
            int pixel;
            A = Color.alpha(Color.BLACK);
            R = Color.red(Color.BLACK);
            G = Color.green(Color.BLACK);
            B = Color.blue(Color.BLACK);
             rA = Color.alpha(colorToReplace);
             rR = Color.red( colorToReplace);
             rG = Color.green(colorToReplace);
             rB = Color.blue(colorToReplace);


            // iteration through pixels
            for (int y = 0; y < height; ++y) {
                for (int x = 0; x < width; ++x) {
                    // get current index in 2D-matrix
                    int index = y * width + x;
                    pixel = pixels[index];
                   int rrA = Color.alpha(pixel);
                   int rrR = Color.red(pixel);
                   int rrG = Color.green(pixel);
                    int rrB = Color.blue(pixel);

                    if( rA-20 < rrA && rrA < rA+20 && rR-20 < rrR && rrR < rR+20 &&
                            rG-20 < rrG && rrG< rG+20 && rB-20 < rrB && rrB < rB+20 ){

                        //change A-RGB individually
                        A = Color.alpha(Color.BLACK);
                        R = Color.red(Color.BLACK);
                        G = Color.green(Color.BLACK);
                        B = Color.blue(Color.BLACK);
                        pixels[index] = Color.argb(A,R,G,B);
                    /*or change the whole color
                    pixels[index] = colorThatWillReplace;*/
                    }
                }
            }
            bmOut.setPixels(pixels, 0, width, 0, 0, width, height);
            mCanvas.drawBitmap(bmOut,0,0,null);

        }




    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();


        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touch_start(x, y);
                im(x,y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:

                touch_move(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                touch_up();
                invalidate();
                break;
        }
        if(event.getAction()==MotionEvent.ACTION_UP && event.getAction()==MotionEvent.ACTION_DOWN)
            detector.onTouchEvent(event);
        return true;
    }

    public Bitmap getBitmap()
    {
        //this.measure(100, 100);
        //this.layout(0, 0, 100, 100);
        this.setDrawingCacheEnabled(true);
        this.buildDrawingCache();
        Bitmap bmp = Bitmap.createBitmap(this.getDrawingCache());
        this.setDrawingCacheEnabled(false);


        return bmp;
    }

    public void setStrokeSize(int size){
        float fSize = (float) size;
        this.mPaint.setStrokeWidth(fSize);
    }



    public void clear(){
        mBitmap.eraseColor(Color.GREEN);
        invalidate();
        System.gc();

    }
    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

        @Override

        public boolean onScale(ScaleGestureDetector detector) {

            scaleFactor *= detector.getScaleFactor();

            scaleFactor = Math.max(MIN_ZOOM, Math.min(scaleFactor, MAX_ZOOM));
            invalidate();

            return true;

        }

    }

}