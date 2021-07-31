package sg.edu.np.mad.livre;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class RecordViewHolder extends RecyclerView.ViewHolder {
    ImageView bookCover;
    TextView date, name, readTime;

    public RecordViewHolder(View itemView) {
        super(itemView);
        bookCover = itemView.findViewById(R.id.recordBookCover);
        date = itemView.findViewById(R.id.recordDate);
        name = itemView.findViewById(R.id.recordName);
        readTime = itemView.findViewById(R.id.recordReadTime);
    }
}
