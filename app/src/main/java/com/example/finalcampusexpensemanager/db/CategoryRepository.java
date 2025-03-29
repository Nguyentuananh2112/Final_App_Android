package com.example.finalcampusexpensemanager.db;

import android.content.Context;

import com.example.finalcampusexpensemanager.model.CategoryModel;

import java.util.List;

public class CategoryRepository {
    private DatabaseHelper dbHelper;

    public CategoryRepository(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public List<CategoryModel> getAllCategories() {
        return dbHelper.getAllCategories();
    }

    public long insertCategory(String name, String description) {
        return dbHelper.insertCategory(name, description);
    }

    public int updateCategory(int id, String name, String description) {
        return dbHelper.updateCategory(id, name, description);
    }

    public int deleteCategory(int id) {
        return dbHelper.deleteCategory(id);
    }
}