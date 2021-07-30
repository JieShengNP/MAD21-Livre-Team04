package sg.edu.np.mad.livre;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
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
import android.util.TypedValue;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

public class CustomiseBook extends AppCompatActivity {

    DBHandler dbHandler;
    ImageView tag, coverImg;
    TextView cusTxt;
    Button submitBtn, cusCoverBtn;
    EditText customTitle, customAuthor, customPublishYear, customISBN, customBlurb;
    public static String thumbnailBM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customise_book);

        //find views
        dbHandler = new DBHandler(this);
        submitBtn = findViewById(R.id.customiseDoneBtn);
        customTitle = findViewById(R.id.cusTitleEdit);
        customAuthor = findViewById(R.id.cusAuthEdit);
        customPublishYear = findViewById(R.id.cusYearEdit);
        customISBN = findViewById(R.id.cusISBNEdit);
        customBlurb = findViewById(R.id.cusSynopsisEdit);
        cusCoverBtn = findViewById(R.id.cusCoverBtn);
        coverImg = findViewById(R.id.coverCus);
        tag = findViewById(R.id.cusTag);

        if(customBlurb.getText().toString().length() == 0){
            thumbnailBM = null;
        }

        //check if intent has title, set if have
        Intent receivedIntent = getIntent();
        if(receivedIntent.getSerializableExtra("Title") != null) {
            customTitle.setText(receivedIntent.getSerializableExtra("Title").toString());
        }

        //set onclick for tag
        tag.setOnClickListener(v -> back());

        //open app for user to select image onclick
        cusCoverBtn.setOnClickListener(v -> launcher.launch("image/*"));

        //text changed listeners, validate when triggered
        customAuthor.addTextChangedListener(new TextWatcher() {
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
        customPublishYear.addTextChangedListener(new TextWatcher() {
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
        customISBN.addTextChangedListener(new TextWatcher() {
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
            ((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(customBlurb.getWindowToken(), 0);

            //check for missing values, toast and error if there are
            //no errors -> submit
            if(thumbnailBM == null){
                cusCoverBtn.setError("Please set book cover");
                Toast.makeText(getApplicationContext(), "Invalid", Toast.LENGTH_SHORT).show();
                return;
            }
            else if(customTitle.getText().toString().length() == 0){
                customTitle.setError("Please enter a Title");
                Toast.makeText(getApplicationContext(), "Invalid", Toast.LENGTH_SHORT).show();
                return;
            }
            else if(customBlurb.getText().toString().length() == 0){
                customBlurb.setError("Please enter a Synopsis");
                Toast.makeText(getApplicationContext(), "Invalid", Toast.LENGTH_SHORT).show();
                return;
            }
            else if(!(AuthorValidation() && PubYearValidation() && ISBNValidation())){
                Toast.makeText(getApplicationContext(), "Invalid", Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(getApplicationContext(), "Valid", Toast.LENGTH_SHORT).show();
            ValidatedSubmission();
        });
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
                        cusCoverBtn.setError(null);
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
            //error is fails
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Invalid file", Toast.LENGTH_SHORT).show();
            thumbnailBM = "Unavailable";
            return;

        }
    }

    public void unsavedChangesWarning() {
        //alert dialogue (removing custom book)
        AlertDialog.Builder bui = new AlertDialog.Builder(CustomiseBook.this);
        bui.setMessage("Delete unsaved changes?")
                .setCancelable(true)
                .setPositiveButton("Sure", (dialog, id) -> finish())
                //User chooses not to
                .setNegativeButton("No", (dialog, id) -> {return;});

        //Creating dialog box
        AlertDialog alert = bui.create();
        //Setting the title manually
        alert.setTitle("Are you sure?");
        alert.show();
    }

    public void ValidatedSubmission(){
        //for valid submissions
        //create book object and set values
        Book book = new Book();
        book.setName(customTitle.getText().toString());
        book.setAuthor(customAuthor.getText().toString());
        book.setYear(customPublishYear.getText().toString());
        book.setIsbn(customISBN.getText().toString());
        book.setBlurb(customBlurb.getText().toString());
        book.setAdded(false);
        book.setCustom(true);
        book.setArchived(false);
        book.setThumbnail(thumbnailBM);

        //create intents and set putextras, start intent -> go to bookdetails
        Intent intent = new Intent(getBaseContext(), BookDetails.class);
        intent.putExtra("BookObject", book);
        intent.putExtra("prev", "Cus");
        startActivity(intent);
    }

    public Boolean AuthorValidation(){
        //author's validation

        //regex that return true for any numbers in string
        Pattern p = Pattern.compile(".*\\d+.*");

        if(customAuthor.getText().toString().length() == 0){
            customAuthor.setError("Please enter an Author");
        }
        else if(p.matcher(customAuthor.getText()).matches()){
            customAuthor.setError("No numbers!");
        }
        else if(customAuthor.getText().length() > 20){
            customAuthor.setError("Max length 20!");
        }
        else{
            return true;
        }
        return false;
    }

    public Boolean PubYearValidation(){
        //validation for year that works for API 19
        //create date object out of input
        String newDateStr = "01/"+"01/"+customPublishYear.getText();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        Date strDate = new Date();
        try {
            strDate = sdf.parse(newDateStr);
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }

        //check length of input and validity of date object
        if(customPublishYear.getText().length() > 4 || customPublishYear.getText().length() < 1){
            customPublishYear.setError("Invalid Year!");
        }
        else if((new Date()).before(strDate)){
            customPublishYear.setError("Invalid Year!");
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
        b.setIsbn(customISBN.getText().toString());
        if(b.getIsbn().length() != 13){
            customISBN.setError("Invalid ISBN-13!");
        }else if (dbHandler.isBookAdded(b)){
            customISBN.setError("Custom book exists with this ISBN");
        }
        else{
            return true;
        }
        return false;
    }

    public void setOrientationDifferences(){
        tag = findViewById(R.id.cusTag);
        cusTxt = findViewById(R.id.customiseText);
        //if landscape
        if(getApplicationContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            //tag
            tag.getLayoutParams().height = Math.round(70 * Resources.getSystem().getDisplayMetrics().density);
            ((ViewGroup.MarginLayoutParams) tag.getLayoutParams()).setMargins(0,0,0,0);
            tag.setRotation(270);
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) tag.getLayoutParams();
            p.setMargins(0, Math.round(20 * Resources.getSystem().getDisplayMetrics().density), Math.round(52 * Resources.getSystem().getDisplayMetrics().density),0);

            //catalogue text
            cusTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 42);
            ViewGroup.MarginLayoutParams ct = (ViewGroup.MarginLayoutParams) cusTxt.getLayoutParams();
            ct.setMargins(Math.round(48 * Resources.getSystem().getDisplayMetrics().density), Math.round(0 * Resources.getSystem().getDisplayMetrics().density),0,0);
            cusTxt.getLayoutParams().height = Math.round(62 * Resources.getSystem().getDisplayMetrics().density);

            }
        else { //if portrait
            //tag
            tag.getLayoutParams().height = Math.round(107 * Resources.getSystem().getDisplayMetrics().density);
            ((ViewGroup.MarginLayoutParams) tag.getLayoutParams()).setMargins(0, 0, Math.round(52 * Resources.getSystem().getDisplayMetrics().density),0);
            tag.setRotation(0);
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) tag.getLayoutParams();
            p.setMargins(0,0, Math.round(52 * Resources.getSystem().getDisplayMetrics().density),0);

            //catalogue text
            cusTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 60);
            ViewGroup.MarginLayoutParams ct = (ViewGroup.MarginLayoutParams) cusTxt.getLayoutParams();
            ct.setMargins(Math.round(48 * Resources.getSystem().getDisplayMetrics().density), Math.round(16 * Resources.getSystem().getDisplayMetrics().density),0,0);
            cusTxt.getLayoutParams().height = Math.round(85 * Resources.getSystem().getDisplayMetrics().density);

            }
        //ensure changes are applied
        tag.requestLayout();
    }

    public void back(){

        //check for any value in any of the strings
        //alert user if there are
        //finish if there aren't
        if(customTitle.getText().toString().length() != 0 || customAuthor.getText().toString().length() != 0 || customPublishYear.getText().toString().length() != 0 || customISBN.getText().toString().length() != 0 || customBlurb.getText().toString().length() != 0){
            unsavedChangesWarning();
        }
        else{
            finish();
        }
    }
}