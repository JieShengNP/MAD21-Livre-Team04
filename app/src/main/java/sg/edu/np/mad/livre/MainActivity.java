package sg.edu.np.mad.livre;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    ImageView recordsTag,libraryChain, timerFrame;
    Chronometer timer;
    Handler handler;
    long tMilliSec, tStart, tBuff, tUpdate = 0L;
    int sec,min,hour;
    boolean timerRunning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        timer = findViewById(R.id.timerText);
        timerFrame = findViewById(R.id.timerFrame);

        handler = new Handler();

        //set timer to 0
//        time = 0;
//        timerText.setText("00:00:00");
//        timeRunning = true;




        timerFrame.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!timerRunning)
                {
                    tStart = SystemClock.uptimeMillis();
                    handler.postDelayed(runnable, 0);
                    timer.start();
                    timerRunning = true;
                }
                else
                {
                    tBuff += tMilliSec;
                    handler.removeCallbacks(runnable);
                    timer.stop();
                    timerRunning = false;
                }
                return false;
            }
        });
    }

    public Runnable runnable = new Runnable() {
        @Override
        public void run() {
            tMilliSec = SystemClock.uptimeMillis() - tStart;
            tUpdate = tBuff + tMilliSec;
            //Converting milliseconds into Hour Min Sec
            sec = (int) (tUpdate/1000);
            min = sec/60;
            sec = sec % 60;
            hour = min/60;
            min = min % 60;

            timer.setText(String.format("%02d",hour) + ":" + String.format("%02d",min) + ":" + String.format("%02d",sec));
            handler.postDelayed(this,1000);
        }
    };
}