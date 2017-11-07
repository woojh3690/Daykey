package woo.Daykey;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.net.Uri;
import android.provider.CalendarContract;

import java.util.Calendar;
import java.util.TimeZone;

import static android.provider.CalendarContract.CONTENT_URI;
import static android.provider.ContactsContract.Directory.ACCOUNT_NAME;

class AddCalendar {//extends Thread{
    private int id;
    private String[] startTime;
    SQLiteDatabase database;
    private String title;
    private Context mainContext;

    AddCalendar(Context mainContext) {
        //Log.i("addCalendar : " , "시작됨");
        SettingPreferences settingPreferences = new SettingPreferences(mainContext);
        this.mainContext = mainContext;
        this.id = settingPreferences.getInt("id");
        addCalendarAccount();
        add();
    }

    AddCalendar(Context mainContext, int id, String startTime, String title) {
        this.mainContext = mainContext;
        this.id = id;
        this.startTime = startTime.split("/");
        this.title = title;
        addAllDay();
    }

    private void addAllDay() {
        Calendar beginTime = Calendar.getInstance();
        beginTime.set(Integer.parseInt(startTime[0]), Integer.parseInt(startTime[1]) - 1, Integer.parseInt(startTime[2]), 0, 0);
        beginTime.add(Calendar.DATE, 1);
        Calendar endTime = Calendar.getInstance();
        endTime.set(Integer.parseInt(startTime[0]), Integer.parseInt(startTime[1]) - 1, Integer.parseInt(startTime[2]), 0, 0);
        endTime.add(Calendar.DATE, 1);

        ContentValues cv = new ContentValues();
        cv.put("calendar_id", id);
        cv.put("title", title);
        cv.put(CalendarContract.Events.DTSTART, beginTime.getTimeInMillis());
        cv.put(CalendarContract.Events.DTEND, beginTime.getTimeInMillis());
        cv.put("allDay", 1);
        cv.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().getID());
        mainContext.getContentResolver().insert(Uri.parse(CONTENT_URI + "/events"), cv);
    }

    private void add() {
        SQLiteOpenHelper sqLiteOpenHelper = new SqlHelper(mainContext);

        try {
            database = sqLiteOpenHelper.getReadableDatabase();
            String[] columns = {"date", "schedule"};
            Cursor cursor = database.query("calendarTable", columns, null, null, null, null, null);

            while (cursor.moveToNext()) {
                startTime = cursor.getString(0).split("/");
                title = cursor.getString(1);
                addAllDay();
            }
            cursor.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            database.close();
        }
    }

    private void addCalendarAccount() {

        if (!checkAccount()) {
            Uri calUri = CalendarContract.Calendars.CONTENT_URI;

            ContentValues cv = new ContentValues();
            cv.put(CalendarContract.Calendars.ACCOUNT_NAME, "Daykey");
            cv.put(CalendarContract.Calendars.ACCOUNT_TYPE, CalendarContract.ACCOUNT_TYPE_LOCAL);
            cv.put(CalendarContract.Calendars.NAME, "대기고등학교");
            cv.put(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME, "학사일정");
            cv.put(CalendarContract.Calendars.CALENDAR_COLOR, Color.parseColor("#ff44ff"));
            cv.put(CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL, CalendarContract.Calendars.CAL_ACCESS_OWNER);
            cv.put(CalendarContract.Calendars.OWNER_ACCOUNT, true);
            cv.put(CalendarContract.Calendars.VISIBLE, 1);
            cv.put(CalendarContract.Calendars.SYNC_EVENTS, 1);

            calUri = calUri.buildUpon()
                    .appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true")
                    .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_NAME, ACCOUNT_NAME)
                    .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE, CalendarContract.ACCOUNT_TYPE_LOCAL)
                    .build();
            Uri result = mainContext.getContentResolver().insert(calUri, cv);

            assert result != null;
            id = Integer.parseInt(result.getLastPathSegment());

            SettingPreferences set = new SettingPreferences(mainContext);
            set.saveInt("id", id);
        }
    }

    private boolean checkAccount() {
        boolean check = false;
        final Uri CALENDAR_URI = Uri.parse("content://com.android.calendar/calendars");
        final String[] FIELDS = {
                CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
                CalendarContract.Calendars._ID
        };

        ContentResolver contentResolver = mainContext.getApplicationContext().getContentResolver();

        try (Cursor cursor = contentResolver.query(CALENDAR_URI, FIELDS, null, null, null)) {
            assert cursor != null;
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    String displayName = cursor.getString(0);
                    if (displayName.equals("학사일정")) {
                        check = true;
                        int accountId = cursor.getInt(1);
                        SettingPreferences set = new SettingPreferences(mainContext);
                        set.saveInt("id", accountId);
                    }
                }
            }
        } catch (AssertionError ex) {
            ex.printStackTrace();
        }

        return check;
    }
}
