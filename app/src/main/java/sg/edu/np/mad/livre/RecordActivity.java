package sg.edu.np.mad.livre;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.icu.text.AlphabeticIndex;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class RecordActivity extends AppCompatActivity {
    ImageView libraryTag;
    TextView totalTime;
    DBHandler dbHandler;
    ArrayList<Records> recordList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        libraryTag = findViewById(R.id.recordLibraryTag);
        totalTime = findViewById(R.id.recordTotalTime);
        dbHandler = new DBHandler(this);
        recordList = dbHandler.GetAllRecords();

        setTotalTime();

        libraryTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecordActivity.this, LibraryActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        setTotalTime();

        RecyclerView recyclerView = findViewById(R.id.recordRecyclerView);
        RecordAdapter adapter = new RecordAdapter(recordList);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(manager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());


    }

    public void setTotalTime()
    {
        int totalTimeSec = dbHandler.GetTotalReadingTimeInSec();
        if(totalTimeSec > 0)
        {
            int min = totalTimeSec/60;
            totalTimeSec = totalTimeSec % 60;
            int hour = min / 60;
            min = min % 60;
            totalTime.setText(String.format("%02d",hour) + "H " + String.format("%02d",min) + "M " + String.format("%02d",totalTimeSec) + "S");
        }
        else
        {
            totalTime.setText("00:00:00");
        }
    }
}