package sg.edu.np.mad.livre;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

public class BookDetails extends AppCompatActivity {

    DBHandler dbHandler;
    ImageView bookImage;
    TextView bookTitle, bookDetails, bookDurationRead, bookDescription;
    Button addToLibraryBtn, toggleArchiveBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_activity);

        dbHandler = new DBHandler(this);

        bookImage = findViewById(R.id.thumbdet);
        bookTitle = findViewById(R.id.detTitle);
        bookDetails = findViewById(R.id.detinfo);
        bookDurationRead = findViewById(R.id.timeSpentDet);
        bookDescription = findViewById(R.id.detDesc);

        Intent receivedIntent = getIntent();
        // Start Location: 0 for Archive/Library, 1 for Catalogue (Helps with buttons assignment)
        int startLocation = receivedIntent.getIntExtra("StartLocation", 0);
        Book book = (Book) receivedIntent.getSerializableExtra("BookObject");
        Picasso.get()
                .load(book.getThumbnail())
                .resize(135, 210)
                .into(bookImage);
        bookTitle.setText(book.getName());
        bookDetails.setText(book.getAuthor() + "\nISBN: " + book.getIsbn());
        bookDurationRead.setText("Reading Time: " + String.valueOf(book.getReadSeconds()));
        bookDescription.setText(book.getBlurb());


        if(startLocation == 0){
            toggleArchiveBtn = findViewById(R.id.detToggleArcBtn);
            if (book.isArchived()){
                toggleArchiveBtn.setText("Move to Library");
            } else {
                toggleArchiveBtn.setText("Move to Archive");
            }
            toggleArchiveBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (book.isArchived()){
                        book.setArchived(false);
                        Toast.makeText(getBaseContext(), "Moved to Library!", Toast.LENGTH_SHORT).show();
                        toggleArchiveBtn.setText("Move to Archive");
                    } else {
                        book.setArchived(true);
                        Toast.makeText(getBaseContext(), "Moved to Archive!", Toast.LENGTH_SHORT).show();
                        toggleArchiveBtn.setText("Move to Library");
                    }
                    dbHandler.ToggleArchive(book);
                }
            });
            toggleArchiveBtn.setVisibility(View.VISIBLE);
        }
        if(startLocation == 1){
            addToLibraryBtn = findViewById(R.id.detAddToLibBtn);
            addToLibraryBtn.setVisibility(View.VISIBLE);
        }
    }
}