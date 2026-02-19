package com.example.aryavediclibrary.model;

public class CategoryModel {
    public String name;       // Display name (e.g., "Swami Dayanand Ji")
    public String category;   // folder name in assets (e.g., "dayanand")
    public int imageRes;      // drawable resource id

    public CategoryModel(String name, String category, int imageRes) {
        this.name = name;
        this.category = category;
        this.imageRes = imageRes;
    }
}
