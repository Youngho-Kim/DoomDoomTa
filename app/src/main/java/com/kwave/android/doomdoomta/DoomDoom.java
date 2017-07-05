package com.kwave.android.doomdoomta;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.media.AudioTrack;
import android.media.SoundPool;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;


/**
 * Created by kwave on 2017-07-05.
 */

public class DoomDoom extends View {

    public static float PAD_PADDING_RATIO = 0.025f;
    public static int NUMB_OF_ROW = 4;
    public static int NUMB_OF_COL = 4;
    Rect[] pads = new Rect[NUMB_OF_COL*NUMB_OF_ROW];
    Paint[] paints = new Paint[pads.length];
    int[] sounds = new int[pads.length];

    private Paint padPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint pressedPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    SoundPool mSoundPool;
    public DoomDoom(Context context, @Nullable AttributeSet attrs){
        super(context, attrs);
        for(int i= 0; i < pads.length; i++){
            pads[i] = new Rect();
        }

    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        prepareSoundPool();
        preparePaint();

    }
        private void preparePaint(){
            padPaint.setColor(Color.CYAN);
            pressedPaint.setColor(Color.BLUE);
            for(int i = 0; i< paints.length;i++){
                paints[i] = padPaint;
            }
        }

    public void prepareSoundPool(){
        mSoundPool = new SoundPool(8, AudioTrack.MODE_STREAM,0);
        sounds[1] = mSoundPool.load(getContext(), R.raw.aa,0);
        sounds[2] = mSoundPool.load(getContext(), R.raw.bb,0);
        sounds[3] = mSoundPool.load(getContext(), R.raw.cc,0);
        sounds[4] = mSoundPool.load(getContext(), R.raw.dd,0);
        sounds[5] = mSoundPool.load(getContext(), R.raw.ee,0);
        sounds[6] = mSoundPool.load(getContext(), R.raw.ff,0);
        sounds[7] = mSoundPool.load(getContext(), R.raw.gg,0);
        sounds[8] = mSoundPool.load(getContext(), R.raw.hh,0);
        sounds[9] = mSoundPool.load(getContext(), R.raw.ii,0);
        sounds[10] = mSoundPool.load(getContext(), R.raw.jj,0);
        sounds[11] = mSoundPool.load(getContext(), R.raw.kk,0);
        sounds[12] = mSoundPool.load(getContext(), R.raw.ll,0);
        sounds[13] = mSoundPool.load(getContext(), R.raw.mm,0);
        sounds[14] = mSoundPool.load(getContext(), R.raw.nn,0);
        sounds[15] = mSoundPool.load(getContext(), R.raw.oo,0);
        sounds[0] = mSoundPool.load(getContext(), R.raw.zz,0);




    }
    // 이미지의 사이즈가 내가 만들 수 없는 사이즈인 경우 사용자에게 알려줄때 사용
//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//
//    }

    // Rect에 대한 사이즈를 확정할 것.
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
//        int sum_pad_width = (int) (getWidth() - (1-(NUMB_OF_COL + 1) * PAD_PADDING_RATIO));
        int padding_width = (int) (getWidth()* PAD_PADDING_RATIO);
        int sum_pad_width = getWidth() - (NUMB_OF_COL + 1) *padding_width;
        int pad_width = sum_pad_width/NUMB_OF_COL;

        int padding_height = (int) (getHeight()* PAD_PADDING_RATIO);
        int sum_pad_height = (getHeight() - (NUMB_OF_ROW + 1) *padding_height);
        int pad_height = sum_pad_height/NUMB_OF_ROW;
        int col_idx = 0;
        int row_idx = 0;
        for(int i=0; i < pads.length;i++){
            col_idx = i % NUMB_OF_COL;
            row_idx = i / NUMB_OF_ROW;
            pads[i].set(
                    (col_idx + 1) * padding_width + col_idx * pad_width,
                    (row_idx + 1) * padding_height + row_idx * pad_height,
                    (col_idx + 1) * padding_width + (col_idx + 1) * pad_width,
                    (row_idx + 1) * padding_height + (row_idx + 1) * pad_height

            );
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for(Rect pad: pads){
            canvas.drawRect(pad,padPaint);
        }
        for(int i=0; i<pads.length;i++){
            canvas.drawRect(pads[i], paints[i]);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mSoundPool.release();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();
        switch(action){
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                invalidatePad(new Point((int) event.getX(), (int)event.getY()),true);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_CANCEL:
                invalidatePad(new Point((int) event.getX(), (int)event.getY()),false);
                break;
        }
        return true;

    }

    private void invalidatePad(Point point, boolean down) {
        for (int i = 0; i < pads.length; i++) {
            if (pads[i].contains(point.x,point.y)) {
                paints[i] = (down)? pressedPaint : padPaint;
                if(down){
                    mSoundPool.play(sounds[i],1f,1f,0,0,1);
                }
                invalidate(pads[i]);
                return;
            }
        }
    }

}
