package sg.edu.np.mad.livre;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

@SuppressWarnings("ALL")
public class CatItemsAdapter extends RecyclerView.Adapter<CatViewHolder>{
    ArrayList<Book> data;

    public CatItemsAdapter(ArrayList<Book> input){
        data = input;
    }

    @NonNull
    @Override
    public CatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item;

        if(viewType == 0) {
            item = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.catalogue_item,
                    parent,
                    false
            );
        } else {
            item = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.catalogue_enditem,
                    parent,
                    false
            );
        }
        return new CatViewHolder(item);
    }

    @Override
    public int getItemViewType(int position) {
        if (position != (getItemCount() -1)){
            return 0;
        }
        else{
            return 1;
        }

    }

    @Override
    public void onBindViewHolder(@NonNull CatViewHolder holder, int position) {
        Book b = data.get(position);
        holder.cattitle.setText(b.name);
        holder.catdesc.setText(b.blurb);
        String catauthordate = b.author + " · " + b.year;
        holder.catauthordate.setText(catauthordate);




        holder.catthumb.setOnClickListener(v -> {
            Intent bookDetailsIntent = new Intent(holder.catthumb.getContext(), BookDetails.class);
            bookDetailsIntent.putExtra("BookObject", b);
            holder.catthumb.getContext().startActivity(bookDetailsIntent);;
        });
        if (b.isCustom()) {
            holder.customtxt.setVisibility(View.VISIBLE);

            if(!(b.getThumbnail()).equals("Unavailable")){
                byte[] decodedString = Base64.decode(b.getThumbnail(), Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                holder.catthumb.setImageBitmap(decodedByte);
            }
        }
        else{
            holder.customtxt.setVisibility(View.GONE);
            Picasso.get()
                    .load(b.getThumbnail())
                    .resize(90, 140)
                    .into(holder.catthumb);
        }
  }





    public int getItemCount() {
        return data.size();
    }
}