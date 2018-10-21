package webservicescontroller;/**
 * Created by Deepak on 5/11/2017.
 */

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import commons.SDKHelper;
import controller.SDKControl;
import iclasses.CheckCurrency;
import utils.SDKUtils;
import webservicescontroller.servicehandlers.WebserviceAPIErrorHandler;

/**********************************************************************
 * VVDN Technologies
 * All rights reserved.
 * This software is the confidential and proprietary information of
 * VVDN Technologies. ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with VVDN.
 ********************************************************************/
public class CurrencyLocator {
    /**
     * This class variable is used to keep the debugging track of this class
     */
    private String TAG = CurrencyLocator.class.getSimpleName();
    /**
     * This interface class object is used to keep update about the broadcast transaction
     */
    private CheckCurrency mCheckCurrency = null;

    /***
     * This class constructor is used to initialize this class and transaction
     * handler listener
     *
     * @param mCheckCurrency
     */
    public CurrencyLocator(CheckCurrency mCheckCurrency) {
        this.mCheckCurrency = mCheckCurrency;
        checkCurrenty();
    }

    /***
     * This class method is used to post the transaction on the block chain
     */
    public void checkCurrenty() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, SDKHelper.USD_TO_BTC,
                responselistner, errorListener) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return super.getParams();
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = super.getHeaders();
                if (headers == null || headers.equals(Collections.emptyMap())) {
                    headers = new HashMap<String, String>();
                }
                return headers;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(SDKHelper.WEBCALL_TIMEOUT, SDKHelper.RETRY_COUNT,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // Adding request to request queue
        SDKControl.getInstance().addToRequestQueue(stringRequest, SDKHelper.TAG_CURRENCY_CONVERTOR);

    }

    /***
     * This response handler listener is used to handle the response from the API
     */
    Response.Listener<String> responselistner = new Response.Listener<String>() {
        @Override
        public void onResponse(String mResponse) {
            if (mCheckCurrency != null)
                mCheckCurrency.checkCurrency(mResponse);
            else
                SDKUtils.showErrorLog(TAG, "mCurrencyConvertorCallback null");
        }
    };
    /***
     * This callback method is used to handle the error response from the api
     */
    Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError volleyError) {
            WebserviceAPIErrorHandler.getWebAPIErrorHanlderInstance().VolleyErrorHandler(volleyError);
            if (mCheckCurrency != null) {
                mCheckCurrency.conversionError(volleyError);
            }
        }
    };
}
