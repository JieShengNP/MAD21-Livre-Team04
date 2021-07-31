package sg.edu.np.mad.livre;

import android.content.Context;
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
import java.util.Collections;
import java.util.Comparator;

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
            case 2: {
                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_popularbooks_start, parent, false);
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
        if (position == bookList.size() + 1){
            return;
        }
        if (position == 0){
            Context context = holder.pageTitle.getContext();
            if (bookList.size() > 0) {
                if (bookList.size() == 1) {
                    holder.pageTitle.setText(context.getString(R.string.popularTitleSingle));
                    holder.pageDesc.setText(context.getString(R.string.popularDescSingle));
                } else {
                    holder.pageTitle.setText(context.getString(R.string.popularTitle, bookList.size()));
                    holder.pageDesc.setText(context.getString(R.string.popularDesc, bookList.size()));
                    //Sort by Readers and Time
                    Collections.sort(bookList, new Comparator() {

                        public int compare(Object o1, Object o2) {

                            Integer reader1 = ((PopularBook) o1).getTotalReaders();
                            Integer reader2 = ((PopularBook) o2).getTotalReaders();
                            int sComp = reader1.compareTo(reader2);

                            if (sComp != 0) {
                                return sComp;
                            }

                            Integer time1 = ((PopularBook) o1).totalTime;
                            Integer time2 = ((PopularBook) o2).totalTime;
                            return time1.compareTo(time2);
                        }});
                    Collections.reverse(bookList);
                }
            } else {
                holder.pageTitle.setText(context.getString(R.string.popularTitleNone));
                holder.pageDesc.setText(context.getString(R.string.popularDescNone));
            }
            return;
        }
        // Offset Position
        position -= 1;
        holder.bookTitle.setText(bookList.get(position).title);
        Picasso.get()
                .load(bookList.get(position).thumbnail)
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
        // End Item
        if (position == bookList.size() + 1){
            return 1;
        }

        // Start Item
        if (position == 0){
            return 2;
        }

        // Everything Else
        return 0;
    }

    @Override
    public int getItemCount() {
        return bookList.size() + 2;
    }
}
