package webservicescontroller.eotservices;/**
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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import commons.GlobalKeys;
import commons.SDKHelper;
import controller.SDKControl;
import iclasses.iclasseseot.EotPriceConverterListener;
import model.eotmodel.EotPriceConverterModel;

/**********************************************************************
 * Embedded Downloads
 * All rights reserved.
 * This software is the confidential and proprietary information of
 * Embedded Downloads. ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Embedded Downloads.
 ********************************************************************/
public class EotCurrencyUpdateHandler {
    /**
     * Debugging variable of this class
     */
    private String TAG = EotCurrencyUpdateHandler.class.getSimpleName();
    /**
     * This interface class object is used to manage the response of the uncounted wallet bitoins
     */
    private EotPriceConverterListener mEotPriceConverterListener = null;
    private EotPriceConverterModel mEotPriceConverterModel = null;

    public EotCurrencyUpdateHandler(EotPriceConverterListener mEotPriceConverterListener) {
        this.mEotPriceConverterListener = mEotPriceConverterListener;
        mEotPriceConverterModel = new EotPriceConverterModel();
    }

    /**
     * Making json object request
     */
    public void checkCurrentPrice() {
        JsonArrayRequest jsonObjReq = new JsonArrayRequest(Request.Method.GET, SDKHelper.EOT_TO_USD,
                null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray mCurrentPricce) {
                try {
                    parseCurrentPrice(mCurrentPricce);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }
            }

        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if (mEotPriceConverterListener != null)
                    mEotPriceConverterListener.EotPriceConverterFailed(volleyError);
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
     * @param mCurrentPriceArray
     */
    private void parseCurrentPrice(JSONArray mCurrentPriceArray) {
        try {
            if (mCurrentPriceArray != null) {
                int size = mCurrentPriceArray.length();
                JSONObject mJsonObject = new JSONObject();
                for (int i = 0; i < size; i++) {
                    mJsonObject = mCurrentPriceArray.getJSONObject(i);
                    if (mJsonObject != null && mJsonObject.has(GlobalKeys.PRICE_USD)) {
                        String mEOT_USD_Price = mJsonObject.getString(GlobalKeys.PRICE_USD);
                        String mEot_BTC_Price = mJsonObject.getString(GlobalKeys.PRICE_BTC);
                        String mEot_Price_Last_Update = mJsonObject.getString(GlobalKeys.PRICE_LAST_UPDATE);
                        mEotPriceConverterModel.setUsdPrice(mEOT_USD_Price);
                        mEotPriceConverterModel.setBtcPrice(mEot_BTC_Price);
                        mEotPriceConverterModel.setLastUpdate(mEot_Price_Last_Update);
                        if (mEotPriceConverterListener != null) {
                            mEotPriceConverterListener.EotPriceConversionSuccess(mEotPriceConverterModel);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
