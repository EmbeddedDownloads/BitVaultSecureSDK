package webservicescontroller;/**
 * Created by ${e} on 5/26/2017.
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

import java.text.DecimalFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import commons.SDKHelper;
import controller.SDKControl;
import iclasses.WalletBalanceCallback;
import model.WalletBalanceModel;

/**********************************************************************
 * Embedded Downloads
 * All rights reserved.
 * This software is the confidential and proprietary information of
 * Embedded Downloads. ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Embedded Downloads.
 ********************************************************************/
public class FetchEotWalletBalance {
    /**
     * Debugging variable of this class
     */
    private String TAG = FetchEotWalletBalance.class.getSimpleName();
    /**
     * Request json instance object
     */
    private JSONObject jsonObject;
    /**
     * This interface class object is used to manage the response of the uncounted wallet bitoins
     */
    private WalletBalanceCallback mWalletBalanceCallback = null;
    private WalletBalanceModel mWalletBalanceModel = null;

    public FetchEotWalletBalance(WalletBalanceCallback mWalletBalanceCallback) {
        this.mWalletBalanceCallback = mWalletBalanceCallback;
        mWalletBalanceModel = new WalletBalanceModel();
    }

    /**
     * Making json object request
     *
     * @param wallet_address
     */
    public void checkWalletForUpdateBalance(String wallet_address) {
        String mWalletAPIUrl = getServerUrl(wallet_address);

        JsonArrayRequest jsonObjReq = new JsonArrayRequest(Request.Method.GET, mWalletAPIUrl,
                null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray mWalletBalanceResponse) {
                try {
                    parseWalletBalanceResponse(mWalletBalanceResponse);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if (mWalletBalanceCallback != null)
                    mWalletBalanceCallback.failedWalletBalanceCallback(volleyError);
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
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                SDKHelper.WEBCALL_TIMEOUT, SDKHelper.RETRY_COUNT,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        SDKControl.getInstance().addToRequestQueue(jsonObjReq, SDKHelper.TAG_FETCH_WALLET_BALANCE);
    }

    /***
     * This method is used to parse the network response
     *
     * @param mWalletBalanceResponse
     */
    private void parseWalletBalanceResponse(JSONArray mWalletBalanceResponse) {
        try {
            mWalletBalanceModel = new WalletBalanceModel();
            double updatedBalance = 0;
            double tempbalance = 0;
            String mFinalBalance = "0.0";
            JSONObject mJsonObject = new JSONObject();
            int mSizeOfArray = mWalletBalanceResponse.length();
            int count = 0;
            boolean isToUpdateBalance = false;
            if (mWalletBalanceResponse != null && mSizeOfArray >= 1) {
                for (int i = 0; i < mSizeOfArray; i++) {
                    mJsonObject = mWalletBalanceResponse.getJSONObject(i);
                    updatedBalance = mJsonObject.getDouble(SDKHelper.TAG_AMOUNT);
                    tempbalance = tempbalance + updatedBalance;
                    DecimalFormat formatter = new DecimalFormat("0.00000000");
                    mFinalBalance = formatter.format(tempbalance);
                    count = count + 1;
                    if (count == mSizeOfArray)
                        isToUpdateBalance = true;
                }
                if (isToUpdateBalance) {
                    mWalletBalanceModel.setmWalletId(0);
                    mWalletBalanceModel.setmWalletBalance(String.valueOf(mFinalBalance));
                    if (mWalletBalanceCallback != null)
                        mWalletBalanceCallback.successWalletBalanceCallback(mWalletBalanceModel);
                }
            } else {
                mWalletBalanceModel.setmWalletId(0);
                mWalletBalanceModel.setmWalletBalance("0.0");
                if (mWalletBalanceCallback != null)
                    mWalletBalanceCallback.successWalletBalanceCallback(mWalletBalanceModel);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /***
     * This method is used to get the address of the server to get the data from
     *
     * @param wallet_address
     */
    private String getServerUrl(String wallet_address) {
        String mInsightUrl = "";
        mInsightUrl = SDKHelper.EOT_EXPLORER + wallet_address + SDKHelper.INSIGHT_TO;
        return mInsightUrl;
    }
}
