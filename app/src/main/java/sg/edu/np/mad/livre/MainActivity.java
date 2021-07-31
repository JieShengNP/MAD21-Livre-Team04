package sg.edu.np.mad.livre;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import android.os.Handler;
import android.os.SystemClock;
import android.view.View;

import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.Collections;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final long INTERVAL_MS = 1000;
    ImageView recordsTag, libraryChain, timerFrame;
    ImageView playButton, pauseButton, prevButton, nextButton, shuffleButton;
    Button disclaimerButton;
    Chronometer timer;
    Handler handler;
    long tMilliSec, tStart = 0L;
    int sec, min, hour, currentMusic, isbn;
    TextView musicName;
    boolean timerRunning, wasPaused, musicStart;
    DBHandler dbHandler;
    ArrayList<MusicTrack> musicList;
    MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        timer = findViewById(R.id.timerText);
        timerFrame = findViewById(R.id.timerFrame);
        disclaimerButton = findViewById(R.id.disclaimerButton);
        playButton = findViewById(R.id.button_play);
        pauseButton = findViewById(R.id.button_pause);
        prevButton = findViewById(R.id.button_prev);
        nextButton = findViewById(R.id.button_next);
        shuffleButton = findViewById(R.id.button_shuffle);
        musicName = findViewById(R.id.musicName);

        handler = new Handler();
        dbHandler = new DBHandler(this);
        isbn = getIntent().getIntExtra("Isbn", -1);

        //start recurring toast
        handler.postDelayed(toastRunnable, 0);

        timer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                tMilliSec += INTERVAL_MS;
                sec = (int) (tMilliSec / 1000);
                min = sec / 60;
                sec = sec % 60;
                hour = min / 60;
                min = min % 60;

                timer.setText(String.format("%02d", hour) + ":" + String.format("%02d", min) + ":" + String.format("%02d", sec));
            }
        });

        timerFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //timer not running
                if (!timerRunning) {
                    handler.removeCallbacks(toastRunnable);
                    Toast.makeText(MainActivity.this, "Press again to stop", Toast.LENGTH_SHORT).show();
                    tStart = SystemClock.uptimeMillis();

                    timer.start();
                    timerRunning = true;
                }
                //timer running
                else {
                    AlertDialog();
                }
            }
        });

        disclaimerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setCancelable(true);
                builder.setTitle("Disclaimer");
                builder.setMessage
                        (
                                "We DO NOT OWN the music tracks used \n" +
                                        "All music tracks used are Royalty Free and are licensed under Creative Commons License.\n" +
                                        "All music tracks used belong to Ron Gelinas\n" +
                                        "SoundCloud: Ron Gelinas Chillout Lounge \n" +
                                        "Youtube : Ron Gelinas Chillout Lounge "
                        );
                builder.setPositiveButton("Youtube", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent youtubeIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/user/iamRottenRon"));
                        startActivity(youtubeIntent);
                    }
                });
                builder.setNegativeButton("SoundCloud", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent soundIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://soundcloud.com/atmospheric-music-portal"));
                        startActivity(soundIntent);
                    }
                });
                builder.setNeutralButton("Close", null);
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        //initialize music tracks list
        InitializeMusicList();
        currentMusic = 0;
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!timerRunning) {
                    handler.removeCallbacks(toastRunnable);
                    Toast.makeText(MainActivity.this, "Please Start Timer First", Toast.LENGTH_SHORT).show();
                    handler.postDelayed(toastRunnable, 0);
                } else {
                    playButton.setVisibility(View.INVISIBLE);
                    pauseButton.setVisibility(View.VISIBLE);
                    PlayMusic();
                }
            }
        });

        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pauseButton.setVisibility(View.INVISIBLE);
                playButton.setVisibility(View.VISIBLE);

                PauseMusic();
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!timerRunning) {
                    handler.removeCallbacks(toastRunnable);
                    Toast.makeText(MainActivity.this, "Please Start Timer First", Toast.LENGTH_SHORT).show();
                    handler.postDelayed(toastRunnable, 0);
                } else {
                    NextMusic();
                }
            }
        });

        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!timerRunning) {
                    handler.removeCallbacks(toastRunnable);
                    Toast.makeText(MainActivity.this, "Please Start Timer First", Toast.LENGTH_SHORT).show();
                    handler.postDelayed(toastRunnable, 0);
                } else {
                    PrevMusic();
                }
            }
        });

        shuffleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Collections.shuffle(musicList);
                Toast.makeText(MainActivity.this, "Playlist Shuffled", Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    public void onBackPressed() {
        if (timerRunning) {
            AlertDialog();
        } else {
            Intent intent = new Intent(MainActivity.this, LibraryActivity.class);
            startActivity(intent);
            Toast.makeText(MainActivity.this, "Returning to library", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // stop toast from showing when activity paused if timer not running
        if (!timerRunning) {
            handler.removeCallbacks(toastRunnable);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //resume toast if timer was not running
        if (!timerRunning) {
            handler.postDelayed(toastRunnable, 0);
        } else {
            tMilliSec = SystemClock.uptimeMillis() - tStart;
        }
    }

    ;

    // Shows toast message to remind user to press timer to start
    public Runnable toastRunnable = new Runnable() {
        @Override
        public void run() {
            if (!timerRunning) {
                handler.removeCallbacks(toastRunnable);
                Toast.makeText(getBaseContext(), "Press the timer to begin", Toast.LENGTH_SHORT).show();
            }
            handler.postDelayed(this, 10000);
        }
    };

    public void AlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setMessage("Do you want to stop the timer?");
        builder.setPositiveButton("Yesh", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (!timerRunning) {
                    //stop showing of toast message if timer hasn't run yet
                    handler.removeCallbacks(toastRunnable);
                    Toast.makeText(getBaseContext(), "Returning to library!", Toast.LENGTH_SHORT).show();
                } else {
                    // stop timer
                    timer.stop();
                    timerRunning = false;

                    Book dbBook = dbHandler.FindBookByID(isbn);
                    dbBook.setReadSeconds(dbBook.getReadSeconds() + (int) (tMilliSec / 1000));

                    //Update Firebase
                    if (!dbBook.isCustom()) {
                        UpdateFirebase(dbBook, (int) (tMilliSec / 1000));
                    }
                    //Updating Database
                    dbHandler.UpdateLog(dbBook, (int) (tMilliSec / 1000));
                    dbHandler.UpdateTotalTime(dbBook);
                    Toast.makeText(MainActivity.this, "Time saved!", Toast.LENGTH_SHORT).show();
                }

                Intent intent = new Intent(MainActivity.this, LibraryActivity.class);
                startActivity(intent);
                if (musicStart) {
                    mp.release();
                }
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

    public void InitializeMusicList() {
        musicList = new ArrayList<MusicTrack>();
        musicList.add(new MusicTrack(
                "Another Day \n (Original Mix)",
                "android.resource://" + getPackageName() + "/" + R.raw.another_day,
                "Ron Gelinas"
        ));
        musicList.add(new MusicTrack(
                "Beacon of Light \n (Original Mix)",
                "android.resource://" + getPackageName() + "/" + R.raw.beacon_of_light,
                "Ron Gelinas"
        ));
        musicList.add(new MusicTrack(
                "Easy \n (Original Mix)",
                "android.resource://" + getPackageName() + "/" + R.raw.easy,
                "Ron Gelinas"
        ));
        musicList.add(new MusicTrack(
                "Endeavour \n (Original Mix)",
                "android.resource://" + getPackageName() + "/" + R.raw.endeavour,
                "Ron Gelinas"
        ));
        musicList.add(new MusicTrack(
                "Equinox \n (Original Mix)",
                "android.resource://" + getPackageName() + "/" + R.raw.equinox,
                "Ron Gelinas"
        ));
        musicList.add(new MusicTrack(
                "Evening Out \n (Original Mix)",
                "android.resource://" + getPackageName() + "/" + R.raw.evening_out,
                "Ron Gelinas"
        ));
        musicList.add(new MusicTrack(
                "Far Away \n (Original Mix)",
                "android.resource://" + getPackageName() + "/" + R.raw.far_away,
                "Ron Gelinas"
        ));
        musicList.add(new MusicTrack(
                "New Direction (Instrumental) \n (Original Mix)",
                "android.resource://" + getPackageName() + "/" + R.raw.new_direction,
                "Ron Gelinas"
        ));
        musicList.add(new MusicTrack(
                "The Art of Healing \n (Original Mix)",
                "android.resource://" + getPackageName() + "/" + R.raw.the_art_of_healing,
                "Ron Gelinas"
        ));
        musicList.add(new MusicTrack(
                "Windsurfing \n (Original Mix)",
                "android.resource://" + getPackageName() + "/" + R.raw.windsurfing,
                "Ron Gelinas"
        ));
        Collections.shuffle(musicList);
    }

    public void PlayMusic() {
        //MP just started
        if (!wasPaused) {
            try {
                musicName.setText(musicList.get(currentMusic).getTrackName() + "\n" + musicList.get(currentMusic).getTrackAuthor());
                mp = new MediaPlayer();
                mp.setDataSource(MainActivity.this, Uri.parse(musicList.get(currentMusic).getTrackFileLocation()));
                mp.prepare();
                mp.start();
                musicStart = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //Pausing of mp
        else {
            wasPaused = !wasPaused;
            mp.start();
        }
    }

    public void PauseMusic() {
        wasPaused = !wasPaused;
        mp.pause();
    }

    public void NextMusic() {
        PauseMusic();
        if (currentMusic == musicList.size() - 1) {
            currentMusic = 0;
        } else {
            currentMusic++;
        }

        mp.reset();
        try {
            musicName.setText(musicList.get(currentMusic).getTrackName() + "\n" + musicList.get(currentMusic).getTrackAuthor());
            mp.setDataSource(MainActivity.this, Uri.parse(musicList.get(currentMusic).getTrackFileLocation()));
            mp.prepare();
            PlayMusic();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void PrevMusic() {
        PauseMusic();
        if (currentMusic == 0) {
            currentMusic = musicList.size() - 1;
        } else {
            currentMusic--;
        }

        mp.reset();
        try {
            musicName.setText(musicList.get(currentMusic).getTrackName() + "\n" + musicList.get(currentMusic).getTrackAuthor());
            mp.setDataSource(MainActivity.this, Uri.parse(musicList.get(currentMusic).getTrackFileLocation()));
            mp.prepare();
            PlayMusic();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void UpdateFirebase(Book book, int extraTime) {
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference database = FirebaseDatabase.getInstance("https://livre-46ac7-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("public/books").child(book.getIsbn());
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    PopularBook popBook = snapshot.getValue(PopularBook.class);
                    if (!popBook.readers.containsKey(userID)) {
                        database.child("readers").child(userID).setValue(true);
                        database.child("totalReaders").setValue(popBook.getTotalReaders() + 1);
                    }
                    database.child("totalTime").setValue(popBook.totalTime + extraTime);
                } else {
                    PopularBook popBook = new PopularBook();
                    popBook.isbn = book.getIsbn();
                    popBook.title = book.getName();
                    popBook.author = book.getAuthor();
                    popBook.blurb = book.getBlurb();
                    popBook.year = book.getYear();
                    popBook.thumbnail = book.getThumbnail();
                    popBook.readers.put(userID, true);
                    popBook.setTotalReaders();
                    popBook.totalTime = extraTime;
                    database.getRef().setValue(popBook);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}