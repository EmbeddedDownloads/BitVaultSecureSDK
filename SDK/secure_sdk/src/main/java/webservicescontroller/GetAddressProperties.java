package webservicescontroller;/**
 * Created by Deepak on 4/11/2017.
 */


import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import commons.SDKHelper;
import controller.SDKControl;
import iclasses.BitAddressProperties;


/**********************************************************************
 * VVDN Technologies
 * All rights reserved.
 * This software is the confidential and proprietary information of
 * VVDN Technologies. ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with VVDN.
 ********************************************************************/
public class GetAddressProperties {
    /**
     * Debugging variable of this class
     */
    private String TAG = GetAddressProperties.class.getSimpleName();
    /**
     * Request json instance object
     */
    private JSONObject jsonObject;
    /**
     * This interface class object is used to manage the response of the uncounted wallet bitoins
     */
    private BitAddressProperties mBitAddressProperties = null;

    public GetAddressProperties(BitAddressProperties mBitAddressProperties) {
        this.mBitAddressProperties = mBitAddressProperties;
    }

    /**
     * Making json object request
     *
     * @param wallet_address
     */
    public void checkMyWallet(String wallet_address) {

        JsonArrayRequest jsonObjReq = new JsonArrayRequest(Request.Method.GET, wallet_address,
                null, new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray mAddressProperties) {
                try {
                    if (mBitAddressProperties != null)
                        mBitAddressProperties.AddressPropertiesSuccess(mAddressProperties);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }
            }

        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if (mBitAddressProperties != null)
                    mBitAddressProperties.AddressPropertiesFailure(volleyError);
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
        // Canceling request
        SDKControl.getInstance().getRequestQueue().cancelAll(new RequestQueue.RequestFilter() {
            @Override
            public boolean apply(Request<?> request) {
                return true;
            }
        });
        // Adding request to request queue
        SDKControl.getInstance().addToRequestQueue(jsonObjReq, SDKHelper.TAG_WALLET_ADDRESS_PROPERTIES);
    }
}
