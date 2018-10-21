package webservicescontroller;/**
 * Created by Deepak on 4/12/2017.
 */


import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import commons.SDKConstants;
import commons.SDKHelper;
import controller.SDKControl;
import iclasses.BroadcastBitCoinTransactionHandler;
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
public class BitCoinTransactionBroadcast {
    /**
     * This class variable is used to keep the debugging track of this class
     */
    private String TAG = BitCoinTransactionBroadcast.class.getSimpleName();
    /**
     * This interface class object is used to keep update about the broadcast transaction
     */
    private BroadcastBitCoinTransactionHandler mBroadcastBitCoinTransactionHandler = null;
    private JSONObject mJsonRequest = null;
    private int mWallet_id=-1;

    /***
     * This class constructor is used to initialize this class and transaction
     * handler listener
     *
     * @param mBroadcastBitCoinTransactionHandler
     */
    public BitCoinTransactionBroadcast(BroadcastBitCoinTransactionHandler mBroadcastBitCoinTransactionHandler) {
        this.mBroadcastBitCoinTransactionHandler = mBroadcastBitCoinTransactionHandler;
        mJsonRequest = new JSONObject();
    }

    /***
     * This class method is used to post the transaction on the block chain
     *
     * @param mTransaction
     * @param mWallet_id
     */
    public void broadCastTransaction(final String mTransaction, int mWallet_id) {
        this.mWallet_id = mWallet_id;
        try {
            mJsonRequest.put(SDKHelper.RAW_TX, mTransaction);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String mTransactionHistoryUrl = "";
        if (SDKConstants.WALLET_TYPE== SDKHelper.ZERO){
            mTransactionHistoryUrl = SDKHelper.TESTNET_URL_PUSH_TRANSACTION_URL;
        }else{
            mTransactionHistoryUrl = SDKHelper.MAIN_PUSH_TRANSACTION_URL;
        }
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, mTransactionHistoryUrl,
                mJsonRequest, responselistner, errorListener) {
            /**
             * Passing some request headers
             * */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map headers = new HashMap();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return super.getParams();
            }

        };
        SDKControl.getInstance().cancelPendingRequests(new RequestQueue.RequestFilter() {
            @Override
            public boolean apply(Request<?> request) {
                return true;
            }
        });
        SDKControl.getInstance().cancelPendingRequests(SDKHelper.TAG_SEND_TRANS);
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(SDKHelper.WEBCALL_TIMEOUT, SDKHelper.RETRY_COUNT,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        jsonObjReq.setShouldCache(false);
        // Adding request to request queue
        SDKControl.getInstance().addToRequestQueue(jsonObjReq, SDKHelper.TAG_SEND_TRANS);

    }

    Response.Listener<JSONObject> responselistner = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject mTransactionBroadcastId) {
            if (mBroadcastBitCoinTransactionHandler != null) {
                String mTx_id = "";
                try {
                    mTx_id = mTransactionBroadcastId.getString(SDKHelper.KEY_TX_ID);
                    SDKUtils.showLog(TAG,"Transaction Id : "+mTx_id);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mBroadcastBitCoinTransactionHandler.TransactionBroadcastSuccess(mTx_id,mWallet_id);
            }
        }
    };
    Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError volleyError) {
            if (mBroadcastBitCoinTransactionHandler != null) {
                mBroadcastBitCoinTransactionHandler.TransactionBroadcastFailure(volleyError);
            }
        }
    };

}
