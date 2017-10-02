package woo.Daykey;

import android.app.Activity;
import android.app.Fragment;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.Calendar;

public class FmSchedule extends Fragment {
    CalendarAdapter calendarAdapter;
    TextView monthText, calendarTextView;
    Activity activity;
    Calendar calendar = Calendar.getInstance();
    Button addSche;
    View view;
    private SQLiteDatabase db;
    private int mSelectedPageIndex = 1;
    private ViewPager viewPager;
    final FmCalendar[] fragList = new FmCalendar[3];

    public FmSchedule(SQLiteDatabase db) {
        this.db = db;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i("schedule", "onCreate");
        view = inflater.inflate(R.layout.flagment_schedule, container, false);
        viewPager = (ViewPager) view.findViewById(R.id.viewPager);
        monthText = (TextView) view.findViewById(R.id.monthText);
        calendarTextView = (TextView)view.findViewById(R.id.calendarTextView);
        addSche = (Button)view.findViewById(R.id.add_schedule);

        monthText.setText(calendar.get(Calendar.YEAR) + "년 " + (calendar.get(Calendar.MONTH) + 1) + "월");
        activity = getActivity();

        addFmCalendar();

        calendarAdapter = new CalendarAdapter(getChildFragmentManager(), fragList);
        calendarAdapter.notifyDataSetChanged();
        viewPager.setAdapter(calendarAdapter);
        viewPager.setCurrentItem(1, false);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mSelectedPageIndex = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == ViewPager.SCROLL_STATE_IDLE) {
                    if (mSelectedPageIndex < 1) {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                fragList[0].onPreviousMonth();
                            }
                        });

                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                fragList[1].onPreviousMonth();
                            }
                        });

                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                fragList[2].onPreviousMonth();
                            }
                        });
                    } else if (mSelectedPageIndex > 1) {

                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                fragList[0].onNextMonth();
                            }
                        });

                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                fragList[1].onNextMonth();
                            }
                        });

                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                fragList[2].onNextMonth();
                            }
                        });
                    }
                    monthText.setText(fragList[1].getCalendarTitle());
                    viewPager.setCurrentItem(1, false);
                }
            }
        });

        return view;
    }

    public void addFmCalendar() {
        fragList[1] = new FmCalendar(db, calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH), calendarTextView, addSche);//현재달

        calendar.add(Calendar.MONTH, -1);
        fragList[0] = new FmCalendar(db, calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH), calendarTextView, addSche);//이전달

        calendar.add(Calendar.MONTH, 2);
        fragList[2] = new FmCalendar(db, calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH), calendarTextView, addSche);//다음달
    }
}