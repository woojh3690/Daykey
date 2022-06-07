package woo.Daykey;

import android.app.Fragment;
import android.database.Cursor;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

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
            textViews[i] = view.findViewById(id);
        }
        setProfile();

        if ((grade == -1) || (aClass == -1)) {
            //textViews[17].setText("설정에서 프로필을 설정해 주세요");
            Toast.makeText(getActivity(), "설정에서 프로필을 설정해 주세요", Toast.LENGTH_LONG).show();
        }
        setTextViews();
        return view;
    }

    void setTextViews() { //textview 설정
        try {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void setProfile() {
        SettingPreferences set = new SettingPreferences(getActivity());
        grade = set.getInt("grade");
        aClass = set.getInt("class");
    }

}
