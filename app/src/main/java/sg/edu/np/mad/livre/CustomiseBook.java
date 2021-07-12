package sg.edu.np.mad.livre;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class CustomiseBook extends AppCompatActivity {

    DBHandler dbHandler;
    Button submitBtn;
    EditText customTitle, customAuthor, customPublishYear, customISBN, customBlurb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customise_book);

        dbHandler = new DBHandler(this);
        submitBtn = findViewById(R.id.customiseSubmitBtn);
        customTitle = findViewById(R.id.cusTitleEdit);
        customAuthor = findViewById(R.id.cusAuthEdit);
        customPublishYear = findViewById(R.id.cusYearEdit);
        customISBN = findViewById(R.id.cusISBNEdit);
        customBlurb = findViewById(R.id.cusSynopsisEdit);

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Book book = new Book();
                book.setName(customTitle.getText().toString());
                book.setAuthor(customAuthor.getText().toString());
                book.setYear(customPublishYear.getText().toString());
                book.setIsbn(customISBN.getText().toString());
                book.setBlurb(customBlurb.getText().toString());
                // Placeholder Thumbnail
                book.setThumbnail("android.resource://" + getPackageName() + "/" + R.drawable.shelf_bust);
                dbHandler.AddBook(book);
                Toast.makeText(getBaseContext(), "Successfully created new book!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getBaseContext(), LibraryActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }
}