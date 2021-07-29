package sg.edu.np.mad.livre;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.widget.VideoView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashScreenActivity extends AppCompatActivity {
    private VideoView splashVid;
    SharedPreferences sharedPreferences;
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
                FirebaseAuth.AuthStateListener authListener = new FirebaseAuth.AuthStateListener() {
                    @Override
                    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            String userId = firebaseUser.getUid();
                            String userEmail = firebaseUser.getEmail();
                            SharedPreferences.Editor editor = getSharedPreferences(SignInActivity.sharedPrefName, MODE_PRIVATE).edit();
                            editor.putString("FirebaseUser", userId);
                            editor.putString("FirebaseEmail", userEmail);
                            editor.commit();
                        }
                    }
                };
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                sharedPreferences = getSharedPreferences(SignInActivity.sharedPrefName, MODE_PRIVATE);
                String userID = sharedPreferences.getString("FirebaseUser", "");
                if (userID.equals(user.getUid())) {
                    Intent intent = new Intent(SplashScreenActivity.this, LibraryActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(SplashScreenActivity.this, SignUpActivity.class);
                    startActivity(intent);
                }
                //close this activity
                finish();
            }
        },SPLASH_TIME_OUT);
    }
}