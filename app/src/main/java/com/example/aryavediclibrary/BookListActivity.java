package com.example.aryavediclibrary;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aryavediclibrary.adapter.BookmarkAdapter;
import com.example.aryavediclibrary.adapter.SimpleBookAdapter;
import com.example.aryavediclibrary.model.BookmarkModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class BookListActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<BookmarkModel> books = new ArrayList<>(); // BookmarkModel use kiya

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_list);
        setBlackStatusBarIcons();
        // 1️⃣ Category fetch karo
        String category = getIntent().getStringExtra("CATEGORY");

        // 2️⃣ RecyclerView setup
        recyclerView = findViewById(R.id.recyclerBooks);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 3️⃣ List of book names (Strings)
        ArrayList<String> books = new ArrayList<>();

        try {
            // 4️⃣ Assets folder se files list karo
            String[] files = getAssets().list("books/" + category);

            if (files != null) {
                for (String file : files) {

                    // file = "satyarth_prakash.pdf"
                    String bookName = file.replace(".pdf", "");

                    books.add(bookName);   // UI me sirf naam
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 5️⃣ Adapter setup karo
        SimpleBookAdapter adapter = new SimpleBookAdapter(this, books, category);
        recyclerView.setAdapter(adapter);
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