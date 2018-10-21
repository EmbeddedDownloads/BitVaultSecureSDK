package receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import commons.SDKConstants;
import utils.NetworkUtil;
import utils.SDKUtils;

/**
 * Created by ${e} on 7/18/2017.
 */

public class NetworkChangeReceiver extends BroadcastReceiver {
    private String TAG = NetworkChangeReceiver.class.getSimpleName();
    private static boolean isFirstConnect = true;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (checkInternet(context)) {
            if (isFirstConnect) {
                isFirstConnect = false;
            }
        } else {
            SDKUtils.showErrorLog(TAG, "Internet is not working");
            SDKConstants.isMQTTRegistered = false;
            isFirstConnect = true;
        }
    }

    /***
     * This method is used to check the internet if internet is working or not
     * @param context
     * @return
     */
    private boolean checkInternet(Context context) {
        if (NetworkUtil.getConnectivityStatus(context)) {
            return true;
        } else {
            return false;
        }
    }
}
