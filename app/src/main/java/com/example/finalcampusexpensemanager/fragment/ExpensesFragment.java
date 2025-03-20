package com.example.finalcampusexpensemanager.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalcampusexpensemanager.R;
import com.example.finalcampusexpensemanager.db.DatabaseHelper;
import com.example.finalcampusexpensemanager.model.ExpenseModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ExpensesFragment extends Fragment {
    private DatabaseHelper dbHelper;
    private int userId;
    private RecyclerView recyclerView;
    private ExpenseAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_expenses, container, false);

        dbHelper = new DatabaseHelper(getContext());
        // Lấy user_id từ Bundle
        if (getArguments() != null) {
            userId = getArguments().getInt("USER_ID", 0);
        }

        recyclerView = view.findViewById(R.id.rv_expenses);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        // Thêm DividerItemDecoration ngay sau khi thiết lập LayoutManager
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        loadExpenses();

        EditText etDescription = view.findViewById(R.id.et_description);
        EditText etAmount = view.findViewById(R.id.et_amount);
        Button btnAddExpense = view.findViewById(R.id.btn_add_expense);

        btnAddExpense.setOnClickListener(v -> {
            String description = etDescription.getText().toString().trim();
            String amountStr = etAmount.getText().toString().trim();
            if (description.isEmpty() || amountStr.isEmpty()) {
                Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }
            int amount = (int) (Double.parseDouble(amountStr) * 100); // Convert to cents
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String date = sdf.format(Calendar.getInstance().getTime());

            long result = dbHelper.insertExpense(userId, 1, description, date, amount, false, null, null, null);
            if (result != -1) {
                Toast.makeText(getContext(), "Expense added", Toast.LENGTH_SHORT).show();
                etDescription.setText("");
                etAmount.setText("");
                loadExpenses();
            } else {
                Toast.makeText(getContext(), "Failed to add expense", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void loadExpenses() {
        List<ExpenseModel> expenses = dbHelper.getExpensesByUser(userId);
        adapter = new ExpenseAdapter(expenses);
        recyclerView.setAdapter(adapter);
    }

    private static class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ViewHolder> {
        private List<ExpenseModel> expenses;

        public ExpenseAdapter(List<ExpenseModel> expenses) {
            this.expenses = expenses;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.expense_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            ExpenseModel expense = expenses.get(position);
            holder.textView.setText(expense.getDescription() + ": $" + (expense.getAmount() / 100.0));
        }

        @Override
        public int getItemCount() {
            return expenses.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView textView;

            ViewHolder(View itemView) {
                super(itemView);
                textView = itemView.findViewById(android.R.id.text1);
            }
        }
    }
}