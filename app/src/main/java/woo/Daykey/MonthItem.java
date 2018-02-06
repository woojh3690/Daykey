package woo.Daykey;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.HashMap;

import static woo.Daykey.MainActivity.set;

class MonthItem {
    String dayText = "";//요일 + 일정 텍스트 변수
    private SQLiteDatabase db;
    private int day;
    private String trimDay;
    private String schoolSche;
    private String userSche = "";
    private String userScheAndName = "";
    private HashMap<String, Integer> map = new HashMap<>();
    private String sche;
    private String scheAndName;

    MonthItem(int day, String trimDay, SQLiteDatabase db) {
        this.day = day;
        this.trimDay = trimDay;
        this.db = db;

        init();
    }

    private void init() {
        todaySchedule();
        userSchedule();
        setDayText();
    }

    private void setDayText() {
        if (schoolSche.equals("")) {
            sche = userSche;
            scheAndName = userScheAndName;
        } else {
            sche = schoolSche + "\n" + userSche;
            scheAndName = schoolSche + "\n" + userScheAndName;
        }
        dayText = day + "\n" + sche;
    }

    private void userSchedule() {
        String schedule;

        try {
            String[] columns = {"schedule, num, name"};
            String where = " date = ? AND ((grade = ? AND class = ?) OR boolean_public = 1)";

            int grade = set.getInt("grade");
            int aClass = set.getInt("class");

            String[] at = {trimDay, String.valueOf(grade), String.valueOf(aClass)};
            Cursor cursor = db.query("userTable", columns,  where, at, null, null, null);

            while (cursor.moveToNext()) {
                schedule = cursor.getString(0);
                int num = cursor.getInt(1);
                String name = cursor.getString(2);
                map.put(schedule + "(" + name + ")", num);

                if (userSche.equals("")) {
                    userSche = schedule;
                    userScheAndName = schedule + "(" + name + ")";
                } else {
                    userSche += "\n" + schedule;
                    userScheAndName += "\n" + schedule + "(" + name + ")";
                }
            }
            cursor.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    //오늘의 일정 표시
    private void todaySchedule() {
        String schedule = "";

        try {
            String[] columns = {"schedule"};
            String where = " date = ?";
            String[] at = {trimDay};
            Cursor cursor = db.query("calendarTable", columns,  where, at, null, null, null);

            while (cursor.moveToNext()) {
                schedule = cursor.getString(0);
                map.put(schedule, -1);
            }
            cursor.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        schoolSche = schedule;
    }

    String[] getDayText() {
        return new String[]{String.valueOf(day), sche};
    }

    String getTrimDay() {
        return trimDay;
    }

    String getScheAndName() {
        return scheAndName;
    }

    HashMap<String, Integer> getMap() {
        return map;
    }
}