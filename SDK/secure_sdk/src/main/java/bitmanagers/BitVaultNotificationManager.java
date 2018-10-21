package bitmanagers;

import android.content.Context;
import android.content.Intent;

/**
 * Created by ${e} on 7/14/2017.
 */

public class BitVaultNotificationManager {
    private Context mContext = null;

    /***
     * This method is used to start the MQTT service to subscribed for notifications
     * @param mContext
     */
    public void subscribeNotifications(Context mContext) {
        this.mContext = mContext;
        try {
            if (mContext != null) {
                mContext.startService(new Intent(mContext, BitVaultMQTTServiceManager.class));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
