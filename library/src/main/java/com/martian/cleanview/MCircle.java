package com.martian.cleanview;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.ColorInt;

/**
 * @author Shenluw
 *         创建日期：2017/4/28 21:33
 */
public class MCircle implements MDrawable {

    private float x, y, r;
    @ColorInt
    private int color;


    public MCircle() {
    }

    public MCircle(float x, float y, float r) {
        this.x = x;
        this.y = y;
        this.r = r;
    }

    @Override
    public void draw(Paint paint, Canvas canvas, float pivotX, float pivotY) {
        int origin = paint.getColor();
        paint.setColor(this.color);
        canvas.drawCircle(x, y, r, paint);
        paint.setColor(origin);
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void set(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getR() {
        return r;
    }

    public void setR(float r) {
        this.r = r;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
