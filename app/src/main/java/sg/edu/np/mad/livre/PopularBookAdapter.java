package sg.edu.np.mad.livre;

import android.graphics.drawable.Drawable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class PopularBookAdapter extends RecyclerView.Adapter<PopularBookViewHolder> {

    ArrayList<PopularBook> bookList;

    public PopularBookAdapter(ArrayList<PopularBook> bookList) {
        this.bookList = bookList;
    }

    @Override
    public PopularBookViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case 1: {
                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_popularbooks_end, parent, false);
                return new PopularBookViewHolder(itemView);
            }
            case 0:
            default:{
                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_popularbooks, parent, false);
                return new PopularBookViewHolder(itemView);
            }
        }
    }

    @Override
    public void onBindViewHolder(PopularBookViewHolder holder, int position) {
        if (position == bookList.size()){
            return;
        }
        Drawable defaultImage = holder.bookThumbnail.getContext().getResources().getDrawable(R.drawable.shelf_bust);
        holder.bookTitle.setText(bookList.get(position).title);
        Picasso.get()
                .load(bookList.get(position).thumbnail)
                .placeholder(defaultImage)
                .resize(90, 140)
                .into(holder.bookThumbnail);
        holder.bookAuthorYear.setText(bookList.get(position).author + " . " + bookList.get(position).year);
        holder.bookDesc.setText(bookList.get(position).blurb);
        String timeFormat = "";
        double calculatedTime = bookList.get(position).totalTime;

        // Format in 2dp
        DecimalFormat df = new DecimalFormat("#0.00");
        String timeSpent = "";
        if (bookList.get(position).totalTime < 60) {
            timeFormat = " Total Seconds Read";
            timeSpent = String.valueOf(bookList.get(position).totalTime);
        } else if ((calculatedTime = (bookList.get(position).totalTime / 60.0)) < 60) {
            timeFormat = " Total Minutes Read";
            timeSpent = df.format(calculatedTime);
        } else if ((calculatedTime = (bookList.get(position).totalTime / 3600.0)) < 60) {
            timeFormat = " Total Hours Read";
            timeSpent = df.format(calculatedTime);
        }
        // Make the number text bigger in TextView
        SpannableStringBuilder readerSpannable = new SpannableStringBuilder(bookList.get(position).getTotalReaders() + " Total Readers");
        readerSpannable.setSpan(new RelativeSizeSpan(1.5f), 0, String.valueOf(bookList.get(position).getTotalReaders()).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        SpannableStringBuilder timeSpannable = new SpannableStringBuilder(timeSpent + timeFormat);
        timeSpannable.setSpan(new RelativeSizeSpan(1.5f), 0, timeSpent.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        holder.bookStats.setText(TextUtils.concat(readerSpannable, "\n", timeSpannable));
    }

    @Override
    public int getItemViewType(int position) {
        return (position == bookList.size()) ? 1 : 0;
    }

    @Override
    public int getItemCount() {
        return bookList.size() + 1;
    }
}
