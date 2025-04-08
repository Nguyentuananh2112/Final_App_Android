package com.example.finalcampusexpensemanager.fragment;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.finalcampusexpensemanager.DashboardActivity;
import com.example.finalcampusexpensemanager.R;
import com.example.finalcampusexpensemanager.db.DatabaseHelper;
import com.example.finalcampusexpensemanager.helper.NotificationHelper;
import com.example.finalcampusexpensemanager.model.CategoryModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ExpensesFragment extends Fragment implements OnCategoryAddedListener {
    private TabLayout tabLayout;
    private MaterialButton dateInput, saveButton, addCategoryButton;
    private TextInputEditText noteInput, amountInput;
    private int userId;
    private Spinner categorySpinner;
    private String selectedCategory = "";
    private boolean isIncomeMode = true;
    private DatabaseHelper dbHelper;
    private ArrayAdapter<String> categoryAdapter;

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

        double amount;
        try {
            amount = Double.parseDouble(amountStr);
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
        
        // Kiểm tra nếu là chi tiêu và vượt quá thu nhập
        if (!isIncomeMode) {
            double totalIncome = dbHelper.getTotalIncome(userId);
            double totalExpense = dbHelper.getTotalExpense(userId);
            double newTotalExpense = totalExpense + amount;
            
            if (newTotalExpense > totalIncome) {
                showBudgetExceededDialog(totalIncome, totalExpense, newTotalExpense);
                return;
            }
        }
        
        saveTransactionToDatabase(userId, categoryId, note, date, amount, type);
    }

    private void showBudgetExceededDialog(double totalIncome, double totalExpense, double newTotalExpense) {
        // Debug log
        System.out.println("Total Income: " + totalIncome);
        System.out.println("Total Expense: " + totalExpense);
        System.out.println("New Total Expense: " + newTotalExpense);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("CẢNH BÁO CHI TIÊU")
                .setMessage(String.format(
                    "Tổng thu nhập: %,.0f VND\n" +
                    "Tổng chi tiêu hiện tại: %,.0f VND\n" +
                    "Chi tiêu mới: %,.0f VND\n" +
                    "Tổng chi tiêu sau khi thêm: %,.0f VND\n\n" +
                    "Bạn có đồng ý với mức chi tiêu này không?",
                    totalIncome, 
                    totalExpense,
                    Double.parseDouble(amountInput.getText().toString()),
                    newTotalExpense
                ))
                .setPositiveButton("Có", (dialog, which) -> {
                    String date = dateInput.getText().toString().trim();
                    String note = noteInput.getText().toString().trim();
                    double amount = Double.parseDouble(amountInput.getText().toString().trim());
                    int categoryId = getCategoryIdFromName(selectedCategory);
                    String type = "Expense";
                    
                    // Lưu giao dịch và thêm vào lịch sử cảnh báo
                    long result = dbHelper.insertExpense(userId, categoryId, note, date, (int) amount, false, null, null, null, type);
                    if (result != -1) {
                        // Thêm vào lịch sử cảnh báo
                        dbHelper.addBudgetWarning(userId, selectedCategory, amount, totalIncome);
                        
                        // Kiểm tra và hiển thị thông báo hệ thống
                        SharedPreferences prefs = requireContext().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
                        boolean notificationsEnabled = prefs.getBoolean("notifications_enabled", false);
                        
                        if (notificationsEnabled) {
                            NotificationHelper notificationHelper = new NotificationHelper(requireContext());
                            // Hiển thị thông báo phần trăm vượt mức
                            notificationHelper.showBudgetExceededPercentageNotification(totalIncome, totalExpense, amount);
                        }
                        
                        // Reset form và cập nhật HomeFragment
                        resetForm();
                        notifyHomeFragment(type, amount, date, note, selectedCategory);
                    } else {
                        Toast.makeText(getContext(), "Failed to save Expense", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Không", null)
                .show();
    }

    private void saveTransactionToDatabase(int userId, int categoryId, String note, String date, double amount, String type) {
        long result = dbHelper.insertExpense(userId, categoryId, note, date, (int) amount, false, null, null, null, type);

        if (result != -1) {
            // Kiểm tra xem người dùng có bật thông báo không
            SharedPreferences prefs = requireContext().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
            boolean notificationsEnabled = prefs.getBoolean("notifications_enabled", false);

            if (notificationsEnabled) {
                // Lấy tổng thu nhập và chi tiêu
                double totalIncome = dbHelper.getTotalIncome(userId);
                double totalExpense = dbHelper.getTotalExpense(userId);

                // Hiển thị thông báo cho cả thu nhập và chi tiêu
                NotificationHelper notificationHelper = new NotificationHelper(requireContext());
                
                if (type.equals("Expense") && totalExpense > totalIncome) {
                    // Nếu là chi tiêu và vượt quá thu nhập, hiển thị cảnh báo
                    notificationHelper.showBudgetWarningNotification(totalIncome, totalExpense);
                } else {
                    // Hiển thị thông báo thêm giao dịch thành công cho cả thu nhập và chi tiêu
                    notificationHelper.showTransactionNotification(type, amount, selectedCategory);
                }
            }
            
            // Reset form và cập nhật HomeFragment
            resetForm();
            notifyHomeFragment(type, amount, date, note, selectedCategory);
        } else {
            Toast.makeText(getContext(), "Lưu thất bại", Toast.LENGTH_SHORT).show();
        }
    }

    private void notifyHomeFragment(String type, double amount, String date, String note, String category) {
        HomeFragment homeFragment = (HomeFragment) getParentFragmentManager().findFragmentByTag("HomeFragment");
        if (homeFragment != null) {
            homeFragment.updateTransaction(type, (int) amount, date, note, category);
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
        List<String> categoryNames = new ArrayList<>();
        categoryNames.add("Select Category");
        for (CategoryModel category : dbHelper.getAllCategories()) {
            categoryNames.add(category.getName());
        }
        categoryAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, categoryNames);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);
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
        categoryFragment.setOnCategoryAddedListener(this);
        categoryFragment.show(getParentFragmentManager(), "CategoryFragment");
    }

    @Override
    public void onCategoryAdded(CategoryModel newCategory) {
        categoryAdapter.add(newCategory.getName());
        categoryAdapter.notifyDataSetChanged();
    }
}

interface OnCategoryAddedListener {
    void onCategoryAdded(CategoryModel newCategory);
}