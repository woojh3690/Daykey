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
        NetworkInfo activeNetwork = null;
        try {
            assert cm != null;
            activeNetwork = cm.getActiveNetworkInfo();
        } catch (AssertionError e) {
            e.printStackTrace();
        }
        return activeNetwork != null && activeNetwork.isConnected();
    }
}
