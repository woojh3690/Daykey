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

import static woo.Daykey.MainActivity.set;

/**
 * 알림을 보냅니다.
 */

class PushNotification {
    private Context context;

    PushNotification(Context context) {
        this.context = context;
    }

    void send(String tag, String title, String message, int priority, String type, Notification.Style style) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("type", type);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Notification.Builder notificationBuilder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationBuilder = new Notification.Builder(context, set.getString("channel"));
        } else {
            notificationBuilder = new Notification.Builder(context);
        }
        notificationBuilder
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setLights(173,500,2000)
                .setPriority(priority)
                .setContentIntent(pendingIntent)
                .setStyle(style);

        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        assert pm != null;
        PowerManager.WakeLock wakelock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK| PowerManager.ACQUIRE_CAUSES_WAKEUP, tag);
        wakelock.acquire(5000);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        assert notificationManager != null;
        notificationManager.notify(0 , notificationBuilder.build());
    }
}
