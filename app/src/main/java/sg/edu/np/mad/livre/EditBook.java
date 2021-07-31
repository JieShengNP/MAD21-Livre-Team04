package sg.edu.np.mad.livre;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

public class EditBook extends AppCompatActivity {

    public static DBHandler dbHandler;
    ImageView tag, coverImg;
    TextView editTxt;
    Button submitBtn, editCoverBtn;
    EditText editTitle, editAuthor, editPublishYear, editISBN, editBlurb;
    public static String thumbnailBM;
    public static Book book;
    public static int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_book);

        //find views
        dbHandler = new DBHandler(this);
        submitBtn = findViewById(R.id.editDoneBtn);
        editTitle = findViewById(R.id.editTitle);
        editAuthor = findViewById(R.id.editAuth);
        editPublishYear = findViewById(R.id.editYear);
        editISBN = findViewById(R.id.editISBN);
        editBlurb = findViewById(R.id.editSynopsis);
        editCoverBtn = findViewById(R.id.editCoverBtn);
        coverImg = findViewById(R.id.coverEdit);
        tag = findViewById(R.id.editTag);



        //check if intent has title, set if have
        Intent receivedIntent = getIntent();
        try {
            book = (Book) receivedIntent.getSerializableExtra("BookObjectForEdit");

            //find id of book, finish if error
            int idnew = dbHandler.GetBookId(book);
            if(idnew == -1) {
                Toast.makeText(getApplicationContext(), "Book does not exist, please delete.", Toast.LENGTH_SHORT).show();
                finish();
            }

            id = idnew;

            editTitle.setText(book.getName());
            editAuthor.setText(book.getAuthor());
            editPublishYear.setText(book.getYear());
            editISBN.setText(book.getIsbn());
            editBlurb.setText(book.getBlurb());

            if(book.getThumbnail().equals("Unavailable")){
                thumbnailBM = "Unavailable";
            }else{
                thumbnailBM = book.getThumbnail();
                //decode thumbnail and set
                byte[] decodedString = Base64.decode(thumbnailBM, Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                coverImg.setImageBitmap(decodedByte);
            }
        }
        catch (Exception e){
            e.printStackTrace();
            Toast.makeText(EditBook.this, "Error", Toast.LENGTH_SHORT).show();
            finish();
        }



        //set onclick for tag
        tag.setOnClickListener(v -> back());

        //open app for user to select image onclick
        editCoverBtn.setOnClickListener(v -> launcher.launch("image/*"));

        //text changed listeners, validate when triggered
        editAuthor.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                //After text is modified, validate and show error if necessary
                AuthorValidation();
            }
        });
        editPublishYear.addTextChangedListener(new TextWatcher() {
            //do nothing
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                //After text is modified, validate and show error if necessary
                PubYearValidation();
            }
        });
        editISBN.addTextChangedListener(new TextWatcher() {
            //do nothing
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                //After text is modified, validate and show error if necessary
                ISBNValidation();
            }
        });

        //when submit button is clicked
        submitBtn.setOnClickListener(v -> {
            //Hide keyboard
            ((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(editBlurb.getWindowToken(), 0);

            //check for missing values, toast and error if there are
            //no errors -> submit
            if(!isChanged()){
                Toast.makeText(getApplicationContext(), "No changes to save", Toast.LENGTH_SHORT).show();
                return;
            }
            else if(thumbnailBM == null){
                editCoverBtn.setError("Please set book cover");
                return;
            }
            else if(editTitle.getText().toString().length() == 0){
                editTitle.setError("Please enter a Title");
                Toast.makeText(getApplicationContext(), "Invalid", Toast.LENGTH_SHORT).show();
                return;
            }
            else if(editBlurb.getText().toString().length() == 0){
                editBlurb.setError("Please enter a Synopsis");
                Toast.makeText(getApplicationContext(), "Invalid", Toast.LENGTH_SHORT).show();
                return;
            }
            else if(!(AuthorValidation() && PubYearValidation() && ISBNValidation())){
                Toast.makeText(getApplicationContext(), "Invalid", Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(getApplicationContext(), "Valid", Toast.LENGTH_SHORT).show();
            ValidatedEdit();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        //reset original book from database
        if(id != -1 && book !=null){
            //find id of book, finish if error
            int idnew = dbHandler.GetBookId(book);
            if(idnew == -1) {
                Toast.makeText(getApplicationContext(), "Book does not exist, please delete.", Toast.LENGTH_SHORT).show();
                finish();
            }

            id = idnew;

            //create copy of original
            book = dbHandler.FindBookByID(idnew);
        }
    }

    @Override
    public void onBackPressed() {
        //overwrite default onbackpressed
        back();
    }

    @Override
    public void onConfigurationChanged(@NotNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //make changed based on orientation
        setOrientationDifferences();
    }

    //open app for user to select images and get result
    ActivityResultLauncher<String> launcher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri result) {
                    if (result != null) {
                        //if result is not null

                        //Based on buld version, decode image to bitmap and resize to not crash app with big sizes
                        //error if process reaches exception
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                            try {
                                Bitmap bitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(getApplicationContext().getContentResolver(), result));
                                bitmap = Bitmap.createScaledBitmap(bitmap, 102, 160, false);

                                //set resized bitmap
                                bitSet(bitmap);

                            } catch (IOException e) {
                                e.printStackTrace();
                                Toast.makeText(getApplicationContext(), "Invalid file", Toast.LENGTH_SHORT).show();
                                thumbnailBM = "Unavailable";
                                return;
                            }
                        }
                        else {
                            //older version
                            try {
                                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), result);
                                bitmap = Bitmap.createScaledBitmap(bitmap, 102, 160, false);

                                //set resized bitmap
                                bitSet(bitmap);
                            } catch (IOException e) {
                                e.printStackTrace();
                                Toast.makeText(getApplicationContext(), "Invalid file", Toast.LENGTH_SHORT).show();
                                thumbnailBM = "Unavailable";
                                return;
                            }
                        }
                        //remove error
                        editCoverBtn.setError(null);
                    }
                }
            });

    public void bitSet(Bitmap bitmap){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        //try to compress with png, if it fails, use jpg, show error message if both fail
        try{
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        }
        catch (Exception e){
            e.printStackTrace();
            try{
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            }
            catch (Exception ex){
                ex.printStackTrace();
                Toast.makeText(getApplicationContext(), "Invalid file", Toast.LENGTH_SHORT).show();
                thumbnailBM = "Unavailable";
                return;
            }
        }

        //ensure that image can be encoded and decoded in full to prevent future errors
        try {
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            thumbnailBM = Base64.encodeToString(byteArray, Base64.DEFAULT);

            //decode
            byte[] decodedString = Base64.decode(thumbnailBM, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

            coverImg.setImageBitmap(decodedByte);
        }
        catch (Exception e){
            //decode
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Invalid file", Toast.LENGTH_SHORT).show();
            thumbnailBM = "Unavailable";
            return;

        }
    }

    public void unsavedChangesWarning() {
        //alert dialogue (removing edit book)
        AlertDialog.Builder bui = new AlertDialog.Builder(EditBook.this);
        bui.setMessage("Delete unsaved changes?")
                .setCancelable(true)
                .setPositiveButton("Sure", (dialog, id) -> {

                    //find id of book, finish if error
                    int idnew = dbHandler.GetBookId(book);
                    if(idnew == -1) {
                        Toast.makeText(getApplicationContext(), "Book does not exist, please delete.", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    //get book from database
                    book = dbHandler.FindBookByID(idnew);

                    Intent bookDetailsIntent = new Intent(getApplicationContext(), BookDetails.class);
                    bookDetailsIntent.putExtra("BookObject", book);
                    bookDetailsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(bookDetailsIntent);
                })
                //User chooses not to
                .setNegativeButton("No", (dialog, id) -> {return;});

        //Creating dialog box
        AlertDialog alert = bui.create();
        //Setting the title manually
        alert.setTitle("Are you sure?");
        alert.show();
    }

    public void ValidatedEdit(){
        //for valid edits
        //create book object and set values


        Book newBook = book;
        newBook.setName(editTitle.getText().toString());
        newBook.setName(editTitle.getText().toString());
        newBook.setAuthor(editAuthor.getText().toString());
        newBook.setYear(editPublishYear.getText().toString());
        newBook.setIsbn(editISBN.getText().toString());
        newBook.setBlurb(editBlurb.getText().toString());
        newBook.setCustom(true);
        newBook.setThumbnail(thumbnailBM);

        Log.v("AAAAAAAAA", String.valueOf(id));
        //create intents and set putextras, start intent -> go to bookdetails
        Intent intent = new Intent(getApplicationContext(), BookDetails.class);
        intent.putExtra("BookObject", newBook);
        intent.putExtra("prev", "Edit");
        intent.putExtra("EditId", String.valueOf(id));
        startActivity(intent);
    }

    public Boolean AuthorValidation(){
        //author's validation

        //regex that return true for any numbers in string
        Pattern p = Pattern.compile(".*\\d+.*");

        if(editAuthor.getText().toString().length() == 0){
            editAuthor.setError("Please enter an Author");
        }
        else if(p.matcher(editAuthor.getText()).matches()){
            editAuthor.setError("No numbers!");
        }
        else if(editAuthor.getText().length() > 20){
            editAuthor.setError("Max length 20!");
        }
        else{
            return true;
        }
        return false;
    }

    public Boolean PubYearValidation(){
        //validation for year that works for API 19
        //create date object out of input
        String newDateStr = "01/"+"01/"+editPublishYear.getText();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        Date strDate = new Date();
        try {
            strDate = sdf.parse(newDateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //check length of input and validity of date object
        if(editPublishYear.getText().length() > 4 || editPublishYear.getText().length() < 1){
            editPublishYear.setError("Invalid Year!");
        }
        else if((new Date()).before(strDate)){
            editPublishYear.setError("Invalid Year!");
        }
        else{
            return true;
        }
        return false;
    }

    public Boolean ISBNValidation(){
        //create book object and validate
        Book b = new Book();
        b.setCustom(true);
        b.setIsbn(editISBN.getText().toString());
        if(b.getIsbn().length() != 13){
            editISBN.setError("Invalid ISBN-13!");
        }else if (dbHandler.isBookAdded(b) && !(b.getIsbn().equals(book.getIsbn()))){
                editISBN.setError("Custom book exists with this ISBN");
        }
        else{
            return true;
        }

        return false;
    }

    public void setOrientationDifferences(){
        tag = findViewById(R.id.editTag);
        editTxt = findViewById(R.id.editText);
        //if landscape
        if(getApplicationContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            //tag
            tag.getLayoutParams().height = Math.round(70 * Resources.getSystem().getDisplayMetrics().density);
            ((ViewGroup.MarginLayoutParams) tag.getLayoutParams()).setMargins(0,0,0,0);
            tag.setRotation(270);
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) tag.getLayoutParams();
            p.setMargins(0, Math.round(20 * Resources.getSystem().getDisplayMetrics().density), Math.round(52 * Resources.getSystem().getDisplayMetrics().density),0);

            //catalogue text
            editTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 42);
            ViewGroup.MarginLayoutParams ct = (ViewGroup.MarginLayoutParams) editTxt.getLayoutParams();
            ct.setMargins(Math.round(48 * Resources.getSystem().getDisplayMetrics().density), Math.round(0 * Resources.getSystem().getDisplayMetrics().density),0,0);
            editTxt.getLayoutParams().height = Math.round(62 * Resources.getSystem().getDisplayMetrics().density);

        }
        else { //if portrait
            //tag
            tag.getLayoutParams().height = Math.round(107 * Resources.getSystem().getDisplayMetrics().density);
            ((ViewGroup.MarginLayoutParams) tag.getLayoutParams()).setMargins(0, 0, Math.round(52 * Resources.getSystem().getDisplayMetrics().density),0);
            tag.setRotation(0);
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) tag.getLayoutParams();
            p.setMargins(0,0, Math.round(52 * Resources.getSystem().getDisplayMetrics().density),0);

            //catalogue text
            editTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 60);
            ViewGroup.MarginLayoutParams ct = (ViewGroup.MarginLayoutParams) editTxt.getLayoutParams();
            ct.setMargins(Math.round(48 * Resources.getSystem().getDisplayMetrics().density), Math.round(16 * Resources.getSystem().getDisplayMetrics().density),0,0);
            editTxt.getLayoutParams().height = Math.round(85 * Resources.getSystem().getDisplayMetrics().density);

        }
        //ensure changes are applied
        tag.requestLayout();
    }

    public void back(){

        //check for any value in any of the strings
        //alert user if there are
        //finish if there aren't
        if(isChanged()){
            unsavedChangesWarning();
        }
        else{
            //find id of book, finish if error
            int idnew = dbHandler.GetBookId(book);
            if(idnew == -1) {
                Toast.makeText(getApplicationContext(), "Book does not exist, please delete.", Toast.LENGTH_SHORT).show();
                finish();
            }

            //get book from database
            book = dbHandler.FindBookByID(idnew);

            Intent bookDetailsIntent = new Intent(getApplicationContext(), BookDetails.class);
            bookDetailsIntent.putExtra("BookObject", book);
            bookDetailsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(bookDetailsIntent);
        }
    }

    public Boolean isChanged(){
        //return true if anything has been changed, false if no
        return !editTitle.getText().toString().equals(book.getName()) || !editAuthor.getText().toString().equals(book.getAuthor()) || !editPublishYear.getText().toString().equals(book.getYear()) || !editISBN.getText().toString().equals(book.getIsbn()) || !editBlurb.getText().toString().equals(book.getBlurb()) || !thumbnailBM.equals(book.getThumbnail());
    }
}