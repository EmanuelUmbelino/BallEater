package com.example.emanuelumbelino.jogojamv;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.Random;

/**
 * Created by Emanuel.Umbelino on 15/03/2016.
 */

public class MainActivityView extends View implements Runnable
{
    Context context;
    private Handler handler;
    private String[] types = new String[3];
    private Random rand = new Random();
    private Ball enemy;
    private Ball player;

    public MainActivityView(Context c)
    {
        super (c);
        context = c;

        types[0] = "Player";
        types[1] = "Basic";
        types[2] = "Pusher";
        enemy = new Ball(rand.nextInt(100),rand.nextInt(100), 10, types[1]);
        player = new Ball(0,0, 10, types[0]);

        handler = new Handler();
    }

    public boolean onTouchEvent(MotionEvent event) {
        int x = (int)event.getX();
        int y = (int)event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP:
        }
        return false;
    }

    private void Update()
    {
        enemy.goToPosition(player.getX(), player.getY(), player.getR());
    }

    @Override
    public void run()
    {
        handler.postDelayed(this, 30);
        Update();
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.FILL);

        player.setX(canvas.getWidth() / 2);player.setY(canvas.getWidth() / 2);
        canvas.drawCircle(player.getX(), player.getY(), player.getR(), paint);

        canvas.drawCircle(enemy.getX(), enemy.getY(), enemy.getR(), paint);
    }

}

class Ball
{
    private float x, y, r, velX, velY;
    private String type;

    public Ball(int posX, int posY, int ray, String myType)
    {
        x = posX;
        y = posY;
        r = ray;
        type = myType;

    }
    public void goToPosition(float posY, float posX, float posR)
    {
        double angleRadians = Math.atan2(posY - this.y, posX - this.x);
        double distance = Math.sqrt(Math.pow((posY - this.y),2) + Math.pow((posX - this.x),2));
        if((6.67 *0.1*this.r*posR)/(distance) < 20 && (6.67 *0.1*this.r*posR)/(distance) > -20)
        {
            this.velX = Float.parseFloat(String.valueOf((6.67 *0.1*this.r*posR)/(Float.parseFloat(String.valueOf(distance))) * Math.cos(angleRadians)));
            this.velY = Float.parseFloat(String.valueOf((6.67 *0.1*this.r*posR)/(Float.parseFloat(String.valueOf(distance))) * Math.sin(angleRadians)));
        }
        if(posR < this.r)
        {
            this.setY(this.y+this.velY);
            this.setX(this.x+this.velX);
        }
        else
        {
            this.setY(this.y-this.velY);
            this.setX(this.x-this.velX);
        }
    }

    public float getX() {return x;}
    public void setX(float x) {this.x = x;}
    public float getY() {return y;}
    public void setY(float y) {this.y = y;}
    public float getR() {
        return r;
    }
}