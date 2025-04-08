package com.example.finalcampusexpensemanager.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalcampusexpensemanager.R;
import com.example.finalcampusexpensemanager.db.DatabaseHelper;
import com.example.finalcampusexpensemanager.model.ExpenseModel;

import java.util.List;

public class TransactionDetailAdapter extends RecyclerView.Adapter<TransactionDetailAdapter.TransactionViewHolder> {
    private List<ExpenseModel> transactionList;
    private DatabaseHelper dbHelper;

    public TransactionDetailAdapter(List<ExpenseModel> transactionList, DatabaseHelper dbHelper) {
        this.transactionList = transactionList;
        this.dbHelper = dbHelper;
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaction_detail, parent, false);
        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        ExpenseModel transaction = transactionList.get(position);
        String categoryName = dbHelper.getCategoryName(transaction.getCategoryId());
        holder.tvTransactionType.setText(transaction.getType());
        holder.tvTransactionAmount.setText("Amount: $" + transaction.getAmount());
        holder.tvTransactionDate.setText("Date: " + transaction.getDate());
        holder.tvTransactionCategory.setText("Category: " + categoryName);
        holder.tvTransactionDescription.setText("Note: " + (transaction.getDescription() != null && !transaction.getDescription().isEmpty() ? transaction.getDescription() : "No note"));
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    static class TransactionViewHolder extends RecyclerView.ViewHolder {
        TextView tvTransactionType, tvTransactionAmount, tvTransactionDate, tvTransactionCategory, tvTransactionDescription;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTransactionType = itemView.findViewById(R.id.tv_transaction_type);
            tvTransactionAmount = itemView.findViewById(R.id.tv_transaction_amount);
            tvTransactionDate = itemView.findViewById(R.id.tv_transaction_date);
            tvTransactionCategory = itemView.findViewById(R.id.tv_transaction_category);
            tvTransactionDescription = itemView.findViewById(R.id.tv_transaction_description);
        }
    }
}