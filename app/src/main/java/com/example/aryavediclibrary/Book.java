package com.example.aryavediclibrary;

public class Book {
    private int id;
    private String title;
    private String pdfPath;
    private String category;

    public Book(int id, String title, String pdfPath, String category) {
        this.id = id;
        this.title = title;
        this.pdfPath = pdfPath;
        this.category = category;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getPdfPath() {
        return pdfPath;
    }

    public String getCategory() {
        return category;
    }

    @Override
    public String toString() {
        return title;
    }
}
