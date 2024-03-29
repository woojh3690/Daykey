package woo.Daykey;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.legacy.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
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
    private Calendar mCalendar;
    private int startDay = 1;
    private int jump = 4;
    private int loopTime = 5;
    private int curDay;
    private int firstDayOfWeek = 0;

    public FmDiet() {
        this.mCalendar = Calendar.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.flagment_diet, container, false);
        viewPager = view.findViewById(R.id.dietViewPager);

        recalculate(); //시작하는 요일, 마지막 일 저장

        for(int i = 0; i < 5; i++) {
            weekTvList[i] = view.findViewById(weekList[i]);
        }

        dietAdapter = new DietAdapter(getChildFragmentManager(), loopTime);
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
                Log.i("포지션 : ", String.valueOf(position));
                setWeekText(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });
        return view;
    }

    private void setAdapter() {
        FmChidedDiet fmChidedDiet = new FmChidedDiet();
        Bundle args = new Bundle();
        args.putInt("firstWeek", firstDayOfWeek - 2);
        args.putInt("start", startDay);
        args.putInt("finish", startDay + jump);
        fmChidedDiet.setArguments(args);
        dietAdapter.setFlagList(0, fmChidedDiet);

        startDay = startDay + jump + 3;
        jump = 4;

        for (int i = 1; i < loopTime; i++) {
            fmChidedDiet = new FmChidedDiet();
            args = new Bundle();
            args.putInt("firstWeek", 0);
            args.putInt("start",  startDay);
            args.putInt("finish", startDay + jump);
            fmChidedDiet.setArguments(args);
            dietAdapter.setFlagList(i, fmChidedDiet);
            startDay += 7;
        }
    }

    private void recalculate() {
        //날짜 세팅
        curDay = mCalendar.get(Calendar.DATE);
        mCalendar.set(Calendar.DAY_OF_MONTH, 1);

        firstDayOfWeek = mCalendar.get(Calendar.DAY_OF_WEEK);
        int sumDay = firstDayOfWeek - 1;

        //시작하는 요일에따라
        if (firstDayOfWeek == Calendar.SUNDAY) {//일요일이면
            startDay++;
            firstDayOfWeek = 2;
        } else if (firstDayOfWeek == Calendar.SATURDAY) {//토요일이면
            startDay += 2;
            firstDayOfWeek = 2;
            loopTime = 4;
            sumDay = -1;
        } else {
            jump -= (firstDayOfWeek - 2);
        }

        curDay = (curDay + sumDay) / 7;

        if (curDay >= loopTime) {
            curDay = loopTime - 1;
        }
    }

    private class DietAdapter extends FragmentPagerAdapter{
        private FmChidedDiet[] flagList;

        DietAdapter(FragmentManager fm, int pageSize) {
            super(fm);
            flagList = new FmChidedDiet[pageSize];
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
