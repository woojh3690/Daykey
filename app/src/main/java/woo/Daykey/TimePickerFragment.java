package woo.Daykey;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import static woo.Daykey.MainActivity.getMainContext;

/**
 * 시간 다이얼
 */

public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {
    SettingPreferences set = new SettingPreferences(getMainContext());

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        TextView hourTv = (TextView)getActivity().findViewById(R.id.hourTextview);
        TextView minTv = (TextView)getActivity().findViewById(R.id.minuteTextView);
        Switch aSwitch = (Switch)getActivity().findViewById(R.id.alarmSwitch);
        Context context = getMainContext();

        //hourTv.setText(""+hourOfDay + "시");
        //minTv.setText("" + minute + "분");

        set.saveInt("hour", hourOfDay);
        set.saveInt("min", minute);

        AlarmBroadcast alarm = new AlarmBroadcast(context);
        alarm.Alarm(0);

        //aSwitch.setChecked(true);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int hour = set.getInt("hour");
        int min = set.getInt("min");
        return new TimePickerDialog(this.getActivity(), this, hour, min, false);
    }

}
