package sg.edu.np.mad.livre;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.widget.VideoView;

public class SplashScreenActivity extends AppCompatActivity {
    private VideoView splashVid;
    //Splash screen
    private static int SPLASH_TIME_OUT = 3000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);

        splashVid = findViewById(R.id.splashscreenVid);
        //setting video path
        String uri = "android.resource://" + getPackageName() + "/" + R.raw.lanima;
        splashVid.setVideoURI(Uri.parse(uri));
        splashVid.start();
        splashVid.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
            }
        });


        new Handler().postDelayed(new Runnable(){
            @Override
            public void run(){
                Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
                startActivity(intent);

                //close this activity
                finish();
            }
        },SPLASH_TIME_OUT);
    }
}