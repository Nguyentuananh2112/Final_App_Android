package com.example.finalcampusexpensemanager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import androidx.core.content.ContextCompat;
import androidx.core.content.pm.PackageInfoCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager2.widget.ViewPager2;

import com.example.finalcampusexpensemanager.adapter.ViewPagerAdapter;
import com.example.finalcampusexpensemanager.helper.NotificationHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;

public class DashboardActivity
        extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    BottomNavigationView bottomNavigationView;
    NavigationView navigationView;
    DrawerLayout drawerLayout;
    Toolbar toolbar;
    ViewPager2 viewPager2;
    int userId;
    private boolean notificationsEnabled = true;
    private static final int NOTIFICATION_PERMISSION_CODE = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        drawerLayout = findViewById(R.id.drawer_layout);
        toolbar = findViewById(R.id.toolbar);
        viewPager2 = findViewById(R.id.viewPager);
        navigationView = findViewById(R.id.nav_View);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        // Vào mục values file string.xml thêm như sau:
//        <string name="close_nav">Close Navigation</string>
//        <string name="open_nav">Open Navigation</string>
//        mục đích là để đóng và mở thanh 3 dọc đó

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        // Lấy user_id từ Intent
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
                        .setMessage("Ara you sure wanna logout ?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int which) {
                                Toast.makeText(DashboardActivity.this, "Logou Successfully", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(DashboardActivity.this, SignInActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
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

        // Lấy trạng thái thông báo từ SharedPreferences
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        notificationsEnabled = prefs.getBoolean("notifications_enabled", true);

        // Cập nhật icon thông báo
        updateNotificationIcon();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_notification) {
            // Toggle trạng thái thông báo
            notificationsEnabled = !notificationsEnabled;
            
            // Lưu trạng thái vào SharedPreferences
            SharedPreferences.Editor editor = getSharedPreferences("AppPrefs", MODE_PRIVATE).edit();
            editor.putBoolean("notifications_enabled", notificationsEnabled);
            editor.apply();
            
            // Cập nhật icon
            updateNotificationIcon();
            
            // Hiển thị thông báo
            Toast.makeText(this, 
                notificationsEnabled ? "Notifications enabled" : "Notifications disabled", 
                Toast.LENGTH_SHORT).show();

            // Nếu bật thông báo, yêu cầu quyền thông báo của hệ thống
            if (notificationsEnabled) {
                checkAndRequestNotificationPermission();
            }
            
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void checkAndRequestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, NOTIFICATION_PERMISSION_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == NOTIFICATION_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Quyền đã được cấp
            } else {
                // Quyền bị từ chối
            }
        }
    }

    private void updateNotificationIcon() {
        MenuItem notificationItem = toolbar.getMenu().findItem(R.id.action_notification);
        if (notificationItem != null) {
            notificationItem.setIcon(notificationsEnabled ? 
                R.drawable.notification_on : R.drawable.notification_off);
        }
    }

    private void setupViewPager(){
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), getLifecycle());
        viewPagerAdapter.setUserId(userId); // Truyền user_id cho Adapter
        viewPager2.setAdapter(viewPagerAdapter);
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                MenuItem item = null;

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

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
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
        // click xong tự động đóng vào
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}
