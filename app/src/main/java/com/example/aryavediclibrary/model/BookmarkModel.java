package com.example.aryavediclibrary.model;

public class BookmarkModel {

    private String bookPath;     // e.g. "dayanand/satyarth_prakash.pdf"
    private String bookName;     // Display name
    private int pageNumber;
    private String description;

    public BookmarkModel(String bookPath, String bookName, int pageNumber, String description) {
        this.bookPath = bookPath;
        this.bookName = bookName;
        this.pageNumber = pageNumber;
        this.description = description;
    }

    public String getBookPath() {
        return bookPath;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBookName() {
        return bookName;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public String getDescription() {
        return description;
    }
}
