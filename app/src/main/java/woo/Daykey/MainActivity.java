package woo.Daykey;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.messaging.FirebaseMessaging;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private int id;
    static Handler mhandler;
    static SqlHelper sqlHelper;
    static SQLiteDatabase db;
    static SettingPreferences set;
    private Context mainContext;
    TextView name, grade;

    ProgressDialog dialog;
    Toolbar toolbar;

    public static final String baseUrl = "https://daykey.jje.hs.kr";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mainContext = getApplicationContext();
        sqlHelper = new SqlHelper(mainContext);
        db = sqlHelper.getReadableDatabase();
        set = new SettingPreferences(mainContext);
        setid();

        if (savedInstanceState == null) {
            setHandler();
            network();//공지사항 가져오기
        }

        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        toolbar = findViewById(R.id.toolbar);

        fuc(); //건들지 말것
        if (savedInstanceState != null) {
            // 화면전환 전에 넣어주었던 pointList 를 꺼내서 세팅
            Bundle bundle = savedInstanceState.getBundle("save_data");
            assert bundle != null;
            id = bundle.getInt("restart", R.id.main);
            changeView();
        } else {
            getPermission();
        }
    }

    private void setid() {
        id = R.id.main;
        String type = getIntent().getStringExtra("type");
        if (type != null) {
            switch (type) {
                case "news":
                    id = R.id.news;
                    break;
                case "home":
                    id = R.id.home;
                    break;
                case "sci":
                    id = R.id.science;
                    break;
            }
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Bundle bundle = new Bundle();
        bundle.putInt("restart", id);
        outState.putBundle("save_data", bundle);
    }

    //데이터베이스 확인, 없으면 네트워크 연결확인
    private void DataCheck() {
        Calendar calendar = Calendar.getInstance();
        int curMonth = calendar.get(Calendar.MONTH);

        if (curMonth != set.getInt("db_version")) {
            if (GetWhatKindOfNetwork.check(mainContext)) {
                FirebaseMessaging.getInstance().subscribeToTopic("ALL");
                /*FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(this,  new OnSuccessListener<InstanceIdResult>() {
                    @Override
                    public void onSuccess(InstanceIdResult instanceIdResult) {
                        String mToken = instanceIdResult.getToken();
                        Log.e("파이어베이스 토큰",mToken);
                    }
                });*/
                makeNotificationChannel();
                getDietData();
                new CalendarDataParsing(mainContext).start(); //학사일정 가져오기

                set.saveBoolean("firstStart", false);
            } else {
                if (set.getBoolean("firstStart")) {
                    Toast.makeText(mainContext, "인터넷을 연결해 주세요\n" +
                            "처음 앱을 실행했을 때에는 데이터를 가져오는 과정이 필요합니다.", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Toast.makeText(this, "새로운 식단 데이터를 가져오기 위해\n인터넷을 연결해 주세요", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        } else {
            changeView();
        }
    }

    boolean doubleBackToExitPressedOnce = false;

    //back 버튼이 눌렸을때 목록창 닫기
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "'뒤로' 버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //주석은 툴바 메뉴버튼
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    //화면 전환기능
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        boolean aReturn = true;
        if (id != item.getItemId()) {
            id = item.getItemId();
            changeView();
        } else {
            aReturn = false;
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return aReturn;
    }

    //프로그레스 다이얼 로그
    private void showProgressDialog() {
        dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("데이터를 가져오고 있습니다. 잠시만 기다려 주세요");
        dialog.setCancelable(false);

        dialog.show();
    }

    //모르는 기능, 건들지 말것
    private void fuc() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View header = navigationView.getHeaderView(0);

        // 메뉴머리 텍스트 설정
        if ((set.getInt("grade") != -1) && (set.getInt("class") != -1)) {
            name = header.findViewById(R.id.tv_name);
            grade = header.findViewById(R.id.tv_grade);
            name.setText(set.getString("name"));
            grade.setText(
                    String.format("%s학년 %s반", set.getInt("grade"), set.getInt("class"))
            );
        }
    }

    private void network() {
        sqlHelper.boardParsing(0);
        sqlHelper.boardParsing(1);
        sqlHelper.boardParsing(2);
        new ServerScheduleParsing(false).start();
    }

    //식단 저장
    private void getDietData() {
        showProgressDialog();

        Thread thread = new Thread(() -> {
            try {
                db.execSQL("drop table if exists " + "dietTable");
                db.execSQL("create table " + "dietTable " + "(date INTEGER, menu text);");
            } catch (SQLException e) {
                e.printStackTrace();
            }
            set.saveBoolean("diet", false);

            DietParsing parser = new DietParsing(set);
            getDietData(parser, true);
            getDietData(parser, false);
        });

        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        set.saveBoolean("diet", true);
        mhandler.sendEmptyMessage(1); //작업 종료 메시지
    }

    private void getDietData(DietParsing parser, boolean type) {
        String strType = (type) ? "M" : "D";
        String url = MainActivity.baseUrl + "/daykey/19152/food?foodType=" + strType;

        try {
            GetHtmlText getHtmlText = new GetHtmlText(url);
            parser.parse(getHtmlText.getHtmlString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("HandlerLeak")
    private void setHandler() {
        mhandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 1:
                        dialog.dismiss();
                        changeView();
                        break;
                    case 2:
                        changeScheView();
                        break;
                }
            }
        };
    }

    private void getPermission() {
        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                DataCheck();
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                Toast.makeText(mainContext, "권한 거부", Toast.LENGTH_LONG).show();
                finish();
            }
        };

        TedPermission.with(mainContext)
                .setPermissionListener(permissionListener)
                .setPermissions(Manifest.permission.WRITE_CALENDAR, Manifest.permission.READ_CALENDAR)
                .check();
    }

    public void changeView() {
        if (id == R.id.main) {
            viewMain();
        } else if (id == R.id.diet) {
            changeDietView();
        } else if (id == R.id.schedule) {
            changeScheView();
        } else if (id == R.id.TimeTable) {
            FragmentManager fm = getFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.frm1, new FmTimeTable());

            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.commit();
            toolbar.setTitle("시간표");
        } else if (id == R.id.news) {
            FragmentManager fm = getFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.frm1, new FmNews());

            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.commit();
            toolbar.setTitle("공지사항");
        } else if (id == R.id.home) {
            FragmentManager fm = getFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.frm1, new FmHome());

            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.commit();
            toolbar.setTitle("가정통신문");
        } else if (id == R.id.science) {
            FragmentManager fm = getFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.frm1, new FmScience());

            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.commit();
            toolbar.setTitle("과학중점 공지사항");
        } else if (id == R.id.setting) {
            FragmentManager fm = getFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.frm1, new FmSetting());

            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.commit();
            toolbar.setTitle("설정");
        } else if (id == R.id.about) {
            FragmentManager fm = getFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.frm1, new FmAbout());

            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.commit();
            toolbar.setTitle("About");
        }
    }

    // 메인 화면 보기
    public void viewMain() {
        try {
            FragmentManager fm = getFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.frm1, new FmMain());

            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.commit();
            toolbar.setTitle("대기고등학교");
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }

    }

    private void changeDietView() {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.frm1, new FmDiet());

        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
        toolbar.setTitle("식단");
        if (!set.getBoolean("diet")) {
            Toast.makeText(mainContext, "식단이 올라오지 않았습니다.\n대기고등학교 홈페이에 문의 바랍니다.",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void changeScheView() {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.frm1, new FmSchedule());

        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
        toolbar.setTitle("학사일정");
    }

    private void makeNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            android.app.NotificationManager notificationManager = (android.app.NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel channelMessage = new NotificationChannel("channel_id", "공지", android.app.NotificationManager.IMPORTANCE_DEFAULT);
            channelMessage.setDescription("channel description");
            channelMessage.enableLights(true);
            channelMessage.setLightColor(Color.BLUE);
            channelMessage.enableVibration(true);
            channelMessage.setVibrationPattern(new long[]{100, 200, 100, 200});
            channelMessage.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(channelMessage);
            //notificationManager.createNotificationChannelGroup(new NotificationChannelGroup("공지", "woo.Daykey.Notice"));
            set.saveString("channel", channelMessage.getId());
        }
    }
}