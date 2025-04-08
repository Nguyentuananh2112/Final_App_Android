package com.example.finalcampusexpensemanager;

import com.example.finalcampusexpensemanager.model.CategoryModel;

public interface OnCategoryChangeListener {
    void onCategoryAdded(CategoryModel newCategory);
    void onCategoryEdited(CategoryModel editedCategory);
    void onCategoryDeleted(int categoryId);
}