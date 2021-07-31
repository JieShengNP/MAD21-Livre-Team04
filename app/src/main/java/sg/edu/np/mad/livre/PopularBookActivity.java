package sg.edu.np.mad.livre;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class PopularBookActivity extends AppCompatActivity {

    TextView backText, pageTitle, pageDesc;
    ProgressDialog progressDialog;
    CountDownTimer cdt;
    PopularBookAdapter popularBookAdapter;
    ArrayList<PopularBook> bookList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popular_book);

        // Initialisation
        backText = findViewById(R.id.popularBackText);
        pageTitle = findViewById(R.id.popularTitle);
        pageDesc = findViewById(R.id.popularDesc);
        bookList = new ArrayList<>();

        // Set text to return to Library Activity
        backText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    finish();
                }
                return true;
            }
        });

        // Set Progress Dialogue to show retrieval progress (also for CDT as an indicator)
        progressDialog = new ProgressDialog(PopularBookActivity.this);
        progressDialog.setTitle("Retrieving Data");
        progressDialog.setMessage("Getting the crunchiest data just for you.\nPlease wait.");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        // Create CountDownTimer to wait for Firebase
        cdt = new CountDownTimer(10000, 100) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (progressDialog != null && !progressDialog.isShowing()) {
                    // Update items when firebase finishes loading

                    popularBookAdapter.notifyDataSetChanged();
                    cancel();
                }
            }

            @Override
            public void onFinish() {

            }
        };

        cdt.start();

        // Obtain Data From Firebase
        Query dataQuery = FirebaseDatabase.getInstance("https://livre-46ac7-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("public/books")
                .orderByChild("totalReaders").limitToLast(10);
        DataSnapshot dataSnapshot;
        dataQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    PopularBook book = ds.getValue(PopularBook.class);
                    bookList.add(0, book);
                }
                // Stop Progress Dialogue and Wait for CDT to update
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(PopularBookActivity.this, "An error has occurred.", Toast.LENGTH_SHORT).show();
            }
        });

        // Initialise Popular Book Recycler View
        popularBookAdapter = new PopularBookAdapter(bookList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        RecyclerView recyclerView = findViewById(R.id.popularRecyclerView);
        recyclerView.setAdapter(popularBookAdapter);
        recyclerView.setLayoutManager(linearLayoutManager);
    }
}