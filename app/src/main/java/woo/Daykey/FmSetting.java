package woo.Daykey;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.pes.androidmaterialcolorpickerdialog.ColorPicker;
import com.pes.androidmaterialcolorpickerdialog.ColorPickerCallback;

import static java.lang.Integer.parseInt;
import static woo.Daykey.MainActivity.set;

public class FmSetting extends PreferenceFragment {
    String strVersion;
    View view;

    Preference setTime, switchAlarm, calendarSyc, name, aClass, email, grade, version, password,
            reset, timer;

    public FmSetting() {
    }

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
        password = findPreference("password");
        version = findPreference("version");
        reset = findPreference("reset");
        timer = findPreference("timer");

        try {
            PackageInfo i = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
            strVersion = i.versionName;
        } catch(PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        setSummary();
        setTime.setEnabled(set.getBoolean("alarm"));

        switchAlarm.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                AlarmBroadcast alarmBroadcast = new AlarmBroadcast(getActivity());
                boolean switched = (boolean)newValue;
                if (switched) {
                    alarmBroadcast.Alarm();
                    set.saveBoolean("alarm", true);
                    setTime.setEnabled(true);
                } else {
                    alarmBroadcast.cancelAlarm();
                    set.saveBoolean("alarm", false);
                    setTime.setEnabled(false);
                }
                return true;
            }
        });

        setTime.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                TimePickerFragment timePickerFragment = new TimePickerFragment();
                timePickerFragment.show(getFragmentManager(), "TAG");
                return true;
            }
        });

        timer.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                boolean switched = (boolean)newValue;
                if (switched) {
                    set.saveBoolean("timer", true);
                } else {
                    set.saveBoolean("timer", false);
                }
                return true;
            }
        });

        calendarSyc.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                boolean switched = (boolean)newValue;

                if (switched) {
                    String strColor = set.getString("color");
                    final ColorPicker colorPicker = new ColorPicker(
                            getActivity(), // Context
                            Integer.parseInt(strColor.substring(1, 3), 16), // Default Alpha value
                            Integer.parseInt(strColor.substring(3, 5), 16), // Default Red value
                            Integer.parseInt(strColor.substring(5, 7), 16), // Default Green value
                            Integer.parseInt(strColor.substring(7, 9), 16) // Default Blue value
                    );
                    colorPicker.enableAutoClose();
                    colorPicker.setCallback(new ColorPickerCallback() {
                        @Override
                        public void onColorChosen(@ColorInt int color) {
                            set.saveString("color", String.format("#%08X", (color)));
                            final CalendarManager calendarManager = new CalendarManager(getActivity());
                            if (calendarManager.deleteAccount()) {
                                if (calendarManager.addAccount()) {
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            calendarManager.addSchedule();
                                        }
                                    }).start();
                                }
                            }
                            set.saveBoolean("calendar", true);
                        }
                    });
                    colorPicker.show();
                    Toast.makeText(getActivity(), "달력에 표시될 색을 선택해 주세요.", Toast.LENGTH_SHORT).show();
                } else {
                    new CalendarManager(getActivity()).deleteAccount();
                    set.saveBoolean("calendar", false);
                    Toast.makeText(getActivity(), "일정이 지워졌습니다.", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });

        name.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                String textName;
                textName = (String)newValue;
                if (!TextUtils.isEmpty(textName)) {
                    set.saveString("name", textName);
                    name.setSummary(textName);
                } else {
                    Toast.makeText(getActivity(), "이름을 입력해 주세요", Toast.LENGTH_SHORT).show();
                }
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
                String textEmail = (String)newValue;
                set.saveString("email", textEmail);
                email.setSummary(textEmail);
                return true;
            }
        });

        password.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                try {
                    String password = (String)newValue;
                    if(!password.equals("")) {
                        if(password.length() < 4) {
                            Toast.makeText(getActivity(), "비밀번호는 4자리 이상 입력해야 합니다.", Toast.LENGTH_SHORT).show();
                            return false;
                        } else {
                            set.saveInt("password", parseInt(password.trim()));
                        }
                    } else {
                        Toast.makeText(getActivity(), "값을 입력해 주세요", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            }
        });

        reset.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                ResetFragment resetFragment = new ResetFragment();
                resetFragment.show(getFragmentManager(), "TAG");
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

        if (!(intGrade == -1)) {
            grade.setSummary(intGrade + "학년");
        }

        if (!(intClass == -1)) {
            aClass.setSummary(intClass + "반");
        }
        version.setSummary(strVersion);
    }
}