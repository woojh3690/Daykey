package woo.Daykey;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

class GetHtmlText{
    private String urlAddress;
    private String htmlString = "noData";

    GetHtmlText(String urlAddress) {
        this.urlAddress = urlAddress;
    }

    String getHtmlString() {
        final StringBuilder sb = new StringBuilder();
        HttpURLConnection conn = null;
        try {
            URL url = new URL(urlAddress);
            conn = (HttpURLConnection)url.openConnection();//접속

            if (conn != null) {
                conn.setConnectTimeout(2000);
                conn.setUseCaches(false);

                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    //데이터 읽기
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));

                    while(true) {
                        String line = br.readLine();
                        if (line == null) break;
                        sb.append(line).append("\n");
                    }
                    br.close(); //스트림 해제
                }
            }
            htmlString = sb.toString();

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            assert conn != null;
            conn.disconnect();//연결 끊기
        }

        return htmlString;
    }

}
