package com.example.r00143659.beacondeployment;

import android.content.Context;
        import android.graphics.Canvas;
        import android.graphics.Color;
        import android.graphics.Paint;
        import android.graphics.Rect;
        import android.view.View;

public class Rectangle extends View {

    private Rect rectangle;
    private Paint paint;

    public Rectangle(Context context) {
        super(context);
        int x = 100;
        int y = 100;
        int sideLength = 1000;
        int sideLength2 = 1000;
        // create a rectangle that we'll draw later
        rectangle = new Rect(x, y, sideLength, sideLength2);

        // create the Paint and set its color
        paint = new Paint();
        paint.setColor(Color.GRAY);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.TRANSPARENT);
        canvas.drawRect(rectangle, paint);
    }

}