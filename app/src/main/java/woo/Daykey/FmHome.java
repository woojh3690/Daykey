package woo.Daykey;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import static woo.Daykey.MainActivity.getMainContext;
import static woo.Daykey.MainActivity.getWhatKindOfNetwork;

/**
 *가정통신문
 */

class FmHome extends Fragment {
    private SwipeRefreshLayout swipeRefreshLayout;
    private Context newsContext;
    private ListView listView;
    private Context context;
    private SQLiteDatabase db;

    private String title;
    private String teacherName;
    private String visitors;
    private String date;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.flagment_home, container, false);
        swipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.swipe_refresh_wrapper_home);
        listView = (ListView)view.findViewById(R.id.home_listView);

        context = getMainContext();
        newsContext = view.getContext();
        SqlHelper SqlHelper = new SqlHelper(context);
        db = SqlHelper.getReadableDatabase();

        new setAdaptor().execute(Boolean.FALSE); //리스트뷰에 아이템 넣기
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new setAdaptor().execute(Boolean.TRUE);
            }
        });
        swipeRefreshLayout.setColorSchemeColors(
                getResources().getColor(R.color.colorAccent),
                getResources().getColor(R.color.colorPrimary));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String url = null;
                String[] columns = {"url"};
                String where = " _id = ?";
                String[] at = { String.valueOf(position + 1) };
                try (Cursor cursor = db.query("homeTable", columns, where, at, null, null, null)) {
                    while (cursor.moveToNext()) {
                        url = cursor.getString(0);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                Uri uri = Uri.parse("http://www.daykey.hs.kr/daykey/0601/board/14114/" + url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
        return view;
    }

    private class setAdaptor extends AsyncTask<Boolean, Void, NewsAdapter> {
        boolean toast = false;

        @Override
        protected NewsAdapter doInBackground(Boolean... params) {
            if (params[0]) {
                newsSave();
            }

            NewsAdapter newsAdapter = new NewsAdapter();
            for (int i = 1; i < 11; i++) {
                getNews(i);
                newsAdapter.addItem(new NewsItem(title, teacherName, visitors, date));
            }
            return newsAdapter;
        }

        @Override
        protected void onPostExecute(NewsAdapter newsAdapter) {
            if (toast) {
                Toast.makeText(newsContext, "네트워크에 연결해 주세요.", Toast.LENGTH_SHORT).show();
            } else {
                listView.setAdapter(newsAdapter);
            }
            swipeRefreshLayout.setRefreshing(false);
        }

        void newsSave() {
            if (getWhatKindOfNetwork(context)) {
                final String sql = "drop table " + "homeTable";
                final String create3 = "create table " + "homeTable " + "(_id INTEGER PRIMARY KEY AUTOINCREMENT, title text, teacherName text, visitors text, date text, url text);";

                try {
                    db.execSQL(sql);
                    db.execSQL(create3);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Thread thread = new BoardParsing(context, "http://www.daykey.hs.kr/daykey/0601/board/14114", 2);
                thread.start();

                try {
                    thread.join();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else {
                toast = true;
            }
        }

        private void getNews(int position) {
            String[] columns = {"title", "teacherName", "visitors", "date"};
            String where = " _id = ?";
            String[] at = { String.valueOf(position) };
            try (Cursor cursor = db.query("homeTable", columns, where, at, null, null, null)) {
                while (cursor.moveToNext()) {
                    title = cursor.getString(0);
                    teacherName = cursor.getString(1);
                    visitors = cursor.getString(2);
                    date = cursor.getString(3);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private class NewsAdapter extends BaseAdapter {
        ArrayList<NewsItem> items = new ArrayList<>();

        void addItem(NewsItem item) {
            items.add(item);
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            NewsItemView view;
            if (convertView == null) {
                view = new NewsItemView(newsContext);
            } else {
                view = (NewsItemView) convertView;
            }

            NewsItem item = items.get(position);
            view.setTitleView(item.getTitle());
            view.setDateView(item.getDate());
            view.setWriterView(item.getWriter());
            view.setVisitorsView(item.getVisitors());
            return view;
        }
    }
}