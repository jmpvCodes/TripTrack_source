package com.example.triptrack;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.style.LineBackgroundSpan;

public class CustomCircleSpan implements LineBackgroundSpan {
    private final float radius;
    private final int color;
    private final float offsetX;
    private final float offsetY;

    public CustomCircleSpan(float radius, int color, float offsetX, float offsetY) {
        this.radius = radius;
        this.color = color;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }

    @Override
    public void drawBackground(Canvas canvas, Paint paint, int left, int right, int top, int baseline, int bottom,
                               CharSequence text, int start, int end, int lnum) {
        int oldColor = paint.getColor();
        if (color != 0) {
            paint.setColor(color);
        }
        canvas.drawCircle((float) (left + right) / 2 + offsetX, bottom - radius + offsetY, radius, paint);
        paint.setColor(oldColor);
    }
}


