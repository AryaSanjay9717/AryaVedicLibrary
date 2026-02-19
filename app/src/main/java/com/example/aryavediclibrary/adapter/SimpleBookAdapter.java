package com.example.aryavediclibrary.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aryavediclibrary.BookReaderActivity;
import com.example.aryavediclibrary.R;

import java.util.List;

public class SimpleBookAdapter extends RecyclerView.Adapter<SimpleBookAdapter.ViewHolder> {

    private final Context context;
    private final List<String> books;
    private final String category;

    public SimpleBookAdapter(Context context, List<String> books, String category) {
        this.context = context;
        this.books = books;
        this.category = category;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.row_book, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        String bookName = books.get(position);
        holder.txtBookName.setText(bookName);

        // 🔥 SET BOOK COVER IMAGE
        holder.imgBook.setImageResource(getCoverImage(bookName));

        // 🔗 OPEN BOOK ON CLICK
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, BookReaderActivity.class);
            intent.putExtra("PDF_NAME", category + "/" + bookName + ".pdf");
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return books.size();
    }

    // ✅ BOOK NAME (Hindi) → DRAWABLE (English)
    private int getCoverImage(String bookName) {

        switch (bookName) {

            case "सत्यार्थ प्रकाश":
                return R.drawable.satyarth_prakash;

            case "संस्कारविधि":
                return R.drawable.sanskarvidhi;

            case "आदर्श नित्य कर्म विधि":
                return R.drawable.adarsh;

            case "उपदेश मञ्जरी":
                return R.drawable.updesh;

            case "पंच महायज्ञ विधि":
                return R.drawable.panch;

            case "व्यवहारभानु":
                return R.drawable.vyavhar;

            default:
                return R.drawable.ic_book_placeholder;
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imgBook;
        TextView txtBookName;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgBook = itemView.findViewById(R.id.imgBook);
            txtBookName = itemView.findViewById(R.id.txtBookName);
        }
    }
}
