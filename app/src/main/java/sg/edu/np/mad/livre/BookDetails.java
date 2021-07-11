package sg.edu.np.mad.livre;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

public class BookDetails extends AppCompatActivity {

    ImageView bookImage;
    TextView bookTitle, bookDetails, bookDurationRead, bookDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_activity);

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
    }
}