package iclasses;

import com.android.volley.VolleyError;

import org.json.JSONArray;

/**
 * Created by Deepak on 4/6/2017.
 */

public interface WalletUnspentCountHandler {
    public void walletUnspentCountSuccess(JSONArray mJsonArrayResponse);
    public void walletUnspentCountFailure(VolleyError mErrorResponse);
}
