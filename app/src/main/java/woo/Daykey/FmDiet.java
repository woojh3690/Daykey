package woo.Daykey;

import android.app.Fragment;
import android.app.FragmentManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Calendar;

public class FmDiet extends Fragment {
    int[] weekList = {R.id.mon, R.id.tue, R.id.wed, R.id.thu, R.id.fri};
    TextView[] weekTvList = new TextView[5];
    ViewPager viewPager;
    DietAdapter dietAdapter;
    Calendar mCalendar = Calendar.getInstance();
    private int startDay = 1;
    private int jump = 4;
    private int loopTime = 5;
    private int curDay;
    private int firstDayOfWeek;
    private SQLiteDatabase db;

    public FmDiet(SQLiteDatabase db) {
        this.db = db;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.flagment_diet, container, false);
        viewPager = (ViewPager)view.findViewById(R.id.dietViewPager);
        dietAdapter = new DietAdapter(getChildFragmentManager());
        for(int i = 0; i < 5; i++) {
            weekTvList[i] = (TextView)view.findViewById(weekList[i]);
        }

        recalculate(); //시작하는 요일, 마지막 일 저장
        setAdapter();
        setWeekText(curDay);
        viewPager.setAdapter(dietAdapter);
        viewPager.setCurrentItem(curDay, false);
        //Log.i("curDay : ", ""+curDay);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                setWeekText(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });
        return view;
    }

    private void setAdapter() {
        int i;
        dietAdapter.setFlagList(0, new FmChidedDiet(db, firstDayOfWeek, startDay, startDay + jump));
        startDay = startDay + jump + 3;
        jump = 4;

        for (i = 1; i < loopTime; i++) {
            dietAdapter.setFlagList(i, new FmChidedDiet(db, startDay, startDay + jump));
            startDay += 7;
        }

        if (loopTime == 4) {
            dietAdapter.setFlagList(i, new FmChidedDiet(db, startDay, startDay));
        }
    }

    private void recalculate() {
        //날짜 세팅
        curDay = mCalendar.get(Calendar.DATE);
        mCalendar.set(Calendar.DAY_OF_MONTH, 1);

        firstDayOfWeek = mCalendar.get(Calendar.DAY_OF_WEEK);
        Log.i("firstDayOfWeek : ", ""+ firstDayOfWeek);
        int sumDay = firstDayOfWeek - 1;

        //시작하는 요일에따라
        if (firstDayOfWeek == Calendar.SUNDAY) {//일요일이면
            startDay++;
        } else if (firstDayOfWeek == Calendar.SATURDAY) {//토요일이면
            startDay += 2;
            loopTime = 4;
        } else {
            jump -= (firstDayOfWeek - 2);
        }

        curDay = (curDay + sumDay) / 7;
    }

    private class DietAdapter extends FragmentPagerAdapter{
        private FmChidedDiet[] flagList = new FmChidedDiet[5];

        DietAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return flagList[position];
        }

        @Override
        public int getCount() {
            return flagList.length;
        }

        void setFlagList(int num, FmChidedDiet fmChidedDiet) {
            flagList[num] = fmChidedDiet;
        }

        int[] getStartFinish(int position) {
            int[] array = new int[2];
            array[0] = flagList[position].getStart();
            array[1] = flagList[position].getFinish();
            return array;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {

        }
    }

    void setWeekText(int position) {
        for (int i = 0; i < 5; i++) { //초기화
            weekTvList[i].setText(" ");
        }

        int[] list = dietAdapter.getStartFinish(position);
        for (int i = 0; list[0] <= list[1]; list[0]++) {
            if (list[0] > 0) {
                String temp = list[0] + "일";
                weekTvList[i].setText(temp);
            }
            i++;
        }
    }
}
