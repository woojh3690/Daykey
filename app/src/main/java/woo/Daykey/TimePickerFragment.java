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

class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {
    SettingPreferences set;
    Context mainContext;

    public TimePickerFragment(Context mainContext, SettingPreferences set) {
        this.mainContext = mainContext;
        this.set = set;
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        set.saveInt("hour", hourOfDay);
        set.saveInt("min", minute);

        AlarmBroadcast alarm = new AlarmBroadcast(mainContext);
        alarm.Alarm(0);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int hour = set.getInt("hour");
        int min = set.getInt("min");
        return new TimePickerDialog(this.getActivity(), this, hour, min, false);
    }

}
