package sg.edu.np.mad.livre;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

public class CustomiseBook extends AppCompatActivity {

    DBHandler dbHandler;
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
        if(submitBtn.getText().toString().length() == 0){
            unsavedChangesWarning();
        }
        else if(customTitle.getText().toString().length() == 0){
            unsavedChangesWarning();
        }
        else if(customAuthor.getText().toString().length() == 0){
            unsavedChangesWarning();
        }
        else if(customPublishYear.getText().toString().length() == 0){
            unsavedChangesWarning();
        }
        else if(customISBN.getText().toString().length() == 0){
            unsavedChangesWarning();
        }
        else if(customBlurb.getText().toString().length() == 0){
            unsavedChangesWarning();
        }
        else{
            finish();
        }
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
            Log.v("test", "test");
            customISBN.setError("Invalid ISBN-13!");
        }else if (dbHandler.isBookAdded(b)){
            customISBN.setError("Custom book exists with this ISBN");
        }
        else{
            return true;
        }
        return false;
    }
}