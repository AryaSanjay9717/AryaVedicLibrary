package com.example.aryavediclibrary;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aryavediclibrary.adapter.CategoryAdapter;
import com.example.aryavediclibrary.model.CategoryModel;

import java.util.ArrayList;

import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<CategoryModel> categories = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setWhiteStatusBarIcons();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setOverflowIcon(ContextCompat.getDrawable(this, R.drawable.ic_overflow_white));


        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        recyclerView = findViewById(R.id.recyclerCategories);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Add categories
        categories.add(new CategoryModel("महर्षि स्वामी दयानंद सरस्वती", "dayanand", R.drawable.ic_dayanand));
        categories.add(new CategoryModel("वेद", "ved", R.drawable.ic_ved));
        categories.add(new CategoryModel("हवन", "hawan", R.drawable.ic_hawan));
        categories.add(new CategoryModel("भजन", "bhajan", R.drawable.ic_bhajan));
        categories.add(new CategoryModel("अन्य पुस्तके", "other", R.drawable.ic_other));

        CategoryAdapter adapter = new CategoryAdapter(this, categories);
        recyclerView.setAdapter(adapter);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // ✅ Add "Track Progress" dynamically if needed
        MenuItem progressItem = menu.findItem(R.id.menu_track_progress);
        if (progressItem == null) {
            menu.add(Menu.NONE, R.id.menu_track_progress, 100, "Track Progress")
                    .setIcon(R.drawable.ic_progress) // optional icon
                    .setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.menu_bookmarks) {
            startActivity(new Intent(this, BookmarksActivity.class));
            return true;
        }

        // 🔴 NEW: Track Progress
        if (id == R.id.menu_track_progress) {
            startActivity(new Intent(this, TrackProgressActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    private void setWhiteStatusBarIcons() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            getWindow().getInsetsController().setSystemBarsAppearance(
                    0,
                    android.view.WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            );
        } else {
            getWindow().getDecorView().setSystemUiVisibility(0);
        }
    }
}
