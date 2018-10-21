package bitmanagers;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.IBinder;

import pushmessages.PushMessageClientAsync;
import utils.NetworkUtil;
import utils.SDKUtils;

/**
 * Created by ${e} on 7/14/2017.
 */
public class BitVaultMQTTServiceManager extends Service {
    private Context mContext = null;
    private String TAG = BitVaultMQTTServiceManager.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mContext = this;
        initializeNetworkVerifier();
        new PushMessageClientAsync(this);
        return Service.START_STICKY;
    }

    /***
     * Initialize the network verifier
     */
    private void initializeNetworkVerifier() {
        registerReceiver(mBroadcastReceiver,
                new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sendBroadcast(new Intent("BackgroundService"));
    }

    /***
     * This method is used to register for network calls, if network changes in background
     */
    BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (NetworkUtil.getConnectivityStatus(mContext)) {
                new PushMessageClientAsync(mContext);
            } else {
                SDKUtils.showLog(TAG, "----Network Down----");
            }
        }
    };
}
