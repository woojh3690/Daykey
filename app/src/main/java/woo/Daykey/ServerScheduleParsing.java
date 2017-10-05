package woo.Daykey;

import android.content.ContentValues;
import android.database.sqlite.SQLiteAbortException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 서버에 있는 스케줄 json으로 파싱하고 데이터 베이스에 넣기
 */

class ServerScheduleParsing {
    SQLiteDatabase db;

    ServerScheduleParsing(SQLiteDatabase db) {
        Log.i("serverschedule", "싲가도");
        this.db = db;
        reset();
        insert();
    }

    void reset() {
        String drop = "drop table if exists " + "userTable";
        String create = "create table " + "userTable" + "(num integer, name text, grade integer, class integer, date text, schedule text)";
        try {
            db.execSQL(drop);
            db.execSQL(create);
        } catch (SQLiteAbortException e) {
            e.printStackTrace();
        }
    }

    void insert() {
        GetHtmlText getHtmlText = new GetHtmlText("http://wooserver.iptime.org/daykey/schedule");
        String strJson = getHtmlText.getHtmlString();

        try {
            JSONArray jsonArray = new JSONArray(strJson);
            for(int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                int num = Integer.parseInt(jsonObject.getString("num"));
                String name = jsonObject.getString("name");
                int grade = Integer.parseInt(jsonObject.getString("grade"));
                int intClass = Integer.parseInt(jsonObject.getString("class"));
                String date = jsonObject.getString("year") + jsonObject.getString("month")
                        + jsonObject.getString("date");
                String sche = jsonObject.getString("sche");

                Log.i("data : ", num + name + grade + intClass + date + sche);
                insertCalendarData(num, name, grade, intClass, date, sche);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void insertCalendarData(int num, String name, int grade, int intClass, String date, String schedule) {
        try {
            ContentValues values = new ContentValues();
            values.put("num", num);
            values.put("name", name);
            values.put("grade", grade);
            values.put("class", intClass);
            values.put("date", date);
            values.put("schedule", schedule);
            db.insert("userTable", null, values);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
