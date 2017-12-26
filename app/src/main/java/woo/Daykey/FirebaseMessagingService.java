package woo.Daykey;

import android.app.Notification;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
    private static final String TAG = "FirebaseMsgService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        String title = remoteMessage.getData().get("title");

        if (title.equals("등록")) {
            FirebaseMessaging.getInstance().subscribeToTopic("ALL");
        } else {
            String message = remoteMessage.getData().get("message");
            String type = remoteMessage.getData().get("type");
            PushNotification pushNotification = new PushNotification(getApplicationContext());
            pushNotification.send(TAG, title, message, Notification.PRIORITY_HIGH, type, null);
        }
    }
}

