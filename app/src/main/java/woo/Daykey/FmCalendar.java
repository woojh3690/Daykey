package woo.Daykey;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 달력
 */

public class FmCalendar extends Fragment{
    private Context mainContext;
    private SQLiteDatabase db;
    private View view;
    TextView calendarTextView;
    GridView monthView;
    MonthAdapter monthAdapter;
    Button add_sche;
    private int year;
    private int month;
    static String trimDate = null;

    public FmCalendar() {
    }

    FmCalendar(SQLiteDatabase db, Context mainContext, int year, int month, TextView textView, Button add_sche) {
        this.mainContext = mainContext;
        this.db = db;
        this.year = year;
        this.month = month;
        this.calendarTextView = textView;
        this.add_sche = add_sche;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view  = inflater.inflate(R.layout.flagment_calendar, container, false);
        monthView = (GridView)view.findViewById(R.id.monthView);
        monthAdapter = new MonthAdapter(view.getContext(), db, year, month);
        add_sche.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (trimDate == null || trimDate.endsWith("00")) {
                    Toast.makeText(mainContext, "날짜를 선택해주세요.", Toast.LENGTH_SHORT).show();
                } else {
                    dialogShow();
                }
            }
        });

        monthView.setAdapter(monthAdapter);
        monthView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                view.setSelected(true);
                MonthItem item = (MonthItem)monthAdapter.getItem(position);
                String[] listDayText = item.getDayText();
                trimDate = item.getTrimDay();
                calendarTextView.setText(listDayText[1].replace(",", "\n"));
                add_sche.setText(listDayText[0] + "일\n일정\n추가");
            }
        });
        return view;
    }

    private void dialogShow() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mainContext);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_schedule, null);
        builder.setView(dialogView);
        final Button submit = (Button) dialogView.findViewById(R.id.buttonSubmit);
        final TextView textDate = (TextView) dialogView.findViewById(R.id.textViewDate);
        final EditText editTextSche = (EditText) dialogView.findViewById(R.id.editTextSchedule);

        textDate.setText(trimDate); //텍스트 날짜 설정

        final AlertDialog dialog = builder.create();
        submit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String strSche = editTextSche.getText().toString();
                Log.i("test", strSche);
                dialog.dismiss();
            }
        });

        dialog.show();

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
