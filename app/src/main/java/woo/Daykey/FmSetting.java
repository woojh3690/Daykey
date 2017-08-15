package woo.Daykey;

import android.content.ContentUris;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.provider.CalendarContract;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import static java.lang.Integer.parseInt;

public class FmSetting extends PreferenceFragment{
    Context context = MainActivity.getMainContext();
    SettingPreferences set = new SettingPreferences(context);
    String strVersion;
    View view;

    Preference setTime, switchAlarm, calendarSyc, name, aClass, email, grade, version;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        addPreferencesFromResource(R.xml.referencescreen);

        switchAlarm = findPreference("switch_alarm");
        setTime = findPreference("time_set");
        calendarSyc = findPreference("calendar_syc");
        name = findPreference("name");
        grade = findPreference("grade");
        aClass = findPreference("class");
        email = findPreference("email");
        version = findPreference("version");

        try {
            PackageInfo i = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            strVersion = i.versionName;
        } catch(PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        setSummary();

        switchAlarm.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                AlarmBroadcast alarmBroadcast = new AlarmBroadcast(context);
                boolean switched = (boolean)newValue;

                if (switched) {
                    alarmBroadcast.Alarm(0);
                    set.saveBoolean("alarm", true);
                    setTime.setEnabled(true);
                    setTime.setShouldDisableView(true);
                } else {
                    alarmBroadcast.cancelAlarm();
                    set.saveBoolean("alarm", false);
                    setTime.setEnabled(false);
                    setTime.setShouldDisableView(false);
                }

                return true;
            }
        });

        setTime.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                TimePickerFragment timePickerFragment = new TimePickerFragment();
                timePickerFragment.show(getFragmentManager(), "TAG");
                return false;
            }
        });

        calendarSyc.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                boolean switched = (boolean)newValue;

                if (switched) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            new AddCalendar(context);
                        }
                    }).start();

                    set.saveBoolean("calendar", true);
                } else {
                    context.getContentResolver().delete (ContentUris.withAppendedId (CalendarContract.Calendars.CONTENT_URI, set.getInt("id")), null, null);
                    Toast.makeText(context, "일정이 지워졌습니다.", Toast.LENGTH_SHORT).show();
                    set.saveBoolean("calendar", false);
                }

                return true;
            }
        });

        name.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                String textName = " ";
                textName = (String)newValue;
                set.saveString("name", textName);
                name.setSummary(textName);
                return true;
            }
        });

        grade.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                String temp = (String)newValue;

                try {
                    int intGrade = parseInt(temp);
                    set.saveInt("grade", intGrade);
                    setSummary();
                } catch (Exception e) {
                    grade.setSummary(" ");
                }

                return true;
            }
        });

        aClass.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                String temp = (String)newValue;

                try {
                    int intClass = Integer.parseInt(temp);
                    set.saveInt("class", intClass);
                    setSummary();
                } catch (Exception e) {
                    aClass.setSummary(" ");
                }

                return true;
            }
        });

        email.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                String textEmail = " ";
                textEmail = (String)newValue;
                set.saveString("email", textEmail);
                email.setSummary(textEmail);
                return true;
            }
        });

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    void setSummary() {
        int intGrade = set.getInt("grade");
        int intClass = set.getInt("class");
        name.setSummary(set.getString("name"));
        email.setSummary(set.getString("email"));

        if (!(intGrade == 0)) {
            grade.setSummary(intGrade + "학년");
        }

        if (!(intClass == 0)) {
            aClass.setSummary(intClass + "반");
        }

        version.setSummary(strVersion);
    }
}