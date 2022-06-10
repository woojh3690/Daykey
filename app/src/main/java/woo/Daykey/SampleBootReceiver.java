package woo.Daykey;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class SampleBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            SettingPreferences set = new SettingPreferences(context);
            if (set.getBoolean("alarm"))
                new AlarmBroadcast(context).AlarmWithNoToast();
        }
    }
}

