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
    private SqlHelper sqlHelper;
    private SQLiteDatabase db;
    private Context mainContext;
    static Handler mhandler;
    TextView name, grade;
    WebView mWebView;
    WebSettings mWebSettings;
    ProgressDialog dialog;
    Toolbar toolbar;
    SettingPreferences set;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mainContext = getApplicationContext();
        sqlHelper = new SqlHelper(mainContext);
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

        getPermission();
        fuc();//건들지 말것

        if(savedInstanceState!=null) {
            // 화면전환 전에 넣어주었던 pointList 를 꺼내서 세팅
            Bundle bundle = savedInstanceState.getBundle("save_data");
            id = bundle.getInt("restart", R.id.main);

            if (id == R.id.main) {
                viewMain();
            } else if (id == R.id.diet) {
                changeDietView();
            } else if (id == R.id.schedule) {
                changeScheView();
            }
        } else {
            viewMain();
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
        //Log.i("DataCheck", "실행됨");
        if (set.getBoolean("diet")) {
            dietSave();
        } else {
            if (GetWhatKindOfNetwork.check(mainContext)) {
                loadWebView();
                getSchedule();//일정가져오기
                set.saveBoolean("firstStart", true);
            } else {
                if(!set.getBoolean("firstStart")) {
                    Toast.makeText(mainContext, "인터넷을 연결해 주세요\n" +
                            "처음 앱을 실행했을 때에는 데이터를 가져오는 과정이 필요합니다.", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Toast.makeText(this, "인터넷을 연결해 주세요", Toast.LENGTH_SHORT).show();
                }
            }
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
        showProgressDialog("데이터를 가져오고 있습니다. 잠시만 기다려 주세요");
        try {
            mWebView.setWebViewClient(new WebViewClient() {
                @Override//페이지 로딩이 끝나면 불린다.
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);

                    mWebView.loadUrl("javascript:window.HtmlViewer.showHTML('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");

                    if (!dismiss) {
                        mWebView.loadUrl("javascript:chgTab('D')");//석식 로딩
                        dismiss = true;
                    }
                }
            });
            mWebSettings = mWebView.getSettings();
            mWebSettings.setJavaScriptEnabled(true);
            mWebView.addJavascriptInterface(new DietParsing(db, set), "HtmlViewer");
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
            ft.replace(R.id.frm1, new FmTimeTable(mainContext, db));

            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.commit();
            toolbar.setTitle("시간표");
        } else if (id == R.id.news) {
            FragmentManager fm = getFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.frm1, new FmNews(mainContext, db));

            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.commit();
            toolbar.setTitle("공지사항");
        } else if (id == R.id.home) {
            FragmentManager fm = getFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.frm1, new FmHome(mainContext, db));

            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.commit();
            toolbar.setTitle("가정통신문");
        } else if (id == R.id.science) {
            FragmentManager fm = getFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.frm1, new FmScience(db, mainContext));

            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.commit();
            toolbar.setTitle("과학중점 공지사항");
        } else if (id == R.id.setting) {
            FragmentManager fm = getFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.frm1, new FmSetting(mainContext, set));

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
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.frm1, new FmMain(db));

        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
        toolbar.setTitle("대기고등학교");
    }

    //프로그레스 다이얼 로그
    private void showProgressDialog(String message) {
        dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage(message);
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

        if ( (set.getInt("grade") != 0) && (set.getInt("class") != 0)) { //메뉴머리 텍스트 설정
            name = (TextView)header.findViewById(R.id.tv_name);
            grade = (TextView)header.findViewById(R.id.tv_grade);
            name.setText(set.getString("name"));
            grade.setText(set.getInt("grade") + "학년 " + set.getInt("class") + "반");
        }
    }

    public void newsSave() {
        if (GetWhatKindOfNetwork.check(mainContext)) {
            db = sqlHelper.getReadableDatabase();

            String sql = "drop table " + "newsTable";
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
        }
    }

    public void homeSave() {
        String sql = "drop table " + "homeTable";
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

    public void sciSave() {
        String sql = "drop table " + "sciTable";
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

    public void dietSave() {
        Calendar calendar = Calendar.getInstance();
        int curMonth = calendar.get(Calendar.MONTH);

        //만약 식단 DB 버전이 다르다면
        if(! (curMonth == set.getInt("db_version")) ) {
            String sql = "drop table " + "dietTable";
            String create1 = "create table " + "dietTable " + "(date INTEGER, menu text);";
            try {
                db.execSQL(sql);
                db.execSQL(create1);
            } catch (SQLException e) {
                e.printStackTrace();
            }

            loadWebView();
        }
    }

    public void getSchedule() {
        CalendarDataParsing calendarDataParsing = new CalendarDataParsing();
        calendarDataParsing.setSqlHelper(db, mainContext);
        calendarDataParsing.start();
    }

    public void setHandler() {
        mhandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 1) {
                    dialog.dismiss();
                }
            }
        };
    }

    public void getPermission() {
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
        ft.replace(R.id.frm1, new FmSchedule(db, set));

        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
        toolbar.setTitle("학사일정");
    }
}