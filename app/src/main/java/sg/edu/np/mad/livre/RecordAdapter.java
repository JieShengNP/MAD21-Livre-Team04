package sg.edu.np.mad.livre;

import android.net.Uri;
import android.view.LayoutInflater;
import android.graphics.drawable.Drawable;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class RecordAdapter extends RecyclerView.Adapter<RecordViewHolder> {
   ArrayList<Records> recordsArrayList;
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
        String URL = dbHandler.FindBookByISBN(record.getIsbn()).getThumbnail();
        if (URL != null){
            Picasso.get()
                    .load(URL)
                    .placeholder(holder.bookCover.getContext().getResources().getDrawable(R.drawable.shelf_bust))
                    .resize(90, 140)
                    .into(holder.bookCover);
        }
        holder.date.setText(new SimpleDateFormat("dd MMM yyyy, EEE HH:mm:ss").format(record.getDateRead()));
        holder.name.setText(record.getName());
    }

    @Override
    public int getItemCount() {
        return recordsArrayList.size();
    }


}
