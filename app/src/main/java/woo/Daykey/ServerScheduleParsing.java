package woo.Daykey;

import android.content.ContentValues;
import android.database.sqlite.SQLiteAbortException;
import android.os.Handler;
import android.os.Message;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static woo.Daykey.MainActivity.db;

/**
 * 서버에 있는 스케줄 json으로 파싱하고 데이터 베이스에 넣기
 */

class ServerScheduleParsing extends Thread{
    private Handler handler = MainActivity.mhandler;
    private boolean send;

    ServerScheduleParsing(boolean value) {
        this.send = value;
    }

    @Override
    public void run() {
        super.run();
        reset();
        insert();
        if (send) {
            Message message = handler.obtainMessage();
            message.what = 2;
            handler.sendMessage(message);
        }
    }

    private void reset() {
        String drop = "drop table if exists " + "userTable";
        String create = "create table " + "userTable" + "(num integer, name text, " +
                "grade integer, class integer, password integer, date text, schedule text, boolean_public integer)";
        try {
            db.execSQL(drop);
            db.execSQL(create);
        } catch (SQLiteAbortException e) {
            e.printStackTrace();
        }
    }

    private void insert() {
        GetHtmlText getHtmlText = new GetHtmlText("http://wooserver.iptime.org/daykey/schedule");
        String strJson = getHtmlText.getHtmlString();

        try {
            JSONArray jsonArray = new JSONArray(strJson);
            for(int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                //변수 저장
                int num = Integer.parseInt(jsonObject.getString("num"));
                String name = jsonObject.getString("name");
                int grade = Integer.parseInt(jsonObject.getString("grade"));
                int intClass = Integer.parseInt(jsonObject.getString("class_"));
                int password = Integer.parseInt(jsonObject.getString("password"));
                String date = jsonObject.getString("year") + "/"
                        + jsonObject.getString("month") + "/" + jsonObject.getString("date");
                String sche = jsonObject.getString("sche");
                byte boolean_public = (byte)jsonObject.getInt("booleanPublic");


                insertCalendarData(num, name, grade, intClass, password, date, sche, boolean_public);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void insertCalendarData(int num, String name, int grade, int intClass, int password, String date, String schedule, byte boolean_public) {
        try {
            ContentValues values = new ContentValues();
            values.put("num", num);
            values.put("name", name);
            values.put("grade", grade);
            values.put("class", intClass);
            values.put("password", password);
            values.put("date", date);
            values.put("schedule", schedule);
            values.put("boolean_public", boolean_public);
            db.insert("userTable", null, values);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
