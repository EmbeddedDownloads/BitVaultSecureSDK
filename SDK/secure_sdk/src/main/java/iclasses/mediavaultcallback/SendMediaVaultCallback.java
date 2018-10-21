package iclasses.mediavaultcallback;

import model.MediaVaultDataToPBCModel;

/**
 *  Created by vvdn on 12/09/2017
 */

public interface SendMediaVaultCallback {
    void sendMediaVaultCallback(String status, String message, MediaVaultDataToPBCModel dataToPBCModel);
}
