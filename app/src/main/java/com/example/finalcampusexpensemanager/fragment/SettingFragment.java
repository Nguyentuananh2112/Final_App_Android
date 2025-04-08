package com.example.finalcampusexpensemanager.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
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

        // ánh xạ các thành phần trong layout
        TextView tvUsername = view.findViewById(R.id.tv_username);
        TextView tvEmail = view.findViewById(R.id.tv_email);
        TextView tvPhone = view.findViewById(R.id.tv_phone);
        Button btnLogout = view.findViewById(R.id.btn_logout);

        //hiểm thị in4 user lên giao diện
        tvUsername.setText("Username: " + username);
        tvEmail.setText("Email: " + email);
        tvPhone.setText("Phone: " + phone);


        btnLogout.setOnClickListener(v -> {
            // hiển thị tb có muốn logout hay k
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Logout"); // tieu de cua hop thoai
            builder.setMessage("Are you sure you want to logout ?");// Noi dung cua thong bao


            // Nut ok de logout
            builder.setPositiveButton("OK", new  DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    // nhan ok thi chuyen sang man hinh login
                    Intent intent = new Intent(getActivity(), SignInActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                }
            });

            // nut cancel
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) { // int which xác định nút nào được nhấn trong thong bao
                    dialog.dismiss();// muc dich la de dong thong bao
                }
            });
            // hien thi thong bao xac nhan
            builder.create().show();

        });
        return view;
    }
}