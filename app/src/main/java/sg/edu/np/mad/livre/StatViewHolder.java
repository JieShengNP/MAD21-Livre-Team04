package sg.edu.np.mad.livre;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class StatViewHolder extends RecyclerView.ViewHolder {
    TextView statName, statValue;

    public StatViewHolder(View itemView)
    {
        super(itemView);
        statName = itemView.findViewById(R.id.statName);
        statValue = itemView.findViewById(R.id.statValue);
    }
}
