package sg.edu.np.mad.livre;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class CatViewHolder extends RecyclerView.ViewHolder {
    //declare views
    TextView cattitle;
    TextView catauthordate;
    TextView customtxt;
    TextView catdesc;
    ImageView catthumb;

    public CatViewHolder(View itemView) {
        super(itemView);
        //set views
        cattitle = itemView.findViewById(R.id.catTitle);
        catauthordate = itemView.findViewById(R.id.catauthorDate);
        catdesc = itemView.findViewById(R.id.catDesc);
        catthumb = itemView.findViewById(R.id.catThumb);
        customtxt = itemView.findViewById(R.id.customTxt);
    }
}
