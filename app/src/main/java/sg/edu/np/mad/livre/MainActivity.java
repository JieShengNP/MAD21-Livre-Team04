package sg.edu.np.mad.livre;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
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

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    ImageView recordsTag,libraryChain, timerFrame;
    Chronometer timer;
    Handler handler;
    long tMilliSec, tStart = 0L;
    int sec,min,hour;
    boolean timerRunning;
    DBHandler dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        timer = findViewById(R.id.timerText);
        timerFrame = findViewById(R.id.timerFrame);
        handler = new Handler();
        dbHandler = new DBHandler(this);
        String isbn = getIntent().getStringExtra("Isbn");

        timerFrame.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //timer not running
                if (!timerRunning)
                {
                    tStart = SystemClock.uptimeMillis();
                    handler.postDelayed(runnable, 0);
                    timer.start();
                    timerRunning = true;
                }
                //timer running
                else
                {
                    AlertDialog(isbn);
                }
                return false;
            }
        });
    }

    public Runnable runnable = new Runnable() {
        @Override
        public void run() {
            tMilliSec = SystemClock.uptimeMillis() - tStart;
            //Converting milliseconds into Hour Min Sec
            sec = (int) (tMilliSec/1000);
            min = sec/60;
            sec = sec % 60;
            hour = min/60;
            min = min % 60;

            timer.setText(String.format("%02d",hour) + ":" + String.format("%02d",min) + ":" + String.format("%02d",sec));
            handler.postDelayed(this,1000);
        }
    };

    public void AlertDialog(String isbn)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Are you sure?");
        builder.setMessage("Do you want to stop the timer?");
        builder.setPositiveButton("Yesh", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                handler.removeCallbacks(runnable);

                // stop timer
                timer.stop();
                timerRunning = false;

                Book dbBook = dbHandler.FindBookByISBN(isbn);
                dbBook.setReadSeconds(dbBook.getReadSeconds() + (int)(tMilliSec/1000));

                //Updating Database
                dbHandler.updateLog(isbn, dbBook.getReadSeconds(),dbBook.name);
                dbHandler.updateTotalTime(dbBook);

                Intent intent = new Intent(MainActivity.this, LibraryActivity.class);
                startActivity(intent);
                finish();
            }
        });
        builder.setNegativeButton("Nah", null);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}