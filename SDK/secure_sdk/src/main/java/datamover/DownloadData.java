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
import org.spongycastle.util.encoders.Base64;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import bitmanagers.BitVaultWalletManager;
import bitmanagers.EOTWalletManager;
import commons.SDKConstants;
import commons.SDKErrors;
import commons.SDKHelper;
import commons.SecureSDKException;
import controller.Preferences;
import controller.SDKControl;
import iclasses.DecryptDataCallback;
import iclasses.ReceiveMessageCallBack;
import iclasses.TransactionHistoryCallback;
import model.EotTransactionHistory;
import model.MatchTransactionModel;
import model.NotificationMessageModel;
import model.PullMessageModel;
import model.TransactionHistoryModel;
import securityencryption.DataHelper;
import securityencryption.EncryptDecryptData;
import utils.SDKUtils;
import webservicescontroller.servicehandlers.InputStreamVolleyRequest;

/**
 * Created by Vinod Singh on 17/5/17.
 */

public class DownloadData implements DecryptDataCallback, TransactionHistoryCallback {
    private Context mContext;
    private ReceiveMessageCallBack mReceiveMessageCallback;
    private PullMessageModel pullMessageModel,pullMessageModel2,pullMessageModel3;
    private NotificationMessageModel notificationMessageModel,notificationMessageModel2,notificationMessageModel3;
    private int mCounterAck = 0,mCounterAckSucccess = 0, mCounterFileDownload = 0,mCounterFileDownNodes = 0
            ,mCrcMatched = 0,mLastMessageVal = 0,mCounterMessageDown =0,mCounterMessageDownSucc =0;
    private ArrayList<String> listHistoryTxId = new ArrayList<>();
    private ArrayList<MatchTransactionModel> matchTxList = new ArrayList<>();
    private ArrayList<MatchTransactionModel> matchTxList2 = new ArrayList<>();
    private ArrayList<MatchTransactionModel> matchTxList3 = new ArrayList<>();
    private boolean isByNotification = false;
    private InputStream resultStream = null;

    // Constructor to initialise the objects
    public DownloadData(Context mContext, ReceiveMessageCallBack receiveMessageCallback) {
        this.mContext = mContext;
        this.mReceiveMessageCallback = receiveMessageCallback;
    }

    /***
     * This method is used to get downloading message blocks from PBC
     *
     * @param jsonInputObject
     * @return
     */

    public void downloadMessage(JSONObject jsonInputObject) {
        isByNotification = true;
        downLoadBlocksFromPbc1(jsonInputObject, SDKHelper.URL_RECEIVE_DATA_NOTIFICATION_NODE1);
        downLoadBlocksFromPbc2(jsonInputObject, SDKHelper.URL_RECEIVE_DATA_NOTIFICATION_NODE2);
        downLoadBlocksFromPbc3(jsonInputObject, SDKHelper.URL_RECEIVE_DATA_NOTIFICATION_NODE3);
        SDKUtils.showLog("StartDownloadMessageNotification",""+ System.currentTimeMillis());
    }

    public void downloadMessage(JSONObject jsonInputObject, List<Integer> addressId) {
        isByNotification = false;
        downLoadBlocksFromPbc1(jsonInputObject, SDKHelper.URL_RECEIVE_DATA_PULL_NODE1);
        downLoadBlocksFromPbc2(jsonInputObject, SDKHelper.URL_RECEIVE_DATA_PULL_NODE2);
        downLoadBlocksFromPbc3(jsonInputObject, SDKHelper.URL_RECEIVE_DATA_PULL_NODE3);
        SDKUtils.showLog("StartDownloadBLocks",""+ System.currentTimeMillis());
    }

    /***
     * This method is used to start downloading new message from PBC node 1
     *
     * @param jsonInputObject,url
     * @return
     */

    private void downLoadBlocksFromPbc1(final JSONObject jsonInputObject, final String URL) {
        try {
            FileReadWrite.writeToFileall(jsonInputObject.toString(), SDKHelper.FILE_PATH);
            JsonObjectRequest stringPostRequest = new JsonObjectRequest(Request.Method.POST, URL, jsonInputObject,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            mCounterMessageDown++;
                            // response
                            FileReadWrite.writeToFileall(response.toString(), SDKHelper.FILE_PATH);
                            try {
                                if (response != null && !response.toString().equalsIgnoreCase("") && response.getString(SDKHelper.TAG_RESULT_SET) != null
                                        && !response.getString(SDKHelper.TAG_RESULT_SET).equalsIgnoreCase("null")) {

                                    SDKUtils.showLog(SDKHelper.TAG_DOWNLOAD_DATA, response.toString());
                                    Gson gson = new Gson();
                                    if (isByNotification) {
                                        notificationMessageModel = new NotificationMessageModel();
                                        notificationMessageModel = gson.fromJson(response.toString(), NotificationMessageModel.class);
                                        if (notificationMessageModel != null && notificationMessageModel.getResultSet() != null) {
                                            if (notificationMessageModel.getResultSet().getFileId() != null && !notificationMessageModel.getResultSet().getFileId().equalsIgnoreCase("")) {
                                                mCounterMessageDownSucc++;
                                            }
                                        }
                                    } else {
                                        pullMessageModel = new PullMessageModel();
                                        pullMessageModel = gson.fromJson(response.toString(), PullMessageModel.class);
                                        if (pullMessageModel != null && pullMessageModel.getResultSet().length != 0) {
                                            mCounterMessageDownSucc++;
                                        }
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
                            mCounterMessageDown++;
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
     * This method is used to start downloading new message from PBC node 2
     *
     * @param jsonInputObject,url
     * @return
     */
    private void downLoadBlocksFromPbc2(final JSONObject jsonInputObject, final String URL) {
        try {
            FileReadWrite.writeToFileall(jsonInputObject.toString(), SDKHelper.FILE_PATH);
            JsonObjectRequest stringPostRequest = new JsonObjectRequest(Request.Method.POST, URL, jsonInputObject,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            // response
                            mCounterMessageDown++;
                            FileReadWrite.writeToFileall(response.toString(), SDKHelper.FILE_PATH);
                                try {
                                    if (response != null && !response.toString().equalsIgnoreCase("") && response.getString(SDKHelper.TAG_RESULT_SET) != null
                                            && !response.getString(SDKHelper.TAG_RESULT_SET).equalsIgnoreCase("null")) {
                                        SDKUtils.showLog(SDKHelper.TAG_DOWNLOAD_DATA, response.toString());
                                        Gson gson = new Gson();

                                        if (isByNotification) {
                                            notificationMessageModel2 = new NotificationMessageModel();
                                            notificationMessageModel2 = gson.fromJson(response.toString(), NotificationMessageModel.class);
                                            if (notificationMessageModel2 != null && notificationMessageModel2.getResultSet() != null) {
                                                if (notificationMessageModel2.getResultSet().getFileId() != null && !notificationMessageModel2.getResultSet().getFileId().equalsIgnoreCase("")) {
                                                    mCounterMessageDownSucc++;
                                                }
                                            }
                                        } else {
                                            pullMessageModel2 = new PullMessageModel();
                                            pullMessageModel2 = gson.fromJson(response.toString(), PullMessageModel.class);
                                            if (pullMessageModel2 != null && pullMessageModel2.getResultSet().length != 0) {
                                                mCounterMessageDownSucc++;
                                            }
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
                            mCounterMessageDown++;
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
     * This method is used to start downloading new message from PBC node 3
     *
     * @param jsonInputObject,url
     * @return
     */
    private void downLoadBlocksFromPbc3(final JSONObject jsonInputObject, final String URL) {
        try {
            FileReadWrite.writeToFileall(jsonInputObject.toString(), SDKHelper.FILE_PATH);
            JsonObjectRequest stringPostRequest = new JsonObjectRequest(Request.Method.POST, URL, jsonInputObject,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            // response
                            mCounterMessageDown++;
                            FileReadWrite.writeToFileall(response.toString(), SDKHelper.FILE_PATH);
                            try {
                                if (response != null && !response.toString().equalsIgnoreCase("") && response.getString(SDKHelper.TAG_RESULT_SET) != null
                                        && !response.getString(SDKHelper.TAG_RESULT_SET).equalsIgnoreCase("null")) {
                                    SDKUtils.showLog(SDKHelper.TAG_DOWNLOAD_DATA, response.toString());
                                    Gson gson = new Gson();

                                    if (isByNotification) {
                                        notificationMessageModel3 = new NotificationMessageModel();
                                        notificationMessageModel3 = gson.fromJson(response.toString(), NotificationMessageModel.class);
                                        if (notificationMessageModel3 != null && notificationMessageModel3.getResultSet() != null) {
                                            if (notificationMessageModel3.getResultSet().getFileId() != null && !notificationMessageModel3.getResultSet().getFileId().equalsIgnoreCase("")) {
                                                mCounterMessageDownSucc++;
                                            }
                                        }
                                    } else {
                                        pullMessageModel3 = new PullMessageModel();
                                        pullMessageModel3 = gson.fromJson(response.toString(), PullMessageModel.class);
                                        if (pullMessageModel3 != null && pullMessageModel3.getResultSet().length != 0) {
                                            mCounterMessageDownSucc++;
                                        }

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
                            mCounterMessageDown++;
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
    private void checkDownloadBlocks(){
        if(mCounterMessageDown == 3 ){
            if(mCounterMessageDownSucc >= 2 ){
                mCounterMessageDown = 0;
                mCounterMessageDownSucc = 0;
                SDKUtils.showLog("StopDownloadBLocks",""+ System.currentTimeMillis());
                SDKUtils.showLog("StartDownGetHisttory",""+ System.currentTimeMillis());
                callgetAllTxHistory();
            }
            else{
                mCounterMessageDown = 0;
                mCounterMessageDownSucc = 0;
                if(isByNotification){
                    isByNotification = false;
                }
                new Preferences().saveData(mContext,"", SDKHelper.PREFERENCE_RECEIVE_NAME, SDKHelper.PREFERENCE_RECEIVE_KEY);
                mReceiveMessageCallback.receiveMessageCallback(SDKHelper.TAG_CAP_FAILED, SDKErrors.RECEIVE_MESSAGE_ERROR, "", "","","");
            }
        }
    }

    /***
     * This method is used to get fetch all the transaction Id From Insight server
     */
    private void callgetAllTxHistory() {
        if(BitVaultWalletManager.getWalletInstance() !=  null) {
            try {
                BitVaultWalletManager.getWalletInstance().getTransactionsHistory(this);
            } catch (SecureSDKException e) {
                e.printStackTrace();
                new Preferences().saveData(mContext, "", SDKHelper.PREFERENCE_RECEIVE_NAME, SDKHelper.PREFERENCE_RECEIVE_KEY);
                mReceiveMessageCallback.receiveMessageCallback(SDKHelper.TAG_CAP_FAILED, SDKErrors.RECEIVE_MESSAGE_ERROR, "", "","","");
            }
        }
        else{
            new Preferences().saveData(mContext, "", SDKHelper.PREFERENCE_RECEIVE_NAME, SDKHelper.PREFERENCE_RECEIVE_KEY);
            mReceiveMessageCallback.receiveMessageCallback(SDKHelper.TAG_CAP_FAILED, SDKErrors.RECEIVE_MESSAGE_ERROR, "", "","","");
        }
    }

    @Override
    public void transactionHistorySuccess(JSONObject mHistoryResponse, String walletAddress) {
        SDKUtils.showLog("StopDownGet_EotHisttory",""+ System.currentTimeMillis());
        if(mHistoryResponse != null) {
            Gson gson = new Gson();
            EotTransactionHistory eotTransactionHistoryModel = gson.fromJson(mHistoryResponse.toString(), EotTransactionHistory.class);
            if (eotTransactionHistoryModel != null && eotTransactionHistoryModel.getTxs() != null) {
                for (int i = 0; i < eotTransactionHistoryModel.getTxs().length; i++) {
                    listHistoryTxId.add(eotTransactionHistoryModel.getTxs()[i].getTxid());
                }
            }
        }
        checkWalletAndCall();

    }

    @Override
    public void allWalletsTransactionHistory(JSONObject mHistoryResponse, ArrayList<String> mWalletsList) {
        SDKUtils.showLog("StopDownGetHisttory",""+ System.currentTimeMillis());
        if(mHistoryResponse != null) {
            Gson gson = new Gson();
            TransactionHistoryModel transactionHistoryModel = gson.fromJson(mHistoryResponse.toString(), TransactionHistoryModel.class);
            if (transactionHistoryModel != null && transactionHistoryModel.getItems() != null) {
                for (int i = 0; i < transactionHistoryModel.getItems().length; i++) {
                    listHistoryTxId.add(transactionHistoryModel.getItems()[i].getTxid());
                }
            }
        }
        if(EOTWalletManager.getEOTWalletInstance() != null) {
            EOTWalletManager.getEOTWalletInstance().getWalletTransactionsHistory(this);
        }else{
            checkWalletAndCall();
        }

    }

    @Override
    public void transactionHistoryFailed(VolleyError mVolleyError) {
        checkWalletAndCall();
    }

    /***
     * This method is used to get TransactionId History Recursively for all the walletId and start downloading.
     */
    private void checkWalletAndCall() {
        SDKUtils.showLog("StartDownCompareTXId",""+ System.currentTimeMillis());
            if(listHistoryTxId != null && listHistoryTxId.size()!= 0 ){
                if(isByNotification){
                    getMessageByNotification();
                }
                else{
                    getMessageFiles();
                }
            }
            else{
                new Preferences().saveData(mContext,"", SDKHelper.PREFERENCE_RECEIVE_NAME, SDKHelper.PREFERENCE_RECEIVE_KEY);
                mReceiveMessageCallback.receiveMessageCallback(SDKHelper.TAG_CAP_FAILED, SDKErrors.RECEIVE_MESSAGE_ERROR, "", "","","");
            }
    }


    /***
     * This method is used to call download matched transaction Ids files from PBC
     */
    private void getMessageFiles() {
        if(pullMessageModel != null && pullMessageModel.getResultSet() != null && pullMessageModel.getResultSet().length != 0){
            for (int i = 0; i < pullMessageModel.getResultSet().length; i++) {
                for (int j = 0; j < listHistoryTxId.size(); j++) {
                    if (pullMessageModel.getResultSet()[i].getTransactionId().equals(Base64.toBase64String(DataHelper.hashGenerate(listHistoryTxId.get(j))))) {
                        MatchTransactionModel matchTransactionModel = new MatchTransactionModel(listHistoryTxId.get(j),
                                pullMessageModel.getResultSet()[i].getTransactionId(), pullMessageModel.getResultSet()[i].getFileId()
                                , pullMessageModel.getResultSet()[i].getCrc(), pullMessageModel.getResultSet()[i].getTimestamp(), pullMessageModel.getResultSet()[i].getPbcId()
                                , pullMessageModel.getResultSet()[i].getAppId(), pullMessageModel.getResultSet()[i].getReceiver(), pullMessageModel.getResultSet()[i].getTag(),
                                pullMessageModel.getResultSet()[i].getSessionKey(), pullMessageModel.getResultSet()[i].getSender(),pullMessageModel.getResultSet()[i].getWebServerKey());
                        matchTxList.add(matchTransactionModel);
                    }
                }
            }
        }
        if(pullMessageModel2 != null && pullMessageModel2.getResultSet() != null && pullMessageModel2.getResultSet().length != 0){
            for (int i = 0; i < pullMessageModel2.getResultSet().length; i++) {
                for (int j = 0; j < listHistoryTxId.size(); j++) {
                    if (pullMessageModel2.getResultSet()[i].getTransactionId().equals(Base64.toBase64String(DataHelper.hashGenerate(listHistoryTxId.get(j))))) {
                        MatchTransactionModel matchTransactionModel = new MatchTransactionModel(listHistoryTxId.get(j),
                                pullMessageModel2.getResultSet()[i].getTransactionId(), pullMessageModel2.getResultSet()[i].getFileId()
                                , pullMessageModel2.getResultSet()[i].getCrc(), pullMessageModel2.getResultSet()[i].getTimestamp(), pullMessageModel2.getResultSet()[i].getPbcId()
                                , pullMessageModel2.getResultSet()[i].getAppId(), pullMessageModel2.getResultSet()[i].getReceiver(), pullMessageModel2.getResultSet()[i].getTag(),
                                pullMessageModel2.getResultSet()[i].getSessionKey(), pullMessageModel2.getResultSet()[i].getSender(),pullMessageModel2.getResultSet()[i].getWebServerKey());
                        matchTxList2.add(matchTransactionModel);
                    }
                }
            }
        }
        if(pullMessageModel3 != null && pullMessageModel3.getResultSet() != null && pullMessageModel3.getResultSet().length != 0){
            for (int i = 0; i < pullMessageModel3.getResultSet().length; i++) {
                for (int j = 0; j < listHistoryTxId.size(); j++) {
                    if (pullMessageModel3.getResultSet()[i].getTransactionId().equals(Base64.toBase64String(DataHelper.hashGenerate(listHistoryTxId.get(j))))) {
                        MatchTransactionModel matchTransactionModel = new MatchTransactionModel(listHistoryTxId.get(j),
                                pullMessageModel3.getResultSet()[i].getTransactionId(), pullMessageModel3.getResultSet()[i].getFileId()
                                , pullMessageModel3.getResultSet()[i].getCrc(), pullMessageModel3.getResultSet()[i].getTimestamp(), pullMessageModel3.getResultSet()[i].getPbcId()
                                , pullMessageModel3.getResultSet()[i].getAppId(), pullMessageModel3.getResultSet()[i].getReceiver(), pullMessageModel3.getResultSet()[i].getTag(),
                                pullMessageModel3.getResultSet()[i].getSessionKey(), pullMessageModel3.getResultSet()[i].getSender(),pullMessageModel3.getResultSet()[i].getWebServerKey());
                        matchTxList3.add(matchTransactionModel);
                    }
                }
            }
        }
        SDKUtils.showLog("StopCompareTXId",""+ System.currentTimeMillis());
        SDKUtils.showLog("DownmatchTxList",""+ matchTxList3.size());
        SDKUtils.showLog("DownmatchTxList2",""+ matchTxList3.size());
        SDKUtils.showLog("DownmatchTxList3",""+ matchTxList3.size());
        checkNodesAndCall(1);
    }

    /***
     * This method is used to call download matched transaction Ids files from PBC in Notification case
     */
     private void getMessageByNotification(){
         if(notificationMessageModel != null && notificationMessageModel.getResultSet()!= null) {
             if (notificationMessageModel.getResultSet().getTransactionId() != null && !notificationMessageModel.getResultSet().getTransactionId().equalsIgnoreCase("")) {
                 for (int j = 0; j < listHistoryTxId.size(); j++) {
                     if (notificationMessageModel.getResultSet().getTransactionId().equals(Base64.toBase64String(DataHelper.hashGenerate(listHistoryTxId.get(j))))) {
                         MatchTransactionModel matchTransactionModel = new MatchTransactionModel(listHistoryTxId.get(j),
                                 notificationMessageModel.getResultSet().getTransactionId(), notificationMessageModel.getResultSet().getFileId()
                                 , notificationMessageModel.getResultSet().getCrc(), notificationMessageModel.getResultSet().getTimestamp(), notificationMessageModel.getResultSet().getPbcId()
                                 , notificationMessageModel.getResultSet().getAppId(), notificationMessageModel.getResultSet().getReceiver(), notificationMessageModel.getResultSet().getTag(),
                                 notificationMessageModel.getResultSet().getSessionKey(), notificationMessageModel.getResultSet().getSender(), notificationMessageModel.getResultSet().getWebServerKey());
                         matchTxList.add(matchTransactionModel);
                     }
                 }
             }
         }
         if(notificationMessageModel2 != null && notificationMessageModel2.getResultSet() != null) {
             if (notificationMessageModel2.getResultSet().getTransactionId() != null && !notificationMessageModel2.getResultSet().getTransactionId().equalsIgnoreCase("")) {
                 for (int j = 0; j < listHistoryTxId.size(); j++) {
                     if (notificationMessageModel2.getResultSet().getTransactionId().equals(Base64.toBase64String(DataHelper.hashGenerate(listHistoryTxId.get(j))))) {
                         MatchTransactionModel matchTransactionModel = new MatchTransactionModel(listHistoryTxId.get(j),
                                 notificationMessageModel2.getResultSet().getTransactionId(), notificationMessageModel2.getResultSet().getFileId()
                                 , notificationMessageModel2.getResultSet().getCrc(), notificationMessageModel2.getResultSet().getTimestamp(), notificationMessageModel2.getResultSet().getPbcId()
                                 , notificationMessageModel2.getResultSet().getAppId(), notificationMessageModel2.getResultSet().getReceiver(), notificationMessageModel2.getResultSet().getTag(),
                                 notificationMessageModel2.getResultSet().getSessionKey(), notificationMessageModel2.getResultSet().getSender(), notificationMessageModel2.getResultSet().getWebServerKey());
                         matchTxList2.add(matchTransactionModel);
                     }
                 }
             }
         }
         if(notificationMessageModel3 != null && notificationMessageModel3.getResultSet() !=  null) {
             if (notificationMessageModel3.getResultSet().getTransactionId() != null && !notificationMessageModel3.getResultSet().getTransactionId().equalsIgnoreCase("")) {
                 for (int j = 0; j < listHistoryTxId.size(); j++) {
                     if (notificationMessageModel3.getResultSet().getTransactionId().equals(Base64.toBase64String(DataHelper.hashGenerate(listHistoryTxId.get(j))))) {
                         MatchTransactionModel matchTransactionModel = new MatchTransactionModel(listHistoryTxId.get(j),
                                 notificationMessageModel3.getResultSet().getTransactionId(), notificationMessageModel3.getResultSet().getFileId()
                                 , notificationMessageModel3.getResultSet().getCrc(), notificationMessageModel3.getResultSet().getTimestamp(), notificationMessageModel3.getResultSet().getPbcId()
                                 , notificationMessageModel3.getResultSet().getAppId(), notificationMessageModel3.getResultSet().getReceiver(), notificationMessageModel3.getResultSet().getTag(),
                                 notificationMessageModel3.getResultSet().getSessionKey(), notificationMessageModel3.getResultSet().getSender(), notificationMessageModel3.getResultSet().getWebServerKey());
                         matchTxList3.add(matchTransactionModel);
                     }
                 }
             }
         }
         SDKUtils.showLog("StopCompareTXId",""+ System.currentTimeMillis());
         SDKUtils.showLog("DownmatchTxList",""+ matchTxList3.size());
         SDKUtils.showLog("DownmatchTxList2",""+ matchTxList3.size());
         SDKUtils.showLog("DownmatchTxList3",""+ matchTxList3.size());
         checkNodesAndCall(1);
     }
    /***
     * This method is used check file data available or not on nodes and start file downloading
     */
    private void checkNodesAndCall(int value){
            if (matchTxList != null && matchTxList.size() > value - 1  && matchTxList2 != null && matchTxList2.size() > value - 1
                    && matchTxList3 != null && matchTxList3.size() > value - 1) {
                mLastMessageVal = value;
                SDKUtils.showLog("StartDownloadFiles",""+ System.currentTimeMillis());
                getMessageFilesFromPBC1(matchTxList.get(value - 1));
                getMessageFilesFromPBC2(matchTxList2.get(value - 1));
                getMessageFilesFromPBC3(matchTxList3.get(value - 1));
                mCounterFileDownNodes = 3;
            }
            else if (matchTxList != null && matchTxList.size() > value - 1  && matchTxList2 != null && matchTxList2.size() > value - 1 ) {
                mLastMessageVal = value;
                SDKUtils.showLog("StartDownloadFiles",""+ System.currentTimeMillis());
                getMessageFilesFromPBC1(matchTxList.get(value - 1));
                getMessageFilesFromPBC2(matchTxList2.get(value - 1));
                mCounterFileDownNodes = 2;
            }
            else if (matchTxList2 != null && matchTxList2.size() > value -1 && matchTxList3 != null && matchTxList3.size() > value -1 ) {
                mLastMessageVal = value;
                SDKUtils.showLog("StartDownloadFiles",""+ System.currentTimeMillis());
                getMessageFilesFromPBC2(matchTxList2.get(value - 1));
                getMessageFilesFromPBC3(matchTxList3.get(value - 1));
                mCounterFileDownNodes = 2;
            }
            else if (matchTxList != null && matchTxList.size() > value -1 && matchTxList3 != null && matchTxList3.size() > value -1 ) {
                mLastMessageVal = value;
                SDKUtils.showLog("StartDownloadFiles",""+ System.currentTimeMillis());
                getMessageFilesFromPBC1(matchTxList.get(value - 1));
                getMessageFilesFromPBC3(matchTxList3.get(value - 1));
                mCounterFileDownNodes = 2;
            }
            else{
                if(isByNotification){
                    isByNotification = false;
                }
                new Preferences().saveData(mContext,"", SDKHelper.PREFERENCE_RECEIVE_NAME, SDKHelper.PREFERENCE_RECEIVE_KEY);
                mReceiveMessageCallback.receiveMessageCallback(SDKHelper.TAG_CAP_FAILED, SDKErrors.RECEIVE_MESSAGE_ERROR, "", "","","");
            }
    }

    /***
     * This method is used to download file from PBC node 1
     *
     * @param matchTransactionModel
     * @return
     */
    private void getMessageFilesFromPBC1(final MatchTransactionModel matchTransactionModel) {
        String url = SDKHelper.URL_GET_FILE_NODE1 + matchTransactionModel.getUrl();
        InputStreamVolleyRequest request = new InputStreamVolleyRequest(Request.Method.GET, url,
                new Response.Listener<byte[]>() {
                    @Override
                    public void onResponse(byte[] response) {
                        mCounterFileDownload++;
                        InputStream responseStream = new ByteArrayInputStream(response);
                        if(resultStream == null){
                            resultStream = new ByteArrayInputStream(response);
                        }
                        SDKUtils.showLog("DownResponseLengthNode1",""+ response.length);
                        validateCRC(responseStream, matchTransactionModel);

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                mCounterFileDownload++;
                SDKUtils.showLog("DownResponseFailed1",""+ mCounterFileDownload);
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
    private void getMessageFilesFromPBC2(final MatchTransactionModel matchTransactionModel) {
        String url = SDKHelper.URL_GET_FILE_NODE2 + matchTransactionModel.getUrl();
        InputStreamVolleyRequest request = new InputStreamVolleyRequest(Request.Method.GET, url,
                new Response.Listener<byte[]>() {
                    @Override
                    public void onResponse(byte[] response) {
                        mCounterFileDownload++;
                        InputStream responseStream = new ByteArrayInputStream(response);
                        if(resultStream == null){
                            resultStream = new ByteArrayInputStream(response);
                        }
                        SDKUtils.showLog("DownResponseLengthNode2",""+ response.length);
                        validateCRC(responseStream, matchTransactionModel);

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                mCounterFileDownload++;
                SDKUtils.showLog("DownResponseFailed2",""+ mCounterFileDownload);
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
    private void getMessageFilesFromPBC3(final MatchTransactionModel matchTransactionModel) {
        String url = SDKHelper.URL_GET_FILE_NODE3 + matchTransactionModel.getUrl();
        InputStreamVolleyRequest request = new InputStreamVolleyRequest(Request.Method.GET, url,
                new Response.Listener<byte[]>() {
                    @Override
                    public void onResponse(byte[] response) {
                        mCounterFileDownload++;
                        InputStream responseStream = new ByteArrayInputStream(response);
                        if(resultStream == null){
                            resultStream = new ByteArrayInputStream(response);
                        }
                        SDKUtils.showLog("DownResponseLengthNode3",""+ response.length);
                        validateCRC(responseStream, matchTransactionModel);

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                mCounterFileDownload++;
                SDKUtils.showLog("DownResponseFailed3",""+ mCounterFileDownload);
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
    private void validateCRC(InputStream response, MatchTransactionModel matchTransactionModel) {
        if(response != null){
            try {
                //  MessageHelper.hashGenrateForMessage(new String(response))
                final String fileHash =  DataHelper.calculateHash(response, SDKConstants.SHA_256);
                String EncryptMsgforCrc = matchTransactionModel.getTag() + "|$$|" + matchTransactionModel.getHashTxId() + "|$$|" + matchTransactionModel.getReceiver() +
                        "|$$|" + fileHash + "|$$|" + matchTransactionModel.getSessionKey() + "|$$|" + matchTransactionModel.getPbcId()
                        + "|$$|" + matchTransactionModel.getAppId() + "|$$|" + matchTransactionModel.getTimestamp() + "|$$|" + matchTransactionModel.getSenderAddress() + "|$$|" + matchTransactionModel.getWebServerKey();
                SDKUtils.showLog(SDKHelper.TAG_ENCRYPTION, EncryptMsgforCrc);

                String mCRC = null;
                try {
                    mCRC = DataHelper.getCRC(EncryptMsgforCrc);
                    SDKUtils.showLog(SDKHelper.TAG_ENCRYPTION, mCRC);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // if validate call decrypt message
                if (mCRC.equals(matchTransactionModel.getCrc())) {
                    mCrcMatched ++;
                    SDKUtils.showLog("DownmCrcMatched",""+ mCrcMatched);
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
     */
    private void validateAllNodeCrc(MatchTransactionModel matchTransactionModel){
        if(mCounterFileDownload == mCounterFileDownNodes){
            SDKUtils.showLog("StopDownValidateFiles",""+ System.currentTimeMillis());
            if(mCrcMatched >= 2){
                SDKUtils.showLog("StartDownDecryption",""+ System.currentTimeMillis());
                new EncryptDecryptData().decryptData(matchTransactionModel.getTxId(), matchTransactionModel.getSessionKey(),
                        resultStream, this, matchTransactionModel);

            }
            else{
                checkAllMessageReceive();
            }
            resultStream = null;
        }
    }


    @Override
    public void decryptedMessage(String decryptMessage, MatchTransactionModel matchTransactionModel) {
        if (decryptMessage != null && !decryptMessage.equalsIgnoreCase("")) {
            mCounterAck = 0;
            mCounterAckSucccess = 0;
            SDKUtils.showLog("StopDownDecryption",""+ System.currentTimeMillis());
            SDKUtils.showLog("StartDownAck_Files",""+ System.currentTimeMillis());
            sendAcnowledgementToPBC1(SDKHelper.URL_RECEIVE_ACK_1, matchTransactionModel, decryptMessage);
            sendAcnowledgementToPBC2(SDKHelper.URL_RECEIVE_ACK_2, matchTransactionModel, decryptMessage);
            sendAcnowledgementToPBC3(SDKHelper.URL_RECEIVE_ACK_3, matchTransactionModel, decryptMessage);
        } else {
            checkAllMessageReceive();
            mReceiveMessageCallback.receiveMessageCallback(SDKHelper.TAG_CAP_FAILED, SDKErrors.RECEIVE_MESSAGE_ERROR, "", "","","");
        }
    }

    /***
     * This method is used to send Acknowledgement back to PBC node 1 after downloading file successfully
     *
     * @param URL
     * @param matchTransactionModel
     * @param decryptMessage
     * @return
     */
    private void sendAcnowledgementToPBC1(final String URL, final MatchTransactionModel matchTransactionModel, final String decryptMessage) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(SDKHelper.DATA_TAG, matchTransactionModel.getTag());
            jsonObject.put(SDKHelper.TRANSACTION_ID, matchTransactionModel.getHashTxId());
            jsonObject.put(SDKHelper.CRC, matchTransactionModel.getCrc());
            FileReadWrite.writeToFileall(matchTransactionModel.toString(), SDKHelper.FILE_PATH);
            JsonObjectRequest stringPostRequest = new JsonObjectRequest(Request.Method.POST, URL, jsonObject,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            mCounterAck++;
                            // response
                            FileReadWrite.writeToFileall(response.toString(), SDKHelper.FILE_PATH);
                            SDKUtils.showLog(SDKHelper.TAG_DOWNLOAD_DATA, response.toString());
                            try {
                                String status = response.getString(SDKHelper.TAG_STATUS);
                                String message = response.getString(SDKHelper.TAG_MESSAGE);
                                if (status.equals(SDKHelper.TAG_SUCCESS) && message.equals(SDKHelper.BLOCK_DELETE_SUCCESS)) {
                                    mCounterAckSucccess++;
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                           sendMessageCallBackAfterAck(matchTransactionModel,decryptMessage);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            mCounterAck++;
                            sendMessageCallBackAfterAck(matchTransactionModel, decryptMessage);
                        }
                    });

            // Adding JsonObject request to request queue
            SDKControl.getInstance().addToRequestQueue(stringPostRequest, SDKHelper.TAG_DOWNLOAD_DATA);
        } catch (Exception e) {
            e.printStackTrace();
            sendMessageCallBackAfterAck(matchTransactionModel, decryptMessage);
        }
    }
    /***
     * This method is used to send Acknowledgement back to PBC node 2 after downloading file successfully
     *
     * @param URL
     * @param matchTransactionModel
     * @param decryptMessage
     * @return
     */
    private void sendAcnowledgementToPBC2(final String URL, final MatchTransactionModel matchTransactionModel, final String decryptMessage) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(SDKHelper.DATA_TAG, matchTransactionModel.getTag());
            jsonObject.put(SDKHelper.TRANSACTION_ID, matchTransactionModel.getHashTxId());
            jsonObject.put(SDKHelper.CRC, matchTransactionModel.getCrc());
            FileReadWrite.writeToFileall(matchTransactionModel.toString(), SDKHelper.FILE_PATH);
            JsonObjectRequest stringPostRequest = new JsonObjectRequest(Request.Method.POST, URL, jsonObject,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            // response
                            mCounterAck++;
                            FileReadWrite.writeToFileall(response.toString(), SDKHelper.FILE_PATH);
                            SDKUtils.showLog(SDKHelper.TAG_DOWNLOAD_DATA, response.toString());
                            try {
                                String status = response.getString(SDKHelper.TAG_STATUS);
                                String message = response.getString(SDKHelper.TAG_MESSAGE);
                                if (status.equals(SDKHelper.TAG_SUCCESS) && message.equals(SDKHelper.BLOCK_DELETE_SUCCESS)) {
                                    mCounterAckSucccess++;
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            sendMessageCallBackAfterAck(matchTransactionModel, decryptMessage);

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            mCounterAck++;
                            sendMessageCallBackAfterAck(matchTransactionModel, decryptMessage);
                        }
                    });

            // Adding JsonObject request to request queue
            SDKControl.getInstance().addToRequestQueue(stringPostRequest, SDKHelper.TAG_DOWNLOAD_DATA);
        } catch (Exception e) {
            e.printStackTrace();
            sendMessageCallBackAfterAck(matchTransactionModel, decryptMessage);
        }
    }
    /***
     * This method is used to send Acknowledgement back to PBC node 3 after downloading file successfully
     *
     * @param URL
     * @param matchTransactionModel
     * @param decryptMessage
     * @return
     */
    private void sendAcnowledgementToPBC3(final String URL, final MatchTransactionModel matchTransactionModel, final String decryptMessage) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(SDKHelper.DATA_TAG, matchTransactionModel.getTag());
            jsonObject.put(SDKHelper.TRANSACTION_ID, matchTransactionModel.getHashTxId());
            jsonObject.put(SDKHelper.CRC, matchTransactionModel.getCrc());
            FileReadWrite.writeToFileall(matchTransactionModel.toString(), SDKHelper.FILE_PATH);
            JsonObjectRequest stringPostRequest = new JsonObjectRequest(Request.Method.POST, URL, jsonObject,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            // response
                            mCounterAck++;
                            FileReadWrite.writeToFileall(response.toString(), SDKHelper.FILE_PATH);
                            SDKUtils.showLog(SDKHelper.TAG_DOWNLOAD_DATA, response.toString());
                            try {
                                String status = response.getString(SDKHelper.TAG_STATUS);
                                String message = response.getString(SDKHelper.TAG_MESSAGE);
                                if (status.equals(SDKHelper.TAG_SUCCESS) && message.equals(SDKHelper.BLOCK_DELETE_SUCCESS)) {
                                    mCounterAckSucccess++;
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                          sendMessageCallBackAfterAck(matchTransactionModel,decryptMessage);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            mCounterAck++;
                            sendMessageCallBackAfterAck(matchTransactionModel, decryptMessage);
                        }
                    });

            // Adding JsonObject request to request queue
            SDKControl.getInstance().addToRequestQueue(stringPostRequest, SDKHelper.TAG_DOWNLOAD_DATA);
        } catch (Exception e) {
            e.printStackTrace();
            sendMessageCallBackAfterAck(matchTransactionModel, decryptMessage);

        }
    }

    /***
     * This method is used to send receive callback after processing acknowledgement calls & calculating validation .
     */
    private void sendMessageCallBackAfterAck(MatchTransactionModel matchTransactionModel, String decryptMessage) {
        if (mCounterAckSucccess == 2) {
            SDKUtils.showLog("StopDownAck_Files",""+ System.currentTimeMillis());
            mReceiveMessageCallback.receiveMessageCallback(SDKHelper.TAG_CAP_OK, SDKHelper.RECEIVE_MESSAGE,
                    decryptMessage, matchTransactionModel.getSenderAddress(),matchTransactionModel.getTag(),matchTransactionModel.getTxId());
            mCounterAck = 0;
            mCounterAckSucccess = 0;
            checkAllMessageReceive();
        } else {
            if (mCounterAck == 3) {
                mCounterAck = 0;
                mCounterAckSucccess = 0;
                mReceiveMessageCallback.receiveMessageCallback(SDKHelper.TAG_CAP_FAILED, SDKErrors.RECEIVE_MESSAGE_ERROR, "", "","","");
                checkAllMessageReceive();
            }
        }
    }

    /***
     * This method is used to check all messages downloading completion
     */
    private void checkAllMessageReceive(){
        if(matchTxList.size() > mLastMessageVal || matchTxList2.size() > mLastMessageVal || matchTxList3.size() > mLastMessageVal){
            mLastMessageVal++;
            mCrcMatched = 0;
            mCounterFileDownload = 0;
            mCounterFileDownNodes = 0;
            checkNodesAndCall(mLastMessageVal);
            SDKUtils.showLog("StartDownNext",""+ System.currentTimeMillis());
        }
        else{
            SDKUtils.showLog("NoDownNext",""+ System.currentTimeMillis());
            mReceiveMessageCallback.receiveMessageCallback(SDKHelper.TAG_NO_MESSAGE, SDKHelper.NO_NEWMESSAGE_AVAILABLE, "", "","","");
            new Preferences().saveData(mContext,"", SDKHelper.PREFERENCE_RECEIVE_NAME, SDKHelper.PREFERENCE_RECEIVE_KEY);
        }
        if(isByNotification){
            isByNotification = false;
        }
    }
}
