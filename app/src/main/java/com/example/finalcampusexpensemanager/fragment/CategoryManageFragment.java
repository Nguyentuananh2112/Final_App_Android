package com.example.finalcampusexpensemanager.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalcampusexpensemanager.R;
import com.example.finalcampusexpensemanager.adapter.CategoryAdapter;
import com.example.finalcampusexpensemanager.db.CategoryRepository;
import com.example.finalcampusexpensemanager.model.CategoryModel;

import java.util.ArrayList;
import java.util.List;

public class CategoryManageFragment extends Fragment implements CategoryAdapter.OnCategoryActionListener {
    private RecyclerView recyclerView;
    private TextView emptyView;
    private CategoryAdapter categoryAdapter;
    private CategoryRepository categoryRepository;
    private List<CategoryModel> categoryList;

    public CategoryManageFragment() {
        // Constructor rỗng bắt buộc cho Fragment
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category_manage, container, false);


        recyclerView = view.findViewById(R.id.category_recycler_view);
        emptyView = view.findViewById(R.id.empty_view);

        categoryRepository = new CategoryRepository(getActivity());
        categoryList = new ArrayList<>();
        categoryAdapter = new CategoryAdapter(categoryList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(categoryAdapter);

        loadCategories();

        return view;
    }

    private void loadCategories() {
        categoryList.clear();
        categoryList.addAll(categoryRepository.getAllCategories());
        categoryAdapter.notifyDataSetChanged();
        updateEmptyView();
    }

    // Phương thức công khai để thêm danh mục từ bên ngoài
    public void addNewCategory(CategoryModel newCategory) {
        categoryList.add(newCategory);
        categoryAdapter.notifyItemInserted(categoryList.size() - 1);
        updateEmptyView();
    }

    private void updateEmptyView() {
        if (categoryList.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onEdit(CategoryModel category) {
        int updated = categoryRepository.updateCategory(category.getId(), category.getName(), category.getDescription());
        if (updated > 0) {
            int position = categoryList.indexOf(category);
            if (position != -1) {
                categoryAdapter.notifyItemChanged(position);
            }
        }
    }

    @Override
    public void onDelete(CategoryModel category) {
        int position = categoryList.indexOf(category);
        if (position != -1) {
            int deleted = categoryRepository.deleteCategory(category.getId());
            if (deleted > 0) {
                categoryList.remove(position);
                categoryAdapter.notifyItemRemoved(position);
                updateEmptyView();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadCategories();
    }

}