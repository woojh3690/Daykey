package woo.Daykey;

import android.database.Cursor;

import static woo.Daykey.MainActivity.db;
import static woo.Daykey.MainActivity.SqlHelper;

class MonthItem {
    String dayText;//요일 + 일정 텍스트 변수
    private String trimDay;//

    MonthItem(int day, String trimDay) {
        this.trimDay = trimDay;
        this.dayText = day + "\n" + todaySchedule();
    }

    //오늘의 일정 표시
    private String todaySchedule() {
        String schedule = "";

        try { //데이터베이스에 쿼리문을 날려 schedule변수에 오늘의 일정 저장
            db = SqlHelper.getReadableDatabase();
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
}
