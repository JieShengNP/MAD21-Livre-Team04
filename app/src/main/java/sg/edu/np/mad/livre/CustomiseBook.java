package sg.edu.np.mad.livre;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Book book = new Book();
                book.setName(customTitle.getText().toString());
                book.setAuthor(customAuthor.getText().toString());
                book.setYear(customPublishYear.getText().toString());
                book.setIsbn(customISBN.getText().toString());
                book.setBlurb(customBlurb.getText().toString());
                book.setAdded(false);
                book.setCustom(true);
                book.setArchived(false);
                // Placeholder Thumbnail
                book.setThumbnail("unavailable");
                Intent intent = new Intent(getBaseContext(), BookDetails.class);
                intent.putExtra("BookObject", book);
                intent.putExtra("isFromCus", true);
                startActivity(intent);
            }
        });
    }
}