package woo.Daykey;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.text.Html;

class NewsParsing extends Thread{
    private String htmlStr;
    private final String str6 = ">";
    private String[] url = new String[10];

    private SQLiteDatabase db;
    private SqlHelper SqlHelper;


    NewsParsing(Context mainContext) {
        SqlHelper = new SqlHelper(mainContext);
        db = SqlHelper.getReadableDatabase();
    }

    @Override
    public void run() {
        super.run();
        GetHtmlText getHtmlText = new GetHtmlText("http://www.daykey.hs.kr/daykey/0701/board/14117");
        this.htmlStr = getHtmlText.getHtmlString();
        findUrl();
        find();
    }

    //none;'> 위치 찾기
    private void find() {
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
        try {
            db = SqlHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("title", title);
            values.put("teacherName", teacherName);
            values.put("visitors", NumOfVisitors);
            values.put("date", date);
            values.put("url", url);
            db.insert("newsTable", null, values);

            SqlHelper.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void findUrl() {
        int finish = 0;
        for (int i = 0; finish != 10; i++) {
            final String str1 = "'";
            final String str2 = "1";
            if (str1.equals(changeType(i))) {
                if (str2.equals(changeType(i + 1))) {
                    if ("4".equals(changeType(i + 2))) {
                        if (str2.equals(changeType(i + 3))) {
                            if (str2.equals(changeType(i + 4))) {
                                if ("7".equals(changeType(i + 5))) {
                                    if (str1.equals(changeType(i + 6))) {
                                        url[finish] = htmlStr.substring(i + 9, i + 16);
                                        finish++;
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
