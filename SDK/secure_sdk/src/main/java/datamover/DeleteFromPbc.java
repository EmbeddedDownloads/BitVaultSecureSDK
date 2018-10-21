package datamover;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import commons.SDKErrors;
import commons.SDKHelper;
import controller.SDKControl;
import iclasses.mediavaultcallback.DeleteMediaFromPbcCallBack;

/**
 * Created by Vinod Singh on 10/10/17.
 */

public class DeleteFromPbc {
    private Context mContext;
    private int mCounterSuccess = 0, mCounterCallApi = 0;


    // Constructor to initialise the objects
    public DeleteFromPbc(Context mContext) {
        this.mContext = mContext;
    }

    /***
     * This method is used to call all 3 nodes to delete message from PBC
     * @param jsonObject,deleteMediaFromPbcCallBack
     * @return
     */
    public void deleteMediaFromPbc(JSONObject jsonObject,String fileId, DeleteMediaFromPbcCallBack deleteMediaFromPbcCallBack) {
        mCounterSuccess = 0;
        mCounterCallApi = 0;
        deleteFileFromPbc1(SDKHelper.DELETE_MEDIA_NODE1,jsonObject,fileId,deleteMediaFromPbcCallBack);
        deleteFileFromPbc2(SDKHelper.DELETE_MEDIA_NODE2,jsonObject,fileId,deleteMediaFromPbcCallBack);
        deleteFileFromPbc3(SDKHelper.DELETE_MEDIA_NODE3,jsonObject,fileId,deleteMediaFromPbcCallBack);
    }

    /***
     * This method is used to delete file from PBC node 1
     *
     * @param baseUrl,jsonObject,methodType
     * @return
     */

    private void deleteFileFromPbc1(String baseUrl, JSONObject jsonObject, final String fileId, final DeleteMediaFromPbcCallBack deleteMediaFromPbcCallBack){
        try {
            final JsonObjectRequest stringPostRequest = new JsonObjectRequest(Request.Method.POST, baseUrl, jsonObject,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            mCounterCallApi++;
                            try {
                                if(response != null && !response.toString().equals("")){
                                    mCounterSuccess++;
                                    String status = response.getString(SDKHelper.TAG_STATUS);
                                    String message = response.getString(SDKHelper.TAG_MESSAGE);
                                    checkCounter(status,message,fileId,deleteMediaFromPbcCallBack);
                                }
                                else{
                                    checkCounter(SDKHelper.TAG_CAP_FAILED, SDKErrors.MEDIA_VAULT_SERVER_ERROR,fileId,deleteMediaFromPbcCallBack);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                checkCounter(SDKHelper.TAG_CAP_FAILED, SDKErrors.MEDIA_VAULT_SERVER_ERROR,fileId,deleteMediaFromPbcCallBack);
                            }
                        }

                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            mCounterCallApi++;
                            if(error != null && !error.toString().equals("")){
                                checkCounter(SDKHelper.TAG_CAP_FAILED,  error.toString(),fileId,deleteMediaFromPbcCallBack);
                            }else{
                                checkCounter(SDKHelper.TAG_CAP_FAILED, SDKErrors.MEDIA_VAULT_SERVER_ERROR,fileId,deleteMediaFromPbcCallBack);
                            }
                        }
                    });

            // Adding JsonObject request to request queue
            SDKControl.getInstance().addToRequestQueue(stringPostRequest, SDKHelper.TAG_DOWNLOAD_DATA);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /***
     * This method is used to delete file from PBC node 2
     *
     * @param baseUrl,jsonObject,methodType
     * @return
     */

    private void deleteFileFromPbc2(String baseUrl, JSONObject jsonObject,final String fileId,  final DeleteMediaFromPbcCallBack deleteMediaFromPbcCallBack){
        try {
            final JsonObjectRequest stringPostRequest = new JsonObjectRequest(Request.Method.POST, baseUrl, jsonObject,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            mCounterCallApi++;
                            try {
                                if(response != null && !response.toString().equals("")){
                                    mCounterSuccess++;
                                    String status = response.getString(SDKHelper.TAG_STATUS);
                                    String message = response.getString(SDKHelper.TAG_MESSAGE);
                                    checkCounter(status,message,fileId,deleteMediaFromPbcCallBack);
                                }
                                else{
                                    checkCounter(SDKHelper.TAG_CAP_FAILED, SDKErrors.MEDIA_VAULT_SERVER_ERROR,fileId,deleteMediaFromPbcCallBack);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                checkCounter(SDKHelper.TAG_CAP_FAILED, SDKErrors.MEDIA_VAULT_SERVER_ERROR,fileId,deleteMediaFromPbcCallBack);
                            }
                        }

                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            mCounterCallApi++;
                            if(error != null && !error.toString().equals("")){
                                checkCounter(SDKHelper.TAG_CAP_FAILED,  error.toString(),fileId,deleteMediaFromPbcCallBack);
                            }else{
                                checkCounter(SDKHelper.TAG_CAP_FAILED, SDKErrors.MEDIA_VAULT_SERVER_ERROR,fileId,deleteMediaFromPbcCallBack);
                            }
                        }
                    });

            // Adding JsonObject request to request queue
            SDKControl.getInstance().addToRequestQueue(stringPostRequest, SDKHelper.TAG_DOWNLOAD_DATA);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /***
     * This method is used to delete file from PBC node 3
     *
     * @param baseUrl,jsonObject,methodType
     * @return
     */

    private void deleteFileFromPbc3(String baseUrl, JSONObject jsonObject, final String fileId, final DeleteMediaFromPbcCallBack deleteMediaFromPbcCallBack){
        try {
            final JsonObjectRequest stringPostRequest = new JsonObjectRequest(Request.Method.POST, baseUrl, jsonObject,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            mCounterCallApi++;
                            try {
                                if(response != null && !response.toString().equals("")){
                                    mCounterSuccess++;
                                    String status = response.getString(SDKHelper.TAG_STATUS);
                                    String message = response.getString(SDKHelper.TAG_MESSAGE);
                                    checkCounter(status,message,fileId,deleteMediaFromPbcCallBack);
                                }
                                else{
                                    checkCounter(SDKHelper.TAG_CAP_FAILED, SDKErrors.MEDIA_VAULT_SERVER_ERROR,fileId,deleteMediaFromPbcCallBack);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                checkCounter(SDKHelper.TAG_CAP_FAILED, SDKErrors.MEDIA_VAULT_SERVER_ERROR,fileId,deleteMediaFromPbcCallBack);
                            }
                        }

                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            mCounterCallApi++;
                            if(error != null && !error.toString().equals("")){
                                checkCounter(SDKHelper.TAG_CAP_FAILED,  error.toString(),fileId,deleteMediaFromPbcCallBack);
                            }else{
                                checkCounter(SDKHelper.TAG_CAP_FAILED, SDKErrors.MEDIA_VAULT_SERVER_ERROR,fileId,deleteMediaFromPbcCallBack);
                            }
                        }
                    });

            // Adding JsonObject request to request queue
            SDKControl.getInstance().addToRequestQueue(stringPostRequest, SDKHelper.TAG_DOWNLOAD_DATA);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /***
     * This method is used to return callback after data deleting completed by all nodes
     *
     * @param status,message,deleteMediaFromPbcCallBack
     * @return
     */
    private void checkCounter(String status, String message,final String fileId, DeleteMediaFromPbcCallBack deleteMediaFromPbcCallBack) {
        if (mCounterSuccess == 2) {
            deleteMediaFromPbcCallBack.deletedFileFromPbc(status, message,fileId);
            mCounterSuccess = 0;
            mCounterCallApi = 0;
        } else {
            if (mCounterCallApi == 2 && mCounterSuccess == 0) {
                deleteMediaFromPbcCallBack.deletedFileFromPbc(status, message,fileId);
                mCounterSuccess = 0;
                mCounterCallApi = 0;
            } else if (mCounterCallApi == 3 && mCounterSuccess <= 1) {
                deleteMediaFromPbcCallBack.deletedFileFromPbc(SDKHelper.TAG_CAP_FAILED, SDKErrors.MEDIA_VAULT_SERVER_ERROR,fileId);
                mCounterSuccess = 0;
                mCounterCallApi = 0;
            }
        }
    }
}
