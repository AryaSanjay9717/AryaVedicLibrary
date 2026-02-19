package com.example.aryavediclibrary.model;

public class ReadingProgress {
    private String bookId;
    private String bookTitle;
    private int totalPages;
    private int readPages;
    private int lastPage;
    private long lastReadTime;

    public ReadingProgress(String bookId, String bookTitle, int totalPages, int readPages, int lastPage, long lastReadTime) {
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.totalPages = totalPages;
        this.readPages = readPages;
        this.lastPage = lastPage;
        this.lastReadTime = lastReadTime;
    }

    public String getBookId() {
        return bookId;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public int getReadPages() {
        return readPages;
    }

    public int getLastPage() {
        return lastPage;
    }

    public long getLastReadTime() {
        return lastReadTime;
    }
}
