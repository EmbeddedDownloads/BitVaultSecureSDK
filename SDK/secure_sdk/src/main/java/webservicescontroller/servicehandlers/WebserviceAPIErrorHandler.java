package webservicescontroller.servicehandlers;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.embedded.wallet.R;

import bitmanagers.BitVaultBaseManager;
import commons.SecureSDKException;
import utils.SDKUtils;

/**
 * Webservice API Error Handler
 *
 * @author Anshuman
 */
public class WebserviceAPIErrorHandler {

    /**
     * Debugging TAG
     */
    private String TAG = WebserviceAPIErrorHandler.class.getSimpleName();
    private static WebserviceAPIErrorHandler mWebserviceAPIErrorHandlerInstance = null;

    /***
     * This method is used to return the instance of the api handler class
     *
     * @return
     */
    public static WebserviceAPIErrorHandler getWebAPIErrorHanlderInstance() {
        if (mWebserviceAPIErrorHandlerInstance == null)
            mWebserviceAPIErrorHandlerInstance = new WebserviceAPIErrorHandler();
        return mWebserviceAPIErrorHandlerInstance;
    }

    /**
     * Volley Error Handler
     *
     * @param mError
     */
    public void VolleyErrorHandler(VolleyError mError) {
        Context mContext = null;
        try {
            mContext = BitVaultBaseManager.getInstance().getContext();
        } catch (SecureSDKException e) {
            e.printStackTrace();
        }
        if (mContext != null) {
            SDKUtils.showErrorLog(TAG, "VolleyError :" + mError);
            if (mError instanceof NoConnectionError) {
                SDKUtils.showToast(mContext, mContext.getResources()
                        .getString(R.string.network_error));
            } else if (mError instanceof TimeoutError) {
                SDKUtils.showToast(mContext, mContext.getResources()
                        .getString(R.string.network_slow_error));
            } else if (mError instanceof AuthFailureError) {
            } else if (mError instanceof ServerError) {
                SDKUtils.showToast(mContext, mContext.getResources()
                        .getString(R.string.server_error));
            } else if (mError instanceof NetworkError) {
                SDKUtils.showToast(mContext, mContext.getResources()
                        .getString(R.string.network_error));
            } else if (mError instanceof ParseError) {
            } else if (mError instanceof NoConnectionError) {
                SDKUtils.showToast(mContext, mContext.getResources()
                        .getString(R.string.network_error));
            }
        }
    }

    /**
     * Volley Error Handler for only driver list as on error, user should be
     * navigated back to the map screen
     *
     * @param mError
     */
    public String VolleyErrorHandlerReturningString(VolleyError mError
    ) {
        Context mContext = null;
        try {
            mContext = BitVaultBaseManager.getInstance().getContext();
        } catch (SecureSDKException e) {
            e.printStackTrace();
        }
        String error_message = "Error Occured";
        if (mContext != null) {
            if (mError instanceof NoConnectionError) {
                error_message = mContext.getResources().getString(
                        R.string.network_error);
            } else if (mError instanceof TimeoutError) {
                error_message = mContext.getResources().getString(
                        R.string.network_slow_error);
            } else if (mError instanceof AuthFailureError) {
            } else if (mError instanceof ServerError) {
            } else if (mError instanceof NetworkError) {
                error_message = mContext.getResources().getString(
                        R.string.network_error);
            } else if (mError instanceof ParseError) {
            }
        }
        return error_message;
    }
}
