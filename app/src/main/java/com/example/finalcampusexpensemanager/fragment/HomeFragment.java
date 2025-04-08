package com.example.finalcampusexpensemanager.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
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
import java.util.List;

public class HomeFragment extends Fragment {
    private TextView totalBalance, incomeAmount, expenseAmount;
    private RecyclerView transactionRecyclerView;
    private TransactionAdapter transactionAdapter;
    private List<ExpenseModel> transactionList;
    private DatabaseHelper dbHelper;
    private int userId;
    private MaterialButton seeAllButton;

    public HomeFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        totalBalance = view.findViewById(R.id.total_balance);
        incomeAmount = view.findViewById(R.id.income_amount);
        expenseAmount = view.findViewById(R.id.expense_amount);
        transactionRecyclerView = view.findViewById(R.id.transaction_recycler_view);
        seeAllButton = view.findViewById(R.id.see_all_button);

        dbHelper = new DatabaseHelper(getContext());
        if (getArguments() != null) {
            userId = getArguments().getInt("USER_ID", 0);
        }

        transactionList = new ArrayList<>();
        transactionAdapter = new TransactionAdapter(transactionList);
        transactionRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        transactionRecyclerView.setAdapter(transactionAdapter);
        seeAllButton = view.findViewById(R.id.see_all_button);

        seeAllButton.setOnClickListener(v -> openTransactionHistory());
        loadTransactions();

        return view;
    }

    private void openTransactionHistory() {
        TransactionHistoryFragment fragment = TransactionHistoryFragment.newInstance(userId);
        fragment.show(getParentFragmentManager(), "TransactionHistoryFragment");
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Yêu cầu quyền thông báo của hệ thống
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 1);
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
            }
        }
    }

//    private void loadTransactions() {
//        transactionList.clear();
//        transactionList.addAll(dbHelper.getExpensesByUser(userId));
//        transactionAdapter.notifyDataSetChanged();
//        updateBalance();
//    }

    public void loadTransactions() {
        // Làm mới danh sách giao dịch (5 giao dịch gần nhất)
        transactionList.clear();
        List<ExpenseModel> allTransactions = dbHelper.getExpensesByUser(userId);
        int limit = Math.min(allTransactions.size(), 5);
        transactionList.addAll(allTransactions.subList(0, limit));
        transactionAdapter.notifyDataSetChanged();

        // Cập nhật các con số Total Balance, Income, Expense
        updateBalance();
    }

    private void updateBalance() {
        int totalIncome = 0;
        int totalExpense = 0;

        for (ExpenseModel transaction : dbHelper.getExpensesByUser(userId)) {
            if ("Income".equals(transaction.getType())) {
                totalIncome += transaction.getAmount();
            } else if ("Expense".equals(transaction.getType())) {
                totalExpense += transaction.getAmount();
            }
        }

        // Cập nhật hiển thị số tiền
        incomeAmount.setText(String.format("%,d VND", totalIncome));
        expenseAmount.setText(String.format("%,d VND", totalExpense));
        totalBalance.setText(String.format("%,d VND", (totalIncome - totalExpense)));

        // Kiểm tra và hiển thị thông báo nếu chi tiêu vượt quá thu nhập
        if (totalExpense > totalIncome) {
            SharedPreferences prefs = requireContext().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
            boolean notificationsEnabled = prefs.getBoolean("notifications_enabled", false);
            
            if (notificationsEnabled) {
                NotificationHelper notificationHelper = new NotificationHelper(requireContext());
                notificationHelper.showBudgetWarningNotification(totalIncome, totalExpense);
            }
        }
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


    private void showWelcomeNotification() {
        SharedPreferences prefs = requireContext().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        boolean notificationsEnabled = prefs.getBoolean("notifications_enabled", false);
        
        if (notificationsEnabled) {
            NotificationHelper notificationHelper = new NotificationHelper(requireContext());
            notificationHelper.showWelcomeNotification();
        }
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