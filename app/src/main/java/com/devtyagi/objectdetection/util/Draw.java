package com.devtyagi.objectdetection.util;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;


public class Draw extends View {

    Paint boundryPaint, textPaint;
    Rect rect;
    String text;

    public Draw(Context context, Rect rect, String text) {
        super(context);
        this.rect = rect;
        this.text = text;

        boundryPaint = new Paint();
        boundryPaint.setColor(Color.YELLOW);
        boundryPaint.setStrokeWidth(10f);
        boundryPaint.setStyle(Paint.Style.STROKE);

        textPaint = new Paint();
        textPaint.setColor(Color.YELLOW);
        textPaint.setStrokeWidth(50f);
        textPaint.setTextSize(32f);
        textPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawText(text, rect.centerX(), rect.centerY(), textPaint);
        canvas.drawRect(rect.left, rect.top, rect.right, rect.bottom, boundryPaint);
    }
}
