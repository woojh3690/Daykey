package woo.Daykey;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * 네트워크에 연결됬는지 확인
 */

class GetWhatKindOfNetwork {

    static boolean check(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean check = false;
        check = activeNetwork != null && activeNetwork.isConnected();
        Log.i("check" +
                "network", check + "");
        return check;
    }
}
