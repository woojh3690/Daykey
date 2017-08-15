package woo.Daykey;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MonthItemView extends RelativeLayout {
    TextView textView;

    public MonthItemView(Context context) {
        super(context);

        init(context);
    }

    public MonthItemView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context);
    }

    private void init(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.item_month, this, true);
        textView = (TextView) findViewById(R.id.textView);
    }

    public void setDay(String day) {
        if (day.startsWith("0")) {
            day = " ";
            textView.setText(day);
        } else {
            final SpannableStringBuilder sp = new SpannableStringBuilder(String.valueOf(day));
            sp.setSpan(new ForegroundColorSpan(Color.BLACK), 0, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            textView.setText(sp);
        }
    }
}
