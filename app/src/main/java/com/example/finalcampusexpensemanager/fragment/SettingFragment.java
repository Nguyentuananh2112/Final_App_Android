package com.example.finalcampusexpensemanager.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.finalcampusexpensemanager.R;
import com.example.finalcampusexpensemanager.SignInActivity;

public class SettingFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);

        Bundle bundle = getActivity().getIntent().getExtras();
        String username = bundle.getString("USERNAME_ACCOUNT", "");
        String email = bundle.getString("USER_EMAIL", "");
        String phone = bundle.getString("USER_PHONE", "");

        TextView tvUsername = view.findViewById(R.id.tv_username);
        TextView tvEmail = view.findViewById(R.id.tv_email);
        TextView tvPhone = view.findViewById(R.id.tv_phone);
        Button btnLogout = view.findViewById(R.id.btn_logout);

        tvUsername.setText("Username: " + username);
        tvEmail.setText("Email: " + email);
        tvPhone.setText("Phone: " + phone);

        btnLogout.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), SignInActivity.class);
            startActivity(intent);
            getActivity().finish();
        });

        return view;
    }
}