package woo.Daykey;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.TimePicker;

/**
 * 시간 다이얼
 */

public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {
    SettingPreferences set;

    public TimePickerFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.set = new SettingPreferences(getActivity());
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        set.saveInt("hour", hourOfDay);
        set.saveInt("min", minute);

        AlarmBroadcast alarm = new AlarmBroadcast(getActivity());
        alarm.Alarm();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int hour = set.getInt("hour");
        int min = set.getInt("min");
        return new TimePickerDialog(this.getActivity(), this, hour, min, false);
    }

}
