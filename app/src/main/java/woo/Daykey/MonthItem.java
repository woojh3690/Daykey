package woo.Daykey;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

class MonthItem {
    String dayText;//요일 + 일정 텍스트 변수
    private SQLiteDatabase db;
    private int day;
    private String trimDay;
    private String strTodaySchedule;

    MonthItem(int day, String trimDay, SQLiteDatabase db) {
        this.day = day;
        this.trimDay = trimDay;
        this.db = db;
        this.strTodaySchedule = todaySchedule();
        this.dayText = day + "\n" + strTodaySchedule;
    }

    //오늘의 일정 표시
    private String todaySchedule() {
        String schedule = "";

        try { //데이터베이스에 쿼리문을 날려 schedule변수에 오늘의 일정 저장
            String[] columns = {"schedule"};
            String where = " date = ?";
            String[] at = {trimDay};
            Cursor cursor = db.query("calendarTable", columns,  where, at, null, null, null);

            while (cursor.moveToNext()) {
                schedule = cursor.getString(0);
            }
            cursor.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return schedule;
    }

    String[] getDayText() {
        return new String[]{String.valueOf(day), strTodaySchedule};
    }
}
