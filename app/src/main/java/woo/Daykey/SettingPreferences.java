package woo.Daykey;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import static android.content.Context.MODE_PRIVATE;

/**
 * 설정값 저장
 */

class SettingPreferences{
    private SharedPreferences pref;

    SettingPreferences(Context mainContext) {
        pref = mainContext.getSharedPreferences("setting", MODE_PRIVATE);
    }

    //값 불러오기(부울)
    Boolean getBoolean(String key){

        Boolean defaultSetting = false;
        switch (key) {
            case "alarm":
                defaultSetting = true;
                break;
            case "calendar":
                defaultSetting = true;
                break;
            case "diet":
                defaultSetting = false;
                break;
            case "firstStart":
                defaultSetting = true;
                break;
            default:
                Log.w("Setting getBoolean", "값 불러오기 오류");
                break;
        }

        return pref.getBoolean(key , defaultSetting);
    }

    //값 저장하기(부울)
    void saveBoolean(String key, Boolean bool){
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(key, bool);
        editor.apply();
    }

    //설정 불러오기(int)
    int getInt(String key) {
        int defaultSet;
        switch (key) {
            case "hour":
                defaultSet = 7;
                break;
            case "min":
                defaultSet = 30;
                break;
            case "id":
                defaultSet = -1;
                break;
            case "db_version":
                defaultSet = -1;
                break;
            case "class":
                defaultSet = -1;
                break;
            case "grade":
                defaultSet = -1;
                break;
            case "password":
                defaultSet = -1;
                break;
            default:
                defaultSet = -1;
        }

        return pref.getInt(key, defaultSet);
    }

    //설정 설정하기(int)
    void saveInt(String key, int num) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(key, num);
        editor.apply();
    }

    //값 불러오기(String)
    String getString(String key){
        String defaultSetting = "오류";
        switch (key) {
            case "name":
                defaultSetting = " ";
                break;
            case "email":
                defaultSetting = " ";
                break;
            default:
                Log.w("Setting getString", "값 불러오기 오류");
                break;
        }

        return pref.getString(key , defaultSetting);
    }

    //설정 저장하기(String)
    void saveString(String key, String str) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(key, str);
        editor.apply();
    }
}