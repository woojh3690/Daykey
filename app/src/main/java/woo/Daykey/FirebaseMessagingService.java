package woo.Daykey;

import androidx.core.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.RemoteMessage;

import java.util.HashMap;
import java.util.Map;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
    private static final String TAG = "FirebaseMsgService";
    private Map<String, String> dictionary = new HashMap<>();

    public FirebaseMessagingService() {
        dictionary.put("news", "공지사항이");
        dictionary.put("home", "가정통신문이");
        dictionary.put("sci", "과학중점공지가");
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        RemoteMessage.Notification noti =  remoteMessage.getNotification();
        assert noti != null;

        String type = remoteMessage.getData().get("type");
        Log.i("Firebase 확인 :", type);
        String title = dictionary.get(type);
        String message = remoteMessage.getData().get("message");
        String urlID = remoteMessage.getData().get("urlID");

        PushNotification pushNotification = new PushNotification(getApplicationContext(), TAG, title,
                message, NotificationCompat.PRIORITY_DEFAULT, urlID, type, null);
        pushNotification.send();
    }
}

