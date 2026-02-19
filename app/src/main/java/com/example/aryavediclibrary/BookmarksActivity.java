package com.example.aryavediclibrary;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;

import com.example.aryavediclibrary.adapter.BookmarkAdapter;
import com.example.aryavediclibrary.model.BookmarkModel;

import java.util.ArrayList;
import java.util.Map;

public class BookmarksActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    TextView txtEmpty;
    ArrayList<BookmarkModel> list = new ArrayList<>();
    BookmarkAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmarks);
        setBlackStatusBarIcons();

        recyclerView = findViewById(R.id.recyclerBookmarks);
        txtEmpty = findViewById(R.id.txtEmpty);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadBookmarks();

        adapter = new BookmarkAdapter(this, list, true);
        recyclerView.setAdapter(adapter);

        updateEmptyState();

        // ✅ Professional swipe (icon + background)
        ItemTouchHelper helper = new ItemTouchHelper(
                new SwipeToDeleteCallback(this) {

                    @Override
                    public void onSwiped(
                            @NonNull RecyclerView.ViewHolder viewHolder,
                            int direction
                    ) {

                        int position = viewHolder.getAdapterPosition();
                        BookmarkModel removed = list.get(position);

                        SharedPreferences prefs =
                                getSharedPreferences("bookmarks", MODE_PRIVATE);

                        String baseKey =
                                removed.getBookPath() + "_" + removed.getPageNumber();

                        prefs.edit()
                                .remove(baseKey)
                                .remove(baseKey + "_desc")
                                .apply();

                        list.remove(position);
                        adapter.notifyItemRemoved(position);

                        updateEmptyState();
                    }
                }
        );

        helper.attachToRecyclerView(recyclerView);
    }

    // 🔁 Show / hide empty text
    private void updateEmptyState() {
        if (list.isEmpty()) {
            txtEmpty.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            txtEmpty.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    // 📚 Load bookmarks from SharedPreferences
    private void loadBookmarks() {

        SharedPreferences prefs = getSharedPreferences("bookmarks", MODE_PRIVATE);
        Map<String, ?> all = prefs.getAll();

        list.clear();

        for (String key : all.keySet()) {

            // only description keys
            if (!key.endsWith("_desc")) continue;

            /*
             key format:
             dayanand/satyarth_prakash.pdf_45_desc
             */

            String base = key.replace("_desc", "");
            String description = prefs.getString(key, "");

            int lastUnderscore = base.lastIndexOf("_");
            if (lastUnderscore == -1) continue;

            String bookPath = base.substring(0, lastUnderscore);
            String pageStr = base.substring(lastUnderscore + 1);

            int pageNumber;
            try {
                pageNumber = Integer.parseInt(pageStr);
            } catch (NumberFormatException e) {
                continue;
            }

            // display name
            String bookName = bookPath;
            if (bookName.contains("/")) {
                bookName = bookName.substring(bookName.lastIndexOf("/") + 1);
            }
            if (bookName.endsWith(".pdf")) {
                bookName = bookName.replace(".pdf", "");
            }

            list.add(new BookmarkModel(
                    bookPath,
                    bookName,
                    pageNumber,
                    description
            ));
        }
    }
    private void setBlackStatusBarIcons() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            getWindow().getInsetsController().setSystemBarsAppearance(
                    android.view.WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                    android.view.WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            );
        } else {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            );
        }
    }
}