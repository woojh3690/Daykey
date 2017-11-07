package woo.Daykey;

import android.app.Fragment;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import static woo.Daykey.MainActivity.db;

public class FmTimeTable extends Fragment {
    private TextView[] textViews = new TextView[35];
    private int grade = -1;
    private int aClass = -1;

    public FmTimeTable() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.flagment_timetable, container, false);
        String packageName = getActivity().getPackageName();

        for(int i = 0; i < 35; i++) {
            String resName = "timetable_tv_" + (i + 1);
            int id = getResources().getIdentifier(resName, "id", packageName);
            textViews[i] = (TextView)view.findViewById(id);
        }
        setProfile();

        if ((grade == -1) || (aClass == -1)) {
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
        SettingPreferences set = new SettingPreferences(getActivity());
        grade = set.getInt("grade");
        aClass = set.getInt("class");
    }

}
