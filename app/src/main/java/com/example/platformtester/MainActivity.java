package com.example.platformtester;



import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.ImageView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;


import java.util.ArrayList;


public class MainActivity extends AppCompatActivity
{
    private static final String TAG = "MainActivity";

    private Button leftButton, rightButton, pauseButton;
    private TextView score;
    private ImageView shrekImage;
    private RelativeLayout pauseMenu;
    private ConstraintLayout layout;

    private MediaPlayer AllStar;
    private MediaPlayer jumpSound;

    private ArrayList<Platform> platformList;

    private int shrekX;
    private int shrekY;
    private int shrekSpeed;
    private int shrekVelo;
    private int minShrekX;
    private int maxShrekX;
    private int minShrekY;
    private int maxShrekY;
    private int scoreNum;

    private boolean alive;
    private boolean pauseClicked;

    private View.OnTouchListener leftListener;
    private View.OnTouchListener rightListener;

    Thread mainGame;
    Thread moveDown;

    DisplayMetrics displayMetrics = new DisplayMetrics();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);

        layout = new ConstraintLayout(this);
        layout = findViewById(R.id.root);

        score = findViewById(R.id.score_Count);
        shrekImage = findViewById(R.id.shrek);
        leftButton = findViewById(R.id.left_Button);
        rightButton = findViewById(R.id.right_Button);
        pauseButton = findViewById(R.id.pause_Button);
        pauseMenu = findViewById(R.id.pause_Menu);

        Bundle extras = getIntent().getExtras();
        String character = null;
        if(extras!=null)
            character = extras.getString("character");
        if(character.equals("Donkey"))
            shrekImage.setImageResource(R.drawable.donkey);
        else
        if(character.equals("Fiona"))
            shrekImage.setImageResource(R.drawable.fiona);

        Log.i(TAG,"onCreate");
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        Log.i(TAG,"onStart");
      /*
        AllStar = MediaPlayer.create(MainActivity.this,R.raw.all_star);
        AllStar.start();
        AllStar.setLooping(true);
*/


        platformList = new ArrayList<>();
        //platformList.add(new Platform(100, 1300));
        //platformList.add(new Platform(600, 800));
        platformList.add(new Platform());
        platformList.add(new Platform());
        platformList.add(new Platform());
        /*platformList.add(new Platform());
        platformList.add(new Platform());
        platformList.add(new Platform());*/



        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        shrekX = displayMetrics.widthPixels/2;
        shrekY = displayMetrics.heightPixels/2;

        scoreNum = 0;
        shrekSpeed = 30;
        shrekVelo = 0;


        alive = true;
        pauseClicked = true;

        minShrekX = -100 ; // the left of the screen
        maxShrekX = displayMetrics.widthPixels - 200; // the right of the screen
        minShrekY = 0 ; // the top of the screen
        maxShrekY = displayMetrics.heightPixels; // the bottom of the screen
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        Log.i(TAG,"onResume");

        /*Thread buttonMaker = new Thread()
        {
            public void run()
            {
                while(alive)
                {*/
        leftListener = new View.OnTouchListener()
        {
            private Handler mHandler;

            @Override public boolean onTouch(View v, MotionEvent event)
            {
                switch(event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        if (mHandler != null)
                            return true;
                        mHandler = new Handler();
                        mHandler.postDelayed(mAction, 1);
                        break;
                    case MotionEvent.ACTION_UP:
                        if (mHandler == null)
                            return true;
                        mHandler.removeCallbacks(mAction);
                        mHandler = null;
                        break;
                }
                return true;
            }

            Runnable mAction = new Runnable()
            {
                @Override
                public void run()
                {
                    mHandler.postDelayed(this, 1);
                    moveLeft();
                    Log.i(TAG,"moving left");
                    //mHandler.postDelayed(this, 1);
                }
            };
        };
        rightListener = new View.OnTouchListener()
        {
            private Handler mHandler;

            @Override public boolean onTouch(View v, MotionEvent event)
            {
                switch(event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        if (mHandler != null)
                            return true;
                        mHandler = new Handler();
                        mHandler.postDelayed(mAction, 1);
                        break;
                    case MotionEvent.ACTION_UP:
                        if (mHandler == null)
                            return true;
                        mHandler.removeCallbacks(mAction);
                        mHandler = null;
                        break;
                }
                return true;
            }

            Runnable mAction = new Runnable()
            {
                @Override
                public void run()
                {
                    mHandler.postDelayed(this, 1);
                    moveRight();
                    Log.i(TAG,"moving right");
                    //mHandler.postDelayed(this, 1);
                }
            };
        };

        // This allows the user to hold the right button and move shrek to the right
        rightButton.setOnTouchListener(rightListener);
        // This allows the user to hold the left button and move shrek to the left
        leftButton.setOnTouchListener(leftListener);
/*
                    try{Thread.sleep(35);}
                    catch(InterruptedException e){}
                }
            }
        };*/

        mainGame = new Thread()
        {
            public void run()
            {
                jump();
                updateYPos();
                while(alive)
                {
                    int prevY = shrekY;
                    shrekVelo += 5;
                    try{Thread.sleep(35);}
                    catch(InterruptedException e){}
                    updateYPos();
                    for(int x = 0; x < platformList.size(); x++)
                    {
                        if(shrekVelo >= 0 && prevY <= platformList.get(x).getY() && shrekY >= platformList.get(x).getY() && shrekX >= platformList.get(x).getMinX() && shrekX <= platformList.get(x).getMaxX() + 225)
                        {
                            jump(platformList.get(x).getY());
                            moveDown.interrupt();
                            if(shrekY + shrekVelo > platformList.get(x).getY())
                                shrekVelo = platformList.get(x).getY() - shrekY;
                            updateYPos();
                        }
                    }
                    if(shrekY > maxShrekY + 200)
                    {
                        Log.i(TAG,"dead shrek");
                        //   jump();
                        alive = false;
                        deathActivate();
                    }
                }
                //deathActivate();
            }
        };
        //buttonMaker.start();
        mainGame.start();
    }

    protected void onPause()
    {
        super.onPause();
        // AllStar.pause();


        Log.i(TAG,"onPause");

    }

    protected void onStop()
    {
        super.onStop();
        //AllStar.pause();

        Log.i(TAG,"onStop");

    }

    protected void onDestroy()
    {
        super.onDestroy();
        // AllStar.stop();
        Log.i(TAG,"onDestroy");
    }


    // This causes shrek to move right by changing his x position to be higher
    public void moveRight()
    {
        shrekX += shrekSpeed;
        shrekImage.setX(shrekX);
        shrekImage.setScaleX(Float.valueOf("1f"));
        if(shrekX > maxShrekX)
            shrekX = minShrekX;
    }

    // This causes shrek to move left by changing his x position to be lower
    public void moveLeft()
    {
        shrekX -= shrekSpeed;
        shrekImage.setX(shrekX);
        shrekImage.setScaleX(Float.valueOf("-1f"));
        if(shrekX < minShrekX)
            shrekX = maxShrekX;
    }

    // this changes shrek's velocity to -100
    public void jump()
    {
        shrekVelo = -80;
        //  deathActivate();

    }

    public void jump(int newLevel)
    {
        shrekVelo = -80;
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                scoreNum += 100;
                score.setText("Score: " + scoreNum);
            }
        });

        moveDown = new Thread()
        {
            int overallPlatVelo = 0;
            boolean waiting = false;
            public void run()
            {
                if(newLevel != maxShrekY - 215)
                {
                    createPlatforms(4);
                    for(int y = newLevel; y <= maxShrekY; y += overallPlatVelo)
                    {
                        if(y - newLevel < maxShrekY/2)
                            overallPlatVelo += 1;
                        else if(overallPlatVelo > 0)
                            overallPlatVelo -= 1;
                        try{Thread.sleep(3);}
                        catch(InterruptedException e){}
                        if(y + overallPlatVelo > maxShrekY)
                            overallPlatVelo = maxShrekY - y;
                        //waiting = true;

                        for(int x = platformList.size() - 1; x >= 0; x--)
                        {
                            final int w = x;
                            runOnUiThread(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    try
                                    {
                                        platformList.get(w).updateYCord(overallPlatVelo);
                                        if(platformList.get(w).getY() > maxShrekY)
                                        {
                                            platformList.get(w).platformImage.setVisibility(View.GONE);
                                            platformList.remove(w);
                                        }
                                    }
                                    catch(IndexOutOfBoundsException e){}
                                }
                            });
                        }

                        //while(waiting)
                        //{
                        //try{Thread.sleep(100);}
                        //catch(InterruptedException e){}
                        //}
                    }
                }
            }
        };
        moveDown.start();
    }

    public void createPlatforms(int x)
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                for(int y = 0; y < x; y++)
                    platformList.add(new Platform());
            }
        });
    }


    // This checks to see if shrek is touching a platform and returns true if he is otherwise it returns false
    public boolean touchingGround()
    {
        return shrekY >= maxShrekY;
    }

    public void updateYPos()
    {
        if(pauseClicked)
        {
            shrekY += shrekVelo;
        }
        shrekImage.setY(shrekY);
    }


    // This allows the pause button to pop up the pause menu and close it again
    public void pauseActivate(View view)
    {

        if(pauseClicked)
        {
            pauseMenu.setVisibility(View.VISIBLE);
            pauseMenu.bringToFront();
            leftButton.setVisibility(View.INVISIBLE);
            rightButton.setVisibility(View.INVISIBLE);
            pauseButton.setBackgroundResource(R.drawable.ic_baseline_play_arrow_24);
            pauseClicked = false;
        }

        else
        {
            pauseMenu.setVisibility(View.INVISIBLE);
            leftButton.setVisibility(View.VISIBLE);
            rightButton.setVisibility(View.VISIBLE);
            pauseButton.setBackgroundResource(R.drawable.ic_baseline_pause_24);
            pauseClicked = true;
        }

    }

    // This takes the player to the death screen after they lose the game
    public void deathActivate()
    {

        Intent deathIntent = new Intent(MainActivity.this, DeathActivity.class);
        deathIntent.putExtra("score",scoreNum);
        startActivity(deathIntent);
    }

    // This allows the restart button in the pause menu to restart the game
    public void restartActivate(View view)
    {
        Intent restartIntent = new Intent(MainActivity.this, MainActivity.class);
        startActivity(restartIntent);
    }

    // This allows the menu button to go back to the main menu of the game that needs to be added later
    public void menuActivate(View view)
    {
        Intent menuIntent = new Intent(MainActivity.this, SplashActivity.class);
        startActivity(menuIntent);
    }



    class Platform
    {
        private int posMinX;
        private int posMaxX;
        private int platVelo;
        private int posY;
        private Context context;
        private final ImageView platformImage;

        public Platform()
        {
            posMinX = (int)(Math.random() * (720) - 150);
            posMaxX = posMinX + 150;
            posY = (int)(Math.random() * 1600) - 600;
            platformImage = new ImageView(MainActivity.this);
            platformImage.setImageResource(R.drawable.wooden_plank);
            platformImage.setVisibility(View.VISIBLE);
            platformImage.setX(posMinX + 190);
            updateYCord();
            layout.addView(platformImage);
        }

        public Platform(int pMinX, int pY)
        {
            posMinX = pMinX;
            posMaxX = pMinX + 150;
            posY = pY;
            platformImage = new ImageView(MainActivity.this);
            platformImage.setImageResource(R.drawable.wooden_plank);
            platformImage.setVisibility(View.VISIBLE);
            platformImage.setX(posMinX + 190);
            updateYCord();
            layout.addView(platformImage);
        }

        public void updateYCord()
        {
            platformImage.setY(posY + 215);
        }

        public void updateYCord(int update)
        {
            posY += update;
            if(posY == maxShrekY)
                posY -= 215;
            /*runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {*/
            platformImage.setY(posY + 215);
            //}
            //});

            /*if(mainPlatform)
            {
                platVelo = 0;
                while(posY <= maxShrekY)
                {
                    platVelo += 1;
                    try{Thread.sleep(5);}
                    catch(InterruptedException e){}
                    if(posY + platVelo >= maxShrekY - 100)
                    {
                        posY = maxShrekY - 100;
                        platformImage.setY(posY + 135);
                        break;
                    }
                    posY += platVelo;
                    platformImage.setY(posY + 235);
                }
            }
            else
            {
                platVelo = 0;
                while(posY <= maxShrekY - update)
                {
                    platVelo += 1;
                    try{Thread.sleep(5);}
                    catch(InterruptedException e){}
                    if(posY + platVelo >= maxShrekY - update)
                    {
                        posY = maxShrekY - update;
                        platformImage.setY(posY + 235);
                        break;
                    }
                    posY += platVelo;
                    platformImage.setY(posY + 235);
                    if(posY > maxShrekY)
                    {
                        removeFromLayout(platform);
                    }
                }
            }*/
        }

        public int getMaxX(){return posMaxX;}
        public int getMinX(){return posMinX;}
        public int getY(){return posY;}
        public void setY(int a){posY = a;}
    }

    class Enemy
    {
        private int posMinX;
        private int posMaxX;
        private int eVelo;
        private int posY;
        private Context context;
        private ImageView humptyImage;
        private ImageView piperImage;

        public Enemy()
        {
            humptyImage = new ImageView(MainActivity.this);
            humptyImage.setImageResource(R.drawable.humptydumpty);
            humptyImage.setVisibility(View.VISIBLE);

            piperImage = new ImageView(MainActivity.this);
            piperImage.setImageResource(R.drawable.piedpiper);
            piperImage.setVisibility(View.VISIBLE);

        }
    }
}
