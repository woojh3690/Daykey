package woo.Daykey;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.SQLException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static woo.Daykey.FmSchedule.addSche;
import static woo.Daykey.FmSchedule.calendarTextView;
import static woo.Daykey.MainActivity.db;
import static woo.Daykey.MainActivity.set;

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

    public FmCalendar() {
        //init();
    }

    FmCalendar(int year, int month) {
        this.year = year;
        this.month = month;
        //init();
    }

//    private void init() {
//        LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        assert inflater != null;
//        View view = inflater.inflate(R.layout.flagment_schedule, null);
//        this.calendarTextView = (TextView)view.findViewById(R.id.calendarTextView);
//        this.addSche = (Button)view.findViewById(R.id.add_schedule);
//        this.deleteSche = (Button)view.findViewById(R.id.delete_schedule);
//    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.flagment_calendar, container, false);
        GridView monthView = (GridView) view.findViewById(R.id.monthView);
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
                addSche.setText(listDayText[0] + "일\n일정 추가");
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