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
import iclasses.SendMessageCallback;
import model.DataToPBCModel;
import utils.SDKUtils;
import webservicescontroller.servicehandlers.VolleyMultipartRequest;

/**
 * Created by Vinod Singh on 28/4/17.
 */

public class UploadData {
    private Context mContext;
    private SendMessageCallback mSendMessageCallback;
    private int mCounterSuccess = 0, mCounterCallApi = 0;

    // Constructor to initialise the objects
    public UploadData(Context mContext, SendMessageCallback sendMessageCallback) {
        this.mContext = mContext;
        this.mSendMessageCallback = sendMessageCallback;
    }

    /***
     * This method is used to call all 3 nodes to send message to PBC
     * @param dataToPBCModel
     * @return
     */
    public void sendMessageToPbc(final DataToPBCModel dataToPBCModel, String messageType) {
        mCounterSuccess = 0;
        mCounterCallApi = 0;
        sendMessageToPbc3(dataToPBCModel, SDKHelper.URL_NODE3,messageType);
        sendMessageToPbc1(dataToPBCModel, SDKHelper.URL_NODE1,messageType);
        sendMessageToPbc2(dataToPBCModel, SDKHelper.URL_NODE2,messageType);

    }

    /***
     * This method is used to send message file to PBC server Node 1
     *
     * @param dataToPBCModel,url,messageType
     * @return
     */
    private void sendMessageToPbc1(final DataToPBCModel dataToPBCModel, String url, final String messageType) {
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
                        FileReadWrite.writeToFileall(result.toString(), SDKHelper.FILE_PATH);
                        if (status.equalsIgnoreCase(SDKHelper.TAG_CAP_OK) || status.equalsIgnoreCase(SDKHelper.TAG_SUCCESS)) {
                            mCounterSuccess++;
                            checkCounter(status, dataToPBCModel,messageType);

                        } else {
                            checkCounter(status, dataToPBCModel,messageType);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        checkCounter(SDKHelper.TAG_CAP_FAILED, dataToPBCModel,messageType);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mCounterCallApi++;
                    checkCounter(SDKHelper.TAG_CAP_FAILED, dataToPBCModel,messageType);
                }
            }) {

                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    try {
                        params.put(SDKHelper.TRANSACTION_ID, dataToPBCModel.getHashTXId());
                        params.put(SDKHelper.CRC, dataToPBCModel.getCrc());
                        params.put(SDKHelper.DATA_TAG, dataToPBCModel.getTag());
                        params.put(SDKHelper.TAG_RECEIVER, dataToPBCModel.getReceiverWalletAdress());
                        params.put(SDKHelper.TAG_PBC_ID, dataToPBCModel.getPbcId());
                        params.put(SDKHelper.APPID, dataToPBCModel.getAppId());
                        params.put(SDKHelper.TAG_TIMESTAMP, String.valueOf(dataToPBCModel.getTimeStamp()));
                        params.put(SDKHelper.TAG_SESSION_KEY, dataToPBCModel.getEncryptedSessionKey());
                        params.put(SDKHelper.TAG_SENDER, dataToPBCModel.getSenderAddress());
                        params.put(SDKHelper.TAG_WEB_SERVER_KEY, dataToPBCModel.getWebServerKey());
                        return (params != null || params.isEmpty()) ? params : super.getHeaders();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return params;
                }

                @Override
                protected Map<String, DataPart> getByteData() {
                    Map<String, DataPart> params = new HashMap<>();
                    FileReadWrite.writeToFileall(dataToPBCModel.toString(), SDKHelper.FILE_PATH);
                    // read file in bytes from the given path location to send
                    try {

                        SDKUtils.showLog("read start node1 ", System.currentTimeMillis() + "");
                        params.put(SDKHelper.TAG_FILE, new DataPart(SDKHelper.FILE_NAME, FileReadWrite.readFromFile(dataToPBCModel.getFilepath()), "multipart/form-data"));
                        SDKUtils.showLog("read end node1", System.currentTimeMillis() + "");
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
     * @param dataToPBCModel,url,messageType
     * @return
     */
    private void sendMessageToPbc2(final DataToPBCModel dataToPBCModel, String url, final String messageType) {
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
                        FileReadWrite.writeToFileall(result.toString(), SDKHelper.FILE_PATH);
                        if (status.equalsIgnoreCase(SDKHelper.TAG_CAP_OK) || status.equalsIgnoreCase(SDKHelper.TAG_SUCCESS)) {
                            mCounterSuccess++;
                            checkCounter(status, dataToPBCModel,messageType);

                        } else {
                            checkCounter(status, dataToPBCModel,messageType);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        checkCounter(SDKHelper.TAG_CAP_FAILED, dataToPBCModel,messageType);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mCounterCallApi++;
                    checkCounter(SDKHelper.TAG_CAP_FAILED, dataToPBCModel,messageType);
                }
            }) {

                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    try {
                        params.put(SDKHelper.TRANSACTION_ID, dataToPBCModel.getHashTXId());
                        params.put(SDKHelper.CRC, dataToPBCModel.getCrc());
                        params.put(SDKHelper.DATA_TAG, dataToPBCModel.getTag());
                        params.put(SDKHelper.TAG_RECEIVER, dataToPBCModel.getReceiverWalletAdress());
                        params.put(SDKHelper.TAG_PBC_ID, dataToPBCModel.getPbcId());
                        params.put(SDKHelper.APPID, dataToPBCModel.getAppId());
                        params.put(SDKHelper.TAG_TIMESTAMP, String.valueOf(dataToPBCModel.getTimeStamp()));
                        params.put(SDKHelper.TAG_SESSION_KEY, dataToPBCModel.getEncryptedSessionKey());
                        params.put(SDKHelper.TAG_SENDER, dataToPBCModel.getSenderAddress());
                        params.put(SDKHelper.TAG_WEB_SERVER_KEY, dataToPBCModel.getWebServerKey());
                        return (params != null || params.isEmpty()) ? params : super.getHeaders();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return params;
                }

                @Override
                protected Map<String, DataPart> getByteData() {
                    Map<String, DataPart> params = new HashMap<>();
                    FileReadWrite.writeToFileall(dataToPBCModel.toString(), SDKHelper.FILE_PATH);
                    // read file in bytes from the given path location to send
                    try {
                        SDKUtils.showLog("read start node2 ", System.currentTimeMillis() + "");
                        params.put(SDKHelper.TAG_FILE, new DataPart(SDKHelper.FILE_NAME, FileReadWrite.readFromFile(dataToPBCModel.getFilepath()), "multipart/form-data"));
                        SDKUtils.showLog("read end node2", System.currentTimeMillis() + "");
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
     * @param dataToPBCModel,url,messageType
     * @return
     */
    private void sendMessageToPbc3(final DataToPBCModel dataToPBCModel, String url, final String messageType) {
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
                        FileReadWrite.writeToFileall(result.toString(), SDKHelper.FILE_PATH);
                        if (status.equalsIgnoreCase(SDKHelper.TAG_CAP_OK) || status.equalsIgnoreCase(SDKHelper.TAG_SUCCESS)) {
                            mCounterSuccess++;
                            checkCounter(status, dataToPBCModel,messageType);

                        } else {
                            checkCounter(status, dataToPBCModel,messageType);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        checkCounter(SDKHelper.TAG_CAP_FAILED, dataToPBCModel,messageType);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mCounterCallApi++;
                    checkCounter(SDKHelper.TAG_CAP_FAILED, dataToPBCModel,messageType);
                }
            }) {

                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    try {
                        params.put(SDKHelper.TRANSACTION_ID, dataToPBCModel.getHashTXId());
                        params.put(SDKHelper.CRC, dataToPBCModel.getCrc());
                        params.put(SDKHelper.DATA_TAG, dataToPBCModel.getTag());
                        params.put(SDKHelper.TAG_RECEIVER, dataToPBCModel.getReceiverWalletAdress());
                        params.put(SDKHelper.TAG_PBC_ID, dataToPBCModel.getPbcId());
                        params.put(SDKHelper.APPID, dataToPBCModel.getAppId());
                        params.put(SDKHelper.TAG_TIMESTAMP, String.valueOf(dataToPBCModel.getTimeStamp()));
                        params.put(SDKHelper.TAG_SESSION_KEY, dataToPBCModel.getEncryptedSessionKey());
                        params.put(SDKHelper.TAG_SENDER, dataToPBCModel.getSenderAddress());
                        params.put(SDKHelper.TAG_WEB_SERVER_KEY, dataToPBCModel.getWebServerKey());
                        return (params != null || params.isEmpty()) ? params : super.getHeaders();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return params;
                }

                @Override
                protected Map<String, DataPart> getByteData() {
                    Map<String, DataPart> params = new HashMap<>();
                    FileReadWrite.writeToFileall(dataToPBCModel.toString(), SDKHelper.FILE_PATH);
                    // read file in bytes from the given path location to send
                    try {
                        SDKUtils.showLog("read start node3 ", System.currentTimeMillis() + "");
                        params.put(SDKHelper.TAG_FILE, new DataPart(SDKHelper.FILE_NAME, FileReadWrite.readFromFile(dataToPBCModel.getFilepath()), "multipart/form-data"));
                        SDKUtils.showLog("read end node3", System.currentTimeMillis() + "");
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
    private void checkCounter(String status, DataToPBCModel dataToPBCModel, String messageType) {
        if (mCounterSuccess == 2) {
            mSendMessageCallback.sendMessageCallback(status, SDKHelper.SEND_MESSAGE_SUCCESS, dataToPBCModel.getTxId(),messageType,dataToPBCModel.getSessionKey());
            mCounterSuccess = 0;
            mCounterCallApi = 0;
        } else {
            if (mCounterCallApi == 2 && mCounterSuccess == 0) {
                mSendMessageCallback.sendMessageCallback(status, SDKErrors.SEND_MESSAGE_VOLLEY_ERROR, dataToPBCModel.getTxId(),messageType,"");
                mCounterSuccess = 0;
                mCounterCallApi = 0;
            } else if (mCounterCallApi == 3 && mCounterSuccess <= 1) {
                mSendMessageCallback.sendMessageCallback(status, SDKErrors.SEND_MESSAGE_VOLLEY_ERROR, dataToPBCModel.getTxId(),messageType,"");
                mCounterSuccess = 0;
                mCounterCallApi = 0;
            }
        }
    }
}

