package webservicescontroller.eotservices;/**
 * Created by Deepak on 4/6/2017.
 */

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import commons.SDKHelper;
import controller.SDKControl;
import iclasses.TransactionHistoryCallback;

/**********************************************************************
 * VVDN Technologies
 * All rights reserved.
 * This software is the confidential and proprietary information of
 * VVDN Technologies. ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with VVDN.
 ********************************************************************/
public class EotTransactionsHistoryHandler {
    /**
     * Debugging variable of this class
     */
    private String TAG = EotTransactionsHistoryHandler.class.getSimpleName();
    /**
     * Request json instance object
     */
    private JSONObject jsonObject;
    /**
     * This interface class object is used to manage the response of the uncounted wallet bitoins
     */
    private TransactionHistoryCallback mTransactionHistoryCallback = null;

    public EotTransactionsHistoryHandler(TransactionHistoryCallback mTransactionHistoryCallback) {
        this.mTransactionHistoryCallback = mTransactionHistoryCallback;
    }

    /**
     * Making json object request
     *
     * @param wallet_address
     */
    public void getHistory(final String wallet_address) {
        String mWalletAPIUrl = getServerUrl(wallet_address);
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, mWalletAPIUrl,
                null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject mWalletHistory) {
                try {
                    if (mTransactionHistoryCallback != null)
                        mTransactionHistoryCallback.transactionHistorySuccess(mWalletHistory, wallet_address);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }
            }

        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if (mTransactionHistoryCallback != null)
                    mTransactionHistoryCallback.transactionHistoryFailed(volleyError);
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
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                return super.parseNetworkResponse(response);
            }
        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                SDKHelper.WEBCALL_TIMEOUT, SDKHelper.RETRY_COUNT,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        jsonObjReq.setShouldCache(false);
        SDKControl.getInstance().addToRequestQueue(jsonObjReq, SDKHelper.TAG_TRANSACTION_HISTORY_HANDLER);
    }

    /***
     * This method is used to get the address of the server to get the data from
     *
     * @param wallet_address
     */
    private String getServerUrl(String wallet_address) {
        String mInsightUrl = "";
        mInsightUrl = SDKHelper.EOT_TRANSACTION_HISTORY_URL + wallet_address + SDKHelper.EOT_TRANSACTION_PAGE;
        return mInsightUrl;
    }
}
