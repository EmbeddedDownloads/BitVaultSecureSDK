package webservicescontroller;//package webservicescontroller;/**
// * Created by ${e} on 5/26/2017.
// */
//
//import com.android.volley.AuthFailureError;
//import com.android.volley.DefaultRetryPolicy;
//import com.android.volley.NetworkResponse;
//import com.android.volley.Request;
//import com.android.volley.Response;
//import com.android.volley.VolleyError;
//import com.android.volley.toolbox.JsonObjectRequest;
//
//import org.json.JSONArray;
//import org.json.JSONObject;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.HashMap;
//import java.util.Map;
//
//import commons.GlobalKeys;
//import commons.SDKHelper;
//import controller.SDKControl;
//import iclasses.WalletBalanceCallback;
//import model.WalletBalanceModel;
//import utils.SDKUtils;
//
///**********************************************************************
// * Embedded Downloads
// * All rights reserved.
// * This software is the confidential and proprietary information of
// * Embedded Downloads. ("Confidential Information"). You shall not
// * disclose such Confidential Information and shall use it only in
// * accordance with the terms of the license agreement you entered into
// * with Embedded Downloads.
// ********************************************************************/
//public class FetchWalletBalanceHandler {
//    /**
//     * Debugging variable of this class
//     */
//    private String TAG = FetchWalletBalanceHandler.class.getSimpleName();
//    /**
//     * Request json instance object
//     */
//    private JSONObject jsonObject;
//    /**
//     * This interface class object is used to manage the response of the uncounted wallet bitoins
//     */
//    private WalletBalanceCallback mWalletBalanceCallback = null;
//    private WalletBalanceModel mWalletBalanceModel = null;
//
//    public FetchWalletBalanceHandler(WalletBalanceCallback mWalletBalanceCallback) {
//        this.mWalletBalanceCallback = mWalletBalanceCallback;
//        mWalletBalanceModel = new WalletBalanceModel();
//    }
//
//    /**
//     * Making json object request
//     *
//     * @param wallet_address
//     * @param wallet_id
//     */
//    public void checkWalletForUpdateBalance(String wallet_address, final int wallet_id) {
//        String mWalletAPIUrl = getServerUrl(wallet_address);
//        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, mWalletAPIUrl,
//                null, new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject mWalletBalanceResponse) {
//                try {
//                    parseWalletBalanceResponse(mWalletBalanceResponse, wallet_id);
//                } catch (IllegalArgumentException e) {
//                    e.printStackTrace();
//                }
//            }
//
//        }, new Response.ErrorListener() {
//
//            @Override
//            public void onErrorResponse(VolleyError volleyError) {
//                if (mWalletBalanceCallback != null)
//                    mWalletBalanceCallback.failedWalletBalanceCallback(volleyError);
//            }
//        }) {
//
//            /**
//             * Passing some request headers
//             * */
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                Map<String, String> headers = super.getHeaders();
//                if (headers == null || headers.equals(Collections.emptyMap())) {
//                    headers = new HashMap<String, String>();
//                }
//                return headers;
//            }
//
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                return super.getParams();
//            }
//
//            @Override
//            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
//                return super.parseNetworkResponse(response);
//            }
//        };
//        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
//                SDKHelper.WEBCALL_TIMEOUT, SDKHelper.RETRY_COUNT,
//                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//        SDKControl.getInstance().addToRequestQueue(jsonObjReq, SDKHelper.TAG_FETCH_WALLET_BALANCE);
//    }
//
//    /***
//     * This method is used to parse the network response
//     *
//     * @param mWalletBalanceResponse
//     * @param wallet_id
//     */
//    private void parseWalletBalanceResponse(JSONObject mWalletBalanceResponse, int wallet_id) {
//        try {
//            if (mWalletBalanceResponse != null) {
//                String mWalletBalance = mWalletBalanceResponse.getString(GlobalKeys.KEY_BALANCE);
//                String mWalletTotalReceived = mWalletBalanceResponse.getString(GlobalKeys.KEY_TOTAL_RECEIVED);
//                String mWalletTotalSent = mWalletBalanceResponse.getString(GlobalKeys.KEY_TOTAL_SENT);
//                String mWalletUnconfirmed = mWalletBalanceResponse.getString(GlobalKeys.KEY_UNCONFIRMED_BALANCE);
//                String mWalletAddress = mWalletBalanceResponse.getString(GlobalKeys.KEY_ADDRESS);
//                mWalletBalanceModel.setmWalletAddress(mWalletAddress);
//                mWalletBalanceModel.setmWalletBalance(mWalletBalance);
//                mWalletBalanceModel.setmWalletTotalReceivedBitCoins(mWalletTotalReceived);
//                mWalletBalanceModel.setmWalletTotalSpentBitCoins(mWalletTotalSent);
//                mWalletBalanceModel.setmWalletUnconfirmedBalance(mWalletUnconfirmed);
//                mWalletBalanceModel.setmWalletId(wallet_id);
//                JSONArray mJsonArrayTransactions = mWalletBalanceResponse.getJSONArray(GlobalKeys.KEY_TRANSACTIONS);
//                ArrayList<String> mTransactionsList = new ArrayList<>();
//                for (int i = 0; i < mJsonArrayTransactions.length(); i++) {
//                    String mTransaction = mJsonArrayTransactions.getString(i);
//                    mTransactionsList.add(mTransaction);
//                }
//                if (mTransactionsList != null)
//                    mWalletBalanceModel.setmTransactionsList(mTransactionsList);
//                if (mWalletBalanceCallback != null)
//                    mWalletBalanceCallback.successWalletBalanceCallback(mWalletBalanceModel);
//            } else {
//                SDKUtils.showErrorLog(TAG, "---------------Json Array null----------");
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    /***
//     * This method is used to get the address of the server to get the data from
//     *
//     * @param wallet_address
//     */
//    private String getServerUrl(String wallet_address) {
//        String mInsightUrl = SDKHelper.URL_GET_BALANCE + wallet_address;
//        return mInsightUrl;
//    }
//}
