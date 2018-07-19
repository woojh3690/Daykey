package woo.Daykey;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import java.util.Date;

/**
 * 알림을 보냅니다.
 */

class PushNotification {
    private Context context;

    PushNotification(Context context) {
        this.context = context;
    }

    void send(String tag, String title, String message, int priority, String type, NotificationCompat.Style style) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("type", type);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        SettingPreferences set = new SettingPreferences(context);


        String GROUP_KEY = "woo.Daykey.Notice";

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context, set.getString("channel"))
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setPriority(priority)
                        .setContentIntent(pendingIntent)
                        .setGroup(GROUP_KEY)
                        .setStyle(style);

        Notification summaryNotification =
                new NotificationCompat.Builder(context, set.getString("channel"))
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setStyle(new NotificationCompat.InboxStyle())
                        .setGroup(GROUP_KEY)
                        .setGroupSummary(true)
                        .build();

        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        assert pm != null;
        PowerManager.WakeLock wakelock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK
                | PowerManager.ACQUIRE_CAUSES_WAKEUP, tag);
        wakelock.acquire(5000);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify((int)((new Date().getTime() / 1000L) % Integer.MAX_VALUE), notificationBuilder.build());
        notificationManager.notify(0, summaryNotification);
    }
}
