package woo.Daykey;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.HashMap;

import static woo.Daykey.FmSchedule.addSche;
import static woo.Daykey.FmSchedule.calendarTextView;

/**
 * 달력
 */

public class FmCalendar extends Fragment{
    private MonthAdapter monthAdapter;
    private int year;
    private int month;
    static HashMap<String, Integer> map;
    static String scheAndName = null;
    static String trimDate = null;
    FmSchedule fmSchedule;

    public FmCalendar() {
    }

//    @Override
//    public void onAttach(Context context) {
//
//        super.onAttach(context);
//        MainActivity mainActivity;
//
//        if (context instanceof Activity) {
//            Log.i("액티비티 확인 : ", " ㅇㅇ");
//            mainActivity = (MainActivity) context;
//            fmSchedule = (FmSchedule)mainActivity.getFragmentManager().findFragmentById(R.id.schedule);
//        }
//    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        year = getArguments().getInt("year");
        month = getArguments().getInt("month");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.flagment_calendar, container, false);
        GridView monthView = view.findViewById(R.id.monthView);
        monthAdapter = new MonthAdapter(view.getContext(), year, month);

        monthView.setAdapter(monthAdapter);
        monthView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                view.setSelected(true);
                MonthItem item = (MonthItem)monthAdapter.getItem(position);
                String[] listDayText = item.getDayText();
                trimDate = item.getTrimDay();
                scheAndName = item.getScheAndName().replace(", ", "\n");
                calendarTextView.setText(scheAndName);
                if (listDayText[0].equals("0")) {
                    addSche.setText("일정 추가");
                } else {
                    addSche.setText(listDayText[0] + "일\n일정 추가");
                }
                map = item.getMap();
            }
        });
        return view;
    }

    void onPreviousMonth() {
        monthAdapter.setPreviousMonth();
        monthAdapter.notifyDataSetChanged();
    }

    void onNextMonth() {
        monthAdapter.setNextMonth();
        monthAdapter.notifyDataSetChanged();
    }

    String getCalendarTitle() {
        return monthAdapter.getCurrentYear() + "년 " + monthAdapter.getCurrentMonth() + "월";
    }
}