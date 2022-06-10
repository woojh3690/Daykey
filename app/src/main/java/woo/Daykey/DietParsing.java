package woo.Daykey;

import static woo.Daykey.MainActivity.db;

import android.content.ContentValues;
import android.text.Html;
import android.util.Log;

import java.util.Calendar;

class DietParsing {
    private static final String TAG = "DietParsing";
    private static final int FINISH = 0;

    private String htmlSr;
    private String string1 = "새", string2 = "창", string3 = "열", string4 = "림", string5 = "\"", string6 = ">";
    private String aString1 = "<", aString2 = "/", aString3 = "a", aString4 = ">";
    private String tempBody;

    private ContentValues values;
    private SettingPreferences set;

    private int htmlInt;
    private int check = 1; //값이 '0'이 되면은 중첩된 소괄호 까지 완전히 닫힌 것
    private boolean insertCheck = false;

    DietParsing(SettingPreferences set) {
        this.set = set;
    }

    public void parse(String html) {
        htmlSr = html;
        htmlInt = htmlSr.length();
        find();
    }

    //새창열림\> 위치 찾기
    private void find() {
        for (int i = 0; i < htmlInt; i++) {
            if (string1.equals(changeType(i))) {
                if (string2.equals(changeType(i + 1))) {
                    if (string3.equals(changeType(i + 2))) {
                        if (string4.equals(changeType(i + 3))) {
                            if (string5.equals(changeType(i + 4))) {
                                if (string6.equals(changeType(i + 5))) {
                                    getDate(i + 6);
                                }
                            }
                        }
                    }
                }
            }
        } //:viewData 위치 찾기 끝!
    }

    //num 값을 이용하여 소괄호 안의 문자열 가져오는 함수
    private void getDate(int num) {
        int distance = 0;
        check = 1;

        //소괄호 사이에 거리를 distance 에 저장하기
        while (check != FINISH) {
            checkParentheses(num + distance);
            distance += 1;
        }

        tempBody = htmlSr.substring(num, num + distance);

        boolean result1 = tempBody.startsWith("개인정보처리방침");
        boolean result2 = tempBody.startsWith("저작권지침및신고");
        boolean result3 = tempBody.startsWith("공공데이터");
        boolean result4 = tempBody.startsWith("이메일무단수집거부");
        boolean result5 = tempBody.startsWith("영상정보처리기기 설치·운영 계획");

        //쓸데 없는 정보 걸러네기
        if (result1) {
            Log.i(TAG, "result1 true");
        } else if (result2) {
            Log.i(TAG, "result2 true");
        } else if (result3) {
            Log.i(TAG, "result3 true");
        } else if (result4) {
            Log.i(TAG, "result4 true");
        } else if (result5) {
            Log.i(TAG, "result5 true");
        } else {
            trim(tempBody);
        }
    }


    //num 위치에 </a>가 확인되면은 check = 0
    private void checkParentheses(int num) {
        //</a> 위치 찾기
        if (aString1.equals(changeType(num))) {
            if (aString2.equals(changeType(num + 1))) {
                if (aString3.equals(changeType(num + 2))) {
                    if (aString4.equals(changeType(num + 3))) {
                        check = 0;
                    }
                }
            }
        }
    }

    //String 식단 가공
    private void trim(String string) {
        String date = string.substring(0, 2).trim();
        String tempMenu = "?";

        //String 식단을 메뉴만 잘라내기
        for (int i = 78; i < string.length(); i++) {
            if (aString4.equals(String.valueOf(string.charAt(i)))) {
                if (aString1.equals(String.valueOf(string.charAt(i + 1)))) {
                    tempMenu = string.substring(78, i + 1);
                }
            }
        }

        //특수문자 해석
        tempMenu = String.valueOf(Html.fromHtml(tempMenu, Html.FROM_HTML_MODE_LEGACY));
        tempMenu = tempMenu.replace(".", "").replaceAll("\\d", "");
        tempMenu = tempMenu.replace("()", "");

        if (tempMenu.startsWith(aString4)) {
            tempMenu = tempMenu.substring(1);
        }

        //<br>을 enter 로 치환한다음 insertCalendarData 함수 호출
        insertDietData(Integer.parseInt(date), tempMenu);
    }


    //location 값에 있는 result 의 문자값 하나를 반환
    private String changeType(int location) {
        return String.valueOf(htmlSr.charAt(location));
    }

    //테이블에 급식 데이터 쓰기
    private void insertDietData(int date, String menu) {
        try {
            values = new ContentValues();
            values.put("date", date);
            values.put("menu", menu);
            db.insert("dietTable", null, values);

            if (!insertCheck) {
                //급식 DB 버전 저장
                Calendar calendar = Calendar.getInstance();
                int today = calendar.get(Calendar.MONTH);
                set.saveInt("db_version", today);
                this.insertCheck = true;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
