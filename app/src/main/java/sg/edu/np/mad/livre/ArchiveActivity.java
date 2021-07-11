package sg.edu.np.mad.livre;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;

public class ArchiveActivity extends AppCompatActivity {

    ImageView libraryImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_archive);

        //Set the Library Image to send user back to Library Activity
        libraryImage = findViewById(R.id.archiveArchiveImage);
        libraryImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //Retrieve Data and Populate Recycler View
        //TODO: Populate ArrayList<ArrayList<Book>> with contents
        ArrayList<ArrayList<Book>> bookNestedList = new ArrayList<>();

        LibraryAdapter libraryAdapter = new LibraryAdapter(bookNestedList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        RecyclerView recyclerView = findViewById(R.id.archiveRecyclerView);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(libraryAdapter);
    }
}