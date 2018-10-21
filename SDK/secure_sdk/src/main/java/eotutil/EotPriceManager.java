package eotutil;

import android.content.Context;

import com.embedded.wallet.R;

import iclasses.iclasseseot.EotPriceConverterListener;
import utils.SDKUtils;
import webservicescontroller.eotservices.EotCurrencyUpdateHandler;

/**
 * Created by vvdn on 11/2/2017.
 */

public class EotPriceManager {
    private static EotPriceManager mEotPriceManager = null;

    /***
     * This method is used to fetch the current eot price from the web
     * @param mContext
     * @param mEotPriceConverterListener
     */
    public void getEotCurrentPrice(Context mContext, EotPriceConverterListener mEotPriceConverterListener) {
        if (mContext != null) {
            if (SDKUtils.isNetworkAvailable(mContext)) {
                new EotCurrencyUpdateHandler(mEotPriceConverterListener).checkCurrentPrice();
            } else {
                SDKUtils.showToast(mContext, mContext.getResources().getString(R.string.network_error));
            }
        }
    }
}
