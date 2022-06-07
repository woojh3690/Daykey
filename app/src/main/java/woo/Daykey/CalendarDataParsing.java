package woo.Daykey;

import android.content.ContentValues;

import android.content.Context;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static java.lang.Integer.parseInt;
import static woo.Daykey.MainActivity.db;
import static woo.Daykey.MainActivity.set;

class CalendarDataParsing extends Thread{
    private String htmlString;
    private Context context;

    CalendarDataParsing(Context context) {
        this.context = context;
    }

    @Override
    public void run() {
        super.run();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy", Locale.KOREA);
        SimpleDateFormat formatter2 = new SimpleDateFormat("MM", Locale.KOREA);
        Date date = new Date();
        int year = Integer.parseInt(formatter.format(date));
        int month = Integer.parseInt(formatter2.format(date));
        if (month < 3) {
            year -= 1;
        }

        dropCalendarTable();
        getUrlToHTML(MainActivity.baseUrl + "/daykey/0204/schedule?section=1&schdYear=" + year);
        getUrlToHTML(MainActivity.baseUrl + "/daykey/0204/schedule?section=2&schdYear=" + year);

        if (set.getBoolean("calendar")) {
            CalendarManager calendarManager = new CalendarManager(context);
            if (calendarManager.deleteAccount()) {
                if (calendarManager.addAccount()) {
                    calendarManager.addSchedule();
                }
            }
        }
    }

    private void dropCalendarTable() {
        try {
            db.execSQL("drop table if exists" + " calendarTable");
            db.execSQL("create table " + "calendarTable " + "(date text, schedule text);");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getUrlToHTML(String strUrl) {
        final String letter = ":", sstr = "s", estr = "e", Dstr = "D", astr = "a", tstr = "t";
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
                        startSplit[1] = formChange(month);
                    }
                }

                String finalDate = startSplit[0] + "/" + startSplit[1] + "/" + String.valueOf(startDate);

                insertCalendarData(finalDate, schedule[7]);
            }
        }
    }


    //num 위치에 소괄호가 확인되면은 True 를 반환하는 함수
    private void checkParentheses(int num) {
        if ("(".equals(changeType(num))) {
            check += 1;
        } else if (")".equals(changeType(num))){
            check -= 1;
        }
    }

    //location 값에 있는 result 의 문자값 하나를 반환
    private String changeType(int location) {
        return String.valueOf(htmlString.charAt(location) );
    }

    private void insertCalendarData(String date, String schedule) {
        try {
            ContentValues values = new ContentValues();
            values.put("date", date);
            values.put("schedule", schedule);
            db.insert("calendarTable", null, values);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    //마지막일 구하는 함수
    private int lastDate(String date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");//"yyyyMMdd"형식의 데이터포맷의 틀을 만든다.

        //String의 날짜를 Date로 형변환
        Date tdate = null;
        try {
            tdate = dateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar cal = Calendar.getInstance();//Calendar형으로 시스템날짜를 가져온다.
        cal.setTime(tdate);//Date형의 입력받은 날짜를 Calendar형으로 변환한다.
        return cal.getActualMaximum(Calendar.DAY_OF_MONTH);//입력받은 날짜의 그달의 마지막일을 구한다.
    }

    //int 1이면 string 01로변경
    private String formChange(int num) {
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