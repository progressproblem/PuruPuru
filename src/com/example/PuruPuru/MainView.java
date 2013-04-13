package com.example.PuruPuru;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.*;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.*;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainView extends SurfaceView implements SurfaceHolder.Callback, View.OnTouchListener, SensorEventListener {

    private static final String TAG = MainView.class.getSimpleName();
    private static final int SOURCE_WIDTH = 480;
    private static final int SOURCE_HEIGHT = 800;
    private static final int OPPAI_START_X = 80;
    private static final int OPPAI_START_Y = 400;
    private static final int MEGANE_X = 100;
    private static final int MEGANE_Y = 200;

    private SurfaceHolder holder;
    private int clientHeight;
    private int clientWidth;
    private float	resizeScale = 1.0f;
    private int offsetLeft = 0;
    private int offsetTop = 0;

    // 背景データ
    private Bitmap backgroundImage;

    // おっぱいデータ
    private Bitmap oppaiImage;
    private int now_oppai_x;
    private int now_oppai_y;

    private long animateTime = 2000l;
    private long startTime = -1;
    private boolean nowAnimate = false;

    // 眼鏡データ
    private Bitmap meganeImage;

    private boolean useAccelerometer = false;
    private boolean useEyeglass = false;

    public MainView(Context context) {
        super(context);
        init(context);
    }

    public MainView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MainView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public void init(Context context){
        setOnTouchListener(this);
        holder = getHolder();
        holder.addCallback(this);

        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display d =windowManager.getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        d.getMetrics(displayMetrics);
        clientHeight =  displayMetrics.heightPixels;
        clientWidth = displayMetrics.widthPixels;

        useAccelerometer = Setting.useAccelerometer(context);
        useEyeglass = Setting.useEyeglass(context);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;

        if (SOURCE_WIDTH != clientWidth || SOURCE_HEIGHT != clientHeight){
            if((long) SOURCE_WIDTH * clientHeight > (long)clientWidth * SOURCE_HEIGHT) {
                resizeScale = (float)clientWidth / SOURCE_WIDTH;
            } else {
                resizeScale = (float)clientHeight / SOURCE_HEIGHT;
            }

            offsetLeft = ( clientWidth - (int)(SOURCE_WIDTH * resizeScale)) / 2;
            offsetTop = (clientHeight - (int)(SOURCE_HEIGHT * resizeScale)) / 2;
        }

        Resources res = context.getResources();
        backgroundImage = BitmapFactory.decodeResource(res, R.drawable.back, options);
        backgroundImage = resizeBitmap(backgroundImage);

        oppaiImage = BitmapFactory.decodeResource(res, R.drawable.oppai, options);
        oppaiImage = resizeBitmap(oppaiImage);

        meganeImage = BitmapFactory.decodeResource(res, R.drawable.megane, options);
        meganeImage = resizeBitmap(meganeImage);
    }

    private Bitmap resizeBitmap(Bitmap srcBitmap){
        if (resizeScale == 1.0f){
            return srcBitmap;
        }

        int	srcWidth = srcBitmap.getWidth();
        int	srcHeight = srcBitmap.getHeight();
        Matrix matrix = new Matrix();
        matrix.postScale(resizeScale,resizeScale,0,0);
        Bitmap resizedBitmap = Bitmap.createBitmap(srcBitmap,0,0, srcWidth, srcHeight ,matrix,true);
        srcBitmap.recycle();
        return resizedBitmap;
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) {
        draw();
        startnow();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
    }

    public void draw() {
        Canvas c = holder.lockCanvas();

        c.drawBitmap(backgroundImage, getResizedX(0), getResizedY(0), null);

        drawOppai(c);

        drawMegane(c);

        holder.unlockCanvasAndPost(c);
    }

    private float getResizedX(float x){
        return x * resizeScale +  offsetLeft;
    }

    private float getResizedY(float y){
        return y * resizeScale + offsetTop;
    }

    public void drawOppai(Canvas c){
        int dx = 0;
        int dy = 0;

        if (nowAnimate){
            long nowTime = SystemClock.uptimeMillis();
            long passed = nowTime - startTime;

            if (passed <= animateTime){
                double t = passed / 1000.0d; // 秒に変換

                double r  = 20;
                double mm = 0.5d;   // 質量
                double kk = 100.0d; //バネ定数
                double KK = 2.0d;   // 抵抗

                double DD = KK * KK - 4.0d * mm * kk;
                double DD2= Math.sqrt(-DD);

                double nY =
                        r * Math.exp( -KK / 2.0d / mm * t )*(Math.cos( DD2 / 2.0d / mm * t)
                                +KK / DD2 * Math.sin( DD2 / 2.0d / mm * t)
                        );

                double angle = 30.0d;      // 移動する向き 0=上、90=右…
                double angleBlur = 20.0d;  // 角度のブレ
                double blur = Math.random() * angleBlur - angleBlur / 2.0d;

                double theta = (angle + blur) * Math.PI / 180.0d;
                dx = (int)(-nY * Math.sin(theta));
                dy = (int)( nY *  Math.cos(theta));
            } else {
                nowAnimate = false;
            }
        }

        now_oppai_x = OPPAI_START_X - dx;
        now_oppai_y = OPPAI_START_Y - dy;

        c.drawBitmap(oppaiImage, getResizedX(now_oppai_x),  getResizedY(now_oppai_y), null);
    }

    public void drawMegane(Canvas c){
        if (useEyeglass){
            c.drawBitmap(meganeImage,  getResizedX(MEGANE_X),  getResizedY(MEGANE_Y), null);
        }
    }

    public void startnow(){
        ScheduledExecutorService executor =
                Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(new Runnable() {
            public void run() {
                draw();
            }
        }, 50, 50, TimeUnit.MILLISECONDS); // 20fps位？
    }

    private void startAnimation(){
        nowAnimate = true;
        startTime = SystemClock.uptimeMillis();
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                if (!isTouchable((int)event.getX(), (int)event.getY())){
                    break;
                }
                startAnimation();
                break;
            case MotionEvent.ACTION_MOVE:
                break;
        }
        return false;
    }

    private boolean isTouchable(int x, int y){
        int imageWidth = oppaiImage.getWidth();
        int imageHeight = oppaiImage.getHeight();

        float resized_oppai_x = getResizedX(now_oppai_x);
        float resized_oppai_y = getResizedY(now_oppai_y);

        if (x <  resized_oppai_x || x > ( resized_oppai_x + imageWidth - 1) ||
                y < resized_oppai_y || y > (resized_oppai_y + imageHeight - 1)){
            return false;
        }

        int bmpX = (int)(x - resized_oppai_x);
        int bmpY = (int)(y - resized_oppai_y);
        Integer color = oppaiImage.getPixel(bmpX, bmpY);
        int alpha = Color.alpha(color);
        return alpha >= 16;  // あまり薄い色もタッチ不可に
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (!useAccelerometer || event.accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE) return;

        float[] accelerometerValues = null;
        switch (event.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                accelerometerValues = event.values.clone();
                break;
        }

        if (accelerometerValues != null) {
            float targetValue =
                    Math.abs(accelerometerValues[0]) +
                            Math.abs(accelerometerValues[1]) +
                            Math.abs(accelerometerValues[2]);
            if(targetValue > 22.0f) {
                startAnimation();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }
}
