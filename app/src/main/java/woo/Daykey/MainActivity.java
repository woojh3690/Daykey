package woo.Daykey;

import android.Manifest;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivityLog";
    private int id;
    static boolean dismiss = false;
    static Handler mhandler;
    static SQLiteDatabase db;
    static SettingPreferences set;
    private Context mainContext;
    TextView name, grade;
    WebView mWebView;
    WebSettings mWebSettings;
    ProgressDialog dialog;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mainContext = getApplicationContext();
        SqlHelper sqlHelper = new SqlHelper(mainContext);
        db = sqlHelper.getReadableDatabase();
        set = new SettingPreferences(mainContext);

        if(savedInstanceState == null) {
            setHandler();
            newsSave();//공지사항 가져오기
            defaultAlarm();//처음 앱을 시작했다면 알람 설정
        }

        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mWebView = (WebView) findViewById(R.id.webview);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        fuc();//건들지 말것

        if (savedInstanceState!=null) {
            // 화면전환 전에 넣어주었던 pointList 를 꺼내서 세팅
            Bundle bundle = savedInstanceState.getBundle("save_data");
            assert bundle != null;
            id = bundle.getInt("restart", R.id.main);

            if (id == R.id.main) {
                viewMain();
            } else if (id == R.id.diet) {
                changeDietView();
            } else if (id == R.id.schedule) {
                changeScheView();
            }
        } else {
            //viewMain();
            getPermission();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Bundle bundle = new Bundle();
        bundle.putInt("restart", id);
        outState.putBundle("save_data", bundle);
    }

    //데이터베이스 확인, 없으면 네트워크 연결확인
    private void DataCheck() {
        Calendar calendar = Calendar.getInstance();
        int curMonth = calendar.get(Calendar.MONTH);

        if (!(curMonth == set.getInt("db_version")) || !set.getBoolean("diet")) {
            if (GetWhatKindOfNetwork.check(mainContext)) {
                dietSave();
                if (set.getBoolean("firstStart")) {
                    getSchedule();
                    set.saveBoolean("firstStart", false);
                }
            } else {
                if(set.getBoolean("firstStart")) {
                    Toast.makeText(mainContext, "인터넷을 연결해 주세요\n" +
                            "처음 앱을 실행했을 때에는 데이터를 가져오는 과정이 필요합니다.", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    viewMain();
                    Toast.makeText(this, "식단 데이터가 없습니다.\n인터넷을 연결해 주세요", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            viewMain();
        }
    }

    //처음 앱을 시작하면 알람 설정
    private void defaultAlarm() {
        SettingPreferences set = new SettingPreferences(mainContext);

        if (set.getBoolean("alarm")) {
            Intent intent = new Intent(mainContext, AlarmBroadcastReceive.class);
            PendingIntent sender = PendingIntent.getBroadcast(mainContext, 0, intent, PendingIntent.FLAG_NO_CREATE);

            if (sender == null) {
                AlarmBroadcast alarmBroadcast = new AlarmBroadcast(mainContext);
                alarmBroadcast.Alarm(1);
            }
        }
    }

    //웹뷰 로딩
    public void loadWebView() {
        //Log.i("loadWebView", "실행됨");
        showProgressDialog();
        try {
            mWebView.setWebViewClient(new WebViewClient() {
                @Override//페이지 로딩이 끝나면 불린다.
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);

                    mWebView.loadUrl("javascript:window.HtmlViewer.showHTML('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");

                    if (!dismiss) {
                        mWebView.loadUrl("javascript:chgTab('D')");//석식 로딩
                    }
                }
            });
            mWebSettings = mWebView.getSettings();
            mWebSettings.setJavaScriptEnabled(true);
            mWebView.addJavascriptInterface(new DietParsing(set), "HtmlViewer");
            mWebView.loadUrl("http://www.daykey.hs.kr/daykey/19152/food");//중식 로딩
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.e(TAG, "loadView Error");
        }
    }

    //back 버튼이 눌렸을때 목록창 닫기
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //주석은 툴바 메뉴버튼
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    //화면 전환기능
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        id = item.getItemId();
        changeView();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View header = navigationView.getHeaderView(0);

        if ( (set.getInt("grade") != -1) && (set.getInt("class") != -1)) { //메뉴머리 텍스트 설정
            name = (TextView)header.findViewById(R.id.tv_name);
            grade = (TextView)header.findViewById(R.id.tv_grade);
            name.setText(set.getString("name"));
            grade.setText(set.getInt("grade") + "학년 " + set.getInt("class") + "반");
        }
    }

    //뉴스 저장
    private void newsSave() {
        if (GetWhatKindOfNetwork.check(mainContext)) {
            String sql = "drop table if exists " + "newsTable";
            String create3 = "create table " + "newsTable " + "(_id INTEGER PRIMARY KEY AUTOINCREMENT, title text, teacherName text, visitors text, date text, url text);";
            try {
                db.execSQL(sql);
                db.execSQL(create3);
            } catch (SQLException e) {
                e.printStackTrace();
            }

            Thread thread = new BoardParsing(mainContext, "http://www.daykey.hs.kr/daykey/0701/board/14117", 1);
            thread.start();

            homeSave();
            sciSave();
            ServerScheduleParsing serverScheduleParsing = new ServerScheduleParsing(db);
            serverScheduleParsing.start();
        }
    }

    //가정통신문 저장
    private void homeSave() {
        String sql = "drop table if exists " + "homeTable";
        String create3 = "create table " + "homeTable " + "(_id INTEGER PRIMARY KEY AUTOINCREMENT, title text, teacherName text, visitors text, date text, url text);";
        try {
            db.execSQL(sql);
            db.execSQL(create3);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Thread thread = new BoardParsing(mainContext, "http://www.daykey.hs.kr/daykey/0601/board/14114", 2);
        thread.start();
    }

    //과학중점 저장
    private void sciSave() {
        String sql = "drop table if exists " + "sciTable";
        String create3 = "create table " + "sciTable " + "(_id INTEGER PRIMARY KEY AUTOINCREMENT, title text, teacherName text, visitors text, date text, url text);";
        try {
            db.execSQL(sql);
            db.execSQL(create3);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Thread thread = new BoardParsing(mainContext, "http://www.daykey.hs.kr/daykey/19516/board/20170", 3);
        thread.start();
    }

    //식단 저장
    private void dietSave() {
        String sql = "drop table if exists " + "dietTable";
        String create1 = "create table " + "dietTable " + "(date INTEGER, menu text);";
        try {
            db.execSQL(sql);
            db.execSQL(create1);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        loadWebView();
    }

    //일정 저장
    private void getSchedule() {
        CalendarDataParsing calendarDataParsing = new CalendarDataParsing();
        calendarDataParsing.setSqlHelper(db, mainContext);
        calendarDataParsing.start();
    }

    private void setHandler() {
        mhandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 1:
                        dialog.dismiss();
                        viewMain();
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

        new TedPermission(mainContext)
                .setPermissionListener(permissionListener)
                .setPermissions(Manifest.permission.WRITE_CALENDAR)
                .check();
    }

    private void changeDietView() {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.frm1, new FmDiet(db));

        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
        toolbar.setTitle("식단");
        if(!set.getBoolean("diet")) {
            Toast.makeText(mainContext, "식단이 올라오지 않았습니다.\n대기고등학교 홈페이에 문의 바랍니다.",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void changeScheView() {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.frm1, new FmSchedule(mhandler));

        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
        toolbar.setTitle("학사일정");
    }
}