package com.martian.cleanview;

import android.graphics.Canvas;
import android.graphics.Paint;

public interface MDrawable {

    void draw(Paint paint, Canvas canvas, float pivotX, float pivotY);

}