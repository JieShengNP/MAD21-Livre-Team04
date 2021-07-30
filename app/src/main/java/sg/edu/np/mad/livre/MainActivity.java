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

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final long INTERVAL_MS = 1000;
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
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://livre-46ac7-default-rtdb.asia-southeast1.firebasedatabase.app/");
//        Usage of Firebase Database
//        DatabaseReference myRef = database.getReference("Key");
//        myRef.setValue("Value");
        timer = findViewById(R.id.timerText);
        timerFrame = findViewById(R.id.timerFrame);
        handler = new Handler();
        dbHandler = new DBHandler(this);
        isbn = getIntent().getStringExtra("Isbn");

        //initial toast
        Toast.makeText(getBaseContext(), "Press the timer to begin",Toast.LENGTH_SHORT).show();
        //start recurring toast
        handler.postDelayed(toastRunnable, 10000);

        timer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                tMilliSec += INTERVAL_MS;
                sec = (int) (tMilliSec/1000);
                min = sec/60;
                sec = sec % 60;
                hour = min/60;
                min = min % 60;

                timer.setText(String.format("%02d",hour) + ":" + String.format("%02d",min) + ":" + String.format("%02d",sec));
            }
        });

        timerFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //timer not running
                if (!timerRunning)
                {
                    handler.removeCallbacks(toastRunnable);
                    Toast.makeText(MainActivity.this, "Press again to stop", Toast.LENGTH_SHORT).show();
                    tStart = SystemClock.uptimeMillis();

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
        if (timerRunning)
        {
            AlertDialog(isbn);
        }
        else
        {
            Intent intent = new Intent(MainActivity.this, LibraryActivity.class);
            startActivity(intent);
            Toast.makeText(MainActivity.this, "Returning to library",Toast.LENGTH_SHORT).show();
            finish();
        }
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
        else
        {
            tMilliSec = SystemClock.uptimeMillis() - tStart;
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
                    // stop timer
                    timer.stop();
                    timerRunning = false;

                    Book dbBook = dbHandler.FindBookByISBN(isbn);
                    dbBook.setReadSeconds(dbBook.getReadSeconds() + (int)(tMilliSec/1000));

                    //Updating Database
                    dbHandler.updateLog(isbn, (int) (tMilliSec/1000),dbBook.name);
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