package sg.edu.np.mad.livre;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.HashMap;

public class StatActivity extends AppCompatActivity {
    private static final String TAG = "StatActivity";
    ImageView libraryTag;
    DBHandler dbHandler;
    ArrayList<String> hashKeys;
    HashMap<String, String> statList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stat);
        libraryTag = findViewById(R.id.statLibraryTag);
        dbHandler = new DBHandler(this);

        libraryTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //initialising HashMapKeyList
        InitializeHashKeyList();
        //Mapping Data
        MapData();


        //RecyclerView
        RecyclerView recyclerView = findViewById(R.id.statRecyclerView);
        StatAdapter adapter = new StatAdapter(hashKeys, statList);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(manager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    public void InitializeHashKeyList()
    {
        hashKeys = new ArrayList<>();
        hashKeys.add("Total Books in Library");
        hashKeys.add("Total Books Read");
        hashKeys.add("Total Time Spent Reading");
        hashKeys.add("Average Time Read Per Session");
        hashKeys.add("Book Most Time Spent On");
        hashKeys.add("Time Spent on Book");
        hashKeys.add("Favourite Author");
    }

    public void MapData()
    {
        statList = new HashMap<String, String>();
        //Overrideable Int variable
        int count = dbHandler.GetTotalBooksInLibrary();

        // Mapping Total Books in Library
        if (count != 0)
        {
            statList.put(hashKeys.get(0),String.valueOf(count));
        }
        else
        {
            statList.put(hashKeys.get(0), "None");
        }

        //Mapping Total Books Read
        count = dbHandler.GetTotalBooksRead();
        if (count != 0)
        {
            statList.put(hashKeys.get(1),String.valueOf(count));
        }
        else
        {
            statList.put(hashKeys.get(1), "None");
        }

        //Mapping Total Time Spend Reading
        count = dbHandler.GetTotalReadingTimeInSec();
        if(count > 0)
        {
            //less than 1 minute
            if (count < 60)
            {

                statList.put(hashKeys.get(2), String.format("%02d",count) + "S");
            }
            //more than 1 hour
            else if (count >= 3600)
            {
                int min = count/60;
                count = count % 60;
                int hour = min / 60;
                min = min % 60;
                statList.put(hashKeys.get(2), String.format("%02d",hour) + "H " + String.format("%02d",min) + "M " + String.format("%02d",count) + "S");
            }
            //less than 1 hour , more than 1 minute
            else
            {
                int min = count/60;
                count = count % 60;
                statList.put(hashKeys.get(2), String.format("%02d",min) + "M " + String.format("%02d",count) + "S");
            }
        }
        else
        {
            statList.put(hashKeys.get(2),"0S");
        }

        //Mapping Avg Time Spend Reading
        count = dbHandler.GetAvgTimePerSession();
        if(count > 0)
        {
            //less than 1 minute
            if (count < 60)
            {

                statList.put(hashKeys.get(3), String.format("%02d",count) + "S");
            }
            //more than 1 hour
            else if (count >= 3600)
            {
                int min = count/60;
                count = count % 60;
                int hour = min / 60;
                min = min % 60;
                statList.put(hashKeys.get(3), String.format("%02d",hour) + "H " + String.format("%02d",min) + "M " + String.format("%02d",count) + "S");
            }
            //less than 1 hour , more than 1 minute
            else
            {
                int min = count/60;
                count = count % 60;
                statList.put(hashKeys.get(3), String.format("%02d",min) + "M " + String.format("%02d",count) + "S");
            }
        }
        else
        {
            statList.put(hashKeys.get(3),"0S");
        }

        //Mapping Book Most Time Spent On
        Book book = dbHandler.GetBookMostTimeSpent();
        if (book != null)
        {
            statList.put(hashKeys.get(4), book.getName());
            //Mapping Time Spent on Book
            count = book.getReadSeconds();
            if (count < 60)
            {

                statList.put(hashKeys.get(5), String.format("%02d",count) + "S");
            }
            //more than 1 hour
            else if (count >= 3600)
            {
                int min = count/60;
                count = count % 60;
                int hour = min / 60;
                min = min % 60;
                statList.put(hashKeys.get(5), String.format("%02d",hour) + "H " + String.format("%02d",min) + "M " + String.format("%02d",count) + "S");
            }
            //less than 1 hour , more than 1 minute
            else
            {
                int min = count/60;
                count = count % 60;
                statList.put(hashKeys.get(5), String.format("%02d",min) + "M " + String.format("%02d",count) + "S");
            }
        }
        else
        {
            statList.put(hashKeys.get(4), "None");
            statList.put(hashKeys.get(5), "0S");
        }

        //Mapping Favourite Author
        statList.put(hashKeys.get(6), dbHandler.GetFavouriteAuthor());
    }
}