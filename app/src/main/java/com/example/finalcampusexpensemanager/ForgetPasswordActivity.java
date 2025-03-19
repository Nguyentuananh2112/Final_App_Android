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
import com.example.finalcampusexpensemanager.model.UserModel;
import com.google.android.material.textfield.TextInputEditText;

public class ForgetPasswordActivity extends AppCompatActivity {
    TextInputEditText edtAccount, edtEmail;
    Button btnConfirm, btnCancel;
    UserDb userDb;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgetpassword);
        userDb = new UserDb(ForgetPasswordActivity.this);
        edtAccount = findViewById(R.id.edtAccount);
        edtEmail = findViewById(R.id.edtEmail);
        btnConfirm = findViewById(R.id.btnConfirm);
        btnCancel = findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edtAccount.setText("");
                edtEmail.setText("");
                Intent intent = new Intent(ForgetPasswordActivity.this, SignInActivity.class);
                startActivity(intent);
            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String account =edtAccount.getText().toString().trim();
                String email = edtEmail.getText().toString().trim();
                if (TextUtils.isEmpty(account)){
                    edtAccount.setError("Account not empty");
                    return;
                }
                if (TextUtils.isEmpty(email)){
                    edtEmail.setError("Email not empty");
                    return;
                }

                UserModel infoUser = userDb.getInfoUser(account, email, 1);
                assert infoUser != null;
                if (infoUser.getUsername() != null && infoUser.getEmail() != null){
                    // success
                    Intent intentPassword = new Intent(ForgetPasswordActivity.this, UpdatePasswordActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt("ID_ACCOUNT_USER", infoUser.getId());
                    intentPassword.putExtras(bundle);
                    startActivity(intentPassword);

                }else {
                    //fail
                    Toast.makeText(ForgetPasswordActivity.this, "Invalid Account", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
