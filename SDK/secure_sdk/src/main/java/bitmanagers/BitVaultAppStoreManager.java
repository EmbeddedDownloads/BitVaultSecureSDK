package bitmanagers;

import android.content.Context;

import com.android.volley.Request;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import commons.SDKErrors;
import commons.SDKHelper;
import datamover.AppStoreDataTransfer;
import iclasses.mediavaultcallback.DeleteMediaFromAppStore;
import iclasses.mediavaultcallback.MediaFileDetailsOperationCallBack;
import iclasses.mediavaultcallback.MediaVaultGetFileInfo;
import utils.SDKUtils;

/**
 * Created by Vinod Singh on 4/10/17.
 */

public class BitVaultAppStoreManager extends BitVaultBaseManager {

    private static BitVaultAppStoreManager mBitVaultAppStoreManager = null;
    private Context mContext = null;


    /**
     * Method to get single instance of AppStore Manager
     *
     * @return -- Instance of BitVaultAppStoreManager class
     */
    public static BitVaultAppStoreManager getAppStoreManagerInstance() {
        if(mBitVaultAppStoreManager == null) {
            return new BitVaultAppStoreManager();
        }
        else {
            return mBitVaultAppStoreManager;
        }
    }

    public BitVaultAppStoreManager(){
        try {
            mContext = getContext();
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to save secure file details on AppStore Cloud
     *
     */

    public void saveFileDetails(String encFileKey, String encTxnID, String filename,
                                String id, String type, String walletAddress, MediaFileDetailsOperationCallBack mediaFileDetailsOperationCallBack){
        if (mediaFileDetailsOperationCallBack != null) {
            if(encFileKey != null && !encFileKey.isEmpty() && encTxnID != null && !encTxnID.isEmpty() &&
                    filename != null && !filename.isEmpty() && id != null && !id.isEmpty() && type != null && !type.isEmpty() && walletAddress != null && !walletAddress.isEmpty()){
                if(mContext !=  null) {
                    try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put(SDKHelper.APPID, mContext.getPackageName());
                        jsonObject.put(SDKHelper.MEDIADVAULT_ENCFILE_KEY, encFileKey);
                        jsonObject.put(SDKHelper.MEDIADVAULT_ENCTXNID, encTxnID);
                        jsonObject.put(SDKHelper.MEDIADVAULT_FILEANME, filename);
                        jsonObject.put(SDKHelper.MEDIA_VAULT_FILE_ID, id);
                        jsonObject.put(SDKHelper.MEDIADVAULT_TYPE, type);
                        jsonObject.put(SDKHelper.MEDIA_VAULT_WALLET_ADDRESS, walletAddress);
                        new AppStoreDataTransfer(mContext).operationsFileDetailsToAppStore(SDKHelper.BASE_URL_APPSTORE,jsonObject,id, Request.Method.POST,mediaFileDetailsOperationCallBack);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        mediaFileDetailsOperationCallBack.mediaFileDetailsOperationCallBack(SDKHelper.TAG_CAP_FAILED , SDKErrors.MEDIA_VAULT_WRONG_FILEINPUT,id);
                    }
                }
                else{
                    mediaFileDetailsOperationCallBack.mediaFileDetailsOperationCallBack(SDKHelper.TAG_CAP_FAILED, SDKErrors.CONTEXT_NULL,id);
                }
            }
            else{
                mediaFileDetailsOperationCallBack.mediaFileDetailsOperationCallBack(SDKHelper.TAG_CAP_FAILED , SDKErrors.MEDIA_VAULT_WRONG_FILEINPUT,id);
                }
        }
        else{
            SDKUtils.showToast(mContext, SDKErrors.MEDIA_VAULT_CALlBACK);
        }
    }

    /**
     * Method to delete secure file details from AppStore Cloud
     *
     */


    public void deleteFileDetails(String id, DeleteMediaFromAppStore deleteMediaFromAppStore){

        if (deleteMediaFromAppStore != null) {
            if(id != null && !id.isEmpty()){
                if(mContext !=  null) {
                    try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put(SDKHelper.APPID,mContext.getPackageName());
                        jsonObject.put(SDKHelper.MEDIA_VAULT_FILE_ID,id);
                        new AppStoreDataTransfer(mContext).deleteFileDetailsFromAppStore(SDKHelper.DELETE_URL_APPSTORE,jsonObject,id, Request.Method.POST,deleteMediaFromAppStore);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        deleteMediaFromAppStore.deleteMediaFromAppstoreCallBack(SDKHelper.TAG_CAP_FAILED , SDKErrors.MEDIA_VAULT_WRONG_FILEINPUT,id);
                    }
                }
                else{
                    deleteMediaFromAppStore.deleteMediaFromAppstoreCallBack(SDKHelper.TAG_CAP_FAILED, SDKErrors.CONTEXT_NULL,id);
                }
            }
            else{
                deleteMediaFromAppStore.deleteMediaFromAppstoreCallBack(SDKHelper.TAG_CAP_FAILED , SDKErrors.MEDIA_VAULT_WRONG_FILEINPUT,id);
            }
        }
        else{
            SDKUtils.showToast(mContext, SDKErrors.MEDIA_VAULT_CALlBACK);
        }
    }

    /**
     * Method to update secure file details on AppStore Cloud
     *
     */

    public void updateFileDetails(String id, String filename, MediaFileDetailsOperationCallBack mediaFileDetailsOperationCallBack){
        if (mediaFileDetailsOperationCallBack != null) {
            if(id != null && !id.isEmpty() && filename != null && !filename.isEmpty()){
                if(mContext !=  null) {
                    try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put(SDKHelper.APPID,mContext.getPackageName());
                        jsonObject.put(SDKHelper.MEDIA_VAULT_FILE_ID,id);
                        jsonObject.put(SDKHelper.MEDIADVAULT_FILEANME,filename);
                        new AppStoreDataTransfer(mContext).operationsFileDetailsToAppStore(SDKHelper.BASE_URL_APPSTORE,jsonObject,id, Request.Method.PUT,mediaFileDetailsOperationCallBack);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        mediaFileDetailsOperationCallBack.mediaFileDetailsOperationCallBack(SDKHelper.TAG_CAP_FAILED , SDKErrors.MEDIA_VAULT_WRONG_FILEINPUT,id);
                    }
                }
                else{
                    mediaFileDetailsOperationCallBack.mediaFileDetailsOperationCallBack(SDKHelper.TAG_CAP_FAILED, SDKErrors.CONTEXT_NULL,id);
                }
            }
            else{
                mediaFileDetailsOperationCallBack.mediaFileDetailsOperationCallBack(SDKHelper.TAG_CAP_FAILED , SDKErrors.MEDIA_VAULT_WRONG_FILEINPUT,id);
            }
        }
        else{
            SDKUtils.showToast(mContext, SDKErrors.MEDIA_VAULT_CALlBACK);
        }
    }

    /**
     * Method to get secure file info from AppStore Cloud corresponding to id and appId
     *
     */

    public void getFileInfo(String id, MediaVaultGetFileInfo mediaVaultGetFileInfo){
        if (mediaVaultGetFileInfo != null) {
            if(id != null && !id.isEmpty()){
                if(mContext !=  null) {
                    try {
                        String inputParam = "/" + mContext.getPackageName() + "/" + id;
                        new AppStoreDataTransfer(mContext).getFileDetailsFromAppStore(inputParam,mediaVaultGetFileInfo);

                    } catch (Exception e) {
                        e.printStackTrace();
                        mediaVaultGetFileInfo.getMediaVaultFileInfo(SDKHelper.TAG_CAP_FAILED , SDKErrors.MEDIA_VAULT_WRONG_FILEINPUT,"");
                    }
                }
                else{
                    mediaVaultGetFileInfo.getMediaVaultFileInfo(SDKHelper.TAG_CAP_FAILED, SDKErrors.CONTEXT_NULL,"");
                }
            }
            else{
                mediaVaultGetFileInfo.getMediaVaultFileInfo(SDKHelper.TAG_CAP_FAILED , SDKErrors.MEDIA_VAULT_WRONG_FILEINPUT,"");
            }
        }
        else{
            SDKUtils.showToast(mContext, SDKErrors.MEDIA_VAULT_CALlBACK);
        }

    }

    /**
     * Method to get all secure files info from AppStore Cloud corresponding to walletIdList
     *
     */

    public void getFileInfoList(List<Integer> walletId, MediaVaultGetFileInfo mediaVaultGetFileInfo){
        if (mediaVaultGetFileInfo != null) {
            int walletSize = walletId.size();
            if(walletId != null && walletSize > 0){
                if(mContext !=  null) {
                    try {
                        String inputParam = "/" + mContext.getPackageName() + "/?" + SDKHelper.MEDIADVAULT_WALLET_ADDRESSES;
                        for (int i = 0; i < walletSize; i++) {
                            String walletAdd = BitVaultWalletManager.getWalletInstance().getWalletAddress(walletId.get(i));
                            if (i == 0) {
                                inputParam = inputParam + walletAdd;
                            } else {
                                inputParam = inputParam + "," + walletAdd;
                            }
                        }
                        new AppStoreDataTransfer(mContext).getFileDetailsFromAppStore(inputParam, mediaVaultGetFileInfo);

                    } catch(Exception e){
                            e.printStackTrace();
                            mediaVaultGetFileInfo.getMediaVaultFileInfo(SDKHelper.TAG_CAP_FAILED, SDKErrors.MEDIA_VAULT_WRONG_FILEINPUT, "");
                        }
                    }

                else{
                    mediaVaultGetFileInfo.getMediaVaultFileInfo(SDKHelper.TAG_CAP_FAILED, SDKErrors.CONTEXT_NULL,"");
                }
            }
            else{
                mediaVaultGetFileInfo.getMediaVaultFileInfo(SDKHelper.TAG_CAP_FAILED , SDKErrors.MEDIA_VAULT_WRONG_FILEINPUT,"");
            }
        }
        else{
            SDKUtils.showToast(mContext, SDKErrors.MEDIA_VAULT_CALlBACK);
        }

    }


}
