package woo.Daykey;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.provider.CalendarContract;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static android.provider.ContactsContract.Directory.ACCOUNT_NAME;
import static java.lang.Integer.parseInt;
import static woo.Daykey.MainActivity.SqlHelper;
import static woo.Daykey.MainActivity.db;
import static woo.Daykey.MainActivity.getMainContext;

class CalendarDataParsing extends Thread{
    private int id;
    private String htmlString;
    private boolean account = false;
    private Context mainContext;

    @Override
    public void run() {
        super.run();
        mainContext = getMainContext();
        addCalendarAccount();
        String strUrl1 = "http://www.daykey.hs.kr/daykey/0204/schedule?section=1&schdYear=2017";
        String strUrl2 = "http://www.daykey.hs.kr/daykey/0204/schedule?section=2&schdYear=2017";
        getUrlToHTML(strUrl1);
        getUrlToHTML(strUrl2);
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
            Uri result = getMainContext().getContentResolver().insert(calUri, cv);

            assert result != null;
            id = Integer.parseInt(result.getLastPathSegment());

            SettingPreferences set = new SettingPreferences(mainContext);
            set.saveInt("id", id);
        } else {
            account = true;
        }
    }

    private boolean checkAccount() {
        boolean check = false;
        final Uri CALENDAR_URI = Uri.parse("content://com.android.calendar/calendars");
        final String[] FIELDS = {
                CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
                CalendarContract.Calendars._ID
        };

        ContentResolver contentResolver = getMainContext().getApplicationContext().getContentResolver();

        Cursor cursor = contentResolver.query(CALENDAR_URI, FIELDS, null, null, null);
        try {
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    String displayName = cursor.getString(0);
                    if (displayName.equals("학사일정")) {
                        check = true;
                        id = cursor.getInt(1);
                        SettingPreferences set = new SettingPreferences(mainContext);
                        set.saveInt("id", id);
                    }
                }
            }
        } catch (AssertionError ex) {
            ex.printStackTrace();
        }

        return check;
    }

    private void getUrlToHTML(String strUrl) {
        final String letter = ":", sstr = "s", estr = "e", Dstr = "D", astr = "a", tstr = "t";

        /*
        try{
            Log.i("getUrlToHTML", "실행됨");
            URL url = new URL(strUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection(); // URL 을 연결한 객체 생성.
            conn.setRequestMethod("GET"); // get 방식 통신
            conn.setDoOutput(true);       // 쓰기모드 지정
            conn.setDoInput(true);        // 읽기모드 지정
            conn.setUseCaches(false);     // 캐싱데이터를 받을지 안받을지
            conn.setDefaultUseCaches(false); // 캐싱데이터 디폴트 값 설정

            InputStream is = conn.getInputStream();        //input 스트림 개방

            StringBuilder builder = new StringBuilder();   //문자열을 담기 위한 객체
            BufferedReader reader = new BufferedReader(new InputStreamReader(is,"UTF-8"));  //문자열 셋 세팅
            String line;

            while ((line = reader.readLine()) != null) {
                builder.append(line).append("\n");
            }

            htmlString =  builder.toString();



        } catch (MalformedURLException | ProtocolException exception) {
            exception.printStackTrace();
        } catch (IOException io){
            io.printStackTrace();
        }*/

        GetHtmlText getHtmlText = new GetHtmlText(strUrl);
        htmlString = getHtmlText.getHtmlString();
        //:viewData 위치 찾기
        for (int i = 0; i < htmlString.length(); i++) {
            if (letter.equals(changeType(i))) {
                if (sstr.equals(changeType(i + 1))) {
                    if (estr.equals(changeType(i + 2))) {
                        if (tstr.equals(changeType(i + 3))) {
                            if (Dstr.equals(changeType(i + 4))) {
                                if (astr.equals(changeType(i + 5))) {
                                    if (tstr.equals(changeType(i + 6))) {
                                        if (astr.equals(changeType(i + 7))) {
                                            getDate(i+8);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }//:viewData 위치 찾기 끝!
    }

    private static final int FINISH = 0;
    private int check = 0; //값이 '0'이 되면은 중첩된 소괄호 까지 완전히 닫힌 것

    //num 값을 이용하여 소괄호 안의 문자열 가져오는 함수
    private void getDate(int num) {
        int distance = 0;

        //소괄호 사이에 거리를 distance 에 저장하기
        checkParentheses(num + distance);
        distance += 1;
        try {
            if (check != FINISH) {
                while (check != FINISH) {
                    checkParentheses(num + distance);
                    distance += 1;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        String tempBody = htmlString.substring(num + 1, num + distance - 8);
        final String[] schedule = tempBody.split("'");

        //만약 일정이 하루에 끝나지 않는다면
        if(schedule[3].equals(schedule[5])) {
            insertCalendarData(schedule[3], schedule[7]);
        } else {
            //Log.i(TAG, "일정 다름" + tempBody);
            String[] startSplit = schedule[3].split("/");
            String[] finishSplit = schedule[5].split("/");
            int startDate = parseInt(startSplit[2]);

            if (startSplit[1].equals(finishSplit[1])) { //일정이 같은달에 있나?
                while ( startDate != parseInt(finishSplit[2])) {
                    insertCalendarData(startSplit[0] + "/" + startSplit[1] + "/" +String.valueOf(startDate), schedule[7]);
                    startDate++;
                }
                insertCalendarData(startSplit[0] + "/" + startSplit[1] + "/" +String.valueOf(startDate), schedule[7]);
            } else {
                while ( startDate != parseInt(finishSplit[2])) {
                    insertCalendarData(startSplit[0] + "/" + startSplit[1] + "/" + String.valueOf(startDate), schedule[7]);
                    startDate++;

                    if (startDate > lastDate(startSplit[0] + startSplit[1] + startSplit[2])) {
                        startDate = 0;
                        int month = parseInt(startSplit[1]);
                        if (month > 12) {
                            month = 1;
                        }
                        startSplit[1] = fromChange(month);
                    }
                }

                String finalDate = startSplit[0] + "/" + startSplit[1] + "/" + String.valueOf(startDate);

                insertCalendarData(finalDate, schedule[7]);

                if (account) {
                    new AddCalendar(id, finalDate, schedule[7]);//구글 캘린더에 스케줄 추가
                }
            }
        }
    }


    //num 위치에 소괄호가 확인되면은 True 를 반환하는 함수
    private void checkParentheses(int num) {
        final String leftParenthese = "(";
        final String rightParenthese = ")";

        if (leftParenthese.equals(changeType(num))) {
            check += 1;
        } else if (rightParenthese.equals(changeType(num))){
            check -= 1;
        }
    }

    //location 값에 있는 result 의 문자값 하나를 반환
    private String changeType(int location) {
        return String.valueOf(htmlString.charAt(location) );
    }

    private void insertCalendarData(String date, String schedule) {
        try {
            db = SqlHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("date", date);
            values.put("schedule", schedule);
            db.insert("calendarTable", null, values);
            //SqlHelper.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    //마지막일 구하는 함수
    private int lastDate(String date) {
        SimpleDateFormat transeDate = new SimpleDateFormat("yyyyMMdd");//"yyyyMMdd"형식의 데이터포맷의 틀을 만든다.

        //String의 날짜를 Date로 형변환
        Date tdate = null;
        try {
            tdate = transeDate.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar cal = Calendar.getInstance();//Calendar형으로 시스템날짜를 가져온다.
        cal.setTime(tdate);//Date형의 입력받은 날짜를 Calendar형으로 변환한다.
        return cal.getActualMaximum(Calendar.DAY_OF_MONTH);//입력받은 날짜의 그달의 마지막일을 구한다.
    }

    //int 1이면 string 01로변경
    private String fromChange(int num) {
        String result;
        String date1 = String.valueOf(num);

        if (date1.length() == 1) {
            result = "0" + date1;
        } else if (date1.length() == 2) {
            result = date1;
        } else {
            result = null;
        }

        return result;
    }

}