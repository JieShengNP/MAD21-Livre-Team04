package sg.edu.np.mad.livre;

import android.view.View;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

public class LibraryViewHolder extends RecyclerView.ViewHolder {

    ImageView book1, book2, book3, book4;

    public LibraryViewHolder(View itemView) {
        super(itemView);
        book1 = itemView.findViewById(R.id.recyclerViewBook1);
        book2 = itemView.findViewById(R.id.recyclerViewBook2);
        book3 = itemView.findViewById(R.id.recyclerViewBook3);
        book4 = itemView.findViewById(R.id.recyclerViewBook4);
    }
}
