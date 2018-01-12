package woo.Daykey;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

class BoardParsing extends Thread{

    private String strUrl;
    private int num;

    private SQLiteDatabase db;

    BoardParsing(SQLiteDatabase db, String url, int num) {
        this.strUrl = url;
        this.num = num;
        this.db = db;
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
    private void insertNewsData(String title, String teacherName, String numOfVisitors, String date, String url) {
        //Log.i("finish 호출됨", title + " / " + teacherName + " / " + numOfVisitors + " / " + date + " / " + tempUrl);
        try {
            ContentValues values = new ContentValues();
            values.put("title", title);
            values.put("teacherName", teacherName);
            values.put("visitors", numOfVisitors);
            values.put("date", date);
            values.put("url", url);

            if (num == 1) {
                db.insert("newsTable", null, values);
            } else if (num == 2) {
                db.insert("homeTable", null, values);
            } else if (num == 3) {
                db.insert("sciTable", null, values);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }}
