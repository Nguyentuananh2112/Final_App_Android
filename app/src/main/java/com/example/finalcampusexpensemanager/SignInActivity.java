package com.example.finalcampusexpensemanager;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.finalcampusexpensemanager.db.DatabaseHelper;
import com.example.finalcampusexpensemanager.db.UserDb;
import com.example.finalcampusexpensemanager.model.UserModel;
import com.example.finalcampusexpensemanager.utils.HashUtil;
import com.google.android.material.textfield.TextInputEditText;

import java.io.FileInputStream;

public class SignInActivity extends AppCompatActivity {
    DatabaseHelper dbHelper;
    TextInputEditText editUserName, editPassword;
    TextView tvSignUp, tvForgetPassword;
    Button btnLogin;
    UserDb userDb;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        dbHelper = new DatabaseHelper(this);

//        userDb = new UserDb(SignInActivity.this);
        tvForgetPassword = findViewById(R.id.tvForgetpassword);
        tvSignUp = findViewById(R.id.tvSignUpAccount);
        editUserName = findViewById(R.id.editTextUsername);
        editPassword = findViewById(R.id.editPassword);
        btnLogin = findViewById(R.id.signInButton);


        tvForgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentForgetPass = new Intent(SignInActivity.this, ForgetPasswordActivity.class);
                startActivity(intentForgetPass);
            }
        });

        // Di chuyển đến trang signup
        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });
        loginWithDatabase();

    }


    private void loginWithDatabase(){
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = editUserName.getText().toString().trim();
                String password = editPassword.getText().toString().trim();
                if (TextUtils.isEmpty(user)){
                    editUserName.setError("Username not empty");
                    return;
                }
                if (TextUtils.isEmpty(password)){
                    editPassword.setError("Password not empty");
                    return;
                }
                // Băm mật khẩu trước khi kiểm tra trong database
                String hashedPassword = HashUtil.hashPassword(password);

                // Lấy thông tin user từ database (so sánh với mật khẩu băm)
                UserModel userData = dbHelper.getInfoUser(user, hashedPassword, 0);



//                assert userData != null;
                if (userData.getId() > 0) { // Kiểm tra xem user có tồn tại không
                    //login success
                    Intent intent = new Intent(SignInActivity.this, DashboardActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt("USER_ID", userData.getId());
                    bundle.putString("USERNAME_ACCOUNT", userData.getUsername());
                    bundle.putString("USER_EMAIL", userData.getEmail());
                    bundle.putString("USER_PHONE", userData.getPhone());
                    intent.putExtras(bundle);
                    startActivity(intent);
                    finish();
                }else {
                    // login fail
                    Toast.makeText(SignInActivity.this, "Account Invalid", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }


    // login local storage
    private void loginWithDataInternalFile(){
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = editUserName.getText().toString().trim();
                String Password = editPassword.getText().toString().trim();
                if (TextUtils.isEmpty(user) || TextUtils.isEmpty(Password)){
                    Toast.makeText(SignInActivity.this, "Username and password is not empty",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                // Xử lý get data từ trong file để check login
                try {
                    FileInputStream fileInputStream = openFileInput("account.txt");
                    int read = -1;
                    StringBuilder builder = new StringBuilder();

                    while ((read = fileInputStream.read()) != -1){
                        builder.append((char) read);
                    }

                    // Lấy đc data và gắn vào builder
                    fileInputStream.close();
                    String[] infoAccount = null; // tạo mảng

                    // Biến mảng về chuỗi
                    infoAccount = builder.toString().trim().split("\n");

                    // Mảng chứa tất cả các tk đã signup
                    boolean checkAccount = false;
                    int sizeArray = infoAccount.length;
                    for (int i = 0; i < sizeArray; i++) {
                        // indexOf đi tìm vị trí có nằm ở trong chuỗi hay là k
                        // Nó sẽ xét username check từ 0 dến ký tự "|" đến hết cái password
                        String username = infoAccount[i].substring(0,infoAccount[i].indexOf("|"));
                        // pass thi se tiep tuc check tu "|" den het cai mk password
                        String pass = infoAccount[i].substring(infoAccount[i].indexOf("|")+1);

                        // check login
                        if (username.equals(user) && pass.equals(Password)){
                            //login success
                            checkAccount = true;
                            break;
                        }

                    }
                    if (checkAccount){
                        // Thông báo login success
                        // Vào Main Page
                        Intent intent = new Intent(SignInActivity.this, DashboardActivity.class);
                        Bundle bundle = new Bundle();

                        // gửi data username sang
                        bundle.putString("USERNAME_ACCOUNT", user);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        finish();
                    }else {
                        // notifi login false
                        Toast.makeText(SignInActivity.this, "Invalid Account", Toast.LENGTH_SHORT).show();
                        return;
                    }

                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

            }
        });
    }
}
