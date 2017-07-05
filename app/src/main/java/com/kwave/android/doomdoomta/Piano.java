package com.kwave.android.doomdoomta;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.MotionEvent;
import android.view.View;

import java.util.Arrays;
import java.util.Locale;
import static android.media.AudioAttributes.CONTENT_TYPE_MUSIC;
/**
 * Created by kwave on 2017-07-05.
 */

public class Piano extends View {

    public static final String TAG = "PIANO";
    public static final int MAX_FINGERS = 5;
    public static final int WHITE_KEYS_COUNT = 7;
    public static final int BLACK_KEYS_COUNT = 5;
    public static final float BLACK_TO_WHITE_WIDTH_RATIO = 0.625f;
    public static final float BLACK_TO_WHITE_HEIGHT_RATIO = 0.54f;

    private Paint mWhiteKeyPaint, mBlackKeyPaint, mBlackKeyHitPaint, mWhiteKeyHitPaint;

    //두개이 int 좌표를 갖는 객체
    private Point[]  mFingerPoints = new Point[MAX_FINGERS];
    private int[] mFingerTones = new int[MAX_FINGERS];

    //MediaPlayer를 이용해 소리를 실행해주는 객체, 동시 실행 개수, 반복 주기, 등등을 편하게 관리할 수 있다.
    private SoundPool mSoundpool;
    //Map<Integer,Integer> 같은 객체
    private SparseIntArray mToneToIndexMap = new SparseIntArray();
    private Paint mCKeyPaint, mCSharpKeyPaint, mDKeyPaint,
            mDSharpKeyPaint, mEKeyPaint, mFKeyPaint,
            mFSharpKeyPaint, mGKeyPaint, mGSharpKeyPaint,
            mAKeyPaint, mASharpKeyPaint, mBKeyPaint;
    private Rect mCKey = new Rect(), mCSharpKey = new Rect(), mDKey = new Rect(),
            mDSharpKey = new Rect(), mEKey = new Rect(), mFKey = new Rect(),
            mFSharpKey = new Rect(), mGKey = new Rect(), mGSharpKey = new Rect(),
            mAKey = new Rect(), mASharpKey = new Rect(), mBKey = new Rect();

    private MotionEvent.PointerCoords mPointerCoords;

    private int width;
    private int height;


    public Piano(Context context) {
        this(context, null);
    }

    public Piano(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    //measure의 이전이라는 보장은 없음, draw의 이전이라는 보장은 있음
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mPointerCoords = new MotionEvent.PointerCoords();
        Arrays.fill(mFingerPoints, null);
        Arrays.fill(mFingerTones,-1);
        loadKeySamples(getContext());
        setupPaints();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        width = getWidth();
        height = getHeight();

        int whiteKeyWidth = width /WHITE_KEYS_COUNT;
        int blackKeyWidth = (int) (whiteKeyWidth * BLACK_TO_WHITE_WIDTH_RATIO);
        int blackKeyHeight = (int) (height * BLACK_TO_WHITE_HEIGHT_RATIO);

        mCKey.set(0,0,whiteKeyWidth, height);
        mCSharpKey.set(whiteKeyWidth -(blackKeyWidth/2),0,whiteKeyWidth+(blackKeyWidth/2),blackKeyHeight);
        mDKey.set(whiteKeyWidth, 0, 2 * whiteKeyWidth, height);
        mDSharpKey.set(2 * whiteKeyWidth - (blackKeyWidth / 2), 0,
                2 * whiteKeyWidth + (blackKeyWidth / 2), blackKeyHeight);
        mEKey.set(2 * whiteKeyWidth, 0, 3 * whiteKeyWidth, height);
        mFKey.set(3 * whiteKeyWidth, 0, 4 * whiteKeyWidth, height);
        mFSharpKey.set(4 * whiteKeyWidth - (blackKeyWidth / 2), 0,
                4 * whiteKeyWidth + (blackKeyWidth / 2), blackKeyHeight);
        mGKey.set(4 * whiteKeyWidth, 0, 5 * whiteKeyWidth, height);
        mGSharpKey.set(5 * whiteKeyWidth - (blackKeyWidth / 2), 0,
                5 * whiteKeyWidth + (blackKeyWidth / 2), blackKeyHeight);
        mAKey.set(5 * whiteKeyWidth, 0, 6 * whiteKeyWidth, height);
        mASharpKey.set(6 * whiteKeyWidth - (blackKeyWidth / 2), 0,
                6 * whiteKeyWidth + (blackKeyWidth / 2), blackKeyHeight);
        mBKey.set(6 * whiteKeyWidth, 0, 7 * whiteKeyWidth, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRect(mCKey, mCKeyPaint);
        canvas.drawRect(mDKey, mDKeyPaint);
        canvas.drawRect(mEKey, mEKeyPaint);
        canvas.drawRect(mFKey, mFKeyPaint);
        canvas.drawRect(mGKey, mGKeyPaint);
        canvas.drawRect(mAKey, mAKeyPaint);
        canvas.drawRect(mBKey, mBKeyPaint);

        canvas.drawRect(mCSharpKey, mCSharpKeyPaint);
        canvas.drawRect(mDSharpKey, mDSharpKeyPaint);
        canvas.drawRect(mFSharpKey, mFSharpKeyPaint);
        canvas.drawRect(mGSharpKey, mGSharpKeyPaint);
        canvas.drawRect(mASharpKey, mASharpKeyPaint);

    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //pointer  개수
        int pointerCount = event.getPointerCount();
        int cappedPointerCount = (pointerCount>MAX_FINGERS)? MAX_FINGERS : pointerCount;

        int actionIndex = event.getActionIndex();
        //원래 액션에는 Pointer 정보가 들어가나 Masked로 순수 Action을 추출함
        int action = event.getActionMasked();
        int id = event.getPointerId(actionIndex);

        if((action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_POINTER_DOWN) && id<MAX_FINGERS){
            mFingerPoints[id] = new Point((int) event.getX(actionIndex),(int) event.getY(actionIndex));
            String log = String.format(Locale.getDefault(),"pointerCount: %d, actionIndex: %d, action: %d id: %d",pointerCount,actionIndex,action,id);
            Log.e(TAG,log);
            for (int i = 0; i < cappedPointerCount; i++) {
                Log.e(TAG,"FOR");
                int index = event.findPointerIndex(i);
                if(mFingerPoints[i] != null && index != -1){
                    mFingerPoints[i].set((int)event.getX(index),(int)event.getY(index));
                    int tone = getToneForPoint(mFingerPoints[i]);
                    if(tone != mFingerTones[i] && tone != -1){
                        invalidateKey(mFingerTones[i]);
                        mFingerTones[i] = tone;
                        invalidateKey(mFingerTones[i]);
                        isKeyDown(i);
                    }
                }
            }
        }else if((action == MotionEvent.ACTION_UP|| action == MotionEvent.ACTION_POINTER_UP) && id<MAX_FINGERS){
            mFingerPoints[id] = null;
            invalidateKey(mFingerTones[id]);
            mFingerTones[id] = -1;

        }

        updatePaints();

        return true;
    }

    private void updatePaints() {
        mCKeyPaint = mWhiteKeyPaint;
        mDKeyPaint = mWhiteKeyPaint;
        mEKeyPaint = mWhiteKeyPaint;
        mFKeyPaint = mWhiteKeyPaint;
        mGKeyPaint = mWhiteKeyPaint;
        mAKeyPaint = mWhiteKeyPaint;
        mBKeyPaint = mWhiteKeyPaint;
        mCSharpKeyPaint = mBlackKeyPaint;
        mDSharpKeyPaint = mBlackKeyPaint;
        mFSharpKeyPaint = mBlackKeyPaint;
        mGSharpKeyPaint = mBlackKeyPaint;
        mASharpKeyPaint = mBlackKeyPaint;

        for (Point fingerPoint : mFingerPoints) {
            if (fingerPoint != null) {
                if (mCSharpKey.contains(fingerPoint.x, fingerPoint.y)) {
                    mCSharpKeyPaint = mBlackKeyHitPaint;
                } else if (mDSharpKey.contains(fingerPoint.x, fingerPoint.y)) {
                    mDSharpKeyPaint = mBlackKeyHitPaint;
                } else if (mFSharpKey.contains(fingerPoint.x, fingerPoint.y)) {
                    mFSharpKeyPaint = mBlackKeyHitPaint;
                } else if (mGSharpKey.contains(fingerPoint.x, fingerPoint.y)) {
                    mGSharpKeyPaint = mBlackKeyHitPaint;
                } else if (mASharpKey.contains(fingerPoint.x, fingerPoint.y)) {
                    mASharpKeyPaint = mBlackKeyHitPaint;
                } else if (mCKey.contains(fingerPoint.x, fingerPoint.y)) {
                    mCKeyPaint = mWhiteKeyHitPaint;
                } else if (mDKey.contains(fingerPoint.x, fingerPoint.y)) {
                    mDKeyPaint = mWhiteKeyHitPaint;
                } else if (mEKey.contains(fingerPoint.x, fingerPoint.y)) {
                    mEKeyPaint = mWhiteKeyHitPaint;
                } else if (mFKey.contains(fingerPoint.x, fingerPoint.y)) {
                    mFKeyPaint = mWhiteKeyHitPaint;
                } else if (mGKey.contains(fingerPoint.x, fingerPoint.y)) {
                    mGKeyPaint = mWhiteKeyHitPaint;
                } else if (mAKey.contains(fingerPoint.x, fingerPoint.y)) {
                    mAKeyPaint = mWhiteKeyHitPaint;
                } else if (mBKey.contains(fingerPoint.x, fingerPoint.y)) {
                    mBKeyPaint = mWhiteKeyHitPaint;
                }
            }
        }
    }

    private boolean isKeyDown(int i) {
        int key = getToneForPoint(mFingerPoints[i]);
        for (int i1 = 0; i1 < mFingerPoints.length; i1++) {
            if(i1 != i){
                Point fingerPoint = mFingerPoints[i1];
                if (fingerPoint != null) {
                    int otherKey = getToneForPoint(fingerPoint);
                    if(otherKey == key){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private int getToneForPoint(Point point) {
        if (mCKey.contains(point.x, point.y))
            return R.raw.c;
        if (mDKey.contains(point.x, point.y))
            return R.raw.d;
        if (mEKey.contains(point.x, point.y))
            return R.raw.e;
        if (mFKey.contains(point.x, point.y))
            return R.raw.f;
        if (mGKey.contains(point.x, point.y))
            return R.raw.g;
        if (mAKey.contains(point.x, point.y))
            return R.raw.a;
        if (mBKey.contains(point.x, point.y))
            return R.raw.b;

        return -1;
    }

    private void invalidateKey(int tone) {
        switch (tone){
            case R.raw.c:
                invalidate(mCKey);
                break;
            case R.raw.d:
                invalidate(mDKey);
                break;
            case R.raw.e:
                invalidate(mEKey);
                break;
            case R.raw.f:
                invalidate(mFKey);
                break;
            case R.raw.g:
                invalidate(mGKey);
                break;
            case R.raw.a:
                invalidate(mAKey);
                break;
            case R.raw.b:
                invalidate(mBKey);
                break;
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        releaseResources();
    }

    private void setupPaints() {
        mWhiteKeyPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mWhiteKeyPaint.setStyle(Paint.Style.STROKE);
        mWhiteKeyPaint.setColor(Color.BLACK);
        mWhiteKeyPaint.setStrokeWidth(3);

        mCKeyPaint = mWhiteKeyPaint;
        mDKeyPaint = mWhiteKeyPaint;
        mEKeyPaint = mWhiteKeyPaint;
        mFKeyPaint = mWhiteKeyPaint;
        mGKeyPaint = mWhiteKeyPaint;
        mAKeyPaint = mWhiteKeyPaint;
        mBKeyPaint = mWhiteKeyPaint;

        mWhiteKeyHitPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mWhiteKeyHitPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mWhiteKeyHitPaint.setColor(Color.LTGRAY);

        mBlackKeyPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBlackKeyPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mBlackKeyPaint.setColor(Color.BLACK);

        mCSharpKeyPaint = mBlackKeyPaint;
        mDSharpKeyPaint = mBlackKeyPaint;
        mFSharpKeyPaint = mBlackKeyPaint;
        mGSharpKeyPaint = mBlackKeyPaint;
        mASharpKeyPaint = mBlackKeyPaint;

        mBlackKeyHitPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBlackKeyHitPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mBlackKeyHitPaint.setColor(Color.DKGRAY);

    }

    private void loadKeySamples(Context context) {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            SoundPool.Builder soundPoolBuilder = new SoundPool.Builder();
            AudioAttributes attrs =
                    new AudioAttributes.Builder()
                            .setContentType(CONTENT_TYPE_MUSIC).setUsage(AudioAttributes.USAGE_GAME)
                            .build();
            mSoundpool = soundPoolBuilder.setMaxStreams(5).setAudioAttributes(attrs).build();
        }else{
            mSoundpool = new SoundPool(5, AudioManager.STREAM_MUSIC,0);
        }
        mToneToIndexMap.put(R.raw.a,mSoundpool.load(context,R.raw.a,1));
        mToneToIndexMap.put(R.raw.b,mSoundpool.load(context,R.raw.b,1));
        mToneToIndexMap.put(R.raw.c,mSoundpool.load(context,R.raw.c,1));
        mToneToIndexMap.put(R.raw.d,mSoundpool.load(context,R.raw.d,1));
        mToneToIndexMap.put(R.raw.e,mSoundpool.load(context,R.raw.e,1));
        mToneToIndexMap.put(R.raw.f,mSoundpool.load(context,R.raw.f,1));
        mToneToIndexMap.put(R.raw.g,mSoundpool.load(context,R.raw.g,1));

    }

    private void releaseResources() {
        mToneToIndexMap.clear();
        mSoundpool.release();
    }

}
