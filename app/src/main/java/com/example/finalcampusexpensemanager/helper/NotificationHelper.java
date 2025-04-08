package com.example.finalcampusexpensemanager.helper;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import com.example.finalcampusexpensemanager.DashboardActivity;
import com.example.finalcampusexpensemanager.R;

public class NotificationHelper {
    private static final String CHANNEL_ID = "expense_manager_channel";
    private static final String CHANNEL_NAME = "Expense Manager Notifications";
    private static final String CHANNEL_DESCRIPTION = "Notifications for expense manager app";
    private static final int NOTIFICATION_ID = 1;
    private final Context context;
    private final NotificationManagerCompat notificationManager;

    public NotificationHelper(Context context) {
        this.context = context;
        this.notificationManager = NotificationManagerCompat.from(context);
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription(CHANNEL_DESCRIPTION);
            channel.enableLights(true);
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{100, 200, 300, 400, 500});
            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            manager.createNotificationChannel(channel);
        }
    }

    public void showTransactionNotification(String type, double amount, String category) {
        if (!checkNotificationPermission()) {
            return;
        }

        Intent intent = new Intent(context, DashboardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        );

        String title = type.equals("Income") ? "Thêm thu nhập thành công" : "Thêm chi tiêu thành công";
        String content = String.format(
            "%s: %,.0f VND\nDanh mục: %s",
            type.equals("Income") ? "Thu nhập" : "Chi tiêu",
            amount,
            category
        );

        NotificationCompat.MessagingStyle messagingStyle = new NotificationCompat.MessagingStyle("Hệ thống")
            .addMessage(content, System.currentTimeMillis(), "Hệ thống");

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setStyle(messagingStyle)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setVibrate(new long[]{100, 200, 300, 400, 500})
            .setDefaults(NotificationCompat.DEFAULT_SOUND);

        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }

    public void showBudgetExceededNotification(String category, double amount, double budget) {
        if (!checkNotificationPermission()) {
            return;
        }

        String title = "Cảnh báo vượt ngân sách";
        String message = String.format(
            "Danh mục %s đã vượt quá ngân sách!\nĐã chi: %,.0f VND\nNgân sách: %,.0f VND",
            category, amount, budget
        );
        showNotification(title, message);
    }

    public void showNotification(String title, String message) {
        if (!checkNotificationPermission()) {
            return;
        }

        Intent intent = new Intent(context, DashboardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent);

        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }

    public void showWelcomeNotification() {
        if (!checkNotificationPermission()) {
            return;
        }

        Intent intent = new Intent(context, DashboardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.MessagingStyle messagingStyle = new NotificationCompat.MessagingStyle("Hệ thống")
            .addMessage("Thông báo đã được bật. Bạn sẽ nhận được thông báo khi thêm giao dịch mới.", 
                System.currentTimeMillis(), "Hệ thống");

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Chào mừng đến với Expense Manager")
            .setStyle(messagingStyle)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setVibrate(new long[]{100, 200, 300, 400, 500})
            .setDefaults(NotificationCompat.DEFAULT_SOUND);

        notificationManager.notify(0, builder.build());
    }

    public void showBudgetWarningNotification(double totalIncome, double totalExpense) {
        if (!checkNotificationPermission()) {
            return;
        }

        double percentage = ((totalExpense - totalIncome) / totalIncome) * 100;
        percentage = Math.round(percentage * 100.0) / 100.0;

        String title = "Cảnh báo chi tiêu";
        String content = String.format(
            "Bạn đã vượt mức ngân sách chi phí %.2f%%\n" +
            "Thu nhập: %,.0f VND\n" +
            "Chi tiêu: %,.0f VND",
            percentage,
            totalIncome,
            totalExpense
        );

        NotificationCompat.MessagingStyle messagingStyle = new NotificationCompat.MessagingStyle("Hệ thống")
            .addMessage(content, System.currentTimeMillis(), "Hệ thống");

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setStyle(messagingStyle)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setVibrate(new long[]{100, 200, 300, 400, 500})
            .setDefaults(NotificationCompat.DEFAULT_SOUND);

        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }

    public void showBudgetExceededPercentageNotification(double totalIncome, double totalExpense, double newExpense) {
        if (!checkNotificationPermission()) {
            return;
        }

        double percentage = ((totalExpense + newExpense - totalIncome) / totalIncome) * 100;
        
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("CẢNH BÁO CHI TIÊU VƯỢT MỨC")
                .setContentText(String.format("Chi tiêu vượt mức %.2f%% so với thu nhập", percentage))
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(String.format(
                            "Chi tiêu vượt mức %.2f%% so với thu nhập\n" +
                            "Thu nhập: %,.0f VND\n" +
                            "Chi tiêu hiện tại: %,.0f VND\n" +
                            "Chi tiêu mới: %,.0f VND",
                            percentage,
                            totalIncome,
                            totalExpense,
                            newExpense
                        )))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setVibrate(new long[]{0, 1000, 500, 1000});

        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    private boolean checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) 
                != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }
} 