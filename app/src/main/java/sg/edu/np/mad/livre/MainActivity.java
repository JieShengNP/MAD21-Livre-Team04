package sg.edu.np.mad.livre;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

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
    String isbn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        timer = findViewById(R.id.timerText);
        timerFrame = findViewById(R.id.timerFrame);
        handler = new Handler();
        dbHandler = new DBHandler(this);
        isbn = getIntent().getStringExtra("Isbn");
        handler.postDelayed(toastRunnable, 0);
        timerFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //timer not running
                if (!timerRunning)
                {
                    handler.removeCallbacks(toastRunnable);
                    Toast.makeText(MainActivity.this, "Press again to stop", Toast.LENGTH_SHORT).show();
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
            }
        });
    }

    @Override
    public void onBackPressed() {
        AlertDialog(isbn);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // stop toast from showing when activity paused if timer not running
        if (!timerRunning)
        {
            handler.removeCallbacks(toastRunnable);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //resume toast if timer was not running
        if (!timerRunning)
        {
            handler.postDelayed(toastRunnable,0);
        }
    }

    //Runnable for timer to update
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
            handler.postDelayed(this,0);
        }
    };

    // Shows toast message to remind user to press timer to start
    public Runnable toastRunnable = new Runnable() {
        @Override
        public void run() {
            if(!timerRunning)
            {
                Toast.makeText(getBaseContext(), "Press the timer to begin",Toast.LENGTH_SHORT).show();
            }
            handler.postDelayed(this, 10000);
        }
    };
    public void AlertDialog(String isbn)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setMessage("Do you want to stop the timer?");
        builder.setPositiveButton("Yesh", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if(!timerRunning)
                {
                    //stop showing of toast message if timer hasn't run yet
                    handler.removeCallbacks(toastRunnable);
                    Toast.makeText(getBaseContext(), "Returning to library!", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    //stops timer from updating
                    handler.removeCallbacks(runnable);

                    // stop timer
                    timer.stop();
                    timerRunning = false;

                    Book dbBook = dbHandler.FindBookByISBN(isbn);
                    dbBook.setReadSeconds(dbBook.getReadSeconds() + (int)(tMilliSec/1000));

                    //Updating Database
                    dbHandler.updateLog(isbn, dbBook.getReadSeconds(),dbBook.name);
                    dbHandler.updateTotalTime(dbBook);
                    Toast.makeText(MainActivity.this, "Time saved!", Toast.LENGTH_SHORT).show();
                }

                Intent intent = new Intent(MainActivity.this, LibraryActivity.class);
                startActivity(intent);
                finish();
            }
        });
        builder.setNegativeButton("Nah", null);
        AlertDialog alertDialog = builder.create();
        //changing alertdialog button and text
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#6F5339"));
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#6F5339"));
                TextView message = alertDialog.findViewById(android.R.id.message);
                message.setTextSize(30);
            }
        });

        alertDialog.show();


    }
}