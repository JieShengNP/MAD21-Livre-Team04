package sg.edu.np.mad.livre;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class LibraryAdapter extends RecyclerView.Adapter<LibraryViewHolder> {
    ArrayList<ArrayList<Book>> bookListData;

    public LibraryAdapter(ArrayList<ArrayList<Book>> bookList) {
        bookListData = bookList;
    }

    @Override
    public LibraryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_librarybooks, parent, false);
        return new LibraryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(LibraryViewHolder holder, int position) {
        // Gets the Book List for the current shelf and populate the shelf with the images of the books.
        ArrayList<Book> bookDataList = bookListData.get(position);
        Context context = holder.book1.getContext();
        Drawable defaultImage = context.getResources().getDrawable(R.drawable.shelf_bust);
        String URL;
        for (int i = 0; i < bookDataList.size(); i++) {
            if ((URL = bookDataList.get(i).getThumbnail()) != null) {
                if (!bookDataList.get(i).isCustom()) {
                    SetImage(i, Uri.parse(URL), holder, defaultImage);
                } else {
                    if (!(bookDataList.get(i).getThumbnail()).equals("Unavailable")) {
                        byte[] decodedString = Base64.decode(bookDataList.get(i).getThumbnail(), Base64.DEFAULT);
                        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                        SetImage(i, decodedByte, holder, defaultImage);
                    } else {
                        SetImage(i, Uri.parse(("android.resource://" + context.getPackageName() + "/" + R.drawable.shelf_bust)), holder, defaultImage);
                    }
                }
            } else {
                SetImage(i, Uri.parse(("android.resource://" + context.getPackageName() + "/" + R.drawable.shelf_bust)), holder, defaultImage);
            }
        }
        //Set On Click Listeners for Each Image which will bring them to BookDetails page
        holder.book1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent bookDetailsIntent = new Intent(holder.book1.getContext(), BookDetails.class);
                bookDetailsIntent.putExtra("BookObject", bookDataList.get(0));
                holder.book1.getContext().startActivity(bookDetailsIntent);
            }
        });
        holder.book2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent bookDetailsIntent = new Intent(holder.book2.getContext(), BookDetails.class);
                bookDetailsIntent.putExtra("BookObject", bookDataList.get(1));
                holder.book2.getContext().startActivity(bookDetailsIntent);
            }
        });
        holder.book3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent bookDetailsIntent = new Intent(holder.book3.getContext(), BookDetails.class);
                bookDetailsIntent.putExtra("BookObject", bookDataList.get(2));
                holder.book3.getContext().startActivity(bookDetailsIntent);
            }
        });
        holder.book4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent bookDetailsIntent = new Intent(holder.book4.getContext(), BookDetails.class);
                bookDetailsIntent.putExtra("BookObject", bookDataList.get(3));
                holder.book4.getContext().startActivity(bookDetailsIntent);
            }
        });
    }

    //For custom books with Base64 Encoding
    public void SetImage(int position, Bitmap bitmap, LibraryViewHolder holder, Drawable defaultImage) {
        switch (position) {
            case (0):
                holder.book1.setVisibility(View.VISIBLE);
                holder.book1.setImageBitmap(bitmap);
                break;
            case (1):
                holder.book2.setVisibility(View.VISIBLE);
                holder.book2.setImageBitmap(bitmap);
                break;
            case (2):
                holder.book3.setVisibility(View.VISIBLE);
                holder.book3.setImageBitmap(bitmap);
                break;
            case (3):
                holder.book4.setVisibility(View.VISIBLE);
                holder.book4.setImageBitmap(bitmap);
                break;
        }
    }

    public void SetImage(int position, Uri uri, LibraryViewHolder holder, Drawable defaultImage) {
        switch (position) {
            case (0):
                holder.book1.setVisibility(View.VISIBLE);
                Picasso.get()
                        .load(uri)
                        .placeholder(defaultImage)
                        .resize(90, 140)
                        .into(holder.book1);
                break;
            case (1):
                holder.book2.setVisibility(View.VISIBLE);
                Picasso.get()
                        .load(uri)
                        .placeholder(defaultImage)
                        .resize(90, 140)
                        .into(holder.book2);
                break;
            case (2):
                holder.book3.setVisibility(View.VISIBLE);
                Picasso.get()
                        .load(uri)
                        .placeholder(defaultImage)
                        .resize(90, 140)
                        .into(holder.book3);
                break;
            case (3):
                holder.book4.setVisibility(View.VISIBLE);
                Picasso.get()
                        .load(uri)
                        .placeholder(defaultImage)
                        .resize(90, 140)
                        .into(holder.book4);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return bookListData.size();
    }
}
