package messagemanager;

import android.content.Context;

import iclasses.FeeFromCloudCallback;

/**
 * Created by Vinod Singh on 16/5/17.
 */

public class FeeDescriptor {
    private Context mContext;
    private FeeFromCloudCallback mfeeFromCloudCallback;
    private String mJsonString = "{fees: {msg_char_fee: 0.0001,receiver_fee: 0.0001,media_fee: {_2MB: 0.0001,_4MB: 0.0002,_6MB:0.0003,_8MB: 0.0004,_10MB: 0.0005}}}";

    // Constructor to initialise objects
    public FeeDescriptor(Context mContext, FeeFromCloudCallback feeFromCloudCallback) {
        this.mContext = mContext;
        this.mfeeFromCloudCallback = feeFromCloudCallback;
    }

    /***
     * This method is used to get Message sending fee from app cloud
     *
     * @param  appId
     * @return
     */
    public void getFeeFromAppCloud(final String appId){
        mfeeFromCloudCallback.onResponseFeeCloud(mJsonString);
    }


}

