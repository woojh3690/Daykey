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
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

import static woo.Daykey.MainActivity.getWhatKindOfNetwork;

/**
 * intent를 받아서 알람 보여주는 class
 */

public class AlarmBroadcastReceive extends BroadcastReceiver {
    String launch = "점심이 없다 OTL";
    String dinner = "저녁이 없다 OTL";
    String info = "데이터가 없습니다.";

    SqlHelper SqlHelper;
    SQLiteDatabase db;

    @Override
    public void onReceive(Context context, Intent intent) { //알람 시간이 되었을때 onReceive를 호출함
        try {
            SqlHelper = new SqlHelper(context);
            firstInfoSave(context);
            todayMenuSave();
            //todayScheduleSave();

            //NotificationManager 안드로이드 상태바에 메세지를 던지기위한 서비스 불러오고
            NotificationManager notificationmanager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);

            //노티바 스타일
            Notification.BigTextStyle style = new Notification.BigTextStyle();
            style.setSummaryText("급식보기 +");
            style.setBigContentTitle("오늘의 급식!");
            style.bigText("점심 : " + launch + "저녁 : " + dinner);

            //노티바 만들기
            Notification.Builder builder = new Notification.Builder(context);
            builder.setSmallIcon(R.mipmap.ic_launcher)
                    .setWhen(System.currentTimeMillis())
                    .setContentTitle("공지사항")
                    .setContentText(info)
                    .setDefaults(Notification.DEFAULT_SOUND|Notification.DEFAULT_VIBRATE)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .setStyle(style);//스타일 넣어주기

            notificationmanager.notify(1, builder.build());

            AlarmBroadcast alarmBroadcast = new AlarmBroadcast(context);
            alarmBroadcast.Alarm(1);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //첫번째 공지사항 불러오기
    private void firstInfoSave(Context context) {
        db = SqlHelper.getReadableDatabase();
        if (getWhatKindOfNetwork(context)) {
            String sql = "drop table " + "newsTable";
            String create3 = "create table " + "newsTable " + "(_id INTEGER PRIMARY KEY AUTOINCREMENT, title text, teacherName text, visitors text, date text, url text);";
            try{
                db.execSQL(sql);
                db.execSQL(create3);
            }catch(SQLException e){
                e.printStackTrace();
            }

            Thread thread = new NewsParsing(context);
            thread.start();

            try {
                thread.join();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        try {
            String[] columns = {"_id", "title"};
            String where = " _id = 1";
            Cursor cursor = db.query("newsTable", columns,  where, null, null, null, null);

            while (cursor.moveToNext()) {
                info = cursor.getString(1);
            }
            cursor.close();
        } catch (Exception ex) {
            ex.printStackTrace();
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
            String TAG = "AlarmBroadcastReceive";
            Log.e(TAG, "allCalendarPrint error");
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