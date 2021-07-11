package sg.edu.np.mad.livre;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class LibraryAdapter extends RecyclerView.Adapter<LibraryViewHolder> {
    ArrayList<ArrayList<Book>> bookListData;

    public LibraryAdapter(ArrayList<ArrayList<Book>> bookList){
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
        for(int i = 0; i < bookDataList.size(); i++){
            if ((URL = bookDataList.get(i).getThumbnail()) != null){
                SetImage(i, Uri.parse(URL), holder, defaultImage);
            } else {
                SetImage(i, Uri.parse(("android.resource://" + context.getPackageName() + "/" + R.drawable.shelf_bust)), holder, defaultImage);
            }
        }
    }

    public void SetImage(int position, Uri uri, LibraryViewHolder holder, Drawable defaultImage){
        switch(position){
            case(0):
                Picasso.get()
                        .load(uri)
                        .placeholder(defaultImage)
                        .resize(90, 140)
                        .into(holder.book1);
                break;
            case(1):
                Picasso.get()
                        .load(uri)
                        .placeholder(defaultImage)
                        .resize(90, 140)
                        .into(holder.book2);
                break;
            case(2):
                Picasso.get()
                        .load(uri)
                        .placeholder(defaultImage)
                        .resize(90, 140)
                        .into(holder.book3);
                break;
            case(3):
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
