package sg.edu.np.mad.livre;

import android.view.LayoutInflater;
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
        String imageUri = "http://covers.openlibrary.org/b/isbn/"+ record.getIsbn() +"-S.jpg";
        Picasso.get().load(imageUri).into(holder.bookCover);
        SimpleDateFormat sdf = new SimpleDateFormat("DD Mon yyyy HH:mm:ss");
        holder.date.setText(sdf.format(record.getDateRead()));
        holder.name.setText(record.getName());
    }

    @Override
    public int getItemCount() {
        return 0;
    }


}
