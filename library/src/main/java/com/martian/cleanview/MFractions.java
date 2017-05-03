package com.martian.cleanview;

import android.animation.FloatEvaluator;
import android.animation.TimeAnimator;
import android.animation.TimeAnimator.TimeListener;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.util.Pools.Pool;
import android.support.v4.util.Pools.SimplePool;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static android.content.ContentValues.TAG;

/**
 * @author Shenluw
 *         创建日期：2017/4/29 20:31
 */
public class MFractions implements MDrawable, TimeListener {
    private final Pool<MyText> pool = new SimplePool<>(32);
    private final List<MyText> texts = new ArrayList<>();
    private final FloatEvaluator evaluator = new FloatEvaluator();
    private float time;
    private float target;
    private float interval;
    private float startTextSize;
    private float textSizeDecrease;
    private int range;
    private float pivotX, pivotY;
    private float limitFactor;
    private Random random = new Random();
    private CharSequence[] fractionTexts;

    private class MyText extends MText {
        float startY;
        float duration, time;
    }

    @Override
    public void draw(Paint paint, Canvas canvas, float pivotX, float pivotY) {
        float origin = paint.getTextSize();
        int size = texts.size();
        for (int i = 0; i < size; i++) {
            MText text = texts.get(i);
            float textSize = text.getTextSize();
            paint.setTextSize(textSize);
            text.draw(paint, canvas, pivotX, pivotY);
        }
        paint.setTextSize(origin);
    }

    private MyText obtain() {
        MyText instance = pool.acquire();
        return (instance != null) ? instance : new MyText();
    }

    private void recycle(MyText mText) {
        pool.release(mText);
    }

    @Override
    public void onTimeUpdate(TimeAnimator animation, long totalTime, long deltaTime) {
        updateTexts(deltaTime);
        if (totalTime > limitFactor * animation.getDuration()) return;
        time += deltaTime;
        if (time >= interval) {
            time = 0;
            MyText text = obtain();
            if (fractionTexts.length > 0)
                text.setText(fractionTexts[random.nextInt(fractionTexts.length)]);
            text.setTextSize(startTextSize);
            text.setY(random.nextInt(range));
            text.setX(pivotX);
            text.setAngle(random.nextInt(360));
            text.startY = text.getY();
            text.duration = Math.max(0, animation.getDuration() - totalTime);
            text.time = 0;
            texts.add(text);
        }
    }

    private void updateTexts(long deltaTime) {
        int size = texts.size();
        for (int i = size - 1; i >= 0; i--) {
            MyText text = texts.get(i);
            float y =
                    evaluator.evaluate(Math.min(1f, text.time / text.duration), text.startY, target);
            text.time += deltaTime;
            if (y >= target) {
                texts.remove(i);
                recycle(text);
            } else
                text.setY(y);
        }
    }

    public float getTarget() {
        return target;
    }

    public void setTarget(float target) {
        this.target = target;
    }

    public float getInterval() {
        return interval;
    }

    public void setInterval(float interval) {
        this.interval = interval;
    }

    public float getStartTextSize() {
        return startTextSize;
    }

    public void setStartTextSize(float startTextSize) {
        this.startTextSize = startTextSize;
    }

    public float getTextSizeDecrease() {
        return textSizeDecrease;
    }

    public void setTextSizeDecrease(float textSizeDecrease) {
        this.textSizeDecrease = textSizeDecrease;
    }

    public void setRange(int range) {
        this.range = range;
    }

    public CharSequence[] getFractionTexts() {
        return fractionTexts;
    }

    public void setFractionTexts(CharSequence[] fractionTexts) {
        this.fractionTexts = fractionTexts;
    }

    public void setPivotX(float pivotX) {
        this.pivotX = pivotX;
    }

    public void setPivotY(float pivotY) {
        this.pivotY = pivotY;
    }

    public void setLimitFactor(float factor) {
        this.limitFactor = factor;
    }
}
