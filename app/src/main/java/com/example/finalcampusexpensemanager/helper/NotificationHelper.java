package com.example.finalcampusexpensemanager.helper;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
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
    private final NotificationManager notificationManager;
    private final Vibrator vibrator;

    public NotificationHelper(Context context) {
        this.context = context;
        this.notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        this.vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
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
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{100, 200, 300, 400, 500});
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void vibrate() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            vibrator.vibrate(500);
        }
    }

    private boolean checkNotificationPermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            return NotificationManagerCompat.from(context).areNotificationsEnabled();
        }
        return true;
    }

    private boolean checkNotificationEnabled() {
        SharedPreferences prefs = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        return prefs.getBoolean("notifications_enabled", true);
    }

    public void showTransactionNotification(String type, double amount, String category) {
        if (!checkNotificationPermission() || !checkNotificationEnabled()) {
            return;
        }

        vibrate();

        Intent intent = new Intent(context, DashboardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        );

        String title = type.equals("Income") ? "Income Added Successfully" : "Expense Added Successfully";
        String content = String.format(
            "%s: %,.0f VND\nCategory: %s",
            type.equals("Income") ? "Income" : "Expense",
            amount,
            category
        );

        NotificationCompat.MessagingStyle messagingStyle = new NotificationCompat.MessagingStyle("System")
            .addMessage(content, System.currentTimeMillis(), "System");

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
        if (!checkNotificationPermission() || !checkNotificationEnabled()) {
            return;
        }

        String title = "Budget Exceeded Warning";
        String message = String.format(
            "Category %s has exceeded the budget!\nSpent: %,.0f VND\nBudget: %,.0f VND",
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

    public void showBudgetWarningNotification(int totalIncome, int totalExpense) {
        if (!checkNotificationPermission() || !checkNotificationEnabled()) {
            return;
        }

        vibrate();
        
        double percentage = ((double) (totalExpense - totalIncome) / totalIncome) * 100;
        
        Intent intent = new Intent(context, DashboardActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("WARNING: Expenses Exceed Income")
                .setContentText(String.format("Expenses exceed income by %.2f%%", percentage))
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(String.format(
                                "Total Income: %,d VND\n" +
                                "Total Expense: %,d VND\n" +
                                "Exceeded by: %,d VND (%.2f%%)",
                                totalIncome,
                                totalExpense,
                                totalExpense - totalIncome,
                                percentage
                        )))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        notificationManager.notify(2, builder.build());
    }

    public void showBudgetExceededPercentageNotification(double totalIncome, double totalExpense, double newExpense) {
        if (!checkNotificationPermission() || !checkNotificationEnabled()) {
            return;
        }

        double percentage = ((totalExpense + newExpense - totalIncome) / totalIncome) * 100;
        
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("EXPENSE EXCEEDED WARNING")
                .setContentText(String.format("Expenses exceed income by %.2f%%", percentage))
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(String.format(
                            "Expenses exceed income by %.2f%%\n" +
                            "Income: %,.0f VND\n" +
                            "Current Expenses: %,.0f VND\n" +
                            "New Expense: %,.0f VND",
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
} 