package datamover;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import commons.SDKConstants;
import commons.SDKErrors;
import commons.SDKHelper;
import controller.SDKControl;
import iclasses.mediavaultcallback.MediaVaultDecryptDataCallback;
import iclasses.mediavaultcallback.MediaVaultReceiveCallBack;
import model.MediaVaultBlockModel;
import securityencryption.DataHelper;
import securityencryption.EncryptDecryptData;
import webservicescontroller.servicehandlers.InputStreamVolleyRequest;

/**
 * Created by vvdn on 9/15/2017.
 */

public class DownloadMediaVaultFile implements MediaVaultDecryptDataCallback {
    private Context mContext;
    private MediaVaultReceiveCallBack mReceiveMessageCallback;
    private MediaVaultBlockModel mediaVaultBlockModel, mediaVaultBlockModel2, mediaVaultBlockModel3;
    private int  mCounterFileDownload = 0, mCrcMatched = 0, mCounterMediaDown = 0, mCounterMediaDownSucc = 0;
    private String mEncryptedTxId = "", mEncryptedFileKey = "";
    private InputStream resultStream = null;

    // Constructor to initialise the objects
    public DownloadMediaVaultFile(Context mContext, MediaVaultReceiveCallBack receiveMediaVaultCallback) {
        this.mContext = mContext;
        this.mReceiveMessageCallback = receiveMediaVaultCallback;
    }

    /***
     * This method is used to get downloading file blocks from PBC
     *
     * @param jsonObject,receiveMessageCallback
     * @param encryptedTxId
     *@param encryptedFileKey @return
     */

    public void downloadMessage(JSONObject jsonObject, String encryptedTxId, String encryptedFileKey) {
        downLoadBlocksFromPbc1(jsonObject, SDKHelper.RECEIVE_MEDIA_NODE1);
        downLoadBlocksFromPbc2(jsonObject, SDKHelper.RECEIVE_MEDIA_NODE2);
        downLoadBlocksFromPbc3(jsonObject, SDKHelper.RECEIVE_MEDIA_NODE3);
        this.mEncryptedTxId = encryptedTxId;
        this.mEncryptedFileKey = encryptedFileKey;
    }

    /***
     * This method is used to start downloading new media file from PBC node 1
     *
     * @param jsonInputObject,url
     * @return
     */

    private void downLoadBlocksFromPbc1(final JSONObject jsonInputObject, final String URL) {
        try {
            FileReadWrite.writeToFileall(jsonInputObject.toString(), SDKHelper.MEDIA_VAULT_FILE_PATH);
            JsonObjectRequest stringPostRequest = new JsonObjectRequest(Request.Method.POST, URL, jsonInputObject,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            mCounterMediaDown++;
                            // response
                            FileReadWrite.writeToFileall(response.toString(), SDKHelper.MEDIA_VAULT_FILE_PATH);
                            try {
                                if (response != null && !response.toString().equalsIgnoreCase("") && response.getString(SDKHelper.TAG_RESULT_SET) != null && !response.getString(SDKHelper.TAG_RESULT_SET).equalsIgnoreCase("null")) {

                                    Gson gson = new Gson();
                                    mediaVaultBlockModel = new MediaVaultBlockModel();
                                    mediaVaultBlockModel = gson.fromJson(response.toString(), MediaVaultBlockModel.class);
                                        if (mediaVaultBlockModel != null) {
                                            mCounterMediaDownSucc++;
                                        }
                                }

                            } catch (JsonSyntaxException e) {
                                e.printStackTrace();
                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            }

                            checkDownloadBlocks();
                        }

                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // error in downloading
                            mCounterMediaDown++;
                            checkDownloadBlocks();
                        }
                    });

            // Adding JsonObject request to request queue
            SDKControl.getInstance().addToRequestQueue(stringPostRequest, SDKHelper.TAG_DOWNLOAD_DATA);
        } catch (Exception e) {
            e.printStackTrace();
            checkDownloadBlocks();
        }
    }

    /***
     * This method is used to start downloading new media file from PBC node 2
     *
     * @param jsonInputObject,url
     * @return
     */
    private void downLoadBlocksFromPbc2(final JSONObject jsonInputObject, final String URL) {
        try {
            FileReadWrite.writeToFileall(jsonInputObject.toString(), SDKHelper.MEDIA_VAULT_FILE_PATH);
            JsonObjectRequest stringPostRequest = new JsonObjectRequest(Request.Method.POST, URL, jsonInputObject,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            // response
                            mCounterMediaDown++;
                            FileReadWrite.writeToFileall(response.toString(), SDKHelper.MEDIA_VAULT_FILE_PATH);
                            try {
                                if (response != null && !response.toString().equalsIgnoreCase("") && response.getString(SDKHelper.TAG_RESULT_SET) != null
                                        && !response.getString(SDKHelper.TAG_RESULT_SET).equalsIgnoreCase("null")) {
                                    Gson gson = new Gson();

                                    mediaVaultBlockModel2 = new MediaVaultBlockModel();
                                    mediaVaultBlockModel2 = gson.fromJson(response.toString(), MediaVaultBlockModel.class);
                                        if (mediaVaultBlockModel2 != null) {
                                            mCounterMediaDownSucc++;
                                        }
                                    }

                            } catch (JsonSyntaxException e) {
                                e.printStackTrace();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            checkDownloadBlocks();
                        }

                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // error in downloading
                            mCounterMediaDown++;
                            checkDownloadBlocks();
                        }
                    });

            // Adding JsonObject request to request queue
            SDKControl.getInstance().addToRequestQueue(stringPostRequest, SDKHelper.TAG_DOWNLOAD_DATA);
        } catch (Exception e) {
            e.printStackTrace();
            checkDownloadBlocks();
        }
    }

    /***
     * This method is used to start downloading new media file from PBC node 3
     *
     * @param jsonInputObject,url
     * @return
     */
    private void downLoadBlocksFromPbc3(final JSONObject jsonInputObject, final String URL) {
        try {
            FileReadWrite.writeToFileall(jsonInputObject.toString(), SDKHelper.MEDIA_VAULT_FILE_PATH);
            JsonObjectRequest stringPostRequest = new JsonObjectRequest(Request.Method.POST, URL, jsonInputObject,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            // response
                            mCounterMediaDown++;
                            FileReadWrite.writeToFileall(response.toString(), SDKHelper.MEDIA_VAULT_FILE_PATH);
                            try {
                                if (response != null && !response.toString().equalsIgnoreCase("") && response.getString(SDKHelper.TAG_RESULT_SET) != null
                                        && !response.getString(SDKHelper.TAG_RESULT_SET).equalsIgnoreCase("null")) {
                                    Gson gson = new Gson();

                                    mediaVaultBlockModel3 = new MediaVaultBlockModel();
                                    mediaVaultBlockModel3 = gson.fromJson(response.toString(), MediaVaultBlockModel.class);
                                        if (mediaVaultBlockModel3 != null) {
                                            mCounterMediaDownSucc++;
                                        }

                                    }
                            } catch (JsonSyntaxException e) {
                                e.printStackTrace();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            checkDownloadBlocks();
                        }

                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // error in downloading
                            mCounterMediaDown++;
                            checkDownloadBlocks();
                        }
                    });

            // Adding JsonObject request to request queue
            SDKControl.getInstance().addToRequestQueue(stringPostRequest, SDKHelper.TAG_DOWNLOAD_DATA);
        } catch (Exception e) {
            e.printStackTrace();
            checkDownloadBlocks();
        }
    }

    /***
     * This method is used check and process data from all node
     */
    private void checkDownloadBlocks() {
        if (mCounterMediaDown == 3) {
            if (mCounterMediaDownSucc >= 2) {
                getMediaFiles();
            } else {
                mCounterMediaDown = 0;
                mCounterMediaDownSucc = 0;
                mReceiveMessageCallback.receiveMediaVaultCallback(SDKHelper.TAG_CAP_FAILED, SDKErrors.RECEIVE_MEDIA_VAULT_ERROR, "",null);
            }
        }
    }



    /***
     * This method is used to call download media files from PBC
     */
    private void getMediaFiles() {
        if(mCounterMediaDownSucc == 2){
            if(mediaVaultBlockModel != null && mediaVaultBlockModel2 !=  null ){
                getMediaFilesFromPBC1(mediaVaultBlockModel);
                getMediaFilesFromPBC2(mediaVaultBlockModel2);
            }
            else if (mediaVaultBlockModel2 !=null && mediaVaultBlockModel3 != null){
                getMediaFilesFromPBC2(mediaVaultBlockModel2);
                getMediaFilesFromPBC3(mediaVaultBlockModel3);
            }else {
                getMediaFilesFromPBC1(mediaVaultBlockModel);
                getMediaFilesFromPBC3(mediaVaultBlockModel3);
            }
        }
        else if (mCounterMediaDownSucc == 3){
            getMediaFilesFromPBC1(mediaVaultBlockModel);
            getMediaFilesFromPBC2(mediaVaultBlockModel2);
            getMediaFilesFromPBC3(mediaVaultBlockModel3);
        }
    }



    /***
     * This method is used to download file from PBC node 1
     *
     * @param matchTransactionModel
     * @return
     */
    private void getMediaFilesFromPBC1(final MediaVaultBlockModel matchTransactionModel) {
        String url = SDKHelper.GET_MEDIA_FILE_NODE1 + matchTransactionModel.getResultSet().getFileId();
        InputStreamVolleyRequest request = new InputStreamVolleyRequest(Request.Method.GET, url,
                new Response.Listener<byte[]>() {
                    @Override
                    public void onResponse(byte[] response) {
                        mCounterFileDownload++;
                        InputStream responseStream = new ByteArrayInputStream(response);
                        if(resultStream == null){
                            resultStream = new ByteArrayInputStream(response);
                        }
                        validateCRC(responseStream, matchTransactionModel);

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                mCounterFileDownload++;
                validateCRC(null, matchTransactionModel);

            }
        }, null);
        RequestQueue mRequestQueue = Volley.newRequestQueue(mContext, new HurlStack());
        mRequestQueue.add(request);
    }

    /***
     * This method is used to download file from PBC node 2
     *
     * @param matchTransactionModel
     * @return
     */
    private void getMediaFilesFromPBC2(final MediaVaultBlockModel matchTransactionModel) {
        String url = SDKHelper.GET_MEDIA_FILE_NODE2 + matchTransactionModel.getResultSet().getFileId();
        InputStreamVolleyRequest request = new InputStreamVolleyRequest(Request.Method.GET, url,
                new Response.Listener<byte[]>() {
                    @Override
                    public void onResponse(byte[] response) {
                        mCounterFileDownload++;
                        InputStream responseStream = new ByteArrayInputStream(response);
                        if(resultStream == null){
                            resultStream = new ByteArrayInputStream(response);
                        }
                        validateCRC(responseStream, matchTransactionModel);


                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                mCounterFileDownload++;
                validateCRC(null, matchTransactionModel);

            }
        }, null);
        RequestQueue mRequestQueue = Volley.newRequestQueue(mContext, new HurlStack());
        mRequestQueue.add(request);
    }

    /***
     * This method is used to download file from PBC node 3
     *
     * @param matchTransactionModel
     * @return
     */
    private void getMediaFilesFromPBC3(final MediaVaultBlockModel matchTransactionModel) {
        String url = SDKHelper.GET_MEDIA_FILE_NODE3 + matchTransactionModel.getResultSet().getFileId();
        InputStreamVolleyRequest request = new InputStreamVolleyRequest(Request.Method.GET, url,
                new Response.Listener<byte[]>() {
                    @Override
                    public void onResponse(byte[] response) {
                        mCounterFileDownload++;
                        InputStream responseStream = new ByteArrayInputStream(response);
                        if(resultStream == null){
                            resultStream = new ByteArrayInputStream(response);
                        }
                        validateCRC(responseStream, matchTransactionModel);

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                mCounterFileDownload++;
                validateCRC(null, matchTransactionModel);
            }
        }, null);
        RequestQueue mRequestQueue = Volley.newRequestQueue(mContext, new HurlStack());
        mRequestQueue.add(request);
    }

    /***
     * This method is used to compare new generated CRC and received CRC to validate
     *
     * @param response
     * @param matchTransactionModel
     */
    private void validateCRC(InputStream response, MediaVaultBlockModel matchTransactionModel) {
        if (response != null) {
            try {
                String EncryptMsgforCrc = matchTransactionModel.getResultSet().getTag() + "|$$|" + matchTransactionModel.getResultSet().getId()
                        + "|$$|" + matchTransactionModel.getResultSet().getWalletAddress()
                        + "|$$|" + DataHelper.calculateHash(response, SDKConstants.SHA_256)
                        + "|$$|" + matchTransactionModel.getResultSet().getPbcId()
                        + "|$$|" + matchTransactionModel.getResultSet().getAppId()
                        + "|$$|" + matchTransactionModel.getResultSet().getTimestamp()
                        + "|$$|" + matchTransactionModel.getResultSet().getWebServerKey();
                String mCRC = null;
                try {
                    mCRC = DataHelper.getCRC(EncryptMsgforCrc);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // if validate call decrypt message
                if (mCRC.equals(matchTransactionModel.getResultSet().getCrc())) {
                    mCrcMatched++;
                }else{
                    resultStream = null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        validateAllNodeCrc(matchTransactionModel);
    }


    /***
     * This method is used to compare all nodes crc and call decryption process
     * @param matchTransactionModel
     */
    private void validateAllNodeCrc(MediaVaultBlockModel matchTransactionModel) {
        if (mCounterFileDownload == mCounterMediaDownSucc) {
            if (mCrcMatched >= 2 && resultStream != null) {
                new EncryptDecryptData().decryptMediaFile(mEncryptedTxId, mEncryptedFileKey,
                        resultStream, this, matchTransactionModel);
            } else {
                mReceiveMessageCallback.receiveMediaVaultCallback(SDKHelper.TAG_CAP_FAILED, SDKErrors.MEDIA_VAULT_CRC_VALIDATION, "", null);

            }
            resultStream = null;
            mCounterMediaDown = 0;
            mCounterMediaDownSucc = 0;
            mCounterFileDownload = 0;
            mCrcMatched = 0;
        }
    }


    @Override
    public void decryptedMediaVaultFile(String decryptMessage, MediaVaultBlockModel matchTransactionModel) {
        if (decryptMessage != null && !decryptMessage.equalsIgnoreCase("")) {
            mReceiveMessageCallback.receiveMediaVaultCallback(SDKHelper.TAG_CAP_OK, SDKHelper.RECEIVE_MESSAGE, decryptMessage,matchTransactionModel);

        } else {
            mReceiveMessageCallback.receiveMediaVaultCallback(SDKHelper.TAG_CAP_FAILED, SDKErrors.RECEIVE_MESSAGE_ERROR, "", null);
        }
    }
}
