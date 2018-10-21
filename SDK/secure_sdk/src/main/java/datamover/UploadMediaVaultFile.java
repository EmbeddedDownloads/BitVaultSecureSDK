package datamover;

import android.content.Context;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import commons.SDKErrors;
import commons.SDKHelper;
import controller.SDKControl;
import iclasses.mediavaultcallback.SendMediaVaultCallback;
import model.MediaVaultDataToPBCModel;
import utils.SDKUtils;
import webservicescontroller.servicehandlers.VolleyMultipartRequest;

/**
 * Created by vvdn on 9/15/2017.
 */

public class UploadMediaVaultFile {
    private Context mContext;
    private SendMediaVaultCallback mSendMessageCallback;
    private int mCounterSuccess = 0, mCounterCallApi = 0;

    // Constructor to initialise the objects
    public UploadMediaVaultFile(Context mContext, SendMediaVaultCallback sendMessageCallback) {
        this.mContext = mContext;
        this.mSendMessageCallback = sendMessageCallback;
    }

    /***
     * This method is used to call all 3 nodes to send message to PBC
     *
     * @param dataToPBCModel
     * @return
     */
    public void sendMediaFileToPbc(final MediaVaultDataToPBCModel dataToPBCModel) {
          mCounterSuccess = 0;
          mCounterCallApi = 0;
          sendFileToPbc1(dataToPBCModel, SDKHelper.UPLOAD_MEDIA_NODE1);
          sendFileToPbc2(dataToPBCModel, SDKHelper.UPLOAD_MEDIA_NODE2);
          sendFileToPbc3(dataToPBCModel, SDKHelper.UPLOAD_MEDIA_NODE3);

    }

    /***
     * This method is used to send message file to PBC server Node 1
     *
     * @param dataToPBCModel,url
     * @return
     */
    private void sendFileToPbc1(final MediaVaultDataToPBCModel dataToPBCModel, String url) {
        try {
            VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, url, new Response.Listener<NetworkResponse>() {
                @Override
                public void onResponse(NetworkResponse response) {
                    mCounterCallApi++;
                    String resultResponse = new String(response.data);
                    try {
                        JSONObject result = new JSONObject(resultResponse);
                        String status = result.getString(SDKHelper.TAG_STATUS);
                        String message = result.getString(SDKHelper.TAG_MESSAGE);
                        FileReadWrite.writeToFileall(result.toString(), SDKHelper.MEDIA_VAULT_FILE_PATH);
                        if (status.equalsIgnoreCase(SDKHelper.TAG_CAP_OK) || status.equalsIgnoreCase(SDKHelper.TAG_SUCCESS)) {
                            mCounterSuccess++;
                            checkCounter(status, dataToPBCModel);
                        } else {
                            checkCounter(status, dataToPBCModel);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        checkCounter(SDKHelper.TAG_CAP_FAILED, dataToPBCModel);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mCounterCallApi++;
                    checkCounter(SDKHelper.TAG_CAP_FAILED, dataToPBCModel);
                }
            }) {

                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    try {
                        params.put(SDKHelper.MEDIA_VAULT_FILE_ID, dataToPBCModel.getEncryptedUniqueFileId());
                        params.put(SDKHelper.CRC, dataToPBCModel.getCrc());
                        params.put(SDKHelper.DATA_TAG, dataToPBCModel.getTag());
                        params.put(SDKHelper.TAG_PBC_ID, dataToPBCModel.getPbcId());
                        params.put(SDKHelper.APPID, dataToPBCModel.getAppId());
                        params.put(SDKHelper.MEDIA_VAULT_WALLET_ADDRESS, dataToPBCModel.getWalletAddress());
                        params.put(SDKHelper.TAG_TIMESTAMP, String.valueOf(dataToPBCModel.getTimeStamp()));
                        params.put(SDKHelper.TAG_WEB_SERVER_KEY, dataToPBCModel.getWebServerKey());
                        SDKUtils.showErrorLog("params..", String.valueOf(params.toString()));
                        return (params != null || params.isEmpty()) ? params : super.getHeaders();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    return params;
                }

                @Override
                protected Map<String, DataPart> getByteData() {
                    Map<String, DataPart> params = new HashMap<>();
                    FileReadWrite.writeToFileall(dataToPBCModel.toString(), SDKHelper.MEDIA_VAULT_FILE_PATH);
                    // read file in bytes from the given path location to send
                    try {

                        SDKUtils.showLog("read start ", System.currentTimeMillis() + "");
                        FileReadWrite.readFromFile(dataToPBCModel.getFilepath());
                        SDKUtils.showLog("read end ", System.currentTimeMillis() + "");

                        params.put(SDKHelper.TAG_FILE, new DataPart(SDKHelper.MEDIA_VAULT_FILE_NAME, FileReadWrite.readFromFile(dataToPBCModel.getFilepath()),
                                "multipart/form-data"));
                        SDKUtils.showErrorLog("params11..", String.valueOf(params.toString()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return params;
                }
            };
            multipartRequest.shouldCache();
            SDKControl.getInstance().addToRequestQueue(multipartRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /***
     * This method is used to send message file to PBC server Node 2
     *
     * @param dataToPBCModel,url
     * @return
     */
    private void sendFileToPbc2(final MediaVaultDataToPBCModel dataToPBCModel, String url) {
        try {
            VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, url, new Response.Listener<NetworkResponse>() {
                @Override
                public void onResponse(NetworkResponse response) {
                    mCounterCallApi++;
                    String resultResponse = new String(response.data);
                    try {
                        JSONObject result = new JSONObject(resultResponse);
                        String status = result.getString(SDKHelper.TAG_STATUS);
                        String message = result.getString(SDKHelper.TAG_MESSAGE);
                        FileReadWrite.writeToFileall(result.toString(), SDKHelper.MEDIA_VAULT_FILE_PATH);
                        if (status.equalsIgnoreCase(SDKHelper.TAG_CAP_OK) || status.equalsIgnoreCase(SDKHelper.TAG_SUCCESS)) {
                            mCounterSuccess++;
                            checkCounter(status, dataToPBCModel);

                        } else {
                            checkCounter(status, dataToPBCModel);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        checkCounter(SDKHelper.TAG_CAP_FAILED, dataToPBCModel);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mCounterCallApi++;
                    checkCounter(SDKHelper.TAG_CAP_FAILED, dataToPBCModel);
                }
            }) {

                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    try {
                        params.put(SDKHelper.MEDIA_VAULT_FILE_ID, dataToPBCModel.getEncryptedUniqueFileId());
                        params.put(SDKHelper.CRC, dataToPBCModel.getCrc());
                        params.put(SDKHelper.DATA_TAG, dataToPBCModel.getTag());
                        params.put(SDKHelper.TAG_PBC_ID, dataToPBCModel.getPbcId());
                        params.put(SDKHelper.APPID, dataToPBCModel.getAppId());
                        params.put(SDKHelper.MEDIA_VAULT_WALLET_ADDRESS, dataToPBCModel.getWalletAddress());
                        params.put(SDKHelper.TAG_TIMESTAMP, String.valueOf(dataToPBCModel.getTimeStamp()));
                        params.put(SDKHelper.TAG_WEB_SERVER_KEY, dataToPBCModel.getWebServerKey());
                        SDKUtils.showErrorLog("params2..", String.valueOf(params.toString()));
                        return (params != null || params.isEmpty()) ? params : super.getHeaders();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return params;
                }

                @Override
                protected Map<String, DataPart> getByteData() {
                    Map<String, DataPart> params = new HashMap<>();
                    FileReadWrite.writeToFileall(dataToPBCModel.toString(), SDKHelper.MEDIA_VAULT_FILE_PATH);
                    // read file in bytes from the given path location to send
                    try {
                        params.put(SDKHelper.TAG_FILE, new DataPart(SDKHelper.MEDIA_VAULT_FILE_NAME, FileReadWrite.readFromFile(dataToPBCModel.getFilepath()), "multipart/form-data"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return params;
                }
            };
            multipartRequest.shouldCache();
            SDKControl.getInstance().addToRequestQueue(multipartRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /***
     * This method is used to send message file to PBC server Node 3
     *
     * @param dataToPBCModel,url
     * @return
     */
    private void sendFileToPbc3(final MediaVaultDataToPBCModel dataToPBCModel, String url) {
        try {
            VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, url, new Response.Listener<NetworkResponse>() {
                @Override
                public void onResponse(NetworkResponse response) {
                    mCounterCallApi++;
                    String resultResponse = new String(response.data);
                    try {
                        JSONObject result = new JSONObject(resultResponse);
                        String status = result.getString(SDKHelper.TAG_STATUS);
                        String message = result.getString(SDKHelper.TAG_MESSAGE);
                        FileReadWrite.writeToFileall(result.toString(), SDKHelper.MEDIA_VAULT_FILE_PATH);
                        if (status.equalsIgnoreCase(SDKHelper.TAG_CAP_OK) || status.equalsIgnoreCase(SDKHelper.TAG_SUCCESS)) {
                            mCounterSuccess++;
                            checkCounter(status, dataToPBCModel);

                        } else {
                            checkCounter(status, dataToPBCModel);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        checkCounter(SDKHelper.TAG_CAP_FAILED, dataToPBCModel);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mCounterCallApi++;
                    checkCounter(SDKHelper.TAG_CAP_FAILED, dataToPBCModel);
                }
            }) {

                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    try {
                        params.put(SDKHelper.MEDIA_VAULT_FILE_ID, dataToPBCModel.getEncryptedUniqueFileId());
                        params.put(SDKHelper.CRC, dataToPBCModel.getCrc());
                        params.put(SDKHelper.DATA_TAG, dataToPBCModel.getTag());
                        params.put(SDKHelper.TAG_PBC_ID, dataToPBCModel.getPbcId());
                        params.put(SDKHelper.APPID, dataToPBCModel.getAppId());
                        params.put(SDKHelper.MEDIA_VAULT_WALLET_ADDRESS, dataToPBCModel.getWalletAddress());
                        params.put(SDKHelper.TAG_TIMESTAMP, String.valueOf(dataToPBCModel.getTimeStamp()));
                        params.put(SDKHelper.TAG_WEB_SERVER_KEY, dataToPBCModel.getWebServerKey());
                        SDKUtils.showErrorLog("params3..", String.valueOf(params.toString()));
                        return (params != null || params.isEmpty()) ? params : super.getHeaders();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return params;
                }

                @Override
                protected Map<String, DataPart> getByteData() {
                    Map<String, DataPart> params = new HashMap<>();
                    FileReadWrite.writeToFileall(dataToPBCModel.toString(), SDKHelper.MEDIA_VAULT_FILE_PATH);
                    // read file in bytes from the given path location to send
                    try {
                        params.put(SDKHelper.TAG_FILE, new DataPart(SDKHelper.MEDIA_VAULT_FILE_NAME, FileReadWrite.readFromFile(dataToPBCModel.getFilepath()), "multipart/form-data"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return params;
                }
            };
            multipartRequest.shouldCache();
            SDKControl.getInstance().addToRequestQueue(multipartRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /***
     * This method is used to return callback after data sending completed by all nodes
     *
     * @param dataToPBCModel
     * @return
     */
    private void checkCounter(String status, MediaVaultDataToPBCModel dataToPBCModel) {
        if (mCounterSuccess == 2) {
            mSendMessageCallback.sendMediaVaultCallback(status, SDKHelper.SEND_MEDIA_FILE_SUCCESS, dataToPBCModel);
            mCounterSuccess = 0;
            mCounterCallApi = 0;
        } else {
            if (mCounterCallApi == 2 && mCounterSuccess == 0) {
                mSendMessageCallback.sendMediaVaultCallback(status, SDKErrors.MEDIA_VAULT_VOLLEY_ERROR, dataToPBCModel);
                mCounterSuccess = 0;
                mCounterCallApi = 0;
            } else if (mCounterCallApi == 3 && mCounterSuccess <= 1) {
                mSendMessageCallback.sendMediaVaultCallback(status, SDKErrors.MEDIA_VAULT_VOLLEY_ERROR, dataToPBCModel);
                mCounterSuccess = 0;
                mCounterCallApi = 0;
            }
        }
    }
}
