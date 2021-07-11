package sg.edu.np.mad.livre;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;

public class LibraryActivity extends AppCompatActivity {

    ImageView archiveImage, catalogueImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);

        //Set the Archive Image to send user back to Archive Activity
        archiveImage = findViewById(R.id.libraryArchiveImage);
        archiveImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LibraryActivity.this, ArchiveActivity.class);
                startActivity(intent);
            }
        });

        //Set the Catalogue Image to send user back to Catalogue Activity
        catalogueImage = findViewById(R.id.libraryCatalogueImage);
        catalogueImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LibraryActivity.this, CatalogueActivity.class);
                startActivity(intent);
            }
        });

        //Retrieve Data and Populate Recycler View
        //TODO: Populate ArrayList<ArrayList<Book>> with contents
        ArrayList<ArrayList<Book>> bookNestedList = new ArrayList<>();

        LibraryAdapter libraryAdapter = new LibraryAdapter(bookNestedList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        RecyclerView recyclerView = findViewById(R.id.libraryRecyclerView);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(libraryAdapter);
    }
}