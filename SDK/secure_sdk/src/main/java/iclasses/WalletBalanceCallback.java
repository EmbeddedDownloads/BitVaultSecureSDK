package iclasses;

import com.android.volley.VolleyError;

import model.WalletBalanceModel;

/**
 * Created by ${e} on 5/26/2017.
 */

public interface WalletBalanceCallback {
    public void successWalletBalanceCallback(WalletBalanceModel mWalletBalanceModel);
    public void failedWalletBalanceCallback(VolleyError mError);
}
