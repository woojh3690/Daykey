package woo.Daykey;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import java.util.Calendar;

/**
 * 알람 등록
 */

class AlarmBroadcast {
    private final Context mainContext;
    private int nextDay = 1;

    AlarmBroadcast(Context context) {
        this.mainContext = context;
    }

    void Alarm() {
        Calendar cal = AlarmWithNoToast();
        SettingPreferences set = new SettingPreferences(mainContext);
        Toast.makeText(mainContext, "알림이 설정되었습니다. " + cal.get(Calendar.YEAR) + "년 " +
                (cal.get(Calendar.MONTH) + 1) + "월 " +
                cal.get(Calendar.DAY_OF_MONTH) + "일 " +
                set.getInt("hour") + "시 " +
                set.getInt("min") + "분", Toast.LENGTH_LONG).show();
    }

    Calendar AlarmWithNoToast() {
        Calendar calendar = null;

        try {
            AlarmManager am = (AlarmManager) mainContext.getSystemService(Context.ALARM_SERVICE);//알람서비스를 가져오기
            Intent intent = new Intent(mainContext, AlarmBroadcastReceive.class);// 알람이 발생했을 경우, AlarmBroadcastReceive에게 방송
            PendingIntent sender = PendingIntent.getBroadcast(mainContext, 0, intent, PendingIntent.FLAG_IMMUTABLE);

            setNextDay();

            calendar = Calendar.getInstance();
            calendar.add(Calendar.DATE, nextDay);

            SettingPreferences set = new SettingPreferences(mainContext);

            //알람시간 설정
            calendar.set(calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH),
                    set.getInt("hour"),
                    set.getInt("min"));

            am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);//알람 예약
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return calendar;
    }

    //알람 취소
    void cancelAlarm() {
        AlarmManager am = (AlarmManager) mainContext.getSystemService(Context.ALARM_SERVICE);//알람서비스를 가져오기
        Intent intent = new Intent(mainContext, AlarmBroadcastReceive.class);

        PendingIntent sender = PendingIntent.getBroadcast(mainContext, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        if (sender != null) {
            am.cancel(sender);//알람 취소
            sender.cancel();
        }
        Toast.makeText(mainContext, "알림이 취소 되었습니다.", Toast.LENGTH_SHORT).show();
    }

    //몇일 뒤에 알람이 울릴 것인지 설정
    private void setNextDay() {
        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        if (dayOfWeek == 7) {
            nextDay = 2;
        } else if (dayOfWeek == 6) {
            nextDay = 3;
        }
    }
}