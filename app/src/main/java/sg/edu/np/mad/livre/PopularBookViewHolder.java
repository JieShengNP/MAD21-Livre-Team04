package sg.edu.np.mad.livre;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class PopularBookViewHolder extends RecyclerView.ViewHolder {

    ImageView bookThumbnail;
    TextView bookTitle, bookAuthorYear, bookDesc, bookStats;
    //Start Item
    TextView pageTitle, pageDesc;

    public PopularBookViewHolder(View itemView) {
        super(itemView);
        bookThumbnail = itemView.findViewById(R.id.popularBookThumbnail);
        bookTitle = itemView.findViewById(R.id.popularBookTitle);
        bookAuthorYear = itemView.findViewById(R.id.popularBookAuthorYear);
        bookDesc = itemView.findViewById(R.id.popularBookDesc);
        bookStats = itemView.findViewById(R.id.popularBookStats);

        pageTitle = itemView.findViewById(R.id.popularTitle);
        pageDesc = itemView.findViewById(R.id.popularDesc);
    }
}
