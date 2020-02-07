package com.example.thinkanddraw.custom_view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class MyFreeDrwable extends FrameLayout {
    private Bitmap mBitmap;
    private ArrayList<PathHolder> mPathList;
    private COLOUR mCurrentColour;

    public enum COLOUR {
        RED, BLUE, GREEN;
    }

    public MyFreeDrwable(Context context) {
        super(context);
        init();
    }

    private void init() {
        mPathList = new ArrayList<>();
        mCurrentColour = COLOUR.RED;
    }

    public MyFreeDrwable(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mPathList != null && mPathList.size() > 0) {
            for (PathHolder holder : mPathList) {
                canvas.drawPath(holder.getPath(), holder.getmPaint());
            }
        }
        super.onDraw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                onDown(event);
                return true;
            case MotionEvent.ACTION_MOVE:
                onMove(event);
                invalidate();
                return true;

        }
        return super.onTouchEvent(event);
    }

    private void onDown(MotionEvent event) {
        PathHolder pathHolder = new PathHolder();
        pathHolder.getPath().moveTo(event.getX(), event.getY());
        mPathList.add(pathHolder);
    }

    private void onMove(MotionEvent event) {
        mPathList.get(mPathList.size() - 1).getPath().lineTo(event.getX(), event.getY());
    }

    public void captureChanges() {
        setDrawingCacheEnabled(true);
        mBitmap = Bitmap.createBitmap(getDrawingCache());
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        mBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        setDrawingCacheEnabled(false);
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }

    public void undoLastChange() {
        if (mPathList != null && mPathList.size() > 0) {
            mPathList.remove(mPathList.size() - 1);
            invalidate();
        }
    }

    public void setBrushColourToRed() {
        mCurrentColour = COLOUR.RED;
    }

    public void setBrushColourToBlue() {
        mCurrentColour = COLOUR.BLUE;
    }

    public void setBrushColourToGreen() {
        mCurrentColour = COLOUR.GREEN;
    }

    private class PathHolder {
        public Paint getmPaint() {
            return mPaint;
        }

        public Path getPath() {
            return mPath;
        }

        private Paint mPaint;
        private Path mPath;

        public PathHolder() {
            mPaint = new Paint();
            mPath = new Path();
            mPaint.setAntiAlias(true);
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeJoin(Paint.Join.ROUND);
            mPaint.setStrokeWidth(5);

            switch (mCurrentColour) {
                case RED:
                    mPaint.setARGB(220, 255, 43, 43);
                    break;
                case BLUE:
                    mPaint.setARGB(220, 50, 43, 255);
                    break;
                case GREEN:
                    mPaint.setARGB(220, 50, 255, 43);
                    break;
            }
        }
    }
}
