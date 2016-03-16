package com.example.emanuelumbelino.jogojamv;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.view.View;

/**
 * Created by Emanuel.Umbelino on 15/03/2016.
 */

public class MainActivityView extends View implements Runnable
{
    Context context;
    private Handler handler;
    private String[] enemiesTypes = new String[2];


    public MainActivityView(Context c)
    {
        super (c);
        context = c;

        enemiesTypes[0] = "Basic";
        enemiesTypes[1] = "Pusher";

        handler = new Handler();
    }


    private void Update()
    {

    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        Rect ourRect = new Rect();
        ourRect.set(0, 0, canvas.getWidth(), canvas.getHeight());

        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.FILL);

        canvas.drawCircle(canvas.getWidth()/2, canvas.getHeight()/2, 5, paint);

    }

    @Override
    public void run()
    {
        handler.postDelayed(this, 30);

        Update();
        invalidate();
    }
}

class Enemy
{
    private int x, y, r;
    private String type;

    public Enemy(int posX, int posY, int r, String myType)
    {
        x = posX;
        y = posY;
        type = myType;

    }

    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }
    public int getR() {
        return r;
    }
}