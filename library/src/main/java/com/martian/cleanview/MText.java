package com.martian.cleanview;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextUtils;

/**
 * @author Shenluw
 *         创建日期：2017/4/28 21:40
 */
public class MText implements MDrawable {

    private float angle;
    private float x, y;

    private CharSequence text;
    private float textSize;

    @Override
    public void draw(Paint paint, Canvas canvas, float pivotX, float pivotY) {
        if (TextUtils.isEmpty(text)) return;
        canvas.rotate(angle, pivotX, pivotY);
        float old = paint.getTextSize();
        paint.setTextSize(textSize);
        float baseX = x - paint.measureText(text, 0, text.length()) / 2;
        float baseY = y - (paint.descent() + paint.ascent()) / 2;
        canvas.drawText(text, 0, text.length(), baseX, baseY, paint);
        paint.setTextSize(old);
        canvas.rotate(-angle, pivotX, pivotY);
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public CharSequence getText() {
        return text;
    }

    public void setText(CharSequence text) {
        this.text = text;
    }

    public float getTextSize() {
        return textSize;
    }

    public void setTextSize(float textSize) {
        this.textSize = textSize;
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }


    public float getAngle() {
        return angle;
    }
}
