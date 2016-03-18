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

/**
 * Created by Emanuel.Umbelino on 15/03/2016.
 */

public class MainActivityView extends View implements Runnable
{
    Context context;
    private Handler handler;
    private int screenWidth;
    private int screenHeight;
    private String[] enemiesTypes = new String[2];
    private Ball player = new Ball(0,0,2,"Player", screenWidth, screenHeight);
    private Ball[] enemy = new Ball[10];
    private int wait = 0;
    private int go = 0;
    Paint paint = new Paint();

    private final float TOUCH_SCALE_FACTOR = 180.0f / 320;
    private float mPreviousX;
    private float mPreviousY;

    public MainActivityView(Context c)
    {
        super (c);
        context = c;

        screenHeight = c.getResources().getDisplayMetrics().heightPixels - 125;
        screenWidth = c.getResources().getDisplayMetrics().widthPixels;

        enemiesTypes[0] = "Basic";
        enemiesTypes[1] = "Pusher";

        player= new Ball(screenWidth/2,screenHeight/2,15,"Player", screenWidth, screenHeight);
        handler = new Handler();
        handler.post(this);
    }

    private void Update()
    {
        if (wait < 30)
            wait ++;
        else if (go < enemy.length)
        {
            wait = 0;
            enemy[go] = new Ball(screenWidth*2 * Math.random()-screenWidth,
                    screenHeight*2 * Math.random()-screenWidth,
                    20 * Math.random()+ 10,"Basic", screenWidth, screenHeight);
            go++;
        }
        for(int i = 0; i < go; i++)
        {
            enemy[i].goToPosition(player.getY(), player.getX(), player.getR());
            for(int f = 0; f < go; f++)
            {
                if(i!=f)
                {
                    enemy[i].goToPosition(enemy[f].getY(),enemy[f].getX(),enemy[f].getR());
                    enemy[i].eat(enemy[f].getY(),enemy[f].getX(),enemy[f].getR(),enemy[f]);
                }
            }
        }
        if(player.inMove)
            player.goToPosition(mPreviousY, mPreviousX, 0);
    }
    @Override
    public boolean onTouchEvent(MotionEvent e) {

        float x = e.getX();
        float y = e.getY();

        switch (e.getAction()) {
            case MotionEvent.ACTION_MOVE:

                float dx = x - mPreviousX;
                float dy = y - mPreviousY;

        }
        player.goToPosition(y, x, 0);
        mPreviousX = x;
        mPreviousY = y;
        return true;
    }
    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.FILL);

        canvas.drawCircle(player.getX() , player.getY(), player.getR(), paint);

        for(int i = 0; i < go; i++)
        {
            canvas.drawCircle(enemy[i].getX(), enemy[i].getY(), enemy[i].getR(), paint);
        }
    }

    @Override
    public void run()
    {
        handler.postDelayed(this, 30);

        Update();
        invalidate();
    }
}

class Ball
{
    private double x, y, r, velX, velY;
    private String type;
    private int height, width;
    public boolean inMove;

    public Ball(double posX, double posY, double ray, String myType, int widthT, int heightT)
    {
        x = posX;
        y = posY;
        type = myType;
        r = ray;
        height = heightT;
        width = widthT;
        inMove = false;
    }
    public void goToPosition(float posY, float posX, float posR)
    {
        double angleRadians = Math.atan2(posY - this.y, posX - this.x);
        double distance = Math.sqrt(Math.pow((posY - this.y), 2) + Math.pow((posX - this.x), 2));
        if(type.equals("Player"))
        {
            velX = distance/10 * Math.cos(angleRadians);
            velY = distance/10 * Math.sin(angleRadians);
            this.setX(this.x + this.velX);
            this.setY(this.y + this.velY);
            if(this.x != posX || this.y != posY)
                inMove = true;
            else
                inMove = false;

        }
        else
        {
            if((6.67 *0.05*this.r*posR)/ distance < 200/r && (6.67 *0.05*this.r*posR)/ distance > -200/r)
            {
                velX = (6.67 *0.05*this.r * posR) / distance * Math.cos(angleRadians);
                velY = (6.67 *0.05*this.r * posR) / distance * Math.sin(angleRadians);
            }

            if(posR < this.r)
            {
                if(this.y+this.velY - r > -height && this.y+this.velY - r < height*2)
                    this.setY(this.y+this.velY);
                if(this.x+this.velX - r> -width && this.x+this.velX - r < width*2)
                    this.setX(this.x + this.velX);
            }
            else if (posR > this.r)
            {
                if(this.y-this.velY - r > -height && this.y-this.velY + r < height*2)
                    this.setY(this.y-this.velY);
                if(this.x-this.velX - r > -width && this.x-this.velX + r < width*2)
                    this.setX(this.x - this.velX);
            }
        }
    }
    public void eat(float posY, float posX, float posR, Ball enemy)
    {
        double distance = Math.sqrt(Math.pow((posY - this.y),2) + Math.pow((posX - this.x),2));
        if(distance < this.r && posR < this.r)
        {
            this.r += posR/2;
            enemy.r = 0;
        }
    }

    public float getX() {return Float.parseFloat(String.valueOf(x));}
    public void setX(double x) {this.x = x;}
    public float getY() {return Float.parseFloat(String.valueOf(y));}
    public void setY(double y) {this.y = y;}
    public float getR() {
        return Float.parseFloat(String.valueOf(r));
    }
}