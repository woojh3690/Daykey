package woo.Daykey;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

/**
 * 달력
 */

class FmCalendar extends Fragment{
    TextView calendarTextView;
    GridView monthView;
    MonthAdapter monthAdapter;
    private int year;
    private int month;

    FmCalendar(int year, int month, TextView textView) {
        this.year = year;
        this.month = month;
        this.calendarTextView = textView;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view  = inflater.inflate(R.layout.flagment_calendar, container, false);
        monthView = (GridView)view.findViewById(R.id.monthView);
        monthAdapter = new MonthAdapter(view.getContext(), year, month);

        monthView.setAdapter(monthAdapter);
        monthView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                view.setSelected(true);
                MonthItem item = (MonthItem)monthAdapter.getItem(position);
                calendarTextView.setText(item.getDayText());
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
