package woo.Daykey;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
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

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 달력
 */

public class FmCalendar extends Fragment{
    private Context mainContext;
    private SQLiteDatabase db;
    private SettingPreferences set;
    TextView calendarTextView;
    GridView monthView;
    MonthAdapter monthAdapter;
    Button addSche;
    private int year;
    private int month;
    static String trimDate = null;

    public FmCalendar() {
    }

    FmCalendar(SQLiteDatabase db, Context mainContext, SettingPreferences set, int year, int month, TextView textView, Button addSche) {
        this.mainContext = mainContext;
        this.db = db;
        this.set = set;
        this.year = year;
        this.month = month;
        this.calendarTextView = textView;
        this.addSche = addSche;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.flagment_calendar, container, false);
        monthView = (GridView) view.findViewById(R.id.monthView);
        monthAdapter = new MonthAdapter(view.getContext(), db, year, month);
        addSche.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (trimDate == null || trimDate.endsWith("00")) {
                    Toast.makeText(mainContext, "날짜를 선택해주세요.", Toast.LENGTH_SHORT).show();
                } else {

                    boolean check = false;
                    if(set.getString("name").equals(" ")) {
                        Toast.makeText(mainContext, "프로필에 정확한 이름을 입력해 주세요", Toast.LENGTH_SHORT).show();
                        check = true;
                    } else if(set.getInt("grade") == -1) {
                        Toast.makeText(mainContext, "프로필에 정확한 학년을 설정해 주세요", Toast.LENGTH_SHORT).show();
                        check = true;
                    } else if(set.getInt("class") == -1) {
                        Toast.makeText(mainContext, "프로필에 정확한 반을 설정해 주세요", Toast.LENGTH_SHORT).show();
                        check = true;
                    } else if(set.getInt("password") == -1) {
                        Toast.makeText(mainContext, "프로필에 정확한 비밀번호를 설정해 주세요", Toast.LENGTH_SHORT).show();
                        check = true;
                    } else {
                        dialogShow();
                    }

                    if (check) {
                        Toast.makeText(mainContext, "설정에서 프로필을 변경할 수 있습니다.", Toast.LENGTH_SHORT).show();
                    }

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
                addSche.setText(listDayText[0] + "일\n일정\n추가");
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
                String strSche = null;
                strSche = editTextSche.getText().toString();
                Log.i("strSche : ? -->", " 호잇 : " + strSche);

                if(!(strSche.equals(""))) {
                    String[] list = {"http://wooserver.iptime.org/daykey/schedule/save", strSche};
                    HttpAsyncTask httpAsyncTask = new HttpAsyncTask();
                    httpAsyncTask.execute(list);
                    dialog.dismiss();
                } else {
                    Toast.makeText(mainContext, "일정을 입력해 주세요.", Toast.LENGTH_SHORT).show();
                }

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

    private class HttpAsyncTask extends AsyncTask<String, Void, String> {

        HttpAsyncTask() {
        }

        @Override
        protected String doInBackground(String... urls) {
            String[] dateList = trimDate.split("/");
            ScheduleModel scheduleModel = new ScheduleModel();
            scheduleModel.setName(set.getString("name"));
            scheduleModel.setGrade(set.getInt("grade"));
            scheduleModel.setClass_(set.getInt("class"));
            scheduleModel.setPassword(set.getInt("password"));
            scheduleModel.setYear(dateList[0]);
            scheduleModel.setMonth(dateList[1]);
            scheduleModel.setDate(dateList[2]);
            scheduleModel.setSche(urls[1]);

            return POST(urls[0], scheduleModel);
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }
    }

    public static String POST(String url, ScheduleModel scheduleModel){
        InputStream is = null;
        String result = "";
        try {
            URL urlCon = new URL(url);
            HttpURLConnection httpCon = (HttpURLConnection)urlCon.openConnection();

            String json = "";

            JSONObject jsonObject = new JSONObject();
            jsonObject.accumulate("name", scheduleModel.getName());
            jsonObject.accumulate("grade", scheduleModel.getGrade());
            jsonObject.accumulate("class", scheduleModel.getClass_());
            jsonObject.accumulate("password", scheduleModel.getPassword());
            jsonObject.accumulate("year", scheduleModel.getYear());
            jsonObject.accumulate("month", scheduleModel.getMonth());
            jsonObject.accumulate("date", scheduleModel.getDate());
            jsonObject.accumulate("sche", scheduleModel.getSche());

            // convert JSONObject to JSON to String
            json = jsonObject.toString();

            // ** Alternative way to convert Person object to JSON string usin Jackson Lib
            // ObjectMapper mapper = new ObjectMapper();
            // json = mapper.writeValueAsString(person);

            // Set some headers to inform server about the type of the content
            httpCon.setRequestProperty("Accept", "application/json");
            httpCon.setRequestProperty("Content-type", "application/json");

            // OutputStream으로 POST 데이터를 넘겨주겠다는 옵션.
            httpCon.setDoOutput(true);
            // InputStream으로 서버로 부터 응답을 받겠다는 옵션.
            httpCon.setDoInput(true);

            OutputStream os = httpCon.getOutputStream();
            os.write(json.getBytes("utf-8"));
            os.flush();
            // receive response as inputStream
            try {
                is = httpCon.getInputStream();
                // convert inputstream to string
                if(is != null)
                    result = convertInputStreamToString(is);
                else
                    result = "Did not work!";
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                httpCon.disconnect();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        return result;
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException{
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;
    }
}