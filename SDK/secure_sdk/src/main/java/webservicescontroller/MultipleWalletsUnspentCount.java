package webservicescontroller;/**
 * Created by Deepak on 4/6/2017.
 */

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import commons.SDKConstants;
import commons.SDKHelper;
import controller.SDKControl;
import iclasses.WalletUnspentCountHandler;
import utils.SDKUtils;

/**********************************************************************
 * VVDN Technologies
 * All rights reserved.
 * This software is the confidential and proprietary information of
 * VVDN Technologies. ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with VVDN.
 ********************************************************************/
public class MultipleWalletsUnspentCount {
    /**
     * Debugging variable of this class
     */
    private String TAG = MultipleWalletsUnspentCount.class.getSimpleName();
    /**
     * Request json instance object
     */
    private JSONObject jsonObject;
    /**
     * This interface class object is used to manage the response of the uncounted wallet bitoins
     */
    private WalletUnspentCountHandler mWalletUnspentCountHandler = null;
    private int wallet_id = -1;

    public MultipleWalletsUnspentCount(WalletUnspentCountHandler mWalletUnspentCountHandler) {
        this.mWalletUnspentCountHandler = mWalletUnspentCountHandler;
    }

    /**
     * Making json object request
     *
     * @param mWalletsListOfAddresses
     */
    public void getWalletsUnspent(ArrayList<String> mWalletsListOfAddresses) {
        this.wallet_id = wallet_id;
        String mWalletAPIUrl = getServerUrl(mWalletsListOfAddresses);
        SDKUtils.showLog(TAG, "Checking for wallet : " + mWalletAPIUrl);
        JsonArrayRequest jsonObjReq = new JsonArrayRequest(Request.Method.GET, mWalletAPIUrl,
                null, new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray mUncountCoinsResponse) {
                try {
                    if (mWalletUnspentCountHandler != null && mUncountCoinsResponse.length() >= 1) {
                        mWalletUnspentCountHandler.walletUnspentCountSuccess(mUncountCoinsResponse);
                    } else {
                        if (mWalletUnspentCountHandler != null)
                            mWalletUnspentCountHandler.walletUnspentCountFailure(null);
                    }
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }
            }

        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if (mWalletUnspentCountHandler != null)
                    mWalletUnspentCountHandler.walletUnspentCountFailure(volleyError);
            }
        }) {

            /**
             * Passing some request headers
             * */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = super.getHeaders();
                if (headers == null || headers.equals(Collections.emptyMap())) {
                    headers = new HashMap<String, String>();
                }
                return headers;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return super.getParams();
            }

            @Override
            protected Response<JSONArray> parseNetworkResponse(NetworkResponse response) {
                return super.parseNetworkResponse(response);
            }
        };
        SDKControl.getInstance().cancelPendingRequests(SDKHelper.TAG_WALLET_UNSPENT_COUNT);

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                SDKHelper.WEBCALL_TIMEOUT, SDKHelper.RETRY_COUNT,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        jsonObjReq.setShouldCache(false);
        SDKControl.getInstance().addToRequestQueue(jsonObjReq, SDKHelper.TAG_WALLET_UNSPENT_COUNT);
    }

    /***
     * This method is used to get the address of the server to get the data from
     *
     * @param mList
     */
    private String getServerUrl(ArrayList<String> mList) {
        String mInsightUrl = ""/*SDKHelper.HTTPS_EXT + SDKHelper.INSIGHT_SERVER_DOMAIN_NAME +
                SDKHelper.INSIGHT_API_EXT + SDKHelper.INSIGHT_API_ADDRESS + wallet_address*//*"1yZDSbmWRKbxcXU1jBx4Agw1dEmor9rtn"*//* +
                SDKHelper.INSIGHT_TO*/;
        String mWalletsAddresses = SDKUtils.getStringFromArrayListWithSeparator(mList);
        if (SDKConstants.WALLET_TYPE == SDKHelper.ZERO)
            mInsightUrl = SDKHelper.TESTNET_URL_BIT_PAY_EXPLORERS + mWalletsAddresses + SDKHelper.INSIGHT_TO;
        else
            mInsightUrl = SDKHelper.MAIN_BIT_PAY_EXPLORERS + mWalletsAddresses + SDKHelper.INSIGHT_TO;
        return mInsightUrl;
    }
}
