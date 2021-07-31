package sg.edu.np.mad.livre;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;

public class StatAdapter extends RecyclerView.Adapter<StatViewHolder> {
    ArrayList<String> hashKeys;
    HashMap<String, String> statList;

    public StatAdapter(ArrayList<String> keys, HashMap<String, String> map) {
        hashKeys = keys;
        statList = map;
    }

    @Override
    public StatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_stat, parent, false);
        return new StatViewHolder(item);
    }

    @Override
    public void onBindViewHolder(StatViewHolder holder, int position) {
        String statKey = hashKeys.get(position);
        String statMapValue = statList.get(statKey);
        holder.statName.setText(statKey);
        holder.statValue.setText(statMapValue);
    }

    public int getItemCount() {
        return hashKeys.size();
    }
}
