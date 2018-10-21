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
import iclasses.mediavaultcallback.DeleteMediaFromAppStore;
import iclasses.mediavaultcallback.MediaFileDetailsOperationCallBack;
import iclasses.mediavaultcallback.MediaVaultGetFileInfo;

/**
 * Created by Vinod Singh on 5/10/17.
 */

public class AppStoreDataTransfer {
    private Context mContext;

    // Constructor to initialise the objects
    public AppStoreDataTransfer(Context context) {
        this.mContext = context;
    }

    /***
     * This method is used to perform save & update file details operation on AppStore Cloud
     *
     * @param jsonObject,id
     * @return
     */

    public void operationsFileDetailsToAppStore(String baseUrl,JSONObject jsonObject, final String id,
                                               int methodType, final MediaFileDetailsOperationCallBack mediaFileDetailsOperationCallBack){
        try {
            final JsonObjectRequest stringPostRequest = new JsonObjectRequest(methodType, baseUrl, jsonObject,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                            if(response != null && !response.toString().equals("")){

                                String status = response.getString(SDKHelper.TAG_STATUS);
                                String message = response.getJSONObject(SDKHelper.TAG_RESULT).getString(SDKHelper.TAG_MESSAGE);
                                mediaFileDetailsOperationCallBack.mediaFileDetailsOperationCallBack(status,message,id);
                            }
                            else{
                                mediaFileDetailsOperationCallBack.mediaFileDetailsOperationCallBack(SDKHelper.TAG_CAP_FAILED, SDKErrors.MEDIA_VAULT_SERVER_ERROR,id);
                            }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                mediaFileDetailsOperationCallBack.mediaFileDetailsOperationCallBack(SDKHelper.TAG_CAP_FAILED, SDKErrors.MEDIA_VAULT_SERVER_ERROR,id);
                            }
                        }

                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            if(error != null && !error.toString().equals("")){
                                mediaFileDetailsOperationCallBack.mediaFileDetailsOperationCallBack(SDKHelper.TAG_CAP_FAILED, error.toString(),id);
                            }else{
                                mediaFileDetailsOperationCallBack.mediaFileDetailsOperationCallBack(SDKHelper.TAG_CAP_FAILED, SDKErrors.MEDIA_VAULT_SERVER_ERROR,id);
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
     * This method is used to perform get media file details from AppStore Cloud
     *
     * @param inputParams,mediaFileDetailsOperationCallBack
     * @return
     */

    public void getFileDetailsFromAppStore(String inputParams,final MediaVaultGetFileInfo mediaVaultGetFileInfo){
        try {
            String input = SDKHelper.BASE_URL_APPSTORE + inputParams;
            final JsonObjectRequest stringPostRequest = new JsonObjectRequest(Request.Method.GET, input,null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                if(response != null && !response.toString().equals("")){
                                    String status = response.getString(SDKHelper.TAG_STATUS);
                                    mediaVaultGetFileInfo.getMediaVaultFileInfo(status, SDKHelper.MEDIAVAULT_GETFILE_SUCCESS,response.toString());
                                }
                                else{
                                    mediaVaultGetFileInfo.getMediaVaultFileInfo(SDKHelper.TAG_CAP_FAILED, SDKErrors.MEDIA_VAULT_SERVER_ERROR,"");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                mediaVaultGetFileInfo.getMediaVaultFileInfo(SDKHelper.TAG_CAP_FAILED, SDKErrors.MEDIA_VAULT_SERVER_ERROR,"");
                            }
                        }

                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            if(error != null && !error.toString().equals("")){
                                mediaVaultGetFileInfo.getMediaVaultFileInfo(SDKHelper.TAG_CAP_FAILED, error.toString(),"");
                            }else{
                                mediaVaultGetFileInfo.getMediaVaultFileInfo(SDKHelper.TAG_CAP_FAILED, SDKErrors.MEDIA_VAULT_SERVER_ERROR,"");
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
     * This method is used to perform delete operation from AppStore Cloud
     *
     * @param jsonObject,id
     * @return
     */

    public void deleteFileDetailsFromAppStore(String baseUrl,JSONObject jsonObject, final String id,
                                                int methodType, final DeleteMediaFromAppStore deleteMediaFromAppStore){
        try {
            final JsonObjectRequest stringPostRequest = new JsonObjectRequest(methodType, baseUrl, jsonObject,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                if(response != null && !response.toString().equals("")){
                                    String status = response.getString(SDKHelper.TAG_STATUS);
                                    String message = response.getJSONObject(SDKHelper.TAG_RESULT).getString(SDKHelper.TAG_MESSAGE);
                                    deleteMediaFromAppStore.deleteMediaFromAppstoreCallBack(status,message,id);
                                }
                                else{
                                    deleteMediaFromAppStore.deleteMediaFromAppstoreCallBack(SDKHelper.TAG_CAP_FAILED, SDKErrors.MEDIA_VAULT_SERVER_ERROR,id);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                deleteMediaFromAppStore.deleteMediaFromAppstoreCallBack(SDKHelper.TAG_CAP_FAILED, SDKErrors.MEDIA_VAULT_SERVER_ERROR,id);
                            }
                        }

                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            if(error != null && !error.toString().equals("")){
                                deleteMediaFromAppStore.deleteMediaFromAppstoreCallBack(SDKHelper.TAG_CAP_FAILED, error.toString(),id);
                            }else{
                                deleteMediaFromAppStore.deleteMediaFromAppstoreCallBack(SDKHelper.TAG_CAP_FAILED, SDKErrors.MEDIA_VAULT_SERVER_ERROR,id);
                            }
                        }
                    });

            // Adding JsonObject request to request queue
            SDKControl.getInstance().addToRequestQueue(stringPostRequest, SDKHelper.TAG_DOWNLOAD_DATA);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
