package com.example.finalcampusexpensemanager.fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalcampusexpensemanager.OnCategoryChangeListener;
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

    public void addNewCategory(CategoryModel newCategory) {
        categoryList.add(newCategory);
        categoryAdapter.notifyItemInserted(categoryList.size() - 1);
        updateEmptyView();

        // Thông báo cho ExpensesFragment
        notifyExpensesFragmentOnAdded(newCategory);
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
        LinearLayout dialogLayout = new LinearLayout(getActivity());
        dialogLayout.setOrientation(LinearLayout.VERTICAL);
        dialogLayout.setPadding(50, 40, 50, 40);

        TextView title = new TextView(getActivity());
        title.setText("Edit Category");
        title.setTextSize(18);
        title.setTypeface(null, android.graphics.Typeface.BOLD);
        title.setGravity(android.view.Gravity.CENTER);
        title.setPadding(0, 0, 0, 40);
        dialogLayout.addView(title);

        EditText editName = new EditText(getActivity());
        editName.setHint("Category Name");
        editName.setText(category.getName());
        editName.setTextSize(16);
        LinearLayout.LayoutParams nameParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        nameParams.setMargins(0, 0, 0, 20);
        editName.setLayoutParams(nameParams);
        dialogLayout.addView(editName);

        EditText editDescription = new EditText(getActivity());
        editDescription.setHint("Description");
        editDescription.setText(category.getDescription());
        editDescription.setTextSize(16);
        LinearLayout.LayoutParams descParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        descParams.setMargins(0, 0, 0, 40);
        editDescription.setLayoutParams(descParams);
        dialogLayout.addView(editDescription);

        LinearLayout buttonLayout = new LinearLayout(getActivity());
        buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
        buttonLayout.setGravity(android.view.Gravity.END);
        dialogLayout.addView(buttonLayout);

        Button cancelButton = new Button(getActivity());
        cancelButton.setText("Cancel");
        LinearLayout.LayoutParams cancelParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        cancelParams.setMargins(0, 0, 20, 0);
        cancelButton.setLayoutParams(cancelParams);
        buttonLayout.addView(cancelButton);

        Button saveButton = new Button(getActivity());
        saveButton.setText("Save");
        buttonLayout.addView(saveButton);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(dialogLayout);
        AlertDialog dialog = builder.create();

        saveButton.setOnClickListener(v -> {
            String newName = editName.getText().toString().trim();
            String newDescription = editDescription.getText().toString().trim();

            if (newName.isEmpty()) {
                Toast.makeText(getActivity(), "Category name cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            int updated = categoryRepository.updateCategory(category.getId(), newName, newDescription);

            if (updated > 0) {
                int position = -1;
                for (int i = 0; i < categoryList.size(); i++) {
                    if (categoryList.get(i).getId() == category.getId()) {
                        position = i;
                        break;
                    }
                }

                if (position != -1) {
                    categoryList.get(position).setName(newName);
                    categoryList.get(position).setDescription(newDescription);
                    categoryAdapter.notifyItemChanged(position);
                    Toast.makeText(getActivity(), "Category updated", Toast.LENGTH_SHORT).show();

                    // Thông báo cho ExpensesFragment
                    notifyExpensesFragmentOnEdited(categoryList.get(position));
                }
            } else {
                Toast.makeText(getActivity(), "Failed to update category", Toast.LENGTH_SHORT).show();
            }
            dialog.dismiss();
        });

        cancelButton.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
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

                // Thông báo cho ExpensesFragment
                notifyExpensesFragmentOnDeleted(category.getId());
            }
        }
    }

    private void notifyExpensesFragmentOnAdded(CategoryModel newCategory) {
        FragmentManager fragmentManager = getParentFragmentManager();
        Fragment expensesFragment = fragmentManager.findFragmentByTag("ExpensesFragment");
        if (expensesFragment instanceof OnCategoryChangeListener) {
            ((OnCategoryChangeListener) expensesFragment).onCategoryAdded(newCategory);
        }
    }

    private void notifyExpensesFragmentOnEdited(CategoryModel editedCategory) {
        FragmentManager fragmentManager = getParentFragmentManager();
        Fragment expensesFragment = fragmentManager.findFragmentByTag("ExpensesFragment");
        if (expensesFragment instanceof OnCategoryChangeListener) {
            ((OnCategoryChangeListener) expensesFragment).onCategoryEdited(editedCategory);
        }
    }

    private void notifyExpensesFragmentOnDeleted(int categoryId) {
        FragmentManager fragmentManager = getParentFragmentManager();
        Fragment expensesFragment = fragmentManager.findFragmentByTag("ExpensesFragment");
        if (expensesFragment instanceof OnCategoryChangeListener) {
            ((OnCategoryChangeListener) expensesFragment).onCategoryDeleted(categoryId);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadCategories();
    }
}