package woo.Daykey;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.android.gms.common.internal.Objects;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

import static woo.Daykey.MainActivity.db;

class SqlHelper extends SQLiteOpenHelper {
    private final String[] match = {"newsTable", "homeTable", "sciTable"};
    Context context;

    SqlHelper(Context context) {
        super(context, "Database.db", null, 3);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL("create table " + "dietTable " + "(date INTEGER, menu text);");
            db.execSQL("create table " + "calendarTable " + "(date text, schedule text);");
            db.execSQL("create table " + "timetable " + "(grade integer, week text, class integer, first text, second text, third text, fourth text, fifth text, sixth text, seventh text)");
            db.execSQL("create table " + "userTable" + "(num integer, name text, grade integer, class integer, date text, schedule text, boolean_public integer)");

            insertTimeTable(db); //시간표
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i("onUpgrade", "업그레이드 호출됨");
        String sql = "drop table if exists " + "timetable";
        String create = "create table " + "timetable " + "(grade integer, week text, class integer, first text, second text, third text, fourth text, fifth text, sixth text, seventh text)";
        try {
            db.execSQL(sql);
            db.execSQL(create);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        insertTimeTable(db);
    }

    public boolean boardParsing(int num) {
        final String[] matchUrl= {"http://www.daykey.hs.kr/daykey/0701/board/14117",
                "http://www.daykey.hs.kr/daykey/0601/board/14114", "http://www.daykey.hs.kr/daykey/19516/board/20170"};
        if (GetWhatKindOfNetwork.check(context)) {
            try {
                SQLiteDatabase db = super.getWritableDatabase();
                db.execSQL("drop table if exists " + String.valueOf(match[num]));
                db.execSQL("create table " + String.valueOf(match[num]) + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, title text, teacherName text, visitors text, date text, url text);");

                Thread thread = new BoardParsing(matchUrl[num], num);
                thread.start();
                thread.join();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        } else {
            return false;
        }
    }

    public String[][] getBoardData(int num) {
        String[] columns = {"title", "teacherName", "visitors", "date"};
        String[][] data = new String[10][4];
        try {
            SQLiteDatabase db = super.getWritableDatabase();
            Cursor cursor = db.query(match[num], columns, null, null, null, null, null);
            int i = 0;
            while (cursor.moveToNext()) {
                data[i][0] = cursor.getString(0);
                data[i][1] = cursor.getString(1);
                data[i][2] = cursor.getString(2);
                data[i][3] = cursor.getString(3);
                i++;
            }
            cursor.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return data;
    }

    private class BoardParsing extends Thread {

        private String strUrl;
        private int num;

        BoardParsing(String url, int num) {
            this.strUrl = url;
            this.num = num;
        }

        @Override
        public void run() {
            super.run();
            try {
                Document doc = Jsoup.connect(strUrl).get();
                Elements table = doc.select("table.wb");
                table = table.select("tbody");
                Elements tr = table.select("tr");

                for (int i = 0; i < tr.size(); i++) {
                    Element elementsTitle = tr.get(i).select("td").get(1);
                    String title = elementsTitle.text();
                    String num = tr.get(i).select("td").get(0).text();
                    if (num.equals("")) {
                        title = "[공지]"+ title;
                    }

                    Elements temp = elementsTitle.select("img");
                    if(temp.size() != 0) {
                        title += "(new)";
                    }

                    String teacherName = tr.get(i).select("td").get(2).text();
                    String numOfVisitors = tr.get(i).select("td").get(3).text();
                    String date = tr.get(i).select("td").get(4).text();
                    String tempUrl = elementsTitle.select("a").attr("onclick");
                    tempUrl = tempUrl.substring(27, 34);

                    insertNewsData(title, teacherName, numOfVisitors, date, tempUrl);
                }
                //Log.i("보드 : ", "데이터 : " + table.text());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //테이블에 공지사항 데이터 쓰기
        void insertNewsData(String title, String teacherName, String numOfVisitors, String date, String url) {
            //Log.i("finish 호출됨", title + " / " + teacherName + " / " + numOfVisitors + " / " + date + " / " + tempUrl);
            try {
                ContentValues values = new ContentValues();
                values.put("title", title);
                values.put("teacherName", teacherName);
                values.put("visitors", numOfVisitors);
                values.put("date", date);
                values.put("url", url);
                db.insert(match[num], null, values);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    //기본 보충시간표
    private void insertTimeTable(SQLiteDatabase db) {
        db.execSQL("insert into timetable values('1','월','1','한문','체이','수송','국현','사홍','자율, 동아리','자율, 동아리');");
        db.execSQL("insert into timetable values('1','월','2','과유','영웅','체이','한문','탐이','자율, 동아리','자율, 동아리');");
        db.execSQL("insert into timetable values('1','월','3','수송','국현','사현','체이','과유','자율, 동아리','자율, 동아리');");
        db.execSQL("insert into timetable values('1','월','4','영문','한문','진김','수송','체이','자율, 동아리','자율, 동아리');");
        db.execSQL("insert into timetable values('1','월','5','국맹','史한','영문','사현','수송','자율, 동아리','자율, 동아리');");
        db.execSQL("insert into timetable values('1','월','6','진김','탐이','논신','과유','영웅','자율, 동아리','자율, 동아리');");
        db.execSQL("insert into timetable values('1','월','7','史정','논신','음유','사홍','史한','자율, 동아리','자율, 동아리');");
        db.execSQL("insert into timetable values('1','월','8','사현','국맹','영웅','史정','수제','자율, 동아리','자율, 동아리');");
        db.execSQL("insert into timetable values('1','월','9','탐이','수제','과유','과민','영문','자율, 동아리','자율, 동아리');");
        db.execSQL("insert into timetable values('1','월','10','수제','공오','사홍','국맹','음유','자율, 동아리','자율, 동아리');");
        db.execSQL("insert into timetable values('1','화','1','史한','수송','영웅','영문','사고','史범','과수');");
        db.execSQL("insert into timetable values('1','화','2','과수','국현','수송','과이','史범','사고','史한');");
        db.execSQL("insert into timetable values('1','화','3','공오','영문','과수','史범','수송','과이','史정');");
        db.execSQL("insert into timetable values('1','화','4','과유','영웅','史범','사고','史한','과수','국현');");
        db.execSQL("insert into timetable values('1','화','5','체이','한문','과이','과수','국현','사홍','수송');");
        db.execSQL("insert into timetable values('1','화','6','국맹','체이','史정','수제','사홍','영웅','한문');");
        db.execSQL("insert into timetable values('1','화','7','수제','국맹','사고','진김','과이','한문','영문');");
        db.execSQL("insert into timetable values('1','화','8','사고','진김','사홍','한문','수제','영문','과유');");
        db.execSQL("insert into timetable values('1','화','9','史정','사홍','음유','국맹','체이','과유','수제');");
        db.execSQL("insert into timetable values('1','화','10','한문','과유','진김','史정','국맹','체이','영웅');");
        db.execSQL("insert into timetable values('1','수','1','史정','국맹','원문','과이','과유','사공','체이');");
        db.execSQL("insert into timetable values('1','수','2','국맹','체이','음유','원문','사공','史정','수송');");
        db.execSQL("insert into timetable values('1','수','3','원문','수송','체이','국맹','진김','사고','한문');");
        db.execSQL("insert into timetable values('1','수','4','공오','음유','史정','수송','국맹','체이','원윤');");
        db.execSQL("insert into timetable values('1','수','5','사고','원윤','과유','한문','史정','수송','국맹');");
        db.execSQL("insert into timetable values('1','수','6','국현','史한','사고','사공','음유','영문','수제');");
        db.execSQL("insert into timetable values('1','수','7','영웅','국현','한문','과민','史한','수제','과유');");
        db.execSQL("insert into timetable values('1','수','8','과유','과민','영웅','국현','탐이','한문','史한');");
        db.execSQL("insert into timetable values('1','수','9','史한','사공','수제','사고','국현','진김','영웅');");
        db.execSQL("insert into timetable values('1','수','10','사공','수제','과민','과유','영웅','탐이','사고');");
        db.execSQL("insert into timetable values('1','목','1','수송','진김','음유','논현','공오','영문','과유');");
        db.execSQL("insert into timetable values('1','목','2','사현','영문','공오','과유','진김','논현','수송');");
        db.execSQL("insert into timetable values('1','목','3','음유','국맹','史한','영문','수송','사공','한문');");
        db.execSQL("insert into timetable values('1','목','4','국맹','사공','수송','과이','과유','한문','영문');");
        db.execSQL("insert into timetable values('1','목','5','사공','탐이','진김','수송','체이','음유','영웅');");
        db.execSQL("insert into timetable values('1','목','6','수제','체이','원재','과민','과이','국맹','사현');");
        db.execSQL("insert into timetable values('1','목','7','체이','과유','수제','사공','원재','공오','탐이');");
        db.execSQL("insert into timetable values('1','목','8','원웅','공오','체이','수제','과호','史한','사공');");
        db.execSQL("insert into timetable values('1','목','9','史한','원웅','사현','한문','국맹','과호','수제');");
        db.execSQL("insert into timetable values('1','목','10','한문','국현','과호','원웅','수제','사현','史한');");
        db.execSQL("insert into timetable values('1','금','1','국맹','사현','수송','탐이','한문','자율활동','방과후활동');");
        db.execSQL("insert into timetable values('1','금','2','한문','국맹','사홍','영문','수송','자율활동','방과후활동');");
        db.execSQL("insert into timetable values('1','금','3','논현','과유','탐이','사홍','영웅','자율활동','방과후활동');");
        db.execSQL("insert into timetable values('1','금','4','탐이','수송','사현','논현','사홍','자율활동','방과후활동');");
        db.execSQL("insert into timetable values('1','금','5','영문','史한','공오','과유','논현','자율활동','방과후활동');");
        db.execSQL("insert into timetable values('1','금','6','과유','공오','한문','수제','史한','자율활동','방과후활동');");
        db.execSQL("insert into timetable values('1','금','7','수제','체이','영웅','국맹','사현','자율활동','방과후활동');");
        db.execSQL("insert into timetable values('1','금','8','체이','논신','수제','음유','국맹','자율활동','방과후활동');");
        db.execSQL("insert into timetable values('1','금','9','논신','영웅','체이','한문','공오','자율활동','방과후활동');");
        db.execSQL("insert into timetable values('1','금','10','史한','영문','논신','체이','수제','자율활동','방과후활동');");
        db.execSQL("insert into timetable values('2','월','1','음고','체정','수강','미김','미김','자율, 동아리','자율, 동아리');");
        db.execSQL("insert into timetable values('2','월','2','생람','문현','영재','음고','수공','자율, 동아리','자율, 동아리');");
        db.execSQL("insert into timetable values('2','월','3','문광','미김','윤홍','영재','생람','자율, 동아리','자율, 동아리');");
        db.execSQL("insert into timetable values('2','월','4','영재','윤홍','수승','문현','논홍','자율, 동아리','자율, 동아리');");
        db.execSQL("insert into timetable values('2','월','5','문현','물수','문문','수강','수승','자율, 동아리','자율, 동아리');");
        db.execSQL("insert into timetable values('2','월','6','수이','생람','문광','수공','영상','자율, 동아리','자율, 동아리');");
        db.execSQL("insert into timetable values('2','월','7','영윤','문문','지김','문광','수강','자율, 동아리','자율, 동아리');");
        db.execSQL("insert into timetable values('2','월','8','수공','수이','영윤','문문','지김','자율, 동아리','자율, 동아리');");
        db.execSQL("insert into timetable values('2','월','9','영상','음고','물수','영윤','영재','자율, 동아리','자율, 동아리');");
        db.execSQL("insert into timetable values('2','월','10','지김','영상','수공','수이','영윤','자율, 동아리','자율, 동아리');");
        db.execSQL("insert into timetable values('2','화','1','문광','수승','영윤','수강','수공','사현','윤홍');");
        db.execSQL("insert into timetable values('2','화','2','수승','사현','문광','영윤','윤홍','미김','수강');");
        db.execSQL("insert into timetable values('2','화','3','수강','수공','윤홍','음고','문광','음유','사현');");
        db.execSQL("insert into timetable values('2','화','4','영윤','미김','사현','윤홍','음고','수승','문광');");
        db.execSQL("insert into timetable values('2','화','5','음고','수이','과강','미김','화봉','생람','지김');");
        db.execSQL("insert into timetable values('2','화','6','미김','과강','문현','수공','수승','영윤','미김');");
        db.execSQL("insert into timetable values('2','화','7','체정','음고','수강','영재','생람','화봉','문현');");
        db.execSQL("insert into timetable values('2','화','8','지김','체정','영재','문현','미김','과강','수이');");
        db.execSQL("insert into timetable values('2','화','9','생람','화봉','체정','수이','지김','문현','영재');");
        db.execSQL("insert into timetable values('2','화','10','영재','생람','화봉','체정','수강','수이','음고');");
        db.execSQL("insert into timetable values('2','수','1','영재','지홍','문현','윤홍','생람','수승','사현');");
        db.execSQL("insert into timetable values('2','수','2','사현','체정','지홍','영재','윤홍','문현','수강');");
        db.execSQL("insert into timetable values('2','수','3','윤홍','사현','수승','체정','문현','영재','지홍');");
        db.execSQL("insert into timetable values('2','수','4','문광','영재','생람','사현','체정','지홍','윤홍');");
        db.execSQL("insert into timetable values('2','수','5','수이','문광','체정','수공','영재','미김','물수');");
        db.execSQL("insert into timetable values('2','수','6','체정','화봉','수이','음고','물수','화봉','생람');");
        db.execSQL("insert into timetable values('2','수','7','음유','수승','과강','미김','화봉','생람','수이');");
        db.execSQL("insert into timetable values('2','수','8','물수','생람','화봉','수강','문광','음유','음고');");
        db.execSQL("insert into timetable values('2','수','9','미김','과강','수강','물수','문문','수이','문광');");
        db.execSQL("insert into timetable values('2','수','10','문현','문문','수공','수승','미김','과강','미김');");
        db.execSQL("insert into timetable values('2','목','1','문현','수승','영윤','지홍','문광','지현','윤홍');");
        db.execSQL("insert into timetable values('2','목','2','문광','지현','지홍','수승','음유','윤홍','미김');");
        db.execSQL("insert into timetable values('2','목','3','수강','윤홍','지현','영윤','수승','문광','지홍');");
        db.execSQL("insert into timetable values('2','목','4','미김','수강','윤홍','문현','지현','지홍','음유');");
        db.execSQL("insert into timetable values('2','목','5','수이','음유','생람','화봉','문현','영윤','수공');");
        db.execSQL("insert into timetable values('2','목','6','영윤','물수','문문','수이','지김','수강','문현');");
        db.execSQL("insert into timetable values('2','목','7','지김','수공','문현','물수','수이','문문','영윤');");
        db.execSQL("insert into timetable values('2','목','8','수승','미김','화봉','생람','문문','수공','수강');");
        db.execSQL("insert into timetable values('2','목','9','수공','문문','미김','수강','화봉','생람','수승');");
        db.execSQL("insert into timetable values('2','목','10','물수','수이','문광','문문','생람','화봉','지김');");
        db.execSQL("insert into timetable values('2','금','1','윤홍','음유','영재','논김','문광','자율활동','방과후활동');");
        db.execSQL("insert into timetable values('2','금','2','영윤','문광','논김','수승','윤홍','자율활동','방과후활동');");
        db.execSQL("insert into timetable values('2','금','3','수승','영윤','미김','논홍','문현','자율활동','방과후활동');");
        db.execSQL("insert into timetable values('2','금','4','수공','수승','문광','영윤','수강','자율활동','방과후활동');");
        db.execSQL("insert into timetable values('2','금','5','지김','문문','영상','수강','영윤','자율활동','방과후활동');");
        db.execSQL("insert into timetable values('2','금','6','음유','수강','지김','영재','문문','자율활동','방과후활동');");
        db.execSQL("insert into timetable values('2','금','7','물수','영상','수이','수공','미김','자율활동','방과후활동');");
        db.execSQL("insert into timetable values('2','금','8','영재','물수','문현','수이','영상','자율활동','방과후활동');");
        db.execSQL("insert into timetable values('2','금','9','수이','지김','수공','문현','음유','자율활동','방과후활동');");
        db.execSQL("insert into timetable values('2','금','10','수강','문현','음유','물수','영재','자율활동','방과후활동');");
        db.execSQL("insert into timetable values('3','월','1','사공','수박','수문','지조','영경','자율,동아리','자율, 동아리');");
        db.execSQL("insert into timetable values('3','월','2','독양','영경','사공','논임','지조','자율,동아리','자율, 동아리');");
        db.execSQL("insert into timetable values('3','월','3','수오','윤고','수박','체정','사공','자율,동아리','자율, 동아리');");
        db.execSQL("insert into timetable values('3','월','4','지조','논양','윤고','수오','체최','자율,동아리','자율, 동아리');");
        db.execSQL("insert into timetable values('3','월','5','독손','지조','수오','영경','윤고','자율,동아리','자율, 동아리');");
        db.execSQL("insert into timetable values('3','월','6','물고','독손','생고','지현','수김','자율,동아리','자율, 동아리');");
        db.execSQL("insert into timetable values('3','월','7','수김','생고','물고','독양','수박','자율,동아리','자율, 동아리');");
        db.execSQL("insert into timetable values('3','월','8','독임','영지','수김','물고','수송','자율,동아리','자율, 동아리');");
        db.execSQL("insert into timetable values('3','월','9','동범','화김','체최','독손','영지','자율,동아리','자율, 동아리');");
        db.execSQL("insert into timetable values('3','월','10','지현','동범','수송','화봉','생고','자율,동아리','자율, 동아리');");
        db.execSQL("insert into timetable values('3','월','11','독강','지현','화김','수송','동범','자율,동아리','자율, 동아리');");
        db.execSQL("insert into timetable values('3','월','12','영지','독임','화봉','독강','화김','자율,동아리','자율, 동아리');");
        db.execSQL("insert into timetable values('3','화','1','수오','독손','논문','사공','논양','논임','지조');");
        db.execSQL("insert into timetable values('3','화','2','영상','사공','수오','논문','독손','논양','체정');");
        db.execSQL("insert into timetable values('3','화','3','독손','수문','독임','영경','수오','지조','논문');");
        db.execSQL("insert into timetable values('3','화','4','영지','독신','지조','수문','사공','독손','수오');");
        db.execSQL("insert into timetable values('3','화','5','영경','지조','독신','독강','수박','수문','사공');");
        db.execSQL("insert into timetable values('3','화','6','독임','수박','영상','수송','독신','영경','화김');");
        db.execSQL("insert into timetable values('3','화','7','지현','화김','수송','독임','독강','영상','독신');");
        db.execSQL("insert into timetable values('3','화','8','독양','독강','화김','지현','수송','생고','영상');");
        db.execSQL("insert into timetable values('3','화','9','물고','지현','수박','수김','체최','영지','수송');");
        db.execSQL("insert into timetable values('3','화','10','수김','영지','물고','생고','지현','체최','독강');");
        db.execSQL("insert into timetable values('3','화','11','독강','생고','체최','영지','물고','수박','수김');");
        db.execSQL("insert into timetable values('3','화','12','생고','영경','독양','체최','수김','물고','지현');");
        db.execSQL("insert into timetable values('3','수','1','중김','독임','수문','영경','지조','독강','체정');");
        db.execSQL("insert into timetable values('3','수','2','동범','중김','독손','지조','독임','수문','영경');");
        db.execSQL("insert into timetable values('3','수','3','논임','독신','수오','중김','영지','지조','논양');");
        db.execSQL("insert into timetable values('3','수','4','지조','영경','수박','독강','중김','수오','동범');");
        db.execSQL("insert into timetable values('3','수','5','체최','지조','독임','수오','독양','중김','영지');");
        db.execSQL("insert into timetable values('3','수','6','논오','독강','수김','영지','체최','생고','지현');");
        db.execSQL("insert into timetable values('3','수','7','수김','동범','영지','화김','독강','독손','물고');");
        db.execSQL("insert into timetable values('3','수','8','영지','수박','독신','물고','지현','수김','논오');");
        db.execSQL("insert into timetable values('3','수','9','수송','영상','물고','지현','화김','독신','생고');");
        db.execSQL("insert into timetable values('3','수','10','독양','수송','생고','영상','독손','화김','독임');");
        db.execSQL("insert into timetable values('3','수','11','영경','생고','독양','독손','물고','수송','영상');");
        db.execSQL("insert into timetable values('3','수','12','독손','화김','지현','수송','영상','수박','독신');");
        db.execSQL("insert into timetable values('3','목','1','독신','영상','윤고','지조','수오','체정','독손');");
        db.execSQL("insert into timetable values('3','목','2','윤고','독신','영지','수문','수박','수오','지조');");
        db.execSQL("insert into timetable values('3','목','3','동범','독손','영상','체정','수문','지조','독양');");
        db.execSQL("insert into timetable values('3','목','4','독손','수문','체최','논양','영상','논임','윤고');");
        db.execSQL("insert into timetable values('3','목','5','수오','지조','동범','논임','체최','윤고','영상');");
        db.execSQL("insert into timetable values('3','목','6','수송','체최','독양','영경','독손','동범','화김');");
        db.execSQL("insert into timetable values('3','목','7','체최','화김','논오','영지','수송','영경','생고');");
        db.execSQL("insert into timetable values('3','목','8','영경','생고','수송','체최','독강','화김','동범');");
        db.execSQL("insert into timetable values('3','목','9','독양','수박','독강','생고','독임','수김','영경');");
        db.execSQL("insert into timetable values('3','목','10','독강','영경','수박','화김','독신','영지','수김');");
        db.execSQL("insert into timetable values('3','목','11','생강','생강','독임','수김','생고','수박','독신');");
        db.execSQL("insert into timetable values('3','목','12','생고','수김','생강','생강','영지','독강','수송');");
        db.execSQL("insert into timetable values('3','금','1','윤고','수오','동범','영지','독양','자율활동','방과후활동');");
        db.execSQL("insert into timetable values('3','금','2','지조','독강','윤고','수오','체정','자율활동','방과후활동');");
        db.execSQL("insert into timetable values('3','금','3','사공','영경','지조','독강','윤고','자율활동','방과후활동');");
        db.execSQL("insert into timetable values('3','금','4','독임','독양','사공','영경','지조','자율활동','방과후활동');");
        db.execSQL("insert into timetable values('3','금','5','논양','수문','논임','독손','사공','자율활동','방과후활동');");
        db.execSQL("insert into timetable values('3','금','6','화김','수김','물고','생람','수송','자율활동','방과후활동');");
        db.execSQL("insert into timetable values('3','금','7','수송','생람','체최','지현','수김','자율활동','방과후활동');");
        db.execSQL("insert into timetable values('3','금','8','생람','화김','수김','체최','독손','자율활동','방과후활동');");
        db.execSQL("insert into timetable values('3','금','9','생강','생강','화봉','생고','독강','자율활동','방과후활동');");
        db.execSQL("insert into timetable values('3','금','10','물고','수박','생강','생강','체최','자율활동','방과후활동');");
        db.execSQL("insert into timetable values('3','금','11','화봉','체최','지현','화김','영지','자율활동','방과후활동');");
        db.execSQL("insert into timetable values('3','금','12','체최','생고','수박','물고','동범','자율활동','방과후활동');");
    }
}
