package sg.edu.np.mad.livre;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;

public class CatItemsAdapter extends RecyclerView.Adapter<CatViewHolder>{
    ArrayList<Book> data;
    DBHandler dbhandler;

    public CatItemsAdapter(ArrayList<Book> input){
        data = input;
    }

    @NonNull
    @Override
    public CatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item;

        //inflate layout based on type
        if(viewType == 0) {
            //normal item
            item = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.catalogue_item,
                    parent,
                    false
            );
        } else {
            //last item
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
        //get position and set text to views
        Book b = data.get(position);
        holder.cattitle.setText(b.getName());
        holder.catdesc.setText(b.getBlurb());
        String catauthordate = b.getAuthor() + " · " + b.getYear();
        holder.catauthordate.setText(catauthordate);



        //set onclick listeners to thumbnail and title
        holder.catthumb.setOnClickListener(v -> catItemClick(holder, b));

        holder.cattitle.setOnClickListener(v -> catItemClick(holder, b));

        if (b.isCustom()) {
            //if book is custom
            //make indicator that book is cusom visisble
            holder.customtxt.setVisibility(View.VISIBLE);

            //if thumbnail is not unavailable, decode bitmap and set to thumbnail
            if(!(b.getThumbnail()).equals("Unavailable")){
                byte[] decodedString = Base64.decode(b.getThumbnail(), Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                holder.catthumb.setImageBitmap(decodedByte);
            }
        }
        else{
            //for not custom books
            //don't show custom book indicator
            holder.customtxt.setVisibility(View.GONE);

            //get image from link with Picasso
            Picasso.get()
                    .load(b.getThumbnail())
                    .resize(90, 140)
                    .into(holder.catthumb);
        }
    }

    //get item count
    public int getItemCount() {
        return data.size();
    }

    //method for when title/thumbnail of catalogue is clicked
    public void catItemClick(CatViewHolder holder, Book b){
        dbhandler = new DBHandler(holder.catthumb.getContext());
        Book tempBook = dbhandler.FindBookByISBN(b.getIsbn(), b.isCustom());
        if(tempBook != null){
            b = tempBook;
        }
        Intent bookDetailsIntent = new Intent(holder.catthumb.getContext(), BookDetails.class);
        bookDetailsIntent.putExtra("BookObject", b);
        holder.catthumb.getContext().startActivity(bookDetailsIntent);
    }
}