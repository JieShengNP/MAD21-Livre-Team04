package sg.edu.np.mad.livre;

import androidx.appcompat.app.AppCompatActivity;
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
                startActivity(intent);
            }
        });

        setTotalTime();

        RecordAdapter recordAdapter = new RecordAdapter(recordList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        RecyclerView recyclerView = findViewById(R.id.recordRecyclerView);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(recordAdapter);


    }

    public void setTotalTime()
    {
        int totalTimeSec = dbHandler.GetTotalReadingTimeInSec();
        if(totalTimeSec == 0)
        {
            int min = totalTimeSec/60;
            totalTimeSec = totalTimeSec % 60;
            int hour = min / 60;
            min = min % 60;
            totalTime.setText(String.format("%02d",hour) + ":" + String.format("%02d",min) + ":" + String.format("%02d",totalTimeSec));
        }
        else
        {
            totalTime.setText("00:00:00");
        }
    }
}