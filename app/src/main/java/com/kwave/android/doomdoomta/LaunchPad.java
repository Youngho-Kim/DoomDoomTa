package com.kwave.android.doomdoomta;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.util.Log;

import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import java.util.Locale;
import java.util.ArrayList;

import static android.media.AudioAttributes.CONTENT_TYPE_MUSIC;


public class LaunchPad extends View {

    private static final String TAG = "LAUNCHPAD" ;
    public static int NUMB_OF_ROW = 4;
    public static int NUMB_OF_COL = 4;
    private int NUMB_PAINT = 4;
    public static float pads_pading_ratio = 0.025f;

    Rect background = new Rect();
    Rect[] pads = new Rect[NUMB_OF_COL * NUMB_OF_ROW];
    Paint[] padPaints = new Paint[pads.length];
    Paint[] pressedPadPaint = new Paint[NUMB_PAINT];
    int[] padSounds = new int[pads.length];
    Paint backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    Paint padPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    ArrayList<MotionEvent> playList = new ArrayList<>();

    SoundPool mSoundPool;

    public LaunchPad(Context context) {
        this(context , null);
    }

    public LaunchPad(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        preparePads();
        preparePaints();
        prepareSoundPool();
    }

    private void prepareSoundPool() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mSoundPool = new SoundPool.Builder()
                    .setMaxStreams(8)
                    .setAudioAttributes(
                            new AudioAttributes.Builder()
                                    .setContentType(CONTENT_TYPE_MUSIC)
                                    .setUsage(AudioAttributes.USAGE_GAME)
                                    .build())
                    .build();
        }else{
            mSoundPool = new SoundPool(8, AudioManager.STREAM_MUSIC,0);
        }
        padSounds[1] = mSoundPool.load(getContext(),R.raw.aa,0);
        padSounds[2] = mSoundPool.load(getContext(),R.raw.bb,0);
        padSounds[3] = mSoundPool.load(getContext(),R.raw.cc,0);
        padSounds[4] = mSoundPool.load(getContext(),R.raw.dd,0);
        padSounds[5] = mSoundPool.load(getContext(),R.raw.ee,0);
        padSounds[6] = mSoundPool.load(getContext(),R.raw.ff,0);
        padSounds[7] = mSoundPool.load(getContext(),R.raw.gg,0);
        padSounds[8] = mSoundPool.load(getContext(),R.raw.hh,0);
        padSounds[9] = mSoundPool.load(getContext(),R.raw.ii,0);
        padSounds[10] = mSoundPool.load(getContext(),R.raw.jj,0);
        padSounds[11] = mSoundPool.load(getContext(),R.raw.kk,0);
        padSounds[12] = mSoundPool.load(getContext(),R.raw.ll,0);
        padSounds[13] = mSoundPool.load(getContext(),R.raw.mm,0);
        padSounds[14] = mSoundPool.load(getContext(),R.raw.nn,0);
        padSounds[15] = mSoundPool.load(getContext(),R.raw.oo,0);
        padSounds[0] = mSoundPool.load(getContext(),R.raw.zz,0);


    }

    private void preparePaints() {
        backgroundPaint.setColor(Color.DKGRAY);
        padPaint.setColor(Color.LTGRAY);
        pressedPadPaint[0] = new Paint(Paint.ANTI_ALIAS_FLAG);
        pressedPadPaint[0].setColor(Color.MAGENTA);
        pressedPadPaint[1] = new Paint(Paint.ANTI_ALIAS_FLAG);
        pressedPadPaint[1].setColor(Color.CYAN);
        pressedPadPaint[2] = new Paint(Paint.ANTI_ALIAS_FLAG);
        pressedPadPaint[2].setColor(Color.GREEN);
        pressedPadPaint[3] = new Paint(Paint.ANTI_ALIAS_FLAG);
        pressedPadPaint[3].setColor(Color.YELLOW);

    }

    private void preparePads() {
        for (int i = 0; i < pads.length; i++) {
            pads[i] = new Rect();
            padPaints[i] = padPaint;
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        background.set(0,0,getWidth(),getHeight());
        int pad_padding_width = (int) (pads_pading_ratio*getWidth());
        int pad_padding_height = (int) (pads_pading_ratio*getHeight());
        int sum_pad_width =(getWidth() - (NUMB_OF_COL+1) * pad_padding_width );
        int sum_pad_height  =(getHeight() - (NUMB_OF_ROW+1) * pad_padding_height);
        int pad_width = sum_pad_width/NUMB_OF_COL;
        int pad_height = sum_pad_height/NUMB_OF_ROW;
        for (int i = 0; i < pads.length; i++) {
            int col_idx = i % NUMB_OF_COL;
            int row_idx = i / NUMB_OF_ROW;
            pads[i].set(
                    (col_idx+1) * pad_padding_width + col_idx *pad_width,
                    (row_idx +1) * pad_padding_height + row_idx *pad_height ,
                    (col_idx+1) * pad_padding_width + (col_idx +1)*pad_width,
                    (row_idx +1) * pad_padding_height + (row_idx +1)*pad_height
            );
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRect(background,backgroundPaint);
        for (int i = 0; i < pads.length; i++) {
            canvas.drawRect(pads[i], padPaints[i]);
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        playList.add(MotionEvent.obtain(event));
        int pointerCount = event.getPointerCount();
        int maskedAction = event.getActionMasked();
        Point point = new Point((int) event.getX(), (int) event.getY());
        switch (maskedAction) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                invalidatePad(point, true);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                invalidatePad(point, false);
        }
//        testActionId(event);
        return true;
    }
    public void playPlayList(){
        final ArrayList<MotionEvent> fixedPlayList = new ArrayList<>(playList);
        new AsyncTask<String, MotionEvent, String>(){

            @Override
            protected String doInBackground(String... params) {

                for (MotionEvent motionEvent : fixedPlayList) {
                    try {
                        Thread.sleep(40);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    publishProgress(motionEvent);
                }
                return null;
            }
            @Override
            protected void onProgressUpdate(MotionEvent... values) {
                super.onProgressUpdate(values);
                LaunchPad.this.dispatchTouchEvent(values[0]);
            }
        }.execute();
    }

    private void invalidatePad(Point point, boolean down) {
        for (int i = 0; i < pads.length; i++) {
            if(pads[i].contains(point.x,point.y)){
                updatePaint(i, down);
                invalidate(pads[i]);
                return;
            }
        }
    }

    private void updatePaint(int i, boolean down) {
        padPaints[i] = (down) ? randomPaint() : padPaint;
        if(down){
            mSoundPool.play(padSounds[i],1.0f,1.0f,0,0,1);
        }
    }

    private Paint randomPaint(){
        int random = (((int) (Math.random()*10))% NUMB_PAINT);
        return pressedPadPaint[random];
    }

    private void testActionId(MotionEvent event) {
        int actionIndex = event.getActionIndex();
        int action = event.getAction();
        int maskedAction = event.getActionMasked();
        if(maskedAction != 2)
            Log.e(TAG,String.format(Locale.getDefault(),"action: %d, actionIndex: %d, maskedAction: %d",action,actionIndex,maskedAction));
    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mSoundPool.release();
    }
}
