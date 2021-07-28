package sg.edu.np.mad.livre;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

public class CustomiseBook extends AppCompatActivity {

    DBHandler dbHandler;
    ImageView tag;
    TextView cusTxt;
    Button submitBtn;
    EditText customTitle, customAuthor, customPublishYear, customISBN, customBlurb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customise_book);

        dbHandler = new DBHandler(this);
        submitBtn = findViewById(R.id.customiseDoneBtn);
        customTitle = findViewById(R.id.cusTitleEdit);
        customAuthor = findViewById(R.id.cusAuthEdit);
        customPublishYear = findViewById(R.id.cusYearEdit);
        customISBN = findViewById(R.id.cusISBNEdit);
        customBlurb = findViewById(R.id.cusSynopsisEdit);
        tag = findViewById(R.id.CusCatalogue);

        Intent receivedIntent = getIntent();
        if(receivedIntent.getSerializableExtra("Title") != null) {
            customTitle.setText(receivedIntent.getSerializableExtra("Title").toString());
        }

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


        submitBtn.setOnClickListener(v -> {
            //Hide keyboard
            ((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(customBlurb.getWindowToken(), 0);
            if(customTitle.getText().toString().length() == 0){
                customTitle.setError("Please enter a Title");
                Toast.makeText(getBaseContext(), "Invalid!", Toast.LENGTH_SHORT).show();
                return;
            }
            if(customBlurb.getText().toString().length() == 0){
                customBlurb.setError("Please enter a Synopsis");
                Toast.makeText(getBaseContext(), "Invalid", Toast.LENGTH_SHORT).show();
                return;
            }
            if(AuthorValidation() && PubYearValidation() && ISBNValidation()){
                Toast.makeText(getBaseContext(), "Valid", Toast.LENGTH_SHORT).show();
                ValidatedSubmission();
            }
            else{
                Toast.makeText(getBaseContext(), "Invalid", Toast.LENGTH_SHORT).show();
            }

        });
    }

    @Override
    public void onBackPressed() {
        //check for any value in any of the strings
        //alert user if there are
        //finish if there aren't
        if(customTitle.getText().toString().length() != 0){
            unsavedChangesWarning();
        }
        else if(customAuthor.getText().toString().length() != 0){
            unsavedChangesWarning();
        }
        else if(customPublishYear.getText().toString().length() != 0){
            unsavedChangesWarning();
        }
        else if(customISBN.getText().toString().length() != 0){
            unsavedChangesWarning();
        }
        else if(customBlurb.getText().toString().length() != 0){
            unsavedChangesWarning();
        }
        else{
            finish();
        }

        tag.setOnClickListener(v -> {
            Intent intent = new Intent(CustomiseBook.this, CatalogueActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //make changed based on orientation
        setOrientationDifferences();
    }

    private void unsavedChangesWarning() {
        //alert dialogue (removing custom book)
        AlertDialog.Builder bui = new AlertDialog.Builder(CustomiseBook.this);
        bui.setMessage("Delete unsaved changes?")
                .setCancelable(true)
                .setPositiveButton("Sure", (dialog, id) -> {finish();})
                //User chooses not to
                .setNegativeButton("No", (dialog, id) -> {return;});

        //Creating dialog box
        AlertDialog alert = bui.create();
        //Setting the title manually
        alert.setTitle("Are you sure?");
        alert.show();
    }

    public void ValidatedSubmission(){
        Book book = new Book();
        book.setName(customTitle.getText().toString());
        book.setAuthor(customAuthor.getText().toString());
        book.setYear(customPublishYear.getText().toString());
        book.setIsbn(customISBN.getText().toString());
        book.setBlurb(customBlurb.getText().toString());
        book.setAdded(false);
        book.setCustom(true);
        book.setArchived(false);
        book.setThumbnail("unavailable");
        Intent intent = new Intent(getBaseContext(), BookDetails.class);
        intent.putExtra("BookObject", book);
        intent.putExtra("isFromCus", true);
        startActivity(intent);
    }

    public Boolean AuthorValidation(){
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
        String newDateStr = "01/"+"01/"+customPublishYear.getText();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        Date strDate = new Date();
        try {
            strDate = sdf.parse(newDateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }

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
        tag = findViewById(R.id.CusCatalogue);
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
        tag.requestLayout();
    }
}