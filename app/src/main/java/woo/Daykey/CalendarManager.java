package woo.Daykey;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.provider.CalendarContract;

import java.util.Calendar;
import java.util.TimeZone;

import static android.provider.CalendarContract.CONTENT_URI;
import static android.provider.ContactsContract.Directory.ACCOUNT_NAME;
import static woo.Daykey.MainActivity.set;
import static woo.Daykey.MainActivity.db;

public class CalendarManager {
    private Context context;
    private int id;

    CalendarManager(Context context) {
        this.context = context;
        this.id = set.getInt("id");
    }

    public boolean addAccount() {
        if (!checkAccount()) {
            Uri calUri = CalendarContract.Calendars.CONTENT_URI;

            ContentValues cv = new ContentValues();
            cv.put(CalendarContract.Calendars.ACCOUNT_NAME, "Daykey");
            cv.put(CalendarContract.Calendars.ACCOUNT_TYPE, CalendarContract.ACCOUNT_TYPE_LOCAL);
            cv.put(CalendarContract.Calendars.NAME, "대기고등학교");
            cv.put(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME, "학사일정");
            cv.put(CalendarContract.Calendars.CALENDAR_COLOR, Color.parseColor(set.getString("color")));
            cv.put(CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL, CalendarContract.Calendars.CAL_ACCESS_OWNER);
            cv.put(CalendarContract.Calendars.OWNER_ACCOUNT, true);
            cv.put(CalendarContract.Calendars.VISIBLE, 1);
            cv.put(CalendarContract.Calendars.SYNC_EVENTS, 1);

            calUri = calUri.buildUpon()
                    .appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true")
                    .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_NAME, ACCOUNT_NAME)
                    .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE, CalendarContract.ACCOUNT_TYPE_LOCAL)
                    .build();
            Uri result = context.getContentResolver().insert(calUri, cv);

            assert result != null;
            id = Integer.parseInt(result.getLastPathSegment());
            set.saveInt("id", id);

            return true;
        } else {
            return false;
        }
    }

    public boolean deleteAccount() {
        if (checkAccount()) {
            context.getContentResolver().delete (ContentUris.withAppendedId (CalendarContract.Calendars.CONTENT_URI, set.getInt("id")), null, null);
        }
        return true;
    }

    public void addSchedule() {
        id = set.getInt("id");
        if(id == -1) {
            return;
        }

        try {
            String[] columns = {"date", "schedule"};
            Cursor cursor = db.query("calendarTable", columns, null, null, null, null, null);

            while (cursor.moveToNext()) {
                String[] startTime = cursor.getString(0).split("/");
                String title = cursor.getString(1);
                addOneDay(startTime, title);
            }
            cursor.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void addOneDay(String[] startTime, String title) {
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
        context.getContentResolver().insert(Uri.parse(CONTENT_URI + "/events"), cv);
    }

    private boolean checkAccount() {
        boolean check = false;
        final Uri CALENDAR_URI = Uri.parse("content://com.android.calendar/calendars");
        final String[] FIELDS = {
                CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
                CalendarContract.Calendars._ID
        };

        ContentResolver contentResolver = context.getApplicationContext().getContentResolver();

        try (Cursor cursor = contentResolver.query(CALENDAR_URI, FIELDS, null, null, null)) {
            assert cursor != null;
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    String displayName = cursor.getString(0);
                    if (displayName.equals("학사일정")) {
                        check = true;
                        int accountId = cursor.getInt(1);
                        SettingPreferences set = new SettingPreferences(context);
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
