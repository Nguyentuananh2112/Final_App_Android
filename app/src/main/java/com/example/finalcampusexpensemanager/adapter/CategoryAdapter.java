package com.example.finalcampusexpensemanager.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalcampusexpensemanager.R;
import com.example.finalcampusexpensemanager.model.CategoryModel;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private List<CategoryModel> categoryList;
    private OnCategoryActionListener listener;

    public interface OnCategoryActionListener {
        void onEdit(CategoryModel category);
        void onDelete(CategoryModel category);
    }

    public CategoryAdapter(List<CategoryModel> categoryList, OnCategoryActionListener listener) {
        this.categoryList = categoryList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        CategoryModel category = categoryList.get(position);
        Log.d("CategoryAdapter", "Binding category at position " + position + ": " + category.getName());
        holder.categoryName.setText(category.getName());

        holder.editButton.setOnClickListener(v -> listener.onEdit(category));
        holder.deleteButton.setOnClickListener(v -> listener.onDelete(category));
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public void updateList(List<CategoryModel> newList) {
        Log.d("CategoryAdapter", "Updating list with " + newList.size() + " items");
        categoryList.clear();
        categoryList.addAll(newList);
        notifyDataSetChanged();
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView categoryName;
        Button editButton, deleteButton;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryName = itemView.findViewById(R.id.category_name);
            editButton = itemView.findViewById(R.id.edit_button);
            deleteButton = itemView.findViewById(R.id.delete_button);
        }
    }
}