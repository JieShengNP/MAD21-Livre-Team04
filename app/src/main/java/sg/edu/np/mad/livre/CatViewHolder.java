package sg.edu.np.mad.livre;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

public class CatViewHolder extends RecyclerView.ViewHolder {
    TextView cattitle;
    TextView catauthordate;
    TextView catdesc;
    ImageView catthumb;
    public CatViewHolder(View itemView) {
        super(itemView);
        cattitle = itemView.findViewById(R.id.catTitle);
        catauthordate = itemView.findViewById(R.id.catauthorDate);
        catdesc = itemView.findViewById(R.id.catDesc);
        catthumb = itemView.findViewById(R.id.catThumb);
    }
}
