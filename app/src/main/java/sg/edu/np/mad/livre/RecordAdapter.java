package sg.edu.np.mad.livre;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class RecordAdapter extends RecyclerView.Adapter<RecordViewHolder> {
    private static final String TAG = "RecordAdapter";
   ArrayList<Records> recordsArrayList;
    private int hour;

    public RecordAdapter(ArrayList<Records> input)
   {
       recordsArrayList = input;
   }

    @Override
    public RecordViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_records, parent, false);
        return new RecordViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecordViewHolder holder, int position) {
        Records record = recordsArrayList.get(position);
        DBHandler dbHandler = new DBHandler(holder.bookCover.getContext());
        Book book = dbHandler.FindBookByID(record.getBookID());

        // If book can be found in library
        if (book != null)
        {
                if (book.isCustom())
                {
                    if(book.getThumbnail().equals("Unavailable"))
                    {
                        holder.bookCover.setImageDrawable(holder.bookCover.getContext()
                                .getResources().getDrawable(R.drawable.shelf_bust));
                    }
                    else
                    {
                        byte[] decodedString = Base64.decode(book.getThumbnail(), Base64.DEFAULT);
                        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                        holder.bookCover.setImageBitmap(decodedByte);
                    }
                }
                else
                {
                    if (book.getThumbnail() != "Unavailable")
                    {
                        Picasso.get()
                                .load(book.getThumbnail())
                                .resize(90, 140)
                                .into(holder.bookCover);
                    }
                    else
                    {
                        holder.bookCover.setImageDrawable(holder.bookCover.getContext()
                                .getResources().getDrawable(R.drawable.shelf_bust));
                    }
                }
        }
        //if image not available possibly due to no set thumbnail or deletion of book from library
        else
        {
            holder.bookCover.setImageDrawable(holder.bookCover.getContext()
                    .getResources().getDrawable(R.drawable.shelf_bust));
        }

        int readTime = record.getTimeReadSec();
        String Time = "   Read Time: ";
        //less than 1 minute
        if (readTime < 60) {
            Time += String.format("%02d", readTime) + " S";
        }
        //more than 1 hour
        else if (readTime >= 3600) {
            int min = readTime / 60;
            readTime = readTime % 60;
            int hour = min / 60;
            min = min % 60;
            Time += String.format("%02d", hour) + " H " + String.format("%02d", min) + " M " + String.format("%02d", readTime) + " S";
        }
        //less than 1 hour , more than 1 minute
        else {
            int min = readTime / 60;
            readTime = readTime % 60;
            Time += String.format("%02d", min) + " M " + String.format("%02d", readTime) + " S";
        }
        holder.date.setText(new SimpleDateFormat("dd MMM yyyy, EEE HH:mm:ss").format(record.getDateRead()));
        holder.name.setText(record.getName());
        holder.readTime.setText(Time);
    }

    @Override
    public int getItemCount() {
        return recordsArrayList.size();
    }


}
