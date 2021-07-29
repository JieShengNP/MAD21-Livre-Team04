package sg.edu.np.mad.livre;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

public class BookDetails extends AppCompatActivity {

    static DBHandler dbHandler;
    ImageView bookImage, backtag, editBook;
    TextView bookTitle, bookDetails, bookDurationRead, bookDescription;
    Button SingleActionBtn, toggleArchiveBtn, startReadingBtn, removeBtn;
    Book book;
    String id;
    static Boolean isFromCus;
    static Boolean isFromEdit;
    static Boolean wasChanged;

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
        SingleActionBtn = findViewById(R.id.detSingleActionBtn);
        toggleArchiveBtn = findViewById(R.id.detToggleArcBtn);
        startReadingBtn = findViewById(R.id.detStartBtn);
        backtag = findViewById(R.id.detailsBackTag);
        removeBtn = findViewById(R.id.detRemove);
        editBook = findViewById(R.id.editBtn);


        Intent receivedIntent = getIntent();
        try {
            book = (Book) receivedIntent.getSerializableExtra("BookObject");
            book.setAdded(dbHandler.isBookAdded(book));
            isFromEdit = false;
            isFromCus = false;
            if(getIntent().getStringExtra("prev") != null){
                if(getIntent().getStringExtra("prev").equals("Cus")){
                    isFromCus = true;
                    isFromEdit = false;
                }
                else if (getIntent().getStringExtra("prev").equals("Edit")){
                    isFromEdit = true;
                    isFromCus = false;
                    id = getIntent().getStringExtra("EditId");
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
            Toast.makeText(BookDetails.this, "Error", Toast.LENGTH_SHORT).show();
            finish();
        }

        if(wasChanged == null){
            wasChanged = false;
        }

        Log.v("WC", String.valueOf(wasChanged));
        Log.v("isFCus", String.valueOf(isFromCus));
        Log.v("isedit", String.valueOf(isFromEdit));
        Log.v("isadded", String.valueOf(book.isAdded()));
        Log.v("iscustom", String.valueOf(book.isCustom()));


        backtag.setOnClickListener(v -> backClick());

        editBook.setOnClickListener(v -> editBookClick());

        bookImage.setVisibility(View.VISIBLE);


        String isCustom = "";
        if (book.isCustom()){
            if((book.isAdded() && isFromEdit && wasChanged) || (book.isAdded() && !isFromEdit)) {
                editBook.setVisibility(View.VISIBLE);
            }
            else{
                editBook.setVisibility(View.GONE);
            }

            isCustom = "\nCustom";
            removeBtn.setText("Delete Custom Book");

            if(!(book.getThumbnail()).equals("Unavailable")){
                byte[] decodedString = Base64.decode(book.getThumbnail(), Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                bookImage.setImageBitmap(decodedByte);
            }
        }else{
            editBook.setVisibility(View.GONE);
            removeBtn.setText("Remove from Library");

            Picasso.get()
                    .load(book.getThumbnail())
                    .resize(135, 210)
                    .into(bookImage);

        }

        bookTitle.setText(book.getName());
        bookDetails.setText(book.getAuthor() +" · "+ book.getYear() +"\nISBN: " + book.getIsbn() + isCustom);
        bookDurationRead.setText("Reading Time: " + CalculateTotalTime());
        bookDescription.setText(book.getBlurb());



        if((book.isCustom() && book.isAdded() && isFromCus)|| (book.isCustom() && isFromEdit && wasChanged) || (book.isCustom() && !isFromCus && !isFromEdit) || (!book.isCustom && book.isAdded())){
            toggleArchiveBtn.setVisibility(View.VISIBLE);
            startReadingBtn.setVisibility(View.VISIBLE);
            SingleActionBtn.setVisibility(View.GONE);
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
            if (book.isCustom() && isFromEdit){
                SingleActionBtn.setText("Save Changes");
                SingleActionBtn.setOnClickListener(v -> saveClick(id));
            }
            else{
                SingleActionBtn.setText("Add to Library");
                SingleActionBtn.setOnClickListener(v -> addclick());
            }



            toggleArchiveBtn.setVisibility(View.GONE);
            startReadingBtn.setVisibility(View.GONE);
            SingleActionBtn.setVisibility(View.VISIBLE);
            removeBtn.setVisibility(View.GONE);

        }

        removeBtn.setOnClickListener(v-> remove(dbHandler));


   }

    public void saveClick(String id) {
        int rowsAffected = dbHandler.UpdateBook(book, id);
        if (rowsAffected == 0){
            Toast.makeText(getBaseContext(), "Book does not exist, please delete.", Toast.LENGTH_SHORT).show();
            finish();
        }
        Toast.makeText(getBaseContext(), "Saved", Toast.LENGTH_SHORT).show();
        wasChanged = true;
        recreate();

    }

    public void editBookClick() {
        Intent editIntent = new Intent(getApplicationContext(), EditBook.class);
        editIntent.putExtra("BookObject", book);
        startActivity(editIntent);
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
                        backClick();
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

        if ((book.isCustom() && book.isAdded() && isFromCus) || wasChanged) {
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
            returnMessage += hour + "H ";
        }
        if (min > 0){
            returnMessage += min + "M ";
        }
        returnMessage += sec + "S";
        return returnMessage;
    }
}