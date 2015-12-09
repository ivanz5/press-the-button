package com.ivanzhur.pressthebutton;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;


public class MainActivity extends Activity {

    View startButton;
    Circle buttonOverlay;
    static float density;
    static boolean pressed = false;
    static TextView timeTextView, bestTextView;

    final static int HIDE = 0;
    final static int SHOW = 1;

    static SharedPreferences preferences;
    static SharedPreferences.Editor editor;
    static final String NAME_PREFERENCES = "com.ivanzhur.pressthebutton.sharedpreferences";
    static final String BEST_SCORE = "bestScore";
    static String bestString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setVariables();
    }

    @Override
    protected void onStop(){
        super.onStop();
        buttonOverlay.stopAnimation();
    }

    private void setVariables(){
        startButton = findViewById(R.id.startButton);
        buttonOverlay = (Circle)findViewById(R.id.buttonOverlay);
        timeTextView = (TextView)findViewById(R.id.timeTextView);
        bestTextView = (TextView)findViewById(R.id.bestTextView);

        buttonOverlay.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    if (pressed) return false;
                    pressed = true;
                    buttonOverlay.startAnimation();
                }
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    // in following method "pressed" is set to false
                    buttonOverlay.stopAnimation();
                }
                return true;
            }
        });

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        density = metrics.density;
        bestString = getString(R.string.best);

        preferences = getSharedPreferences(NAME_PREFERENCES, Context.MODE_PRIVATE);
        editor = preferences.edit();
        showBestScore(saveBestScore(0));
    }

    public static void toggleBest(int action){
        if (action == HIDE){
            bestTextView.setVisibility(View.INVISIBLE);
            return;
        }
        Animation animation = new AlphaAnimation(1-action, action); // Change
        animation.setDuration(300);
        int visibility = (action == SHOW) ? View.VISIBLE : View.INVISIBLE; // Change
        bestTextView.setVisibility(visibility);
        bestTextView.startAnimation(animation);
    }

    public static void showBestScore(int score){
        bestTextView.setText(bestString + getTimeFromMillis(score));
    }

    public void resetBestScore(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.reset_title);
        builder.setMessage(R.string.reset_message);
        builder.setPositiveButton(R.string.reset_confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                editor.putInt(BEST_SCORE, 0);
                editor.apply();
                showBestScore(0);
            }
        });
        builder.setNegativeButton(R.string.reset_cancel, null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public static String getTimeFromMillis(int millis){
        int time_minutes = millis / 60000;
        int time_seconds = (millis - 60000*time_minutes) / 1000;
        int time_millis = (millis % 1000)/10;
        String zero_seconds = (time_seconds < 10) ? ":0" : ":";
        String zero_millis = (time_millis < 10) ? ".0" : ".";
        return time_minutes + zero_seconds + time_seconds + zero_millis + time_millis;
    }

    public static int saveBestScore(int score){
        int currentBestScore = preferences.getInt(BEST_SCORE, 0);
        if (score > currentBestScore){
            editor.putInt(BEST_SCORE, score);
            editor.apply();
            return score;
        }
        return currentBestScore;
    }
}
