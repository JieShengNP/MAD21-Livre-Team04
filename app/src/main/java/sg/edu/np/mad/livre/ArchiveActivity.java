package sg.edu.np.mad.livre;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

public class ArchiveActivity extends AppCompatActivity {

    ImageView libraryImage, catalogueImage, recordTag;
    DBHandler dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_archive);

        dbHandler = new DBHandler(this);

        //Set the Library Image to send user back to Library Activity
        libraryImage = findViewById(R.id.archiveArchiveImage);
        libraryImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //Set the Catalogue Image to send user back to Catalogue Activity
        catalogueImage = findViewById(R.id.archiveCatalogueImage);
        catalogueImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ArchiveActivity.this, CatalogueActivity.class);
                startActivity(intent);
            }
        });
        recordTag = findViewById(R.id.archiveRecordTag);
        recordTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ArchiveActivity.this, RecordActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Retrieve Data and Populate Recycler View
        //Nested List is used to split into chunks for Recycler View
        ArrayList<ArrayList<Book>> bookNestedList = new ArrayList<>();
        ArrayList<Book> allNonArchivedBookList = dbHandler.GetAllArchivedBooks();
        if (allNonArchivedBookList != null){
            //Separating to 4 per list
            int chunk = 4;
            for(int i = 0; i < allNonArchivedBookList.size(); i += chunk){
                List<Book> splitBookList = allNonArchivedBookList.subList(i, Math.min(i + chunk, allNonArchivedBookList.size()));
                ArrayList<Book> splitBookArray = new ArrayList<>();
                splitBookArray.addAll(splitBookList);
                bookNestedList.add(splitBookArray);
            }
        }
        LibraryAdapter libraryAdapter = new LibraryAdapter(bookNestedList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        RecyclerView recyclerView = findViewById(R.id.archiveRecyclerView);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(libraryAdapter);
        libraryAdapter.notifyDataSetChanged();
    }
}