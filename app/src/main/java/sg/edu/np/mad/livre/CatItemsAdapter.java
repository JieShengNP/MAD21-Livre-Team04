package sg.edu.np.mad.livre;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
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

    public CatItemsAdapter(ArrayList<Book> input){
        data = input;
    }

    @NonNull
    @Override
    public CatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = null;

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
        if (position == getItemCount()-1){
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
        holder.catauthordate.setText(b.author + ", " + b.year);

        Picasso.get()
                .load(b.getThumbnail())
                .resize(90, 140)
                .into(holder.catthumb);


//        holder.img.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v) {
//                Log.d("Debug", "Image clicked");
//
//                new AlertDialog.Builder(holder.img.getContext())
//                        .setTitle("Profile")
//                        .setMessage(u.name)
//                        .setPositiveButton("View", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                Intent viewProfile = new Intent(holder.img.getContext(), MainActivity.class);
//                                viewProfile.putExtra("id", position);
//                                holder.img.getContext().startActivity(viewProfile);
//                            }
//                        })
//                        .setNegativeButton("Close", null)
//                        .show();
//
//            }
//        });
  }





    public int getItemCount() {
        return data.size();
    }
}