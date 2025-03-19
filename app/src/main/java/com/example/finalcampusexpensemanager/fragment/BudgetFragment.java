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
import com.example.finalcampusexpensemanager.model.BudgetModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class BudgetFragment extends Fragment {
    private DatabaseHelper dbHelper;
    private int userId;
    private RecyclerView recyclerView;
    private BudgetAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_budget, container, false);

        dbHelper = new DatabaseHelper(getContext());
        userId = getActivity().getIntent().getExtras().getInt("USER_ID", 0);

        recyclerView = view.findViewById(R.id.rv_budgets);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        // Thêm DividerItemDecoration ngay sau khi thiết lập LayoutManager
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        loadBudgets();

        EditText etBudgetAmount = view.findViewById(R.id.et_budget_amount);
        Button btnAddBudget = view.findViewById(R.id.btn_add_budget);

        btnAddBudget.setOnClickListener(v -> {
            String amountStr = etBudgetAmount.getText().toString().trim();
            if (amountStr.isEmpty()) {
                Toast.makeText(getContext(), "Please enter budget amount", Toast.LENGTH_SHORT).show();
                return;
            }
            int amount = (int) (Double.parseDouble(amountStr) * 100); // Convert to cents
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM", Locale.getDefault());
            String month = sdf.format(Calendar.getInstance().getTime());

            long result = dbHelper.insertBudget(userId, 1, month, amount);
            if (result != -1) {
                Toast.makeText(getContext(), "Budget added", Toast.LENGTH_SHORT).show();
                etBudgetAmount.setText("");
                loadBudgets();
            } else {
                Toast.makeText(getContext(), "Failed to add budget", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void loadBudgets() {
        List<BudgetModel> budgets = dbHelper.getBudgetsByUser(userId);
        adapter = new BudgetAdapter(budgets);
        recyclerView.setAdapter(adapter);
    }

    private static class BudgetAdapter extends RecyclerView.Adapter<BudgetAdapter.ViewHolder> {
        private List<BudgetModel> budgets;

        public BudgetAdapter(List<BudgetModel> budgets) {
            this.budgets = budgets;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.expense_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            BudgetModel budget = budgets.get(position);
            holder.textView.setText(budget.getMonth() + ": $" + (budget.getBudgetAmount() / 100.0));
        }

        @Override
        public int getItemCount() {
            return budgets.size();
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