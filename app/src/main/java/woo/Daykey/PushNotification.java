package woo.Daykey;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;

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

        SettingPreferences set = new SettingPreferences(context);
        Notification.Builder notificationBuilder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                notificationBuilder = new Notification.Builder(context, set.getString("channel"));
            } catch (NullPointerException e) {
                NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                NotificationChannel channelMessage = new NotificationChannel("channel_id", "channel_name", NotificationManager.IMPORTANCE_DEFAULT);
                channelMessage.setDescription("channel description");
                channelMessage.enableLights(true);
                channelMessage.setLightColor(Color.GREEN);
                channelMessage.enableVibration(true);
                channelMessage.setVibrationPattern(new long[]{100, 200, 100, 200});
                channelMessage.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
                notificationManager.createNotificationChannel(channelMessage);
                String id = channelMessage.getId();
                set.saveString("channel", id);
                notificationBuilder = new Notification.Builder(context, id);
            }
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
