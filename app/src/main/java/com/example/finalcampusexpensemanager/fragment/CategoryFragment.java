package com.example.finalcampusexpensemanager.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.finalcampusexpensemanager.OnCategoryChangeListener;
import com.example.finalcampusexpensemanager.R;
import com.example.finalcampusexpensemanager.db.DatabaseHelper;
import com.example.finalcampusexpensemanager.model.CategoryModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class CategoryFragment extends DialogFragment {

    private TextInputEditText categoryNameInput, categoryDescriptionInput;
    private MaterialButton saveCategoryButton;
    private TextInputLayout categoryNameLayout, categoryDescriptionLayout;
    private DatabaseHelper dbHelper;
    private OnCategoryChangeListener listener;

    public CategoryFragment() {
        // Required empty public constructor
    }

    public void setOnCategoryChangeListener(OnCategoryChangeListener listener) {
        this.listener = listener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category, container, false);

        categoryNameLayout = view.findViewById(R.id.category_name_layout);
        categoryNameInput = view.findViewById(R.id.category_name_input);
        categoryDescriptionLayout = view.findViewById(R.id.category_description_layout);
        categoryDescriptionInput = view.findViewById(R.id.category_description_input);
        saveCategoryButton = view.findViewById(R.id.save_category_button);

        dbHelper = new DatabaseHelper(requireContext());

        saveCategoryButton.setOnClickListener(v -> saveCategory());

        return view;
    }

    private void saveCategory() {
        String name = categoryNameInput.getText().toString().trim();
        String description = categoryDescriptionInput.getText().toString().trim();

        categoryNameLayout.setError(null);

        if (name.isEmpty()) {
            categoryNameLayout.setError("Category name is required");
            return;
        }

        try {
            if (dbHelper.isCategoryExists(name)) {
                categoryNameLayout.setError("Category '" + name + "' already exists");
                return;
            }

            long result = dbHelper.insertCategory(name, description);
            if (result != -1) {
                Toast.makeText(requireContext(), "Category '" + name + "' saved successfully",
                        Toast.LENGTH_SHORT).show();
                CategoryModel newCategory = new CategoryModel((int) result, name, description, null, null);

                // Thông báo qua listener nếu có
                if (listener != null) {
                    listener.onCategoryAdded(newCategory);
                }

                // Thông báo cho ExpensesFragment
                notifyExpensesFragmentOnAdded(newCategory);

                resetForm();
                dismiss();
            } else {
                Toast.makeText(requireContext(), "Failed to save category",
                        Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(requireContext(), "Error: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private void notifyExpensesFragmentOnAdded(CategoryModel newCategory) {
        FragmentManager fragmentManager = getParentFragmentManager();
        Fragment expensesFragment = fragmentManager.findFragmentByTag("ExpensesFragment");
        if (expensesFragment instanceof OnCategoryChangeListener) {
            ((OnCategoryChangeListener) expensesFragment).onCategoryAdded(newCategory);
        }
    }

    private void resetForm() {
        categoryNameInput.setText("");
        categoryDescriptionInput.setText("");
        categoryNameLayout.setError(null);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
        }
    }
}