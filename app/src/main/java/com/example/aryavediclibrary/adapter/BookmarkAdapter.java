package com.example.aryavediclibrary.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aryavediclibrary.BookReaderActivity;
import com.example.aryavediclibrary.R;
import com.example.aryavediclibrary.model.BookmarkModel;

import java.util.List;

public class BookmarkAdapter extends RecyclerView.Adapter<BookmarkAdapter.ViewHolder> {

    private final Context context;
    private final List<BookmarkModel> list;
    private final boolean isBookmarkScreen;

    public BookmarkAdapter(Context context, List<BookmarkModel> list, boolean isBookmarkScreen) {
        this.context = context;
        this.list = list;
        this.isBookmarkScreen = isBookmarkScreen;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.row_bookmark, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        BookmarkModel model = list.get(position);

        // 📘 Book name
        holder.txtBook.setText(model.getBookName());

        // 📄 Page number
        if (isBookmarkScreen) {
            holder.txtPage.setVisibility(View.VISIBLE);
            holder.txtPage.setText("Page " + (model.getPageNumber() + 1));
        } else {
            holder.txtPage.setVisibility(View.GONE);
        }

        // 📝 Description (if exists)
        if (model.getDescription() != null && !model.getDescription().isEmpty()) {
            holder.txtDescription.setVisibility(View.VISIBLE);
            holder.txtDescription.setText(model.getDescription());
        } else {
            holder.txtDescription.setVisibility(View.GONE);
        }

        // ➡️ Click → open exact PDF + page
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, BookReaderActivity.class);
            intent.putExtra("PDF_NAME", model.getBookPath());
            intent.putExtra("PAGE", model.getPageNumber());
            context.startActivity(intent);
        });
        holder.itemView.setOnLongClickListener(v -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Edit Bookmark Note");

            final EditText input = new EditText(context);
            input.setText(model.getDescription());
            input.setHint("Enter description");

            builder.setView(input);

            builder.setPositiveButton("Save", (d, w) -> {
                String newDesc = input.getText().toString().trim();

                SharedPreferences prefs =
                        context.getSharedPreferences("bookmarks", Context.MODE_PRIVATE);

                String key = model.getBookName() + "_" + model.getPageNumber() + "_desc";
                prefs.edit().putString(key, newDesc).apply();

                model.setDescription(newDesc);
                notifyItemChanged(position);
            });

            builder.setNegativeButton("Cancel", null);
            builder.show();

            return true;
        });
        holder.itemView.startAnimation(
                AnimationUtils.loadAnimation(context, R.anim.item_anim)
        );

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtBook, txtPage, txtDescription;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtBook = itemView.findViewById(R.id.txtBookName);
            txtPage = itemView.findViewById(R.id.txtPageNumber);
            txtDescription = itemView.findViewById(R.id.txtBookmarkDescription);
        }
    }
}
