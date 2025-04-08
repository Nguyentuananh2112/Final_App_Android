package com.example.finalcampusexpensemanager;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager2.widget.ViewPager2;

import com.example.finalcampusexpensemanager.adapter.ViewPagerAdapter;
import com.example.finalcampusexpensemanager.db.DatabaseHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;

import java.util.List;

public class DashboardActivity
        extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    BottomNavigationView bottomNavigationView;
    NavigationView navigationView;
    DrawerLayout drawerLayout;
    Toolbar toolbar;
    ViewPager2 viewPager2;
    int userId;
    private boolean isNotificationOn = true;
    private MenuItem notificationMenuItem;
    private DatabaseHelper dbHelper;
    private static final String CHANNEL_ID = "budget_notification_channel";
    private static final int NOTIFICATION_ID = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        
        // Tạo Notification Channel
        createNotificationChannel();
        
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        drawerLayout = findViewById(R.id.drawer_layout);
        toolbar = findViewById(R.id.toolbar);
        viewPager2 = findViewById(R.id.viewPager);
        navigationView = findViewById(R.id.nav_View);
        dbHelper = new DatabaseHelper(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            userId = bundle.getInt("USER_ID", 0);
        }
        setupViewPager();

        // Xử lý logout
        Menu menu = navigationView.getMenu();
        MenuItem itemLogout = menu.findItem(R.id.nav_logout);
        itemLogout.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem menuItem) {
                new AlertDialog.Builder(DashboardActivity.this)
                        .setTitle("Logout")
                        .setMessage("Are you sure wanna logout")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int which) {
                                Toast.makeText(DashboardActivity.this, " Logout Successfully", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(DashboardActivity.this, SignInActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
                return true;
            }
        });

        // Xử lý click vào tab bottom
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.menu_home){
                    viewPager2.setCurrentItem(0);
                } else if (item.getItemId() == R.id.menu_expense) {
                    viewPager2.setCurrentItem(1);
                } else if (item.getItemId() == R.id.menu_category) {
                    viewPager2.setCurrentItem(2);
                } else if (item.getItemId() == R.id.budget) {
                    viewPager2.setCurrentItem(3);
                } else if (item.getItemId() == R.id.menu_setting) {
                    viewPager2.setCurrentItem(4);
                }else {
                    viewPager2.setCurrentItem(0);
                }
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        notificationMenuItem = menu.findItem(R.id.action_notification);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_notification) {
            List<String> warnings = dbHelper.getBudgetWarnings(userId);
            if (warnings.isEmpty()) {
                Toast.makeText(this, "Không có cảnh báo nào", Toast.LENGTH_SHORT).show();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Lịch sử cảnh báo chi tiêu");
                
                StringBuilder message = new StringBuilder();
                for (String warning : warnings) {
                    message.append(warning).append("\n\n");
                }
                
                builder.setMessage(message.toString())
                        .setPositiveButton("Đóng", null)
                        .show();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupViewPager(){
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), getLifecycle());
        viewPagerAdapter.setUserId(userId);
        viewPager2.setAdapter(viewPagerAdapter);
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (position == 0){
                    bottomNavigationView.getMenu().findItem(R.id.menu_home).setChecked(true);
                }else if (position ==1){
                    bottomNavigationView.getMenu().findItem(R.id.menu_expense).setChecked(true);
                } else if (position == 2) {
                    bottomNavigationView.getMenu().findItem(R.id.menu_category).setChecked(true);
                } else if (position == 3) {
                    bottomNavigationView.getMenu().findItem(R.id.budget).setChecked(true);
                } else if (position == 4) {
                    bottomNavigationView.getMenu().findItem(R.id.menu_setting).setChecked(true);
                } else {
                    bottomNavigationView.getMenu().findItem(R.id.menu_home).setChecked(true);
                }
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_home){
            viewPager2.setCurrentItem(0);
        }else if (item.getItemId() == R.id.menu_expense) {
            viewPager2.setCurrentItem(1);
        } else if (item.getItemId() == R.id.menu_category) {
            viewPager2.setCurrentItem(2);
        } else if (item.getItemId() == R.id.budget) {
            viewPager2.setCurrentItem(3);
        } else if (item.getItemId() == R.id.menu_setting) {
            viewPager2.setCurrentItem(4);
        }else {
            viewPager2.setCurrentItem(0);
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Budget Notifications";
            String description = "Notifications for budget alerts";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void showBudgetNotification(String title, String message) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.notification_on)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    private void checkBudgetAndShowNotification() {
        // Lấy tổng thu nhập và chi tiêu
        double totalIncome = dbHelper.getTotalIncome(userId);
        double totalExpense = dbHelper.getTotalExpense(userId);
        
        // Kiểm tra nếu chi tiêu vượt quá thu nhập
        if (totalExpense > totalIncome) {
            String title = "CẢNH BÁO: CHI TIÊU VƯỢT QUÁ THU NHẬP";
            String message = String.format("Bạn đã chi tiêu $%.2f vượt quá thu nhập $%.2f", 
                totalExpense, totalIncome);
            showBudgetNotification(title, message);
        }
        
        // Kiểm tra các danh mục vượt ngân sách
        List<String> exceededCategories = dbHelper.checkBudgetExceeded(userId);
        for (String category : exceededCategories) {
            if (!category.startsWith("Tổng chi tiêu")) {
                showBudgetNotification("CẢNH BÁO NGÂN SÁCH", category);
            }
        }
    }
}
