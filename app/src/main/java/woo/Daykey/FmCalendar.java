package woo.Daykey;

import android.app.Fragment;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

/**
 * 달력
 */

public class FmCalendar extends Fragment{
    private SQLiteDatabase db;
    TextView calendarTextView;
    GridView monthView;
    MonthAdapter monthAdapter;
    Button add_sche;
    private int year;
    private int month;

    public FmCalendar() {
    }

    FmCalendar(SQLiteDatabase db, int year, int month, TextView textView, Button add_sche) {
        this.db = db;
        this.year = year;
        this.month = month;
        this.calendarTextView = textView;
        this.add_sche = add_sche;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view  = inflater.inflate(R.layout.flagment_calendar, container, false);
        monthView = (GridView)view.findViewById(R.id.monthView);
        monthAdapter = new MonthAdapter(view.getContext(), db, year, month);
        add_sche.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        monthView.setAdapter(monthAdapter);
        monthView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                view.setSelected(true);
                MonthItem item = (MonthItem)monthAdapter.getItem(position);
                String[] listDayText = item.getDayText();
                calendarTextView.setText(listDayText[1].replace(",", "\n"));
                add_sche.setText(listDayText[0] + "일\n일정\n추가");
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
