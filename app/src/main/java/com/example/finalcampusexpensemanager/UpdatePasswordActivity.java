package com.example.finalcampusexpensemanager;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.finalcampusexpensemanager.db.UserDb;
import com.example.finalcampusexpensemanager.utils.HashUtil;
import com.google.android.material.textfield.TextInputEditText;

public class UpdatePasswordActivity extends AppCompatActivity {
    TextInputEditText edtNewPassword, edtConfirmNewPassword;
    Button btnSaveChange, btnCancel;

    Intent intent;
    Bundle bundle;
    private int idUser = 0;
    UserDb userDb;

    private boolean isStrongPassword(String password) {
        return password.length() >= 8 &&
                password.matches(".*[a-z].*") && // it nhat 1 chu cai thuong
                password.matches(".*[A-Z].*") && // it nhat 1 chu cai in hoa
                password.matches(".*\\d.*") && // it nhat 1 so
                password.matches(".*[!@#$%^&*()].*"); // it nhat 1 ky tu dac biet
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_update_password);

        edtNewPassword = findViewById(R.id.edtNewPassword);
        edtConfirmNewPassword = findViewById(R.id.edtConfirmNewPassword);
        btnSaveChange = findViewById(R.id.btnSaveChangePassword);
        btnCancel = findViewById(R.id.btnCancel);
        intent = getIntent();
        bundle = intent.getExtras();

        if (bundle != null){
            idUser = bundle.getInt("ID_ACCOUNT_USER", 0);
        }
        userDb = new UserDb(UpdatePasswordActivity.this);

        btnSaveChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = edtNewPassword.getText().toString().trim();
                String confirmPassword = edtConfirmNewPassword.getText().toString().trim();

                if (TextUtils.isEmpty(password)){
                    edtNewPassword.setError("New password can not empty");
                    return;
                }
                // Kiem tra do manh cua password, neu password hop le thi moi cho nhap email
                if (!isStrongPassword(password)){
                    edtNewPassword.setError("Password must have at least 8 characters, " +
                            "including uppercase, lowercase, number, and special character");
                    return;
                }

                if (TextUtils.isEmpty(confirmPassword)){
                    edtNewPassword.setError("New confirm password can not empty");
                    return;
                }
                if (!confirmPassword.equals(password)){
                    edtNewPassword.setError("New confirm password is not same password");
                    return;
                }

                // Băm mật khẩu trước khi lưu
                String hashedPassword = HashUtil.hashPassword(password);
                int update = userDb.updateAccountPassword(idUser, hashedPassword);// Sử dụng hashedPassword

                if (update == -1){
                    // fail
                    Toast.makeText(UpdatePasswordActivity.this, "Error, Try again", Toast.LENGTH_SHORT).show();
                }
                else {
                    //success
                    Toast.makeText(UpdatePasswordActivity.this, "Update password success", Toast.LENGTH_SHORT).show();

                    // sau khi success thi se quay ve login
                    Intent login = new Intent(UpdatePasswordActivity.this, SignInActivity.class);
                    startActivity(login);
                    finish();
                }
            }
        });


        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UpdatePasswordActivity.this, SignInActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}

