package com.example.emanuelumbelino.jogojamv;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Debug;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.Random;


public class MainActivityView extends View implements Runnable
{
    Context context;
    private Handler handler;
    private int screenWidth;
    private int screenHeight;
    private String[] enemiesTypes = new String[4];
    private Ball player = new Ball(0,0,2,"Player", screenWidth, screenHeight);
    private Ball[] enemy = new Ball[15];
    private int wait = 0;
    private int go = 0;
    Paint paint = new Paint();

    private float mPreviousX;
    private float mPreviousY;

    public MainActivityView(Context c)
    {
        super (c);
        context = c;

        screenHeight = c.getResources().getDisplayMetrics().heightPixels - 125;
        screenWidth = c.getResources().getDisplayMetrics().widthPixels;

        enemiesTypes[0] = "Basic";
        enemiesTypes[1] = "Basic";
        enemiesTypes[2] = "Shoot";
        enemiesTypes[3] = "Explosion";

        player= new Ball(screenWidth/2,screenHeight/2,15,"Player", screenWidth, screenHeight);
        handler = new Handler();
        handler.post(this);
    }

    private void update()
    {
        if (wait < 30)
            wait ++;
        else if (go < enemy.length)
        {
            wait = 0;
            enemy[go] = new Ball(screenWidth * Math.random(),
                    screenHeight * Math.random(),
                    7 * Math.random()+ player.getR(),enemiesTypes[new Random().nextInt(enemiesTypes.length)], screenWidth, screenHeight);
            go++;
        }
        for(int i = 0; i < go; i++)
        {
            enemy[i].update();
            enemy[i].goToPosition(player.getY(), player.getX(), player.getR());
            /*for(int f = 0; f < go; f++)
            {
                if(i!=f)
                {
                    enemy[i].goToPosition(enemy[f].getY(),enemy[f].getX(),enemy[f].getR());
                    enemy[i].eat(enemy[f].getY(),enemy[f].getX(),enemy[f].getR(),enemy[f]);
                }
            }*/
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

        paint.setColor(player.myColor);
        player.draw(canvas,paint);

        for(int i = 0; i < go; i++)
        {
            enemy[i].draw(canvas,paint);
        }
    }

    @Override
    public void run()
    {
        handler.postDelayed(this, 30);

        update();
        invalidate();
    }
}

class Ball
{
    private double x, y, r, velX, velY;
    private String type;
    private int diference;
    private int heightScreen, widthScreen;
    public int myColor = Color.BLACK;
    public boolean inMove;
    private boolean die;
    private Ball[] particles;

    public Ball(double posX, double posY, double ray, String myType, int widthT, int heightT)
    {
        x = posX;
        y = posY;
        type = myType;
        r = ray;
        heightScreen = heightT;
        widthScreen = widthT;
        inMove = false;
        die = false;
        particles = new Ball[5];
        if(type.equals("Player"))
            myColor = Color.BLUE;
        else if(type.equals("Explosion"))
            myColor = Color.RED;
        else if(type.equals("Shoot"))
            myColor = Color.YELLOW;
    }

    void defaultMovement(double distance, double angleRadians, float posR)
    {
        if((6.67 *0.05*this.r*posR)/ distance < 200/r && (6.67 *0.05*this.r*posR)/ distance > -200/r)
        {
            velX = (6.67 *0.05*this.r * posR) / distance * Math.cos(angleRadians);
            velY = (6.67 *0.05*this.r * posR) / distance * Math.sin(angleRadians);
        }

        if(posR < this.r)
        {
            if(this.y+this.velY - r > 0 && this.y+this.velY - r < heightScreen)
                this.setY(this.y+this.velY);
            if(this.x+this.velX - r> 0 && this.x+this.velX - r < widthScreen)
                this.setX(this.x + this.velX);
        }
        else if (posR > this.r)
        {
            if(this.y-this.velY - r > 0 && this.y-this.velY + r < heightScreen)
                this.setY(this.y-this.velY);
            if(this.x-this.velX - r > 0 && this.x-this.velX + r < widthScreen)
                this.setX(this.x - this.velX);
        }
    }

    void playerMovement(double distance, double angleRadians, float posX, float posY)
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

    void explosionMovement(double angleRadians)
    {
        if(!inMove)
        {
            inMove = true;
            velX = 10 * Math.cos(angleRadians);
            velY = 10 * Math.sin(angleRadians);
        }
        this.setX(this.x + this.velX);
        this.setY(this.y + this.velY);
        if(this.y+this.velY - r < 0 || this.y+this.velY + r > heightScreen ||
                this.x+this.velX - r < 0 || this.x+this.velX + r > widthScreen)
        {
            if(this.y+this.velY - r < 0)
                diference = 90;
            else if (this.y+this.velY + r > heightScreen)
                diference = 0;
            else if (this.x+this.velX - r < 0)
                diference = 180;
            else if (this.x+this.velX + r > widthScreen)
                diference = 270;
            if(r > 1)
            {
                for (int i = 0; i < particles.length; i++) {
                    particles[i] = new Ball(x, y, r / 2, type, widthScreen, heightScreen);
                }
            }
            velX = 0;
            velY = 0;
            r = 0;
            die = true;
        }
    }

    void shootMovement(double distance, double angleRadians)
    {
        if(!inMove || this.y+this.velY - r < 0 || this.y+this.velY + r > heightScreen ||
                this.x+this.velX - r < 0 || this.x+this.velX + r > widthScreen)
        {
            inMove = true;
            velX = distance/30 * Math.cos(angleRadians);
            velY = distance/30 * Math.sin(angleRadians);
        }
        this.setX(this.x + this.velX);
        this.setY(this.y + this.velY);
    }

    public void goToPosition(float posY, float posX, float posR)
    {
        if(!die)
        {
            double angleRadians = Math.atan2(posY - this.y, posX - this.x);
            switch (type)
            {
                case "Player":
                {
                    double distance = Math.sqrt(Math.pow((posY - this.y), 2) + Math.pow((posX - this.x), 2));
                    playerMovement(distance, angleRadians, posX, posY);
                }
                break;
                case "Shoot":
                {
                    double distance = Math.sqrt(Math.pow((posY - this.y), 2) + Math.pow((posX - this.x), 2));
                    shootMovement(distance, angleRadians);
                }
                break;
                case "Explosion":
                {
                    explosionMovement(angleRadians);
                }
                break;
                default:
                {
                    double distance = Math.sqrt(Math.pow((posY - this.y), 2) + Math.pow((posX - this.x), 2));
                    defaultMovement(distance, angleRadians, posR);
                }
            }
        }
    }
    public void update()
    {
        if(type.equals("Explosion") && die)
        {
            for(int i = 0, angle = diference; i < particles.length; i++, angle += 45)
            {
                float particleX = 1000 * Float.parseFloat(String.valueOf(Math.cos(angle)));
                float particleY = 1000 * Float.parseFloat(String.valueOf(Math.sin(angle)));
                particles[i].goToPosition(particleY, particleX, 0);
            }

        }
    }
    public void draw(Canvas canvas, Paint paint)
    {
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(myColor);

        canvas.drawCircle(getX(), getY(), getR(), paint);
        if (type.equals("Explosion") && die)
        {
            for (int i = 0; i < particles.length; i++)
            {
                particles[i].draw(canvas,paint);
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