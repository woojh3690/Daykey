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
import android.view.WindowManager;

import java.util.Date;

/**
 * 알림을 보냅니다.
 */

class PushNotification {
    private Context context;
    private String tag;
    private String title;
    private String message;
    private int priority;
    private String urlID;
    private String type;
    private NotificationCompat.Style style;
    private SettingPreferences set;

    PushNotification(Context context, String tag, String title, String message, int priority,
                     String urlID, String type, NotificationCompat.Style style) {
        this.context = context;
        this.tag = tag;
        this.title = title;
        this.message = message;
        this.priority = priority;
        this.urlID = urlID;
        this.type = type;
        this.style = style;

        set = new SettingPreferences(context);
    }

    void send() {
        Intent intent;
        if (urlID == null) {
            intent = new Intent(context, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("type", type);
            sendDiet(intent);
        } else {
            Uri uri = Uri.parse("http://www.daykey.hs.kr/daykey/0701/board/14117/" + urlID);
            intent = new Intent(Intent.ACTION_VIEW, uri);
            sendNews(intent);
        }
    }

    private void sendNews(Intent intent) {
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        String GROUP_KEY = "woo.Daykey.Notice";
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context, set.getString("channel"))
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("새로운 "+title+" 있습니다.")
                        .setContentText(message)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setPriority(priority)
                        .setLights(173,500,2000)
                        .setContentIntent(pendingIntent)
                        .setGroup(GROUP_KEY);

        Notification summaryNotification =
                new NotificationCompat.Builder(context, set.getString("channel"))
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setStyle(new NotificationCompat.InboxStyle())
                        .setAutoCancel(true)
                        .setGroup(GROUP_KEY)
                        .setGroupSummary(true)
                        .build();

        wakeup();
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify((int)((new Date().getTime() / 1000L) % Integer.MAX_VALUE), notificationBuilder.build());
        notificationManager.notify(0, summaryNotification);
    }

    private void sendDiet(Intent intent) {
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context, set.getString("channel"))
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setAutoCancel(true)
                        .setLights(173,500,2000)
                        .setSound(defaultSoundUri)
                        .setPriority(priority)
                        .setContentIntent(pendingIntent)
                        .setStyle(style);

        wakeup();
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify((int)((new Date().getTime() / 1000L) % Integer.MAX_VALUE), notificationBuilder.build());
    }

    private void wakeup() {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        assert pm != null;
        PowerManager.WakeLock wakelock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK
                | PowerManager.ACQUIRE_CAUSES_WAKEUP, tag);
        wakelock.acquire(5000);
    }
}
