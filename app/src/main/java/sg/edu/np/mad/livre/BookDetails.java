package sg.edu.np.mad.livre;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

public class BookDetails extends AppCompatActivity {

    DBHandler dbHandler;
    ImageView bookImage, backtag;
    TextView bookTitle, bookDetails, bookDurationRead, bookDescription;
    Button addToLibraryBtn, toggleArchiveBtn, startReadingBtn, removeBtn;
    Book book;
    static Boolean isFromCus;

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
        addToLibraryBtn = findViewById(R.id.detAddToLibBtn);
        toggleArchiveBtn = findViewById(R.id.detToggleArcBtn);
        startReadingBtn = findViewById(R.id.detStartBtn);
        backtag = findViewById(R.id.detailsBackTag);
        removeBtn = findViewById(R.id.detRemove);

        backtag.setOnClickListener(v -> backClick());

        bookImage.setVisibility(View.VISIBLE);


        Intent receivedIntent = getIntent();
        book = (Book) receivedIntent.getSerializableExtra("BookObject");
        book.setAdded(dbHandler.isBookAdded(book));
        isFromCus =  getIntent().getExtras().getBoolean("isFromCus");


        String isCustom = "";
        if (book.isCustom){
            isCustom = "\nCustom";
            removeBtn.setText("Delete Custom Book");

            if(!(book.getThumbnail()).equals("Unavailable")){
                byte[] decodedString = Base64.decode(book.getThumbnail(), Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                bookImage.setImageBitmap(decodedByte);
            }
        }else{
            removeBtn.setText("Remove from Library");

            Picasso.get()
                    .load(book.getThumbnail())
                    .resize(135, 210)
                    .into(bookImage);

        }


;
        bookTitle.setText(book.getName());
        bookDetails.setText(book.getAuthor() +" · "+ book.getYear() +"\nISBN: " + book.getIsbn() + isCustom);
        bookDurationRead.setText("Reading Time: " + CalculateTotalTime());
        bookDescription.setText(book.getBlurb());



        if(book.isAdded()){
            toggleArchiveBtn.setVisibility(View.VISIBLE);
            startReadingBtn.setVisibility(View.VISIBLE);
            addToLibraryBtn.setVisibility(View.GONE);
            removeBtn.setVisibility(View.VISIBLE);

            if (book.isArchived()){
                toggleArchiveBtn.setText("Move to Library");
            } else {
                startReadingBtn.setVisibility(View.VISIBLE);
                toggleArchiveBtn.setText("Move to Archive");
            }

            toggleArchiveBtn.setOnClickListener(v -> togArClick(dbHandler));
            startReadingBtn.setOnClickListener(v -> startClick());
        }
        else{
            toggleArchiveBtn.setVisibility(View.GONE);
            startReadingBtn.setVisibility(View.GONE);
            addToLibraryBtn.setVisibility(View.VISIBLE);
            removeBtn.setVisibility(View.GONE);

            addToLibraryBtn.setOnClickListener(v -> addclick());
        }

        removeBtn.setOnClickListener(v-> remove(dbHandler));


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        backClick();
    }

    public void togArClick(DBHandler dbHandler){
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

    public void startClick(){
        Intent intent = new Intent(BookDetails.this, MainActivity.class);
        intent.putExtra("Isbn", book.getIsbn());
        startActivity(intent);
    }

    public void addclick(){
        book.setAdded(true);
        book.setArchived(false);
        dbHandler.AddBook(book);
        Toast.makeText(getBaseContext(), "Added", Toast.LENGTH_SHORT).show();
        recreate();
    }

    public void remove(DBHandler dbHandler){
        if (book.isCustom()){
            //alert dialogue (removing custom book)
            AlertDialog.Builder bui = new AlertDialog.Builder(BookDetails.this);
            bui.setMessage("If you delete this book, all your reading logs will be erased. You will have to customise this book again in order to read it.")
                    .setCancelable(true)
                    .setPositiveButton("Oh, delete it already!", (dialog, id) -> {
                        //User chooses to delete custom book
                        dbHandler.EraseLogs(book);
                        dbHandler.RemoveBook(book);
                        Toast.makeText(getBaseContext(), "Deleted", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    //User chooses not to
                    .setNegativeButton("Nevermind", (dialog, id) -> {return;});

            //Creating dialog box
            AlertDialog alert = bui.create();
            //Setting the title manually
            alert.setTitle("Are you sure?");
            alert.show();

        }
        else{
            dbHandler.RemoveBook(book);
            Toast.makeText(getBaseContext(), "Removed", Toast.LENGTH_SHORT).show();
            recreate();
        }

    }

    public void backClick() {
        if (book.isCustom() && book.isAdded() && isFromCus) {
            Intent intent = new Intent(BookDetails.this, LibraryActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else {
            finish();
        }
    }

    public String CalculateTotalTime(){
        int sec = book.getReadSeconds();
        int min = sec/60;
        sec = sec % 60;
        int hour = min/60;
        min = min % 60;
        String returnMessage = "";
        if (hour > 0){
            returnMessage += hour + "h ";
        }
        if (min > 0){
            returnMessage += min + "m ";
        }
        returnMessage += sec + "s";
        return returnMessage;
    }
}