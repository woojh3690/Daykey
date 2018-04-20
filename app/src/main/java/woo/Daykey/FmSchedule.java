package woo.Daykey;

import android.app.Activity;
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
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
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
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import static woo.Daykey.FmCalendar.map;
import static woo.Daykey.FmCalendar.scheAndName;
import static woo.Daykey.FmCalendar.trimDate;
import static woo.Daykey.MainActivity.db;
import static woo.Daykey.MainActivity.set;

public class FmSchedule extends Fragment {
    CalendarAdapter calendarAdapter;
    TextView monthText;
    static TextView calendarTextView;
    Activity activity;
    Calendar calendar = Calendar.getInstance();
    static Button addSche, deleteSche;
    View view;
    private int mSelectedPageIndex = 1;
    private ViewPager viewPager;
    final FmCalendar[] fragList = new FmCalendar[3];

    public FmSchedule() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.flagment_schedule, container, false);
        viewPager = (ViewPager) view.findViewById(R.id.viewPager);
        monthText = (TextView) view.findViewById(R.id.monthText);
        calendarTextView = (TextView)view.findViewById(R.id.calendarTextView);
        addSche = (Button)view.findViewById(R.id.add_schedule);
        deleteSche = (Button)view.findViewById(R.id.delete_schedule);
        buttonInit();
        monthText.setText(calendar.get(Calendar.YEAR) + "년 " + (calendar.get(Calendar.MONTH) + 1) + "월");
        activity = getActivity();

        addFmCalendar();

        calendarAdapter = new CalendarAdapter(getChildFragmentManager(), fragList);
        //calendarAdapter.notifyDataSetChanged();
        viewPager.setAdapter(calendarAdapter);
        viewPager.setCurrentItem(1, false);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mSelectedPageIndex = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == ViewPager.SCROLL_STATE_IDLE) {
                    if (mSelectedPageIndex < 1) {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                fragList[0].onPreviousMonth();
                                fragList[1].onPreviousMonth();
                                monthText.setText(fragList[1].getCalendarTitle());
                                fragList[2].onPreviousMonth();
                            }
                        });
                    } else if (mSelectedPageIndex > 1) {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                fragList[0].onNextMonth();
                                fragList[1].onNextMonth();
                                monthText.setText(fragList[1].getCalendarTitle());
                                fragList[2].onNextMonth();
                            }
                        });
                    }

                    viewPager.setCurrentItem(1, false);
                }
            }
        });

        return view;
    }

    private void buttonInit() {
        //일정추가
        addSche.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (trimDate == null || trimDate.endsWith("00")) {
                    Toast.makeText(getActivity(), "날짜를 선택해주세요", Toast.LENGTH_SHORT).show();
                } else {

                    boolean check = false;
                    if (set.getString("name").equals(" ")) {
                        Toast.makeText(getActivity(), "프로필에 정확한 이름을 입력해 주세요", Toast.LENGTH_SHORT).show();
                        check = true;
                    } else if (set.getInt("grade") == -1) {
                        Toast.makeText(getActivity(), "프로필에 정확한 학년을 설정해 주세요", Toast.LENGTH_SHORT).show();
                        check = true;
                    } else if (set.getInt("class") == -1) {
                        Toast.makeText(getActivity(), "프로필에 정확한 반을 설정해 주세요", Toast.LENGTH_SHORT).show();
                        check = true;
                    } else if (set.getInt("password") == -1) {
                        Toast.makeText(getActivity(), "프로필에 정확한 비밀번호를 설정해 주세요", Toast.LENGTH_SHORT).show();
                        check = true;
                    } else if (set.getString("email").equals(" ")) {
                        Toast.makeText(getActivity(), "프로필에 정확한 이메일을 설정해 주세요", Toast.LENGTH_SHORT).show();
                        check = true;
                    } else {
                        addDialogShow();
                    }

                    if (check) {
                        Toast.makeText(getActivity(), "설정에서 프로필을 변경할 수 있습니다.\n프로필은 일정을 삭제할 때 사용됩니다", Toast.LENGTH_LONG).show();
                    }

                }
            }
        });

        //일정삭제
        deleteSche.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (trimDate == null || trimDate.endsWith("00") || TextUtils.isEmpty(scheAndName)) {
                    Toast.makeText(getActivity(), "일정이 있는 날짜를 선택해 주세요", Toast.LENGTH_SHORT).show();
                } else {
                    deleteDialogShow();
                }
            }
        });
    }

    public void addFmCalendar() {
        fragList[1] = new FmCalendar(calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH));//현재달

        calendar.add(Calendar.MONTH, -1);
        fragList[0] = new FmCalendar(calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH));//이전달

        calendar.add(Calendar.MONTH, 2);
        fragList[2] = new FmCalendar(calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH));//다음달
    }

    private void addDialogShow() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_schedule, null);
        builder.setView(dialogView);
        final Button submit = (Button) dialogView.findViewById(R.id.buttonSubmit);
        final TextView textDate = (TextView) dialogView.findViewById(R.id.textViewDate);
        final EditText editTextSche = (EditText) dialogView.findViewById(R.id.editTextSchedule);
        final CheckBox boolean_public = (CheckBox) dialogView.findViewById(R.id.boolean_public);

        textDate.setText(trimDate); //텍스트 날짜 설정

        final AlertDialog dialog = builder.create();
        submit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String strSche = editTextSche.getText().toString();

                if(!TextUtils.isEmpty(strSche)) {
                    if (strSche.length() > 1) {
                        if (GetWhatKindOfNetwork.check(getActivity())) {
                            int check_public = boolean_public.isChecked() ? 1 : 0;
                            String[] list = {"http://wooserver.iptime.org/daykey/schedule/save", strSche, set.getString("email"), String.valueOf(check_public)};
                            FmSchedule.HttpAsyncTask httpAsyncTask = new FmSchedule.HttpAsyncTask();
                            httpAsyncTask.execute(list);
                            Toast.makeText(getActivity(), "[" + strSche + "]" + " 일정이 추가되었습니다.", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        } else {
                            Toast.makeText(getActivity(), "네트워크에 연결해 주세요", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getActivity(), "일정은 2자 이상이여햐 합니다", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "일정을 입력해 주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialog.show();
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
            scheduleModel.setEmail(urls[2]);
            scheduleModel.setBoolean_public(Byte.valueOf(urls[3]));

            return post(urls[0], scheduleModel);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            new ServerScheduleParsing(true).start();
        }
    }

    private static String post(String url, ScheduleModel scheduleModel){
        InputStream is;
        String result = "";
        try {
            URL urlCon = new URL(url);
            HttpURLConnection httpCon = (HttpURLConnection)urlCon.openConnection();

            String json;

            JSONObject jsonObject = new JSONObject();
            jsonObject.accumulate("name", scheduleModel.getName());
            jsonObject.accumulate("grade", scheduleModel.getGrade());
            jsonObject.accumulate("class", scheduleModel.getClass_());
            jsonObject.accumulate("password", scheduleModel.getPassword());
            jsonObject.accumulate("year", scheduleModel.getYear());
            jsonObject.accumulate("month", scheduleModel.getMonth());
            jsonObject.accumulate("date", scheduleModel.getDate());
            jsonObject.accumulate("sche", scheduleModel.getSche());
            jsonObject.accumulate("email", scheduleModel.getEmail());
            jsonObject.accumulate("boolean_public", scheduleModel.getBoolean_public());

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
        String line;
        StringBuilder result = new StringBuilder();
        while((line = bufferedReader.readLine()) != null)
            result.append(line);

        inputStream.close();
        return result.toString();
    }

    private void deleteDialogShow() {
        final List<String> ListItems = new ArrayList<>();
        Collections.addAll(ListItems, scheAndName.split("\n"));
        final CharSequence[] items =  ListItems.toArray(new String[ ListItems.size()]);

        final List<Integer> SelectedItems  = new ArrayList<>();
        int defaultItem = 0;
        SelectedItems.add(defaultItem);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("일정삭제");
        builder.setSingleChoiceItems(items, defaultItem,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SelectedItems.clear();
                        SelectedItems.add(which);
                    }
                });
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String msg = "";

                if (!SelectedItems.isEmpty()) {
                    int index = SelectedItems.get(0);
                    msg = ListItems.get(index);
                }

                int num = map.get(msg);

                if (num == -1) {
                    Toast.makeText(getActivity(), "공식 일정은 삭제할 수 없습니다", Toast.LENGTH_SHORT).show();
                } else {

                    try {
                        String[] columns = {"name, grade, class, password"};
                        String where = " num = " + num;
                        Cursor cursor = db.query("userTable", columns,  where, null, null, null, null);

                        while(cursor.moveToNext()) {
                            if (set.getString("name").equals(cursor.getString(0))) {
                                if (set.getInt("grade") == cursor.getInt(1)) {
                                    if (set.getInt("class") == cursor.getInt(2)) {
                                        if (set.getInt("password") == cursor.getInt(3)) {
                                            if(GetWhatKindOfNetwork.check(getActivity())) {
                                                new PostDeleteId(num).start();
                                                Toast.makeText(getActivity(), "삭제되었습니다", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(getActivity(), "네트워크에 연결해 주세요", Toast.LENGTH_SHORT).show();
                                            }
                                        } else {
                                            Toast.makeText(getActivity(), "프로필 비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        Toast.makeText(getActivity(), "프로필 반이 일치하지 않습니다", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(getActivity(), "프로필 학년이 일치하지 않습니다", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(getActivity(), "프로필 이름이 일치하지 않습니다", Toast.LENGTH_SHORT).show();
                            }
                        }

                        cursor.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        builder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        builder.show();
    }
}