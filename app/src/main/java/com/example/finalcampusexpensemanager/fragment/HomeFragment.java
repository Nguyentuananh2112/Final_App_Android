package com.example.finalcampusexpensemanager.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.finalcampusexpensemanager.R;
import com.example.finalcampusexpensemanager.db.DatabaseHelper;
import com.example.finalcampusexpensemanager.model.BudgetModel;
import com.example.finalcampusexpensemanager.model.ExpenseModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {
    private DatabaseHelper dbHelper;
    private int userId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        dbHelper = new DatabaseHelper(getContext());
        userId = getActivity().getIntent().getExtras().getInt("USER_ID", 0);

//        TextView tvTotalExpenses = view.findViewById(R.id.tv_total_expenses);
//        TextView tvTotalBudget = view.findViewById(R.id.tv_total_budget);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM", Locale.getDefault());
        String currentMonth = sdf.format(Calendar.getInstance().getTime());

        List<ExpenseModel> expenses = dbHelper.getExpensesByUser(userId);
        List<BudgetModel> budgets = dbHelper.getBudgetsByUser(userId);

        int totalExpenses = 0;
        for (ExpenseModel expense : expenses) {
            if (expense.getDate().startsWith(currentMonth)) {
                totalExpenses += expense.getAmount();
            }
        }

        int totalBudget = 0;
        for (BudgetModel budget : budgets) {
            if (budget.getMonth().equals(currentMonth)) {
                totalBudget += budget.getBudgetAmount();
            }
        }

//        tvTotalExpenses.setText("Total Expenses: $" + (totalExpenses / 100.0));
//        tvTotalBudget.setText("Total Budget: $" + (totalBudget / 100.0));

        return view;
    }
}