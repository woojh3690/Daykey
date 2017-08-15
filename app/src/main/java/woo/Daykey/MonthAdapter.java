package woo.Daykey;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

class MonthAdapter extends BaseAdapter {
    private Context mainContext;
    private Calendar mCalendar;

    private MonthItem[] items;
    private int firstDay; //1일의 요일
    private int lastDay; //마직막 일
    private int setYear = 1; //보이는 달력 위치에 년도
    private int setMonth = 2; //보이는 달력 위치에 달

    MonthAdapter(Context context, int year, int month) {
        mainContext = context;
        this.setYear = year;
        this.setMonth = month;
        items = new MonthItem[7 * 6];

        Date date = new Date();
        mCalendar = Calendar.getInstance();
        mCalendar.setTime(date);

        recalculate();
        resetDayNumbers();
    }

    private void resetDayNumbers() {
        for (int i = 0; i < 42; i++) {
            int dayNumber = (i+1) - firstDay;
            if (dayNumber < 1 || dayNumber > lastDay) {
                dayNumber = 0;
            }

            items[i] = new MonthItem(dayNumber, setYear + "/" + fromChange(setMonth + 1) + "/" + fromChange(dayNumber));
        }
    }

    //보여지는 년도, 달, 마지막 일 계산
    private void recalculate() {
        //날짜 세팅
        mCalendar.set(Calendar.YEAR, setYear);
        mCalendar.set(Calendar.MONTH, setMonth);
        mCalendar.set(Calendar.DAY_OF_MONTH, 1);

        firstDay = mCalendar.get(Calendar.DAY_OF_WEEK) - 1;//시작하는 요일 확인
        lastDay = mCalendar.getActualMaximum(Calendar.DATE); //마지막 일
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MonthItemView view;

        if (convertView == null) {
            view = new MonthItemView(mainContext);
        } else {
            view = (MonthItemView) convertView;
        }

        if (!Objects.equals(items[position].dayText, "")) {
            view.setDay(items[position].dayText);
        }

        return view;
    }

    //이전달 세팅
    void setPreviousMonth() {
        mCalendar.add(Calendar.MONTH, -1);
        getDate();
        resetDayNumbers();
    }

    //다음달 세팅
    void setNextMonth() {
        mCalendar.add(Calendar.MONTH, 1);
        getDate();
        resetDayNumbers();
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

    private void getDate() {
        setYear = mCalendar.get(Calendar.YEAR);
        setMonth = mCalendar.get(Calendar.MONTH);
        firstDay = mCalendar.get(Calendar.DAY_OF_WEEK) - 1;
        lastDay = mCalendar.getActualMaximum(Calendar.DATE);
    }

    @Override //전체 달력칸 반환
    public int getCount() {
        return 7 * 6;
    }

    @Override //아이템 반환
    public Object getItem(int position) {
        return items[position];
    }

    @Override //아이템 위치 반환
    public long getItemId(int position) {
        return position;
    }

    //보여지는 년도 반환
    int getCurrentYear() {
        return setYear;
    }

    //보여지는 달 반환
    int getCurrentMonth() {
        return setMonth + 1;
    }
}
