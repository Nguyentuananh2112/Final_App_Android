package com.example.finalcampusexpensemanager;

import android.content.Context;
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
import com.google.android.material.textfield.TextInputEditText;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;

public class SignUpActivity extends AppCompatActivity {

    TextInputEditText edtUsername, edtPassword, edtEmail, edtPhone;
    Button btnRegister, btnCancel;
    UserDb userDb;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        userDb = new UserDb(SignUpActivity.this);
        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        edtEmail = findViewById(R.id.edtEmail);
        edtPhone = findViewById(R.id.edtPhone);
        btnRegister = findViewById(R.id.btnRegister);
        btnCancel = findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                startActivity(intent);
                finish();
            }
        });
        registerUser();
    }


    private void registerUser(){
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = edtUsername.getText().toString().trim();
                if (TextUtils.isEmpty(username)){
                    edtUsername.setError("Username not empty");
                    return;
                }
                String password = edtPassword.getText().toString().trim();
                if (TextUtils.isEmpty(password)){
                    edtPassword.setError("Password not empty");
                    return;
                }
                String email = edtEmail.getText().toString().trim();
                if (TextUtils.isEmpty(email)){
                    edtEmail.setError("Email not empty");
                    return;
                }
                String phoneNumber = edtPhone.getText().toString().trim();
                if (TextUtils.isEmpty(phoneNumber)){
                    edtPhone.setError("Phone not empty");
                    return;
                }

                // check tk da dky hay ch
                boolean checkUsername = userDb.checkUsernameExists(username);
                if (checkUsername){
                    edtUsername.setError("Username Already Exists");
                    return;
                }

                long insertUser = userDb.insertUserToDatabase(username, password, email, phoneNumber);
                if (insertUser == -1){
                    // Fail
                    Toast.makeText(SignUpActivity.this, "Error: Register Account Fail", Toast.LENGTH_SHORT).show();
                }else {
                    // Success
                    Toast.makeText(SignUpActivity.this, "Register Account Success", Toast.LENGTH_SHORT).show();
                    // return login page
                    Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                    startActivity(intent);

                }
            }
        });
    }



    // sigUp nay la de luu vao file trong local storage
    // Bay gio k dung den
    private void signUp(){
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = edtUsername.getText().toString().trim();
                String password = edtPassword.getText().toString().trim();
                if (TextUtils.isEmpty(user)){
                    edtUsername.setError("Username not empty");
                    return;
                }
                if (TextUtils.isEmpty(user)){
                    edtPassword.setError("Password not empty");
                    return;
                }


                // Lưu data user vào local storage
                FileOutputStream fileOutputStream = null;
                try {
                    user += "|"; //lối chuỗi ngăn cách giữa tk và mk

                    // Context.MODE_APPEND ghi nối ngay đằng sau
                    fileOutputStream = openFileOutput("account.txt", Context.MODE_APPEND);

                    fileOutputStream.write(user.getBytes(StandardCharsets.UTF_8));
                    fileOutputStream.write(password.getBytes(StandardCharsets.UTF_8));
                    fileOutputStream.write('\n');
                    fileOutputStream.close();

                    edtUsername.setText("");
                    edtPassword.setText("");

                    // thông báo
                    Toast.makeText(SignUpActivity.this, "Successfully", Toast.LENGTH_SHORT).show();

                    // tự động chuyển về login page
                    Intent intentLogin = new Intent(SignUpActivity.this, SignInActivity.class);
                    startActivity(intentLogin);

                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
}
