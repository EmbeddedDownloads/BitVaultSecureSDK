package bitmanagers;/**
 * Created by Vinod Singh on 5/5/2017.
 */

import android.content.Context;

import com.embedded.wallet.R;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import commons.SDKConstants;
import commons.SDKErrors;
import commons.SDKHelper;
import commons.SecureSDKException;
import controller.Preferences;
import datamover.DeleteFromPbc;
import datamover.DownloadData;
import datamover.DownloadMediaVaultFile;
import datamover.UploadData;
import datamover.UploadMediaVaultFile;
import iclasses.EncryptDataCallBack;
import iclasses.FeeCallback;
import iclasses.FeeFromCloudCallback;
import iclasses.ReceiveMessageCallBack;
import iclasses.SendMessageCallback;
import iclasses.TransactionBuilder;
import iclasses.mediavaultcallback.DeleteMediaFromPbcCallBack;
import iclasses.mediavaultcallback.MediaVaultEncryptDataCallBack;
import iclasses.mediavaultcallback.MediaVaultReceiveCallBack;
import iclasses.mediavaultcallback.SendMediaVaultCallback;
import messagemanager.FeeCalculate;
import messagemanager.FeeDescriptor;
import model.DataMoverModel;
import model.DataToPBCModel;
import model.MediaVaultDataToPBCModel;
import model.MessageFeeModel;
import securityencryption.EncryptDecryptData;
import securityencryption.SignatureECDSA;
import utils.SDKUtils;
import valle.btc.BTCUtils;

/**********************************************************************
 * VVDN Technologies
 * All rights reserved.
 * This software is the confidential and proprietary information of
 * VVDN Technologies. ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with VVDN.
 ************************************************************
 * ********/
public final class BitVaultDataManager extends BitVaultBaseManager implements EncryptDataCallBack,
        MediaVaultEncryptDataCallBack, FeeFromCloudCallback, TransactionBuilder {

    final private static String BTCpubKey = "0450863AD64A87AE8A2FE83C1AF1A8403CB53F53E486D8511DAD8A04887E5B23522CD" +
            "470243453A299FA9E77237716103ABC11A1DF38855ED6F2EE187E9C582BA6";
    private static BitVaultDataManager mSecureMessageSDK = null;
    private SendMessageCallback mSendMessageCallback = null;
    private FeeCallback mFeeCallback = null;
    private int mCharSize = 0;
    private long mMediaSize = 0l;
    private int mWalletId = 0;
    private boolean isBitCoinTransaction = false;
    private DataMoverModel mDataMoverModel = null;
    private String mDataSessionKey = "";
    private String mSendingcase = "";
    private Context mContext = null;
    private String mWebserverKey = "";
    private String mMessageType = "";
    private String mEotSenderAddress = "";
    private String mWalletType = "";

    private SendMediaVaultCallback sendMediaVaultCallback = null;

    /**
     * Method to get single instance of Secure Message SDK
     *
     * @return -- Instance of SecureMessanger class
     */
    public static BitVaultDataManager getSecureMessangerInstance() {
        if(mSecureMessageSDK == null) {
            return new BitVaultDataManager();
        }
        else {
            return mSecureMessageSDK;
        }
    }

    public BitVaultDataManager(){
        try {
            mContext = getContext();
        } catch (SecureSDKException e) {
            e.printStackTrace();
        }
    }

    /***
     * This method is used to get the fee from App store cloud and return fee to apps.
     *
     * @param charSize,mediaSize
     * @return
     *
     */
    public void getFee(int charSize, long mediaSize, FeeCallback feeCallback) {
        if (feeCallback != null) {
            if(mContext != null && mContext.getPackageName().equalsIgnoreCase(SDKHelper.SECURE_MESSAGE_PKG)){
                if (charSize > SDKConstants.MESSAGE_CHAR_COUNT_ZERO && charSize < SDKConstants.MESSAGE_CHAR_COUNT_LIMIT) {
                        callFeeApi(charSize,mediaSize, feeCallback);
                }
                else if(mediaSize > 0){
                    callFeeApi(charSize,mediaSize, feeCallback);
                }
                else {
                    feeCallback.getFeeCallBack(SDKHelper.TAG_CAP_FAILED, SDKErrors.SECURE_MESSAGE_SIZE, 0.0);
                }
            }
            else if(mContext != null && mContext.getPackageName().equalsIgnoreCase(SDKHelper.MEDIA_VAULT_PKG)){
                if (mediaSize > SDKConstants.MESSAGE_CHAR_COUNT_ZERO && mediaSize <= SDKConstants.MEDIA_LIMIT) {
                    callFeeApi(SDKConstants.MESSAGE_CHAR_COUNT_ZERO,mediaSize, feeCallback);
                }
                else {
                    feeCallback.getFeeCallBack(SDKHelper.TAG_CAP_FAILED, SDKErrors.SECURE_MEDIA_FILE_SIZE, 0.0);
                }
            }
            }
           else {
                SDKUtils.showToast(mContext, SDKErrors.SECURE_MESSAGE_CALBACK);
           }
    }


    /***
     * This method is used to send secure message to PBC server over BitVault to BitVault.
     *
     * @param walletId,message_Priority,messageToSend
     * @return
     *
     */
    public void sendMessage(int walletId, int charSize, long mediaSize, DataMoverModel dataMoverModelToSend,
                            SendMessageCallback sendMessageCallback, String txId, String webserverKey,
                            String messageType, String eotSenderAddress, String walletType) {
        if (sendMessageCallback != null) {
            this.mSendMessageCallback = sendMessageCallback;
            if (dataMoverModelToSend != null &&  dataMoverModelToSend.getMessage_tag() != null
                    && dataMoverModelToSend.getPbcId() != null && dataMoverModelToSend.getReceiverAddress() != null &&
                    dataMoverModelToSend.getMessageFiles() != null) {
                if (walletId > 0 || eotSenderAddress != null && !eotSenderAddress.isEmpty()) {
                    if (charSize > 0 || mediaSize > 0) {
                        try {
                            if (txId == null || txId.equalsIgnoreCase("")) {
                                if(walletType != null && !walletType.isEmpty()) {
                                    if (mContext != null) {
                                        isBitCoinTransaction = true;
                                        this.mCharSize = charSize;
                                        this.mMediaSize = mediaSize;
                                        this.mWalletId = walletId;
                                        this.mEotSenderAddress = eotSenderAddress;
                                        this.mDataMoverModel = dataMoverModelToSend;
                                        this.mWalletType = walletType;
                                        this.mSendingcase = SDKHelper.BITVAULT_TO_BITVAULT;
                                        this.mWebserverKey = webserverKey;
                                        this.mMessageType = messageType;
                                        new FeeDescriptor(mContext, this).getFeeFromAppCloud(mContext.getPackageName());
                                    } else {
                                        sendMessageCallback.sendMessageCallback(SDKHelper.TAG_CAP_FAILED, SDKErrors.CONTEXT_NULL, "", messageType, "");
                                    }
                                }else{
                                    sendMessageCallback.sendMessageCallback(SDKHelper.TAG_CAP_FAILED, SDKErrors.WALLET_TYPE_WRONG, "", messageType,"");
                                }
                            } else {
                               if(senderAddress(eotSenderAddress,walletId) != null){
                                   // this case is for send message to pbc in case of retry
                                   this.mMessageType = messageType;
                                   new EncryptDecryptData().encryptData(dataMoverModelToSend.getMessage_tag(), dataMoverModelToSend.getReceiverAddress(),
                                           BTCpubKey, txId, dataMoverModelToSend.getMessageFiles(), dataMoverModelToSend.getPbcId(),
                                           mContext.getPackageName(), senderAddress(),
                                           SDKHelper.BITVAULT_TO_BITVAULT, webserverKey, this);
                               }else{
                                   sendMessageCallback.sendMessageCallback(SDKHelper.TAG_CAP_FAILED, SDKErrors.SECURE_MESSAGE, "", messageType,"");
                               }

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            sendMessageCallback.sendMessageCallback(SDKHelper.TAG_CAP_FAILED, SDKErrors.SECURE_MESSAGE, "", messageType,"");
                        }
                    } else {
                        sendMessageCallback.sendMessageCallback(SDKHelper.TAG_CAP_FAILED, SDKErrors.SECURE_MESSAGE_FILE_SIZE, "", messageType,"");
                    }
                } else {
                    sendMessageCallback.sendMessageCallback(SDKHelper.TAG_CAP_FAILED, SDKErrors.SECURE_MESSAGE_WALLETID, "", messageType,"");
                }
            }
            else {
                sendMessageCallback.sendMessageCallback(SDKHelper.TAG_CAP_FAILED, SDKErrors.SECURE_MESSAGE, "",messageType,"");
            }
        } else {
            SDKUtils.showToast(mContext, SDKErrors.SECURE_MESSAGE_CALBACK);
        }
    }

    /***
     * This method is used to send secure message to PBC server over Desktop to Desktop
     *
     * @param walletId,messageToSend
     * @return
     *
     */
    public void sendMessage(int walletId, long mediaSize, DataMoverModel dataMoverModelToSend, String dataSesssionKey,
                            SendMessageCallback sendMessageCallback, String txId, String webserverKey, String messageType, String eotSenderAddress, String walletType) {
        if (sendMessageCallback != null) {
            this.mSendMessageCallback = sendMessageCallback;
            if (dataMoverModelToSend != null &&  dataMoverModelToSend.getMessage_tag() != null && dataMoverModelToSend.getPbcId() != null
                    && dataMoverModelToSend.getReceiverAddress() != null) {
                if(mediaSize > 0) {
                    if (walletId > 0 || eotSenderAddress != null && !eotSenderAddress.isEmpty()) {
                        // generate, encrypt and send message to Pbc Server for Desktop to Desktop case
                        try {
                            if (dataSesssionKey != null && !dataSesssionKey.equalsIgnoreCase("")) {
                                if (txId == null || txId.equalsIgnoreCase("")) {
                                    if(walletType != null && !walletType.isEmpty()) {
                                        if (mContext != null) {
                                            setValuesGlobal(true, 0, mediaSize, walletId, dataMoverModelToSend, dataSesssionKey
                                                    , SDKHelper.DESKTOP_TO_DESKTOP, webserverKey, messageType,eotSenderAddress,walletType);
                                            new FeeDescriptor(mContext, this).getFeeFromAppCloud(mContext.getPackageName());
                                        } else {
                                            sendMessageCallback.sendMessageCallback(SDKHelper.TAG_CAP_FAILED, SDKErrors.CONTEXT_NULL, "", messageType, "");
                                        }
                                    }else{
                                        sendMessageCallback.sendMessageCallback(SDKHelper.TAG_CAP_FAILED, SDKErrors.WALLET_TYPE_WRONG, "", messageType,"");
                                    }
                                } else {
                                    if(senderAddress(eotSenderAddress,walletId) != null) {
                                        // this case is for send message to pbc in case of retry
                                        this.mMessageType = messageType;
                                        new EncryptDecryptData().encryptData(dataMoverModelToSend.getMessage_tag(),
                                                dataMoverModelToSend.getReceiverAddress(), BTCpubKey, txId, dataSesssionKey,
                                                dataMoverModelToSend.getPbcId(), mContext.getPackageName(),
                                                senderAddress(),
                                                SDKHelper.DESKTOP_TO_DESKTOP, webserverKey, this);
                                    }else{
                                        sendMessageCallback.sendMessageCallback(SDKHelper.TAG_CAP_FAILED, SDKErrors.SECURE_MESSAGE, "", messageType,"");
                                    }
                                }
                            } else {
                                sendMessageCallback.sendMessageCallback(SDKHelper.TAG_CAP_FAILED, SDKErrors.SECURE_MESSAGE_SESSIONkEY, "",messageType,"");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            sendMessageCallback.sendMessageCallback(SDKHelper.TAG_CAP_FAILED, SDKErrors.SECURE_MESSAGE, "",messageType,"");
                        }

                    } else {
                        sendMessageCallback.sendMessageCallback(SDKHelper.TAG_CAP_FAILED, SDKErrors.SECURE_MESSAGE_WALLETID, "",messageType,"");
                    }

                }
                else {
                    sendMessageCallback.sendMessageCallback(SDKHelper.TAG_CAP_FAILED, SDKErrors.SECURE_MESSAGE_FILE_SIZE, "",messageType,"");
                }

            } else {
                sendMessageCallback.sendMessageCallback(SDKHelper.TAG_CAP_FAILED, SDKErrors.SECURE_MESSAGE, "",messageType,"");
            }
        } else {
            SDKUtils.showToast(mContext, SDKErrors.SECURE_MESSAGE_CALBACK);
        }

    }

    /***
     * This method is used to send secure message to PBC server over BitVault to Desktop
     *
     * @param walletId,,messageToSend
     * @return
     *
     */
    public void sendMessage (int walletId, long mediaSize, DataMoverModel dataMoverModelToSend,
                             SendMessageCallback sendMessageCallback, String txId, String webserverKey, String messageType){
        if (sendMessageCallback != null) {
            this.mSendMessageCallback = sendMessageCallback;
            if (dataMoverModelToSend != null &&  dataMoverModelToSend.getMessage_tag() != null && dataMoverModelToSend.getPbcId() != null
                    && dataMoverModelToSend.getReceiverAddress() != null && dataMoverModelToSend.getMessageFiles() != null) {
                if(mediaSize > 0) {
                    if (walletId > 0) {
                        if (dataMoverModelToSend.getMessageFiles() != null && !dataMoverModelToSend.getMessageFiles().equalsIgnoreCase("")) {
                            // generate, encrypt and send message to Pbc Server for BitVault to Desktop case
                            try {
                                if (txId == null || txId.equalsIgnoreCase("")) {
                                    if (mContext != null) {
                                        setValuesGlobal(true,0, mediaSize, walletId, dataMoverModelToSend, ""
                                                , SDKHelper.BITVAULT_TO_DESKTOP,webserverKey,messageType,"","");
                                        new FeeDescriptor(mContext, this).getFeeFromAppCloud(mContext.getPackageName());
                                    } else {
                                        sendMessageCallback.sendMessageCallback(SDKHelper.TAG_CAP_FAILED, SDKErrors.CONTEXT_NULL, "",messageType,"");
                                    }
                                } else {
                                    this.mMessageType = messageType;
                                    new EncryptDecryptData().encryptData(dataMoverModelToSend.getMessage_tag(), dataMoverModelToSend.getReceiverAddress(),BTCpubKey, txId,
                                            dataMoverModelToSend.getMessageFiles(), dataMoverModelToSend.getPbcId(), mContext.getPackageName(),
                                            BitVaultWalletManager.getWalletInstance().getWalletAddress(walletId), SDKHelper.BITVAULT_TO_DESKTOP,webserverKey, this);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                sendMessageCallback.sendMessageCallback(SDKHelper.TAG_CAP_FAILED, SDKErrors.SECURE_MESSAGE, "",messageType,"");
                            }
                        }
                    } else {
                        sendMessageCallback.sendMessageCallback(SDKHelper.TAG_CAP_FAILED, SDKErrors.SECURE_MESSAGE_WALLETID, "",messageType,"");
                    }

                }
                else {
                    sendMessageCallback.sendMessageCallback(SDKHelper.TAG_CAP_FAILED, SDKErrors.SECURE_MESSAGE_FILE_SIZE, "",messageType,"");
                }

            } else {
                sendMessageCallback.sendMessageCallback(SDKHelper.TAG_CAP_FAILED, SDKErrors.SECURE_MESSAGE, "",messageType,"");
            }
        } else {
            SDKUtils.showToast(mContext, SDKErrors.SECURE_MESSAGE_CALBACK);
        }

    }
    /***
     * This method is used to receive new incoming message using Notification
     *
     * @param tag,walletAdress,hashTxId
     * @return
     *
     */
    public void receiveMessage(String tag, String receiverAddress, String hashTxId, ReceiveMessageCallBack receiveMessageCallBack) {
        if (receiveMessageCallBack != null) {
            if (tag != null && !tag.equalsIgnoreCase("") && receiverAddress != null && !receiverAddress.equalsIgnoreCase("") && hashTxId != null && !hashTxId.equalsIgnoreCase("")) {
                try {
                    JSONObject requestInput = new JSONObject();
                    requestInput.put(SDKHelper.DATA_TAG, tag);
                    requestInput.put(SDKHelper.TRANSACTION_ID, hashTxId);
                    requestInput.put(SDKHelper.TAG_RECEIVER, receiverAddress);
                        if (mContext != null) {
                            new Preferences().saveData(mContext, SDKHelper.PREFERENCES_CHECK, SDKHelper.PREFERENCE_RECEIVE_NAME, SDKHelper.PREFERENCE_RECEIVE_KEY);
                            new DownloadData(mContext, receiveMessageCallBack).downloadMessage(requestInput);

                        } else {
                            receiveMessageCallBack.receiveMessageCallback(SDKHelper.TAG_CAP_FAILED, SDKErrors.CONTEXT_NULL, "", "","","");
                        }
                } catch (JSONException e) {
                    e.printStackTrace();
                    receiveMessageCallBack.receiveMessageCallback(SDKHelper.TAG_CAP_FAILED, SDKErrors.SECURE_MESSAGE_RECEIVE_INPUT, "", "","","");
                }
            } else {
                receiveMessageCallBack.receiveMessageCallback(SDKHelper.TAG_CAP_FAILED, SDKErrors.SECURE_MESSAGE_RECEIVE_INPUT, "", "","","");
            }
        } else {
                SDKUtils.showToast(mContext, SDKErrors.SECURE_MESSAGE_CALBACK);
        }
    }

    /***
     * This method is used to receive new incoming message using pull Request
     *
     * @param walletId,receiveMessageCallBack
     * @return
     *
     */
    public void receiveMessage(List<Integer> walletId,String eotReceiverAdd, ReceiveMessageCallBack receiveMessageCallBack) {
        if(mContext != null && new Preferences().getData(mContext, SDKHelper.PREFERENCE_RECEIVE_NAME, SDKHelper.PREFERENCE_RECEIVE_KEY).equals("")) {
            new Preferences().saveData(mContext, SDKHelper.PREFERENCES_CHECK, SDKHelper.PREFERENCE_RECEIVE_NAME, SDKHelper.PREFERENCE_RECEIVE_KEY);
            if (receiveMessageCallBack != null) {
                int walletSize = walletId.size();
                if (walletId != null && walletSize >= 0) {
                    try {
                        JSONArray jsonArray = new JSONArray();
                        for (int i = 0; i < walletSize; i++) {
                            jsonArray.put(BitVaultWalletManager.getWalletInstance().getWalletAddress(walletId.get(i)));
                        }
                        jsonArray.put(eotReceiverAdd);
                        JSONObject listWallet = new JSONObject();
                        listWallet.put(SDKHelper.TAG_RECEIVER, jsonArray);
                        if (mContext != null) {
                            SDKUtils.showLog("WalletList", "" + listWallet);
                            new DownloadData(mContext, receiveMessageCallBack).downloadMessage(listWallet,walletId);
                        } else {
                            new Preferences().saveData(mContext,"", SDKHelper.PREFERENCE_RECEIVE_NAME, SDKHelper.PREFERENCE_RECEIVE_KEY);
                            receiveMessageCallBack.receiveMessageCallback(SDKHelper.TAG_CAP_FAILED, SDKErrors.CONTEXT_NULL, "", "","","");
                        }
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        new Preferences().saveData(mContext,"", SDKHelper.PREFERENCE_RECEIVE_NAME, SDKHelper.PREFERENCE_RECEIVE_KEY);
                        receiveMessageCallBack.receiveMessageCallback(SDKHelper.TAG_CAP_FAILED, SDKErrors.SECURE_MESSAGE_RECEIVE_INPUT, "", "","","");
                    } catch (SecureSDKException e) {
                        e.printStackTrace();
                        new Preferences().saveData(mContext,"", SDKHelper.PREFERENCE_RECEIVE_NAME, SDKHelper.PREFERENCE_RECEIVE_KEY);
                        receiveMessageCallBack.receiveMessageCallback(SDKHelper.TAG_CAP_FAILED, SDKErrors.SECURE_MESSAGE_RECEIVE_INPUT, "", "","","");
                    }
                } else {
                    new Preferences().saveData(mContext,"", SDKHelper.PREFERENCE_RECEIVE_NAME, SDKHelper.PREFERENCE_RECEIVE_KEY);
                    receiveMessageCallBack.receiveMessageCallback(SDKHelper.TAG_CAP_FAILED, SDKErrors.SECURE_MESSAGE_RECEIVE_INPUT, "", "","","");
                }
            } else {
                new Preferences().saveData(mContext,"", SDKHelper.PREFERENCE_RECEIVE_NAME, SDKHelper.PREFERENCE_RECEIVE_KEY);
                SDKUtils.showToast(mContext, SDKErrors.SECURE_MESSAGE_CALBACK);
            }
        }
    }



    /***
     * This method is used to upload media file to PBC to make it a secure media file.
     *
     * @param walletId,txId,messageToSend
     * @return
     */
    public void uploadMediaVaultFile(int walletId, long mediaSize, DataMoverModel dataMoverModelToSend,
                                     SendMediaVaultCallback sendMediaVaultCallback, String txId, String webserverKey,
                                     String eotSenderAddress, String walletType) {
        if (sendMediaVaultCallback != null) {
            this.sendMediaVaultCallback = sendMediaVaultCallback;
            if ( dataMoverModelToSend != null && dataMoverModelToSend.getMessage_tag() != null && dataMoverModelToSend.getMessageFiles() != null
                    && dataMoverModelToSend.getPbcId() != null && dataMoverModelToSend.getReceiverAddress() != null) {
                if (walletId > 0  || eotSenderAddress != null && !eotSenderAddress.isEmpty()) {
                    if (mediaSize > 0) {
                        try {
                            if (txId == null || txId.equalsIgnoreCase("")) {
                                if (mContext != null) {
                                    isBitCoinTransaction = true;
                                    this.mMediaSize = mediaSize;
                                    this.mWalletId = walletId;
                                    this.mDataMoverModel = dataMoverModelToSend;
                                    this.mWalletType = walletType;
                                    this.mEotSenderAddress = eotSenderAddress;
                                    this.mWebserverKey = webserverKey;
                                    this.mSendingcase =  SDKHelper.MEDIA_VAULT;
                                    new FeeDescriptor(mContext, this).getFeeFromAppCloud(mContext.getPackageName());
                                } else {
                                    sendMediaVaultCallback.sendMediaVaultCallback(SDKHelper.TAG_CAP_FAILED, SDKErrors.CONTEXT_NULL, null);
                                }
                            } else {
                                // this case is for send file  to pbc in case of retry
                                if(senderAddress(eotSenderAddress,walletId) != null){
                                new EncryptDecryptData().encryptMedia(dataMoverModelToSend.getMessage_tag(), dataMoverModelToSend.getReceiverAddress(),
                                        BTCpubKey, txId, dataMoverModelToSend.getMessageFiles(), dataMoverModelToSend.getPbcId(),
                                        mContext.getPackageName(), BitVaultWalletManager.getWalletInstance().getWalletAddress(walletId),
                                        webserverKey, this);
                                }else{
                                    sendMediaVaultCallback.sendMediaVaultCallback(SDKHelper.TAG_CAP_FAILED, SDKErrors.MEDIA_VAULT_WALLETID,null);
                                }

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            sendMediaVaultCallback.sendMediaVaultCallback(SDKHelper.TAG_CAP_FAILED, SDKErrors.MEDIA_VAULT_EMPTY_MESSAGE, null);
                        }
                    } else {
                        sendMediaVaultCallback.sendMediaVaultCallback(SDKHelper.TAG_CAP_FAILED, SDKErrors.SECURE_MEDIA_FILE_SIZE, null);

                    }
                } else {
                    sendMediaVaultCallback.sendMediaVaultCallback(SDKHelper.TAG_CAP_FAILED, SDKErrors.MEDIA_VAULT_WALLETID, null);
                }
            } else {
                sendMediaVaultCallback.sendMediaVaultCallback(SDKHelper.TAG_CAP_FAILED, SDKErrors.MEDIA_VAULT_EMPTY_MESSAGE, null);
            }
        } else {
            SDKUtils.showToast(mContext, SDKErrors.MEDIA_VAULT_CALlBACK);
        }
    }
    /***
     * This method is used to download secure media from PBC
     *
     * @param fileUniqueId,tag,walletAddress,receiveMessageCallBack
     * @return
     */
    public void receiveSecureFile(String fileUniqueId, String tag,String walletAddress,String encryptedTxId,String encryptedFileKey,MediaVaultReceiveCallBack mediaVaultReceiveCallBack) {
        if (mContext != null) {
            if (mediaVaultReceiveCallBack != null) {
                if (fileUniqueId != null && !fileUniqueId.equalsIgnoreCase("") && tag != null && !tag.equalsIgnoreCase("")
                        && walletAddress != null && !walletAddress.equalsIgnoreCase("") && encryptedTxId != null && !encryptedTxId.equalsIgnoreCase("")
                        && encryptedFileKey != null && !encryptedFileKey.equalsIgnoreCase("")) {
                    try {
                        if (mContext != null) {
                            JSONObject obj = new JSONObject();
                            obj.put(SDKHelper.MEDIA_VAULT_FILE_ID,fileUniqueId);
                            obj.put(SDKHelper.DATA_TAG,tag);
                            obj.put(SDKHelper.MEDIA_VAULT_WALLET_ADDRESS,walletAddress);
                            obj.put(SDKHelper.PUB_KEY,BTCpubKey);
                            String signKEY = new SignatureECDSA().signBytesVal(fileUniqueId + "|$$|" + tag + "|$$|" + walletAddress,BTCpubKey);
                            if(signKEY !=  null){
                                obj.put(SDKHelper.MEDIAVAULT_SIGNNATURE,signKEY);
                                SDKUtils.showLog("receiveInput", obj.toString());
                                new DownloadMediaVaultFile(mContext, mediaVaultReceiveCallBack).downloadMessage(obj,encryptedTxId,encryptedFileKey);
                            }
                            else{
                                mediaVaultReceiveCallBack.receiveMediaVaultCallback(SDKHelper.TAG_CAP_FAILED, SDKErrors.MEDIA_VAULT_RECEIVE_INPUT, "", null);
                            }

                        } else {
                            mediaVaultReceiveCallBack.receiveMediaVaultCallback(SDKHelper.TAG_CAP_FAILED, SDKErrors.CONTEXT_NULL, "", null);
                        }
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        mediaVaultReceiveCallBack.receiveMediaVaultCallback(SDKHelper.TAG_CAP_FAILED, SDKErrors.MEDIA_VAULT_RECEIVE_INPUT, "", null);
                    }
                } else {
                    mediaVaultReceiveCallBack.receiveMediaVaultCallback(SDKHelper.TAG_CAP_FAILED, SDKErrors.MEDIA_VAULT_RECEIVE_INPUT, "", null);
                }
            } else {
                SDKUtils.showToast(mContext, SDKErrors.MEDIA_VAULT_CALlBACK);
            }
        }
        else{
            mediaVaultReceiveCallBack.receiveMediaVaultCallback(SDKHelper.TAG_CAP_FAILED, SDKErrors.CONTEXT_NULL, "", null);
        }
    }

    /**
     * Method to delete secure media file from PBC
     *
     */

    public void deleteMediaFileFromPbc(String fileUniqueId, String tag,String walletAddress,String crc, DeleteMediaFromPbcCallBack deleteMediaFromPbcCallBack){

        if (deleteMediaFromPbcCallBack != null) {
            if(fileUniqueId != null && !fileUniqueId.equalsIgnoreCase("") && tag != null && !tag.equalsIgnoreCase("")
                    && walletAddress != null && !walletAddress.equalsIgnoreCase("") && crc != null &&  !crc.equalsIgnoreCase("")){
                    try {
                        if (mContext != null) {
                            JSONObject obj = new JSONObject();
                            obj.put(SDKHelper.MEDIA_VAULT_FILE_ID,fileUniqueId);
                            obj.put(SDKHelper.DATA_TAG,tag);
                            obj.put(SDKHelper.MEDIA_VAULT_WALLET_ADDRESS,walletAddress);
                            obj.put(SDKHelper.CRC,crc);
                            obj.put(SDKHelper.PUB_KEY,BTCpubKey);
                            String signKEY = new SignatureECDSA().signBytesVal(fileUniqueId + "|$$|" + tag + "|$$|" + walletAddress,BTCpubKey);
                            if(signKEY !=  null){
                                obj.put(SDKHelper.MEDIAVAULT_SIGNNATURE,signKEY);
                                new DeleteFromPbc(mContext).deleteMediaFromPbc(obj,fileUniqueId,deleteMediaFromPbcCallBack);
                            }
                            else{
                                deleteMediaFromPbcCallBack.deletedFileFromPbc(SDKHelper.TAG_CAP_FAILED, SDKErrors.MEDIA_VAULT_RECEIVE_INPUT,fileUniqueId);
                            }

                        } else {
                            deleteMediaFromPbcCallBack.deletedFileFromPbc(SDKHelper.TAG_CAP_FAILED, SDKErrors.CONTEXT_NULL,fileUniqueId);
                        }
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        deleteMediaFromPbcCallBack.deletedFileFromPbc(SDKHelper.TAG_CAP_FAILED, SDKErrors.MEDIA_VAULT_RECEIVE_INPUT,fileUniqueId);
                    }
            }
            else{
                deleteMediaFromPbcCallBack.deletedFileFromPbc(SDKHelper.TAG_CAP_FAILED , SDKErrors.MEDIA_VAULT_WRONG_FILEINPUT,fileUniqueId);
            }
        }
        else{
            SDKUtils.showToast(mContext, SDKErrors.MEDIA_VAULT_CALlBACK);
        }
    }
    @Override
    public void encryptDataResponse(DataToPBCModel dataToPBCModel, String msgCase) {
        try {
            if (dataToPBCModel != null && msgCase != null) {
                // call data transfer api
                new UploadData(mContext, mSendMessageCallback).sendMessageToPbc(dataToPBCModel,mMessageType);
                mSendMessageCallback =null;

            } else {
                mSendMessageCallback.sendMessageCallback(SDKHelper.TAG_CAP_FAILED, SDKErrors.SECURE_MESSAGE_ENCRYPTION_ERROR, dataToPBCModel.getTxId(),mMessageType,"");
                mSendMessageCallback =null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            mSendMessageCallback.sendMessageCallback(SDKHelper.TAG_CAP_FAILED, SDKErrors.SECURE_MESSAGE_ENCRYPTION_ERROR, dataToPBCModel.getTxId(),mMessageType,"");
            mSendMessageCallback =null;
        }

    }

    @Override
    public void encryptMediaResponse(MediaVaultDataToPBCModel dataToPBCModel) {
        try {
            if (dataToPBCModel != null) {
                new UploadMediaVaultFile(mContext, sendMediaVaultCallback).sendMediaFileToPbc(dataToPBCModel);
                sendMediaVaultCallback = null;
            } else {
                sendMediaVaultCallback.sendMediaVaultCallback(SDKHelper.TAG_CAP_FAILED, SDKErrors.MEDIA_VAULT_ENCRYPTION_ERROR, dataToPBCModel);
                sendMediaVaultCallback = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendMediaVaultCallback.sendMediaVaultCallback(SDKHelper.TAG_CAP_FAILED, SDKErrors.MEDIA_VAULT_ENCRYPTION_ERROR, dataToPBCModel);
            sendMediaVaultCallback = null;
        }

    }

    @Override
    public void onResponseFeeCloud(String resultFee) {
        if (isBitCoinTransaction) {
            try {
                if (resultFee != null && !resultFee.equalsIgnoreCase("")) {
                    MessageFeeModel messageFeeModel = new Gson().fromJson(resultFee, MessageFeeModel.class);
                    if(messageFeeModel != null) {
                        double finalMessageFee = new FeeCalculate().calculateFee(mCharSize, mMediaSize, messageFeeModel);
                        if (finalMessageFee >= 0) {
                            try {
                                if (mWalletType.equalsIgnoreCase(mContext.getResources().getString(R.string.wallet_eot))) {

                                    // call Bitcoin payment method for message sending  charge
                                    if (SDKUtils.isNetworkAvailable(mContext)) {
                                        EOTWalletManager.getEOTWalletInstance().transferEot(mDataMoverModel.getReceiverAddress()
                                                , BTCUtils.convertDecimalFormatPattern(finalMessageFee),
                                                String.valueOf(messageFeeModel.getFees().getReceiver_fee()),this);
                                    } else {
                                        sendCallbackFee(SDKHelper.TAG_CAP_FAILED, mContext.getResources().getString(R.string.network_error), "", mMessageType);
                                    }

                                }else {
                                    // call Bitcoin payment method for message sending  charge
                                    if (SDKUtils.isNetworkAvailable(mContext)) {
                                        BitVaultWalletManager.getWalletInstance().sendBitCoins(mWalletId, mDataMoverModel.getReceiverAddress(),
                                                BTCUtils.parseValue(String.valueOf(messageFeeModel.getFees().getReceiver_fee()))
                                                , BTCUtils.parseValue(BTCUtils.convertDecimalFormatPattern(finalMessageFee)), this);
                                    } else {
                                        sendCallbackFee(SDKHelper.TAG_CAP_FAILED, mContext.getResources().getString(R.string.network_error), "", mMessageType);
                                    }
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                                sendCallbackFee(SDKHelper.TAG_CAP_FAILED, SDKHelper.UNABLE_PROCESS_FEE, "",mMessageType);
                            }
                        } else {
                            sendCallbackFee(SDKHelper.TAG_CAP_FAILED, SDKHelper.UNABLE_PROCESS_FEE, "",mMessageType);
                        }
                    }
                    else{
                        sendCallbackFee(SDKHelper.TAG_CAP_FAILED, SDKHelper.UNABLE_PROCESS_FEE,"",mMessageType);
                    }
                } else {
                    sendCallbackFee(SDKHelper.TAG_CAP_FAILED, SDKHelper.UNABLE_PROCESS_FEE,"",mMessageType);
                }
            } catch (Exception e) {
                e.printStackTrace();
                sendCallbackFee(SDKHelper.TAG_CAP_FAILED, SDKHelper.UNABLE_PROCESS_FEE,"",mMessageType);
            }
        }
        else{
            try {
                if (resultFee != null && !resultFee.equalsIgnoreCase("")) {
                    MessageFeeModel messageFeeModel = new Gson().fromJson(resultFee, MessageFeeModel.class);
                    if(messageFeeModel != null) {
                        double finalMessageFee = new FeeCalculate().calculateFee(mCharSize, mMediaSize, messageFeeModel);
                        mFeeCallback.getFeeCallBack(SDKHelper.TAG_CAP_OK, SDKHelper.GOT_MESSAGE, finalMessageFee + Double.parseDouble(messageFeeModel.getFees().getReceiver_fee()));
                    }
                    else{
                        mFeeCallback.getFeeCallBack(SDKHelper.TAG_CAP_FAILED, SDKHelper.UNABLE_PROCESS_FEE, 0.0);
                    }
                } else {
                    mFeeCallback.getFeeCallBack(SDKHelper.TAG_CAP_FAILED, SDKHelper.UNABLE_PROCESS_FEE, 0.0);
                }
            } catch (Exception e) {
                e.printStackTrace();
                mFeeCallback.getFeeCallBack(SDKHelper.TAG_CAP_FAILED, SDKHelper.UNABLE_PROCESS_FEE, 0.0);
            }
        }


    }

    @Override
    public void RequestedTransaction(String mTransaction) {

    }

    @Override
    public void TransactionId(String mTxId) {
            try {
                SDKUtils.showLog(SDKHelper.TAG_UPLOAD_DATA,mTxId);
                if (mTxId != null && !mTxId.equalsIgnoreCase("")) {
                    if(senderAddress() != null) {
                        if (mSendingcase.equalsIgnoreCase(SDKHelper.BITVAULT_TO_BITVAULT)) {
                            new EncryptDecryptData().encryptData(mDataMoverModel.getMessage_tag(), mDataMoverModel.getReceiverAddress(),
                                    BTCpubKey, mTxId, mDataMoverModel.getMessageFiles(), mDataMoverModel.getPbcId(),
                                    mContext.getPackageName(), senderAddress(), SDKHelper.BITVAULT_TO_BITVAULT, mWebserverKey, this);
                        } else if (mSendingcase.equalsIgnoreCase(SDKHelper.DESKTOP_TO_DESKTOP)) {
                            new EncryptDecryptData().encryptData(mDataMoverModel.getMessage_tag(),
                                    mDataMoverModel.getReceiverAddress(), BTCpubKey, mTxId, mDataSessionKey,
                                    mDataMoverModel.getPbcId(), mContext.getPackageName(), senderAddress(), SDKHelper.DESKTOP_TO_DESKTOP, mWebserverKey, this);
                        } else if (mSendingcase.equalsIgnoreCase(SDKHelper.BITVAULT_TO_DESKTOP)) {
                            new EncryptDecryptData().encryptData(mDataMoverModel.getMessage_tag(), mDataMoverModel.getReceiverAddress(), BTCpubKey, mTxId,
                                    mDataMoverModel.getMessageFiles(), mDataMoverModel.getPbcId(),
                                    mContext.getPackageName(),senderAddress(), SDKHelper.BITVAULT_TO_DESKTOP, mWebserverKey, this);
                        } else if (mSendingcase.equalsIgnoreCase(SDKHelper.MEDIA_VAULT)) {
                            new EncryptDecryptData().encryptMedia(mDataMoverModel.getMessage_tag(), mDataMoverModel.getReceiverAddress(),
                                    BTCpubKey, mTxId, mDataMoverModel.getMessageFiles(), mDataMoverModel.getPbcId(),
                                    mContext.getPackageName(),senderAddress(), mWebserverKey,
                                    this);
                        }
                    }else{
                        sendCallbackTransaction(SDKHelper.TAG_CAP_FAILED, SDKErrors.SECURE_MESSAGE, "", mMessageType);
                    }
                } else {
                    sendCallbackTransaction(SDKHelper.TAG_CAP_FAILED, SDKErrors.BITCOIN_TRANSACTION_FAILED, "", mMessageType);
                }
            } catch (Exception e) {
                e.printStackTrace();
                sendCallbackTransaction(SDKHelper.TAG_CAP_FAILED, SDKErrors.BITCOIN_TRANSACTION_FAILED, "", mMessageType);
            }
    }

    @Override
    public void TransactionFailed(String error) {
        sendCallbackTransaction(SDKHelper.TAG_CAP_FAILED, error, "", mMessageType);
    }

    /***
     * This method is used to save data in the global variables to user later in callbacks.
     *
     * @param isBitcoinTransaction,charSize,mediaSize,walletId,dataMoverModelToSend,msgCases
     * @return
     *
     */

    private void setValuesGlobal(boolean isBitcoinTransaction, int charSize, long mediaSize, int walletId,
                                 DataMoverModel dataMoverModelToSend, String dataSesssionKey,
                                 String msgCases, String webserverKey, String messageType, String eotSenderAddress, String walletType) {
        isBitCoinTransaction = isBitcoinTransaction;
        this.mCharSize = charSize;
        this.mMediaSize = mediaSize;
        this.mWalletId = walletId;
        this.mDataMoverModel = dataMoverModelToSend;
        this.mDataSessionKey = dataSesssionKey;
        this.mSendingcase = msgCases;
        this.mWebserverKey = webserverKey;
        this.mMessageType = messageType;
        this.mEotSenderAddress = eotSenderAddress;
        this.mWalletType = walletType;
    }

    /***
     * This method is used to call and process fee
     *
     * @param charSize,mediaSize,feeCallback
     * @return
     *
     */
    private void callFeeApi(int charSize, long mediaSize, FeeCallback feeCallback){
        this.mFeeCallback = feeCallback;
        this.mCharSize = charSize;
        this.mMediaSize = mediaSize;
        try {
            if (mContext != null) {
                isBitCoinTransaction = false;
                new FeeDescriptor(mContext, this).getFeeFromAppCloud(mContext.getPackageName());
            } else {
                feeCallback.getFeeCallBack(SDKHelper.TAG_CAP_FAILED, SDKErrors.CONTEXT_NULL, 0.0);
            }
        } catch (Exception e) {
            e.printStackTrace();
            feeCallback.getFeeCallBack(SDKHelper.TAG_CAP_FAILED, SDKHelper.UNABLE_PROCESS_FEE, 0.0);
        }
    }

    /***
     * This method is used to send fee process failed callBack
     *
     * @param tagCapFailed,unableProcessFee,s,messageType
     * @return
     *
     */
    private void sendCallbackFee(String tagCapFailed, String unableProcessFee, String s, String messageType){
        if(mContext != null && mContext.getPackageName().equalsIgnoreCase(SDKHelper.SECURE_MESSAGE_PKG)){
            mSendMessageCallback.sendMessageCallback(tagCapFailed, unableProcessFee, s, messageType,"");
        }
        else if(mContext != null && mContext.getPackageName().equalsIgnoreCase(SDKHelper.MEDIA_VAULT_PKG)){
            sendMediaVaultCallback.sendMediaVaultCallback(tagCapFailed, unableProcessFee, null);
        }
    }

    /***
     * This method is used to send transaction failed callBack
     *
     * @param tagCapFailed,error,s,messageType
     * @return
     *
     */
    private void sendCallbackTransaction(String tagCapFailed, String error, String s, String mMessageType){
        if(mContext != null && mContext.getPackageName().equalsIgnoreCase(SDKHelper.SECURE_MESSAGE_PKG)){
            if(mSendMessageCallback != null) {
                mSendMessageCallback.sendMessageCallback(tagCapFailed, error, "", mMessageType,"");
                mSendMessageCallback = null;

            }
            else if(mContext != null && mContext.getPackageName().equalsIgnoreCase(SDKHelper.MEDIA_VAULT_PKG)){
                if (sendMediaVaultCallback != null) {
                    sendMediaVaultCallback.sendMediaVaultCallback(tagCapFailed, error, null);
                    sendMediaVaultCallback = null;
                }
            }
        }
    }

    /***
     * This method is used to set sender address
     *
     */
    private String senderAddress() {
        String senderAddress = "";
        if (mEotSenderAddress == null || mEotSenderAddress.equals("")) {
            try {
                senderAddress = BitVaultWalletManager.getWalletInstance().getWalletAddress(mWalletId);
            } catch (SecureSDKException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            senderAddress = mEotSenderAddress;
        }
        return senderAddress;
    }

        /***
         * This method is used to set sender address in case of Retry
         *
         */
     private String senderAddress(String eotSenderAddress,int walletId){
        String senderAddress = "";
        if(eotSenderAddress == null || eotSenderAddress.equals("")){
            try {
                senderAddress =  BitVaultWalletManager.getWalletInstance().getWalletAddress(walletId);
            } catch (SecureSDKException e) {
                e.printStackTrace();
                return  null;
            }
        }else{
            senderAddress = eotSenderAddress;
        }
        return senderAddress;
    }
}
