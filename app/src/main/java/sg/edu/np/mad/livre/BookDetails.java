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
    Button addToLibraryBtn, toggleArchiveBtn, startReadingBtn;

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
        bookDetails.setText(book.getAuthor() +" · "+ book.getYear() +"\nISBN: " + book.getIsbn());
        bookDurationRead.setText("Reading Time: " + CalculateTotalTime(book));
        bookDescription.setText(book.getBlurb());


        if(startLocation == 0){
            toggleArchiveBtn = findViewById(R.id.detToggleArcBtn);
            startReadingBtn = findViewById(R.id.detStartBtn);
            toggleArchiveBtn.setVisibility(View.VISIBLE);
            if (book.isArchived()){
                toggleArchiveBtn.setText("Move to Library");
            } else {
                startReadingBtn.setVisibility(View.VISIBLE);
                toggleArchiveBtn.setText("Move to Archive");
            }
            toggleArchiveBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (book.isArchived()){
                        book.setArchived(false);
                        Toast.makeText(getBaseContext(), "Moved to Library!", Toast.LENGTH_SHORT).show();
                        toggleArchiveBtn.setText("Move to Archive");
                        startReadingBtn.setVisibility(View.VISIBLE);
                    } else {
                        book.setArchived(true);
                        Toast.makeText(getBaseContext(), "Moved to Archive!", Toast.LENGTH_SHORT).show();
                        toggleArchiveBtn.setText("Move to Library");
                        startReadingBtn.setVisibility(View.GONE);
                    }
                    dbHandler.ToggleArchive(book);
                }
            });
            startReadingBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(BookDetails.this, MainActivity.class);
                    intent.putExtra("Isbn", book.getIsbn());
                    startActivity(intent);
                }
            });
        }
        if(startLocation == 1){
            addToLibraryBtn = findViewById(R.id.detAddToLibBtn);
            addToLibraryBtn.setVisibility(View.VISIBLE);
        }


    }
    public String CalculateTotalTime(Book book){
        int sec = book.getReadSeconds();
        int min = sec/60;
        sec = sec % 60;
        int hour = min/60;
        min = min % 60;
        String returnMessage = "";
        if (hour > 0){
            returnMessage += hour + "H ";
        }
        if (min > 0){
            returnMessage += min + "M ";
        }
        returnMessage += sec + "S";
        return returnMessage;
    }
}