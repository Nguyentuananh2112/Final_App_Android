package com.example.finalcampusexpensemanager.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.finalcampusexpensemanager.R;
import com.example.finalcampusexpensemanager.db.DatabaseHelper;
import com.example.finalcampusexpensemanager.model.CategoryModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ExpensesFragment extends Fragment implements OnCategoryAddedListener {

    private TabLayout tabLayout;
    private MaterialButton dateInput, saveButton, addCategoryButton;
    private TextInputEditText noteInput, amountInput;
    private Spinner categorySpinner;
    private MaterialTextView amountLabel, categoryLabel;
    private String selectedCategory = "";
    private boolean isIncomeMode = true;

    private DatabaseHelper dbHelper;
    private ArrayAdapter<String> categoryAdapter;
    private List<CategoryModel> categories;

    private static final int COLOR_ORANGE = 0xFFFF5722;
    private static final int COLOR_WHITE = 0xFFFFFFFF;
    private static final int COLOR_BLACK = 0xFF000000;

    public ExpensesFragment() {
    }

    @SuppressLint({"SetTextI18n", "MissingInflatedId"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_expenses, container, false);

        // Initialize UI components
        tabLayout = view.findViewById(R.id.tab_layout);
        dateInput = view.findViewById(R.id.date_input);
        noteInput = view.findViewById(R.id.note_input);
        amountLabel = view.findViewById(R.id.amount_label);
        amountInput = view.findViewById(R.id.amount_input);
        categoryLabel = view.findViewById(R.id.category_label);
        categorySpinner = view.findViewById(R.id.category_spinner);
        saveButton = view.findViewById(R.id.btn_save_button);
        addCategoryButton = view.findViewById(R.id.btn_add_category);

        // Initialize database and category list
        dbHelper = new DatabaseHelper(getContext());
        categories = new ArrayList<>();

        // Setup Spinner
        setupCategorySpinner();

        // Setup initial state
        resetTabButtons();
        tabLayout.getTabAt(0).select();
        amountLabel.setText("Income");

        // Tab selection listener
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    switchToIncomeMode();
                } else {
                    switchToExpenseMode();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        // Date picker event
        dateInput.setOnClickListener(v -> showDatePicker());

        // Category spinner selection
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
                if (!selectedItem.equals("Select Category")) {
                    selectedCategory = selectedItem;
                } else {
                    selectedCategory = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedCategory = "";
            }
        });

        // Save button event
        saveButton.setOnClickListener(v -> saveExpense());

        // Add category button event
        addCategoryButton.setOnClickListener(v -> openCategoryFragment());

        return view;
    }

    private void openCategoryFragment() {
        CategoryFragment categoryFragment = new CategoryFragment();
        categoryFragment.setOnCategoryAddedListener(this); // Truyền listener vào CategoryFragment
        categoryFragment.show(getParentFragmentManager(), "CategoryFragment");
    }

    @Override
    public void onResume() {
        super.onResume();
        updateCategorySpinner();
    }

    private void switchToExpenseMode() {
        isIncomeMode = false;
        amountLabel.setText("Expense");
        resetTabButtons();
        tabLayout.getTabAt(1).select();
    }

    private void switchToIncomeMode() {
        isIncomeMode = true;
        amountLabel.setText("Income");
        resetTabButtons();
        tabLayout.getTabAt(0).select();
    }

    private void resetTabButtons() {
        // Material TabLayout handles styling automatically
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        new DatePickerDialog(
                getContext(),
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String selectedDate = String.format("%02d/%02d/%d", selectedMonth + 1, selectedDay, selectedYear);
                    dateInput.setText(selectedDate);
                },
                year, month, day
        ).show();
    }

    private void saveExpense() {
        String date = dateInput.getText().toString().trim();
        String note = noteInput.getText().toString().trim();
        String amountStr = amountInput.getText().toString().trim();

        if (date.isEmpty() || date.equals("Select Date")) {
            Toast.makeText(getContext(), "Please select a date", Toast.LENGTH_SHORT).show();
            return;
        }

        if (amountStr.isEmpty() || amountStr.equals("0$") || amountStr.equals("0")) {
            new AlertDialog.Builder(getContext())
                    .setTitle("Confirm")
                    .setMessage("Your balance is 0, do you want to continue?")
                    .setPositiveButton("Yes", (dialog, which) -> saveToDatabase(date, note, amountStr))
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                    .setCancelable(false)
                    .show();
            return;
        }

        if (selectedCategory.isEmpty()) {
            Toast.makeText(getContext(), "Please select a category", Toast.LENGTH_SHORT).show();
            return;
        }

        saveToDatabase(date, note, amountStr);
    }

    private void saveToDatabase(String date, String note, String amountStr) {
        int userId = 1; // Replace with actual user ID
        int amount;
        try {
            amount = Integer.parseInt(amountStr.replace("$", "").trim());
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Invalid amount format", Toast.LENGTH_SHORT).show();
            return;
        }

        int categoryId = getCategoryIdFromName(selectedCategory);
        if (categoryId == -1) {
            Toast.makeText(getContext(), "Invalid category: " + selectedCategory + " not found in database", Toast.LENGTH_LONG).show();
            return;
        }

        try {
            String type = isIncomeMode ? "Income" : "Expense";
            long result = dbHelper.insertExpense(
                    userId,
                    categoryId,
                    note,
                    date,
                    amount,
                    false,
                    null,
                    null,
                    null
            );

            if (result != -1) {
                String summary = type +
                        "\nDate: " + date +
                        "\nNote: " + note +
                        "\nAmount: " + amount +
                        "\nCategory: " + selectedCategory;
                Toast.makeText(getContext(), "Saved:\n" + summary, Toast.LENGTH_LONG).show();
                resetForm();
            } else {
                Toast.makeText(getContext(), "Failed to save to database", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(getContext(), "Error saving expense: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private int getCategoryIdFromName(String categoryName) {
        List<CategoryModel> categories = dbHelper.getAllCategories();
        if (categories.isEmpty()) {
            Toast.makeText(getContext(), "No categories found in database", Toast.LENGTH_LONG).show();
            return -1;
        }
        for (CategoryModel category : categories) {
            if (category.getName().equalsIgnoreCase(categoryName)) {
                return category.getId();
            }
        }
        return -1;
    }

    private void setupCategorySpinner() {
        List<String> categoryNames = new ArrayList<>();
        categoryNames.add("Select Category");
        for (CategoryModel category : dbHelper.getAllCategories()) {
            categoryNames.add(category.getName());
        }

        categoryAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, categoryNames);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);
    }

    private void updateCategorySpinner() {
        List<String> categoryNames = new ArrayList<>();
        categoryNames.add("Select Category");
        for (CategoryModel category : dbHelper.getAllCategories()) {
            categoryNames.add(category.getName());
        }

        categoryAdapter.clear();
        categoryAdapter.addAll(categoryNames);
        categoryAdapter.notifyDataSetChanged();
        categorySpinner.setSelection(0);
    }

    private void resetForm() {
        noteInput.setText("");
        amountInput.setText("");
        dateInput.setText("Select Date");
        selectedCategory = "";
        categorySpinner.setSelection(0);
    }

    @Override
    public void onCategoryAdded(CategoryModel newCategory) {
        categoryAdapter.add(newCategory.getName());
        categoryAdapter.notifyDataSetChanged();
    }
}

// Định nghĩa interface trong cùng file
interface OnCategoryAddedListener {
    void onCategoryAdded(CategoryModel newCategory);
}