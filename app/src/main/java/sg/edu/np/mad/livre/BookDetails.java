package sg.edu.np.mad.livre;

import android.app.ActivityManager;
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

import java.util.List;

public class BookDetails extends AppCompatActivity {

    static DBHandler dbHandler;
    ImageView bookImage, backtag, editBook;
    TextView bookTitle, bookDetails, bookDurationRead, bookDescription;
    Button SingleActionBtn, toggleArchiveBtn, startReadingBtn, removeBtn;
    String id;
    static Book book;
    static Boolean isFromCus;
    static Boolean isFromEdit;
    static Boolean wasChanged;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_activity);
        dbHandler = new DBHandler(this);

        //find views
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

        //get intent and get book object.
        //find out which view user came from

        Intent receivedIntent = getIntent();
        try {
            isFromEdit = false;
            isFromCus = false;
            if (getIntent().getStringExtra("prev") != null) {
                if (getIntent().getStringExtra("prev").equals("Cus")) {
                    isFromCus = true;
                    isFromEdit = false;
                } else if (getIntent().getStringExtra("prev").equals("Edit")) {
                    isFromEdit = true;
                    isFromCus = false;
                    //get id of book if user came from edit
                    id = getIntent().getStringExtra("EditId");
                }
            }

            if (book == null) {
                book = (Book) receivedIntent.getSerializableExtra("BookObject");
                //set added of book
                book.setAdded(dbHandler.isBookAdded(book));
            }
        } catch (Exception e) {
            //finish if there is an error
            e.printStackTrace();
            Toast.makeText(BookDetails.this, "Error", Toast.LENGTH_SHORT).show();
            finish();
        }

        //set waschanged (if book came from edit has has been changed), to false if null
        if (wasChanged == null) {
            wasChanged = false;
        }

        //set onclick listeners and make
        backtag.setOnClickListener(v -> backClick());

        editBook.setOnClickListener(v -> editBookClick());

        bookImage.setVisibility(View.VISIBLE);

        //show and hide bookdurationread based on whether book isadded
        if (book.isAdded()) {
            bookDurationRead.setVisibility(View.VISIBLE);
        } else {
            bookDurationRead.setVisibility(View.GONE);
        }

        //set string that would say "custom" if book iscustom
        //and blank if it is not
        String customTxt = "";
        if (book.isCustom()) {
            //if book is custom

            //if book user edits a book and saves the edit
            //or has edited the book and does not have an edit pending saving
            //show edit button, if not, don't
            if ((book.isAdded() && isFromEdit && wasChanged) || (book.isAdded() && !isFromEdit)) {
                editBook.setVisibility(View.VISIBLE);
            } else {
                editBook.setVisibility(View.GONE);
            }

            //set custom text and set remove button
            customTxt = "\nCustom";
            removeBtn.setText("Delete Custom Book");

            //if books's thumbnail is not unavailable, decode base64 to bitmap and set it
            if (!(book.getThumbnail()).equals("Unavailable")) {
                byte[] decodedString = Base64.decode(book.getThumbnail(), Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                bookImage.setImageBitmap(decodedByte);
            }
        } else {
            //if book is not custom

            //dont show option to edit & set text to remove button
            editBook.setVisibility(View.GONE);
            removeBtn.setText("Remove from Library");

            //use picasso to get and set image
            Picasso.get()
                    .load(book.getThumbnail())
                    .resize(135, 210)
                    .into(bookImage);

        }

        //set details of book to view
        bookTitle.setText(book.getName());
        String details = book.getAuthor() + " · " + book.getYear() + "\nISBN: " + book.getIsbn() + customTxt;
        bookDetails.setText(details);
        String duration = "Reading Time: " + CalculateTotalTime();
        bookDurationRead.setText(duration);
        bookDescription.setText(book.getBlurb());


        //if book is not custom but is added
        //or if book is from custom and just got added
        //or is from edit and was just changed
        if ((book.isCustom() && book.isAdded() && isFromCus) || (book.isCustom() && isFromEdit && wasChanged) || (book.isCustom() && !isFromCus && !isFromEdit) || (!book.isCustom() && book.isAdded())) {
            //show buttons meant for edited book with no edits or creation pending
            toggleArchiveBtn.setVisibility(View.VISIBLE);
            SingleActionBtn.setVisibility(View.GONE);
            removeBtn.setVisibility(View.VISIBLE);

            //set start reading button and text of toggle archive button based on whether book is archived
            if (book.isArchived()) {
                toggleArchiveBtn.setText("Move to Library");
            } else {
                startReadingBtn.setVisibility(View.VISIBLE);
                toggleArchiveBtn.setText("Move to Archive");
            }

            // set onclicklisteners
            toggleArchiveBtn.setOnClickListener(v -> togArClick(dbHandler));
            startReadingBtn.setOnClickListener(v -> startClick());
        } else {
            //something (edit/add) is pending for the book

            //if book is from edit and is pending changes, show button button that saves
            //if not show button that adds to library
            if (book.isCustom() && isFromEdit) {
                SingleActionBtn.setText("Save Changes");
                SingleActionBtn.setOnClickListener(v -> saveClick(id));
            } else {
                SingleActionBtn.setText("Add to Library");
                SingleActionBtn.setOnClickListener(v -> addclick());
            }

            //change button visibility accordingly
            toggleArchiveBtn.setVisibility(View.GONE);
            startReadingBtn.setVisibility(View.GONE);
            SingleActionBtn.setVisibility(View.VISIBLE);
            removeBtn.setVisibility(View.GONE);

        }

        //set onclicklistener for remove button
        removeBtn.setOnClickListener(v -> remove(dbHandler));
    }

    public void saveClick(String id) {
        //user wants to save changes to book

        //try to update book, finish if it fails & tell user to delete book
        int rowsAffected = dbHandler.UpdateBook(book, id);
        if (rowsAffected == 0) {
            Toast.makeText(getApplicationContext(), "Book does not exist, please delete.", Toast.LENGTH_SHORT).show();
            finish();
        }

        //if it works, save and bring user to library
        Toast.makeText(getBaseContext(), "Saved", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getApplicationContext(),LibraryActivity.class);
        book = null;
        startActivity(intent);
        wasChanged = true;

    }

    public void editBookClick() {
        //user wants to edit book, open editbook class and pass book object

        Intent editIntent = new Intent(getApplicationContext(), EditBook.class);
        editIntent.putExtra("BookObjectForEdit", book);

        book = null;
        startActivity(editIntent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //overwrite onbackpressed

        backClick();
    }

    public void togArClick(DBHandler dbHandler) {
        //toggle book between archived and not archived, set buttons and text accordingly

        if (book.isArchived()) {
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

        //update database
        dbHandler.ToggleArchive(book);
    }

    public void startClick() {
        //user wants to start reading

        //user wants to start reading, start activity and pass isbn
        try {
            Intent intent = new Intent(BookDetails.this, MainActivity.class);
            intent.putExtra("BookID", dbHandler.GetBookId(book));
            book = null;
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(getBaseContext(), "Error", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(BookDetails.this, LibraryActivity.class);
            book = null;
            startActivity(intent);
        }
    }

    public void addclick() {
        //user wants to add book, update information, update database, toast
        //and recreate to show updated buttons and options
        book.setAdded(true);
        book.setArchived(false);
        dbHandler.AddBook(book);
        recreate();
        Toast.makeText(getBaseContext(), "Added", Toast.LENGTH_SHORT).show();
    }

    public void remove(DBHandler dbHandler) {
        //user wants to remove book

        if (book.isCustom()) {
            //if book is custom

            //alert dialogue (removing custom book permanently)
            AlertDialog.Builder bui = new AlertDialog.Builder(BookDetails.this);
            bui.setMessage("If you delete this book, all your reading logs for this book will be erased. You will have to customise this book again in order to read it.")
                    .setCancelable(true)
                    .setPositiveButton("Oh, delete it already!", (dialog, id) -> {
                        //User chooses to delete custom book and erase logs, exit activity
                        book.setID(dbHandler.GetBookId(book));
                        dbHandler.EraseLogs(book.getID());
                        dbHandler.RemoveBook(book);
                        book = null;
                        Intent intent = new Intent(getApplicationContext(), LibraryActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        Toast.makeText(getBaseContext(), "Deleted", Toast.LENGTH_SHORT).show();
                    })
                    //User chooses not to
                    .setNegativeButton("Nevermind", (dialog, id) -> {
                        return;
                    });

            //Creating dialog box
            AlertDialog alert = bui.create();
            //Setting the title manually
            alert.setTitle("Are you sure?");
            alert.show();

        } else {
            //alert dialogue (removing custom book permanently)
            AlertDialog.Builder bui = new AlertDialog.Builder(BookDetails.this);
            bui.setMessage("If you remove this book, all your reading logs for this book will be erased. This is irreversible!")
                    .setCancelable(true)
                    .setPositiveButton("Oh, remove it already!", (dialog, id) -> {
                        //User chooses to delete custom book and erase logs, exit activity
                        book.setAdded(false);
                        book.setReadSeconds(0);
                        book.setID(dbHandler.GetBookId(book));
                        dbHandler.EraseLogs(book.getID());
                        dbHandler.RemoveBook(book);
                        recreate();
                        Toast.makeText(getBaseContext(), "Removed", Toast.LENGTH_SHORT).show();
                    })
                    //User chooses not to
                    .setNegativeButton("Nevermind", (dialog, id) -> {
                        return;
                    });

            //Creating dialog box
            AlertDialog alert = bui.create();
            //Setting the title manually
            alert.setTitle("Are you sure?");
            alert.show();

        }

    }

    public void backClick() {
        //to exit activity
        //is book is is from cus or edit and was just added/saved, go to library and clear queue
        if ((book.isCustom() && book.isAdded() && isFromCus) || wasChanged) {
            //set waschanged to false to
            wasChanged = false;
            book = null;

            //intent
            Intent intent = new Intent(BookDetails.this, LibraryActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else {
            book = null;

            //if activity is last one in stack, go to library
            ActivityManager mngr = (ActivityManager) getSystemService(ACTIVITY_SERVICE);

            List<ActivityManager.RunningTaskInfo> taskList = mngr.getRunningTasks(10);

            if (taskList.get(0).numActivities == 1 &&
                    taskList.get(0).topActivity.getClassName().equals(this.getClass().getName())) {
                Intent intent = new Intent(BookDetails.this, LibraryActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            } else {

                finish();
            }
        }
    }

    public String CalculateTotalTime() {
        //return a string with time in 00H 00M 00S
        int sec = 0;

        //if book.getReadSeconds() is invalid, return unavailable
        try {
            sec = book.getReadSeconds();
        } catch (Exception e) {
            e.printStackTrace();
            return "Unavailable";
        }

        int min = sec / 60;
        sec = sec % 60;
        int hour = min / 60;
        min = min % 60;

        String returnMessage = "";
        if (hour > 0) {
            returnMessage += hour + "H ";
        }
        if (min > 0) {
            returnMessage += min + "M ";
        }
        returnMessage += sec + "S";

        return returnMessage;
    }
}