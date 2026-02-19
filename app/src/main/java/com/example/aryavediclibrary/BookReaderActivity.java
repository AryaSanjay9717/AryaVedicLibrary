package com.example.aryavediclibrary;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.aryavediclibrary.model.ReadingProgress;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.listener.OnTapListener;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.MaterialToolbar;

public class BookReaderActivity extends AppCompatActivity
        implements OnPageChangeListener, OnTapListener {

    PDFView pdfView;
    ImageButton btnBookmark;
    TextView pageNumber;
    AppBarLayout appBarLayout;
    MaterialToolbar toolbar;
    View fastScrollThumb;
    private boolean suppressRender = false;
    private static final String PREFS_PROGRESS = "book_progress";
    private static final String PREFS_BOOKMARKS = "bookmarks";

    private String pdfName;
    private int lastPage = 0;
    private int totalPages = 0;
    private DatabaseHelper dbHelper;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private Handler hideHandler = new Handler(Looper.getMainLooper());
    private Runnable hideThumbRunnable;

    private boolean isToolbarVisible = false;
    private boolean isThumbDragging = false;

    private int statusBarHeight = 0;
    private final int[] pdfViewLocation = new int[2];
    private boolean userHasScrolled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_reader);

        // 🔥 Status bar BLACK + white icons
        getWindow().setStatusBarColor(Color.BLACK);
        setWhiteStatusBarIcons();

        dbHelper = new DatabaseHelper(this);
        int resourceId = getResources()
                .getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight =
                    getResources().getDimensionPixelSize(resourceId);
        }

        pdfView = findViewById(R.id.pdfView);
        btnBookmark = findViewById(R.id.btnBookmark);
        pageNumber = findViewById(R.id.pageNumber);
        appBarLayout = findViewById(R.id.appBarLayout);
        toolbar = findViewById(R.id.toolbar);
        fastScrollThumb = findViewById(R.id.fastScrollThumb);

        // 🔥 Default invisible
        fastScrollThumb.setVisibility(View.GONE);

        pdfName = getIntent().getStringExtra("PDF_NAME");
        if (pdfName == null || pdfName.isEmpty()) {
            Toast.makeText(this, "PDF not found!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        SharedPreferences progressPrefs =
                getSharedPreferences(PREFS_PROGRESS, MODE_PRIVATE);
        lastPage = progressPrefs.getInt(pdfName, 0);

        int openPage = getIntent().getIntExtra("PAGE", -1);
        if (openPage != -1) lastPage = openPage;

        pdfView.fromAsset("books/" + pdfName)
                .defaultPage(lastPage)
                .enableSwipe(true)
                .enableDoubletap(true)
                .onPageChange(this)
                .onTap(this)
                .onLoad(pageCount -> {
                    totalPages = pageCount;
                    updateBookmarkIcon();

                    fastScrollThumb.post(() -> {
                        fastScrollThumb.setVisibility(View.GONE);
                        userHasScrolled = false;   // 🔥 reset
                    });
                    saveProgressToDatabase(lastPage);
                })
                .onError(t ->
                        Toast.makeText(this,
                                "PDF load error",
                                Toast.LENGTH_LONG).show())
                .load();

        btnBookmark.setOnClickListener(v -> handleBookmarkClick());
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.menu_track_progress) {
                showProgressDialog();
                return true;
            }
            return false;
        });
        fastScrollThumb.post(() -> fastScrollThumb.setVisibility(View.GONE));
        hideToolbar();
        setupFastScroll();
    }

    // ================= PAGE CHANGE =================

    @Override
    public void onPageChanged(int page, int pageCount) {

        // 🔥 REAL scroll detect (first time)
        if (!userHasScrolled) {
            userHasScrolled = true;
        }
        lastPage = page;

        // ✅ Save progress to SharedPreferences
        getSharedPreferences(PREFS_PROGRESS, MODE_PRIVATE)
                .edit()
                .putInt(pdfName, page)
                .apply();

        // ✅ Save progress to Database
        dbHelper.updateReadingProgress(
                pdfName,           // bookId
                pdfName,           // bookTitle (same as name)
                pageCount,         // totalPages
                page + 1,          // readPages (human-friendly)
                page               // lastPage
        );
        if (!isThumbDragging) {
            fastScrollThumb.setVisibility(View.VISIBLE);
            fastScrollThumb.bringToFront();
        }

        if (isThumbDragging) {
            pageNumber.setVisibility(View.GONE);
        }

        if (suppressRender) {
            lastPage = page;
            saveLastPage(page);
            return;
        }

        lastPage = page;
        saveLastPage(page);
        updateBookmarkIcon();

        pageNumber.setText((page + 1) + " / " + pageCount);
        pageNumber.setVisibility(View.VISIBLE);

        float density = getResources().getDisplayMetrics().density;
        float minY = 40 * density;
        float maxY = pdfView.getHeight()
                - fastScrollThumb.getHeight()
                - 24 * density;

        float percent = (float) page / (float) (pageCount - 1);
        float y = minY + percent * (maxY - minY);
        fastScrollThumb.setY(y);

        handler.removeCallbacksAndMessages(null);
        handler.postDelayed(() -> {
            if (!isThumbDragging) {
                fastScrollThumb.setVisibility(View.GONE);
            }
            pageNumber.setVisibility(View.GONE);
            hideToolbar();
        }, 2000);
}

    private void saveLastPage(int page) {
        getSharedPreferences(PREFS_PROGRESS, MODE_PRIVATE)
                .edit()
                .putInt(pdfName, page)
                .apply();
    }
// ================= 📊 SQLITE PROGRESS =================

    private void saveProgressToDatabase(int currentPage) {

        if (totalPages == 0) return;

        String bookId = pdfName;          // UNIQUE
        String bookTitle = pdfName
                .replace(".pdf", "")
                .replace("_", " ");

        int readPages = currentPage + 1;

        dbHelper.updateReadingProgress(
                bookId,
                bookTitle,
                totalPages,
                readPages,
                currentPage
        );
    }

    private void showProgressDialog() {
        String bookId = pdfName;
        ReadingProgress progress = dbHelper.getReadingProgress(bookId);

        if (progress != null) {
            int readPages = progress.getReadPages();
            int totalPages = progress.getTotalPages();
            if (totalPages > 0) {
                int percentage = (int) (((float) readPages / totalPages) * 100);

                String message = "You have read " + readPages + " out of " + totalPages + " pages (" + percentage + "%)."
                        + "\n\nKeep up the great work!";

                new AlertDialog.Builder(this)
                        .setTitle("Your Reading Progress")
                        .setMessage(message)
                        .setPositiveButton("OK", null)
                        .show();
            } else {
                Toast.makeText(this, "Could not calculate reading progress.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "No progress found for this book.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveProgressToDatabase(lastPage); // 🔥 safety
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveProgressToDatabase(lastPage); // 🔥 safety
    }
    // ================= BOOKMARK =================

    private void handleBookmarkClick() {
        int page = pdfView.getCurrentPage();
        SharedPreferences prefs =
                getSharedPreferences(PREFS_BOOKMARKS, MODE_PRIVATE);

        String keyBase = pdfName + "_" + page;
        boolean alreadyBookmarked =
                prefs.contains(keyBase + "_desc");

        if (alreadyBookmarked) {
            prefs.edit().remove(keyBase + "_desc").apply();
            updateBookmarkIcon();
            Toast.makeText(this,
                    "Bookmark removed",
                    Toast.LENGTH_SHORT).show();
        } else {
            showDescriptionDialog(keyBase);
        }
    }

    private void showDescriptionDialog(String keyBase) {

        AlertDialog.Builder builder =
                new AlertDialog.Builder(this);
        builder.setTitle("Add bookmark note");

        EditText input = new EditText(this);
        input.setHint("Why is this page important?");
        input.setInputType(
                InputType.TYPE_CLASS_TEXT
                        | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        input.setMinLines(2);

        builder.setView(input);

        builder.setPositiveButton("Save",
                (dialog, which) -> {
                    String desc =
                            input.getText().toString().trim();
                    if (desc.isEmpty()) desc = "No description";

                    getSharedPreferences(
                            PREFS_BOOKMARKS,
                            MODE_PRIVATE)
                            .edit()
                            .putString(keyBase + "_desc", desc)
                            .apply();

                    updateBookmarkIcon();
                    Toast.makeText(this,
                            "Bookmark added",
                            Toast.LENGTH_SHORT).show();
                });

        builder.setNegativeButton("Cancel",
                (dialog, which) -> dialog.dismiss());

        builder.show();
    }

    private void updateBookmarkIcon() {
        int page = pdfView.getCurrentPage();
        SharedPreferences prefs =
                getSharedPreferences(PREFS_BOOKMARKS, MODE_PRIVATE);

        boolean bookmarked =
                prefs.contains(pdfName + "_" + page + "_desc");

        btnBookmark.setImageResource(
                bookmarked
                        ? R.drawable.ic_star_filled
                        : R.drawable.ic_star_outline
        );
    }

    // ================= TAP / TOOLBAR =================

    @Override
    public boolean onTap(MotionEvent e) {
        toggleToolbar();
        return true;
    }

    private void toggleToolbar() {
        if (isToolbarVisible) hideToolbar();
        else showToolbar();
    }

    private void showToolbar() {
        appBarLayout.setVisibility(View.VISIBLE);
        isToolbarVisible = true;

        updateThumbZOrder(); // 🔥 thumb peeche chala jayega
        fastScrollThumb.setVisibility(View.VISIBLE);
        isToolbarVisible = true;
        setWhiteStatusBarIcons();
    }

    private void hideToolbar() {
        appBarLayout.setVisibility(View.GONE);
        isToolbarVisible = false;

        updateThumbZOrder(); // 🔥 thumb normal z
        setBlackStatusBarIcons();
    }

    // ================= STATUS BAR =================

    private void setWhiteStatusBarIcons() {
        if (android.os.Build.VERSION.SDK_INT
                >= android.os.Build.VERSION_CODES.R) {

            getWindow().getInsetsController()
                    .setSystemBarsAppearance(
                            0,
                            android.view.WindowInsetsController
                                    .APPEARANCE_LIGHT_STATUS_BARS
                    );
        } else {
            getWindow().getDecorView()
                    .setSystemUiVisibility(0);
        }
    }

    private void setBlackStatusBarIcons() {
        if (android.os.Build.VERSION.SDK_INT
                >= android.os.Build.VERSION_CODES.R) {

            getWindow().getInsetsController()
                    .setSystemBarsAppearance(
                            android.view.WindowInsetsController
                                    .APPEARANCE_LIGHT_STATUS_BARS,
                            android.view.WindowInsetsController
                                    .APPEARANCE_LIGHT_STATUS_BARS
                    );
        } else {
            getWindow().getDecorView()
                    .setSystemUiVisibility(
                            View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }
    // ================= FAST SCROLL =================

    private void setupFastScroll() {

        final float density = getResources().getDisplayMetrics().density;
        final float TOP_PADDING = 40 * density;
        final float BOTTOM_PADDING = 24 * density;

        fastScrollThumb.setOnTouchListener((v, event) -> {

            if (pdfView.getPageCount() == 0) return false;

            float pdfTop = pdfView.getY();
            float pdfHeight = pdfView.getHeight();
            int pageCount = pdfView.getPageCount();

            switch (event.getAction()) {

                case MotionEvent.ACTION_DOWN:
                    isThumbDragging = true;
                    suppressRender = true;   // 🔥 render freeze
                    // 🔥 toolbar auto hide
                    if (isToolbarVisible) {
                        hideToolbar();
                    }

                    updateThumbZOrder();
                    v.bringToFront();
                    return true;

                case MotionEvent.ACTION_MOVE:
                    if (!isThumbDragging) return false;

                    float fingerY =
                            event.getRawY()
                                    - pdfTop
                                    - v.getHeight() / 2f;

                    float minY = TOP_PADDING;
                    float maxY =
                            pdfHeight - v.getHeight() - BOTTOM_PADDING;

                    fingerY =
                            Math.max(minY, Math.min(fingerY, maxY));

                    v.setY(fingerY);

                    float percent =
                            (fingerY - minY) / (maxY - minY);

                    int page =
                            (int) (percent * (pageCount - 1));

                    // 🔥 NO animation → no hang
                    pdfView.jumpTo(page, false);
                    return true;

                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    isThumbDragging = false;
                    suppressRender = false;

                    // 🔥 final page force render
                    pdfView.jumpTo(pdfView.getCurrentPage(), false);
                    return true;
            }
            return false;
        });
    }
    private void updateThumbZOrder() {
        if (isToolbarVisible) {
            // toolbar ke peeche rahe
            fastScrollThumb.setTranslationZ(0f);
            appBarLayout.setTranslationZ(10f);
        } else {
            // normal condition
            fastScrollThumb.setTranslationZ(10f);
        }
    }
}
