package woo.Daykey;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import static woo.Daykey.MainActivity.sqlHelper;
import static woo.Daykey.MainActivity.db;

public class FmNews extends Fragment {
    private SwipeRefreshLayout swipeRefreshLayout;
    private Context newsContext;
    private ListView listView;

    public FmNews() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.flagment_news, container, false);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_wrapper_news);
        listView = view.findViewById(R.id.newsListView);
        newsContext = view.getContext();

        new SetAdaptor().execute(Boolean.FALSE); //리스트뷰에 아이템 넣기
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new SetAdaptor().execute(Boolean.TRUE);
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
                try {
                    Cursor cursor = db.query("newsTable", columns, where, at, null, null, null);
                    while (cursor.moveToNext()) {
                        url = cursor.getString(0);
                    }
                    cursor.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                Uri uri = Uri.parse(MainActivity.baseUrl + "/daykey/0701/board/14117/" + url);
                startActivity(new Intent(Intent.ACTION_VIEW, uri));
            }
        });

        return view;
    }

    @SuppressLint("StaticFieldLeak")
    private class SetAdaptor extends AsyncTask<Boolean, Void, NewsAdapter> {
        boolean toast = true;

        @Override
        protected NewsAdapter doInBackground(Boolean... params) {
            NewsAdapter newsAdapter = new NewsAdapter();
            if (params[0]) {
                toast = sqlHelper.boardParsing(0);
            }
            return newsAdapter;
        }

        @Override
        protected void onPostExecute(NewsAdapter newsAdapter) {
            if (toast) {
                listView.setAdapter(newsAdapter);
                String[][] data = sqlHelper.getBoardData(0);
                for (int i = 0; i < 10; i++) {
                    newsAdapter.addItem(new NewsItem(data[i][0], data[i][1], data[i][2], data[i][3]));
                }
            }
            swipeRefreshLayout.setRefreshing(false);
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
            ListItemView view;
            if (convertView == null) {
                view = new ListItemView(newsContext);
            } else {
                view = (ListItemView) convertView;
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