package com.miruker.lib.mailtransferservice.Utils;


import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Build;

import com.miruker.lib.mailtransferservice.R;



public class NotificationUtility {

	
	@SuppressWarnings("deprecation")
	public static void showNotification(Context con ,String notifyMessage , String title , String summary)
	{
    	NotificationManager notificationManager = (NotificationManager) con.getSystemService(Context.NOTIFICATION_SERVICE);
    	Notification.Builder notification = new Notification.Builder(con);
    	notification.setWhen(System.currentTimeMillis());
    	notification.setTicker(notifyMessage);
    	notification.setLargeIcon(BitmapFactory.decodeResource(con.getResources(), R.drawable.ic_stat_big));
    	notification.setContentTitle(con.getString(R.string.mesNotification));
    	notification.setContentText(summary);
    	notification.setSmallIcon(R.drawable.ic_stat_small);
       	notification.setAutoCancel(true);

       	if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
       		notificationManager.notify(R.string.app_name, notification.build());
       	else
       		notificationManager.notify(R.string.app_name,notification.getNotification());
	}

	/**
	 * 通知の削除
	 * @param con コンテキスト
	 */
	public static void clearNotification(Context con)
	{
		NotificationManager notificationManager = (NotificationManager) con.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancel(R.string.app_name);
	}
}
