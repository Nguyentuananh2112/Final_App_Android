package com.example.finalcampusexpensemanager.fragment;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalcampusexpensemanager.R;
import com.example.finalcampusexpensemanager.db.DatabaseHelper;
import com.example.finalcampusexpensemanager.helper.NotificationHelper;
import com.example.finalcampusexpensemanager.model.CategoryModel;
import com.example.finalcampusexpensemanager.model.ExpenseModel;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.AlertDialog;
import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;

public class HomeFragment extends Fragment {
    private TextView totalBalance, incomeAmount, expenseAmount;
    private MaterialButton dateInput;
    private RecyclerView transactionRecyclerView;
    private TransactionAdapter transactionAdapter;
    private List<ExpenseModel> transactionList;
    private DatabaseHelper dbHelper;
    private int userId;

    public HomeFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        totalBalance = view.findViewById(R.id.total_balance);
        incomeAmount = view.findViewById(R.id.income_amount);
        expenseAmount = view.findViewById(R.id.expense_amount);
        dateInput = view.findViewById(R.id.date_input);
        transactionRecyclerView = view.findViewById(R.id.transaction_recycler_view);

        dbHelper = new DatabaseHelper(getContext());
        if (getArguments() != null) {
            userId = getArguments().getInt("USER_ID", 0);
        }

        transactionList = new ArrayList<>();
        transactionAdapter = new TransactionAdapter(transactionList);
        transactionRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        transactionRecyclerView.setAdapter(transactionAdapter);

        dateInput.setOnClickListener(v -> showDatePicker());

        loadTransactions();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Kiểm tra xem đã hỏi về thông báo chưa
        SharedPreferences prefs = requireContext().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        boolean hasAskedForNotification = prefs.getBoolean("has_asked_for_notification", false);

        if (!hasAskedForNotification) {
            // Yêu cầu quyền thông báo
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
            }
            
            // Lưu trạng thái đã hỏi
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("has_asked_for_notification", true);
            editor.apply();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Quyền được cấp, hiển thị thông báo chào mừng
                NotificationHelper notificationHelper = new NotificationHelper(requireContext());
                notificationHelper.showWelcomeNotification();
                
                // Lưu tùy chọn bật thông báo
                SharedPreferences prefs = requireContext().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("notifications_enabled", true);
                editor.apply();
            } else {
                // Lưu tùy chọn tắt thông báo
                SharedPreferences prefs = requireContext().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("notifications_enabled", false);
                editor.apply();
            }
        }
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

    private void loadTransactions() {
        transactionList.clear();
        transactionList.addAll(dbHelper.getExpensesByUser(userId));
        transactionAdapter.notifyDataSetChanged();
        updateBalance();
    }

    private void updateBalance() {
        int totalIncome = 0;
        int totalExpense = 0;

        for (ExpenseModel transaction : transactionList) {
            if ("Income".equals(transaction.getType())) {
                totalIncome += transaction.getAmount();
            } else if ("Expense".equals(transaction.getType())) {
                totalExpense += transaction.getAmount();
            }
        }

        incomeAmount.setText("$" + totalIncome);
        expenseAmount.setText("$" + totalExpense);
        totalBalance.setText("$" + (totalIncome - totalExpense));
    }

    public void updateTransaction(String type, int amount, String date, String note, String category) {
        loadTransactions(); // Tải lại từ SQLite để đảm bảo đồng bộ
    }

    private int getCategoryId(String categoryName) {
        for (CategoryModel category : dbHelper.getAllCategories()) {
            if (category.getName().equalsIgnoreCase(categoryName)) {
                return category.getId();
            }
        }
        return -1;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadTransactions();
    }


}

class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {
    private List<ExpenseModel> transactions;

    public TransactionAdapter(List<ExpenseModel> transactions) {
        this.transactions = transactions;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_1, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ExpenseModel transaction = transactions.get(position);
        String categoryName = getCategoryName(holder.itemView.getContext(), transaction.getCategoryId());
        String displayText = transaction.getType() + ": $" + transaction.getAmount() +
                " | " + transaction.getDate() + " | " + categoryName +
                " | " + (transaction.getDescription().isEmpty() ? "No note" : transaction.getDescription());
        holder.textView.setText(displayText);
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    private String getCategoryName(Context context, int categoryId) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        for (CategoryModel category : dbHelper.getAllCategories()) {
            if (category.getId() == categoryId) {
                return category.getName();
            }
        }
        return "Unknown";
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        ViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(android.R.id.text1);
        }
    }


}