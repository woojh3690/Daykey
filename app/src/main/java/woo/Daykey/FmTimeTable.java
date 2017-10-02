package woo.Daykey;

import android.app.Fragment;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class FmTimeTable extends Fragment {
    private Context mainContext;
    private SQLiteDatabase db;
    private TextView[] textViews = new TextView[35];
    private int grade = -1;
    private int aClass = -1;

    public FmTimeTable(Context mainContext, SQLiteDatabase db) {
        this.mainContext = mainContext;
        this.db = db;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.flagment_timetable, container, false);
        String packageName = mainContext.getPackageName();

        for(int i = 0; i < 35; i++) {
            String resName = "timetable_tv_" + (i + 1);
            int id = getResources().getIdentifier(resName, "id", packageName);
            textViews[i] = (TextView)view.findViewById(id);
        }
        setProfile();

        if ((grade == 0) || (aClass == 0)) {
            textViews[17].setText("프로필을 설정해 주세요");
        }
        setTextViews();
        return view;
    }

    void setTextViews() { //textview 설정
        String[] columns = {"first", "second", "third", "fourth", "fifth", "sixth", "seventh"};
        String where = "grade = ? and class = ?";
        String[] select = {String.valueOf(grade), String.valueOf(aClass)};
        Cursor cursor = db.query("timetable", columns, where, select, null, null, null);

        int temp = 0;
        while(cursor.moveToNext()) {
            for (int i = 0; i < 7; i++) {
                textViews[temp].setText(cursor.getString(i));
                temp++;
            }
        }
        cursor.close();
    }

    void setProfile() {
        SettingPreferences set = new SettingPreferences(mainContext);
        grade = set.getInt("grade");
        aClass = set.getInt("class");
    }

}
