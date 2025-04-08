package com.example.finalcampusexpensemanager.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalcampusexpensemanager.R;
import com.example.finalcampusexpensemanager.db.DatabaseHelper;
import com.example.finalcampusexpensemanager.model.CategoryModel;
import com.example.finalcampusexpensemanager.model.ExpenseModel;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class TransactionHistoryFragment extends DialogFragment {
    private RecyclerView transactionRecyclerView;
    private TransactionHistoryAdapter transactionAdapter;
    private List<ExpenseModel> transactionList;
    private DatabaseHelper dbHelper;
    private MaterialButton backButton;
    private int userId;

    public TransactionHistoryFragment() {}

    public static TransactionHistoryFragment newInstance(int userId) {
        TransactionHistoryFragment fragment = new TransactionHistoryFragment();
        Bundle args = new Bundle();
        args.putInt("USER_ID", userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userId = getArguments().getInt("USER_ID", 0);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_transaction_history, container, false);

        transactionRecyclerView = view.findViewById(R.id.transaction_history_recycler_view);
        backButton = view.findViewById(R.id.back_button);
        dbHelper = new DatabaseHelper(getContext());

        transactionList = new ArrayList<>();
        transactionAdapter = new TransactionHistoryAdapter(transactionList, this::deleteTransaction);
        transactionRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        transactionRecyclerView.setAdapter(transactionAdapter);

        backButton.setOnClickListener(v -> dismiss());
        loadTransactions();

        return view;
    }

    private void loadTransactions() {
        transactionList.clear();
        transactionList.addAll(dbHelper.getExpensesByUser(userId));
        transactionAdapter.notifyDataSetChanged();
    }

    private void deleteTransaction(int transactionId) {
        int result = dbHelper.deleteExpense(transactionId);
        if (result > 0) {
            Toast.makeText(getContext(), "Transaction deleted", Toast.LENGTH_SHORT).show();
            loadTransactions(); // Làm mới danh sách trong TransactionHistoryFragment
            notifyHomeFragment(); // Thông báo HomeFragment cập nhật
        } else {
            Toast.makeText(getContext(), "Failed to delete transaction", Toast.LENGTH_SHORT).show();
        }
    }

    private void notifyHomeFragment() {
        HomeFragment homeFragment = (HomeFragment) getParentFragmentManager().findFragmentByTag("HomeFragment");
        if (homeFragment != null) {
            homeFragment.loadTransactions(); // Gọi loadTransactions để cập nhật cả danh sách và số liệu
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }
}

class TransactionHistoryAdapter extends RecyclerView.Adapter<TransactionHistoryAdapter.ViewHolder> {
    private List<ExpenseModel> transactions;
    private final OnDeleteClickListener deleteClickListener;

    interface OnDeleteClickListener {
        void onDeleteClick(int transactionId);
    }

    public TransactionHistoryAdapter(List<ExpenseModel> transactions, OnDeleteClickListener deleteClickListener) {
        this.transactions = transactions;
        this.deleteClickListener = deleteClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_transaction_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ExpenseModel transaction = transactions.get(position);
        String categoryName = getCategoryName(holder.itemView.getContext(), transaction.getCategoryId());
        String displayText = transaction.getType() + ": $" + transaction.getAmount() +
                " | " + transaction.getDate() + " | " + categoryName +
                " | " + (transaction.getDescription().isEmpty() ? "No note" : transaction.getDescription());
        holder.textView.setText(displayText);
        holder.deleteButton.setOnClickListener(v -> deleteClickListener.onDeleteClick(transaction.getId()));
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
        Button deleteButton;

        ViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.transaction_text);
            deleteButton = itemView.findViewById(R.id.delete_button);
        }
    }
}