package com.example.aryavediclibrary;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.aryavediclibrary.model.ReadingProgress;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Database info
    private static final String DATABASE_NAME = "arya_vedic.db";
    private static final int DATABASE_VERSION = 3; // ⬅️ INCREASED (progress added)

    // Table names
    public static final String TABLE_BOOKS = "books";
    public static final String TABLE_THOUGHTS = "thoughts";
    public static final String TABLE_NOTES = "notes";
    public static final String TABLE_ENGAGEMENT = "engagement";
    public static final String TABLE_BOOKMARKS = "bookmarks";
    public static final String TABLE_PROGRESS = "reading_progress"; // ✅ NEW

    // Book table columns
    public static final String KEY_BOOK_ID = "id";
    public static final String KEY_BOOK_TITLE = "title";
    public static final String KEY_BOOK_PDF_PATH = "pdf_path";
    public static final String KEY_BOOK_CATEGORY = "category";

    // Bookmark columns
    public static final String KEY_BM_ID = "id";
    public static final String KEY_BM_BOOK_PATH = "book_path";
    public static final String KEY_BM_PAGE = "page_no";
    public static final String KEY_BM_DESC = "description";
    public static final String KEY_BM_TIME = "created_time";

    // 📊 Progress columns
    public static final String KEY_PR_BOOK_ID = "book_id";
    public static final String KEY_PR_BOOK_TITLE = "book_title";
    public static final String KEY_PR_TOTAL_PAGES = "total_pages";
    public static final String KEY_PR_READ_PAGES = "read_pages";
    public static final String KEY_PR_LAST_PAGE = "last_read_page";
    public static final String KEY_PR_LAST_TIME = "last_read_time";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // Books table
        db.execSQL(
                "CREATE TABLE " + TABLE_BOOKS + " (" +
                        KEY_BOOK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        KEY_BOOK_TITLE + " TEXT," +
                        KEY_BOOK_PDF_PATH + " TEXT," +
                        KEY_BOOK_CATEGORY + " TEXT" +
                        ")"
        );

        // Thoughts table
        db.execSQL(
                "CREATE TABLE " + TABLE_THOUGHTS + " (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "thought TEXT," +
                        "explanation TEXT" +
                        ")"
        );

        // Notes table
        db.execSQL(
                "CREATE TABLE " + TABLE_NOTES + " (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "book_id INTEGER," +
                        "page_no INTEGER," +
                        "note_text TEXT" +
                        ")"
        );

        // Engagement table
        db.execSQL(
                "CREATE TABLE " + TABLE_ENGAGEMENT + " (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "type TEXT," +
                        "value TEXT," +
                        "time TEXT" +
                        ")"
        );

        // Bookmarks table
        db.execSQL(
                "CREATE TABLE " + TABLE_BOOKMARKS + " (" +
                        KEY_BM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        KEY_BM_BOOK_PATH + " TEXT," +
                        KEY_BM_PAGE + " INTEGER," +
                        KEY_BM_DESC + " TEXT," +
                        KEY_BM_TIME + " TEXT" +
                        ")"
        );

        // 📊 Reading Progress table (NEW)
        db.execSQL(
                "CREATE TABLE " + TABLE_PROGRESS + " (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        KEY_PR_BOOK_ID + " TEXT UNIQUE," +
                        KEY_PR_BOOK_TITLE + " TEXT," +
                        KEY_PR_TOTAL_PAGES + " INTEGER," +
                        KEY_PR_READ_PAGES + " INTEGER," +
                        KEY_PR_LAST_PAGE + " INTEGER," +
                        KEY_PR_LAST_TIME + " INTEGER" +
                        ")"
        );

        addInitialBooks(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        if (oldVersion < 2) {
            db.execSQL(
                    "CREATE TABLE IF NOT EXISTS " + TABLE_BOOKMARKS + " (" +
                            KEY_BM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                            KEY_BM_BOOK_PATH + " TEXT," +
                            KEY_BM_PAGE + " INTEGER," +
                            KEY_BM_DESC + " TEXT," +
                            KEY_BM_TIME + " TEXT" +
                            ")"
            );
        }

        if (oldVersion < 3) {
            db.execSQL(
                    "CREATE TABLE IF NOT EXISTS " + TABLE_PROGRESS + " (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            KEY_PR_BOOK_ID + " TEXT UNIQUE," +
                            KEY_PR_BOOK_TITLE + " TEXT," +
                            KEY_PR_TOTAL_PAGES + " INTEGER," +
                            KEY_PR_READ_PAGES + " INTEGER," +
                            KEY_PR_LAST_PAGE + " INTEGER," +
                            KEY_PR_LAST_TIME + " INTEGER" +
                            ")"
            );
        }
    }

    /* ---------------- BOOK METHODS ---------------- */

    public void addBook(Book book) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_BOOK_TITLE, book.getTitle());
        values.put(KEY_BOOK_PDF_PATH, book.getPdfPath());
        values.put(KEY_BOOK_CATEGORY, book.getCategory());
        db.insert(TABLE_BOOKS, null, values);
        db.close();
    }

    public List<Book> getAllBooks() {
        List<Book> bookList = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_BOOKS, null);

        if (cursor.moveToFirst()) {
            do {
                bookList.add(new Book(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3)
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return bookList;
    }

    /* ---------------- BOOKMARK METHODS ---------------- */

    public void addBookmark(String bookPath, int pageNo, String description) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(KEY_BM_BOOK_PATH, bookPath);
        cv.put(KEY_BM_PAGE, pageNo);
        cv.put(KEY_BM_DESC, description);
        cv.put(KEY_BM_TIME, String.valueOf(System.currentTimeMillis()));
        db.insert(TABLE_BOOKMARKS, null, cv);
        db.close();
    }

    public Cursor getAllBookmarks() {
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery(
                "SELECT * FROM " + TABLE_BOOKMARKS + " ORDER BY " + KEY_BM_ID + " DESC",
                null
        );
    }

    /* ---------------- 📊 PROGRESS METHODS ---------------- */

    // ✅ Insert / Update reading progress
    public void updateReadingProgress(String bookId, String bookTitle,
                                      int totalPages, int readPages, int lastPage) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(KEY_PR_BOOK_ID, bookId);
        cv.put(KEY_PR_BOOK_TITLE, bookTitle);
        cv.put(KEY_PR_TOTAL_PAGES, totalPages);
        cv.put(KEY_PR_READ_PAGES, readPages);
        cv.put(KEY_PR_LAST_PAGE, lastPage);
        cv.put(KEY_PR_LAST_TIME, System.currentTimeMillis());

        db.insertWithOnConflict(
                TABLE_PROGRESS,
                null,
                cv,
                SQLiteDatabase.CONFLICT_REPLACE
        );
        db.close();
    }

    public ReadingProgress getReadingProgress(String bookId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_PROGRESS,
                new String[]{KEY_PR_BOOK_ID, KEY_PR_BOOK_TITLE, KEY_PR_TOTAL_PAGES, KEY_PR_READ_PAGES, KEY_PR_LAST_PAGE, KEY_PR_LAST_TIME},
                KEY_PR_BOOK_ID + "=?",
                new String[]{bookId}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            ReadingProgress progress = new ReadingProgress(
                    cursor.getString(cursor.getColumnIndexOrThrow(KEY_PR_BOOK_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(KEY_PR_BOOK_TITLE)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(KEY_PR_TOTAL_PAGES)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(KEY_PR_READ_PAGES)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(KEY_PR_LAST_PAGE)),
                    cursor.getLong(cursor.getColumnIndexOrThrow(KEY_PR_LAST_TIME)));
            cursor.close();
            return progress;
        }
        if (cursor != null) {
            cursor.close();
        }
        return null;
    }

    // ✅ Get all progress (Track Progress screen)
    public Cursor getAllReadingProgress() {
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery(
                "SELECT * FROM " + TABLE_PROGRESS +
                        " ORDER BY " + KEY_PR_LAST_TIME + " DESC",
                null
        );
    }

    // ✅ Get single book progress
    public Cursor getProgressForBook(String bookId) {
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery(
                "SELECT * FROM " + TABLE_PROGRESS +
                        " WHERE " + KEY_PR_BOOK_ID + "=?",
                new String[]{bookId}
        );
    }

    /* ---------------- INITIAL DATA ---------------- */

    private void addInitialBooks(SQLiteDatabase db) {
        addBook(db, new Book(0, "Introduction to the Vedas", "vedas_intro.pdf", "Vedas"));
        addBook(db, new Book(0, "Rigveda - Chapter 1", "rigveda_ch1.pdf", "Vedas"));
    }

    private void addBook(SQLiteDatabase db, Book book) {
        ContentValues values = new ContentValues();
        values.put(KEY_BOOK_TITLE, book.getTitle());
        values.put(KEY_BOOK_PDF_PATH, book.getPdfPath());
        values.put(KEY_BOOK_CATEGORY, book.getCategory());
        db.insert(TABLE_BOOKS, null, values);
    }
}
