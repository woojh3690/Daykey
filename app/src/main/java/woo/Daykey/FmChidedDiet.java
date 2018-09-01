package woo.Daykey;

import android.app.Fragment;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

import static woo.Daykey.MainActivity.db;

public class FmChidedDiet extends Fragment{
    private int[] idArray = {R.id.dietText1, R.id.dietText2, R.id.dietText3, R.id.dietText4, R.id.dietText5, R.id.dietText6, R.id.dietText7, R.id.dietText8, R.id.dietText9, R.id.dietText10};
    private TextView[] tvArray = new TextView[10];
    private int start;
    private int finish;
    private int firstWeek = 0;
    private final String[] columns = {"menu"};
    private Map<Integer, String> lunManu = new HashMap<>();
    private Map<Integer, String> dinManu = new HashMap<>();

    public FmChidedDiet() {
    }

//    public FmChidedDiet(int start, int finish) {
//        this.start = start;
//        this.finish = finish;
//    }
//
//    public FmChidedDiet(int firstWeek, int start, int finish) {
//        this.firstWeek = firstWeek - 2;
//        this.start = start;
//        this.finish = finish;
//    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.firstWeek = getArguments().getInt("firstWeek");
        this.start = getArguments().getInt("start");
        this.finish = getArguments().getInt("finish");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.flagment_chiled_diet, container, false);
        resetDiet();
        reset(view);
        return view;
    }

    private void resetDiet() {
        for (int i = start ; i <= finish; i++) {
            try {
                String[] at = {String.valueOf(i)};
                Cursor cursor = db.query("dietTable", columns, " date = ?", at, null, null, null);
                int checkNum = 0;
                while(cursor.moveToNext()) {
                    String menu = cursor.getString(0);
                    if (checkNum == 0) {
                        lunManu.put(i, menu);
                        checkNum = 1;
                    } else {
                        dinManu.put(i, menu);
                    }
                }
                cursor.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private void reset(View view) {
        for (int i = 0; i < tvArray.length; i++) {
            tvArray[i] = view.findViewById(idArray[i]);//tvArray[]초기화
        }
        int i = 0;

        for (int v = start - firstWeek; v <= finish; v++) {
            String temp = lunManu.get(v);
            //Log.i("확인 : ", v +" / " + finish);
            tvArray[i].setText(temp);//점심 텍스트 설정
            tvArray[i + 1].setText(dinManu.get(v));//저녁 텍스트 설정
            i += 2;
        }
    }

    public int getStart() {
        return start - firstWeek;
    }

    public int getFinish() {
        return finish;
    }
}
