package iclasses.mediavaultcallback;

import model.MediaVaultBlockModel;

/**
 *  Created by vvdn on 12/09/2017
 */

public interface MediaVaultReceiveCallBack {
    void receiveMediaVaultCallback(String status, String message, String messageList, MediaVaultBlockModel mediaVaultBlockModel);
}
