package woo.Daykey;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

class ListItemView extends LinearLayout {
    TextView titleView;
    TextView dateView;
    TextView writer;
    TextView visitors;

    ListItemView(Context context) {
        super(context);
        init(context);
    }

//    public NewsItemView(Context context, @Nullable AttributeSet attrs) {
//        super(context, attrs);
//        init(context);
//    }

    private void init(Context context) {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.list_item, this, true);

        titleView = (TextView)findViewById(R.id.titleView);
        dateView = (TextView)findViewById(R.id.dateView);
        writer = (TextView)findViewById(R.id.writerView);
        visitors = (TextView) findViewById(R.id.visitorView);
    }

    public void setTitleView(String title) {
        this.titleView.setText(title);
    }

    public void setDateView(String date) {
        this.dateView.setText(date);
    }

    public void setWriterView(String writer) {
        this.writer.setText(writer);
    }

    public void setVisitorsView(String visitors) {
        String s = "조회수 : " + visitors;
        this.visitors.setText(s);
    }

}
