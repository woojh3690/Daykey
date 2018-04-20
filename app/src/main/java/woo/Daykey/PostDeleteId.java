package woo.Daykey;

import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 데이터 전송
 */

class PostDeleteId extends Thread {
    int num;
    SQLiteDatabase db = MainActivity.db;
    private Handler handler = MainActivity.mhandler;

    PostDeleteId(int num) {
        this.num = num;
    }

    @Override
    public void run() {
        super.run();
        String result = post(num);
        Log.i("Delete Post Result", "기본 "+result);

        String delete = "DELETE FROM userTable WHERE num=" + num + ";";
        db.execSQL(delete);

        Message message = handler.obtainMessage();
        message.what = 2;
        handler.sendMessage(message);
    }

    private static String post(int num) {
        InputStream is = null;
        String result = "";
        try {
            URL urlCon = new URL("http://wooserver.iptime.org/daykey/schedule/delete");
            HttpURLConnection httpCon = (HttpURLConnection)urlCon.openConnection();

            String json = "";

            JSONObject jsonObject = new JSONObject();
            jsonObject.accumulate("num", num);

            // convert JSONObject to JSON to String
            json = jsonObject.toString();

            // ** Alternative way to convert Person object to JSON string usin Jackson Lib
            // ObjectMapper mapper = new ObjectMapper();
            // json = mapper.writeValueAsString(person);

            // Set some headers to inform server about the type of the content
            httpCon.setRequestProperty("Accept", "application/json");
            httpCon.setRequestProperty("Content-type", "application/json");

            // OutputStream으로 POST 데이터를 넘겨주겠다는 옵션.
            httpCon.setDoOutput(true);
            // InputStream으로 서버로 부터 응답을 받겠다는 옵션.
            httpCon.setDoInput(true);

            OutputStream os = httpCon.getOutputStream();
            os.write(json.getBytes("utf-8"));
            os.flush();
            // receive response as inputStream
            try {
                is = httpCon.getInputStream();
                // convert inputstream to string
                if(is != null)
                    result = convertInputStreamToString(is);
                else
                    result = "Did not work!";
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                httpCon.disconnect();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        return result;
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException{
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        StringBuilder result = new StringBuilder();
        while((line = bufferedReader.readLine()) != null)
            result.append(line);

        inputStream.close();
        return result.toString();
    }
}
