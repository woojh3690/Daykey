package woo.Daykey;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.nfc.Tag;
import android.view.View;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * intent를 받아서 알람 보여주는 class
 */

public class AlarmBroadcastReceive extends BroadcastReceiver {
    private static final String TAG = "AlarmBroadcastReceive";
    String launch = "점심이 없다 OTL";
    String dinner = "저녁이 없다 OTL";

    SqlHelper SqlHelper;
    SQLiteDatabase db;

    @Override
    public void onReceive(Context context, Intent intent) { //알람 시간이 되었을때 onReceive를 호출함
        try {
            SqlHelper = new SqlHelper(context);
            todayMenuSave();

            //노티바 스타일
            Notification.BigTextStyle style = new Notification.BigTextStyle();
            style.setSummaryText("급식보기 +");
            style.setBigContentTitle("오늘의 메뉴!");
            String message = "점심 : " + launch + "\n" + "저녁 : " + dinner;
            style.bigText(message);

            PushNotification pushNotification = new PushNotification(context);
            pushNotification.send(TAG, "오늘의 메뉴!", message.split("\\r?\\n")[0], Notification.PRIORITY_DEFAULT, "main", style);

            AlarmBroadcast alarmBroadcast = new AlarmBroadcast(context);
            alarmBroadcast.Alarm(1);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //오늘의 매뉴 저장
    private void todayMenuSave() {
        try {
            db = SqlHelper.getReadableDatabase();

            String[] columns = {"date", "menu"};
            String where = " date = " + dateNow();

            //쿼리를 통해 해당날짜에 급식데이터를 가져온다
            Cursor cursor = db.query("dietTable", columns, where, null, null, null, null);

            int checkNum = 0;
            while(cursor.moveToNext()) {
                String menu = cursor.getString(1);

                if (checkNum == 0) {
                    launch = menu;
                    checkNum = 1;
                } else  {
                    dinner = menu;
                }
            }
            cursor.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    //오늘 날짜
    private String dateNow() {
        long now = System.currentTimeMillis();// 현재시간을 msec 으로 구한다.
        Date date = new Date(now);// 현재시간을 date 변수에 저장한다.
        SimpleDateFormat sdfNow = new SimpleDateFormat("d");// 시간을 나타냇 포맷을 정한다 ( yyyy/MM/dd 같은 형태로 변형 가능 )
        return sdfNow.format(date);// nowDate 변수에 값을 장한다.
    }
}