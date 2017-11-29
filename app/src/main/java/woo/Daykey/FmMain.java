package woo.Daykey;

import android.app.Fragment;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static woo.Daykey.MainActivity.db;
import static woo.Daykey.MainActivity.set;

public class FmMain extends Fragment {

    TextView dietViewMain1;
    TextView dietViewMain2;
    TextView timer;
    private boolean goTime = true;

    public FmMain() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.flagment_main, container, false);
        timer = (TextView)view.findViewById(R.id.timer);
        dietViewMain1 = (TextView) view.findViewById(R.id.dietViewMain1);
        dietViewMain2 = (TextView) view.findViewById(R.id.dietViewMain2);
        todayMenuPrint();

        TextView mainCalendarView = (TextView) view.findViewById(R.id.mainCalendarView);
        todaySchedulePrint(mainCalendarView);
        return view;
    }

    void todayMenuPrint() {
        try {
            String[] columns = {"date", "menu"};
            String where = "date = " + dateNow();
            Cursor cursor = db.query("dietTable", columns, where, null, null, null, null);

            int checkNum = 0;
            while(cursor.moveToNext()) {
                String menu = cursor.getString(1);

                if (checkNum == 0) {
                    dietViewMain1.setText("점심 : " + menu + "");
                    checkNum = 1;
                } else  {
                    dietViewMain2.setText("저녁 : " + menu + "");
                }
            }

            cursor.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void todaySchedulePrint(TextView calendar) {
        try {
            String[] columns = {"date", "schedule"};
            String where = " date = ?";
            String[] at = { dateNowFull() };
            Cursor cursor = db.query("calendarTable", columns,  where, at, null, null, null);

            while (cursor.moveToNext()) {
                String schedule = cursor.getString(1);
                calendar.setText(schedule);
            }
            cursor.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public String dateNow() {
        // 현재시간을 msec 으로 구한다.
        long now = System.currentTimeMillis();
        // 현재시간을 date 변수에 저장한다.
        Date date = new Date(now);
        // 시간을 나타냇 포맷을 정한다 ( yyyy/MM/dd 같은 형태로 변형 가능 )
        SimpleDateFormat sdfNow = new SimpleDateFormat("d");
        // nowDate 변수에 값을 저장한다.

        return sdfNow.format(date);
    }

    static String dateNowFull() {
        // 현재시간을 msec 으로 구한다.
        long now = System.currentTimeMillis();
        // 현재시간을 date 변수에 저장한다.
        Date date = new Date(now);
        // 시간을 나타냇 포맷을 정한다 ( yyyy/MM/dd 같은 형태로 변형 가능 )
        SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy/MM/dd");
        // nowDate 변수에 값을 저장한다.

        return sdfNow.format(date);
    }

    private class AsyncTaskTimer extends AsyncTask<Void, Void, Void> {
        Calendar cal;
        String timeCre;

        @Override
        protected void onPreExecute() {
            cal = new GregorianCalendar();
            timeCre = String.format("%d:%s", cal.get(Calendar.HOUR), formChange(cal.get(Calendar.MINUTE)));
            timer.setText(timeCre);
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            while (set.getBoolean("timer") && goTime) {
                cal = new GregorianCalendar();
                timeCre = String.format("%d:%s", cal.get(Calendar.HOUR), formChange(cal.get(Calendar.MINUTE)));
                publishProgress();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                timeCre = String.format("%d %s", cal.get(Calendar.HOUR), formChange(cal.get(Calendar.MINUTE)));
                publishProgress();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            timer.setText(" ");
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            //Log.i("test", timeCre);
            timer.setText(timeCre);
            super.onProgressUpdate(values);
        }
    }

    @Override
    public void onPause() {
        goTime = false;
        super.onPause();
    }

    @Override
    public void onResume() {
        goTime = true;
        AsyncTaskTimer asyncTaskTimer = new AsyncTaskTimer();
        asyncTaskTimer.execute();
        super.onResume();
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