package com.ivanzhur.pressthebutton;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import java.util.Random;

public class Circle extends View {
    final int numColors = 9;
    private RectF oval;
    private Paint arcPaint;
    private float startAngle, sweepAngle;
    int[] colors, backgrounds;
    int currentColor, currentN;
    Handler handler;
    Random random;

    float padding = 2*MainActivity.density;
    final int frequency = 20;
    static boolean isStopping = false;
    float decrement = 20;
    final float base_angle_multp = 2;
    double angle_multp;
    float rLeft, rTop, rRight, rBottom;
    int millis;

    public Circle(Context context, AttributeSet attrs) {
        super(context, attrs);
        arcPaint = new Paint();
        arcPaint.setAntiAlias(true);
        arcPaint.setColor(ContextCompat.getColor(context, R.color.deep_purple_500));
        oval = new RectF(0, 0, 0, 0);

        startAngle = sweepAngle = 0;
        handler = new Handler();
        random = new Random();
        generateColorsArray(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawArc(oval, startAngle, sweepAngle, true, arcPaint);
        canvas.drawArc(oval, startAngle + 180, sweepAngle, true, arcPaint);
    }

    private void showTime(){
        MainActivity.timeTextView.setText(MainActivity.getTimeFromMillis(millis));
    }

    public void startAnimation(){
        padding = 2*MainActivity.density;
        rLeft = padding;
        rTop = padding;
        rRight = getWidth() - padding;
        rBottom = getHeight() - padding;

        oval = new RectF(rLeft, rTop, rRight, rBottom);
        currentN = 1;
        currentColor = colors[currentN];
        arcPaint.setColor(currentColor);
        startAngle = 0;
        sweepAngle = 1;
        isStopping = false;

        MainActivity.toggleBest(MainActivity.HIDE);
        millis = 0;
        handler.post(nextFrame);
    }

    public void stopAnimation(){
        isStopping = true;
        arcPaint.setColor(colors[1]);
        setBackgroundResource(backgrounds[0]);
    }

    Runnable nextFrame = new Runnable() {
        @Override
        public void run() {
            if (isStopping){
                rLeft += decrement;
                rTop += decrement;
                rRight -= decrement;
                rBottom -= decrement;

                if (rLeft >= rRight){
                    oval = new RectF(0, 0, 0, 0);

                    invalidate();
                    millis-=frequency; // Extra lap was added after stop, need to substract it
                    int best = MainActivity.saveBestScore(millis);
                    MainActivity.showBestScore(best);
                    MainActivity.toggleBest(MainActivity.SHOW); // Show best score
                    MainActivity.pressed = false; // Indicate that animation completed
                    return;
                }
                oval = new RectF(rLeft, rTop, rRight, rBottom);
            }

            if (sweepAngle < 180) sweepAngle += 3.6f*angle_multp;
            else{
                sweepAngle = 1;

                if (!isStopping){
                    setBackgroundResource(backgrounds[currentN]);
                    generateNextColor();
                    arcPaint.setColor(currentColor);
                }
            }

            invalidate();
            if (!isStopping) {
                showTime();
                angle_multp = base_angle_multp * Math.log((millis+0.1f)/7500/2 + Math.E);
                millis += frequency;
            }

            startAngle += 0.8*Math.pow(angle_multp, 1.2);
            if (startAngle > 180) startAngle = 0;
            handler.postDelayed(nextFrame, frequency);
        }
    };


    private void generateNextColor(){
        int next = random.nextInt(numColors);
        while (next == currentN) next = random.nextInt(numColors);
        currentColor = colors[next];
        currentN = next;
    }

    private void generateColorsArray(Context context){
        colors = new int[numColors];
        backgrounds = new int[numColors];

        colors[0] = ContextCompat.getColor(context, R.color.pink_500);
        colors[1] = ContextCompat.getColor(context, R.color.indigo_500);
        colors[2] = ContextCompat.getColor(context, R.color.cyan_500);
        colors[3] = ContextCompat.getColor(context, R.color.teal_500);
        colors[4] = ContextCompat.getColor(context, R.color.green_500);
        colors[5] = ContextCompat.getColor(context, R.color.orange_500);
        colors[6] = ContextCompat.getColor(context, R.color.blue_grey_500);
        colors[7] = ContextCompat.getColor(context, R.color.brown_500);
        colors[8] = ContextCompat.getColor(context, R.color.grey_500);

        backgrounds[0] = R.drawable.circle_button;
        backgrounds[1] = R.drawable.circle_indigo;
        backgrounds[2] = R.drawable.circle_cyan;
        backgrounds[3] = R.drawable.circle_teal;
        backgrounds[4] = R.drawable.circle_green;
        backgrounds[5] = R.drawable.circle_orange;
        backgrounds[6] = R.drawable.circle_blue_grey;
        backgrounds[7] = R.drawable.circle_brown;
        backgrounds[8] = R.drawable.circle_grey;
    }
}
