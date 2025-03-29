package com.example.finalcampusexpensemanager.adapter;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.finalcampusexpensemanager.fragment.BudgetFragment;
import com.example.finalcampusexpensemanager.fragment.CategoryManageFragment;
import com.example.finalcampusexpensemanager.fragment.ExpensesFragment;
import com.example.finalcampusexpensemanager.fragment.HomeFragment;
import com.example.finalcampusexpensemanager.fragment.ReportFragment;
import com.example.finalcampusexpensemanager.fragment.SettingFragment;

public class ViewPagerAdapter extends FragmentStateAdapter {

    int userId;
    public ViewPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
    @NonNull
    @Override
    public Fragment createFragment(int position) {

//        if (position == 0){
//            return new HomeFragment();
//        } else if (position == 1) {
//            return new ExpensesFragment();
//        } else if (position == 2) {
//            return new CategoryManageFragment();
//        } else if (position == 3) {
//            return  new SettingFragment();
//        } else {
//            return new HomeFragment();

        Bundle bundle = new Bundle();
        bundle.putInt("USER_ID", userId);

        switch (position) {
            case 0:
                HomeFragment homeFragment = new HomeFragment();
                homeFragment.setArguments(bundle);
                return homeFragment;
            case 1:
                ExpensesFragment expensesFragment = new ExpensesFragment();
                expensesFragment.setArguments(bundle);
                return expensesFragment;
            case 2:
                CategoryManageFragment categoryManageFragment = new CategoryManageFragment();
                categoryManageFragment.setArguments(bundle);
                return categoryManageFragment;

            case 3:
                BudgetFragment budgetFragment = new BudgetFragment();
                budgetFragment.setArguments(bundle);
                return budgetFragment;
            case 4:
                SettingFragment settingFragment = new SettingFragment();
                settingFragment.setArguments(bundle);
                return settingFragment;
            default:
                HomeFragment defaultFragment = new HomeFragment();
                defaultFragment.setArguments(bundle);
                return defaultFragment;

        }
    }

    @Override
    public int getItemCount() {
        return 5;
    }
}
