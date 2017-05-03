package com.martian.cleanview;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

/**
 * @author Shenluw
 *         创建日期：2017/4/28 21:34
 */
public class MSymmetryArc implements MDrawable {

    private RectF oval;
    private float startAngle;
    public float sweepAngle;
    private float degrees;

    public MSymmetryArc() {
    }

    public MSymmetryArc(RectF oval, float startAngle, float sweepAngle) {
        this.oval = oval;
        this.startAngle = startAngle;
        this.sweepAngle = sweepAngle;
    }

    @Override
    public void draw(Paint paint, Canvas canvas, float pivotX, float pivotY) {
        canvas.rotate(degrees, pivotX, pivotY);
        canvas.drawArc(oval, startAngle, sweepAngle, false, paint);
        canvas.drawArc(oval, startAngle + 180, sweepAngle, false, paint);
        canvas.rotate(-degrees, pivotX, pivotY);
    }

    public void addDegrees(float degrees) {
        this.degrees += degrees;
    }

    public void setDegrees(float degrees) {
        this.degrees = degrees;
    }

    public float getDegrees() {
        return degrees;
    }

    public RectF getOval() {
        return oval;
    }

    public void setOval(RectF oval) {
        this.oval = oval;
    }

    public float getStartAngle() {
        return startAngle;
    }

    public void setStartAngle(float startAngle) {
        this.startAngle = startAngle;
    }

    public float getSweepAngle() {
        return sweepAngle;
    }

    public void setSweepAngle(float sweepAngle) {
        this.sweepAngle = sweepAngle;
    }
}
