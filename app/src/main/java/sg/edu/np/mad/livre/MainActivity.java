package sg.edu.np.mad.livre;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseDatabase database = FirebaseDatabase.getInstance("https://livre-46ac7-default-rtdb.asia-southeast1.firebasedatabase.app/");
//        Usage of Firebase Database
//        DatabaseReference myRef = database.getReference("Key");
//        myRef.setValue("Value");
    }
}