package com.miruker.lib.mailtransferservice.Utils;


import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;

import com.miruker.lib.mailtransferservice.R;


public class NotificationUtils {


    @SuppressWarnings("deprecation")
    public static void showNotification(Context con, String notifyMessage, String title, String summary) {
        NotificationManager notificationManager = (NotificationManager) con.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification.Builder builder = new Notification.Builder(con);
        builder.setWhen(System.currentTimeMillis());
        builder.setTicker(notifyMessage);
        builder.setContentTitle(con.getString(R.string.mesNotification));
        builder.setContentText(summary);
        builder.setSmallIcon(R.drawable.ic_stat_small);
        builder.setAutoCancel(true);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setColor(Color.GRAY);
            builder.setPriority(Notification.PRIORITY_DEFAULT);
            builder.setVisibility(Notification.VISIBILITY_SECRET);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            notificationManager.notify(R.string.app_name, builder.build());
        else
            notificationManager.notify(R.string.app_name, builder.getNotification());
    }

    /**
     * 通知の削除
     *
     * @param con コンテキスト
     */
    public static void clearNotification(Context con) {
        NotificationManager notificationManager = (NotificationManager) con.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(R.string.app_name);
    }
}
