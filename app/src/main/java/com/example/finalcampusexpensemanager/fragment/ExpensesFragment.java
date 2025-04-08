package com.example.finalcampusexpensemanager.fragment;

import android.annotation.SuppressLint;
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
import androidx.viewpager2.widget.ViewPager2;

import com.example.finalcampusexpensemanager.OnCategoryChangeListener;
import com.example.finalcampusexpensemanager.R;
import com.example.finalcampusexpensemanager.db.DatabaseHelper;
import com.example.finalcampusexpensemanager.model.CategoryModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ExpensesFragment extends Fragment implements OnCategoryChangeListener {
    private TabLayout tabLayout;
    private MaterialButton dateInput, saveButton, addCategoryButton;
    private TextInputEditText noteInput, amountInput;
    private int userId;
    private Spinner categorySpinner;
    private String selectedCategory = "";
    private boolean isIncomeMode = true;
    private DatabaseHelper dbHelper;
    private ArrayAdapter<String> categoryAdapter;
    private List<String> categoryNames;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_expenses, container, false);

        tabLayout = view.findViewById(R.id.tab_layout);
        dateInput = view.findViewById(R.id.date_input);
        noteInput = view.findViewById(R.id.note_input);
        amountInput = view.findViewById(R.id.amount_input);
        categorySpinner = view.findViewById(R.id.category_spinner);
        saveButton = view.findViewById(R.id.btn_save_button);
        addCategoryButton = view.findViewById(R.id.btn_add_category);

        dbHelper = new DatabaseHelper(getContext());

        if (getArguments() != null) {
            userId = getArguments().getInt("USER_ID", 0);
        }

        setupCategorySpinner();

        tabLayout.getTabAt(0).select();
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                isIncomeMode = tab.getPosition() == 0;
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        dateInput.setOnClickListener(v -> showDatePicker());

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
                selectedCategory = selectedItem.equals("Select Category") ? "" : selectedItem;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedCategory = "";
            }
        });

        saveButton.setOnClickListener(v -> saveTransaction());

        addCategoryButton.setOnClickListener(v -> openCategoryFragment());

        return view;
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        new DatePickerDialog(getContext(), (view, selectedYear, selectedMonth, selectedDay) -> {
            String selectedDate = String.format("%02d/%02d/%d", selectedMonth + 1, selectedDay, selectedYear);
            dateInput.setText(selectedDate);
        }, year, month, day).show();
    }

    private void saveTransaction() {
        String date = dateInput.getText().toString().trim();
        String note = noteInput.getText().toString().trim();
        String amountStr = amountInput.getText().toString().trim();

        if (date.isEmpty() || date.equals("Select Date")) {
            Toast.makeText(getContext(), "Please select a date", Toast.LENGTH_SHORT).show();
            return;
        }
        if (amountStr.isEmpty()) {
            Toast.makeText(getContext(), "Please enter an amount", Toast.LENGTH_SHORT).show();
            return;
        }
        if (selectedCategory.isEmpty()) {
            Toast.makeText(getContext(), "Please select a category", Toast.LENGTH_SHORT).show();
            return;
        }

        int amount;
        try {
            amount = Integer.parseInt(amountStr);
            if (amount < 0) {
                Toast.makeText(getContext(), "Amount must be non-negative", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Invalid amount format", Toast.LENGTH_SHORT).show();
            return;
        }

        int categoryId = getCategoryIdFromName(selectedCategory);
        if (categoryId == -1) {
            Toast.makeText(getContext(), "Invalid category", Toast.LENGTH_SHORT).show();
            return;
        }

        String type = isIncomeMode ? "Income" : "Expense";
        long result = dbHelper.insertExpense(userId, categoryId, note, date, amount, false, null, null, null, type);

        if (result != -1) {
            Toast.makeText(getContext(), type + " saved successfully", Toast.LENGTH_SHORT).show();
            resetForm();
            notifyHomeFragment(type, amount, date, note, selectedCategory);
        } else {
            Toast.makeText(getContext(), "Failed to save " + type, Toast.LENGTH_SHORT).show();
        }
    }

    private void notifyHomeFragment(String type, int amount, String date, String note, String category) {
        HomeFragment homeFragment = (HomeFragment) getParentFragmentManager().findFragmentByTag("HomeFragment");
        if (homeFragment != null) {
            homeFragment.updateTransaction(type, amount, date, note, category);
        }
        requireActivity().findViewById(R.id.viewPager).post(() -> {
            ViewPager2 viewPager = requireActivity().findViewById(R.id.viewPager);
            viewPager.setCurrentItem(0);
        });
    }

    private int getCategoryIdFromName(String categoryName) {
        for (CategoryModel category : dbHelper.getAllCategories()) {
            if (category.getName().equalsIgnoreCase(categoryName)) {
                return category.getId();
            }
        }
        return -1;
    }

    private void setupCategorySpinner() {
        categoryNames = new ArrayList<>();
        categoryNames.add("Select Category");
        for (CategoryModel category : dbHelper.getAllCategories()) {
            categoryNames.add(category.getName());
        }
        categoryAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, categoryNames);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);
    }

    private void refreshCategorySpinner() {
        categoryNames.clear();
        categoryNames.add("Select Category");
        for (CategoryModel category : dbHelper.getAllCategories()) {
            categoryNames.add(category.getName());
        }
        categoryAdapter.notifyDataSetChanged();

        if (!categoryNames.contains(selectedCategory)) {
            selectedCategory = "";
            categorySpinner.setSelection(0);
        }
    }

    private void resetForm() {
        noteInput.setText("");
        amountInput.setText("");
        dateInput.setText("Select Date");
        categorySpinner.setSelection(0);
        selectedCategory = "";
    }

    private void openCategoryFragment() {
        CategoryFragment categoryFragment = new CategoryFragment();
        categoryFragment.setOnCategoryChangeListener(this);
        categoryFragment.show(getParentFragmentManager(), "CategoryFragment");
    }

    @Override
    public void onCategoryAdded(CategoryModel newCategory) {
        categoryNames.add(newCategory.getName());
        categoryAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCategoryEdited(CategoryModel editedCategory) {
        for (int i = 0; i < categoryNames.size(); i++) {
            if (getCategoryIdFromName(categoryNames.get(i)) == editedCategory.getId()) {
                categoryNames.set(i, editedCategory.getName());
                break;
            }
        }
        categoryAdapter.notifyDataSetChanged();

        if (selectedCategory.equalsIgnoreCase(editedCategory.getName())) {
            selectedCategory = editedCategory.getName();
        }
    }

    @Override
    public void onCategoryDeleted(int categoryId) {
        refreshCategorySpinner();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Làm mới Spinner khi fragment được quay lại
        refreshCategorySpinner();
    }
}