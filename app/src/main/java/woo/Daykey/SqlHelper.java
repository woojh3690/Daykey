package woo.Daykey;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

class SqlHelper extends SQLiteOpenHelper {

    SqlHelper(Context context) {
        super(context, "Database.db", null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            String create1 = "create table " + "dietTable " + "(date INTEGER, menu text);";
            String create2 = "create table " + "calendarTable " + "(date text, schedule text);";
            String create3 = "create table " + "timetable " + "(grade integer, week text, class integer, first text, second text, third text, fourth text, fifth text, sixth text, seventh text)";
            String create4 = "create table " + "userTable" + "(num integer, name text, grade integer, class integer, date text, schedule text, boolean_public integer)";
            db.execSQL(create1);
            db.execSQL(create2);
            db.execSQL(create3);
            db.execSQL(create4);

            insertTimeTable(db); //시간표
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i("onUpgrade", "업그레이드 호출됨");
        String sql = "drop table if exists " + "timetable";
        String create = "create table " + "timetable " + "(grade integer, week text, class integer, first text, second text, third text, fourth text, fifth text, sixth text, seventh text)";
        try {
            db.execSQL(sql);
            db.execSQL(create);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        insertTimeTable(db);
    }

    //기본 보충시간표
    private void insertTimeTable(SQLiteDatabase db) {
        db.execSQL("insert into timetable values('1','월','1','수이','체정','음김','영봉','과이','자율, 동아리','자율, 동아리');");
        db.execSQL("insert into timetable values('1','월','2','과유','체정','史한','음김','논홍','자율, 동아리','자율, 동아리');");
        db.execSQL("insert into timetable values('1','월','3','영완','논홍','수이','과이','원상','자율, 동아리','자율, 동아리');");
        db.execSQL("insert into timetable values('1','월','4','진김','원상','논홍','수이','사현','자율, 동아리','자율, 동아리');");
        db.execSQL("insert into timetable values('1','월','5','기오','사현','국맹','원완','史한','자율, 동아리','자율, 동아리');");
        db.execSQL("insert into timetable values('1','월','6','국맹','국현','원완','사공','수제','자율, 동아리','자율, 동아리');");
        db.execSQL("insert into timetable values('1','월','7','영웅','사공','체정','국현','진김','자율, 동아리','자율, 동아리');");
        db.execSQL("insert into timetable values('1','월','8','과호','기오','체정','영웅','과유','자율, 동아리','자율, 동아리');");
        db.execSQL("insert into timetable values('1','월','9','수제','史한','과유','국맹','과호','자율, 동아리','자율, 동아리');");
        db.execSQL("insert into timetable values('1','월','10','원봉','과유','사현','수제','기오','자율, 동아리','자율, 동아리');");
        db.execSQL("insert into timetable values('1','화','1','논홍','사현','원웅','史김','수이','과유','영완');");
        db.execSQL("insert into timetable values('1','화','2','수공','진김','국맹','영봉','논신','수이','원웅');");
        db.execSQL("insert into timetable values('1','화','3','영봉','수이','음김','과이','史한','국맹','과유');");
        db.execSQL("insert into timetable values('1','화','4','국현','史한','사홍','수이','과이','음김','史김');");
        db.execSQL("insert into timetable values('1','화','5','수이','논홍','史한','국현','사홍','체정','수공');");
        db.execSQL("insert into timetable values('1','화','6','사현','史김','과유','영웅','논홍','체정','영봉');");
        db.execSQL("insert into timetable values('1','화','7','원완','과유','국신','수공','史김','논홍','수제');");
        db.execSQL("insert into timetable values('1','화','8','국신','국맹','수공','원완','수제','史한','사현');");
        db.execSQL("insert into timetable values('1','화','9','수제','논신','史김','사현','체정','원봉','국현');");
        db.execSQL("insert into timetable values('1','화','10','史한','영웅','진김','수제','체정','국현','국맹');");
        db.execSQL("insert into timetable values('1','수','1','국신','수이','국맹','과이','과유','체정','史한');");
        db.execSQL("insert into timetable values('1','수','2','수이','과이','史김','과유','사홍','체정','국현');");
        db.execSQL("insert into timetable values('1','수','3','史김','국현','수이','영웅','체정','사홍','과유');");
        db.execSQL("insert into timetable values('1','수','4','과유','史한','수공','국맹','체정','영완','수이');");
        db.execSQL("insert into timetable values('1','수','5','국현','영봉','논신','영완','史김','수이','과이');");
        db.execSQL("insert into timetable values('1','수','6','진김','영완','영웅','수제','史한','과이','수공');");
        db.execSQL("insert into timetable values('1','수','7','영웅','수제','과이','史한','영완','영봉','국맹');");
        db.execSQL("insert into timetable values('1','수','8','史한','史김','수제','국현','진김','영웅','논신');");
        db.execSQL("insert into timetable values('1','수','9','국맹','사홍','과유','영봉','국현','수제','영웅');");
        db.execSQL("insert into timetable values('1','수','10','영완','논신','영봉','사홍','수공','史김','수제');");
        db.execSQL("insert into timetable values('1','목','1','영봉','국현','수공','영웅','사공','사홍','진김');");
        db.execSQL("insert into timetable values('1','목','2','사공','영완','수이','기오','과이','史한','국신');");
        db.execSQL("insert into timetable values('1','목','3','논신','체정','국현','수공','국맹','영봉','수이');");
        db.execSQL("insert into timetable values('1','목','4','수이','체정','과이','국맹','영봉','논신','영웅');");
        db.execSQL("insert into timetable values('1','목','5','체정','음김','과유','진김','영웅','수이','국맹');");
        db.execSQL("insert into timetable values('1','목','6','체정','기오','사홍','음김','과유','국현','수제');");
        db.execSQL("insert into timetable values('1','목','7','과유','史한','수제','논신','사홍','음김','기오');");
        db.execSQL("insert into timetable values('1','목','8','국맹','사홍','사공','영봉','영완','수제','과유');");
        db.execSQL("insert into timetable values('1','목','9','史한','과이','영완','수제','수공','기오','사공');");
        db.execSQL("insert into timetable values('1','목','10','수제','영웅','국맹','사공','국현','과이','史한');");
        db.execSQL("insert into timetable values('1','금','1','국현','기오','논신','史한','수이','자율활동','방과후활동');");
        db.execSQL("insert into timetable values('1','금','2','영웅','수이','국현','사현','영봉','자율활동','방과후활동');");
        db.execSQL("insert into timetable values('1','금','3','사공','史한','진김','기오','사현','자율활동','방과후활동');");
        db.execSQL("insert into timetable values('1','금','4','기오','사공','영봉','국현','과유','자율활동','방과후활동');");
        db.execSQL("insert into timetable values('1','금','5','영봉','과유','수이','과이','사공','자율활동','방과후활동');");
        db.execSQL("insert into timetable values('1','금','6','史한','국맹','수제','과호','논신','자율활동','방과후활동');");
        db.execSQL("insert into timetable values('1','금','7','과호','사현','체정','국맹','수제','자율활동','방과후활동');");
        db.execSQL("insert into timetable values('1','금','8','수제','과이','체정','음김','논홍','자율활동','방과후활동');");
        db.execSQL("insert into timetable values('1','금','9','논홍','음김','영웅','체정','진김','자율활동','방과후활동');");
        db.execSQL("insert into timetable values('1','금','10','과유','논홍','음김','체정','과호','자율활동','방과후활동');");
        db.execSQL("insert into timetable values('2','월','1','논문','음진','영경','사홍','생람','자율,동아리','자율, 동아리');");
        db.execSQL("insert into timetable values('2','월','2','수승','사홍','영지','생람','음진','자율,동아리','자율, 동아리');");
        db.execSQL("insert into timetable values('2','월','3','영경','국양','수문','수승','사홍','자율,동아리','자율, 동아리');");
        db.execSQL("insert into timetable values('2','월','4','영지','국광','사홍','미부','국양','자율,동아리','자율, 동아리');");
        db.execSQL("insert into timetable values('2','월','5','논신','수훈','수오','물수','영경','자율,동아리','자율, 동아리');");
        db.execSQL("insert into timetable values('2','월','6','미부','수오','논신','국고','영지','자율,동아리','자율, 동아리');");
        db.execSQL("insert into timetable values('2','월','7','국양','한문','생람','논신','물수','자율,동아리','자율, 동아리');");
        db.execSQL("insert into timetable values('2','월','8','국광','생람','국양','수오','논신','자율,동아리','자율, 동아리');");
        db.execSQL("insert into timetable values('2','월','9','수송','영경','미부','국광','화장','자율,동아리','자율, 동아리');");
        db.execSQL("insert into timetable values('2','월','10','지현','영지','물수','수훈','국고','자율,동아리','자율, 동아리');");
        db.execSQL("insert into timetable values('2','월','11','음진','수송','지현','한문','국임','자율,동아리','자율, 동아리');");
        db.execSQL("insert into timetable values('2','월','12','국고','논승','화장','국임','수송','자율,동아리','자율, 동아리');");
        db.execSQL("insert into timetable values('2','화','1','미부','국고','국임','한문','수승','영상','수문');");
        db.execSQL("insert into timetable values('2','화','2','수승','한문','국고','국임','미부','국광','영상');");
        db.execSQL("insert into timetable values('2','화','3','영상','음진','국광','미부','논문','영지','수승');");
        db.execSQL("insert into timetable values('2','화','4','한문','영상','수승','음진','수문','영경','국임');");
        db.execSQL("insert into timetable values('2','화','5','수오','국광','영지','논승','국임','지현','한문');");
        db.execSQL("insert into timetable values('2','화','6','음진','물수','영경','수오','화장','수훈','국광');");
        db.execSQL("insert into timetable values('2','화','7','국임','체육','화장','국광','지현','미부','수오');");
        db.execSQL("insert into timetable values('2','화','8','수훈','체육','지현','영상','국고','수오','화장');");
        db.execSQL("insert into timetable values('2','화','9','과강','미부','음진','물수','국고','체육','생람');");
        db.execSQL("insert into timetable values('2','화','10','화장','생람','체육','수송','음김','과강','미부');");
        db.execSQL("insert into timetable values('2','화','11','영경','수송','수훈','체육','물수','국고','생람');");
        db.execSQL("insert into timetable values('2','화','12','영지','지현','생람','체육','수훈','수송','음진');");
        db.execSQL("insert into timetable values('2','수','1','윤홍','체육','영지','사현','음진','지현','윤고');");
        db.execSQL("insert into timetable values('2','수','2','사현','체육','영경','윤고','음진','윤홍','수문');");
        db.execSQL("insert into timetable values('2','수','3','음진','수문','윤홍','체육','윤고','사현','미부');");
        db.execSQL("insert into timetable values('2','수','4','음진','사현','윤고','체육','윤홍','수승','국광');");
        db.execSQL("insert into timetable values('2','수','5','화장','생람','체육','수오','미부','국고','음진');");
        db.execSQL("insert into timetable values('2','수','6','수훈','화장','체육','국광','국임','생람','음진');");
        db.execSQL("insert into timetable values('2','수','7','물수','미부','논승','국고','수오','음김','영경');");
        db.execSQL("insert into timetable values('2','수','8','국임','수오','국광','미부','물수','음김','영지');");
        db.execSQL("insert into timetable values('2','수','9','영지','국임','지현','수훈','생람','수송','논승');");
        db.execSQL("insert into timetable values('2','수','10','영경','국고','국임','수송','국광','물수','지현');");
        db.execSQL("insert into timetable values('2','수','11','미부','과강','생람','논승','수송','수훈','화장');");
        db.execSQL("insert into timetable values('2','수','12','지현','수송','미부','과강','수훈','화장','국고');");
        db.execSQL("insert into timetable values('2','목','1','사현','수문','수승','영지','윤고','윤홍','미부');");
        db.execSQL("insert into timetable values('2','목','2','윤홍','윤고','사현','영경','미부','수승','국임');");
        db.execSQL("insert into timetable values('2','목','3','국광','국임','윤고','사현','윤홍','한문','생람');");
        db.execSQL("insert into timetable values('2','목','4','수승','미부','윤홍','논문','수문','사현','윤고');");
        db.execSQL("insert into timetable values('2','목','5','지현','영상','음진','수오','수훈','국광','화장');");
        db.execSQL("insert into timetable values('2','목','6','영상','생람','수오','논승','한문','미부','지현');");
        db.execSQL("insert into timetable values('2','목','7','수오','영지','수훈','생람','영상','화장','음진');");
        db.execSQL("insert into timetable values('2','목','8','음진','영경','생람','지현','물수','수훈','수오');");
        db.execSQL("insert into timetable values('2','목','9','국임','화장','수송','영상','지현','영지','물수');");
        db.execSQL("insert into timetable values('2','목','10','미부','수훈','한문','화장','수송','영경','논승');");
        db.execSQL("insert into timetable values('2','목','11','물수','지현','영지','국광','음김','수송','영상');");
        db.execSQL("insert into timetable values('2','목','12','생람','물수','영경','수송','음김','국임','국광');");
        db.execSQL("insert into timetable values('2','금','1','국광','사홍','국임','수승','국양','자율활동','방과후활동');");
        db.execSQL("insert into timetable values('2','금','2','국양','논문','수문','사홍','지현','자율활동','방과후활동');");
        db.execSQL("insert into timetable values('2','금','3','수승','영지','국고','지김','사홍','자율활동','방과후활동');");
        db.execSQL("insert into timetable values('2','금','4','지김','영경','사홍','국고','생람','자율활동','방과후활동');");
        db.execSQL("insert into timetable values('2','금','5','물수','생람','영지','국양','미부','자율활동','방과후활동');");
        db.execSQL("insert into timetable values('2','금','6','수오','국양','영경','지현','물수','자율활동','방과후활동');");
        db.execSQL("insert into timetable values('2','금','7','영지','지현','수오','수훈','국광','자율활동','방과후활동');");
        db.execSQL("insert into timetable values('2','금','8','영경','화장','논승','미부','한문','자율활동','방과후활동');");
        db.execSQL("insert into timetable values('2','금','9','음진','수훈','한문','수송','국고','자율활동','방과후활동');");
        db.execSQL("insert into timetable values('2','금','10','국임','수송','생람','영상','음진','자율활동','방과후활동');");
        db.execSQL("insert into timetable values('2','금','11','국고','미부','화장','국임','영지','자율활동','방과후활동');");
        db.execSQL("insert into timetable values('2','금','12','한문','영상','미부','물수','영경','자율활동','방과후활동');");
        db.execSQL("insert into timetable values('3','월','1','국강','논조','진고','체최','수공','자율, 동아리','자율, 동아리');");
        db.execSQL("insert into timetable values('3','월','2','진고','국현','수공','체최','논조','자율, 동아리','자율, 동아리');");
        db.execSQL("insert into timetable values('3','월','3','논조','수공','국손','진고','체최','자율, 동아리','자율, 동아리');");
        db.execSQL("insert into timetable values('3','월','4','수공','진고','영문','논조','체최','자율, 동아리','자율, 동아리');");
        db.execSQL("insert into timetable values('3','월','5','국현','사김','영수','지김','생고','자율, 동아리','자율, 동아리');");
        db.execSQL("insert into timetable values('3','월','6','지김','국강','화김','생고','사김','자율, 동아리','자율, 동아리');");
        db.execSQL("insert into timetable values('3','월','7','영문','영수','사김','국손','국현','자율, 동아리','자율, 동아리');");
        db.execSQL("insert into timetable values('3','월','8','화김','수송','수강','영문','영수','자율, 동아리','자율, 동아리');");
        db.execSQL("insert into timetable values('3','월','9','수강','지김','수박','화김','수송','자율, 동아리','자율, 동아리');");
        db.execSQL("insert into timetable values('3','월','10','수박','생고','수송','국강','국손','자율, 동아리','자율, 동아리');");
        db.execSQL("insert into timetable values('3','화','1','국현','국손','사공','논강','지조','윤고','논송');");
        db.execSQL("insert into timetable values('3','화','2','논강','국현','지조','국손','윤고','논송','사공');");
        db.execSQL("insert into timetable values('3','화','3','국손','논강','윤고','국양','사공','지조','영수');");
        db.execSQL("insert into timetable values('3','화','4','지조','국강','영수','사공','논강','영문','윤고');");
        db.execSQL("insert into timetable values('3','화','5','영문','수송','지김','물고','수박','수강','생고');");
        db.execSQL("insert into timetable values('3','화','6','수박','영문','수송','영수','국현','국강','물고');");
        db.execSQL("insert into timetable values('3','화','7','화김','국양','생고','수송','지김','국현','수박');");
        db.execSQL("insert into timetable values('3','화','8','지김','수박','국강','생고','국손','화김','국양');");
        db.execSQL("insert into timetable values('3','화','9','체최','화김','물고','국강','영수','생고','지김');");
        db.execSQL("insert into timetable values('3','화','10','체최','생고','영문','지김','국양','물고','화김');");
        db.execSQL("insert into timetable values('3','수','1','논박','국현','지조','영상','사공','영수','진문');");
        db.execSQL("insert into timetable values('3','수','2','지조','국강','영상','논박','진문','사공','영수');");
        db.execSQL("insert into timetable values('3','수','3','진문','사공','논박','논강','영문','국현','지조');");
        db.execSQL("insert into timetable values('3','수','4','사공','진문','영수','지조','논박','논강','국현');");
        db.execSQL("insert into timetable values('3','수','5','체최','영수','국손','수송','화김','물고','국양');");
        db.execSQL("insert into timetable values('3','수','6','체최','수강','국양','생고','수송','화김','지김');");
        db.execSQL("insert into timetable values('3','수','7','물고','화김','수강','영문','국강','지김','생고');");
        db.execSQL("insert into timetable values('3','수','8','화김','지김','생고','영수','물고','국손','수송');");
        db.execSQL("insert into timetable values('3','수','9','국강','영문','물고','국양','국손','생고','영상');");
        db.execSQL("insert into timetable values('3','수','10','수송','국손','국현','지김','영상','국강','영문');");
        db.execSQL("insert into timetable values('3','목','1','영수','지조','사김','국양','체최','영문','중김');");
        db.execSQL("insert into timetable values('3','목','2','지조','중김','국양','영문','체최','영수','국손');");
        db.execSQL("insert into timetable values('3','목','3','수송','영수','국강','체최','지조','중김','국현');");
        db.execSQL("insert into timetable values('3','목','4','중김','국손','수송','체최','국양','지조','사김');");
        db.execSQL("insert into timetable values('3','목','5','영문','국강','국현','수강','논고','화김','수박');");
        db.execSQL("insert into timetable values('3','목','6','국손','논고','수강','화김','영문','물고','영수');");
        db.execSQL("insert into timetable values('3','목','7','논고','수송','체최','수박','수강','생고','물고');");
        db.execSQL("insert into timetable values('3','목','8','과강','과강','체최','물고','국현','사김','국강');");
        db.execSQL("insert into timetable values('3','목','9','화김','생고','과강','과강','수박','국손','수송');");
        db.execSQL("insert into timetable values('3','목','10','수강','화김','물고','사김','생고','과강','과강');");
        db.execSQL("insert into timetable values('3','금','1','수송','국손','수공','윤고','지조','자율활동','방과후활동');");
        db.execSQL("insert into timetable values('3','금','2','지조','윤고','사김','수송','수공','자율활동','방과후활동');");
        db.execSQL("insert into timetable values('3','금','3','수공','지조','영문','사김','윤고','자율활동','방과후활동');");
        db.execSQL("insert into timetable values('3','금','4','윤고','수공','지조','국손','국현','자율활동','방과후활동');");
        db.execSQL("insert into timetable values('3','금','5','국강','체최','생고','논완','화김','자율활동','방과후활동');");
        db.execSQL("insert into timetable values('3','금','6','수박','체최','국현','생고','논완','자율활동','방과후활동');");
        db.execSQL("insert into timetable values('3','금','7','체최','논완','국강','화김','영수','자율활동','방과후활동');");
        db.execSQL("insert into timetable values('3','금','8','체최','생고','수박','수강','영문','자율활동','방과후활동');");
        db.execSQL("insert into timetable values('3','금','9','사김','영문','수강','국현','체최','자율활동','방과후활동');");
        db.execSQL("insert into timetable values('3','금','10','수강','영수','화김','수박','체최','자율활동','방과후활동');");
    }
}
