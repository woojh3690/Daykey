package woo.Daykey;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.text.Html;
import android.util.Log;

class BoardParsing extends Thread{
    private String htmlStr = "noData";
    private final String str6 = ">";
    private String[] url = new String[10];
    private String strUrl;
    private int num;

    private SQLiteDatabase db;
    private SqlHelper SqlHelper;

    BoardParsing(Context mainContext, String url, int num) {
        this.strUrl = url;
        this.num = num;
        this.SqlHelper = new SqlHelper(mainContext);
        this.db = SqlHelper.getReadableDatabase();
    }

    @Override
    public void run() {
        super.run();
        GetHtmlText getHtmlText = new GetHtmlText(strUrl);
        this.htmlStr = getHtmlText.getHtmlString();

        if(!htmlStr.equals("noData")) {
            findUrl();
            find();
        }
    }

    //none;'> 위치 찾기
    private void find() {
        try {
            int finish = 0;
            for (int i = 0; finish != 10; i++) {
                final String str1 = "n";
                if (str1.equals(changeType(i))) {
                    if ("o".equals(changeType(i + 1))) {
                        if (str1.equals(changeType(i + 2))) {
                            if ("e".equals(changeType(i + 3))) {
                                if (";".equals(changeType(i + 4))) {
                                    if ("'".equals(changeType(i + 5))) {
                                        if (str6.equals(changeType(i + 6))) {
                                            getDate(i+7, finish);
                                            finish++;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    //num 값을 이용하여 소괄호 안의 문자열 가져오는 함수
    private void getDate(int num, int finish) {
        try {
            int distance = 0;

            for (int i = 0; distance == 0; i++) {
                int temp = i + num;
                if ("<".equals(changeType(temp))) {
                    if ("/".equals(changeType(temp + 1))) {
                        if ("t".equals(changeType(temp + 2))) {
                            if ("r".equals(changeType(temp + 3))) {
                                if (str6.equals(changeType(temp + 4))) {
                                    distance = i;
                                }
                            }
                        }
                    }
                }
            }

            //문자열 가공
            String tempStr = htmlStr.substring(num + 1, num + distance - 1);
            String[] arr = tempStr.split("</a>");
            String tempTitle = arr[0];
            String[] info = arr[1].split("<td>");
            String[] teacherName = info[1].split("</td>");
            String[] NumOfVisitors = info[2].split("</td>");
            String[] date = info[3].split("</td>");

            if (tempTitle.endsWith("&nbsp;")) { //새로운 개시글인지 확인
                Log.i("temptitle", tempTitle);
                tempTitle = tempTitle.substring(0, tempTitle.length() - 91) + " (new)";
            }

            String title;
            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.N) {
                // noinspection deprecation
                title = String.valueOf(Html.fromHtml(tempTitle));
            } else {
                title = String.valueOf(Html.fromHtml(tempTitle, Html.FROM_HTML_MODE_LEGACY));
            }

            insertNewsData(title, teacherName[0], NumOfVisitors[0], date[0], url[finish]); //저장
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //location 값에 있는 htmlStr 의 문자값 하나를 반환
    private String changeType(int location) {
        return String.valueOf(htmlStr.charAt(location));
    }

    //테이블에 공지사항 데이터 쓰기
    private void insertNewsData(String title, String teacherName, String NumOfVisitors, String date, String url) {
        //Log.i("finish 호출됨", title+ teacherName+ NumOfVisitors+ date+ url);
        try {
            db = SqlHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("title", title);
            values.put("teacherName", teacherName);
            values.put("visitors", NumOfVisitors);
            values.put("date", date);
            values.put("url", url);

            if (num == 1) {
                db.insert("newsTable", null, values);
            } else if (num == 2) {
                db.insert("homeTable", null, values);
            } else if (num == 3) {
                db.insert("sciTable", null, values);
            }

            SqlHelper.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void findUrl() {
        int finish = 0;
        for (int i = 0; finish != 10; i++) {
            final String str1 = "'" ;
            String[] str2 = {};

            if (num == 1) {
                str2 = new String[]{"1", "4", "1", "1", "7"};
            } else if (num == 2) {
                str2 = new String[]{"1", "4", "1", "1", "4"};
            } else if (num == 3) {
                str2 = new String[]{"2", "0", "1", "7", "0"};
            }

            if (str1.equals(changeType(i))) {
                if (str2[0].equals(changeType(i + 1))) {
                    if (str2[1].equals(changeType(i + 2))) {
                        if (str2[2].equals(changeType(i + 3))) {
                            if (str2[3].equals(changeType(i + 4))) {
                                if (str2[4].equals(changeType(i + 5))) {
                                    if (str1.equals(changeType(i + 6))) {
                                        url[finish] = htmlStr.substring(i + 9, i + 16);
                                        finish++;
                                        //Log.i("확인", num + " ");
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}